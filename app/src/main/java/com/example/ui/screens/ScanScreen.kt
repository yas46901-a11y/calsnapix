package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.AhaarUiState
import com.example.model.LoggedMeal

/**
 * ScanScreen delegates to CameraMealPreviewScreen with full CameraX meal framing and preview integration.
 */
@Composable
fun ScanScreen(
  uiState: AhaarUiState,
  onLogMeal: (LoggedMeal) -> Unit,
  onNavigateToHistory: () -> Unit = {},
  onBack: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  CameraMealPreviewScreen(
    uiState = uiState,
    onLogMeal = onLogMeal,
    onNavigateToHistory = onNavigateToHistory,
    onBack = onBack,
    modifier = modifier
  )
}
