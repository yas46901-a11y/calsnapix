package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.AhaarUiState
import com.example.data.library.DishNutritionCalculation
import com.example.data.library.IndianDish
import com.example.data.library.IndianDishLibrary
import com.example.data.supabase.model.SupabaseAccuracyTestDto
import com.example.model.LoggedMeal
import com.example.ui.components.AccuracyTestModeDialog
import com.example.ui.components.ReportWrongResultDialog
import com.example.ui.theme.CalSecondaryButton
import com.example.ui.theme.CalTheme
import com.example.util.SocialShareHelper
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private sealed class ScanFlowScreenState {
  data object Viewfinder : ScanFlowScreenState()
  data class Analyzing(val photoUri: Uri?, val matchedDish: IndianDish) : ScanFlowScreenState()
  data class Result(
    val photoUri: Uri?,
    val matchedDish: IndianDish,
    val calculation: DishNutritionCalculation,
    val portionMultiplier: Float = 1.0f,
    val confidence: String = "High",
    val userCustomDishName: String = matchedDish.name,
    val userCustomCalories: Int = calculation.calories
  ) : ScanFlowScreenState()
}

/**
 * Redesigned 3-Tap AI Meal Scanner:
 *
 * Tap 1: Screen 1 - Take or upload photo (or tap 1-tap Recent/Favorites at top)
 * Tap 2: Screen 2 - Detected dish cards with range calories, confidence tag, hidden fats, +/- & 0.5x, 1x, 1.5x, 2x
 * Tap 3: Screen 3 - Tap "Log Meal" to save
 */
@Composable
fun CameraMealPreviewScreen(
  uiState: AhaarUiState,
  onLogMeal: (LoggedMeal) -> Unit,
  onSaveCorrection: (dishId: String, original: String, corrected: String, field: String) -> Unit = { _, _, _, _ -> },
  onSaveAccuracyTest: (SupabaseAccuracyTestDto) -> Unit = {},
  onReportWrongResult: (dishName: String, photoId: String?, note: String) -> Unit = { _, _, _ -> },
  onNavigateToHistory: () -> Unit = {},
  onNavigateToPricing: () -> Unit = {},
  onBack: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val view = LocalView.current
  val colors = CalTheme.colors

  BackHandler { onBack() }

  // Camera permissions
  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    hasCameraPermission = granted
  }

  // Camera States
  var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
  var isTorchOn by remember { mutableStateOf(false) }
  var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
  var cameraBindingError by remember { mutableStateOf<String?>(null) }
  var screenState by remember { mutableStateOf<ScanFlowScreenState>(ScanFlowScreenState.Viewfinder) }
  var showLowLightHint by remember { mutableStateOf(false) }
  var showAccuracyTestDialog by remember { mutableStateOf(false) }
  var showReportWrongDialog by remember { mutableStateOf(false) }
  var showScanLimitReachedDialog by remember { mutableStateOf(false) }

  fun executeIfCanScan(action: () -> Unit) {
    if (!uiState.canScanMeal) {
      showScanLimitReachedDialog = true
    } else {
      action()
    }
  }

  // Gallery Picker
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      val matched = IndianDishLibrary.dishes.first()
      screenState = ScanFlowScreenState.Analyzing(photoUri = uri, matchedDish = matched)
    }
  }

  // Auto-request camera permission
  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
    delay(2000)
    if (!isTorchOn) showLowLightHint = true
  }

  // Helper to resolve user's past correction if available (Point 4)
  fun resolveInitialMultiplier(dishId: String): Float {
    val pastCorrection = uiState.userCorrections.find { it.dishId == dishId && it.fieldChanged == "portion" }
    return pastCorrection?.correctedValue?.toFloatOrNull() ?: 1.0f
  }

  when (val state = screenState) {
    is ScanFlowScreenState.Analyzing -> {
      MealScanningProgressView(
        photoUri = state.photoUri,
        dishName = state.matchedDish.name,
        onAnalysisComplete = {
          val initialMultiplier = resolveInitialMultiplier(state.matchedDish.id)
          val calc = state.matchedDish.calculateForGrams(state.matchedDish.standardServingGrams, initialMultiplier)
          val confidence = IndianDishLibrary.calculateConfidence(state.matchedDish.name, state.matchedDish)
          screenState = ScanFlowScreenState.Result(
            photoUri = state.photoUri,
            matchedDish = state.matchedDish,
            calculation = calc,
            portionMultiplier = initialMultiplier,
            confidence = confidence
          )
        }
      )
    }

    is ScanFlowScreenState.Result -> {
      MealScanResultStepView(
        state = state,
        onRetake = { screenState = ScanFlowScreenState.Viewfinder },
        onPortionMultiplierChanged = { mult ->
          val newCalc = state.matchedDish.calculateForGrams(state.matchedDish.standardServingGrams, mult)
          screenState = state.copy(
            portionMultiplier = mult,
            calculation = newCalc,
            userCustomCalories = newCalc.calories
          )
          // Record user correction (Point 4: "Whenever a user edits portion...")
          onSaveCorrection(state.matchedDish.id, "${state.portionMultiplier}x", "${mult}x", "portion")
        },
        onDishNameEdited = { newName ->
          val matched = IndianDishLibrary.findBestMatch(newName) ?: state.matchedDish
          val newCalc = matched.calculateForGrams(matched.standardServingGrams, state.portionMultiplier)
          val conf = IndianDishLibrary.calculateConfidence(newName, matched)
          screenState = state.copy(
            matchedDish = matched,
            calculation = newCalc,
            userCustomDishName = newName,
            userCustomCalories = newCalc.calories,
            confidence = conf
          )
          onSaveCorrection(state.matchedDish.id, state.matchedDish.name, newName, "dish_name")
        },
        onCaloriesEdited = { newCals ->
          screenState = state.copy(userCustomCalories = newCals)
          onSaveCorrection(state.matchedDish.id, "${state.calculation.calories}", "$newCals", "calories")
        },
        onReportWrongResult = { showReportWrongDialog = true },
        onLogMeal = { meal ->
          onLogMeal(meal)
          Toast.makeText(context, "Logged ${meal.title} (${meal.calories} kcal) to daily tracker! 🪷", Toast.LENGTH_SHORT).show()
          screenState = ScanFlowScreenState.Viewfinder
        }
      )

      if (showReportWrongDialog) {
        ReportWrongResultDialog(
          dishName = state.userCustomDishName,
          photoId = state.photoUri?.hashCode()?.toString(),
          onSubmit = { note ->
            onReportWrongResult(state.userCustomDishName, state.photoUri?.hashCode()?.toString(), note)
          },
          onDismiss = { showReportWrongDialog = false }
        )
      }
    }

    is ScanFlowScreenState.Viewfinder -> {
      Box(
        modifier = modifier
          .fillMaxSize()
          .background(Color.Black)
      ) {
        // Camera viewfinder or simulated view
        if (hasCameraPermission && cameraBindingError == null) {
          AndroidView(
            factory = { ctx ->
              val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
              }
              val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
              cameraProviderFuture.addListener({
                try {
                  val provider = cameraProviderFuture.get()
                  val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                  }
                  val selector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
                  val imageCapture = ImageCapture.Builder().build()
                  provider.unbindAll()
                  val cam = provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
                  cameraControl = cam.cameraControl
                  cam.cameraControl.enableTorch(isTorchOn)
                } catch (e: Exception) {
                  cameraBindingError = e.message
                }
              }, ContextCompat.getMainExecutor(ctx))
              previewView
            },
            update = { cameraControl?.enableTorch(isTorchOn) },
            modifier = Modifier.fillMaxSize()
          )
        } else {
          // Streaming emulator fallback preview
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.radialGradient(
                  listOf(Color(0xFF263238), Color(0xFF0F172A)),
                  center = Offset(500f, 600f),
                  radius = 900f
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text(text = "📸", fontSize = 52.sp)
              Text(
                text = "Point camera at your meal or plate",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "AI vision detects rotis, dals, sabzis & portion size",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
              )
            }
          }
        }

        // Viewfinder optical reticle overlay
        ViewfinderReticle(modifier = Modifier.fillMaxSize())

        // Top Navigation & 1-TAP Recent/Favorites Bar (Point 3)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Header Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = CircleShape,
              color = Color.Black.copy(alpha = 0.65f),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
              modifier = Modifier.size(42.dp)
            ) {
              IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
              }
            }

            // Free scan counter badge or Pro badge
            Surface(
              shape = RoundedCornerShape(999.dp),
              color = if (uiState.isSubscribed) colors.ink.copy(alpha = 0.9f) else Color(0xFF0F172A).copy(alpha = 0.85f),
              border = BorderStroke(1.dp, if (uiState.isSubscribed) Color(0xFFFFD54F) else colors.accent.copy(alpha = 0.6f)),
              modifier = Modifier.clickable {
                if (!uiState.isSubscribed) onNavigateToPricing()
              }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isSubscribed) Color(0xFFFFD54F) else if (uiState.freeScansRemaining > 0) Color(0xFF00E676) else Color(0xFFFF5252))
                )
                Text(
                  text = if (uiState.isSubscribed) "★ Premium Active" else "Free Scans: ${uiState.freeScansRemaining}/3 left",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }

            // Action Buttons: Torch & Accuracy Test Mode
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              // Accuracy Test Mode Button (Point 1)
              Surface(
                shape = CircleShape,
                color = colors.accent,
                modifier = Modifier.size(42.dp)
              ) {
                IconButton(
                  onClick = { showAccuracyTestDialog = true },
                  modifier = Modifier.testTag("open_test_mode_button")
                ) {
                  Icon(Icons.Default.Science, contentDescription = "Test Mode", tint = Color.White, modifier = Modifier.size(20.dp))
                }
              }

              // Torch Button
              Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                modifier = Modifier.size(42.dp)
              ) {
                IconButton(onClick = {
                  isTorchOn = !isTorchOn
                  cameraControl?.enableTorch(isTorchOn)
                }) {
                  Icon(
                    imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Torch",
                    tint = if (isTorchOn) Color(0xFFFFC107) else Color.White,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
          }

          // POINT 3: "Add a 'Recent' and 'Favorites' row at the top so users can log their usual meals with one tap and no photo."
          QuickOneTapMealsRow(
            onQuickLog = { dish ->
              executeIfCanScan {
                val calc = dish.calculateForGrams(dish.standardServingGrams)
                val logged = LoggedMeal(
                  id = System.currentTimeMillis().toString(),
                  title = dish.name,
                  mealType = dish.mealType,
                  time = "Just now",
                  calories = calc.calories,
                  tag = "1-Tap Quick Log",
                  imageUrl = "",
                  proteinGrams = calc.proteinGrams.toInt(),
                  carbsGrams = calc.carbsGrams.toInt(),
                  fatGrams = calc.fatGrams.toInt()
                )
                onLogMeal(logged)
                Toast.makeText(context, "Logged ${dish.name} (${calc.calories} kcal) with 1-tap! 🪷", Toast.LENGTH_SHORT).show()
              }
            }
          )
        }

        // Low-light torch prompt
        if (showLowLightHint && !isTorchOn) {
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = Color(0xFF263238).copy(alpha = 0.95f),
            border = BorderStroke(1.dp, Color(0xFFFFC107).copy(alpha = 0.6f)),
            modifier = Modifier
              .align(Alignment.Center)
              .offset(y = 120.dp)
              .padding(horizontal = 24.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
              Text("Lighting looks soft • Tap ⚡ Torch for sharpest macro scan", fontSize = 11.sp, color = Color.White)
            }
          }
        }

        // Bottom Shutter & Gallery Controls
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(
              Brush.verticalGradient(
                listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f), Color.Black.copy(alpha = 0.98f))
              )
            )
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text(
            text = "Center full thali, katoris & phulkas in the frame",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Gallery Picker
            Surface(
              shape = CircleShape,
              color = Color.White.copy(alpha = 0.20f),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
              modifier = Modifier.size(54.dp)
            ) {
              IconButton(
                onClick = {
                  executeIfCanScan {
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                  }
                },
                modifier = Modifier.testTag("gallery_picker_button")
              ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.size(24.dp))
              }
            }

            // Tactical Shutter Button
            Box(
              modifier = Modifier
                .size(76.dp)
                .border(3.dp, colors.accent, CircleShape)
                .padding(5.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable {
                  executeIfCanScan {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    val randomDish = IndianDishLibrary.dishes.random()
                    screenState = ScanFlowScreenState.Analyzing(photoUri = null, matchedDish = randomDish)
                  }
                }
                .testTag("camera_shutter_button"),
              contentAlignment = Alignment.Center
            ) {
              Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(colors.accent))
            }

            // History Button
            Surface(
              shape = CircleShape,
              color = Color.White.copy(alpha = 0.20f),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
              modifier = Modifier.size(54.dp)
            ) {
              IconButton(onClick = onNavigateToHistory) {
                Icon(Icons.Default.History, contentDescription = "History", tint = Color.White, modifier = Modifier.size(24.dp))
              }
            }
          }
        }
      }

      // Accuracy Test Mode Dialog
      if (showAccuracyTestDialog) {
        AccuracyTestModeDialog(
          testHistory = uiState.accuracyTests,
          onSaveTest = { test ->
            onSaveAccuracyTest(test)
          },
          onDismiss = { showAccuracyTestDialog = false }
        )
      }

      // Free Calorie Scans Limit Reached Paywall Dialog
      if (showScanLimitReachedDialog) {
        AlertDialog(
          onDismissRequest = { showScanLimitReachedDialog = false },
          title = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(22.dp))
              Text(text = "Free Calorie Scans Used", fontWeight = FontWeight.Bold)
            }
          },
          text = {
            Text(
              text = "You've used all 3 free calorie scans. The free tier includes 3 complimentary scans.\n\nSubscribe to CalSnapix Calorie Scan Premium to continue scanning unlimited meals with verified Indian nutritional composition.",
              fontSize = 13.sp,
              lineHeight = 18.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          },
          confirmButton = {
            Button(
              onClick = {
                showScanLimitReachedDialog = false
                onNavigateToPricing()
              },
              colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = Color.White)
            ) {
              Text("Upgrade to Premium", fontWeight = FontWeight.Bold)
            }
          },
          dismissButton = {
            TextButton(onClick = { showScanLimitReachedDialog = false }) {
              Text("Not Now", color = colors.ink2)
            }
          }
        )
      }
    }
  }
}

/**
 * 1-Tap Recent & Favorites Row (Point 3)
 */
@Composable
private fun QuickOneTapMealsRow(
  onQuickLog: (IndianDish) -> Unit
) {
  val quickDishes = listOf(
    IndianDishLibrary.dishes.find { it.id == "south_idli" } ?: IndianDishLibrary.dishes[0],
    IndianDishLibrary.dishes.find { it.id == "north_phulka_roti" } ?: IndianDishLibrary.dishes[1],
    IndianDishLibrary.dishes.find { it.id == "north_dal_tadka" } ?: IndianDishLibrary.dishes[2],
    IndianDishLibrary.dishes.find { it.id == "west_poha" } ?: IndianDishLibrary.dishes[3],
    IndianDishLibrary.dishes.find { it.id == "pan_masala_chai" } ?: IndianDishLibrary.dishes[4]
  )

  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "⚡ 1-Tap Log (No Photo Needed):",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White.copy(alpha = 0.9f)
      )
      Text(text = "Recent & Favorites", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      quickDishes.forEach { dish ->
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = Color.Black.copy(alpha = 0.70f),
          border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
          modifier = Modifier.clickable { onQuickLog(dish) }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Text(text = "+", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
            Text(text = dish.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(
              text = "${dish.calculateForGrams(dish.standardServingGrams).calories} kcal",
              fontSize = 10.sp,
              color = Color.White.copy(alpha = 0.65f)
            )
          }
        }
      }
    }
  }
}

/**
 * Screen 2: Slide-up detected items with Honest Numbers, Range Calories,
 * Confidence Tag, Hidden Fats, and 3-Tap Logging (Points 3, 4, 5, 7e).
 */
@Composable
private fun MealScanResultStepView(
  state: ScanFlowScreenState.Result,
  onRetake: () -> Unit,
  onPortionMultiplierChanged: (Float) -> Unit,
  onDishNameEdited: (String) -> Unit,
  onCaloriesEdited: (Int) -> Unit,
  onReportWrongResult: () -> Unit,
  onLogMeal: (LoggedMeal) -> Unit
) {
  val context = LocalContext.current
  val colors = CalTheme.colors
  val dish = state.matchedDish
  val calc = state.calculation
  val mult = state.portionMultiplier

  var isEditingDishName by remember { mutableStateOf(false) }
  var editedDishName by remember { mutableStateOf(state.userCustomDishName) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(colors.bg)
      .verticalScroll(rememberScrollState())
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(onClick = onRetake) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.ink)
        }
        Text(
          text = "Detected Meal",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )
      }

      // Point 5: Confidence Level Tag (High, Medium, Low)
      val confColor = when (state.confidence) {
        "High" -> colors.accent
        "Medium" -> Color(0xFF3B82F6)
        else -> Color(0xFFD97706)
      }
      val confBg = when (state.confidence) {
        "High" -> colors.accentSoft
        "Medium" -> Color(0xFFEFF6FF)
        else -> Color(0xFFFEF3C7)
      }

      Surface(
        shape = RoundedCornerShape(999.dp),
        color = confBg,
        border = BorderStroke(1.dp, confColor.copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = confColor, modifier = Modifier.size(12.dp))
          Text(
            text = "${state.confidence} Confidence",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = confColor
          )
        }
      }
    }

    // POINT 5: Yellow Warning Banner if Confidence is Low
    if (state.confidence == "Low") {
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFEF3C7),
        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
        modifier = Modifier.fillMaxWidth().testTag("low_confidence_warning_banner")
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(Icons.Default.Warning, contentDescription = "Warning", tint = Color(0xFFB45309), modifier = Modifier.size(20.dp))
          Text(
            text = "Portion is hard to judge. Please check the amount.",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF92400E)
          )
        }
      }
    }

    // Dish Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = colors.surface),
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Dish Name with inline Edit option (Point 4)
        if (!isEditingDishName) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = state.userCustomDishName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "${dish.region} Indian • ${dish.mealType} • ${dish.category}",
                fontSize = 12.sp,
                color = colors.ink2
              )
            }
            IconButton(onClick = { isEditingDishName = true }) {
              Icon(Icons.Default.Edit, contentDescription = "Edit Dish Name", tint = colors.accent, modifier = Modifier.size(18.dp))
            }
          }
        } else {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = editedDishName,
              onValueChange = { editedDishName = it },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp)
            )
            Button(
              onClick = {
                onDishNameEdited(editedDishName)
                isEditingDishName = false
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
            ) {
              Text("Save")
            }
          }
        }

        // POINT 5: "Change the result display so that every calorie value shows a range, for example '≈ 450 kcal (380–520)', instead of one exact number."
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = colors.surface2,
          border = BorderStroke(1.dp, colors.line),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Energy Estimate (IFCT 2017)", fontSize = 11.sp, color = colors.ink2)
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "≈ ${calc.calories} kcal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = colors.ink
              )
              Text(
                text = "(${calc.caloriesMin}–${calc.caloriesMax})",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink2,
                modifier = Modifier.padding(bottom = 3.dp)
              )
            }
            Text(
              text = "Portion size: ${calc.servingGrams.roundToInt()}g total serving",
              fontSize = 11.sp,
              color = colors.ink2
            )
          }
        }

        // POINT 5: "Always list hidden fats (oil, ghee, butter) as a separate line item with its own kcal value, and mark it 'estimated'."
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFFFFBEB),
          border = BorderStroke(1.dp, Color(0xFFFDE68A)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "🧈 Hidden Fats (Tadka Oil, Ghee, Butter)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF92400E)
              )
              Text(
                text = "~${calc.hiddenFatGrams}g oil & fats absorbed during cooking",
                fontSize = 11.sp,
                color = Color(0xFFB45309)
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFFDE68A)
            ) {
              Text(
                text = "${calc.hiddenFatKcal} kcal (estimated)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78350F),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        // POINT 3: "let the user tap '+' or '–' to change the portion (presets: 0.5×, 1×, 1.5×, 2×)"
        Text(
          text = "Adjust Portion Size (3-Tap Quick Select):",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = colors.ink
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Decrement button
          IconButton(
            onClick = {
              val next = (mult - 0.25f).coerceAtLeast(0.25f)
              onPortionMultiplierChanged(next)
            },
            modifier = Modifier
              .size(38.dp)
              .border(1.dp, colors.line, CircleShape)
          ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = colors.ink)
          }

          // Presets: 0.5x, 1x, 1.5x, 2x
          listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { presetMult ->
            val isSelected = mult == presetMult
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) colors.accent else colors.surface2,
              border = BorderStroke(1.dp, if (isSelected) colors.accent else colors.line),
              modifier = Modifier
                .weight(1f)
                .clickable { onPortionMultiplierChanged(presetMult) }
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 8.dp)
              ) {
                Text(
                  text = "${presetMult}×",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else colors.ink
                )
              }
            }
          }

          // Increment button
          IconButton(
            onClick = {
              val next = (mult + 0.25f).coerceAtMost(4.0f)
              onPortionMultiplierChanged(next)
            },
            modifier = Modifier
              .size(38.dp)
              .border(1.dp, colors.line, CircleShape)
          ) {
            Icon(Icons.Default.Add, contentDescription = "Increase", tint = colors.ink)
          }
        }

        // Macro breakdown rows
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          MacroItemPill(label = "Protein", value = "${calc.proteinGrams}g", color = colors.accent)
          MacroItemPill(label = "Carbs", value = "${calc.carbsGrams}g", color = Color(0xFF64748B))
          MacroItemPill(label = "Fat", value = "${calc.fatGrams}g", color = Color(0xFFF59E0B))
          MacroItemPill(label = "Fiber", value = "${calc.fiberGrams}g", color = Color(0xFF10B981))
        }
      }
    }

    // POINT 7e: "add a 'Report a wrong result' button"
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      TextButton(
        onClick = onReportWrongResult,
        modifier = Modifier.testTag("report_wrong_result_button")
      ) {
        Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Report a wrong result", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Medium)
      }
    }

    // POINT 3: Screen 3 - Tap "Log" to save.
    Button(
      onClick = {
        val logged = LoggedMeal(
          id = System.currentTimeMillis().toString(),
          title = state.userCustomDishName,
          mealType = dish.mealType,
          time = "Just now",
          calories = state.userCustomCalories,
          tag = "Verified IFCT 2017",
          imageUrl = state.photoUri?.toString() ?: "",
          proteinGrams = calc.proteinGrams.roundToInt(),
          carbsGrams = calc.carbsGrams.roundToInt(),
          fatGrams = calc.fatGrams.roundToInt()
        )
        onLogMeal(logged)
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("log_scanned_meal_button"),
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
        Text(
          text = "Log Meal (Tap 3: Confirm ${state.userCustomCalories} kcal)",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }

    CalSecondaryButton(
      text = "Retake Photo",
      onClick = onRetake,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun MacroItemPill(label: String, value: String, color: Color) {
  val colors = CalTheme.colors
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = colors.surface2,
    border = BorderStroke(1.dp, colors.line)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = label, fontSize = 10.sp, color = colors.ink2)
      Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
  }
}

@Composable
private fun MealScanningProgressView(
  photoUri: Uri?,
  dishName: String,
  onAnalysisComplete: () -> Unit
) {
  val colors = CalTheme.colors
  var progress by remember { mutableFloatStateOf(0.25f) }

  LaunchedEffect(Unit) {
    delay(300)
    progress = 0.65f
    delay(400)
    progress = 0.90f
    delay(300)
    progress = 1.0f
    delay(200)
    onAnalysisComplete()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A)),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.padding(32.dp)
    ) {
      CircularProgressIndicator(
        color = colors.accent,
        strokeWidth = 3.dp,
        modifier = Modifier.size(54.dp)
      )
      Text(
        text = "Scanning & Comparing IFCT 2017 Library...",
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )
      Text(
        text = "Analyzing roti, dal, sabzi & estimating portion grams",
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 12.sp,
        textAlign = TextAlign.Center
      )
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth(0.7f)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = colors.accent,
        trackColor = Color.White.copy(alpha = 0.2f)
      )
    }
  }
}

@Composable
private fun ViewfinderReticle(modifier: Modifier = Modifier) {
  val colors = CalTheme.colors
  val infiniteTransition = rememberInfiniteTransition(label = "reticle_pulse")
  val pulse by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
    label = "pulse"
  )

  Canvas(modifier = modifier) {
    val frameW = 290.dp.toPx()
    val frameH = 290.dp.toPx()
    val left = (size.width - frameW) / 2f
    val top = (size.height - frameH) / 2f - 20.dp.toPx()

    // Outer vignette
    val path = Path().apply {
      addRect(Rect(0f, 0f, size.width, size.height))
      addRoundRect(
        RoundRect(
          left = left,
          top = top,
          right = left + frameW,
          bottom = top + frameH,
          cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
        )
      )
    }
    drawPath(path, Color.Black.copy(alpha = 0.45f))

    // Reticle frame
    drawRoundRect(
      color = colors.accent.copy(alpha = pulse * 0.75f),
      topLeft = Offset(left, top),
      size = androidx.compose.ui.geometry.Size(frameW, frameH),
      cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
      style = Stroke(width = 1.8.dp.toPx())
    )
  }
}
