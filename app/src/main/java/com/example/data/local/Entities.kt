package com.example.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
  tableName = "users",
  indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val email: String,
  val passwordHash: String,
  val salt: String,
  val avatarUrl: String = "",
  val goal: String = "lose_weight",
  val dietType: String = "veg",
  val region: String = "north",
  val dailyCalorieTarget: Int = 2000,
  val proteinTargetGrams: Int = 90,
  val carbsTargetGrams: Int = 210,
  val fatsTargetGrams: Int = 55,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "meals",
  indices = [Index(value = ["userId"])]
)
data class MealEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val userId: Long,
  val title: String,
  val mealType: String, // Breakfast, Lunch, Snack, Dinner
  val time: String,
  val calories: Int,
  val proteinGrams: Int,
  val carbsGrams: Int,
  val fatGrams: Int,
  val tag: String,
  val imageUrl: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

/**
 * Room entity to store local persistent logs of scanned meals.
 */
@Entity(
  tableName = "scanned_meals",
  indices = [
    Index(value = ["userId"]),
    Index(value = ["scannedAt"]),
    Index(value = ["barcode"])
  ]
)
data class ScannedMealEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val userId: Long,
  val dishName: String,
  val mealType: String = "Lunch", // Breakfast, Lunch, Snack, Dinner
  val servingDescription: String = "1 standard serving",
  val portionMultiplier: Float = 1.0f,
  val imageUrl: String = "",
  val photoUri: String? = null,
  val scanSource: String = "CAMERA", // CAMERA, GALLERY, BARCODE, MANUAL
  val barcode: String? = null,
  val chefNote: String = "",
  val scannedAt: Long = System.currentTimeMillis(),
  val loggedAsMeal: Boolean = true
)

/**
 * Room entity to store detailed nutritional information for a scanned meal.
 * Linked to [ScannedMealEntity] via foreign key with CASCADE deletion.
 */
@Entity(
  tableName = "nutritional_information",
  foreignKeys = [
    ForeignKey(
      entity = ScannedMealEntity::class,
      parentColumns = ["id"],
      childColumns = ["scannedMealId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["scannedMealId"])]
)
data class NutritionalInfoEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val scannedMealId: Long = 0,
  val calories: Int,
  val proteinGrams: Double,
  val carbsGrams: Double,
  val fatGrams: Double,
  val fiberGrams: Double = 0.0,
  val sugarGrams: Double = 0.0,
  val sodiumMg: Double = 0.0,
  val saturatedFatGrams: Double = 0.0,
  val micronutrients: String = "" // e.g. "Iron, Calcium, Vitamin B12"
)

/**
 * 1-to-1 Room relation linking a scanned meal log with its complete nutritional information.
 */
data class ScannedMealWithNutrition(
  @Embedded
  val scannedMeal: ScannedMealEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "scannedMealId"
  )
  val nutritionalInfo: NutritionalInfoEntity?
)

/**
 * Room entity representing a user's personalized nutritional profile.
 * Stores biometric data (age, gender), activity level, and cultural dietary preferences (e.g. Vegetarian, Vegan, Jain).
 */
@Entity(
  tableName = "nutritional_profiles",
  foreignKeys = [
    ForeignKey(
      entity = UserEntity::class,
      parentColumns = ["id"],
      childColumns = ["userId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["userId"], unique = true),
    Index(value = ["dietaryPreference"])
  ]
)
data class NutritionalProfileEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val userId: Long,
  val age: Int,
  val gender: String, // "male", "female", "other"
  val activityLevel: String, // "sedentary", "lightly-active", "moderately-active", "very-active"
  val dietaryPreference: String, // "vegetarian", "vegan", "jain", "eggetarian", "non_vegetarian", "sattvic"
  val heightCm: Float = 168f,
  val currentWeightKg: Float = 68f,
  val targetWeightKg: Float = 62f,
  val regionalCuisine: String = "north",
  val primaryGoal: String = "lose_weight",
  val dailyCalorieTarget: Int = 1800,
  val proteinTargetGrams: Int = 90,
  val carbsTargetGrams: Int = 210,
  val fatsTargetGrams: Int = 50,
  val waterTargetGlasses: Int = 8,
  val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 1-to-1 Room relation linking a user with their complete nutritional profile.
 */
data class UserWithNutritionalProfile(
  @Embedded
  val user: UserEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "userId"
  )
  val profile: NutritionalProfileEntity?
)


