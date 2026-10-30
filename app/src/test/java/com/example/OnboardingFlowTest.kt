package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.onboarding.OnboardingFlowScreen
import com.example.ui.screens.onboarding.PREF_KEY_ONBOARDING_COMPLETED
import com.example.ui.screens.onboarding.TOTAL_ONBOARDING_STEPS
import com.example.ui.theme.AhaarTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OnboardingFlowTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private lateinit var context: Context

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    // Clear preferences before test
    context.getSharedPreferences("ahaar_auth_session", Context.MODE_PRIVATE)
      .edit()
      .clear()
      .commit()
    context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
      .edit()
      .clear()
      .commit()
  }

  @Test
  fun testTotalOnboardingStepsIsNine() {
    assertEquals(9, TOTAL_ONBOARDING_STEPS)
  }

  @Test
  fun testOnboardingUiElementsDisplayed() {
    var finishCalled = false

    composeTestRule.setContent {
      AhaarTheme {
        OnboardingFlowScreen(
          uiState = AhaarUiState(onboardingStep = 0),
          onFinishOnboarding = { finishCalled = true }
        )
      }
    }

    // Verify progress indicator is displayed
    composeTestRule.onNodeWithTag("onboarding_progress_indicator").assertIsDisplayed()

    // Verify 'Skip' button is displayed
    composeTestRule.onNodeWithTag("onboarding_skip_button").assertIsDisplayed()

    // Verify continue button is displayed
    composeTestRule.onNodeWithTag("onboarding_continue_button").assertIsDisplayed()

    // Verify nutrition benefits are present
    composeTestRule.onNodeWithText("AI-POWERED INDIAN NUTRITION").assertIsDisplayed()
    composeTestRule.onNodeWithText("Meet CalSnapix\nYour AI Nutritionist").assertIsDisplayed()
  }

  @Test
  fun testSkipButtonSavesOnboardingCompletedInSharedPreferences() {
    var finishCalled = false

    composeTestRule.setContent {
      AhaarTheme {
        OnboardingFlowScreen(
          uiState = AhaarUiState(onboardingStep = 0),
          onFinishOnboarding = { finishCalled = true }
        )
      }
    }

    // Click 'Skip' button
    composeTestRule.onNodeWithTag("onboarding_skip_button").performClick()

    // Assert onFinishOnboarding was called
    assertTrue(finishCalled)

    // Verify 'onboarding_completed' flag is saved in SharedPreferences
    val sessionPrefs = context.getSharedPreferences("ahaar_auth_session", Context.MODE_PRIVATE)
    assertTrue(sessionPrefs.getBoolean(PREF_KEY_ONBOARDING_COMPLETED, false))

    val appPrefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    assertTrue(appPrefs.getBoolean(PREF_KEY_ONBOARDING_COMPLETED, false))
  }

  @Test
  fun testScreenProgressionAndCompletionSavesFlag() {
    var currentStep = 0
    var finishCalled = false

    composeTestRule.setContent {
      AhaarTheme {
        OnboardingFlowScreen(
          uiState = AhaarUiState(onboardingStep = 8), // Screen 9 of 9 (final step)
          onStepChanged = { currentStep = it },
          onFinishOnboarding = { finishCalled = true }
        )
      }
    }

    // Verify final CTA button text
    composeTestRule.onNodeWithText("Claim Blueprint & Start Tracking 🚀").assertIsDisplayed()

    // Click continue on final screen
    composeTestRule.onNodeWithTag("onboarding_continue_button").performClick()

    // Verify finish called and SharedPreferences flag saved
    assertTrue(finishCalled)
    val sessionPrefs = context.getSharedPreferences("ahaar_auth_session", Context.MODE_PRIVATE)
    assertTrue(sessionPrefs.getBoolean(PREF_KEY_ONBOARDING_COMPLETED, false))
  }
}
