package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import coil.compose.AsyncImage
import com.example.ui.components.GlassBorderBrush
import com.example.ui.components.GlassBorderStroke
import com.example.ui.theme.AhaarPrimary
import com.example.ui.theme.AhaarPrimaryContainer
import com.example.ui.theme.AhaarPrimaryFixed
import com.example.ui.theme.AhaarSecondary
import com.example.ui.theme.AhaarTertiary
import com.example.ui.theme.AhaarTertiaryFixed

enum class AuthMode {
  LOGIN,
  SIGN_UP
}

@Composable
fun AuthScreen(
  onLogin: (String, String) -> Unit,
  onSignUp: (String, String, String) -> Unit,
  errorMessage: String?,
  isLoading: Boolean,
  logoUrl: String,
  onGoogleSignIn: () -> Unit = {},
  onPasteOAuthCallback: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var mode by remember { mutableStateOf(AuthMode.LOGIN) }
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var localValidationErr by remember { mutableStateOf<String?>(null) }
  var showPrivacyDialog by remember { mutableStateOf(false) }
  var showTermsDialog by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Transparent)
      .verticalScroll(scrollState)
      .padding(horizontal = 24.dp, vertical = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    // App Logo & Brand Header (Official Uploaded CalSnapix Logo)
    Box(
      modifier = Modifier
        .size(84.dp)
        .clip(CircleShape)
        .background(Color(0xFF0F172A))
        .border(2.dp, AhaarPrimary, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(R.drawable.ic_calsnapix_logo),
        contentDescription = "CalSnapix Logo",
        modifier = Modifier
          .size(76.dp)
          .clip(CircleShape)
      )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "CalSnapix",
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Personalized Indian Nutrition & Wellness",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Segmented Mode Switch: Login vs Sign Up (Glassmorphic)
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color.White.copy(alpha = 0.70f),
      border = BorderStroke(1.dp, GlassBorderBrush),
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(4.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (mode == AuthMode.LOGIN) AhaarPrimaryContainer else Color.Transparent,
          modifier = Modifier
            .weight(1f)
            .clickable {
              mode = AuthMode.LOGIN
              localValidationErr = null
            }
            .testTag("tab_login")
        ) {
          Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Sign In",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = if (mode == AuthMode.LOGIN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (mode == AuthMode.SIGN_UP) AhaarPrimaryContainer else Color.Transparent,
          modifier = Modifier
            .weight(1f)
            .clickable {
              mode = AuthMode.SIGN_UP
              localValidationErr = null
            }
            .testTag("tab_signup")
        ) {
          Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Sign Up",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = if (mode == AuthMode.SIGN_UP) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Card with Form Inputs - Frosted Glassmorphic
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
      border = BorderStroke(1.2.dp, GlassBorderBrush),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Name field (on Sign Up)
        AnimatedVisibility(visible = mode == AuthMode.SIGN_UP) {
          OutlinedTextField(
            value = name,
            onValueChange = {
              name = it
              localValidationErr = null
            },
            label = { Text("Full Name") },
            placeholder = { Text("e.g. Priya Sharma") },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("name_input"),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
          )
        }

        // Email field
        OutlinedTextField(
          value = email,
          onValueChange = {
            email = it
            localValidationErr = null
          },
          label = { Text("Email Address") },
          placeholder = { Text("e.g. priya@ahaar.ai") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Email,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("email_input"),
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
          )
        )

        // Password field
        OutlinedTextField(
          value = password,
          onValueChange = {
            password = it
            localValidationErr = null
          },
          label = { Text("Password") },
          placeholder = { Text("Minimum 6 characters") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          },
          trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
              Icon(
                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = if (passwordVisible) "Hide password" else "Show password"
              )
            }
          },
          visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("password_input"),
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = if (mode == AuthMode.LOGIN) ImeAction.Done else ImeAction.Next
          ),
          keyboardActions = KeyboardActions(
            onDone = {
              if (mode == AuthMode.LOGIN) {
                onLogin(email, password)
              }
            }
          )
        )

        // Confirm Password field (on Sign Up)
        AnimatedVisibility(visible = mode == AuthMode.SIGN_UP) {
          OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
              confirmPassword = it
              localValidationErr = null
            },
            label = { Text("Confirm Password") },
            placeholder = { Text("Re-enter password") },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.LockReset,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("confirm_password_input"),
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Password,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = {
                if (password != confirmPassword) {
                  localValidationErr = "Passwords do not match"
                } else {
                  onSignUp(name, email, password)
                }
              }
            )
          )
        }

        // Error message banner
        val displayedError = localValidationErr ?: errorMessage
        if (displayedError != null) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = displayedError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontSize = 12.sp
              )
            }
          }
        }

        // Submit Button
        Button(
          onClick = {
            if (mode == AuthMode.LOGIN) {
              if (email.isBlank() || password.isBlank()) {
                localValidationErr = "Please enter both email and password"
              } else {
                onLogin(email, password)
              }
            } else {
              if (name.isBlank()) {
                localValidationErr = "Please enter your name"
              } else if (email.isBlank()) {
                localValidationErr = "Please enter your email"
              } else if (password.length < 6) {
                localValidationErr = "Password must be at least 6 characters"
              } else if (password != confirmPassword) {
                localValidationErr = "Passwords do not match"
              } else {
                onSignUp(name, email, password)
              }
            }
          },
          enabled = !isLoading,
          colors = ButtonDefaults.buttonColors(
            containerColor = AhaarPrimaryContainer,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("submit_auth_button")
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(22.dp),
              color = Color.White,
              strokeWidth = 2.dp
            )
          } else {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = if (mode == AuthMode.LOGIN) "Sign In to CalSnapix" else "Create Free Account (Sign Up)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        // OR Divider
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
          )
          Text(
            text = "OR",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp)
          )
          HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
          )
        }

        // Supabase Google OAuth Button
        OutlinedButton(
          onClick = onGoogleSignIn,
          enabled = !isLoading,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White.copy(alpha = 0.85f),
            contentColor = MaterialTheme.colorScheme.onSurface
          ),
          border = BorderStroke(1.dp, GlassBorderBrush),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("google_oauth_button")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Surface(
              shape = CircleShape,
              color = Color.White,
              border = BorderStroke(0.5.dp, Color(0xFFE0E0E0)),
              modifier = Modifier.size(24.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "G",
                  fontWeight = FontWeight.Black,
                  fontSize = 15.sp,
                  color = Color(0xFF4285F4)
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = if (mode == AuthMode.LOGIN) "Sign in with Google" else "Sign up with Google",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        // Switch mode prompt below Google sign in
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (mode == AuthMode.LOGIN) "Don't have an account?" else "Already have an account?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (mode == AuthMode.LOGIN) "Sign Up" else "Sign In",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = AhaarPrimary,
            modifier = Modifier
              .clickable {
                mode = if (mode == AuthMode.LOGIN) AuthMode.SIGN_UP else AuthMode.LOGIN
                localValidationErr = null
              }
              .padding(4.dp)
              .testTag("switch_auth_mode_link")
          )
        }
      }
    }

    // Terms & Conditions and Privacy Policy Links
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = "By continuing, you agree to our",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp
      )
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Terms & Conditions",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = AhaarPrimary,
          fontSize = 11.sp,
          modifier = Modifier
            .clickable { showTermsDialog = true }
            .padding(4.dp)
            .testTag("terms_link")
        )
        Text(
          text = "•",
          color = MaterialTheme.colorScheme.outlineVariant,
          fontSize = 11.sp
        )
        Text(
          text = "Privacy Policy",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = AhaarPrimary,
          fontSize = 11.sp,
          modifier = Modifier
            .clickable { showPrivacyDialog = true }
            .padding(4.dp)
            .testTag("privacy_link")
        )
      }
    }

    // Comprehensive Terms & Conditions Dialog
    if (showTermsDialog) {
      AlertDialog(
        onDismissRequest = { showTermsDialog = false },
        title = {
          Text(text = "CalSnapix Terms & Conditions", fontWeight = FontWeight.Bold)
        },
        text = {
          Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "Last Updated: October 2026\nApp: CalSnapix • Indian Nutrition & Wellness",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = AhaarPrimary
            )
            Text(
              text = "1. Acceptance of Terms:\nBy creating an account, accessing, or subscribing to CalSnapix, you accept and agree to be bound by these Terms & Conditions. If you do not agree, do not use the application.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurface,
              lineHeight = 17.sp
            )
            Text(
              text = "2. Wellness & Non-Medical Disclaimer:\nCalSnapix provides AI-powered caloric, macronutrient, micronutrient, and Ayurvedic dietary estimates for personal wellness information and habit tracking only. It is NOT medical advice, diagnosis, or clinical treatment. Consult a licensed physician or registered dietitian before starting any calorie deficit or workout regimen.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 17.sp
            )
            Text(
              text = "3. AI Meal Estimation & Accuracy:\nAI food recognition via CameraX and barcode scanning provides high-confidence estimates based on standard Indian and international food composition databases. Actual caloric density may vary by oil, ghee, preparation method, and recipe variation.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 17.sp
            )
            Text(
              text = "4. User Accounts & Security:\nYou are responsible for maintaining the confidentiality of your login credentials and for all activities under your account. You agree to provide accurate biometric inputs for optimal calculation.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 17.sp
            )
            Text(
              text = "5. Subscription & Billing Plans:\nCalSnapix Pro plans (Daily Pass, Weekly, Monthly, 6-Months, and Yearly) grant full access to unlimited AI meal scans, complete micronutrient metrics, and 24/7 MyCal AI dietitian chat. Subscriptions can be managed and cancelled anytime.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 17.sp
            )
            Text(
              text = "6. Intellectual Property & Modifications:\nAll algorithms, branding, and UI designs of CalSnapix are proprietary. We reserve the right to improve features and revise terms with appropriate notice.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 17.sp
            )
          }
        },
        confirmButton = {
          Button(
            onClick = { showTermsDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = AhaarPrimary, contentColor = Color.White)
          ) {
            Text("I Agree & Understand")
          }
        }
      )
    }

    // Comprehensive Privacy Policy Dialog
    if (showPrivacyDialog) {
      AlertDialog(
        onDismissRequest = { showPrivacyDialog = false },
        title = {
          Text(text = "CalSnapix Privacy Policy", fontWeight = FontWeight.Bold)
        },
        text = {
          Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "Effective Date: October 2026\nApp: CalSnapix • Indian Nutrition & Wellness",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = AhaarPrimary
            )
            Text(
              text = "1. Zero-Knowledge Nutrition Privacy:\nYour health, body weight, and meal habits are personal. CalSnapix never sells, rents, monetizes, or brokers your personal health data to insurance companies, data brokers, or third-party advertisers.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurface,
              lineHeight = 17.sp
            )
            Text(
              text = "2. Data We Collect:\n• Biometric Profile: Age, biological gender, height, current weight, and goal weight strictly to calculate your Basal Metabolic Rate (BMR) and Total Daily Energy Expenditure (TDEE).\n• Food & Meal Logs: Consumed foods, portion sizes, and macro records to generate your nutrition charts.\n• Camera & Photo Access: Used in real-time exclusively to analyze meals via the CameraX live viewfinder. Photos are analyzed transiently and not retained on remote advertising servers.\n• Barcode Scans: Only the numerical EAN/UPC product code is queried against nutritional databases.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 17.sp
            )
            Text(
              text = "3. Security & Cryptographic Storage:\nAll personal credentials and nutrition blueprints are stored using secure local and cloud databases protected with 256-bit encryption.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 17.sp
            )
            Text(
              text = "4. Your Absolute Right to Erasure:\nYou maintain full ownership of your data. You may permanently delete your account, meal records, and biometric history at any time from your profile with instantaneous data wipe.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 17.sp
            )
          }
        },
        confirmButton = {
          Button(
            onClick = { showPrivacyDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = AhaarPrimary, contentColor = Color.White)
          ) {
            Text("Close")
          }
        }
      )
    }

    // Security & Data Protection Note
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Security,
        contentDescription = null,
        tint = AhaarTertiary,
        modifier = Modifier.size(16.dp)
      )
      Text(
        text = "256-Bit Cryptographic Storage • Privacy Protected",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 10.sp
      )
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}
