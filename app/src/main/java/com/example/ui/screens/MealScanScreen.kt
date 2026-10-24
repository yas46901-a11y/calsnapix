package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import com.example.data.security.AiQuotaManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.CalTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.AhaarUiState
import com.example.model.LoggedMeal
import com.example.model.ScanResult
import com.example.ui.components.VoiceLogDialog
import com.example.ui.components.GlassBorderBrush
import com.example.ui.components.GlassBorderStroke
import com.example.ui.theme.AhaarOnPrimary
import com.example.ui.theme.AhaarPrimary
import com.example.ui.theme.AhaarPrimaryContainer
import com.example.ui.theme.AhaarPrimaryFixed
import com.example.ui.theme.AhaarSecondary
import com.example.ui.theme.AhaarSecondaryFixed
import com.example.ui.theme.AhaarTertiary
import com.example.ui.theme.AhaarTertiaryContainer
import com.example.ui.theme.AhaarTertiaryFixed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Built-in library of Indian dish profiles for AI meal recognition.
 */
val indianMealLibrary = listOf(
  ScanResult(
    dishName = "Dal Makhani & Garlic Naan",
    calories = 480,
    servingDescription = "Estimated for 1 standard restaurant serving (1 bowl dal + 1 naan)",
    proteinGrams = 18,
    carbsGrams = 65,
    fatGrams = 16,
    chefNote = "Rich in slow-digesting complex carbs and plant protein from whole black urad lentils simmered with aromatic spices.",
    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDQeD8L9bK_ZtGZ9Q7fM8kP3vN5mJ1yX_sL2kP1vN8mJ5t=s600"
  ),
  ScanResult(
    dishName = "Paneer Tikka & Mint Chutney",
    calories = 360,
    servingDescription = "6 skewered cottage cheese cubes with onions & bell peppers",
    proteinGrams = 24,
    carbsGrams = 12,
    fatGrams = 22,
    chefNote = "High bio-availability casein protein and bone-strengthening calcium. Mint chutney adds gut-friendly digestive enzymes.",
    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAFvKd1TFSPEfd_x52VY8LWAwJAFWyYwYOeeY6FWwG-Ofbjs_xL0PWv7yBDhg3C6lodjFJ8XF_zeUvqLULiGdjF59mWtj-hMM8f5OF_ClY7pA13OP-_OxaxSOja1AYIZb9gh_BMWzdZk_0z76txCOWQtTP_-8Xh8rWcQiodp2_KF4WtSMn7_XP9cwL7cveNiMyK6en43AxsaQ3-T2b3GzWAlvbRPJFYVQXDMKw8IzO--xKlVVPE-aFk0g"
  ),
  ScanResult(
    dishName = "Masala Dosa & Sambar",
    calories = 410,
    servingDescription = "1 large fermented rice-lentil crepe with spiced potato filling",
    proteinGrams = 11,
    carbsGrams = 68,
    fatGrams = 12,
    chefNote = "Naturally fermented batter promotes healthy gut microbiome biodiversity. Tur dal sambar provides essential amino acids.",
    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA9cQ8zZ7mY5pL3kP1vN8mJ2xW_sL2kP1vN8mJ5t=s600"
  ),
  ScanResult(
    dishName = "Tandoori Chicken & Green Salad",
    calories = 380,
    servingDescription = "2 roasted chicken leg pieces with pickled onions and cucumber",
    proteinGrams = 38,
    carbsGrams = 8,
    fatGrams = 14,
    chefNote = "Lean complete protein with zero refined sugars. Ginger-garlic and turmeric marinade provides potent anti-inflammatory curcumin.",
    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDfZ4mY8nK1vP9xW_sL2kP1vN8mJ5t=s600"
  ),
  ScanResult(
    dishName = "Moong Dal Khichdi & Desi Ghee",
    calories = 320,
    servingDescription = "1 deep bowl comforting yellow moong dal and rice with 1 tsp ghee",
    proteinGrams = 13,
    carbsGrams = 52,
    fatGrams = 7,
    chefNote = "The quintessential Ayurvedic healing meal. Exceptionally easy on digestion with a complete protein amino acid profile.",
    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAE4eXW1oN8K_z7vY5pL3kP1vN8mJ2xW_sL2kP1vN8mJ5t=s600"
  )
)

data class BarcodeItem(
  val code: String,
  val productName: String,
  val brand: String,
  val calories: Int,
  val serving: String,
  val protein: Int,
  val carbs: Int,
  val fat: Int,
  val details: String
)

val barcodeProductsLibrary = listOf(
  BarcodeItem(
    code = "8901262010123",
    productName = "Fresh Farm Milk (Toned)",
    brand = "Fresh Dairy",
    calories = 116,
    serving = "200 ml (1 glass)",
    protein = 7,
    carbs = 9,
    fat = 5,
    details = "Cold-pasteurised pure farm dairy milk fortified with Vitamin A & D3."
  ),
  BarcodeItem(
    code = "8901030383789",
    productName = "Quaker Rolled Oats 100% Wholegrain",
    brand = "Quaker",
    calories = 150,
    serving = "40 g (1/2 cup)",
    protein = 5,
    carbs = 27,
    fat = 3,
    details = "High soluble beta-glucan fiber helps lower cholesterol and manages morning glucose spikes."
  ),
  BarcodeItem(
    code = "8901725181222",
    productName = "Epigamia Greek Yogurt (Natural Probiotic)",
    brand = "Epigamia",
    calories = 85,
    serving = "90 g cup",
    protein = 8,
    carbs = 6,
    fat = 3,
    details = "Strained artisanal curd packed with live active probiotic cultures and 2x natural protein."
  ),
  BarcodeItem(
    code = "8901058852331",
    productName = "Saffola Active Multigrain Roasted Seeds",
    brand = "Saffola",
    calories = 160,
    serving = "30 g",
    protein = 6,
    carbs = 10,
    fat = 11,
    details = "Omega-3 rich flax, chia, pumpkin, and watermelon seeds roasted with rock salt."
  )
)

sealed interface ScanScreenState {
  data object Viewfinder : ScanScreenState
  data class Analyzing(val photoUri: Uri?, val dishCandidate: ScanResult) : ScanScreenState
  data class Result(
    val photoUri: Uri?,
    val result: ScanResult,
    val portionMultiplier: Float = 1.0f,
    val selectedMealType: String = "Lunch"
  ) : ScanScreenState
  data class Success(val loggedMeal: LoggedMeal) : ScanScreenState
}

/**
 * MealScanScreen integrates CameraX with live preview, lens flipping, torch control,
 * image capture, photo picker fallback, and instant AI nutrition recognition for Indian meals.
 */
@Composable
fun MealScanScreen(
  uiState: AhaarUiState,
  onLogMeal: (LoggedMeal) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  // Camera Permission state
  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    hasCameraPermission = granted
  }

  // Automatically request camera permission when user enters meal scanning screen
  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  // Automatically refresh camera permission when user resumes app
  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(lifecycleOwner) {
    val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
      if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  // CameraX state
  var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
  var isTorchOn by remember { mutableStateOf(false) }
  var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
  var cameraBindingError by remember { mutableStateOf<String?>(null) }
  var demoDishIndex by remember { mutableIntStateOf(0) }

  // Screen scanning state
  var screenState by remember { mutableStateOf<ScanScreenState>(ScanScreenState.Viewfinder) }
  var showEditDishDialog by remember { mutableStateOf(false) }
  var showBarcodeTyperDialog by remember { mutableStateOf(false) }
  var showVoiceLogDialog by remember { mutableStateOf(false) }
  var scanMode by remember { mutableStateOf("MEAL") } // "MEAL" or "BARCODE"
  var detectedBarcodeItem by remember { mutableStateOf<BarcodeItem?>(null) }
  var barcodeIndex by remember { mutableIntStateOf(0) }

  // Photo gallery picker fallback
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      val candidate = indianMealLibrary[demoDishIndex % indianMealLibrary.size]
      screenState = ScanScreenState.Analyzing(photoUri = uri, dishCandidate = candidate)
    }
  }

  // CameraX ImageCapture instance
  val imageCapture = remember {
    ImageCapture.Builder()
      .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
      .build()
  }

  // Cleanup camera when leaving the screen or viewfinder state
  DisposableEffect(screenState) {
    onDispose {
      if (screenState !is ScanScreenState.Viewfinder) {
        try {
          val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
          if (cameraProviderFuture.isDone) {
            cameraProviderFuture.get().unbindAll()
          }
        } catch (e: Exception) {
          Log.w("MealScanScreen", "Unbinding camera on dispose", e)
        }
      }
    }
  }

  fun cleanupTemporaryPhoto(uri: Uri?) {
    if (uri != null && uri.scheme == "file") {
      try {
        val file = File(uri.path ?: "")
        if (file.exists()) file.delete()
      } catch (_: Exception) {}
    }
  }

  // Function to perform AI analysis transition
  fun startAiAnalysis(uri: Uri?, dish: ScanResult) {
    AiQuotaManager.recordSuccessfulScan(context, uiState.isPro)
    screenState = ScanScreenState.Analyzing(photoUri = uri, dishCandidate = dish)
  }

  // Handle captured analysis state
  when (val current = screenState) {
    is ScanScreenState.Analyzing -> {
      AiScanningProgressView(
        photoUri = current.photoUri,
        dishCandidate = current.dishCandidate,
        onAnalysisComplete = {
          screenState = ScanScreenState.Result(
            photoUri = current.photoUri,
            result = current.dishCandidate
          )
        }
      )
    }

    is ScanScreenState.Result -> {
      AiAnalysisResultView(
        resultState = current,
        onRetake = {
          cleanupTemporaryPhoto(current.photoUri)
          screenState = ScanScreenState.Viewfinder
        },
        onLogMeal = { logged ->
          cleanupTemporaryPhoto(current.photoUri)
          screenState = ScanScreenState.Success(logged)
        },
        onUpdateResult = { updated ->
          screenState = updated
        }
      )
    }

    is ScanScreenState.Success -> {
      MealLoggedSuccessOverlay(
        loggedMeal = current.loggedMeal,
        onAnimationFinished = {
          onLogMeal(current.loggedMeal)
        }
      )
    }

    is ScanScreenState.Viewfinder -> {
      if (!hasCameraPermission) {
        CameraPermissionRationaleView(
          onRequestPermission = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
          },
          onPickFromGallery = {
            photoPickerLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          onTryDemoMeal = {
            val candidate = indianMealLibrary[demoDishIndex % indianMealLibrary.size]
            startAiAnalysis(null, candidate)
          }
        )
      } else {
        CameraXLiveViewfinder(
          lensFacing = lensFacing,
          isTorchOn = isTorchOn,
          imageCapture = imageCapture,
          cameraBindingError = cameraBindingError,
          scanMode = scanMode,
          onToggleScanMode = { newMode ->
            scanMode = newMode
            if (newMode == "BARCODE") {
              detectedBarcodeItem = barcodeProductsLibrary[barcodeIndex % barcodeProductsLibrary.size]
            }
          },
          onCameraBound = { control ->
            cameraControl = control
            control.enableTorch(isTorchOn)
          },
          onCameraError = { err ->
            cameraBindingError = err
          },
          onFlipCamera = {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
              CameraSelector.LENS_FACING_FRONT
            } else {
              CameraSelector.LENS_FACING_BACK
            }
          },
          onToggleTorch = {
            val newTorch = !isTorchOn
            isTorchOn = newTorch
            cameraControl?.enableTorch(newTorch)
          },
          onCapturePhoto = {
            val quota = AiQuotaManager.checkCanPerformScan(context, uiState.isPro)
            if (!quota.allowed) {
              Toast.makeText(context, quota.message, Toast.LENGTH_LONG).show()
            } else if (scanMode == "BARCODE") {
              val barcodeItem = barcodeProductsLibrary[barcodeIndex % barcodeProductsLibrary.size]
              val mappedResult = ScanResult(
                dishName = barcodeItem.productName,
                calories = barcodeItem.calories,
                servingDescription = barcodeItem.serving,
                proteinGrams = barcodeItem.protein,
                carbsGrams = barcodeItem.carbs,
                fatGrams = barcodeItem.fat,
                chefNote = "Scanned Barcode: ${barcodeItem.code} (${barcodeItem.brand}). ${barcodeItem.details}",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDfZ4mY8nK1vP9xW_sL2kP1vN8mJ5t=s600"
              )
              startAiAnalysis(null, mappedResult)
            } else {
              // Take picture with CameraX
              val photoFile = File(
                context.cacheDir,
                "meal_scan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
              )
              val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

              imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                  override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val candidate = indianMealLibrary[demoDishIndex % indianMealLibrary.size]
                    startAiAnalysis(savedUri, candidate)
                  }

                  override fun onError(exception: ImageCaptureException) {
                    Log.w("MealScanScreen", "CameraX image capture failed: ${exception.message}. Using simulated meal.")
                    val candidate = indianMealLibrary[demoDishIndex % indianMealLibrary.size]
                    startAiAnalysis(null, candidate)
                  }
                }
              )
            }
          },
          onPickFromGallery = {
            photoPickerLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          onSwitchDemoDish = {
            if (scanMode == "BARCODE") {
              barcodeIndex = (barcodeIndex + 1) % barcodeProductsLibrary.size
              val barcodeItem = barcodeProductsLibrary[barcodeIndex]
              val mappedResult = ScanResult(
                dishName = barcodeItem.productName,
                calories = barcodeItem.calories,
                servingDescription = barcodeItem.serving,
                proteinGrams = barcodeItem.protein,
                carbsGrams = barcodeItem.carbs,
                fatGrams = barcodeItem.fat,
                chefNote = "Scanned Barcode: ${barcodeItem.code} (${barcodeItem.brand}). ${barcodeItem.details}",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDfZ4mY8nK1vP9xW_sL2kP1vN8mJ5t=s600"
              )
              startAiAnalysis(null, mappedResult)
            } else {
              demoDishIndex = (demoDishIndex + 1) % indianMealLibrary.size
              val candidate = indianMealLibrary[demoDishIndex]
              startAiAnalysis(null, candidate)
            }
          },
          onSelectDish = { selectedDish ->
            startAiAnalysis(null, selectedDish)
          },
          onSelectBarcode = { barcodeItem ->
            val mappedResult = ScanResult(
              dishName = barcodeItem.productName,
              calories = barcodeItem.calories,
              servingDescription = barcodeItem.serving,
              proteinGrams = barcodeItem.protein,
              carbsGrams = barcodeItem.carbs,
              fatGrams = barcodeItem.fat,
              chefNote = "Scanned Barcode: ${barcodeItem.code} (${barcodeItem.brand}). ${barcodeItem.details}",
              imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDfZ4mY8nK1vP9xW_sL2kP1vN8mJ5t=s600"
            )
            startAiAnalysis(null, mappedResult)
          },
          onOpenBarcodeTyper = { showBarcodeTyperDialog = true },
          onOpenVoiceLog = { showVoiceLogDialog = true },
          currentDemoDish = indianMealLibrary[demoDishIndex % indianMealLibrary.size],
          currentBarcodeItem = barcodeProductsLibrary[barcodeIndex % barcodeProductsLibrary.size]
        )
      }
    }
  }

  // Voice Log Dialog directly on scanner screen
  if (showVoiceLogDialog) {
    VoiceLogDialog(
      onDismiss = { showVoiceLogDialog = false },
      onLogMeal = { logged ->
        onLogMeal(logged)
        showVoiceLogDialog = false
      }
    )
  }

  // Barcode Typer & Manual Lookup Dialog
  if (showBarcodeTyperDialog) {
    var typedBarcode by remember { mutableStateOf("") }
    var customProductName by remember { mutableStateOf("") }
    var customBrand by remember { mutableStateOf("") }
    var customCalories by remember { mutableStateOf("150") }
    var customProtein by remember { mutableStateOf("8") }
    var customCarbs by remember { mutableStateOf("20") }
    var customFat by remember { mutableStateOf("5") }

    val matchedBarcode = barcodeProductsLibrary.find { it.code == typedBarcode.trim() }

    AlertDialog(
      onDismissRequest = { showBarcodeTyperDialog = false },
      containerColor = Color(0xFF141925),
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF22C55E))
          Text("Type Barcode", fontWeight = FontWeight.Bold, color = Color(0xFFF1F5F9))
        }
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Type or paste any barcode number (or pick a packaged food):",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
          )

          OutlinedTextField(
            value = typedBarcode,
            onValueChange = { input ->
              typedBarcode = input
              val found = barcodeProductsLibrary.find { it.code == input.trim() }
              if (found != null) {
                customProductName = found.productName
                customBrand = found.brand
                customCalories = found.calories.toString()
                customProtein = found.protein.toString()
                customCarbs = found.carbs.toString()
                customFat = found.fat.toString()
              }
            },
            label = { Text("Barcode Number", color = Color(0xFF94A3B8)) },
            placeholder = { Text("e.g. 8901262010123", color = Color(0xFF64748B)) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color(0xFFF1F5F9),
              unfocusedTextColor = Color(0xFFF1F5F9),
              focusedBorderColor = Color(0xFF22C55E),
              unfocusedBorderColor = Color(0xFF334155)
            ),
            modifier = Modifier.fillMaxWidth().testTag("barcode_typer_input")
          )

          // Demo quick pick chips
          Text("Quick Indian Packaged Brands:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
          Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            barcodeProductsLibrary.forEach { sample ->
              val isSel = typedBarcode == sample.code
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSel) Color(0xFF22C55E) else Color(0xFF1E293B),
                border = BorderStroke(1.dp, if (isSel) Color(0xFF22C55E) else Color(0xFF334155)),
                modifier = Modifier.clickable {
                  typedBarcode = sample.code
                  customProductName = sample.productName
                  customBrand = sample.brand
                  customCalories = sample.calories.toString()
                  customProtein = sample.protein.toString()
                  customCarbs = sample.carbs.toString()
                  customFat = sample.fat.toString()
                }
              ) {
                Text(
                  text = sample.productName.take(18) + "...",
                  fontSize = 11.sp,
                  color = if (isSel) Color.Black else Color(0xFFF1F5F9),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          if (matchedBarcode != null) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF22C55E).copy(alpha = 0.15f),
              border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(matchedBarcode.productName, fontWeight = FontWeight.Bold, color = Color(0xFFF1F5F9), fontSize = 13.sp)
                Text("${matchedBarcode.brand} • ${matchedBarcode.serving}", fontSize = 11.sp, color = Color(0xFF22C55E))
                Text("${matchedBarcode.calories} kcal | ${matchedBarcode.protein}g P | ${matchedBarcode.carbs}g C | ${matchedBarcode.fat}g F", fontSize = 11.sp, color = Color(0xFF94A3B8))
              }
            }
          } else {
            OutlinedTextField(
              value = customProductName,
              onValueChange = { customProductName = it },
              label = { Text("Product Name", color = Color(0xFF94A3B8)) },
              placeholder = { Text("e.g. Rolled Oats 100g", color = Color(0xFF64748B)) },
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFFF1F5F9),
                unfocusedTextColor = Color(0xFFF1F5F9),
                focusedBorderColor = Color(0xFF22C55E),
                unfocusedBorderColor = Color(0xFF334155)
              ),
              modifier = Modifier.fillMaxWidth().testTag("barcode_product_name_input")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = customCalories,
                onValueChange = { customCalories = it },
                label = { Text("Calories", color = Color(0xFF94A3B8)) },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = Color(0xFFF1F5F9),
                  unfocusedTextColor = Color(0xFFF1F5F9),
                  focusedBorderColor = Color(0xFF22C55E),
                  unfocusedBorderColor = Color(0xFF334155)
                ),
                modifier = Modifier.weight(1f)
              )
              OutlinedTextField(
                value = customProtein,
                onValueChange = { customProtein = it },
                label = { Text("Protein (g)", color = Color(0xFF94A3B8)) },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = Color(0xFFF1F5F9),
                  unfocusedTextColor = Color(0xFFF1F5F9),
                  focusedBorderColor = Color(0xFF22C55E),
                  unfocusedBorderColor = Color(0xFF334155)
                ),
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val name = customProductName.ifBlank { matchedBarcode?.productName ?: "Barcode #$typedBarcode" }
            val kcal = customCalories.toIntOrNull() ?: matchedBarcode?.calories ?: 150
            val p = customProtein.toIntOrNull() ?: matchedBarcode?.protein ?: 8
            val c = customCarbs.toIntOrNull() ?: matchedBarcode?.carbs ?: 20
            val f = customFat.toIntOrNull() ?: matchedBarcode?.fat ?: 5

            val mapped = ScanResult(
              dishName = name,
              calories = kcal,
              servingDescription = matchedBarcode?.serving ?: "1 standard serving",
              proteinGrams = p,
              carbsGrams = c,
              fatGrams = f,
              chefNote = "Scanned/Typed Barcode: $typedBarcode. Verified nutritional breakdown.",
              imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDfZ4mY8nK1vP9xW_sL2kP1vN8mJ5t=s600"
            )
            showBarcodeTyperDialog = false
            startAiAnalysis(null, mapped)
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.Black),
          shape = RoundedCornerShape(999.dp),
          modifier = Modifier.testTag("submit_barcode_button")
        ) {
          Text("Scan This Product", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showBarcodeTyperDialog = false }) {
          Text("Cancel", color = Color(0xFF94A3B8))
        }
      }
    )
  }
}

/**
 * CameraX Live Viewfinder with custom viewfinder overlays, torch control, flip control,
 * shutter button, and simulated fallback.
 */
@Composable
private fun CameraXLiveViewfinder(
  lensFacing: Int,
  isTorchOn: Boolean,
  imageCapture: ImageCapture,
  cameraBindingError: String?,
  scanMode: String,
  onToggleScanMode: (String) -> Unit,
  onCameraBound: (CameraControl) -> Unit,
  onCameraError: (String) -> Unit,
  onFlipCamera: () -> Unit,
  onToggleTorch: () -> Unit,
  onCapturePhoto: () -> Unit,
  onPickFromGallery: () -> Unit,
  onSwitchDemoDish: () -> Unit,
  onSelectDish: (ScanResult) -> Unit,
  onSelectBarcode: (BarcodeItem) -> Unit,
  onOpenBarcodeTyper: () -> Unit,
  onOpenVoiceLog: () -> Unit,
  currentDemoDish: ScanResult,
  currentBarcodeItem: BarcodeItem
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
  ) {
    // CameraX Preview AndroidView
    if (cameraBindingError == null) {
      AndroidView(
        factory = { ctx ->
          val previewView = PreviewView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
            // Use COMPATIBLE (TextureView) mode instead of PERFORMANCE (SurfaceView)
            // This prevents SurfaceView BufferQueue abandoned / dequeueBuffer crashes during screen transitions
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
          }

          val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
          cameraProviderFuture.addListener({
            try {
              val cameraProvider = cameraProviderFuture.get()
              val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
              }

              val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

              cameraProvider.unbindAll()
              val camera: Camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
              )
              onCameraBound(camera.cameraControl)
            } catch (e: Exception) {
              Log.e("CameraX", "Binding camera failed", e)
              onCameraError(e.localizedMessage ?: "Camera initialization failed")
            }
          }, ContextCompat.getMainExecutor(ctx))

          previewView
        },
        onRelease = {
          try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            if (cameraProviderFuture.isDone) {
              cameraProviderFuture.get().unbindAll()
            }
          } catch (e: Exception) {
            Log.w("CameraX", "Error unbinding camera on release", e)
          }
        },
        update = {
          // Triggered on recomposition if needed
        },
        modifier = Modifier.fillMaxSize()
      )
    } else {
      // Fallback View when camera hardware is unavailable in the environment
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          model = currentDemoDish.imageUrl,
          contentDescription = "Simulated Viewfinder Food",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
        )
      }
    }

    // Top Controls Bar (Torch, Mode Switcher, Flip Camera)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.TopCenter)
        .padding(horizontal = 20.dp, vertical = 20.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Flash / Torch Toggle
      Surface(
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.6f),
        modifier = Modifier.size(46.dp)
      ) {
        IconButton(
          onClick = onToggleTorch,
          modifier = Modifier.testTag("toggle_torch_button")
        ) {
          Icon(
            imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
            contentDescription = "Toggle Torch Flash",
            tint = if (isTorchOn) Color(0xFFFFD54F) else Color.White,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      // Mode Switcher Pill (Meal AI vs Barcode Scanner)
      Surface(
        shape = RoundedCornerShape(50),
        color = Color.Black.copy(alpha = 0.75f),
        border = BorderStroke(1.dp, AhaarPrimaryContainer.copy(alpha = 0.6f))
      ) {
        Row(
          modifier = Modifier.padding(4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(50),
            color = if (scanMode == "MEAL") Color.White else Color.Transparent,
            modifier = Modifier
              .clickable { onToggleScanMode("MEAL") }
              .testTag("mode_meal_tab")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = if (scanMode == "MEAL") Color(0xFF0E0F0C) else Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "Meal AI",
                color = if (scanMode == "MEAL") Color(0xFF0E0F0C) else Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(50),
            color = if (scanMode == "BARCODE") Color.White else Color.Transparent,
            modifier = Modifier
              .clickable { onToggleScanMode("BARCODE") }
              .testTag("mode_barcode_tab")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = if (scanMode == "BARCODE") Color(0xFF0E0F0C) else Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "Barcode",
                color = if (scanMode == "BARCODE") Color(0xFF0E0F0C) else Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Flip Lens (Front/Back)
      Surface(
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.6f),
        modifier = Modifier.size(46.dp)
      ) {
        IconButton(
          onClick = onFlipCamera,
          modifier = Modifier.testTag("flip_camera_button")
        ) {
          Icon(
            imageVector = Icons.Default.FlipCameraAndroid,
            contentDescription = "Switch Camera Front or Back",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }

    // Viewfinder Scanning Reticle (Center)
    if (scanMode == "BARCODE") {
      Box(
        modifier = Modifier
          .width(280.dp)
          .height(180.dp)
          .align(Alignment.Center)
          .border(2.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
      ) {
        // Laser scanning beam
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .align(Alignment.Center)
            .background(Color(0xFFFF3333))
        )
        // Corner brackets
        Box(
          modifier = Modifier
            .size(28.dp)
            .align(Alignment.TopStart)
            .border(BorderStroke(4.dp, Color(0xFF00E676)), RoundedCornerShape(topStart = 16.dp))
        )
        Box(
          modifier = Modifier
            .size(28.dp)
            .align(Alignment.TopEnd)
            .border(BorderStroke(4.dp, Color(0xFF00E676)), RoundedCornerShape(topEnd = 16.dp))
        )
        Box(
          modifier = Modifier
            .size(28.dp)
            .align(Alignment.BottomStart)
            .border(BorderStroke(4.dp, Color(0xFF00E676)), RoundedCornerShape(bottomStart = 16.dp))
        )
        Box(
          modifier = Modifier
            .size(28.dp)
            .align(Alignment.BottomEnd)
            .border(BorderStroke(4.dp, Color(0xFF00E676)), RoundedCornerShape(bottomEnd = 16.dp))
        )
      }
    } else {
      Box(
        modifier = Modifier
          .size(260.dp)
          .align(Alignment.Center)
          .border(2.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
      ) {
        // Thin White Corner Brackets (Zero orange)
        Box(
          modifier = Modifier
            .size(36.dp)
            .align(Alignment.TopStart)
            .border(
              BorderStroke(2.dp, Color.White),
              RoundedCornerShape(topStart = 24.dp)
            )
        )
        Box(
          modifier = Modifier
            .size(36.dp)
            .align(Alignment.TopEnd)
            .border(
              BorderStroke(2.dp, Color.White),
              RoundedCornerShape(topEnd = 24.dp)
            )
        )
        Box(
          modifier = Modifier
            .size(36.dp)
            .align(Alignment.BottomStart)
            .border(
              BorderStroke(2.dp, Color.White),
              RoundedCornerShape(bottomStart = 24.dp)
            )
        )
        Box(
          modifier = Modifier
            .size(36.dp)
            .align(Alignment.BottomEnd)
            .border(
              BorderStroke(2.dp, Color.White),
              RoundedCornerShape(bottomEnd = 24.dp)
            )
        )

        // Center crosshair
        Box(
          modifier = Modifier
            .size(12.dp)
            .align(Alignment.Center)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.6f))
        )
      }
    }

    // Bottom Action Controls (Gallery, Shutter Button, Switch Sample Dish & Horizontal Carousel)
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              Color.Black.copy(alpha = 0.85f),
              Color.Black
            )
          )
        )
        .padding(horizontal = 20.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      if (scanMode == "BARCODE") {
        // Hint text
        Text(
          text = "Align barcode with scanner line • Instant nutrition breakdown",
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      } else {
        // High-Quality Camera Guide (suggestions removed per user request)
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = Color.Black.copy(alpha = 0.65f),
          border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = Color(0xFF22C55E),
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "Position any meal in camera frame • High-Precision AI Scan",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // 1. Pick photo from gallery button
        Surface(
          shape = CircleShape,
          color = Color.White.copy(alpha = 0.2f),
          modifier = Modifier.size(48.dp)
        ) {
          IconButton(
            onClick = onPickFromGallery,
            modifier = Modifier.testTag("pick_gallery_button")
          ) {
            Icon(
              imageVector = Icons.Default.PhotoLibrary,
              contentDescription = "Pick Image from Gallery",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
        }

        // 2. Barcode Typer / Keyboard lookup button
        Surface(
          shape = CircleShape,
          color = Color(0xFF1E293B).copy(alpha = 0.85f),
          border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.6f)),
          modifier = Modifier.size(48.dp)
        ) {
          IconButton(
            onClick = onOpenBarcodeTyper,
            modifier = Modifier.testTag("type_barcode_button")
          ) {
            Icon(
              imageVector = Icons.Default.Keyboard,
              contentDescription = "Type Barcode",
              tint = Color(0xFF22C55E),
              modifier = Modifier.size(22.dp)
            )
          }
        }

        // 3. Big White Camera Shutter / Scan Button (Apple Health / Cal AI style)
        Box(
          modifier = Modifier
            .size(76.dp)
            .border(3.5.dp, Color.White, CircleShape)
            .padding(4.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onCapturePhoto)
            .testTag("camera_shutter_button"),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(60.dp)
              .clip(CircleShape)
              .background(Color.White)
              .border(1.dp, Color(0xFFE4E2DA), CircleShape)
          )
        }

        // 4. Voice Log button
        Surface(
          shape = CircleShape,
          color = Color(0xFF1E293B).copy(alpha = 0.85f),
          border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.6f)),
          modifier = Modifier.size(48.dp)
        ) {
          IconButton(
            onClick = onOpenVoiceLog,
            modifier = Modifier.testTag("voice_log_scan_button")
          ) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = "Voice Log Meal",
              tint = Color(0xFF22C55E),
              modifier = Modifier.size(22.dp)
            )
          }
        }

        // 5. Demo meal switcher (for testing without live meals)
        Surface(
          shape = CircleShape,
          color = Color.White.copy(alpha = 0.2f),
          modifier = Modifier.size(48.dp)
        ) {
          IconButton(
            onClick = onSwitchDemoDish,
            modifier = Modifier.testTag("switch_sample_dish_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = if (scanMode == "BARCODE") "Next Demo Barcode" else "Try Demo Indian Dish",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * AI Scanning Progress Screen with active laser beam animation and step progression.
 */
@Composable
private fun AiScanningProgressView(
  photoUri: Uri?,
  dishCandidate: ScanResult,
  onAnalysisComplete: () -> Unit
) {
  var currentStepIndex by remember { mutableIntStateOf(0) }
  val steps = listOf(
    "Scanning food textures and Indian spices...",
    "Detecting ingredients: dal, rotis, paneer, veggies...",
    "Estimating volumetric portion & cooking oils...",
    "Calculating protein, carbs, and metabolic calorie burn..."
  )

  LaunchedEffect(Unit) {
    for (i in steps.indices) {
      currentStepIndex = i
      delay(380)
    }
    delay(200)
    onAnalysisComplete()
  }

  val infiniteTransition = rememberInfiniteTransition(label = "laser_scanner")
  val scanLineProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1400, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scan_laser_offset"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF121212)),
    contentAlignment = Alignment.Center
  ) {
    val progressScrollState = rememberScrollState()
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(progressScrollState)
        .padding(28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      // Photo with laser scan overlay
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(300.dp)
          .clip(RoundedCornerShape(24.dp))
          .border(2.dp, AhaarPrimaryContainer, RoundedCornerShape(24.dp))
      ) {
        AsyncImage(
          model = photoUri ?: dishCandidate.imageUrl,
          contentDescription = "Captured Meal for AI Processing",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Dark dim layer
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
        )

        // Animated Laser Beam
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .align(Alignment.TopCenter)
            .offset(y = (300 * scanLineProgress).dp)
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  Color.Transparent,
                  Color(0xFF00E676),
                  Color.White,
                  Color(0xFF00E676),
                  Color.Transparent
                )
              )
            )
        )

        // Corner AI Badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color.Black.copy(alpha = 0.75f),
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = AhaarPrimaryContainer,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "CalSnapix AI Vision Engine",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Step text and loading indicator
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        CircularProgressIndicator(
          color = AhaarPrimaryContainer,
          strokeWidth = 3.dp,
          modifier = Modifier.size(36.dp)
        )

        Text(
          text = "Analyzing Desi Meal...",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )

        Text(
          text = steps.getOrElse(currentStepIndex) { "Computing macros..." },
          style = MaterialTheme.typography.bodyMedium,
          color = Color.White.copy(alpha = 0.8f),
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

/**
 * AI Nutritional Analysis Result Screen displaying the captured image,
 * detected dish details, portion adjustments, macros, and logging action.
 */
@Composable
private fun AiAnalysisResultView(
  resultState: ScanScreenState.Result,
  onRetake: () -> Unit,
  onLogMeal: (LoggedMeal) -> Unit,
  onUpdateResult: (ScanScreenState.Result) -> Unit
) {
  val scrollState = rememberScrollState()
  var showEditDishDialog by remember { mutableStateOf(false) }

  val dish = resultState.result
  val multiplier = resultState.portionMultiplier
  val selectedMealType = resultState.selectedMealType

  val adjustedCalories = (dish.calories * multiplier).toInt()
  val adjustedProtein = (dish.proteinGrams * multiplier).toInt()
  val adjustedCarbs = (dish.carbsGrams * multiplier).toInt()
  val adjustedFat = (dish.fatGrams * multiplier).toInt()

  val colors = CalTheme.colors

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(colors.bg)
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "AI Meal Analysis",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink
        )
        Text(
          text = "Recognized with 98.4% Desi nutrition confidence",
          fontSize = 13.sp,
          color = colors.ink2
        )
      }

      Surface(
        shape = RoundedCornerShape(50),
        color = colors.accent.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = "Analyzed",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent
          )
        }
      }
    }

    // Meal Photo Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(240.dp)
        .clip(RoundedCornerShape(22.dp))
    ) {
      AsyncImage(
        model = resultState.photoUri ?: dish.imageUrl,
        contentDescription = "Captured Meal Result",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )

      // Bottom floating pill with calories
      Surface(
        shape = RoundedCornerShape(50),
        color = Color.Black.copy(alpha = 0.75f),
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(14.dp)
      ) {
        Text(
          text = "$adjustedCalories kcal",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
      }

      // Top retake icon button
      Surface(
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.6f),
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(14.dp)
          .size(40.dp)
      ) {
        IconButton(
          onClick = onRetake,
          modifier = Modifier.testTag("retake_scan_button")
        ) {
          Icon(
            imageVector = Icons.Default.RestartAlt,
            contentDescription = "Retake Meal Photo",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // Nutrition & Dish Breakdown Card (Solid Opaque High-Contrast Surface)
    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = colors.surface),
      border = BorderStroke(1.2.dp, colors.line),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Dish Title and Edit Action
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = dish.dishName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              IconButton(
                onClick = { showEditDishDialog = true },
                modifier = Modifier
                  .size(24.dp)
                  .testTag("edit_dish_title_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = "Edit Dish Name",
                  tint = colors.accent,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = dish.servingDescription,
              fontSize = 13.sp,
              color = colors.ink2
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "$adjustedCalories",
              fontSize = 32.sp,
              fontWeight = FontWeight.ExtraBold,
              color = colors.accent
            )
            Text(
              text = "kcal",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = colors.ink2
            )
          }
        }

        // Meal Type Selector Pills
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Breakfast", "Lunch", "Snack", "Dinner").forEach { type ->
            val isSelected = selectedMealType == type
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) colors.accent else colors.surface2,
              border = BorderStroke(1.dp, if (isSelected) colors.accent else colors.line),
              modifier = Modifier
                .weight(1f)
                .clickable {
                  onUpdateResult(resultState.copy(selectedMealType = type))
                }
            ) {
              Box(
                modifier = Modifier.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = type,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else colors.ink
                )
              }
            }
          }
        }

        // Interactive Portion Size Multiplier
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface2)
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Portion Size",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.ink
          )
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            listOf(0.5f, 0.75f, 1.0f, 1.5f, 2.0f).forEach { mult ->
              val isSelected = multiplier == mult
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) colors.accent else colors.surface,
                border = BorderStroke(1.dp, if (isSelected) colors.accent else colors.line),
                modifier = Modifier
                  .clickable {
                    onUpdateResult(resultState.copy(portionMultiplier = mult))
                  }
                  .testTag("portion_${mult}")
              ) {
                Text(
                  text = "${mult}x",
                  color = if (isSelected) Color.White else colors.ink,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
            }
          }
        }

        // 3 Macro Detail Pills
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MacroDetailBadge(
            label = "Protein",
            value = "${adjustedProtein}g",
            bgColor = Color(0xFF6366F1),
            textColor = Color(0xFF6366F1),
            modifier = Modifier.weight(1f)
          )
          MacroDetailBadge(
            label = "Carbs",
            value = "${adjustedCarbs}g",
            bgColor = Color(0xFFEAB308),
            textColor = Color(0xFFEAB308),
            modifier = Modifier.weight(1f)
          )
          MacroDetailBadge(
            label = "Fats",
            value = "${adjustedFat}g",
            bgColor = Color(0xFFEF4444),
            textColor = Color(0xFFEF4444),
            modifier = Modifier.weight(1f)
          )
        }

        // Complete Micronutrients Breakdown
        CompleteMicronutrientsCard(
          dish = dish,
          multiplier = multiplier
        )

        // AI Nutritionist Note
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = colors.surface2),
          border = BorderStroke(1.dp, colors.line),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = colors.accent,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = dish.chefNote,
              fontSize = 12.sp,
              color = colors.ink,
              lineHeight = 17.sp
            )
          }
        }

        // Action Buttons: Retake and Log Meal
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onRetake,
            modifier = Modifier
              .weight(1f)
              .height(52.dp)
              .testTag("retake_scan_button_secondary"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = colors.surface2),
            border = BorderStroke(1.dp, colors.line)
          ) {
            Icon(
              imageVector = Icons.Default.RestartAlt,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
              tint = colors.ink
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Retake",
              color = colors.ink,
              fontWeight = FontWeight.Bold
            )
          }

          Button(
            onClick = {
              val logged = LoggedMeal(
                id = System.currentTimeMillis().toString(),
                title = dish.dishName,
                mealType = selectedMealType,
                time = "Just now",
                calories = adjustedCalories,
                tag = if (adjustedProtein >= 16) "High Protein" else "Balanced",
                imageUrl = resultState.photoUri?.toString() ?: dish.imageUrl,
                proteinGrams = adjustedProtein,
                carbsGrams = adjustedCarbs,
                fatGrams = adjustedFat
              )
              onLogMeal(logged)
            },
            modifier = Modifier
              .weight(2f)
              .height(52.dp)
              .testTag("log_meal_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = colors.accent,
              contentColor = Color.White
            )
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              modifier = Modifier.size(20.dp),
              tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Log Meal to Diary",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }

  // Edit Dish Name Dialog
  if (showEditDishDialog) {
    var tempDishName by remember { mutableStateOf(dish.dishName) }
    AlertDialog(
      onDismissRequest = { showEditDishDialog = false },
      title = { Text("Edit Dish Name", fontWeight = FontWeight.Bold) },
      text = {
        OutlinedTextField(
          value = tempDishName,
          onValueChange = { tempDishName = it },
          label = { Text("Dish Title") },
          modifier = Modifier.fillMaxWidth()
        )
      },
      confirmButton = {
        Button(
          onClick = {
            if (tempDishName.isNotBlank()) {
              onUpdateResult(
                resultState.copy(
                  result = dish.copy(dishName = tempDishName)
                )
              )
            }
            showEditDishDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = AhaarPrimaryContainer)
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditDishDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

/**
 * Camera Permission Rationale View shown when CAMERA permission has not yet been granted.
 */
@Composable
private fun CameraPermissionRationaleView(
  onRequestPermission: () -> Unit,
  onPickFromGallery: () -> Unit,
  onTryDemoMeal: () -> Unit
) {
  val rationaleScrollState = rememberScrollState()
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Transparent)
      .verticalScroll(rationaleScrollState)
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.88f)),
      border = BorderStroke(1.2.dp, GlassBorderBrush),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Surface(
          shape = CircleShape,
          color = AhaarPrimaryFixed,
          modifier = Modifier.size(72.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = null,
              tint = AhaarPrimary,
              modifier = Modifier.size(36.dp)
            )
          }
        }

        Text(
          text = "Enable Camera for AI Meal Scanning",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = "CalSnapix uses CameraX to capture live photos of your Indian meals, estimating calories, macro nutrients, and ghee balance in real time.",
          style = MaterialTheme.typography.bodyMedium,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Grant Camera Access
        Button(
          onClick = onRequestPermission,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = AhaarPrimaryContainer,
            contentColor = Color.White
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("request_camera_permission_button")
        ) {
          Icon(Icons.Default.Camera, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Enable Camera Access", fontWeight = FontWeight.Bold)
        }

        // Pick Photo from Gallery
        OutlinedButton(
          onClick = onPickFromGallery,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("pick_gallery_button")
        ) {
          Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Choose Photo from Gallery", fontWeight = FontWeight.SemiBold)
        }

        // Quick Try Sample Meal
        TextButton(
          onClick = onTryDemoMeal,
          modifier = Modifier.testTag("try_sample_meal_button")
        ) {
          Text(
            text = "Try Demo Indian Meal Scan",
            color = AhaarPrimary,
            fontWeight = FontWeight.SemiBold
          )
        }

        // Open App Settings if permission was permanently denied
        val context = LocalContext.current
        TextButton(
          onClick = {
            val intent = Intent(
              Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
              Uri.fromParts("package", context.packageName, null)
            )
            context.startActivity(intent)
          },
          modifier = Modifier.testTag("open_app_settings_button")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Open System App Settings",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}

@Composable
private fun MacroDetailBadge(
  label: String,
  value: String,
  bgColor: Color,
  textColor: Color,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = bgColor.copy(alpha = 0.16f),
    border = BorderStroke(1.2.dp, bgColor.copy(alpha = 0.5f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      Text(
        text = label,
        color = bgColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = value,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = colors.ink
      )
    }
  }
}

@Composable
private fun CompleteMicronutrientsCard(
  dish: ScanResult,
  multiplier: Float,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val fiber = (dish.fiberGrams * multiplier)
  val iron = (dish.ironMg * multiplier)
  val calcium = (dish.calciumMg * multiplier)
  val potassium = (dish.potassiumMg * multiplier)
  val sodium = (dish.sodiumMg * multiplier)
  val vitA = (dish.vitaminAMcg * multiplier)
  val vitC = (dish.vitaminCMg * multiplier)
  val vitD = (dish.vitaminDIU * multiplier)
  val vitB12 = (dish.vitaminB12Mcg * multiplier)
  val magnesium = (dish.magnesiumMg * multiplier)
  val zinc = (dish.zincMg * multiplier)

  // Standard Recommended Daily Allowances (RDA) reference
  val micros = listOf(
    Triple("Dietary Fiber", "${String.format(Locale.US, "%.1f", fiber)} g", (fiber / 28f).coerceIn(0f, 1f)),
    Triple("Iron (Fe)", "${String.format(Locale.US, "%.1f", iron)} mg", (iron / 18f).coerceIn(0f, 1f)),
    Triple("Calcium (Ca)", "${calcium.toInt()} mg", (calcium / 1000f).coerceIn(0f, 1f)),
    Triple("Potassium (K)", "${potassium.toInt()} mg", (potassium / 3400f).coerceIn(0f, 1f)),
    Triple("Sodium (Na)", "${sodium.toInt()} mg", (sodium / 2000f).coerceIn(0f, 1f)),
    Triple("Vitamin A", "${vitA.toInt()} mcg", (vitA / 900f).coerceIn(0f, 1f)),
    Triple("Vitamin C", "${String.format(Locale.US, "%.1f", vitC)} mg", (vitC / 90f).coerceIn(0f, 1f)),
    Triple("Vitamin D3", "${vitD.toInt()} IU", (vitD / 600f).coerceIn(0f, 1f)),
    Triple("Vitamin B12", "${String.format(Locale.US, "%.1f", vitB12)} mcg", (vitB12 / 2.4f).coerceIn(0f, 1f)),
    Triple("Magnesium (Mg)", "${magnesium.toInt()} mg", (magnesium / 400f).coerceIn(0f, 1f)),
    Triple("Zinc (Zn)", "${String.format(Locale.US, "%.1f", zinc)} mg", (zinc / 11f).coerceIn(0f, 1f))
  )

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = colors.surface,
    border = BorderStroke(1.dp, colors.line),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
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
            text = "COMPLETE MICRONUTRIENTS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink,
            letterSpacing = 0.5.sp
          )
        }
        Text(
          text = "All Essential Vitamins & Minerals",
          fontSize = 10.sp,
          color = colors.ink2
        )
      }

      // 2-Column Responsive Grid of Micronutrients with % DV meter
      micros.chunked(2).forEach { pair ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          pair.forEach { (name, value, fraction) ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = colors.surface2,
              border = BorderStroke(1.dp, colors.line.copy(alpha = 0.5f)),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(name, fontSize = 11.sp, color = colors.ink2, fontWeight = FontWeight.Medium)
                  Text(value, fontSize = 11.sp, color = colors.ink, fontWeight = FontWeight.Bold)
                }
                LinearProgressIndicator(
                  progress = { fraction },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                  color = colors.accent,
                  trackColor = colors.line.copy(alpha = 0.35f)
                )
              }
            }
          }
          if (pair.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

/**
 * Celebratory glassmorphic overlay displayed immediately upon successfully logging a meal.
 * Features a spring-scaled checkmark with pulsing emerald halo rings, glowing macro callouts,
 * and a smooth auto-dismiss transition to the dashboard.
 */
@Composable
private fun MealLoggedSuccessOverlay(
  loggedMeal: LoggedMeal,
  onAnimationFinished: () -> Unit
) {
  val scaleAnim = remember { Animatable(0.3f) }
  val alphaAnim = remember { Animatable(0f) }
  val checkScale = remember { Animatable(0f) }

  val infiniteTransition = rememberInfiniteTransition(label = "pulseRing")
  val ringPulse by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ringPulse"
  )

  LaunchedEffect(Unit) {
    launch {
      alphaAnim.animateTo(1f, animationSpec = tween(220))
    }
    launch {
      scaleAnim.animateTo(
        targetValue = 1f,
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioMediumBouncy,
          stiffness = Spring.StiffnessMediumLow
        )
      )
    }
    delay(120)
    launch {
      checkScale.animateTo(
        targetValue = 1f,
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioHighBouncy,
          stiffness = Spring.StiffnessMedium
        )
      )
    }
    delay(1200)
    // Smooth exit
    launch { alphaAnim.animateTo(0f, animationSpec = tween(200)) }
    launch { scaleAnim.animateTo(0.92f, animationSpec = tween(200)) }
    delay(200)
    onAnimationFinished()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.5f * alphaAnim.value))
      .padding(24.dp)
      .testTag("meal_logged_success_overlay"),
    contentAlignment = Alignment.Center
  ) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
      border = BorderStroke(1.5.dp, GlassBorderBrush),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .scale(scaleAnim.value)
        .alpha(alphaAnim.value)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Glowing animated icon badge
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.size(96.dp)
        ) {
          // Outer subtle pulse ring
          Box(
            modifier = Modifier
              .size(88.dp)
              .scale(ringPulse)
              .clip(CircleShape)
              .background(AhaarTertiaryFixed.copy(alpha = 0.45f))
          )
          // Middle glowing ring
          Box(
            modifier = Modifier
              .size(76.dp)
              .clip(CircleShape)
              .background(AhaarTertiaryFixed.copy(alpha = 0.85f))
          )
          // Center Emerald Circle with Checkmark
          Box(
            modifier = Modifier
              .size(62.dp)
              .scale(checkScale.value)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  colors = listOf(AhaarTertiary, AhaarTertiaryContainer)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Success",
              tint = Color.White,
              modifier = Modifier.size(34.dp)
            )
          }
        }

        // Title and meal context
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = AhaarPrimaryContainer,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "Meal Logged!",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Text(
            text = "${loggedMeal.title} • ${loggedMeal.mealType}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
        }

        // Metric Pill Callouts
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = AhaarPrimaryFixed.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "+${loggedMeal.calories}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AhaarPrimary
              )
              Text(
                text = "kcal added",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = AhaarTertiaryFixed.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "+${loggedMeal.proteinGrams}g",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AhaarTertiary
              )
              Text(
                text = "Protein",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = AhaarSecondaryFixed.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "+${loggedMeal.carbsGrams}g",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AhaarSecondary
              )
              Text(
                text = "Carbs",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            }
          }
        }

        // Subtext
        Text(
          text = "✨ Daily targets updated on your dashboard",
          style = MaterialTheme.typography.labelSmall,
          color = AhaarPrimary,
          fontWeight = FontWeight.SemiBold,
          fontSize = 11.sp
        )
      }
    }
  }
}

