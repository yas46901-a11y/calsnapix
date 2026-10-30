package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AhaarBottomNav
import com.example.ui.components.AhaarMainTopBar
import com.example.ui.components.GlassBackground
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CameraMealPreviewScreen
import com.example.ui.screens.DailyNutritionTrackingScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MealHistoryScreen
import com.example.ui.screens.MealScanScreen
import com.example.ui.screens.PricingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.RecipesScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.onboarding.OnboardingFlowScreen
import com.example.ui.theme.AhaarTheme

class MainActivity : ComponentActivity() {
  private val viewModel: AhaarViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    handleAuthIntent(intent)
    setContent {
      AhaarTheme {
        AhaarApp(viewModel)
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleAuthIntent(intent)
  }

  private fun handleAuthIntent(intent: Intent?) {
    val data = intent?.dataString ?: return
    if (data.contains("auth-callback") || data.contains("access_token") || data.startsWith("calsnapix://")) {
      viewModel.handleSupabaseOAuthCallback(data)
    }
  }
}

@Composable
fun AhaarApp(viewModel: AhaarViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsState()

  val isAuthScreen = uiState.currentScreen == AppDestination.AUTH

  val isOnboarding = when (uiState.currentScreen) {
    AppDestination.ONBOARDING_1,
    AppDestination.ONBOARDING_2,
    AppDestination.ONBOARDING_3,
    AppDestination.ONBOARDING_4 -> true
    else -> false
  }

  GlassBackground {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = Color.Transparent,
      topBar = {
        if (!isAuthScreen && !isOnboarding && uiState.currentScreen != AppDestination.NUTRITION_TRACKING && uiState.currentScreen != AppDestination.PRICING && uiState.currentScreen != AppDestination.MEAL_HISTORY && uiState.currentScreen != AppDestination.SCAN) {
          val title = when (uiState.currentScreen) {
            AppDestination.HOME -> "CalSnapix"
            AppDestination.SCAN -> "Scan Meal"
            AppDestination.RECIPES -> "Desi Kitchen"
            AppDestination.PROGRESS -> "Analytics"
            AppDestination.PROFILE -> "My Profile"
            AppDestination.NUTRITION_TRACKING -> "Daily Nutrition"
            AppDestination.MEAL_HISTORY -> "Meal History"
            else -> "CalSnapix"
          }
          AhaarMainTopBar(
            title = title,
            logoUrl = uiState.logoUrl,
            avatarUrl = uiState.avatarUrl,
            onProfileClick = { viewModel.navigateTo(AppDestination.PROFILE) }
          )
        }
      },
      bottomBar = {
        if (!isAuthScreen && !isOnboarding) {
          AhaarBottomNav(
            currentScreen = uiState.currentScreen,
            onNavigate = { viewModel.navigateTo(it) }
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (uiState.currentScreen) {
          AppDestination.AUTH -> {
            val context = LocalContext.current
            AuthScreen(
              onLogin = { email, pass -> viewModel.login(email, pass) },
              onSignUp = { name, email, pass -> viewModel.signUp(name, email, pass) },
              onGoogleSignIn = {
                val oauthUrl = viewModel.getGoogleOAuthUrl()
                try {
                  val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(oauthUrl))
                  context.startActivity(browserIntent)
                } catch (e: Exception) {
                  viewModel.signInWithGoogleDirect()
                }
              },
              onPasteOAuthCallback = { viewModel.handleSupabaseOAuthCallback(it) },
              errorMessage = uiState.authError,
              isLoading = uiState.isAuthLoading,
              logoUrl = uiState.logoUrl,
              onBack = null
            )
          }
          AppDestination.ONBOARDING_1,
          AppDestination.ONBOARDING_2,
          AppDestination.ONBOARDING_3,
          AppDestination.ONBOARDING_4 -> {
            val context = LocalContext.current
            OnboardingFlowScreen(
              uiState = uiState,
              onStepChanged = { viewModel.setOnboardingStep(it) },
              onSaveAnswer = { key, value -> viewModel.saveOnboardingAnswer(key, value) },
              onUpdateBiometrics = { h, w, a, g -> viewModel.updateOnboardingBiometrics(h, w, a, g) },
              onUpdateGoal = { g, tw, p -> viewModel.updateOnboardingGoal(g, tw, p) },
              onUpdatePreferences = { d, r, al -> viewModel.updateOnboardingPreferences(d, r, al) },
              onUpdateLifestyle = { f, o, act -> viewModel.updateOnboardingLifestyle(f, o, act) },
              onUpdateMealTimings = { b, l, d, s, w -> viewModel.updateOnboardingMealTimings(b, l, d, s, w) },
              onApplyReferral = { viewModel.applyReferralCode(it) },
              onSignUp = { name, email, pass -> viewModel.signUp(name, email, pass) },
              onLogin = { email, pass -> viewModel.login(email, pass) },
              onGoogleSignIn = {
                val oauthUrl = viewModel.getGoogleOAuthUrl()
                try {
                  val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(oauthUrl))
                  context.startActivity(browserIntent)
                } catch (e: Exception) {
                  // In headless
                }
              },
              onNavigateToAuth = { viewModel.navigateTo(AppDestination.AUTH) },
              onFinishOnboarding = { viewModel.finishOnboarding() }
            )
          }
        AppDestination.HOME -> {
          HomeScreen(
            uiState = uiState,
            onNavigate = { viewModel.navigateTo(it) },
            onLogWater = { viewModel.logWater() },
            onLogWaterBottle = { viewModel.logWaterBottle() },
            onUndoWater = { viewModel.undoWater() },
            onToggleWaterGlass = { viewModel.toggleWaterGlass(it) },
            onUpdateWaterGoal = { viewModel.updateWaterGoal(it) },
            onAddCustomMeal = { viewModel.addCustomMeal(it) },
            onDeleteMeal = { viewModel.deleteMeal(it) },
            onUpdateCalorieGoal = { viewModel.updateCalorieTarget(it) },
            onSelectRegion = { viewModel.selectRegion(it) },
            onUpdateStepGoal = { viewModel.updateStepGoal(it) },
            onAddManualSteps = { viewModel.logManualSteps(it) },
            onRefreshStepData = { viewModel.refreshStepData() },
            onStartStepTracking = { viewModel.startStepTracking() },
            reminderManager = viewModel.mealReminderManager,
            onSimulateStreak = { viewModel.simulateWaterStreak(it) },
            onSimulateWeeklyMacro = { viewModel.simulateWeeklyMacro(it) }
          )
        }
        AppDestination.SCAN -> {
          CameraMealPreviewScreen(
            uiState = uiState,
            onLogMeal = { viewModel.logScannedMeal(it) },
            onSaveCorrection = { dishId, orig, corr, f -> viewModel.saveUserCorrection(dishId, orig, corr, f) },
            onSaveAccuracyTest = { test -> viewModel.saveAccuracyTest(test) },
            onReportWrongResult = { dish, photoId, note -> viewModel.reportWrongResult(dish, photoId, note) },
            onNavigateToHistory = { viewModel.navigateTo(AppDestination.MEAL_HISTORY) },
            onNavigateToPricing = { viewModel.navigateTo(AppDestination.PRICING) },
            onBack = { viewModel.navigateTo(AppDestination.HOME) }
          )
        }
        AppDestination.RECIPES -> {
          RecipesScreen(
            uiState = uiState,
            onSearchChange = { viewModel.updateRecipeQuery(it) },
            onCategorySelect = { viewModel.selectRecipeCategory(it) },
            onBookmarkToggle = { viewModel.toggleRecipeBookmark(it) },
            onLogRecipeAsMeal = { viewModel.addCustomMeal(it) }
          )
        }
        AppDestination.PROGRESS -> {
          ProgressScreen(
            uiState = uiState,
            onLogWater = { viewModel.logWater() },
            onLogWaterBottle = { viewModel.logWaterBottle() },
            onToggleWaterGlass = { viewModel.toggleWaterGlass(it) },
            onLogDailyWeight = { weight, note -> viewModel.logDailyWeight(weight, note) },
            onNavigate = { viewModel.navigateTo(it) }
          )
        }
        AppDestination.PROFILE -> {
          ProfileScreen(
            uiState = uiState,
            onLogout = { viewModel.logout() },
            onDeleteAccount = { viewModel.deleteAccount() },
            onRecalibrateBlueprint = { viewModel.recalibrateOnboarding() },
            onTestSupabase = { viewModel.testSupabaseConnection() },
            onSyncSupabase = { viewModel.syncWithSupabase() },
            onDismissSupabaseMessage = { viewModel.clearSupabaseSyncMessage() },
            onUpdateDietaryPreference = { dietKey ->
              viewModel.toggleDiet(dietKey)
              viewModel.syncNutritionalProfileFromCurrentState()
            },
            onLinkInstagram = { handle -> viewModel.linkAndVerifyInstagram(handle) },
            onUnlinkInstagram = { viewModel.unlinkInstagram() },
            onNavigateToPricing = { viewModel.navigateTo(AppDestination.PRICING) },
            onNavigateToAuth = { viewModel.navigateTo(AppDestination.AUTH) },
            onUpdateGoalType = { goalId -> viewModel.updateGoalType(goalId) },
            onDeleteMyData = { viewModel.deleteMyData() },
            onToggleAiConsent = { consent -> viewModel.toggleAiTrainingConsent(consent) },
            onSaveAccuracyTest = { test -> viewModel.saveAccuracyTest(test) }
          )
        }
        AppDestination.NUTRITION_TRACKING -> {
          DailyNutritionTrackingScreen(
            uiState = uiState,
            onNavigate = { viewModel.navigateTo(it) },
            onUpdateMacroTargets = { c, p, f -> viewModel.updateMacroTargets(c, p, f) },
            onSavePersonalizedGoals = { cal, c, p, f -> viewModel.updatePersonalizedGoals(cal, c, p, f) }
          )
        }
        AppDestination.PRICING -> {
          PricingScreen(
            uiState = uiState,
            onBack = { viewModel.navigateTo(AppDestination.HOME) },
            onSelectPlan = { planId ->
              viewModel.selectSubscriptionPlan(planId)
              viewModel.navigateTo(AppDestination.HOME)
            }
          )
        }
        AppDestination.MEAL_HISTORY -> {
          MealHistoryScreen(
            uiState = uiState,
            onBack = { viewModel.navigateTo(AppDestination.HOME) },
            onDeleteScannedMeal = { viewModel.deleteScannedMealLog(it) },
            onSeedSampleHistory = { viewModel.seed30DayHistory() }
          )
        }
      }

      // Point 6: Single-screen Daily Summary modal shown after logging meal
      uiState.lastLoggedMealSummary?.let { summary ->
        com.example.ui.components.PostMealSummaryModal(
          summary = summary,
          onDismiss = { viewModel.dismissMealSummaryModal() }
        )
      }
    }
  }
}
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}
