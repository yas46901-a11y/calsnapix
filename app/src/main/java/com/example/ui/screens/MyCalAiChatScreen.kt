package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.view.HapticFeedbackConstants
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.AhaarUiState
import com.example.data.gemini.GeminiService
import com.example.ui.theme.CalTheme
import kotlinx.coroutines.launch

data class ChatMessage(
  val id: String = System.currentTimeMillis().toString(),
  val isUser: Boolean,
  val text: String,
  val timestamp: String = "Just now"
)

/**
 * Full-screen genuine AI Dietitian chat interface powered by MyCal AI.
 * Offers live two-way conversational Indian dietetics coaching, customized food swaps,
 * macro calibration advice, voice input, and clinical nutrition support.
 */
@Composable
fun MyCalAiChatScreen(
  uiState: AhaarUiState,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  var inputText by remember { mutableStateOf("") }
  var isAiTyping by remember { mutableStateOf(false) }

  val messages = remember {
    mutableStateListOf(
      ChatMessage(
        isUser = false,
        text = "Namaste ${uiState.userName.substringBefore(" ")}! 🙏 I am **MyCal AI**, your personal Clinical Dietitian and Indian Nutrition Coach.\n\nI can help you calibrate your daily calories (${uiState.dailyCalorieTarget} kcal), design high-protein vegetarian meal plans, navigate Indian wedding buffets, or balance your doshas. What's on your mind today?"
      )
    )
  }

  val suggestedPrompts = listOf(
    "🥗 Dinner ideas under 400 kcal?",
    "💪 How to hit 100g protein as a vegetarian?",
    "🍛 Is Dal Makhani okay for fat loss?",
    "☕ Best healthy evening snack swaps for chai?",
    "🌾 Bajra vs Ragi roti for weight loss?",
    "🔥 How to naturally stimulate digestive agni?",
    "🍽️ What to eat at an Indian wedding buffet?"
  )

  // Speech to text launcher
  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
      if (!spoken.isNullOrBlank()) {
        inputText = spoken
      }
    }
  }

  fun sendMessage(queryText: String) {
    val q = queryText.trim()
    if (q.isBlank() || isAiTyping) return

    inputText = ""
    messages.add(ChatMessage(isUser = true, text = q))
    isAiTyping = true

    coroutineScope.launch {
      listState.animateScrollToItem(messages.size - 1)
      val response = GeminiService.askNutritionAssistant(q)
      isAiTyping = false
      messages.add(ChatMessage(isUser = false, text = response))
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    Surface(
      modifier = modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .imePadding(),
      color = colors.bg
    ) {
      Column(
        modifier = Modifier.fillMaxSize()
      ) {
        // 1. Full-screen Header
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = colors.surface,
          border = BorderStroke(1.dp, colors.line)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              IconButton(
                onClick = {
                  view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                  onDismiss()
                },
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(colors.surface2)
                  .testTag("mycal_ai_back_button")
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Close",
                  tint = colors.ink,
                  modifier = Modifier.size(20.dp)
                )
              }

              // AI Badge & Title
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.linearGradient(
                      colors = listOf(colors.accent, Color(0xFF6366F1), Color(0xFF10B981))
                    )
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(22.dp)
                )
              }

              Column {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "MyCal AI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.ink,
                    letterSpacing = (-0.3).sp
                  )
                  // Pulsing active online dot
                  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                  val scale by infiniteTransition.animateFloat(
                    initialValue = 0.8f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                      animation = tween(800),
                      repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse_scale"
                  )
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .scale(scale)
                      .clip(CircleShape)
                      .background(Color(0xFF10B981))
                  )
                }
                Text(
                  text = "AI Clinical Dietitian • Active Coaching",
                  fontSize = 11.sp,
                  color = colors.ink2
                )
              }
            }

            IconButton(
              onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                messages.clear()
                messages.add(
                  ChatMessage(
                    isUser = false,
                    text = "Chat cleared! How can I assist your nutrition and Indian meal plan today?"
                  )
                )
              },
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(colors.surface2)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Restart Chat",
                tint = colors.ink2,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        // 2. Dedicated Search & Type Bar
        var searchNutritionQuery by remember { mutableStateOf("") }
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = colors.surface,
          border = BorderStroke(1.dp, colors.line),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search MyCal AI",
              tint = colors.accent,
              modifier = Modifier.size(20.dp)
            )
            BasicTextField(
              value = searchNutritionQuery,
              onValueChange = { searchNutritionQuery = it },
              singleLine = true,
              textStyle = TextStyle(fontSize = 13.sp, color = colors.ink, fontWeight = FontWeight.Normal),
              modifier = Modifier
                .weight(1f)
                .testTag("mycal_ai_search_bar"),
              decorationBox = { innerTextField ->
                if (searchNutritionQuery.isEmpty()) {
                  Text(
                    text = "Type or search food calories, diet plans...",
                    fontSize = 13.sp,
                    color = colors.ink2
                  )
                }
                innerTextField()
              },
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
              keyboardActions = KeyboardActions(
                onSearch = {
                  if (searchNutritionQuery.isNotBlank()) {
                    sendMessage(searchNutritionQuery)
                    searchNutritionQuery = ""
                  }
                }
              )
            )
            if (searchNutritionQuery.isNotEmpty()) {
              IconButton(
                onClick = {
                  sendMessage(searchNutritionQuery)
                  searchNutritionQuery = ""
                },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Send,
                  contentDescription = "Send Search",
                  tint = colors.accent,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }

        // 3. Weight-Based Nutrition Calculator & Coaching Targets
        var userWeightKg by remember { mutableStateOf(68) }

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = colors.surface,
          border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.35f)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("mycal_ai_weight_macro_card")
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
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = colors.accent, modifier = Modifier.size(16.dp))
                Text("WEIGHT-BASED NUTRITION TARGETS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.ink, letterSpacing = 0.5.sp)
              }

              // Stepper for weight
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                IconButton(
                  onClick = { if (userWeightKg > 40) userWeightKg-- },
                  modifier = Modifier.size(24.dp).clip(CircleShape).background(colors.surface2)
                ) {
                  Icon(Icons.Default.Remove, contentDescription = "Decrease Weight", tint = colors.ink, modifier = Modifier.size(12.dp))
                }
                Text("${userWeightKg} kg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.accent)
                IconButton(
                  onClick = { if (userWeightKg < 160) userWeightKg++ },
                  modifier = Modifier.size(24.dp).clip(CircleShape).background(colors.surface2)
                ) {
                  Icon(Icons.Default.Add, contentDescription = "Increase Weight", tint = colors.ink, modifier = Modifier.size(12.dp))
                }
              }
            }

            // Calculations row
            val minProtein = (userWeightKg * 1.6f).toInt()
            val maxProtein = (userWeightKg * 2.2f).toInt()
            val targetCalories = (userWeightKg * 28).toInt()
            val waterLiters = String.format(java.util.Locale.US, "%.1f", userWeightKg * 0.035f)

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = colors.surface2,
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text("Protein / Wt", fontSize = 10.sp, color = colors.ink2)
                  Text("${minProtein}–${maxProtein}g", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                  Text("1.6–2.2g/kg", fontSize = 9.sp, color = colors.ink2)
                }
              }
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = colors.surface2,
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text("Calories / Wt", fontSize = 10.sp, color = colors.ink2)
                  Text("~${targetCalories} kcal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.accent)
                  Text("28 kcal/kg", fontSize = 9.sp, color = colors.ink2)
                }
              }
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = colors.surface2,
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text("Water / Wt", fontSize = 10.sp, color = colors.ink2)
                  Text("${waterLiters} L", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                  Text("35 ml/kg", fontSize = 9.sp, color = colors.ink2)
                }
              }
            }

            // Quick Ask AI Button
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = colors.accentSoft,
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  sendMessage("Please build my exact Indian meal plan for ${userWeightKg}kg body weight to hit ${minProtein}g–${maxProtein}g protein and ${targetCalories} kcal.")
                }
            ) {
              Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "🎯 Ask MyCal AI for My ${userWeightKg}kg Meal Plan",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.accent
                )
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = colors.accent, modifier = Modifier.size(13.dp))
              }
            }
          }
        }

        // 4. Quick Suggested Prompts Horizontal Carousel
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          suggestedPrompts.forEach { prompt ->
            Surface(
              shape = RoundedCornerShape(999.dp),
              color = colors.surface,
              border = BorderStroke(1.dp, colors.line),
              modifier = Modifier
                .clickable {
                  view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                  sendMessage(prompt)
                }
                .testTag("suggested_prompt_${prompt.take(10)}")
            ) {
              Text(
                text = prompt,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = colors.ink,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }

        // 3. Conversation Messages Stream
        LazyColumn(
          state = listState,
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          item { Spacer(modifier = Modifier.height(4.dp)) }

          items(messages) { msg ->
            if (msg.isUser) {
              // User message bubble (Right aligned)
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                Surface(
                  shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                  color = colors.ink,
                  modifier = Modifier.fillMaxWidth(0.82f)
                ) {
                  Text(
                    text = msg.text,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = colors.bg,
                    modifier = Modifier.padding(14.dp)
                  )
                }
              }
            } else {
              // MyCal AI response bubble (Left aligned)
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
              ) {
                Surface(
                  shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                  color = colors.surface,
                  border = BorderStroke(1.dp, colors.line),
                  modifier = Modifier.fillMaxWidth(0.92f)
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(15.dp)
                      )
                      Text(
                        text = "MyCal AI Dietitian",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = msg.text,
                      fontSize = 14.sp,
                      lineHeight = 21.sp,
                      color = colors.ink
                    )
                  }
                }
              }
            }
          }

          if (isAiTyping) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
              ) {
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = colors.surface,
                  border = BorderStroke(1.dp, colors.line),
                  modifier = Modifier.padding(vertical = 4.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    CircularProgressIndicator(
                      modifier = Modifier.size(16.dp),
                      strokeWidth = 2.dp,
                      color = colors.accent
                    )
                    Text(
                      text = "MyCal AI is formulating dietitian advice...",
                      fontSize = 12.sp,
                      color = colors.ink2
                    )
                  }
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // 4. Bottom Input Bar with Speech to text & Send
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = colors.surface,
          border = BorderStroke(1.dp, colors.line)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Mic button
            IconButton(
              onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                try {
                  val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask MyCal AI your nutrition question...")
                  }
                  speechLauncher.launch(intent)
                } catch (e: Exception) {
                  inputText = "How many calories in 2 rotis with dal?"
                }
              },
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(colors.surface2)
                .testTag("mycal_ai_mic_button")
            ) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Input",
                tint = colors.accent,
                modifier = Modifier.size(20.dp)
              )
            }

            OutlinedTextField(
              value = inputText,
              onValueChange = { inputText = it },
              placeholder = { Text("Ask about calories, macros, recipes...", fontSize = 13.sp) },
              singleLine = true,
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
              keyboardActions = KeyboardActions(onSend = { sendMessage(inputText) }),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.accent,
                unfocusedBorderColor = colors.line,
                focusedContainerColor = colors.bg,
                unfocusedContainerColor = colors.bg
              ),
              shape = RoundedCornerShape(24.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("mycal_ai_chat_input")
            )

            // Send Button
            IconButton(
              onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                sendMessage(inputText)
              },
              enabled = inputText.isNotBlank() && !isAiTyping,
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (inputText.isNotBlank()) colors.ink else colors.surface2)
                .testTag("mycal_ai_send_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (inputText.isNotBlank()) colors.bg else colors.ink2,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}
