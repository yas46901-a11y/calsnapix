package com.example.data.gamification

/**
 * Badge rank tier with corresponding accent colors.
 */
enum class BadgeTier(val label: String, val colorHex: Long) {
  BRONZE("Bronze", 0xFFCD7F32),
  SILVER("Silver", 0xFF94A3B8),
  GOLD("Gold", 0xFFF59E0B),
  DIAMOND("Diamond", 0xFF06B6D4)
}

/**
 * Representation of an individual gamification achievement badge.
 */
data class AchievementBadge(
  val id: String,
  val title: String,
  val description: String,
  val category: String, // "Hydration Streak", "Weekly Macros", "Logging Streak"
  val tier: BadgeTier,
  val iconEmoji: String,
  val progressCurrent: Int,
  val progressMax: Int,
  val isUnlocked: Boolean = false,
  val unlockedDate: String? = null,
  val xpAwarded: Int = 100
) {
  val progressFraction: Float
    get() = if (progressMax > 0) (progressCurrent.toFloat() / progressMax.toFloat()).coerceIn(0f, 1f) else 0f
}

/**
 * Overall gamification progress profile.
 */
data class GamificationProfile(
  val totalXp: Int = 350,
  val userLevel: Int = 2,
  val levelTitle: String = "Mindful Foodie",
  val nextLevelXp: Int = 500,
  val currentWaterStreakDays: Int = 3,
  val bestWaterStreakDays: Int = 7,
  val macroGoalsHitThisWeek: Int = 4,
  val weeklyMacroGoalTarget: Int = 7,
  val badges: List<AchievementBadge> = emptyList()
) {
  val levelProgressFraction: Float
    get() = if (nextLevelXp > 0) (totalXp.toFloat() / nextLevelXp.toFloat()).coerceIn(0f, 1f) else 0f

  val unlockedCount: Int
    get() = badges.count { it.isUnlocked }
}
