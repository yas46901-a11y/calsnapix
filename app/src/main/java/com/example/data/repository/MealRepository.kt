package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.MealDao
import com.example.data.local.MealEntity
import com.example.data.local.NutritionalInfoEntity
import com.example.data.local.ScannedMealDao
import com.example.data.local.ScannedMealEntity
import com.example.data.local.ScannedMealWithNutrition
import com.example.model.LoggedMeal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MealRepository(context: Context) {
  private val database = AppDatabase.getDatabase(context)
  private val mealDao: MealDao = database.mealDao()
  private val scannedMealDao: ScannedMealDao = database.scannedMealDao()

  fun observeUserMeals(userId: Long): Flow<List<LoggedMeal>> {
    return mealDao.observeMealsByUserId(userId).map { list ->
      list.map { entity ->
        LoggedMeal(
          id = entity.id.toString(),
          title = entity.title,
          mealType = entity.mealType,
          time = entity.time,
          calories = entity.calories,
          proteinGrams = entity.proteinGrams,
          carbsGrams = entity.carbsGrams,
          fatGrams = entity.fatGrams,
          tag = entity.tag,
          imageUrl = entity.imageUrl
        )
      }
    }
  }

  suspend fun insertMeal(userId: Long, meal: LoggedMeal): Long = withContext(Dispatchers.IO) {
    val entity = MealEntity(
      userId = userId,
      title = meal.title,
      mealType = meal.mealType,
      time = meal.time,
      calories = meal.calories,
      proteinGrams = meal.proteinGrams,
      carbsGrams = meal.carbsGrams,
      fatGrams = meal.fatGrams,
      tag = meal.tag,
      imageUrl = meal.imageUrl
    )
    mealDao.insertMeal(entity)
  }

  suspend fun deleteMeal(mealId: Long) = withContext(Dispatchers.IO) {
    mealDao.deleteMealById(mealId)
  }

  suspend fun clearUserMeals(userId: Long) = withContext(Dispatchers.IO) {
    mealDao.clearMealsForUser(userId)
  }

  // --- Scanned Meals & Nutritional Information Logs ---

  fun observeScannedMeals(userId: Long): Flow<List<ScannedMealWithNutrition>> {
    return scannedMealDao.observeScannedMealsByUserId(userId)
  }

  fun observeRecentScannedMeals(userId: Long, limit: Int = 10): Flow<List<ScannedMealWithNutrition>> {
    return scannedMealDao.observeRecentScannedMeals(userId, limit)
  }

  suspend fun getScannedMealById(id: Long): ScannedMealWithNutrition? = withContext(Dispatchers.IO) {
    scannedMealDao.getScannedMealWithNutritionById(id)
  }

  suspend fun logScannedMealWithNutrition(
    userId: Long,
    dishName: String,
    mealType: String,
    servingDescription: String,
    portionMultiplier: Float,
    calories: Int,
    proteinGrams: Double,
    carbsGrams: Double,
    fatGrams: Double,
    fiberGrams: Double = 0.0,
    sugarGrams: Double = 0.0,
    sodiumMg: Double = 0.0,
    saturatedFatGrams: Double = 0.0,
    micronutrients: String = "",
    imageUrl: String = "",
    photoUri: String? = null,
    scanSource: String = "CAMERA",
    barcode: String? = null,
    chefNote: String = "",
    scannedAt: Long = System.currentTimeMillis()
  ): Long = withContext(Dispatchers.IO) {
    val mealEntity = ScannedMealEntity(
      userId = userId,
      dishName = dishName,
      mealType = mealType,
      servingDescription = servingDescription,
      portionMultiplier = portionMultiplier,
      imageUrl = imageUrl,
      photoUri = photoUri,
      scanSource = scanSource,
      barcode = barcode,
      chefNote = chefNote,
      scannedAt = scannedAt
    )
    val nutritionEntity = NutritionalInfoEntity(
      scannedMealId = 0,
      calories = calories,
      proteinGrams = proteinGrams,
      carbsGrams = carbsGrams,
      fatGrams = fatGrams,
      fiberGrams = fiberGrams,
      sugarGrams = sugarGrams,
      sodiumMg = sodiumMg,
      saturatedFatGrams = saturatedFatGrams,
      micronutrients = micronutrients
    )
    scannedMealDao.insertScannedMealWithNutrition(mealEntity, nutritionEntity)
  }

  suspend fun deleteScannedMeal(id: Long) = withContext(Dispatchers.IO) {
    scannedMealDao.deleteScannedMealById(id)
  }

  suspend fun clearUserScannedMeals(userId: Long) = withContext(Dispatchers.IO) {
    scannedMealDao.clearScannedMealsForUser(userId)
  }
}

