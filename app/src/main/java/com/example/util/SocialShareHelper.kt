package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.AhaarUiState
import com.example.data.gamification.AchievementBadge
import com.example.data.gamification.GamificationProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility to format and launch the Android Sharesheet for daily macro-nutrient progress,
 * gamification badge achievements, and scanned meal logs.
 */
object SocialShareHelper {

  /**
   * Generates a beautifully formatted social caption for daily macro-nutrients and hydration.
   */
  fun buildDailyProgressShareText(state: AhaarUiState): String {
    val dateStr = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(Date())
    val remainingCals = (state.dailyCalorieTarget - state.currentCaloriesConsumed).coerceAtLeast(0)
    val calStatus = if (state.currentCaloriesConsumed <= state.dailyCalorieTarget) {
      "🎯 ${remainingCals} kcal remaining under target"
    } else {
      "⚡ Surplus of ${state.currentCaloriesConsumed - state.dailyCalorieTarget} kcal"
    }

    val waterPct = if (state.totalWaterGlassesTarget > 0) {
      ((state.waterGlassesLogged.toFloat() / state.totalWaterGlassesTarget.toFloat()) * 100).toInt()
    } else 0

    return buildString {
      appendLine("🥗 My CalSnapix Daily Nutrition Snapshot")
      appendLine("📅 $dateStr")
      appendLine("━━━━━━━━━━━━━━━━━━━━━")
      appendLine("🔥 Calories: ${state.currentCaloriesConsumed} / ${state.dailyCalorieTarget} kcal")
      appendLine("   $calStatus")
      appendLine()
      appendLine("📊 Daily Macro Breakdown:")
      appendLine("  • 💪 Protein: ${state.proteinConsumedGrams}g / ${state.proteinTargetGrams}g")
      appendLine("  • 🌾 Carbs: ${state.carbsConsumedGrams}g / ${state.carbsTargetGrams}g")
      appendLine("  • 🥑 Fats: ${state.fatsConsumedGrams}g / ${state.fatsTargetGrams}g")
      appendLine()
      appendLine("💧 Hydration: ${state.waterGlassesLogged}/${state.totalWaterGlassesTarget} glasses ($waterPct%)")
      if (state.gamificationProfile.currentWaterStreakDays > 0) {
        appendLine("   🌊 ${state.gamificationProfile.currentWaterStreakDays}-Day Water Streak!")
      }
      appendLine()
      if (state.currentSteps > 0) {
        appendLine("👟 Steps: ${state.currentSteps} / ${state.dailyStepGoal} (${state.activeCaloriesBurned} kcal burned)")
        appendLine()
      }
      appendLine("🌟 Consistency is key! Powered by Indian mindful nutrition on #CalSnapix")
      appendLine("#DesiFitness #MacroTracking #HealthyIndianDiet #NutritionGoals #MindfulEating")
    }
  }

  /**
   * Generates a social caption for a specific badge achievement.
   */
  fun buildBadgeAchievementShareText(
    badge: AchievementBadge,
    profile: GamificationProfile,
    userName: String = "Priya"
  ): String {
    return buildString {
      appendLine("🏆 Achievement Unlocked on CalSnapix!")
      appendLine("━━━━━━━━━━━━━━━━━━━━━")
      appendLine("${badge.iconEmoji} ${badge.title}")
      appendLine("⭐ Tier: ${badge.tier.label} Badge")
      appendLine("📜 ${badge.description}")
      appendLine("✨ +${badge.xpAwarded} XP Earned")
      appendLine()
      appendLine("🎖️ My Fitness Profile:")
      appendLine("  • Level ${profile.userLevel}: ${profile.levelTitle}")
      appendLine("  • Total XP: ${profile.totalXp} XP")
      appendLine("  • 💧 Current Hydration Streak: ${profile.currentWaterStreakDays} Days")
      appendLine("  • 🎯 Macro Consistency: ${profile.macroGoalsHitThisWeek}/${profile.weeklyMacroGoalTarget} Days Hit")
      appendLine()
      appendLine("Crushing my health goals with Indian meal scanning and mindful habits! 🇮🇳✨")
      appendLine("#CalSnapix #BadgeUnlocked #DesiNutrition #HealthStreak #LevelUp")
    }
  }

  /**
   * Generates a social caption for a scanned meal.
   */
  fun buildScannedMealShareText(
    dishName: String,
    calories: Int,
    protein: Double,
    carbs: Double,
    fat: Double,
    fiber: Double = 0.0,
    chefNote: String = ""
  ): String {
    return buildString {
      appendLine("🍽️ Scanned with CalSnapix AI:")
      appendLine("✨ $dishName")
      appendLine("━━━━━━━━━━━━━━━━━━━━━")
      appendLine("🔥 Energy: $calories kcal")
      appendLine("💪 Protein: ${protein.toInt()}g")
      appendLine("🌾 Carbs: ${carbs.toInt()}g")
      appendLine("🥑 Fat: ${fat.toInt()}g")
      if (fiber > 0) {
        appendLine("🌿 Dietary Fiber: ${String.format(Locale.getDefault(), "%.1f", fiber)}g")
      }
      if (chefNote.isNotBlank()) {
        appendLine()
        appendLine("💡 Note: $chefNote")
      }
      appendLine()
      appendLine("AI Indian Food Scanner • #CalSnapix #IndianFood #MindfulNutrition #DesiDiet")
    }
  }

  /**
   * Generates a 30-day intake pattern summary text.
   */
  fun build30DayPatternShareText(
    totalMeals: Int,
    avgCals: Int,
    avgProtein: Int,
    onTargetPct: Int,
    streakDays: Int
  ): String {
    return buildString {
      appendLine("📈 My 30-Day Nutrition Review on CalSnapix")
      appendLine("━━━━━━━━━━━━━━━━━━━━━")
      appendLine("🍽️ Total Meals Scanned: $totalMeals meals")
      appendLine("🔥 30-Day Avg Intake: $avgCals kcal/day")
      appendLine("💪 30-Day Avg Protein: $avgProtein g/day")
      appendLine("🎯 Target Consistency: $onTargetPct% of days on track")
      appendLine("🌊 Longest Hydration Streak: $streakDays days")
      appendLine()
      appendLine("Consistent daily tracking with #CalSnapix makes sustainable Indian health possible! 🥗💪")
      appendLine("#30DayTransformation #MacroReview #DesiHealth #FitnessJourney")
    }
  }

  /**
   * Opens the standard Android Sharesheet via Intent.ACTION_SEND and Intent.createChooser.
   */
  fun launchSharesheet(context: Context, text: String, title: String = "Share your nutrition progress via") {
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, text)
      putExtra(Intent.EXTRA_SUBJECT, title)
      type = "text/plain"
    }
    val chooser = Intent.createChooser(sendIntent, title).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
      context.startActivity(chooser)
    } catch (e: Exception) {
      Toast.makeText(context, "No sharing app found on this device", Toast.LENGTH_SHORT).show()
    }
  }

  /**
   * Copies formatted text to clipboard and shows user toast.
   */
  fun copyToClipboard(context: Context, text: String, label: String = "CalSnapix Progress") {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
  }
}
