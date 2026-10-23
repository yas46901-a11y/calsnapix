package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.model.LoggedMeal
import com.example.ui.screens.RecipesScreen
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
class AiKitchenVoiceLogTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun testAiKitchenVoiceLogButtonAndBannerDisplayed() {
    var loggedMealResult: LoggedMeal? = null

    composeTestRule.setContent {
      AhaarTheme {
        RecipesScreen(
          uiState = AhaarUiState(),
          onSearchChange = {},
          onCategorySelect = {},
          onBookmarkToggle = {},
          onLogRecipeAsMeal = { loggedMealResult = it }
        )
      }
    }

    // Verify AI Voice Log button and AI Kitchen button are present
    composeTestRule.onNodeWithTag("ai_kitchen_voice_log_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ai_kitchen_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ai_kitchen_voice_banner").assertIsDisplayed()

    // Click on the Voice Log button to open the Voice Meal Logger Dialog
    composeTestRule.onNodeWithTag("ai_kitchen_voice_log_button").performClick()

    // Verify Voice Log dialog elements are visible
    composeTestRule.onNodeWithTag("ai_kitchen_voice_tap_mic").assertIsDisplayed()
    composeTestRule.onNodeWithTag("voice_log_transcript_input").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ai_kitchen_confirm_voice_log").assertIsDisplayed()

    // Confirm voice logging
    composeTestRule.onNodeWithTag("ai_kitchen_confirm_voice_log").performClick()
    assertNotNull(loggedMealResult)
    assertTrue(loggedMealResult!!.calories > 0)
    assertTrue(loggedMealResult!!.title.isNotBlank())
  }
}
