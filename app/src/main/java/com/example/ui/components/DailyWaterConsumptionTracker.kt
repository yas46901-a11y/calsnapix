package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalCard
import com.example.ui.theme.CalTheme
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Modern, Apple Health / Cal AI style Daily Water Consumption Tracker.
 * Allows users to log glasses, track real-time hydration volume in Liters,
 * view interactive glass visualizers, and adjust daily hydration goals.
 */
@Composable
fun DailyWaterConsumptionTracker(
  glassesLogged: Int,
  targetGlasses: Int,
  onLogGlass: () -> Unit,
  onLogBottle: () -> Unit = {},
  onUndoGlass: () -> Unit = {},
  onToggleGlassIndex: (Int) -> Unit = {},
  onUpdateTarget: (Int) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  var showGoalDialog by remember { mutableStateOf(false) }

  val waterColor = Color(0xFF0284C7) // Sky / Hydration Blue
  val waterLightBg = Color(0xFFE0F2FE) // Sky-100

  val safeTarget = targetGlasses.coerceAtLeast(1)
  val fraction = (glassesLogged.toFloat() / safeTarget.toFloat()).coerceIn(0f, 1f)
  val percent = ((glassesLogged.toFloat() / safeTarget.toFloat()) * 100f).roundToInt()

  val animatedFraction by animateFloatAsState(
    targetValue = fraction,
    animationSpec = tween(durationMillis = 650),
    label = "water_progress"
  )

  val loggedLiters = String.format(Locale.US, "%.2f", glassesLogged * 0.25f)
  val targetLiters = String.format(Locale.US, "%.2f", safeTarget * 0.25f)

  CalCard(
    modifier = modifier.testTag("daily_water_consumption_tracker"),
    backgroundColor = colors.surface
  ) {
    // 1. Header with Title, Hydration Icon, and Goal Editor
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
            .background(waterLightBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = null,
            tint = waterColor,
            modifier = Modifier.size(17.dp)
          )
        }
        Column {
          Text(
            text = "DAILY HYDRATION",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            color = colors.ink2
          )
          Text(
            text = "$loggedLiters L of $targetLiters L goal",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = colors.ink2
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = if (percent >= 100) colors.accentSoft else waterLightBg,
          modifier = Modifier.clickable {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showGoalDialog = true
          }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = if (percent >= 100) Icons.Default.Check else Icons.Default.Edit,
              contentDescription = null,
              tint = if (percent >= 100) colors.accent else waterColor,
              modifier = Modifier.size(12.dp)
            )
            Text(
              text = if (percent >= 100) "Goal Met 🎉" else "Target: ${safeTarget}g",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (percent >= 100) colors.accent else waterColor
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Primary Hydration Ring & Detailed Metrics
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = "$glassesLogged",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.ink,
            letterSpacing = (-0.5).sp
          )
          Text(
            text = " / $safeTarget glasses",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink2,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        val adviceText = when {
          percent >= 100 -> "Optimal hydration achieved! 💧 Healthy cellular balance"
          percent >= 75 -> "Almost at your goal! Just one more glass"
          percent >= 50 -> "Halfway mark! Good hydration aids digestion"
          percent >= 25 -> "Good start. Keep sipping regularly"
          else -> "Hydrate now: kickstart your morning metabolism"
        }
        Text(
          text = adviceText,
          fontSize = 12.sp,
          color = if (percent >= 100) colors.accent else colors.ink2,
          fontWeight = if (percent >= 100) FontWeight.SemiBold else FontWeight.Normal
        )
      }

      // Circular Hydration Progress Ring
      Box(
        modifier = Modifier.size(62.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawArc(
            color = colors.line,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
          )
          drawArc(
            color = if (percent >= 100) Color(0xFF10B981) else waterColor,
            startAngle = -90f,
            sweepAngle = 360f * animatedFraction,
            useCenter = false,
            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
          )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "$percent%",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 3. Interactive Glass Visualizer (Grid/Row of Glasses)
    Text(
      text = "TAP TO LOG INDIVIDUAL GLASSES (250 ML EACH)",
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.6.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Display glass icons in a wrapped row (up to safeTarget)
    val displayCount = maxOf(safeTarget, glassesLogged)
    val rows = (displayCount + 7) / 8 // 8 per row
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      for (r in 0 until rows) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          for (c in 0 until 8) {
            val glassIndex = r * 8 + c
            if (glassIndex < displayCount) {
              val isFilled = glassIndex < glassesLogged
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(38.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isFilled) waterColor else colors.surface2)
                  .clickable {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onToggleGlassIndex(glassIndex)
                  },
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isFilled) Icons.Default.WaterDrop else Icons.Default.LocalDrink,
                  contentDescription = "Glass ${glassIndex + 1}",
                  tint = if (isFilled) Color.White else colors.ink2.copy(alpha = 0.5f),
                  modifier = Modifier.size(16.dp)
                )
              }
            } else {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 4. Action Buttons: + Glass, + Bottle, - Undo
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (glassesLogged > 0) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = colors.surface2,
          modifier = Modifier
            .clickable {
              view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
              onUndoGlass()
            }
            .testTag("undo_water_glass_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "−1 Glass",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink2
            )
          }
        }
      }

      Button(
        onClick = {
          view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
          onLogGlass()
        },
        modifier = Modifier
          .weight(1f)
          .testTag("log_water_glass_button"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = waterColor, contentColor = Color.White),
        contentPadding = PaddingValues(vertical = 10.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("+ Glass (250 ml)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = {
          view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
          onLogBottle()
        },
        modifier = Modifier
          .weight(1f)
          .testTag("log_water_bottle_button"),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, waterColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = waterColor),
        contentPadding = PaddingValues(vertical = 10.dp)
      ) {
        Icon(Icons.Default.LocalDrink, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("+ Bottle (500 ml)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }

  // Hydration Goal Customization Dialog
  if (showGoalDialog) {
    var customGoalInput by remember { mutableStateOf(safeTarget.toString()) }
    AlertDialog(
      onDismissRequest = { showGoalDialog = false },
      title = {
        Text("Daily Hydration Goal", fontWeight = FontWeight.Bold, color = colors.ink)
      },
      text = {
        Column {
          Text(
            "Medical authorities generally recommend 8 glasses (approx. 2.0 Liters) per day for active adults.",
            fontSize = 13.sp,
            color = colors.ink2
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = customGoalInput,
            onValueChange = { customGoalInput = it.filter { ch -> ch.isDigit() } },
            label = { Text("Glasses of Water (250ml each)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(6, 8, 10, 12).forEach { preset ->
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (customGoalInput == preset.toString()) waterLightBg else colors.surface2,
                border = if (customGoalInput == preset.toString()) BorderStroke(1.dp, waterColor) else null,
                modifier = Modifier
                  .weight(1f)
                  .clickable { customGoalInput = preset.toString() }
              ) {
                Text(
                  text = "${preset}g (${String.format("%.1f", preset * 0.25f)}L)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  textAlign = TextAlign.Center,
                  color = if (customGoalInput == preset.toString()) waterColor else colors.ink,
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
            val parsed = customGoalInput.toIntOrNull()
            if (parsed != null && parsed in 2..24) {
              onUpdateTarget(parsed)
            }
            showGoalDialog = false
          }
        ) {
          Text("Save Target", color = waterColor, fontWeight = FontWeight.Bold)
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
