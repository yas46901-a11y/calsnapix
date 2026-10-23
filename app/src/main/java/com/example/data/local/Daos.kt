package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
  suspend fun getUserByEmail(email: String): UserEntity?

  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  suspend fun getUserById(id: Long): UserEntity?

  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  fun observeUserById(id: Long): Flow<UserEntity?>

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insertUser(user: UserEntity): Long

  @Update
  suspend fun updateUser(user: UserEntity)

  @Query("SELECT COUNT(*) FROM users")
  suspend fun getUserCount(): Int

  @Query("DELETE FROM users WHERE id = :id")
  suspend fun deleteUserById(id: Long)
}

@Dao
interface MealDao {
  @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY timestamp DESC")
  fun observeMealsByUserId(userId: Long): Flow<List<MealEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMeal(meal: MealEntity): Long

  @Query("DELETE FROM meals WHERE id = :mealId")
  suspend fun deleteMealById(mealId: Long)

  @Query("DELETE FROM meals WHERE userId = :userId")
  suspend fun clearMealsForUser(userId: Long)
}

/**
 * Data Access Object for local logs of scanned meals and nutritional information.
 */
@Dao
interface ScannedMealDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScannedMeal(meal: ScannedMealEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNutritionalInfo(info: NutritionalInfoEntity): Long

  @Transaction
  suspend fun insertScannedMealWithNutrition(
    meal: ScannedMealEntity,
    nutrition: NutritionalInfoEntity
  ): Long {
    val mealId = insertScannedMeal(meal)
    insertNutritionalInfo(nutrition.copy(scannedMealId = mealId))
    return mealId
  }

  @Transaction
  @Query("SELECT * FROM scanned_meals WHERE userId = :userId ORDER BY scannedAt DESC")
  fun observeScannedMealsByUserId(userId: Long): Flow<List<ScannedMealWithNutrition>>

  @Transaction
  @Query("SELECT * FROM scanned_meals WHERE userId = :userId ORDER BY scannedAt DESC LIMIT :limit")
  fun observeRecentScannedMeals(userId: Long, limit: Int = 10): Flow<List<ScannedMealWithNutrition>>

  @Transaction
  @Query("SELECT * FROM scanned_meals WHERE id = :id LIMIT 1")
  suspend fun getScannedMealWithNutritionById(id: Long): ScannedMealWithNutrition?

  @Query("SELECT * FROM scanned_meals WHERE userId = :userId ORDER BY scannedAt DESC")
  suspend fun getScannedMealsByUserId(userId: Long): List<ScannedMealEntity>

  @Query("SELECT * FROM nutritional_information WHERE scannedMealId = :scannedMealId LIMIT 1")
  suspend fun getNutritionalInfoForMeal(scannedMealId: Long): NutritionalInfoEntity?

  @Query("SELECT * FROM scanned_meals WHERE barcode = :barcode LIMIT 1")
  suspend fun getScannedMealByBarcode(barcode: String): ScannedMealEntity?

  @Transaction
  @Query("SELECT * FROM scanned_meals WHERE userId = :userId AND dishName LIKE '%' || :query || '%' ORDER BY scannedAt DESC")
  fun searchScannedMeals(userId: Long, query: String): Flow<List<ScannedMealWithNutrition>>

  @Query("DELETE FROM scanned_meals WHERE id = :id")
  suspend fun deleteScannedMealById(id: Long)

  @Query("DELETE FROM scanned_meals WHERE userId = :userId")
  suspend fun clearScannedMealsForUser(userId: Long)

  @Query("SELECT COUNT(*) FROM scanned_meals WHERE userId = :userId")
  suspend fun getScannedMealCount(userId: Long): Int
}

/**
 * Data Access Object for local persistent user nutritional profiles.
 */
@Dao
interface NutritionalProfileDao {
  @Query("SELECT * FROM nutritional_profiles WHERE userId = :userId LIMIT 1")
  fun observeProfileByUserId(userId: Long): Flow<NutritionalProfileEntity?>

  @Query("SELECT * FROM nutritional_profiles WHERE userId = :userId LIMIT 1")
  suspend fun getProfileByUserId(userId: Long): NutritionalProfileEntity?

  @Query("SELECT * FROM nutritional_profiles WHERE dietaryPreference = :dietaryPreference")
  fun observeProfilesByDiet(dietaryPreference: String): Flow<List<NutritionalProfileEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: NutritionalProfileEntity): Long

  @Update
  suspend fun updateProfile(profile: NutritionalProfileEntity)

  @Query("DELETE FROM nutritional_profiles WHERE userId = :userId")
  suspend fun deleteProfileByUserId(userId: Long)

  @Transaction
  @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
  fun observeUserWithProfile(userId: Long): Flow<UserWithNutritionalProfile?>

  @Transaction
  @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
  suspend fun getUserWithProfile(userId: Long): UserWithNutritionalProfile?
}


