package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Enforces per-user daily rate limits and scan quotas for AI meal recognition.
 * Prevents API bill runaway and abuse on client and proxy server layers.
 */
object AiQuotaManager {
  private const val PREFS_NAME = "calsnapix_ai_quota"
  private const val KEY_LAST_SCAN_DATE = "last_scan_date"
  private const val KEY_DAILY_SCAN_COUNT = "daily_scan_count"

  const val FREE_TIER_DAILY_SCAN_LIMIT = 5

  private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  }

  private fun getTodayDateString(): String {
    return SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
  }

  /**
   * Checks if the user is allowed to perform an AI meal scan or ask AI.
   * CalSnapix is 100% free with unlimited AI scans and barcode lookups for all users.
   */
  fun checkCanPerformScan(context: Context, isPro: Boolean = true): QuotaResult {
    return QuotaResult(
      allowed = true,
      remainingScans = -1,
      isPro = true,
      message = "Unlimited AI meal scans unlocked for free!"
    )
  }

  /**
   * Increments the daily scan counter after a successful scan.
   */
  fun recordSuccessfulScan(context: Context, isPro: Boolean) {
    if (isPro) return // No count tracking needed for unlimited Pro users

    val prefs = getPrefs(context)
    val today = getTodayDateString()
    val lastDate = prefs.getString(KEY_LAST_SCAN_DATE, "") ?: ""
    val count = if (lastDate == today) {
      prefs.getInt(KEY_DAILY_SCAN_COUNT, 0)
    } else {
      0
    }

    prefs.edit()
      .putString(KEY_LAST_SCAN_DATE, today)
      .putInt(KEY_DAILY_SCAN_COUNT, count + 1)
      .apply()
  }

  data class QuotaResult(
    val allowed: Boolean,
    val remainingScans: Int,
    val isPro: Boolean,
    val message: String = ""
  )
}
