package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AhaarUiState
import com.example.ui.components.CalPlanItem
import com.example.ui.components.CalPricingConfig
import com.example.ui.theme.CalTheme
import java.util.Locale

/**
 * Dedicated Full-Screen Pricing View displaying the 5 subscription tiers:
 * Daily ($0.09), Weekly ($0.90), Monthly ($1.99), 6-Month ($24.99), and Yearly ($19.99).
 * Highlights the daily cost of 0.09 and visually emphasizes the percentage discounts
 * for longer-term plans, along with the 20% Instagram follow discount integration.
 */
@Composable
fun PricingScreen(
  uiState: AhaarUiState,
  onBack: () -> Unit,
  onSelectPlan: (String) -> Unit,
  onOpenInstagram: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val colors = CalTheme.colors
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  var selectedPlanId by remember { mutableStateOf(if (uiState.activeSubscriptionPlan != "Free") uiState.activeSubscriptionPlan else "yearly") }
  var showChatPopUpDemo by remember { mutableStateOf(false) }

  // Check if 20% Instagram discount is active
  val isInstagramDiscountActive = uiState.isInstagramFollowVerified || uiState.activeSubscriptionDiscountPercent > 0
  val discountMultiplier = if (isInstagramDiscountActive) 0.80 else 1.0

  val activePlan = remember(selectedPlanId) {
    CalPricingConfig.allPlans.find { it.id == selectedPlanId } ?: CalPricingConfig.allPlans.last()
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(colors.bg)
      .verticalScroll(scrollState)
      .statusBarsPadding()
      .padding(horizontal = 20.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar with Back Arrow & Title
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
          Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = colors.accent, modifier = Modifier.size(14.dp))
          Text(
            text = "PRO MEMBERSHIP",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.accent
          )
        }
      }
    }

    // Header Headline
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(
        text = "Choose Your Pro Plan",
        fontSize = 30.sp,
        fontWeight = FontWeight.ExtraBold,
        color = colors.ink,
        letterSpacing = (-0.6).sp
      )
      Text(
        text = "Unlimited AI Indian meal scans, complete micronutrients breakdown, and 24/7 MyCal AI dietitian chat.",
        fontSize = 14.sp,
        color = colors.ink2,
        lineHeight = 20.sp
      )
    }

    // 🌟 MASTER HIGHLIGHT: PROMINENT DAILY COST CALLOUT (0.09 / DAY BASELINE)
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.ink,
      modifier = Modifier
        .fillMaxWidth()
        .shadow(6.dp, RoundedCornerShape(20.dp))
        .testTag("daily_cost_spotlight_banner")
    ) {
      Box(modifier = Modifier.fillMaxWidth()) {
        // Decorative background aura
        Box(
          modifier = Modifier
            .size(120.dp)
            .align(Alignment.TopEnd)
            .clip(CircleShape)
            .background(colors.accent.copy(alpha = 0.18f))
        )

        Column(
          modifier = Modifier.padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
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
                text = "⚡ START TODAY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = colors.bg,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
            Text(
              text = "MICRO-PRICING GUARANTEE",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White.copy(alpha = 0.7f),
              letterSpacing = 0.5.sp
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            Column {
              Text(
                text = if (isInstagramDiscountActive) "JUST $0.07" else "JUST $0.09",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = colors.accent,
                letterSpacing = (-1).sp
              )
              Text(
                text = "per day • less than a cup of chai ☕",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.White.copy(alpha = 0.12f),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text("UP TO", fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                Text(
                  text = if (isInstagramDiscountActive) "84% OFF" else "80% OFF",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
              }
            }
          }
        }
      }
    }

    // Instagram Verified 20% Discount Status Banner
    if (isInstagramDiscountActive) {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.accentSoft,
        border = BorderStroke(1.5.dp, colors.accent),
        modifier = Modifier.fillMaxWidth().testTag("active_instagram_discount_banner")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(shape = CircleShape, color = colors.accent, modifier = Modifier.size(32.dp)) {
            Box(contentAlignment = Alignment.Center) {
              Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "🎉 Extra 20% Instagram Discount Active",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
            Text(
              text = "Follower of @_calsnapix (${uiState.linkedInstagramHandle ?: "Verified"}) • Code INSTA20 applied",
              fontSize = 11.sp,
              color = colors.ink2
            )
          }
        }
      }
    } else {
      // Prompt to link Instagram and get 20% OFF
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE1306C).copy(alpha = 0.08f),
        border = BorderStroke(1.2.dp, Color(0xFFE1306C).copy(alpha = 0.35f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onOpenInstagram() }
          .testTag("follow_instagram_prompt_card")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text("📸", fontSize = 22.sp)
            Column {
              Text(
                text = "Want an additional 20% OFF?",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "Follow @_calsnapix on Instagram to unlock code INSTA20",
                fontSize = 11.sp,
                color = colors.ink2
              )
            }
          }
          Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE1306C)) {
            Text(
              text = "FOLLOW",
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }

    // 🌟 24/7 AI DIETITIAN CHAT POP-UP SHOWCASE
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = colors.surface,
      border = BorderStroke(1.5.dp, colors.accent.copy(alpha = 0.6f)),
      modifier = Modifier
        .fillMaxWidth()
        .clickable { showChatPopUpDemo = true }
        .testTag("pricing_chat_spotlight_card")
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
            Text("24/7 MyCal AI Dietitian Chat", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          }
          Surface(shape = RoundedCornerShape(6.dp), color = colors.accentSoft) {
            Text("POP-UP DEMO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.accent, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
          }
        }
        Text(
          text = "Included with all Pro plans. Instant feedback on Indian thalis, dining out, and portion control anytime.",
          fontSize = 12.sp,
          color = colors.ink2,
          lineHeight = 17.sp
        )
      }
    }

    // SECTION TITLE: ALL FIVE SUBSCRIPTION TIERS
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "All 5 Subscription Tiers",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink
      )
      Text(
        text = "Tap to select",
        fontSize = 12.sp,
        color = colors.ink2
      )
    }

    // 5 SUBSCRIPTION TIERS LIST
    CalPricingConfig.allPlans.forEach { plan ->
      val isSelected = selectedPlanId == plan.id
      val finalPriceUsd = plan.priceUsd * discountMultiplier
      val formattedFinalPrice = String.format(Locale.US, "$%.2f", finalPriceUsd)
      val formattedOrigPrice = String.format(Locale.US, "$%.2f", plan.originalPriceUsd)

      val dynamicPerDayText = when (plan.id) {
        "daily" -> String.format(Locale.US, "$%.2f / day", finalPriceUsd)
        "weekly" -> String.format(Locale.US, "$%.2f / day", finalPriceUsd / 7.0)
        "monthly" -> String.format(Locale.US, "$%.2f / day", finalPriceUsd / 30.0)
        "six_months" -> String.format(Locale.US, "$%.2f / day", finalPriceUsd / 180.0)
        "yearly" -> String.format(Locale.US, "$%.2f / day", finalPriceUsd / 365.0)
        else -> plan.perDayCost
      }

      Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) colors.surface else colors.surface2,
        border = BorderStroke(
          if (isSelected) 2.5.dp else 1.dp,
          if (isSelected) colors.accent else colors.line
        ),
        shadowElevation = if (isSelected) 4.dp else 0.dp,
        modifier = Modifier
          .fillMaxWidth()
          .clickable { selectedPlanId = plan.id }
          .testTag("pricing_tier_${plan.id}")
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Top Row: Name, Radio, Discount Badge, and Final Total Price
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Custom Radio Indicator
              Surface(
                shape = CircleShape,
                color = if (isSelected) colors.accent else Color.Transparent,
                border = BorderStroke(2.dp, if (isSelected) colors.accent else colors.ink2),
                modifier = Modifier.size(22.dp)
              ) {
                if (isSelected) {
                  Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                  }
                }
              }

              Column {
                Text(
                  text = plan.name,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.ink
                )
                if (plan.badge != null) {
                  Spacer(modifier = Modifier.height(2.dp))
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) colors.accent else colors.ink
                  ) {
                    Text(
                      text = plan.badge,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }
            }

            // Price column
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = formattedOrigPrice,
                fontSize = 12.sp,
                color = colors.ink2,
                textDecoration = TextDecoration.LineThrough
              )
              Text(
                text = formattedFinalPrice,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) colors.accent else colors.ink
              )
            }
          }

          // 1-Week Limited Alert banner for daily & weekly
          if (plan.isLimitedWeekOnly) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = colors.warn.copy(alpha = 0.12f),
              border = BorderStroke(1.dp, colors.warn.copy(alpha = 0.4f))
            ) {
              Text(
                text = "⚡ Available this week only! Removed after 7 days.",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.warn,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          // 🌟 PROMINENT PER-DAY COST BOX WITH EMPHASIZED PERCENTAGE DISCOUNT
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) colors.accent else colors.accentSoft,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "DAILY COST:",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else colors.ink
                )
                Text(
                  text = dynamicPerDayText,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Black,
                  color = if (isSelected) Color.White else colors.accent
                )
              }

              // Visual Percentage Discount Highlight
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) Color.White else colors.ink
              ) {
                Text(
                  text = if (isInstagramDiscountActive) "SAVE ${plan.discountPercent + 4}%" else "SAVE ${plan.discountPercent}%",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  color = if (isSelected) colors.accent else colors.bg,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }

          Text(
            text = plan.subtitle,
            fontSize = 12.sp,
            color = colors.ink2
          )
        }
      }
    }

    // Action CTA Button
    Button(
      onClick = {
        onSelectPlan(selectedPlanId)
        Toast.makeText(context, "🎉 Selected ${activePlan.name}! Welcome to CalSnapix Pro.", Toast.LENGTH_LONG).show()
        onBack()
      },
      colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .testTag("pricing_screen_subscribe_button")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
        Text(
          text = "Continue with ${activePlan.name}",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Guarantee & Trust
    Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("✓ Cancel anytime", fontSize = 11.sp, color = colors.ink2)
      Text("•", color = colors.line)
      Text("✓ Instant activation", fontSize = 11.sp, color = colors.ink2)
      Text("•", color = colors.line)
      Text("✓ 100% Secure Checkout", fontSize = 11.sp, color = colors.ink2)
    }

    Spacer(modifier = Modifier.height(20.dp))
  }

  // 24/7 AI Dietitian Chat Pop-Up Dialog
  if (showChatPopUpDemo) {
    AlertDialog(
      onDismissRequest = { showChatPopUpDemo = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = colors.accent)
          Text("24/7 MyCal AI Dietitian Chat", fontWeight = FontWeight.Bold, color = colors.ink)
        }
      },
      text = {
        Column(
          modifier = Modifier.verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Your 24/7 AI Dietitian is included with every Pro plan:",
            fontSize = 13.sp,
            color = colors.ink2
          )
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surface2,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text("• Instant meal breakdown for rotis, dals, and street snacks", fontSize = 12.sp, color = colors.ink)
              Text("• Dynamic portion recommendations before ordering", fontSize = 12.sp, color = colors.ink)
              Text("• Ayurvedic eating window reminders based on Agni", fontSize = 12.sp, color = colors.ink)
              Text("• Micronutrient tracking: Calcium, Iron & B12 guidance", fontSize = 12.sp, color = colors.ink)
            }
          }
          Text(
            text = "🌟 Available without limits on Daily, Weekly, Monthly, 6-Month, and Yearly plans!",
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
}
