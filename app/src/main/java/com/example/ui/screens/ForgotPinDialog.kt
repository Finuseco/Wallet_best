package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ToofanButton
import com.example.ui.components.ToofanInputField
import com.example.ui.theme.*

@Composable
fun ForgotPinDialog(
    isOpen: Boolean,
    step: Int,
    phone: String,
    channel: String,
    otp: String,
    newPin: String,
    confirmPin: String,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onPhoneChange: (String) -> Unit,
    onChannelChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onNewPinChange: (String) -> Unit,
    onConfirmPinChange: (String) -> Unit,
    onRequestSubmit: () -> Unit,
    onVerifySubmit: () -> Unit,
    onResetSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFF5B37).copy(alpha = 0.12f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            tint = Color(0xFFFF5B37),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = "Réinitialiser le PIN",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ToofanMainDark
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Step Indicator Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val stepLabels = listOf("1. Canal", "2. OTP", "3. PIN")
                    stepLabels.forEachIndexed { index, label ->
                        val active = step == (index + 1)
                        val completed = step > (index + 1)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (active) ToofanGreen else if (completed) ToofanGreen.copy(alpha = 0.2f) else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (active || completed) ToofanGreen else Color(0xFFE2E8F0)),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = label,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (active) Color.White else if (completed) ToofanGreen else Color(0xFF64748B),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                // Error Banner
                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFECEF),
                        border = BorderStroke(1.dp, Color(0xFFFF4868).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFFF4868), modifier = Modifier.size(18.dp))
                            Text(
                                text = errorMessage,
                                fontFamily = MulishFontFamily,
                                fontSize = 12.sp,
                                color = Color(0xFFFF4868)
                            )
                        }
                    }
                }

                // Success Popup Step
                if (successMessage != null || step == 4) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ToofanGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(32.dp))
                            }
                        }
                        Text(
                            text = "PIN Réinitialisé !",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ToofanMainDark
                        )
                        Text(
                            text = successMessage ?: "Votre code PIN a été modifié avec succès. Vous pouvez maintenant vous connecter avec votre nouveau PIN.",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = ToofanBodyText,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    when (step) {
                        1 -> {
                            // Step 1: Select Channel & Input Phone
                            Text(
                                text = "Entrez votre numéro CashPay et choisissez le canal de réception de votre code de vérification :",
                                fontFamily = MulishFontFamily,
                                fontSize = 12.sp,
                                color = ToofanBodyText
                            )

                            ToofanInputField(
                                value = phone,
                                onValueChange = onPhoneChange,
                                placeholder = "+243812345678"
                            )

                            Text(
                                text = "Canal de réception :",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = ToofanMainDark
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val channels = listOf("sms" to "💬 SMS", "whatsapp" to "🟢 WhatsApp")
                                channels.forEach { (chKey, chLabel) ->
                                    val isSel = channel == chKey
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onChannelChange(chKey) },
                                        color = if (isSel) ToofanGreen.copy(alpha = 0.12f) else Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, if (isSel) ToofanGreen else Color(0xFFE2E8F0))
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = chLabel,
                                                fontFamily = MulishFontFamily,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = if (isSel) ToofanGreen else ToofanMainDark
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            ToofanButton(
                                title = "Recevoir le code OTP",
                                onClick = onRequestSubmit,
                                isLoading = isLoading,
                                enabled = phone.isNotBlank() && phone.length >= 8
                            )
                        }

                        2 -> {
                            // Step 2: Verify OTP
                            Text(
                                text = "Saisissez le code de vérification à 6 chiffres envoyé sur votre $channel :",
                                fontFamily = MulishFontFamily,
                                fontSize = 12.sp,
                                color = ToofanBodyText
                            )

                            ToofanInputField(
                                value = otp,
                                onValueChange = onOtpChange,
                                placeholder = "482731"
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            ToofanButton(
                                title = "Vérifier le code OTP",
                                onClick = onVerifySubmit,
                                isLoading = isLoading,
                                enabled = otp.length == 6
                            )
                        }

                        3 -> {
                            // Step 3: Reset PIN
                            Text(
                                text = "Définissez votre nouveau code PIN à 4 chiffres :",
                                fontFamily = MulishFontFamily,
                                fontSize = 12.sp,
                                color = ToofanBodyText
                            )

                            OutlinedTextField(
                                value = newPin,
                                onValueChange = { if (it.length <= 4) onNewPinChange(it) },
                                placeholder = { Text("Nouveau PIN (4 chiffres)") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = confirmPin,
                                onValueChange = { if (it.length <= 4) onConfirmPinChange(it) },
                                placeholder = { Text("Confirmer le nouveau PIN") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            ToofanButton(
                                title = "Réinitialiser le PIN",
                                onClick = onResetSubmit,
                                isLoading = isLoading,
                                enabled = newPin.length == 4 && confirmPin.length == 4 && newPin == confirmPin
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", fontFamily = MulishFontFamily, color = Color(0xFF64748B))
            }
        }
    )
}
