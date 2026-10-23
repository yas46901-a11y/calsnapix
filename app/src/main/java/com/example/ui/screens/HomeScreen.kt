package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import com.example.data.gemini.GeminiService
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.AhaarUiState
import com.example.AppDestination
import com.example.model.LoggedMeal
import com.example.ui.components.VoiceLogDialog
import com.example.ui.theme.CalCard
import com.example.ui.theme.CalPrimaryButton
import com.example.ui.theme.CalSecondaryButton
import com.example.ui.theme.CalTheme
import java.text.SimpleDateFormat
import androidx.compose.ui.graphics.Brush
import java.util.Date
import java.util.Locale

/**
 * Modern CalSnapix Home Dashboard.
 * "Apple Health meets Cal AI" — Clean whitespace, single emerald accent, tabular numerals,
 * zero orange, high contrast WCAG AA compliant.
 */
@Composable
fun HomeScreen(
  uiState: AhaarUiState,
  onNavigate: (AppDestination) -> Unit,
  onLogWater: () -> Unit,
  onUndoWater: () -> Unit = {},
  onAddCustomMeal: (LoggedMeal) -> Unit,
  onDeleteMeal: (String) -> Unit,
  onUpdateCalorieGoal: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val scrollState = rememberScrollState()

  var showAddMealDialog by remember { mutableStateOf(false) }
  var showVoiceLogDialog by remember { mutableStateOf(false) }
  var showGoalDialog by remember { mutableStateOf(false) }
  var showMyCalAiDialog by remember { mutableStateOf(false) }
  var newMealTitle by remember { mutableStateOf("") }
  var newMealCalories by remember { mutableStateOf("350") }
  var newMealProtein by remember { mutableStateOf("16") }
  var newMealCarbs by remember { mutableStateOf("45") }
  var newMealFat by remember { mutableStateOf("12") }
  var newMealType by remember { mutableStateOf("Lunch") }

  // Calorie calculations
  val targetKcal = uiState.dailyCalorieTarget
  val consumedKcal = uiState.currentCaloriesConsumed
  val remainingKcal = (targetKcal - consumedKcal).coerceAtLeast(0)
  val progressFraction = if (targetKcal > 0) (consumedKcal.toFloat() / targetKcal.toFloat()).coerceIn(0f, 1f) else 0f

  val animatedProgress by animateFloatAsState(
    targetValue = progressFraction,
    animationSpec = tween(durationMillis = 800),
    label = "calorie_ring_progress"
  )

  val todayFormatted = remember {
    val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    sdf.format(Date())
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(colors.bg)
      .verticalScroll(scrollState)
      .padding(horizontal = CalTheme.sidePadding, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Clean Header: Greeting + Date & Avatar (No redundant CalSnapix cards)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Good day, ${uiState.userName.substringBefore(" ")}",
          fontSize = 26.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink,
          letterSpacing = (-0.5).sp
        )
        Text(
          text = todayFormatted,
          fontSize = 14.sp,
          fontWeight = FontWeight.Normal,
          color = colors.ink2
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // MyCal AI Dietitian Smart Button
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = colors.accent.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.5f)),
          modifier = Modifier
            .clickable {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              showMyCalAiDialog = true
            }
            .testTag("mycal_ai_top_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "MyCal AI",
              tint = colors.accent,
              modifier = Modifier.size(15.dp)
            )
            Text(
              text = "MyCal AI",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent
            )
          }
        }

        // Modern Stylized Avatar (Vector badge with user initials and gradient)
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                colors = listOf(colors.accent, Color(0xFF6366F1), Color(0xFF10B981))
              )
            )
            .border(1.5.dp, colors.line, CircleShape)
            .clickable {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              onNavigate(AppDestination.PROFILE)
            }
            .testTag("home_profile_avatar"),
          contentAlignment = Alignment.Center
        ) {
          val initials = uiState.userName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
            .ifBlank { "CS" }
          Text(
            text = initials,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
        }
      }
    }

    // Referral Reward Banner if active
    if (uiState.referralRewardActive && uiState.referralRewardMessage != null) {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.accentSoft,
        modifier = Modifier.fillMaxWidth().testTag("referral_active_banner")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = uiState.referralRewardMessage,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.accent
          )
        }
      }
    }

    // 1b. Streak Counter & Weekly Summary Card
    CalCard(modifier = Modifier.testTag("weekly_summary_card")) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text("🔥", fontSize = 18.sp)
            Text(
              text = "${uiState.streakDays}-Day Streak",
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
              text = "On Track",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        // 7-day visual dots (Mon-Sun)
        val days = listOf("M", "T", "W", "T", "F", "S", "S")
        val currentDayIdx = remember {
          val cal = java.util.Calendar.getInstance()
          val dow = cal.get(java.util.Calendar.DAY_OF_WEEK)
          (dow + 5) % 7 // Monday = 0, Tuesday = 1, Wednesday = 2, Thursday = 3, Friday = 4, Saturday = 5, Sunday = 6
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          days.forEachIndexed { idx, day ->
            val isToday = idx == currentDayIdx
            val isStreakDay = (isToday && uiState.streakDays >= 1) || (idx < currentDayIdx && (currentDayIdx - idx) < uiState.streakDays)
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = day,
                fontSize = 11.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                color = if (isToday) colors.accent else colors.ink2
              )
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(
                    when {
                      isToday -> colors.accent
                      isStreakDay -> colors.accent.copy(alpha = 0.25f)
                      else -> colors.surface2
                    }
                  ),
                contentAlignment = Alignment.Center
              ) {
                if (isStreakDay) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (isToday) colors.bg else colors.accent,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // 2. Large Calorie Ring Card (Accent color, tabular numerals 48-64px)
    CalCard(
      modifier = Modifier.testTag("home_calorie_card"),
      onClick = { showGoalDialog = true }
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "CALORIES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink2,
            letterSpacing = 1.sp
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { showGoalDialog = true }
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "Edit Goal",
              tint = colors.ink2,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Goal: $targetKcal",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = colors.ink2
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Center Ring
        Box(
          modifier = Modifier.size(200.dp),
          contentAlignment = Alignment.Center
        ) {
          val ringTrackColor = colors.surface2
          val ringProgressColor = colors.accent

          Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val strokeW = 16.dp.toPx()
            // Track
            drawArc(
              color = ringTrackColor,
              startAngle = 135f,
              sweepAngle = 270f,
              useCenter = false,
              style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
            // Progress
            drawArc(
              color = ringProgressColor,
              startAngle = 135f,
              sweepAngle = 270f * animatedProgress,
              useCenter = false,
              style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
          }

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = remainingKcal.toString(),
              fontSize = 52.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink,
              letterSpacing = (-1.5).sp
            )
            Text(
              text = "KCAL REMAINING",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = colors.ink2,
              letterSpacing = 0.8.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Eaten & Target sub-row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface2)
            .padding(vertical = 10.dp, horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Eaten", fontSize = 11.sp, color = colors.ink2)
            Text("$consumedKcal kcal", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.ink)
          }
          Box(modifier = Modifier.width(1.dp).height(24.dp).background(colors.line))
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Burn / Target", fontSize = 11.sp, color = colors.ink2)
            Text("$targetKcal kcal", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.accent)
          }
        }
      }
    }

    // 3. Three Slim Macro Bars Card (Protein: Indigo, Carbs: Muted Gold, Fat: Dusty Rose)
    CalCard(
      modifier = Modifier.testTag("home_macro_card"),
      onClick = { onNavigate(AppDestination.NUTRITION_TRACKING) }
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "MACRONUTRIENTS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink2,
            letterSpacing = 1.sp
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(colors.accentSoft)
              .clickable { onNavigate(AppDestination.NUTRITION_TRACKING) }
              .padding(horizontal = 8.dp, vertical = 3.dp)
              .testTag("view_macro_charts_button")
          ) {
            Text(
              text = "Daily Charts →",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent
            )
          }
        }

        // Protein
        MacroSlimRow(
          label = "Protein",
          currentGrams = uiState.proteinConsumedGrams,
          targetGrams = uiState.proteinTargetGrams,
          color = colors.protein
        )

        // Carbs
        MacroSlimRow(
          label = "Carbs",
          currentGrams = uiState.carbsConsumedGrams,
          targetGrams = uiState.carbsTargetGrams,
          color = colors.carbs
        )

        // Fat
        MacroSlimRow(
          label = "Fat",
          currentGrams = uiState.fatsConsumedGrams,
          targetGrams = uiState.fatsTargetGrams,
          color = colors.fat
        )
      }
    }

    // 3b. Dedicated Hydration Card with Circular Progress and Undo
    CalCard(modifier = Modifier.testTag("home_water_card")) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Circular progress ring
          val waterFraction = (uiState.waterGlassesLogged.toFloat() / uiState.totalWaterGlassesTarget.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
          Box(modifier = Modifier.size(54.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              drawArc(
                color = colors.line,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
              )
              drawArc(
                color = colors.protein,
                startAngle = -90f,
                sweepAngle = 360f * waterFraction,
                useCenter = false,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
              )
            }
            Icon(
              imageVector = Icons.Default.Opacity,
              contentDescription = null,
              tint = colors.protein,
              modifier = Modifier.size(22.dp)
            )
          }

          Column {
            Text(
              text = "HYDRATION",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink2,
              letterSpacing = 0.8.sp
            )
            Text(
              text = "${uiState.waterGlassesLogged} of ${uiState.totalWaterGlassesTarget} glasses",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
            val loggedL = String.format(Locale.US, "%.1f", uiState.waterGlassesLogged * 0.25f)
            val targetL = String.format(Locale.US, "%.1f", uiState.totalWaterGlassesTarget * 0.25f)
            Text(
              text = "$loggedL L / $targetL L goal",
              fontSize = 12.sp,
              color = colors.ink2
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          if (uiState.waterGlassesLogged > 0) {
            IconButton(
              onClick = onUndoWater,
              modifier = Modifier.size(36.dp).testTag("undo_water_button")
            ) {
              Text(
                text = "-1",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink2
              )
            }
          }
          Button(
            onClick = onLogWater,
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.protein, contentColor = androidx.compose.ui.graphics.Color.White),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.testTag("add_glass_button")
          ) {
            Text("+ Glass", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 4. Quick Action Row: 4 Equal Minimalist Tiles (Scan, Voice, Water, Add)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      QuickActionTile(
        title = "Scan",
        icon = Icons.Default.PhotoCamera,
        modifier = Modifier.weight(1f).testTag("quick_action_scan"),
        onClick = { onNavigate(AppDestination.SCAN) }
      )
      QuickActionTile(
        title = "Voice",
        icon = Icons.Default.Mic,
        modifier = Modifier.weight(1f).testTag("quick_action_voice"),
        onClick = { showVoiceLogDialog = true }
      )
      QuickActionTile(
        title = "Water",
        icon = Icons.Default.Opacity,
        badge = "${uiState.waterGlassesLogged}/${uiState.totalWaterGlassesTarget}",
        modifier = Modifier.weight(1f).testTag("quick_action_water"),
        onClick = { onLogWater() }
      )
      QuickActionTile(
        title = "Add",
        icon = Icons.Default.Add,
        modifier = Modifier.weight(1f).testTag("quick_action_add"),
        onClick = { showAddMealDialog = true }
      )
    }

    // 4b. Quick Re-Log Favorites
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "Quick Re-Log Favorites",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink2,
        letterSpacing = 0.8.sp
      )
      val favs = listOf(
        Triple("2 Phulkas & Dal", 340, "Lunch"),
        Triple("Moong Dal Chilla", 240, "Breakfast"),
        Triple("Masala Chai & Roti", 220, "Snack"),
        Triple("Paneer Bhurji & Roti", 380, "Dinner")
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        favs.forEach { (name, kcal, mealType) ->
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier
              .clickable {
                onAddCustomMeal(
                  LoggedMeal(
                    id = "fav_${System.currentTimeMillis()}",
                    title = name,
                    mealType = mealType,
                    time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                    calories = kcal,
                    proteinGrams = (kcal * 0.18 / 4).toInt(),
                    carbsGrams = (kcal * 0.55 / 4).toInt(),
                    fatGrams = (kcal * 0.27 / 9).toInt()
                  )
                )
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
              }
              .testTag("fav_chip_$name")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = colors.accent, modifier = Modifier.size(14.dp))
              Text(name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.ink)
              Text("• $kcal kcal", fontSize = 11.sp, color = colors.ink2)
            }
          }
        }
      }
    }

    // 5. Today's Meals Section (Clean list: time, name, kcal)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Today's Meals",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink,
          letterSpacing = (-0.3).sp
        )
        Text(
          text = "${uiState.todayMeals.size} logged",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = colors.ink2
        )
      }

      if (uiState.todayMeals.isEmpty()) {
        CalCard {
          Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "No meals logged yet today",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colors.ink2
              )
              Spacer(modifier = Modifier.height(10.dp))
              TextButton(onClick = { onNavigate(AppDestination.SCAN) }) {
                Text("Tap to scan your first meal", color = colors.accent, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      } else {
        uiState.todayMeals.forEach { meal ->
          TodayMealItem(
            meal = meal,
            onLogAgain = {
              onAddCustomMeal(
                meal.copy(
                  id = "relog_${System.currentTimeMillis()}",
                  time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                )
              )
              view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            },
            onDelete = { onDeleteMeal(meal.id) }
          )
        }
      }
    }

    // 6. Insight of the Day Card
    CalCard(backgroundColor = colors.surface2) {
      Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(colors.accentSoft),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(16.dp)
          )
        }
        Column {
          Text(
            text = "Digestive Agni Peak",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Your digestive fire is strongest between 12:30 PM & 2:00 PM. Prioritize nutrient-dense lentils, paneer, and whole grains during lunch for optimal metabolic burn.",
            fontSize = 12.sp,
            color = colors.ink2,
            lineHeight = 17.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(80.dp)) // Space for floating bottom nav
  }

  // Voice Log Dialog
  if (showVoiceLogDialog) {
    VoiceLogDialog(
      onDismiss = { showVoiceLogDialog = false },
      onLogMeal = { loggedMeal ->
        onAddCustomMeal(loggedMeal)
        showVoiceLogDialog = false
      }
    )
  }

  // Add Custom Meal Dialog
  if (showAddMealDialog) {
    AlertDialog(
      onDismissRequest = { showAddMealDialog = false },
      containerColor = colors.surface,
      title = {
        Text("Log Custom Meal", fontWeight = FontWeight.Bold, color = colors.ink)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = newMealTitle,
            onValueChange = { newMealTitle = it },
            label = { Text("Meal Name") },
            placeholder = { Text("e.g. 2 Rotis with Dal Tadka") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = colors.accent,
              unfocusedBorderColor = colors.line
            ),
            modifier = Modifier.fillMaxWidth().testTag("add_meal_title_input")
          )
          // Portion Size with Indian Units
          Text("Portion Size (Indian Units):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.ink2)
          val portionOptions = listOf(
            "1 katori (150g)" to 1.0f,
            "2 katoris" to 2.0f,
            "1 roti / phulka" to 0.6f,
            "2 rotis" to 1.2f,
            "3 rotis" to 1.8f,
            "1 glass" to 0.8f,
            "1 standard thali" to 1.8f
          )
          var selectedPortionLabel by remember { mutableStateOf("1 katori (150g)") }
          Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            portionOptions.forEach { (label, mult) ->
              val isSel = selectedPortionLabel == label
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSel) colors.accent else colors.surface2,
                border = BorderStroke(1.dp, if (isSel) colors.accent else colors.line),
                modifier = Modifier.clickable {
                  selectedPortionLabel = label
                  newMealCalories = (350 * mult).toInt().toString()
                  newMealProtein = (16 * mult).toInt().toString()
                }
              ) {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSel) colors.bg else colors.ink,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newMealCalories,
              onValueChange = { newMealCalories = it },
              label = { Text("Calories (kcal)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.accent,
                unfocusedBorderColor = colors.line
              ),
              modifier = Modifier.weight(1f).testTag("add_meal_calories_input")
            )
            OutlinedTextField(
              value = newMealProtein,
              onValueChange = { newMealProtein = it },
              label = { Text("Protein (g)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.accent,
                unfocusedBorderColor = colors.line
              ),
              modifier = Modifier.weight(1f)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newMealTitle.isNotBlank()) {
              val kcal = newMealCalories.toIntOrNull() ?: 350
              val protein = newMealProtein.toIntOrNull() ?: 15
              val carbs = newMealCarbs.toIntOrNull() ?: 45
              val fat = newMealFat.toIntOrNull() ?: 10
              val meal = LoggedMeal(
                id = "custom_${System.currentTimeMillis()}",
                title = newMealTitle.trim(),
                mealType = newMealType,
                time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                calories = kcal,
                tag = if (protein >= 20) "High Protein" else "Balanced",
                imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600&auto=format&fit=crop&q=80",
                proteinGrams = protein,
                carbsGrams = carbs,
                fatGrams = fat
              )
              onAddCustomMeal(meal)
              newMealTitle = ""
              showAddMealDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
          shape = CalTheme.pillShape,
          modifier = Modifier.testTag("confirm_add_meal_button")
        ) {
          Text("Add to Log", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddMealDialog = false }) {
          Text("Cancel", color = colors.ink2)
        }
      }
    )
  }

  // Edit Goal Dialog
  if (showGoalDialog) {
    var goalInput by remember { mutableStateOf(targetKcal.toString()) }
    AlertDialog(
      onDismissRequest = { showGoalDialog = false },
      containerColor = colors.surface,
      title = { Text("Update Calorie Target", fontWeight = FontWeight.Bold, color = colors.ink) },
      text = {
        OutlinedTextField(
          value = goalInput,
          onValueChange = { goalInput = it },
          label = { Text("Daily Target (kcal)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.accent,
            unfocusedBorderColor = colors.line
          ),
          modifier = Modifier.fillMaxWidth().testTag("calorie_goal_input")
        )
      },
      confirmButton = {
        Button(
          onClick = {
            goalInput.toIntOrNull()?.let { onUpdateCalorieGoal(it) }
            showGoalDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
          shape = CalTheme.pillShape,
          modifier = Modifier.testTag("confirm_goal_button")
        ) {
          Text("Save Target", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showGoalDialog = false }) {
          Text("Cancel", color = colors.ink2)
        }
      }
    )
  }

  // Fullscreen MyCal AI Genuine Clinical Dietitian Chat
  if (showMyCalAiDialog) {
    MyCalAiChatScreen(
      uiState = uiState,
      onDismiss = { showMyCalAiDialog = false }
    )
  }
}

@Composable
private fun MacroSlimRow(
  label: String,
  currentGrams: Int,
  targetGrams: Int,
  color: Color
) {
  val colors = CalTheme.colors
  val progress = if (targetGrams > 0) (currentGrams.toFloat() / targetGrams.toFloat()).coerceIn(0f, 1f) else 0f

  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.ink)
      Text("${currentGrams}g / ${targetGrams}g", fontSize = 12.sp, color = colors.ink2)
    }

    // Slim progress bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(7.dp)
        .clip(RoundedCornerShape(999.dp))
        .background(colors.surface2)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(fraction = progress)
          .height(7.dp)
          .clip(RoundedCornerShape(999.dp))
          .background(color)
      )
    }
  }
}

@Composable
private fun QuickActionTile(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier,
  badge: String? = null,
  onClick: () -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Surface(
    modifier = modifier
      .height(72.dp)
      .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
      .border(1.dp, colors.line, RoundedCornerShape(16.dp))
      .clip(RoundedCornerShape(16.dp))
      .clickable {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        onClick()
      },
    color = colors.surface,
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxSize().padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = colors.ink,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = colors.ink
      )
      if (badge != null) {
        Text(
          text = badge,
          fontSize = 10.sp,
          color = colors.ink2
        )
      }
    }
  }
}

@Composable
private fun TodayMealItem(
  meal: LoggedMeal,
  onLogAgain: () -> Unit,
  onDelete: () -> Unit
) {
  val colors = CalTheme.colors

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
      .border(1.dp, colors.line, RoundedCornerShape(16.dp)),
    color = colors.surface,
    shape = RoundedCornerShape(16.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Dish photo or placeholder
      AsyncImage(
        model = meal.imageUrl,
        contentDescription = meal.title,
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(10.dp)),
        contentScale = ContentScale.Crop
      )

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = meal.title,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
          color = colors.ink
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${meal.time} • ${meal.mealType} • ${meal.proteinGrams}g P • ${meal.carbsGrams}g C",
          fontSize = 12.sp,
          color = colors.ink2
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "${meal.calories} kcal",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onLogAgain,
            modifier = Modifier.size(28.dp).testTag("relog_meal_${meal.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Log Again",
              tint = colors.accent,
              modifier = Modifier.size(16.dp)
            )
          }
          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp).testTag("delete_meal_${meal.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete",
              tint = colors.ink2,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}
