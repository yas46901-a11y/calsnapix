package com.example.model

data class GoalOption(
  val id: String,
  val title: String,
  val description: String,
  val tag1: String,
  val tag2: String,
  val iconName: String
)

data class RegionalCuisine(
  val id: String,
  val title: String,
  val sampleDishes: String,
  val imageUrl: String
)

data class MealWindow(
  val name: String,
  val desiName: String,
  val time: String,
  val iconName: String
)

data class LoggedMeal(
  val id: String,
  val title: String,
  val mealType: String,
  val time: String,
  val calories: Int,
  val tag: String = "Indian",
  val imageUrl: String = "",
  val proteinGrams: Int = 15,
  val carbsGrams: Int = 40,
  val fatGrams: Int = 12
)

data class Recipe(
  val id: String,
  val title: String,
  val category: String,
  val rating: Double,
  val prepTime: String,
  val calories: Int,
  val macroHighlight: String,
  val imageUrl: String,
  val isBookmarked: Boolean = false
)

data class DayCalorieRecord(
  val dayLabel: String,
  val calories: Int,
  val target: Int = 2000,
  val isSurplus: Boolean = false
)

data class WeightLogEntry(
  val id: String,
  val dateLabel: String, // e.g. "Aug 22", "Sep 1"
  val weightKg: Float,
  val note: String = "",
  val changeFromStartKg: Float = 0f
)

data class ScanResult(
  val dishName: String,
  val calories: Int,
  val servingDescription: String,
  val proteinGrams: Int,
  val carbsGrams: Int,
  val fatGrams: Int,
  val chefNote: String,
  val imageUrl: String,
  val fiberGrams: Float = 6.2f,
  val ironMg: Float = 3.8f,
  val calciumMg: Float = 160f,
  val potassiumMg: Float = 440f,
  val sodiumMg: Float = 380f,
  val vitaminAMcg: Float = 220f,
  val vitaminCMg: Float = 18f,
  val vitaminDIU: Float = 50f,
  val vitaminB12Mcg: Float = 0.9f,
  val magnesiumMg: Float = 68f,
  val zincMg: Float = 2.6f
)

