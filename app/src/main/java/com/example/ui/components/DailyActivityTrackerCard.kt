package com.example.ui.components

import android.Manifest
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalCard
import com.example.ui.theme.CalPrimaryButton
import com.example.ui.theme.CalTheme
import java.util.Calendar
import kotlin.math.roundToInt

/**
 * Enterprise Apple Health / Cal AI style Activity Tracker Card.
 * Displays real-time device step count, active calorie burn, distance, active minutes,
 * and 24-hour activity distribution.
 */
@Composable
fun DailyActivityTrackerCard(
  currentSteps: Int,
  dailyStepGoal: Int,
  activeCaloriesBurned: Int,
  distanceKm: Float,
  activeMinutes: Int,
  activityLevel: String,
  hourlySteps: List<Int>,
  isTracking: Boolean,
  hasPermission: Boolean,
  sensorSource: String,
  caloriesConsumed: Int,
  onUpdateGoal: (Int) -> Unit,
  onAddManualSteps: (Int) -> Unit,
  onRefresh: () -> Unit,
  onPermissionGranted: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val context = LocalContext.current

  var showGoalDialog by remember { mutableStateOf(false) }
  var showQuickAddMenu by remember { mutableStateOf(false) }

  // Activity recognition permission launcher
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
      onPermissionGranted()
    }
  }

  val progressFraction = if (dailyStepGoal > 0) {
    (currentSteps.toFloat() / dailyStepGoal.toFloat()).coerceIn(0f, 1f)
  } else 0f

  val animatedProgress by animateFloatAsState(
    targetValue = progressFraction,
    animationSpec = tween(durationMillis = 800),
    label = "step_progress"
  )

  CalCard(
    modifier = modifier.testTag("daily_activity_tracker_card"),
    backgroundColor = colors.surface
  ) {
    // 1. Header with Status & Source
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
            .size(32.dp)
            .clip(CircleShape)
            .background(colors.accentSoft),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(17.dp)
          )
        }
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "DAILY ACTIVITY & STEPS",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp,
              color = colors.ink2
            )
            // Pulse live indicator
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isTracking) colors.accent else Color.Gray)
            )
          }
          Text(
            text = sensorSource,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = colors.ink2
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onRefresh()
          },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh telemetry",
            tint = colors.ink2,
            modifier = Modifier.size(16.dp)
          )
        }
        IconButton(
          onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showGoalDialog = true
          },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Step Target",
            tint = colors.ink2,
            modifier = Modifier.size(15.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Permission Banner if not yet granted on Android 10+
    if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp),
        color = colors.accentSoft,
        border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.25f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Auto Step Tracking Paused",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
            Text(
              text = "Grant motion activity permission to count steps automatically in background.",
              fontSize = 11.sp,
              color = colors.ink2,
              lineHeight = 15.sp
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.accent,
            modifier = Modifier.clickable {
              permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
          ) {
            Text(
              text = "Enable",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    // 3. Primary Step Display with Circular Ring
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = String.format("%,d", currentSteps),
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.ink,
            letterSpacing = (-0.5).sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "steps",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink2,
            modifier = Modifier.padding(bottom = 4.dp)
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "Goal: ${String.format("%,d", dailyStepGoal)}",
            fontSize = 12.sp,
            color = colors.ink2
          )
          Text(
            text = "•",
            fontSize = 12.sp,
            color = colors.ink2
          )
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = colors.accentSoft
          ) {
            Text(
              text = activityLevel,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      // Progress Ring
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(64.dp)
      ) {
        CircularProgressIndicator(
          progress = { 1f },
          modifier = Modifier.size(64.dp),
          color = colors.surface2,
          strokeWidth = 6.dp,
          strokeCap = StrokeCap.Round
        )
        CircularProgressIndicator(
          progress = { animatedProgress },
          modifier = Modifier.size(64.dp),
          color = colors.accent,
          strokeWidth = 6.dp,
          strokeCap = StrokeCap.Round
        )
        Text(
          text = "${(progressFraction * 100).roundToInt()}%",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 4. Metric Grid: Active Calories, Distance, Active Time
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      ActivityMetricChip(
        icon = Icons.Default.LocalFireDepartment,
        value = "$activeCaloriesBurned kcal",
        label = "Active Burn",
        tint = Color(0xFFEF4444),
        modifier = Modifier.weight(1f)
      )
      ActivityMetricChip(
        icon = Icons.Default.NearMe,
        value = "${String.format("%.1f", distanceKm)} km",
        label = "Distance",
        tint = Color(0xFF3B82F6),
        modifier = Modifier.weight(1f)
      )
      ActivityMetricChip(
        icon = Icons.Default.Schedule,
        value = "$activeMinutes min",
        label = "Active Time",
        tint = Color(0xFF10B981),
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 5. Net Energy Balance Pill
    val netCalories = (caloriesConsumed - activeCaloriesBurned).coerceAtLeast(0)
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
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(15.dp)
          )
          Text(
            text = "Net Intake:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colors.ink2
          )
          Text(
            text = "$netCalories kcal",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
        }
        Text(
          text = "($caloriesConsumed in - $activeCaloriesBurned burned)",
          fontSize = 11.sp,
          color = colors.ink2
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 6. 24-Hour Hourly Step Distribution Chart
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val maxHourly = (hourlySteps.maxOrNull() ?: 1).coerceAtLeast(50)

    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "TODAY'S ACTIVITY RHYTHM",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.6.sp,
          color = colors.ink2
        )
        Text(
          text = "Peak: $maxHourly steps/hr",
          fontSize = 10.sp,
          color = colors.ink2
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(36.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
      ) {
        hourlySteps.forEachIndexed { hour, count ->
          val heightFraction = (count.toFloat() / maxHourly.toFloat()).coerceIn(0.08f, 1f)
          val isCurrent = hour == currentHour
          val barColor = when {
            isCurrent -> colors.accent
            count > 0 -> colors.accent.copy(alpha = 0.45f)
            else -> colors.surface2
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight(heightFraction)
              .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
              .background(barColor)
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("12 AM", fontSize = 9.sp, color = colors.ink2)
        Text("6 AM", fontSize = 9.sp, color = colors.ink2)
        Text("12 PM", fontSize = 9.sp, color = colors.ink2)
        Text("6 PM", fontSize = 9.sp, color = colors.ink2)
        Text("11 PM", fontSize = 9.sp, color = colors.ink2)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 7. Quick Walk Simulation / Manual Logging (Useful for testing & indoor walks)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Surface(
        modifier = Modifier
          .weight(1f)
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onAddManualSteps(250)
          },
        shape = RoundedCornerShape(8.dp),
        color = colors.surface2
      ) {
        Row(
          modifier = Modifier.padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = colors.accent, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("+250 Steps", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = colors.ink)
        }
      }

      Surface(
        modifier = Modifier
          .weight(1f)
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onAddManualSteps(1000)
          },
        shape = RoundedCornerShape(8.dp),
        color = colors.surface2
      ) {
        Row(
          modifier = Modifier.padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = colors.accent, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("+1,000 Walk", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = colors.ink)
        }
      }
    }
  }

  // Step Target Edit Dialog
  if (showGoalDialog) {
    var goalInput by remember { mutableStateOf(dailyStepGoal.toString()) }
    AlertDialog(
      onDismissRequest = { showGoalDialog = false },
      title = {
        Text("Set Daily Step Target", fontWeight = FontWeight.Bold, color = colors.ink)
      },
      text = {
        Column {
          Text(
            "Recommended baseline is 10,000 steps per day for cardiovascular and metabolic health.",
            fontSize = 13.sp,
            color = colors.ink2
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = goalInput,
            onValueChange = { goalInput = it.filter { ch -> ch.isDigit() } },
            label = { Text("Daily Steps") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(6000, 8000, 10000, 12000).forEach { preset ->
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = colors.surface2,
                modifier = Modifier
                  .weight(1f)
                  .clickable { goalInput = preset.toString() }
              ) {
                Text(
                  text = "${preset / 1000}k",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  textAlign = TextAlign.Center,
                  color = colors.ink,
                  modifier = Modifier.padding(vertical = 6.dp)
                )
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(
          onClick = {
            val parsed = goalInput.toIntOrNull()
            if (parsed != null && parsed > 500) {
              onUpdateGoal(parsed)
            }
            showGoalDialog = false
          }
        ) {
          Text("Save Goal", color = colors.accent, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showGoalDialog = false }) {
          Text("Cancel", color = colors.ink2)
        }
      }
    )
  }
}

@Composable
private fun ActivityMetricChip(
  icon: ImageVector,
  value: String,
  label: String,
  tint: Color,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = colors.surface2
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(tint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = tint,
          modifier = Modifier.size(13.dp)
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink
      )
      Text(
        text = label,
        fontSize = 10.sp,
        color = colors.ink2
      )
    }
  }
}
