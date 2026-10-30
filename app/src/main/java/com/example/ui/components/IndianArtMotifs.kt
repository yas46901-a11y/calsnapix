package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalTheme

/**
 * Standard shape for cards in minimal luxury automotive style.
 */
val JharokhaArchShape = RoundedCornerShape(14.dp)

/**
 * Clean minimal motif - neutral circle outline.
 */
@Composable
fun IndianMandalaMotif(
  modifier: Modifier = Modifier,
  size: Dp = 120.dp,
  petals: Int = 12,
  animated: Boolean = false,
  alphaMultiplier: Float = 0.4f
) {
  val colors = CalTheme.colors
  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      drawCircle(
        color = colors.line.copy(alpha = 0.4f * alphaMultiplier),
        radius = this.size.minDimension / 2.2f,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
      )
    }
  }
}

/**
 * Clean 1px divider for section separation.
 */
@Composable
fun IndianRangoliDivider(
  modifier: Modifier = Modifier,
  badgeText: String? = null
) {
  val colors = CalTheme.colors

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .weight(1f)
        .height(1.dp)
        .background(colors.line)
    )

    if (badgeText != null) {
      Surface(
        shape = RoundedCornerShape(999.dp),
        color = colors.surface2,
        border = BorderStroke(1.dp, colors.line),
        modifier = Modifier.padding(horizontal = 8.dp)
      ) {
        Text(
          text = badgeText,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = colors.ink2,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
      }
    }

    Box(
      modifier = Modifier
        .weight(1f)
        .height(1.dp)
        .background(colors.line)
    )
  }
}

/**
 * Clean minimal horizontal divider.
 */
@Composable
fun IndianToranBorder(
  modifier: Modifier = Modifier,
  scallops: Int = 10,
  height: Dp = 1.dp
) {
  val colors = CalTheme.colors
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(1.dp)
      .background(colors.line.copy(alpha = 0.5f))
  )
}

/**
 * Clean minimal greeting badge.
 */
@Composable
fun IndianGreetingBadge(
  title: String = "Welcome Back",
  subtitle: String? = null,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors

  Surface(
    shape = RoundedCornerShape(999.dp),
    color = colors.surface2,
    border = BorderStroke(1.dp, colors.line),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(colors.accent)
      )
      Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = colors.ink
      )
      if (subtitle != null) {
        Text(
          text = "• $subtitle",
          fontSize = 11.sp,
          color = colors.ink2
        )
      }
    }
  }
}

/**
 * Clean status indicator dot.
 */
@Composable
fun IndianDiyaLamp(
  modifier: Modifier = Modifier,
  size: Dp = 16.dp
) {
  val colors = CalTheme.colors
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(colors.accent.copy(alpha = 0.15f)),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(colors.accent)
    )
  }
}
