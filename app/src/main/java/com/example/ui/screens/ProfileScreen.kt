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
  val scrollState = rememberScrollState()

  var circadianReminders by remember { mutableStateOf(true) }
  var waterReminders by remember { mutableStateOf(true) }
  var vedicFastingMode by remember { mutableStateOf(false) }

  var selectedAvatarEmoji by remember { mutableStateOf("🧘") }
  var selectedGradientIdx by remember { mutableStateOf(0) }
  var showAvatarCustomizerDialog by remember { mutableStateOf(false) }
  var showDeleteAccountConfirmDialog by remember { mutableStateOf(false) }
  var showPricingPaywallDialog by remember { mutableStateOf(false) }

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

    // CalSnapix Pro Upgrade Banner (Opens 5 plans pricing paywall)
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.5.dp, colors.accent),
      modifier = Modifier
        .fillMaxWidth()
        .clickable { showPricingPaywallDialog = true }
        .testTag("profile_pricing_plans_banner")
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.weight(1f)
        ) {
          Surface(
            shape = CircleShape,
            color = colors.accent,
            modifier = Modifier.size(40.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text("👑", fontSize = 18.sp)
            }
          }
          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text("CalSnapix Pro Plans", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.ink)
              Surface(shape = RoundedCornerShape(4.dp), color = colors.accentSoft) {
                Text("FROM $0.05/DAY", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = colors.accent, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
              }
            }
            Text("5 Plans (Daily, Weekly, Monthly, 6-Mo & Yearly) • Extra 20% Insta Discount", fontSize = 11.sp, color = colors.ink2)
          }
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View Plans", tint = colors.accent, modifier = Modifier.size(20.dp))
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

    // MyCal AI Dietitian Banner
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth().testTag("mycal_ai_profile_banner")
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Surface(
          shape = CircleShape,
          color = colors.accent.copy(alpha = 0.15f),
          modifier = Modifier.size(44.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "MyCal AI",
              tint = colors.accent,
              modifier = Modifier.size(24.dp)
            )
          }
        }
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("MyCal AI Nutrition Engine", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.ink)
            Surface(shape = RoundedCornerShape(4.dp), color = colors.accent) {
              Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = colors.bg, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
            }
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text("Multimodal Indian food recognition & Ayurvedic calorie balancing active.", fontSize = 11.sp, color = colors.ink2)
        }
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
          title = "Circadian Agni Reminders",
          subtitle = "Alerts for optimal meal digestion windows (8:30 AM & 1:30 PM)",
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

    // DEDICATED SETTINGS SECTION: LINK INSTAGRAM & AUTO-APPLY 20% DISCOUNT
    val instagramProfileUrl = "https://www.instagram.com/_calsnapix?vrfl=MTZkajNnaTlreTR2dA=="
    var instaHandleInput by remember(uiState.linkedInstagramHandle) {
      mutableStateOf(uiState.linkedInstagramHandle?.removePrefix("@") ?: "")
    }

    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(
        1.5.dp,
        if (uiState.isInstagramFollowVerified) colors.accent else Color(0xFFE1306C).copy(alpha = 0.5f)
      ),
      modifier = Modifier.fillMaxWidth().testTag("profile_link_instagram_section")
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
              color = Color(0xFFE1306C),
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text("📸", fontSize = 18.sp)
              }
            }
            Column {
              Text(
                text = "Link Instagram Account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "Follow @_calsnapix for an automatic 20% discount",
                fontSize = 11.sp,
                color = colors.ink2
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(999.dp),
            color = if (uiState.isInstagramFollowVerified) colors.accent else Color(0xFFE1306C).copy(alpha = 0.15f)
          ) {
            Text(
              text = if (uiState.isInstagramFollowVerified) "✓ 20% ACTIVE" else "20% OFF",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (uiState.isInstagramFollowVerified) Color.White else Color(0xFFE1306C),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        if (uiState.isInstagramFollowVerified) {
          // Already Linked & Verified Status Card
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.accentSoft,
            border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.35f))
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = colors.accent, modifier = Modifier.size(18.dp))
                Text(
                  text = "Linked with ${uiState.linkedInstagramHandle} • Following @_calsnapix",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.ink
                )
              }
              Text(
                text = "🎉 20% Discount Code INSTA20 is automatically applied to your subscription across all tiers (Daily, Weekly, Monthly, 6-Month, and Yearly).",
                fontSize = 12.sp,
                color = colors.ink2,
                lineHeight = 17.sp
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = onNavigateToPricing,
                  colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.weight(1f).height(42.dp).testTag("view_discounted_pricing_button")
                ) {
                  Text("View 20% OFF Plans", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                  onClick = onUnlinkInstagram,
                  shape = RoundedCornerShape(10.dp),
                  border = BorderStroke(1.dp, colors.line),
                  modifier = Modifier.height(42.dp).testTag("unlink_instagram_button")
                ) {
                  Text("Unlink", fontSize = 12.sp, color = colors.ink2)
                }
              }
            }
          }
        } else {
          // Input & Follow Verification Flow
          Text(
            text = "Step 1: Follow @_calsnapix on Instagram\nStep 2: Enter your handle to verify follow status and auto-apply code INSTA20 (20% off all plans).",
            fontSize = 12.sp,
            color = colors.ink2,
            lineHeight = 18.sp
          )

          OutlinedTextField(
            value = instaHandleInput,
            onValueChange = { instaHandleInput = it },
            label = { Text("Your Instagram Username") },
            placeholder = { Text("e.g. priya_fit or @priya_fit") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("profile_instagram_handle_input")
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                try {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramProfileUrl))
                  context.startActivity(intent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Opening Instagram @_calsnapix", Toast.LENGTH_SHORT).show()
                }
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)),
              modifier = Modifier.weight(1f).height(46.dp).testTag("profile_follow_insta_btn")
            ) {
              Text("Follow @_calsnapix", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Button(
              onClick = {
                val clean = instaHandleInput.trim().removePrefix("@")
                if (clean.isBlank()) {
                  Toast.makeText(context, "Please enter your Instagram handle", Toast.LENGTH_SHORT).show()
                } else {
                  onLinkInstagram("@$clean")
                  Toast.makeText(context, "🎉 Verified following @_calsnapix! 20% discount code INSTA20 applied.", Toast.LENGTH_LONG).show()
                }
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
              modifier = Modifier.weight(1.2f).height(46.dp).testTag("profile_verify_insta_btn")
            ) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Verify & Apply 20%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // DEDICATED SUBSCRIPTION & PRICING ACCESS CARD
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth().testTag("profile_subscription_overview_card")
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
                  imageVector = Icons.Default.ElectricBolt,
                  contentDescription = null,
                  tint = colors.accent,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Column {
              Text(
                text = "Subscription & Plans",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "Current plan: ${if (uiState.activeSubscriptionPlan == "Free") "Free Version (3 scans/day)" else "CalSnapix Pro (${uiState.activeSubscriptionPlan.replaceFirstChar { it.uppercase() }})"}",
                fontSize = 12.sp,
                color = colors.ink2
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(999.dp),
            color = if (uiState.activeSubscriptionPlan != "Free") colors.accent else colors.surface2
          ) {
            Text(
              text = if (uiState.activeSubscriptionPlan != "Free") "PRO ACTIVE" else "FREE TIER",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (uiState.activeSubscriptionPlan != "Free") Color.White else colors.ink,
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
              Text("5 TIERS AVAILABLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.ink2)
              Text(
                text = if (uiState.isInstagramFollowVerified) "From $0.07 / day (20% OFF)" else "From $0.09 / day",
                fontSize = 16.sp,
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
              Text("View 5 Plans", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
            }
          }
        }
      }
    }

    // Invite Friends & Earn 20% OFF Card
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val referralCode = remember(uiState.userName) {
      val base = uiState.userName.filter { it.isLetter() }.take(4).uppercase()
      if (base.length >= 2) "${base}20" else "YASH20"
    }

    Surface(
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth().testTag("invite_friends_card")
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = colors.accentSoft,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.CardGiftcard,
                  contentDescription = null,
                  tint = colors.accent,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Column {
              Text(
                text = "Invite Friends & Get 20% OFF",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "Share with up to 3 friends: they get 20% OFF & you get 20% OFF renewal!",
                fontSize = 11.sp,
                color = colors.ink2
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(999.dp),
            color = colors.accentSoft
          ) {
            Text(
              text = "20% OFF",
              fontSize = 11.sp,
              color = colors.accent,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = colors.bg,
          border = BorderStroke(1.dp, colors.line)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Your Referral Code",
                fontSize = 10.sp,
                color = colors.ink2
              )
              Text(
                text = referralCode,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = colors.accent
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              IconButton(
                onClick = {
                  clipboardManager.setText(AnnotatedString(referralCode))
                  Toast.makeText(context, "Code copied: $referralCode (20% OFF)", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("copy_referral_code_button")
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "Copy Code",
                  tint = colors.accent,
                  modifier = Modifier.size(20.dp)
                )
              }

              Button(
                onClick = {
                  val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                      Intent.EXTRA_TEXT,
                      "Hey! Join me on CalSnapix for effortless calorie & macro tracking. Use my referral code $referralCode to unlock 20% OFF your plan: https://calsnapix.in"
                    )
                    type = "text/plain"
                  }
                  context.startActivity(Intent.createChooser(sendIntent, "Share CalSnapix Referral"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("share_referral_code_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Share,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share", fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
            }
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
      onSubscribeSuccess = { _ -> showPricingPaywallDialog = false },
      initialInstagramFollowed = uiState.isInstagramFollowVerified
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
