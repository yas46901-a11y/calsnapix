package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.gamification.AchievementBadge
import com.example.data.gamification.GamificationProfile
import com.example.ui.theme.CalCard
import com.example.ui.theme.CalTheme
import kotlin.math.roundToInt

/**
 * Modern Gamification & Badges Card displaying hydration streaks,
 * weekly macro consistency, XP levels, and unlockable achievement badges.
 */
@Composable
fun GamificationBadgesCard(
  profile: GamificationProfile,
  onSimulateStreak: (Int) -> Unit = {},
  onSimulateWeeklyMacro: (Int) -> Unit = {},
  onShareBadge: ((AchievementBadge) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  var selectedBadge by remember { mutableStateOf<AchievementBadge?>(null) }

  CalCard(
    modifier = modifier.testTag("gamification_badges_card"),
    backgroundColor = colors.surface
  ) {
    // 1. Level & XP Progress Header
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
            .size(34.dp)
            .clip(CircleShape)
            .background(Color(0xFFFEF3C7)), // Warm Amber
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = Color(0xFFD97706),
            modifier = Modifier.size(18.dp)
          )
        }
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "LEVEL ${profile.userLevel} • ${profile.levelTitle.uppercase()}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp,
              color = colors.ink2
            )
          }
          Text(
            text = "${profile.totalXp} XP (${profile.unlockedCount} / ${profile.badges.size} Badges Unlocked)",
            fontSize = 11.sp,
            color = colors.ink2
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(999.dp),
        color = colors.accentSoft
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.Star, contentDescription = null, tint = colors.accent, modifier = Modifier.size(12.dp))
          Text(
            text = "${profile.totalXp} XP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // XP Progress Bar
    LinearProgressIndicator(
      progress = { profile.levelProgressFraction },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = colors.accent,
      trackColor = colors.surface2,
      strokeCap = StrokeCap.Round
    )

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Streaks Row: Hydration Streak & Weekly Macro Goals Met
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Hydration Streak Pill
      Surface(
        modifier = Modifier
          .weight(1f)
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onSimulateStreak(profile.currentWaterStreakDays + 1)
          },
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFE0F2FE), // Sky-100
        border = BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.2f))
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Color(0xFF0284C7)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
          }
          Column {
            Text(
              text = "${profile.currentWaterStreakDays}-Day Streak",
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFF0369A1)
            )
            Text(
              text = "Best: ${profile.bestWaterStreakDays}d (Tap +1)",
              fontSize = 10.sp,
              color = Color(0xFF0284C7)
            )
          }
        }
      }

      // Weekly Macro Streak Pill
      Surface(
        modifier = Modifier
          .weight(1f)
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onSimulateWeeklyMacro((profile.macroGoalsHitThisWeek + 1).coerceAtMost(7))
          },
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFEF3C7), // Amber-100
        border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.2f))
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Color(0xFFD97706)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
          }
          Column {
            Text(
              text = "${profile.macroGoalsHitThisWeek}/${profile.weeklyMacroGoalTarget} Macro Days",
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFFB45309)
            )
            Text(
              text = "Weekly Goal (Tap +1)",
              fontSize = 10.sp,
              color = Color(0xFFD97706)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 3. Horizontal Scrollable Badges Carousel
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "ACHIEVEMENT BADGES",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp,
        color = colors.ink2
      )
      Text(
        text = "Tap to view details",
        fontSize = 10.sp,
        color = colors.ink2
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      profile.badges.forEach { badge ->
        BadgeItemTile(
          badge = badge,
          onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            selectedBadge = badge
          }
        )
      }
    }
  }

  // Badge Details Celebration Modal
  selectedBadge?.let { badge ->
    AlertDialog(
      onDismissRequest = { selectedBadge = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = badge.iconEmoji, fontSize = 28.sp)
          Column {
            Text(text = badge.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.ink)
            Text(text = "${badge.tier.label} Tier • ${badge.category}", fontSize = 11.sp, color = Color(badge.tier.colorHex))
          }
        }
      },
      text = {
        Column {
          Text(
            text = badge.description,
            fontSize = 13.sp,
            color = colors.ink2,
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.surface2,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = if (badge.isUnlocked) "Status: Unlocked 🎉" else "Status: In Progress",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (badge.isUnlocked) colors.accent else colors.ink
                )
                Text(
                  text = "+${badge.xpAwarded} XP",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFD97706)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              LinearProgressIndicator(
                progress = { badge.progressFraction },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = if (badge.isUnlocked) colors.accent else Color(badge.tier.colorHex),
                trackColor = colors.surface
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "${badge.progressCurrent} / ${badge.progressMax} completed",
                fontSize = 10.sp,
                color = colors.ink2
              )
            }
          }
        }
      },
      confirmButton = {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (onShareBadge != null) {
            TextButton(
              onClick = {
                val b = selectedBadge
                selectedBadge = null
                if (b != null) onShareBadge(b)
              }
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Share via Sharesheet", fontWeight = FontWeight.Bold)
            }
          }
          TextButton(onClick = { selectedBadge = null }) {
            Text("Done", color = colors.accent, fontWeight = FontWeight.Bold)
          }
        }
      }
    )
  }
}

@Composable
private fun BadgeItemTile(
  badge: AchievementBadge,
  onClick: () -> Unit
) {
  val colors = CalTheme.colors
  val tierColor = Color(badge.tier.colorHex)

  Surface(
    modifier = Modifier
      .width(105.dp)
      .clickable { onClick() }
      .testTag("badge_item_${badge.id}"),
    shape = RoundedCornerShape(12.dp),
    color = if (badge.isUnlocked) colors.surface2 else colors.surface2.copy(alpha = 0.5f),
    border = BorderStroke(1.dp, if (badge.isUnlocked) tierColor else colors.line)
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(if (badge.isUnlocked) tierColor.copy(alpha = 0.15f) else colors.surface),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = badge.iconEmoji,
          fontSize = 20.sp
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = badge.title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 1,
        color = if (badge.isUnlocked) colors.ink else colors.ink2
      )

      Text(
        text = if (badge.isUnlocked) "Unlocked" else "${badge.progressCurrent}/${badge.progressMax}",
        fontSize = 9.sp,
        fontWeight = if (badge.isUnlocked) FontWeight.Bold else FontWeight.Normal,
        color = if (badge.isUnlocked) colors.accent else colors.ink2
      )
    }
  }
}
