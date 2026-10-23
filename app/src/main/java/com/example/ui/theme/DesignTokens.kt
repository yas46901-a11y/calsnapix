package com.example.ui.theme

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * CalSnapix Single Design Token System.
 * "Apple Health meets Cal AI" — Minimalist, calm, spacious, zero orange.
 */
@Immutable
data class CalColors(
  val bg: Color,
  val surface: Color,
  val surface2: Color,
  val ink: Color,
  val ink2: Color,
  val line: Color,
  val accent: Color,
  val accentSoft: Color,
  val protein: Color,
  val carbs: Color,
  val fat: Color,
  val success: Color,
  val warn: Color,
  val danger: Color,
  val isDark: Boolean
)

val LightCalColors = CalColors(
  bg = Color(0xFFF7F6F2),          // warm ivory
  surface = Color(0xFFFFFFFF),     // crisp white
  surface2 = Color(0xFFEFEDE7),    // elevated warm ivory
  ink = Color(0xFF0E0F0C),         // primary deep text
  ink2 = Color(0xFF5E6159),        // secondary muted text
  line = Color(0xFFE4E2DA),        // delicate dividing line
  accent = Color(0xFF0F5C47),      // deep emerald
  accentSoft = Color(0xFFDCEBE4),  // soft emerald mist
  protein = Color(0xFF4C5BD4),     // modern indigo
  carbs = Color(0xFFD9A441),       // muted gold
  fat = Color(0xFFD9697F),         // dusty rose
  success = Color(0xFF2E9E6B),     // fresh jade
  warn = Color(0xFFC98A1B),        // amber gold
  danger = Color(0xFFC8433B),      // muted crimson
  isDark = false
)

val DarkCalColors = CalColors(
  bg = Color(0xFF0B0C0B),          // true dark
  surface = Color(0xFF151716),     // elevated surface
  surface2 = Color(0xFF1E211F),    // secondary surface
  ink = Color(0xFFF4F3EE),         // primary light text
  ink2 = Color(0xFFA3A79F),        // secondary muted text
  line = Color(0xFF2A2D2B),        // dark line
  accent = Color(0xFF4ADE9F),      // vibrant mint emerald
  accentSoft = Color(0xFF14302A),  // deep emerald container
  protein = Color(0xFF8C97F2),     // soft indigo
  carbs = Color(0xFFE8C06A),       // soft gold
  fat = Color(0xFFEE90A2),         // soft rose
  success = Color(0xFF4ADE9F),
  warn = Color(0xFFE8C06A),
  danger = Color(0xFFF87171),
  isDark = true
)

val LocalCalColors = staticCompositionLocalOf { LightCalColors }

object CalTheme {
  val colors: CalColors
    @Composable
    get() = LocalCalColors.current

  val cardRadius: Dp = 20.dp
  val pillRadius: Dp = 999.dp
  val cardShape = RoundedCornerShape(20.dp)
  val pillShape = RoundedCornerShape(999.dp)
  val sidePadding: Dp = 20.dp
}

/**
 * Root theme provider that can override dark mode (e.g. for onboarding or user toggle).
 */
@Composable
fun CalDesignThemeProvider(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colors = if (darkTheme) DarkCalColors else LightCalColors
  CompositionLocalProvider(LocalCalColors provides colors) {
    content()
  }
}

/**
 * Primary action button: 56dp pill, full width, --ink background with --bg text.
 * Mimics the clean Cal AI aesthetic with 0.97 press scale and subtle haptic feedback.
 */
@Composable
fun CalPrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  leadingIcon: (@Composable () -> Unit)? = null,
  trailingIcon: (@Composable () -> Unit)? = null
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.97f else 1f,
    animationSpec = tween(durationMillis = 150),
    label = "button_scale"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(56.dp)
      .scale(scale)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled && !isLoading
      ) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        onClick()
      },
    shape = CalTheme.pillShape,
    color = if (enabled) colors.ink else colors.line,
    contentColor = if (enabled) colors.bg else colors.ink2
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(22.dp),
          color = colors.bg,
          strokeWidth = 2.5.dp
        )
      } else {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(8.dp))
          }
          Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) colors.bg else colors.ink2,
            letterSpacing = (-0.2).sp
          )
          if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailingIcon()
          }
        }
      }
    }
  }
}

/**
 * Accent CTA button: used for key milestones / final action of a flow in deep emerald.
 */
@Composable
fun CalAccentButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  trailingIcon: (@Composable () -> Unit)? = null
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.97f else 1f,
    animationSpec = tween(durationMillis = 150),
    label = "accent_button_scale"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(56.dp)
      .scale(scale)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled && !isLoading
      ) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        onClick()
      },
    shape = CalTheme.pillShape,
    color = if (enabled) colors.accent else colors.line,
    contentColor = Color.White
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(22.dp),
          color = Color.White,
          strokeWidth = 2.5.dp
        )
      } else {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = (-0.2).sp
          )
          if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailingIcon()
          }
        }
      }
    }
  }
}

/**
 * Secondary outline button: 56dp pill, 1px --line border, --ink text.
 */
@Composable
fun CalSecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.97f else 1f,
    animationSpec = tween(durationMillis = 150),
    label = "sec_button_scale"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(56.dp)
      .scale(scale)
      .border(1.dp, colors.line, CalTheme.pillShape)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled
      ) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        onClick()
      },
    shape = CalTheme.pillShape,
    color = Color.Transparent,
    contentColor = colors.ink
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = colors.ink,
        letterSpacing = (-0.2).sp
      )
    }
  }
}

/**
 * Standard CalSnapix Card:
 * --surface background, 1px --line border, 20dp corners, soft shadow.
 */
@Composable
fun CalCard(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  backgroundColor: Color = CalTheme.colors.surface,
  borderColor: Color = CalTheme.colors.line,
  content: @Composable ColumnScope.() -> Unit
) {
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
      .shadow(elevation = 1.dp, shape = CalTheme.cardShape, clip = false)
      .border(1.dp, borderColor, CalTheme.cardShape)
      .clip(CalTheme.cardShape)
      .then(clickModifier),
    color = backgroundColor,
    shape = CalTheme.cardShape
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      content = content
    )
  }
}

/**
 * CalSnapix Real Brand Logo:
 * Deep emerald rounded square with a crisp white 'C' embracing an energetic flame,
 * flanked by subtle camera scan corner brackets and sparkles.
 */
@Composable
fun CalLogo(
  modifier: Modifier = Modifier,
  size: Dp = 72.dp,
  showSparkles: Boolean = true
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(RoundedCornerShape(size * 0.28f))
      .background(Color(0xFF0F5C47)),
    contentAlignment = Alignment.Center
  ) {
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
      val w = this.size.width
      val h = this.size.height
      val strokeW = w * 0.06f

      // 1. Subtle camera corner scan brackets
      val bracketLen = w * 0.18f
      val inset = w * 0.14f

      // Top Left Bracket
      val pTL = Path().apply {
        moveTo(inset, inset + bracketLen)
        lineTo(inset, inset)
        lineTo(inset + bracketLen, inset)
      }
      drawPath(pTL, color = Color.White.copy(alpha = 0.5f), style = Stroke(width = strokeW * 0.7f, cap = StrokeCap.Round, join = StrokeJoin.Round))

      // Bottom Right Bracket
      val pBR = Path().apply {
        moveTo(w - inset - bracketLen, h - inset)
        lineTo(w - inset, h - inset)
        lineTo(w - inset, h - inset - bracketLen)
      }
      drawPath(pBR, color = Color.White.copy(alpha = 0.5f), style = Stroke(width = strokeW * 0.7f, cap = StrokeCap.Round, join = StrokeJoin.Round))

      // 2. Center Flame Path
      val flame = Path().apply {
        val cx = w * 0.52f
        val cy = h * 0.52f
        moveTo(cx, cy - h * 0.18f)
        cubicTo(cx + w * 0.08f, cy - h * 0.08f, cx + w * 0.12f, cy + h * 0.06f, cx, cy + h * 0.14f)
        cubicTo(cx - w * 0.12f, cy + h * 0.06f, cx - w * 0.08f, cy - h * 0.08f, cx, cy - h * 0.18f)
        close()
      }
      drawPath(flame, color = Color.White.copy(alpha = 0.95f))

      // 3. Crisp 'C' wrapping around the flame
      val cPath = Path().apply {
        val cx = w * 0.48f
        val cy = h * 0.52f
        val r = w * 0.24f
        arcTo(
          rect = androidx.compose.ui.geometry.Rect(cx - r, cy - r, cx + r, cy + r),
          startAngleDegrees = 45f,
          sweepAngleDegrees = 270f,
          forceMoveTo = false
        )
      }
      drawPath(cPath, color = Color.White, style = Stroke(width = strokeW * 1.3f, cap = StrokeCap.Round))

      // 4. Little accent sparkle on top right
      if (showSparkles) {
        val sx = w * 0.78f
        val sy = h * 0.24f
        val sLen = w * 0.06f
        drawLine(Color.White.copy(alpha = 0.85f), androidx.compose.ui.geometry.Offset(sx, sy - sLen), androidx.compose.ui.geometry.Offset(sx, sy + sLen), strokeWidth = strokeW * 0.6f, cap = StrokeCap.Round)
        drawLine(Color.White.copy(alpha = 0.85f), androidx.compose.ui.geometry.Offset(sx - sLen, sy), androidx.compose.ui.geometry.Offset(sx + sLen, sy), strokeWidth = strokeW * 0.6f, cap = StrokeCap.Round)
      }
    }
  }
}
