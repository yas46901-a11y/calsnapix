package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =====================================================================
// PREMIUM LUXURY AUTOMOTIVE & FINTECH DESIGN SYSTEM PALETTE
// Minimal, modern, neutral base with single precision accent and soft gray
// =====================================================================

// Neutral Base Tokens
val AutoNeutralLight = Color(0xFFF8F9FA)       // Crisp near-white base (#F8F9FA)
val AutoNeutralDark = Color(0xFF0F1115)        // Very dark charcoal / obsidian (#0F1115)

// Surface Tokens
val AutoSurfaceLight = Color(0xFFFFFFFF)       // Clean white card surface
val AutoSurfaceDark = Color(0xFF171A21)        // Elevated dark surface (#171A21)

// Secondary Elevated Surface Tokens
val AutoSurface2Light = Color(0xFFF1F3F6)      // Subtle secondary card surface
val AutoSurface2Dark = Color(0xFF20242D)       // Subtle secondary dark surface

// Text / Ink Tokens
val AutoInkLight = Color(0xFF111827)           // Deep charcoal primary text (#111827)
val AutoInkDark = Color(0xFFF9FAFB)            // Crisp near-white primary text (#F9FAFB)
val AutoInk2Light = Color(0xFF6B7280)          // Soft gray secondary text (#6B7280)
val AutoInk2Dark = Color(0xFF9CA3AF)           // Soft gray secondary text dark (#9CA3AF)

// Divider / Line Tokens
val AutoLineLight = Color(0xFFE5E7EB)          // Subtle divider line (#E5E7EB)
val AutoLineDark = Color(0xFF262B35)           // Subtle dark divider line (#262B35)

// Single Precision Accent Token (Used sparingly for primary actions)
val AutoAccentLight = Color(0xFF2563EB)        // Precision electric blue (#2563EB)
val AutoAccentDark = Color(0xFF3B82F6)         // Electric blue dark mode (#3B82F6)
val AutoAccentSoftLight = Color(0xFFEFF6FF)    // Soft accent tint
val AutoAccentSoftDark = Color(0xFF1E293B)     // Dark accent tint

// Functional Status Tokens
val AutoSuccessLight = Color(0xFF10B981)       // Precision status green
val AutoSuccessDark = Color(0xFF34D399)
val AutoWarnLight = Color(0xFFF59E0B)          // Amber warning
val AutoWarnDark = Color(0xFFFBBF24)
val AutoDangerLight = Color(0xFFEF4444)        // Alert red
val AutoDangerDark = Color(0xFFF87171)

// Mappings into CalTheme
val CalBgLight = AutoNeutralLight
val CalBgDark = AutoNeutralDark

val CalSurfaceLight = AutoSurfaceLight
val CalSurfaceDark = AutoSurfaceDark

val CalSurface2Light = AutoSurface2Light
val CalSurface2Dark = AutoSurface2Dark

val CalInkLight = AutoInkLight
val CalInkDark = AutoInkDark

val CalInk2Light = AutoInk2Light
val CalInk2Dark = AutoInk2Dark

val CalLineLight = AutoLineLight
val CalLineDark = AutoLineDark

val CalAccentLight = AutoAccentLight
val CalAccentDark = AutoAccentDark

val CalAccentSoftLight = AutoAccentSoftLight
val CalAccentSoftDark = AutoAccentSoftDark

val CalProteinLight = AutoAccentLight
val CalProteinDark = AutoAccentDark

val CalCarbsLight = Color(0xFF64748B)
val CalCarbsDark = Color(0xFF94A3B8)

val CalFatLight = Color(0xFF475569)
val CalFatDark = Color(0xFF64748B)

val CalSuccessLight = AutoSuccessLight
val CalSuccessDark = AutoSuccessDark

val CalWarnLight = AutoWarnLight
val CalWarnDark = AutoWarnDark

val CalDangerLight = AutoDangerLight
val CalDangerDark = AutoDangerDark

// Backward-compatible semantic bindings mapped to luxury automotive/fintech palette
val AhaarPrimary = AutoAccentLight
val AhaarOnPrimary = Color.White
val AhaarPrimaryContainer = AutoAccentSoftLight
val AhaarPrimaryFixed = AutoAccentSoftLight
val AhaarSecondary = AutoInk2Light
val AhaarSecondaryFixed = AutoLineLight
val AhaarTertiary = AutoAccentLight
val AhaarTertiaryContainer = AutoAccentSoftLight
val AhaarTertiaryFixed = AutoLineLight

