package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.QrScannerDialog
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.SuccessGreen
import java.util.Locale

@Composable
fun SendBtcDialog(
    isOpen: Boolean,
    btcBalance: Double,
    liveBtcRateUsd: Double,
    isLoading: Boolean,
    errorMessage: String?,
    success: Boolean,
    txRef: String?,
    onDismiss: () -> Unit,
    onSendBtc: (address: String, amountBtc: Double, pin: String) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val effectiveRate = if (liveBtcRateUsd > 1000.0) liveBtcRateUsd else 82000.0

    var addressInput by remember { mutableStateOf("") }
    var usdAmountInput by remember { mutableStateOf("") }
    var btcAmountInput by remember { mutableStateOf("") }
    var activeInputMode by remember { mutableStateOf("USD") } // "USD" or "BTC"
    var pinInput by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }
    var showQrScanner by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = {
        if (!isLoading) onDismiss()
    }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF101827),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyBitcoin,
                                contentDescription = "Bitcoin",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Envoyer des BTC",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = MulishFontFamily
                            )
                            Text(
                                text = "Réseau Bitcoin Mini Blockchain",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (success) {
                    // Success View
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF064E3B).copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Transfert BTC Réussi !",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                fontFamily = MulishFontFamily
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Votre envoi a été validé et diffusé avec succès sur le réseau.",
                                color = Color(0xFFD1FAE5),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = MulishFontFamily
                            )
                            if (!txRef.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Réf: $txRef",
                                    color = Color(0xFF6EE7B7),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Terminer", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    // Live Market Rate & Available Balance Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Taux en direct",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8),
                                    fontFamily = MulishFontFamily
                                )
                                Text(
                                    text = "1 BTC ≈ $ ${String.format(Locale.US, "%,.2f", effectiveRate)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B),
                                    fontFamily = MulishFontFamily
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Solde disponible",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8),
                                    fontFamily = MulishFontFamily
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%.6f", btcBalance)} BTC",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Destination BTC Address Field
                    Text(
                        text = "Adresse Bitcoin de destination",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = {
                            addressInput = it.trim().removePrefix("bitcoin:").substringBefore("?")
                        },
                        placeholder = { Text("bc1q... ou 1...", color = Color(0xFF64748B), fontSize = 13.sp) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFF59E0B),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B)
                        ),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Paste from clipboard button
                                IconButton(onClick = {
                                    val clipText = clipboardManager.getText()?.text?.trim()
                                    if (!clipText.isNullOrBlank()) {
                                        addressInput = clipText.removePrefix("bitcoin:").substringBefore("?")
                                        Toast.makeText(context, "Adresse collée !", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Presse-papier vide", Toast.LENGTH_SHORT).show()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ContentPaste,
                                        contentDescription = "Coller l'adresse",
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                                // Camera QR Code scanner button
                                IconButton(onClick = { showQrScanner = true }) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "Scanner QR",
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Dual Amount Mode Toggle ($ USD vs ₿ BTC)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (activeInputMode == "USD") Color(0xFFF59E0B) else Color.Transparent)
                                .clickable { activeInputMode = "USD" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "USD ($)",
                                color = if (activeInputMode == "USD") Color.Black else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false,
                                fontFamily = MulishFontFamily
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (activeInputMode == "BTC") Color(0xFFF59E0B) else Color.Transparent)
                                .clickable { activeInputMode = "BTC" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BTC (₿)",
                                color = if (activeInputMode == "BTC") Color.Black else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (activeInputMode == "USD") {
                        // Saisie en Dollars avec conversion automatique en BTC
                        OutlinedTextField(
                            value = usdAmountInput,
                            onValueChange = { text ->
                                val clean = text.filter { it.isDigit() || it == '.' }
                                usdAmountInput = clean
                                val usdVal = clean.toDoubleOrNull() ?: 0.0
                                btcAmountInput = if (usdVal > 0.0 && effectiveRate > 0.0) {
                                    String.format(Locale.US, "%.8f", usdVal / effectiveRate)
                                } else ""
                            },
                            label = { Text("Montant en Dollars ($)", color = Color(0xFF94A3B8)) },
                            placeholder = { Text("Ex: 20", color = Color(0xFF64748B)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFF59E0B),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF1E293B)
                            ),
                            trailingIcon = {
                                TextButton(onClick = {
                                    val fullUsd = btcBalance * effectiveRate
                                    usdAmountInput = String.format(Locale.US, "%.2f", fullUsd)
                                    btcAmountInput = String.format(Locale.US, "%.8f", btcBalance)
                                }) {
                                    Text("MAX", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (btcAmountInput.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Équivaut à : $btcAmountInput BTC (envoyé en BTC)",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        // Saisie directe en BTC avec conversion en USD
                        OutlinedTextField(
                            value = btcAmountInput,
                            onValueChange = { text ->
                                val clean = text.filter { it.isDigit() || it == '.' }
                                btcAmountInput = clean
                                val btcVal = clean.toDoubleOrNull() ?: 0.0
                                usdAmountInput = if (btcVal > 0.0 && effectiveRate > 0.0) {
                                    String.format(Locale.US, "%.2f", btcVal * effectiveRate)
                                } else ""
                            },
                            label = { Text("Montant en Bitcoins (BTC)", color = Color(0xFF94A3B8)) },
                            placeholder = { Text("Ex: 0.00025", color = Color(0xFF64748B)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFF59E0B),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF1E293B)
                            ),
                            trailingIcon = {
                                TextButton(onClick = {
                                    btcAmountInput = String.format(Locale.US, "%.8f", btcBalance)
                                    usdAmountInput = String.format(Locale.US, "%.2f", btcBalance * effectiveRate)
                                }) {
                                    Text("MAX", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (usdAmountInput.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Valeur estimée : ≈ $ $usdAmountInput USD",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Security PIN Code Field (4 digits)
                    Text(
                        text = "Code PIN CashPay (4 chiffres)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            pinInput = it.filter { ch -> ch.isDigit() }.take(4)
                        },
                        placeholder = { Text("••••", color = Color(0xFF64748B)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFF59E0B),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B)
                        ),
                        trailingIcon = {
                            IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                Icon(
                                    imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Afficher PIN",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Error Message Display
                    if (!errorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF7F1D1D).copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFFCA5A5),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp),
                                fontFamily = MulishFontFamily
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action Button
                    val finalAmountBtc = btcAmountInput.toDoubleOrNull() ?: 0.0
                    val canSubmit = addressInput.isNotBlank() &&
                            finalAmountBtc > 0.0 &&
                            pinInput.length == 4 &&
                            !isLoading

                    Button(
                        onClick = {
                            onSendBtc(addressInput, finalAmountBtc, pinInput)
                        },
                        enabled = canSubmit,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF59E0B),
                            disabledContainerColor = Color(0xFF475569)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
                                Text(
                                    text = "Confirmer l'envoi BTC",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showQrScanner) {
        QrScannerDialog(
            onDismissRequest = { showQrScanner = false },
            onQrScanned = { scanned ->
                addressInput = scanned.trim().removePrefix("bitcoin:").substringBefore("?")
                showQrScanner = false
                Toast.makeText(context, "Adresse scannée avec succès !", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
