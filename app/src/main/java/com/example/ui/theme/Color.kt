package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// CalSnapix Apple Health meets Cal AI palette — Orange is completely banned.
val CalBgLight = Color(0xFFF7F6F2)
val CalBgDark = Color(0xFF0B0C0B)

val CalSurfaceLight = Color(0xFFFFFFFF)
val CalSurfaceDark = Color(0xFF151716)

val CalSurface2Light = Color(0xFFEFEDE7)
val CalSurface2Dark = Color(0xFF1E211F)

val CalInkLight = Color(0xFF0E0F0C)
val CalInkDark = Color(0xFFF4F3EE)

val CalInk2Light = Color(0xFF5E6159)
val CalInk2Dark = Color(0xFFA3A79F)

val CalLineLight = Color(0xFFE4E2DA)
val CalLineDark = Color(0xFF2A2D2B)

val CalAccentLight = Color(0xFF0F5C47) // Deep Emerald
val CalAccentDark = Color(0xFF4ADE9F)

val CalAccentSoftLight = Color(0xFFDCEBE4)
val CalAccentSoftDark = Color(0xFF14302A)

// Macro Colors
val CalProteinLight = Color(0xFF4C5BD4) // Indigo
val CalProteinDark = Color(0xFF8C97F2)

val CalCarbsLight = Color(0xFFD9A441)   // Muted Gold
val CalCarbsDark = Color(0xFFE8C06A)

val CalFatLight = Color(0xFFD9697F)     // Dusty Rose
val CalFatDark = Color(0xFFEE90A2)

val CalSuccessLight = Color(0xFF2E9E6B)
val CalSuccessDark = Color(0xFF4ADE9F)

val CalWarnLight = Color(0xFFC98A1B)
val CalWarnDark = Color(0xFFE8C06A)

val CalDangerLight = Color(0xFFC8433B)
val CalDangerDark = Color(0xFFF87171)

// Backward-compatible mappings (Orange eliminated — mapped to emerald and ink)
val AhaarPrimary = CalAccentLight
val AhaarPrimaryContainer = CalInkLight
val AhaarOnPrimary = Color.White
val AhaarOnPrimaryContainer = CalBgLight
val AhaarPrimaryFixed = CalAccentSoftLight
val AhaarPrimaryFixedDim = CalAccentSoftLight

val AhaarSecondary = CalInk2Light
val AhaarSecondaryContainer = CalSurface2Light
val AhaarSecondaryFixed = CalSurface2Light
val AhaarSecondaryFixedDim = CalLineLight
val AhaarOnSecondaryFixed = CalInkLight
val AhaarOnSecondaryFixedVariant = CalInk2Light

val AhaarTertiary = CalAccentLight
val AhaarTertiaryContainer = CalAccentSoftLight
val AhaarTertiaryFixed = CalAccentSoftLight
val AhaarTertiaryFixedDim = CalAccentSoftLight
val AhaarOnTertiaryFixed = CalInkLight
val AhaarOnTertiaryFixedVariant = CalAccentLight

val AhaarSurface = CalSurfaceLight
val AhaarBackground = CalBgLight
val AhaarSurfaceContainerLowest = Color.White
val AhaarSurfaceContainerLow = CalSurface2Light
val AhaarSurfaceContainer = CalSurface2Light
val AhaarSurfaceContainerHigh = CalLineLight
val AhaarSurfaceContainerHighest = CalLineLight

val AhaarOnSurface = CalInkLight
val AhaarOnSurfaceVariant = CalInk2Light
val AhaarOutline = CalLineLight
val AhaarOutlineVariant = CalLineLight
