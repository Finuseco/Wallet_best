package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.MonetizationOn
import com.example.ui.components.FloatingCapsuleBottomBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserProfileEntity
import com.example.data.model.AgentOperationRecord
import com.example.ui.theme.MulishFontFamily
import com.example.ui.viewmodel.DashboardUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentServicesDialog(
    isOpen: Boolean,
    uiState: DashboardUiState,
    userProfile: UserProfileEntity?,
    onDismiss: () -> Unit,
    onOpenQrScanner: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit,
    onToggleAgentBalanceVisibility: () -> Unit,
    onCentralActionClick: () -> Unit,
    // Bascule Commission
    onOpenSweepDialog: (String) -> Unit,
    onCloseSweepDialog: () -> Unit,
    onSweepCurrencyChange: (String) -> Unit,
    onSweepAmountChange: (String) -> Unit,
    onSweepIsAllChange: (Boolean) -> Unit,
    onSweepPinChange: (String) -> Unit,
    onSubmitSweepCommission: () -> Unit,
    // Dépôt Client (Multi-step)
    onDepositClientRefChange: (String) -> Unit,
    onSearchDepositClient: () -> Unit = {},
    onConfirmDepositClient: () -> Unit = {},
    onDepositAmountChange: (String) -> Unit,
    onDepositCurrencyChange: (String) -> Unit,
    onSubmitDepositAmount: () -> Unit = {},
    onDepositPinChange: (String) -> Unit,
    onSubmitDepositPin: () -> Unit = {},
    onSubmitDeposit: () -> Unit,
    onResetDeposit: () -> Unit,
    // Retrait Client (Demande & Confirmation OTP Client)
    onWithdrawClientRefChange: (String) -> Unit,
    onWithdrawAmountChange: (String) -> Unit,
    onWithdrawCurrencyChange: (String) -> Unit,
    onWithdrawChannelChange: (String) -> Unit,
    onWithdrawClientOtpChange: (String) -> Unit,
    onInitiateWithdraw: () -> Unit,
    onSubmitWithdrawOtp: () -> Unit,
    onResetWithdraw: () -> Unit,
    // Prêt Loan Me
    onLoanClientRefChange: (String) -> Unit,
    onSearchLoanTarget: () -> Unit,
    onLoanAmountChange: (String) -> Unit,
    onLoanCurrencyChange: (String) -> Unit,
    onLoanPinChange: (String) -> Unit,
    onSubmitLoanRepay: () -> Unit,
    onResetLoanRepay: () -> Unit,
    onOpenRegisterCustomer: () -> Unit = {},
    onOpenCustomerList: () -> Unit = {},
    onOpenShopping: (Int) -> Unit = {},
    onOpenStarlink: () -> Unit = {}
) {
    if (!isOpen) return

    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val isVisible = uiState.isAgentBalanceVisible

    // Active sub-modals for operational services
    var activeModal by remember { mutableStateOf<String?>(null) } // "deposit", "withdraw", "loan", "history"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            topBar = {
                // Modified Top Bar with overlapping commission balances
                Box(modifier = Modifier.fillMaxWidth()) {
                    CashPayTopHeader(
                        userProfile = userProfile,
                        unreadNotifCount = 0,
                        onNotificationClick = { /* no-op */ },
                        onSettingsClick = { /* no-op */ },
                        height = 200.dp,
                        onBackClick = null 
                    )

                    // Agent Commission Balances overlapping the header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 115.dp)
                    ) {
                        val commMap = if (uiState.agentCommissionsMap.isNotEmpty()) {
                            uiState.agentCommissionsMap
                        } else {
                            mapOf("USD" to uiState.agentCommissionUsd, "CDF" to uiState.agentCommissionCdf)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val colors = listOf(Color(0xFF0F172A), Color(0xFF312E81), Color(0xFF1E3A8A), Color(0xFF065F46))
                            var cIdx = 0
                            commMap.forEach { (curr, amt) ->
                                AgentCommissionCard(
                                    currency = curr,
                                    amount = amt,
                                    isVisible = isVisible,
                                    color = colors[cIdx % colors.size],
                                    onSweep = { onOpenSweepDialog(curr) }
                                )
                                cIdx++
                            }
                        }
                    }
                }
            },
            bottomBar = {
                FloatingCapsuleBottomBar(
                    selectedTab = 0,
                    onTabSelected = { tab ->
                        onDismiss()
                        onTabSelected(tab)
                    },
                    onCentralActionClick = onCentralActionClick
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8F9FD)),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
            ) {
                // Section Title
                item {
                    Text(
                        text = "Services Agent",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(horizontal = 20.dp).padding(top = 20.dp)
                    )
                }

                // Grid of actions
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Group: Opérations
                        Text("Opérations Client", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), fontSize = 14.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AgentActionSquare("Dépôt", "Espèces", Icons.Default.ArrowDownward, Color(0xFF059669), Modifier.weight(1f)) { activeModal = "deposit" }
                            AgentActionSquare("Retrait", "Espèces", Icons.Default.ArrowUpward, Color(0xFFFF6600), Modifier.weight(1f)) { activeModal = "withdraw" }
                        }
                        
                        // Group: Services
                        Text("Gestion & Services", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), fontSize = 14.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AgentActionSquare("Scanner", "QR Code", Icons.Default.QrCodeScanner, Color(0xFF000E38), Modifier.weight(1f)) { onOpenQrScanner("agent_withdraw") }
                            AgentActionSquare("Prêt", "Loan Me", Icons.Default.Payments, Color(0xFF7C3AED), Modifier.weight(1f)) { activeModal = "loan" }
                            AgentActionSquare("Starlink", "Manager", Icons.Default.Public, Color(0xFF0284C7), Modifier.weight(1f)) { onOpenStarlink() }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AgentActionSquare("Historique", "Opérations", Icons.Default.Receipt, Color(0xFF0066FF), Modifier.weight(1f)) { activeModal = "history" }
                            AgentActionSquare("Virement", "Comm.", Icons.Default.SwapHoriz, Color(0xFF00B386), Modifier.weight(1f)) { onOpenSweepDialog("USD") }
                        }

                        // Group: Clients
                        Text("Clients & Shopping", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), fontSize = 14.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AgentActionSquare("KYC", "Client", Icons.Default.PersonAdd, Color(0xFF10B981), Modifier.weight(1f)) { onOpenRegisterCustomer() }
                            AgentActionSquare("Mes Clients", "Parrainés", Icons.Default.Person, Color(0xFF6366F1), Modifier.weight(1f)) { onOpenCustomerList() }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AgentActionSquare("Shopping", "Boutiques", Icons.Default.Store, Color(0xFFFF6600), Modifier.weight(1f)) { onOpenShopping(0) }
                            AgentActionSquare("Catalogue", "Public", Icons.Default.ShoppingBag, Color(0xFF8B5CF6), Modifier.weight(1f)) { onOpenShopping(3) }
                        }
                    }
                }
            }
        }
    }

    // Modal: DÉPÔT CLIENT
    if (activeModal == "deposit") {
        AgentDepositModal(
            uiState = uiState,
            onClose = { 
                onResetDeposit()
                activeModal = null 
            },
            onClientRefChange = onDepositClientRefChange,
            onSearchClient = onSearchDepositClient,
            onConfirmClient = onConfirmDepositClient,
            onAmountChange = onDepositAmountChange,
            onCurrencyChange = onDepositCurrencyChange,
            onSubmitAmount = onSubmitDepositAmount,
            onPinChange = onDepositPinChange,
            onSubmitPin = onSubmitDepositPin,
            onSubmit = onSubmitDeposit,
            onReset = onResetDeposit,
            onOpenQrScanner = { onOpenQrScanner("agent_deposit") }
        )
    }

    // Modal: RETRAIT CLIENT (DEMANDE & OTP)
    if (activeModal == "withdraw") {
        AgentWithdrawModal(
            uiState = uiState,
            onClose = { 
                onResetWithdraw()
                activeModal = null 
            },
            onClientRefChange = onWithdrawClientRefChange,
            onAmountChange = onWithdrawAmountChange,
            onCurrencyChange = onWithdrawCurrencyChange,
            onChannelChange = onWithdrawChannelChange,
            onClientOtpChange = onWithdrawClientOtpChange,
            onInitiate = onInitiateWithdraw,
            onSubmitOtp = onSubmitWithdrawOtp,
            onReset = onResetWithdraw,
            onOpenQrScanner = { onOpenQrScanner("agent_withdraw") }
        )
    }

    // Modal: REMBOURSEMENT PRÊT LOAN ME
    if (activeModal == "loan") {
        AgentLoanModal(
            uiState = uiState,
            onClose = { 
                onResetLoanRepay()
                activeModal = null 
            },
            onClientRefChange = onLoanClientRefChange,
            onSearchTarget = onSearchLoanTarget,
            onAmountChange = onLoanAmountChange,
            onCurrencyChange = onLoanCurrencyChange,
            onPinChange = onLoanPinChange,
            onSubmit = onSubmitLoanRepay,
            onReset = onResetLoanRepay,
            onOpenQrScanner = { onOpenQrScanner("agent_loan") }
        )
    }

    // Modal: HISTORIQUE DES OPÉRATIONS
    if (activeModal == "history") {
        AgentHistoryModal(
            history = uiState.agentOperationsHistory,
            onClose = { activeModal = null }
        )
    }

    // Modal Sheet: BASCULER COMMISSION VERS COMPTE PRINCIPAL
    if (uiState.isSweepCommissionDialogOpen) {
        SweepCommissionModalDialog(
            uiState = uiState,
            onClose = onCloseSweepDialog,
            onCurrencyChange = onSweepCurrencyChange,
            onAmountChange = onSweepAmountChange,
            onIsAllChange = onSweepIsAllChange,
            onPinChange = onSweepPinChange,
            onSubmit = onSubmitSweepCommission
        )
    }
}

// ====================================================
// --- AGENT COMMISSION CARD ---
// ====================================================

@Composable
private fun AgentCommissionCard(
    currency: String,
    amount: Double,
    isVisible: Boolean,
    color: Color,
    onSweep: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(110.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Commission $currency",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = if (isVisible) "${String.format(java.util.Locale.US, "%,.2f", amount)} $currency" else "•••••• $currency",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Surface(
                onClick = onSweep,
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Basculer",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun AgentActionSquare(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(82.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    fontFamily = MulishFontFamily,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

// ====================================================
// --- MODAL: DÉPÔT CLIENT (PARCOURS 5 ÉTAPES CONFORME API) ---
// ====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentDepositModal(
    uiState: DashboardUiState,
    onClose: () -> Unit,
    onClientRefChange: (String) -> Unit,
    onSearchClient: () -> Unit,
    onConfirmClient: () -> Unit,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onSubmitAmount: () -> Unit,
    onPinChange: (String) -> Unit,
    onSubmitPin: () -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    onOpenQrScanner: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val step = uiState.agentDepositStep

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with title and close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dépôt d'argent — Espace Agent",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = when (step) {
                            "identify" -> "Étape 1/4 : Recherche du client"
                            "confirm_client" -> "Étape 2/4 : Identification du bénéficiaire"
                            "amount" -> "Étape 3/4 : Choix devise & montant"
                            "pin" -> "Étape 4/4 : Validation & Code PIN Agent"
                            "completed" -> "Dépôt validé et complété"
                            else -> "Service de dépôt d'espèces"
                        },
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            // Step Progress Bar Indicator (when not completed)
            if (step != "completed") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val stepIndex = when (step) {
                        "identify" -> 1
                        "confirm_client" -> 2
                        "amount" -> 3
                        "pin" -> 4
                        else -> 1
                    }
                    for (i in 1..4) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (i <= stepIndex) Color(0xFF059669) else Color(0xFFE2E8F0)
                                )
                        )
                    }
                }
            }

            // ----------------------------------------------------
            // ÉTAPE 5 : REÇU OFFICIEL (COMPLETED)
            // ----------------------------------------------------
            if (step == "completed" || uiState.agentDepositSuccessDetail != null) {
                val detail = uiState.agentDepositSuccessDetail ?: uiState.agentDepositDetail
                val res = uiState.agentDepositSuccess

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(Color(0xFF059669), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = "Dépôt effectué avec succès !",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = Color(0xFF065F46)
                        )
                        Text(
                            text = "Le compte du client a été crédité en direct.",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF047857),
                            textAlign = TextAlign.Center
                        )

                        // Reçu détaillé
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("N° Transaction", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
                                    Text(
                                        text = "TX-${detail?.transactionId ?: res?.transactionId ?: (System.currentTimeMillis() % 100000)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        fontFamily = MulishFontFamily
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Client Bénéficiaire", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
                                    Text(
                                        text = detail?.clientName ?: uiState.agentDepositFoundClient?.fullName ?: "Client CashPay",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        fontFamily = MulishFontFamily
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Montant Déposé", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
                                    Text(
                                        text = "${detail?.depositedAmount ?: detail?.depositAmount ?: uiState.agentDepositAmount} ${detail?.currency ?: uiState.agentDepositCurrency}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF059669),
                                        fontFamily = MulishFontFamily
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Frais d'opération", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
                                    Text(
                                        text = "0.00 ${detail?.currency ?: uiState.agentDepositCurrency} (Gratuit)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF059669),
                                        fontFamily = MulishFontFamily
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total Débité Agent", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
                                    Text(
                                        text = "${detail?.totalDebited ?: detail?.totalDebitAgent ?: uiState.agentDepositAmount} ${detail?.currency ?: uiState.agentDepositCurrency}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        fontFamily = MulishFontFamily
                                    )
                                }
                                if (detail?.agentRemainingBalance != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Nouveau solde agent", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
                                        Text(
                                            text = "${String.format(java.util.Locale.US, "%,.2f", detail.agentRemainingBalance)} ${detail.currency}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A),
                                            fontFamily = MulishFontFamily
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onReset,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(46.dp)
                            ) {
                                Text("Nouveau Dépôt", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onClose,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(46.dp)
                            ) {
                                Text("Fermer", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // ÉTAPE 1 : IDENTIFY (RECHERCHE CLIENT / SCANNER)
            // ----------------------------------------------------
            else if (step == "identify") {
                Text(
                    text = "Veuillez identifier le compte du client souhaitant déposer des espèces.",
                    fontFamily = MulishFontFamily,
                    fontSize = 13.sp,
                    color = Color(0xFF475569)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.agentDepositClientRef,
                        onValueChange = onClientRefChange,
                        label = { Text("Numéro de téléphone ou Wallet ID") },
                        placeholder = { Text("Ex: 0820000000 ou WALLET-XXXX") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B))
                        },
                        trailingIcon = {
                            if (uiState.agentDepositClientRef.isNotBlank()) {
                                IconButton(onClick = { onClientRefChange("") }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Effacer", tint = Color(0xFF94A3B8))
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onOpenQrScanner,
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanner QR Client",
                            tint = Color(0xFF00C48C)
                        )
                    }
                }

                if (uiState.agentDepositError != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFEF2F2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.agentDepositError,
                                fontFamily = MulishFontFamily,
                                color = Color(0xFFDC2626),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = onSearchClient,
                    enabled = !uiState.isAgentDepositLoading && uiState.agentDepositClientRef.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    if (uiState.isAgentDepositLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rechercher le client", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // ÉTAPE 2 : CONFIRM_CLIENT (CONFIRMATION DU CLIENT TROUVÉ)
            // ----------------------------------------------------
            else if (step == "confirm_client") {
                val client = uiState.agentDepositFoundClient

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color(0xFF0F172A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val initials = (client?.fullName ?: client?.firstName ?: "C")
                                    .split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .joinToString("")
                                Text(
                                    text = initials.ifBlank { "CP" },
                                    color = Color(0xFF00C48C),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    fontFamily = MulishFontFamily
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = client?.fullName ?: client?.firstName ?: "Client CashPay",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = client?.phone ?: "-",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFECFDF5),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "ID: ${client?.walletId ?: uiState.agentDepositClientRef}",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF059669),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Compte client vérifié et actif pour dépôt d'espèces.",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 11.sp,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }
                }

                if (uiState.agentDepositError != null) {
                    Text(
                        text = uiState.agentDepositError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onReset,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Modifier", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    }

                    Button(
                        onClick = onConfirmClient,
                        enabled = !uiState.isAgentDepositLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        if (uiState.isAgentDepositLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Confirmer", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // ÉTAPE 3 : AMOUNT (CHOIX DEVISE & MONTANT)
            // ----------------------------------------------------
            else if (step == "amount") {
                val client = uiState.agentDepositFoundClient
                val returnedBalances = if (uiState.agentDepositAgentBalances.isNotEmpty()) {
                    uiState.agentDepositAgentBalances
                } else if (uiState.agentBalancesMap.isNotEmpty()) {
                    uiState.agentBalancesMap
                } else {
                    mapOf("CDF" to 250000.0, "USD" to 150.0)
                }

                // Mini client banner
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bénéficiaire : ${client?.fullName ?: client?.firstName ?: uiState.agentDepositClientRef}",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                Text(
                    text = "Choisissez la devise selon les disponibilités de votre portefeuille :",
                    fontFamily = MulishFontFamily,
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )

                // Currency selector showing agent's actual balance in each currency
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    returnedBalances.forEach { (curr, bal) ->
                        val isSelected = uiState.agentDepositCurrency == curr
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF059669) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) Color(0xFF059669) else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier.clickable { onCurrencyChange(curr) }
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = curr,
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Solde: ${String.format(java.util.Locale.US, "%,.0f", bal)}",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.agentDepositAmount,
                    onValueChange = onAmountChange,
                    label = { Text("Montant reçu en espèces (${uiState.agentDepositCurrency})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        Text(
                            text = uiState.agentDepositCurrency,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669),
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                )

                // Quick amount chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = if (uiState.agentDepositCurrency == "USD" || uiState.agentDepositCurrency == "EUR") {
                        listOf("10", "20", "50", "100", "200")
                    } else {
                        listOf("5000", "10000", "25000", "50000", "100000")
                    }
                    presets.forEach { amt ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.clickable { onAmountChange(amt) }
                        ) {
                            Text(
                                text = "+$amt ${uiState.agentDepositCurrency}",
                                fontFamily = MulishFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFECFDF5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Frais d'opération : 0.00 ${uiState.agentDepositCurrency}. Le montant débité de votre compte est égal au montant déposé.",
                        fontFamily = MulishFontFamily,
                        fontSize = 11.sp,
                        color = Color(0xFF047857),
                        modifier = Modifier.padding(10.dp)
                    )
                }

                if (uiState.agentDepositError != null) {
                    Text(
                        text = uiState.agentDepositError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onReset,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Retour", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    }

                    Button(
                        onClick = onSubmitAmount,
                        enabled = !uiState.isAgentDepositLoading && (uiState.agentDepositAmount.toDoubleOrNull() ?: 0.0) > 0.0,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.5f).height(48.dp)
                    ) {
                        if (uiState.isAgentDepositLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Continuer", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // ÉTAPE 4 : PIN (RÉCAPITULATIF & CODE PIN AGENT)
            // ----------------------------------------------------
            else if (step == "pin") {
                val detail = uiState.agentDepositDetail

                // Financial recap card strictly populated from server response
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Récapitulatif Financier Fourni par le Serveur",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF0F172A)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Bénéficiaire", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
                            Text(
                                text = detail?.clientName ?: uiState.agentDepositFoundClient?.fullName ?: "Client",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                fontFamily = MulishFontFamily
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Montant du dépôt", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
                            Text(
                                text = "${detail?.depositAmount ?: uiState.agentDepositAmount} ${detail?.currency ?: uiState.agentDepositCurrency}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669),
                                fontFamily = MulishFontFamily
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Frais agent", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
                            Text(
                                text = "0.00 ${detail?.currency ?: uiState.agentDepositCurrency}",
                                fontSize = 12.sp,
                                color = Color(0xFF059669),
                                fontFamily = MulishFontFamily
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total débité agent", fontSize = 12.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
                            Text(
                                text = "${detail?.totalDebitAgent ?: uiState.agentDepositAmount} ${detail?.currency ?: uiState.agentDepositCurrency}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A),
                                fontFamily = MulishFontFamily
                            )
                        }
                        if (detail?.agentBalanceBefore != null && detail.agentBalanceAfter != null) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Votre solde (Avant → Après)", fontSize = 11.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%,.2f", detail.agentBalanceBefore)} → ${String.format(java.util.Locale.US, "%,.2f", detail.agentBalanceAfter)} ${detail.currency}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155),
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.agentDepositPin,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) onPinChange(it) },
                    label = { Text("Code PIN Agent (4 chiffres)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                if (uiState.agentDepositError != null) {
                    Text(
                        text = uiState.agentDepositError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onReset,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Annuler", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    }

                    Button(
                        onClick = onSubmitPin,
                        enabled = !uiState.isAgentDepositLoading && uiState.agentDepositPin.length == 4,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                    ) {
                        if (uiState.isAgentDepositLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Valider le Dépôt", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ====================================================
// --- MODAL: RETRAIT CLIENT (DEMANDE & OTP CLIENT) ---
// ====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentWithdrawModal(
    uiState: DashboardUiState,
    onClose: () -> Unit,
    onClientRefChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onChannelChange: (String) -> Unit,
    onClientOtpChange: (String) -> Unit,
    onInitiate: () -> Unit,
    onSubmitOtp: () -> Unit,
    onReset: () -> Unit,
    onOpenQrScanner: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Retrait demandé par un client",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            if (uiState.agentWithdrawSuccess != null) {
                val res = uiState.agentWithdrawSuccess
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Retrait validé avec succès !",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "Vous pouvez remettre les espèces au client.",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFFB45309)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Réf : ${res.reference ?: "WTH-CONFIRMED"}",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onReset,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Nouveau Retrait", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (uiState.agentWithdrawStep == 1) {
                // Étape 1 : Identification du client
                Text(
                    text = "Identifiez d'abord le compte client à débiter.",
                    fontFamily = MulishFontFamily,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.agentWithdrawClientRef,
                        onValueChange = onClientRefChange,
                        label = { Text("Numéro ou Wallet ID client") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD97706),
                            focusedLabelColor = Color(0xFFD97706)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onOpenQrScanner,
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanner",
                            tint = Color(0xFF00C48C)
                        )
                    }
                }

                if (uiState.agentWithdrawError != null) {
                    Text(
                        text = uiState.agentWithdrawError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onInitiate, // Step 1 -> Step 2 (Search)
                    enabled = !uiState.isAgentWithdrawLoading && uiState.agentWithdrawClientRef.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) {
                    if (uiState.isAgentWithdrawLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Vérifier l'utilisateur", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (uiState.agentWithdrawStep == 2) {
                // Étape 2 : Saisie du Montant
                val client = uiState.agentWithdrawFoundClient
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7).copy(alpha = 0.5f)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFD97706))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = client?.fullName ?: "Client CashPay", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Wallet ID: ${client?.walletId ?: uiState.agentWithdrawClientRef}", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("USD", "CDF", "EUR").forEach { curr ->
                        val selected = uiState.agentWithdrawCurrency == curr
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (selected) Color(0xFFD97706) else Color(0xFFF1F5F9),
                            modifier = Modifier.clickable { onCurrencyChange(curr) }
                        ) {
                            Text(
                                text = curr,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (selected) Color.White else Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.agentWithdrawAmount,
                    onValueChange = onAmountChange,
                    label = { Text("Montant à retirer") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD97706),
                        focusedLabelColor = Color(0xFFD97706)
                    )
                )

                if (uiState.agentWithdrawError != null) {
                    Text(
                        text = uiState.agentWithdrawError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onInitiate, // Step 2 -> Step 3 (Submit Amount)
                    enabled = !uiState.isAgentWithdrawLoading && uiState.agentWithdrawAmount.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) {
                    if (uiState.isAgentWithdrawLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Continuer vers le canal", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (uiState.agentWithdrawStep == 3) {
                uiState.agentWithdrawPreview?.let { quote -> Text("Montant : ${quote.amount} ${quote.currency} · Frais : ${quote.fee} · Total débité du client : ${quote.totalDebit}") }
                // Étape 3 : Choix du Canal
                Text(
                    text = "Choisissez le canal pour envoyer l'OTP au client :",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )

                val channels = uiState.agentWithdrawOtpChannels.ifEmpty { listOf("sms", "whatsapp", "email") }
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    channels.forEach { code ->
                        val selected = uiState.agentWithdrawChannel == code
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onChannelChange(code) },
                            border = BorderStroke(1.dp, if (selected) Color(0xFF0F172A) else Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (code == "whatsapp") Icons.Default.Chat else if (code == "sms") Icons.Default.Sms else Icons.Default.Smartphone,
                                    contentDescription = null,
                                    tint = if (selected) Color.White else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = when(code) {
                                        "whatsapp" -> "WhatsApp"
                                        "sms" -> "SMS"
                                        "app" -> "Notification App"
                                        else -> code.uppercase()
                                    },
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (selected) Color.White else Color(0xFF334155)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (selected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00C48C), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                if (uiState.agentWithdrawError != null) {
                    Text(
                        text = uiState.agentWithdrawError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onInitiate, // Step 3 -> Step 4 (Initiate)
                    enabled = !uiState.isAgentWithdrawLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) {
                    if (uiState.isAgentWithdrawLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Envoyer l'OTP au Client", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Étape 4 : Saisie de l'OTP client
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Demande en attente d'OTP",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "Le client doit vous fournir le code reçu par ${uiState.agentWithdrawChannel.uppercase()}.",
                            fontFamily = MulishFontFamily,
                            fontSize = 11.sp,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                OutlinedTextField(
                    value = uiState.agentWithdrawClientOtp,
                    onValueChange = onClientOtpChange,
                    label = { Text("Code OTP du Client") },
                    placeholder = { Text("Saisissez les 6 chiffres") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD97706),
                        focusedLabelColor = Color(0xFFD97706)
                    )
                )

                if (uiState.agentWithdrawError != null) {
                    Text(
                        text = uiState.agentWithdrawError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onSubmitOtp,
                    enabled = !uiState.isAgentWithdrawLoading && uiState.agentWithdrawClientOtp.length >= 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) {
                    if (uiState.isAgentWithdrawLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Confirmer le Retrait", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ====================================================
// --- MODAL: PRÊT LOAN ME ---
// ====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentLoanModal(
    uiState: DashboardUiState,
    onClose: () -> Unit,
    onClientRefChange: (String) -> Unit,
    onSearchTarget: () -> Unit,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    onOpenQrScanner: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remboursement Prêt Loan Me",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            if (uiState.agentLoanRepaySuccess != null) {
                val res = uiState.agentLoanRepaySuccess
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Prêt remboursé avec succès !",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF5B21B6)
                        )
                        Text(
                            text = res.message ?: "Le prêt du client a été remboursé.",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF6D28D9)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onReset,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Nouveau Remboursement", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.agentLoanClientRef,
                        onValueChange = onClientRefChange,
                        label = { Text("Numéro ou Wallet ID client") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onOpenQrScanner,
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanner",
                            tint = Color(0xFF00C48C)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onSearchTarget,
                        enabled = !uiState.isAgentSearchingLoan,
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF7C3AED), RoundedCornerShape(8.dp))
                    ) {
                        if (uiState.isAgentSearchingLoan) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Rechercher", tint = Color.White)
                        }
                    }
                }

                if (uiState.agentLoanSearchError != null) {
                    Text(
                        text = uiState.agentLoanSearchError,
                        fontFamily = MulishFontFamily,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp
                    )
                }

                val target = uiState.agentLoanTarget
                if (target != null && target.loan == null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Client : ${target.client?.name ?: "Client CashPay"}",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Ce client n'a aucun prêt en cours ou aucune dette à rembourser.",
                                fontFamily = MulishFontFamily,
                                fontSize = 12.5.sp,
                                color = Color(0xFF64748B),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    onReset()
                                    onClose()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B))
                            ) {
                                Text("Fermer", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (target?.loan != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF3E8FF)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Client : ${target.client?.name ?: "Client CashPay"}",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF4C1D95)
                            )
                            Text(
                                text = "Solde restant : ${String.format(java.util.Locale.US, "%.2f", target.loan.remainingBalance ?: 0.0)} ${target.loan.currency}",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color(0xFF7C3AED)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = uiState.agentLoanAmount,
                        onValueChange = onAmountChange,
                        label = { Text("Montant à rembourser (${uiState.agentLoanCurrency})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    uiState.agentLoanFee?.let { fee -> Text("Frais : $fee ${uiState.agentLoanCurrency} · Débit agent : ${uiState.agentLoanTotalDebit} ${uiState.agentLoanCurrency}") }
                    OutlinedTextField(
                        value = uiState.agentLoanPin,
                        onValueChange = { if (it.length <= 4) onPinChange(it) },
                        label = { Text("Code PIN Agent (4 chiffres)") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (uiState.agentLoanRepayError != null) {
                        Text(
                            text = uiState.agentLoanRepayError,
                            fontFamily = MulishFontFamily,
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onSubmit,
                        enabled = !uiState.isAgentLoanRepayLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                    ) {
                        if (uiState.isAgentLoanRepayLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text(if(uiState.agentLoanFee==null) "Vérifier le montant et les frais" else "Confirmer le Remboursement", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        onReset()
                        onClose()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Annuler / Sortir", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                }
            }
        }
    }
}

// ====================================================
// --- MODAL: HISTORIQUE AGENT ---
// ====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentHistoryModal(
    history: List<AgentOperationRecord>,
    onClose: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historique des Opérations Agent",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aucune opération agent enregistrée",
                            fontFamily = MulishFontFamily,
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history, key = { it.id }) { op ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = op.title,
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = if (!op.clientRef.isNullOrBlank()) "${op.clientRef} • ${op.date}" else op.date,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${String.format(java.util.Locale.US, "%,.2f", op.amount)} ${op.currency}",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    if (op.commission > 0.0) {
                                        Text(
                                            text = "+${String.format(java.util.Locale.US, "%.2f", op.commission)} comm.",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = Color(0xFF059669)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ====================================================
// --- MODAL: BASCULER COMMISSION VERS COMPTE PRINCIPAL ---
// ====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SweepCommissionModalDialog(
    uiState: DashboardUiState,
    onClose: () -> Unit,
    onCurrencyChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onIsAllChange: (Boolean) -> Unit,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val available = when (uiState.sweepCurrency.uppercase()) {
        "CDF" -> uiState.agentCommissionCdf
        "EUR" -> uiState.agentCommissionEur
        else -> uiState.agentCommissionUsd
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF00C48C).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = Color(0xFF00C48C),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Basculer vers Compte Principal",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Text(
                text = "Sélectionnez la devise de la commission",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF475569)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("USD", "CDF", "EUR").forEach { curr ->
                    val selected = uiState.sweepCurrency == curr
                    val currAvailable = when (curr) {
                        "CDF" -> uiState.agentCommissionCdf
                        "EUR" -> uiState.agentCommissionEur
                        else -> uiState.agentCommissionUsd
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onCurrencyChange(curr) }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = curr,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (selected) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.1f", currAvailable)}",
                                fontFamily = MulishFontFamily,
                                fontSize = 10.sp,
                                color = if (selected) Color(0xFF00C48C) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Commission disponible :",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF166534)
                    )
                    Text(
                        text = "${String.format(java.util.Locale.US, "%,.2f", available)} ${uiState.sweepCurrency}",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color(0xFF15803D)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (uiState.sweepIsAll) Color(0xFF00C48C) else Color(0xFFE2E8F0),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onIsAllChange(true) }
                ) {
                    Text(
                        text = "Tout basculer (100%)",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (uiState.sweepIsAll) Color.White else Color(0xFF334155),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (!uiState.sweepIsAll) Color(0xFF00C48C) else Color(0xFFE2E8F0),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onIsAllChange(false) }
                ) {
                    Text(
                        text = "Montant personnalisé",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (!uiState.sweepIsAll) Color.White else Color(0xFF334155),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }

            if (!uiState.sweepIsAll) {
                OutlinedTextField(
                    value = uiState.sweepAmount,
                    onValueChange = onAmountChange,
                    label = { Text("Montant à basculer (${uiState.sweepCurrency})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Text(
                text = "✓ Déplacement instantané de la commission vers votre solde principal ${uiState.sweepCurrency} correspondant, sans conversion de devise.",
                fontFamily = MulishFontFamily,
                fontSize = 11.sp,
                color = Color(0xFF059669)
            )

            OutlinedTextField(
                value = uiState.sweepPin,
                onValueChange = { if (it.length <= 4) onPinChange(it) },
                label = { Text("Confirmez avec votre code PIN (4 chiffres)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            if (uiState.sweepCommissionError != null) {
                Text(
                    text = uiState.sweepCommissionError,
                    fontFamily = MulishFontFamily,
                    color = Color(0xFFDC2626),
                    fontSize = 12.sp
                )
            }

            if (uiState.sweepCommissionSuccess != null) {
                Text(
                    text = uiState.sweepCommissionSuccess,
                    fontFamily = MulishFontFamily,
                    color = Color(0xFF059669),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onSubmit,
                enabled = !uiState.isSweepingCommission && available > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C48C))
            ) {
                if (uiState.isSweepingCommission) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text(
                        text = "Confirmer la Bascule",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
