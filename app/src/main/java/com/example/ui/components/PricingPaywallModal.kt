package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CalTheme
import java.util.Locale

enum class PricingCurrency(val symbol: String, val code: String, val eurRate: Double) {
  USD("$", "USD", 1.0),
  EUR("€", "EUR", 0.92)
}

data class CalPlanItem(
  val id: String,
  val name: String,
  val subtitle: String,
  val durationLabel: String,
  val priceUsd: Double,
  val originalPriceUsd: Double? = null,
  val durationDays: Int,
  val showPerDayCost: Boolean = true,
  val badge: String? = null,
  val isPopular: Boolean = false
) {
  fun getEffectivePrice(currency: PricingCurrency, discountPercent: Int = 0): Double {
    val discountedUsd = priceUsd * (1.0 - (discountPercent / 100.0))
    val converted = if (currency == PricingCurrency.EUR) discountedUsd * currency.eurRate else discountedUsd
    return (Math.round(converted * 100.0) / 100.0).coerceAtLeast(0.01)
  }

  fun getOriginalPriceFormatted(currency: PricingCurrency): String? {
    val orig = originalPriceUsd ?: return null
    val origConverted = if (currency == PricingCurrency.EUR) orig * currency.eurRate else orig
    return "${currency.symbol}${String.format(Locale.US, "%.2f", origConverted)}"
  }

  fun getFormattedPrice(currency: PricingCurrency, discountPercent: Int = 0): String {
    val p = getEffectivePrice(currency, discountPercent)
    return "${currency.symbol}${String.format(Locale.US, "%.2f", p)}"
  }

  fun getPerDayCostFormatted(currency: PricingCurrency, discountPercent: Int = 0): String {
    val effectivePrice = getEffectivePrice(currency, discountPercent)
    val perDay = effectivePrice / durationDays
    return "${currency.symbol}${String.format(Locale.US, "%.2f", perDay)}/day"
  }
}

object CalPricingConfig {
  val allPlans = listOf(
    CalPlanItem(
      id = "daily",
      name = "Daily Pass",
      subtitle = "Full 24-hr unrestricted AI meal scans & nutrition assistant",
      durationLabel = "1 Day",
      priceUsd = 0.09,
      originalPriceUsd = null,
      durationDays = 1,
      showPerDayCost = false,
      badge = "DAILY PASS"
    ),
    CalPlanItem(
      id = "weekly",
      name = "Weekly Pass",
      subtitle = "7 days unlimited calorie, barcode & macro tracking",
      durationLabel = "7 Days",
      priceUsd = 0.59,
      originalPriceUsd = null,
      durationDays = 7,
      showPerDayCost = false,
      badge = "WEEKLY PASS"
    ),
    CalPlanItem(
      id = "monthly",
      name = "Monthly Plan",
      subtitle = "Continuous Indian meal tracking & personalized calorie deficit",
      durationLabel = "1 Month",
      priceUsd = 1.99,
      originalPriceUsd = 5.99,
      durationDays = 30,
      showPerDayCost = true,
      badge = "SAVE 67%"
    ),
    CalPlanItem(
      id = "six_months",
      name = "6-Month Plan",
      subtitle = "Sustained weight transformation semester program",
      durationLabel = "6 Months",
      priceUsd = 9.99,
      originalPriceUsd = 39.99,
      durationDays = 180,
      showPerDayCost = true,
      badge = "SAVE 75%"
    ),
    CalPlanItem(
      id = "yearly",
      name = "Yearly Plan",
      subtitle = "Ultimate annual wellness & lifestyle mastery • Highest savings",
      durationLabel = "1 Year (365 Days)",
      priceUsd = 14.99,
      originalPriceUsd = 69.99,
      durationDays = 365,
      showPerDayCost = true,
      badge = "BEST VALUE • 79% OFF",
      isPopular = true
    )
  )

  data class CompetitorPlanComparison(
    val planName: String,
    val otherBrandsUsd: Double,
    val calSnapixUsd: Double
  ) {
    fun getOtherBrandsFormatted(currency: PricingCurrency): String {
      val converted = if (currency == PricingCurrency.EUR) otherBrandsUsd * currency.eurRate else otherBrandsUsd
      return "${currency.symbol}${String.format(Locale.US, "%.2f", converted)}"
    }

    fun getCalSnapixFormatted(currency: PricingCurrency, discountPercent: Int = 0): String {
      val discountedUsd = calSnapixUsd * (1.0 - (discountPercent / 100.0))
      val converted = if (currency == PricingCurrency.EUR) discountedUsd * currency.eurRate else discountedUsd
      return "${currency.symbol}${String.format(Locale.US, "%.2f", converted)}"
    }

    fun getSavingsPercent(discountPercent: Int = 0): Int {
      val actualCalSnapix = calSnapixUsd * (1.0 - (discountPercent / 100.0))
      return (((otherBrandsUsd - actualCalSnapix) / otherBrandsUsd) * 100).toInt()
    }
  }

  val competitorComparisons = listOf(
    CompetitorPlanComparison("Per Month", 9.99, 1.99),
    CompetitorPlanComparison("6-Month Plan", 69.99, 9.99),
    CompetitorPlanComparison("Yearly Plan", 99.99, 14.99)
  )
}

/**
 * Reusable Pricing Paywall content featuring the 5 plans, currency switcher (USD & EUR),
 * per-day costs, other brands comparison, and highlighted "Day Only" per-day cost.
 */
@Composable
fun PricingPaywallContent(
  onSubscribeSuccess: (String) -> Unit,
  onDismiss: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val scrollState = rememberScrollState()
  val context = LocalContext.current

  var selectedCurrency by remember { mutableStateOf(PricingCurrency.USD) }
  var selectedPlanId by remember { mutableStateOf("yearly") }

  val activeDiscountPercent = 0
  val selectedPlan = CalPricingConfig.allPlans.find { it.id == selectedPlanId } ?: CalPricingConfig.allPlans.last()
  val dailyPlan = CalPricingConfig.allPlans.first { it.id == "daily" }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(colors.bg)
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar with Currency Selector & Dismiss
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Currency Switcher: USD ($) by default and EUR (€)
      Surface(
        shape = RoundedCornerShape(999.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.line)
      ) {
        Row(
          modifier = Modifier.padding(3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          PricingCurrency.values().forEach { curr ->
            val isSelected = selectedCurrency == curr
            Surface(
              shape = RoundedCornerShape(999.dp),
              color = if (isSelected) colors.ink else Color.Transparent,
              modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .clickable { selectedCurrency = curr }
            ) {
              Text(
                text = "${curr.symbol} ${curr.code}",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) colors.bg else colors.ink2,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
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

    // Headline & Subtitle
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(
        text = "CalSnapix Calorie Scan Premium",
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        color = colors.ink,
        letterSpacing = (-0.5).sp
      )
      Text(
        text = "Choose a plan to unlock unlimited AI Indian food scans and personalized nutrition insights.",
        fontSize = 13.sp,
        color = colors.ink2,
        lineHeight = 18.sp
      )
    }

    // 1. HIGHLIGHTED "DAY ONLY" PER-DAY COST CALLOUT
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = colors.ink,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("day_only_per_day_cost_highlight")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFFFD54F)
          ) {
            Text(
              text = "DAY ONLY HIGHLIGHT",
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = Color.Black,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          Text(
            text = "NO COMMITMENT",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.7f)
          )
        }

        Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "${dailyPlan.getFormattedPrice(selectedCurrency, 0)} / day",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
          )
        }

        Text(
          text = "Try CalSnapix for a single full day at our absolute lowest entry rate. Enjoy 24 hours of unrestricted AI vision scans with zero recurring renewals.",
          fontSize = 12.sp,
          color = Color.White.copy(alpha = 0.85f),
          lineHeight = 16.sp
        )
      }
    }

    // 4. PLAN SELECTION CARDS (ALL 5 PLANS)
    Text(
      text = "Select Your Plan",
      fontSize = 17.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      CalPricingConfig.allPlans.forEach { plan ->
        val isSelected = selectedPlanId == plan.id
        val effectivePriceFormatted = plan.getFormattedPrice(selectedCurrency, activeDiscountPercent)
        val originalPriceFormatted = plan.getOriginalPriceFormatted(selectedCurrency)

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isSelected) colors.surface else colors.bg,
          border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) colors.ink else colors.line
          ),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { selectedPlanId = plan.id }
            .testTag("pricing_plan_card_${plan.id}")
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            // Plan Title Row & Badges
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = plan.name,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.ink
                )

                if (plan.badge != null) {
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = colors.accentSoft
                  ) {
                    Text(
                      text = plan.badge,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = colors.accent,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              // Radio / Check indicator
              Box(
                modifier = Modifier
                  .size(20.dp)
                  .clip(CircleShape)
                  .background(if (isSelected) colors.ink else Color.Transparent)
                  .border(2.dp, if (isSelected) colors.ink else colors.line, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                if (isSelected) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = colors.bg,
                    modifier = Modifier.size(12.dp)
                  )
                }
              }
            }

            Text(
              text = plan.subtitle,
              fontSize = 11.sp,
              color = colors.ink2,
              lineHeight = 15.sp
            )

            // Price Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = effectivePriceFormatted,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Black,
                  color = colors.ink
                )

                if (originalPriceFormatted != null) {
                  Text(
                    text = originalPriceFormatted,
                    fontSize = 13.sp,
                    color = colors.ink2,
                    textDecoration = TextDecoration.LineThrough,
                    modifier = Modifier.padding(bottom = 2.dp)
                  )
                }

                Text(
                  text = "/ ${plan.durationLabel}",
                  fontSize = 11.sp,
                  color = colors.ink2,
                  modifier = Modifier.padding(bottom = 2.dp)
                )
              }

              // PER-DAY COST: Show in every plan except day and week!
              if (plan.showPerDayCost) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = colors.surface2,
                  border = BorderStroke(1.dp, colors.line)
                ) {
                  Text(
                    text = plan.getPerDayCostFormatted(selectedCurrency, activeDiscountPercent),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.accent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // 5. COMPARISON BETWEEN OTHER BRANDS AND US
    Text(
      text = "Comparison: CalSnapix vs Other Brands",
      fontSize = 17.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )

    Surface(
      shape = RoundedCornerShape(18.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("brand_comparison_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Table Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("PLAN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.ink2, modifier = Modifier.weight(1.2f))
          Text("OTHER BRANDS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828), textAlign = TextAlign.Center, modifier = Modifier.weight(1.3f))
          Text("CALSNAPIX", fontSize = 11.sp, fontWeight = FontWeight.Black, color = colors.accent, textAlign = TextAlign.End, modifier = Modifier.weight(1.3f))
        }

        CalPricingConfig.competitorComparisons.forEach { comp ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(colors.surface2.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1.2f)) {
              Text(
                text = comp.planName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "Save ${comp.getSavingsPercent(activeDiscountPercent)}%",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.accent
              )
            }

            Text(
              text = comp.getOtherBrandsFormatted(selectedCurrency),
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = colors.ink2,
              textDecoration = TextDecoration.LineThrough,
              textAlign = TextAlign.Center,
              modifier = Modifier.weight(1.3f)
            )

            Text(
              text = comp.getCalSnapixFormatted(selectedCurrency, activeDiscountPercent),
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = colors.accent,
              textAlign = TextAlign.End,
              modifier = Modifier.weight(1.3f)
            )
          }
        }

        // Feature Comparison Bullets
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf(
            "Indian & Regional Cuisine Recognition" to "Instant with Gemini AI (vs Generic/Weak)",
            "Barcode & QR Nutrition Scanning" to "Included (vs $9.99/mo Paywall in others)",
            "24/7 AI Nutrition Assistant" to "Included Unlimited (vs Paid Add-on)",
            "Ad-Free & Local Privacy" to "100% Encrypted (vs Commercial Data Trackers)"
          ).forEach { (feat, comp) ->
            Row(
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.accent, modifier = Modifier.size(16.dp))
              Column {
                Text(feat, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.ink)
                Text(comp, fontSize = 11.sp, color = colors.ink2)
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // 6. MAIN CALL TO ACTION BUTTON
    Button(
      onClick = { onSubscribeSuccess(selectedPlan.id) },
      colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("pricing_activate_plan_button")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
        Text(
          text = "Get ${selectedPlan.name} • ${selectedPlan.getFormattedPrice(selectedCurrency, activeDiscountPercent)}",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Text(
      text = "All prices in ${selectedCurrency.code} (${selectedCurrency.symbol}). One-time and recurring options. Cancel anytime.",
      fontSize = 11.sp,
      color = colors.ink2,
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
fun PricingPaywallDialog(
  onDismiss: () -> Unit,
  onSubscribeSuccess: (String) -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = CalTheme.colors.bg,
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 24.dp)
    ) {
      PricingPaywallContent(
        onSubscribeSuccess = onSubscribeSuccess,
        onDismiss = onDismiss
      )
    }
  }
}
