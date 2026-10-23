package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.ui.screens.DailyNutritionTrackingScreen
import com.example.ui.theme.AhaarTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DailyNutritionTrackingTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun testDailyNutritionTrackingScreenRendersSuccessfully() {
    var navigatedDestination: AppDestination? = null
    var updatedCarbs = 0
    var updatedProtein = 0
    var updatedFat = 0

    composeTestRule.setContent {
      AhaarTheme {
        DailyNutritionTrackingScreen(
          uiState = AhaarUiState(
            carbsConsumedGrams = 165,
            proteinConsumedGrams = 78,
            fatsConsumedGrams = 42,
            carbsTargetGrams = 200,
            proteinTargetGrams = 90,
            fatsTargetGrams = 50
          ),
          onNavigate = { navigatedDestination = it },
          onUpdateMacroTargets = { c, p, f ->
            updatedCarbs = c
            updatedProtein = p
            updatedFat = f
          }
        )
      }
    }

    // Verify root screen and main Recharts-style cards
    composeTestRule.onNodeWithTag("daily_nutrition_tracking_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("recharts_donut_card").assertIsDisplayed()
    composeTestRule.onNodeWithTag("recharts_donut_canvas").assertIsDisplayed()

    // Verify macro progress cards
    composeTestRule.onNodeWithTag("macro_card_carbs").assertExists()
    composeTestRule.onNodeWithTag("macro_card_protein").assertExists()
    composeTestRule.onNodeWithTag("macro_card_fats").assertExists()

    // Verify scanned meals breakdown
    composeTestRule.onNodeWithTag("scanned_meals_macro_breakdown_card").assertExists()

    // Test back button
    composeTestRule.onNodeWithTag("nutrition_back_button").performClick()
    assertEquals(AppDestination.HOME, navigatedDestination)
  }

  @Test
  fun testCalibrateMacroTargetsDialog() {
    var targetUpdated = false

    composeTestRule.setContent {
      AhaarTheme {
        DailyNutritionTrackingScreen(
          uiState = AhaarUiState(),
          onNavigate = {},
          onUpdateMacroTargets = { _, _, _ -> targetUpdated = true }
        )
      }
    }

    // Click calibrate goals button
    composeTestRule.onNodeWithTag("calibrate_macro_targets_button").performClick()
    composeTestRule.waitForIdle()
  }

  @Test
  fun testPersonalizedMacroGoalsComponentAndPresets() {
    var savedCalorie = 0
    var savedCarbs = 0
    var savedProtein = 0
    var savedFat = 0

    composeTestRule.setContent {
      AhaarTheme {
        com.example.ui.components.PersonalizedMacroGoalsCard(
          uiState = AhaarUiState(
            dailyCalorieTarget = 1800,
            carbsTargetGrams = 225,
            proteinTargetGrams = 112,
            fatsTargetGrams = 50
          ),
          onSaveGoals = { cal, c, p, f ->
            savedCalorie = cal
            savedCarbs = c
            savedProtein = p
            savedFat = f
          }
        )
      }
    }

    // Verify component is displayed
    composeTestRule.onNodeWithTag("personalized_macro_goals_card").assertIsDisplayed()
    composeTestRule.onNodeWithTag("edit_personalized_goals_button").assertIsDisplayed()

    // Click edit goals button
    composeTestRule.onNodeWithTag("edit_personalized_goals_button").performClick()
    composeTestRule.waitForIdle()

    // Select High Protein preset
    composeTestRule.onNodeWithTag("macro_preset_high_protein").performClick()
    composeTestRule.waitForIdle()

    // Click save
    composeTestRule.onNodeWithTag("save_personalized_goals_button").performClick()
    composeTestRule.waitForIdle()

    // Verify goals updated with non-zero values
    assertTrue("Calorie should be saved", savedCalorie > 0)
    assertTrue("Carbs should be saved", savedCarbs > 0)
    assertTrue("Protein should be saved", savedProtein > 0)
    assertTrue("Fat should be saved", savedFat > 0)
  }
}
