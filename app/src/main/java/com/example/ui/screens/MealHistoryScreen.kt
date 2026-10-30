package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.AhaarUiState
import com.example.data.local.ScannedMealWithNutrition
import com.example.ui.components.GlassCard
import com.example.ui.components.glassBorderBrush
import com.example.ui.components.glassSurfaceBrush
import com.example.ui.theme.CalCard
import com.example.ui.theme.CalPrimaryButton
import com.example.ui.theme.CalSecondaryButton
import com.example.ui.theme.CalTheme
import com.example.util.SocialShareHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 30-Day Scanned Meal History and Daily Intake Pattern Review.
 * Displays past scanned meals with chronological day groupings, 30-day calorie trend analytics,
 * macro intake breakdown, filters, and social sharing via the Android Sharesheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealHistoryScreen(
  uiState: AhaarUiState,
  onBack: () -> Unit,
  onDeleteScannedMeal: (Long) -> Unit,
  onSeedSampleHistory: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val colors = CalTheme.colors
  val isDark = colors.isDark
  val focusManager = LocalFocusManager.current

  BackHandler { onBack() }

  // Filter States
  var searchQuery by remember { mutableStateOf("") }
  var selectedTimeRange by remember { mutableStateOf("All 30 Days") } // "All 30 Days", "Past 7 Days", "Today"
  var selectedMealType by remember { mutableStateOf("All") } // "All", "Breakfast", "Lunch", "Dinner", "Snack"
  var selectedDayHighlightIndex by remember { mutableIntStateOf(-1) }

  // Dialog States
  var mealToDelete by remember { mutableStateOf<ScannedMealWithNutrition?>(null) }
  var mealDetailToShow by remember { mutableStateOf<ScannedMealWithNutrition?>(null) }

  val now = System.currentTimeMillis()
  val thirtyDaysAgo = now - TimeUnit.DAYS.toMillis(30)
  val sevenDaysAgo = now - TimeUnit.DAYS.toMillis(7)
  val startOfToday = remember {
    val cal = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    cal.timeInMillis
  }

  // Filter meals to last 30 days and apply active search and type filters
  val allScannedMeals = uiState.scannedMealLogs

  val filteredMeals by remember(allScannedMeals, searchQuery, selectedTimeRange, selectedMealType, selectedDayHighlightIndex) {
    derivedStateOf {
      allScannedMeals.filter { log ->
        val scannedAt = log.scannedMeal.scannedAt
        // Time range filter
        val inTimeRange = when (selectedTimeRange) {
          "Today" -> scannedAt >= startOfToday
          "Past 7 Days" -> scannedAt >= sevenDaysAgo
          else -> scannedAt >= thirtyDaysAgo || allScannedMeals.size <= 10 // Fallback to include all if small
        }

        // Meal type filter
        val inType = if (selectedMealType == "All") true else {
          log.scannedMeal.mealType.equals(selectedMealType, ignoreCase = true)
        }

        // Search query filter
        val matchesSearch = if (searchQuery.isBlank()) true else {
          log.scannedMeal.dishName.contains(searchQuery, ignoreCase = true) ||
            log.scannedMeal.chefNote.contains(searchQuery, ignoreCase = true)
        }

        inTimeRange && inType && matchesSearch
      }
    }
  }

  // Calculate 30-Day intake pattern stats
  val last30DaysMeals = remember(allScannedMeals) {
    allScannedMeals.filter { it.scannedMeal.scannedAt >= thirtyDaysAgo || allScannedMeals.size <= 10 }
  }

  // Group by day key (YYYY-MM-DD)
  val dailyIntakeMap = remember(last30DaysMeals) {
    val sdfKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val map = mutableMapOf<String, MutableList<ScannedMealWithNutrition>>()
    last30DaysMeals.forEach { meal ->
      val key = sdfKey.format(Date(meal.scannedMeal.scannedAt))
      map.getOrPut(key) { mutableListOf() }.add(meal)
    }
    map
  }

  val totalMealsIn30Days = last30DaysMeals.size
  val daysWithLogsCount = dailyIntakeMap.size.coerceAtLeast(1)
  val totalCaloriesIn30Days = last30DaysMeals.sumOf { it.nutritionalInfo?.calories ?: 0 }
  val averageDailyCalories = (totalCaloriesIn30Days / daysWithLogsCount).coerceAtLeast(0)
  val totalProteinIn30Days = last30DaysMeals.sumOf { it.nutritionalInfo?.proteinGrams ?: 0.0 }
  val averageDailyProtein = (totalProteinIn30Days / daysWithLogsCount).toInt().coerceAtLeast(0)

  // Target consistency: how many logged days were within ±15% of daily target
  val targetKcal = uiState.dailyCalorieTarget
  val daysOnTargetCount = dailyIntakeMap.values.count { dayMeals ->
    val dayTotal = dayMeals.sumOf { it.nutritionalInfo?.calories ?: 0 }
    dayTotal in (targetKcal * 0.85).toInt()..(targetKcal * 1.15).toInt()
  }
  val targetConsistencyPct = if (daysWithLogsCount > 0) {
    ((daysOnTargetCount.toFloat() / daysWithLogsCount.toFloat()) * 100).toInt()
  } else 100

  // Group filtered meals by friendly date header
  val groupedMealsByDate = remember(filteredMeals) {
    val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayKey = dayFormat.format(Date(now))
    val yesterdayKey = dayFormat.format(Date(now - TimeUnit.DAYS.toMillis(1)))

    filteredMeals.groupBy { meal ->
      val mealDateKey = dayFormat.format(Date(meal.scannedMeal.scannedAt))
      when (mealDateKey) {
        todayKey -> "Today • " + SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(meal.scannedMeal.scannedAt))
        yesterdayKey -> "Yesterday • " + SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(meal.scannedMeal.scannedAt))
        else -> SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date(meal.scannedMeal.scannedAt))
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Transparent)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = CalTheme.sidePadding, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header Bar
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier.size(40.dp).testTag("meal_history_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = colors.ink
            )
          }
          Column {
            Text(
              text = "Meal Scan History",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink,
              letterSpacing = (-0.3).sp
            )
            Text(
              text = "Past 30 Days Intake Patterns",
              fontSize = 12.sp,
              color = colors.ink2
            )
          }
        }

        // Share 30-Day Review Button
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = colors.accentSoft,
          border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.3f)),
          modifier = Modifier
            .clickable {
              val shareText = SocialShareHelper.build30DayPatternShareText(
                totalMeals = totalMealsIn30Days,
                avgCals = averageDailyCalories,
                avgProtein = averageDailyProtein,
                onTargetPct = targetConsistencyPct,
                streakDays = uiState.streakDays
              )
              SocialShareHelper.launchSharesheet(context, shareText, "Share 30-Day Nutrition Review")
            }
            .testTag("share_30_day_history_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share",
              tint = colors.accent,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Share Report",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent
            )
          }
        }
      }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // 1. 30-Day Intake Pattern Analytics Dashboard Card
        item {
          PatternAnalyticsCard(
            totalMeals = totalMealsIn30Days,
            averageDailyCalories = averageDailyCalories,
            averageDailyProtein = averageDailyProtein,
            targetKcal = targetKcal,
            targetConsistencyPct = targetConsistencyPct,
            dailyIntakeMap = dailyIntakeMap,
            now = now,
            onDaySelected = { dayIndex ->
              selectedDayHighlightIndex = if (selectedDayHighlightIndex == dayIndex) -1 else dayIndex
            }
          )
        }

        // 2. Search & Filter Bar
        item {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Search Input
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              placeholder = { Text("Search scanned Indian dishes...", fontSize = 14.sp, color = colors.ink2) },
              leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = colors.ink2, modifier = Modifier.size(20.dp))
              },
              trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                  IconButton(onClick = { searchQuery = "" }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = colors.ink2, modifier = Modifier.size(18.dp))
                  }
                }
              },
              singleLine = true,
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
              keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
              modifier = Modifier.fillMaxWidth().testTag("history_search_input"),
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surface.copy(alpha = 0.85f),
                unfocusedContainerColor = colors.surface.copy(alpha = 0.70f),
                focusedBorderColor = colors.accent,
                unfocusedBorderColor = colors.line
              )
            )

            // Time Filter Pills
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf("All 30 Days", "Past 7 Days", "Today").forEach { timeRange ->
                val isSelected = selectedTimeRange == timeRange
                Surface(
                  shape = RoundedCornerShape(999.dp),
                  color = if (isSelected) colors.ink else colors.surface.copy(alpha = 0.8f),
                  border = BorderStroke(1.dp, if (isSelected) colors.ink else colors.line),
                  modifier = Modifier
                    .clickable { selectedTimeRange = timeRange }
                    .testTag("time_filter_${timeRange.replace(" ", "_")}")
                ) {
                  Text(
                    text = timeRange,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) colors.bg else colors.ink,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(4.dp))

              // Meal Type Filters
              listOf("All", "Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                val isSelected = selectedMealType == type
                Surface(
                  shape = RoundedCornerShape(999.dp),
                  color = if (isSelected) colors.accentSoft else colors.surface.copy(alpha = 0.6f),
                  border = BorderStroke(1.dp, if (isSelected) colors.accent else colors.line),
                  modifier = Modifier
                    .clickable { selectedMealType = type }
                    .testTag("type_filter_$type")
                ) {
                  Text(
                    text = type,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) colors.accent else colors.ink2,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }
              }
            }
          }
        }

        // 3. Seed Sample Data Card if history has fewer than 5 items
        if (allScannedMeals.size < 5) {
          item {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = colors.accentSoft.copy(alpha = 0.8f),
              border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.35f)),
              modifier = Modifier.fillMaxWidth().testTag("seed_sample_history_card")
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(colors.accent),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Explore 30-Day Intake Trends",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.ink
                  )
                  Text(
                    text = "Load realistic 30-day Indian meal scan history to explore calorie trends, macro ratios, and consistency patterns.",
                    fontSize = 11.sp,
                    color = colors.ink2
                  )
                }
                Surface(
                  shape = RoundedCornerShape(999.dp),
                  color = colors.accent,
                  contentColor = Color.White,
                  modifier = Modifier
                    .clickable { onSeedSampleHistory() }
                    .testTag("load_sample_history_button")
                ) {
                  Text(
                    text = "Load Data",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                  )
                }
              }
            }
          }
        }

        // 4. Chronological Grouped Scanned Meal List
        if (filteredMeals.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.History,
                  contentDescription = null,
                  tint = colors.ink2.copy(alpha = 0.5f),
                  modifier = Modifier.size(48.dp)
                )
                Text(
                  text = if (searchQuery.isNotBlank()) "No meals matching \"$searchQuery\"" else "No meals logged in this filter",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Medium,
                  color = colors.ink2
                )
              }
            }
          }
        } else {
          groupedMealsByDate.forEach { (dateHeader, mealsForDay) ->
            item {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = dateHeader,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.ink,
                  letterSpacing = 0.2.sp
                )
                val dayCals = mealsForDay.sumOf { it.nutritionalInfo?.calories ?: 0 }
                Text(
                  text = "$dayCals kcal total • ${mealsForDay.size} meals",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = colors.ink2
                )
              }
            }

            items(mealsForDay, key = { it.scannedMeal.id }) { mealLog ->
              ScannedMealHistoryItemCard(
                mealLog = mealLog,
                onCardClick = { mealDetailToShow = mealLog },
                onShareClick = {
                  val shareText = SocialShareHelper.buildScannedMealShareText(
                    dishName = mealLog.scannedMeal.dishName,
                    calories = mealLog.nutritionalInfo?.calories ?: 0,
                    protein = mealLog.nutritionalInfo?.proteinGrams ?: 0.0,
                    carbs = mealLog.nutritionalInfo?.carbsGrams ?: 0.0,
                    fat = mealLog.nutritionalInfo?.fatGrams ?: 0.0,
                    fiber = mealLog.nutritionalInfo?.fiberGrams ?: 0.0,
                    chefNote = mealLog.scannedMeal.chefNote
                  )
                  SocialShareHelper.launchSharesheet(context, shareText, "Share Meal")
                },
                onDeleteClick = { mealToDelete = mealLog }
              )
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(60.dp))
        }
      }
    }
  }

  // Delete Confirmation Dialog
  mealToDelete?.let { meal ->
    AlertDialog(
      onDismissRequest = { mealToDelete = null },
      title = { Text("Delete Scanned Meal", fontWeight = FontWeight.Bold, color = colors.ink) },
      text = { Text("Remove \"${meal.scannedMeal.dishName}\" from your 30-day meal history?", color = colors.ink2) },
      confirmButton = {
        TextButton(
          onClick = {
            onDeleteScannedMeal(meal.scannedMeal.id)
            mealToDelete = null
          }
        ) {
          Text("Delete", color = colors.danger, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { mealToDelete = null }) {
          Text("Cancel", color = colors.ink2)
        }
      },
      containerColor = colors.surface
    )
  }

  // Meal Detail Dialog
  mealDetailToShow?.let { meal ->
    MealDetailSheetDialog(
      meal = meal,
      onDismiss = { mealDetailToShow = null },
      onShare = {
        val shareText = SocialShareHelper.buildScannedMealShareText(
          dishName = meal.scannedMeal.dishName,
          calories = meal.nutritionalInfo?.calories ?: 0,
          protein = meal.nutritionalInfo?.proteinGrams ?: 0.0,
          carbs = meal.nutritionalInfo?.carbsGrams ?: 0.0,
          fat = meal.nutritionalInfo?.fatGrams ?: 0.0,
          fiber = meal.nutritionalInfo?.fiberGrams ?: 0.0,
          chefNote = meal.scannedMeal.chefNote
        )
        SocialShareHelper.launchSharesheet(context, shareText, "Share Meal")
      }
    )
  }
}

/**
 * 30-Day Intake Pattern Analytics Card with Trend Chart and Consistency Metrics.
 */
@Composable
private fun PatternAnalyticsCard(
  totalMeals: Int,
  averageDailyCalories: Int,
  averageDailyProtein: Int,
  targetKcal: Int,
  targetConsistencyPct: Int,
  dailyIntakeMap: Map<String, List<ScannedMealWithNutrition>>,
  now: Long,
  onDaySelected: (Int) -> Unit
) {
  val colors = CalTheme.colors
  val isDark = colors.isDark

  CalCard(
    modifier = Modifier.fillMaxWidth().testTag("30_day_pattern_analytics_card")
  ) {
    Column(
      modifier = Modifier.padding(2.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "30-DAY INTAKE PATTERNS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent,
            letterSpacing = 1.sp
          )
          Text(
            text = "Daily Intake Review & Consistency",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
        }

        Surface(
          shape = RoundedCornerShape(999.dp),
          color = colors.accentSoft
        ) {
          Text(
            text = "$targetConsistencyPct% on target",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // 3 Top Stat Boxes
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatTile(
          label = "Avg Intake",
          value = "$averageDailyCalories",
          unit = "kcal/day",
          color = colors.accent,
          modifier = Modifier.weight(1f)
        )
        StatTile(
          label = "Avg Protein",
          value = "$averageDailyProtein",
          unit = "g/day",
          color = colors.protein,
          modifier = Modifier.weight(1f)
        )
        StatTile(
          label = "Total Scans",
          value = "$totalMeals",
          unit = "meals",
          color = colors.carbs,
          modifier = Modifier.weight(1f)
        )
      }

      // 30-Day Daily Calorie Intake Trend Bar Chart
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "30-Day Calorie Trend (Target: $targetKcal kcal)",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink2
          )
          Text(
            text = "Last 30 Days →",
            fontSize = 10.sp,
            color = colors.ink2
          )
        }

        // Horizontal bar sequence representing the past 30 days
        val sdfKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val daysList = remember(now, dailyIntakeMap) {
          (29 downTo 0).map { daysAgo ->
            val time = now - TimeUnit.DAYS.toMillis(daysAgo.toLong())
            val key = sdfKey.format(Date(time))
            val meals = dailyIntakeMap[key] ?: emptyList()
            val totalCal = meals.sumOf { it.nutritionalInfo?.calories ?: 0 }
            Triple(daysAgo, key, totalCal)
          }
        }

        // 30-Bar Sparkline / Visual Timeline
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = colors.surface2.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth().height(72.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            daysList.forEachIndexed { index, (daysAgo, key, totalCal) ->
              val maxBarHeight = 56.dp
              val fraction = (totalCal.toFloat() / (targetKcal * 1.3f).toFloat()).coerceIn(0.06f, 1f)
              val barColor = when {
                totalCal == 0 -> colors.line.copy(alpha = 0.5f)
                totalCal in (targetKcal * 0.85).toInt()..(targetKcal * 1.15).toInt() -> colors.accent
                totalCal < targetKcal * 0.85 -> colors.warn
                else -> colors.danger
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .padding(horizontal = 1.dp)
                  .height(maxBarHeight * fraction)
                  .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                  .background(barColor)
                  .clickable { onDaySelected(index) }
              )
            }
          }
        }

        // Legend
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          LegendDot(color = colors.accent, label = "On Target")
          Spacer(modifier = Modifier.width(12.dp))
          LegendDot(color = colors.warn, label = "Under (<85%)")
          Spacer(modifier = Modifier.width(12.dp))
          LegendDot(color = colors.danger, label = "Surplus (>115%)")
        }
      }
    }
  }
}

@Composable
private fun LegendDot(color: Color, label: String) {
  val colors = CalTheme.colors
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Text(text = label, fontSize = 10.sp, color = colors.ink2)
  }
}

@Composable
private fun StatTile(
  label: String,
  value: String,
  unit: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = color.copy(alpha = 0.10f),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = colors.ink2)
      Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.ink)
      Text(text = unit, fontSize = 9.sp, color = colors.ink2)
    }
  }
}

/**
 * Individual Scanned Meal Item Card in History List.
 */
@Composable
private fun ScannedMealHistoryItemCard(
  mealLog: ScannedMealWithNutrition,
  onCardClick: () -> Unit,
  onShareClick: () -> Unit,
  onDeleteClick: () -> Unit
) {
  val colors = CalTheme.colors
  val isDark = colors.isDark
  val meal = mealLog.scannedMeal
  val nutrition = mealLog.nutritionalInfo

  val timeFormatted = remember(meal.scannedAt) {
    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(meal.scannedAt))
  }

  val typeColor = when (meal.mealType.lowercase()) {
    "breakfast" -> colors.warn
    "lunch" -> colors.accent
    "dinner" -> Color(0xFF6366F1)
    else -> colors.carbs
  }

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = if (isDark) Color(0xFF191B1A).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.90f),
    border = BorderStroke(1.dp, glassBorderBrush()),
    modifier = Modifier
      .fillMaxWidth()
      .shadow(2.dp, RoundedCornerShape(16.dp))
      .clickable { onCardClick() }
      .testTag("scanned_meal_card_${meal.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Dish Thumbnail / Icon
      Box(
        modifier = Modifier
          .size(52.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(typeColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        if (meal.imageUrl.isNotBlank()) {
          AsyncImage(
            model = meal.imageUrl,
            contentDescription = meal.dishName,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
          )
        } else {
          Icon(
            imageVector = when (meal.scanSource) {
              "BARCODE" -> Icons.Default.QrCodeScanner
              else -> Icons.Default.Restaurant
            },
            contentDescription = null,
            tint = typeColor,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      // Meal Details
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(3.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = typeColor.copy(alpha = 0.15f)
          ) {
            Text(
              text = meal.mealType,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = typeColor,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Text(
            text = timeFormatted,
            fontSize = 11.sp,
            color = colors.ink2
          )
          if (meal.scanSource == "BARCODE") {
            Text(text = "• Barcode", fontSize = 10.sp, color = colors.ink2)
          }
        }

        Text(
          text = meal.dishName,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        // Macros Row
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${nutrition?.calories ?: 0} kcal",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent
          )
          Text(text = "•", fontSize = 10.sp, color = colors.ink2)
          Text(
            text = "P: ${nutrition?.proteinGrams?.toInt() ?: 0}g",
            fontSize = 11.sp,
            color = colors.protein,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "C: ${nutrition?.carbsGrams?.toInt() ?: 0}g",
            fontSize = 11.sp,
            color = colors.carbs,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "F: ${nutrition?.fatGrams?.toInt() ?: 0}g",
            fontSize = 11.sp,
            color = colors.fat,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Quick Actions: Share via Sharesheet + Delete
      Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onShareClick,
          modifier = Modifier.size(36.dp).testTag("share_meal_button_${meal.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            tint = colors.accent,
            modifier = Modifier.size(18.dp)
          )
        }
        IconButton(
          onClick = onDeleteClick,
          modifier = Modifier.size(36.dp).testTag("delete_meal_button_${meal.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = colors.ink2.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

/**
 * Fullscreen / Modal Dialog showing full nutrition panel for a scanned meal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealDetailSheetDialog(
  meal: ScannedMealWithNutrition,
  onDismiss: () -> Unit,
  onShare: () -> Unit
) {
  val colors = CalTheme.colors
  val isDark = colors.isDark
  val entity = meal.scannedMeal
  val nutrition = meal.nutritionalInfo

  BasicAlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.fillMaxWidth().padding(16.dp)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = if (isDark) Color(0xFF161817) else Color.White,
      border = BorderStroke(1.dp, glassBorderBrush()),
      modifier = Modifier.shadow(16.dp, RoundedCornerShape(24.dp))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = entity.mealType.uppercase(),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent,
              letterSpacing = 1.sp
            )
            Text(
              text = entity.dishName,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.ink2)
          }
        }

        if (entity.imageUrl.isNotBlank()) {
          AsyncImage(
            model = entity.imageUrl,
            contentDescription = entity.dishName,
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
              .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop
          )
        }

        // Calorie Banner
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = colors.accentSoft,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "Total Energy", fontSize = 12.sp, color = colors.ink2)
              Text(
                text = "${nutrition?.calories ?: 0} kcal",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accent
              )
            }
            Text(
              text = entity.servingDescription,
              fontSize = 12.sp,
              color = colors.ink2
            )
          }
        }

        // Macros Breakdown
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MacroDetailTile(label = "Protein", value = "${nutrition?.proteinGrams?.toInt() ?: 0}g", color = colors.protein, modifier = Modifier.weight(1f))
          MacroDetailTile(label = "Carbohydrates", value = "${nutrition?.carbsGrams?.toInt() ?: 0}g", color = colors.carbs, modifier = Modifier.weight(1f))
          MacroDetailTile(label = "Healthy Fats", value = "${nutrition?.fatGrams?.toInt() ?: 0}g", color = colors.fat, modifier = Modifier.weight(1f))
        }

        // Additional Micronutrients
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface2)
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(text = "MICRONUTRIENTS & FIBER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.ink2)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Dietary Fiber", fontSize = 12.sp, color = colors.ink)
            Text(text = "${nutrition?.fiberGrams ?: 0.0}g", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Sodium", fontSize = 12.sp, color = colors.ink)
            Text(text = "${nutrition?.sodiumMg?.toInt() ?: 0}mg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          }
          if (nutrition?.micronutrients?.isNotBlank() == true) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = "Key Minerals", fontSize = 12.sp, color = colors.ink)
              Text(text = nutrition.micronutrients, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.accent)
            }
          }
        }

        if (entity.chefNote.isNotBlank()) {
          Text(
            text = "💡 ${entity.chefNote}",
            fontSize = 12.sp,
            color = colors.ink2
          )
        }

        // Share Button
        CalPrimaryButton(
          text = "Share Dish via Android Sharesheet",
          leadingIcon = {
            Icon(Icons.Default.Share, contentDescription = null, tint = colors.bg, modifier = Modifier.size(18.dp))
          },
          onClick = {
            onShare()
            onDismiss()
          }
        )
      }
    }
  }
}

@Composable
private fun MacroDetailTile(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = color.copy(alpha = 0.12f),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = label, fontSize = 10.sp, color = colors.ink2, maxLines = 1)
      Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.ink)
    }
  }
}
