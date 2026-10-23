package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.UserEntity
import com.example.data.repository.SupabaseRepository
import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.model.SupabaseMealDto
import com.example.data.supabase.model.SupabaseScannedMealDto
import com.example.data.supabase.model.SupabaseUserProfileDto
import com.example.model.LoggedMeal
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SupabaseIntegrationTest {

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val supabaseRepository = SupabaseRepository(context)

  @Test
  fun testSupabaseClientConfigurationState() {
    // With default placeholder, isConfigured should be false
    val configured = SupabaseClient.isConfigured()
    val endpoint = SupabaseClient.getSupabaseUrl()
    assertNotNull(endpoint)
    // Default placeholder in .env.example should safely indicate unconfigured
    if (endpoint.contains("your-project.supabase.co") || endpoint.isBlank()) {
      assertFalse(configured)
    }
  }

  @Test
  fun testSupabaseUnconfiguredConnectionGraceful() = runTest {
    if (!supabaseRepository.isConfigured()) {
      val testRes = supabaseRepository.testConnection()
      assertTrue(testRes.isFailure)
      val err = testRes.exceptionOrNull()
      assertNotNull(err)
      assertTrue(err?.message?.contains("not configured", ignoreCase = true) == true)
    }
  }

  @Test
  fun testSupabaseOfflineMealSyncGraceful() = runTest {
    // When offline / unconfigured, repository methods return Result.success(Unit) to allow seamless offline Room operation
    if (!supabaseRepository.isConfigured()) {
      val meal = LoggedMeal(
        id = "m_1",
        title = "Roti and Dal",
        mealType = "Lunch",
        time = "1:00 PM",
        calories = 350,
        proteinGrams = 12,
        carbsGrams = 55,
        fatGrams = 7,
        tag = "Balanced",
        imageUrl = ""
      )
      val res = supabaseRepository.uploadMeal(1L, meal)
      assertTrue(res.isSuccess)
    }
  }

  @Test
  fun testMoshiDtoSerialization() {
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    // 1. User Profile DTO
    val profileAdapter = moshi.adapter(SupabaseUserProfileDto::class.java)
    val profileDto = SupabaseUserProfileDto(
      userId = 1L,
      name = "Priya Sharma",
      email = "priya@ahaar.ai",
      goal = "lose_weight",
      dailyCalorieTarget = 2000
    )
    val profileJson = profileAdapter.toJson(profileDto)
    assertTrue(profileJson.contains("\"user_id\":1"))
    assertTrue(profileJson.contains("\"daily_calorie_target\":2000"))

    val parsedProfile = profileAdapter.fromJson(profileJson)
    assertNotNull(parsedProfile)
    assertEquals(1L, parsedProfile?.userId)
    assertEquals("Priya Sharma", parsedProfile?.name)

    // 2. Meal DTO
    val mealAdapter = moshi.adapter(SupabaseMealDto::class.java)
    val mealDto = SupabaseMealDto(
      userId = 1L,
      title = "Paneer Tikka",
      mealType = "Dinner",
      time = "8:00 PM",
      calories = 280,
      proteinGrams = 22.0,
      carbsGrams = 8.0,
      fatGrams = 14.0
    )
    val mealJson = mealAdapter.toJson(mealDto)
    assertTrue(mealJson.contains("\"protein_grams\":22"))
    val parsedMeal = mealAdapter.fromJson(mealJson)
    assertNotNull(parsedMeal)
    assertEquals("Paneer Tikka", parsedMeal?.title)

    // 3. Scanned Meal DTO
    val scanAdapter = moshi.adapter(SupabaseScannedMealDto::class.java)
    val scanDto = SupabaseScannedMealDto(
      userId = 1L,
      dishName = "Moong Dal Khichdi",
      mealType = "Dinner",
      calories = 310,
      proteinGrams = 14.0,
      carbsGrams = 52.0,
      fatGrams = 6.0,
      fiberGrams = 5.0,
      scanSource = "CAMERA"
    )
    val scanJson = scanAdapter.toJson(scanDto)
    assertTrue(scanJson.contains("\"dish_name\":\"Moong Dal Khichdi\""))
    val parsedScan = scanAdapter.fromJson(scanJson)
    assertNotNull(parsedScan)
    assertEquals(310, parsedScan?.calories)

    // 4. Supabase User Response with boolean email_verified
    val userJson = """
      {
        "id": "user-uuid-12345",
        "email": "yas46901@gmail.com",
        "user_metadata": {
          "email": "yas46901@gmail.com",
          "email_verified": true,
          "full_name": "Yas Sharma",
          "name": "Yas Sharma",
          "picture": "https://lh3.googleusercontent.com/avatar.jpg"
        }
      }
    """.trimIndent()
    val userAdapter = moshi.adapter(com.example.data.supabase.model.SupabaseAuthUserResponse::class.java)
    val parsedUser = userAdapter.fromJson(userJson)
    assertNotNull(parsedUser)
    assertEquals("yas46901@gmail.com", parsedUser?.email)
    assertEquals("Yas Sharma", parsedUser?.userMetadata?.fullName)
    assertEquals(true, parsedUser?.userMetadata?.emailVerified)
  }

  @Test
  fun testGoogleOAuthUrlGeneration() {
    val url = SupabaseClient.getGoogleOAuthUrl()
    assertTrue(url.contains("provider=google"))
    assertTrue(url.contains("redirect_to=calsnapix%3A%2F%2Fauth-callback") || url.contains("redirect_to=calsnapix://auth-callback"))
    assertTrue(url.contains("/auth/v1/authorize"))
  }

  @Test
  fun testAuthCallbackUriParsingFragment() {
    val sampleUri = "calsnapix://auth-callback#access_token=sample_jwt_token_123&token_type=bearer&expires_in=3600&refresh_token=sample_refresh_456"
    val params = SupabaseClient.parseAuthCallbackUri(sampleUri)
    assertEquals("sample_jwt_token_123", params["access_token"])
    assertEquals("bearer", params["token_type"])
    assertEquals("3600", params["expires_in"])
    assertEquals("sample_refresh_456", params["refresh_token"])
  }

  @Test
  fun testAuthCallbackUriParsingQueryError() {
    val sampleErrorUri = "calsnapix://auth-callback?error=access_denied&error_description=User+cancelled"
    val params = SupabaseClient.parseAuthCallbackUri(sampleErrorUri)
    assertEquals("access_denied", params["error"])
    assertEquals("User cancelled", params["error_description"])
  }

  @Test
  fun testAuthCallbackUriParsingLocalhost() {
    val localhostUri = "http://localhost:3000/#access_token=supabase_jwt_789&token_type=bearer&expires_in=3600&refresh_token=refresh_101"
    val params = SupabaseClient.parseAuthCallbackUri(localhostUri)
    assertEquals("supabase_jwt_789", params["access_token"])
    assertEquals("bearer", params["token_type"])
    assertEquals("refresh_101", params["refresh_token"])
  }
}
