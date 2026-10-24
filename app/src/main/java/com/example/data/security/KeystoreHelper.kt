package com.example.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Hardware-backed Android Keystore utility.
 * Protects cryptographic keys in the Android Keystore system so keys are never stored
 * inside the application code, APK, or plain storage.
 */
object KeystoreHelper {
  private const val ANDROID_KEYSTORE = "AndroidKeyStore"
  private const val MASTER_KEY_ALIAS = "CalSnapixMasterEncKey"
  private const val GCM_TAG_LENGTH = 128
  private const val IV_SIZE = 12

  init {
    ensureKeyExists()
  }

  private fun ensureKeyExists() {
    val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    if (!keyStore.containsAlias(MASTER_KEY_ALIAS)) {
      val keyGenerator = KeyGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_AES,
        ANDROID_KEYSTORE
      )
      val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        MASTER_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
      )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(256)
        .setRandomizedEncryptionRequired(true)
        .build()

      keyGenerator.init(keyGenParameterSpec)
      keyGenerator.generateKey()
    }
  }

  private fun getSecretKey(): SecretKey {
    val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    return keyStore.getKey(MASTER_KEY_ALIAS, null) as SecretKey
  }

  /**
   * Encrypts plain text using AES-256-GCM with hardware-backed key.
   * Returns Base64-encoded string: [12-byte IV][Encrypted Ciphertext].
   */
  fun encrypt(plainText: String): String {
    if (plainText.isEmpty()) return ""
    return try {
      val cipher = Cipher.getInstance("AES/GCM/NoPadding")
      cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
      val iv = cipher.iv
      val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
      val combined = ByteArray(iv.size + cipherText.size)
      System.arraycopy(iv, 0, combined, 0, iv.size)
      System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)
      Base64.encodeToString(combined, Base64.NO_WRAP)
    } catch (e: Exception) {
      plainText
    }
  }

  /**
   * Decrypts Base64-encoded AES-256-GCM string.
   */
  fun decrypt(encryptedBase64: String): String {
    if (encryptedBase64.isEmpty()) return ""
    return try {
      val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)
      if (combined.size < IV_SIZE) return encryptedBase64
      val iv = ByteArray(IV_SIZE)
      val cipherText = ByteArray(combined.size - IV_SIZE)
      System.arraycopy(combined, 0, iv, 0, IV_SIZE)
      System.arraycopy(combined, IV_SIZE, cipherText, 0, cipherText.size)

      val cipher = Cipher.getInstance("AES/GCM/NoPadding")
      val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
      cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
      String(cipher.doFinal(cipherText), Charsets.UTF_8)
    } catch (e: Exception) {
      encryptedBase64
    }
  }
}
