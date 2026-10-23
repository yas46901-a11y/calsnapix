package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalTheme
import kotlin.math.abs

/**
 * Modern iOS / Cal AI style smooth scroll wheel picker with tactile haptic snapping.
 * Zero "+ / -" buttons.
 */
@Composable
fun <T> WheelPicker(
  items: List<T>,
  selectedIndex: Int,
  onItemSelected: (index: Int, item: T) -> Unit,
  modifier: Modifier = Modifier,
  visibleItemCount: Int = 5,
  itemHeight: Dp = 44.dp,
  itemLabel: (T) -> String = { it.toString() },
  unitLabel: String? = null,
  testTag: String = "wheel_picker"
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val listState = rememberLazyListState(initialFirstVisibleItemIndex = (selectedIndex - (visibleItemCount / 2)).coerceAtLeast(0))
  val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

  val halfVisible = visibleItemCount / 2
  val totalHeight = itemHeight * visibleItemCount

  // Track currently centered item
  val currentCenterIndex by remember {
    derivedStateOf {
      val firstVisible = listState.firstVisibleItemIndex
      val firstOffset = listState.firstVisibleItemScrollOffset
      val offsetFraction = if (itemHeight.value > 0) firstOffset.toFloat() / (itemHeight.value * 3) else 0f
      (firstVisible + halfVisible + offsetFraction.toInt()).coerceIn(0, (items.size - 1).coerceAtLeast(0))
    }
  }

  // Trigger haptics and selection callback on snap settled
  var previousSnappedIndex by remember { mutableStateOf(selectedIndex) }

  LaunchedEffect(listState) {
    snapshotFlow { listState.firstVisibleItemIndex }
      .collect { firstIndex ->
        val snapped = (firstIndex + halfVisible).coerceIn(0, items.size - 1)
        if (snapped != previousSnappedIndex && snapped in items.indices) {
          previousSnappedIndex = snapped
          view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
          onItemSelected(snapped, items[snapped])
        }
      }
  }

  // Scroll to index if changed externally
  LaunchedEffect(selectedIndex) {
    val targetScroll = (selectedIndex - halfVisible).coerceAtLeast(0)
    if (abs(listState.firstVisibleItemIndex - targetScroll) > 1) {
      listState.animateScrollToItem(targetScroll)
    }
  }

  Box(
    modifier = modifier
      .height(totalHeight)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    // Center selection highlight pill / indicator
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .height(itemHeight)
        .padding(horizontal = 12.dp),
      shape = RoundedCornerShape(12.dp),
      color = colors.surface2.copy(alpha = 0.85f)
    ) {}

    // Wheel items
    LazyColumn(
      state = listState,
      flingBehavior = flingBehavior,
      contentPadding = PaddingValues(vertical = itemHeight * halfVisible),
      modifier = Modifier
        .fillMaxWidth()
        .height(totalHeight)
    ) {
      items(items.size) { index ->
        val item = items[index]
        val distanceFromCenter = abs(index - currentCenterIndex)
        val alpha by animateFloatAsState(
          targetValue = when (distanceFromCenter) {
            0 -> 1f
            1 -> 0.55f
            2 -> 0.25f
            else -> 0.1f
          },
          label = "wheel_item_alpha"
        )
        val scale by animateFloatAsState(
          targetValue = when (distanceFromCenter) {
            0 -> 1.08f
            1 -> 0.94f
            else -> 0.85f
          },
          label = "wheel_item_scale"
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .scale(scale)
            .alpha(alpha),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = itemLabel(item),
            fontSize = if (distanceFromCenter == 0) 22.sp else 18.sp,
            fontWeight = if (distanceFromCenter == 0) FontWeight.Bold else FontWeight.Medium,
            color = if (distanceFromCenter == 0) colors.ink else colors.ink2,
            textAlign = TextAlign.Center
          )
          if (distanceFromCenter == 0 && unitLabel != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = unitLabel,
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = colors.accent
            )
          }
        }
      }
    }

    // Top fade gradient
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(itemHeight * 1.2f)
        .align(Alignment.TopCenter)
        .background(
          Brush.verticalGradient(
            colors = listOf(colors.bg, colors.bg.copy(alpha = 0f))
          )
        )
    )

    // Bottom fade gradient
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(itemHeight * 1.2f)
        .align(Alignment.BottomCenter)
        .background(
          Brush.verticalGradient(
            colors = listOf(colors.bg.copy(alpha = 0f), colors.bg)
          )
        )
    )
  }
}

/**
 * Modern Unit Switcher Pill (e.g. cm vs ft/in, kg vs lb)
 */
@Composable
fun UnitTogglePill(
  options: List<String>,
  selectedIndex: Int,
  onSelectOption: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(999.dp),
    color = colors.surface2
  ) {
    Row(
      modifier = Modifier.padding(3.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      options.forEachIndexed { index, label ->
        val isSelected = index == selectedIndex
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = if (isSelected) colors.ink else Color.Transparent,
          modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable {
              if (!isSelected) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onSelectOption(index)
              }
            }
        ) {
          Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) colors.bg else colors.ink2,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
          )
        }
      }
    }
  }
}

/**
 * Dual Wheel Picker for Height & Weight side by side with Unit Toggles.
 */
@Composable
fun HeightWeightPickerRow(
  heightCm: Int,
  weightKg: Float,
  onHeightChanged: (Int) -> Unit,
  onWeightChanged: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  var heightUnit by remember { mutableStateOf("cm") } // "cm" or "ft"
  var weightUnit by remember { mutableStateOf("kg") } // "kg" or "lb"

  val heightCmList = remember { (120..220).toList() }
  val weightKgList = remember { (35..180).map { it.toFloat() } }

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Height Column
    Column(
      modifier = Modifier.weight(1f),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Height",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )
        UnitTogglePill(
          options = listOf("cm", "ft"),
          selectedIndex = if (heightUnit == "cm") 0 else 1,
          onSelectOption = { heightUnit = if (it == 0) "cm" else "ft" }
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      val currentHeightIndex = heightCmList.indexOf(heightCm).coerceAtLeast(0)
      WheelPicker(
        items = heightCmList,
        selectedIndex = currentHeightIndex,
        onItemSelected = { _, cm -> onHeightChanged(cm) },
        itemLabel = { cm ->
          if (heightUnit == "cm") {
            "$cm"
          } else {
            val totalInches = (cm / 2.54).toInt()
            val feet = totalInches / 12
            val inches = totalInches % 12
            "$feet'$inches\""
          }
        },
        unitLabel = if (heightUnit == "cm") "cm" else null,
        testTag = "height_wheel_picker"
      )
    }

    // Weight Column
    Column(
      modifier = Modifier.weight(1f),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Weight",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )
        UnitTogglePill(
          options = listOf("kg", "lb"),
          selectedIndex = if (weightUnit == "kg") 0 else 1,
          onSelectOption = { weightUnit = if (it == 0) "kg" else "lb" }
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      val currentWeightIndex = weightKgList.indexOfFirst { abs(it - weightKg) < 0.6f }.coerceAtLeast(0)
      WheelPicker(
        items = weightKgList,
        selectedIndex = currentWeightIndex,
        onItemSelected = { _, kg -> onWeightChanged(kg) },
        itemLabel = { kg ->
          if (weightUnit == "kg") {
            String.format(java.util.Locale.US, "%.1f", kg)
          } else {
            String.format(java.util.Locale.US, "%.1f", kg * 2.20462f)
          }
        },
        unitLabel = if (weightUnit == "kg") "kg" else "lb",
        testTag = "weight_wheel_picker"
      )
    }
  }
}
