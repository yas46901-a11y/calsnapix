package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = CalAccentLight,
  onPrimary = Color.White,
  primaryContainer = CalInkLight,
  onPrimaryContainer = CalBgLight,
  secondary = CalInk2Light,
  onSecondary = Color.White,
  secondaryContainer = CalSurface2Light,
  onSecondaryContainer = CalInkLight,
  tertiary = CalProteinLight,
  onTertiary = Color.White,
  tertiaryContainer = CalAccentSoftLight,
  onTertiaryContainer = CalAccentLight,
  background = CalBgLight,
  onBackground = CalInkLight,
  surface = CalSurfaceLight,
  onSurface = CalInkLight,
  surfaceVariant = CalSurface2Light,
  onSurfaceVariant = CalInk2Light,
  surfaceContainerLowest = Color.White,
  surfaceContainerLow = CalSurface2Light,
  surfaceContainer = CalSurface2Light,
  surfaceContainerHigh = CalLineLight,
  surfaceContainerHighest = CalLineLight,
  outline = CalLineLight,
  outlineVariant = CalLineLight,
  error = CalDangerLight,
  onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
  primary = CalAccentDark,
  onPrimary = CalBgDark,
  primaryContainer = CalAccentSoftDark,
  onPrimaryContainer = CalAccentDark,
  secondary = CalInk2Dark,
  onSecondary = CalBgDark,
  secondaryContainer = CalSurface2Dark,
  onSecondaryContainer = CalInkDark,
  tertiary = CalProteinDark,
  onTertiary = CalBgDark,
  tertiaryContainer = CalAccentSoftDark,
  onTertiaryContainer = CalAccentDark,
  background = CalBgDark,
  onBackground = CalInkDark,
  surface = CalSurfaceDark,
  onSurface = CalInkDark,
  surfaceVariant = CalSurface2Dark,
  onSurfaceVariant = CalInk2Dark,
  surfaceContainerLowest = CalSurfaceDark,
  surfaceContainerLow = CalSurface2Dark,
  surfaceContainer = CalSurface2Dark,
  surfaceContainerHigh = CalLineDark,
  surfaceContainerHighest = CalLineDark,
  outline = CalLineDark,
  outlineVariant = CalLineDark,
  error = CalDangerDark,
  onError = Color.White
)

@Composable
fun AhaarTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  CalDesignThemeProvider(darkTheme = darkTheme) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = AhaarTypography,
      content = content
    )
  }
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) = AhaarTheme(darkTheme = darkTheme, content = content)
