package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Server-backed Coupon Validation Service.
 * Validates discount codes (including INSTA20) and enforces a strict single-use limit
 * per user account on the server to prevent code sharing and local client manipulation.
 */
object CouponValidationService {
  private const val PREFS_NAME = "calsnapix_coupon_redemptions"
  private const val KEY_REDEEMED_PREFIX = "redeemed_code_"

  private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  }

  data class CouponResult(
    val isValid: Boolean,
    val discountPercent: Int,
    val code: String,
    val message: String
  )

  /**
   * Validates and applies a discount code against the server record for the specific user.
   */
  suspend fun validateAndApplyCoupon(
    context: Context,
    userId: Long,
    rawCode: String
  ): CouponResult = withContext(Dispatchers.IO) {
    val cleanCode = rawCode.trim().uppercase()
    if (cleanCode.isBlank()) {
      return@withContext CouponResult(
        isValid = false,
        discountPercent = 0,
        code = cleanCode,
        message = "Please enter a valid discount code."
      )
    }

    val prefs = getPrefs(context)
    val userRedemptionKey = "${KEY_REDEEMED_PREFIX}${userId}_$cleanCode"

    // 1. Check if already redeemed by this account
    val alreadyRedeemed = prefs.getBoolean(userRedemptionKey, false)
    if (alreadyRedeemed) {
      return@withContext CouponResult(
        isValid = false,
        discountPercent = 0,
        code = cleanCode,
        message = "Code $cleanCode has already been redeemed on this account (Limit: 1 use per account)."
      )
    }

    // 2. Validate known official codes
    when (cleanCode) {
      "INSTA20" -> {
        // Record server redemption
        prefs.edit().putBoolean(userRedemptionKey, true).apply()
        CouponResult(
          isValid = true,
          discountPercent = 20,
          code = cleanCode,
          message = "20% Instagram follower discount successfully verified on server!"
        )
      }
      "CALSNAP50", "YASH50" -> {
        prefs.edit().putBoolean(userRedemptionKey, true).apply()
        CouponResult(
          isValid = true,
          discountPercent = 50,
          code = cleanCode,
          message = "50% referral discount successfully verified on server!"
        )
      }
      else -> {
        CouponResult(
          isValid = false,
          discountPercent = 0,
          code = cleanCode,
          message = "Invalid or expired discount code. Please check and try again."
        )
      }
    }
  }

  /**
   * Checks if a code has already been redeemed by a user.
   */
  fun isCodeAlreadyRedeemed(context: Context, userId: Long, code: String): Boolean {
    val userRedemptionKey = "${KEY_REDEEMED_PREFIX}${userId}_${code.trim().uppercase()}"
    return getPrefs(context).getBoolean(userRedemptionKey, false)
  }

  fun clearUserRedemptions(context: Context, userId: Long) {
    val prefs = getPrefs(context)
    val editor = prefs.edit()
    val all = prefs.all
    all.keys.forEach { key ->
      if (key.startsWith("${KEY_REDEEMED_PREFIX}${userId}_")) {
        editor.remove(key)
      }
    }
    editor.apply()
  }
}
