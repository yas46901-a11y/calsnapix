package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthRepositoryTest {

  private lateinit var context: Context
  private lateinit var authRepository: AuthRepository

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    authRepository = AuthRepository(context)
  }

  @Test
  fun testInitializeAndDefaultUserLogin() = runBlocking {
    authRepository.initialize()
    val loginResult = authRepository.login("priya@ahaar.ai", "password123")
    assertTrue(loginResult.isSuccess)
    val user = loginResult.getOrNull()
    assertNotNull(user)
    assertEquals("priya@ahaar.ai", user?.email)
    assertEquals("Priya Sharma", user?.name)
  }

  @Test
  fun testInvalidPasswordFails() = runBlocking {
    authRepository.initialize()
    val loginResult = authRepository.login("priya@ahaar.ai", "wrongpassword")
    assertTrue(loginResult.isFailure)
  }

  @Test
  fun testSignUpAndLoginFlow() = runBlocking {
    authRepository.initialize()
    val signUpResult = authRepository.signUp("Rohan Verma", "rohan@ahaar.ai", "securePass99")
    assertTrue(signUpResult.isSuccess)
    val newUser = signUpResult.getOrNull()
    assertNotNull(newUser)
    assertEquals("rohan@ahaar.ai", newUser?.email)

    // Verify logging in with new credentials succeeds
    val loginResult = authRepository.login("rohan@ahaar.ai", "securePass99")
    assertTrue(loginResult.isSuccess)
    assertTrue(authRepository.isLoggedIn())
    assertEquals("Rohan Verma", authRepository.getSavedUserName())
  }

  @Test
  fun testSessionPersistenceRemainsLoggedIn() = runBlocking {
    authRepository.initialize()
    assertTrue(authRepository.isLoggedIn())
    val newRepoInstance = AuthRepository(context)
    assertTrue(newRepoInstance.isLoggedIn())
  }
}
