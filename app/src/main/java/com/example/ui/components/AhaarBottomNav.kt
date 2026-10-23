package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppDestination
import com.example.ui.theme.CalTheme

private data class NavItemData(
  val destination: AppDestination,
  val label: String,
  val tag: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
)

/**
 * Modern CalSnapix Bottom Navigation.
 * 4 Tabs (Home, Recipes, Progress, Profile) + floating center Scan FAB in --ink / --bg.
 * Active = --ink, Inactive = --ink-2. Zero orange.
 */
@Composable
fun AhaarBottomNav(
  currentScreen: AppDestination,
  onNavigate: (AppDestination) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  val leftItems = listOf(
    NavItemData(AppDestination.HOME, "Home", "nav_home", Icons.Filled.Home, Icons.Outlined.Home),
    NavItemData(AppDestination.RECIPES, "Recipes", "nav_recipes", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu)
  )

  val rightItems = listOf(
    NavItemData(AppDestination.PROGRESS, "Analytics", "nav_progress", Icons.AutoMirrored.Filled.TrendingUp, Icons.AutoMirrored.Outlined.TrendingUp),
    NavItemData(AppDestination.PROFILE, "Profile", "nav_profile", Icons.Filled.Person, Icons.Outlined.Person)
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding(),
    contentAlignment = Alignment.BottomCenter
  ) {
    // Frosted bar container
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp),
      color = colors.surface.copy(alpha = 0.94f),
      border = BorderStroke(1.dp, colors.line)
    ) {
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left 2 items
        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          leftItems.forEach { item ->
            NavTabItem(
              item = item,
              isSelected = currentScreen == item.destination,
              onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onNavigate(item.destination)
              }
            )
          }
        }

        // Center space reserved for floating scan button
        Spacer(modifier = Modifier.size(64.dp))

        // Right 2 items
        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          rightItems.forEach { item ->
            NavTabItem(
              item = item,
              isSelected = currentScreen == item.destination,
              onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onNavigate(item.destination)
              }
            )
          }
        }
      }
    }

    // Floating circular Scan Button in --ink / --bg centered above the bar
    Box(
      modifier = Modifier
        .offset(y = (-18).dp)
        .size(56.dp)
        .shadow(elevation = 6.dp, shape = CircleShape)
        .clip(CircleShape)
        .background(colors.ink)
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null
        ) {
          view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
          onNavigate(AppDestination.SCAN)
        }
        .testTag("nav_scan"),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Filled.PhotoCamera,
        contentDescription = "Scan Meal",
        tint = colors.bg,
        modifier = Modifier.size(26.dp)
      )
    }
  }
}

@Composable
private fun NavTabItem(
  item: NavItemData,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val colors = CalTheme.colors
  val contentColor by animateColorAsState(
    targetValue = if (isSelected) colors.ink else colors.ink2,
    label = "tab_color"
  )

  Column(
    modifier = Modifier
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      )
      .padding(vertical = 6.dp, horizontal = 12.dp)
      .testTag(item.tag),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
      contentDescription = item.label,
      tint = contentColor,
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = item.label,
      color = contentColor,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
      letterSpacing = (-0.2).sp
    )
  }
}
