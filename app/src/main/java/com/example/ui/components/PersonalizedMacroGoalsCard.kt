package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AhaarUiState
import com.example.ui.theme.CalCard
import com.example.ui.theme.CalTheme
import kotlin.math.roundToInt

data class MacroPreset(
  val id: String,
  val title: String,
  val subtitle: String,
  val carbsPct: Int,
  val proteinPct: Int,
  val fatPct: Int
)

val standardMacroPresets = listOf(
  MacroPreset("balanced", "Balanced Indian", "50% C • 25% P • 25% F", 50, 25, 25),
  MacroPreset("high_protein", "High Protein Desi", "35% C • 40% P • 25% F", 35, 40, 25),
  MacroPreset("fat_loss", "Deficit & Fat Loss", "40% C • 35% P • 25% F", 40, 35, 25),
  MacroPreset("low_carb", "Low Carb / Diabetes", "25% C • 35% P • 40% F", 25, 35, 40)
)

/**
 * Reusable UI Component to set and display personalized daily calorie and macro goals.
 * Complements the Recharts nutrition visualization with interactive goal calibration,
 * dietary presets (Balanced, High Protein, Fat Loss, Low Carb), and real-time progress ratios.
 */
@Composable
fun PersonalizedMacroGoalsCard(
  uiState: AhaarUiState,
  onSaveGoals: (calorieTarget: Int, carbsGrams: Int, proteinGrams: Int, fatGrams: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  var showGoalEditorDialog by remember { mutableStateOf(false) }

  val targetKcal = uiState.dailyCalorieTarget
  val carbsTarget = uiState.carbsTargetGrams
  val proteinTarget = uiState.proteinTargetGrams
  val fatTarget = uiState.fatsTargetGrams

  val carbsEnergyKcal = carbsTarget * 4
  val proteinEnergyKcal = proteinTarget * 4
  val fatEnergyKcal = fatTarget * 9
  val totalCalculatedKcal = (carbsEnergyKcal + proteinEnergyKcal + fatEnergyKcal).coerceAtLeast(1)

  val carbsRatio = ((carbsEnergyKcal.toFloat() / totalCalculatedKcal) * 100f).roundToInt()
  val proteinRatio = ((proteinEnergyKcal.toFloat() / totalCalculatedKcal) * 100f).roundToInt()
  val fatRatio = (100 - carbsRatio - proteinRatio).coerceAtLeast(0)

  // Current consumption vs goal percentages
  val carbsConsumedPct = if (carbsTarget > 0) ((uiState.carbsConsumedGrams.toFloat() / carbsTarget.toFloat()) * 100).roundToInt() else 0
  val proteinConsumedPct = if (proteinTarget > 0) ((uiState.proteinConsumedGrams.toFloat() / proteinTarget.toFloat()) * 100).roundToInt() else 0
  val fatConsumedPct = if (fatTarget > 0) ((uiState.fatsConsumedGrams.toFloat() / fatTarget.toFloat()) * 100).roundToInt() else 0

  CalCard(modifier = modifier.testTag("personalized_macro_goals_card")) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Header with title & Edit Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = colors.accent,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "PERSONALIZED GOALS",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink2,
              letterSpacing = 1.sp
            )
          }
          Text(
            text = "Calorie & macro targets for your Indian lifestyle",
            fontSize = 12.sp,
            color = colors.ink2
          )
        }

        Surface(
          shape = RoundedCornerShape(999.dp),
          color = colors.accentSoft,
          modifier = Modifier
            .clickable {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              showGoalEditorDialog = true
            }
            .testTag("edit_personalized_goals_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "Customize Goals",
              tint = colors.accent,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Set Goals",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent
            )
          }
        }
      }

      // 2. Daily Calorie Target Display Row
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.surface2,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Daily Calorie Budget",
              fontSize = 11.sp,
              color = colors.ink2
            )
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "$targetKcal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink,
                letterSpacing = (-0.5).sp
              )
              Text(
                text = " kcal/day",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.ink2,
                modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.line)
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              horizontalAlignment = Alignment.End
            ) {
              Text(
                text = "Consumed: ${uiState.currentCaloriesConsumed} kcal",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "${(targetKcal - uiState.currentCaloriesConsumed).coerceAtLeast(0)} kcal remaining",
                fontSize = 10.sp,
                color = colors.accent,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      // 3. Recharts-Style Macro Target Proportion Bar
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
        ) {
          val cWeight = (carbsRatio.toFloat() / 100f).coerceIn(0.1f, 0.8f)
          val pWeight = (proteinRatio.toFloat() / 100f).coerceIn(0.1f, 0.8f)
          val fWeight = (fatRatio.toFloat() / 100f).coerceIn(0.1f, 0.8f)

          Box(modifier = Modifier.weight(cWeight).fillMaxHeight().background(colors.carbs))
          Box(modifier = Modifier.weight(pWeight).fillMaxHeight().background(colors.protein))
          Box(modifier = Modifier.weight(fWeight).fillMaxHeight().background(colors.fat))
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Carbs: $carbsRatio%", fontSize = 10.sp, color = colors.carbs, fontWeight = FontWeight.Bold)
          Text("Protein: $proteinRatio%", fontSize = 10.sp, color = colors.protein, fontWeight = FontWeight.Bold)
          Text("Fats: $fatRatio%", fontSize = 10.sp, color = colors.fat, fontWeight = FontWeight.Bold)
        }
      }

      // 4. Three Macro Target Indicator Boxes
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MacroGoalItem(
          label = "Carbs",
          targetGrams = carbsTarget,
          consumedGrams = uiState.carbsConsumedGrams,
          energyKcal = carbsEnergyKcal,
          percentage = carbsConsumedPct,
          color = colors.carbs,
          modifier = Modifier.weight(1f)
        )

        MacroGoalItem(
          label = "Protein",
          targetGrams = proteinTarget,
          consumedGrams = uiState.proteinConsumedGrams,
          energyKcal = proteinEnergyKcal,
          percentage = proteinConsumedPct,
          color = colors.protein,
          modifier = Modifier.weight(1f)
        )

        MacroGoalItem(
          label = "Fats",
          targetGrams = fatTarget,
          consumedGrams = uiState.fatsConsumedGrams,
          energyKcal = fatEnergyKcal,
          percentage = fatConsumedPct,
          color = colors.fat,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }

  // 5. Interactive Goal Calibration Dialog with Presets
  if (showGoalEditorDialog) {
    var editCalorieTarget by remember { mutableStateOf(targetKcal.toString()) }
    var editCarbsGrams by remember { mutableStateOf(carbsTarget.toString()) }
    var editProteinGrams by remember { mutableStateOf(proteinTarget.toString()) }
    var editFatGrams by remember { mutableStateOf(fatTarget.toString()) }
    var selectedPresetId by remember { mutableStateOf<String?>("balanced") }

    AlertDialog(
      onDismissRequest = { showGoalEditorDialog = false },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Tune, contentDescription = null, tint = colors.accent)
          Text("Set Personalized Goals", fontWeight = FontWeight.Bold, color = colors.ink, fontSize = 18.sp)
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text(
            text = "Select a nutrition archetype or customize exact calorie and macronutrient gram goals.",
            fontSize = 12.sp,
            color = colors.ink2
          )

          // Preset Selection Chips
          Text(
            text = "NUTRITION ARCHETYPES",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink2,
            letterSpacing = 0.8.sp
          )

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            standardMacroPresets.forEach { preset ->
              val isSelected = selectedPresetId == preset.id
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) colors.accentSoft else colors.surface2,
                border = if (isSelected) BorderStroke(1.5.dp, colors.accent) else BorderStroke(1.dp, colors.line),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    selectedPresetId = preset.id
                    val cal = editCalorieTarget.toIntOrNull() ?: targetKcal
                    val cKcal = cal * (preset.carbsPct / 100f)
                    val pKcal = cal * (preset.proteinPct / 100f)
                    val fKcal = cal * (preset.fatPct / 100f)

                    editCarbsGrams = (cKcal / 4f).roundToInt().toString()
                    editProteinGrams = (pKcal / 4f).roundToInt().toString()
                    editFatGrams = (fKcal / 9f).roundToInt().toString()
                  }
                  .testTag("macro_preset_${preset.id}")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = preset.title,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) colors.accent else colors.ink
                    )
                    Text(
                      text = preset.subtitle,
                      fontSize = 10.sp,
                      color = colors.ink2
                    )
                  }

                  if (isSelected) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = "Selected",
                      tint = colors.accent,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Daily Calorie Target Input with Stepper Buttons
          Text(
            text = "DAILY CALORIES",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink2,
            letterSpacing = 0.8.sp
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surface2)
                .clickable {
                  val cur = editCalorieTarget.toIntOrNull() ?: targetKcal
                  val next = (cur - 50).coerceAtLeast(1200)
                  editCalorieTarget = next.toString()
                },
              contentAlignment = Alignment.Center
            ) {
              Text("-50", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.ink)
            }

            OutlinedTextField(
              value = editCalorieTarget,
              onValueChange = {
                editCalorieTarget = it
                selectedPresetId = null
              },
              label = { Text("Daily Calories (kcal)") },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )

            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surface2)
                .clickable {
                  val cur = editCalorieTarget.toIntOrNull() ?: targetKcal
                  val next = (cur + 50).coerceAtMost(4000)
                  editCalorieTarget = next.toString()
                },
              contentAlignment = Alignment.Center
            ) {
              Text("+50", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.ink)
            }
          }

          // Macro Grams Inputs
          Text(
            text = "MACRONUTRIENT GRAM TARGETS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink2,
            letterSpacing = 0.8.sp
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = editCarbsGrams,
              onValueChange = {
                editCarbsGrams = it
                selectedPresetId = null
              },
              label = { Text("Carbs (g)") },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("input_carbs_goal")
            )

            OutlinedTextField(
              value = editProteinGrams,
              onValueChange = {
                editProteinGrams = it
                selectedPresetId = null
              },
              label = { Text("Protein (g)") },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("input_protein_goal")
            )

            OutlinedTextField(
              value = editFatGrams,
              onValueChange = {
                editFatGrams = it
                selectedPresetId = null
              },
              label = { Text("Fat (g)") },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("input_fat_goal")
            )
          }

          val calcC = editCarbsGrams.toIntOrNull() ?: carbsTarget
          val calcP = editProteinGrams.toIntOrNull() ?: proteinTarget
          val calcF = editFatGrams.toIntOrNull() ?: fatTarget
          val estKcal = (calcC * 4) + (calcP * 4) + (calcF * 9)

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.surface2,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Total Macro Energy:", fontSize = 11.sp, color = colors.ink2)
              Text("$estKcal kcal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.accent)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val cal = editCalorieTarget.toIntOrNull() ?: targetKcal
            val c = editCarbsGrams.toIntOrNull() ?: carbsTarget
            val p = editProteinGrams.toIntOrNull() ?: proteinTarget
            val f = editFatGrams.toIntOrNull() ?: fatTarget
            onSaveGoals(cal, c, p, f)
            showGoalEditorDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("save_personalized_goals_button")
        ) {
          Text("Save Goals", color = colors.bg, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showGoalEditorDialog = false }) {
          Text("Cancel", color = colors.ink2)
        }
      }
    )
  }
}

@Composable
private fun MacroGoalItem(
  label: String,
  targetGrams: Int,
  consumedGrams: Int,
  energyKcal: Int,
  percentage: Int,
  color: Color,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val progress = (consumedGrams.toFloat() / targetGrams.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
  val animProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(500), label = "prog")

  Surface(
    shape = RoundedCornerShape(10.dp),
    color = colors.surface2,
    border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = color
      )

      Text(
        text = "${targetGrams}g",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink
      )

      // Progress Track
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(5.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(colors.line)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(animProgress)
            .fillMaxHeight()
            .clip(RoundedCornerShape(3.dp))
            .background(color)
        )
      }

      Text(
        text = "$consumedGrams g / $percentage%",
        fontSize = 10.sp,
        color = colors.ink2,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
