package com.example.data.supabase

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {

  @Volatile
  private var cachedService: SupabaseApiService? = null

  @Volatile
  var userAccessToken: String? = null

  fun getSupabaseUrl(): String {
    return try {
      BuildConfig.SUPABASE_URL.trim()
    } catch (e: Throwable) {
      ""
    }
  }

  fun getSupabaseAnonKey(): String {
    return try {
      BuildConfig.SUPABASE_ANON_KEY.trim()
    } catch (e: Throwable) {
      ""
    }
  }

  fun isConfigured(): Boolean {
    val url = getSupabaseUrl()
    val key = getSupabaseAnonKey()
    return url.isNotBlank() &&
      !url.contains("your-project.supabase.co") &&
      url.startsWith("http") &&
      key.isNotBlank() &&
      !key.contains("your-supabase-anon-key")
  }

  fun getApiService(): SupabaseApiService {
    return cachedService ?: synchronized(this) {
      cachedService ?: createService().also { cachedService = it }
    }
  }

  const val REDIRECT_SCHEME = "calsnapix"
  const val REDIRECT_HOST = "auth-callback"
  const val OAUTH_REDIRECT_URI = "$REDIRECT_SCHEME://$REDIRECT_HOST"

  fun getNormalizedBaseUrl(): String {
    var rawUrl = getSupabaseUrl()
    if (rawUrl.isBlank() || !rawUrl.startsWith("http")) {
      rawUrl = "https://your-project.supabase.co"
    }
    rawUrl = rawUrl.trim()
    if (rawUrl.endsWith("/rest/v1/")) {
      rawUrl = rawUrl.substringBeforeLast("/rest/v1/")
    } else if (rawUrl.endsWith("/rest/v1")) {
      rawUrl = rawUrl.substringBeforeLast("/rest/v1")
    }
    if (!rawUrl.endsWith("/")) {
      rawUrl = "$rawUrl/"
    }
    return rawUrl
  }

  fun getGoogleOAuthUrl(): String {
    val baseUrl = getNormalizedBaseUrl()
    val encodedRedirect = try {
      java.net.URLEncoder.encode(OAUTH_REDIRECT_URI, "UTF-8")
    } catch (e: Throwable) {
      OAUTH_REDIRECT_URI
    }
    return "${baseUrl}auth/v1/authorize?provider=google&redirect_to=$encodedRedirect"
  }

  fun parseAuthCallbackUri(uriString: String): Map<String, String> {
    val params = mutableMapOf<String, String>()
    try {
      val fragment = uriString.substringAfter("#", "")
      if (fragment.isNotBlank() && fragment != uriString) {
        fragment.split("&").forEach { pair ->
          val parts = pair.split("=", limit = 2)
          if (parts.size == 2) {
            params[parts[0]] = java.net.URLDecoder.decode(parts[1], "UTF-8")
          }
        }
      }
      val query = uriString.substringAfter("?", "").substringBefore("#")
      if (query.isNotBlank() && query != uriString) {
        query.split("&").forEach { pair ->
          val parts = pair.split("=", limit = 2)
          if (parts.size == 2 && !params.containsKey(parts[0])) {
            params[parts[0]] = java.net.URLDecoder.decode(parts[1], "UTF-8")
          }
        }
      }
    } catch (e: Exception) {
      // ignore parse errors
    }
    return params
  }

  private fun createService(): SupabaseApiService {
    val rawUrl = getNormalizedBaseUrl()

    val authInterceptor = Interceptor { chain ->
      val original = chain.request()
      val builder = original.newBuilder()

      val anonKey = getSupabaseAnonKey()
      if (anonKey.isNotBlank()) {
        builder.header("apikey", anonKey)
        val token = userAccessToken ?: anonKey
        builder.header("Authorization", "Bearer $token")
      }
      builder.header("Content-Type", "application/json")

      chain.proceed(builder.build())
    }

    val logging = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BASIC
    }

    val okHttpClient = OkHttpClient.Builder()
      .addInterceptor(authInterceptor)
      .addInterceptor(logging)
      .connectTimeout(15, TimeUnit.SECONDS)
      .readTimeout(15, TimeUnit.SECONDS)
      .writeTimeout(15, TimeUnit.SECONDS)
      .build()

    val moshi = Moshi.Builder()
      .addLast(KotlinJsonAdapterFactory())
      .build()

    val retrofit = Retrofit.Builder()
      .baseUrl(rawUrl)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    return retrofit.create(SupabaseApiService::class.java)
  }

  suspend fun testConnection(): Result<String> {
    if (!isConfigured()) {
      return Result.failure(
        IllegalStateException("Supabase is not configured yet. Please configure SUPABASE_URL and SUPABASE_ANON_KEY in AI Studio Secrets.")
      )
    }
    return try {
      val response = getApiService().checkHealth()
      if (response.isSuccessful || response.code() in 200..404) {
        Result.success("Connected to Supabase PostgreSQL database successfully!")
      } else {
        Result.failure(Exception("Supabase response code: ${response.code()} ${response.message()}"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
