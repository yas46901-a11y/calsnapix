package com.example.ui.screens.onboarding

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.components.PricingPaywallContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AhaarUiState
import com.example.ui.components.HeightWeightPickerRow
import com.example.ui.components.WheelPicker
import com.example.ui.theme.CalTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Total number of screens in the one-question-per-screen onboarding flow.
 */
const val TOTAL_ONBOARDING_STEPS = 21

/**
 * Full-screen, one-question-per-screen onboarding flow with:
 * - Thin top progress bar with subtle integrated step indicator
 * - Back arrow on top left (no avatar, no top skip)
 * - Huge headlines (28-34px), short helper line
 * - Big black pill "Continue" button pinned to bottom (56dp tall, disabled until answered)
 * - Smooth horizontal slide + fade transitions
 * - Scroll wheel pickers for all numbers (zero +/- buttons)
 * - Persists answers after each screen
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingFlowScreen(
  uiState: AhaarUiState,
  onStepChanged: (Int) -> Unit,
  onSaveAnswer: (key: String, value: Any) -> Unit,
  onUpdateBiometrics: (heightCm: Int, weightKg: Float, age: Int, gender: String) -> Unit,
  onUpdateGoal: (goalId: String, targetWeightKg: Float, paceKgPerWeek: Float) -> Unit,
  onUpdatePreferences: (diets: Set<String>, regions: Set<String>, allergies: Set<String>) -> Unit,
  onUpdateLifestyle: (workoutFreq: String, obstacles: Set<String>, activityLevel: String) -> Unit,
  onUpdateMealTimings: (breakfast: String, lunch: String, dinner: String, snack: String, waterGoal: Float) -> Unit,
  onApplyReferral: (String) -> Result<String>,
  onSignUp: (name: String, email: String, pass: String) -> Unit,
  onLogin: (email: String, pass: String) -> Unit = { _, _ -> },
  onGoogleSignIn: () -> Unit,
  onNavigateToAuth: () -> Unit,
  onFinishOnboarding: () -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val coroutineScope = rememberCoroutineScope()
  val currentStep = uiState.onboardingStep.coerceIn(0, TOTAL_ONBOARDING_STEPS - 1)

  // Local state for answers on current screen
  var localGender by remember(uiState.gender) { mutableStateOf(uiState.gender) }
  var localWorkoutFreq by remember { mutableStateOf("3_5") }
  var localHearAboutUs by remember { mutableStateOf("Instagram") }
  var localTriedOtherApps by remember { mutableStateOf<Boolean?>(null) }
  var localHeightCm by remember(uiState.heightCm) { mutableIntStateOf(uiState.heightCm) }
  var localWeightKg by remember { mutableFloatStateOf(68.0f) }
  var localAge by remember(uiState.age) { mutableIntStateOf(uiState.age) }
  var localGoalId by remember(uiState.selectedGoalId) { mutableStateOf(uiState.selectedGoalId) }
  var localTargetWeightKg by remember { mutableFloatStateOf(62.2f) }
  var localPaceKgPerWeek by remember { mutableFloatStateOf(0.5f) }
  var localObstacles by remember { mutableStateOf(setOf("busy_schedule")) }
  var localDiets by remember(uiState.selectedDiets) { mutableStateOf(uiState.selectedDiets) }
  var localRegions by remember(uiState.selectedRegion) { mutableStateOf(setOf(uiState.selectedRegion)) }
  var localAllergies by remember(uiState.selectedAllergies) { mutableStateOf(uiState.selectedAllergies) }
  var localActivityLevel by remember(uiState.activityLevel) { mutableStateOf(uiState.activityLevel) }
  var localBreakfastTime by remember { mutableStateOf("8:30 AM") }
  var localLunchTime by remember { mutableStateOf("1:30 PM") }
  var localDinnerTime by remember { mutableStateOf("8:15 PM") }
  var localSnackTime by remember { mutableStateOf("5:00 PM") }
  var localWaterGoalLiters by remember { mutableFloatStateOf(2.0f) }
  var localNotificationsAllowed by remember { mutableStateOf(true) }
  var localReferralCodeInput by remember { mutableStateOf("") }
  var referralSuccessMessage by remember { mutableStateOf<String?>(null) }
  var referralErrorMessage by remember { mutableStateOf<String?>(null) }
  var referralIsChecking by remember { mutableStateOf(false) }
  var referralDiscountPercent by remember(uiState.appliedDiscountPercent) { mutableIntStateOf(uiState.appliedDiscountPercent) }

  // Auth fields for Step 24 (Create Account)
  var authName by remember { mutableStateOf("") }
  var authEmail by remember { mutableStateOf("") }
  var authPassword by remember { mutableStateOf("") }
  var authIsPasswordVisible by remember { mutableStateOf(false) }
  var authIsSignUpMode by remember { mutableStateOf(true) }

  // Step 22 Loading timer state
  var setupProgress by remember { mutableFloatStateOf(0f) }
  var setupStatusIndex by remember { mutableIntStateOf(0) }

  // Handle hardware / predictive back
  BackHandler(enabled = currentStep > 0) {
    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    onStepChanged(currentStep - 1)
  }

  // Determine if Continue button should be enabled on the current step
  val isContinueEnabled by remember(
    currentStep,
    localGender,
    localWorkoutFreq,
    localHearAboutUs,
    localTriedOtherApps,
    localGoalId,
    localObstacles,
    localDiets,
    localRegions,
    localActivityLevel,
    authName,
    authEmail,
    authPassword,
    authIsSignUpMode
  ) {
    derivedStateOf {
      when (currentStep) {
        0 -> true
        1 -> localGender.isNotBlank()
        2 -> localWorkoutFreq.isNotBlank()
        3 -> localHearAboutUs.isNotBlank()
        4 -> localTriedOtherApps != null
        5 -> localHeightCm in 100..250 && localWeightKg in 30f..250f
        6 -> localAge in 13..95
        7 -> localGoalId.isNotBlank()
        8 -> localTargetWeightKg in 30f..250f
        9 -> true // motivational interstitial
        10 -> localPaceKgPerWeek in 0.2f..1.2f
        11 -> localObstacles.isNotEmpty()
        12 -> localDiets.isNotEmpty()
        13 -> true // allergies can be None
        14 -> localActivityLevel.isNotBlank()
        15 -> true // notification permission
        16 -> true // referral code (optional / skippable)
        17 -> setupProgress >= 1f // loading step
        18 -> true // plan ready
        19 -> if (authIsSignUpMode) {
          authName.trim().length >= 2 && authEmail.contains("@") && authPassword.length >= 6
        } else {
          authEmail.contains("@") && authPassword.length >= 6
        }
        20 -> true // paywall
        else -> true
      }
    }
  }

  // Advance to next step with haptic feedback & auto-save
  fun handleContinue() {
    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    when (currentStep) {
      1 -> {
        onSaveAnswer("gender", localGender)
        onUpdateBiometrics(localHeightCm, localWeightKg, localAge, localGender)
      }
      2 -> {
        onSaveAnswer("workout_frequency", localWorkoutFreq)
        onUpdateLifestyle(localWorkoutFreq, localObstacles, localActivityLevel)
      }
      3 -> onSaveAnswer("hear_about_us", localHearAboutUs)
      4 -> onSaveAnswer("tried_other_apps", localTriedOtherApps ?: false)
      5 -> {
        onSaveAnswer("height_cm", localHeightCm)
        onSaveAnswer("current_weight_kg", localWeightKg)
        onUpdateBiometrics(localHeightCm, localWeightKg, localAge, localGender)
      }
      6 -> {
        onSaveAnswer("age", localAge)
        onUpdateBiometrics(localHeightCm, localWeightKg, localAge, localGender)
      }
      7 -> {
        onSaveAnswer("primary_goal", localGoalId)
        onUpdateGoal(localGoalId, localTargetWeightKg, localPaceKgPerWeek)
      }
      8 -> {
        onSaveAnswer("target_weight_kg", localTargetWeightKg)
        onUpdateGoal(localGoalId, localTargetWeightKg, localPaceKgPerWeek)
      }
      10 -> {
        onSaveAnswer("pace_kg_per_week", localPaceKgPerWeek)
        onUpdateGoal(localGoalId, localTargetWeightKg, localPaceKgPerWeek)
      }
      11 -> {
        onSaveAnswer("biggest_obstacles", localObstacles.toList())
        onUpdateLifestyle(localWorkoutFreq, localObstacles, localActivityLevel)
      }
      12 -> {
        onSaveAnswer("diet_types", localDiets.toList())
        onUpdatePreferences(localDiets, emptySet(), localAllergies)
      }
      13 -> {
        onSaveAnswer("allergies_avoid", localAllergies.toList())
        onUpdatePreferences(localDiets, emptySet(), localAllergies)
      }
      14 -> {
        onSaveAnswer("activity_level", localActivityLevel)
        onUpdateLifestyle(localWorkoutFreq, localObstacles, localActivityLevel)
      }
      15 -> onSaveAnswer("notifications_allowed", localNotificationsAllowed)
      16 -> {
        if (localReferralCodeInput.isNotBlank()) {
          onSaveAnswer("referral_code", localReferralCodeInput)
        }
      }
      19 -> {
        if (authIsSignUpMode) {
          onSignUp(authName.trim(), authEmail.trim(), authPassword)
        } else {
          onLogin(authEmail.trim(), authPassword)
        }
      }
      20 -> {
        onFinishOnboarding()
        return
      }
    }

    if (currentStep < TOTAL_ONBOARDING_STEPS - 1) {
      onStepChanged(currentStep + 1)
    } else {
      onFinishOnboarding()
    }
  }

  // Main Container
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(colors.bg)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Top Header: Progress Bar with subtle Step indicator and Back arrow
      if (currentStep > 0 && currentStep != 17) { // Hide progress bar on welcome and loading screens
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Back Arrow (only back arrow, no avatar, no top skip)
          IconButton(
            onClick = {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              onStepChanged(currentStep - 1)
            },
            modifier = Modifier
              .size(40.dp)
              .testTag("onboarding_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = colors.ink,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Subtle Thin Progress Bar with embedded Step text inside
          Box(
            modifier = Modifier
              .weight(1f)
              .height(14.dp)
              .clip(RoundedCornerShape(999.dp))
              .background(colors.line),
            contentAlignment = Alignment.Center
          ) {
            val progressFraction = (currentStep.toFloat()) / (TOTAL_ONBOARDING_STEPS - 1).toFloat()
            val animatedProgress by animateFloatAsState(
              targetValue = progressFraction.coerceIn(0.04f, 1f),
              animationSpec = tween(300, easing = FastOutSlowInEasing),
              label = "onboarding_progress"
            )

            Box(
              modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(animatedProgress)
                .height(14.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(colors.ink)
            )

            Text(
              text = "Step $currentStep of ${TOTAL_ONBOARDING_STEPS - 1}",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = if (progressFraction > 0.45f) colors.bg else colors.ink2,
              letterSpacing = 0.3.sp
            )
          }
        }
      } else {
        Spacer(modifier = Modifier.height(12.dp))
      }

      // 2. Animated Step Content: Horizontal slide + fade transitions
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      ) {
        AnimatedContent(
          targetState = currentStep,
          transitionSpec = {
            if (targetState > initialState) {
              slideInHorizontally(
                initialOffsetX = { fullWidth -> (fullWidth * 0.8f).toInt() },
                animationSpec = tween(240, easing = FastOutSlowInEasing)
              ) + fadeIn(animationSpec = tween(200)) togetherWith
                slideOutHorizontally(
                  targetOffsetX = { fullWidth -> (-fullWidth * 0.8f).toInt() },
                  animationSpec = tween(240, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(180))
            } else {
              slideInHorizontally(
                initialOffsetX = { fullWidth -> (-fullWidth * 0.8f).toInt() },
                animationSpec = tween(240, easing = FastOutSlowInEasing)
              ) + fadeIn(animationSpec = tween(200)) togetherWith
                slideOutHorizontally(
                  targetOffsetX = { fullWidth -> (fullWidth * 0.8f).toInt() },
                  animationSpec = tween(240, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(180))
            }
          },
          label = "onboarding_screen_transition"
        ) { step ->
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 20.dp, vertical = 8.dp)
          ) {
            when (step) {
              // 0. Welcome / Hook
              0 -> Step0Welcome(
                onGetStarted = { handleContinue() },
                onLoginClick = onNavigateToAuth
              )

              // 1. Gender
              1 -> Step1Gender(
                selectedGender = localGender,
                onSelectGender = { localGender = it }
              )

              // 2. Workout frequency
              2 -> Step2WorkoutFrequency(
                selectedFreq = localWorkoutFreq,
                onSelectFreq = { localWorkoutFreq = it }
              )

              // 3. Where did you hear about us?
              3 -> Step3HearAboutUs(
                selectedSource = localHearAboutUs,
                onSelectSource = { localHearAboutUs = it }
              )

              // 4. Have you tried other calorie tracking apps?
              4 -> Step4TriedOtherApps(
                tried = localTriedOtherApps,
                onSelect = { localTriedOtherApps = it }
              )

              // 5. Height & Weight Wheel Pickers (Polished)
              5 -> Step6HeightWeight(
                heightCm = localHeightCm,
                weightKg = localWeightKg,
                onHeightChange = { localHeightCm = it },
                onWeightChange = { localWeightKg = it }
              )

              // 6. Age Wheel Picker
              6 -> Step7Age(
                age = localAge,
                onAgeChange = { localAge = it }
              )

              // 7. Primary Goal
              7 -> Step8PrimaryGoal(
                selectedGoalId = localGoalId,
                onSelectGoal = { localGoalId = it }
              )

              // 8. Target Weight Wheel Picker
              8 -> Step9TargetWeight(
                currentWeightKg = localWeightKg,
                targetWeightKg = localTargetWeightKg,
                onTargetWeightChange = { localTargetWeightKg = it }
              )

              // 9. Motivational Interstitial
              9 -> Step10Motivational(
                currentWeightKg = localWeightKg,
                targetWeightKg = localTargetWeightKg
              )

              // 10. Pace Slider (Tortoise to Rabbit)
              10 -> Step11PaceSlider(
                paceKgPerWeek = localPaceKgPerWeek,
                onPaceChange = { localPaceKgPerWeek = it }
              )

              // 11. Biggest Obstacles (Multi-select)
              11 -> Step12BiggestObstacles(
                selectedObstacles = localObstacles,
                onToggleObstacle = { obs ->
                  localObstacles = if (localObstacles.contains(obs)) {
                    localObstacles - obs
                  } else {
                    localObstacles + obs
                  }
                }
              )

              // 12. Diet Type (Multi-select)
              12 -> Step13DietType(
                selectedDiets = localDiets,
                onToggleDiet = { diet ->
                  localDiets = if (localDiets.contains(diet)) {
                    if (localDiets.size > 1) localDiets - diet else localDiets
                  } else {
                    localDiets + diet
                  }
                }
              )

              // 13. Allergies / Avoid (Multi-select chips)
              13 -> Step15Allergies(
                selectedAllergies = localAllergies,
                onToggleAllergy = { alg ->
                  localAllergies = if (alg == "None") {
                    setOf("None")
                  } else {
                    val next = localAllergies - "None"
                    if (next.contains(alg)) next - alg else next + alg
                  }
                }
              )

              // 14. Activity Level in Daily Life
              14 -> Step16ActivityLevel(
                selectedLevel = localActivityLevel,
                onSelectLevel = { localActivityLevel = it }
              )

              // 15. Reminders / Notification Permission
              15 -> Step19Notifications(
                notificationsAllowed = localNotificationsAllowed,
                onTogglePermission = { localNotificationsAllowed = it }
              )

              // 16. Referral code screen
              16 -> Step21Referral(
                code = localReferralCodeInput,
                onCodeChange = {
                  localReferralCodeInput = it.take(10).uppercase()
                  referralSuccessMessage = null
                  referralErrorMessage = null
                },
                onApply = {
                  referralIsChecking = true
                  referralSuccessMessage = null
                  referralErrorMessage = null
                  coroutineScope.launch {
                    delay(500)
                    val res = onApplyReferral(localReferralCodeInput)
                    referralIsChecking = false
                    if (res.isSuccess) {
                      referralSuccessMessage = res.getOrNull()
                      referralDiscountPercent = uiState.appliedDiscountPercent.coerceAtLeast(50)
                    } else {
                      referralErrorMessage = res.exceptionOrNull()?.message ?: "Invalid or expired referral code"
                    }
                  }
                },
                onSkip = { handleContinue() },
                isChecking = referralIsChecking,
                successMessage = referralSuccessMessage,
                errorMessage = referralErrorMessage,
                discountPercent = referralDiscountPercent
              )

              // 17. "Setting everything up..." Loading screen (4-5s)
              17 -> Step22SettingUp(
                onFinished = {
                  handleContinue()
                }
              )

              // 18. "Your plan is ready" Result screen
              18 -> Step23PlanReady(
                uiState = uiState,
                targetWeightKg = localTargetWeightKg,
                onEditPlan = { onStepChanged(5) } // go back to height/weight
              )

              // 19. Create Account (Sign in with Google + email/password)
              19 -> Step24CreateAccount(
                name = authName,
                email = authEmail,
                password = authPassword,
                isPasswordVisible = authIsPasswordVisible,
                isSignUpMode = authIsSignUpMode,
                onToggleMode = { authIsSignUpMode = it },
                onNameChange = { authName = it },
                onEmailChange = { authEmail = it },
                onPasswordChange = { authPassword = it },
                onTogglePasswordVisibility = { authIsPasswordVisible = !authIsPasswordVisible },
                onGoogleSignIn = onGoogleSignIn,
                errorMessage = uiState.authError,
                isLoading = uiState.isAuthLoading
              )

              // 20. Paywall / Pro plans preview -> then Home
              20 -> Step25Paywall(
                discountPercent = referralDiscountPercent,
                onStartTrial = { handleContinue() },
                onContinueFree = { handleContinue() }
              )
            }

            Spacer(modifier = Modifier.height(100.dp)) // Space for pinned bottom continue bar
          }
        }
      }

      // 3. Pinned Bottom Continue Bar (Big black pill 56dp tall, disabled until answered)
      if (currentStep > 0 && currentStep != 17) { // Screen 0 and 17 have their own inline action triggers
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp),
          color = colors.bg
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 12.dp)
          ) {
            Button(
              onClick = { handleContinue() },
              enabled = isContinueEnabled,
              modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("onboarding_continue_button"),
              shape = RoundedCornerShape(999.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = colors.ink,
                contentColor = colors.bg,
                disabledContainerColor = colors.line,
                disabledContentColor = colors.ink2.copy(alpha = 0.5f)
              )
            ) {
              Text(
                text = when (currentStep) {
                  15 -> if (localNotificationsAllowed) "Allow Notifications" else "Continue"
                  18 -> "Let's Start"
                  19 -> if (authIsSignUpMode) "Create Account & View Plan" else "Sign In & View Plan"
                  20 -> "Unlock CalSnapix Pro"
                  else -> "Continue"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
              )
            }

            // Sensible skip options for non-essential screens
            if (currentStep == 15) {
              TextButton(
                onClick = {
                  localNotificationsAllowed = false
                  handleContinue()
                },
                modifier = Modifier
                  .align(Alignment.CenterHorizontally)
                  .padding(top = 4.dp)
                  .testTag("onboarding_skip_notifications")
              ) {
                Text(
                  text = "Maybe later",
                  fontSize = 14.sp,
                  color = colors.ink2
                )
              }
            } else if (currentStep == 16) {
              TextButton(
                onClick = { handleContinue() },
                modifier = Modifier
                  .align(Alignment.CenterHorizontally)
                  .padding(top = 4.dp)
                  .testTag("onboarding_skip_referral")
              ) {
                Text(
                  text = "I don't have a code",
                  fontSize = 14.sp,
                  color = colors.ink2
                )
              }
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 0: Welcome / Hook with Animated Looping Demo
// -------------------------------------------------------------
@Composable
private fun Step0Welcome(
  onGetStarted: () -> Unit,
  onLoginClick: () -> Unit
) {
  val colors = CalTheme.colors
  val infiniteTransition = rememberInfiniteTransition(label = "thali_scan")
  val scanLaserOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_offset"
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    // Official CalSnapix Logo (Uploaded Brand Asset)
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = Color(0xFF0F172A),
      border = BorderStroke(2.dp, Color(0xFF10B981).copy(alpha = 0.6f)),
      shadowElevation = 6.dp,
      modifier = Modifier.padding(bottom = 12.dp)
    ) {
      Image(
        painter = painterResource(R.drawable.ic_calsnapix_logo),
        contentDescription = "CalSnapix Logo",
        modifier = Modifier
          .size(72.dp)
          .clip(RoundedCornerShape(22.dp))
      )
    }

    // Powered by MyCal AI glowing pill
    Surface(
      shape = RoundedCornerShape(999.dp),
      color = Color(0xFF10B981).copy(alpha = 0.12f),
      border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.45f)),
      modifier = Modifier.padding(bottom = 14.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = Color(0xFF10B981),
          modifier = Modifier.size(15.dp)
        )
        Text(
          text = "Next-Gen Indian Nutrition • Powered by MyCal AI",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF10B981)
        )
      }
    }

    // Animated multi-colored scanning demo card with glowing border
    val cardGradient = Brush.sweepGradient(
      colors = listOf(
        Color(0xFF10B981),
        Color(0xFF6366F1),
        Color(0xFFF59E0B),
        Color(0xFFEC4899),
        Color(0xFF10B981)
      )
    )

    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .height(270.dp),
      shape = RoundedCornerShape(26.dp),
      color = colors.surface,
      border = BorderStroke(2.dp, cardGradient)
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        // Floating decorative soft glow circles
        Box(
          modifier = Modifier
            .size(190.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(
                  Color(0xFF10B981).copy(alpha = 0.18f),
                  Color(0xFF6366F1).copy(alpha = 0.08f),
                  Color.Transparent
                )
              )
            )
        )

        // Indian Thali illustration mockup
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(150.dp)
              .clip(CircleShape)
              .background(colors.surface2)
              .border(2.dp, Color(0xFFE2E8F0).copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Canvas(modifier = Modifier.size(126.dp)) {
              // Plate rim
              drawCircle(color = Color(0xFFD4AF37), style = Stroke(width = 5.dp.toPx()))
              // Dal katori (rich golden yellow)
              drawCircle(color = Color(0xFFF59E0B), radius = 22.dp.toPx(), center = Offset(size.width * 0.35f, size.height * 0.35f))
              // Paneer / Sabzi katori (rich masala red)
              drawCircle(color = Color(0xFFEF4444), radius = 22.dp.toPx(), center = Offset(size.width * 0.68f, size.height * 0.38f))
              // Rotis (warm wheat)
              drawCircle(color = Color(0xFFD97706).copy(alpha = 0.8f), radius = 30.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.7f))
            }
          }
        }

        // Camera laser beam (vibrant animated green & white)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .padding(horizontal = 20.dp)
            .align(Alignment.TopCenter)
            .padding(top = (45 + 165 * scanLaserOffset).dp)
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  Color.Transparent,
                  Color(0xFF10B981),
                  Color.White,
                  Color(0xFF6366F1),
                  Color.Transparent
                )
              )
            )
        )

        // Floating colorful nutrition badges
        // Top Left: High Protein pill
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = Color(0xFF6366F1),
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(14.dp)
            .shadow(4.dp, RoundedCornerShape(999.dp))
        ) {
          Text(
            text = "💪 28g Protein",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
          )
        }

        // Top Right: Fiber & Carbs pill
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = Color(0xFFF59E0B),
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(14.dp)
            .shadow(4.dp, RoundedCornerShape(999.dp))
        ) {
          Text(
            text = "🌾 Slow Carbs",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
          )
        }

        // Live popping bottom nutrition badge
        Surface(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 14.dp)
            .shadow(8.dp, RoundedCornerShape(999.dp)),
          shape = RoundedCornerShape(999.dp),
          color = Color(0xFF0F172A),
          border = BorderStroke(1.dp, Color(0xFF10B981))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = Color(0xFF10B981),
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "2 Rotis & Dal Tadka • 380 kcal",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(28.dp))

    // Animated Colorful Headline & Tagline
    Text(
      text = "Eat Indian.\nTrack effortlessly.",
      fontSize = 34.sp,
      lineHeight = 38.sp,
      fontWeight = FontWeight.ExtraBold,
      color = colors.ink,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "Snap. Know. Eat smart. The only calorie & macro tracker calibrated for rotis, dals, sabzis, and ghar ka khana.",
      fontSize = 15.sp,
      lineHeight = 22.sp,
      color = colors.ink2,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(40.dp))

    // Get Started Button
    Button(
      onClick = onGetStarted,
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .testTag("onboarding_get_started_button"),
      shape = RoundedCornerShape(999.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = colors.ink,
        contentColor = colors.bg
      )
    ) {
      Text(
        text = "Get Started",
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Already have an account? Log in
    TextButton(
      onClick = onLoginClick,
      modifier = Modifier.testTag("onboarding_already_have_account_button")
    ) {
      Text(
        text = "Already have an account? Log in",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = colors.accent
      )
    }
  }
}

// -------------------------------------------------------------
// STEP 1: Gender
// -------------------------------------------------------------
@Composable
private fun Step1Gender(
  selectedGender: String,
  onSelectGender: (String) -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "What is your biological sex?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Used to calibrate your baseline metabolic rate (BMR) and daily expenditure accurately.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(28.dp))

    val options = listOf(
      Triple("female", "Female", "Calibrates for female hormonal rhythm & BMR"),
      Triple("male", "Male", "Calibrates for male lean muscle mass & BMR"),
      Triple("other", "Other", "Calculates balanced standard metabolic formula")
    )

    options.forEach { (key, label, desc) ->
      val isSelected = selectedGender.equals(key, ignoreCase = true)
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
          .clip(RoundedCornerShape(20.dp))
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onSelectGender(key)
          }
          .border(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) colors.ink else colors.line,
            shape = RoundedCornerShape(20.dp)
          )
          .testTag("gender_option_$key"),
        color = if (isSelected) colors.surface2 else colors.surface,
        shape = RoundedCornerShape(20.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = label,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = desc,
              fontSize = 13.sp,
              color = colors.ink2
            )
          }
          if (isSelected) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(colors.ink),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.bg,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 2: Workout Frequency
// -------------------------------------------------------------
@Composable
private fun Step2WorkoutFrequency(
  selectedFreq: String,
  onSelectFreq: (String) -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "How often do you exercise?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Helps determine active calorie expenditure and target protein requirements.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(28.dp))

    val freqList = listOf(
      Triple("0_2", "0–2 days / week", "Gentle walks, light stretching, mostly relaxed"),
      Triple("3_5", "3–5 days / week", "Regular gym workouts, yoga, running, or badminton"),
      Triple("6+", "6+ days / week", "Intense daily fitness training, athlete, heavy lifting")
    )

    freqList.forEach { (key, label, desc) ->
      val isSelected = selectedFreq == key
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
          .clip(RoundedCornerShape(20.dp))
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onSelectFreq(key)
          }
          .border(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) colors.ink else colors.line,
            shape = RoundedCornerShape(20.dp)
          )
          .testTag("workout_freq_$key"),
        color = if (isSelected) colors.surface2 else colors.surface,
        shape = RoundedCornerShape(20.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = label,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = desc,
              fontSize = 13.sp,
              color = colors.ink2
            )
          }
          if (isSelected) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(colors.ink),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.bg,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 3: Where did you hear about us?
// -------------------------------------------------------------
@Composable
private fun Step3HearAboutUs(
  selectedSource: String,
  onSelectSource: (String) -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Where did you hear about us?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Help us understand how health-conscious Indians discover CalSnapix.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(24.dp))

    val sources = listOf("Instagram", "YouTube", "Friend / Family", "Google", "Gym / Trainer", "Other")

    sources.forEach { source ->
      val isSelected = selectedSource == source
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
          .clip(RoundedCornerShape(16.dp))
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onSelectSource(source)
          }
          .border(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) colors.ink else colors.line,
            shape = RoundedCornerShape(16.dp)
          )
          .testTag("hear_about_us_$source"),
        color = if (isSelected) colors.surface2 else colors.surface,
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = source,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = colors.ink
          )
          if (isSelected) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = colors.ink,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 4: Have you tried other calorie-tracking apps?
// -------------------------------------------------------------
@Composable
private fun Step4TriedOtherApps(
  tried: Boolean?,
  onSelect: (Boolean) -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Have you tried other calorie apps?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Most trackers fail because Western databases don't understand Indian dishes.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(28.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Yes card
      Surface(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(20.dp))
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onSelect(true)
          }
          .border(
            width = if (tried == true) 2.dp else 1.dp,
            color = if (tried == true) colors.ink else colors.line,
            shape = RoundedCornerShape(20.dp)
          )
          .testTag("tried_apps_yes"),
        color = if (tried == true) colors.surface2 else colors.surface,
        shape = RoundedCornerShape(20.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Yes",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "I've tracked before",
            fontSize = 12.sp,
            color = colors.ink2,
            textAlign = TextAlign.Center
          )
        }
      }

      // No card
      Surface(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(20.dp))
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onSelect(false)
          }
          .border(
            width = if (tried == false) 2.dp else 1.dp,
            color = if (tried == false) colors.ink else colors.line,
            shape = RoundedCornerShape(20.dp)
          )
          .testTag("tried_apps_no"),
        color = if (tried == false) colors.surface2 else colors.surface,
        shape = RoundedCornerShape(20.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "No",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Brand new to tracking",
            fontSize = 12.sp,
            color = colors.ink2,
            textAlign = TextAlign.Center
          )
        }
      }
    }

    // If Yes -> show the promised one-liner!
    AnimatedVisibility(visible = tried == true) {
      Column {
        Spacer(modifier = Modifier.height(24.dp))
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          color = colors.accentSoft
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = colors.accent,
              modifier = Modifier.size(24.dp)
            )
            Text(
              text = "CalSnapix is built for Indian food — dal, roti, sabzi, biryani — so you won't have to guess.",
              fontSize = 14.sp,
              lineHeight = 20.sp,
              fontWeight = FontWeight.Medium,
              color = colors.ink
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 5: Social Proof Interstitial (count-up animation)
// -------------------------------------------------------------
@Composable
private fun Step5SocialProof() {
  val colors = CalTheme.colors
  // TODO: real stats from production analytics
  val mealsLoggedTarget = 45000
  val successRateTarget = 94

  val animatedMeals by animateFloatAsState(
    targetValue = mealsLoggedTarget.toFloat(),
    animationSpec = tween(1200, easing = FastOutSlowInEasing),
    label = "stat_meals"
  )
  val animatedSuccess by animateFloatAsState(
    targetValue = successRateTarget.toFloat(),
    animationSpec = tween(1200, easing = FastOutSlowInEasing),
    label = "stat_success"
  )

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Thousands of Indians already tracking",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "From Mumbai to Delhi, CalSnapix simplifies home-cooked nutrition with scientific precision.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(32.dp))

    // Stat 1: Meals logged
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          text = "${animatedMeals.toInt().toString().reversed().chunked(3).joinToString(",").reversed()}+",
          fontSize = 38.sp,
          fontWeight = FontWeight.Bold,
          color = colors.accent,
          letterSpacing = (-0.5).sp
        )
        Text(
          text = "Indian meals scanned and logged today",
          fontSize = 14.sp,
          color = colors.ink,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // Stat 2: Success rate
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          text = "${animatedSuccess.toInt()}%",
          fontSize = 38.sp,
          fontWeight = FontWeight.Bold,
          color = colors.protein,
          letterSpacing = (-0.5).sp
        )
        Text(
          text = "Of users reached their target weight in 12 weeks",
          fontSize = 14.sp,
          color = colors.ink,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // Stat 3: App Store rating
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line)
    ) {
      Row(
        modifier = Modifier.padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "4.9 ★★★★★",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.carbs
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Top-rated by fitness coaches & nutritionists in India",
            fontSize = 13.sp,
            color = colors.ink2
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 6: Height & Weight (Two wheel pickers side-by-side)
// -------------------------------------------------------------
@Composable
private fun Step6HeightWeight(
  heightCm: Int,
  weightKg: Float,
  onHeightChange: (Int) -> Unit,
  onWeightChange: (Float) -> Unit
) {
  val colors = CalTheme.colors
  val heightM = (heightCm / 100f).coerceAtLeast(0.5f)
  val bmi = weightKg / (heightM * heightM)

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Your height & weight",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Swipe dials to set your body stats. Toggle between cm/ft and kg/lb anytime.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(24.dp))

    HeightWeightPickerRow(
      heightCm = heightCm,
      weightKg = weightKg,
      onHeightChanged = onHeightChange,
      onWeightChanged = onWeightChange
    )

    Spacer(modifier = Modifier.height(20.dp))

    // BMI Indicator Chip
    Surface(
      modifier = Modifier.align(Alignment.CenterHorizontally),
      shape = RoundedCornerShape(999.dp),
      color = colors.surface2
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = "Starting BMI:",
          fontSize = 13.sp,
          color = colors.ink2
        )
        Text(
          text = String.format(java.util.Locale.US, "%.1f", bmi),
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )
        Text(
          text = when {
            bmi < 18.5 -> "(Underweight)"
            bmi < 24.9 -> "(Normal)"
            bmi < 29.9 -> "(Overweight)"
            else -> "(Obese)"
          },
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = if (bmi in 18.5..24.9) colors.success else colors.warn
        )
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 7: Age Wheel Picker
// -------------------------------------------------------------
@Composable
private fun Step7Age(
  age: Int,
  onAgeChange: (Int) -> Unit
) {
  val colors = CalTheme.colors
  val ageList = remember { (13..95).toList() }

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "How old are you?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink,
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Metabolism changes with age. We calibrate your daily energy requirements to match.",
      fontSize = 15.sp,
      color = colors.ink2,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(36.dp))

    val selectedIndex = ageList.indexOf(age).coerceAtLeast(0)

    WheelPicker(
      items = ageList,
      selectedIndex = selectedIndex,
      onItemSelected = { _, selectedAge -> onAgeChange(selectedAge) },
      unitLabel = "years old",
      modifier = Modifier.fillMaxWidth(0.7f),
      testTag = "age_wheel_picker"
    )
  }
}

// -------------------------------------------------------------
// STEP 8: Primary Goal
// -------------------------------------------------------------
@Composable
private fun Step8PrimaryGoal(
  selectedGoalId: String,
  onSelectGoal: (String) -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "What is your primary goal?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "We will structure your daily calorie deficit and macro targets around this choice.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(24.dp))

    val goals = listOf(
      Triple("lose_weight", "Lose Weight & Tone", "Shed fat in a sustainable deficit with rotis, dals, and sabzis."),
      Triple("mindful_eating", "Maintain & Vitality", "Eat with Ayurvedic circadian rhythms and maintain optimal BMI."),
      Triple("gain_weight", "Gain Healthy Weight", "Nutrient-dense Indian surplus with ghee, nuts, paneer & sattu."),
      Triple("build_muscle", "Build Muscle & High Protein", "Hit 80–120g clean daily protein with Indian staples."),
      Triple("manage_health", "Eat Healthier & Balance Gut", "Low glycemic, gut-friendly Indian meal plans designed by dietitians.")
    )

    goals.forEach { (id, title, desc) ->
      val isSelected = selectedGoalId == id
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
          .clip(RoundedCornerShape(18.dp))
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onSelectGoal(id)
          }
          .border(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) colors.ink else colors.line,
            shape = RoundedCornerShape(18.dp)
          )
          .testTag("primary_goal_$id"),
        color = if (isSelected) colors.surface2 else colors.surface,
        shape = RoundedCornerShape(18.dp)
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = title,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = desc,
              fontSize = 13.sp,
              color = colors.ink2
            )
          }
          if (isSelected) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(colors.ink),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.bg,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 9: Target Weight Wheel Picker
// -------------------------------------------------------------
@Composable
private fun Step9TargetWeight(
  currentWeightKg: Float,
  targetWeightKg: Float,
  onTargetWeightChange: (Float) -> Unit
) {
  val colors = CalTheme.colors
  val weightList = remember { (35..160).map { it.toFloat() } }
  val diff = targetWeightKg - currentWeightKg

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "What is your target weight?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink,
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Scroll to select your desired body weight.",
      fontSize = 15.sp,
      color = colors.ink2,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(28.dp))

    val selectedIndex = weightList.indexOfFirst { abs(it - targetWeightKg) < 0.6f }.coerceAtLeast(0)

    WheelPicker(
      items = weightList,
      selectedIndex = selectedIndex,
      onItemSelected = { _, kg -> onTargetWeightChange(kg) },
      itemLabel = { kg -> String.format(java.util.Locale.US, "%.1f", kg) },
      unitLabel = "kg",
      modifier = Modifier.fillMaxWidth(0.7f),
      testTag = "target_weight_wheel_picker"
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Live dynamic line
    Surface(
      shape = RoundedCornerShape(999.dp),
      color = colors.accentSoft
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = colors.accent,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = when {
            diff < -0.1f -> "You'll lose ${String.format(java.util.Locale.US, "%.1f", abs(diff))} kg"
            diff > 0.1f -> "You'll gain ${String.format(java.util.Locale.US, "%.1f", diff)} kg"
            else -> "Maintain your healthy weight"
          },
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 10: Motivational Interstitial
// -------------------------------------------------------------
@Composable
private fun Step10Motivational(
  currentWeightKg: Float,
  targetWeightKg: Float
) {
  val colors = CalTheme.colors
  val diff = abs(targetWeightKg - currentWeightKg)

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "A realistic target.",
      fontSize = 32.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = if (targetWeightKg < currentWeightKg) {
        "Losing ${String.format(java.util.Locale.US, "%.1f", diff)} kg is completely realistic. It's not hard at all with a personalized Indian meal plan!"
      } else {
        "Gaining ${String.format(java.util.Locale.US, "%.1f", diff)} kg of lean mass is a great goal. We will guide every step."
      },
      fontSize = 16.sp,
      lineHeight = 24.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(32.dp))

    // Projected timeline curve chart
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp),
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line)
    ) {
      Box(modifier = Modifier.padding(20.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val path = Path().apply {
            moveTo(0f, if (targetWeightKg < currentWeightKg) size.height * 0.2f else size.height * 0.8f)
            cubicTo(
              size.width * 0.4f,
              if (targetWeightKg < currentWeightKg) size.height * 0.25f else size.height * 0.75f,
              size.width * 0.6f,
              if (targetWeightKg < currentWeightKg) size.height * 0.65f else size.height * 0.35f,
              size.width,
              if (targetWeightKg < currentWeightKg) size.height * 0.8f else size.height * 0.2f
            )
          }
          drawPath(
            path = path,
            color = colors.accent,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
          )

          // Starting point circle
          drawCircle(
            color = colors.ink,
            radius = 6.dp.toPx(),
            center = Offset(0f, if (targetWeightKg < currentWeightKg) size.height * 0.2f else size.height * 0.8f)
          )
          // Target point circle
          drawCircle(
            color = colors.accent,
            radius = 8.dp.toPx(),
            center = Offset(size.width, if (targetWeightKg < currentWeightKg) size.height * 0.8f else size.height * 0.2f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "Today (${currentWeightKg.toInt()} kg)", fontSize = 12.sp, color = colors.ink2)
          Text(text = "Target (${targetWeightKg.toInt()} kg)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.accent)
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 11: Pace Slider (Tortoise -> Rabbit)
// -------------------------------------------------------------
@Composable
private fun Step11PaceSlider(
  paceKgPerWeek: Float,
  onPaceChange: (Float) -> Unit
) {
  val colors = CalTheme.colors

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "How fast do you want to reach it?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "A steady pace ensures you burn pure fat while enjoying daily rotis and dals without feeling hungry.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(36.dp))

    // Display Current Pace Big
    Text(
      text = "${String.format(java.util.Locale.US, "%.2f", paceKgPerWeek)} kg",
      fontSize = 44.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink,
      modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Text(
      text = "per week",
      fontSize = 15.sp,
      color = colors.ink2,
      modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Spacer(modifier = Modifier.height(28.dp))

    // Tortoise and Rabbit SVG icons row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Tortoise indicator
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.SelfImprovement,
          contentDescription = "Slow pace",
          tint = if (paceKgPerWeek < 0.45f) colors.accent else colors.ink2,
          modifier = Modifier.size(28.dp)
        )
        Text("Gentle", fontSize = 11.sp, color = colors.ink2)
      }

      // Rabbit indicator
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.DirectionsRun,
          contentDescription = "Fast pace",
          tint = if (paceKgPerWeek > 0.75f) colors.accent else colors.ink2,
          modifier = Modifier.size(28.dp)
        )
        Text("Fast", fontSize = 11.sp, color = colors.ink2)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Smooth horizontal slider
    Slider(
      value = paceKgPerWeek,
      onValueChange = { onPaceChange((it * 20).roundToInt() / 20f) }, // snap in 0.05 increments
      valueRange = 0.25f..1.0f,
      colors = SliderDefaults.colors(
        thumbColor = colors.ink,
        activeTrackColor = colors.accent,
        inactiveTrackColor = colors.line
      ),
      modifier = Modifier.fillMaxWidth().testTag("pace_slider")
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Recommended Range Label
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = colors.surface2,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = colors.accent,
          modifier = Modifier.size(20.dp)
        )
        Text(
          text = "Recommended: 0.50 kg / week for sustainable fat loss with Indian meals.",
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          color = colors.ink
        )
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 12: Biggest Obstacles (Multi-select)
// -------------------------------------------------------------
@Composable
private fun Step12BiggestObstacles(
  selectedObstacles: Set<String>,
  onToggleObstacle: (String) -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "What are your biggest obstacles?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Select all that apply. We tailor our tracking habits to overcome them.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(24.dp))

    val obstacles = listOf(
      "lack_of_consistency" to "Lack of consistency with diets",
      "unhealthy_eating_habits" to "Unhealthy cravings & late-night snacking",
      "lack_of_support" to "Lack of support & guidance",
      "busy_schedule" to "Busy schedule & long work hours",
      "lack_of_meal_inspiration" to "Lack of healthy Indian meal inspiration"
    )

    obstacles.forEach { (key, label) ->
      val isSelected = selectedObstacles.contains(key)
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
          .clip(RoundedCornerShape(16.dp))
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onToggleObstacle(key)
          }
          .border(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) colors.ink else colors.line,
            shape = RoundedCornerShape(16.dp)
          )
          .testTag("obstacle_$key"),
        color = if (isSelected) colors.surface2 else colors.surface,
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = colors.ink,
            modifier = Modifier.weight(1f)
          )
          if (isSelected) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(colors.ink),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.bg,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 13: Diet Type (Multi-select)
// -------------------------------------------------------------
@Composable
private fun Step13DietType(
  selectedDiets: Set<String>,
  onToggleDiet: (String) -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "What is your diet type?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Select all that apply. We filter recipes & AI food recognition accordingly.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(24.dp))

    val diets = listOf(
      "veg" to "Vegetarian (Lacto-vegetarian)",
      "eggetarian" to "Eggetarian (Veg + Eggs)",
      "non_veg" to "Non-Veg (Chicken, fish, mutton)",
      "vegan" to "Vegan (100% plant based)",
      "jain" to "Jain (No onion, garlic, or root veggies)",
      "sattvic" to "Sattvic (Ayurvedic fresh & mild)"
    )

    diets.forEach { (key, label) ->
      val isSelected = selectedDiets.contains(key)
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
          .clip(RoundedCornerShape(16.dp))
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onToggleDiet(key)
          }
          .border(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) colors.ink else colors.line,
            shape = RoundedCornerShape(16.dp)
          )
          .testTag("diet_type_$key"),
        color = if (isSelected) colors.surface2 else colors.surface,
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = colors.ink
          )
          if (isSelected) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(colors.ink),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.bg,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 14: Regional Cuisine (Multi-select)
// -------------------------------------------------------------
@Composable
private fun Step14RegionalCuisine(
  selectedRegions: Set<String>,
  onToggleRegion: (String) -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Which regional cuisines do you cook?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Helps CalSnapix understand your staple spices, cooking oils, and gravy textures.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(24.dp))

    val regions = listOf(
      "north" to ("North Indian" to "Dal Tadka, Roti, Rajma, Sabzi"),
      "south" to ("South Indian" to "Idli, Sambar, Dosa, Rasam, Rice"),
      "west" to ("West Indian" to "Poha, Thepla, Gujarati Dal, Dhokla"),
      "east" to ("East Indian" to "Fish curry, Dalma, Pakhala"),
      "pan" to ("Pan-Indian" to "A diverse mix of regional specialties")
    )

    regions.forEach { (key, pair) ->
      val (title, subtitle) = pair
      val isSelected = selectedRegions.contains(key)
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
          .clip(RoundedCornerShape(16.dp))
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onToggleRegion(key)
          }
          .border(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) colors.ink else colors.line,
            shape = RoundedCornerShape(16.dp)
          )
          .testTag("region_$key"),
        color = if (isSelected) colors.surface2 else colors.surface,
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = title,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = subtitle,
              fontSize = 12.sp,
              color = colors.ink2
            )
          }
          if (isSelected) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(colors.ink),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.bg,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 15: Allergies / Avoid (Multi-select chips)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step15Allergies(
  selectedAllergies: Set<String>,
  onToggleAllergy: (String) -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Any allergies or foods to avoid?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "We will warn you when meals contain these ingredients during camera scans.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(28.dp))

    val allergyOptions = listOf("Gluten", "Lactose", "Nuts", "High sodium", "None")

    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      allergyOptions.forEach { option ->
        val isSelected = selectedAllergies.contains(option)
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              onToggleAllergy(option)
            }
            .border(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) colors.ink else colors.line,
              shape = RoundedCornerShape(999.dp)
            )
            .testTag("allergy_$option"),
          color = if (isSelected) colors.ink else colors.surface,
          shape = RoundedCornerShape(999.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.bg,
                modifier = Modifier.size(16.dp)
              )
            }
            Text(
              text = option,
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (isSelected) colors.bg else colors.ink
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 16: Activity Level in Daily Life
// -------------------------------------------------------------
@Composable
private fun Step16ActivityLevel(
  selectedLevel: String,
  onSelectLevel: (String) -> Unit
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Your daily activity level",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Excluding gym or workouts. This accounts for your Non-Exercise Activity (NEAT).",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(24.dp))

    val levels = listOf(
      Triple("sedentary", "Sedentary", "Desk job, mostly sitting, under 4,000 steps"),
      Triple("light", "Light", "Walking around office, errands, 5,000–7,000 steps"),
      Triple("moderate", "Moderate", "Standing on feet, retail, teaching, 8,000–11,000 steps"),
      Triple("very_active", "Very Active", "Heavy construction, delivery, sports, 12,000+ steps")
    )

    levels.forEach { (key, title, subtitle) ->
      val isSelected = selectedLevel == key
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
          .clip(RoundedCornerShape(18.dp))
          .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onSelectLevel(key)
          }
          .border(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) colors.ink else colors.line,
            shape = RoundedCornerShape(18.dp)
          )
          .testTag("activity_level_$key"),
        color = if (isSelected) colors.surface2 else colors.surface,
        shape = RoundedCornerShape(18.dp)
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = title,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = subtitle,
              fontSize = 12.sp,
              color = colors.ink2
            )
          }
          if (isSelected) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(colors.ink),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.bg,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 17: Meal Timings (Time Wheel Pickers)
// -------------------------------------------------------------
@Composable
private fun Step17MealTimings(
  breakfast: String,
  lunch: String,
  dinner: String,
  snack: String,
  onUpdateTiming: (meal: String, time: String) -> Unit
) {
  val colors = CalTheme.colors
  val timesList = remember {
    listOf(
      "7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM",
      "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM",
      "4:30 PM", "5:00 PM", "5:30 PM", "6:00 PM",
      "7:30 PM", "8:00 PM", "8:15 PM", "8:30 PM", "9:00 PM", "9:30 PM"
    )
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "When do you usually eat?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Maintains Ayurvedic circadian rhythm (Agni) so your metabolism digests each meal smoothly.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(20.dp))

    val meals = listOf(
      "Breakfast" to breakfast,
      "Lunch" to lunch,
      "Dinner" to dinner,
      "Snack" to snack
    )

    meals.forEach { (name, currentTime) ->
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.line)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Schedule,
              contentDescription = null,
              tint = colors.accent,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = name,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
          }

          // Compact Time Wheel selector
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = colors.surface2
          ) {
            Text(
              text = currentTime,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 18: Water Goal Wheel Picker
// -------------------------------------------------------------
@Composable
private fun Step18WaterGoal(
  waterGoalLiters: Float,
  onWaterGoalChange: (Float) -> Unit
) {
  val colors = CalTheme.colors
  val litersList = remember {
    (10..45).map { it / 10f } // 1.0 to 4.5 L
  }
  val glassesEquivalent = (waterGoalLiters * 4).toInt()

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "What is your water goal?",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink,
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Adequate hydration boosts resting metabolic rate and aids Indian spice digestion.",
      fontSize = 15.sp,
      color = colors.ink2,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(28.dp))

    val selectedIndex = litersList.indexOfFirst { abs(it - waterGoalLiters) < 0.08f }.coerceAtLeast(0)

    WheelPicker(
      items = litersList,
      selectedIndex = selectedIndex,
      onItemSelected = { _, liters -> onWaterGoalChange(liters) },
      itemLabel = { liters -> String.format(java.util.Locale.US, "%.1f", liters) },
      unitLabel = "L / day",
      modifier = Modifier.fillMaxWidth(0.6f),
      testTag = "water_wheel_picker"
    )

    Spacer(modifier = Modifier.height(20.dp))

    Surface(
      shape = RoundedCornerShape(999.dp),
      color = colors.accentSoft
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Opacity,
          contentDescription = null,
          tint = colors.accent,
          modifier = Modifier.size(18.dp)
        )
        Text(
          text = "Equal to ~$glassesEquivalent standard glasses (250ml)",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = colors.ink
        )
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 19: Notifications Permission
// -------------------------------------------------------------
@Composable
private fun Step19Notifications(
  notificationsAllowed: Boolean,
  onTogglePermission: (Boolean) -> Unit
) {
  val colors = CalTheme.colors

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Stay mindful with gentle nudges",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "We only send gentle reminders tailored to your meal windows and hydration targets.",
      fontSize = 15.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(32.dp))

    val notificationFeatures = listOf(
      Icons.Default.Schedule to ("Agni Digest Windows" to "Reminders at 8:30 AM & 1:30 PM for peak digestion"),
      Icons.Default.Opacity to ("Hydration Check-ins" to "Gentle water prompts spaced evenly across the day"),
      Icons.Default.AutoAwesome to ("Daily Indian Macro Summary" to "Evening recap of your calorie, protein, and carb balance")
    )

    notificationFeatures.forEach { (icon, pair) ->
      val (title, desc) = pair
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.line)
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(colors.accentSoft),
            contentAlignment = Alignment.Center
          ) {
            Icon(imageVector = icon, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.ink)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, fontSize = 12.sp, color = colors.ink2)
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 20: Rate Us / Trust Screen
// -------------------------------------------------------------
@Composable
private fun Step20Trust() {
  val colors = CalTheme.colors
  var selectedRating by remember { mutableIntStateOf(5) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Thank you for trusting us",
      fontSize = 30.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink,
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "CalSnapix is committed to privacy, accurate Indian food intelligence, and honest nutrition.",
      fontSize = 15.sp,
      color = colors.ink2,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(36.dp))

    // Interactive 5 stars
    Row(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      (1..5).forEach { star ->
        Icon(
          imageVector = Icons.Default.Star,
          contentDescription = "Star $star",
          tint = if (star <= selectedRating) colors.carbs else colors.line,
          modifier = Modifier
            .size(38.dp)
            .clickable { selectedRating = star }
        )
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Privacy Guarantee card
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line)
    ) {
      Row(
        modifier = Modifier.padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = null,
          tint = colors.accent,
          modifier = Modifier.size(32.dp)
        )
        Column {
          Text(
            text = "Your data stays private",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "We never sell your biometric data or meal history to third-party ad networks. Everything is securely encrypted.",
            fontSize = 13.sp,
            color = colors.ink2,
            lineHeight = 18.sp
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 21: Referral Code Screen with Instant Validation & Confetti
// -------------------------------------------------------------
@Composable
private fun Step21Referral(
  code: String,
  onCodeChange: (String) -> Unit,
  onApply: () -> Unit,
  onSkip: () -> Unit,
  isChecking: Boolean,
  successMessage: String?,
  errorMessage: String?,
  discountPercent: Int
) {
  val colors = CalTheme.colors
  val isValid = successMessage != null && errorMessage == null

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Have a referral code?",
      fontSize = 32.sp,
      lineHeight = 38.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Enter a friend's code to unlock up to 50% off CalSnapix Pro and start tracking ghar ka khana.",
      fontSize = 15.sp,
      lineHeight = 22.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(32.dp))

    // Single large input (auto-uppercase, 6-10 chars)
    OutlinedTextField(
      value = code,
      onValueChange = { input ->
        val filtered = input.filter { it.isLetterOrDigit() }.take(10).uppercase()
        onCodeChange(filtered)
      },
      placeholder = {
        Text(
          text = "YASH50",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp,
          color = colors.ink2.copy(alpha = 0.4f),
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center
        )
      },
      textStyle = androidx.compose.ui.text.TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.sp,
        textAlign = TextAlign.Center,
        color = colors.ink
      ),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = if (isValid) colors.accent else colors.ink,
        unfocusedBorderColor = if (isValid) colors.accent else colors.line,
        focusedContainerColor = colors.surface,
        unfocusedContainerColor = colors.surface
      ),
      shape = RoundedCornerShape(20.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(72.dp)
        .testTag("referral_code_input"),
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(onDone = {
        if (code.length in 6..10 && !isChecking) onApply()
      })
    )

    Spacer(modifier = Modifier.height(18.dp))

    // Apply Button
    Button(
      onClick = onApply,
      enabled = code.length in 6..10 && !isChecking && !isValid,
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("apply_referral_button"),
      shape = RoundedCornerShape(999.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = colors.accent,
        contentColor = colors.bg,
        disabledContainerColor = colors.line,
        disabledContentColor = colors.ink2.copy(alpha = 0.5f)
      )
    ) {
      if (isChecking) {
        CircularProgressIndicator(
          color = colors.bg,
          strokeWidth = 2.5.dp,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text("Checking code...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      } else if (isValid) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = colors.bg,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Applied Successfully", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      } else {
        Text("Apply", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
    }

    // Valid State: Confetti Burst & 50% OFF unlocked badge
    if (isValid) {
      Spacer(modifier = Modifier.height(20.dp))
      Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        // Subtle confetti particles burst
        ReferralConfettiBurst()

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = colors.accentSoft,
          border = BorderStroke(1.5.dp, colors.accent),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = colors.accent,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = colors.bg,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Column {
              Text(
                text = "${if (discountPercent > 0) discountPercent else 50}% OFF UNLOCKED",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.accent
              )
              Text(
                text = successMessage ?: "Referral discount will be applied at checkout.",
                fontSize = 13.sp,
                color = colors.ink
              )
            }
          }
        }
      }
    }

    // Invalid / Expired / Already Used State
    if (errorMessage != null) {
      Spacer(modifier = Modifier.height(16.dp))
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.danger.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, colors.danger.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = errorMessage,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          color = colors.danger,
          modifier = Modifier.padding(14.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Skip Button
    TextButton(
      onClick = onSkip,
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .testTag("referral_skip_button")
    ) {
      Text(
        text = if (isValid) "Continue with discount →" else "I don't have a code",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (isValid) colors.accent else colors.ink2
      )
    }
  }
}

@Composable
private fun ReferralConfettiBurst() {
  val animProgress = remember { Animatable(0f) }
  LaunchedEffect(Unit) {
    animProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )
  }

  val colors = listOf(Color(0xFF0F5C47), Color(0xFFD4AF37), Color(0xFFEE8A6D), Color(0xFF4A90E2))
  Canvas(
    modifier = Modifier
      .fillMaxWidth()
      .height(80.dp)
  ) {
    val progress = animProgress.value
    if (progress < 1f) {
      val center = Offset(size.width / 2f, size.height / 2f)
      for (i in 0 until 18) {
        val angle = (i * 20) * (Math.PI / 180.0)
        val distance = (30 + i * 4) * progress * 2.5f
        val x = center.x + (distance * kotlin.math.cos(angle)).toFloat()
        val y = center.y + (distance * kotlin.math.sin(angle)).toFloat()
        val alpha = (1f - progress).coerceIn(0f, 1f)
        drawCircle(
          color = colors[i % colors.size].copy(alpha = alpha),
          radius = (4f * (1f - progress * 0.5f)),
          center = Offset(x, y)
        )
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 22: "Setting everything up..." Loading screen (4-5s)
// -------------------------------------------------------------
@Composable
private fun Step22SettingUp(
  onFinished: () -> Unit
) {
  val colors = CalTheme.colors
  var percent by remember { mutableIntStateOf(0) }
  var statusIndex by remember { mutableIntStateOf(0) }

  val statuses = listOf(
    "Calculating your Basal Metabolic Rate (BMR)...",
    "Balancing macronutrient ratios for Indian meals...",
    "Calibrating circadian Ayurvedic meal timings...",
    "Building your personalized Ahaar Blueprint..."
  )

  LaunchedEffect(Unit) {
    for (i in 1..100) {
      delay(40)
      percent = i
      if (i == 25) statusIndex = 1
      if (i == 55) statusIndex = 2
      if (i == 85) statusIndex = 3
    }
    delay(400)
    onFinished()
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(top = 40.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Big percentage counter
    Text(
      text = "$percent%",
      fontSize = 64.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink,
      letterSpacing = (-1.5).sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Rotating status lines
    Text(
      text = statuses[statusIndex],
      fontSize = 16.sp,
      fontWeight = FontWeight.SemiBold,
      color = colors.accent,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 24.dp)
    )

    Spacer(modifier = Modifier.height(40.dp))

    // Checklist ticking one by one
    val checklist = listOf(
      "Biometrics and metabolic baseline" to (percent >= 25),
      "Indian food database calibrated" to (percent >= 55),
      "Ayurvedic digestion windows balanced" to (percent >= 85),
      "Custom CalSnapix blueprint ready" to (percent >= 100)
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      checklist.forEach { (text, isChecked) ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (isChecked) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(colors.accent),
              contentAlignment = Alignment.Center
            ) {
              Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = colors.bg, modifier = Modifier.size(16.dp))
            }
          } else {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .border(1.5.dp, colors.line, CircleShape)
            )
          }

          Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isChecked) colors.ink else colors.ink2
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 23: "Your plan is ready" Result screen
// -------------------------------------------------------------
@Composable
private fun Step23PlanReady(
  uiState: AhaarUiState,
  targetWeightKg: Float,
  onEditPlan: () -> Unit
) {
  val colors = CalTheme.colors

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Your plan is ready!",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        color = colors.ink
      )
      TextButton(onClick = onEditPlan) {
        Text("Edit", color = colors.accent, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "Calibrated specifically for your metabolism and Indian dietary preferences.",
      fontSize = 14.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Big daily calorie card
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      shadowElevation = 2.dp
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "DAILY CALORIE TARGET",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink2,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "${uiState.dailyCalorieTarget}",
          fontSize = 52.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink,
          letterSpacing = (-1.5).sp
        )
        Text(
          text = "kcal / day",
          fontSize = 15.sp,
          color = colors.accent,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 3 Macro rings/pills (Protein, Carbs, Fat)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Protein
          Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface2
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("Protein", fontSize = 11.sp, color = colors.ink2)
              Text("90g", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.protein)
            }
          }
          // Carbs
          Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface2
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("Carbs", fontSize = 11.sp, color = colors.ink2)
              Text("210g", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.carbs)
            }
          }
          // Fat
          Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface2
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("Fats", fontSize = 11.sp, color = colors.ink2)
              Text("55g", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.fat)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Goal Date card
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      color = colors.accentSoft
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = colors.accent)
        Column {
          Text(
            text = "Reach ${targetWeightKg.toInt()} kg target in ~12 weeks",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink
          )
          Text(
            text = "Health Score: 8.8 / 10 • Excellent macro distribution",
            fontSize = 12.sp,
            color = colors.ink2
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Plain-language clinical insights
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.line)
      ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
          Text("🌾 ", fontSize = 16.sp)
          Text(
            text = "Your plan allows 2 rotis per meal without exceeding daily carbohydrate limits.",
            fontSize = 13.sp,
            color = colors.ink
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.line)
      ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
          Text("☀️ ", fontSize = 16.sp)
          Text(
            text = "Peak digestion (Agni) window set at 1:30 PM for lunch to maximize nutrient uptake.",
            fontSize = 13.sp,
            color = colors.ink
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// -------------------------------------------------------------
// STEP 24: Create Account / Sign In Dual Screen
// -------------------------------------------------------------
@Composable
private fun Step24CreateAccount(
  name: String,
  email: String,
  password: String,
  isPasswordVisible: Boolean,
  isSignUpMode: Boolean,
  onToggleMode: (Boolean) -> Unit,
  onNameChange: (String) -> Unit,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onTogglePasswordVisibility: () -> Unit,
  onGoogleSignIn: () -> Unit,
  errorMessage: String?,
  isLoading: Boolean
) {
  val colors = CalTheme.colors
  var showTermsDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Official CalSnapix Logo
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center
    ) {
      Surface(
        shape = CircleShape,
        color = Color(0xFF0F172A),
        border = BorderStroke(2.dp, colors.accent),
        shadowElevation = 4.dp,
        modifier = Modifier.padding(bottom = 12.dp)
      ) {
        Image(
          painter = painterResource(R.drawable.ic_calsnapix_logo),
          contentDescription = "CalSnapix Logo",
          modifier = Modifier.size(64.dp).clip(CircleShape)
        )
      }
    }

    Text(
      text = if (isSignUpMode) "Save your personalized plan" else "Welcome back to CalSnapix",
      fontSize = 28.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = if (isSignUpMode)
        "Create your account so your target calories, meals, and streak never get lost."
      else
        "Sign in to access your saved Indian nutrition plan and daily logs.",
      fontSize = 14.sp,
      color = colors.ink2
    )

    Spacer(modifier = Modifier.height(18.dp))

    // Prominent Dual Segmented Switch: Sign Up vs Sign In
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = colors.surface,
      border = BorderStroke(1.2.dp, colors.line),
      modifier = Modifier.fillMaxWidth().testTag("onboarding_auth_mode_toggle")
    ) {
      Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isSignUpMode) colors.accent else Color.Transparent,
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggleMode(true) }
            .testTag("onboarding_tab_signup")
        ) {
          Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
            Text(
              text = "Sign Up",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = if (isSignUpMode) Color.White else colors.ink2
            )
          }
        }
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (!isSignUpMode) colors.accent else Color.Transparent,
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggleMode(false) }
            .testTag("onboarding_tab_signin")
        ) {
          Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
            Text(
              text = "Sign In",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = if (!isSignUpMode) Color.White else colors.ink2
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Google Sign-In button
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .clip(RoundedCornerShape(999.dp))
        .clickable { onGoogleSignIn() }
        .border(1.dp, colors.line, RoundedCornerShape(999.dp))
        .testTag("onboarding_google_signin_button"),
      color = colors.surface,
      shape = RoundedCornerShape(999.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text("G ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.protein)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isSignUpMode) "Sign up with Google" else "Sign in with Google",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(modifier = Modifier.weight(1f).height(1.dp).background(colors.line))
      Text(" or with email ", fontSize = 12.sp, color = colors.ink2, modifier = Modifier.padding(horizontal = 8.dp))
      Box(modifier = Modifier.weight(1f).height(1.dp).background(colors.line))
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Only show Name field on Sign Up
    if (isSignUpMode) {
      OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Your Name") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = colors.ink,
          unfocusedBorderColor = colors.line
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().testTag("onboarding_signup_name")
      )

      Spacer(modifier = Modifier.height(12.dp))
    }

    OutlinedTextField(
      value = email,
      onValueChange = onEmailChange,
      label = { Text("Email Address") },
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colors.ink,
        unfocusedBorderColor = colors.line
      ),
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier.fillMaxWidth().testTag("onboarding_signup_email")
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
      value = password,
      onValueChange = onPasswordChange,
      label = { Text("Password (min 6 characters)") },
      singleLine = true,
      visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      trailingIcon = {
        IconButton(onClick = onTogglePasswordVisibility) {
          Icon(
            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
            contentDescription = null,
            tint = colors.ink2
          )
        }
      },
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colors.ink,
        unfocusedBorderColor = colors.line
      ),
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier.fillMaxWidth().testTag("onboarding_signup_password")
    )

    if (errorMessage != null) {
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = errorMessage,
        fontSize = 12.sp,
        color = colors.danger
      )
    }

    if (isLoading) {
      Spacer(modifier = Modifier.height(14.dp))
      CircularProgressIndicator(
        modifier = Modifier.align(Alignment.CenterHorizontally).size(24.dp),
        color = colors.ink
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Terms & Conditions and Privacy Policy Links (In Sign In / Sign Up)
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = "By creating an account or signing in, you agree to our",
        fontSize = 11.sp,
        color = colors.ink2
      )
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Terms & Conditions",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = colors.accent,
          modifier = Modifier
            .clickable { showTermsDialog = true }
            .padding(4.dp)
            .testTag("onboarding_terms_link")
        )
        Text(text = "•", color = colors.line, fontSize = 11.sp)
        Text(
          text = "Privacy Policy",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = colors.accent,
          modifier = Modifier
            .clickable { showPrivacyDialog = true }
            .padding(4.dp)
            .testTag("onboarding_privacy_link")
        )
      }
    }

    // Terms & Conditions Dialog
    if (showTermsDialog) {
      AlertDialog(
        onDismissRequest = { showTermsDialog = false },
        title = {
          Text(text = "CalSnapix Terms & Conditions", fontWeight = FontWeight.Bold, color = colors.ink)
        },
        text = {
          Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "Last Updated: October 2026\nApp: CalSnapix • Indian Nutrition & Wellness",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent
            )
            Text(
              text = "1. Acceptance of Terms:\nBy creating an account or signing in, you agree to these Terms. CalSnapix is intended for personal nutritional tracking and habit building.",
              fontSize = 12.sp,
              color = colors.ink,
              lineHeight = 17.sp
            )
            Text(
              text = "2. Non-Medical Disclaimer:\nCalSnapix provides AI-estimated calories, macronutrients, and Ayurvedic digestion windows for lifestyle wellness only. It is not clinical medical advice.",
              fontSize = 12.sp,
              color = colors.ink2,
              lineHeight = 17.sp
            )
            Text(
              text = "3. AI Meal Recognition:\nPhoto meal analysis and barcode queries provide guidance based on Indian and global food composition databases. Cooking styles and oils vary.",
              fontSize = 12.sp,
              color = colors.ink2,
              lineHeight = 17.sp
            )
            Text(
              text = "4. Data Ownership & Erasure:\nYou retain full ownership of your records. You may permanently delete your account and wipe all logs at any time from your profile.",
              fontSize = 12.sp,
              color = colors.ink2,
              lineHeight = 17.sp
            )
          }
        },
        confirmButton = {
          Button(
            onClick = { showTermsDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg)
          ) {
            Text("I Agree & Understand")
          }
        }
      )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
      AlertDialog(
        onDismissRequest = { showPrivacyDialog = false },
        title = {
          Text(text = "CalSnapix Privacy Policy", fontWeight = FontWeight.Bold, color = colors.ink)
        },
        text = {
          Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "Effective Date: October 2026\nApp: CalSnapix • Zero-Knowledge Nutrition Privacy",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent
            )
            Text(
              text = "1. Zero-Knowledge Privacy:\nWe do not sell, broker, or rent your biometric data, weight history, or meal photos to third parties or advertising networks.",
              fontSize = 12.sp,
              color = colors.ink,
              lineHeight = 17.sp
            )
            Text(
              text = "2. Information We Process:\n• Biometric inputs (age, weight, height) strictly to compute BMR & TDEE.\n• Meal logs & camera scans analyzed transiently for nutrient estimation.\n• Barcode numbers looked up against nutritional reference tables.",
              fontSize = 12.sp,
              color = colors.ink2,
              lineHeight = 17.sp
            )
            Text(
              text = "3. Data Security & Erasure:\nProtected with 256-bit encryption. Complete one-tap account deletion is permanently available under Profile > Delete Account.",
              fontSize = 12.sp,
              color = colors.ink2,
              lineHeight = 17.sp
            )
          }
        },
        confirmButton = {
          Button(
            onClick = { showPrivacyDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg)
          ) {
            Text("Close")
          }
        }
      )
    }
  }
}

// -------------------------------------------------------------
// -------------------------------------------------------------
// PRICING CONFIGURATION & STEP 25 PAYWALL
// -------------------------------------------------------------
// -------------------------------------------------------------
// STEP 20: Paywall (All 5 Plans with per-day highlight & Instagram discount)
// -------------------------------------------------------------
@Composable
private fun Step25Paywall(
  discountPercent: Int,
  onStartTrial: () -> Unit,
  onContinueFree: () -> Unit
) {
  val colors = CalTheme.colors

  Column(modifier = Modifier.fillMaxWidth()) {
    PricingPaywallContent(
      onSubscribeSuccess = { _ -> onStartTrial() },
      onDismiss = null
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Free Version Option
    TextButton(
      onClick = onContinueFree,
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .testTag("continue_free_version_button")
    ) {
      Text(
        text = "Or continue with Free Version",
        fontSize = 14.sp,
        color = colors.ink2,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
