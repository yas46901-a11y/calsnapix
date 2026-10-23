package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.CalTheme

/**
 * Minimalist Top Bar for Onboarding steps.
 * WCAG AA compliant text colors (--ink and --ink-2), clean back arrow, skip button.
 */
@Composable
fun AhaarOnboardingTopBar(
  stepNumber: Int,
  totalSteps: Int = 4,
  canGoBack: Boolean,
  onBackClick: () -> Unit,
  onSkipClick: () -> Unit,
  onProfileClick: () -> Unit = {},
  logoUrl: String = "",
  avatarUrl: String = "",
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = colors.bg,
    border = BorderStroke(1.dp, colors.line.copy(alpha = 0.5f))
  ) {
    Row(
      modifier = Modifier
        .statusBarsPadding()
        .fillMaxWidth()
        .height(56.dp)
        .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      if (canGoBack) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.size(40.dp).testTag("onboarding_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = colors.ink,
            modifier = Modifier.size(20.dp)
          )
        }
      } else {
        Spacer(modifier = Modifier.size(40.dp))
      }

      // Step indicator pill
      Surface(
        shape = CalTheme.pillShape,
        color = colors.surface2,
        modifier = Modifier.testTag("onboarding_step_indicator")
      ) {
        Text(
          text = "Step $stepNumber of $totalSteps",
          style = androidx.compose.ui.text.TextStyle(
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink
          ),
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
      }

      TextButton(
        onClick = onSkipClick,
        modifier = Modifier.testTag("onboarding_skip_button")
      ) {
        Text(
          text = "Skip",
          color = colors.ink2,
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

/**
 * Modern Clean Main Top Bar.
 * High-contrast title and avatar, no duplicate labels or clutter.
 */
@Composable
fun AhaarMainTopBar(
  title: String,
  logoUrl: String,
  avatarUrl: String,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = colors.bg,
    border = BorderStroke(1.dp, colors.line.copy(alpha = 0.5f))
  ) {
    Row(
      modifier = Modifier
        .statusBarsPadding()
        .fillMaxWidth()
        .height(56.dp)
        .padding(horizontal = 20.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink,
        letterSpacing = (-0.4).sp
      )

      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .clickable(onClick = onProfileClick)
          .testTag("top_bar_profile_avatar"),
        contentAlignment = Alignment.Center
      ) {
        if (avatarUrl.isNotBlank()) {
          AsyncImage(
            model = avatarUrl,
            contentDescription = "Profile",
            modifier = Modifier.fillMaxSize().clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        } else {
          Surface(
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            color = colors.surface2
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = colors.ink,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }
  }
}
