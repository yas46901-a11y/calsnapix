package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CalTheme

data class CalPlanItem(
  val id: String,
  val name: String,
  val subtitle: String,
  val priceUsd: Double = 0.0,
  val originalPriceUsd: Double = 0.0,
  val priceInr: Double = 0.0,
  val originalPriceInr: Double = 0.0,
  val perDayCost: String = "Free",
  val perDayCostInr: String = "Free",
  val perDayHighlight: String = "100% FREE",
  val discountPercent: Int = 100,
  val isLimitedWeekOnly: Boolean = false,
  val validityNotice: String? = null,
  val badge: String? = null
)

object CalPricingConfig {
  val allPlans = listOf(
    CalPlanItem(
      id = "free_forever",
      name = "CalSnapix Free Forever",
      subtitle = "Unlimited AI Indian food scans, QR & barcode reader, and 24/7 AI nutrition assistant",
      priceUsd = 0.0,
      priceInr = 0.0,
      perDayCost = "Free",
      perDayCostInr = "Free",
      perDayHighlight = "100% FREE FOREVER",
      discountPercent = 100,
      badge = "FREE FOREVER"
    )
  )
}

/**
 * Free celebration modal: all features unlocked with no fees or subscription.
 */
@Composable
fun PricingPaywallContent(
  onSubscribeSuccess: (String) -> Unit,
  onDismiss: (() -> Unit)? = null,
  initialInstagramFollowed: Boolean = false,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(colors.bg)
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar with Dismiss
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
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

      if (onDismiss != null) {
        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(colors.surface2)
            .testTag("dismiss_paywall_button")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.ink, modifier = Modifier.size(18.dp))
        }
      }
    }

    // Headline
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(
        text = "CalSnapix is Free",
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = colors.ink,
        letterSpacing = (-0.5).sp
      )
      Text(
        text = "Every feature is completely free for everyone. No subscription required.",
        fontSize = 14.sp,
        color = colors.ink2
      )
    }

    // Big Free Status Card
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.ink,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Surface(shape = RoundedCornerShape(6.dp), color = colors.accent) {
          Text(
            text = "ALL ACCESS UNLOCKED",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.bg,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }

        Text(
          text = "₹0 • Always Free",
          fontSize = 26.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )

        Text(
          text = "Unlimited food photo analysis, high-accuracy barcode & QR scanner, daily macro calculators, and Vedic meal window timers are 100% free.",
          fontSize = 12.sp,
          color = Color.White.copy(alpha = 0.85f),
          lineHeight = 17.sp
        )
      }
    }

    // Feature highlights
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      listOf(
        "Unlimited Indian Food Photo Scans" to Icons.Default.AutoAwesome,
        "High-Precision Barcode & QR Code Reader" to Icons.Default.QrCodeScanner,
        "24/7 AI Nutrition Assistant (Non-Medical)" to Icons.Default.ElectricBolt,
        "Ayurvedic Meal Windows & Water Tracking" to Icons.Default.Favorite,
        "Private & Encrypted Local Storage" to Icons.Default.Security
      ).forEach { (feat, icon) ->
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = colors.surface,
          border = BorderStroke(1.dp, colors.line),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(icon, contentDescription = null, tint = colors.accent, modifier = Modifier.size(18.dp))
            Text(feat, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.ink, modifier = Modifier.weight(1f))
            Icon(Icons.Default.CheckCircle, contentDescription = "Free", tint = colors.accent, modifier = Modifier.size(16.dp))
          }
        }
      }
    }

    // Continue CTA
    Button(
      onClick = { onSubscribeSuccess("free_forever") },
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("free_continue_cta_button")
    ) {
      Text("Continue with Free Access", fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
fun PricingPaywallDialog(
  onDismiss: () -> Unit,
  onSubscribeSuccess: (String) -> Unit,
  initialInstagramFollowed: Boolean = false
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = CalTheme.colors.bg,
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 24.dp)
    ) {
      PricingPaywallContent(
        onSubscribeSuccess = onSubscribeSuccess,
        onDismiss = onDismiss,
        initialInstagramFollowed = initialInstagramFollowed
      )
    }
  }
}
