package com.example.ui.components

import android.content.Context
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalTheme

const val PREF_KEY_SEEN_MEAL_SCAN_TOOLTIPS = "has_seen_meal_scan_tooltips"

enum class ScanTooltipStep(
  val stepNumber: Int,
  val badgeText: String,
  val title: String,
  val description: String,
  val icon: ImageVector,
  val actionLabel: String
) {
  LAUNCH_SCANNER(
    stepNumber = 1,
    badgeText = "Step 1 of 3 • Quick Action",
    title = "Tap 'Scan' to Start",
    description = "Open the AI neural camera to scan home-cooked rotis, dals, sabzis, or packaged food in one tap.",
    icon = Icons.Default.PhotoCamera,
    actionLabel = "Next: Camera Guide →"
  ),
  FRAME_ALIGNMENT(
    stepNumber = 2,
    badgeText = "Step 2 of 3 • Framing",
    title = "Align Inside Reticles",
    description = "Position your food plate within the saffron corner brackets. Gemini AI detects dishes in under 2 seconds.",
    icon = Icons.Default.Restaurant,
    actionLabel = "Next: Nutrition →"
  ),
  REVIEW_DIAGNOSTICS(
    stepNumber = 3,
    badgeText = "Step 3 of 3 • Nutrition",
    title = "Instant Macro Reports",
    description = "Review calories, protein, carbs, fats, and Vedic meal window timing, then log directly to your diary.",
    icon = Icons.Default.AutoAwesome,
    actionLabel = "Start Scanning Now 🚀"
  )
}

/**
 * Premium, minimal contextual tooltip overlay for the Home Screen.
 * Guides new users through meal and food scanning without disrupting the clean UI.
 */
@Composable
fun ScanContextualTooltipBanner(
  currentStep: ScanTooltipStep,
  isVisible: Boolean,
  onNext: () -> Unit,
  onPrevious: () -> Unit,
  onDismiss: () -> Unit,
  onActionClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val context = LocalContext.current

  val infiniteTransition = rememberInfiniteTransition(label = "pulse_tooltip")
  val borderAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "tooltip_border_glow"
  )

  AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { -it / 3 },
    exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { -it / 3 },
    modifier = modifier.testTag("scan_contextual_tooltip_container")
  ) {
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
      border = BorderStroke(
        width = 1.5.dp,
        brush = Brush.horizontalGradient(
          listOf(
            Color(0xFFFF9933).copy(alpha = borderAlpha),
            Color(0xFFFFC107).copy(alpha = borderAlpha)
          )
        )
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .shadow(12.dp, RoundedCornerShape(18.dp))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Top Header: Badge, Step Indicator Dots & Dismiss Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(999.dp),
              color = Color(0xFFFF9933).copy(alpha = 0.2f),
              border = BorderStroke(0.8.dp, Color(0xFFFF9933).copy(alpha = 0.6f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
              ) {
                Icon(
                  imageVector = currentStep.icon,
                  contentDescription = null,
                  tint = Color(0xFFFF9933),
                  modifier = Modifier.size(13.dp)
                )
                Text(
                  text = currentStep.badgeText,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFFF9933)
                )
              }
            }

            // Progress Indicator Dots
            Row(
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              ScanTooltipStep.values().forEach { step ->
                val isCurrent = step == currentStep
                Box(
                  modifier = Modifier
                    .size(if (isCurrent) 7.dp else 5.dp)
                    .clip(CircleShape)
                    .background(
                      if (isCurrent) Color(0xFFFFC107) else Color.White.copy(alpha = 0.3f)
                    )
                )
              }
            }
          }

          // Dismiss Button (✕)
          IconButton(
            onClick = {
              view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
              onDismiss()
            },
            modifier = Modifier
              .size(28.dp)
              .testTag("tooltip_dismiss_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Dismiss Tooltip",
              tint = Color.White.copy(alpha = 0.7f),
              modifier = Modifier.size(16.dp)
            )
          }
        }

        // Title & Description
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
          Text(
            text = currentStep.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = currentStep.description,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.8f),
            lineHeight = 18.sp
          )
        }

        // Bottom Controls: Back, Skip, & Action / Next Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            if (currentStep.stepNumber > 1) {
              TextButton(
                onClick = {
                  view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                  onPrevious()
                },
                modifier = Modifier.testTag("tooltip_back_button")
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = null,
                  tint = Color.White.copy(alpha = 0.7f),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "Back",
                  fontSize = 12.sp,
                  color = Color.White.copy(alpha = 0.7f)
                )
              }
            } else {
              TextButton(
                onClick = {
                  view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                  onDismiss()
                },
                modifier = Modifier.testTag("tooltip_skip_button")
              ) {
                Text(
                  text = "Skip Tour",
                  fontSize = 12.sp,
                  color = Color.White.copy(alpha = 0.6f)
                )
              }
            }
          }

          // Primary Step Action Button
          Button(
            onClick = {
              view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
              if (currentStep == ScanTooltipStep.REVIEW_DIAGNOSTICS) {
                onActionClick()
              } else {
                onNext()
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFFF9933),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.testTag("tooltip_next_button")
          ) {
            Text(
              text = currentStep.actionLabel,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }
}

/**
 * Discreet chip on the Home screen that lets users view or restart the Meal Scanning Guide anytime.
 */
@Composable
fun ScanGuideChip(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Surface(
    shape = RoundedCornerShape(999.dp),
    color = colors.surface,
    border = BorderStroke(1.dp, colors.line),
    modifier = modifier
      .clickable {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        onClick()
      }
      .testTag("home_scan_tooltip_help_button")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Restaurant,
        contentDescription = "Meal Scanning Guide",
        tint = Color(0xFFFF9933),
        modifier = Modifier.size(13.dp)
      )
      Text(
        text = "Scan Guide",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = colors.ink
      )
      Box(
        modifier = Modifier
          .size(5.dp)
          .clip(CircleShape)
          .background(Color(0xFFFF9933))
      )
    }
  }
}
