package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.NutritionalProfileDao
import com.example.data.local.NutritionalProfileEntity
import com.example.data.local.UserWithNutritionalProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository providing access to user nutritional profiles backed by Room SQLite database.
 */
class NutritionalProfileRepository(context: Context) {
  private val database = AppDatabase.getDatabase(context)
  private val profileDao: NutritionalProfileDao = database.nutritionalProfileDao()

  /**
   * Observe the user's nutritional profile in real-time.
   */
  fun observeProfile(userId: Long): Flow<NutritionalProfileEntity?> {
    return profileDao.observeProfileByUserId(userId)
  }

  /**
   * Get the user's current nutritional profile.
   */
  suspend fun getProfile(userId: Long): NutritionalProfileEntity? = withContext(Dispatchers.IO) {
    profileDao.getProfileByUserId(userId)
  }

  /**
   * Observe user joined with their complete nutritional profile.
   */
  fun observeUserWithProfile(userId: Long): Flow<UserWithNutritionalProfile?> {
    return profileDao.observeUserWithProfile(userId)
  }

  /**
   * Query profiles matching a specific dietary preference (e.g. Vegetarian, Vegan, Jain).
   */
  fun observeProfilesByDiet(dietaryPreference: String): Flow<List<NutritionalProfileEntity>> {
    return profileDao.observeProfilesByDiet(dietaryPreference)
  }

  /**
   * Insert or update the user's nutritional profile.
   */
  suspend fun saveProfile(profile: NutritionalProfileEntity): Long = withContext(Dispatchers.IO) {
    profileDao.insertOrUpdateProfile(profile)
  }

  /**
   * Delete the user's nutritional profile.
   */
  suspend fun deleteProfile(userId: Long) = withContext(Dispatchers.IO) {
    profileDao.deleteProfileByUserId(userId)
  }
}
