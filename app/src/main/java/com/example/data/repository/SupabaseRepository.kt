package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.NutritionalInfoEntity
import com.example.data.local.ScannedMealEntity
import com.example.data.local.ScannedMealWithNutrition
import com.example.data.local.UserEntity
import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.model.SupabaseAuthRequest
import com.example.data.supabase.model.SupabaseAuthResponse
import com.example.data.supabase.model.SupabaseIdTokenAuthRequest
import com.example.data.supabase.model.SupabaseMealDto
import com.example.data.supabase.model.SupabaseScannedMealDto
import com.example.data.supabase.model.SupabaseUserProfileDto
import com.example.model.LoggedMeal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabaseRepository(private val context: Context) {

  private val tag = "SupabaseRepo"
  private val userDao = AppDatabase.getDatabase(context).userDao()

  fun isConfigured(): Boolean {
    return SupabaseClient.isConfigured()
  }

  fun getSupabaseEndpoint(): String {
    return SupabaseClient.getSupabaseUrl()
  }

  suspend fun testConnection(): Result<String> = withContext(Dispatchers.IO) {
    SupabaseClient.testConnection()
  }

  suspend fun syncUserProfile(user: UserEntity): Result<Unit> = withContext(Dispatchers.IO) {
    if (!isConfigured()) return@withContext Result.success(Unit)
    try {
      val dto = SupabaseUserProfileDto(
        userId = user.id,
        name = user.name,
        email = user.email,
        goal = user.goal,
        dietType = user.dietType,
        region = user.region,
        dailyCalorieTarget = user.dailyCalorieTarget,
        proteinTargetGrams = user.proteinTargetGrams,
        carbsTargetGrams = user.carbsTargetGrams,
        fatsTargetGrams = user.fatsTargetGrams
      )
      val response = SupabaseClient.getApiService().upsertUserProfile(dto)
      if (response.isSuccessful) {
        Result.success(Unit)
      } else {
        Log.w(tag, "Failed to upsert profile: ${response.code()} ${response.message()}")
        Result.failure(Exception("Supabase sync failed: ${response.code()}"))
      }
    } catch (e: Exception) {
      Log.e(tag, "Error syncing profile", e)
      Result.failure(e)
    }
  }

  suspend fun syncOnboardingAnswers(user: UserEntity, answersJson: String): Result<Unit> = withContext(Dispatchers.IO) {
    if (!isConfigured()) return@withContext Result.success(Unit)
    try {
      val dto = SupabaseUserProfileDto(
        userId = user.id,
        name = user.name,
        email = user.email,
        goal = user.goal,
        dietType = user.dietType,
        region = user.region,
        dailyCalorieTarget = user.dailyCalorieTarget,
        proteinTargetGrams = user.proteinTargetGrams,
        carbsTargetGrams = user.carbsTargetGrams,
        fatsTargetGrams = user.fatsTargetGrams,
        onboardingAnswers = answersJson
      )
      val response = SupabaseClient.getApiService().upsertUserProfile(dto)
      if (response.isSuccessful) {
        Result.success(Unit)
      } else {
        Log.w(tag, "Failed to sync onboarding answers: ${response.code()}")
        Result.failure(Exception("Supabase onboarding sync failed: ${response.code()}"))
      }
    } catch (e: Exception) {
      Log.e(tag, "Error syncing onboarding answers", e)
      Result.failure(e)
    }
  }

  suspend fun uploadMeal(userId: Long, meal: LoggedMeal): Result<Unit> = withContext(Dispatchers.IO) {
    if (!isConfigured()) return@withContext Result.success(Unit)
    try {
      val dto = SupabaseMealDto(
        userId = userId,
        title = meal.title,
        mealType = meal.mealType,
        time = meal.time,
        calories = meal.calories,
        proteinGrams = meal.proteinGrams.toDouble(),
        carbsGrams = meal.carbsGrams.toDouble(),
        fatGrams = meal.fatGrams.toDouble(),
        tag = meal.tag,
        imageUrl = meal.imageUrl
      )
      val response = SupabaseClient.getApiService().insertMeal(dto)
      if (response.isSuccessful) {
        Result.success(Unit)
      } else {
        Result.failure(Exception("Failed to upload meal: ${response.code()}"))
      }
    } catch (e: Exception) {
      Log.e(tag, "Error uploading meal", e)
      Result.failure(e)
    }
  }

  suspend fun uploadScannedMeal(
    userId: Long,
    meal: ScannedMealEntity,
    nutrition: NutritionalInfoEntity?
  ): Result<Unit> = withContext(Dispatchers.IO) {
    if (!isConfigured()) return@withContext Result.success(Unit)
    try {
      val dto = SupabaseScannedMealDto(
        userId = userId,
        dishName = meal.dishName,
        mealType = meal.mealType,
        servingDescription = meal.servingDescription,
        portionMultiplier = meal.portionMultiplier,
        imageUrl = meal.imageUrl,
        photoUri = meal.photoUri,
        scanSource = meal.scanSource,
        barcode = meal.barcode,
        chefNote = meal.chefNote,
        calories = nutrition?.calories ?: 0,
        proteinGrams = nutrition?.proteinGrams ?: 0.0,
        carbsGrams = nutrition?.carbsGrams ?: 0.0,
        fatGrams = nutrition?.fatGrams ?: 0.0,
        fiberGrams = nutrition?.fiberGrams ?: 0.0,
        sugarGrams = nutrition?.sugarGrams ?: 0.0,
        sodiumMg = nutrition?.sodiumMg ?: 0.0,
        saturatedFatGrams = nutrition?.saturatedFatGrams ?: 0.0,
        micronutrients = nutrition?.micronutrients ?: ""
      )
      val response = SupabaseClient.getApiService().insertScannedMeal(dto)
      if (response.isSuccessful) {
        Result.success(Unit)
      } else {
        Result.failure(Exception("Failed to upload scanned meal: ${response.code()}"))
      }
    } catch (e: Exception) {
      Log.e(tag, "Error uploading scanned meal", e)
      Result.failure(e)
    }
  }

  suspend fun syncAllToSupabase(
    user: UserEntity,
    meals: List<LoggedMeal>,
    scannedMeals: List<ScannedMealWithNutrition>
  ): Result<Int> = withContext(Dispatchers.IO) {
    if (!isConfigured()) {
      return@withContext Result.failure(
        IllegalStateException("Supabase is not configured yet. Add your SUPABASE_URL and SUPABASE_ANON_KEY in Secrets.")
      )
    }
    var syncedCount = 0

    // 1. Sync User Profile
    val userResult = syncUserProfile(user)
    if (userResult.isSuccess) syncedCount++

    // 2. Sync Meals
    for (m in meals) {
      val res = uploadMeal(user.id, m)
      if (res.isSuccess) syncedCount++
    }

    // 3. Sync Scanned Meals
    for (sm in scannedMeals) {
      val res = uploadScannedMeal(user.id, sm.scannedMeal, sm.nutritionalInfo)
      if (res.isSuccess) syncedCount++
    }

    Result.success(syncedCount)
  }

  suspend fun deleteUserData(userId: Long): Result<Unit> = withContext(Dispatchers.IO) {
    if (!isConfigured()) return@withContext Result.success(Unit)
    try {
      SupabaseClient.getApiService().deleteMealsByUser("eq.$userId")
      SupabaseClient.getApiService().deleteScannedMealsByUser("eq.$userId")
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(tag, "Error deleting user data from Supabase", e)
      Result.failure(e)
    }
  }

  suspend fun signUp(email: String, pass: String, name: String): Result<SupabaseAuthResponse> =
    withContext(Dispatchers.IO) {
      if (!isConfigured()) {
        return@withContext Result.failure(IllegalStateException("Supabase not configured"))
      }
      try {
        val req = SupabaseAuthRequest(email, pass, mapOf("name" to name))
        val resp = SupabaseClient.getApiService().signUp(req)
        if (resp.isSuccessful && resp.body() != null) {
          val body = resp.body()!!
          if (body.accessToken != null) {
            SupabaseClient.userAccessToken = body.accessToken
          }
          Result.success(body)
        } else {
          Result.failure(Exception("Supabase sign up failed: ${resp.code()} ${resp.message()}"))
        }
      } catch (e: Exception) {
        Result.failure(e)
      }
    }

  suspend fun login(email: String, pass: String): Result<SupabaseAuthResponse> =
    withContext(Dispatchers.IO) {
      if (!isConfigured()) {
        return@withContext Result.failure(IllegalStateException("Supabase not configured"))
      }
      try {
        val req = SupabaseAuthRequest(email, pass)
        val resp = SupabaseClient.getApiService().signInWithPassword(req)
        if (resp.isSuccessful && resp.body() != null) {
          val body = resp.body()!!
          if (body.accessToken != null) {
            SupabaseClient.userAccessToken = body.accessToken
          }
          Result.success(body)
        } else {
          Result.failure(Exception("Supabase login failed: ${resp.code()} ${resp.message()}"))
        }
      } catch (e: Exception) {
        Result.failure(e)
      }
    }

  fun getGoogleOAuthUrl(): String {
    return SupabaseClient.getGoogleOAuthUrl()
  }

  suspend fun handleOAuthCallback(uriString: String): Result<UserEntity> = withContext(Dispatchers.IO) {
    val params = SupabaseClient.parseAuthCallbackUri(uriString)
    val error = params["error_description"] ?: params["error"]
    if (error != null) {
      return@withContext Result.failure(Exception("Google Sign-In failed: $error"))
    }

    val accessToken = params["access_token"]
    if (accessToken.isNullOrBlank()) {
      return@withContext Result.failure(Exception("No access token returned from Google OAuth callback"))
    }

    SupabaseClient.userAccessToken = accessToken

    try {
      val userResp = SupabaseClient.getApiService().getAuthUser()
      if (!userResp.isSuccessful || userResp.body() == null) {
        return@withContext Result.failure(Exception("Failed to fetch Google profile: ${userResp.code()}"))
      }
      val authUser = userResp.body()!!
      val email = authUser.email ?: "google_user@calsnapix.ai"
      val name = authUser.userMetadata?.fullName
        ?: authUser.userMetadata?.name
        ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }
      val avatarUrl = authUser.userMetadata?.avatarUrl
        ?: authUser.userMetadata?.picture
        ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuB-i_K4PQ8Tl0t3AwYRbsTEepFAAeSKTwY4YHU3SacimrkoETKnjJ2CgugUwYqHMkN3uE3Pvv1WiD1QKWK2OeWyqbeLTkpgLGWG9Oi7hNtn0bA_4PUujo6srdrjCvehHSHBFb-wQzWYntkfv71y9Ot-l1wSsfThCuKeDjd6Xwwfro3qbahFl0RuX42vcARtnwq02ge9XOkYC6DK_vb11De0-533hJaqk_YMW-R81cm3bCoiQAFzU4WInA"

      // Check existing user in Room
      var localUser = userDao.getUserByEmail(email)
      if (localUser != null) {
        val updated = localUser.copy(name = name, avatarUrl = avatarUrl)
        userDao.updateUser(updated)
        localUser = updated
      } else {
        val newUser = UserEntity(
          name = name,
          email = email,
          passwordHash = "oauth_google",
          salt = "oauth",
          avatarUrl = avatarUrl,
          goal = "lose_weight",
          dietType = "veg",
          region = "north",
          dailyCalorieTarget = 2000,
          proteinTargetGrams = 90,
          carbsTargetGrams = 210,
          fatsTargetGrams = 55
        )
        val id = userDao.insertUser(newUser)
        localUser = newUser.copy(id = id)
      }

      // Upsert profile in Supabase PostgreSQL
      syncUserProfile(localUser)

      // Save session pref
      context.getSharedPreferences("ahaar_auth_session", Context.MODE_PRIVATE)
        .edit()
        .putLong("logged_in_user_id", localUser.id)
        .apply()

      Result.success(localUser)
    } catch (e: Exception) {
      Log.e(tag, "Error handling OAuth callback", e)
      Result.failure(e)
    }
  }

  suspend fun signInWithGoogleIdToken(idToken: String): Result<UserEntity> = withContext(Dispatchers.IO) {
    if (!isConfigured()) {
      return@withContext Result.failure(IllegalStateException("Supabase not configured"))
    }
    try {
      val req = SupabaseIdTokenAuthRequest(provider = "google", idToken = idToken)
      val resp = SupabaseClient.getApiService().signInWithIdToken(req)
      if (resp.isSuccessful && resp.body() != null) {
        val body = resp.body()!!
        if (body.accessToken != null) {
          SupabaseClient.userAccessToken = body.accessToken
        }
        val userResp = SupabaseClient.getApiService().getAuthUser()
        if (userResp.isSuccessful && userResp.body() != null) {
          val authUser = userResp.body()!!
          val email = authUser.email ?: "google_user@calsnapix.ai"
          val name = authUser.userMetadata?.fullName
            ?: authUser.userMetadata?.name
            ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }
          val avatarUrl = authUser.userMetadata?.avatarUrl
            ?: authUser.userMetadata?.picture
            ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuB-i_K4PQ8Tl0t3AwYRbsTEepFAAeSKTwY4YHU3SacimrkoETKnjJ2CgugUwYqHMkN3uE3Pvv1WiD1QKWK2OeWyqbeLTkpgLGWG9Oi7hNtn0bA_4PUujo6srdrjCvehHSHBFb-wQzWYntkfv71y9Ot-l1wSsfThCuKeDjd6Xwwfro3qbahFl0RuX42vcARtnwq02ge9XOkYC6DK_vb11De0-533hJaqk_YMW-R81cm3bCoiQAFzU4WInA"

          var localUser = userDao.getUserByEmail(email)
          if (localUser != null) {
            val updated = localUser.copy(name = name, avatarUrl = avatarUrl)
            userDao.updateUser(updated)
            localUser = updated
          } else {
            val newUser = UserEntity(
              name = name,
              email = email,
              passwordHash = "oauth_google",
              salt = "oauth",
              avatarUrl = avatarUrl,
              goal = "lose_weight",
              dietType = "veg",
              region = "north",
              dailyCalorieTarget = 2000,
              proteinTargetGrams = 90,
              carbsTargetGrams = 210,
              fatsTargetGrams = 55
            )
            val id = userDao.insertUser(newUser)
            localUser = newUser.copy(id = id)
          }

          syncUserProfile(localUser)
          context.getSharedPreferences("ahaar_auth_session", Context.MODE_PRIVATE)
            .edit()
            .putLong("logged_in_user_id", localUser.id)
            .apply()

          Result.success(localUser)
        } else {
          Result.failure(Exception("Failed to fetch user profile after Google sign-in"))
        }
      } else {
        Result.failure(Exception("Supabase Google ID token sign-in failed: ${resp.code()} ${resp.message()}"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
