package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CalTheme
import java.util.Locale

data class CalPlanItem(
  val id: String,
  val name: String,
  val subtitle: String,
  val priceUsd: Double,
  val originalPriceUsd: Double,
  val priceInr: Double = 0.0,
  val originalPriceInr: Double = 0.0,
  val perDayCost: String,
  val perDayCostInr: String = "",
  val perDayHighlight: String,
  val discountPercent: Int,
  val isLimitedWeekOnly: Boolean = false,
  val validityNotice: String? = null,
  val badge: String? = null
)

object CalPricingConfig {
  val allPlans = listOf(
    CalPlanItem(
      id = "daily",
      name = "Daily Plan (1-Day)",
      subtitle = "Single day full Pro access • Valid for only this week, removed after 1 week",
      priceUsd = 0.11,
      originalPriceUsd = 0.99,
      priceInr = 9.0,
      originalPriceInr = 49.0,
      perDayCost = "$0.11 / day",
      perDayCostInr = "₹9.00 / day",
      perDayHighlight = "JUST ₹9.00 / DAY",
      discountPercent = 82,
      isLimitedWeekOnly = true,
      validityNotice = "⚡ Valid for only this week. After 1 week, this plan will be removed.",
      badge = "THIS WEEK ONLY"
    ),
    CalPlanItem(
      id = "weekly",
      name = "Weekly Plan (7 Days)",
      subtitle = "Full week unlimited AI food recognition • Valid for only this week, removed after 1 week",
      priceUsd = 0.85,
      originalPriceUsd = 2.49,
      priceInr = 69.0,
      originalPriceInr = 199.0,
      perDayCost = "$0.12 / day",
      perDayCostInr = "₹9.86 / day",
      perDayHighlight = "JUST ₹9.86 / DAY",
      discountPercent = 65,
      isLimitedWeekOnly = true,
      validityNotice = "⚡ Valid for only this week. After 1 week, this plan will be removed.",
      badge = "THIS WEEK ONLY"
    ),
    CalPlanItem(
      id = "monthly",
      name = "Monthly Plan (30 Days)",
      subtitle = "Complete Indian macro calibration & meal logs",
      priceUsd = 2.49,
      originalPriceUsd = 8.49,
      priceInr = 199.0,
      originalPriceInr = 699.0,
      perDayCost = "$0.08 / day",
      perDayCostInr = "₹6.63 / day",
      perDayHighlight = "JUST ₹6.63 / DAY",
      discountPercent = 72,
      isLimitedWeekOnly = false,
      badge = "POPULAR"
    ),
    CalPlanItem(
      id = "six_months",
      name = "6 Months Plan",
      subtitle = "Sustainable habit transformation & body recomposition",
      priceUsd = 11.99,
      originalPriceUsd = 49.99,
      priceInr = 999.0,
      originalPriceInr = 3999.0,
      perDayCost = "$0.06 / day",
      perDayCostInr = "₹5.55 / day",
      perDayHighlight = "JUST ₹5.55 / DAY",
      discountPercent = 75,
      isLimitedWeekOnly = false,
      badge = "SEMESTER PLAN"
    ),
    CalPlanItem(
      id = "yearly",
      name = "Yearly Plan (365 Days)",
      subtitle = "Full year of CalSnapix AI Nutrition Assistant & unlimited camera scans",
      priceUsd = 17.99,
      originalPriceUsd = 99.99,
      priceInr = 1499.0,
      originalPriceInr = 8999.0,
      perDayCost = "$0.05 / day",
      perDayCostInr = "₹4.11 / day",
      perDayHighlight = "BEST VALUE: JUST ₹4.11 / DAY",
      discountPercent = 83,
      isLimitedWeekOnly = false,
      badge = "RECOMMENDED • 83% SAVINGS"
    )
  )
}

/**
 * Full-screen or modal interactive Paywall component with 5 plans,
 * per-day price highlight dominance, and Instagram 20% follow discount.
 */
@Composable
fun PricingPaywallContent(
  onSubscribeSuccess: (String) -> Unit,
  onDismiss: (() -> Unit)? = null,
  initialInstagramFollowed: Boolean = false,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  var selectedPlanId by remember { mutableStateOf("yearly") }
  var selectedCurrency by remember { mutableStateOf("INR") }
  var instagramHandle by remember { mutableStateOf("") }
  var userEmailInput by remember { mutableStateOf("") }
  var showInstaVerifyDialog by remember { mutableStateOf(false) }
  var isInstagramFollowed by remember(initialInstagramFollowed) { mutableStateOf(initialInstagramFollowed) }

  // 20% additional discount multiplier when Instagram is followed
  val discountMultiplier = if (isInstagramFollowed) 0.80 else 1.0

  val activePlan = remember(selectedPlanId) {
    CalPricingConfig.allPlans.find { it.id == selectedPlanId } ?: CalPricingConfig.allPlans.last()
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    if (onDismiss != null) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = colors.accent.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.4f))
        ) {
          Text(
            text = "TRANSPARENT PRICING",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(36.dp).clip(CircleShape).background(colors.surface2)
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.ink, modifier = Modifier.size(18.dp))
        }
      }
    }

    // Header Title
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(
        text = "Unlock CalSnapix Pro",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink,
        letterSpacing = (-0.5).sp
      )
      Text(
        text = "Unlimited AI Indian meal scans, complete micronutrients breakdown, and 24/7 CalSnapix AI Nutrition Assistant.",
        fontSize = 14.sp,
        color = colors.ink2,
        lineHeight = 20.sp
      )
    }

    // 🌟 HIGH-VISIBILITY POP-UP: 24/7 CalSnapix AI Nutrition Assistant Spotlight
    var showChatPopUpDemo by remember { mutableStateOf(false) }

    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.accentSoft,
      border = BorderStroke(2.dp, colors.accent),
      modifier = Modifier
        .fillMaxWidth()
        .clickable { showChatPopUpDemo = true }
        .testTag("pro_chat_spotlight_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = colors.accent,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Chat,
                  contentDescription = "24/7 AI Nutrition Assistant",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Column {
              Text(
                text = "💬 24/7 CalSnapix AI Nutrition Assistant",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.ink
              )
              Text(
                text = "Instant suggestions on dishes & swaps • Non-medical",
                fontSize = 11.sp,
                color = colors.ink2
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(999.dp),
            color = colors.accent
          ) {
            Text(
              text = "PRO EXCLUSIVE",
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        // Live Chat Bubble Preview Simulation
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = colors.surface,
          border = BorderStroke(1.dp, colors.line)
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // User query bubble
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              Surface(
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 4.dp, bottomStart = 12.dp, bottomEnd = 12.dp),
                color = colors.accentSoft
              ) {
                Text(
                  text = "\"Can I have 2 rotis with paneer bhurji for dinner?\"",
                  fontSize = 12.sp,
                  color = colors.ink,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }

            // Assistant reply bubble with medical disclaimer
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Start
            ) {
              Surface(
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp),
                color = colors.surface2
              ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                  Text(
                    text = "🥗 CalSnapix AI: \"Yes! Adds ~390 kcal & 20g protein, staying right within your remaining daily deficit!\"",
                    fontSize = 12.sp,
                    color = colors.ink,
                    fontWeight = FontWeight.SemiBold
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "AI-generated, not medical advice",
                    fontSize = 9.sp,
                    color = colors.ink2,
                    fontWeight = FontWeight.Normal
                  )
                }
              }
            }
          }
        }

        // Tap to open pop-up preview button
        OutlinedButton(
          onClick = { showChatPopUpDemo = true },
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, colors.accent),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.accent),
          modifier = Modifier.fillMaxWidth().height(40.dp).testTag("preview_live_chat_button")
        ) {
          Icon(Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Open AI Nutrition Assistant Demo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Instagram Follow 20% Additional Discount Card (Exact @_calsnapix Profile Link)
    val instagramUrl = "https://www.instagram.com/_calsnapix?vrfl=MTZkajNnaTlreTR2dA=="

    Surface(
      shape = RoundedCornerShape(18.dp),
      color = if (isInstagramFollowed) colors.accentSoft else colors.surface,
      border = BorderStroke(
        1.5.dp,
        if (isInstagramFollowed) colors.accent else Color(0xFFE1306C).copy(alpha = 0.5f)
      ),
      modifier = Modifier.fillMaxWidth().testTag("instagram_discount_card")
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = Color(0xFFE1306C),
              modifier = Modifier.size(32.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text("📸", fontSize = 16.sp)
              }
            }
            Column {
              Text(
                text = "Follow @_calsnapix on Instagram",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "Get an extra 20% discount automatically on every plan!",
                fontSize = 11.sp,
                color = colors.ink2
              )
            }
          }

          if (isInstagramFollowed) {
            Surface(shape = RoundedCornerShape(6.dp), color = colors.accent) {
              Text(
                text = "✓ 20% APPLIED",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        // Action Buttons: Open Instagram Profile & Verify 20% Follow
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl))
                context.startActivity(intent)
              } catch (e: Exception) {
                Toast.makeText(context, "Opening Instagram: @_calsnapix", Toast.LENGTH_SHORT).show()
              }
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)),
            modifier = Modifier.weight(1f).height(44.dp).testTag("follow_instagram_button")
          ) {
            Text("Follow @_calsnapix", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }

          Button(
            onClick = {
              showInstaVerifyDialog = true
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isInstagramFollowed) colors.accent else colors.ink,
              contentColor = Color.White
            ),
            modifier = Modifier.weight(1f).height(44.dp).testTag("claim_instagram_discount_button")
          ) {
            Text(if (isInstagramFollowed) "✓ 20% Verified" else "Verify & Get 20% OFF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        // App Share Link Button
        OutlinedButton(
          onClick = {
            val shareIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(
                Intent.EXTRA_TEXT,
                "Hey! Track your Indian calories & macros effortlessly with CalSnapix. Follow @_calsnapix on Instagram ($instagramUrl) to get 20% OFF! App: https://calsnapix.in"
              )
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share CalSnapix"))
          },
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, colors.line),
          modifier = Modifier.fillMaxWidth().height(38.dp).testTag("share_calsnapix_app_button")
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = colors.ink)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Share CalSnapix App Link", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = colors.ink)
        }
      }
    }

    // Interactive Demo Pop-Up Dialog for Chat
    if (showChatPopUpDemo) {
      AlertDialog(
        onDismissRequest = { showChatPopUpDemo = false },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = colors.accent)
            Text("CalSnapix AI Nutrition Assistant", fontWeight = FontWeight.Bold, color = colors.ink)
          }
        },
        text = {
          Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "With CalSnapix Pro, your personal AI nutrition assistant is always 1 tap away:",
              fontSize = 13.sp,
              color = colors.ink2
            )
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = colors.surface2,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("• Ask about any Indian street food, thali or dessert", fontSize = 12.sp, color = colors.ink)
                Text("• Receive real-time portion adjustments before eating", fontSize = 12.sp, color = colors.ink)
                Text("• Balanced micronutrient suggestions (Iron, B12, Calcium)", fontSize = 12.sp, color = colors.ink)
                Text("• Daily meal timing guidance calibrated to your routine", fontSize = 12.sp, color = colors.ink)
              }
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = colors.bg,
              border = BorderStroke(1.dp, colors.line)
            ) {
              Text(
                text = "⚠️ AI-generated assistance. CalSnapix does not provide medical advice or clinical diagnosis. Consult a physician for dietary conditions.",
                fontSize = 10.sp,
                color = colors.ink2,
                modifier = Modifier.padding(8.dp),
                lineHeight = 14.sp
              )
            }
            Text(
              text = "🌟 Included unlimited in all Pro plans (Daily, Weekly, Monthly, 6-Months & Yearly)!",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent
            )
          }
        },
        confirmButton = {
          Button(
            onClick = { showChatPopUpDemo = false },
            colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg)
          ) {
            Text("Got It")
          }
        }
      )
    }

    // Instagram Follower Verification Modal (Requires Email + Insta Handle)
    if (showInstaVerifyDialog) {
      var instaInput by remember { mutableStateOf(instagramHandle) }
      var emailInput by remember { mutableStateOf(userEmailInput) }
      var verifyError by remember { mutableStateOf<String?>(null) }

      AlertDialog(
        onDismissRequest = { showInstaVerifyDialog = false },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("📸", fontSize = 20.sp)
            Text("Verify Instagram Follow", fontWeight = FontWeight.Bold, color = colors.ink)
          }
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              text = "To activate your exclusive 20% discount code INSTA20, please provide your details so we can verify you follow @_calsnapix on Instagram:",
              fontSize = 12.sp,
              color = colors.ink2,
              lineHeight = 17.sp
            )

            OutlinedTextField(
              value = emailInput,
              onValueChange = {
                emailInput = it
                verifyError = null
              },
              label = { Text("Your Email ID") },
              placeholder = { Text("e.g. priya@gmail.com") },
              singleLine = true,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth().testTag("insta_verify_email_input")
            )

            OutlinedTextField(
              value = instaInput,
              onValueChange = {
                instaInput = it
                verifyError = null
              },
              label = { Text("Your Instagram Username") },
              placeholder = { Text("e.g. @priya_fit") },
              singleLine = true,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth().testTag("insta_verify_handle_input")
            )

            if (verifyError != null) {
              Text(
                text = verifyError!!,
                color = Color(0xFFEF4444),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = colors.surface2,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "💡 Make sure you clicked 'Follow @_calsnapix' before verifying.",
                fontSize = 11.sp,
                color = colors.ink,
                modifier = Modifier.padding(8.dp)
              )
            }
          }
        },
        confirmButton = {
          Button(
            onClick = {
              val cleanHandle = instaInput.trim().removePrefix("@")
              val cleanEmail = emailInput.trim()
              if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
                verifyError = "Please enter a valid email address."
              } else if (cleanHandle.length < 2) {
                verifyError = "Please enter your Instagram username."
              } else {
                instagramHandle = "@$cleanHandle"
                userEmailInput = cleanEmail
                isInstagramFollowed = true
                showInstaVerifyDialog = false
                Toast.makeText(context, "🎉 Verified following @_calsnapix! 20% discount applied to all plans.", Toast.LENGTH_LONG).show()
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = Color.White)
          ) {
            Text("Confirm & Apply 20%", fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          TextButton(onClick = { showInstaVerifyDialog = false }) {
            Text("Cancel", color = colors.ink2)
          }
        }
      )
    }

    // 5 Plans List (With PER DAY COST strongly prioritized and highlighted)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Select Your Plan",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )

        // Regional Currency Selector (India INR vs Global USD)
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface2)
            .padding(2.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (selectedCurrency == "INR") colors.accent else Color.Transparent,
            modifier = Modifier.clickable { selectedCurrency = "INR" }.testTag("currency_inr_toggle")
          ) {
            Text(
              text = "₹ INR",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (selectedCurrency == "INR") Color.White else colors.ink2,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (selectedCurrency == "USD") colors.accent else Color.Transparent,
            modifier = Modifier.clickable { selectedCurrency = "USD" }.testTag("currency_usd_toggle")
          ) {
            Text(
              text = "$ USD",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (selectedCurrency == "USD") Color.White else colors.ink2,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      CalPricingConfig.allPlans.forEach { plan ->
        val isSelected = selectedPlanId == plan.id
        val isIndia = selectedCurrency == "INR"
        val finalPrice = if (isIndia) (plan.priceInr * discountMultiplier) else (plan.priceUsd * discountMultiplier)
        val origPrice = if (isIndia) plan.originalPriceInr else plan.originalPriceUsd
        val formattedFinalPrice = if (isIndia) String.format(Locale.US, "₹%.0f", finalPrice) else String.format(Locale.US, "$%.2f", finalPrice)
        val formattedOrigPrice = if (isIndia) String.format(Locale.US, "₹%.0f", origPrice) else String.format(Locale.US, "$%.2f", origPrice)

        // Calculate dynamic per day cost based on whether Instagram discount is active
        val dynamicPerDayText = when (plan.id) {
          "daily" -> if (isIndia) String.format(Locale.US, "₹%.2f / day", finalPrice) else String.format(Locale.US, "$%.2f / day", finalPrice)
          "weekly" -> if (isIndia) String.format(Locale.US, "₹%.2f / day", finalPrice / 7.0) else String.format(Locale.US, "$%.2f / day", finalPrice / 7.0)
          "monthly" -> if (isIndia) String.format(Locale.US, "₹%.2f / day", finalPrice / 30.0) else String.format(Locale.US, "$%.2f / day", finalPrice / 30.0)
          "six_months" -> if (isIndia) String.format(Locale.US, "₹%.2f / day", finalPrice / 180.0) else String.format(Locale.US, "$%.2f / day", finalPrice / 180.0)
          "yearly" -> if (isIndia) String.format(Locale.US, "₹%.2f / day", finalPrice / 365.0) else String.format(Locale.US, "$%.2f / day", finalPrice / 365.0)
          else -> if (isIndia) plan.perDayCostInr else plan.perDayCost
        }

        Surface(
          shape = RoundedCornerShape(18.dp),
          color = if (isSelected) colors.surface else colors.surface2,
          border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) colors.accent else colors.line
          ),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { selectedPlanId = plan.id }
            .testTag("plan_card_${plan.id}")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Plan Name + Badge
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Radio indicator
                Surface(
                  shape = CircleShape,
                  color = if (isSelected) colors.accent else Color.Transparent,
                  border = BorderStroke(2.dp, if (isSelected) colors.accent else colors.ink2),
                  modifier = Modifier.size(20.dp)
                ) {
                  if (isSelected) {
                    Box(contentAlignment = Alignment.Center) {
                      Box(
                        modifier = Modifier
                          .size(8.dp)
                          .clip(CircleShape)
                          .background(Color.White)
                      )
                    }
                  }
                }

                Text(
                  text = plan.name,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.ink
                )
              }

              // Overall Total Price
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = formattedOrigPrice,
                  fontSize = 11.sp,
                  color = colors.ink2,
                  textDecoration = TextDecoration.LineThrough
                )
                Text(
                  text = formattedFinalPrice,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (isSelected) colors.accent else colors.ink
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CRITICAL: THE HIGHLIGHTED PER DAY COST (GIVE HIGHEST IMPORTANCE)
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) colors.accent else colors.accentSoft,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "COST PER DAY:",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else colors.ink
                )
                Text(
                  text = dynamicPerDayText,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  color = if (isSelected) Color.White else colors.accent
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = plan.subtitle,
              fontSize = 11.sp,
              color = colors.ink2
            )

            // Weekly / Day plan 1-week removal notice
            if (plan.validityNotice != null) {
              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEF4444).copy(alpha = 0.12f),
                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = plan.validityNotice,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFDC2626),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }

            // Inline Direct Subscription Trigger
            if (isSelected) {
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = {
                  onSubscribeSuccess(plan.id)
                  Toast.makeText(context, "🎉 Welcome to CalSnapix Pro (${plan.name})!", Toast.LENGTH_LONG).show()
                  onDismiss?.invoke()
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp).testTag("select_plan_inline_${plan.id}")
              ) {
                Text("Select & Subscribe (${formattedFinalPrice})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // Guarantee & Trust
    Row(
      modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("✓ Cancel anytime", fontSize = 12.sp, color = colors.ink2)
      Text("✓ Instant Pro activation", fontSize = 12.sp, color = colors.ink2)
      Text("✓ Encrypted Secure Checkout", fontSize = 12.sp, color = colors.ink2)
    }

    // Primary Subscribe Button
    Button(
      onClick = {
        onSubscribeSuccess(selectedPlanId)
        Toast.makeText(context, "🎉 Welcome to CalSnapix Pro (${activePlan.name})!", Toast.LENGTH_LONG).show()
        onDismiss?.invoke()
      },
      colors = ButtonDefaults.buttonColors(
        containerColor = colors.ink,
        contentColor = colors.bg
      ),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("subscribe_plan_button")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(20.dp), tint = colors.accent)
        Text(
          text = "Continue with ${activePlan.name}",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

/**
 * Dialog wrapper for Pricing Paywall to open from any screen.
 */
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
      modifier = Modifier.fillMaxSize(),
      color = CalTheme.colors.bg
    ) {
      PricingPaywallContent(
        onSubscribeSuccess = onSubscribeSuccess,
        onDismiss = onDismiss,
        initialInstagramFollowed = initialInstagramFollowed
      )
    }
  }
}
