package com.example.ui.components

import android.content.Context
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AhaarUiState
import com.example.data.gamification.AchievementBadge
import com.example.ui.theme.CalPrimaryButton
import com.example.ui.theme.CalSecondaryButton
import com.example.ui.theme.CalTheme
import com.example.util.SocialShareHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Frosted Glass Dialog allowing users to preview and share their daily macro-nutrient
 * progress or badge achievements directly to social apps via the Android Sharesheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareProgressDialog(
  uiState: AhaarUiState,
  initialBadge: AchievementBadge? = null,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val colors = CalTheme.colors
  val isDark = colors.isDark

  var selectedTabIndex by remember {
    mutableIntStateOf(if (initialBadge != null) 1 else 0)
  }

  val badges = uiState.gamificationProfile.badges
  var selectedBadge by remember {
    mutableStateOf(initialBadge ?: badges.firstOrNull { it.isUnlocked } ?: badges.firstOrNull())
  }

  val todayFormatted = remember {
    SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
  }

  BasicAlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.fillMaxWidth().padding(16.dp)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = if (isDark) Color(0xFF161817).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.96f),
      border = BorderStroke(1.dp, glassBorderBrush()),
      modifier = Modifier
        .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x33000000), spotColor = Color(0x22000000))
        .testTag("share_progress_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(colors.accentSoft),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = "Share Your Progress",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "Post to Instagram, WhatsApp & more",
                fontSize = 12.sp,
                color = colors.ink2
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.ink2)
          }
        }

        // Mode Selector: Macros vs Badges
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = colors.surface2,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            val tabs = listOf("Daily Macros", "Badge Achievements")
            tabs.forEachIndexed { index, title ->
              val isSelected = selectedTabIndex == index
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(9.dp))
                  .background(if (isSelected) colors.ink else Color.Transparent)
                  .clickable { selectedTabIndex = index }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = title,
                  fontSize = 13.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) colors.bg else colors.ink2
                )
              }
            }
          }
        }

        // Live Social Preview Card
        if (selectedTabIndex == 0) {
          // Tab 0: Daily Macros Live Preview Card
          MacroSharePreviewCard(
            uiState = uiState,
            todayFormatted = todayFormatted
          )
        } else {
          // Tab 1: Badge Achievement Live Preview Card
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (badges.isNotEmpty()) {
              Text(
                text = "SELECT BADGE TO SHARE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink2,
                letterSpacing = 0.8.sp
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                badges.take(4).forEach { badge ->
                  val isSelected = selectedBadge?.id == badge.id
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) colors.accentSoft else colors.surface2,
                    border = BorderStroke(
                      1.dp,
                      if (isSelected) colors.accent else colors.line
                    ),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { selectedBadge = badge }
                  ) {
                    Column(
                      modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      Text(text = badge.iconEmoji, fontSize = 20.sp)
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = badge.title.split(" ").firstOrNull() ?: "",
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = colors.ink,
                        maxLines = 1
                      )
                    }
                  }
                }
              }
            }

            selectedBadge?.let { badge ->
              BadgeSharePreviewCard(
                badge = badge,
                profile = uiState.gamificationProfile
              )
            }
          }
        }

        // Action Buttons
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          CalPrimaryButton(
            text = "Share via Android Sharesheet",
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = colors.bg,
                modifier = Modifier.size(18.dp)
              )
            },
            onClick = {
              if (selectedTabIndex == 0) {
                val shareText = SocialShareHelper.buildDailyProgressShareText(uiState)
                SocialShareHelper.launchSharesheet(context, shareText, "Share Daily Progress")
              } else {
                selectedBadge?.let { badge ->
                  val shareText = SocialShareHelper.buildBadgeAchievementShareText(
                    badge = badge,
                    profile = uiState.gamificationProfile,
                    userName = uiState.userName
                  )
                  SocialShareHelper.launchSharesheet(context, shareText, "Share Achievement")
                }
              }
              onDismiss()
            },
            modifier = Modifier.testTag("confirm_share_button")
          )

          CalSecondaryButton(
            text = "Copy Text to Clipboard",
            onClick = {
              if (selectedTabIndex == 0) {
                val shareText = SocialShareHelper.buildDailyProgressShareText(uiState)
                SocialShareHelper.copyToClipboard(context, shareText)
              } else {
                selectedBadge?.let { badge ->
                  val shareText = SocialShareHelper.buildBadgeAchievementShareText(
                    badge = badge,
                    profile = uiState.gamificationProfile,
                    userName = uiState.userName
                  )
                  SocialShareHelper.copyToClipboard(context, shareText)
                }
              }
            },
            modifier = Modifier.testTag("copy_share_text_button")
          )
        }
      }
    }
  }
}

@Composable
private fun MacroSharePreviewCard(
  uiState: AhaarUiState,
  todayFormatted: String
) {
  val colors = CalTheme.colors
  val isDark = colors.isDark

  Surface(
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, glassBorderBrush()),
    modifier = Modifier
      .fillMaxWidth()
      .shadow(4.dp, RoundedCornerShape(18.dp)),
    color = if (isDark) Color(0xFF1E211F) else Color(0xFFF9F8F5)
  ) {
    Column(
      modifier = Modifier
        .background(
          Brush.linearGradient(
            listOf(
              colors.accent.copy(alpha = if (isDark) 0.18f else 0.12f),
              Color.Transparent
            )
          )
        )
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "CALSNAPIX NUTRITION",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent,
            letterSpacing = 1.sp
          )
          Text(
            text = todayFormatted,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = colors.ink2
          )
        }
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = colors.accent,
          contentColor = Color.White
        ) {
          Text(
            text = "${uiState.currentCaloriesConsumed} kcal",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }

      // Macro Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        PreviewMacroPill(
          label = "Protein",
          value = "${uiState.proteinConsumedGrams}g",
          target = "${uiState.proteinTargetGrams}g",
          color = colors.protein,
          modifier = Modifier.weight(1f)
        )
        PreviewMacroPill(
          label = "Carbs",
          value = "${uiState.carbsConsumedGrams}g",
          target = "${uiState.carbsTargetGrams}g",
          color = colors.carbs,
          modifier = Modifier.weight(1f)
        )
        PreviewMacroPill(
          label = "Fats",
          value = "${uiState.fatsConsumedGrams}g",
          target = "${uiState.fatsTargetGrams}g",
          color = colors.fat,
          modifier = Modifier.weight(1f)
        )
      }

      // Hydration & Steps summary bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
          Text(
            text = "${uiState.waterGlassesLogged}/${uiState.totalWaterGlassesTarget} Glasses",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink
          )
          if (uiState.gamificationProfile.currentWaterStreakDays > 0) {
            Text(
              text = "• ${uiState.gamificationProfile.currentWaterStreakDays}d streak",
              fontSize = 11.sp,
              color = Color(0xFF0284C7)
            )
          }
        }
        if (uiState.currentSteps > 0) {
          Text(
            text = "👟 ${uiState.currentSteps} steps",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink2
          )
        }
      }
    }
  }
}

@Composable
private fun BadgeSharePreviewCard(
  badge: AchievementBadge,
  profile: com.example.data.gamification.GamificationProfile
) {
  val colors = CalTheme.colors
  val isDark = colors.isDark

  Surface(
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, glassBorderBrush()),
    modifier = Modifier
      .fillMaxWidth()
      .shadow(4.dp, RoundedCornerShape(18.dp)),
    color = if (isDark) Color(0xFF1E211F) else Color(0xFFF9F8F5)
  ) {
    Column(
      modifier = Modifier
        .background(
          Brush.linearGradient(
            listOf(
              Color(badge.tier.colorHex).copy(alpha = if (isDark) 0.22f else 0.15f),
              Color.Transparent
            )
          )
        )
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(Color(badge.tier.colorHex).copy(alpha = 0.2f))
          .border(2.dp, Color(badge.tier.colorHex), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(text = badge.iconEmoji, fontSize = 28.sp)
      }

      Text(
        text = badge.title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink,
        textAlign = TextAlign.Center
      )

      Text(
        text = badge.description,
        fontSize = 12.sp,
        color = colors.ink2,
        textAlign = TextAlign.Center
      )

      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = Color(badge.tier.colorHex),
          contentColor = Color.White
        ) {
          Text(
            text = "${badge.tier.label} Tier",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
        Text(
          text = "+${badge.xpAwarded} XP",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = colors.accent
        )
        Text(
          text = "Level ${profile.userLevel} ${profile.levelTitle}",
          fontSize = 11.sp,
          color = colors.ink2
        )
      }
    }
  }
}

@Composable
private fun PreviewMacroPill(
  label: String,
  value: String,
  target: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = color.copy(alpha = 0.12f),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = color)
      Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.ink)
      Text(text = "of $target", fontSize = 9.sp, color = colors.ink2)
    }
  }
}
