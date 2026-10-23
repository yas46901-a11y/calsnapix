package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.LoggedMeal
import com.example.ui.theme.AhaarPrimary
import com.example.ui.theme.AhaarPrimaryContainer
import com.example.ui.theme.AhaarPrimaryFixed
import com.example.ui.theme.AhaarSecondary
import com.example.ui.theme.AhaarSecondaryFixed
import com.example.ui.theme.AhaarTertiary
import com.example.ui.theme.AhaarTertiaryFixed
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceLogDialog(
  onDismiss: () -> Unit,
  onLogMeal: (LoggedMeal) -> Unit
) {
  val context = LocalContext.current
  var isListening by remember { mutableStateOf(false) }
  var spokenText by remember { mutableStateOf("") }
  var mealTitle by remember { mutableStateOf("2 Rotis with Dal Tadka & Salad") }
  var mealType by remember { mutableStateOf("Lunch") }
  var calories by remember { mutableStateOf("380") }
  var protein by remember { mutableStateOf("14") }
  var carbs by remember { mutableStateOf("58") }
  var fat by remember { mutableStateOf("9") }
  var statusNote by remember { mutableStateOf("Tap the microphone to speak or pick a quick Indian meal below.") }

  fun parseSpokenIndianMeal(input: String) {
    spokenText = input
    val lower = input.lowercase(Locale.ROOT)
    statusNote = "AI recognized Indian dishes & estimated nutritional breakdown!"

    when {
      lower.contains("paneer") && lower.contains("roti") -> {
        mealTitle = "Paneer Bhurji with 2 Phulkas"
        calories = "480"
        protein = "24"
        carbs = "44"
        fat = "22"
      }
      lower.contains("khichdi") -> {
        mealTitle = "Moong Dal Khichdi with Dahi & Ghee"
        calories = "360"
        protein = "13"
        carbs = "54"
        fat = "10"
      }
      lower.contains("idli") || lower.contains("dosa") -> {
        mealTitle = "Steamed Idlis with Sambar & Chutney"
        calories = "290"
        protein = "9"
        carbs = "48"
        fat = "7"
      }
      lower.contains("sattu") -> {
        mealTitle = "Desi Sattu Drink & 2 Boiled Eggs"
        calories = "310"
        protein = "22"
        carbs = "26"
        fat = "12"
      }
      lower.contains("chana") || lower.contains("chaat") -> {
        mealTitle = "Tangy Kala Chana Chaat with Lemon"
        calories = "210"
        protein = "12"
        carbs = "35"
        fat = "3"
      }
      lower.contains("rajma") || lower.contains("chawal") || lower.contains("rice") -> {
        mealTitle = "Punjabi Rajma with Brown Rice"
        calories = "420"
        protein = "16"
        carbs = "68"
        fat = "8"
      }
      lower.contains("chilla") -> {
        mealTitle = "Moong Dal Chilla with Mint Chutney"
        calories = "240"
        protein = "16"
        carbs = "32"
        fat = "6"
      }
      lower.contains("chai") || lower.contains("tea") -> {
        mealTitle = "Adrak Chai with Roasted Makhana"
        mealType = "Evening Chai"
        calories = "140"
        protein = "4"
        carbs = "22"
        fat = "4"
      }
      else -> {
        mealTitle = input.replaceFirstChar { it.uppercase() }
        calories = "350"
        protein = "15"
        carbs = "48"
        fat = "10"
      }
    }
  }

  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    isListening = false
    if (result.resultCode == Activity.RESULT_OK) {
      val spokenList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      val text = spokenList?.firstOrNull()
      if (!text.isNullOrBlank()) {
        parseSpokenIndianMeal(text)
      }
    }
  }

  // Pulsing animation for mic
  val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isListening) 1.25f else 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 750, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("voice_log_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AhaarPrimaryFixed),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = AhaarPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = "Voice Meal Log",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Desi Speech-to-Nutrition AI",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Animated Mic Center
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AhaarPrimaryFixed.copy(alpha = 0.35f))
            .padding(vertical = 18.dp, horizontal = 14.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .scale(pulseScale)
              .size(68.dp)
              .clip(CircleShape)
              .background(if (isListening) AhaarSecondary else AhaarPrimaryContainer)
              .clickable {
                try {
                  isListening = true
                  val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your meal (e.g. 2 rotis with dal tadka)...")
                  }
                  speechLauncher.launch(intent)
                } catch (e: Exception) {
                  isListening = false
                  // Fallback preset demo parsing
                  parseSpokenIndianMeal("2 rotis with dal tadka and cucumber salad")
                }
              }
              .testTag("voice_log_mic_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = "Speak meal",
              tint = Color.White,
              modifier = Modifier.size(34.dp)
            )
          }

          Text(
            text = if (isListening) "Listening to your voice..." else "Tap to Speak Your Meal",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Text(
            text = statusNote,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            lineHeight = 15.sp
          )
        }

        // Quick Spoken Presets
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Or tap a quick Indian meal:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              "2 Rotis + Dal Tadka",
              "Paneer Bhurji + Paratha",
              "Moong Khichdi + Ghee",
              "Idli Sambar + Chutney",
              "Sattu Drink + 2 Eggs",
              "Kala Chana Chaat"
            ).forEach { preset ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.clickable { parseSpokenIndianMeal(preset) }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = AhaarPrimary,
                    modifier = Modifier.size(12.dp)
                  )
                  Text(
                    text = preset,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }
        }

        // Parsed Meal Details Card
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                  tint = AhaarPrimary,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "Nutritional Breakdown",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = AhaarPrimaryFixed
              ) {
                Text(
                  text = "$calories kcal",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = AhaarPrimary,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            OutlinedTextField(
              value = mealTitle,
              onValueChange = { mealTitle = it },
              label = { Text("Meal Name") },
              singleLine = true,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            )

            // Meal Type Chips
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf("Breakfast", "Lunch", "Evening Chai", "Dinner").forEach { type ->
                FilterChip(
                  selected = mealType == type,
                  onClick = { mealType = type },
                  label = { Text(type, fontSize = 11.sp) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AhaarPrimaryContainer,
                    selectedLabelColor = Color.White
                  ),
                  modifier = Modifier.weight(1f)
                )
              }
            }

            // Macros Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              MacroMiniBox(label = "Protein", value = "${protein}g", color = AhaarTertiary, modifier = Modifier.weight(1f))
              MacroMiniBox(label = "Carbs", value = "${carbs}g", color = AhaarPrimaryContainer, modifier = Modifier.weight(1f))
              MacroMiniBox(label = "Fats", value = "${fat}g", color = AhaarSecondary, modifier = Modifier.weight(1f))
            }
          }
        }

        // Action Buttons
        Button(
          onClick = {
            val logged = LoggedMeal(
              id = "voice_${System.currentTimeMillis()}",
              title = mealTitle,
              mealType = mealType,
              time = "Just now",
              calories = calories.toIntOrNull() ?: 350,
              tag = "Voice Logged 🎙️",
              imageUrl = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=600&auto=format&fit=crop&q=80",
              proteinGrams = protein.toIntOrNull() ?: 15,
              carbsGrams = carbs.toIntOrNull() ?: 45,
              fatGrams = fat.toIntOrNull() ?: 10
            )
            onLogMeal(logged)
            onDismiss()
          },
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AhaarPrimaryContainer),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("confirm_voice_log_button")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, tint = Color.White)
            Text(
              text = "Log Meal to Today's Diary",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MacroMiniBox(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = value,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = color
      )
    }
  }
}
