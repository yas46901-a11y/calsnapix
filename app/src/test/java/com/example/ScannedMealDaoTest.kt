package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.NutritionalInfoEntity
import com.example.data.local.ScannedMealDao
import com.example.data.local.ScannedMealEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ScannedMealDaoTest {

  private lateinit var database: AppDatabase
  private lateinit var scannedMealDao: ScannedMealDao

  @Before
  fun setup() {
    val context: Context = ApplicationProvider.getApplicationContext()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    scannedMealDao = database.scannedMealDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun testInsertScannedMealWithNutritionalInfo() = runBlocking {
    val meal = ScannedMealEntity(
      userId = 101L,
      dishName = "Palak Paneer with Roti",
      mealType = "Dinner",
      servingDescription = "1 bowl (200g) + 2 rotis",
      portionMultiplier = 1.0f,
      imageUrl = "https://example.com/palak_paneer.jpg",
      scanSource = "CAMERA",
      chefNote = "Rich in iron and vegetarian protein"
    )

    val nutrition = NutritionalInfoEntity(
      calories = 420,
      proteinGrams = 22.5,
      carbsGrams = 38.0,
      fatGrams = 18.0,
      fiberGrams = 6.2,
      sodiumMg = 480.0,
      micronutrients = "Iron, Calcium, Vitamin A"
    )

    val mealId = scannedMealDao.insertScannedMealWithNutrition(meal, nutrition)
    assertTrue(mealId > 0)

    // Retrieve via relation
    val result = scannedMealDao.getScannedMealWithNutritionById(mealId)
    assertNotNull(result)
    assertEquals("Palak Paneer with Roti", result?.scannedMeal?.dishName)
    assertEquals("Dinner", result?.scannedMeal?.mealType)
    assertEquals(101L, result?.scannedMeal?.userId)

    val retrievedNutrition = result?.nutritionalInfo
    assertNotNull(retrievedNutrition)
    assertEquals(420, retrievedNutrition?.calories)
    assertEquals(22.5, retrievedNutrition?.proteinGrams ?: 0.0, 0.01)
    assertEquals(38.0, retrievedNutrition?.carbsGrams ?: 0.0, 0.01)
    assertEquals(18.0, retrievedNutrition?.fatGrams ?: 0.0, 0.01)
    assertEquals(6.2, retrievedNutrition?.fiberGrams ?: 0.0, 0.01)
    assertEquals("Iron, Calcium, Vitamin A", retrievedNutrition?.micronutrients)
  }

  @Test
  fun testObserveScannedMealsByUserId() = runBlocking {
    val meal1 = ScannedMealEntity(
      userId = 200L,
      dishName = "Masala Oats",
      mealType = "Breakfast",
      scannedAt = 1000L
    )
    val meal2 = ScannedMealEntity(
      userId = 200L,
      dishName = "Dal Tadka & Rice",
      mealType = "Lunch",
      scannedAt = 2000L
    )
    val otherUserMeal = ScannedMealEntity(
      userId = 999L,
      dishName = "Chana Salad",
      mealType = "Snack"
    )

    scannedMealDao.insertScannedMealWithNutrition(
      meal1,
      NutritionalInfoEntity(calories = 210, proteinGrams = 8.0, carbsGrams = 34.0, fatGrams = 4.5)
    )
    scannedMealDao.insertScannedMealWithNutrition(
      meal2,
      NutritionalInfoEntity(calories = 360, proteinGrams = 14.0, carbsGrams = 56.0, fatGrams = 7.0)
    )
    scannedMealDao.insertScannedMealWithNutrition(
      otherUserMeal,
      NutritionalInfoEntity(calories = 180, proteinGrams = 9.0, carbsGrams = 22.0, fatGrams = 3.0)
    )

    val userMeals = scannedMealDao.observeScannedMealsByUserId(200L).first()
    assertEquals(2, userMeals.size)
    // Ordered by scannedAt DESC
    assertEquals("Dal Tadka & Rice", userMeals[0].scannedMeal.dishName)
    assertEquals("Masala Oats", userMeals[1].scannedMeal.dishName)
  }

  @Test
  fun testCascadingDeleteScannedMeal() = runBlocking {
    val meal = ScannedMealEntity(
      userId = 300L,
      dishName = "Paneer Tikka Roll",
      mealType = "Snack"
    )
    val nutrition = NutritionalInfoEntity(
      calories = 350,
      proteinGrams = 18.0,
      carbsGrams = 30.0,
      fatGrams = 14.0
    )

    val mealId = scannedMealDao.insertScannedMealWithNutrition(meal, nutrition)
    val beforeDeleteNutrition = scannedMealDao.getNutritionalInfoForMeal(mealId)
    assertNotNull(beforeDeleteNutrition)

    scannedMealDao.deleteScannedMealById(mealId)

    val afterDeleteMeal = scannedMealDao.getScannedMealWithNutritionById(mealId)
    assertNull(afterDeleteMeal)

    val afterDeleteNutrition = scannedMealDao.getNutritionalInfoForMeal(mealId)
    assertNull(afterDeleteNutrition)
  }

  @Test
  fun testClearScannedMealsForUser() = runBlocking {
    scannedMealDao.insertScannedMealWithNutrition(
      ScannedMealEntity(userId = 400L, dishName = "Dosa 1"),
      NutritionalInfoEntity(calories = 200, proteinGrams = 4.0, carbsGrams = 30.0, fatGrams = 6.0)
    )
    scannedMealDao.insertScannedMealWithNutrition(
      ScannedMealEntity(userId = 400L, dishName = "Dosa 2"),
      NutritionalInfoEntity(calories = 220, proteinGrams = 5.0, carbsGrams = 32.0, fatGrams = 7.0)
    )

    assertEquals(2, scannedMealDao.getScannedMealCount(400L))

    scannedMealDao.clearScannedMealsForUser(400L)
    assertEquals(0, scannedMealDao.getScannedMealCount(400L))
  }

  @Test
  fun testBarcodeAndSearchQueries() = runBlocking {
    val barcodeMeal = ScannedMealEntity(
      userId = 500L,
      dishName = "Epigamia Greek Yogurt",
      mealType = "Snack",
      barcode = "8906073123456",
      scanSource = "BARCODE"
    )
    val id = scannedMealDao.insertScannedMealWithNutrition(
      barcodeMeal,
      NutritionalInfoEntity(calories = 130, proteinGrams = 12.0, carbsGrams = 10.0, fatGrams = 4.0)
    )

    val foundByBarcode = scannedMealDao.getScannedMealByBarcode("8906073123456")
    assertNotNull(foundByBarcode)
    assertEquals("Epigamia Greek Yogurt", foundByBarcode?.dishName)

    val searchResults = scannedMealDao.searchScannedMeals(500L, "Greek").first()
    assertEquals(1, searchResults.size)
    assertEquals("Epigamia Greek Yogurt", searchResults[0].scannedMeal.dishName)
    assertEquals(12.0, searchResults[0].nutritionalInfo?.proteinGrams ?: 0.0, 0.01)
  }
}
