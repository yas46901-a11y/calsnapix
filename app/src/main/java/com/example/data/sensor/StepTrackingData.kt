package com.example.data.sensor

import kotlin.math.roundToInt

/**
 * Activity level classification based on daily step volume.
 */
enum class ActivityLevel(
  val label: String,
  val minSteps: Int,
  val description: String,
  val iconName: String
) {
  SEDENTARY(
    label = "Sedentary",
    minSteps = 0,
    description = "Resting or light desk work. Under 3,000 steps.",
    iconName = "airline_seat_recline_normal"
  ),
  LIGHT(
    label = "Lightly Active",
    minSteps = 3000,
    description = "Routine daily movement. 3,000 - 7,499 steps.",
    iconName = "directions_walk"
  ),
  MODERATE(
    label = "Moderately Active",
    minSteps = 7500,
    description = "Meeting health baseline. 7,500 - 9,999 steps.",
    iconName = "directions_run"
  ),
  ACTIVE(
    label = "Active",
    minSteps = 10000,
    description = "Optimal cardio & metabolic burn. 10,000 - 12,499 steps.",
    iconName = "local_fire_department"
  ),
  VERY_ACTIVE(
    label = "Highly Active",
    minSteps = 12500,
    description = "Vigorous movement & high endurance. 12,500+ steps.",
    iconName = "bolt"
  );

  companion object {
    fun fromSteps(steps: Int): ActivityLevel {
      return when {
        steps >= VERY_ACTIVE.minSteps -> VERY_ACTIVE
        steps >= ACTIVE.minSteps -> ACTIVE
        steps >= MODERATE.minSteps -> MODERATE
        steps >= LIGHT.minSteps -> LIGHT
        else -> SEDENTARY
      }
    }
  }
}

/**
 * Real-time daily activity statistics calculated from step telemetry.
 */
data class DailyActivityStats(
  val steps: Int = 0,
  val goalSteps: Int = 10000,
  val caloriesBurned: Int = 0,
  val distanceKm: Float = 0.0f,
  val activeMinutes: Int = 0,
  val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
  val hourlySteps: List<Int> = List(24) { 0 },
  val isHardwareSensorAvailable: Boolean = true,
  val isTracking: Boolean = false,
  val hasPermission: Boolean = false,
  val sensorSource: String = "Hardware Sensor",
  val lastUpdated: Long = System.currentTimeMillis()
) {
  val progressFraction: Float
    get() = if (goalSteps > 0) (steps.toFloat() / goalSteps.toFloat()).coerceIn(0f, 1f) else 0f

  val progressPercent: Int
    get() = (progressFraction * 100f).roundToInt()

  val remainingSteps: Int
    get() = (goalSteps - steps).coerceAtLeast(0)
}
