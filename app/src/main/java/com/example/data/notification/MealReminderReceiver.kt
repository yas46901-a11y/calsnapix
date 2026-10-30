package com.example.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver triggered by AlarmManager at scheduled breakfast, lunch, and dinner times.
 */
class MealReminderReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    val mealType = intent?.getStringExtra(MealReminderManager.EXTRA_MEAL_TYPE) ?: MealReminderManager.MEAL_LUNCH
    val manager = MealReminderManager(context)
    manager.showMealNotification(mealType)
  }
}
