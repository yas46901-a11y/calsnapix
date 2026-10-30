package com.example.data.supabase

import com.example.data.supabase.model.SupabaseAuthRequest
import com.example.data.supabase.model.SupabaseAuthResponse
import com.example.data.supabase.model.SupabaseMealDto
import com.example.data.supabase.model.SupabaseScannedMealDto
import com.example.data.supabase.model.SupabaseUserProfileDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApiService {

  // --- PostgREST Database Endpoints ---

  @GET("rest/v1/user_profiles")
  suspend fun getUserProfile(
    @Query("user_id") userIdFilter: String,
    @Query("select") select: String = "*"
  ): Response<List<SupabaseUserProfileDto>>

  @Headers("Prefer: resolution=merge-duplicates")
  @POST("rest/v1/user_profiles")
  suspend fun upsertUserProfile(
    @Body profile: SupabaseUserProfileDto
  ): Response<ResponseBody>

  @DELETE("rest/v1/user_profiles")
  suspend fun deleteUserProfileByUser(
    @Query("user_id") userIdFilter: String
  ): Response<ResponseBody>

  @GET("rest/v1/meals")
  suspend fun getMeals(
    @Query("user_id") userIdFilter: String,
    @Query("select") select: String = "*"
  ): Response<List<SupabaseMealDto>>

  @Headers("Prefer: return=representation")
  @POST("rest/v1/meals")
  suspend fun insertMeal(
    @Body meal: SupabaseMealDto
  ): Response<List<SupabaseMealDto>>

  @DELETE("rest/v1/meals")
  suspend fun deleteMealsByUser(
    @Query("user_id") userIdFilter: String
  ): Response<ResponseBody>

  @GET("rest/v1/scanned_meals")
  suspend fun getScannedMeals(
    @Query("user_id") userIdFilter: String,
    @Query("select") select: String = "*"
  ): Response<List<SupabaseScannedMealDto>>

  @Headers("Prefer: return=representation")
  @POST("rest/v1/scanned_meals")
  suspend fun insertScannedMeal(
    @Body scannedMeal: SupabaseScannedMealDto
  ): Response<List<SupabaseScannedMealDto>>

  @DELETE("rest/v1/scanned_meals")
  suspend fun deleteScannedMealsByUser(
    @Query("user_id") userIdFilter: String
  ): Response<ResponseBody>

  // --- Supabase GoTrue Auth Endpoints ---

  @POST("auth/v1/signup")
  suspend fun signUp(
    @Body request: SupabaseAuthRequest
  ): Response<SupabaseAuthResponse>

  @POST("auth/v1/token?grant_type=password")
  suspend fun signInWithPassword(
    @Body request: SupabaseAuthRequest
  ): Response<SupabaseAuthResponse>

  @POST("auth/v1/token?grant_type=id_token")
  suspend fun signInWithIdToken(
    @Body request: com.example.data.supabase.model.SupabaseIdTokenAuthRequest
  ): Response<SupabaseAuthResponse>

  @GET("auth/v1/user")
  suspend fun getAuthUser(): Response<com.example.data.supabase.model.SupabaseAuthUserResponse>

  @POST("rest/v1/rpc/calculate_meal_calories")
  suspend fun calculateMealCalories(
    @Body request: com.example.data.supabase.model.SupabaseFoodCalorieRequest
  ): Response<com.example.data.supabase.model.SupabaseFoodCalorieResponse>

  // --- Accuracy Tests ---
  @POST("rest/v1/accuracy_tests")
  suspend fun insertAccuracyTest(
    @Body test: com.example.data.supabase.model.SupabaseAccuracyTestDto
  ): Response<ResponseBody>

  @GET("rest/v1/accuracy_tests")
  suspend fun getAccuracyTests(
    @Query("user_id") userIdFilter: String,
    @Query("select") select: String = "*",
    @Query("order") order: String = "created_at.desc"
  ): Response<List<com.example.data.supabase.model.SupabaseAccuracyTestDto>>

  // --- User Corrections ---
  @POST("rest/v1/user_corrections")
  suspend fun insertUserCorrection(
    @Body correction: com.example.data.supabase.model.SupabaseUserCorrectionDto
  ): Response<ResponseBody>

  @GET("rest/v1/user_corrections")
  suspend fun getUserCorrections(
    @Query("user_id") userIdFilter: String,
    @Query("select") select: String = "*",
    @Query("order") order: String = "created_at.desc"
  ): Response<List<com.example.data.supabase.model.SupabaseUserCorrectionDto>>

  // --- User Goals ---
  @Headers("Prefer: resolution=merge-duplicates")
  @POST("rest/v1/user_goals")
  suspend fun upsertUserGoal(
    @Body goal: com.example.data.supabase.model.SupabaseUserGoalDto
  ): Response<ResponseBody>

  @GET("rest/v1/user_goals")
  suspend fun getUserGoal(
    @Query("user_id") userIdFilter: String,
    @Query("select") select: String = "*"
  ): Response<List<com.example.data.supabase.model.SupabaseUserGoalDto>>

  // --- Wrong Result Reports ---
  @POST("rest/v1/wrong_result_reports")
  suspend fun submitWrongResultReport(
    @Body report: com.example.data.supabase.model.SupabaseWrongResultReportDto
  ): Response<ResponseBody>

  // --- Data Deletion RPC ---
  @POST("rest/v1/rpc/delete_all_user_data")
  suspend fun deleteAllUserData(
    @Body req: com.example.data.supabase.model.SupabaseDeleteUserDataRequest
  ): Response<ResponseBody>

  @GET("rest/v1/")
  suspend fun checkHealth(): Response<ResponseBody>
}
