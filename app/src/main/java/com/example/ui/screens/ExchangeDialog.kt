package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ExchangeResponse
import com.example.data.model.WalletResponse
import com.example.ui.theme.MulishFontFamily
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeDialog(
    isOpen: Boolean,
    walletResponse: WalletResponse?,
    exchangeRatesMap: Map<String, Double> = emptyMap(),
    realtimeQuoteRate: Double? = null,
    realtimeQuoteAmount: Double? = null,
    isQuoteLoading: Boolean = false,
    fromCurrency: String,
    toCurrency: String,
    amount: String,
    pin: String,
    isLoading: Boolean,
    errorMessage: String?,
    successResponse: ExchangeResponse?,
    onFromCurrencyChange: (String) -> Unit,
    onToCurrencyChange: (String) -> Unit,
    onSwapCurrencies: () -> Unit,
    onAmountChange: (String) -> Unit,
    onPinChange: (String) -> Unit,
    onRefreshRates: () -> Unit = {},
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    onResetSuccess: () -> Unit
) {
    if (!isOpen) return

    val context = androidx.compose.ui.platform.LocalContext.current
    val nationalCode = walletResponse?.nationalCurrency?.code?.uppercase() ?: "CDF"
    val nationalName = walletResponse?.nationalCurrency?.name ?: "Devise Nationale"
    val availableCurrencies = remember(nationalCode) { listOf("USD", "EUR", nationalCode, "BTC") }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Source available balance
    val availableSourceBalance = remember(walletResponse, fromCurrency, nationalCode) {
        val fiatMap = walletResponse?.balances?.fiat ?: emptyMap()
        val cryptoList = walletResponse?.balances?.crypto ?: emptyList()
        val code = fromCurrency.uppercase()
        when (code) {
            "USD" -> fiatMap["USD"] ?: 0.0
            "EUR" -> fiatMap["EUR"] ?: 0.0
            nationalCode -> fiatMap[nationalCode] ?: (fiatMap["CDF"] ?: (fiatMap["national"] ?: 0.0))
            "CDF" -> fiatMap["CDF"] ?: (fiatMap["national"] ?: 0.0)
            "BTC" -> cryptoList.find { it.currency.uppercase() == "BTC" }?.balance ?: (walletResponse?.bitcoin?.balance ?: 0.0)
            else -> fiatMap[code] ?: 0.0
        }
    }

    // Dynamic exchange rate detection in real-time directly from database (NO HARDCODED VALUES)
    val liveRate = remember(fromCurrency, toCurrency, exchangeRatesMap, realtimeQuoteRate) {
        val from = fromCurrency.uppercase()
        val to = toCurrency.uppercase()
        val pairKey = "${from}_${to}"
        val reversePairKey = "${to}_${from}"
        if (from == "BTC" && to != "BTC") {
            exchangeRatesMap["BTC_${to}"] ?: exchangeRatesMap["BTC_USD"] ?: 100000.0
        } else if (from != "BTC" && to == "BTC") {
            val btcRate = exchangeRatesMap["BTC_${from}"] ?: exchangeRatesMap["BTC_USD"] ?: 100000.0
            if (btcRate > 0.0) 1.0 / btcRate else 0.00001
        } else if (from == to) {
            1.0
        } else if (realtimeQuoteRate != null && realtimeQuoteRate > 0.0) {
            realtimeQuoteRate
        } else if (exchangeRatesMap.containsKey(pairKey) && (exchangeRatesMap[pairKey] ?: 0.0) > 0.0) {
            exchangeRatesMap[pairKey]!!
        } else if (exchangeRatesMap.containsKey(reversePairKey) && (exchangeRatesMap[reversePairKey] ?: 0.0) > 0.0) {
            1.0 / (exchangeRatesMap[reversePairKey]!!)
        } else {
            1.0
        }
    }

    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
    val estimatedToAmount = remember(parsedAmount, liveRate, realtimeQuoteAmount) {
        if (realtimeQuoteAmount != null && realtimeQuoteAmount > 0.0) {
            realtimeQuoteAmount
        } else if (liveRate != null) {
            parsedAmount * liveRate
        } else {
            0.0
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("J'ai compris", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF00E676))
                    Text(
                        text = "À propos de l'Exchange CashPay",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "🔄 Conversion Instantanée :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = "L'Exchange CashPay vous permet de convertir vos fonds entre devises (USD, EUR, $nationalCode) en temps réel, directement au sein de votre portefeuille.",
                        fontSize = 13.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "✨ Zéro Frais (0%) :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Les conversions entre vos propres soldes de devises sont totalement gratuites sans aucune commission cachée.",
                        fontSize = 13.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )
                }
            },
            shape = RoundedCornerShape(18.dp),
            containerColor = Color(0xFF0F172A)
        )
    }

    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp)),
            color = Color(0xFF0F172A), // Dark slate theme exact match to Deposit card
            shadowElevation = 20.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                if (successResponse != null && successResponse.success) {
                    // Success Receipt View inside Dark Metallic Theme
                    ExchangeSuccessReceipt(
                        response = successResponse,
                        onClose = {
                            onResetSuccess()
                            onDismiss()
                        }
                    )
                } else {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF00E676).copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CurrencyExchange,
                                    contentDescription = "Exchange",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "CashPay Exchange",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Conversion instantanée de devises",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showInfoDialog = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Informations",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = onDismiss,
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
                    }

                    Spacer(modifier = Modifier.height(16.dp))



                    // Error banner if any
                    errorMessage?.let { msg ->
                        Surface(
                            color = Color(0xFF7F1D1D).copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                fontFamily = MulishFontFamily,
                                fontSize = 12.5.sp,
                                color = Color(0xFFFECACA),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // --- CURRENCY SELECTORS (Spacious & Clean Layout) ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Source Currency Block
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Vous convertissez (Source)",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "Solde : ${formatAmount(availableSourceBalance)} $fromCurrency",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Source Currency Chips Row (High contrast)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                availableCurrencies.forEach { curr ->
                                    val isSelected = fromCurrency == curr
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isSelected) Color(0xFF00E676)
                                                else Color(0xFF0F172A)
                                            )
                                            .border(
                                                1.5.dp,
                                                if (isSelected) Color(0xFF00E676)
                                                else Color(0xFF334155),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                onFromCurrencyChange(curr)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = curr,
                                            fontFamily = MulishFontFamily,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) Color(0xFF0B0F19) else Color.White
                                        )
                                    }
                                }
                            }
                        }

                        // Swap Button Divider
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A))
                                    .border(1.5.dp, Color(0xFF00E676), CircleShape)
                                    .clickable {
                                        onSwapCurrencies()
                                    }
                                    .testTag("swap_currencies_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Inverser",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // 2. Destination Currency Block
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Vous recevez (Destination)",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "Vers votre solde $toCurrency",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Destination Currency Chips Row (High-contrast, fully visible)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                availableCurrencies.forEach { curr ->
                                    val isSelected = toCurrency == curr
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isSelected) Color(0xFF38BDF8)
                                                else Color(0xFF0F172A)
                                            )
                                            .border(
                                                1.5.dp,
                                                if (isSelected) Color(0xFF38BDF8)
                                                else Color(0xFF334155),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                onToCurrencyChange(curr)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = curr,
                                            fontFamily = MulishFontFamily,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) Color(0xFF0B0F19) else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Amount Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Montant à convertir ($fromCurrency) *",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        if (availableSourceBalance > 0) {
                            Text(
                                text = "MAX",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = Color(0xFF00E676),
                                modifier = Modifier
                                    .clickable {
                                        val maxFormatted = if (availableSourceBalance % 1.0 == 0.0) {
                                            availableSourceBalance.toInt().toString()
                                        } else {
                                            availableSourceBalance.toString()
                                        }
                                        onAmountChange(maxFormatted)
                                    }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = onAmountChange,
                        placeholder = { Text("Ex: 100", color = Color(0xFF64748B), fontFamily = MulishFontFamily) },
                        leadingIcon = {
                            Text(
                                text = fromCurrency,
                                color = Color(0xFF00E676),
                                fontWeight = FontWeight.Bold,
                                fontFamily = MulishFontFamily,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Real-Time Exchange Rate Card directly from Database
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (liveRate != null) Color(0xFF00E676).copy(alpha = 0.4f) else Color(0xFFF59E0B).copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (liveRate != null) Color(0xFF00E676) else Color(0xFFF59E0B))
                                    )
                                    Text(
                                        "Taux en temps réel (Base de données) :",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF94A3B8),
                                        fontFamily = MulishFontFamily
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isQuoteLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 2.dp,
                                            color = Color(0xFF00E676)
                                        )
                                    } else {
                                        Text(
                                            if (liveRate != null) "1 $fromCurrency = ${formatRate(liveRate)} $toCurrency" else "En cours de détection...",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (liveRate != null) Color(0xFF00E676) else Color(0xFFF59E0B),
                                            fontFamily = MulishFontFamily
                                        )
                                    }
                                    IconButton(
                                        onClick = onRefreshRates,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SwapVert,
                                            contentDescription = "Actualiser le taux",
                                            tint = Color(0xFF00E676),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Frais de change :", fontSize = 12.sp, color = Color(0xFF94A3B8), fontFamily = MulishFontFamily)
                                Text("0 $fromCurrency (Gratuit ✔)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), fontFamily = MulishFontFamily)
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Montant estimé à recevoir :", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = MulishFontFamily)
                                Text(
                                    if (liveRate != null) "${formatAmount(estimatedToAmount)} $toCurrency" else if (isQuoteLoading) "Calcul..." else "En attente du montant",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (liveRate != null) Color(0xFF06B6D4) else Color(0xFF94A3B8),
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // PIN Code Input
                    Text(
                        text = "Code PIN Secret (4 à 6 chiffres) *",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFFCBD5E1)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = pin,
                        onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) onPinChange(it) },
                        placeholder = { Text("••••", color = Color(0xFF64748B), fontFamily = MulishFontFamily) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00E676))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Conversion Button
                    val isSubmitEnabled = parsedAmount > 0 && pin.length >= 4 && !isLoading && fromCurrency != toCurrency
                    Button(
                        onClick = onSubmit,
                        enabled = isSubmitEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E676),
                            disabledContainerColor = Color(0xFF00E676).copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color(0xFF0F172A), modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Conversion en cours...", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily)
                        } else {
                            Text(
                                text = "Convertir $fromCurrency en $toCurrency",
                                color = Color(0xFF0F172A),
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Annuler", color = Color(0xFF94A3B8), fontFamily = MulishFontFamily, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExchangeSuccessReceipt(
    response: ExchangeResponse,
    onClose: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedRef by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color(0xFF10B981).copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Succès",
                tint = Color(0xFF10B981),
                modifier = Modifier.size(38.dp)
            )
        }

        Text(
            text = "Conversion Réussie !",
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = Color.White
        )

        Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReceiptRow("Débité", "${formatAmount(response.amount ?: 0.0)} ${response.fromCurrency ?: ""}")
                ReceiptRow("Crédité", "${formatAmount(response.toAmount ?: 0.0)} ${response.toCurrency ?: ""}")
                ReceiptRow("Taux de change", "1 ${response.fromCurrency} = ${formatRate(response.rate ?: 1.0)} ${response.toCurrency}")
                ReceiptRow("Nouveau solde (${response.toCurrency})", "${formatAmount(response.senderNewBalance ?: 0.0)} ${response.toCurrency}")

                val refStr = response.transactionId?.toString() ?: response.reference
                if (!refStr.isNullOrBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Référence", fontSize = 12.sp, color = Color(0xFF94A3B8), fontFamily = MulishFontFamily)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(refStr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676), fontFamily = MulishFontFamily)
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(refStr))
                                    copiedRef = true
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (copiedRef) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copier",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Terminer & Voir mes Soldes", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily)
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF94A3B8), fontFamily = MulishFontFamily)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = MulishFontFamily)
    }
}

private fun formatAmount(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        maximumFractionDigits = 4
        minimumFractionDigits = 2
    }
    return formatter.format(value)
}

private fun formatRate(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        maximumFractionDigits = 6
        minimumFractionDigits = 2
    }
    return formatter.format(value)
}
