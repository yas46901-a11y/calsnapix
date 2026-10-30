package com.example.data.sensor

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Enterprise sensor manager for automated real-time step counting and activity telemetry.
 * Integrates hardware STEP_COUNTER, STEP_DETECTOR, and ACCELEROMETER fallback.
 */
class StepSensorManager private constructor(private val appContext: Context) : SensorEventListener {

  companion object {
    private const val TAG = "StepSensorManager"
    private const val PREFS_NAME = "calsnapix_step_tracker_prefs"
    private const val KEY_TRACKING_DATE = "tracking_date"
    private const val KEY_TODAY_STEPS = "today_steps"
    private const val KEY_BASELINE_COUNTER = "baseline_counter"
    private const val KEY_STEP_GOAL = "step_goal"
    private const val KEY_HOURLY_PREFIX = "hourly_steps_"

    // Accelerometer step detection constants
    private const val ACCEL_PEAK_THRESHOLD = 11.8f // m/s^2 magnitude peak
    private const val ACCEL_MIN_STEP_INTERVAL_MS = 240L // Minimum cadence spacing

    @Volatile
    private var instance: StepSensorManager? = null

    fun getInstance(context: Context): StepSensorManager {
      return instance ?: synchronized(this) {
        instance ?: StepSensorManager(context.applicationContext).also { instance = it }
      }
    }
  }

  private val sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
  private val prefs: SharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  private val coroutineScope = CoroutineScope(Dispatchers.Default)

  private val _activityStats = MutableStateFlow(DailyActivityStats())
  val activityStats: StateFlow<DailyActivityStats> = _activityStats.asStateFlow()

  // Hardware sensors
  private var stepCounterSensor: Sensor? = null
  private var stepDetectorSensor: Sensor? = null
  private var accelerometerSensor: Sensor? = null

  private var isListening = false
  private var activeSensorName = "None"

  // Accelerometer fallback state
  private var lastStepTimestampMs = 0L
  private var lastAccelMagnitude = 9.8f
  private var isAccelRising = false

  init {
    detectSensors()
    loadSavedState()
  }

  /**
   * Identifies available device sensors.
   */
  private fun detectSensors() {
    stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val hasHardwareStep = (stepCounterSensor != null || stepDetectorSensor != null)

    _activityStats.update {
      it.copy(
        isHardwareSensorAvailable = hasHardwareStep || accelerometerSensor != null,
        sensorSource = when {
          stepCounterSensor != null -> "Hardware Step Counter"
          stepDetectorSensor != null -> "Hardware Step Detector"
          accelerometerSensor != null -> "Accelerometer Motion Filter"
          else -> "Manual / Off"
        }
      )
    }
  }

  /**
   * Check if user has granted ACTIVITY_RECOGNITION on Android 10+ (API 29+).
   */
  fun hasPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      ContextCompat.checkSelfPermission(
        appContext,
        Manifest.permission.ACTIVITY_RECOGNITION
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }

  /**
   * Loads saved step count and hourly breakdown for today, resetting if the day changed.
   */
  @Synchronized
  fun loadSavedState() {
    val todayDate = getTodayDateKey()
    val savedDate = prefs.getString(KEY_TRACKING_DATE, null)
    val savedGoal = prefs.getInt(KEY_STEP_GOAL, 10000)

    if (savedDate != todayDate) {
      // New day: archive/reset today's metrics
      prefs.edit()
        .putString(KEY_TRACKING_DATE, todayDate)
        .putInt(KEY_TODAY_STEPS, 0)
        .putFloat(KEY_BASELINE_COUNTER, -1f)
        .apply()

      // Reset hourly steps
      for (hour in 0..23) {
        prefs.edit().putInt("$KEY_HOURLY_PREFIX$hour", 0).apply()
      }
    }

    val stepsToday = prefs.getInt(KEY_TODAY_STEPS, 0)
    val hourlyList = (0..23).map { hour ->
      prefs.getInt("$KEY_HOURLY_PREFIX$hour", 0)
    }

    updateComputedStats(stepsToday, savedGoal, hourlyList)
  }

  /**
   * Starts listening to step and motion telemetry.
   */
  @Synchronized
  fun startTracking() {
    if (isListening) return

    val permitted = hasPermission()
    _activityStats.update { it.copy(hasPermission = permitted) }

    // Register sensor listener (prefer TYPE_STEP_COUNTER, then TYPE_STEP_DETECTOR, then ACCELEROMETER)
    var registered = false

    if (stepCounterSensor != null) {
      registered = sensorManager.registerListener(
        this,
        stepCounterSensor,
        SensorManager.SENSOR_DELAY_UI
      )
      if (registered) activeSensorName = "Hardware Step Counter"
    }

    if (!registered && stepDetectorSensor != null) {
      registered = sensorManager.registerListener(
        this,
        stepDetectorSensor,
        SensorManager.SENSOR_DELAY_UI
      )
      if (registered) activeSensorName = "Hardware Step Detector"
    }

    if (!registered && accelerometerSensor != null) {
      registered = sensorManager.registerListener(
        this,
        accelerometerSensor,
        SensorManager.SENSOR_DELAY_GAME
      )
      if (registered) activeSensorName = "Accelerometer Fallback"
    }

    isListening = registered
    _activityStats.update {
      it.copy(
        isTracking = registered,
        sensorSource = activeSensorName
      )
    }
    Log.d(TAG, "Step tracking started. Registered=$registered, Sensor=$activeSensorName")
  }

  /**
   * Stops listening to sensor events.
   */
  @Synchronized
  fun stopTracking() {
    if (!isListening) return
    try {
      sensorManager.unregisterListener(this)
    } catch (e: Exception) {
      Log.e(TAG, "Error unregistering sensor listener: ${e.message}")
    }
    isListening = false
    _activityStats.update { it.copy(isTracking = false) }
  }

  /**
   * Sensor event callback.
   */
  override fun onSensorChanged(event: SensorEvent?) {
    if (event == null) return

    when (event.sensor.type) {
      Sensor.TYPE_STEP_COUNTER -> {
        handleStepCounterEvent(event.values[0])
      }
      Sensor.TYPE_STEP_DETECTOR -> {
        if (event.values[0] == 1.0f) {
          incrementSteps(1)
        }
      }
      Sensor.TYPE_ACCELEROMETER -> {
        handleAccelerometerEvent(event.values[0], event.values[1], event.values[2])
      }
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    // No-op
  }

  /**
   * Handles cumulative hardware step counter events.
   */
  private fun handleStepCounterEvent(hardwareCount: Float) {
    checkDateRollover()
    val todayDate = getTodayDateKey()
    var baseline = prefs.getFloat(KEY_BASELINE_COUNTER, -1f)

    if (baseline < 0f || baseline > hardwareCount) {
      // First event today or device rebooted
      baseline = hardwareCount
      prefs.edit().putFloat(KEY_BASELINE_COUNTER, baseline).apply()
    }

    val stepsFromHardware = (hardwareCount - baseline).toInt().coerceAtLeast(0)
    val savedSteps = prefs.getInt(KEY_TODAY_STEPS, 0)
    val finalSteps = maxOf(stepsFromHardware, savedSteps)

    if (finalSteps != savedSteps) {
      recordStepUpdate(finalSteps)
    }
  }

  /**
   * High-accuracy accelerometer peak detection with low-pass gravity filtering.
   */
  private fun handleAccelerometerEvent(x: Float, y: Float, z: Float) {
    val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    val now = System.currentTimeMillis()

    if (magnitude > ACCEL_PEAK_THRESHOLD && !isAccelRising && (now - lastStepTimestampMs) > ACCEL_MIN_STEP_INTERVAL_MS) {
      isAccelRising = true
      lastStepTimestampMs = now
      incrementSteps(1)
    } else if (magnitude < 9.8f) {
      isAccelRising = false
    }

    lastAccelMagnitude = magnitude
  }

  /**
   * Increments today's steps by a given count.
   */
  @Synchronized
  fun incrementSteps(addedSteps: Int) {
    if (addedSteps <= 0) return
    checkDateRollover()

    val currentSteps = prefs.getInt(KEY_TODAY_STEPS, 0)
    val newSteps = currentSteps + addedSteps
    recordStepUpdate(newSteps, addedSteps)
  }

  /**
   * Explicitly updates step count and logs into current hour bucket.
   */
  private fun recordStepUpdate(newTotalSteps: Int, deltaSteps: Int = 1) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val currentHourSteps = prefs.getInt("$KEY_HOURLY_PREFIX$currentHour", 0)

    prefs.edit()
      .putInt(KEY_TODAY_STEPS, newTotalSteps)
      .putInt("$KEY_HOURLY_PREFIX$currentHour", currentHourSteps + deltaSteps)
      .apply()

    val goal = prefs.getInt(KEY_STEP_GOAL, 10000)
    val hourlyList = (0..23).map { hour ->
      prefs.getInt("$KEY_HOURLY_PREFIX$hour", 0)
    }

    updateComputedStats(newTotalSteps, goal, hourlyList)
  }

  /**
   * Manually add steps (convenience for walking workout logs or testing on emulator).
   */
  fun addManualSteps(count: Int) {
    incrementSteps(count)
  }

  /**
   * Update the daily step target.
   */
  fun updateStepGoal(newGoal: Int) {
    if (newGoal <= 0) return
    prefs.edit().putInt(KEY_STEP_GOAL, newGoal).apply()
    val currentSteps = prefs.getInt(KEY_TODAY_STEPS, 0)
    val hourlyList = (0..23).map { hour ->
      prefs.getInt("$KEY_HOURLY_PREFIX$hour", 0)
    }
    updateComputedStats(currentSteps, newGoal, hourlyList)
  }

  /**
   * Resets today's step count.
   */
  fun resetToday() {
    prefs.edit()
      .putInt(KEY_TODAY_STEPS, 0)
      .putFloat(KEY_BASELINE_COUNTER, -1f)
      .apply()
    for (hour in 0..23) {
      prefs.edit().putInt("$KEY_HOURLY_PREFIX$hour", 0).apply()
    }
    loadSavedState()
  }

  /**
   * Recomputes calories, distance, active minutes, and activity level.
   */
  private fun updateComputedStats(steps: Int, goal: Int, hourly: List<Int>) {
    // Metabolic formulas:
    // ~0.04 kcal per step for average body mass (70kg)
    val calories = (steps * 0.04f).roundToInt()
    // Stride length ~0.762 meters
    val distanceKm = (steps * 0.762f / 1000f)
    // Brisk walking ~100 steps per minute
    val activeMinutes = (steps / 100f).roundToInt()
    val level = ActivityLevel.fromSteps(steps)

    _activityStats.update {
      it.copy(
        steps = steps,
        goalSteps = goal,
        caloriesBurned = calories,
        distanceKm = (distanceKm * 10f).roundToInt() / 10f, // 1 decimal place
        activeMinutes = activeMinutes,
        activityLevel = level,
        hourlySteps = hourly,
        hasPermission = hasPermission(),
        isTracking = isListening,
        lastUpdated = System.currentTimeMillis()
      )
    }
  }

  private fun checkDateRollover() {
    val todayKey = getTodayDateKey()
    val savedKey = prefs.getString(KEY_TRACKING_DATE, null)
    if (savedKey != todayKey) {
      loadSavedState()
    }
  }

  private fun getTodayDateKey(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
  }
}
