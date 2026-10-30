package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.theme.CalTheme

/**
 * Trust and Privacy: Report a Wrong Result Dialog (Point 7e).
 *
 * Sends the dish name, photo ID, and user note to admin via Supabase,
 * without sending ANY personal details or account identifiers.
 */
@Composable
fun ReportWrongResultDialog(
  dishName: String,
  photoId: String?,
  onSubmit: (String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  var userNote by remember { mutableStateOf("") }
  var isSubmitted by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = colors.surface),
      border = BorderStroke(1.dp, colors.line),
      modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("report_wrong_result_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
            Icon(
              imageVector = Icons.Default.Flag,
              contentDescription = null,
              tint = Color(0xFFEF4444)
            )
            Text(
              text = "Report a Wrong Result",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.ink2)
          }
        }

        if (!isSubmitted) {
          Text(
            text = "Help us improve our Indian food recognition engine. What was inaccurate?",
            fontSize = 13.sp,
            color = colors.ink2
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.surface2,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(text = "Detected Dish: $dishName", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.ink)
              if (photoId != null) {
                Text(text = "Photo Token: #$photoId", fontSize = 10.sp, color = colors.ink2)
              }
            }
          }

          OutlinedTextField(
            value = userNote,
            onValueChange = { userNote = it },
            placeholder = { Text("e.g. This was Dal Tadka, not Dal Makhani; or the portion was 2 rotis not 1.") },
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp)
              .testTag("report_user_note_input"),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = colors.accent,
              unfocusedBorderColor = colors.line
            )
          )

          // Privacy Assurance Note
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.surface2
          ) {
            Text(
              text = "🔒 Privacy: No personal details, name, or account ID are attached to this report.",
              fontSize = 11.sp,
              color = colors.ink2,
              modifier = Modifier.padding(8.dp)
            )
          }

          Button(
            onClick = {
              onSubmit(userNote.ifBlank { "User reported incorrect portion or dish" })
              isSubmitted = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("submit_report_button"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
          ) {
            Text(text = "Send Anonymous Feedback", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
          }
        } else {
          // Success view
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(text = "✓ Report Received", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.accent)
            Text(
              text = "Thank you! Our nutritional team will audit this dish to improve model accuracy.",
              fontSize = 12.sp,
              color = colors.ink2,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
              onClick = onDismiss,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
            ) {
              Text("Done", color = Color.White)
            }
          }
        }
      }
    }
  }
}
