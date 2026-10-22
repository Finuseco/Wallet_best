package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.CashPaySquareStatusDialog
import com.example.ui.components.QrScannerDialog
import com.example.ui.components.SquareStatusType
import com.example.ui.components.StatusDetailItem
import com.example.ui.theme.MulishFontFamily
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

    // Client-side status popup state
    var localStatusType by remember { mutableStateOf<SquareStatusType?>(null) }
    var localStatusTitle by remember { mutableStateOf("") }
    var localStatusMessage by remember { mutableStateOf("") }

    // Handle external error or success with square pop-up dialog
    val isExternalError = !errorMessage.isNullOrBlank()
    val isInsufficientBalance = isExternalError && (
            errorMessage!!.contains("solde", ignoreCase = true) ||
            errorMessage.contains("balance", ignoreCase = true) ||
            errorMessage.contains("fonds", ignoreCase = true) ||
            errorMessage.contains("insuffisant", ignoreCase = true)
    )

    // Render square status dialog for SUCCESS
    if (success) {
        val lastBtcAmount = btcAmountInput.toDoubleOrNull() ?: 0.0
        val details = mutableListOf<StatusDetailItem>()
        if (lastBtcAmount > 0.0) {
            details.add(StatusDetailItem("Montant", "${String.format(Locale.US, "%.6f", lastBtcAmount)} BTC", isHighlight = true))
        }
        if (addressInput.isNotBlank()) {
            val shortAddr = if (addressInput.length > 18) {
                "${addressInput.take(8)}...${addressInput.takeLast(6)}"
            } else addressInput
            details.add(StatusDetailItem("Destinataire", shortAddr))
        }
        if (!txRef.isNullOrBlank()) {
            details.add(StatusDetailItem("TXID / Réf", txRef.take(16)))
        }

        CashPaySquareStatusDialog(
            isOpen = true,
            type = SquareStatusType.SUCCESS,
            title = "Transfert BTC Réussi",
            message = "Votre transfert de Bitcoin a été validé et diffusé avec succès sur le réseau.",
            details = details,
            primaryButtonText = "Terminer",
            onPrimaryAction = onDismiss,
            onDismiss = onDismiss
        )
        return
    }

    // Render square status dialog for INSUFFICIENT BALANCE
    if (isInsufficientBalance) {
        CashPaySquareStatusDialog(
            isOpen = true,
            type = SquareStatusType.INSUFFICIENT_BALANCE,
            title = "Solde Insuffisant",
            message = errorMessage ?: "Votre solde en BTC est insuffisant pour effectuer ce transfert.",
            details = listOf(
                StatusDetailItem("Solde disponible", "${String.format(Locale.US, "%.6f", btcBalance)} BTC"),
                StatusDetailItem("Devise", "Bitcoin (BTC)")
            ),
            primaryButtonText = "Compris",
            onPrimaryAction = {
                // Clear local inputs and allow user to retry
                localStatusType = null
            },
            onDismiss = {
                localStatusType = null
            }
        )
    } else if (isExternalError) {
        // Render square status dialog for GENERIC / PIN ERROR
        val isPinErr = errorMessage!!.contains("pin", ignoreCase = true)
        CashPaySquareStatusDialog(
            isOpen = true,
            type = if (isPinErr) SquareStatusType.WARNING else SquareStatusType.ERROR,
            title = if (isPinErr) "Code PIN Invalide" else "Échec du Transfert",
            message = errorMessage,
            primaryButtonText = "Fermer",
            onPrimaryAction = {
                localStatusType = null
            },
            onDismiss = {
                localStatusType = null
            }
        )
    }

    // Client-side local validation popup (in case of insufficient balance before call)
    if (localStatusType != null) {
        CashPaySquareStatusDialog(
            isOpen = true,
            type = localStatusType!!,
            title = localStatusTitle,
            message = localStatusMessage,
            primaryButtonText = "OK",
            onPrimaryAction = { localStatusType = null },
            onDismiss = { localStatusType = null }
        )
    }

    // Main Square / Centered Form Dialog
    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 310.dp, max = 350.dp)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF101827),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            shadowElevation = 24.dp
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
                                text = "Envoyer BTC",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = MulishFontFamily
                            )
                            Text(
                                text = "Réseau Bitcoin",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rate & Balance Header Info (Compact & Straight)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Taux direct",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                fontFamily = MulishFontFamily
                            )
                            Text(
                                text = "$ ${String.format(Locale.US, "%,.0f", effectiveRate)}",
                                fontSize = 12.5.sp,
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
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 1. Destination BTC Address
                Text(
                    text = "Adresse de destination",
                    fontSize = 11.5.sp,
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
                    placeholder = { Text("bc1q... ou 1...", color = Color(0xFF64748B), fontSize = 12.sp) },
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
                                    contentDescription = "Coller",
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(onClick = { showQrScanner = true }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scanner",
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Dual Amount Mode Toggle (Compact, concise labels: USD vs BTC)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
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
                            fontFamily = MulishFontFamily
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
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
                            fontFamily = MulishFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (activeInputMode == "USD") {
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
                        label = { Text("Montant USD ($)", color = Color(0xFF94A3B8), fontSize = 11.5.sp) },
                        placeholder = { Text("Ex: 25", color = Color(0xFF64748B), fontSize = 12.sp) },
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
                                Text("MAX", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (btcAmountInput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "≈ $btcAmountInput BTC",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
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
                        label = { Text("Montant BTC (₿)", color = Color(0xFF94A3B8), fontSize = 11.5.sp) },
                        placeholder = { Text("Ex: 0.0005", color = Color(0xFF64748B), fontSize = 12.sp) },
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
                                Text("MAX", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (usdAmountInput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "≈ $ $usdAmountInput USD",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. PIN Code
                Text(
                    text = "Code PIN (4 chiffres)",
                    fontSize = 11.5.sp,
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
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                val finalAmountBtc = btcAmountInput.toDoubleOrNull() ?: 0.0

                Button(
                    onClick = {
                        // Validate before call and show dedicated square dialog if invalid
                        if (addressInput.isBlank()) {
                            localStatusType = SquareStatusType.WARNING
                            localStatusTitle = "Adresse Requise"
                            localStatusMessage = "Veuillez saisir ou scanner une adresse Bitcoin valide."
                            return@Button
                        }
                        if (finalAmountBtc <= 0.0) {
                            localStatusType = SquareStatusType.WARNING
                            localStatusTitle = "Montant Invalide"
                            localStatusMessage = "Veuillez entrer un montant en BTC supérieur à zéro."
                            return@Button
                        }
                        if (finalAmountBtc > btcBalance) {
                            localStatusType = SquareStatusType.INSUFFICIENT_BALANCE
                            localStatusTitle = "Solde Insuffisant"
                            localStatusMessage = "Votre solde actuel (${String.format(Locale.US, "%.6f", btcBalance)} BTC) est inférieur au montant demandé (${String.format(Locale.US, "%.6f", finalAmountBtc)} BTC)."
                            return@Button
                        }
                        if (pinInput.length != 4) {
                            localStatusType = SquareStatusType.WARNING
                            localStatusTitle = "Code PIN Incomplet"
                            localStatusMessage = "Veuillez saisir votre code PIN CashPay à 4 chiffres."
                            return@Button
                        }

                        onSendBtc(addressInput, finalAmountBtc, pinInput)
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B),
                        disabledContainerColor = Color(0xFF475569)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Confirmer l'envoi BTC",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
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
                Toast.makeText(context, "Adresse scannée !", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
