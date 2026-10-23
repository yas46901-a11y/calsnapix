package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalLogo
import kotlinx.coroutines.delay

/**
 * CalSnapix Splash Screen.
 * Dark background (#0B0C0B), real brand logo (emerald square + white C with flame, brackets, sparkles)
 * scaling and fading in smoothly, wordmark "CalSnapix" and tagline "Snap. Know. Eat smart." below.
 * Lasts 1.2s, then gracefully navigates to destination.
 */
@Composable
fun SplashScreen(
  onFinish: () -> Unit
) {
  val scaleAnim = remember { Animatable(0.7f) }
  val alphaAnim = remember { Animatable(0f) }
  val textAlphaAnim = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    // Logo scales and fades in
    alphaAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    )
    scaleAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    )
    textAlphaAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )
    delay(200) // total ~1200ms
    onFinish()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0B0C0B)), // Dark background
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .scale(scaleAnim.value)
          .alpha(alphaAnim.value)
      ) {
        CalLogo(
          size = 96.dp,
          showSparkles = true
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(textAlphaAnim.value)
      ) {
        Text(
          text = "CalSnapix",
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFF4F3EE),
          letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Snap. Know. Eat smart.",
          fontSize = 15.sp,
          fontWeight = FontWeight.Normal,
          color = Color(0xFFA3A79F),
          letterSpacing = 0.2.sp
        )
      }
    }
  }
}
