package com.example.ui.screens.onboarding

import android.content.Context
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AhaarUiState
import androidx.compose.material3.OutlinedButton
import com.example.ui.components.HeightWeightPickerRow
import com.example.ui.components.IndianToranBorder
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.theme.CalTheme
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Professional 9-Step Onboarding Journey for CalSnapix:
 * 0: Welcome & The CalSnapix Promise (Hero Showcase)
 * 1: Primary Health Goal Selection
 * 2: Biological Context & Daily Activity Level
 * 3: Biometrics (Height, Current Weight & Age) with Real-Time BMI
 * 4: Target Weight & Safe Sustainable Pace (Risk-Audited with Underweight Blocker)
 * 5: Dietary Preferences & Regional Indian Flavors
 * 6: Challenges & Eating Obstacles
 * 7: Interactive AI Food Scanner Demonstration
 * 8: Custom Nutrition Blueprint Reveal & Celebration
 */
const val TOTAL_ONBOARDING_STEPS = 9
const val PREF_KEY_ONBOARDING_COMPLETED = "onboarding_completed"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingFlowScreen(
  uiState: AhaarUiState,
  onStepChanged: (Int) -> Unit = {},
  onSaveAnswer: (key: String, value: Any) -> Unit = { _, _ -> },
  onUpdateBiometrics: (heightCm: Int, weightKg: Float, age: Int, gender: String) -> Unit = { _, _, _, _ -> },
  onUpdateGoal: (goalId: String, targetWeightKg: Float, paceKgPerWeek: Float) -> Unit = { _, _, _ -> },
  onUpdatePreferences: (diets: Set<String>, regions: Set<String>, allergies: Set<String>) -> Unit = { _, _, _ -> },
  onUpdateLifestyle: (workoutFreq: String, obstacles: Set<String>, activityLevel: String) -> Unit = { _, _, _ -> },
  onUpdateMealTimings: (breakfast: String, lunch: String, dinner: String, snack: String, waterGoal: Float) -> Unit = { _, _, _, _, _ -> },
  onApplyReferral: (String) -> Result<String> = { Result.success("") },
  onSignUp: (name: String, email: String, pass: String) -> Unit = { _, _, _ -> },
  onLogin: (email: String, pass: String) -> Unit = { _, _ -> },
  onGoogleSignIn: () -> Unit = {},
  onNavigateToAuth: () -> Unit = {},
  onFinishOnboarding: () -> Unit = {}
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val context = LocalContext.current

  // Internal onboarding state synced with initial uiState
  var currentStep by remember(uiState.onboardingStep) {
    mutableIntStateOf(uiState.onboardingStep.coerceIn(0, TOTAL_ONBOARDING_STEPS - 1))
  }

  // User responses state
  var selectedGoalId by remember { mutableStateOf(uiState.selectedGoalId.ifBlank { "lose_weight" }) }
  var selectedGender by remember { mutableStateOf(uiState.gender.ifBlank { "Male" }) }
  var selectedActivityLevel by remember { mutableStateOf(uiState.activityLevel.ifBlank { "moderately_active" }) }
  var ageState by remember { mutableIntStateOf(if (uiState.age in 15..90) uiState.age else 26) }
  var heightCmState by remember { mutableIntStateOf(if (uiState.heightCm in 120..220) uiState.heightCm else 172) }
  var currentWeightKgState by remember { mutableFloatStateOf(if (uiState.currentWeightKg in 35f..180f) uiState.currentWeightKg else 74.0f) }
  var targetWeightKgState by remember { mutableFloatStateOf(if (uiState.targetWeightKg in 35f..180f) uiState.targetWeightKg else 68.0f) }
  var paceKgPerWeekState by remember { mutableFloatStateOf(0.5f) }
  var selectedDietsState by remember { mutableStateOf(if (uiState.selectedDiets.isNotEmpty()) uiState.selectedDiets else setOf("vegetarian")) }
  var selectedRegionState by remember { mutableStateOf(uiState.selectedRegion.ifBlank { "North Indian" }) }
  var selectedObstaclesState by remember { mutableStateOf(setOf("Late night snacking", "Portion estimation")) }
  var showPrivacyDialog by remember { mutableStateOf(false) }

  // Target BMI safety check (Launch Risk Audit #4: Blocks targets below BMI 18.5)
  val heightMeters = heightCmState / 100f
  val targetBmi = if (heightMeters > 0) targetWeightKgState / (heightMeters * heightMeters) else 22f
  val minHealthyWeightKg = if (heightMeters > 0) (18.5f * heightMeters * heightMeters) else 50f
  val isTargetWeightUnderweight = targetBmi < 18.5f

  // Predictive back handler
  BackHandler(enabled = currentStep > 0) {
    val prev = (currentStep - 1).coerceAtLeast(0)
    currentStep = prev
    onStepChanged(prev)
  }

  fun completeOnboardingAndSave() {
    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    val sessionPrefs = context.getSharedPreferences("ahaar_auth_session", Context.MODE_PRIVATE)
    sessionPrefs.edit().putBoolean(PREF_KEY_ONBOARDING_COMPLETED, true).apply()
    val appPrefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    appPrefs.edit().putBoolean(PREF_KEY_ONBOARDING_COMPLETED, true).apply()

    // Sync all gathered data to ViewModel
    onUpdateBiometrics(heightCmState, currentWeightKgState, ageState, selectedGender)
    onUpdateGoal(selectedGoalId, targetWeightKgState, paceKgPerWeekState)
    onUpdatePreferences(selectedDietsState, setOf(selectedRegionState), emptySet())
    onUpdateLifestyle("3-4 times/week", selectedObstaclesState, selectedActivityLevel)
    onUpdateMealTimings("8:30 AM", "1:00 PM", "7:30 PM", "5:00 PM", 2.5f)

    onFinishOnboarding()
  }

  fun handleContinue() {
    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

    // Sync state at each transition
    when (currentStep) {
      1 -> onUpdateGoal(selectedGoalId, targetWeightKgState, paceKgPerWeekState)
      2 -> onUpdateLifestyle("3-4 times/week", selectedObstaclesState, selectedActivityLevel)
      3 -> onUpdateBiometrics(heightCmState, currentWeightKgState, ageState, selectedGender)
      4 -> onUpdateGoal(selectedGoalId, targetWeightKgState, paceKgPerWeekState)
      5 -> onUpdatePreferences(selectedDietsState, setOf(selectedRegionState), emptySet())
      6 -> onSaveAnswer("obstacles", selectedObstaclesState)
    }

    if (currentStep < TOTAL_ONBOARDING_STEPS - 1) {
      val next = currentStep + 1
      currentStep = next
      onStepChanged(next)
    } else {
      completeOnboardingAndSave()
    }
  }

  val canProceed = when (currentStep) {
    4 -> !isTargetWeightUnderweight
    else -> true
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(colors.bg)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. TOP HEADER: Back Button, Progress Bar & Skip Action
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        if (currentStep > 0) {
          IconButton(
            onClick = {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              val prev = currentStep - 1
              currentStep = prev
              onStepChanged(prev)
            },
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(colors.surface2)
              .testTag("onboarding_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = colors.ink,
              modifier = Modifier.size(20.dp)
            )
          }
        } else {
          Spacer(modifier = Modifier.size(40.dp))
        }

        // Progress Text & Bar
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
        ) {
          Text(
            text = "STEP ${currentStep + 1} OF $TOTAL_ONBOARDING_STEPS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = colors.accent,
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          LinearProgressIndicator(
            progress = { (currentStep + 1).toFloat() / TOTAL_ONBOARDING_STEPS },
            modifier = Modifier
              .fillMaxWidth()
              .height(5.dp)
              .clip(RoundedCornerShape(999.dp))
              .testTag("onboarding_progress_indicator"),
            color = colors.accent,
            trackColor = colors.line
          )
        }

        TextButton(
          onClick = { completeOnboardingAndSave() },
          modifier = Modifier.testTag("onboarding_skip_button")
        ) {
          Text(
            text = "Skip",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink2
          )
        }
      }

      // Toran decorative border
      IndianToranBorder(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 2.dp),
        scallops = 14,
        height = 4.dp
      )

      // 2. ANIMATED STEP CONTENT
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
                initialOffsetX = { (it * 0.5f).toInt() },
                animationSpec = tween(260, easing = FastOutSlowInEasing)
              ) + fadeIn(animationSpec = tween(200)) togetherWith
                slideOutHorizontally(
                  targetOffsetX = { (-it * 0.5f).toInt() },
                  animationSpec = tween(260, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(180))
            } else {
              slideInHorizontally(
                initialOffsetX = { (-it * 0.5f).toInt() },
                animationSpec = tween(260, easing = FastOutSlowInEasing)
              ) + fadeIn(animationSpec = tween(200)) togetherWith
                slideOutHorizontally(
                  targetOffsetX = { (it * 0.5f).toInt() },
                  animationSpec = tween(260, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(180))
            }
          },
          label = "onboarding_step_anim"
        ) { step ->
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            when (step) {
              0 -> Step0WelcomeHero(
                onNavigateToAuth = onNavigateToAuth,
                onOpenPrivacyPolicy = { showPrivacyDialog = true }
              )
              1 -> Step1PrimaryGoal(
                selectedGoalId = selectedGoalId,
                onSelectGoal = {
                  view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                  selectedGoalId = it
                }
              )
              2 -> Step2SexAndActivity(
                selectedGender = selectedGender,
                selectedActivity = selectedActivityLevel,
                onSelectGender = {
                  view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                  selectedGender = it
                },
                onSelectActivity = {
                  view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                  selectedActivityLevel = it
                }
              )
              3 -> Step3Biometrics(
                age = ageState,
                heightCm = heightCmState,
                weightKg = currentWeightKgState,
                onAgeChanged = { ageState = it },
                onHeightChanged = { heightCmState = it },
                onWeightChanged = { currentWeightKgState = it }
              )
              4 -> Step4TargetWeightAndPace(
                heightCm = heightCmState,
                currentWeightKg = currentWeightKgState,
                targetWeightKg = targetWeightKgState,
                paceKgPerWeek = paceKgPerWeekState,
                isUnderweight = isTargetWeightUnderweight,
                minHealthyWeightKg = minHealthyWeightKg,
                onTargetWeightChanged = { targetWeightKgState = it },
                onPaceChanged = { paceKgPerWeekState = it }
              )
              5 -> Step5DietAndRegion(
                selectedDiets = selectedDietsState,
                selectedRegion = selectedRegionState,
                onToggleDiet = { diet ->
                  view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                  selectedDietsState = if (selectedDietsState.contains(diet)) {
                    if (selectedDietsState.size > 1) selectedDietsState - diet else selectedDietsState
                  } else {
                    selectedDietsState + diet
                  }
                },
                onSelectRegion = {
                  view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                  selectedRegionState = it
                }
              )
              6 -> Step6HabitsAndObstacles(
                selectedObstacles = selectedObstaclesState,
                onToggleObstacle = { obs ->
                  view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                  selectedObstaclesState = if (selectedObstaclesState.contains(obs)) {
                    selectedObstaclesState - obs
                  } else {
                    selectedObstaclesState + obs
                  }
                }
              )
              7 -> Step7AiScannerShowcase()
              8 -> Step8BlueprintReveal(
                age = ageState,
                gender = selectedGender,
                weightKg = currentWeightKgState,
                targetWeightKg = targetWeightKgState,
                heightCm = heightCmState,
                goalId = selectedGoalId,
                paceKgPerWeek = paceKgPerWeekState,
                onNavigateToAuth = onNavigateToAuth
              )
            }
            Spacer(modifier = Modifier.height(100.dp))
          }
        }
      }

      // 3. PINNED BOTTOM CTA BUTTON
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(elevation = 12.dp),
        color = colors.bg
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
          Button(
            onClick = { handleContinue() },
            enabled = canProceed,
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .testTag("onboarding_continue_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = colors.ink,
              contentColor = colors.bg,
              disabledContainerColor = colors.line,
              disabledContentColor = colors.ink2
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = when (currentStep) {
                  0 -> "Start Personalizing →"
                  1 -> "Continue to Lifestyle →"
                  2 -> "Enter Biometrics →"
                  3 -> "Set Target Weight →"
                  4 -> if (isTargetWeightUnderweight) "Choose Healthy Weight" else "Set Diet Preferences →"
                  5 -> "Select Eating Habits →"
                  6 -> "See AI Food Scanner →"
                  7 -> "Generate My Blueprint ✨"
                  else -> "Claim Blueprint & Start Tracking 🚀"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    if (showPrivacyDialog) {
      PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
    }
  }
}

// ======================================================================================
// SCREEN 0: WELCOME & CALSNAPIX PROMISE HERO
// ======================================================================================
@Composable
private fun Step0WelcomeHero(
  onNavigateToAuth: () -> Unit = {},
  onOpenPrivacyPolicy: () -> Unit = {}
) {
  val colors = CalTheme.colors

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(6.dp))

    Surface(
      shape = RoundedCornerShape(999.dp),
      color = colors.accentSoft,
      border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.4f))
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = colors.accent, modifier = Modifier.size(15.dp))
        Text(
          text = "AI-POWERED INDIAN NUTRITION",
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = colors.accent,
          letterSpacing = 0.5.sp
        )
      }
    }

    Text(
      text = "Meet CalSnapix\nYour AI Nutritionist",
      fontSize = 32.sp,
      fontWeight = FontWeight.ExtraBold,
      color = colors.ink,
      textAlign = TextAlign.Center,
      lineHeight = 38.sp,
      letterSpacing = (-0.6).sp
    )

    Text(
      text = "Trained on 2,000+ authentic Indian dishes, home-cooked thalis, street foods, and Vedic circadian meal wisdom.",
      fontSize = 14.sp,
      color = colors.ink2,
      textAlign = TextAlign.Center,
      lineHeight = 20.sp
    )

    // Visual Feature Spotlight Card
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = colors.surface,
      border = BorderStroke(1.5.dp, colors.line),
      modifier = Modifier
        .fillMaxWidth()
        .shadow(6.dp, RoundedCornerShape(22.dp))
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        OnboardingHeroBullet(
          icon = Icons.Default.QrCodeScanner,
          title = "2-Second Meal Scanning",
          desc = "Snap a photo of rotis, dal, sabzi, or biryani. Instant calorie & macro estimation."
        )
        OnboardingHeroBullet(
          icon = Icons.Default.Spa,
          title = "Ayurvedic Meal Windows",
          desc = "Syncs with circadian rhythms for peak digestion, steady energy, and peaceful sleep."
        )
        OnboardingHeroBullet(
          icon = Icons.Default.Security,
          title = "Zero Data Selling & Private",
          desc = "Your nutrition logs are encrypted on-device. We never sell your personal data."
        )
      }
    }

    // Social Proof
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = colors.surface2,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "4.9 ★ • Loved by over 50,000+ mindful eaters across India",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = colors.ink
        )
      }
    }

    // Privacy Policy Shortcut
    OutlinedButton(
      onClick = onOpenPrivacyPolicy,
      modifier = Modifier.fillMaxWidth().testTag("onboarding_privacy_policy_button"),
      shape = RoundedCornerShape(14.dp),
      border = BorderStroke(1.dp, colors.line)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.accent)
        Text("Privacy Policy & Trust Standards", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.ink)
      }
    }
  }
}

@Composable
private fun OnboardingHeroBullet(icon: ImageVector, title: String, desc: String) {
  val colors = CalTheme.colors
  Row(
    verticalAlignment = Alignment.Top,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Surface(
      shape = CircleShape,
      color = colors.accentSoft,
      modifier = Modifier.size(38.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(imageVector = icon, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
      }
    }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
      Text(text = desc, fontSize = 12.sp, color = colors.ink2, lineHeight = 16.sp)
    }
  }
}

// ======================================================================================
// SCREEN 1: PRIMARY HEALTH GOAL
// ======================================================================================
@Composable
private fun Step1PrimaryGoal(selectedGoalId: String, onSelectGoal: (String) -> Unit) {
  val colors = CalTheme.colors

  val goals = listOf(
    Triple("lose_weight", "Lose Weight & Shred Fat", "Caloric deficit tailored to your pace without giving up favorite rotis"),
    Triple("build_muscle", "Build Lean Muscle", "High-protein target optimizations for Indian gym-goers & athletes"),
    Triple("maintain_tone", "Maintain Weight & Tone Up", "Keep your current weight stable with consistent daily macro tracking"),
    Triple("mindful_ayurveda", "Ayurvedic Circadian Eating", "Focus on clean Agni digestion, mindful fasting windows & energy balance"),
    Triple("metabolic_health", "Manage Blood Sugar & PCOS", "Low-glycemic Indian meal recommendations for hormonal wellness")
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Text(
      text = "What is your main goal?",
      fontSize = 26.sp,
      fontWeight = FontWeight.ExtraBold,
      color = colors.ink,
      textAlign = TextAlign.Center
    )
    Text(
      text = "CalSnapix will calculate your daily calorie target and macro breakdown around this goal.",
      fontSize = 13.sp,
      color = colors.ink2,
      textAlign = TextAlign.Center
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      goals.forEach { (id, title, desc) ->
        val isSelected = selectedGoalId == id
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isSelected) colors.surface else colors.bg,
          border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) colors.ink else colors.line),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelectGoal(id) }
            .testTag("goal_option_$id")
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isSelected) colors.ink else Color.Transparent)
                .border(2.dp, if (isSelected) colors.ink else colors.line, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = colors.bg, modifier = Modifier.size(14.dp))
              }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.ink)
              Text(text = desc, fontSize = 12.sp, color = colors.ink2, lineHeight = 16.sp)
            }
          }
        }
      }
    }
  }
}

// ======================================================================================
// SCREEN 2: BIOLOGICAL SEX & DAILY ACTIVITY LEVEL
// ======================================================================================
@Composable
private fun Step2SexAndActivity(
  selectedGender: String,
  selectedActivity: String,
  onSelectGender: (String) -> Unit,
  onSelectActivity: (String) -> Unit
) {
  val colors = CalTheme.colors

  val activities = listOf(
    Triple("sedentary", "Sedentary (Desk Job)", "Mostly sitting, less than 5,000 steps/day"),
    Triple("lightly_active", "Lightly Active", "Casual walking or light yoga 1-3 days/week"),
    Triple("moderately_active", "Moderately Active", "Gym, running, or workouts 3-5 days/week"),
    Triple("very_active", "Very Active / Athlete", "Intense training 6+ days or active physical job")
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Your Lifestyle Profile",
      fontSize = 26.sp,
      fontWeight = FontWeight.ExtraBold,
      color = colors.ink,
      textAlign = TextAlign.Center
    )
    Text(
      text = "Biological sex and activity factor into clinical Basal Metabolic Rate (BMR) calculations.",
      fontSize = 13.sp,
      color = colors.ink2,
      textAlign = TextAlign.Center
    )

    // Biological Sex
    Text("Biological Sex", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink, modifier = Modifier.fillMaxWidth())
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      listOf("Male" to "👨 Male", "Female" to "👩 Female").forEach { (sex, label) ->
        val isSelected = selectedGender.equals(sex, ignoreCase = true)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (isSelected) colors.ink else colors.surface,
          border = BorderStroke(1.dp, if (isSelected) colors.ink else colors.line),
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelectGender(sex) }
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = label,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) colors.bg else colors.ink
            )
          }
        }
      }
    }

    // Daily Activity Level
    Spacer(modifier = Modifier.height(4.dp))
    Text("Daily Activity Level", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink, modifier = Modifier.fillMaxWidth())

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      activities.forEach { (id, title, desc) ->
        val isSelected = selectedActivity == id
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (isSelected) colors.surface else colors.bg,
          border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) colors.ink else colors.line),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelectActivity(id) }
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isSelected) colors.ink else Color.Transparent)
                .border(2.dp, if (isSelected) colors.ink else colors.line, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = colors.bg, modifier = Modifier.size(12.dp))
              }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
              Text(text = desc, fontSize = 12.sp, color = colors.ink2)
            }
          }
        }
      }
    }
  }
}

// ======================================================================================
// SCREEN 3: BIOMETRICS (HEIGHT, CURRENT WEIGHT & AGE) WITH REAL-TIME BMI
// ======================================================================================
@Composable
private fun Step3Biometrics(
  age: Int,
  heightCm: Int,
  weightKg: Float,
  onAgeChanged: (Int) -> Unit,
  onHeightChanged: (Int) -> Unit,
  onWeightChanged: (Float) -> Unit
) {
  val colors = CalTheme.colors
  val heightMeters = heightCm / 100f
  val currentBmi = if (heightMeters > 0) weightKg / (heightMeters * heightMeters) else 22f

  val bmiCategory = when {
    currentBmi < 18.5f -> "Underweight" to Color(0xFF1976D2)
    currentBmi < 24.9f -> "Normal (Healthy)" to Color(0xFF388E3C)
    currentBmi < 29.9f -> "Overweight" to Color(0xFFF57C00)
    else -> "Obese" to Color(0xFFD32F2F)
  }

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Your Current Biometrics",
      fontSize = 26.sp,
      fontWeight = FontWeight.ExtraBold,
      color = colors.ink,
      textAlign = TextAlign.Center
    )
    Text(
      text = "Scroll the wheel pickers to input your current measurements.",
      fontSize = 13.sp,
      color = colors.ink2,
      textAlign = TextAlign.Center
    )

    // Age Quick Selector
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Age", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          Text("$age years old", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = colors.accent)
        }

        Slider(
          value = age.toFloat(),
          onValueChange = { onAgeChanged(it.roundToInt()) },
          valueRange = 16f..80f,
          steps = 63,
          colors = SliderDefaults.colors(thumbColor = colors.ink, activeTrackColor = colors.ink)
        )
      }
    }

    // Height & Weight Dual Wheel Picker
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
      Box(modifier = Modifier.padding(14.dp)) {
        HeightWeightPickerRow(
          heightCm = heightCm,
          weightKg = weightKg,
          onHeightChanged = onHeightChanged,
          onWeightChanged = onWeightChanged
        )
      }
    }

    // Live BMI Card
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = colors.surface2,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text("YOUR CURRENT BMI", fontSize = 10.sp, fontWeight = FontWeight.Black, color = colors.ink2, letterSpacing = 0.5.sp)
          Text(
            text = String.format(Locale.US, "%.1f", currentBmi),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.ink
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = bmiCategory.second.copy(alpha = 0.15f)
        ) {
          Text(
            text = bmiCategory.first,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = bmiCategory.second,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }
  }
}

// ======================================================================================
// SCREEN 4: TARGET WEIGHT & SAFE SUSTAINABLE PACE (RISK 4 COMPLIANT)
// ======================================================================================
@Composable
private fun Step4TargetWeightAndPace(
  heightCm: Int,
  currentWeightKg: Float,
  targetWeightKg: Float,
  paceKgPerWeek: Float,
  isUnderweight: Boolean,
  minHealthyWeightKg: Float,
  onTargetWeightChanged: (Float) -> Unit,
  onPaceChanged: (Float) -> Unit
) {
  val colors = CalTheme.colors
  val heightMeters = heightCm / 100f
  val targetBmi = if (heightMeters > 0) targetWeightKg / (heightMeters * heightMeters) else 22f

  val weightDiff = (currentWeightKg - targetWeightKg)
  val estimatedWeeks = if (paceKgPerWeek > 0 && weightDiff > 0) (weightDiff / paceKgPerWeek).roundToInt() else 0

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Target Weight & Pace",
      fontSize = 26.sp,
      fontWeight = FontWeight.ExtraBold,
      color = colors.ink,
      textAlign = TextAlign.Center
    )
    Text(
      text = "Select a safe goal weight and sustainable weekly pace.",
      fontSize = 13.sp,
      color = colors.ink2,
      textAlign = TextAlign.Center
    )

    // Target Weight Slider Card
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, if (isUnderweight) Color(0xFFD32F2F) else colors.line),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Target Weight", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          Text(
            text = "${String.format(Locale.US, "%.1f", targetWeightKg)} kg",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = if (isUnderweight) Color(0xFFD32F2F) else colors.ink
          )
        }

        Slider(
          value = targetWeightKg,
          onValueChange = onTargetWeightChanged,
          valueRange = (minHealthyWeightKg - 8f).coerceAtLeast(35f)..(currentWeightKg + 15f).coerceAtLeast(80f),
          colors = SliderDefaults.colors(
            thumbColor = if (isUnderweight) Color(0xFFD32F2F) else colors.ink,
            activeTrackColor = if (isUnderweight) Color(0xFFD32F2F) else colors.ink
          )
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Target BMI: ${String.format(Locale.US, "%.1f", targetBmi)}", fontSize = 12.sp, color = colors.ink2)
          Text(
            text = if (weightDiff > 0) "-${String.format(Locale.US, "%.1f", weightDiff)} kg to lose" else "+${String.format(Locale.US, "%.1f", -weightDiff)} kg to gain",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent
          )
        }
      }
    }

    // MANDATORY UNDERWEIGHT BLOCKER WARNING (LAUNCH RISK 4)
    if (isUnderweight) {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFFEBEE),
        border = BorderStroke(1.5.dp, Color(0xFFD32F2F)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(Icons.Default.Warning, contentDescription = "Safety Alert", tint = Color(0xFFD32F2F), modifier = Modifier.size(22.dp))
          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
              text = "Unsafe Target Weight (BMI < 18.5)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFB71C1C)
            )
            Text(
              text = "For your health and safety, your target weight cannot be below 18.5 BMI. Your minimum healthy target weight is ${String.format(Locale.US, "%.1f", minHealthyWeightKg)} kg.",
              fontSize = 11.sp,
              color = Color(0xFF5D4037),
              lineHeight = 15.sp
            )
          }
        }
      }
    }

    // Weekly Pace Selector
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Weekly Pace", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          Text(
            text = "${String.format(Locale.US, "%.2f", paceKgPerWeek)} kg / week",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(0.25f to "Gentle", 0.50f to "Recommended", 0.75f to "Active", 1.00f to "Max Safe").forEach { (pace, label) ->
            val isSelected = (paceKgPerWeek - pace).let { kotlin.math.abs(it) < 0.05f }
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) colors.ink else colors.surface2,
              border = BorderStroke(1.dp, if (isSelected) colors.ink else colors.line),
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onPaceChanged(pace) }
            ) {
              Column(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "${pace}kg",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) colors.bg else colors.ink
                )
                Text(
                  text = label,
                  fontSize = 9.sp,
                  color = if (isSelected) colors.bg.copy(alpha = 0.8f) else colors.ink2
                )
              }
            }
          }
        }

        if (estimatedWeeks > 0) {
          Text(
            text = "⏳ Achievable in approximately $estimatedWeeks weeks with mindful deficit adherence.",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.accent
          )
        }
      }
    }
  }
}

// ======================================================================================
// SCREEN 5: DIETARY PREFERENCES & REGIONAL INDIAN FLAVORS
// ======================================================================================
@Composable
private fun Step5DietAndRegion(
  selectedDiets: Set<String>,
  selectedRegion: String,
  onToggleDiet: (String) -> Unit,
  onSelectRegion: (String) -> Unit
) {
  val colors = CalTheme.colors

  val dietTypes = listOf(
    "vegetarian" to "🥬 Pure Vegetarian",
    "eggetarian" to "🥚 Eggetarian",
    "non_vegetarian" to "🍗 Non-Vegetarian",
    "vegan" to "🌱 Vegan (Dairy-Free)",
    "jain" to "🌿 Jain (No Onion/Garlic)"
  )

  val regions = listOf(
    "North Indian" to "🍛 North (Roti, Dal, Paneer, Paratha)",
    "South Indian" to "🥥 South (Dosa, Idli, Sambar, Rasam)",
    "West Indian" to "🌶️ West (Poha, Thepla, Bhakri, Thali)",
    "East Indian" to "🐟 East (Macher Jhol, Dal, Rice)"
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Diet & Regional Flavors",
      fontSize = 26.sp,
      fontWeight = FontWeight.ExtraBold,
      color = colors.ink,
      textAlign = TextAlign.Center
    )
    Text(
      text = "CalSnapix calibrates regional spices, grain types, and recipe recommendations.",
      fontSize = 13.sp,
      color = colors.ink2,
      textAlign = TextAlign.Center
    )

    Text("Dietary Lifestyle", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink, modifier = Modifier.fillMaxWidth())

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      dietTypes.forEach { (key, label) ->
        val isSelected = selectedDiets.contains(key)
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isSelected) colors.surface else colors.bg,
          border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) colors.ink else colors.line),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggleDiet(key) }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
            if (isSelected) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.accent, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))
    Text("Regional Cuisine Preference", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink, modifier = Modifier.fillMaxWidth())

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      regions.forEach { (reg, label) ->
        val isSelected = selectedRegion == reg
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isSelected) colors.surface else colors.bg,
          border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) colors.ink else colors.line),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelectRegion(reg) }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
            if (isSelected) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.accent, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }
  }
}

// ======================================================================================
// SCREEN 6: CHALLENGES & EATING OBSTACLES
// ======================================================================================
@Composable
private fun Step6HabitsAndObstacles(
  selectedObstacles: Set<String>,
  onToggleObstacle: (String) -> Unit
) {
  val colors = CalTheme.colors

  val obstacles = listOf(
    "Late night snacking" to "🌙 Late night cravings & sweet snacking",
    "Portion estimation" to "🍽️ Hard to judge roti & cooked rice portions",
    "Frequent ordering" to "🥡 Ordering takeout (Swiggy / Zomato) often",
    "Low water intake" to "💧 Forgetting to drink 8 glasses of water",
    "Busy schedule" to "⏳ No time to manually type out ingredients"
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "What holds you back most?",
      fontSize = 26.sp,
      fontWeight = FontWeight.ExtraBold,
      color = colors.ink,
      textAlign = TextAlign.Center
    )
    Text(
      text = "Select all habits that apply. We adapt AI reminders and portion guidance to help you overcome them.",
      fontSize = 13.sp,
      color = colors.ink2,
      textAlign = TextAlign.Center
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      obstacles.forEach { (key, label) ->
        val isSelected = selectedObstacles.contains(key)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (isSelected) colors.surface else colors.bg,
          border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) colors.ink else colors.line),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onToggleObstacle(key) }
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isSelected) colors.ink else Color.Transparent)
                .border(2.dp, if (isSelected) colors.ink else colors.line, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = colors.bg, modifier = Modifier.size(12.dp))
              }
            }

            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          }
        }
      }
    }

    // Reassurance card
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = colors.accentSoft,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
        Text(
          text = "CalSnapix eliminates manual typing with 2-second meal scans and smart hydration nudges so you never lose momentum.",
          fontSize = 12.sp,
          color = colors.ink,
          lineHeight = 17.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

// ======================================================================================
// SCREEN 7: INTERACTIVE AI FOOD SCANNER SHOWCASE
// ======================================================================================
@Composable
private fun Step7AiScannerShowcase() {
  val colors = CalTheme.colors
  val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
  val laserOffset by infiniteTransition.animateFloat(
    initialValue = 0.10f,
    targetValue = 0.90f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_y"
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Instant AI Food Vision",
      fontSize = 26.sp,
      fontWeight = FontWeight.ExtraBold,
      color = colors.ink,
      textAlign = TextAlign.Center
    )
    Text(
      text = "Point your camera at any meal. Gemini Vision identifies dishes and macro distributions in 2 seconds.",
      fontSize = 13.sp,
      color = colors.ink2,
      textAlign = TextAlign.Center
    )

    // Animated Viewfinder Simulation Card
    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
      border = BorderStroke(2.dp, colors.accent.copy(alpha = 0.7f)),
      modifier = Modifier
        .fillMaxWidth()
        .height(280.dp)
        .shadow(10.dp, RoundedCornerShape(22.dp))
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        // Visual Meal Thali Representation
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Top AI Detection Status
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = colors.accent
            ) {
              Text(
                text = "● LIVE INDIAN THALI SCAN",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            Text("99.4% CONFIDENCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.7f))
          }

          // Center Detected Food Chips
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            DetectedFoodRow("🫓 2x Whole Wheat Roti", "180 kcal", "6g P • 34g C")
            DetectedFoodRow("🍲 Dal Tadka (1 bowl)", "150 kcal", "8g P • 20g C")
            DetectedFoodRow("🧀 Paneer Bhurji (150g)", "220 kcal", "14g P • 8g C")
            DetectedFoodRow("🥗 Cucumber Salad & Raita", "60 kcal", "3g P • 6g C")
          }

          // Bottom Total Banner
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.Black.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("TOTAL ESTIMATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
              Text("610 kcal • 31g Protein", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD54F))
            }
          }
        }

        // Corner Framing Brackets
        Canvas(modifier = Modifier.fillMaxSize()) {
          val pad = 18.dp.toPx()
          val bracketLen = 22.dp.toPx()
          val stroke = 3.dp.toPx()
          val bracketColor = Color(0xFFFF9800)

          drawLine(bracketColor, Offset(pad, pad + bracketLen), Offset(pad, pad), stroke)
          drawLine(bracketColor, Offset(pad, pad), Offset(pad + bracketLen, pad), stroke)
          drawLine(bracketColor, Offset(size.width - pad - bracketLen, pad), Offset(size.width - pad, pad), stroke)
          drawLine(bracketColor, Offset(size.width - pad, pad), Offset(size.width - pad, pad + bracketLen), stroke)
          drawLine(bracketColor, Offset(pad, size.height - pad - bracketLen), Offset(pad, size.height - pad), stroke)
          drawLine(bracketColor, Offset(pad, size.height - pad), Offset(pad + bracketLen, size.height - pad), stroke)
          drawLine(bracketColor, Offset(size.width - pad - bracketLen, size.height - pad), Offset(size.width - pad, size.height - pad), stroke)
          drawLine(bracketColor, Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - bracketLen), stroke)
        }

        // Sweeping Laser Line
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .offset(y = 280.dp * laserOffset)
            .background(
              Brush.horizontalGradient(
                listOf(Color.Transparent, Color(0xFFFF9800), Color(0xFFFFD54F), Color(0xFFFF9800), Color.Transparent)
              )
            )
        )
      }
    }

    Text(
      text = "Works seamlessly for packaged snacks with built-in barcode scanner, home curries, rotis, and restaurant dining.",
      fontSize = 12.sp,
      color = colors.ink2,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
private fun DetectedFoodRow(name: String, cals: String, macros: String) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = Color.White.copy(alpha = 0.12f),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(macros, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
        Text(cals, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFFD54F))
      }
    }
  }
}

// ======================================================================================
// SCREEN 8: CUSTOM NUTRITION BLUEPRINT REVEAL
// ======================================================================================
@Composable
private fun Step8BlueprintReveal(
  age: Int,
  gender: String,
  weightKg: Float,
  targetWeightKg: Float,
  heightCm: Int,
  goalId: String,
  paceKgPerWeek: Float,
  onNavigateToAuth: () -> Unit = {}
) {
  val colors = CalTheme.colors

  // Clinical BMR calculation (Mifflin-St Jeor)
  val bmr = if (gender.equals("Male", ignoreCase = true)) {
    (10 * weightKg) + (6.25f * heightCm) - (5 * age) + 5
  } else {
    (10 * weightKg) + (6.25f * heightCm) - (5 * age) - 161
  }
  val tdee = (bmr * 1.45f).roundToInt()
  val deficit = (paceKgPerWeek * 7700f / 7f).roundToInt()
  val dailyCalorieTarget = when {
    goalId.contains("lose") -> (tdee - deficit).coerceAtLeast(1200)
    goalId.contains("muscle") || goalId.contains("gain") -> (tdee + 300)
    else -> tdee
  }

  val proteinGrams = ((dailyCalorieTarget * 0.25f) / 4f).roundToInt()
  val carbsGrams = ((dailyCalorieTarget * 0.50f) / 4f).roundToInt()
  val fatsGrams = ((dailyCalorieTarget * 0.25f) / 9f).roundToInt()

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Surface(
      shape = RoundedCornerShape(999.dp),
      color = colors.accentSoft
    ) {
      Text(
        text = "🎉 YOUR BLUEPRINT IS READY",
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = colors.accent,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
      )
    }

    Text(
      text = "Your Personalized Plan",
      fontSize = 28.sp,
      fontWeight = FontWeight.ExtraBold,
      color = colors.ink,
      textAlign = TextAlign.Center
    )

    // Master Blueprint Card
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = colors.ink,
      modifier = Modifier
        .fillMaxWidth()
        .shadow(8.dp, RoundedCornerShape(22.dp))
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("DAILY CALORIE TARGET", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.7f), letterSpacing = 0.8.sp)
          Surface(shape = RoundedCornerShape(6.dp), color = colors.accent) {
            Text("OPTIMIZED", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
          }
        }

        Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "$dailyCalorieTarget",
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
          )
          Text(
            text = "kcal / day",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 8.dp)
          )
        }

        Text(
          text = "Calculated from your BMR ($bmr kcal) and a safe sustainable deficit of $deficit kcal/day.",
          fontSize = 12.sp,
          color = Color.White.copy(alpha = 0.75f)
        )

        // Macros Breakdown Row
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color.White.copy(alpha = 0.12f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            BlueprintMacroCol("PROTEIN", "${proteinGrams}g", "25%")
            BlueprintMacroCol("CARBS", "${carbsGrams}g", "50%")
            BlueprintMacroCol("FATS", "${fatsGrams}g", "25%")
          }
        }
      }
    }

    // Hydration & Vedic Timing Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.line),
        modifier = Modifier.weight(1f)
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF29B6F6), modifier = Modifier.size(20.dp))
          Text("Water Target", fontSize = 12.sp, color = colors.ink2)
          Text("8 Glasses (2.5L)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
        }
      }

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.line),
        modifier = Modifier.weight(1f)
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.Timer, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
          Text("Dinner Window", fontSize = 12.sp, color = colors.ink2)
          Text("Before 8:00 PM", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
        }
      }
    }

    // Account Sync Card
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = colors.surface2,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("Sync Plan to Account", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          Text("Access this blueprint across all your devices securely.", fontSize = 12.sp, color = colors.ink2)
        }
        OutlinedButton(
          onClick = onNavigateToAuth,
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, colors.accent)
        ) {
          Text("Sign Up / In", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.accent)
        }
      }
    }
  }
}

@Composable
private fun BlueprintMacroCol(label: String, amount: String, pct: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f))
    Text(amount, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
    Text(pct, fontSize = 10.sp, color = Color(0xFFFFD54F))
  }
}
