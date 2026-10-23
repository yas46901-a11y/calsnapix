package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.AhaarUiState
import com.example.model.LoggedMeal

/**
 * ScanScreen delegates to MealScanScreen with full CameraX integration.
 */
@Composable
fun ScanScreen(
  uiState: AhaarUiState,
  onLogMeal: (LoggedMeal) -> Unit,
  modifier: Modifier = Modifier
) {
  MealScanScreen(
    uiState = uiState,
    onLogMeal = onLogMeal,
    modifier = modifier
  )
}
