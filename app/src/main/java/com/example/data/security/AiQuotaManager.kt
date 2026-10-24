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
   * Verified Pro users have unlimited access. Free users are capped at 5 per day.
   */
  fun checkCanPerformScan(context: Context, isPro: Boolean): QuotaResult {
    if (isPro) {
      return QuotaResult(allowed = true, remainingScans = -1, isPro = true)
    }

    val prefs = getPrefs(context)
    val today = getTodayDateString()
    val lastDate = prefs.getString(KEY_LAST_SCAN_DATE, "") ?: ""
    val currentCount = if (lastDate == today) {
      prefs.getInt(KEY_DAILY_SCAN_COUNT, 0)
    } else {
      0
    }

    val remaining = (FREE_TIER_DAILY_SCAN_LIMIT - currentCount).coerceAtLeast(0)
    val allowed = currentCount < FREE_TIER_DAILY_SCAN_LIMIT

    return QuotaResult(
      allowed = allowed,
      remainingScans = remaining,
      isPro = false,
      message = if (!allowed) {
        "Daily free AI meal scan limit reached (5/5). Upgrade to CalSnapix Pro for unlimited scans."
      } else {
        "$remaining daily free scans remaining."
      }
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
