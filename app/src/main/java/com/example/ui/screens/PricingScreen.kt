package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AhaarUiState
import com.example.ui.components.PricingPaywallContent
import com.example.ui.theme.CalTheme

/**
 * CalSnapix Full-Screen Premium Pricing Screen:
 * Displays all 5 premium tiers, per-day costs on monthly, 6-month, and yearly plans,
 * competitor comparison (Other Brands vs CalSnapix), and currency conversion ($ USD / € EUR).
 * Pure plans-only layout without free bypass or third-party social offers.
 */
@Composable
fun PricingScreen(
  uiState: AhaarUiState,
  onBack: () -> Unit,
  onSelectPlan: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val colors = CalTheme.colors

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(colors.bg)
      .statusBarsPadding()
  ) {
    // Custom Top Navigation Bar with Back Button and Screen Title
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier
          .clip(CircleShape)
          .background(colors.surface2)
          .testTag("pricing_screen_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = colors.ink
        )
      }

      Text(
        text = "Calorie Scan Premium",
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink,
        modifier = Modifier.padding(start = 12.dp)
      )
    }

    PricingPaywallContent(
      onSubscribeSuccess = { planId ->
        onSelectPlan(planId)
        onBack()
      },
      onDismiss = null,
      modifier = Modifier.fillMaxSize()
    )
  }
}
