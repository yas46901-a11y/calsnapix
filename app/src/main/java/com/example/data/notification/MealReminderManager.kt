package com.example.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

/**
 * Manages daily meal reminder alarms and notifications for Breakfast, Lunch, and Dinner
 * scheduled in the user's local timezone.
 */
class MealReminderManager(private val context: Context) {

  companion object {
    const val CHANNEL_ID = "calsnapix_meal_reminders_channel"
    const val CHANNEL_NAME = "Meal & Hydration Reminders"
    const val PREFS_NAME = "calsnapix_notification_prefs"

    const val KEY_BREAKFAST_ENABLED = "remind_breakfast"
    const val KEY_LUNCH_ENABLED = "remind_lunch"
    const val KEY_DINNER_ENABLED = "remind_dinner"
    const val KEY_SNACK_ENABLED = "remind_snack"

    const val EXTRA_MEAL_TYPE = "extra_meal_type"
    const val MEAL_BREAKFAST = "BREAKFAST"
    const val MEAL_LUNCH = "LUNCH"
    const val MEAL_SNACK = "SNACK"
    const val MEAL_DINNER = "DINNER"

    const val NOTIF_ID_BREAKFAST = 3001
    const val NOTIF_ID_LUNCH = 3002
    const val NOTIF_ID_SNACK = 3003
    const val NOTIF_ID_DINNER = 3004
    const val NOTIF_ID_TEST = 3099
  }

  private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  init {
    createNotificationChannel()
  }

  /**
   * Creates the High Importance Notification Channel for Android 8.0+ (API 26+).
   */
  fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = CHANNEL_NAME
      val descriptionText = "Context-aware reminders to log Breakfast, Lunch, and Dinner in your timezone"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
        enableVibration(true)
        setShowBadge(true)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  /**
   * Schedule or update all enabled meal reminders based on current preferences.
   */
  fun scheduleAllReminders() {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? android.app.AlarmManager ?: return

    // Breakfast: 8:30 AM
    if (isBreakfastEnabled()) {
      scheduleDailyAlarm(alarmManager, MEAL_BREAKFAST, 8, 30, NOTIF_ID_BREAKFAST)
    } else {
      cancelAlarm(alarmManager, MEAL_BREAKFAST, NOTIF_ID_BREAKFAST)
    }

    // Lunch: 1:15 PM
    if (isLunchEnabled()) {
      scheduleDailyAlarm(alarmManager, MEAL_LUNCH, 13, 15, NOTIF_ID_LUNCH)
    } else {
      cancelAlarm(alarmManager, MEAL_LUNCH, NOTIF_ID_LUNCH)
    }

    // Snack / Evening Chai: 5:00 PM
    if (isSnackEnabled()) {
      scheduleDailyAlarm(alarmManager, MEAL_SNACK, 17, 0, NOTIF_ID_SNACK)
    } else {
      cancelAlarm(alarmManager, MEAL_SNACK, NOTIF_ID_SNACK)
    }

    // Dinner: 8:30 PM
    if (isDinnerEnabled()) {
      scheduleDailyAlarm(alarmManager, MEAL_DINNER, 20, 30, NOTIF_ID_DINNER)
    } else {
      cancelAlarm(alarmManager, MEAL_DINNER, NOTIF_ID_DINNER)
    }
  }

  private fun scheduleDailyAlarm(
    alarmManager: android.app.AlarmManager,
    mealType: String,
    hourOfDay: Int,
    minute: Int,
    requestCode: Int
  ) {
    val intent = Intent(context, MealReminderReceiver::class.java).apply {
      action = "com.example.action.MEAL_REMINDER"
      putExtra(EXTRA_MEAL_TYPE, mealType)
    }

    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
      PendingIntent.FLAG_UPDATE_CURRENT
    }

    val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags)

    val calendar = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, hourOfDay)
      set(Calendar.MINUTE, minute)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
      if (before(Calendar.getInstance())) {
        add(Calendar.DAY_OF_YEAR, 1)
      }
    }

    try {
      alarmManager.setInexactRepeating(
        android.app.AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        android.app.AlarmManager.INTERVAL_DAY,
        pendingIntent
      )
    } catch (e: Exception) {
      // Fallback
      alarmManager.set(
        android.app.AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
      )
    }
  }

  private fun cancelAlarm(alarmManager: android.app.AlarmManager, mealType: String, requestCode: Int) {
    val intent = Intent(context, MealReminderReceiver::class.java).apply {
      action = "com.example.action.MEAL_REMINDER"
      putExtra(EXTRA_MEAL_TYPE, mealType)
    }
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    } else {
      PendingIntent.FLAG_NO_CREATE
    }
    val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags)
    if (pendingIntent != null) {
      alarmManager.cancel(pendingIntent)
      pendingIntent.cancel()
    }
  }

  /**
   * Displays the notification for the given meal window.
   */
  fun showMealNotification(mealType: String) {
    createNotificationChannel()

    val (title, content, notifId) = when (mealType) {
      MEAL_BREAKFAST -> Triple(
        "Time for Nashta! 🌅",
        "Fuel your morning with balanced carbs and clean protein. Tap to scan your plate.",
        NOTIF_ID_BREAKFAST
      )
      MEAL_LUNCH -> Triple(
        "Midday Lunch Window ☀️",
        "Peak digestion window in Ayurveda. Log your meal to keep your macros on target.",
        NOTIF_ID_LUNCH
      )
      MEAL_SNACK -> Triple(
        "Evening Chai / Snack Time ☕",
        "Reach for roasted makhana, chana, or green tea to sustain energy.",
        NOTIF_ID_SNACK
      )
      MEAL_DINNER -> Triple(
        "Dinner Time 🌙",
        "Light dinner aids sound sleep. Log your meal to seal today's calorie streak!",
        NOTIF_ID_DINNER
      )
      else -> Triple(
        "Meal Logger Reminder 🥗",
        "Remember to capture today's meal to maintain your nutritional logging streak!",
        NOTIF_ID_TEST
      )
    }

    val launchIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra("navigate_to", "SCAN")
    }

    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
      PendingIntent.FLAG_UPDATE_CURRENT
    }

    val pendingIntent = PendingIntent.getActivity(context, notifId, launchIntent, flags)

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_menu_agenda)
      .setContentTitle(title)
      .setContentText(content)
      .setStyle(NotificationCompat.BigTextStyle().bigText(content))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()

    try {
      val notificationManager = NotificationManagerCompat.from(context)
      notificationManager.notify(notifId, notification)
    } catch (e: SecurityException) {
      // Permission not granted on Android 13+
    }
  }

  /**
   * Fires an immediate test notification for user verification.
   */
  fun triggerTestNotification(mealType: String = MEAL_LUNCH) {
    showMealNotification(mealType)
  }

  // Preference getters and setters
  fun isBreakfastEnabled(): Boolean = prefs.getBoolean(KEY_BREAKFAST_ENABLED, true)
  fun isLunchEnabled(): Boolean = prefs.getBoolean(KEY_LUNCH_ENABLED, true)
  fun isDinnerEnabled(): Boolean = prefs.getBoolean(KEY_DINNER_ENABLED, true)
  fun isSnackEnabled(): Boolean = prefs.getBoolean(KEY_SNACK_ENABLED, true)

  fun setBreakfastEnabled(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_BREAKFAST_ENABLED, enabled).apply()
    scheduleAllReminders()
  }

  fun setLunchEnabled(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_LUNCH_ENABLED, enabled).apply()
    scheduleAllReminders()
  }

  fun setDinnerEnabled(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_DINNER_ENABLED, enabled).apply()
    scheduleAllReminders()
  }

  fun setSnackEnabled(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_SNACK_ENABLED, enabled).apply()
    scheduleAllReminders()
  }
}
