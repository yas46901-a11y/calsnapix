package com.example.data.gamification

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Enterprise gamification engine that tracks streaks and awards badges for:
 * 1. Consistent hydration streaks (1d, 3d, 7d, 14d, 30d)
 * 2. Meeting weekly macro-nutrient goals (Protein, Carbs, Fats)
 * 3. Consistent food journal logging
 */
class GamificationManager(private val context: Context) {

  companion object {
    private const val PREFS_NAME = "calsnapix_gamification_prefs"
    private const val KEY_WATER_STREAK = "water_streak_days"
    private const val KEY_BEST_WATER_STREAK = "best_water_streak_days"
    private const val KEY_MACRO_DAYS_THIS_WEEK = "macro_days_this_week"
    private const val KEY_LAST_WATER_EVAL_DATE = "last_water_eval_date"
    private const val KEY_LAST_MACRO_EVAL_DATE = "last_macro_eval_date"
    private const val KEY_UNLOCKED_BADGES = "unlocked_badges_set"
  }

  private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  private val _profileState = MutableStateFlow(GamificationProfile())
  val profileState: StateFlow<GamificationProfile> = _profileState.asStateFlow()

  init {
    loadProfile()
  }

  /**
   * Refreshes gamification metrics and re-evaluates all badges.
   */
  fun loadProfile() {
    val waterStreak = prefs.getInt(KEY_WATER_STREAK, 3)
    val bestWaterStreak = prefs.getInt(KEY_BEST_WATER_STREAK, 7)
    val macroDays = prefs.getInt(KEY_MACRO_DAYS_THIS_WEEK, 4)
    val unlockedSet = prefs.getStringSet(KEY_UNLOCKED_BADGES, emptySet()) ?: emptySet()

    val badges = buildBadgesList(waterStreak, macroDays, unlockedSet)
    val totalXp = badges.filter { it.isUnlocked }.sumOf { it.xpAwarded } + (waterStreak * 25) + (macroDays * 40)
    val userLevel = (totalXp / 250) + 1
    val nextLevelXp = userLevel * 250

    val levelTitle = when (userLevel) {
      1 -> "Novice Eater"
      2 -> "Mindful Foodie"
      3 -> "Macro Master"
      4 -> "Hydration Architect"
      5 -> "Nutritional Champion"
      else -> "Ahaar Legend"
    }

    _profileState.update {
      it.copy(
        totalXp = totalXp,
        userLevel = userLevel,
        levelTitle = levelTitle,
        nextLevelXp = nextLevelXp,
        currentWaterStreakDays = waterStreak,
        bestWaterStreakDays = bestWaterStreak,
        macroGoalsHitThisWeek = macroDays,
        weeklyMacroGoalTarget = 7,
        badges = badges
      )
    }
  }

  /**
   * Evaluates today's hydration and macro performance to update streaks and badges.
   */
  fun evaluateDailyIntake(
    waterGlassesLogged: Int,
    targetWaterGlasses: Int,
    proteinGrams: Int,
    proteinTargetGrams: Int,
    carbsGrams: Int,
    carbsTargetGrams: Int,
    fatsGrams: Int,
    fatsTargetGrams: Int,
    mealCount: Int
  ) {
    val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    var waterStreak = prefs.getInt(KEY_WATER_STREAK, 3)
    var bestWaterStreak = prefs.getInt(KEY_BEST_WATER_STREAK, 7)
    var macroDays = prefs.getInt(KEY_MACRO_DAYS_THIS_WEEK, 4)
    val unlockedSet = prefs.getStringSet(KEY_UNLOCKED_BADGES, mutableSetOf())?.toMutableSet() ?: mutableSetOf()

    // 1. Evaluate Hydration Streak
    val isWaterGoalMet = waterGlassesLogged >= targetWaterGlasses && targetWaterGlasses > 0
    if (isWaterGoalMet) {
      val lastWaterDate = prefs.getString(KEY_LAST_WATER_EVAL_DATE, null)
      if (lastWaterDate != todayKey) {
        waterStreak += 1
        if (waterStreak > bestWaterStreak) bestWaterStreak = waterStreak
        prefs.edit()
          .putInt(KEY_WATER_STREAK, waterStreak)
          .putInt(KEY_BEST_WATER_STREAK, bestWaterStreak)
          .putString(KEY_LAST_WATER_EVAL_DATE, todayKey)
          .apply()
      }
    }

    // 2. Evaluate Weekly Macro-Nutrient Goals
    // Criteria: Protein target reached within 90%, and Carbs/Fat within healthy boundary
    val isProteinGoalMet = proteinTargetGrams > 0 && proteinGrams >= (proteinTargetGrams * 0.90)
    val isCarbsWithinLimit = carbsTargetGrams > 0 && carbsGrams <= (carbsTargetGrams * 1.15)
    val isMacroGoalMet = isProteinGoalMet && isCarbsWithinLimit && mealCount >= 2

    if (isMacroGoalMet) {
      val lastMacroDate = prefs.getString(KEY_LAST_MACRO_EVAL_DATE, null)
      if (lastMacroDate != todayKey) {
        macroDays = (macroDays + 1).coerceAtMost(7)
        prefs.edit()
          .putInt(KEY_MACRO_DAYS_THIS_WEEK, macroDays)
          .putString(KEY_LAST_MACRO_EVAL_DATE, todayKey)
          .apply()
      }
    }

    // Unlock badges dynamically
    if (waterStreak >= 1) unlockedSet.add("badge_water_1")
    if (waterStreak >= 3) unlockedSet.add("badge_water_3")
    if (waterStreak >= 7) unlockedSet.add("badge_water_7")
    if (waterStreak >= 14) unlockedSet.add("badge_water_14")
    if (waterStreak >= 30) unlockedSet.add("badge_water_30")

    if (isProteinGoalMet) unlockedSet.add("badge_protein_goal")
    if (macroDays >= 3) unlockedSet.add("badge_macro_precision_3")
    if (macroDays >= 7) unlockedSet.add("badge_macro_weekly")

    if (mealCount >= 1) unlockedSet.add("badge_first_scan")
    if (mealCount >= 3) unlockedSet.add("badge_logging_day")

    prefs.edit().putStringSet(KEY_UNLOCKED_BADGES, unlockedSet).apply()

    loadProfile()
  }

  /**
   * Helper to manually increment streak for testing/demoing.
   */
  fun simulateHydrationStreak(targetStreak: Int) {
    val unlockedSet = prefs.getStringSet(KEY_UNLOCKED_BADGES, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    if (targetStreak >= 1) unlockedSet.add("badge_water_1")
    if (targetStreak >= 3) unlockedSet.add("badge_water_3")
    if (targetStreak >= 7) unlockedSet.add("badge_water_7")
    if (targetStreak >= 14) unlockedSet.add("badge_water_14")

    prefs.edit()
      .putInt(KEY_WATER_STREAK, targetStreak)
      .putInt(KEY_BEST_WATER_STREAK, maxOf(targetStreak, prefs.getInt(KEY_BEST_WATER_STREAK, 7)))
      .putStringSet(KEY_UNLOCKED_BADGES, unlockedSet)
      .apply()
    loadProfile()
  }

  /**
   * Helper to manually simulate weekly macro accomplishment for testing.
   */
  fun simulateWeeklyMacroGoal(daysMet: Int) {
    val unlockedSet = prefs.getStringSet(KEY_UNLOCKED_BADGES, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    unlockedSet.add("badge_protein_goal")
    if (daysMet >= 3) unlockedSet.add("badge_macro_precision_3")
    if (daysMet >= 7) unlockedSet.add("badge_macro_weekly")

    prefs.edit()
      .putInt(KEY_MACRO_DAYS_THIS_WEEK, daysMet.coerceIn(0, 7))
      .putStringSet(KEY_UNLOCKED_BADGES, unlockedSet)
      .apply()
    loadProfile()
  }

  private fun buildBadgesList(
    waterStreak: Int,
    macroDays: Int,
    unlockedSet: Set<String>
  ): List<AchievementBadge> {
    return listOf(
      AchievementBadge(
        id = "badge_water_1",
        title = "First Splash 💧",
        description = "Logged your complete daily hydration goal.",
        category = "Hydration Streak",
        tier = BadgeTier.BRONZE,
        iconEmoji = "💧",
        progressCurrent = minOf(waterStreak, 1),
        progressMax = 1,
        isUnlocked = unlockedSet.contains("badge_water_1") || waterStreak >= 1,
        xpAwarded = 50
      ),
      AchievementBadge(
        id = "badge_water_3",
        title = "Hydration Habit 🌊",
        description = "Maintained a 3-day continuous water drinking streak.",
        category = "Hydration Streak",
        tier = BadgeTier.SILVER,
        iconEmoji = "🌊",
        progressCurrent = minOf(waterStreak, 3),
        progressMax = 3,
        isUnlocked = unlockedSet.contains("badge_water_3") || waterStreak >= 3,
        xpAwarded = 100
      ),
      AchievementBadge(
        id = "badge_water_7",
        title = "Aqua Master 🏆",
        description = "7 consecutive days of meeting your daily hydration target.",
        category = "Hydration Streak",
        tier = BadgeTier.GOLD,
        iconEmoji = "🏆",
        progressCurrent = minOf(waterStreak, 7),
        progressMax = 7,
        isUnlocked = unlockedSet.contains("badge_water_7") || waterStreak >= 7,
        xpAwarded = 250
      ),
      AchievementBadge(
        id = "badge_water_14",
        title = "Hydration Titan ⚡",
        description = "14 days of sustained optimal cellular hydration.",
        category = "Hydration Streak",
        tier = BadgeTier.DIAMOND,
        iconEmoji = "⚡",
        progressCurrent = minOf(waterStreak, 14),
        progressMax = 14,
        isUnlocked = unlockedSet.contains("badge_water_14") || waterStreak >= 14,
        xpAwarded = 500
      ),
      AchievementBadge(
        id = "badge_protein_goal",
        title = "Protein Powerhouse 🥩",
        description = "Hit 90%+ of your daily clean protein target.",
        category = "Weekly Macros",
        tier = BadgeTier.BRONZE,
        iconEmoji = "🥩",
        progressCurrent = if (unlockedSet.contains("badge_protein_goal")) 1 else 0,
        progressMax = 1,
        isUnlocked = unlockedSet.contains("badge_protein_goal"),
        xpAwarded = 75
      ),
      AchievementBadge(
        id = "badge_macro_precision_3",
        title = "Macro Precision (3d) 🎯",
        description = "Hit your protein, carb, and fat targets for 3 days this week.",
        category = "Weekly Macros",
        tier = BadgeTier.SILVER,
        iconEmoji = "🎯",
        progressCurrent = minOf(macroDays, 3),
        progressMax = 3,
        isUnlocked = unlockedSet.contains("badge_macro_precision_3") || macroDays >= 3,
        xpAwarded = 150
      ),
      AchievementBadge(
        id = "badge_macro_weekly",
        title = "Weekly Macro Champion ⭐",
        description = "Met your weekly macronutrient goals for all 7 days of the week!",
        category = "Weekly Macros",
        tier = BadgeTier.GOLD,
        iconEmoji = "⭐",
        progressCurrent = minOf(macroDays, 7),
        progressMax = 7,
        isUnlocked = unlockedSet.contains("badge_macro_weekly") || macroDays >= 7,
        xpAwarded = 400
      ),
      AchievementBadge(
        id = "badge_first_scan",
        title = "Plate Pioneer 📸",
        description = "Scanned and logged your first meal using AI vision.",
        category = "Food Logging",
        tier = BadgeTier.BRONZE,
        iconEmoji = "📸",
        progressCurrent = if (unlockedSet.contains("badge_first_scan")) 1 else 0,
        progressMax = 1,
        isUnlocked = unlockedSet.contains("badge_first_scan"),
        xpAwarded = 50
      )
    )
  }
}
