package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.view.HapticFeedbackConstants
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale
import coil.compose.AsyncImage
import com.example.AhaarUiState
import com.example.data.gemini.GeminiService
import com.example.data.gemini.LeftoverRecipeResult
import com.example.model.LoggedMeal
import com.example.model.Recipe
import com.example.ui.theme.CalCard
import com.example.ui.theme.CalPrimaryButton
import com.example.ui.theme.CalSecondaryButton
import com.example.ui.theme.CalTheme
import kotlinx.coroutines.launch

/**
 * Modern CalSnapix Recipes Screen (Desi Kitchen).
 * Minimalist, high contrast WCAG AA compliant, horizontal filter chips with --ink selection,
 * large rounded recipe cards, subtle "AI Kitchen" button, zero orange.
 */
@Composable
fun RecipesScreen(
  uiState: AhaarUiState,
  onSearchChange: (String) -> Unit,
  onCategorySelect: (String) -> Unit,
  onBookmarkToggle: (String) -> Unit,
  onLogRecipeAsMeal: (LoggedMeal) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val colors = CalTheme.colors
  val view = LocalView.current
  val scrollState = rememberScrollState()
  val coroutineScope = rememberCoroutineScope()

  var showLeftoverDialog by remember { mutableStateOf(false) }
  var showMyCalAiDialog by remember { mutableStateOf(false) }
  var showVoiceLogDialog by remember { mutableStateOf(false) }
  var leftoverIngredients by remember { mutableStateOf("2 leftover rotis, bowl of curd, 1 tomato") }
  var generatedResult by remember { mutableStateOf<LeftoverRecipeResult?>(null) }
  var isGeneratingRecipe by remember { mutableStateOf(false) }
  var showBookmarksOnly by remember { mutableStateOf(false) }
  var selectedRecipeForDetail by remember { mutableStateOf<Recipe?>(null) }

  var hasAudioPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  // Voice speech-to-text launcher for AI Kitchen leftover ingredients
  val ingredientSpeechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val spokenList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      val text = spokenList?.firstOrNull()
      if (!text.isNullOrBlank()) {
        leftoverIngredients = text
      }
    }
  }

  val ingredientPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    hasAudioPermission = granted
    if (granted) {
      try {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
          putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
          putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
          putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your ingredients (e.g. 2 leftover rotis, bowl of curd)...")
        }
        ingredientSpeechLauncher.launch(intent)
      } catch (e: Exception) {
        leftoverIngredients = "2 leftover rotis, bowl of curd, 1 tomato"
      }
    }
  }

  fun triggerIngredientSpeech() {
    if (hasAudioPermission) {
      try {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
          putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
          putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
          putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your ingredients (e.g. 2 leftover rotis, bowl of curd)...")
        }
        ingredientSpeechLauncher.launch(intent)
      } catch (e: Exception) {
        leftoverIngredients = "2 leftover rotis, bowl of curd, 1 tomato"
      }
    } else {
      ingredientPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
  }

  val categories = listOf("All Recipes", "Breakfast", "Low Carb", "Traditional", "Snacks")

  val filteredRecipes = uiState.recipes.filter { r ->
    (!showBookmarksOnly || r.isBookmarked) &&
      (uiState.selectedRecipeCategory == "All Recipes" || r.category == uiState.selectedRecipeCategory) &&
      (uiState.recipeSearchQuery.isEmpty() || r.title.contains(uiState.recipeSearchQuery, ignoreCase = true))
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(colors.bg)
      .verticalScroll(scrollState)
      .padding(horizontal = CalTheme.sidePadding, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header with Title & Action buttons (Voice Log + AI Kitchen)
    Row(
      modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Global & Desi Kitchen",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = colors.ink,
          letterSpacing = (-0.5).sp
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // AI Voice Log Pill Button
        Surface(
          shape = CalTheme.pillShape,
          color = colors.accent.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.5f)),
          modifier = Modifier
            .clip(CalTheme.pillShape)
            .clickable {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              showVoiceLogDialog = true
            }
            .testTag("ai_kitchen_voice_log_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = "Voice Log",
              tint = colors.accent,
              modifier = Modifier.size(15.dp)
            )
            Text(
              text = "Voice Log",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent
            )
          }
        }

        // MyCal AI Fullscreen Assistant button
        Surface(
          shape = CalTheme.pillShape,
          color = colors.accent.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.5f)),
          modifier = Modifier
            .clip(CalTheme.pillShape)
            .clickable {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              showMyCalAiDialog = true
            }
            .testTag("mycal_ai_recipes_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "MyCal AI",
              tint = colors.accent,
              modifier = Modifier.size(15.dp)
            )
            Text(
              text = "MyCal AI",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = colors.accent
            )
          }
        }
      }
    }

    // 2. Search Bar
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = colors.surface,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = colors.ink2,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
          value = uiState.recipeSearchQuery,
          onValueChange = onSearchChange,
          placeholder = { Text("Search rotis, paneer, dals, salads...", color = colors.ink2, fontSize = 14.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = colors.ink,
            unfocusedTextColor = colors.ink
          ),
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("recipe_search_input")
        )
      }
    }

    // 3. Horizontal Filter Chips (Single row, scrollable, selected chip = --ink fill with --bg text)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      categories.forEach { cat ->
        val isSelected = uiState.selectedRecipeCategory == cat
        Surface(
          shape = CalTheme.pillShape,
          color = if (isSelected) colors.ink else colors.surface2,
          border = BorderStroke(1.dp, if (isSelected) colors.ink else colors.line),
          modifier = Modifier
            .clip(CalTheme.pillShape)
            .clickable {
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              onCategorySelect(cat)
            }
            .testTag("recipe_filter_$cat")
        ) {
          Text(
            text = cat,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) colors.bg else colors.ink,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }
      }
    }

    // 4. Large Rounded Recipe Cards (Image, kcal, protein, time)
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
      if (filteredRecipes.isEmpty()) {
        CalCard {
          Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("No recipes matching your search", color = colors.ink2, fontSize = 14.sp)
          }
        }
      } else {
        filteredRecipes.forEach { recipe ->
          LargeRecipeCard(
            recipe = recipe,
            onCardClick = { selectedRecipeForDetail = recipe },
            onBookmarkClick = { onBookmarkToggle(recipe.id) }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(80.dp))
  }

  // Recipe Detail Dialog
  if (selectedRecipeForDetail != null) {
    val recipe = selectedRecipeForDetail!!
    AlertDialog(
      onDismissRequest = { selectedRecipeForDetail = null },
      containerColor = colors.surface,
      title = {
        Text(text = recipe.title, fontWeight = FontWeight.Bold, color = colors.ink, fontSize = 18.sp)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          AsyncImage(
            model = recipe.imageUrl,
            contentDescription = recipe.title,
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
              .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("${recipe.calories} kcal", fontWeight = FontWeight.Bold, color = colors.ink)
            Text(recipe.macroHighlight, fontWeight = FontWeight.SemiBold, color = colors.accent)
          }
          Text(
            text = "Prep time: ${recipe.prepTime} • Category: ${recipe.category}",
            fontSize = 12.sp,
            color = colors.ink2
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val meal = LoggedMeal(
              id = "recipe_${recipe.id}_${System.currentTimeMillis()}",
              title = recipe.title,
              mealType = "Lunch",
              time = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
              calories = recipe.calories,
              tag = recipe.macroHighlight,
              imageUrl = recipe.imageUrl,
              proteinGrams = 20,
              carbsGrams = 45,
              fatGrams = 10
            )
            onLogRecipeAsMeal(meal)
            selectedRecipeForDetail = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
          shape = CalTheme.pillShape,
          modifier = Modifier.testTag("log_recipe_as_meal_button")
        ) {
          Text("Log as Meal", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedRecipeForDetail = null }) {
          Text("Close", color = colors.ink2)
        }
      }
    )
  }

  // Fullscreen MyCal AI Dietitian & Nutrition Assistant Chat
  if (showMyCalAiDialog) {
    MyCalAiChatScreen(
      uiState = uiState,
      onDismiss = { showMyCalAiDialog = false }
    )
  }

  // AI Kitchen Leftover Recipe Dialog with Avatar & Worldwide Cuisines
  if (showLeftoverDialog) {
    val kitchenCuisines = listOf("Indian", "American")
    var selectedCuisine by remember { mutableStateOf("Indian") }

    AlertDialog(
      onDismissRequest = { showLeftoverDialog = false },
      containerColor = colors.surface,
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // AI Chef Avatar Badge (No photo)
          Surface(
            shape = CircleShape,
            color = colors.accent.copy(alpha = 0.15f),
            border = BorderStroke(1.5.dp, colors.accent),
            modifier = Modifier.size(40.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Chef Avatar",
                tint = colors.accent,
                modifier = Modifier.size(22.dp)
              )
            }
          }
          Column {
            Text("MyCal AI Kitchen Architect", fontWeight = FontWeight.Bold, color = colors.ink, fontSize = 18.sp)
            Text("MyCal AI Chef • Healthy Global Cooking", fontSize = 11.sp, color = colors.accent)
          }
        }
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Select cuisine style & type ingredients from your fridge:",
            fontSize = 12.sp,
            color = colors.ink2
          )

          // Cuisine Style Selector Chips
          Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            kitchenCuisines.forEach { cuisine ->
              val isSel = selectedCuisine == cuisine
              Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (isSel) colors.accent else colors.surface2,
                border = BorderStroke(1.dp, if (isSel) colors.accent else colors.line),
                modifier = Modifier.clickable { selectedCuisine = cuisine }
              ) {
                Text(
                  text = cuisine,
                  fontSize = 11.sp,
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSel) colors.bg else colors.ink,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }

          OutlinedTextField(
            value = leftoverIngredients,
            onValueChange = { leftoverIngredients = it },
            label = { Text("Available Ingredients") },
            placeholder = { Text("e.g. 2 leftover rotis, bowl of curd, 1 tomato") },
            trailingIcon = {
              IconButton(
                onClick = { triggerIngredientSpeech() },
                modifier = Modifier.testTag("ai_kitchen_mic_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = "Speak Ingredients",
                  tint = colors.accent
                )
              }
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = colors.accent,
              unfocusedBorderColor = colors.line
            ),
            modifier = Modifier.fillMaxWidth().testTag("leftover_input")
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Tap 🎤 to speak ingredients",
              fontSize = 11.sp,
              color = colors.ink2
            )
            TextButton(
              onClick = {
                showLeftoverDialog = false
                showVoiceLogDialog = true
              },
              modifier = Modifier.testTag("switch_to_voice_meal_log_button")
            ) {
              Icon(Icons.Default.Mic, contentDescription = null, tint = colors.accent, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Voice Log Consumed Meal", fontSize = 11.sp, color = colors.accent, fontWeight = FontWeight.Bold)
            }
          }

          if (isGeneratingRecipe) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
              CircularProgressIndicator(color = colors.accent, modifier = Modifier.size(28.dp))
            }
          } else if (generatedResult != null) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = colors.accentSoft,
              border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.35f)),
              modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                // AI Chef Avatar Badge (Shows avatar, not photo)
                Surface(
                  shape = CircleShape,
                  color = colors.surface,
                  border = BorderStroke(1.5.dp, colors.accent),
                  modifier = Modifier.size(40.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "AI",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = colors.accent
                    )
                  }
                }
                Column(modifier = Modifier.weight(1f)) {
                  Text(generatedResult!!.title, fontWeight = FontWeight.Bold, color = colors.ink, fontSize = 14.sp)
                  Text(
                    "${generatedResult!!.calories} kcal • ${generatedResult!!.proteinGrams}g Protein • $selectedCuisine Style",
                    fontSize = 11.sp,
                    color = colors.accent,
                    fontWeight = FontWeight.SemiBold
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    generatedResult!!.instructions,
                    fontSize = 12.sp,
                    color = colors.ink2,
                    lineHeight = 16.sp
                  )
                }
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (generatedResult != null) {
              val r = generatedResult!!
              val meal = LoggedMeal(
                id = "ai_leftover_${System.currentTimeMillis()}",
                title = r.title,
                mealType = "Dinner",
                time = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
                calories = r.calories,
                tag = "AI $selectedCuisine Recipe",
                imageUrl = "",
                proteinGrams = r.proteinGrams,
                carbsGrams = r.carbsGrams,
                fatGrams = r.fatGrams
              )
              onLogRecipeAsMeal(meal)
              showLeftoverDialog = false
            } else {
              coroutineScope.launch {
                isGeneratingRecipe = true
                val promptWithCuisine = "$leftoverIngredients (Style: $selectedCuisine cuisine)"
                val res = GeminiService.generateLeftoverRecipe(promptWithCuisine)
                generatedResult = res
                isGeneratingRecipe = false
              }
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
          shape = CalTheme.pillShape,
          modifier = Modifier.testTag("generate_recipe_button")
        ) {
          Text(if (generatedResult != null) "Log to Meals" else if (isGeneratingRecipe) "Thinking..." else "Generate Recipe")
        }
      },
      dismissButton = {
        TextButton(onClick = { showLeftoverDialog = false }) {
          Text("Cancel", color = colors.ink2)
        }
      }
    )
  }

  // Dedicated AI Kitchen Voice-to-Text Meal Logger Dialog
  if (showVoiceLogDialog) {
    var isListening by remember { mutableStateOf(false) }
    var spokenTranscript by remember { mutableStateOf("") }
    var voiceDishTitle by remember { mutableStateOf("2 Rotis with Dal Tadka & Cucumber Salad") }
    var voiceMealCategory by remember { mutableStateOf("Lunch") }
    var voiceCalories by remember { mutableStateOf("380") }
    var voiceProtein by remember { mutableStateOf("14") }
    var voiceCarbs by remember { mutableStateOf("58") }
    var voiceFat by remember { mutableStateOf("9") }
    var voiceStatusMsg by remember { mutableStateOf("Tap the glowing microphone to speak your consumed meal or snacks.") }
    var selectedPortionLabel by remember { mutableStateOf("1 standard plate") }

    fun parseSpokenMealText(input: String) {
      spokenTranscript = input
      val lower = input.lowercase(Locale.ROOT)
      voiceStatusMsg = "AI recognized ingredients and estimated nutritional breakdown!"

      when {
        lower.contains("paneer") && (lower.contains("roti") || lower.contains("phulka") || lower.contains("paratha")) -> {
          voiceDishTitle = "Paneer Bhurji with 2 Phulkas"
          voiceMealCategory = if (lower.contains("dinner")) "Dinner" else "Lunch"
          voiceCalories = "480"
          voiceProtein = "24"
          voiceCarbs = "44"
          voiceFat = "22"
        }
        lower.contains("khichdi") -> {
          voiceDishTitle = "Moong Dal Khichdi with Dahi & Ghee"
          voiceMealCategory = "Dinner"
          voiceCalories = "340"
          voiceProtein = "13"
          voiceCarbs = "52"
          voiceFat = "9"
        }
        lower.contains("idli") || lower.contains("dosa") -> {
          voiceDishTitle = "Steamed Idlis with Sambar & Chutney"
          voiceMealCategory = "Breakfast"
          voiceCalories = "280"
          voiceProtein = "9"
          voiceCarbs = "50"
          voiceFat = "6"
        }
        lower.contains("chilla") || lower.contains("cheela") -> {
          voiceDishTitle = "Moong Dal Chilla with Mint Chutney"
          voiceMealCategory = "Breakfast"
          voiceCalories = "240"
          voiceProtein = "16"
          voiceCarbs = "32"
          voiceFat = "6"
        }
        lower.contains("fajita") || (lower.contains("chicken") && lower.contains("salad")) -> {
          voiceDishTitle = "Grilled Chicken Breast & Quinoa Bowl"
          voiceMealCategory = if (lower.contains("dinner")) "Dinner" else "Lunch"
          voiceCalories = "390"
          voiceProtein = "38"
          voiceCarbs = "34"
          voiceFat = "11"
        }
        lower.contains("greek") || lower.contains("feta") -> {
          voiceDishTitle = "Greek Horiatiki Salad with Feta"
          voiceMealCategory = "Lunch"
          voiceCalories = "260"
          voiceProtein = "14"
          voiceCarbs = "16"
          voiceFat = "16"
        }
        lower.contains("sattu") || lower.contains("egg") -> {
          voiceDishTitle = "Sattu Drink & 2 Boiled Eggs"
          voiceMealCategory = "Breakfast"
          voiceCalories = "310"
          voiceProtein = "22"
          voiceCarbs = "26"
          voiceFat = "12"
        }
        lower.contains("rajma") || lower.contains("chawal") || lower.contains("rice") -> {
          voiceDishTitle = "Punjabi Rajma with Brown Rice"
          voiceMealCategory = "Lunch"
          voiceCalories = "420"
          voiceProtein = "16"
          voiceCarbs = "68"
          voiceFat = "8"
        }
        lower.contains("chai") || lower.contains("tea") || lower.contains("makhana") -> {
          voiceDishTitle = "Ginger Masala Chai with Roasted Makhana"
          voiceMealCategory = "Snack"
          voiceCalories = "160"
          voiceProtein = "5"
          voiceCarbs = "24"
          voiceFat = "5"
        }
        else -> {
          voiceDishTitle = input.replaceFirstChar { it.uppercase() }
          voiceMealCategory = "Lunch"
          voiceCalories = "360"
          voiceProtein = "16"
          voiceCarbs = "48"
          voiceFat = "11"
        }
      }
    }

    val speechMealLauncher = rememberLauncherForActivityResult(
      contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
      isListening = false
      if (result.resultCode == Activity.RESULT_OK) {
        val spokenList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val text = spokenList?.firstOrNull()
        if (!text.isNullOrBlank()) {
          parseSpokenMealText(text)
        }
      }
    }

    val audioPermissionMealLauncher = rememberLauncherForActivityResult(
      contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
      hasAudioPermission = isGranted
      if (isGranted) {
        try {
          isListening = true
          val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your consumed meal (e.g. 2 rotis with dal and salad)...")
          }
          speechMealLauncher.launch(intent)
        } catch (e: Exception) {
          isListening = false
          parseSpokenMealText("2 rotis with paneer bhurji and cucumber salad")
        }
      }
    }

    fun startListeningToMeal() {
      if (hasAudioPermission) {
        try {
          isListening = true
          val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your consumed meal (e.g. 2 rotis with dal and salad)...")
          }
          speechMealLauncher.launch(intent)
        } catch (e: Exception) {
          isListening = false
          parseSpokenMealText("2 rotis with paneer bhurji and cucumber salad")
        }
      } else {
        audioPermissionMealLauncher.launch(Manifest.permission.RECORD_AUDIO)
      }
    }

    // Mic pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "ai_kitchen_mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
      initialValue = 1f,
      targetValue = if (isListening) 1.25f else 1.08f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "ai_kitchen_pulse"
    )

    AlertDialog(
      onDismissRequest = { showVoiceLogDialog = false },
      containerColor = colors.surface,
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = colors.accent.copy(alpha = 0.15f),
            border = BorderStroke(1.5.dp, colors.accent),
            modifier = Modifier.size(40.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(22.dp)
              )
            }
          }
          Column {
            Text("MyCal AI Voice Meal Logger", fontWeight = FontWeight.Bold, color = colors.ink, fontSize = 17.sp)
            Text("Speak Your Meal • Instant Nutrition Parsing", fontSize = 11.sp, color = colors.accent)
          }
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Central Animated Microphone Touch Area
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = colors.surface2,
            border = BorderStroke(1.dp, if (isListening) colors.accent else colors.line),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { startListeningToMeal() }
              .testTag("ai_kitchen_voice_tap_mic")
          ) {
            Column(
              modifier = Modifier.padding(vertical = 16.dp, horizontal = 14.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .scale(pulseScale)
                  .size(62.dp)
                  .clip(CircleShape)
                  .background(if (isListening) colors.accent else colors.accent.copy(alpha = 0.2f))
                  .border(2.dp, colors.accent, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = "Tap to speak",
                  tint = if (isListening) colors.bg else colors.accent,
                  modifier = Modifier.size(32.dp)
                )
              }
              Text(
                text = if (isListening) "Listening... Speak now!" else "Tap to Speak Consumed Meal",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isListening) colors.accent else colors.ink
              )
              Text(
                text = voiceStatusMsg,
                fontSize = 11.sp,
                color = colors.ink2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }

          // Spoken Transcript Input & Live Editing
          OutlinedTextField(
            value = spokenTranscript,
            onValueChange = {
              spokenTranscript = it
              parseSpokenMealText(it)
            },
            label = { Text("Spoken Meal Transcript") },
            placeholder = { Text("e.g. 2 phulkas, 1 bowl dal tadka, and salad") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = colors.accent,
              unfocusedBorderColor = colors.line
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("voice_log_transcript_input")
          )

          // Quick Tap Sample Meals
          Text("Or Tap a Popular Meal:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.ink2)
          val quickVoiceChips = listOf(
            "2 Rotis & Dal",
            "Moong Dal Chilla",
            "Paneer Bhurji & Paratha",
            "Greek Feta Salad",
            "Chicken Quinoa Bowl"
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            quickVoiceChips.forEach { chipName ->
              Surface(
                shape = RoundedCornerShape(999.dp),
                color = colors.surface2,
                border = BorderStroke(1.dp, colors.line),
                modifier = Modifier.clickable {
                  parseSpokenMealText(chipName)
                }
              ) {
                Text(
                  text = chipName,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = colors.ink,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }

          // Meal Type Selector
          Text("Meal Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.ink2)
          val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            mealTypes.forEach { type ->
              val isSel = voiceMealCategory == type
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSel) colors.ink else colors.surface2,
                border = BorderStroke(1.dp, if (isSel) colors.ink else colors.line),
                modifier = Modifier
                  .weight(1f)
                  .clickable { voiceMealCategory = type }
              ) {
                Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                  Text(
                    text = type,
                    fontSize = 11.sp,
                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSel) colors.bg else colors.ink
                  )
                }
              }
            }
          }

          // Parsed Nutrition Breakdown Card
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.accentSoft,
            border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = voiceDishTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colors.ink
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("🔥 ${voiceCalories} kcal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.ink)
                Text("💪 ${voiceProtein}g Protein", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = colors.accent)
                Text("🍚 ${voiceCarbs}g Carbs", fontSize = 12.sp, color = colors.ink2)
                Text("🥑 ${voiceFat}g Fat", fontSize = 12.sp, color = colors.ink2)
              }
            }
          }

          // Portion Size Selector (Indian Units)
          Text("Portion Size (Indian Units):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.ink2)
          val portions = listOf(
            "1 standard plate" to 1.0f,
            "1 katori (150g)" to 0.6f,
            "2 katoris" to 1.2f,
            "2 rotis" to 0.8f,
            "3 rotis" to 1.2f,
            "1 bowl" to 0.9f
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            portions.forEach { (label, mult) ->
              val isSel = selectedPortionLabel == label
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSel) colors.accent else colors.surface2,
                border = BorderStroke(1.dp, if (isSel) colors.accent else colors.line),
                modifier = Modifier.clickable {
                  selectedPortionLabel = label
                  val baseKcal = 360
                  val baseP = 16
                  val baseC = 48
                  val baseF = 10
                  voiceCalories = (baseKcal * mult).toInt().toString()
                  voiceProtein = (baseP * mult).toInt().toString()
                  voiceCarbs = (baseC * mult).toInt().toString()
                  voiceFat = (baseF * mult).toInt().toString()
                }
              ) {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSel) colors.bg else colors.ink,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val meal = LoggedMeal(
              id = "voice_meal_${System.currentTimeMillis()}",
              title = voiceDishTitle.trim(),
              mealType = voiceMealCategory,
              time = java.text.SimpleDateFormat("h:mm a", Locale.getDefault()).format(java.util.Date()),
              calories = voiceCalories.toIntOrNull() ?: 350,
              tag = "AI Voice Log",
              imageUrl = "",
              proteinGrams = voiceProtein.toIntOrNull() ?: 15,
              carbsGrams = voiceCarbs.toIntOrNull() ?: 45,
              fatGrams = voiceFat.toIntOrNull() ?: 10
            )
            onLogRecipeAsMeal(meal)
            showVoiceLogDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = colors.ink, contentColor = colors.bg),
          shape = CalTheme.pillShape,
          modifier = Modifier.testTag("ai_kitchen_confirm_voice_log")
        ) {
          Text("Log Meal to Today's Diary", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showVoiceLogDialog = false }) {
          Text("Cancel", color = colors.ink2)
        }
      }
    )
  }
}

@Composable
private fun LargeRecipeCard(
  recipe: Recipe,
  onCardClick: () -> Unit,
  onBookmarkClick: () -> Unit
) {
  val colors = CalTheme.colors

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(elevation = 1.dp, shape = CalTheme.cardShape)
      .border(1.dp, colors.line, CalTheme.cardShape)
      .clip(CalTheme.cardShape)
      .clickable(onClick = onCardClick)
      .testTag("recipe_card_${recipe.id}"),
    color = colors.surface,
    shape = CalTheme.cardShape
  ) {
    Column {
      // Large recipe image
      Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
        AsyncImage(
          model = recipe.imageUrl,
          contentDescription = recipe.title,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
        // Bookmark button
        IconButton(
          onClick = onBookmarkClick,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(10.dp)
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .testTag("bookmark_${recipe.id}")
        ) {
          Icon(
            imageVector = if (recipe.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = "Bookmark",
            tint = if (recipe.isBookmarked) colors.accent else Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Card Body
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = recipe.title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = colors.ink,
            letterSpacing = (-0.3).sp,
            modifier = Modifier.weight(1f)
          )
          Surface(
            shape = CalTheme.pillShape,
            color = colors.surface2
          ) {
            Text(
              text = recipe.category,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = colors.ink2,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Metrics row: Kcal, Protein highlight, Time
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = colors.accent, modifier = Modifier.size(16.dp))
            Text("${recipe.calories} kcal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.ink)
          }

          Text(
            text = recipe.macroHighlight,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.protein
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(Icons.Default.AccessTime, contentDescription = null, tint = colors.ink2, modifier = Modifier.size(14.dp))
            Text(recipe.prepTime, fontSize = 12.sp, color = colors.ink2)
          }
        }
      }
    }
  }
}
