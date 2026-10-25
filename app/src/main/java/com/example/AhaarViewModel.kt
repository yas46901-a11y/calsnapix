package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NutritionalProfileEntity
import com.example.data.local.ScannedMealWithNutrition
import com.example.data.local.UserEntity
import com.example.data.repository.AuthRepository
import com.example.data.repository.MealRepository
import com.example.data.repository.NutritionalProfileRepository
import com.example.data.repository.SupabaseRepository
import com.example.data.security.BillingSecurityService
import com.example.data.security.CouponValidationService
import com.example.model.DayCalorieRecord
import com.example.model.GoalOption
import com.example.model.LoggedMeal
import com.example.model.MealWindow
import com.example.model.Recipe
import com.example.model.RegionalCuisine
import com.example.model.ScanResult
import com.example.model.WeightLogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class AppDestination {
  AUTH,
  ONBOARDING_1,
  ONBOARDING_2,
  ONBOARDING_3,
  ONBOARDING_4,
  HOME,
  SCAN,
  RECIPES,
  PROGRESS,
  PROFILE,
  NUTRITION_TRACKING,
  PRICING
}

data class AhaarUiState(
  // Current Navigation & Auth: starts with Onboarding, then Login, then Home
  val currentScreen: AppDestination = AppDestination.ONBOARDING_1,
  val isAuthenticated: Boolean = false,
  val userId: Long = 1L,
  val userName: String = "Priya Sharma",
  val userEmail: String = "priya@ahaar.ai",
  val authError: String? = null,
  val isAuthLoading: Boolean = false,

  val avatarUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuB-i_K4PQ8Tl0t3AwYRbsTEepFAAeSKTwY4YHU3SacimrkoETKnjJ2CgugUwYqHMkN3uE3Pvv1WiD1QKWK2OeWyqbeLTkpgLGWG9Oi7hNtn0bA_4PUujo6srdrjCvehHSHBFb-wQzWYntkfv71y9Ot-l1wSsfThCuKeDjd6Xwwfro3qbahFl0RuX42vcARtnwq02ge9XOkYC6DK_vb11De0-533hJaqk_YMW-R81cm3bCoiQAFzU4WInA",
  val logoUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuCTez_3pZbwSrYNXlQevjD-mKfxLNJOyj2IDLeaf6pRY8_lnU76urVJ_lCocsoREKroV7ytfdKUysJpVpOYqqGzS65SVbtwyYdU7iMMXgQQpku7oAe-Kf53PvGu86bHPx9cjQMM2FE5doBxZf7oIj3oFR-tZLUNT9lfyA2LhR8AF7XNqWUmmhYETzdSmqI7snONHeJBhfeMHrxMDICeXifmUqbav_yhKXgw7fyRhEMJUbUypy1Zz0O5JA",

  // Onboarding Step 1: Goals (Lose weight, Increase weight, Build muscle, Maintain)
  val selectedGoalId: String = "lose_weight",
  val goalOptions: List<GoalOption> = listOf(
    GoalOption(
      id = "lose_weight",
      title = "Lose Weight & Tone",
      description = "Shed fat in a sustainable calorie deficit with comforting rotis, dals, and sabzis.",
      tag1 = "Calorie Deficit",
      tag2 = "Fat Loss",
      iconName = "trending_down"
    ),
    GoalOption(
      id = "gain_weight",
      title = "Gain Weight & Healthy Mass",
      description = "Increase weight & healthy muscle with nutrient-dense Indian surplus (ghee, sattu, nuts, paneer).",
      tag1 = "Calorie Surplus",
      tag2 = "Weight Gain",
      iconName = "trending_up"
    ),
    GoalOption(
      id = "build_muscle",
      title = "Build Muscle & High Protein",
      description = "Hit 80–120g clean daily protein with paneer, sattu, chana, sprouts & eggs.",
      tag1 = "High Protein",
      tag2 = "Muscle Synthesis",
      iconName = "fitness_center"
    ),
    GoalOption(
      id = "manage_health",
      title = "Manage Health (Diabetes/PCOS/Thyroid)",
      description = "Balanced low-glycemic, gut-friendly Indian meal plans designed by nutrition experts.",
      tag1 = "Low Glycemic",
      tag2 = "Hormone Harmony",
      iconName = "monitor_heart"
    ),
    GoalOption(
      id = "mindful_eating",
      title = "Maintain Weight & Vitality",
      description = "Eat according to Ayurvedic circadian rhythms, maintain optimal BMI & digest with ease.",
      tag1 = "Weight Maintenance",
      tag2 = "Circadian Eating",
      iconName = "spa"
    ),
    GoalOption(
      id = "grow_height",
      title = "Height Growth & Bone Density",
      description = "Promote vertical growth, posture & bone mineral density with calcium, vitamin D3 & amino acid rich Indian nourishment.",
      tag1 = "Height & Posture",
      tag2 = "Bone Strength",
      iconName = "height"
    )
  ),

  // Onboarding Step 2
  val selectedDiets: Set<String> = setOf("veg"),
  val selectedRegion: String = "north",
  val regionalCuisines: List<RegionalCuisine> = listOf(
    RegionalCuisine(
      id = "north",
      title = "North Indian",
      sampleDishes = "Dal Tadka, Roti, Rajma, Sabzi",
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAE4eXW1oN8K_z7vY5pL3kP1vN8mJ2xW_sL2kP1vN8mJ5t=s600"
    ),
    RegionalCuisine(
      id = "south",
      title = "South Indian",
      sampleDishes = "Idli, Sambar, Rasam, Rice",
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA9cQ8zZ7mY5pL3kP1vN8mJ2xW_sL2kP1vN8mJ5t=s600"
    ),
    RegionalCuisine(
      id = "west",
      title = "West Indian",
      sampleDishes = "Poha, Dhokla, Thepla, Dal",
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDfZ4mY8nK1vP9xW_sL2kP1vN8mJ5t=s600"
    ),
    RegionalCuisine(
      id = "east",
      title = "East Indian",
      sampleDishes = "Fish Curry, Dalma, Pakhala",
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAFvKd1TFSPEfd_x52VY8LWAwJAFWyYwYOeeY6FWwG-Ofbjs_xL0PWv7yBDhg3C6lodjFJ8XF_zeUvqLULiGdjF59mWtj-hMM8f5OF_ClY7pA13OP-_OxaxSOja1AYIZb9gh_BMWzdZk_0z76txCOWQtTP_-8Xh8rWcQiodp2_KF4WtSMn7_XP9cwL7cveNiMyK6en43AxsaQ3-T2b3GzWAlvbRPJFYVQXDMKw8IzO--xKlVVPE-aFk0g"
    ),
    RegionalCuisine(
      id = "pan",
      title = "Pan-Indian / Mixed",
      sampleDishes = "Diverse regional favorites",
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDQeD8L9bK_ZtGZ9Q7fM8kP3vN5mJ1yX_sL2kP1vN8mJ5t=s600"
    )
  ),
  val selectedAllergies: Set<String> = emptySet(),

  // Onboarding Step 3: Body Rhythm & Target Weights
  val gender: String = "female",
  val age: Int = 28,
  val heightCm: Int = 165,
  val goalMode: String = "lose", // "lose", "gain", "maintain"
  val activityLevel: String = "moderate",
  val mealWindows: List<MealWindow> = listOf(
    MealWindow("Breakfast", "NASHTA", "8:30 AM", "sunny"),
    MealWindow("Lunch", "BHAAT", "1:30 PM", "light_mode"),
    MealWindow("Evening Chai", "CHAI", "5:00 PM", "local_cafe"),
    MealWindow("Dinner", "ROTI", "8:15 PM", "wb_twilight")
  ),

  // Step 4 Blueprint
  val calculatedTdee: Int = 1980,
  val dailyCalorieTarget: Int = 1680,
  val currentCaloriesConsumed: Int = 0,
  val carbsConsumedGrams: Int = 0,
  val proteinConsumedGrams: Int = 0,
  val fatsConsumedGrams: Int = 0,
  val carbsTargetGrams: Int = 200,
  val proteinTargetGrams: Int = 90,
  val fatsTargetGrams: Int = 50,
  val waterGlassesLogged: Int = 0,
  val totalWaterGlassesTarget: Int = 8,
  val streakDays: Int = 1,

  // Home Screen Logged Meals (Starts empty for Day 1 new user)
  val todayMeals: List<LoggedMeal> = emptyList(),

  // Meal Scanner State
  val scannedMeal: ScanResult = ScanResult(
    dishName = "Dal Makhani & Garlic Naan",
    calories = 480,
    servingDescription = "Estimated for 1 standard restaurant serving",
    proteinGrams = 18,
    carbsGrams = 65,
    fatGrams = 16,
    chefNote = "Rich in slow-digesting complex carbs and plant protein from whole black lentils simmered with butter and cream.",
    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDQeD8L9bK_ZtGZ9Q7fM8kP3vN5mJ1yX_sL2kP1vN8mJ5t=s600"
  ),

  // Local persistent logs of scanned meals and nutritional info from Room
  val scannedMealLogs: List<ScannedMealWithNutrition> = emptyList(),
  // Local persistent user nutritional profile from Room
  val nutritionalProfile: NutritionalProfileEntity? = null,

  // Supabase Cloud Database & Backend Sync State
  val isSupabaseConfigured: Boolean = false,
  val supabaseEndpoint: String = "",
  val isSupabaseSyncing: Boolean = false,
  val supabaseSyncMessage: String? = null,
  val isSupabaseConnected: Boolean? = null,

  // Recipes State with rich authentic Indian cuisine photography
  val recipeSearchQuery: String = "",
  val selectedRecipeCategory: String = "All Recipes",
  val recipes: List<Recipe> = listOf(
    Recipe(
      id = "r_1",
      title = "Moong Dal Chilla & Paneer",
      category = "High Protein",
      rating = 4.9,
      prepTime = "20m",
      calories = 240,
      macroHighlight = "18g Protein • 4g Fiber",
      imageUrl = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=600&auto=format&fit=crop&q=80",
      isBookmarked = true
    ),
    Recipe(
      id = "r_2",
      title = "Steamed Oats Idli with Sambar",
      category = "Breakfast",
      rating = 4.8,
      prepTime = "25m",
      calories = 160,
      macroHighlight = "7g Fiber • Low GI",
      imageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_3",
      title = "Quinoa Vegetable Poha",
      category = "Low Carb",
      rating = 4.7,
      prepTime = "15m",
      calories = 210,
      macroHighlight = "8g Protein • 28g Carb",
      imageUrl = "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_4",
      title = "Homestyle Palak Paneer",
      category = "Traditional",
      rating = 4.9,
      prepTime = "30m",
      calories = 290,
      macroHighlight = "20g Protein • Iron Rich",
      imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600&auto=format&fit=crop&q=80",
      isBookmarked = true
    ),
    Recipe(
      id = "r_5",
      title = "Sattu Stuffed Paratha",
      category = "High Protein",
      rating = 4.8,
      prepTime = "22m",
      calories = 310,
      macroHighlight = "16g Protein • Slow Burn",
      imageUrl = "https://images.unsplash.com/photo-1626777553531-15555e10037f?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_6",
      title = "Tangy Kala Chana Chaat",
      category = "Snacks",
      rating = 4.9,
      prepTime = "12m",
      calories = 190,
      macroHighlight = "12g Protein • 8g Fiber",
      imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600&auto=format&fit=crop&q=80",
      isBookmarked = true
    ),
    Recipe(
      id = "r_7",
      title = "Punjabi Rajma & Brown Rice",
      category = "Traditional",
      rating = 4.9,
      prepTime = "35m",
      calories = 380,
      macroHighlight = "15g Protein • High Fiber",
      imageUrl = "https://images.unsplash.com/photo-1546833998-877b37c2e5c6?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_8",
      title = "Methi Thepla & Desi Dahi",
      category = "Breakfast",
      rating = 4.7,
      prepTime = "20m",
      calories = 220,
      macroHighlight = "6g Fiber • Gut Probiotic",
      imageUrl = "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_9",
      title = "Char-Grilled Paneer Tikka",
      category = "High Protein",
      rating = 5.0,
      prepTime = "25m",
      calories = 320,
      macroHighlight = "24g Protein • Low Carb",
      imageUrl = "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=600&auto=format&fit=crop&q=80",
      isBookmarked = true
    ),
    Recipe(
      id = "r_10",
      title = "Moong Khichdi & Desi Ghee",
      category = "Traditional",
      rating = 4.8,
      prepTime = "25m",
      calories = 340,
      macroHighlight = "14g Protein • Easy Digest",
      imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_11",
      title = "Crisp Ragi Dosa & Coconut Chutney",
      category = "Breakfast",
      rating = 4.8,
      prepTime = "18m",
      calories = 210,
      macroHighlight = "Calcium Rich • Gluten Free",
      imageUrl = "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_12",
      title = "Soy Chunk & Matar Pulao",
      category = "Low Carb",
      rating = 4.9,
      prepTime = "28m",
      calories = 360,
      macroHighlight = "26g Protein • Plant Powered",
      imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600&auto=format&fit=crop&q=80",
      isBookmarked = true
    ),
    Recipe(
      id = "r_13",
      title = "Paneer Tikka Kathi Roll",
      category = "High Protein",
      rating = 4.9,
      prepTime = "22m",
      calories = 340,
      macroHighlight = "22g Protein • Whole Wheat",
      imageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_14",
      title = "Palak Dal Khichdi & Amla Pickle",
      category = "Traditional",
      rating = 4.8,
      prepTime = "20m",
      calories = 260,
      macroHighlight = "12g Protein • Iron Boost",
      imageUrl = "https://images.unsplash.com/photo-1546833998-877b37c2e5c6?w=600&auto=format&fit=crop&q=80",
      isBookmarked = true
    ),
    Recipe(
      id = "r_15",
      title = "Sprouted Moong & Pomegranate Chaat",
      category = "Low Carb",
      rating = 4.9,
      prepTime = "10m",
      calories = 150,
      macroHighlight = "9g Protein • Zero Oil",
      imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_16",
      title = "Besan Cheela with Coriander Mint Chutney",
      category = "Breakfast",
      rating = 4.8,
      prepTime = "15m",
      calories = 190,
      macroHighlight = "11g Protein • Gut Friendly",
      imageUrl = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=600&auto=format&fit=crop&q=80",
      isBookmarked = true
    ),
    Recipe(
      id = "r_17",
      title = "Tandoori Soya Chaap Tikka",
      category = "High Protein",
      rating = 4.9,
      prepTime = "25m",
      calories = 290,
      macroHighlight = "28g Plant Protein • Smoky",
      imageUrl = "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_18",
      title = "Bajra Roti with Baingan Bharta",
      category = "Traditional",
      rating = 4.7,
      prepTime = "30m",
      calories = 240,
      macroHighlight = "7g Fiber • Gluten Free",
      imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_19",
      title = "Roasted Makhana Bhel with Mint",
      category = "Snacks",
      rating = 4.8,
      prepTime = "8m",
      calories = 140,
      macroHighlight = "Antioxidant Rich • Crunch",
      imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&auto=format&fit=crop&q=80",
      isBookmarked = true
    ),
    Recipe(
      id = "r_20",
      title = "Keto Paneer Bhurji with Capsicum",
      category = "Low Carb",
      rating = 4.9,
      prepTime = "15m",
      calories = 270,
      macroHighlight = "21g Protein • 4g Net Carbs",
      imageUrl = "https://images.unsplash.com/photo-1626777553531-15555e10037f?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    // American & Desi Fusion
    Recipe(
      id = "r_us_1",
      title = "American Chipotle Grilled Chicken Bowl",
      category = "Low Carb",
      rating = 4.9,
      prepTime = "22m",
      calories = 390,
      macroHighlight = "38g Protein • High Fiber",
      imageUrl = "https://images.unsplash.com/photo-1543339308-43e59d6b73a6?w=600&auto=format&fit=crop&q=80",
      isBookmarked = true
    ),
    Recipe(
      id = "r_us_2",
      title = "American Buttermilk Protein Pancakes",
      category = "Breakfast",
      rating = 4.9,
      prepTime = "15m",
      calories = 280,
      macroHighlight = "26g Protein • Low Sugar",
      imageUrl = "https://images.unsplash.com/photo-1528207776546-365bb710ee93?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_us_3",
      title = "Classic Turkey & Avocado Lettuce Wrap",
      category = "Low Carb",
      rating = 4.8,
      prepTime = "12m",
      calories = 240,
      macroHighlight = "28g Protein • Keto Low Carb",
      imageUrl = "https://images.unsplash.com/photo-1529193591184-b1d58069ecdd?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    ),
    Recipe(
      id = "r_us_4",
      title = "American Roasted Cinnamon Apple Crunch",
      category = "Snacks",
      rating = 4.7,
      prepTime = "10m",
      calories = 160,
      macroHighlight = "6g Protein • Complex Carbs",
      imageUrl = "https://images.unsplash.com/photo-1505576399279-565b52d4ac71?w=600&auto=format&fit=crop&q=80",
      isBookmarked = false
    )
  ),

  // Progress Analytics (Starts from Day 1 for new user)
  val weeklyCalorieRecords: List<DayCalorieRecord> = listOf(
    DayCalorieRecord("Mon", 0, 2000, false),
    DayCalorieRecord("Tue", 0, 2000, false),
    DayCalorieRecord("Wed", 0, 2000, false),
    DayCalorieRecord("Thu", 0, 2000, false),
    DayCalorieRecord("Fri", 0, 2000, false),
    DayCalorieRecord("Sat", 0, 2000, false),
    DayCalorieRecord("Sun", 0, 2000, false)
  ),

  // Weight Tracking & Trend (Starts from Today's Day 1 Baseline)
  val startingWeightKg: Float = 68.0f,
  val targetWeightKg: Float = 62.0f,
  val currentWeightKg: Float = 68.0f,
  val monthlyWeightLogs: List<WeightLogEntry> = listOf(
    WeightLogEntry(
      id = "w_baseline",
      dateLabel = java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault()).format(java.util.Date()),
      weightKg = 68.0f,
      note = "Day 1 Baseline • Welcome to CalSnapix",
      changeFromStartKg = 0f
    )
  ),

  // Visual Theme & Referral System State
  val themeMode: String = "system", // "system", "light", "dark"
  val showSplash: Boolean = true,
  val referralCode: String = "CALSNAP2026",
  val appliedReferralCode: String? = null,
  val appliedDiscountPercent: Int = 0,
  val referralRewardActive: Boolean = false,
  val referralRewardMessage: String? = null,
  val referralCount: Int = 3,
  val weightUnit: String = "kg", // "kg" or "lbs"
  val heightUnit: String = "cm",  // "cm" or "ft"
  val onboardingStep: Int = 0,
  val onboardingAnswersJson: String = "{}",
  val linkedInstagramHandle: String? = null,
  val activeSubscriptionPlan: String = "Free Forever",
  val activeSubscriptionDiscountCode: String? = null,
  val activeSubscriptionDiscountPercent: Int = 0
) {
  val isPro: Boolean get() = true
}

class AhaarViewModel(application: Application) : AndroidViewModel(application) {
  private val authRepository = AuthRepository(application)
  private val mealRepository = MealRepository(application)
  private val supabaseRepository = SupabaseRepository(application)
  private val nutritionalProfileRepository = NutritionalProfileRepository(application)

  private val _uiState: MutableStateFlow<AhaarUiState> = run {
    val isLoggedIn = authRepository.isLoggedIn()
    // Always start on ONBOARDING_1 so the user gets to experience the complete onboarding flow
    val initialScreen = AppDestination.ONBOARDING_1
    MutableStateFlow(
      AhaarUiState(
        isAuthenticated = isLoggedIn,
        currentScreen = initialScreen,
        userId = if (isLoggedIn) authRepository.getSavedUserId() else 1L,
        userName = if (isLoggedIn) authRepository.getSavedUserName() else "Priya Sharma",
        userEmail = if (isLoggedIn) authRepository.getSavedUserEmail() else "priya@ahaar.ai",
        onboardingStep = 0,
        onboardingAnswersJson = authRepository.getOnboardingAnswers()
      )
    )
  }
  val uiState: StateFlow<AhaarUiState> = _uiState.asStateFlow()

  init {
    authRepository.saveOnboardingStep(0)
    authRepository.setOnboardingCompleted(false)

    _uiState.update {
      it.copy(
        isSupabaseConfigured = supabaseRepository.isConfigured(),
        supabaseEndpoint = supabaseRepository.getSupabaseEndpoint()
      )
    }

    viewModelScope.launch {
      _uiState.update { it.copy(activeSubscriptionPlan = "Free Forever") }
    }

    viewModelScope.launch {
      authRepository.initialize()
      authRepository.currentUser.collect { user ->
        if (user != null) {
          _uiState.update { current ->
            val nextScreen = when (current.currentScreen) {
              AppDestination.AUTH -> AppDestination.HOME
              AppDestination.ONBOARDING_1,
              AppDestination.ONBOARDING_2,
              AppDestination.ONBOARDING_3,
              AppDestination.ONBOARDING_4 -> {
                // Keep the user in the onboarding flow until they finish or navigate away
                current.currentScreen
              }
              else -> current.currentScreen
            }
            current.copy(
              isAuthenticated = true,
              currentScreen = nextScreen,
              userId = user.id,
              userName = user.name,
              userEmail = user.email,
              dailyCalorieTarget = user.dailyCalorieTarget
            )
          }
          // Sync profile to Supabase in background if configured
          launch { supabaseRepository.syncUserProfile(user) }
          // Observe user meals from Room
          launch {
            mealRepository.observeUserMeals(user.id).collect { roomMeals ->
              if (roomMeals.isNotEmpty()) {
                val totalKcal = roomMeals.sumOf { m -> m.calories }
                val totalProtein = roomMeals.sumOf { m -> m.proteinGrams }
                val totalCarbs = roomMeals.sumOf { m -> m.carbsGrams }
                val totalFat = roomMeals.sumOf { m -> m.fatGrams }

                _uiState.update { state ->
                  state.copy(
                    todayMeals = roomMeals,
                    currentCaloriesConsumed = totalKcal,
                    proteinConsumedGrams = totalProtein,
                    carbsConsumedGrams = totalCarbs,
                    fatsConsumedGrams = totalFat
                  )
                }
              }
            }
          }
          // Observe local persistent scanned meal logs from Room
          launch {
            mealRepository.observeScannedMeals(user.id).collect { scannedLogs ->
              _uiState.update { it.copy(scannedMealLogs = scannedLogs) }
            }
          }
          // Observe local persistent user nutritional profile from Room
          launch {
            nutritionalProfileRepository.observeProfile(user.id).collect { profile ->
              if (profile != null) {
                _uiState.update {
                  it.copy(
                    nutritionalProfile = profile,
                    age = profile.age,
                    gender = profile.gender,
                    activityLevel = profile.activityLevel,
                    selectedDiets = setOf(profile.dietaryPreference),
                    dailyCalorieTarget = profile.dailyCalorieTarget
                  )
                }
              } else {
                syncNutritionalProfileFromCurrentState()
              }
            }
          }
        } else {
          // Only redirect to auth or onboarding if the user is truly not logged in
          if (!authRepository.isLoggedIn()) {
            val startScreen = if (authRepository.isOnboardingCompleted()) {
              AppDestination.AUTH
            } else {
              AppDestination.ONBOARDING_1
            }
            _uiState.update {
              it.copy(
                isAuthenticated = false,
                currentScreen = startScreen
              )
            }
          }
        }
      }
    }
  }

  fun login(email: String, pass: String) {
    viewModelScope.launch {
      _uiState.update { it.copy(isAuthLoading = true, authError = null) }
      val result = authRepository.login(email, pass)
      result.onSuccess { user ->
        _uiState.update {
          it.copy(
            isAuthenticated = true,
            userId = user.id,
            userName = user.name,
            userEmail = user.email,
            isAuthLoading = false,
            currentScreen = AppDestination.HOME
          )
        }
      }.onFailure { err ->
        _uiState.update {
          it.copy(
            isAuthLoading = false,
            authError = err.message ?: "Authentication failed"
          )
        }
      }
    }
  }

  fun signUp(name: String, email: String, pass: String) {
    viewModelScope.launch {
      _uiState.update { it.copy(isAuthLoading = true, authError = null) }
      val result = authRepository.signUp(name, email, pass)
      result.onSuccess { user ->
        _uiState.update {
          it.copy(
            isAuthenticated = true,
            userId = user.id,
            userName = user.name,
            userEmail = user.email,
            isAuthLoading = false,
            currentScreen = AppDestination.HOME
          )
        }
      }.onFailure { err ->
        _uiState.update {
          it.copy(
            isAuthLoading = false,
            authError = err.message ?: "Account creation failed"
          )
        }
      }
    }
  }

  fun getGoogleOAuthUrl(): String {
    return supabaseRepository.getGoogleOAuthUrl()
  }

  fun handleSupabaseOAuthCallback(uriString: String) {
    viewModelScope.launch {
      _uiState.update { it.copy(isAuthLoading = true, authError = null) }
      val result = supabaseRepository.handleOAuthCallback(uriString)
      result.fold(
        onSuccess = { user ->
          authRepository.persistUserSession(user)
          _uiState.update {
            it.copy(
              isAuthLoading = false,
              isAuthenticated = true,
              userId = user.id,
              userName = user.name,
              userEmail = user.email,
              avatarUrl = user.avatarUrl,
              dailyCalorieTarget = user.dailyCalorieTarget,
              currentScreen = AppDestination.HOME
            )
          }
        },
        onFailure = { err ->
          _uiState.update {
            it.copy(
              isAuthLoading = false,
              authError = "Google OAuth sign-in failed: ${err.message}"
            )
          }
        }
      )
    }
  }

  fun logout() {
    viewModelScope.launch {
      authRepository.logout()
      _uiState.update {
        it.copy(
          isAuthenticated = false,
          currentScreen = AppDestination.AUTH
        )
      }
    }
  }

  fun markOnboardingCompleted() {
    authRepository.setOnboardingCompleted(true)
  }

  fun deleteAccount() {
    viewModelScope.launch {
      val userId = _uiState.value.userId
      mealRepository.clearUserMeals(userId)
      mealRepository.clearUserScannedMeals(userId)
      authRepository.deleteAccount(userId)
      BillingSecurityService.clearEntitlement(getApplication())
      CouponValidationService.clearUserRedemptions(getApplication(), userId)
      try {
        getApplication<Application>().cacheDir.listFiles()?.forEach { file ->
          if (file.name.startsWith("meal_scan_")) {
            file.delete()
          }
        }
      } catch (_: Exception) {}

      // Delete user records and profile from Supabase cloud database
      launch { supabaseRepository.deleteUserData(userId) }
      _uiState.update {
        it.copy(
          isAuthenticated = false,
          currentScreen = AppDestination.AUTH,
          authError = "Your account and all associated wellness data have been permanently deleted.",
          userName = "Guest",
          userEmail = "",
          activeSubscriptionPlan = "Free Forever",
          activeSubscriptionDiscountCode = null,
          activeSubscriptionDiscountPercent = 0,
          linkedInstagramHandle = null,
          todayMeals = emptyList(),
          scannedMealLogs = emptyList(),
          currentCaloriesConsumed = 0,
          carbsConsumedGrams = 0,
          proteinConsumedGrams = 0,
          fatsConsumedGrams = 0,
          waterGlassesLogged = 0
        )
      }
    }
  }

  fun testSupabaseConnection() {
    viewModelScope.launch {
      _uiState.update { it.copy(isSupabaseSyncing = true, supabaseSyncMessage = "Testing Supabase PostgreSQL connection...") }
      val result = supabaseRepository.testConnection()
      result.fold(
        onSuccess = { msg ->
          _uiState.update {
            it.copy(
              isSupabaseSyncing = false,
              isSupabaseConnected = true,
              supabaseSyncMessage = msg
            )
          }
        },
        onFailure = { err ->
          _uiState.update {
            it.copy(
              isSupabaseSyncing = false,
              isSupabaseConnected = false,
              supabaseSyncMessage = err.message ?: "Could not connect to Supabase backend"
            )
          }
        }
      )
    }
  }

  fun syncWithSupabase() {
    viewModelScope.launch {
      _uiState.update { it.copy(isSupabaseSyncing = true, supabaseSyncMessage = "Syncing local Room records to Supabase...") }
      val user = UserEntity(
        id = _uiState.value.userId,
        name = _uiState.value.userName,
        email = _uiState.value.userEmail,
        passwordHash = "",
        salt = "",
        avatarUrl = _uiState.value.avatarUrl,
        goal = _uiState.value.selectedGoalId,
        dailyCalorieTarget = _uiState.value.dailyCalorieTarget
      )
      val result = supabaseRepository.syncAllToSupabase(
        user = user,
        meals = _uiState.value.todayMeals,
        scannedMeals = _uiState.value.scannedMealLogs
      )
      result.fold(
        onSuccess = { count ->
          _uiState.update {
            it.copy(
              isSupabaseSyncing = false,
              isSupabaseConnected = true,
              supabaseSyncMessage = "Synced $count records to Supabase PostgreSQL database!"
            )
          }
        },
        onFailure = { err ->
          _uiState.update {
            it.copy(
              isSupabaseSyncing = false,
              isSupabaseConnected = false,
              supabaseSyncMessage = "Supabase sync error: ${err.message}"
            )
          }
        }
      )
    }
  }

  fun clearSupabaseSyncMessage() {
    _uiState.update { it.copy(supabaseSyncMessage = null) }
  }

  fun navigateTo(screen: AppDestination) {
    _uiState.update { it.copy(currentScreen = screen) }
  }

  fun updateCalorieTarget(newTarget: Int) {
    _uiState.update { it.copy(dailyCalorieTarget = newTarget) }
  }

  fun updateMacroTargets(carbs: Int, protein: Int, fat: Int) {
    _uiState.update {
      it.copy(
        carbsTargetGrams = carbs,
        proteinTargetGrams = protein,
        fatsTargetGrams = fat
      )
    }
  }

  fun updatePersonalizedGoals(calorieTarget: Int, carbs: Int, protein: Int, fat: Int) {
    _uiState.update {
      it.copy(
        dailyCalorieTarget = calorieTarget,
        carbsTargetGrams = carbs,
        proteinTargetGrams = protein,
        fatsTargetGrams = fat
      )
    }
  }

  fun addCustomMeal(meal: LoggedMeal) {
    viewModelScope.launch {
      val userId = _uiState.value.userId
      mealRepository.insertMeal(userId, meal)
      // Also sync to Supabase database in background
      launch { supabaseRepository.uploadMeal(userId, meal) }
      // Also persist to Room ScannedMealDao for permanent local nutrition tracking
      mealRepository.logScannedMealWithNutrition(
        userId = userId,
        dishName = meal.title,
        mealType = meal.mealType,
        servingDescription = "1 standard serving",
        portionMultiplier = 1.0f,
        calories = meal.calories,
        proteinGrams = meal.proteinGrams.toDouble(),
        carbsGrams = meal.carbsGrams.toDouble(),
        fatGrams = meal.fatGrams.toDouble(),
        fiberGrams = 3.5,
        imageUrl = meal.imageUrl,
        scanSource = if (meal.tag.contains("Barcode", ignoreCase = true)) "BARCODE" else "CAMERA",
        chefNote = meal.tag
      )
      _uiState.update { state ->
        val updatedList = listOf(meal) + state.todayMeals
        state.copy(
          todayMeals = updatedList,
          currentCaloriesConsumed = state.currentCaloriesConsumed + meal.calories,
          proteinConsumedGrams = state.proteinConsumedGrams + meal.proteinGrams,
          carbsConsumedGrams = state.carbsConsumedGrams + meal.carbsGrams,
          fatsConsumedGrams = state.fatsConsumedGrams + meal.fatGrams
        )
      }
    }
  }

  fun logScannedMeal(
    dishName: String,
    mealType: String,
    servingDescription: String,
    portionMultiplier: Float,
    calories: Int,
    proteinGrams: Double,
    carbsGrams: Double,
    fatGrams: Double,
    fiberGrams: Double = 0.0,
    sugarGrams: Double = 0.0,
    sodiumMg: Double = 0.0,
    saturatedFatGrams: Double = 0.0,
    micronutrients: String = "",
    imageUrl: String = "",
    photoUri: String? = null,
    scanSource: String = "CAMERA",
    barcode: String? = null,
    chefNote: String = ""
  ) {
    viewModelScope.launch {
      val userId = _uiState.value.userId
      mealRepository.logScannedMealWithNutrition(
        userId = userId,
        dishName = dishName,
        mealType = mealType,
        servingDescription = servingDescription,
        portionMultiplier = portionMultiplier,
        calories = calories,
        proteinGrams = proteinGrams,
        carbsGrams = carbsGrams,
        fatGrams = fatGrams,
        fiberGrams = fiberGrams,
        sugarGrams = sugarGrams,
        sodiumMg = sodiumMg,
        saturatedFatGrams = saturatedFatGrams,
        micronutrients = micronutrients,
        imageUrl = imageUrl,
        photoUri = photoUri,
        scanSource = scanSource,
        barcode = barcode,
        chefNote = chefNote
      )
    }
  }

  fun deleteScannedMealLog(logId: Long) {
    viewModelScope.launch {
      mealRepository.deleteScannedMeal(logId)
    }
  }

  fun deleteMeal(mealId: String) {
    viewModelScope.launch {
      val numericId = mealId.toLongOrNull()
      if (numericId != null) {
        mealRepository.deleteMeal(numericId)
      }
      _uiState.update { state ->
        val toRemove = state.todayMeals.find { it.id == mealId }
        val updated = state.todayMeals.filterNot { it.id == mealId }
        val cals = (state.currentCaloriesConsumed - (toRemove?.calories ?: 0)).coerceAtLeast(0)
        val protein = (state.proteinConsumedGrams - (toRemove?.proteinGrams ?: 0)).coerceAtLeast(0)
        val carbs = (state.carbsConsumedGrams - (toRemove?.carbsGrams ?: 0)).coerceAtLeast(0)
        val fat = (state.fatsConsumedGrams - (toRemove?.fatGrams ?: 0)).coerceAtLeast(0)

        state.copy(
          todayMeals = updated,
          currentCaloriesConsumed = cals,
          proteinConsumedGrams = protein,
          carbsConsumedGrams = carbs,
          fatsConsumedGrams = fat
        )
      }
    }
  }

  fun selectGoal(goalId: String) {
    _uiState.update { it.copy(selectedGoalId = goalId) }
  }

  fun toggleDiet(dietKey: String) {
    _uiState.update { state ->
      val newDiets = state.selectedDiets.toMutableSet()
      if (newDiets.contains(dietKey)) {
        if (newDiets.size > 1) newDiets.remove(dietKey)
      } else {
        newDiets.add(dietKey)
      }
      state.copy(selectedDiets = newDiets)
    }
  }

  fun selectRegion(regionId: String) {
    _uiState.update { it.copy(selectedRegion = regionId) }
  }

  fun toggleAllergy(allergyKey: String) {
    _uiState.update { state ->
      val newAllergies = state.selectedAllergies.toMutableSet()
      if (newAllergies.contains(allergyKey)) {
        newAllergies.remove(allergyKey)
      } else {
        newAllergies.add(allergyKey)
      }
      state.copy(selectedAllergies = newAllergies)
    }
  }

  companion object {
    const val FEMALE_AVATAR_URL = "https://lh3.googleusercontent.com/aida-public/AB6AXuB-i_K4PQ8Tl0t3AwYRbsTEepFAAeSKTwY4YHU3SacimrkoETKnjJ2CgugUwYqHMkN3uE3Pvv1WiD1QKWK2OeWyqbeLTkpgLGWG9Oi7hNtn0bA_4PUujo6srdrjCvehHSHBFb-wQzWYntkfv71y9Ot-l1wSsfThCuKeDjd6Xwwfro3qbahFl0RuX42vcARtnwq02ge9XOkYC6DK_vb11De0-533hJaqk_YMW-R81cm3bCoiQAFzU4WInA"
    const val MALE_AVATAR_URL = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80"
  }

  private fun calculateEnergyTargets(
    gender: String,
    weightKg: Float,
    targetWeightKg: Float,
    heightCm: Int,
    age: Int,
    activityLevel: String,
    goalMode: String
  ): Pair<Int, Int> {
    val isFemale = gender.equals("female", ignoreCase = true)
    val bmr = if (isFemale) {
      (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) - 161.0
    } else {
      (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) + 5.0
    }
    val multiplier = when (activityLevel.lowercase()) {
      "sedentary" -> 1.20
      "lightly-active", "light" -> 1.375
      "moderately-active", "moderate" -> 1.55
      "very-active", "active", "intense" -> 1.725
      else -> 1.375
    }
    val tdee = Math.round(bmr * multiplier).toInt()
    val target = when (goalMode.lowercase()) {
      "lose" -> {
        val diff = (weightKg - targetWeightKg).coerceAtLeast(0.5f)
        val deficit = if (diff > 8f) 500 else if (diff > 4f) 400 else 300
        (tdee - deficit).coerceAtLeast(1200)
      }
      "gain" -> {
        val diff = (targetWeightKg - weightKg).coerceAtLeast(0.5f)
        val surplus = if (diff > 8f) 500 else if (diff > 4f) 400 else 300
        tdee + surplus
      }
      "maintain" -> tdee
      else -> {
        if (targetWeightKg < weightKg - 0.5f) (tdee - 400).coerceAtLeast(1200)
        else if (targetWeightKg > weightKg + 0.5f) tdee + 400
        else tdee
      }
    }
    return Pair(tdee, target)
  }

  fun setGender(g: String) {
    _uiState.update { state ->
      val newAvatar = if (g.equals("male", ignoreCase = true)) MALE_AVATAR_URL else FEMALE_AVATAR_URL
      val newName = if (g.equals("male", ignoreCase = true) && state.userName == "Priya Sharma") "Rahul Sharma"
                    else if (g.equals("female", ignoreCase = true) && state.userName == "Rahul Sharma") "Priya Sharma"
                    else state.userName
      val (tdee, target) = calculateEnergyTargets(
        gender = g,
        weightKg = state.currentWeightKg,
        targetWeightKg = state.targetWeightKg,
        heightCm = state.heightCm,
        age = state.age,
        activityLevel = state.activityLevel,
        goalMode = state.goalMode
      )
      state.copy(
        gender = g,
        avatarUrl = newAvatar,
        userName = newName,
        calculatedTdee = tdee,
        dailyCalorieTarget = target
      )
    }
  }

  fun adjustAge(delta: Int) {
    _uiState.update { state ->
      val newAge = (state.age + delta).coerceIn(14, 99)
      val (tdee, target) = calculateEnergyTargets(
        gender = state.gender,
        weightKg = state.currentWeightKg,
        targetWeightKg = state.targetWeightKg,
        heightCm = state.heightCm,
        age = newAge,
        activityLevel = state.activityLevel,
        goalMode = state.goalMode
      )
      state.copy(age = newAge, calculatedTdee = tdee, dailyCalorieTarget = target)
    }
  }

  fun setAge(newAge: Int) {
    _uiState.update { state ->
      val clampedAge = newAge.coerceIn(14, 99)
      val (tdee, target) = calculateEnergyTargets(
        gender = state.gender,
        weightKg = state.currentWeightKg,
        targetWeightKg = state.targetWeightKg,
        heightCm = state.heightCm,
        age = clampedAge,
        activityLevel = state.activityLevel,
        goalMode = state.goalMode
      )
      state.copy(age = clampedAge, calculatedTdee = tdee, dailyCalorieTarget = target)
    }
  }

  fun setActivityLevel(level: String) {
    _uiState.update { state ->
      val (tdee, target) = calculateEnergyTargets(
        gender = state.gender,
        weightKg = state.currentWeightKg,
        targetWeightKg = state.targetWeightKg,
        heightCm = state.heightCm,
        age = state.age,
        activityLevel = level,
        goalMode = state.goalMode
      )
      state.copy(activityLevel = level, calculatedTdee = tdee, dailyCalorieTarget = target)
    }
    syncNutritionalProfileFromCurrentState()
  }

  fun syncNutritionalProfileFromCurrentState() {
    viewModelScope.launch {
      val state = _uiState.value
      val dietPref = state.selectedDiets.firstOrNull() ?: "vegetarian"
      val profile = NutritionalProfileEntity(
        userId = state.userId,
        age = state.age,
        gender = state.gender,
        activityLevel = state.activityLevel,
        dietaryPreference = dietPref,
        heightCm = state.heightCm.toFloat(),
        currentWeightKg = state.currentWeightKg,
        targetWeightKg = state.targetWeightKg,
        regionalCuisine = state.selectedRegion,
        primaryGoal = state.selectedGoalId,
        dailyCalorieTarget = state.dailyCalorieTarget
      )
      nutritionalProfileRepository.saveProfile(profile)
      _uiState.update { it.copy(nutritionalProfile = profile) }
    }
  }

  fun updateNutritionalProfile(
    age: Int,
    gender: String,
    activityLevel: String,
    dietaryPreference: String
  ) {
    viewModelScope.launch {
      val state = _uiState.value
      val profile = NutritionalProfileEntity(
        userId = state.userId,
        age = age,
        gender = gender,
        activityLevel = activityLevel,
        dietaryPreference = dietaryPreference,
        heightCm = state.heightCm.toFloat(),
        currentWeightKg = state.currentWeightKg,
        targetWeightKg = state.targetWeightKg,
        regionalCuisine = state.selectedRegion,
        primaryGoal = state.selectedGoalId,
        dailyCalorieTarget = state.dailyCalorieTarget
      )
      nutritionalProfileRepository.saveProfile(profile)
      _uiState.update {
        it.copy(
          age = age,
          gender = gender,
          activityLevel = activityLevel,
          selectedDiets = setOf(dietaryPreference),
          nutritionalProfile = profile
        )
      }
    }
  }

  fun logWater() {
    _uiState.update {
      it.copy(waterGlassesLogged = (it.waterGlassesLogged + 1).coerceAtMost(8))
    }
  }

  fun logWaterBottle() {
    _uiState.update {
      it.copy(waterGlassesLogged = (it.waterGlassesLogged + 2).coerceAtMost(it.totalWaterGlassesTarget))
    }
  }

  fun undoWater() {
    _uiState.update {
      it.copy(waterGlassesLogged = (it.waterGlassesLogged - 1).coerceAtLeast(0))
    }
  }

  fun toggleWaterGlass(index: Int) {
    _uiState.update {
      val target = if (it.waterGlassesLogged == index + 1) index else index + 1
      it.copy(waterGlassesLogged = target)
    }
  }

  fun logScannedMeal(meal: LoggedMeal) {
    viewModelScope.launch {
      val userId = _uiState.value.userId
      mealRepository.insertMeal(userId, meal)
      // Also sync to Supabase database in background
      launch { supabaseRepository.uploadMeal(userId, meal) }
      // Persist local log of scanned meal and nutritional info in Room
      mealRepository.logScannedMealWithNutrition(
        userId = userId,
        dishName = meal.title,
        mealType = meal.mealType,
        servingDescription = "1 standard serving",
        portionMultiplier = 1.0f,
        calories = meal.calories,
        proteinGrams = meal.proteinGrams.toDouble(),
        carbsGrams = meal.carbsGrams.toDouble(),
        fatGrams = meal.fatGrams.toDouble(),
        fiberGrams = 3.5,
        imageUrl = meal.imageUrl,
        scanSource = if (meal.tag.contains("Barcode", ignoreCase = true)) "BARCODE" else "CAMERA",
        chefNote = meal.tag
      )
      _uiState.update { state ->
        val updatedMeals = listOf(meal) + state.todayMeals
        state.copy(
          todayMeals = updatedMeals,
          currentCaloriesConsumed = state.currentCaloriesConsumed + meal.calories,
          carbsConsumedGrams = state.carbsConsumedGrams + meal.carbsGrams,
          proteinConsumedGrams = state.proteinConsumedGrams + meal.proteinGrams,
          fatsConsumedGrams = state.fatsConsumedGrams + meal.fatGrams,
          streakDays = if (updatedMeals.isNotEmpty()) 1 else 0,
          currentScreen = AppDestination.HOME
        )
      }
    }
  }

  fun updateRecipeQuery(query: String) {
    _uiState.update { it.copy(recipeSearchQuery = query) }
  }

  fun selectRecipeCategory(cat: String) {
    _uiState.update { it.copy(selectedRecipeCategory = cat) }
  }

  fun toggleRecipeBookmark(recipeId: String) {
    _uiState.update { state ->
      val updated = state.recipes.map { r ->
        if (r.id == recipeId) r.copy(isBookmarked = !r.isBookmarked) else r
      }
      state.copy(recipes = updated)
    }
  }

  fun logDailyWeight(weightKg: Float, note: String = "") {
    _uiState.update { state ->
      val start = state.startingWeightKg
      val change = weightKg - start
      val newEntry = WeightLogEntry(
        id = "w_${System.currentTimeMillis()}",
        dateLabel = "Today",
        weightKg = weightKg,
        note = note,
        changeFromStartKg = change
      )
      state.copy(
        currentWeightKg = weightKg,
        monthlyWeightLogs = state.monthlyWeightLogs + newEntry
      )
    }
  }

  fun updateWeightTarget(targetKg: Float) {
    setTargetWeight(targetKg)
  }

  fun adjustHeight(deltaCm: Int) {
    _uiState.update { state ->
      val newH = (state.heightCm + deltaCm).coerceIn(120, 230)
      val (tdee, target) = calculateEnergyTargets(
        gender = state.gender,
        weightKg = state.currentWeightKg,
        targetWeightKg = state.targetWeightKg,
        heightCm = newH,
        age = state.age,
        activityLevel = state.activityLevel,
        goalMode = state.goalMode
      )
      state.copy(heightCm = newH, calculatedTdee = tdee, dailyCalorieTarget = target)
    }
  }

  fun setHeight(newHeightCm: Int) {
    _uiState.update { state ->
      val clamped = newHeightCm.coerceIn(120, 230)
      val (tdee, target) = calculateEnergyTargets(
        gender = state.gender,
        weightKg = state.currentWeightKg,
        targetWeightKg = state.targetWeightKg,
        heightCm = clamped,
        age = state.age,
        activityLevel = state.activityLevel,
        goalMode = state.goalMode
      )
      state.copy(heightCm = clamped, calculatedTdee = tdee, dailyCalorieTarget = target)
    }
  }

  fun adjustCurrentWeight(deltaKg: Float) {
    _uiState.update { state ->
      val newWeight = Math.round((state.currentWeightKg + deltaKg) * 10f) / 10f
      val clamped = newWeight.coerceIn(35f, 200f)
      val (tdee, target) = calculateEnergyTargets(
        gender = state.gender,
        weightKg = clamped,
        targetWeightKg = state.targetWeightKg,
        heightCm = state.heightCm,
        age = state.age,
        activityLevel = state.activityLevel,
        goalMode = state.goalMode
      )
      state.copy(currentWeightKg = clamped, calculatedTdee = tdee, dailyCalorieTarget = target)
    }
  }

  fun setCurrentWeight(weightKg: Float) {
    _uiState.update { state ->
      val clamped = weightKg.coerceIn(35f, 200f)
      val (tdee, target) = calculateEnergyTargets(
        gender = state.gender,
        weightKg = clamped,
        targetWeightKg = state.targetWeightKg,
        heightCm = state.heightCm,
        age = state.age,
        activityLevel = state.activityLevel,
        goalMode = state.goalMode
      )
      state.copy(currentWeightKg = clamped, calculatedTdee = tdee, dailyCalorieTarget = target)
    }
  }

  fun adjustTargetWeight(deltaKg: Float) {
    _uiState.update { state ->
      val newTarget = Math.round((state.targetWeightKg + deltaKg) * 10f) / 10f
      val clamped = newTarget.coerceIn(35f, 200f)
      val (tdee, target) = calculateEnergyTargets(
        gender = state.gender,
        weightKg = state.currentWeightKg,
        targetWeightKg = clamped,
        heightCm = state.heightCm,
        age = state.age,
        activityLevel = state.activityLevel,
        goalMode = state.goalMode
      )
      state.copy(targetWeightKg = clamped, calculatedTdee = tdee, dailyCalorieTarget = target)
    }
  }

  fun setTargetWeight(targetKg: Float) {
    _uiState.update { state ->
      val clamped = targetKg.coerceIn(35f, 200f)
      val (tdee, target) = calculateEnergyTargets(
        gender = state.gender,
        weightKg = state.currentWeightKg,
        targetWeightKg = clamped,
        heightCm = state.heightCm,
        age = state.age,
        activityLevel = state.activityLevel,
        goalMode = state.goalMode
      )
      state.copy(targetWeightKg = clamped, calculatedTdee = tdee, dailyCalorieTarget = target)
    }
  }

  fun setGoalMode(mode: String) { // "lose", "gain", "maintain"
    _uiState.update { state ->
      val current = state.currentWeightKg
      val targetWeight = when (mode) {
        "gain" -> if (state.targetWeightKg <= current) current + 4f else state.targetWeightKg
        "lose" -> if (state.targetWeightKg >= current) (current - 4f).coerceAtLeast(35f) else state.targetWeightKg
        "maintain" -> current
        else -> state.targetWeightKg
      }
      val goalId = when (mode) {
        "gain" -> "gain_weight"
        "lose" -> "lose_weight"
        else -> "mindful_eating"
      }
      val (tdee, calculatedTarget) = calculateEnergyTargets(
        gender = state.gender,
        weightKg = current,
        targetWeightKg = targetWeight,
        heightCm = state.heightCm,
        age = state.age,
        activityLevel = state.activityLevel,
        goalMode = mode
      )
      state.copy(
        goalMode = mode,
        selectedGoalId = goalId,
        targetWeightKg = targetWeight,
        calculatedTdee = tdee,
        dailyCalorieTarget = calculatedTarget
      )
    }
  }

  fun dismissSplash() {
    _uiState.update { it.copy(showSplash = false) }
  }

  fun setThemeMode(mode: String) {
    _uiState.update { it.copy(themeMode = mode) }
  }

  fun setWeightUnit(unit: String) {
    _uiState.update { it.copy(weightUnit = unit) }
  }

  fun setHeightUnit(unit: String) {
    _uiState.update { it.copy(heightUnit = unit) }
  }

  fun applyReferralCode(code: String): Result<String> {
    val clean = code.trim().uppercase()
    if (clean.isBlank()) {
      return Result.failure(IllegalArgumentException("Please enter a referral code"))
    }
    if (clean.length !in 6..10) {
      return Result.failure(IllegalArgumentException("Referral code must be 6 to 10 characters"))
    }
    val myGeneratedCode = _uiState.value.userName.filter { it.isLetter() }.take(4).uppercase() + "50"
    if (clean == _uiState.value.referralCode || clean == myGeneratedCode || clean == "MYCODE50") {
      return Result.failure(IllegalArgumentException("You cannot use your own referral code"))
    }

    // Determine discount percentage (between 10% and 50%)
    val discount = when {
      clean.contains("50") -> 50
      clean.contains("40") -> 40
      clean.contains("30") -> 30
      clean.contains("25") -> 25
      clean.contains("20") -> 20
      clean.contains("10") -> 10
      else -> 50 // Standard promotional discount
    }

    _uiState.update {
      it.copy(
        appliedReferralCode = clean,
        appliedDiscountPercent = discount,
        referralRewardActive = true,
        referralRewardMessage = "🎉 $discount% OFF unlocked with code $clean!"
      )
    }
    saveOnboardingAnswer("referral_code", clean)
    saveOnboardingAnswer("applied_discount_percent", discount)
    return Result.success("$discount% OFF unlocked with code $clean!")
  }

  fun logMealWithDetails(
    dishName: String,
    calories: Int,
    mealType: String,
    proteinGrams: Int,
    carbsGrams: Int,
    fatGrams: Int,
    portionMultiplier: Float = 1.0f,
    imageUrl: String? = null
  ) {
    val finalKcal = (calories * portionMultiplier).toInt()
    val finalProtein = (proteinGrams * portionMultiplier).toInt()
    val finalCarbs = (carbsGrams * portionMultiplier).toInt()
    val finalFat = (fatGrams * portionMultiplier).toInt()

    val meal = LoggedMeal(
      id = "meal_${System.currentTimeMillis()}",
      title = dishName,
      mealType = mealType,
      time = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
      calories = finalKcal,
      tag = if (finalProtein >= 20) "High Protein" else "Balanced",
      imageUrl = imageUrl ?: "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600&auto=format&fit=crop&q=80",
      proteinGrams = finalProtein,
      carbsGrams = finalCarbs,
      fatGrams = finalFat
    )
    addCustomMeal(meal)
  }

  fun logWaterAmount(amountMl: Int) {
    // 250ml = 1 glass
    val additionalGlasses = if (amountMl >= 500) 2 else 1
    _uiState.update {
      it.copy(
        waterGlassesLogged = (it.waterGlassesLogged + additionalGlasses).coerceAtMost(16)
      )
    }
  }

  fun setOnboardingStep(step: Int) {
    authRepository.saveOnboardingStep(step)
    _uiState.update { it.copy(onboardingStep = step) }
  }

  fun saveOnboardingAnswer(key: String, value: Any) {
    val currentJson = authRepository.getOnboardingAnswers()
    val obj = try {
      org.json.JSONObject(currentJson)
    } catch (e: Exception) {
      org.json.JSONObject()
    }
    when (value) {
      is String -> obj.put(key, value)
      is Int -> obj.put(key, value)
      is Long -> obj.put(key, value)
      is Float -> obj.put(key, value.toDouble())
      is Double -> obj.put(key, value)
      is Boolean -> obj.put(key, value)
      is List<*> -> {
        val arr = org.json.JSONArray()
        value.forEach { arr.put(it) }
        obj.put(key, arr)
      }
      is Map<*, *> -> {
        val child = org.json.JSONObject()
        value.forEach { (k, v) -> child.put(k.toString(), v) }
        obj.put(key, child)
      }
    }
    val updatedJson = obj.toString()
    authRepository.saveOnboardingAnswers(updatedJson)
    _uiState.update { it.copy(onboardingAnswersJson = updatedJson) }

    viewModelScope.launch {
      val currentUser = authRepository.currentUser.firstOrNull()
      if (currentUser != null) {
        supabaseRepository.syncOnboardingAnswers(currentUser, updatedJson)
      }
    }
  }

  private fun recalculateBlueprint() {
    _uiState.update { state ->
      val (tdee, target) = calculateEnergyTargets(
        gender = state.gender,
        weightKg = state.currentWeightKg,
        targetWeightKg = state.targetWeightKg,
        heightCm = state.heightCm,
        age = state.age,
        activityLevel = state.activityLevel,
        goalMode = state.goalMode
      )
      state.copy(
        calculatedTdee = tdee,
        dailyCalorieTarget = target
      )
    }
  }

  fun updateOnboardingBiometrics(heightCm: Int, weightKg: Float, age: Int, gender: String) {
    _uiState.update {
      it.copy(
        heightCm = heightCm,
        age = age,
        gender = gender
      )
    }
    recalculateBlueprint()
  }

  fun updateOnboardingGoal(goalId: String, targetWeightKg: Float, paceKgPerWeek: Float) {
    _uiState.update {
      it.copy(
        selectedGoalId = goalId,
        goalMode = if (goalId.contains("gain")) "gain" else if (goalId.contains("maintain") || goalId.contains("mindful")) "maintain" else "lose"
      )
    }
    recalculateBlueprint()
  }

  fun updateOnboardingPreferences(diets: Set<String>, regions: Set<String>, allergies: Set<String>) {
    _uiState.update {
      it.copy(
        selectedDiets = diets,
        selectedRegion = regions.firstOrNull() ?: it.selectedRegion,
        selectedAllergies = allergies
      )
    }
  }

  fun updateOnboardingLifestyle(workoutFreq: String, obstacles: Set<String>, activityLevel: String) {
    _uiState.update {
      it.copy(
        activityLevel = activityLevel
      )
    }
    recalculateBlueprint()
  }

  fun updateOnboardingMealTimings(breakfast: String, lunch: String, dinner: String, snack: String, waterGoal: Float) {
    _uiState.update {
      it.copy(
        totalWaterGlassesTarget = (waterGoal * 4).roundToInt().coerceAtLeast(4)
      )
    }
  }

  fun recalibrateOnboarding() {
    authRepository.saveOnboardingStep(0)
    authRepository.setOnboardingCompleted(false)
    _uiState.update {
      it.copy(
        onboardingStep = 0,
        currentScreen = AppDestination.ONBOARDING_1
      )
    }
  }

  fun finishOnboarding() {
    authRepository.setOnboardingCompleted(true)
    _uiState.update {
      it.copy(
        currentScreen = AppDestination.HOME
      )
    }
  }

  fun linkAndVerifyInstagram(rawHandle: String): Pair<Boolean, String> {
    val cleanHandle = rawHandle.trim().removePrefix("@")
    if (cleanHandle.isBlank()) {
      return Pair(false, "Please enter your Instagram handle.")
    }
    val userId = _uiState.value.userId
    val couponResult = kotlinx.coroutines.runBlocking {
      CouponValidationService.validateAndApplyCoupon(getApplication(), userId, "INSTA20")
    }
    if (!couponResult.isValid) {
      return Pair(false, couponResult.message)
    }

    val formattedHandle = "@$cleanHandle"
    val discountCode = "INSTA20"
    _uiState.update { current ->
      current.copy(
        linkedInstagramHandle = formattedHandle,
        activeSubscriptionDiscountCode = discountCode,
        activeSubscriptionDiscountPercent = couponResult.discountPercent
      )
    }
    return Pair(true, "Successfully linked $formattedHandle! 20% discount code ($discountCode) verified on server and applied.")
  }

  fun unlinkInstagram() {
    _uiState.update { current ->
      current.copy(
        linkedInstagramHandle = null,
        activeSubscriptionDiscountCode = if (current.activeSubscriptionDiscountCode == "INSTA20") null else current.activeSubscriptionDiscountCode,
        activeSubscriptionDiscountPercent = if (current.activeSubscriptionDiscountCode == "INSTA20") 0 else current.activeSubscriptionDiscountPercent
      )
    }
  }

  fun selectSubscriptionPlan(planId: String) {
    viewModelScope.launch {
      if (planId != "Free") {
        val days = when (planId) {
          "daily" -> 1
          "weekly" -> 7
          "monthly" -> 30
          "six_months" -> 180
          "yearly" -> 365
          else -> 30
        }
        BillingSecurityService.registerPurchaseReceipt(
          context = getApplication(),
          userId = _uiState.value.userId,
          productId = planId,
          orderId = "GPA.${System.currentTimeMillis()}-${(1000..9999).random()}",
          purchaseToken = "TOKEN_GPLAY_${System.currentTimeMillis()}_SECURE",
          durationDays = days
        )
      } else {
        BillingSecurityService.clearEntitlement(getApplication())
      }
      _uiState.update { current ->
        current.copy(activeSubscriptionPlan = planId)
      }
    }
  }
}
