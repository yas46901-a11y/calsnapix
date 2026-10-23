package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.AhaarUiState
import com.example.AppDestination
import com.example.data.local.ScannedMealWithNutrition
import com.example.model.LoggedMeal
import com.example.ui.components.PersonalizedMacroGoalsCard
import com.example.ui.theme.CalCard
import com.example.ui.theme.CalPrimaryButton
import com.example.ui.theme.CalSecondaryButton
import com.example.ui.theme.CalTheme
import kotlin.math.roundToInt

enum class MacroType(val label: String, val kcalPerGram: Int) {
  ALL("All Macros", 0),
  CARBS("Carbohydrates", 4),
  PROTEIN("Protein", 4),
  FAT("Fats", 9)
}

/**
 * Modern Daily Nutrition & Macronutrient Tracking Screen.
 * Inspired by Recharts declarative data-visualization:
 * - Interactive Donut chart with smooth arcs & segment selection
 * - Stacked horizontal macro breakdown bars for every scanned & logged meal
 * - Time-of-day grouped macro distribution chart
 * - Individual macro gauges with caloric contribution
 * - Scanned meal nutrition deep-dive modal
 */
@Composable
fun DailyNutritionTrackingScreen(
  uiState: AhaarUiState,
  onNavigate: (AppDestination) -> Unit,
  onUpdateMacroTargets: (carbs: Int, protein: Int, fat: Int) -> Unit = { _, _, _ -> },
  onSavePersonalizedGoals: (calorieTarget: Int, carbs: Int, protein: Int, fat: Int) -> Unit = { _, _, _, _ -> },
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val scrollState = rememberScrollState()

  // Hardware Back button support
  BackHandler {
    onNavigate(AppDestination.HOME)
  }

  // Active highlighted slice on the donut chart
  var selectedMacro by remember { mutableStateOf(MacroType.ALL) }
  var selectedMealForModal by remember { mutableStateOf<MealMacroItem?>(null) }
  var showTargetDialog by remember { mutableStateOf(false) }
  var selectedDayTab by remember { mutableIntStateOf(0) } // 0: Today, 1: Yesterday, 2: 2 Days Ago

  // Calculate aggregated macros
  val carbsGrams = uiState.carbsConsumedGrams
  val proteinGrams = uiState.proteinConsumedGrams
  val fatGrams = uiState.fatsConsumedGrams

  val carbsTarget = uiState.carbsTargetGrams
  val proteinTarget = uiState.proteinTargetGrams
  val fatTarget = uiState.fatsTargetGrams

  val carbsKcal = carbsGrams * 4
  val proteinKcal = proteinGrams * 4
  val fatKcal = fatGrams * 9
  val totalMacroKcal = (carbsKcal + proteinKcal + fatKcal).coerceAtLeast(1)

  val carbsPct = ((carbsKcal.toFloat() / totalMacroKcal) * 100f).roundToInt()
  val proteinPct = ((proteinKcal.toFloat() / totalMacroKcal) * 100f).roundToInt()
  val fatPct = (100 - carbsPct - proteinPct).coerceAtLeast(0)

  // Merge today meals and scanned meal logs
  val mealItems = remember(uiState.todayMeals, uiState.scannedMealLogs) {
    buildMealMacroList(uiState.todayMeals, uiState.scannedMealLogs)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(colors.bg)
      .verticalScroll(scrollState)
      .padding(horizontal = CalTheme.sidePadding, vertical = 12.dp)
      .testTag("daily_nutrition_tracking_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar: Back Button, Title & Target Calibration
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        IconButton(
          onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onNavigate(AppDestination.HOME)
          },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(colors.surface2)
            .testTag("nutrition_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = colors.ink,
            modifier = Modifier.size(20.dp)
          )
        }

        Column {
          Text(
            text = "Daily Nutrition",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink,
            letterSpacing = (-0.5).sp
          )
          Text(
            text = "Macronutrient breakdown from scanned meals",
            fontSize = 12.sp,
            color = colors.ink2
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(999.dp),
        color = colors.surface2,
        border = BorderStroke(1.dp, colors.line),
        modifier = Modifier
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            showTargetDialog = true
          }
          .testTag("calibrate_macro_targets_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Calibrate Goals",
            tint = colors.ink,
            modifier = Modifier.size(15.dp)
          )
          Text(
            text = "Goals",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink
          )
        }
      }
    }

    // Date Switcher Pills: Today, Yesterday, 2 Days Ago
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(colors.surface2)
        .padding(4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      val days = listOf("Today", "Yesterday", "2 Days Ago")
      days.forEachIndexed { index, label ->
        val isSelected = selectedDayTab == index
        Box(
          modifier = Modifier
            .weight(1f)
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) colors.surface else Color.Transparent)
            .clickable {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              selectedDayTab = index
            }
            .testTag("day_tab_$index"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) colors.ink else colors.ink2
          )
        }
      }
    }

    // 1. RECHARTS-STYLE INTERACTIVE DONUT CHART
    CalCard(modifier = Modifier.testTag("recharts_donut_card")) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "MACRONUTRIENT DISTRIBUTION",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink2,
              letterSpacing = 1.sp
            )
            Text(
              text = "Tap a segment to inspect caloric balance",
              fontSize = 12.sp,
              color = colors.ink2
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.accentSoft
          ) {
            Text(
              text = "${uiState.currentCaloriesConsumed} kcal total",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        // Interactive Donut Chart Canvas
        Box(
          modifier = Modifier
            .size(220.dp)
            .testTag("recharts_donut_canvas"),
          contentAlignment = Alignment.Center
        ) {
          RechartsInteractiveDonut(
            carbsGrams = carbsGrams,
            proteinGrams = proteinGrams,
            fatGrams = fatGrams,
            selectedMacro = selectedMacro,
            carbsColor = colors.carbs,
            proteinColor = colors.protein,
            fatColor = colors.fat,
            trackColor = colors.surface2,
            onSelectMacro = { macro ->
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              selectedMacro = if (selectedMacro == macro) MacroType.ALL else macro
            }
          )

          // Center Display based on selection
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
          ) {
            when (selectedMacro) {
              MacroType.ALL -> {
                Text(
                  text = "${uiState.currentCaloriesConsumed}",
                  fontSize = 36.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.ink,
                  letterSpacing = (-1).sp
                )
                Text(
                  text = "TOTAL KCAL",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = colors.ink2,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "$carbsPct% C • $proteinPct% P • $fatPct% F",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = colors.accent
                )
              }
              MacroType.CARBS -> {
                Text(
                  text = "${carbsGrams}g",
                  fontSize = 34.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.carbs,
                  letterSpacing = (-1).sp
                )
                Text(
                  text = "CARBOHYDRATES",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = colors.ink2,
                  letterSpacing = 0.8.sp
                )
                Text(
                  text = "$carbsKcal kcal ($carbsPct%)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = colors.ink2
                )
              }
              MacroType.PROTEIN -> {
                Text(
                  text = "${proteinGrams}g",
                  fontSize = 34.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.protein,
                  letterSpacing = (-1).sp
                )
                Text(
                  text = "PROTEIN",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = colors.ink2,
                  letterSpacing = 0.8.sp
                )
                Text(
                  text = "$proteinKcal kcal ($proteinPct%)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = colors.ink2
                )
              }
              MacroType.FAT -> {
                Text(
                  text = "${fatGrams}g",
                  fontSize = 34.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.fat,
                  letterSpacing = (-1).sp
                )
                Text(
                  text = "HEALTHY FATS",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = colors.ink2,
                  letterSpacing = 0.8.sp
                )
                Text(
                  text = "$fatKcal kcal ($fatPct%)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = colors.ink2
                )
              }
            }
          }
        }

        // Recharts Interactive Legend (Filter pills)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          RechartsLegendItem(
            label = "Carbs",
            grams = carbsGrams,
            targetGrams = carbsTarget,
            percentage = carbsPct,
            color = colors.carbs,
            isSelected = selectedMacro == MacroType.CARBS,
            onClick = {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              selectedMacro = if (selectedMacro == MacroType.CARBS) MacroType.ALL else MacroType.CARBS
            }
          )

          RechartsLegendItem(
            label = "Protein",
            grams = proteinGrams,
            targetGrams = proteinTarget,
            percentage = proteinPct,
            color = colors.protein,
            isSelected = selectedMacro == MacroType.PROTEIN,
            onClick = {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              selectedMacro = if (selectedMacro == MacroType.PROTEIN) MacroType.ALL else MacroType.PROTEIN
            }
          )

          RechartsLegendItem(
            label = "Fats",
            grams = fatGrams,
            targetGrams = fatTarget,
            percentage = fatPct,
            color = colors.fat,
            isSelected = selectedMacro == MacroType.FAT,
            onClick = {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              selectedMacro = if (selectedMacro == MacroType.FAT) MacroType.ALL else MacroType.FAT
            }
          )
        }
      }
    }

    // 2. PERSONALIZED CALORIE & MACRO GOALS COMPONENT
    PersonalizedMacroGoalsCard(
      uiState = uiState,
      onSaveGoals = { cal, c, p, f ->
        onSavePersonalizedGoals(cal, c, p, f)
        onUpdateMacroTargets(c, p, f)
      }
    )

    // 3. DETAILED MACRO PROGRESS GAUGES
    Text(
      text = "DAILY TARGET PROGRESS",
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink2,
      letterSpacing = 1.sp,
      modifier = Modifier.padding(start = 4.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      MacroProgressCard(
        title = "Carbohydrates",
        consumedGrams = carbsGrams,
        targetGrams = carbsTarget,
        kcal = carbsKcal,
        color = colors.carbs,
        sources = "Rotis, Dosa, Poha, Veggies & Lentils",
        testTag = "macro_card_carbs"
      )

      MacroProgressCard(
        title = "Protein",
        consumedGrams = proteinGrams,
        targetGrams = proteinTarget,
        kcal = proteinKcal,
        color = colors.protein,
        sources = "Paneer, Dal, Sattu, Sprouts, Chana",
        testTag = "macro_card_protein"
      )

      MacroProgressCard(
        title = "Fats",
        consumedGrams = fatGrams,
        targetGrams = fatTarget,
        kcal = fatKcal,
        color = colors.fat,
        sources = "Desi Ghee, Seeds, Makhana, Cooking Oil",
        testTag = "macro_card_fats"
      )
    }

    // 3. SCANNED MEALS STACKED MACRO BREAKDOWN (RECHARTS-STYLE HORIZONTAL BARS)
    CalCard(modifier = Modifier.testTag("scanned_meals_macro_breakdown_card")) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "SCANNED MEALS MACRO BARS",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink2,
              letterSpacing = 1.sp
            )
            Text(
              text = "Stacked macro composition for today's meals",
              fontSize = 12.sp,
              color = colors.ink2
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.surface2
          ) {
            Text(
              text = "${mealItems.size} meals",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = colors.ink,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        if (mealItems.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(100.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(colors.surface2),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.Restaurant, contentDescription = null, tint = colors.ink2)
              Spacer(modifier = Modifier.height(6.dp))
              Text("No meals logged yet today", fontSize = 13.sp, color = colors.ink2)
            }
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            mealItems.forEach { item ->
              MealStackedMacroRow(
                item = item,
                carbsColor = colors.carbs,
                proteinColor = colors.protein,
                fatColor = colors.fat,
                onClick = {
                  view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                  selectedMealForModal = item
                }
              )
            }
          }
        }
      }
    }

    // 4. TIME-OF-DAY MACRO GROUPED BAR CHART
    CalCard(modifier = Modifier.testTag("time_of_day_macro_card")) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "TIME-OF-DAY MACRO DISTRIBUTION",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink2,
              letterSpacing = 1.sp
            )
            Text(
              text = "Macro pacing across meal windows",
              fontSize = 12.sp,
              color = colors.ink2
            )
          }

          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(18.dp)
          )
        }

        TimeOfDayGroupedBarChart(
          meals = mealItems,
          carbsColor = colors.carbs,
          proteinColor = colors.protein,
          fatColor = colors.fat,
          trackColor = colors.surface2
        )
      }
    }

    // 5. MICRONUTRIENT & DIETARY QUALITY INSIGHTS
    CalCard(modifier = Modifier.testTag("nutrition_insights_card")) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = "AI NUTRITIONIST INSIGHTS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink,
            letterSpacing = 0.5.sp
          )
        }

        // Insight Pills
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = colors.surface2,
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Est. Dietary Fiber", fontSize = 10.sp, color = colors.ink2)
              Text("24g / 30g", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
              Text("80% of daily fiber", fontSize = 10.sp, color = colors.accent)
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = colors.surface2,
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Protein Energy", fontSize = 10.sp, color = colors.ink2)
              Text("${proteinPct}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.protein)
              Text("Optimal for muscle tone", fontSize = 10.sp, color = colors.ink2)
            }
          }
        }

        Text(
          text = if (proteinPct >= 20) {
            "✓ Excellent macronutrient pacing! Your protein intake is robust and evenly distributed between lunch and dinner, preventing late-night cravings."
          } else {
            "Tip: You are close to your protein target! Consider adding roasted chana, paneer, or Greek yogurt to your evening snack to reach the optimal 25% protein ratio."
          },
          fontSize = 12.sp,
          color = colors.ink2,
          lineHeight = 17.sp
        )
      }
    }

    // 6. QUICK ACTIONS: SCAN MEAL & LOG VOICE
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
          onNavigate(AppDestination.SCAN)
        },
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("nutrition_scan_meal_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = colors.ink,
          contentColor = colors.bg
        ),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Scan Meal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
      }

      OutlinedButton(
        onClick = {
          view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
          onNavigate(AppDestination.RECIPES)
        },
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("nutrition_kitchen_button"),
        border = BorderStroke(1.dp, colors.line),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = colors.accent, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("MyCal AI", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = colors.ink)
      }
    }
  }

  // DIALOG 1: SCANNED MEAL MACRO DETAIL MODAL (RECHARTS TOOLTIP POPUP)
  selectedMealForModal?.let { meal ->
    AlertDialog(
      onDismissRequest = { selectedMealForModal = null },
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = meal.dishName,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
            Text(
              text = "${meal.mealType} • ${meal.time}",
              fontSize = 12.sp,
              color = colors.ink2
            )
          }

          IconButton(
            onClick = { selectedMealForModal = null },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.ink2)
          }
        }
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Calories Header Banner
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surface2,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Total Meal Energy", fontSize = 11.sp, color = colors.ink2)
                Text("${meal.calories} kcal", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.ink)
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = colors.accentSoft
              ) {
                Text(
                  text = meal.scanSource,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.accent,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }
          }

          // Macro Breakdown Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            MacroMetricBox(
              label = "Carbs",
              grams = meal.carbsGrams,
              kcal = meal.carbsGrams * 4,
              color = colors.carbs,
              modifier = Modifier.weight(1f)
            )
            MacroMetricBox(
              label = "Protein",
              grams = meal.proteinGrams,
              kcal = meal.proteinGrams * 4,
              color = colors.protein,
              modifier = Modifier.weight(1f)
            )
            MacroMetricBox(
              label = "Fats",
              grams = meal.fatGrams,
              kcal = meal.fatGrams * 9,
              color = colors.fat,
              modifier = Modifier.weight(1f)
            )
          }

          // Recharts-Style Stacked Meal Bar
          val mealTotalGrams = (meal.carbsGrams + meal.proteinGrams + meal.fatGrams).coerceAtLeast(1)
          val cWeight = meal.carbsGrams.toFloat() / mealTotalGrams
          val pWeight = meal.proteinGrams.toFloat() / mealTotalGrams
          val fWeight = meal.fatGrams.toFloat() / mealTotalGrams

          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
            ) {
              if (cWeight > 0f) {
                Box(
                  modifier = Modifier
                    .weight(cWeight)
                    .fillMaxHeight()
                    .background(colors.carbs)
                )
              }
              if (pWeight > 0f) {
                Box(
                  modifier = Modifier
                    .weight(pWeight)
                    .fillMaxHeight()
                    .background(colors.protein)
                )
              }
              if (fWeight > 0f) {
                Box(
                  modifier = Modifier
                    .weight(fWeight)
                    .fillMaxHeight()
                    .background(colors.fat)
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Carbs: ${(cWeight * 100).roundToInt()}%", fontSize = 10.sp, color = colors.carbs)
              Text("Protein: ${(pWeight * 100).roundToInt()}%", fontSize = 10.sp, color = colors.protein)
              Text("Fat: ${(fWeight * 100).roundToInt()}%", fontSize = 10.sp, color = colors.fat)
            }
          }

          // Chef Note
          if (meal.chefNote.isNotEmpty()) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = colors.surface2,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  Icons.Default.Info,
                  contentDescription = null,
                  tint = colors.ink2,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = meal.chefNote,
                  fontSize = 11.sp,
                  color = colors.ink2,
                  lineHeight = 15.sp
                )
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { selectedMealForModal = null },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink)
        ) {
          Text("Done", color = colors.bg)
        }
      }
    )
  }

  // DIALOG 2: CALIBRATE MACRO TARGETS DIALOG
  if (showTargetDialog) {
    var editCarbs by remember { mutableStateOf(carbsTarget.toString()) }
    var editProtein by remember { mutableStateOf(proteinTarget.toString()) }
    var editFat by remember { mutableStateOf(fatTarget.toString()) }

    AlertDialog(
      onDismissRequest = { showTargetDialog = false },
      title = {
        Text("Calibrate Macro Targets", fontWeight = FontWeight.Bold, color = colors.ink)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "Customize your daily macronutrient gram targets based on your dietary and fitness blueprint.",
            fontSize = 12.sp,
            color = colors.ink2
          )

          OutlinedTextField(
            value = editCarbs,
            onValueChange = { editCarbs = it },
            label = { Text("Carbohydrates Goal (grams)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = editProtein,
            onValueChange = { editProtein = it },
            label = { Text("Protein Goal (grams)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = editFat,
            onValueChange = { editFat = it },
            label = { Text("Fats Goal (grams)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          val totalEstKcal = (editCarbs.toIntOrNull() ?: 0) * 4 +
              (editProtein.toIntOrNull() ?: 0) * 4 +
              (editFat.toIntOrNull() ?: 0) * 9
          Text(
            text = "Estimated Daily Intake: $totalEstKcal kcal",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val c = editCarbs.toIntOrNull() ?: carbsTarget
            val p = editProtein.toIntOrNull() ?: proteinTarget
            val f = editFat.toIntOrNull() ?: fatTarget
            onUpdateMacroTargets(c, p, f)
            showTargetDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink)
        ) {
          Text("Save Targets", color = colors.bg)
        }
      },
      dismissButton = {
        TextButton(onClick = { showTargetDialog = false }) {
          Text("Cancel", color = colors.ink2)
        }
      }
    )
  }
}

/**
 * Interactive Recharts-style Donut Chart drawn using Jetpack Compose Canvas.
 * Supports active segment highlighting, smooth animations, and touch selection.
 */
@Composable
private fun RechartsInteractiveDonut(
  carbsGrams: Int,
  proteinGrams: Int,
  fatGrams: Int,
  selectedMacro: MacroType,
  carbsColor: Color,
  proteinColor: Color,
  fatColor: Color,
  trackColor: Color,
  onSelectMacro: (MacroType) -> Unit
) {
  val totalMacroKcal = (carbsGrams * 4 + proteinGrams * 4 + fatGrams * 9).coerceAtLeast(1).toFloat()

  val carbsSweep = ((carbsGrams * 4f) / totalMacroKcal) * 360f
  val proteinSweep = ((proteinGrams * 4f) / totalMacroKcal) * 360f
  val fatSweep = (360f - carbsSweep - proteinSweep).coerceAtLeast(0f)

  // Animated sweep angles for smooth Recharts loading feel
  val animCarbsSweep by animateFloatAsState(targetValue = carbsSweep, animationSpec = tween(700), label = "c_sweep")
  val animProteinSweep by animateFloatAsState(targetValue = proteinSweep, animationSpec = tween(700), label = "p_sweep")
  val animFatSweep by animateFloatAsState(targetValue = fatSweep, animationSpec = tween(700), label = "f_sweep")

  Canvas(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        // Cycle selection on canvas tap
        val next = when (selectedMacro) {
          MacroType.ALL -> MacroType.CARBS
          MacroType.CARBS -> MacroType.PROTEIN
          MacroType.PROTEIN -> MacroType.FAT
          MacroType.FAT -> MacroType.ALL
        }
        onSelectMacro(next)
      }
  ) {
    val canvasSize = size.minDimension
    val strokeWidthDefault = 20.dp.toPx()
    val strokeWidthSelected = 27.dp.toPx()

    val centerOffset = Offset(size.width / 2f, size.height / 2f)
    val radius = (canvasSize - strokeWidthSelected) / 2f
    val arcTopLeft = Offset(centerOffset.x - radius, centerOffset.y - radius)
    val arcSize = Size(radius * 2f, radius * 2f)

    // Background track ring
    drawCircle(
      color = trackColor,
      radius = radius,
      center = centerOffset,
      style = Stroke(width = strokeWidthDefault)
    )

    var currentStartAngle = -90f

    // 1. Carbs Arc
    val carbsStroke = if (selectedMacro == MacroType.CARBS) strokeWidthSelected else strokeWidthDefault
    val carbsAlpha = if (selectedMacro == MacroType.ALL || selectedMacro == MacroType.CARBS) 1f else 0.35f
    drawArc(
      color = carbsColor.copy(alpha = carbsAlpha),
      startAngle = currentStartAngle,
      sweepAngle = animCarbsSweep,
      useCenter = false,
      topLeft = arcTopLeft,
      size = arcSize,
      style = Stroke(width = carbsStroke, cap = StrokeCap.Round)
    )
    currentStartAngle += animCarbsSweep

    // 2. Protein Arc
    val proteinStroke = if (selectedMacro == MacroType.PROTEIN) strokeWidthSelected else strokeWidthDefault
    val proteinAlpha = if (selectedMacro == MacroType.ALL || selectedMacro == MacroType.PROTEIN) 1f else 0.35f
    drawArc(
      color = proteinColor.copy(alpha = proteinAlpha),
      startAngle = currentStartAngle,
      sweepAngle = animProteinSweep,
      useCenter = false,
      topLeft = arcTopLeft,
      size = arcSize,
      style = Stroke(width = proteinStroke, cap = StrokeCap.Round)
    )
    currentStartAngle += animProteinSweep

    // 3. Fat Arc
    val fatStroke = if (selectedMacro == MacroType.FAT) strokeWidthSelected else strokeWidthDefault
    val fatAlpha = if (selectedMacro == MacroType.ALL || selectedMacro == MacroType.FAT) 1f else 0.35f
    drawArc(
      color = fatColor.copy(alpha = fatAlpha),
      startAngle = currentStartAngle,
      sweepAngle = animFatSweep,
      useCenter = false,
      topLeft = arcTopLeft,
      size = arcSize,
      style = Stroke(width = fatStroke, cap = StrokeCap.Round)
    )
  }
}

/**
 * Recharts-style Legend item acting as an interactive toggle filter.
 */
@Composable
private fun RechartsLegendItem(
  label: String,
  grams: Int,
  targetGrams: Int,
  percentage: Int,
  color: Color,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val colors = CalTheme.colors
  val animScale by animateFloatAsState(
    targetValue = if (isSelected) 1.05f else 1.0f,
    animationSpec = spring(stiffness = Spring.StiffnessMedium),
    label = "legend_scale"
  )

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) color.copy(alpha = 0.15f) else colors.surface2,
    border = if (isSelected) BorderStroke(1.5.dp, color) else BorderStroke(1.dp, colors.line),
    modifier = Modifier
      .clickable(onClick = onClick)
      .padding(horizontal = 4.dp)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
        )
        Text(
          text = label,
          fontSize = 11.sp,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
          color = if (isSelected) colors.ink else colors.ink2
        )
      }
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "${grams}g",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink
      )
      Text(
        text = "$percentage%",
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = color
      )
    }
  }
}

/**
 * Recharts-style Macro Progress Card with dynamic completion percentage and calories.
 */
@Composable
private fun MacroProgressCard(
  title: String,
  consumedGrams: Int,
  targetGrams: Int,
  kcal: Int,
  color: Color,
  sources: String,
  testTag: String
) {
  val colors = CalTheme.colors
  val progress = if (targetGrams > 0) (consumedGrams.toFloat() / targetGrams.toFloat()).coerceIn(0f, 1f) else 0f
  val animProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(600), label = "progress_bar")
  val percentInt = (progress * 100).roundToInt()
  val remaining = (targetGrams - consumedGrams).coerceAtLeast(0)

  CalCard(modifier = Modifier.testTag(testTag)) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
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
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(color)
          )
          Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (consumedGrams >= targetGrams) colors.accentSoft else colors.surface2
        ) {
          Text(
            text = if (consumedGrams >= targetGrams) "Goal Met ✓" else "$remaining g left",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (consumedGrams >= targetGrams) colors.accent else colors.ink2,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      // Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(colors.surface2)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(animProgress)
            .fillMaxHeight()
            .clip(RoundedCornerShape(4.dp))
            .background(color)
        )
      }

      // Numerals Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${consumedGrams}g of ${targetGrams}g ($percentInt%)",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = colors.ink
        )
        Text(
          text = "$kcal kcal",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = color
        )
      }

      Text(
        text = "Key sources: $sources",
        fontSize = 11.sp,
        color = colors.ink2,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

/**
 * Recharts Stacked Horizontal Bar for a single meal.
 */
@Composable
private fun MealStackedMacroRow(
  item: MealMacroItem,
  carbsColor: Color,
  proteinColor: Color,
  fatColor: Color,
  onClick: () -> Unit
) {
  val colors = CalTheme.colors
  val totalGrams = (item.carbsGrams + item.proteinGrams + item.fatGrams).coerceAtLeast(1)
  val cWeight = item.carbsGrams.toFloat() / totalGrams
  val pWeight = item.proteinGrams.toFloat() / totalGrams
  val fWeight = item.fatGrams.toFloat() / totalGrams

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = colors.surface2,
    border = BorderStroke(1.dp, colors.line),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("meal_stacked_row_${item.id}")
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          if (item.imageUrl.isNotEmpty()) {
            AsyncImage(
              model = item.imageUrl,
              contentDescription = item.dishName,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
            )
          } else {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.accentSoft),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Restaurant,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Column {
            Text(
              text = item.dishName,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "${item.mealType} • ${item.time}",
              fontSize = 11.sp,
              color = colors.ink2
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "${item.calories} kcal",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
          Text(
            text = "${item.scanSource}",
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.accent
          )
        }
      }

      // Recharts-Style Stacked Macro Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(5.dp))
      ) {
        if (cWeight > 0f) {
          Box(
            modifier = Modifier
              .weight(cWeight)
              .fillMaxHeight()
              .background(carbsColor)
          )
        }
        if (pWeight > 0f) {
          Box(
            modifier = Modifier
              .weight(pWeight)
              .fillMaxHeight()
              .background(proteinColor)
          )
        }
        if (fWeight > 0f) {
          Box(
            modifier = Modifier
              .weight(fWeight)
              .fillMaxHeight()
              .background(fatColor)
          )
        }
      }

      // Macro Grams Sub-row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Carbs: ${item.carbsGrams}g", fontSize = 10.sp, color = carbsColor, fontWeight = FontWeight.SemiBold)
        Text("Protein: ${item.proteinGrams}g", fontSize = 10.sp, color = proteinColor, fontWeight = FontWeight.SemiBold)
        Text("Fats: ${item.fatGrams}g", fontSize = 10.sp, color = fatColor, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

/**
 * Recharts Time-of-Day Grouped Bar Chart showing Carbs, Protein, and Fat across meal windows.
 */
@Composable
private fun TimeOfDayGroupedBarChart(
  meals: List<MealMacroItem>,
  carbsColor: Color,
  proteinColor: Color,
  fatColor: Color,
  trackColor: Color
) {
  val colors = CalTheme.colors
  val windows = listOf("Breakfast", "Lunch", "Snack", "Dinner")

  // Aggregate macros per window
  val windowData = windows.map { window ->
    val matching = meals.filter { it.mealType.contains(window, ignoreCase = true) }
    val c = matching.sumOf { it.carbsGrams }
    val p = matching.sumOf { it.proteinGrams }
    val f = matching.sumOf { it.fatGrams }
    Triple(c, p, f)
  }

  val maxVal = windowData.maxOfOrNull { maxOf(it.first, it.second, it.third) }?.coerceAtLeast(40) ?: 40

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(130.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Bottom
    ) {
      windows.forEachIndexed { idx, label ->
        val data = windowData[idx]
        val cHeight = (data.first.toFloat() / maxVal.toFloat()).coerceIn(0.08f, 1f)
        val pHeight = (data.second.toFloat() / maxVal.toFloat()).coerceIn(0.08f, 1f)
        val fHeight = (data.third.toFloat() / maxVal.toFloat()).coerceIn(0.08f, 1f)

        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Bottom
        ) {
          // Grouped Bars
          Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.height(100.dp)
          ) {
            // Carbs bar
            Box(
              modifier = Modifier
                .width(8.dp)
                .fillMaxHeight(cHeight)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(carbsColor)
            )
            // Protein bar
            Box(
              modifier = Modifier
                .width(8.dp)
                .fillMaxHeight(pHeight)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(proteinColor)
            )
            // Fat bar
            Box(
              modifier = Modifier
                .width(8.dp)
                .fillMaxHeight(fHeight)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(fatColor)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = label.take(5),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = colors.ink2
          )
        }
      }
    }

    // Chart Legend
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
      ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(carbsColor))
        Text("Carbs", fontSize = 10.sp, color = colors.ink2)
      }
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
      ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(proteinColor))
        Text("Protein", fontSize = 10.sp, color = colors.ink2)
      }
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
      ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(fatColor))
        Text("Fats", fontSize = 10.sp, color = colors.ink2)
      }
    }
  }
}

@Composable
private fun MacroMetricBox(
  label: String,
  grams: Int,
  kcal: Int,
  color: Color,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = colors.surface2,
    border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(label, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(2.dp))
      Text("${grams}g", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.ink)
      Text("$kcal kcal", fontSize = 10.sp, color = colors.ink2)
    }
  }
}

data class MealMacroItem(
  val id: String,
  val dishName: String,
  val mealType: String,
  val time: String,
  val calories: Int,
  val carbsGrams: Int,
  val proteinGrams: Int,
  val fatGrams: Int,
  val imageUrl: String = "",
  val scanSource: String = "AI SCAN",
  val chefNote: String = ""
)

private fun buildMealMacroList(
  todayMeals: List<LoggedMeal>,
  scannedMealLogs: List<ScannedMealWithNutrition>
): List<MealMacroItem> {
  val result = mutableListOf<MealMacroItem>()

  // Add from Room Scanned Meal Logs
  scannedMealLogs.forEach { log ->
    val info = log.nutritionalInfo
    result.add(
      MealMacroItem(
        id = "scanned_${log.scannedMeal.id}",
        dishName = log.scannedMeal.dishName,
        mealType = log.scannedMeal.mealType,
        time = "Scanned",
        calories = info?.calories ?: 350,
        carbsGrams = (info?.carbsGrams ?: 40.0).roundToInt(),
        proteinGrams = (info?.proteinGrams ?: 18.0).roundToInt(),
        fatGrams = (info?.fatGrams ?: 12.0).roundToInt(),
        imageUrl = log.scannedMeal.imageUrl,
        scanSource = log.scannedMeal.scanSource,
        chefNote = log.scannedMeal.chefNote
      )
    )
  }

  // Add from Today Logged Meals if not already added
  todayMeals.forEach { meal ->
    if (result.none { it.dishName.equals(meal.title, ignoreCase = true) }) {
      result.add(
        MealMacroItem(
          id = "meal_${meal.id}",
          dishName = meal.title,
          mealType = meal.mealType,
          time = meal.time,
          calories = meal.calories,
          carbsGrams = meal.carbsGrams,
          proteinGrams = meal.proteinGrams,
          fatGrams = meal.fatGrams,
          imageUrl = meal.imageUrl,
          scanSource = if (meal.tag.contains("Barcode", ignoreCase = true)) "BARCODE" else "AI CAMERA",
          chefNote = meal.tag
        )
      )
    }
  }

  return result
}
