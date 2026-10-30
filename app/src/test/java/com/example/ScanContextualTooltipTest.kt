package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.ScanContextualTooltipBanner
import com.example.ui.components.ScanGuideChip
import com.example.ui.components.ScanTooltipStep
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
class ScanContextualTooltipTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private lateinit var context: Context

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun testScanContextualTooltipDisplaysStep1() {
    var nextClicked = false
    var dismissClicked = false

    composeTestRule.setContent {
      AhaarTheme {
        ScanContextualTooltipBanner(
          currentStep = ScanTooltipStep.LAUNCH_SCANNER,
          isVisible = true,
          onNext = { nextClicked = true },
          onPrevious = {},
          onDismiss = { dismissClicked = true },
          onActionClick = {}
        )
      }
    }

    // Verify container and elements are visible
    composeTestRule.onNodeWithTag("scan_contextual_tooltip_container").assertIsDisplayed()
    composeTestRule.onNodeWithText("Tap 'Scan' to Start").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tooltip_dismiss_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tooltip_next_button").assertIsDisplayed()

    // Test next click
    composeTestRule.onNodeWithTag("tooltip_next_button").performClick()
    assertTrue(nextClicked)
  }

  @Test
  fun testScanContextualTooltipDismiss() {
    var dismissClicked = false

    composeTestRule.setContent {
      AhaarTheme {
        ScanContextualTooltipBanner(
          currentStep = ScanTooltipStep.FRAME_ALIGNMENT,
          isVisible = true,
          onNext = {},
          onPrevious = {},
          onDismiss = { dismissClicked = true },
          onActionClick = {}
        )
      }
    }

    composeTestRule.onNodeWithText("Align Inside Reticles").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tooltip_dismiss_button").performClick()
    assertTrue(dismissClicked)
  }

  @Test
  fun testScanContextualTooltipFinalStepAction() {
    var actionClicked = false

    composeTestRule.setContent {
      AhaarTheme {
        ScanContextualTooltipBanner(
          currentStep = ScanTooltipStep.REVIEW_DIAGNOSTICS,
          isVisible = true,
          onNext = {},
          onPrevious = {},
          onDismiss = {},
          onActionClick = { actionClicked = true }
        )
      }
    }

    composeTestRule.onNodeWithText("Instant Macro Reports").assertIsDisplayed()
    composeTestRule.onNodeWithText("Start Scanning Now 🚀").assertIsDisplayed()

    composeTestRule.onNodeWithTag("tooltip_next_button").performClick()
    assertTrue(actionClicked)
  }

  @Test
  fun testScanGuideChipClick() {
    var chipClicked = false

    composeTestRule.setContent {
      AhaarTheme {
        ScanGuideChip(onClick = { chipClicked = true })
      }
    }

    composeTestRule.onNodeWithTag("home_scan_tooltip_help_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("Scan Guide").assertIsDisplayed()
    composeTestRule.onNodeWithTag("home_scan_tooltip_help_button").performClick()
    assertTrue(chipClicked)
  }
}
