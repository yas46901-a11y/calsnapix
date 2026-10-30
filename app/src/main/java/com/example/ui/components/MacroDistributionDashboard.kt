package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ScannedMealWithNutrition
import com.example.model.LoggedMeal
import com.example.ui.theme.CalCard
import com.example.ui.theme.CalTheme
import kotlin.math.roundToInt

/**
 * 100% Native Jetpack Compose Macronutrient Distribution Dashboard.
 * Replaces legacy WebViews to eliminate Chromium variation seed initialization errors
 * and provide 60fps native hardware-accelerated charting.
 */
@Composable
fun MacroDistributionDashboard(
  scannedMeals: List<ScannedMealWithNutrition>,
  todayMeals: List<LoggedMeal>,
  carbsConsumedGrams: Int,
  proteinConsumedGrams: Int,
  fatsConsumedGrams: Int,
  carbsTargetGrams: Int = 220,
  proteinTargetGrams: Int = 75,
  fatsTargetGrams: Int = 55,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  var selectedTab by remember { mutableIntStateOf(0) }

  // Aggregate macros from today meals if passed parameters are 0
  val totalCarbs = if (carbsConsumedGrams > 0) carbsConsumedGrams else todayMeals.sumOf { it.carbsGrams }
  val totalProtein = if (proteinConsumedGrams > 0) proteinConsumedGrams else todayMeals.sumOf { it.proteinGrams }
  val totalFat = if (fatsConsumedGrams > 0) fatsConsumedGrams else todayMeals.sumOf { it.fatGrams }

  val targetCarbs = if (carbsTargetGrams > 0) carbsTargetGrams else 220
  val targetProtein = if (proteinTargetGrams > 0) proteinTargetGrams else 75
  val targetFats = if (fatsTargetGrams > 0) fatsTargetGrams else 55

  val totalMacroCals = (totalCarbs * 4) + (totalProtein * 4) + (totalFat * 9)

  val totalGrams = (totalCarbs + totalProtein + totalFat).coerceAtLeast(1)
  val carbsPct = (totalCarbs.toDouble() / totalGrams * 100).roundToInt()
  val proteinPct = (totalProtein.toDouble() / totalGrams * 100).roundToInt()
  val fatPct = (totalFat.toDouble() / totalGrams * 100).roundToInt()

  CalCard(
    modifier = modifier.testTag("macro_distribution_dashboard_card")
  ) {
    // 1. Header with title and tab selector
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
          imageVector = Icons.Default.PieChart,
          contentDescription = null,
          tint = colors.accent,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "MACRONUTRIENT DISTRIBUTION",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink2,
          letterSpacing = 0.8.sp
        )
      }

      // Compact Tab Switcher (Donut Chart vs Breakdown List)
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(colors.surface2)
          .padding(2.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (selectedTab == 0) colors.surface else Color.Transparent,
          modifier = Modifier
            .clickable {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              selectedTab = 0
            }
            .testTag("macro_tab_chart")
        ) {
          Text(
            text = "Chart",
            fontSize = 11.sp,
            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
            color = if (selectedTab == 0) colors.ink else colors.ink2,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (selectedTab == 1) colors.surface else Color.Transparent,
          modifier = Modifier
            .clickable {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              selectedTab = 1
            }
            .testTag("macro_tab_breakdown")
        ) {
          Text(
            text = "Meals",
            fontSize = 11.sp,
            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
            color = if (selectedTab == 1) colors.ink else colors.ink2,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 2. High-Level Summary Stat Tiles (Carbs, Protein, Fats)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      MacroSummaryCard(
        label = "Carbs",
        grams = totalCarbs,
        targetGrams = targetCarbs,
        pct = carbsPct,
        color = Color(0xFF3B82F6),
        modifier = Modifier.weight(1f)
      )
      MacroSummaryCard(
        label = "Protein",
        grams = totalProtein,
        targetGrams = targetProtein,
        pct = proteinPct,
        color = Color(0xFF10B981),
        modifier = Modifier.weight(1f)
      )
      MacroSummaryCard(
        label = "Fats",
        grams = totalFat,
        targetGrams = targetFats,
        pct = fatPct,
        color = Color(0xFFF59E0B),
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 3. Tab Content: Native Compose Donut Chart vs Meal Breakdown
    if (selectedTab == 0) {
      NativeComposeMacroDonutChart(
        carbsGrams = totalCarbs,
        proteinGrams = totalProtein,
        fatGrams = totalFat,
        totalCalories = totalMacroCals,
        modifier = Modifier
          .fillMaxWidth()
          .height(220.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(colors.surface2)
          .padding(14.dp)
      )
    } else {
      // Scanned Meals Breakdown List
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (scannedMeals.isNotEmpty()) {
          scannedMeals.take(4).forEach { item ->
            val n = item.nutritionalInfo
            val calories = n?.calories ?: 0
            ScannedMealMacroRow(
              dishName = item.scannedMeal.dishName,
              mealType = item.scannedMeal.mealType,
              calories = calories,
              carbsGrams = (n?.carbsGrams ?: 0.0).roundToInt(),
              proteinGrams = (n?.proteinGrams ?: 0.0).roundToInt(),
              fatGrams = (n?.fatGrams ?: 0.0).roundToInt()
            )
          }
        } else if (todayMeals.isNotEmpty()) {
          todayMeals.take(4).forEach { item ->
            ScannedMealMacroRow(
              dishName = item.title,
              mealType = item.mealType,
              calories = item.calories,
              carbsGrams = item.carbsGrams,
              proteinGrams = item.proteinGrams,
              fatGrams = item.fatGrams
            )
          }
        } else {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No scanned meals logged today. Use Camera scan to see meal-by-meal macro breakdown.",
              fontSize = 12.sp,
              color = colors.ink2,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 4. ICMR-NIN Healthy Macro Ratio Guidelines Footer
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = colors.surface2,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = colors.accent,
          modifier = Modifier.size(14.dp)
        )
        Text(
          text = "Healthy Indian Baseline (ICMR): 50-60% Carbs • 15-20% Protein • 20-30% Healthy Fats.",
          fontSize = 11.sp,
          color = colors.ink2,
          lineHeight = 15.sp
        )
      }
    }
  }
}

/**
 * 100% Native Jetpack Compose Canvas Donut Chart for Macronutrients.
 * Eliminates all webview dependencies and Chromium variation errors.
 */
@Composable
fun NativeComposeMacroDonutChart(
  carbsGrams: Int,
  proteinGrams: Int,
  fatGrams: Int,
  totalCalories: Int,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors

  val totalGrams = (carbsGrams + proteinGrams + fatGrams).coerceAtLeast(1)
  val carbsAngleTarget = (carbsGrams.toFloat() / totalGrams * 360f)
  val proteinAngleTarget = (proteinGrams.toFloat() / totalGrams * 360f)
  val fatAngleTarget = (fatGrams.toFloat() / totalGrams * 360f)

  val animatedCarbsAngle by animateFloatAsState(
    targetValue = carbsAngleTarget,
    animationSpec = tween(700, easing = FastOutSlowInEasing),
    label = "carbs_angle"
  )
  val animatedProteinAngle by animateFloatAsState(
    targetValue = proteinAngleTarget,
    animationSpec = tween(700, easing = FastOutSlowInEasing),
    label = "protein_angle"
  )
  val animatedFatAngle by animateFloatAsState(
    targetValue = fatAngleTarget,
    animationSpec = tween(700, easing = FastOutSlowInEasing),
    label = "fat_angle"
  )

  val carbsPct = (carbsGrams.toDouble() / totalGrams * 100).roundToInt()
  val proteinPct = (proteinGrams.toDouble() / totalGrams * 100).roundToInt()
  val fatPct = (fatGrams.toDouble() / totalGrams * 100).roundToInt()

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceAround
  ) {
    // 1. Donut Ring Canvas
    Box(
      modifier = Modifier.size(150.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(140.dp)) {
        val strokeWidth = 18.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        val arcSize = Size(diameter, diameter)

        // Track Background
        drawArc(
          color = Color(0xFFE2E8F0),
          startAngle = 0f,
          sweepAngle = 360f,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidth)
        )

        // Carbs Arc (Blue)
        if (animatedCarbsAngle > 0f) {
          drawArc(
            color = Color(0xFF3B82F6),
            startAngle = -90f,
            sweepAngle = animatedCarbsAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
          )
        }

        // Protein Arc (Emerald)
        if (animatedProteinAngle > 0f) {
          drawArc(
            color = Color(0xFF10B981),
            startAngle = -90f + animatedCarbsAngle,
            sweepAngle = animatedProteinAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
          )
        }

        // Fat Arc (Amber)
        if (animatedFatAngle > 0f) {
          drawArc(
            color = Color(0xFFF59E0B),
            startAngle = -90f + animatedCarbsAngle + animatedProteinAngle,
            sweepAngle = animatedFatAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
          )
        }
      }

      // Center Stat
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "$totalCalories",
          fontSize = 18.sp,
          fontWeight = FontWeight.ExtraBold,
          color = colors.ink
        )
        Text(
          text = "kcal",
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = colors.ink2
        )
      }
    }

    // 2. Clear Minimalist Legend Column
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(start = 8.dp)
    ) {
      MacroLegendItem(
        color = Color(0xFF3B82F6),
        name = "Carbs",
        grams = carbsGrams,
        percent = carbsPct
      )
      MacroLegendItem(
        color = Color(0xFF10B981),
        name = "Protein",
        grams = proteinGrams,
        percent = proteinPct
      )
      MacroLegendItem(
        color = Color(0xFFF59E0B),
        name = "Fats",
        grams = fatGrams,
        percent = fatPct
      )
    }
  }
}

@Composable
private fun MacroLegendItem(
  color: Color,
  name: String,
  grams: Int,
  percent: Int
) {
  val colors = CalTheme.colors
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Box(
      modifier = Modifier
        .size(10.dp)
        .clip(CircleShape)
        .background(color)
    )
    Column {
      Text(
        text = "$name • ${grams}g",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = colors.ink
      )
      Text(
        text = "$percent% of intake",
        fontSize = 10.sp,
        color = colors.ink2
      )
    }
  }
}

@Composable
private fun MacroSummaryCard(
  label: String,
  grams: Int,
  targetGrams: Int,
  pct: Int,
  color: Color,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = colors.surface2,
    border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = label,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = colors.ink2
        )
        Box(
          modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(color)
        )
      }
      Text(
        text = "${grams}g",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink
      )
      Text(
        text = "$pct% • goal ${targetGrams}g",
        fontSize = 10.sp,
        color = colors.ink2
      )
    }
  }
}

@Composable
private fun ScannedMealMacroRow(
  dishName: String,
  mealType: String,
  calories: Int,
  carbsGrams: Int,
  proteinGrams: Int,
  fatGrams: Int
) {
  val colors = CalTheme.colors
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = colors.surface2,
    border = BorderStroke(1.dp, colors.line),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = dishName,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = colors.ink
        )
        Text(
          text = "$mealType • $calories kcal",
          fontSize = 11.sp,
          color = colors.accent
        )
      }
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = Color(0xFF3B82F6).copy(alpha = 0.15f)
        ) {
          Text(
            text = "C: ${carbsGrams}g",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3B82F6),
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = Color(0xFF10B981).copy(alpha = 0.15f)
        ) {
          Text(
            text = "P: ${proteinGrams}g",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF10B981),
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = Color(0xFFF59E0B).copy(alpha = 0.15f)
        ) {
          Text(
            text = "F: ${fatGrams}g",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF59E0B),
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}
