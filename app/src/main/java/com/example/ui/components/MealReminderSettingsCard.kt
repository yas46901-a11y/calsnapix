package com.example.ui.components

import android.Manifest
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.notification.MealReminderManager
import com.example.ui.theme.CalCard
import com.example.ui.theme.CalTheme

/**
 * Modern M3 card allowing users to configure scheduled meal reminders in their local timezone,
 * grant notification permissions on Android 13+, and test notifications immediately.
 */
@Composable
fun MealReminderSettingsCard(
  reminderManager: MealReminderManager,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val context = LocalContext.current

  var breakfastEnabled by remember { mutableStateOf(reminderManager.isBreakfastEnabled()) }
  var lunchEnabled by remember { mutableStateOf(reminderManager.isLunchEnabled()) }
  var snackEnabled by remember { mutableStateOf(reminderManager.isSnackEnabled()) }
  var dinnerEnabled by remember { mutableStateOf(reminderManager.isDinnerEnabled()) }

  var testFiredMessage by remember { mutableStateOf<String?>(null) }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
      reminderManager.scheduleAllReminders()
    }
  }

  CalCard(
    modifier = modifier.testTag("meal_reminder_settings_card"),
    backgroundColor = colors.surface
  ) {
    // 1. Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(colors.accentSoft),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(17.dp)
          )
        }
        Column {
          Text(
            text = "MEAL REMINDERS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            color = colors.ink2
          )
          Text(
            text = "Scheduled in your local timezone",
            fontSize = 11.sp,
            color = colors.ink2
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(999.dp),
        color = colors.accent,
        modifier = Modifier.clickable {
          view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
          reminderManager.triggerTestNotification(MealReminderManager.MEAL_LUNCH)
          testFiredMessage = "Notification Sent! 🔔"
        }
      ) {
        Text(
          text = "Test Now 🔔",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
      }
    }

    testFiredMessage?.let { msg ->
      Spacer(modifier = Modifier.height(8.dp))
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = colors.accentSoft,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = msg,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = colors.accent,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 2. Meal Window Toggles
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      ReminderToggleRow(
        icon = "🌅",
        title = "Breakfast / Nashta",
        timeLabel = "8:30 AM",
        subtext = "High-protein breakfast window",
        isChecked = breakfastEnabled,
        onCheckedChange = {
          breakfastEnabled = it
          reminderManager.setBreakfastEnabled(it)
        }
      )
      ReminderToggleRow(
        icon = "☀️",
        title = "Midday Lunch / Bhaat",
        timeLabel = "1:15 PM",
        subtext = "Ayurvedic peak digestion window",
        isChecked = lunchEnabled,
        onCheckedChange = {
          lunchEnabled = it
          reminderManager.setLunchEnabled(it)
        }
      )
      ReminderToggleRow(
        icon = "☕",
        title = "Evening Chai & Snack",
        timeLabel = "5:00 PM",
        subtext = "Mid-afternoon energy sustain",
        isChecked = snackEnabled,
        onCheckedChange = {
          snackEnabled = it
          reminderManager.setSnackEnabled(it)
        }
      )
      ReminderToggleRow(
        icon = "🌙",
        title = "Dinner / Roti",
        timeLabel = "8:30 PM",
        subtext = "Light dinner for restorative sleep",
        isChecked = dinnerEnabled,
        onCheckedChange = {
          dinnerEnabled = it
          reminderManager.setDinnerEnabled(it)
        }
      )
    }
  }
}

@Composable
private fun ReminderToggleRow(
  icon: String,
  title: String,
  timeLabel: String,
  subtext: String,
  isChecked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  val colors = CalTheme.colors
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    color = colors.surface2
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(text = icon, fontSize = 20.sp)
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = title,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = colors.surface
            ) {
              Text(
                text = timeLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink2,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
          Text(
            text = subtext,
            fontSize = 11.sp,
            color = colors.ink2
          )
        }
      }

      Switch(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
          checkedThumbColor = Color.White,
          checkedTrackColor = colors.accent,
          uncheckedThumbColor = colors.ink2,
          uncheckedTrackColor = colors.line
        )
      )
    }
  }
}
