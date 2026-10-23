package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.ui.theme.CalTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AhaarUiState
import com.example.AppDestination
import com.example.model.DayCalorieRecord
import com.example.model.WeightLogEntry
import com.example.ui.theme.AhaarPrimary
import com.example.ui.theme.AhaarPrimaryContainer
import com.example.ui.theme.AhaarPrimaryFixed
import com.example.ui.theme.AhaarSecondary
import com.example.ui.theme.AhaarSecondaryFixed
import com.example.ui.theme.AhaarTertiary
import com.example.ui.theme.AhaarTertiaryFixed

@Composable
fun ProgressScreen(
  uiState: AhaarUiState,
  onLogWater: () -> Unit,
  onLogWaterBottle: () -> Unit,
  onToggleWaterGlass: (Int) -> Unit,
  onLogDailyWeight: (Float, String) -> Unit = { _, _ -> },
  onNavigate: (AppDestination) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var selectedDayRecord by remember { mutableStateOf<DayCalorieRecord?>(null) }
  var showLogWeightDialog by remember { mutableStateOf(false) }
  var selectedWeightEntry by remember { mutableStateOf<WeightLogEntry?>(null) }
  var inputWeightKg by remember { mutableStateOf(String.format("%.1f", uiState.currentWeightKg)) }
  var weightNote by remember { mutableStateOf("") }

  val colors = CalTheme.colors
  val glassBorder = BorderStroke(1.dp, colors.line)
  val glassCardColors = CardDefaults.cardColors(containerColor = colors.surface)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(colors.bg)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Progress & Insights",
          style = MaterialTheme.typography.headlineLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Weekly holistic health & adherence analytics",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Surface(
        shape = RoundedCornerShape(50),
        color = AhaarPrimaryFixed
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.TrendingUp,
            contentDescription = null,
            tint = AhaarPrimary,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = "Week 1",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AhaarPrimary
          )
        }
      }
    }

    // Streak Hero Banner
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = glassCardColors,
      border = glassBorder,
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(AhaarPrimaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
          )
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = when (uiState.streakDays) {
              0 -> "0 Days on Track • Start Today! 🌱"
              1 -> "1 Day on Track • Day 1 Active! 🎯"
              else -> "${uiState.streakDays} Days on Track! 🏆"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = if (uiState.streakDays == 0) {
              "Streak resets when inactive for a week. Log your meals today to rebuild your active streak!"
            } else {
              "Consistently hitting your calorie targets with home-cooked Indian meals."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
          )
        }
      }
    }

    // Macro Adherence Score
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = glassCardColors,
      border = glassBorder,
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Macro Adherence Score",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Weekly Avg",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = AhaarPrimaryFixed,
            modifier = Modifier
              .clickable { onNavigate(AppDestination.NUTRITION_TRACKING) }
              .testTag("progress_view_macro_charts_button")
          ) {
            Text(
              text = "Daily Charts →",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = AhaarPrimary,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          MacroScoreRing(
            percentage = 85,
            label = "Carbs",
            color = AhaarPrimaryContainer
          )
          MacroScoreRing(
            percentage = 94,
            label = "Protein",
            color = AhaarTertiary
          )
          MacroScoreRing(
            percentage = 75,
            label = "Fats",
            color = AhaarSecondary
          )
        }
      }
    }

    // 30-Day Weight Trend & Progress (Recharts-style smooth curve canvas chart)
    val weightLogs = uiState.monthlyWeightLogs
    val totalChange = uiState.currentWeightKg - uiState.startingWeightKg
    val isLossProgress = totalChange < 0
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = glassCardColors,
      border = glassBorder,
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("monthly_weight_trend_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Card Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (weightLogs.size <= 1) "Weight Progress • Day 1" else "Weight Progress (Last Month)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (weightLogs.size <= 1) "Day 1 baseline recorded. Track daily to see your trend curve form." else "Daily weigh-in trend & trajectory",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Button(
            onClick = {
              inputWeightKg = String.format("%.1f", uiState.currentWeightKg)
              weightNote = ""
              showLogWeightDialog = true
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = AhaarPrimaryFixed,
              contentColor = AhaarPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("log_weight_button")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Log Weight", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }

        // Weight Milestones Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Starting
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("Starting", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
              Text("${uiState.startingWeightKg} kg", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
          }

          // Current
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isLossProgress) AhaarPrimaryFixed.copy(alpha = 0.5f) else AhaarSecondaryFixed.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("Current", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
              Text("${uiState.currentWeightKg} kg", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = if (isLossProgress) AhaarPrimary else AhaarSecondary)
            }
          }

          // Target
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("Goal", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
              Text("${uiState.targetWeightKg} kg", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AhaarPrimaryContainer)
            }
          }

          // Net Change
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isLossProgress) AhaarPrimaryFixed else AhaarSecondaryFixed,
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("Net", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (isLossProgress) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                  contentDescription = null,
                  tint = if (isLossProgress) AhaarPrimary else AhaarSecondary,
                  modifier = Modifier.size(13.dp)
                )
                Text(
                  text = String.format("%.1f kg", totalChange),
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (isLossProgress) AhaarPrimary else AhaarSecondary,
                  fontSize = 11.sp
                )
              }
            }
          }
        }

        // Selected Tooltip
        selectedWeightEntry?.let { entry ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = AhaarPrimaryFixed.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "${entry.dateLabel}: ${entry.weightKg} kg",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = AhaarPrimary
                )
                if (entry.note.isNotEmpty()) {
                  Text(
                    text = entry.note,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
              Text(
                text = String.format("%+.1f kg from start", entry.changeFromStartKg),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = AhaarPrimary
              )
            }
          }
        }

        // Recharts-Style Smooth Bezier Curve Canvas
        val minWeight = (weightLogs.minOfOrNull { it.weightKg } ?: 60f) - 1f
        val maxWeight = (weightLogs.maxOfOrNull { it.weightKg } ?: 70f) + 1f
        val range = (maxWeight - minWeight).coerceAtLeast(1f)

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          val primaryColor = AhaarPrimary
          val containerColor = AhaarPrimaryContainer

          Canvas(modifier = Modifier.fillMaxSize()) {
            if (weightLogs.isEmpty()) return@Canvas

            val width = size.width
            val height = size.height
            val stepX = if (weightLogs.size > 1) width / (weightLogs.size - 1) else width / 2f

            // Generate points
            val points = weightLogs.mapIndexed { index, entry ->
              val x = if (weightLogs.size > 1) index * stepX else width / 2f
              val normalizedY = if (range > 0f) (entry.weightKg - minWeight) / range else 0.5f
              val y = height - (normalizedY * height)
              androidx.compose.ui.geometry.Offset(x, y.coerceIn(8f, height - 8f))
            }

            // Path for smooth cubic Bezier line
            val linePath = Path()
            val fillPath = Path()

            linePath.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, height)
            fillPath.lineTo(points.first().x, points.first().y)

            for (i in 0 until points.size - 1) {
              val p0 = points[i]
              val p1 = points[i + 1]
              val controlX = (p0.x + p1.x) / 2f
              linePath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
              fillPath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
            }

            fillPath.lineTo(points.last().x, height)
            fillPath.close()

            // Draw Area Gradient under curve
            drawPath(
              path = fillPath,
              brush = Brush.verticalGradient(
                colors = listOf(
                  containerColor.copy(alpha = 0.35f),
                  containerColor.copy(alpha = 0.02f)
                ),
                startY = 0f,
                endY = height
              )
            )

            // Draw Curve Stroke
            drawPath(
              path = linePath,
              color = primaryColor,
              style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw points
            points.forEachIndexed { idx, point ->
              val isSelected = selectedWeightEntry?.id == weightLogs[idx].id
              drawCircle(
                color = Color.White,
                radius = if (isSelected) 7.dp.toPx() else 4.5.dp.toPx(),
                center = point
              )
              drawCircle(
                color = if (isSelected) containerColor else primaryColor,
                radius = if (isSelected) 5.dp.toPx() else 3.dp.toPx(),
                center = point
              )
            }
          }

          // Transparent tap targets across X-axis
          Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            weightLogs.forEach { entry ->
              Box(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxHeight()
                  .clickable { selectedWeightEntry = entry }
              )
            }
          }
        }

        // X-Axis Date Labels
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          weightLogs.forEach { entry ->
            Text(
              text = entry.dateLabel,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 9.sp,
              color = if (selectedWeightEntry?.id == entry.id) AhaarPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = if (selectedWeightEntry?.id == entry.id) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }
    }

    // Weekly Calorie Trends Bar Chart (Glassmorphic)
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = glassCardColors,
      border = glassBorder,
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Weekly Calorie Intake",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.surfaceContainer
          ) {
            Text(
              text = "Target: ${uiState.dailyCalorieTarget} kcal",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              fontSize = 10.sp
            )
          }
        }

        // Selected Day feedback pill
        selectedDayRecord?.let { record ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (record.isSurplus) AhaarPrimaryFixed else AhaarTertiaryFixed,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${record.dayLabel}: ${record.calories} kcal",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (record.isSurplus) AhaarPrimary else AhaarTertiary
              )
              Text(
                text = if (record.isSurplus) "Surplus (+100 kcal)" else "Under Target (-150 kcal)",
                fontSize = 11.sp,
                color = if (record.isSurplus) AhaarPrimary else AhaarTertiary
              )
            }
          }
        }

        // Custom Bar Chart in Jetpack Compose
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          uiState.weeklyCalorieRecords.forEach { record ->
            val isSelected = selectedDayRecord?.dayLabel == record.dayLabel
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Bottom,
              modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { selectedDayRecord = record }
            ) {
              if (record.isSurplus) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = AhaarPrimaryFixed,
                  modifier = Modifier.padding(bottom = 4.dp)
                ) {
                  Text(
                    text = "Festive",
                    style = MaterialTheme.typography.labelSmall,
                    color = AhaarPrimary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                  )
                }
              }

              // Calorie Bar
              val maxKcal = 2400f
              val barHeightPercent = (record.calories.toFloat() / maxKcal).coerceIn(0.1f, 1.0f)
              Box(
                modifier = Modifier
                  .width(if (isSelected) 26.dp else 22.dp)
                  .fillMaxHeight(barHeightPercent)
                  .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                  .background(
                    if (record.isSurplus) AhaarPrimaryContainer else AhaarTertiary
                  )
              )

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = record.dayLabel,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) AhaarPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }
    }

    // Hydration Tracker Card (Glassmorphic)
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = glassCardColors,
      border = glassBorder,
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.WaterDrop,
              contentDescription = null,
              tint = AhaarTertiary,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "Daily Hydration",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Text(
            text = "${uiState.waterGlassesLogged} of ${uiState.totalWaterGlassesTarget} glasses (${(uiState.waterGlassesLogged * 0.25f)}L / 2.0L)",
            style = MaterialTheme.typography.bodySmall,
            color = AhaarTertiary,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }

        // 8 Interactive Water Glasses (Tap any to toggle)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          (0 until uiState.totalWaterGlassesTarget).forEach { idx ->
            val isFilled = idx < uiState.waterGlassesLogged
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isFilled) AhaarTertiaryFixed else MaterialTheme.colorScheme.surfaceContainer)
                .clickable { onToggleWaterGlass(idx) }
                .testTag("water_glass_$idx"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.LocalDrink,
                contentDescription = "Glass ${idx + 1}",
                tint = if (isFilled) AhaarTertiary else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        // Quick log action buttons: +1 Glass & +1 Bottle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = onLogWater,
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("add_water_glass_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = AhaarTertiaryFixed,
              contentColor = AhaarTertiary
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "+1 Glass (250ml)",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }

          OutlinedButton(
            onClick = onLogWaterBottle,
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("add_water_bottle_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Opacity,
              contentDescription = null,
              tint = AhaarTertiary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "+Bottle (500ml)",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = AhaarTertiary
            )
          }
        }
      }
    }

    // CalSnapix Smart Insights
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Text(
        text = "CalSnapix Smart Insights",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      InsightCard(
        title = "Post-Festive Balance",
        content = "Saturday had a mild calorie surplus (+100 kcal). Your Sunday light khichdi dinner naturally balanced your weekly deficit!",
        icon = Icons.Default.CheckCircle,
        iconTint = AhaarPrimary
      )

      InsightCard(
        title = "Protein Boost Milestone",
        content = "You achieved 80g+ protein on 4 out of 7 days this week without any synthetic protein powders, relying on paneer, dal & sattu.",
        icon = Icons.Default.AutoAwesome,
        iconTint = AhaarTertiary
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
  }

  // Daily Weight Logging Dialog
  if (showLogWeightDialog) {
    AlertDialog(
      onDismissRequest = { showLogWeightDialog = false },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = AhaarPrimary)
          Text("Log Today's Weight", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "Track your weight regularly to calibrate your calorie plan.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable {
                  val cur = inputWeightKg.toFloatOrNull() ?: uiState.currentWeightKg
                  inputWeightKg = String.format("%.1f", (cur - 0.2f).coerceAtLeast(35f))
                },
              contentAlignment = Alignment.Center
            ) {
              Text("-", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            OutlinedTextField(
              value = inputWeightKg,
              onValueChange = { inputWeightKg = it },
              label = { Text("Weight (kg)") },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )

            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable {
                  val cur = inputWeightKg.toFloatOrNull() ?: uiState.currentWeightKg
                  inputWeightKg = String.format("%.1f", (cur + 0.2f).coerceAtMost(200f))
                },
              contentAlignment = Alignment.Center
            ) {
              Text("+", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
          }

          OutlinedTextField(
            value = weightNote,
            onValueChange = { weightNote = it },
            label = { Text("Note (optional)") },
            placeholder = { Text("e.g. Morning empty stomach") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val weight = inputWeightKg.toFloatOrNull() ?: uiState.currentWeightKg
            onLogDailyWeight(weight, weightNote)
            showLogWeightDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = AhaarPrimaryContainer)
        ) {
          Text("Save Entry")
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogWeightDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun MacroScoreRing(
  percentage: Int,
  label: String,
  color: Color
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Box(
      modifier = Modifier.size(68.dp),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator(
        progress = { 1.0f },
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        strokeWidth = 6.dp,
        strokeCap = StrokeCap.Round
      )
      CircularProgressIndicator(
        progress = { percentage / 100f },
        modifier = Modifier.fillMaxSize(),
        color = color,
        strokeWidth = 6.dp,
        strokeCap = StrokeCap.Round
      )
      Text(
        text = "$percentage%",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
private fun InsightCard(
  title: String,
  content: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color
) {
  val colors = CalTheme.colors
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = colors.surface),
    border = BorderStroke(1.dp, colors.line),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(iconTint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(20.dp)
        )
      }

      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = content,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 16.sp
        )
      }
    }
  }
}
