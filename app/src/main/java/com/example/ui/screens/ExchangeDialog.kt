package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ExchangeResponse
import com.example.data.model.WalletResponse
import com.example.ui.components.CashPaySquareStatusDialog
import com.example.ui.components.SquareStatusType
import com.example.ui.components.StatusDetailItem
import com.example.ui.theme.MulishFontFamily
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeDialog(
    isOpen: Boolean,
    walletResponse: WalletResponse?,
    usdtBalance: Double = 0.0,
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

    val nationalCode = walletResponse?.nationalCurrency?.code?.uppercase() ?: "CDF"
    val availableCurrencies = remember(nationalCode) { listOf("USD", "EUR", nationalCode, "BTC", "USDT") }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Source available balance
    val availableSourceBalance = remember(walletResponse, fromCurrency, nationalCode, usdtBalance) {
        val fiatMap = walletResponse?.balances?.fiat ?: emptyMap()
        val cryptoList = walletResponse?.balances?.crypto ?: emptyList()
        val code = fromCurrency.uppercase()
        when (code) {
            "USD" -> fiatMap["USD"] ?: 0.0
            "EUR" -> fiatMap["EUR"] ?: 0.0
            nationalCode -> fiatMap[nationalCode] ?: (fiatMap["CDF"] ?: (fiatMap["national"] ?: 0.0))
            "CDF" -> fiatMap["CDF"] ?: (fiatMap["national"] ?: 0.0)
            "BTC" -> cryptoList.find { it.currency.uppercase() == "BTC" }?.balance ?: (walletResponse?.bitcoin?.balance ?: 0.0)
            "USDT" -> if (usdtBalance > 0.0) usdtBalance else (cryptoList.find { it.currency.uppercase() == "USDT" }?.balance ?: 0.0)
            else -> fiatMap[code] ?: 0.0
        }
    }

    // Dynamic exchange rate detection in real-time
    val liveRate = remember(fromCurrency, toCurrency, exchangeRatesMap, realtimeQuoteRate) {
        val from = fromCurrency.uppercase()
        val to = toCurrency.uppercase()
        val pairKey = "${from}_${to}"
        val reversePairKey = "${to}_${from}"
        if (from == "BTC" && to != "BTC") {
            exchangeRatesMap["BTC_${to}"] ?: exchangeRatesMap["BTC_USD"] ?: 82500.0
        } else if (from != "BTC" && to == "BTC") {
            val btcRate = exchangeRatesMap["BTC_${from}"] ?: exchangeRatesMap["BTC_USD"] ?: 82500.0
            if (btcRate > 0.0) 1.0 / btcRate else 0.000012
        } else if (from == "USDT" && to != "USDT") {
            if (to == "USD") 1.0
            else exchangeRatesMap["USDT_${to}"] ?: exchangeRatesMap["USD_${to}"] ?: (if (to == "CDF") 2850.0 else 1.0)
        } else if (from != "USDT" && to == "USDT") {
            if (from == "USD") 1.0
            else {
                val rate = exchangeRatesMap["${from}_USDT"] ?: exchangeRatesMap["${from}_USD"]
                if (rate != null && rate > 0.0) rate
                else {
                    val rev = exchangeRatesMap["USD_${from}"]
                    if (rev != null && rev > 0.0) 1.0 / rev else (if (from == "CDF") 1.0 / 2850.0 else 1.0)
                }
            }
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

    // Check for success or error to display square popup dialog
    if (successResponse != null && successResponse.success) {
        val debitAmount = successResponse.amount ?: parsedAmount
        val creditAmount = successResponse.toAmount ?: estimatedToAmount
        val details = listOf(
            StatusDetailItem("Débité", "${formatAmount(debitAmount, fromCurrency)} $fromCurrency"),
            StatusDetailItem("Crédité", "${formatAmount(creditAmount, toCurrency)} $toCurrency", isHighlight = true),
            StatusDetailItem("Taux", "1 $fromCurrency = ${formatRate(successResponse.rate ?: (liveRate ?: 1.0))} $toCurrency")
        )

        CashPaySquareStatusDialog(
            isOpen = true,
            type = SquareStatusType.SUCCESS,
            title = "Conversion Réussie",
            message = "Vos devises ont été converties instantanément et vos soldes sont à jour.",
            details = details,
            primaryButtonText = "Terminer",
            onPrimaryAction = {
                onResetSuccess()
                onDismiss()
            },
            onDismiss = {
                onResetSuccess()
                onDismiss()
            }
        )
        return
    }

    // Handle error with square status pop-up
    if (!errorMessage.isNullOrBlank()) {
        val isInsufficient = errorMessage.contains("solde", ignoreCase = true) ||
                errorMessage.contains("balance", ignoreCase = true) ||
                errorMessage.contains("fonds", ignoreCase = true)

        CashPaySquareStatusDialog(
            isOpen = true,
            type = if (isInsufficient) SquareStatusType.INSUFFICIENT_BALANCE else SquareStatusType.ERROR,
            title = if (isInsufficient) "Solde Insuffisant" else "Échec de Conversion",
            message = errorMessage,
            details = if (isInsufficient) listOf(
                StatusDetailItem("Solde $fromCurrency", "${formatAmount(availableSourceBalance, fromCurrency)} $fromCurrency")
            ) else emptyList(),
            primaryButtonText = "Fermer",
            onPrimaryAction = {
                // Clear and retry
            },
            onDismiss = {}
        )
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
                        text = "CashPay Exchange",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Conversion instantanée entre devises au taux officiel en temps réel avec 0% de frais cachés.",
                        fontSize = 12.5.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp,
                        fontFamily = MulishFontFamily
                    )
                }
            },
            shape = RoundedCornerShape(18.dp),
            containerColor = Color(0xFF0F172A)
        )
    }

    // Main Square Pop-up Dialog
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
            color = Color(0xFF0F172A),
            shadowElevation = 24.dp,
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
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
                                .background(Color(0xFF00E676).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyExchange,
                                contentDescription = "Exchange",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Exchange",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Conversion de devises",
                                fontFamily = MulishFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showInfoDialog = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Info",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
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
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Currency Selectors Box (Clean, Straight & Compact)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Source
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Source",
                                fontFamily = MulishFontFamily,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "Solde: ${formatAmount(availableSourceBalance, fromCurrency)} $fromCurrency",
                                fontFamily = MulishFontFamily,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E676)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Source Currency Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableCurrencies.forEach { curr ->
                                val isSelected = fromCurrency == curr
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF00E676) else Color(0xFF0F172A))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF00E676) else Color(0xFF334155),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { onFromCurrencyChange(curr) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = curr,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) Color(0xFF0B0F19) else Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Swap Button
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF00E676), CircleShape)
                                .clickable { onSwapCurrencies() }
                                .testTag("swap_currencies_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Inverser",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Destination
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Destination",
                                fontFamily = MulishFontFamily,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "Vers $toCurrency",
                                fontFamily = MulishFontFamily,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Destination Currency Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableCurrencies.forEach { curr ->
                                val isSelected = toCurrency == curr
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF0F172A))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { onToCurrencyChange(curr) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = curr,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) Color(0xFF0B0F19) else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Amount Field with concise label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Montant ($fromCurrency)",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    if (availableSourceBalance > 0) {
                        Text(
                            text = "MAX",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = Color(0xFF00E676),
                            modifier = Modifier
                                .clickable {
                                    val maxFormatted = if (fromCurrency == "BTC") {
                                        String.format(Locale.US, "%.8f", availableSourceBalance)
                                    } else if (availableSourceBalance % 1.0 == 0.0) {
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

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    placeholder = { Text("Ex: 50", color = Color(0xFF64748B), fontFamily = MulishFontFamily, fontSize = 12.sp) },
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

                Spacer(modifier = Modifier.height(10.dp))

                // Rate & Conversion Summary Card (Concise labels, no wrapping!)
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Taux :", fontSize = 11.5.sp, color = Color(0xFF94A3B8), fontFamily = MulishFontFamily)
                            if (isQuoteLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF00E676)
                                )
                            } else {
                                Text(
                                    text = if (liveRate != null) "1 $fromCurrency = ${formatRate(liveRate)} $toCurrency" else "...",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676),
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Frais :", fontSize = 11.5.sp, color = Color(0xFF94A3B8), fontFamily = MulishFontFamily)
                            Text("Gratuit (0%)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), fontFamily = MulishFontFamily)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("À recevoir :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = MulishFontFamily)
                            Text(
                                text = if (liveRate != null && parsedAmount > 0.0) "${formatAmount(estimatedToAmount, toCurrency)} $toCurrency" else "0.00 $toCurrency",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF38BDF8),
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // PIN Code Input (Concise label)
                Text(
                    text = "Code PIN",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp,
                    color = Color(0xFFCBD5E1)
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) onPinChange(it) },
                    placeholder = { Text("••••", color = Color(0xFF64748B), fontFamily = MulishFontFamily) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
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

                Spacer(modifier = Modifier.height(16.dp))

                // Concise Submit Button (Clean, no long text!)
                val isSubmitEnabled = parsedAmount > 0 && pin.length >= 4 && !isLoading && fromCurrency != toCurrency
                Button(
                    onClick = onSubmit,
                    enabled = isSubmitEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E676),
                        disabledContainerColor = Color(0xFF00E676).copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF0F172A), modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Conversion...", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily)
                    } else {
                        Text(
                            text = "Convertir",
                            color = Color(0xFF0F172A),
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Annuler", color = Color(0xFF94A3B8), fontFamily = MulishFontFamily, fontSize = 12.5.sp)
                }
            }
        }
    }
}

private fun formatAmount(value: Double, currency: String = ""): String {
    val formatter = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        if (currency.equals("BTC", ignoreCase = true)) {
            maximumFractionDigits = 8
            minimumFractionDigits = 2
        } else if (currency.equals("USDT", ignoreCase = true)) {
            maximumFractionDigits = 6
            minimumFractionDigits = 2
        } else {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }
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
