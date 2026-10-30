package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CalTheme

// Backward-compatible tokens used across Auth, MealScan, Profile, and Main screens
val GlassBorderTop = Color(0xFFE4E2DA).copy(alpha = 0.8f)
val GlassBorderBottom = Color(0xFFE4E2DA).copy(alpha = 0.3f)
val GlassBorderBrush = Brush.verticalGradient(listOf(GlassBorderTop, GlassBorderBottom))
val GlassBorderStroke = BorderStroke(1.dp, GlassBorderBrush)

/**
 * Root frosted glass background container that provides the theme background
 * and ambient floating glowing orbs for all screens.
 */
@Composable
fun GlassBackground(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit
) {
  val colors = CalTheme.colors
  Box(modifier = modifier.fillMaxSize().background(colors.bg)) {
    GlassBackgroundOrbs()
    content()
  }
}

/**
 * Specular highlight border brush creating the signature glassmorphic edge reflection.
 */
@Composable
fun glassBorderBrush(): Brush {
  val isDark = CalTheme.colors.isDark
  return if (isDark) {
    Brush.verticalGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.28f),
        Color.White.copy(alpha = 0.08f),
        Color(0xFF2A2D2B).copy(alpha = 0.40f)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.95f),
        Color.White.copy(alpha = 0.35f),
        Color(0xFFE4E2DA).copy(alpha = 0.50f)
      )
    )
  }
}

/**
 * Translucent frosted glass fill brush with subtle depth.
 */
@Composable
fun glassSurfaceBrush(alpha: Float = 0.82f): Brush {
  val colors = CalTheme.colors
  return if (colors.isDark) {
    Brush.verticalGradient(
      colors = listOf(
        Color(0xFF1E211F).copy(alpha = alpha),
        Color(0xFF141615).copy(alpha = (alpha * 0.9f).coerceIn(0f, 1f))
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Color.White.copy(alpha = alpha),
        Color(0xFFF9F8F6).copy(alpha = (alpha * 0.92f).coerceIn(0f, 1f))
      )
    )
  }
}

/**
 * Ambient backdrop with floating, diffused radiant orbs (emerald, azure, and amber gold)
 * that illuminate through frosted glassmorphic cards.
 */
@Composable
fun GlassBackgroundOrbs(
  modifier: Modifier = Modifier
) {
  val isDark = CalTheme.colors.isDark

  Box(
    modifier = modifier.fillMaxSize()
  ) {
    // 1. Top-Right Radiant Emerald Orb
    Box(
      modifier = Modifier
        .size(340.dp)
        .offset(x = 140.dp, y = (-70).dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(
              Color(0xFF10B981).copy(alpha = if (isDark) 0.18f else 0.15f),
              Color(0xFF34D399).copy(alpha = if (isDark) 0.08f else 0.06f),
              Color.Transparent
            )
          )
        )
    )

    // 2. Mid-Left Radiant Cyan/Azure Orb
    Box(
      modifier = Modifier
        .size(380.dp)
        .offset(x = (-160).dp, y = 300.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(
              Color(0xFF0284C7).copy(alpha = if (isDark) 0.15f else 0.12f),
              Color(0xFF38BDF8).copy(alpha = if (isDark) 0.06f else 0.04f),
              Color.Transparent
            )
          )
        )
    )

    // 3. Lower-Right Warm Amber/Gold Orb
    Box(
      modifier = Modifier
        .size(320.dp)
        .offset(x = 120.dp, y = 720.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(
              Color(0xFFF59E0B).copy(alpha = if (isDark) 0.12f else 0.09f),
              Color(0xFFFBBF24).copy(alpha = if (isDark) 0.05f else 0.03f),
              Color.Transparent
            )
          )
        )
    )
  }
}

/**
 * Glassmorphic Card Surface:
 * Semi-transparent frosted glass, specular highlight gradient border, soft shadow,
 * and high-contrast readable typography.
 */
@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 20.dp,
  elevation: Dp = 4.dp,
  alpha: Float = 0.82f,
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val shape = RoundedCornerShape(cornerRadius)
  val borderBrush = glassBorderBrush()
  val surfaceBrush = glassSurfaceBrush(alpha)

  val clickModifier = if (onClick != null) {
    val view = LocalView.current
    Modifier.clickable {
      view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
      onClick()
    }
  } else Modifier

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .shadow(
        elevation = elevation,
        shape = shape,
        clip = false,
        ambientColor = Color(0x18000000),
        spotColor = Color(0x14000000)
      )
      .border(BorderStroke(1.dp, borderBrush), shape)
      .clip(shape)
      .then(clickModifier),
    color = Color.Transparent,
    shape = shape
  ) {
    Box(
      modifier = Modifier
        .background(surfaceBrush)
        .padding(18.dp)
    ) {
      Column(content = content)
    }
  }
}

/**
 * Frosted Glass Pill Tag for badges, chips, and small controls.
 */
@Composable
fun GlassPill(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  val shape = RoundedCornerShape(999.dp)
  val borderBrush = glassBorderBrush()
  val surfaceBrush = glassSurfaceBrush(0.85f)

  val clickModifier = if (onClick != null) {
    val view = LocalView.current
    Modifier.clickable {
      view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
      onClick()
    }
  } else Modifier

  Surface(
    modifier = modifier
      .clip(shape)
      .border(BorderStroke(1.dp, borderBrush), shape)
      .then(clickModifier),
    color = Color.Transparent,
    shape = shape
  ) {
    Box(
      modifier = Modifier
        .background(surfaceBrush)
        .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
      content()
    }
  }
}
