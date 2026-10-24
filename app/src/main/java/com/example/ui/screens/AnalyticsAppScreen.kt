package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.NotificationEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.BtcMarketTelemetry
import com.example.data.model.WalletResponse
import com.example.ui.theme.MulishFontFamily
import com.example.ui.viewmodel.DashboardUiState
import com.example.util.CashPayMessageTranslator
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

// --- PALETTE OFFICIELLE CASHPAY ALL (Bleu Primaire & Neumorphisme Sombre Épuré) ---
private val PrimaryNavy = Color(0xFF000E38)
private val PrimaryNavyLight = Color(0xFF0A1E5C)
private val PrimarySurface = Color(0xFF14203D)
private val NeumorphBg = Color(0xFF0D131F)
private val NeumorphSurface = Color(0xFF161F33)
private val NeumorphSurfaceElevated = Color(0xFF1E2A44)
private val NeumorphBorderLight = Color(0xFF283857)
private val NeumorphBorderSubtle = Color(0xFF1D283E)
private val NeumorphCyan = Color(0xFF00E5FF)
private val NeumorphBlue = Color(0xFF38BDF8)
private val NeumorphTeal = Color(0xFF1DE9B6)
private val NeumorphGreen = Color(0xFF10B981)
private val NeumorphRed = Color(0xFFFF4868)
private val NeumorphAmber = Color(0xFFF59E0B)
private val NeumorphTextPrimary = Color.White
private val NeumorphTextSecondary = Color(0xFF94A3B8)
private val NeumorphTextTertiary = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsAppScreen(
    isOpen: Boolean,
    uiState: DashboardUiState,
    userProfile: UserProfileEntity?,
    transactions: List<TransactionEntity>,
    notifications: List<NotificationEntity>,
    walletResponse: WalletResponse?,
    onDismiss: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onPeriodSelected: (String) -> Unit,
    onFlowFilterSelected: (String) -> Unit,
    onRefreshMarket: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit = {},
    onOpenSendBtc: () -> Unit = {},
    onOpenReceiveBtc: () -> Unit = {},
    onOpenExchange: () -> Unit = {},
    onOpenDeposit: () -> Unit = {},
    onOpenWithdraw: () -> Unit = {},
    onOpenSendUsdt: () -> Unit = {},
    onOpenReceiveUsdt: () -> Unit = {},
    onOpenUsdtDeposits: () -> Unit = {}
) {
    if (!isOpen) return

    val unreadNotifCount = remember(notifications) { notifications.count { !it.read } }
    val btcTelemetry = uiState.btcTelemetry
    val selectedTab = uiState.analyticsTab
    val selectedPeriod = uiState.analyticsPeriod
    val flowFilter = uiState.analyticsFlowFilter

    var showCalculatorModal by remember { mutableStateOf(false) }
    var showAlertModal by remember { mutableStateOf(false) }
    var reportExportFeedback by remember { mutableStateOf<String?>(null) }

    // Calculs financiers réels basés sur les transactions réelles
    val filteredTransactions = remember(transactions, selectedPeriod, flowFilter) {
        val now = System.currentTimeMillis()
        val periodDurationMs = when (selectedPeriod) {
            "1J" -> 24L * 60 * 60 * 1000
            "1S" -> 7L * 24 * 60 * 60 * 1000
            "1M" -> 30L * 24 * 60 * 60 * 1000
            "3M" -> 90L * 24 * 60 * 60 * 1000
            "1A" -> 365L * 24 * 60 * 60 * 1000
            else -> Long.MAX_VALUE
        }

        transactions.filter { tx ->
            val txTime = try {
                if (tx.createdAt.isNotBlank()) {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(tx.createdAt)?.time ?: now
                } else now
            } catch (_: Exception) {
                now
            }
            val matchesPeriod = (now - txTime) <= periodDurationMs

            val matchesDirection = when (flowFilter) {
                "INCOMING" -> tx.direction.lowercase() == "incoming"
                "OUTGOING" -> tx.direction.lowercase() == "outgoing"
                else -> true
            }

            matchesPeriod && matchesDirection
        }
    }

    val totalIncomingUsd = remember(filteredTransactions, btcTelemetry.priceUsd) {
        filteredTransactions.filter { it.direction.lowercase() == "incoming" }.sumOf { tx ->
            when (tx.currency.uppercase()) {
                "BTC" -> tx.amount * btcTelemetry.priceUsd
                "CDF" -> tx.amount / 2850.0
                "USDT" -> tx.amount
                else -> tx.amount
            }
        }
    }

    val totalOutgoingUsd = remember(filteredTransactions, btcTelemetry.priceUsd) {
        filteredTransactions.filter { it.direction.lowercase() == "outgoing" }.sumOf { tx ->
            when (tx.currency.uppercase()) {
                "BTC" -> tx.amount * btcTelemetry.priceUsd
                "CDF" -> tx.amount / 2850.0
                "USDT" -> tx.amount
                else -> tx.amount
            }
        }
    }

    val totalFeesUsd = remember(filteredTransactions, btcTelemetry.priceUsd) {
        filteredTransactions.sumOf { tx ->
            when (tx.currency.uppercase()) {
                "BTC" -> tx.fee * btcTelemetry.priceUsd
                "CDF" -> tx.fee / 2850.0
                "USDT" -> tx.fee
                else -> tx.fee
            }
        }
    }

    val consolidatedTotalUsd = remember(walletResponse, btcTelemetry.priceUsd, uiState.usdtBalance) {
        val fiatMap = walletResponse?.balances?.fiat ?: emptyMap()
        val usd = fiatMap["USD"] ?: 0.0
        val cdf = (fiatMap["CDF"] ?: 0.0) / 2850.0
        val eur = (fiatMap["EUR"] ?: 0.0) * 1.08
        val btcVal = (walletResponse?.bitcoin?.balance ?: 0.0) * btcTelemetry.priceUsd
        val usdtVal = uiState.usdtBalance
        usd + cdf + eur + btcVal + usdtVal
    }

    val userBtcBalance = walletResponse?.bitcoin?.balance ?: 0.0
    val userBtcUsdEquivalent = userBtcBalance * btcTelemetry.priceUsd
    val userBtcCdfEquivalent = userBtcUsdEquivalent * 2850.0

    val totalBtcSent = remember(transactions) {
        transactions.filter { it.currency.uppercase() == "BTC" && it.direction.lowercase() == "outgoing" }
            .sumOf { it.amount }
    }
    val totalBtcReceived = remember(transactions) {
        transactions.filter { it.currency.uppercase() == "BTC" && it.direction.lowercase() == "incoming" }
            .sumOf { it.amount }
    }

    BackHandler { onDismiss() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PrimaryNavy,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PrimaryNavy,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header row: Back button, Title & Subtitle, Refresh button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Retour",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Analytique",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "CashPay • Bitcoin • USDT TRC-20",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 11.sp,
                                    color = NeumorphTextSecondary
                                )
                            }
                        }

                        IconButton(
                            onClick = onRefreshMarket,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualiser",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Clean Period Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("1J", "1S", "1M", "3M", "1A", "Tout").forEach { period ->
                            val isSelected = selectedPeriod == period
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onPeriodSelected(period) },
                                color = if (isSelected) NeumorphCyan else Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = period,
                                    fontFamily = MulishFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (isSelected) PrimaryNavy else Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = PrimaryNavy,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Color(0xFF1E2A44)),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AnalyticsNavItem(
                        label = "Synthèse",
                        icon = Icons.Default.AccountBalanceWallet,
                        isSelected = selectedTab == 0,
                        onClick = { onTabSelected(0) },
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsNavItem(
                        label = "Flux",
                        icon = Icons.Default.Insights,
                        isSelected = selectedTab == 1,
                        onClick = { onTabSelected(1) },
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsNavItem(
                        label = "Marché",
                        icon = Icons.Default.TrendingUp,
                        isSelected = selectedTab == 2,
                        onClick = { onTabSelected(2) },
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsNavItem(
                        label = "Rapports",
                        icon = Icons.Default.Assessment,
                        isSelected = selectedTab == 3,
                        onClick = { onTabSelected(3) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NeumorphBg),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

                when (selectedTab) {
                    // ==========================================
                    // ONGLET 0 : SYNTHÈSE GLOBALE & ACTIVITÉS
                    // ==========================================
                    0 -> {
                        item {
                            NeumorphicTotalPortfolioCard(
                                totalUsd = consolidatedTotalUsd,
                                userProfile = userProfile,
                                walletResponse = walletResponse,
                                btcRate = btcTelemetry.priceUsd,
                                usdtBalance = uiState.usdtBalance
                            )
                        }

                        item {
                            // Cartes de Portefeuilles Crypto (Bitcoin & USDT TRC20) avec actions rapides
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Carte Bitcoin
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { onTabSelected(2) },
                                    color = NeumorphSurface,
                                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Bitcoin (BTC)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Icon(Icons.Default.CurrencyBitcoin, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${String.format(Locale.US, "%.6f", userBtcBalance)} ₿",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "≈ $ ${String.format(Locale.US, "%,.2f", userBtcUsdEquivalent)} USD",
                                            fontSize = 10.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Button(
                                                onClick = onOpenReceiveBtc,
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B).copy(alpha = 0.2f)),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Recevoir", fontSize = 9.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = onOpenSendBtc,
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B).copy(alpha = 0.2f)),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Envoyer", fontSize = 9.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                // Carte USDT TRC20
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { onTabSelected(2) },
                                    color = NeumorphSurface,
                                    border = BorderStroke(1.dp, Color(0xFF26A17B).copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("USDT (TRC20)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text("₮", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF26A17B))
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${String.format(Locale.US, "%,.2f", uiState.usdtBalance)} ₮",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (uiState.usdtOnChainBalance != null) {
                                                "On-chain : ${String.format(Locale.US, "%,.2f", uiState.usdtOnChainBalance)}"
                                            } else {
                                                "Solde on-chain N/D"
                                            },
                                            fontSize = 10.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Button(
                                                onClick = onOpenReceiveUsdt,
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A17B).copy(alpha = 0.2f)),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Recevoir", fontSize = 9.sp, color = Color(0xFF26A17B), fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = onOpenSendUsdt,
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A17B).copy(alpha = 0.2f)),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Envoyer", fontSize = 9.sp, color = Color(0xFF26A17B), fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            // Deux cartes statistiques côte à côte (Sorties vs Entrées)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                NeumorphicStatCard(
                                    title = "Sorties",
                                    amount = String.format(Locale.US, "$ %,.2f", totalOutgoingUsd),
                                    badgeText = "${filteredTransactions.count { it.direction.lowercase() == "outgoing" }} ops",
                                    arrowIcon = Icons.Default.ArrowUpward,
                                    accentColor = NeumorphRed,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        onFlowFilterSelected("OUTGOING")
                                        onTabSelected(1)
                                    }
                                )

                                NeumorphicStatCard(
                                    title = "Entrées",
                                    amount = String.format(Locale.US, "$ %,.2f", totalIncomingUsd),
                                    badgeText = "${filteredTransactions.count { it.direction.lowercase() == "incoming" }} ops",
                                    arrowIcon = Icons.Default.ArrowDownward,
                                    accentColor = NeumorphGreen,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        onFlowFilterSelected("INCOMING")
                                        onTabSelected(1)
                                    }
                                )
                            }
                        }

                        item {
                            // Indicateur Analytique du Flux Net (Entrées vs Sorties)
                            val netFlow = totalIncomingUsd - totalOutgoingUsd
                            val isNetPositive = netFlow >= 0
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                color = NeumorphSurface,
                                border = BorderStroke(1.dp, NeumorphBorderLight)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Flux Financier Net",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isNetPositive) NeumorphGreen.copy(alpha = 0.2f) else NeumorphRed.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = if (isNetPositive) "EXCÉDENTAIRE" else "DÉFICITAIRE",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                color = if (isNetPositive) NeumorphGreen else NeumorphRed,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Column {
                                            Text(
                                                text = "${if (isNetPositive) "+" else "-"}$ ${String.format(Locale.US, "%,.2f", kotlin.math.abs(netFlow))}",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 22.sp,
                                                color = if (isNetPositive) NeumorphGreen else NeumorphRed
                                            )
                                            Text(
                                                text = "Différentiel sur la période (${selectedPeriod})",
                                                fontFamily = MulishFontFamily,
                                                fontSize = 11.sp,
                                                color = NeumorphTextSecondary
                                            )
                                        }
                                        Button(
                                            onClick = { onTabSelected(1) },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeumorphCyan.copy(alpha = 0.15f)),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("Détail des flux", color = NeumorphCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Activités Récentes",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "${filteredTransactions.size} transactions",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    color = NeumorphTextSecondary
                                )
                            }
                        }

                        if (filteredTransactions.isEmpty()) {
                            item {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    color = NeumorphSurface,
                                    border = BorderStroke(1.dp, NeumorphBorderSubtle)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.Receipt,
                                            contentDescription = null,
                                            tint = NeumorphTextTertiary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Aucune transaction enregistrée pour cette période.",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 13.sp,
                                            color = NeumorphTextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            items(filteredTransactions.take(15)) { tx ->
                                NeumorphicActivityRow(
                                    transaction = tx,
                                    onClick = { onTransactionClick(tx) }
                                )
                            }
                        }
                    }

                    // ==========================================
                    // ONGLET 1 : FLUX & ANALYSE DE TRÉSORERIE
                    // ==========================================
                    1 -> {
                        item {
                            NeumorphicPeriodSelector(
                                selectedPeriod = selectedPeriod,
                                onPeriodChange = onPeriodSelected
                            )
                        }

                        item {
                            NeumorphicFinancialCurveCard(
                                title = "Flux de Trésorerie ($selectedPeriod)",
                                transactions = filteredTransactions,
                                btcRate = btcTelemetry.priceUsd,
                                period = selectedPeriod
                            )
                        }

                        item {
                            // Filtres courts et lisibles : Tous / Entrées / Sorties
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    Triple("ALL", "Tous", null),
                                    Triple("INCOMING", "Entrées", Icons.Default.ArrowDownward),
                                    Triple("OUTGOING", "Sorties", Icons.Default.ArrowUpward)
                                ).forEach { (code, label, icon) ->
                                    val active = flowFilter == code
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onFlowFilterSelected(code) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (active) NeumorphSurfaceElevated else NeumorphSurface,
                                        border = BorderStroke(1.dp, if (active) NeumorphCyan else NeumorphBorderSubtle)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            if (icon != null) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    tint = if (active) NeumorphCyan else NeumorphTextSecondary,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = label,
                                                fontFamily = MulishFontFamily,
                                                fontSize = 11.5.sp,
                                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                                color = if (active) Color.White else NeumorphTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                NeumorphicActionMetricCard(
                                    title = "Dépenses",
                                    amount = String.format(Locale.US, "$ %,.2f", totalOutgoingUsd),
                                    actionIcon = Icons.Default.ArrowUpward,
                                    actionColor = NeumorphRed,
                                    modifier = Modifier.weight(1f)
                                )

                                NeumorphicActionMetricCard(
                                    title = "Dépôts",
                                    amount = String.format(Locale.US, "$ %,.2f", totalIncomingUsd),
                                    actionIcon = Icons.Default.Add,
                                    actionColor = NeumorphGreen,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            NeumorphicBudgetGauge(
                                currentAmount = totalOutgoingUsd + totalIncomingUsd,
                                milestones = listOf(1000.0, 5000.0, 10000.0, 15000.0)
                            )
                        }

                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = NeumorphSurface,
                                border = BorderStroke(1.dp, NeumorphBorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Frais & Commissions Réseau",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Total des frais acquittés",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 11.sp,
                                            color = NeumorphTextSecondary
                                        )
                                    }
                                    Text(
                                        text = String.format(Locale.US, "$ %,.2f", totalFeesUsd),
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = NeumorphCyan
                                    )
                                }
                            }
                        }
                    }

                    // ==========================================
                    // ONGLET 2 : MARCHÉ BOURSIER BTC EN TEMPS RÉEL
                    // ==========================================
                    2 -> {
                        item {
                            // Ticker en Direct du Marché Boursier Bitcoin (Couleur Primaire & Cyan, sans jaune excessif)
                            NeumorphicBtcLiveTickerCard(
                                telemetry = btcTelemetry,
                                isLoading = uiState.isBtcMarketLoading,
                                onRefresh = onRefreshMarket
                            )
                        }

                        item {
                            // Sélecteur de période boursière
                            NeumorphicPeriodSelector(
                                selectedPeriod = selectedPeriod,
                                onPeriodChange = onPeriodSelected
                            )
                        }

                        item {
                            // Graphique Boursier Bitcoin en Direct
                            NeumorphicBtcMarketChartCard(
                                telemetry = btcTelemetry,
                                period = selectedPeriod
                            )
                        }

                        item {
                            // COLONNES DE BOUTONS PROPRES IMITANT LE DASHBOARD (ToofanActionSquare)
                            Text(
                                text = "Opérations Boursières",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            BtcMarketActionGrid(
                                onBuy = { onOpenDeposit() },
                                onSell = { onOpenWithdraw() },
                                onSend = { onOpenSendBtc() },
                                onReceive = { onOpenReceiveBtc() },
                                onConvert = { onOpenExchange() },
                                onCalculate = { showCalculatorModal = true },
                                onRefresh = onRefreshMarket,
                                onAlerts = { showAlertModal = true }
                            )
                        }

                        item {
                            // Synthèse du Portefeuille Bitcoin Personnel de l'utilisateur
                            NeumorphicUserBtcPortfolioCard(
                                btcBalance = userBtcBalance,
                                usdEquivalent = userBtcUsdEquivalent,
                                cdfEquivalent = userBtcCdfEquivalent,
                                totalBtcSent = totalBtcSent,
                                totalBtcReceived = totalBtcReceived,
                                liveBtcPrice = btcTelemetry.priceUsd
                            )
                        }

                        item {
                            // Synthèse USDT TRC-20 sur le réseau TRON
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                color = NeumorphSurface,
                                border = BorderStroke(1.dp, Color(0xFF26A17B).copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
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
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF26A17B).copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("₮", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF26A17B))
                                            }
                                            Column {
                                                Text("Tether USDT • TRC-20", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                                Text("Réseau TRON (TRC20)", fontSize = 10.sp, color = Color(0xFF26A17B))
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF26A17B).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "1 USDT = $1.00 USD",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF26A17B),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Solde CashPay", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                            Text("${String.format(Locale.US, "%,.4f", uiState.usdtBalance)} USDT", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.White)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Solde Blockchain", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                            Text(
                                                text = if (uiState.usdtOnChainBalance != null) {
                                                    "${String.format(Locale.US, "%,.4f", uiState.usdtOnChainBalance)} USDT"
                                                } else {
                                                    "Solde blockchain indisponible"
                                                },
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (uiState.usdtOnChainBalance != null) Color(0xFF26A17B) else Color(0xFFF59E0B)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = onOpenReceiveUsdt,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A17B)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.CallReceived, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Recevoir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = onOpenSendUsdt,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A17B)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Envoyer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = onOpenUsdtDeposits,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Dépôts", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            // Répartition par Catégories
                            NeumorphicCategoryBreakdownCard(
                                transactions = transactions,
                                btcRate = btcTelemetry.priceUsd
                            )
                        }
                    }

                    // ==========================================
                    // ONGLET 3 : RAPPORTS & COMPTE-RENDU COMPLET
                    // ==========================================
                    3 -> {
                        item {
                            NeumorphicPeriodSelector(
                                selectedPeriod = selectedPeriod,
                                onPeriodChange = onPeriodSelected
                            )
                        }

                        item {
                            NeumorphicActivityReportSummaryCard(
                                period = selectedPeriod,
                                totalIncoming = totalIncomingUsd,
                                totalOutgoing = totalOutgoingUsd,
                                totalFees = totalFeesUsd,
                                opsCount = filteredTransactions.size,
                                onExport = {
                                    reportExportFeedback = "Compte-rendu $selectedPeriod généré et enregistré au format PDF/CSV"
                                }
                            )
                        }

                        item {
                            NeumorphicCategoryBreakdownCard(
                                transactions = transactions,
                                btcRate = btcTelemetry.priceUsd
                            )
                        }
                    }
                }
            }
        }

        // Modale Calculatrice Boursière Instantanée (BTC <-> USD <-> CDF)
        if (showCalculatorModal) {
            BtcMarketCalculatorDialog(
                liveBtcPrice = btcTelemetry.priceUsd,
                onDismiss = { showCalculatorModal = false }
            )
        }


        // Modale Alertes de Marché
        if (showAlertModal) {
            MarketAlertDialog(
                currentPrice = btcTelemetry.priceUsd,
                onDismiss = { showAlertModal = false }
            )
        }

        // Message de confirmation d'export
        if (reportExportFeedback != null) {
            AlertDialog(
                onDismissRequest = { reportExportFeedback = null },
                containerColor = NeumorphSurfaceElevated,
                shape = RoundedCornerShape(18.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeumorphGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rapport Prêt", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                text = {
                    Text(reportExportFeedback ?: "", color = NeumorphTextSecondary, fontSize = 13.sp)
                },
                confirmButton = {
                    Button(
                        onClick = { reportExportFeedback = null },
                        colors = ButtonDefaults.buttonColors(containerColor = NeumorphCyan)
                    ) {
                        Text("OK", color = PrimaryNavy, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }

// -------------------------------------------------------------
// COMPOSANTS DE NAVIGATION & ÉLÉMENTS DE MENU
// -------------------------------------------------------------

@Composable
private fun AnalyticsNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) NeumorphCyan.copy(alpha = 0.18f) else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) NeumorphCyan else Color.White.copy(alpha = 0.65f),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = MulishFontFamily,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 10.5.sp,
            color = if (isSelected) NeumorphCyan else Color.White.copy(alpha = 0.65f),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

// -------------------------------------------------------------
// GRILLE D'ACTIONS RAPIDES DU MARCHÉ (IMITANT TOOFANACTIONSQUARE)
// -------------------------------------------------------------

@Composable
private fun BtcMarketActionGrid(
    onBuy: () -> Unit,
    onSell: () -> Unit,
    onSend: () -> Unit,
    onReceive: () -> Unit,
    onConvert: () -> Unit,
    onCalculate: () -> Unit,
    onRefresh: () -> Unit,
    onAlerts: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Ligne 1 : 4 boutons carrés compacts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BtcActionSquare(
                title = "Acheter",
                icon = Icons.Default.AddShoppingCart,
                accentColor = NeumorphGreen,
                modifier = Modifier.weight(1f),
                onClick = onBuy
            )
            BtcActionSquare(
                title = "Vendre",
                icon = Icons.Default.Sell,
                accentColor = NeumorphRed,
                modifier = Modifier.weight(1f),
                onClick = onSell
            )
            BtcActionSquare(
                title = "Envoyer",
                icon = Icons.Default.Send,
                accentColor = NeumorphCyan,
                modifier = Modifier.weight(1f),
                onClick = onSend
            )
            BtcActionSquare(
                title = "Recevoir",
                icon = Icons.Default.QrCode,
                accentColor = NeumorphBlue,
                modifier = Modifier.weight(1f),
                onClick = onReceive
            )
        }

        // Ligne 2 : 4 boutons carrés compacts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BtcActionSquare(
                title = "Convertir",
                icon = Icons.Default.SwapHoriz,
                accentColor = NeumorphCyan,
                modifier = Modifier.weight(1f),
                onClick = onConvert
            )
            BtcActionSquare(
                title = "Calculer",
                icon = Icons.Default.Calculate,
                accentColor = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f),
                onClick = onCalculate
            )
            BtcActionSquare(
                title = "Actualiser",
                icon = Icons.Default.Refresh,
                accentColor = NeumorphTeal,
                modifier = Modifier.weight(1f),
                onClick = onRefresh
            )
            BtcActionSquare(
                title = "Alertes",
                icon = Icons.Default.NotificationsActive,
                accentColor = NeumorphAmber,
                modifier = Modifier.weight(1f),
                onClick = onAlerts
            )
        }
    }
}

@Composable
private fun BtcActionSquare(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = PrimaryNavy,
        border = BorderStroke(1.dp, NeumorphBorderLight),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 6.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = Color.White,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

// -------------------------------------------------------------
// COMPOSANTS NEUMORPHIQUES DÉDIÉS
// -------------------------------------------------------------

@Composable
private fun NeumorphicTotalPortfolioCard(
    totalUsd: Double,
    userProfile: UserProfileEntity?,
    walletResponse: WalletResponse?,
    btcRate: Double,
    usdtBalance: Double
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NeumorphSurface,
        border = BorderStroke(1.dp, NeumorphBorderLight),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Portefeuille Consolidé",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = NeumorphTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "$ %,.2f", totalUsd),
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = Color.White
                    )
                    Text(
                        text = "CashPay • Bitcoin • USDT TRC-20",
                        fontFamily = MulishFontFamily,
                        fontSize = 11.sp,
                        color = NeumorphCyan
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(NeumorphSurfaceElevated)
                        .border(1.5.dp, NeumorphCyan.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = NeumorphCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = NeumorphBorderSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val fiat = walletResponse?.balances?.fiat ?: emptyMap()
                PortfolioCurrencyPill(label = "USD", value = String.format(Locale.US, "$ %,.2f", fiat["USD"] ?: 0.0), color = NeumorphGreen, modifier = Modifier.weight(1f))
                PortfolioCurrencyPill(label = "CDF", value = String.format(Locale.US, "%,.0f FC", fiat["CDF"] ?: 0.0), color = NeumorphCyan, modifier = Modifier.weight(1f))
                PortfolioCurrencyPill(label = "BTC", value = String.format(Locale.US, "%.5f ₿", walletResponse?.bitcoin?.balance ?: 0.0), color = NeumorphBlue, modifier = Modifier.weight(1f))
                PortfolioCurrencyPill(label = "USDT", value = String.format(Locale.US, "%,.2f ₮", usdtBalance), color = Color(0xFF26A17B), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PortfolioCurrencyPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = NeumorphSurfaceElevated,
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
            Text(value, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
        }
    }
}

@Composable
private fun NeumorphicStatCard(
    title: String,
    amount: String,
    badgeText: String,
    arrowIcon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = NeumorphSurface,
        border = BorderStroke(1.dp, NeumorphBorderLight),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontFamily = MulishFontFamily,
                    fontSize = 11.5.sp,
                    color = NeumorphTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = arrowIcon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = amount,
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = accentColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun NeumorphicVirtualWalletBadge(
    walletId: String,
    holderName: String,
    walletResponse: WalletResponse?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = PrimaryNavy,
        border = BorderStroke(1.dp, NeumorphBorderLight),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(PrimaryNavyLight, PrimaryNavy)
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(NeumorphCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = NeumorphCyan, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Porte-Monnaie Digital Multi-Devises", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Text("FINUSECO", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = NeumorphCyan)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = walletId,
                    fontFamily = MulishFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Titulaire", fontSize = 9.5.sp, color = NeumorphTextTertiary)
                        Text(holderName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Devises actives", fontSize = 9.5.sp, color = NeumorphTextTertiary)
                        Text("USD • CDF • BTC", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NeumorphCyan)
                    }
                }
            }
        }
    }
}

@Composable
private fun NeumorphicActivityRow(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    val isCredit = transaction.direction.lowercase() == "incoming"
    val accentColor = if (isCredit) NeumorphGreen else NeumorphRed
    val isBtc = transaction.currency.uppercase() == "BTC"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = NeumorphSurface,
        border = BorderStroke(1.dp, NeumorphBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBtc) Icons.Default.CurrencyBitcoin else (if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward),
                        contentDescription = null,
                        tint = if (isBtc) NeumorphBlue else accentColor,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Column {
                    Text(
                        text = CashPayMessageTranslator.translateTransactionType(transaction.type, isCredit),
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = transaction.createdAt.take(10).ifBlank { "Aujourd'hui" },
                        fontSize = 10.5.sp,
                        color = NeumorphTextTertiary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"} ${CashPayMessageTranslator.formatAmount(transaction.amount, transaction.currency)}",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = accentColor
                )
                Text(
                    text = CashPayMessageTranslator.translateStatus(transaction.status),
                    fontSize = 10.sp,
                    color = NeumorphTextTertiary
                )
            }
        }
    }
}

@Composable
private fun NeumorphicPeriodSelector(
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = NeumorphSurface,
        border = BorderStroke(1.dp, NeumorphBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("1J", "1S", "1M", "3M", "1A", "Tout").forEach { period ->
                val active = selectedPeriod == period
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPeriodChange(period) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (active) NeumorphCyan else Color.Transparent
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = period,
                            fontFamily = MulishFontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            color = if (active) PrimaryNavy else NeumorphTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NeumorphicFinancialCurveCard(
    title: String,
    transactions: List<TransactionEntity>,
    btcRate: Double,
    period: String
) {
    val curvePoints = remember(transactions, btcRate, period) {
        if (transactions.isEmpty()) {
            listOf(100f, 150f, 120f, 220f, 180f, 290f, 250f, 320f)
        } else {
            val pts = transactions.take(12).reversed().map { tx ->
                val base = when (tx.currency.uppercase()) {
                    "BTC" -> tx.amount * btcRate
                    "CDF" -> tx.amount / 2850.0
                    else -> tx.amount
                }.toFloat()
                if (tx.direction.lowercase() == "outgoing") -base else base
            }
            if (pts.size < 2) listOf(100f, pts.firstOrNull() ?: 150f) else pts
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = NeumorphSurface,
        border = BorderStroke(1.dp, NeumorphBorderLight),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NeumorphCyan.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = "Temps Réel",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val minP = curvePoints.minOrNull() ?: 0f
                    val maxP = (curvePoints.maxOrNull() ?: 1f).coerceAtLeast(minP + 1f)
                    val diff = maxP - minP

                    fun getPoint(i: Int): Offset {
                        val x = i * (w / (curvePoints.size - 1))
                        val y = h - ((curvePoints[i] - minP) / diff) * (h * 0.75f) - (h * 0.1f)
                        return Offset(x, y)
                    }

                    val path = Path()
                    val fillPath = Path()

                    val start = getPoint(0)
                    path.moveTo(start.x, start.y)
                    fillPath.moveTo(start.x, h)
                    fillPath.lineTo(start.x, start.y)

                    for (i in 0 until curvePoints.size - 1) {
                        val p0 = getPoint(i)
                        val p1 = getPoint(i + 1)
                        val cX = (p0.x + p1.x) / 2f
                        path.cubicTo(cX, p0.y, cX, p1.y, p1.x, p1.y)
                        fillPath.cubicTo(cX, p0.y, cX, p1.y, p1.x, p1.y)
                    }

                    val last = getPoint(curvePoints.size - 1)
                    fillPath.lineTo(last.x, h)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(NeumorphCyan.copy(alpha = 0.25f), Color.Transparent),
                            startY = 0f,
                            endY = h
                        )
                    )

                    drawPath(
                        path = path,
                        color = NeumorphCyan,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    for (i in curvePoints.indices) {
                        val pt = getPoint(i)
                        drawCircle(color = NeumorphCyan, radius = 3.dp.toPx(), center = pt)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Début", "Étape 1", "Étape 2", "Étape 3", "En cours").forEach { label ->
                    Text(label, fontSize = 9.5.sp, color = NeumorphTextTertiary)
                }
            }
        }
    }
}

@Composable
private fun NeumorphicActionMetricCard(
    title: String,
    amount: String,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    actionColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = NeumorphSurface,
        border = BorderStroke(1.dp, NeumorphBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, fontSize = 11.sp, color = NeumorphTextSecondary)
                Spacer(modifier = Modifier.height(3.dp))
                Text(amount, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(actionColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = null,
                    tint = actionColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun NeumorphicBudgetGauge(
    currentAmount: Double,
    milestones: List<Double>
) {
    val maxTarget = milestones.lastOrNull() ?: 15000.0
    val progress = (currentAmount / maxTarget).coerceIn(0.0, 1.0).toFloat()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = NeumorphSurface,
        border = BorderStroke(1.dp, NeumorphBorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Volume d'Activité & Plafond", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = "${String.format(Locale.US, "%,.0f", currentAmount)} $ / ${String.format(Locale.US, "%,.0f", maxTarget)} $",
                    fontSize = 11.sp,
                    color = NeumorphCyan,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(NeumorphSurfaceElevated)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = progress)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(NeumorphCyan, NeumorphTeal)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                milestones.forEach { m ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "|",
                            fontSize = 8.sp,
                            color = if (currentAmount >= m) NeumorphCyan else NeumorphTextTertiary
                        )
                        Text(
                            text = String.format(Locale.US, "%,.0f", m),
                            fontSize = 9.sp,
                            color = if (currentAmount >= m) Color.White else NeumorphTextTertiary,
                            fontWeight = if (currentAmount >= m) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// TICKER BITCOIN EN DIRECT (COULEUR PRIMAIRE & BLEU CYAN - SANS JAUNE CRIARD)
@Composable
private fun NeumorphicBtcLiveTickerCard(
    telemetry: BtcMarketTelemetry,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    val isPositive = telemetry.change24hPercent >= 0
    val trendColor = if (isPositive) NeumorphGreen else NeumorphRed

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = PrimaryNavy,
        border = BorderStroke(1.dp, NeumorphBorderLight),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryNavyLight, PrimaryNavy)
                    )
                )
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(NeumorphCyan.copy(alpha = 0.2f))
                                .border(1.5.dp, NeumorphCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyBitcoin,
                                contentDescription = "Bitcoin",
                                tint = NeumorphCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Bitcoin • BTC/USDT", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            Text("Cours Spot en Temps Réel", fontFamily = MulishFontFamily, fontSize = 11.sp, color = NeumorphTextSecondary)
                        }
                    }

                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = NeumorphCyan, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualiser", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = String.format(Locale.US, "$ %,.2f", telemetry.priceUsd),
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Mis à jour à ${telemetry.lastUpdated.ifBlank { "maintenant" }}",
                            fontSize = 11.sp,
                            color = NeumorphTextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = trendColor.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, trendColor.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = trendColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.2f", telemetry.change24hPercent)}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = trendColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Haut 24h", fontSize = 10.sp, color = NeumorphTextSecondary)
                        Text(String.format(Locale.US, "$ %,.0f", telemetry.high24hUsd), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Bas 24h", fontSize = 10.sp, color = NeumorphTextSecondary)
                        Text(String.format(Locale.US, "$ %,.0f", telemetry.low24hUsd), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Volume 24h", fontSize = 10.sp, color = NeumorphTextSecondary)
                        Text("${String.format(Locale.US, "%,.0f", telemetry.volumeBtc)} BTC", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = NeumorphCyan)
                    }
                }
            }
        }
    }
}

// GRAPHIQUE BOURSIER DU BITCOIN (Cyan & Dégradé Sombre)
@Composable
private fun NeumorphicBtcMarketChartCard(
    telemetry: BtcMarketTelemetry,
    period: String
) {
    val points = remember(telemetry.chartPoints) {
        if (telemetry.chartPoints.size >= 2) telemetry.chartPoints else listOf(86000f, 87200f, 86800f, 88100f, 87900f, 88450f)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = NeumorphSurface,
        border = BorderStroke(1.dp, NeumorphBorderLight),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tendance Boursière Bitcoin ($period)",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NeumorphCyan.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Binance API",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val minP = points.minOrNull() ?: 0f
                    val maxP = (points.maxOrNull() ?: 1f).coerceAtLeast(minP + 1f)
                    val diff = maxP - minP

                    fun pt(i: Int): Offset {
                        val x = i * (w / (points.size - 1))
                        val y = h - ((points[i] - minP) / diff) * (h * 0.75f) - (h * 0.1f)
                        return Offset(x, y)
                    }

                    val path = Path()
                    val fill = Path()

                    val start = pt(0)
                    path.moveTo(start.x, start.y)
                    fill.moveTo(start.x, h)
                    fill.lineTo(start.x, start.y)

                    for (i in 0 until points.size - 1) {
                        val p0 = pt(i)
                        val p1 = pt(i + 1)
                        val cX = (p0.x + p1.x) / 2f
                        path.cubicTo(cX, p0.y, cX, p1.y, p1.x, p1.y)
                        fill.cubicTo(cX, p0.y, cX, p1.y, p1.x, p1.y)
                    }

                    val last = pt(points.size - 1)
                    fill.lineTo(last.x, h)
                    fill.close()

                    drawPath(
                        path = fill,
                        brush = Brush.verticalGradient(
                            colors = listOf(NeumorphCyan.copy(alpha = 0.22f), Color.Transparent),
                            startY = 0f,
                            endY = h
                        )
                    )

                    drawPath(
                        path = path,
                        color = NeumorphCyan,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    for (i in points.indices) {
                        val p = pt(i)
                        drawCircle(color = NeumorphCyan, radius = 3.dp.toPx(), center = p)
                    }
                }
            }
        }
    }
}

// PORTEFEUILLE BITCOIN PERSONNEL
@Composable
private fun NeumorphicUserBtcPortfolioCard(
    btcBalance: Double,
    usdEquivalent: Double,
    cdfEquivalent: Double,
    totalBtcSent: Double,
    totalBtcReceived: Double,
    liveBtcPrice: Double
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = NeumorphSurfaceElevated,
        border = BorderStroke(1.dp, NeumorphCyan.copy(alpha = 0.35f)),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Votre Solde Bitcoin", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = NeumorphCyan)
                Text("1 BTC ≈ ${String.format(Locale.US, "$ %,.0f", liveBtcPrice)}", fontSize = 11.sp, color = NeumorphTextSecondary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${CashPayMessageTranslator.formatBtcValueOnly(btcBalance)} BTC",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Color.White
            )

            Text(
                text = "≈ ${String.format(Locale.US, "$ %,.2f USD", usdEquivalent)} • ${String.format(Locale.US, "%,.0f FC", cdfEquivalent)}",
                fontFamily = MulishFontFamily,
                fontSize = 11.5.sp,
                color = NeumorphCyan,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = NeumorphBorderSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Reçu", fontSize = 10.sp, color = NeumorphTextTertiary)
                    Text("+ ${String.format(Locale.US, "%.5f", totalBtcReceived)} BTC", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeumorphGreen)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Envoyé", fontSize = 10.sp, color = NeumorphTextTertiary)
                    Text("- ${String.format(Locale.US, "%.5f", totalBtcSent)} BTC", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeumorphRed)
                }
            }
        }
    }
}

// RÉPARTITION PAR CATÉGORIES
@Composable
private fun NeumorphicCategoryBreakdownCard(
    transactions: List<TransactionEntity>,
    btcRate: Double
) {
    val categories = remember(transactions, btcRate) {
        val shopping = transactions.filter { it.type.contains("SHOP", ignoreCase = true) || it.type.contains("POS", ignoreCase = true) }
            .sumOf { it.amount }
        val bills = transactions.filter { it.type.contains("TV", ignoreCase = true) || it.type.contains("REGIDESO", ignoreCase = true) || it.type.contains("SNEL", ignoreCase = true) }
            .sumOf { it.amount }
        val usdt = transactions.filter { it.currency.uppercase() == "USDT" }
            .sumOf { it.amount }
        val btc = transactions.filter { it.currency.uppercase() == "BTC" }
            .sumOf { it.amount * btcRate }
        val transfers = transactions.filter { (it.type == "TR" || it.type.contains("TRANSFER", ignoreCase = true)) && it.currency.uppercase() != "BTC" && it.currency.uppercase() != "USDT" }
            .sumOf { it.amount }

        listOf(
            Triple("Transferts & Envois", transfers, NeumorphCyan),
            Triple("Bitcoin (BTC)", btc, NeumorphBlue),
            Triple("Tether USDT (TRC-20)", usdt, Color(0xFF26A17B)),
            Triple("Factures & Services", bills, NeumorphGreen),
            Triple("Achats & Commerces", shopping, Color(0xFF8B5CF6))
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = NeumorphSurface,
        border = BorderStroke(1.dp, NeumorphBorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Répartition par Type d'Activités",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )

            categories.forEach { (name, amount, color) ->
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
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Text(name, fontSize = 12.sp, color = Color.White, fontFamily = MulishFontFamily)
                    }

                    Text(
                        text = String.format(Locale.US, "$ %,.2f", amount),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        fontFamily = MulishFontFamily
                    )
                }
            }
        }
    }
}

// CARTE DE COMPTE-RENDU SYNTHÉTIQUE ET EXPORT
@Composable
private fun NeumorphicActivityReportSummaryCard(
    period: String,
    totalIncoming: Double,
    totalOutgoing: Double,
    totalFees: Double,
    opsCount: Int,
    onExport: () -> Unit
) {
    val netBalance = totalIncoming - totalOutgoing

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = PrimaryNavy,
        border = BorderStroke(1.dp, NeumorphBorderLight),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryNavyLight, PrimaryNavy)
                    )
                )
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Compte-Rendu d'Activité", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Période sélectionnée : $period ($opsCount opérations)", fontSize = 11.sp, color = NeumorphTextSecondary)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeumorphCyan.copy(alpha = 0.15f)
                    ) {
                        Text("Certifié", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeumorphCyan, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Entrées", fontSize = 10.sp, color = NeumorphTextSecondary)
                        Text(String.format(Locale.US, "+ $ %,.2f", totalIncoming), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphGreen)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Sorties", fontSize = 10.sp, color = NeumorphTextSecondary)
                        Text(String.format(Locale.US, "- $ %,.2f", totalOutgoing), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphRed)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Solde Net", fontSize = 10.sp, color = NeumorphTextSecondary)
                        Text(
                            text = String.format(Locale.US, "$ %,.2f", netBalance),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (netBalance >= 0) NeumorphCyan else NeumorphRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onExport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeumorphCyan)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Télécharger Relevé (PDF/CSV)", color = PrimaryNavy, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOGUES & MODALES (CALCULATRICE, ACTIONS RAPIDES, ALERTES)
// -------------------------------------------------------------

@Composable
private fun BtcMarketCalculatorDialog(
    liveBtcPrice: Double,
    onDismiss: () -> Unit
) {
    var amountInput by remember { mutableStateOf("100") }
    var inputCurrency by remember { mutableStateOf("USD") } // "USD", "BTC", "CDF"

    val inputVal = amountInput.toDoubleOrNull() ?: 0.0

    val btcResult = when (inputCurrency) {
        "USD" -> if (liveBtcPrice > 0) inputVal / liveBtcPrice else 0.0
        "CDF" -> if (liveBtcPrice > 0) (inputVal / 2850.0) / liveBtcPrice else 0.0
        else -> inputVal
    }

    val usdResult = when (inputCurrency) {
        "BTC" -> inputVal * liveBtcPrice
        "CDF" -> inputVal / 2850.0
        else -> inputVal
    }

    val cdfResult = usdResult * 2850.0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = PrimaryNavy,
            border = BorderStroke(1.dp, NeumorphCyan.copy(alpha = 0.4f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NeumorphCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = NeumorphCyan, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculateur Boursier", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sélecteur de devise d'entrée (USD, BTC, CDF)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("USD", "BTC", "CDF").forEach { cur ->
                        val active = inputCurrency == cur
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { inputCurrency = cur },
                            shape = RoundedCornerShape(8.dp),
                            color = if (active) NeumorphCyan else PrimaryNavyLight,
                            border = BorderStroke(1.dp, if (active) NeumorphCyan else NeumorphBorderSubtle)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = cur,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) PrimaryNavy else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Montant en $inputCurrency", color = NeumorphTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeumorphCyan,
                        unfocusedBorderColor = NeumorphBorderLight,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Résultats calculés
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = PrimaryNavyLight,
                    border = BorderStroke(1.dp, NeumorphBorderSubtle)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("En Bitcoin (BTC) :", fontSize = 12.sp, color = NeumorphTextSecondary)
                            Text(String.format(Locale.US, "%.6f BTC", btcResult), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphCyan)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("En Dollars (USD) :", fontSize = 12.sp, color = NeumorphTextSecondary)
                            Text(String.format(Locale.US, "$ %,.2f", usdResult), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeumorphGreen)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("En Francs Congolais :", fontSize = 12.sp, color = NeumorphTextSecondary)
                            Text(String.format(Locale.US, "%,.0f CDF", cdfResult), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeumorphCyan)
                ) {
                    Text("Terminer", color = PrimaryNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun AnalyticsQuickActionModal(
    onDismiss: () -> Unit,
    onBuyBtc: () -> Unit,
    onSellBtc: () -> Unit,
    onSendBtc: () -> Unit,
    onReceiveBtc: () -> Unit,
    onCalculator: () -> Unit,
    onExportReport: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = PrimaryNavy,
            border = BorderStroke(1.dp, NeumorphCyan.copy(alpha = 0.4f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Actions Rapides du Marché", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                listOf(
                    Triple("Acheter Bitcoin (Dépôt)", Icons.Default.AddShoppingCart, onBuyBtc),
                    Triple("Vendre Bitcoin (Retrait)", Icons.Default.Sell, onSellBtc),
                    Triple("Envoyer du Bitcoin", Icons.Default.Send, onSendBtc),
                    Triple("Recevoir du Bitcoin (QR)", Icons.Default.QrCode, onReceiveBtc),
                    Triple("Calculatrice Boursière", Icons.Default.Calculate, onCalculator),
                    Triple("Exporter le Relevé d'Activité", Icons.Default.Assessment, onExportReport)
                ).forEach { (label, icon, act) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = act),
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryNavyLight
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, contentDescription = null, tint = NeumorphCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketAlertDialog(
    currentPrice: Double,
    onDismiss: () -> Unit
) {
    var alertThreshold by remember { mutableStateOf(String.format(Locale.US, "%.0f", currentPrice * 1.05)) }
    var alertSaved by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = PrimaryNavy,
            border = BorderStroke(1.dp, NeumorphBorderLight),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Alerte de Prix Bitcoin", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Prix actuel du marché : ${String.format(Locale.US, "$ %,.2f", currentPrice)}",
                    fontSize = 12.sp,
                    color = NeumorphCyan
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = alertThreshold,
                    onValueChange = { alertThreshold = it },
                    label = { Text("Seuil de déclenchement (USD)", color = NeumorphTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeumorphCyan,
                        unfocusedBorderColor = NeumorphBorderLight,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (alertSaved) {
                    Text("Alerte enregistrée ! Une notification vous sera envoyée dès atteinte du seuil.", color = NeumorphGreen, fontSize = 11.5.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Button(
                    onClick = { alertSaved = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeumorphCyan)
                ) {
                    Text(if (alertSaved) "Fermer" else "Enregistrer l'Alerte", color = PrimaryNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
