package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalTheme

/**
 * Official, comprehensive CalSnapix Privacy Policy Dialog.
 * Adheres strictly to India Digital Personal Data Protection Act (DPDP Act 2023),
 * CERT-In Cyber Directions, and Google Play User Data policies.
 * Zero data selling, on-device encrypted storage, transparent collection purposes,
 * and user data deletion rights.
 */
@Composable
fun PrivacyPolicyDialog(
  onDismiss: () -> Unit
) {
  val colors = CalTheme.colors

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF10B981).copy(alpha = 0.15f),
          modifier = Modifier.size(36.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
          }
        }
        Column {
          Text(text = "CalSnapix Privacy Policy", fontWeight = FontWeight.Bold, color = colors.ink, fontSize = 17.sp)
          Text(text = "DPDP Act 2023 & Play Policy Compliant", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .height(440.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF10B981).copy(alpha = 0.10f),
          border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
        ) {
          Text(
            text = "🔒 Zero Data Selling Guarantee\nYour logs are encrypted. We never sell, broker, or monetize your health data, food photos, or biometric stats to advertisers or insurance firms.",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF047857),
            lineHeight = 16.sp,
            modifier = Modifier.padding(10.dp)
          )
        }

        PrivacySection(
          number = "1",
          title = "Data Protection & Encryption",
          body = "Your health, body weight, and meal habits are personal. CalSnapix never sells, rents, monetizes, or brokers your personal health metrics, food photos, or biometric records to insurance companies, advertising networks, or data aggregators. All local data is encrypted using 256-bit AES cryptographic storage."
        )

        PrivacySection(
          number = "2",
          title = "Data We Collect & Purpose",
          body = "• Biometric Metrics (Age, Gender, Height, Current Weight, Goal Weight):\nStrictly used to calculate your Basal Metabolic Rate (BMR) and Total Daily Energy Expenditure (TDEE) via the Mifflin-St Jeor formula to provide safe, personalized daily calorie and macronutrient targets.\n\n• Dietary Preferences (Vegetarian, Vegan, Jain, Eggetarian, Allergies):\nUsed exclusively to customize Indian recipe suggestions, avoid allergen triggers, and calibrate Ayurvedic digestion windows.\n\n• Real-Time Food Photos & Camera Frames:\nRequested only in real-time when you trigger the camera scanner. Images are analyzed transiently by Gemini AI to detect foods and calculate macronutrients. Photos are NEVER permanently stored on remote advertising servers, never used for facial recognition, and never sold to third parties.\n\n• Barcode & QR Scans:\nOnly numerical EAN/UPC product codes are queried to fetch nutritional information.\n\n• User Account Credentials (Name & Email Address):\nRequired solely for secure authentication, password recovery, and cross-device sync of daily meal logs and streak. Email is never used for spam or sold."
        )

        PrivacySection(
          number = "3",
          title = "Camera & Sensor Permissions",
          body = "• Camera: Used solely when you tap 'Scan Meal' or 'Barcode'.\n• Storage: Your logs are stored locally on your device in a 256-bit encrypted Room SQLite database with full offline functionality."
        )

        PrivacySection(
          number = "4",
          title = "AI & Cloud Inference",
          body = "Meal scanning and CalSnapix AI Nutrition Assistant utilize Google Gemini Vision models via encrypted TLS 1.3 endpoints. Uploaded meal snapshots are discarded immediately after inference (0-day retention) and are not used for public model training."
        )

        PrivacySection(
          number = "5",
          title = "User Rights & Data Deletion",
          body = "Under the Digital Personal Data Protection Act 2023, you hold full ownership of your data at all times. You can permanently erase your account, food diary, weight records, and biometric blueprint anytime with one tap via 'Delete Account' in Profile Settings or via our web portal at https://calsnapix.in/delete-account."
        )

        PrivacySection(
          number = "6",
          title = "Grievance Officer & Contact",
          body = "In accordance with DPDP Act 2023 requirements:\nData Protection Officer: Yashwant Raj\nEmail: grievance@calsnapix.in / dpo@calsnapix.in\nOfficial Portal: https://calsnapix.in"
        )
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Understood", fontWeight = FontWeight.Bold, color = colors.ink)
      }
    },
    containerColor = colors.surface,
    shape = RoundedCornerShape(20.dp)
  )
}

@Composable
private fun PrivacySection(number: String, title: String, body: String) {
  val colors = CalTheme.colors
  Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
    Text(
      text = "$number. $title",
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      color = colors.ink
    )
    Text(
      text = body,
      fontSize = 11.sp,
      color = colors.ink2,
      lineHeight = 16.sp
    )
  }
}
