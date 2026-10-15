package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.ui.theme.*

@Composable
fun ActivateAgentDialog(
    isOpen: Boolean,
    plan: String,
    pin: String,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onPlanChange: (String) -> Unit,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit,
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
                    color = ToofanGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = ToofanGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Text(
                    text = "Devenir Agent CashPay",
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
                // Success Popup View
                if (successMessage != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ToofanGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(34.dp))
                            }
                        }
                        Text(
                            text = "Agent Activé !",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ToofanGreen
                        )
                        Text(
                            text = successMessage,
                            fontFamily = MulishFontFamily,
                            fontSize = 13.sp,
                            color = ToofanBodyText,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
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

                    Text(
                        text = "Choisissez votre formule et saisissez votre PIN CashPay pour valider l'activation :",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = ToofanBodyText
                    )

                    // Plan Selection Cards
                    val plans = listOf(
                        Triple("promo", "Offre Promotionnelle", "★ Formule Réduite (Avantages Agent Inclus)"),
                        Triple("normal", "Offre Normale", "Formule Standard Complète")
                    )

                    plans.forEach { (pKey, pTitle, pDesc) ->
                        val isSel = plan == pKey
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onPlanChange(pKey) },
                            color = if (isSel) ToofanGreen.copy(alpha = 0.08f) else Color(0xFFF8FAFC),
                            border = BorderStroke(if (isSel) 2.dp else 1.dp, if (isSel) ToofanGreen else Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSel,
                                    onClick = { onPlanChange(pKey) },
                                    colors = RadioButtonDefaults.colors(selectedColor = ToofanGreen)
                                )
                                Column {
                                    Text(
                                        text = pTitle,
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSel) ToofanGreen else ToofanMainDark
                                    )
                                    Text(
                                        text = pDesc,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    // PIN Input
                    Text(
                        text = "Saisissez votre code PIN CashPay :",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = ToofanMainDark
                    )

                    OutlinedTextField(
                        value = pin,
                        onValueChange = { if (it.length <= 4) onPinChange(it) },
                        placeholder = { Text("Code PIN (4 chiffres)") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ToofanButton(
                        title = "Activer le statut Agent",
                        onClick = onSubmit,
                        isLoading = isLoading,
                        enabled = pin.length == 4
                    )
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
