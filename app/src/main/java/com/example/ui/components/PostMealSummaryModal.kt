package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CalTheme

/**
 * Useful Beyond Calories: Daily Post-Log Summary Screen (Point 6).
 *
 * Displays a single-screen clean breakdown with progress bars for
 * calories, protein, carbs, and fat.
 * Highlights strictly no more than 3 primary numbers:
 * - For "Build muscle": Highlights protein intake & target.
 * - For "Lose weight": Highlights calories remaining for the day.
 * - For "Maintain": Highlights calorie balance & energy equilibrium.
 */
data class MealSummaryData(
  val mealName: String,
  val mealCalories: Int,
  val totalCaloriesToday: Int,
  val calorieTarget: Int,
  val proteinTodayGrams: Int,
  val proteinTargetGrams: Int,
  val carbsTodayGrams: Int,
  val carbsTargetGrams: Int,
  val fatTodayGrams: Int,
  val fatTargetGrams: Int,
  val goalType: String = "lose_weight" // "lose_weight", "build_muscle", "maintain"
)

@Composable
fun PostMealSummaryModal(
  summary: MealSummaryData,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val remainingCals = (summary.calorieTarget - summary.totalCaloriesToday).coerceAtLeast(0)
  val isSurplus = summary.totalCaloriesToday > summary.calorieTarget

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = colors.surface),
      border = BorderStroke(1.dp, colors.line),
      modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("post_meal_summary_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Success Header Pill
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = colors.accentSoft,
          border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.35f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = colors.accent,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "Meal Logged Successfully",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent
            )
          }
        }

        // Title and meal addition callout
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = summary.mealName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink,
            textAlign = TextAlign.Center
          )
          Text(
            text = "+${summary.mealCalories} kcal added to daily balance",
            fontSize = 13.sp,
            color = colors.ink2,
            textAlign = TextAlign.Center
          )
        }

        // 3 HIGHLIGHTED METRICS (Point 6: "Keep the summary to one screen, with no more than 3 numbers highlighted")
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = colors.surface2,
          border = BorderStroke(1.dp, colors.line),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            when (summary.goalType) {
              "build_muscle" -> {
                // Metric 1: Highlighted Protein (Primary focus for Muscle Building)
                HighlightedStatItem(
                  number = "${summary.proteinTodayGrams}g",
                  label = "Protein Today",
                  sublabel = "Goal: ${summary.proteinTargetGrams}g",
                  color = colors.accent,
                  isHighlighted = true
                )

                Box(modifier = Modifier.size(1.dp, 36.dp).background(colors.line))

                // Metric 2: Calories Today
                HighlightedStatItem(
                  number = "${summary.totalCaloriesToday}",
                  label = "Calories Intake",
                  sublabel = "/ ${summary.calorieTarget} kcal",
                  color = colors.ink,
                  isHighlighted = false
                )

                Box(modifier = Modifier.size(1.dp, 36.dp).background(colors.line))

                // Metric 3: Carbs
                HighlightedStatItem(
                  number = "${summary.carbsTodayGrams}g",
                  label = "Carbs",
                  sublabel = "Fuel",
                  color = Color(0xFF64748B),
                  isHighlighted = false
                )
              }
              "lose_weight" -> {
                // Metric 1: Highlighted Calories Remaining (Primary focus for Fat Loss)
                HighlightedStatItem(
                  number = if (isSurplus) "+${summary.totalCaloriesToday - summary.calorieTarget}" else "$remainingCals",
                  label = if (isSurplus) "Over Target" else "Remaining",
                  sublabel = "Daily Deficit",
                  color = if (isSurplus) colors.warn else colors.accent,
                  isHighlighted = true
                )

                Box(modifier = Modifier.size(1.dp, 36.dp).background(colors.line))

                // Metric 2: Calories Consumed
                HighlightedStatItem(
                  number = "${summary.totalCaloriesToday}",
                  label = "Consumed",
                  sublabel = "of ${summary.calorieTarget} kcal",
                  color = colors.ink,
                  isHighlighted = false
                )

                Box(modifier = Modifier.size(1.dp, 36.dp).background(colors.line))

                // Metric 3: Protein Protected
                HighlightedStatItem(
                  number = "${summary.proteinTodayGrams}g",
                  label = "Protein",
                  sublabel = "Lean Mass",
                  color = colors.ink,
                  isHighlighted = false
                )
              }
              else -> {
                // "maintain"
                // Metric 1: Calorie Balance
                HighlightedStatItem(
                  number = "${summary.totalCaloriesToday}",
                  label = "Daily Calories",
                  sublabel = "Target: ${summary.calorieTarget}",
                  color = colors.accent,
                  isHighlighted = true
                )

                Box(modifier = Modifier.size(1.dp, 36.dp).background(colors.line))

                // Metric 2: Protein
                HighlightedStatItem(
                  number = "${summary.proteinTodayGrams}g",
                  label = "Protein",
                  sublabel = "/ ${summary.proteinTargetGrams}g",
                  color = colors.ink,
                  isHighlighted = false
                )

                Box(modifier = Modifier.size(1.dp, 36.dp).background(colors.line))

                // Metric 3: Fat
                HighlightedStatItem(
                  number = "${summary.fatTodayGrams}g",
                  label = "Fats",
                  sublabel = "/ ${summary.fatTargetGrams}g",
                  color = colors.ink,
                  isHighlighted = false
                )
              }
            }
          }
        }

        // 4 PROGRESS BARS: Calories, Protein, Carbs, Fat
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Daily Macro Balance",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink
          )

          // Calories bar
          MacroSummaryProgressBar(
            title = "Calories",
            current = summary.totalCaloriesToday,
            target = summary.calorieTarget,
            unit = "kcal",
            color = colors.accent
          )

          // Protein bar (highlighted for muscle goal)
          MacroSummaryProgressBar(
            title = "Protein",
            current = summary.proteinTodayGrams,
            target = summary.proteinTargetGrams,
            unit = "g",
            color = if (summary.goalType == "build_muscle") colors.accent else Color(0xFF10B981)
          )

          // Carbs bar
          MacroSummaryProgressBar(
            title = "Carbohydrates",
            current = summary.carbsTodayGrams,
            target = summary.carbsTargetGrams,
            unit = "g",
            color = Color(0xFF64748B)
          )

          // Fat bar
          MacroSummaryProgressBar(
            title = "Healthy Fats",
            current = summary.fatTodayGrams,
            target = summary.fatTargetGrams,
            unit = "g",
            color = Color(0xFFF59E0B)
          )
        }

        // Close / Done CTA Button
        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("dismiss_post_meal_summary_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
        ) {
          Text(
            text = "Continue to Dashboard",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
          )
        }
      }
    }
  }
}

@Composable
private fun HighlightedStatItem(
  number: String,
  label: String,
  sublabel: String,
  color: Color,
  isHighlighted: Boolean
) {
  val colors = CalTheme.colors
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(2.dp)
  ) {
    Text(
      text = number,
      fontSize = if (isHighlighted) 20.sp else 16.sp,
      fontWeight = if (isHighlighted) FontWeight.Black else FontWeight.Bold,
      color = color
    )
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
      color = colors.ink
    )
    Text(
      text = sublabel,
      fontSize = 10.sp,
      color = colors.ink2
    )
  }
}

@Composable
private fun MacroSummaryProgressBar(
  title: String,
  current: Int,
  target: Int,
  unit: String,
  color: Color
) {
  val colors = CalTheme.colors
  val progress = if (target > 0) (current.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f

  Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = colors.ink2
      )
      Text(
        text = "$current / $target $unit",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink
      )
    }
    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = color,
      trackColor = colors.line
    )
  }
}
