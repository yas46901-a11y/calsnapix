package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CalTheme

// Minimalist borders and surface tokens
val GlassBorderTop = Color(0xFFE4E2DA).copy(alpha = 0.8f)
val GlassBorderBottom = Color(0xFFE4E2DA).copy(alpha = 0.3f)

val GlassBorderBrush = Brush.verticalGradient(
  listOf(GlassBorderTop, GlassBorderBottom)
)

val GlassBorderStroke = BorderStroke(1.dp, GlassBorderBrush)

@Composable
fun glassCardColors(containerAlpha: Float = 0.95f): CardColors {
  val colors = CalTheme.colors
  return CardDefaults.cardColors(
    containerColor = colors.surface.copy(alpha = containerAlpha),
    contentColor = colors.ink
  )
}

/**
 * Clean Canvas Container (Apple Health / Cal AI style).
 * Neutral, spacious warm ivory or true dark canvas with a subtle emerald whisper at top.
 */
@Composable
fun GlassBackground(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit
) {
  val colors = CalTheme.colors
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(colors.bg)
  ) {
    // Subtle accent-soft glow at the top for depth
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              colors.accentSoft.copy(alpha = if (colors.isDark) 0.15f else 0.35f),
              Color.Transparent
            ),
            startY = 0f,
            endY = 400f
          )
        )
    )

    content()
  }
}

/**
 * Standard Glassmorphic Surface for Cards
 */
@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 20.dp,
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val colors = CalTheme.colors
  val shape = RoundedCornerShape(cornerRadius)
  val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .shadow(elevation = 1.dp, shape = shape, clip = false)
      .clip(shape)
      .then(clickModifier),
    shape = shape,
    color = colors.surface,
    border = BorderStroke(1.dp, colors.line)
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      content = content
    )
  }
}
