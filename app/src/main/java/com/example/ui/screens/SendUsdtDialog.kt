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

private val UsdtTeal = Color(0xFF26A17B)
private val UsdtDarkBg = Color(0xFF0F172A)
private val UsdtSurface = Color(0xFF1E293B)

@Composable
fun SendUsdtDialog(
    isOpen: Boolean,
    usdtBalance: Double,
    onChainBalance: Double?,
    isLoading: Boolean,
    errorMessage: String?,
    success: Boolean,
    isPending: Boolean = false,
    txRef: String?,
    onDismiss: () -> Unit,
    onSendUsdt: (address: String, amountUsdt: String, pin: String) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var addressInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }
    var showQrScanner by remember { mutableStateOf(false) }

    // Client-side status popup state
    var localStatusType by remember { mutableStateOf<SquareStatusType?>(null) }
    var localStatusTitle by remember { mutableStateOf("") }
    var localStatusMessage by remember { mutableStateOf("") }

    // Handle success or pending popup
    if (success) {
        CashPaySquareStatusDialog(
            isOpen = true,
            type = SquareStatusType.SUCCESS,
            title = "Transfert USDT Réussi",
            message = "Votre transfert de $amountInput USDT a été exécuté avec succès.",
            primaryButtonText = "Terminer",
            onPrimaryAction = { onDismiss() },
            onDismiss = { onDismiss() },
            details = listOfNotNull(
                StatusDetailItem("Devise", "USDT (TRC-20)"),
                StatusDetailItem("Montant", "$amountInput USDT"),
                StatusDetailItem("Destinataire", addressInput.take(10) + "..."),
                txRef?.let { StatusDetailItem("Référence", it) }
            )
        )
        return
    }

    if (isPending) {
        CashPaySquareStatusDialog(
            isOpen = true,
            type = SquareStatusType.WARNING,
            title = "Transfert TRON en cours",
            message = "Le transfert de $amountInput USDT vers l'adresse externe est en cours de validation sur la blockchain TRON.",
            primaryButtonText = "Compris",
            onPrimaryAction = { onDismiss() },
            onDismiss = { onDismiss() },
            details = listOfNotNull(
                StatusDetailItem("Réseau", "TRON (TRC-20)"),
                StatusDetailItem("Montant", "$amountInput USDT"),
                StatusDetailItem("Destinataire", addressInput.take(10) + "..."),
                txRef?.let { StatusDetailItem("TXID / Réf", it) }
            )
        )
        return
    }

    // Local client validation dialog
    localStatusType?.let { statusType ->
        CashPaySquareStatusDialog(
            isOpen = true,
            type = statusType,
            title = localStatusTitle,
            message = localStatusMessage,
            primaryButtonText = "Corriger",
            onPrimaryAction = { localStatusType = null },
            onDismiss = { localStatusType = null }
        )
    }

    // External Error popup
    if (!errorMessage.isNullOrBlank()) {
        CashPaySquareStatusDialog(
            isOpen = true,
            type = SquareStatusType.ERROR,
            title = "Échec du transfert USDT",
            message = errorMessage,
            primaryButtonText = "Compris",
            onPrimaryAction = { onDismiss() },
            onDismiss = { onDismiss() }
        )
    }

    if (showQrScanner) {
        QrScannerDialog(
            onDismissRequest = { showQrScanner = false },
            onQrScanned = { scanned ->
                showQrScanner = false
                val clean = scanned.replace("tron:", "", ignoreCase = true)
                    .replace("ethereum:", "", ignoreCase = true)
                    .substringBefore("?")
                    .trim()
                addressInput = clean
            }
        )
    }

    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 320.dp, max = 390.dp)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(22.dp),
            color = UsdtDarkBg,
            border = BorderStroke(1.2.dp, UsdtTeal.copy(alpha = 0.5f)),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
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
                                .background(UsdtTeal.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "₮",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = UsdtTeal,
                                fontFamily = MulishFontFamily
                            )
                        }
                        Column {
                            Text(
                                text = "Envoyer des USDT",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = MulishFontFamily
                            )
                            Text(
                                text = "TRC-20 (Interne ou Externe)",
                                fontSize = 10.sp,
                                color = UsdtTeal,
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
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Balance info card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = UsdtSurface,
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Solde CashPay :",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                fontFamily = MulishFontFamily
                            )
                            Text(
                                text = "${String.format(Locale.US, "%,.4f", usdtBalance)} USDT",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = MulishFontFamily
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Solde blockchain :",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                fontFamily = MulishFontFamily
                            )
                            Text(
                                text = if (onChainBalance != null) {
                                    "${String.format(Locale.US, "%,.4f", onChainBalance)} USDT"
                                } else {
                                    "Solde blockchain indisponible"
                                },
                                fontSize = 10.sp,
                                color = if (onChainBalance != null) UsdtTeal else Color(0xFFF59E0B),
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Recipient Address
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Adresse TRON du destinataire",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFCBD5E1),
                        fontFamily = MulishFontFamily
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("T... (Adresse TRC20)", fontSize = 11.sp, color = Color(0xFF64748B))
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        clipboardManager.getText()?.text?.let { clipText ->
                                            addressInput = clipText.trim()
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentPaste,
                                        contentDescription = "Coller",
                                        tint = UsdtTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { showQrScanner = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Scanner",
                                        tint = UsdtTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UsdtTeal,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = UsdtSurface,
                            unfocusedContainerColor = UsdtSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Amount
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Montant à envoyer (USDT)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCBD5E1),
                            fontFamily = MulishFontFamily
                        )
                        Text(
                            text = "≈ $ 1.00 USD/USDT",
                            fontSize = 10.sp,
                            color = UsdtTeal,
                            fontFamily = MulishFontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = {
                            if (it.count { c -> c == '.' } <= 1 && it.length <= 16) {
                                amountInput = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0.00", fontSize = 12.sp, color = Color(0xFF64748B)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UsdtTeal,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = UsdtSurface,
                            unfocusedContainerColor = UsdtSurface
                        )
                    )

                    // Percentage chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(25 to "25%", 50 to "50%", 75 to "75%", 100 to "Max").forEach { (pct, label) ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = UsdtSurface,
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val calc = (usdtBalance * pct / 100.0)
                                        amountInput = String.format(Locale.US, "%.4f", calc).trimEnd('0').trimEnd('.')
                                    }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // PIN CashPay
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Code PIN CashPay (4 chiffres)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFCBD5E1),
                        fontFamily = MulishFontFamily
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 6) pinInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("••••", fontSize = 12.sp, color = Color(0xFF64748B)) },
                        visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        trailingIcon = {
                            IconButton(onClick = { isPinVisible = !isPinVisible }, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = if (isPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Afficher PIN",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UsdtTeal,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = UsdtSurface,
                            unfocusedContainerColor = UsdtSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                Button(
                    onClick = {
                        val trimmedAddr = addressInput.trim()
                        val amt = amountInput.toDoubleOrNull() ?: 0.0

                        if (trimmedAddr.isBlank()) {
                            localStatusType = SquareStatusType.WARNING
                            localStatusTitle = "Adresse manquante"
                            localStatusMessage = "Veuillez entrer une adresse TRON (TRC20) valide."
                            return@Button
                        }
                        if (amt <= 0.0) {
                            localStatusType = SquareStatusType.WARNING
                            localStatusTitle = "Montant invalide"
                            localStatusMessage = "Veuillez renseigner un montant supérieur à 0."
                            return@Button
                        }
                        if (amt > usdtBalance) {
                            localStatusType = SquareStatusType.ERROR
                            localStatusTitle = "Solde insuffisant"
                            localStatusMessage = "Votre solde de ${String.format(Locale.US, "%,.2f", usdtBalance)} USDT est insuffisant pour envoyer $amt USDT."
                            return@Button
                        }
                        if (pinInput.trim().length < 4) {
                            localStatusType = SquareStatusType.WARNING
                            localStatusTitle = "Code PIN requis"
                            localStatusMessage = "Veuillez renseigner votre code PIN CashPay pour autoriser ce transfert."
                            return@Button
                        }

                        onSendUsdt(trimmedAddr, amountInput.trim(), pinInput.trim())
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = UsdtTeal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Traitement...", fontFamily = MulishFontFamily, fontSize = 13.sp)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Confirmer le transfert USDT",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
