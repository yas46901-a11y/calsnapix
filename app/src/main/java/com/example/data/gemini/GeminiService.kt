package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import com.example.model.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Service integrating Google Gemini API using the configured GEMINI_API_KEY.
 */
object GeminiService {
  private const val TAG = "GeminiService"
  private const val GEMINI_MODEL = "gemini-2.5-flash"
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

  private val httpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  val isKeyConfigured: Boolean
    get() {
      val key = BuildConfig.GEMINI_API_KEY.trim()
      return key.isNotEmpty() && key != "MY_GEMINI_API_KEY"
    }

  fun getMaskedApiKey(): String {
    val key = BuildConfig.GEMINI_API_KEY.trim()
    if (key.isEmpty() || key == "MY_GEMINI_API_KEY") return "Not Configured"
    return if (key.length > 8) {
      "${key.take(4)}...${key.takeLast(4)}"
    } else {
      "••••••••"
    }
  }

  /**
   * Generates a healthy Indian recipe using user-provided kitchen leftover ingredients.
   */
  suspend fun generateLeftoverRecipe(ingredients: String): LeftoverRecipeResult = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY.trim()
    if (!isKeyConfigured) {
      Log.d(TAG, "Gemini key not configured or using placeholder; using offline recipe intelligence.")
      return@withContext getOfflineLeftoverFallback(ingredients)
    }

    val prompt = """
      You are an expert Indian clinical nutritionist and chef specializing in ghar-ka-khana.
      The user has these leftover ingredients in their kitchen: "$ingredients".
      Create one innovative, delicious, healthy Indian dish utilizing these leftovers.
      Respond strictly in valid JSON format with this exact structure (no markdown, no backticks, no code block):
      {
        "recipeTitle": "Dish Name",
        "description": "Short explanation of the dish",
        "calories": 280,
        "proteinGrams": 12,
        "carbsGrams": 38,
        "fatGrams": 8,
        "instructions": "Step-by-step instructions in 2-3 sentences"
      }
    """.trimIndent()

    try {
      val requestBodyJson = JSONObject().apply {
        put("contents", JSONArray().apply {
          put(JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().apply {
                put("text", prompt)
              })
            })
          })
        })
      }

      val url = "$BASE_URL/$GEMINI_MODEL:generateContent?key=$apiKey"
      val request = Request.Builder()
        .url(url)
        .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = httpClient.newCall(request).execute()
      val responseText = response.body?.string().orEmpty()

      if (!response.isSuccessful) {
        Log.e(TAG, "Gemini API error status code: ${response.code}")
        return@withContext getOfflineLeftoverFallback(ingredients)
      }

      val jsonResponse = JSONObject(responseText)
      val candidates = jsonResponse.optJSONArray("candidates")
      val firstCandidate = candidates?.optJSONObject(0)
      val contentObj = firstCandidate?.optJSONObject("content")
      val parts = contentObj?.optJSONArray("parts")
      val rawText = parts?.optJSONObject(0)?.optString("text").orEmpty()

      // Clean up any markdown code fencing if present
      val cleanJson = rawText.trim()
        .removePrefix("```json")
        .removePrefix("```")
        .removeSuffix("```")
        .trim()

      val recipeObj = JSONObject(cleanJson)
      LeftoverRecipeResult(
        title = recipeObj.optString("recipeTitle", "Spiced Leftover Delight"),
        description = recipeObj.optString("description", "A tasty, calorie-controlled Indian preparation."),
        calories = recipeObj.optInt("calories", 260),
        proteinGrams = recipeObj.optInt("proteinGrams", 10),
        carbsGrams = recipeObj.optInt("carbsGrams", 35),
        fatGrams = recipeObj.optInt("fatGrams", 8),
        instructions = recipeObj.optString("instructions", "Cook with mild spices and enjoy fresh.")
      )
    } catch (e: Exception) {
      Log.e(TAG, "Error calling Gemini API for leftover recipe", e)
      getOfflineLeftoverFallback(ingredients)
    }
  }

  private fun getOfflineLeftoverFallback(ingredients: String): LeftoverRecipeResult {
    val lower = ingredients.lowercase()
    return when {
      lower.contains("roti") || lower.contains("chapati") -> LeftoverRecipeResult(
        title = "Spiced Roti Papad Chaat",
        description = "Crispy toasted leftover roti strips tossed with roasted cumin, rock salt, creamy curd, and diced veggies.",
        calories = 245,
        proteinGrams = 9,
        carbsGrams = 36,
        fatGrams = 7,
        instructions = "Cut leftover rotis into triangles, dry-roast until crisp on a tawa, and toss with yogurt, mint chutney, and diced tomatoes."
      )
      lower.contains("rice") || lower.contains("chawal") -> LeftoverRecipeResult(
        title = "Lemon Tadka Poha-Rice",
        description = "Lightly tempered spiced brown or white rice with curry leaves, peanuts, and freshly squeezed lemon juice.",
        calories = 290,
        proteinGrams = 8,
        carbsGrams = 48,
        fatGrams = 8,
        instructions = "Temper mustard seeds, curry leaves, and green chillies in 1/2 tsp cold-pressed oil, fold in the rice, and finish with lemon."
      )
      lower.contains("dal") -> LeftoverRecipeResult(
        title = "Nutrient-Dense Missi Paratha",
        description = "Knead leftover dal directly into whole wheat flour with ajwain and kasuri methi for a high-protein breakfast.",
        calories = 270,
        proteinGrams = 12,
        carbsGrams = 42,
        fatGrams = 6,
        instructions = "Mix leftover dal with atta and a pinch of ajwain without adding extra water. Roll thin and cook with light ghee."
      )
      else -> LeftoverRecipeResult(
        title = "Desi Panchmel Veggie Medley",
        description = "Quick stir-fry of your pantry ingredients seasoned with turmeric, jeera, and coriander powder.",
        calories = 220,
        proteinGrams = 8,
        carbsGrams = 32,
        fatGrams = 6,
        instructions = "Chop ingredients evenly, steam or sauté in minimal oil with cumin and coriander, garnish with fresh coriander."
      )
    }
  }

  /**
   * Provides clinical Indian nutrition and Ayurvedic diet advice via Gemini AI.
   */
  suspend fun askNutritionAssistant(query: String): String = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY.trim()
    if (!isKeyConfigured) {
      return@withContext getOfflineDietAdvice(query)
    }

    val prompt = """
      You are an expert AI Indian Dietitian and Ayurvedic Nutritionist.
      The user asks: "$query"
      Provide a concise (3-4 sentences max), clinically accurate, encouraging response tailored to Indian dietary patterns, doshas, and macronutrient balance. Do not include markdown headers or greetings.
    """.trimIndent()

    try {
      val requestBodyJson = JSONObject().apply {
        put("contents", JSONArray().apply {
          put(JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().apply {
                put("text", prompt)
              })
            })
          })
        })
      }

      val url = "$BASE_URL/$GEMINI_MODEL:generateContent?key=$apiKey"
      val request = Request.Builder()
        .url(url)
        .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = httpClient.newCall(request).execute()
      val responseText = response.body?.string().orEmpty()

      if (!response.isSuccessful) {
        return@withContext getOfflineDietAdvice(query)
      }

      val jsonResponse = JSONObject(responseText)
      val candidates = jsonResponse.optJSONArray("candidates")
      val firstCandidate = candidates?.optJSONObject(0)
      val contentObj = firstCandidate?.optJSONObject("content")
      val parts = contentObj?.optJSONArray("parts")
      val rawText = parts?.optJSONObject(0)?.optString("text").orEmpty()
      rawText.trim().ifEmpty { getOfflineDietAdvice(query) }
    } catch (e: Exception) {
      Log.e(TAG, "Error querying Gemini Nutrition Assistant", e)
      getOfflineDietAdvice(query)
    }
  }

  private fun getOfflineDietAdvice(query: String): String {
    val lower = query.lowercase()
    return when {
      lower.contains("pitta") || lower.contains("dal makhani") ->
        "Dal Makhani is rich in heavy black urad lentils and butter, which can elevate Pitta and Kapha if eaten late. For Pitta balance, opt for yellow Moong Dal tempered with cooling cumin, coriander, and fresh cilantro, which aids gentle digestion."
      lower.contains("dinner") || lower.contains("400") || lower.contains("light") ->
        "For a nutritious dinner under 400 kcal, try 1 multigrain phulka with 1 cup Lauki (bottle gourd) sabzi and 100g low-fat Paneer Bhurji. It delivers 22g protein, high dietary fiber, and keeps your circadian digestion light before sleep."
      lower.contains("protein") || lower.contains("snack") || lower.contains("egg") ->
        "High-protein Indian snacks include roasted chana with sattu chaat (18g protein per 100g), boiled egg whites with chaat masala (14g protein per 4 whites), or steamed sprout salad with lime and rock salt. They stabilize blood sugar and prevent sugar cravings."
      lower.contains("metabolism") || lower.contains("weight") ->
        "To naturally stimulate digestive Agni and metabolism, start your morning with warm water and soaked methi seeds or jeera. Ensure each meal has at least 20g protein and stop eating 3 hours before sleep to optimize nightly lipolysis."
      else ->
        "Balancing Indian meals starts with the 2:1:1 rule: half your plate vegetables or salad, one quarter lean protein (dal, paneer, sprouts, eggs), and one quarter complex carbohydrates (millets or whole wheat roti)."
    }
  }
}

data class LeftoverRecipeResult(
  val title: String,
  val description: String,
  val calories: Int,
  val proteinGrams: Int,
  val carbsGrams: Int,
  val fatGrams: Int,
  val instructions: String
)
