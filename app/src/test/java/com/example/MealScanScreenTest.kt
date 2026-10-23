package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.model.LoggedMeal
import com.example.ui.screens.MealScanScreen
import com.example.ui.screens.indianMealLibrary
import com.example.ui.theme.AhaarTheme
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MealScanScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun testMealScanScreenPermissionRationale() {
    var loggedMealResult: LoggedMeal? = null

    composeTestRule.setContent {
      AhaarTheme {
        MealScanScreen(
          uiState = AhaarUiState(),
          onLogMeal = { meal -> loggedMealResult = meal }
        )
      }
    }

    // Verify permission rationale and options are cleanly displayed
    composeTestRule.onNodeWithTag("request_camera_permission_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("pick_gallery_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("try_sample_meal_button").assertIsDisplayed()
  }

  @Test
  fun testIndianMealLibraryContainsRichProfiles() {
    assertTrue(indianMealLibrary.isNotEmpty())
    val dal = indianMealLibrary.find { it.dishName.contains("Dal") }
    assertNotNull(dal)
    assertTrue(dal!!.calories > 0)
    assertTrue(dal.proteinGrams > 0)

    val paneer = indianMealLibrary.find { it.dishName.contains("Paneer") }
    assertNotNull(paneer)
    assertTrue(paneer!!.fatGrams > 0)
  }
}
