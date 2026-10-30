package com.example.data.sensor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity

/**
 * Background and Bound Service for continuous automatic step counting and activity tracking.
 * Coordinates with StepSensorManager to track physical movement.
 */
class StepTrackingService : Service() {

  companion object {
    private const val TAG = "StepTrackingService"
    const val CHANNEL_ID = "calsnapix_step_service_channel"
    const val NOTIFICATION_ID = 2001

    const val ACTION_START_TRACKING = "com.example.action.START_STEP_TRACKING"
    const val ACTION_STOP_TRACKING = "com.example.action.STOP_STEP_TRACKING"
    const val ACTION_SYNC_STEPS = "com.example.action.SYNC_STEPS"

    fun startService(context: Context) {
      val intent = Intent(context, StepTrackingService::class.java).apply {
        action = ACTION_START_TRACKING
      }
      try {
        context.startService(intent)
      } catch (e: Exception) {
        Log.e(TAG, "Failed to start StepTrackingService: ${e.message}")
      }
    }

    fun stopService(context: Context) {
      val intent = Intent(context, StepTrackingService::class.java).apply {
        action = ACTION_STOP_TRACKING
      }
      try {
        context.startService(intent)
      } catch (e: Exception) {
        Log.e(TAG, "Failed to stop StepTrackingService: ${e.message}")
      }
    }
  }

  private val binder = LocalBinder()
  private lateinit var sensorManager: StepSensorManager

  inner class LocalBinder : Binder() {
    fun getService(): StepTrackingService = this@StepTrackingService
    fun getSensorManager(): StepSensorManager = sensorManager
  }

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "StepTrackingService onCreate")
    sensorManager = StepSensorManager.getInstance(applicationContext)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_STOP_TRACKING -> {
        sensorManager.stopTracking()
        stopSelf()
      }
      ACTION_SYNC_STEPS -> {
        sensorManager.loadSavedState()
      }
      ACTION_START_TRACKING, null -> {
        sensorManager.startTracking()
      }
    }
    return START_STICKY
  }

  override fun onBind(intent: Intent?): IBinder {
    sensorManager.startTracking()
    return binder
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG, "StepTrackingService onDestroy")
    sensorManager.stopTracking()
  }
}
