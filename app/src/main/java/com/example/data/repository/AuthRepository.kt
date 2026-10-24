package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale

class AuthRepository(context: Context) {
  private val database = AppDatabase.getDatabase(context)
  private val userDao: UserDao = database.userDao()
  private val prefs = context.getSharedPreferences("ahaar_auth_session", Context.MODE_PRIVATE)

  private val _currentUser = MutableStateFlow<UserEntity?>(null)
  val currentUser: Flow<UserEntity?> = _currentUser.asStateFlow()

  fun isLoggedIn(): Boolean {
    val loggedInFlag = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    val savedId = prefs.getLong(KEY_LOGGED_IN_USER_ID, -1L)
    return loggedInFlag || savedId > 0
  }

  fun getSavedUserId(): Long {
    val savedId = prefs.getLong(KEY_LOGGED_IN_USER_ID, -1L)
    return if (savedId > 0) savedId else 1L
  }

  fun getSavedUserName(): String {
    return prefs.getString(KEY_USER_NAME, "Priya Sharma") ?: "Priya Sharma"
  }

  fun getSavedUserEmail(): String {
    return prefs.getString(KEY_USER_EMAIL, "priya@ahaar.ai") ?: "priya@ahaar.ai"
  }

  fun persistUserSession(user: UserEntity) {
    prefs.edit()
      .putLong(KEY_LOGGED_IN_USER_ID, user.id)
      .putString(KEY_USER_NAME, user.name)
      .putString(KEY_USER_EMAIL, user.email)
      .putBoolean(KEY_IS_LOGGED_IN, true)
      .putBoolean(KEY_ONBOARDING_COMPLETED, true)
      .apply()
    _currentUser.value = user
  }

  suspend fun initialize() {
    withContext(Dispatchers.IO) {
      // 1. Check existing saved session first
      val savedUserId = prefs.getLong(KEY_LOGGED_IN_USER_ID, -1L)
      if (savedUserId > 0) {
        val user = userDao.getUserById(savedUserId)
        if (user != null) {
          _currentUser.value = user
          prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_USER_EMAIL, user.email)
            .apply()
          return@withContext
        }
      }

      // 2. If database is completely empty, seed default user and mark session active
      val userCount = userDao.getUserCount()
      if (userCount == 0) {
        val salt = generateSalt()
        val defaultUser = UserEntity(
          name = "Priya Sharma",
          email = "priya@ahaar.ai",
          passwordHash = hashPassword("password123", salt),
          salt = salt,
          avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCQb32qQ8lE6g2nZ1jY9wK_mR7fD4yX_sL2kP1vN8mJ5t=s400",
          goal = "lose_weight",
          dietType = "veg",
          region = "north",
          dailyCalorieTarget = 2000,
          proteinTargetGrams = 90,
          carbsTargetGrams = 210,
          fatsTargetGrams = 55
        )
        val newId = userDao.insertUser(defaultUser)
        val createdUser = defaultUser.copy(id = newId)
        prefs.edit()
          .putLong(KEY_LOGGED_IN_USER_ID, newId)
          .putString(KEY_USER_NAME, defaultUser.name)
          .putString(KEY_USER_EMAIL, defaultUser.email)
          .putBoolean(KEY_IS_LOGGED_IN, true)
          .putBoolean(KEY_ONBOARDING_COMPLETED, false)
          .apply()
        _currentUser.value = createdUser
        return@withContext
      } else {
        // If users already exist and user was not explicitly logged out, keep first user session
        val isExplicitlyLoggedOut = prefs.contains(KEY_IS_LOGGED_IN) && !prefs.getBoolean(KEY_IS_LOGGED_IN, true)
        if (!isExplicitlyLoggedOut) {
          val firstUser = userDao.getUserById(1L)
          if (firstUser != null) {
            prefs.edit()
              .putLong(KEY_LOGGED_IN_USER_ID, firstUser.id)
              .putString(KEY_USER_NAME, firstUser.name)
              .putString(KEY_USER_EMAIL, firstUser.email)
              .putBoolean(KEY_IS_LOGGED_IN, true)
              .apply()
            _currentUser.value = firstUser
            return@withContext
          }
        }
      }
    }
  }

  fun isOnboardingCompleted(): Boolean {
    return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
  }

  fun setOnboardingCompleted(completed: Boolean = true) {
    prefs.edit()
      .putBoolean(KEY_ONBOARDING_COMPLETED, completed)
      .putInt(KEY_ONBOARDING_CURRENT_STEP, 0)
      .apply()
  }

  fun getOnboardingStep(): Int {
    return prefs.getInt(KEY_ONBOARDING_CURRENT_STEP, 0)
  }

  fun saveOnboardingStep(step: Int) {
    prefs.edit().putInt(KEY_ONBOARDING_CURRENT_STEP, step).apply()
  }

  fun getOnboardingAnswers(): String {
    return prefs.getString(KEY_ONBOARDING_ANSWERS, "{}") ?: "{}"
  }

  fun saveOnboardingAnswers(json: String) {
    prefs.edit().putString(KEY_ONBOARDING_ANSWERS, json).apply()
  }

  suspend fun signUp(
    name: String,
    email: String,
    password: String
  ): Result<UserEntity> = withContext(Dispatchers.IO) {
    val cleanName = name.trim()
    val cleanEmail = email.trim().lowercase(Locale.ROOT)

    if (cleanName.isBlank()) {
      return@withContext Result.failure(IllegalArgumentException("Please enter your name"))
    }
    if (!isValidEmail(cleanEmail)) {
      return@withContext Result.failure(IllegalArgumentException("Please enter a valid email address"))
    }
    if (password.length < 6) {
      return@withContext Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
    }
    val trivialPasswords = setOf("123456", "password", "qwerty", "admin123", "calsnapix", "welcome", "12345678", "abcdef")
    if (trivialPasswords.contains(password.lowercase(Locale.ROOT))) {
      return@withContext Result.failure(IllegalArgumentException("Password is too common and easily guessable. Please choose a stronger password."))
    }

    val existing = userDao.getUserByEmail(cleanEmail)
    if (existing != null) {
      return@withContext Result.failure(IllegalArgumentException("An account with this email already exists"))
    }

    val salt = generateSalt()
    val passwordHash = hashPassword(password, salt)
    val newUser = UserEntity(
      name = cleanName,
      email = cleanEmail,
      passwordHash = passwordHash,
      salt = salt,
      avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCQb32qQ8lE6g2nZ1jY9wK_mR7fD4yX_sL2kP1vN8mJ5t=s400",
      goal = "lose_weight",
      dietType = "veg",
      region = "north",
      dailyCalorieTarget = 2000,
      proteinTargetGrams = 90,
      carbsTargetGrams = 210,
      fatsTargetGrams = 55
    )

    val id = userDao.insertUser(newUser)
    val createdUser = newUser.copy(id = id)
    prefs.edit()
      .putLong(KEY_LOGGED_IN_USER_ID, id)
      .putString(KEY_USER_NAME, createdUser.name)
      .putString(KEY_USER_EMAIL, createdUser.email)
      .putBoolean(KEY_IS_LOGGED_IN, true)
      .putBoolean(KEY_ONBOARDING_COMPLETED, true)
      .apply()
    _currentUser.value = createdUser
    Result.success(createdUser)
  }

  suspend fun login(
    email: String,
    password: String
  ): Result<UserEntity> = withContext(Dispatchers.IO) {
    val cleanEmail = email.trim().lowercase(Locale.ROOT)

    if (cleanEmail.isBlank()) {
      return@withContext Result.failure(IllegalArgumentException("Please enter your email"))
    }
    if (password.isBlank()) {
      return@withContext Result.failure(IllegalArgumentException("Please enter your password"))
    }

    // Rate Limiting & Account Lockout Protection against Brute Force attacks
    val lockKey = "lockout_$cleanEmail"
    val attemptsKey = "attempts_$cleanEmail"
    val lockUntilMs = prefs.getLong(lockKey, 0L)
    val now = System.currentTimeMillis()

    if (now < lockUntilMs) {
      val remainingSeconds = ((lockUntilMs - now) / 1000).coerceAtLeast(1)
      return@withContext Result.failure(
        IllegalStateException("Too many failed login attempts. For your security, this account is temporarily locked. Please try again in $remainingSeconds seconds.")
      )
    }

    val user = userDao.getUserByEmail(cleanEmail)
      ?: run {
        recordFailedLoginAttempt(cleanEmail)
        return@withContext Result.failure(IllegalArgumentException("No account found with this email"))
      }

    val testHash = hashPassword(password, user.salt)
    if (testHash != user.passwordHash) {
      recordFailedLoginAttempt(cleanEmail)
      val attempts = prefs.getInt(attemptsKey, 1)
      val remaining = (5 - attempts).coerceAtLeast(0)
      val warnMessage = if (remaining > 0) {
        "Incorrect password. $remaining attempt(s) remaining before temporary lockout."
      } else {
        "Incorrect password. Account temporarily locked for 5 minutes due to multiple failed attempts."
      }
      return@withContext Result.failure(IllegalArgumentException(warnMessage))
    }

    // Successful login: clear failed attempts
    prefs.edit()
      .remove(attemptsKey)
      .remove(lockKey)
      .putLong(KEY_LOGGED_IN_USER_ID, user.id)
      .putString(KEY_USER_NAME, user.name)
      .putString(KEY_USER_EMAIL, user.email)
      .putBoolean(KEY_IS_LOGGED_IN, true)
      .putBoolean(KEY_ONBOARDING_COMPLETED, true)
      .apply()
    _currentUser.value = user
    Result.success(user)
  }

  private fun recordFailedLoginAttempt(email: String) {
    val attemptsKey = "attempts_$email"
    val lockKey = "lockout_$email"
    val currentAttempts = prefs.getInt(attemptsKey, 0) + 1
    val editor = prefs.edit().putInt(attemptsKey, currentAttempts)
    if (currentAttempts >= 5) {
      // 5-minute lockout
      editor.putLong(lockKey, System.currentTimeMillis() + 300_000L)
      editor.putInt(attemptsKey, 0)
    }
    editor.apply()
  }

  suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
    prefs.edit()
      .remove(KEY_LOGGED_IN_USER_ID)
      .remove(KEY_USER_NAME)
      .remove(KEY_USER_EMAIL)
      .putBoolean(KEY_IS_LOGGED_IN, false)
      .apply()
    _currentUser.value = null
    Result.success(Unit)
  }

  suspend fun deleteAccount(userId: Long): Result<Unit> = withContext(Dispatchers.IO) {
    try {
      userDao.deleteUserById(userId)
      prefs.edit()
        .remove(KEY_LOGGED_IN_USER_ID)
        .remove(KEY_USER_NAME)
        .remove(KEY_USER_EMAIL)
        .putBoolean(KEY_IS_LOGGED_IN, false)
        .remove(KEY_ONBOARDING_COMPLETED)
        .remove(KEY_ONBOARDING_ANSWERS)
        .apply()
      _currentUser.value = null
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun updateUserProfile(updatedUser: UserEntity) = withContext(Dispatchers.IO) {
    userDao.updateUser(updatedUser)
    prefs.edit()
      .putString(KEY_USER_NAME, updatedUser.name)
      .putString(KEY_USER_EMAIL, updatedUser.email)
      .apply()
    _currentUser.value = updatedUser
  }

  private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
  }

  private fun generateSalt(): String {
    val random = SecureRandom()
    val saltBytes = ByteArray(16)
    random.nextBytes(saltBytes)
    return saltBytes.joinToString("") { "%02x".format(it) }
  }

  private fun hashPassword(password: String, salt: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val input = "$salt:$password".toByteArray(Charsets.UTF_8)
    val digest = md.digest(input)
    return digest.joinToString("") { "%02x".format(it) }
  }

  companion object {
    private const val KEY_LOGGED_IN_USER_ID = "logged_in_user_id"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_NAME = "logged_in_user_name"
    private const val KEY_USER_EMAIL = "logged_in_user_email"
    private const val KEY_ONBOARDING_CURRENT_STEP = "onboarding_current_step"
    private const val KEY_ONBOARDING_ANSWERS = "onboarding_answers_json"
  }
}
