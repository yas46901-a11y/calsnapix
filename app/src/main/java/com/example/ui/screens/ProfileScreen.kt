package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.AhaarUiState
import com.example.AppDestination
import com.example.ui.components.GlassBorderBrush
import com.example.ui.components.GlassBorderStroke
import com.example.ui.theme.AhaarPrimary
import com.example.ui.theme.AhaarPrimaryContainer
import com.example.ui.theme.AhaarPrimaryFixed
import com.example.ui.theme.AhaarSecondary
import com.example.ui.theme.AhaarTertiary
import com.example.ui.theme.AhaarTertiaryFixed
import com.example.ui.components.PricingPaywallDialog
import com.example.ui.theme.CalTheme
import androidx.compose.ui.graphics.Brush

@Composable
fun ProfileScreen(
  uiState: AhaarUiState,
  onLogout: () -> Unit,
  onDeleteAccount: () -> Unit,
  onRecalibrateBlueprint: () -> Unit,
  onTestSupabase: () -> Unit = {},
  onSyncSupabase: () -> Unit = {},
  onDismissSupabaseMessage: () -> Unit = {},
  onUpdateDietaryPreference: (String) -> Unit = {},
  onLinkInstagram: (String) -> Unit = {},
  onUnlinkInstagram: () -> Unit = {},
  onNavigateToPricing: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  var circadianReminders by remember { mutableStateOf(true) }
  var waterReminders by remember { mutableStateOf(true) }
  var vedicFastingMode by remember { mutableStateOf(false) }

  var selectedAvatarEmoji by remember { mutableStateOf("🧘") }
  var selectedGradientIdx by remember { mutableStateOf(0) }
  var showAvatarCustomizerDialog by remember { mutableStateOf(false) }
  var showDeleteAccountConfirmDialog by remember { mutableStateOf(false) }
  var showPricingPaywallDialog by remember { mutableStateOf(false) }
  var showFullPrivacyPolicyDialog by remember { mutableStateOf(false) }
  var showFullTermsDialog by remember { mutableStateOf(false) }
  var showBreachPlanDialog by remember { mutableStateOf(false) }

  val avatarGradients = listOf(
    "Emerald Glow" to listOf(Color(0xFF10B981), Color(0xFF059669)),
    "Royal Indigo" to listOf(Color(0xFF6366F1), Color(0xFF4338CA)),
    "Sunset Amber" to listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
    "Rose Berry" to listOf(Color(0xFFEC4899), Color(0xFFBE185D)),
    "Ocean Cyan" to listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
    "Midnight Slate" to listOf(Color(0xFF334155), Color(0xFF0F172A))
  )

  val avatarEmojiOptions = listOf(
    "🧘" to "Yogi",
    "🏃" to "Runner",
    "🥑" to "Nutritionist",
    "🏋️" to "Titan",
    "🥗" to "Vitality",
    "🌟" to "Zen",
    "🌿" to "Herbalist",
    "🦁" to "Fearless",
    "⚡" to "Energy",
    "💪" to "Power"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(colors.bg)
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = "My Profile",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink
      )
      Text(
        text = "Your personal nutrition blueprint and preferences",
        fontSize = 13.sp,
        color = colors.ink2
      )
    }

    // User Identity Card with Customizable Avatar (No Photo)
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Customizable Avatar Badge (Tappable to customize)
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                colors = avatarGradients[selectedGradientIdx % avatarGradients.size].second
              )
            )
            .border(2.dp, colors.accent, CircleShape)
            .clickable { showAvatarCustomizerDialog = true }
            .testTag("profile_avatar_badge"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = selectedAvatarEmoji,
            fontSize = 32.sp
          )

          // Small Edit icon pill badge
          Surface(
            shape = CircleShape,
            color = colors.surface,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier
              .size(22.dp)
              .align(Alignment.BottomEnd)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Customize Avatar",
                tint = colors.accent,
                modifier = Modifier.size(12.dp)
              )
            }
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = uiState.userName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = uiState.userEmail,
            fontSize = 13.sp,
            color = colors.ink2
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = colors.accentSoft
            ) {
              Text(
                text = "Goal: ${uiState.selectedGoalId.replace('_', ' ').replaceFirstChar { it.uppercase() }}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accent,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = colors.surface2,
              border = BorderStroke(0.5.dp, colors.line),
              modifier = Modifier.clickable { showAvatarCustomizerDialog = true }
            ) {
              Text(
                text = "Customize",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }

    // CalSnapix 100% Free Status Banner
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.5f)),
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onNavigateToPricing() }
        .testTag("profile_free_status_banner")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text("✨", fontSize = 15.sp)
          Text("CalSnapix Free Access", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          Surface(shape = RoundedCornerShape(4.dp), color = colors.accentSoft) {
            Text("100% FREE FOREVER", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = colors.accent, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
          }
        }
        Icon(Icons.Default.CheckCircle, contentDescription = "Free Access Active", tint = colors.accent, modifier = Modifier.size(16.dp))
      }
    }

    // Nutritional Blueprint Summary
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = null,
              tint = colors.accent,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "Nutritional Blueprint",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
          }

          TextButton(onClick = onRecalibrateBlueprint) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = null,
              modifier = Modifier.size(14.dp),
              tint = colors.accent
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Recalibrate", color = colors.accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.bg,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Calorie Target", fontSize = 10.sp, color = colors.ink2)
              Text("${uiState.dailyCalorieTarget} kcal", fontWeight = FontWeight.Bold, color = colors.accent)
            }
          }
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.bg,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Protein Target", fontSize = 10.sp, color = colors.ink2)
              Text("90g / day", fontWeight = FontWeight.Bold, color = colors.ink)
            }
          }
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.bg,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Cuisine Root", fontSize = 10.sp, color = colors.ink2)
              Text(uiState.selectedRegion.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold, color = colors.ink)
            }
          }
        }
      }
    }

    // Meal Timings Card (Moved from onboarding)
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth().testTag("profile_meal_timings_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = colors.accentSoft,
            modifier = Modifier.size(32.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Column {
            Text(
              text = "When Do You Really Eat?",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
            Text(
              text = "Circadian Agni meal schedule & bio-rhythms",
              fontSize = 11.sp,
              color = colors.ink2
            )
          }
        }

        // Timing chips
        val mealTimings = listOf(
          "Breakfast" to "8:30 AM",
          "Lunch" to "1:30 PM",
          "Snack" to "5:00 PM",
          "Dinner" to "8:00 PM"
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          mealTimings.forEach { (meal, time) ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = colors.bg,
              border = BorderStroke(1.dp, colors.line),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(meal, fontSize = 10.sp, color = colors.ink2)
                Spacer(modifier = Modifier.height(2.dp))
                Text(time, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.ink)
              }
            }
          }
        }
      }
    }

    // Dietary & Lifestyle Profile Card
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth().testTag("dietary_lifestyle_profile_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
              color = colors.accentSoft,
              modifier = Modifier.size(34.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Tune,
                  contentDescription = null,
                  tint = colors.accent,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
            Column {
              Text(
                text = "Biometrics & Lifestyle",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "Personalized metabolic factors",
                fontSize = 11.sp,
                color = colors.ink2
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(50),
            color = colors.accentSoft
          ) {
            Text(
              text = "Active Plan",
              fontSize = 10.sp,
              color = colors.accent,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        // Schema attributes: Age, Gender, Activity Level
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.bg,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Age", fontSize = 10.sp, color = colors.ink2)
              Text("${uiState.age} yrs", fontWeight = FontWeight.Bold, color = colors.ink)
            }
          }
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.bg,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Gender", fontSize = 10.sp, color = colors.ink2)
              Text(uiState.gender.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold, color = colors.ink)
            }
          }
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.bg,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier.weight(1.3f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Activity Level", fontSize = 10.sp, color = colors.ink2)
              Text(uiState.activityLevel.replace('-', ' ').replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold, color = colors.accent, maxLines = 1)
            }
          }
        }

        // Dietary Preferences
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Dietary Preference:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink
          )
          val diets = listOf(
            "veg" to "Vegetarian",
            "vegan" to "Vegan",
            "jain" to "Jain (No Root)",
            "eggetarian" to "Eggetarian"
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            diets.forEach { (key, label) ->
              val isSelected = uiState.selectedDiets.contains(key)
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) colors.ink else colors.bg,
                border = BorderStroke(1.dp, if (isSelected) colors.ink else colors.line),
                modifier = Modifier
                  .clickable { onUpdateDietaryPreference(key) }
                  .testTag("diet_chip_$key")
              ) {
                Text(
                  text = if (isSelected) "✓ $label" else label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) colors.bg else colors.ink,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    }

    // CalSnapix AI Nutrition Assistant Banner (Small & Compact)
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.35f)),
      modifier = Modifier.fillMaxWidth().testTag("calsnapix_ai_profile_banner")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = "CalSnapix AI",
          tint = colors.accent,
          modifier = Modifier.size(16.dp)
        )
        Text("AI Nutrition Assistant", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.ink)
        Surface(shape = RoundedCornerShape(4.dp), color = colors.accentSoft) {
          Text("24/7 ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.accent, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Text("Non-medical", fontSize = 10.sp, color = colors.ink2)
      }
    }

    // Wellness Reminders & Preferences
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text(
          text = "Ayurvedic Daily Reminders",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )

        PreferenceSwitchRow(
          icon = Icons.Default.Schedule,
          title = "Traditional Meal Windows (Ayurveda)",
          subtitle = "Traditional lifestyle reminders for meal windows (8:30 AM & 1:30 PM)",
          checked = circadianReminders,
          onCheckedChange = { circadianReminders = it }
        )

        PreferenceSwitchRow(
          icon = Icons.Default.Opacity,
          title = "Hydration Prompt (8 Glasses)",
          subtitle = "Gentle reminders to reach your 2-liter goal across the day",
          checked = waterReminders,
          onCheckedChange = { waterReminders = it }
        )

        PreferenceSwitchRow(
          icon = Icons.Default.AutoAwesome,
          title = "Vedic Fasting Calendar",
          subtitle = "Track Ekadashi and intermittent fasting schedules seamlessly",
          checked = vedicFastingMode,
          onCheckedChange = { vedicFastingMode = it }
        )
      }
    }

    // 100% FREE FOREVER MEMBERSHIP CARD
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onNavigateToPricing() }
        .testTag("profile_free_membership_card")
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
              color = colors.accentSoft,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = colors.accent,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Column {
              Text(
                text = "CalSnapix Membership",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "All features unlocked • 100% Free Forever",
                fontSize = 12.sp,
                color = colors.ink2
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(999.dp),
            color = colors.accent
          ) {
            Text(
              text = "FREE FOREVER",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = colors.bg,
          border = BorderStroke(1.dp, colors.line)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("ZERO SUBSCRIPTIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.ink2)
              Text(
                text = "Unlimited Scans & AI Free",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.accent
              )
            }

            Button(
              onClick = onNavigateToPricing,
              colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("open_pricing_screen_button")
            ) {
              Text("View Free Features", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
            }
          }
        }
      }
    }

    // PRIVACY POLICY & TERMS TRANSPARENCY CARD
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth().testTag("profile_legal_transparency_card")
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = "Privacy & Legal Details",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
        }

        Text(
          text = "Learn exactly what data we collect (biometrics, meal photos, barcodes) and why it is needed to calculate your personalized nutrition targets.",
          fontSize = 12.sp,
          color = colors.ink2,
          lineHeight = 17.sp
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { showFullPrivacyPolicyDialog = true },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).testTag("open_privacy_dialog_btn")
          ) {
            Text("Privacy Policy", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          }

          OutlinedButton(
            onClick = { showFullTermsDialog = true },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).testTag("open_terms_dialog_btn")
          ) {
            Text("Terms of Service", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          }
        }
      }
    }



    // Account Actions: Log Out Card
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth().testTag("account_session_card")
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Account Session",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )

        Button(
          onClick = onLogout,
          colors = ButtonDefaults.buttonColors(
            containerColor = colors.bg,
            contentColor = colors.ink
          ),
          border = BorderStroke(1.dp, colors.line),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("profile_logout_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            tint = colors.ink,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Log Out of CalSnapix",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Danger Zone: Delete Account
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color(0xFFEF4444).copy(alpha = 0.08f),
      border = BorderStroke(1.2.dp, Color(0xFFEF4444).copy(alpha = 0.35f)),
      modifier = Modifier.fillMaxWidth().testTag("danger_zone_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
          Text("Danger Zone • Delete Account", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
        }
        Text(
          text = "Permanently delete your profile, custom calorie targets, recorded meals, and historical progress. This action cannot be undone.",
          fontSize = 12.sp,
          color = colors.ink2,
          lineHeight = 17.sp
        )
        Button(
          onClick = { showDeleteAccountConfirmDialog = true },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEF4444),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("delete_account_button")
        ) {
          Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Delete Account & Erase All Data", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }

  // DIALOG 1: Avatar Customizer Dialog (Pick Avatar Emoji & Gradient Theme)
  if (showAvatarCustomizerDialog) {
    AlertDialog(
      onDismissRequest = { showAvatarCustomizerDialog = false },
      title = {
        Text("Customize Your Avatar", fontWeight = FontWeight.Bold, color = colors.ink)
      },
      text = {
        Column(
          modifier = Modifier.verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text("Choose Avatar Persona:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.ink2)
          Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            avatarEmojiOptions.forEach { (emoji, label) ->
              val isSelected = selectedAvatarEmoji == emoji
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) colors.accentSoft else colors.surface2,
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) colors.accent else colors.line),
                modifier = Modifier
                  .clickable { selectedAvatarEmoji = emoji }
                  .padding(2.dp)
              ) {
                Column(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(emoji, fontSize = 26.sp)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = colors.ink)
                }
              }
            }
          }

          Text("Choose Gradient Aura:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.ink2)
          Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            avatarGradients.forEachIndexed { idx, (title, grad) ->
              val isSelected = selectedGradientIdx == idx
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(Brush.linearGradient(grad))
                  .border(if (isSelected) 3.dp else 1.dp, if (isSelected) colors.ink else Color.Transparent, CircleShape)
                  .clickable { selectedGradientIdx = idx },
                contentAlignment = Alignment.Center
              ) {
                if (isSelected) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { showAvatarCustomizerDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg)
        ) {
          Text("Save Avatar")
        }
      }
    )
  }

  // DIALOG 2: Delete Account Confirmation
  if (showDeleteAccountConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteAccountConfirmDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
          Text("Permanently Delete Account?", fontWeight = FontWeight.Bold, color = colors.ink)
        }
      },
      text = {
        Text(
          text = "Are you sure you want to permanently delete your account?\n\nThis will irreversibly wipe all your food logs, weight entries, custom calorie blueprint, and preferences. You will be signed out and returned to the initial setup screen.",
          fontSize = 13.sp,
          color = colors.ink2,
          lineHeight = 19.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showDeleteAccountConfirmDialog = false
            onDeleteAccount()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White)
        ) {
          Text("Yes, Delete Everything")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteAccountConfirmDialog = false }) {
          Text("Cancel", color = colors.ink)
        }
      }
    )
  }

  // DIALOG 3: Pricing Paywall Modal (All 5 plans, per day highlight & Instagram 20% discount)
  if (showPricingPaywallDialog) {
    PricingPaywallDialog(
      onDismiss = { showPricingPaywallDialog = false },
      onSubscribeSuccess = { _ -> showPricingPaywallDialog = false }
    )
  }

  // DIALOG 4: Full Comprehensive Privacy Policy Dialog
  if (showFullPrivacyPolicyDialog) {
    AlertDialog(
      onDismissRequest = { showFullPrivacyPolicyDialog = false },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
          Text(text = "CalSnapix Privacy Policy", fontWeight = FontWeight.Bold, color = colors.ink)
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF10B981).copy(alpha = 0.1f),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.25f))
          ) {
            Text(
              text = "Effective Date: October 2026\nApp: CalSnapix • Privacy & Data Security Policy",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF10B981),
              modifier = Modifier.padding(10.dp)
            )
          }

          Text(
            text = "1. Data Protection & Encryption:\nWe never sell your data. Your logs are encrypted. Your health, body weight, and meal habits are personal. CalSnapix never sells, rents, monetizes, or brokers your personal health metrics, food photos, or biometric records to insurance companies, advertising networks, or data aggregators.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "2. Exact Data We Collect & Exactly Why We Collect It (In Detail):\n\n• Biometric Metrics (Age, Gender, Height, Current Weight, Goal Weight):\nWHY WE COLLECT THIS: We use these measurements strictly to calculate your Basal Metabolic Rate (BMR) and Total Daily Energy Expenditure (TDEE) using the Mifflin-St Jeor formula. This enables CalSnapix to compute your personalized daily caloric requirement and macronutrient targets (Protein, Carbs, Fats). Without this data, safe, customized nutrition targets cannot be generated.\n\n• Food Preferences & Dietary Restrictions (Vegetarian, Vegan, Jain, Eggetarian, Allergies):\nWHY WE COLLECT THIS: Used to filter recommended Indian recipes, avoid non-compliant ingredients, and tailor Ayurvedic digestion windows to your religious, ethical, or medical choices.\n\n• Real-Time Food Photos & Camera Frames:\nWHY WE COLLECT THIS: Requested only in real-time when you trigger the camera scanner. Images are analyzed transiently by Gemini AI to detect foods, portion sizes, and calculate macronutrients. Photos are NEVER stored permanently on remote advertising servers, never used for facial recognition, and never sold to third parties.\n\n• Barcode & QR Code Scans:\nWHY WE COLLECT THIS: Only the numerical EAN/UPC barcode or nutritional QR text is queried against nutritional databases to automatically fetch product brand names, calories, and macronutrients.\n\n• User Account Credentials (Name & Email Address):\nWHY WE COLLECT THIS: Required solely for secure account authentication, password recovery, and cross-device sync of your daily meal logs and streak. Your email is never sold, shared, or used for unsolicited marketing.\n\n• Device Hardware & Local Storage:\nWHY WE COLLECT THIS: To store your daily logged meals, water glasses, and streaks locally in a 256-bit AES encrypted Room SQLite database for fast offline availability.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "3. Camera & Hardware Permissions:\n• Camera Access: Used solely when you tap 'Scan Meal' or 'Barcode'. Photos are analyzed in real-time and never used for facial recognition or unrelated scanning.\n• Storage: Your logs are stored locally on your device in a 256-bit encrypted Room SQLite database.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "4. AI & Cloud Processing:\n• Meal scanning and CalSnapix AI Nutrition Assistant consultations utilize Google Gemini Vision models via secure, encrypted endpoints.\n• Uploaded meal snapshots are discarded after inference and are not used for public model training.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "5. Complete Data Ownership & Deletion:\n• You hold full ownership of your data at all times.\n• You can permanently delete your account, calorie blueprint, food diary, and weight logs at any time using the 'Delete Account' option in Profile Settings.\n• Web deletion portal: calsnapix.in/delete-account",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "6. Security Standards:\n• We never sell your data. Your logs are encrypted.\n• 256-bit AES cryptographic storage.\n• Transport Layer Security (TLS 1.3) on all external communications.\n• SHA-256 password salting.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "7. Contact & Privacy Inquiries:\nIf you have any questions regarding your data or privacy rights, please reach out to:\nEmail: privacy@calsnapix.in\nPublic Privacy URL: calsnapix.in/privacy",
            fontSize = 12.sp,
            color = colors.ink2,
            lineHeight = 17.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showFullPrivacyPolicyDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg)
        ) {
          Text("I Understand")
        }
      }
    )
  }

  // DIALOG 5: Full Comprehensive Terms & Conditions Dialog
  if (showFullTermsDialog) {
    AlertDialog(
      onDismissRequest = { showFullTermsDialog = false },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Article, contentDescription = null, tint = colors.accent, modifier = Modifier.size(24.dp))
          Text(text = "CalSnapix Terms & Conditions", fontWeight = FontWeight.Bold, color = colors.ink)
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = colors.accentSoft,
            border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.25f))
          ) {
            Text(
              text = "Last Updated: October 2026\nTerms of Service Agreement for CalSnapix • 100% Free Service • calsnapix.in/terms",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = colors.accent,
              modifier = Modifier.padding(10.dp)
            )
          }

          Text(
            text = "1. Acceptance of Terms:\nBy creating an account, browsing, scanning meals, or using CalSnapix, you agree to comply with and be bound by these Terms & Conditions. If you disagree with any portion, please cease usage of the app immediately.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "2. No-Guarantee Clause (Estimates, Not Promises):\nAll projected calorie goals, weight timelines, and deficit calculations are algorithmic estimates based on general metabolic formulas. They are NOT guarantees or clinical promises. Individual metabolic rates, body composition, sleep, and compliance vary significantly. Weekly weight change rates are capped at 1.0 kg/week max, and underweight targets (BMI < 18.5) are strictly blocked.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "3. Fuller Health & Medical Disclaimer:\n• AI Assistant Is Not a Doctor/Dietitian: CalSnapix is an informational wellness and meal tracking tool. It does NOT provide clinical diagnosis, medical therapy, or prescription diets.\n• Eating Disorders Contraindication: The app is strictly contraindicated for anyone with an active or historical diagnosis of anorexia, bulimia, orthorexia, or disordered eating patterns.\n• Allergies & Anaphylaxis: Photo scanning and food databases cannot detect microscopic allergen traces, peanut cross-contamination, or cooking oils. Users with severe allergies must independently verify all ingredients.\n• Pregnancy & Lactation: Caloric restriction is contraindicated during pregnancy and breastfeeding.\n• Chronic Illness: Users with Diabetes, hypertension, PCOS, or thyroid conditions must consult their treating physician prior to dietary changes.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "4. 100% Free Service Commitment:\nCalSnapix is 100% free for all users. There are zero subscription fees, zero recurring charges, and zero paywalls. Unlimited AI food recognition, barcode & QR scanning, calorie budgeting, and nutritional guidance are completely unlocked without payment.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "5. Security & Breach Response Protocol:\nIn accordance with CERT-In directions under IT Act Section 70B, confirmed cybersecurity incidents are reported within 6 hours, and affected users are notified via email within 72 hours.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "6. Contact & Grievance Officer:\n• Legal Entity: CalSnapix Technologies Pvt. Ltd., Koramangala, Bengaluru, Karnataka, India - 560001\n• Grievance Officer: Yashwant Raj (grievance@calsnapix.in)\n• DPO Email: dpo@calsnapix.in • Legal: legal@calsnapix.in\n• Web Deletion Portal: calsnapix.in/delete-account",
            fontSize = 12.sp,
            color = colors.ink2,
            lineHeight = 17.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showFullTermsDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg)
        ) {
          Text("Accept & Close")
        }
      }
    )
  }

  // DIALOG 6: Incident Response & Data Breach Plan
  if (showBreachPlanDialog) {
    AlertDialog(
      onDismissRequest = { showBreachPlanDialog = false },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = colors.accent, modifier = Modifier.size(24.dp))
          Text(text = "Security Incident Protocol", fontWeight = FontWeight.Bold, color = colors.ink)
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = colors.accentSoft,
            border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.25f))
          ) {
            Text(
              text = "Mandatory Notification Standards (CERT-In, DPDP Act & Play Security)",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = colors.accent,
              modifier = Modifier.padding(10.dp)
            )
          }

          Text(
            text = "1. Response Team & Ownership:\n• Incident Commander: Lead Security Engineer\n• Privacy Officer: Data Protection Officer (DPO)\n• Public Notifications: Communications Lead\n• Dedicated Escalation Inbox: security@calsnapix.in",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "2. CERT-In Mandatory Reporting (Within 6 Hours):\nUnder India's CERT-In Cyber Security Directions (IT Act Section 70B), cybersecurity incidents meeting mandatory reporting thresholds are reported to incident@cert-in.org.in within 6 hours of discovery.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "3. Affected User Notification (Within 72 Hours):\nIf personal data is ever compromised, affected users will be notified via verified registered email without undue delay, and no later than 72 hours, with clear details on affected records, remediation steps, and protective guidance.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )

          Text(
            text = "4. Defense-In-Depth Architecture:\n• Transient Meal Photos: Held only in memory during AI model inference; deleted immediately after analysis.\n• Hardware Keystore: AES-256-GCM encryption key stored exclusively in Android Keystore, never in APK code.\n• Zero-Trust Database RLS: Strict per-user row level security in database.\n• HTTPS Only: Cleartext HTTP traffic is blocked at system manifest level.\n• Cloud Backup Disabled: allowBackup=false prevents data leaking into cloud backups.",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 17.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showBreachPlanDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg)
        ) {
          Text("Understood")
        }
      }
    )
  }
}

@Composable
private fun PreferenceSwitchRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceContainerLow),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = AhaarPrimary,
        modifier = Modifier.size(20.dp)
      )
    }

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp
      )
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = AhaarPrimaryContainer
      )
    )
  }
}
