package com.example.data.supabase.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabaseUserProfileDto(
  @Json(name = "user_id") val userId: Long,
  @Json(name = "name") val name: String,
  @Json(name = "email") val email: String,
  @Json(name = "goal") val goal: String = "lose_weight",
  @Json(name = "diet_type") val dietType: String = "veg",
  @Json(name = "region") val region: String = "north",
  @Json(name = "daily_calorie_target") val dailyCalorieTarget: Int = 2000,
  @Json(name = "protein_target_grams") val proteinTargetGrams: Int = 90,
  @Json(name = "carbs_target_grams") val carbsTargetGrams: Int = 210,
  @Json(name = "fats_target_grams") val fatsTargetGrams: Int = 55,
  @Json(name = "onboarding_answers") val onboardingAnswers: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseMealDto(
  @Json(name = "id") val id: Long? = null,
  @Json(name = "user_id") val userId: Long,
  @Json(name = "title") val title: String,
  @Json(name = "meal_type") val mealType: String,
  @Json(name = "time") val time: String,
  @Json(name = "calories") val calories: Int,
  @Json(name = "protein_grams") val proteinGrams: Double = 0.0,
  @Json(name = "carbs_grams") val carbsGrams: Double = 0.0,
  @Json(name = "fat_grams") val fatGrams: Double = 0.0,
  @Json(name = "tag") val tag: String = "",
  @Json(name = "image_url") val imageUrl: String = ""
)

@JsonClass(generateAdapter = true)
data class SupabaseScannedMealDto(
  @Json(name = "id") val id: Long? = null,
  @Json(name = "user_id") val userId: Long,
  @Json(name = "dish_name") val dishName: String,
  @Json(name = "meal_type") val mealType: String,
  @Json(name = "serving_description") val servingDescription: String = "1 standard plate",
  @Json(name = "portion_multiplier") val portionMultiplier: Float = 1.0f,
  @Json(name = "image_url") val imageUrl: String = "",
  @Json(name = "photo_uri") val photoUri: String? = null,
  @Json(name = "scan_source") val scanSource: String = "CAMERA",
  @Json(name = "barcode") val barcode: String? = null,
  @Json(name = "chef_note") val chefNote: String = "",
  @Json(name = "calories") val calories: Int,
  @Json(name = "protein_grams") val proteinGrams: Double = 0.0,
  @Json(name = "carbs_grams") val carbsGrams: Double = 0.0,
  @Json(name = "fat_grams") val fatGrams: Double = 0.0,
  @Json(name = "fiber_grams") val fiberGrams: Double = 0.0,
  @Json(name = "sugar_grams") val sugarGrams: Double = 0.0,
  @Json(name = "sodium_mg") val sodiumMg: Double = 0.0,
  @Json(name = "saturated_fat_grams") val saturatedFatGrams: Double = 0.0,
  @Json(name = "micronutrients") val micronutrients: String = ""
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthRequest(
  @Json(name = "email") val email: String,
  @Json(name = "password") val password: String,
  @Json(name = "data") val data: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthResponse(
  @Json(name = "access_token") val accessToken: String? = null,
  @Json(name = "token_type") val tokenType: String? = null,
  @Json(name = "expires_in") val expiresIn: Long? = null,
  @Json(name = "refresh_token") val refreshToken: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseIdTokenAuthRequest(
  @Json(name = "provider") val provider: String = "google",
  @Json(name = "id_token") val idToken: String,
  @Json(name = "nonce") val nonce: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseUserMetadata(
  @Json(name = "full_name") val fullName: String? = null,
  @Json(name = "name") val name: String? = null,
  @Json(name = "picture") val picture: String? = null,
  @Json(name = "avatar_url") val avatarUrl: String? = null,
  @Json(name = "email") val email: String? = null,
  @Json(name = "email_verified") val emailVerified: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthUserResponse(
  @Json(name = "id") val id: String? = null,
  @Json(name = "email") val email: String? = null,
  @Json(name = "phone") val phone: String? = null,
  @Json(name = "user_metadata") val userMetadata: SupabaseUserMetadata? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseFoodCalorieRequest(
  @Json(name = "p_food_query") val foodQuery: String,
  @Json(name = "p_serving_grams") val servingGrams: Double? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseFoodCalorieResponse(
  @Json(name = "found") val found: Boolean = false,
  @Json(name = "dish_name") val dishName: String? = null,
  @Json(name = "serving_grams") val servingGrams: Double? = null,
  @Json(name = "calories") val calories: Int? = null,
  @Json(name = "protein_grams") val proteinGrams: Double? = null,
  @Json(name = "carbs_grams") val carbsGrams: Double? = null,
  @Json(name = "fat_grams") val fatGrams: Double? = null,
  @Json(name = "fiber_grams") val fiberGrams: Double? = null,
  @Json(name = "iron_mg") val ironMg: Double? = null,
  @Json(name = "calcium_mg") val calciumMg: Double? = null,
  @Json(name = "source") val source: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseAccuracyTestDto(
  @Json(name = "id") val id: Long? = null,
  @Json(name = "user_id") val userId: Long? = null,
  @Json(name = "meal_name") val mealName: String,
  @Json(name = "category") val category: String = "Curry / Sabzi",
  @Json(name = "weighed_grams") val weighedGrams: Double,
  @Json(name = "estimated_grams") val estimatedGrams: Double,
  @Json(name = "weighed_kcal") val weighedKcal: Double,
  @Json(name = "estimated_kcal") val estimatedKcal: Double,
  @Json(name = "error_percent") val errorPercent: Double,
  @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseUserCorrectionDto(
  @Json(name = "id") val id: Long? = null,
  @Json(name = "user_id") val userId: Long,
  @Json(name = "dish_id") val dishId: String,
  @Json(name = "original_value") val originalValue: String,
  @Json(name = "corrected_value") val correctedValue: String,
  @Json(name = "field_changed") val fieldChanged: String, // 'portion', 'dish_name', 'calories'
  @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseUserGoalDto(
  @Json(name = "user_id") val userId: Long,
  @Json(name = "goal_type") val goalType: String, // 'lose_weight', 'build_muscle', 'maintain'
  @Json(name = "calorie_target") val calorieTarget: Int,
  @Json(name = "protein_target") val proteinTarget: Int,
  @Json(name = "carbs_target") val carbsTarget: Int,
  @Json(name = "fat_target") val fatTarget: Int,
  @Json(name = "updated_at") val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseWrongResultReportDto(
  @Json(name = "id") val id: Long? = null,
  @Json(name = "dish_name") val dishName: String,
  @Json(name = "photo_id") val photoId: String? = null,
  @Json(name = "user_note") val userNote: String,
  @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseDeleteUserDataRequest(
  @Json(name = "p_user_id") val userId: Long
)


