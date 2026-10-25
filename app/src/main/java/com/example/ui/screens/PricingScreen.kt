package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AhaarUiState
import com.example.ui.theme.CalTheme

/**
 * CalSnapix Free Forever Screen:
 * Displays all features as completely 100% free with zero subscriptions,
 * zero fees, no pricing, and no Instagram or referral requirements.
 */
@Composable
fun PricingScreen(
  uiState: AhaarUiState,
  onBack: () -> Unit,
  onSelectPlan: (String) -> Unit = {},
  onOpenInstagram: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val colors = CalTheme.colors
  val scrollState = rememberScrollState()

  val freeFeatures = listOf(
    Triple(
      "Unlimited AI Food Scans",
      "Instant calorie and macro estimation for rotis, dals, sabzis, thalis, and snacks with CameraX.",
      Icons.Default.AutoAwesome
    ),
    Triple(
      "High-Precision Barcode & QR Scanner",
      "Quickly scan packaged foods, groceries, beverages, and nutritional QR codes with instant nutrient breakdown.",
      Icons.Default.QrCodeScanner
    ),
    Triple(
      "24/7 AI Nutrition Assistant",
      "Get friendly, non-medical dietary suggestions, portion guidance, and answers to your food questions anytime.",
      Icons.Default.ElectricBolt
    ),
    Triple(
      "Ayurvedic Meal Windows & Water Log",
      "Traditional lifestyle circadian meal reminders and full daily 8-glass hydration tracking.",
      Icons.Default.Favorite
    ),
    Triple(
      "100% Private & Locally Encrypted",
      "No data selling, no advertising trackers, and complete one-tap account and data erasure anytime.",
      Icons.Default.Security
    )
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(colors.bg)
      .verticalScroll(scrollState)
      .statusBarsPadding()
      .padding(horizontal = 20.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(colors.surface2)
          .testTag("pricing_screen_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = colors.ink,
          modifier = Modifier.size(20.dp)
        )
      }

      Surface(
        shape = RoundedCornerShape(999.dp),
        color = colors.accentSoft,
        border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.accent, modifier = Modifier.size(14.dp))
          Text(
            text = "100% FREE FOREVER",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.accent
          )
        }
      }
    }

    // Headline
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(
        text = "CalSnapix is Free",
        fontSize = 30.sp,
        fontWeight = FontWeight.ExtraBold,
        color = colors.ink,
        letterSpacing = (-0.6).sp
      )
      Text(
        text = "All features are completely unlocked for everyone. No subscriptions, no trials, and no hidden charges.",
        fontSize = 14.sp,
        color = colors.ink2,
        lineHeight = 20.sp
      )
    }

    // Free Access Spotlight Card
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = colors.ink,
      modifier = Modifier
        .fillMaxWidth()
        .shadow(6.dp, RoundedCornerShape(22.dp))
        .testTag("free_access_spotlight_card")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = colors.accent
          ) {
            Text(
              text = "FREE FOREVER",
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = colors.bg,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
          Text(
            text = "NO SUBSCRIPTION NEEDED",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.7f),
            letterSpacing = 0.5.sp
          )
        }

        Text(
          text = "₹0 • Free For All",
          fontSize = 28.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White
        )

        Text(
          text = "Every user gets unlimited AI food photo recognition, QR & barcode scanning, and customized Indian nutrition tracking with zero fees.",
          fontSize = 13.sp,
          color = Color.White.copy(alpha = 0.85f),
          lineHeight = 18.sp
        )
      }
    }

    // Feature Cards
    Text(
      text = "What's Included (100% Free)",
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      freeFeatures.forEach { (title, desc, icon) ->
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = colors.surface,
          border = BorderStroke(1.dp, colors.line),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = colors.accentSoft,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = colors.accent,
                  modifier = Modifier.size(20.dp)
                )
              }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = desc,
                fontSize = 12.sp,
                color = colors.ink2,
                lineHeight = 17.sp
              )
            }

            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Included Free",
              tint = colors.accent,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Primary Action Button
    Button(
      onClick = onBack,
      colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("pricing_screen_continue_button")
    ) {
      Text(
        text = "Start Tracking (Free)",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}
