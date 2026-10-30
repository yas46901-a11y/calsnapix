package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CalTheme

/**
 * Trust and Privacy Screen (Point 7):
 * (a) Plain language explanation of what photos and health data are stored, and for how long.
 * (b) "Delete my data" button that removes user's photos, logs, corrections, and goals from Supabase.
 * (c) Explicit 30-day auto-deletion policy for food photos unless saved.
 * (d) Supabase Row Level Security (RLS) enforcement disclosure.
 * (f) Model training opt-in consent toggle (explicit opt-in, default false).
 */
@Composable
fun PrivacyAndDataScreenDialog(
  optInModelTraining: Boolean,
  onToggleModelTraining: (Boolean) -> Unit,
  onDeleteMyData: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  var showDeleteConfirmDialog by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = colors.surface),
      border = BorderStroke(1.dp, colors.line),
      modifier = modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 20.dp)
        .testTag("privacy_and_data_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(shape = CircleShape, color = colors.accentSoft, modifier = Modifier.size(36.dp)) {
              Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
              }
            }
            Column {
              Text(
                text = "Privacy & Data Trust",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
              Text(
                text = "Clear rules • Zero unwanted retention",
                fontSize = 11.sp,
                color = colors.ink2
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.ink2)
          }
        }

        // Scrollable content
        Column(
          modifier = Modifier
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Point 7c: 30-Day Auto-Deletion Banner
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.accentSoft,
            border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "🗓️ 30-Day Photo Lifecycle Rule",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accent
              )
              Text(
                text = "Food photos uploaded for recognition are automatically purged from cloud storage after 30 days unless you explicitly save the meal into your permanent history.",
                fontSize = 12.sp,
                color = colors.ink,
                lineHeight = 17.sp
              )
            }
          }

          // Plain Language Summary Sections
          PrivacyFactItem(
            icon = "🔒",
            title = "What We Store",
            description = "Only your meal logs (dish name, calories, macros), custom portion corrections, and chosen wellness goal (e.g. Lose weight, Build muscle)."
          )

          PrivacyFactItem(
            icon = "🛡️",
            title = "Supabase Row Level Security (RLS)",
            description = "Active zero-trust RLS policies are enabled on all database tables. Only your authenticated session token can read or edit your records."
          )

          PrivacyFactItem(
            icon = "🚫",
            title = "No Model Training Without Consent",
            description = "Your personal food photos are NEVER used to train machine learning models unless you explicitly opt in below."
          )

          // Point 7f: AI Training Opt-in Toggle
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surface2,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Contribute to Indian Food AI",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = colors.ink
                )
                Text(
                  text = "Allow anonymized food photos to improve regional dish recognition. (Default: Off)",
                  fontSize = 11.sp,
                  color = colors.ink2
                )
              }
              Switch(
                checked = optInModelTraining,
                onCheckedChange = onToggleModelTraining,
                colors = SwitchDefaults.colors(checkedThumbColor = colors.accent),
                modifier = Modifier.testTag("ai_model_training_consent_switch")
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Point 7b: "Delete my data" Button
          Button(
            onClick = { showDeleteConfirmDialog = true },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFFEF2F2),
              contentColor = Color(0xFFDC2626)
            ),
            border = BorderStroke(1.dp, Color(0xFFFECACA)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("delete_my_data_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
              Text(
                text = "Delete My Data (All Photos, Logs & Goals)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC2626)
              )
            }
          }
        }
      }
    }
  }

  // Delete Confirmation Alert
  if (showDeleteConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirmDialog = false },
      title = { Text("Permanently Delete Your Data?", fontWeight = FontWeight.Bold) },
      text = {
        Text("This will permanently remove all your scanned food photos, daily meal logs, custom portion corrections, and goals from Supabase and your device. This cannot be undone.")
      },
      confirmButton = {
        Button(
          onClick = {
            showDeleteConfirmDialog = false
            onDeleteMyData()
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
        ) {
          Text("Delete Everything", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun PrivacyFactItem(icon: String, title: String, description: String) {
  val colors = CalTheme.colors
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = colors.surface2,
    border = BorderStroke(1.dp, colors.line),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text(text = icon, fontSize = 20.sp)
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.ink)
        Text(text = description, fontSize = 11.sp, color = colors.ink2, lineHeight = 16.sp)
      }
    }
  }
}
