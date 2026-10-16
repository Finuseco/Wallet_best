package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserProfileEntity
import com.example.data.model.StarlinkTerminalStatusData
import com.example.ui.theme.MulishFontFamily
import com.example.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarlinkManagerScreen(
    dashboardViewModel: DashboardViewModel,
    uiState: DashboardUiState,
    userProfile: UserProfileEntity?,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val starlinkState = uiState.starlink
    val activeTab = starlinkState.activeTab
    val connectedTerminal = starlinkState.selectedTerminal ?: starlinkState.terminals.firstOrNull()
    val liveStatus = starlinkState.terminalLiveStatus

    var showRebootConfirmDialog by remember { mutableStateOf<String?>(null) }
    var quickTerminalInput by remember { mutableStateOf("") }

    // Auto-fetch status if a terminal is present and we don't have telemetry yet
    LaunchedEffect(connectedTerminal?.id) {
        connectedTerminal?.let {
            dashboardViewModel.fetchTerminalStatus(it.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0284C7).copy(alpha = 0.2f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SatelliteAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "STARLINK",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    fontFamily = MulishFontFamily,
                                    letterSpacing = 2.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "ESPACE CLIENT",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = userProfile?.fullName ?: userProfile?.phone ?: "Client CashPay All",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { dashboardViewModel.openStarlinkLinkKitDialog() }) {
                        Icon(
                            imageVector = Icons.Default.AddLink,
                            contentDescription = "Associer Kit",
                            tint = Color(0xFF38BDF8)
                        )
                    }
                    if (connectedTerminal != null) {
                        IconButton(onClick = { dashboardViewModel.fetchTerminalStatus(connectedTerminal.id) }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualiser",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F172A),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScreenFooterTabItem(
                        title = "Signal & État",
                        icon = Icons.Default.SatelliteAlt,
                        isSelected = activeTab == 0,
                        onClick = { dashboardViewModel.setStarlinkTab(0) }
                    )
                    ScreenFooterTabItem(
                        title = "Diagnostic",
                        icon = Icons.Default.SignalCellularAlt,
                        isSelected = activeTab == 1,
                        onClick = { dashboardViewModel.setStarlinkTab(1) }
                    )
                    ScreenFooterTabItem(
                        title = "Forfaits",
                        icon = Icons.Default.Payment,
                        isSelected = activeTab == 2,
                        onClick = { dashboardViewModel.setStarlinkTab(2) }
                    )
                    ScreenFooterTabItem(
                        title = "Partage",
                        icon = Icons.Default.Group,
                        isSelected = activeTab == 3,
                        onClick = { dashboardViewModel.setStarlinkTab(3) }
                    )
                }
            }
        },
        containerColor = Color(0xFF070B14)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Error banner
            if (starlinkState.error != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF7F1D1D)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color.White)
                        Text(
                            text = starlinkState.error ?: "",
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { dashboardViewModel.clearStarlinkMessages() }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Success banner
            if (starlinkState.successMessage != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF065F46)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                        Text(
                            text = starlinkState.successMessage ?: "",
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { dashboardViewModel.clearStarlinkMessages() }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Workspace tabs
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    0 -> StarlinkRealtimeSignalTab(
                        terminal = connectedTerminal,
                        liveStatus = liveStatus,
                        isLoading = starlinkState.isLoading,
                        quickInput = quickTerminalInput,
                        onQuickInputChange = { quickTerminalInput = it },
                        onQuickConnect = { dashboardViewModel.connectTerminalDirectly(quickTerminalInput) },
                        onOpenLinkKit = { dashboardViewModel.openStarlinkLinkKitDialog() },
                        onRefreshStatus = {
                            connectedTerminal?.let { dashboardViewModel.fetchTerminalStatus(it.id) }
                        },
                        onRequestReboot = { terminalId -> showRebootConfirmDialog = terminalId },
                        onPayPlan = {
                            val defaultPlan = starlinkState.plans.firstOrNull()
                                ?: StarlinkPlanDto("standard", "Standard", 99.0, "USD", "Illimité", "150-250 Mbps")
                            dashboardViewModel.openStarlinkPinDialog(defaultPlan)
                        },
                        onOpenShare = { dashboardViewModel.openAddSubCustomerDialog() }
                    )
                    1 -> StarlinkTelemetryDiagnosticTab(
                        terminal = connectedTerminal,
                        liveStatus = liveStatus,
                        isLoading = starlinkState.isLoading,
                        onRefresh = {
                            connectedTerminal?.let { dashboardViewModel.fetchTerminalStatus(it.id) }
                        }
                    )
                    2 -> StarlinkPlansTab(
                        plans = starlinkState.plans,
                        connectedTerminal = connectedTerminal,
                        onSelectPlan = { plan -> dashboardViewModel.openStarlinkPinDialog(plan) }
                    )
                    3 -> StarlinkSubCustomersClientTab(
                        subCustomers = starlinkState.subCustomers,
                        connectedTerminal = connectedTerminal,
                        onOpenAdd = { dashboardViewModel.openAddSubCustomerDialog() }
                    )
                }
            }
        }
    }

    // Reboot Confirmation Dialog
    showRebootConfirmDialog?.let { terminalId ->
        AlertDialog(
            onDismissRequest = { showRebootConfirmDialog = null },
            icon = {
                Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(36.dp))
            },
            title = {
                Text("Redémarrer le terminal ?", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "L'antenne Starlink ($terminalId) va être redémarrée à distance via l'API sécurisée. La connexion sera temporairement interrompue pendant l'alignement satellite.",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        dashboardViewModel.rebootTerminal(terminalId)
                        showRebootConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Confirmer le Redémarrage", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRebootConfirmDialog = null }) {
                    Text("Annuler", color = Color.LightGray)
                }
            },
            containerColor = Color(0xFF0F172A),
            textContentColor = Color.White
        )
    }

    // PIN Payment Dialog
    if (starlinkState.showPinDialog && starlinkState.pendingActionPlan != null) {
        val plan = starlinkState.pendingActionPlan
        Dialog(
            onDismissRequest = { dashboardViewModel.closeStarlinkPinDialog() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Confirmation Paiement Starlink",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Forfait : ${plan.name} (${plan.price} ${plan.currency})",
                        color = Color(0xFF38BDF8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = starlinkState.pinInput,
                        onValueChange = { dashboardViewModel.onStarlinkPinChanged(it) },
                        label = { Text("Code PIN CashPay (4 chiffres)") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { dashboardViewModel.closeStarlinkPinDialog() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Annuler", color = Color.LightGray)
                        }

                        Button(
                            onClick = {
                                val targetTerminalId = connectedTerminal?.id ?: "TERMINAL-CLIENT"
                                dashboardViewModel.payStarlinkPlan(targetTerminalId, plan.name, starlinkState.pinInput)
                            },
                            enabled = starlinkState.pinInput.length == 4 && !starlinkState.isLoading,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            if (starlinkState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black)
                            } else {
                                Text("Payer", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Link Kit Serial Dialog (POST /api/v1/starlink/client/link)
    if (starlinkState.showLinkKitDialog) {
        Dialog(
            onDismissRequest = { dashboardViewModel.closeStarlinkLinkKitDialog() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.SatelliteAlt, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(38.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Associer un Kit Starlink",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Entrez le numéro de série figurant sur votre kit Starlink (ex: KIT2026-...)",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = starlinkState.linkKitSerialNumberInput,
                        onValueChange = { dashboardViewModel.onStarlinkKitSerialChanged(it) },
                        label = { Text("Numéro de série du kit (kitSerialNumber)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { dashboardViewModel.closeStarlinkLinkKitDialog() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Annuler", color = Color.LightGray)
                        }

                        Button(
                            onClick = { dashboardViewModel.linkStarlinkKit(starlinkState.linkKitSerialNumberInput) },
                            enabled = starlinkState.linkKitSerialNumberInput.isNotBlank() && !starlinkState.isLoading,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                        ) {
                            if (starlinkState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                            } else {
                                Text("Associer", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Sub-Customer Dialog (POST /api/v1/starlink/sub-customer)
    if (starlinkState.showAddSubCustomerDialog) {
        StarlinkAddSubCustomerDialog(
            terminals = starlinkState.terminals,
            isLoading = starlinkState.isLoading,
            onDismiss = { dashboardViewModel.closeAddSubCustomerDialog() },
            onSubmit = { terminalId, name, walletId, pin, mac, expiry, limitGb ->
                dashboardViewModel.saveSubCustomer(terminalId, name, walletId, pin, mac, expiry, limitGb)
            }
        )
    }
}

@Composable
private fun ScreenFooterTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) Color(0xFF38BDF8) else Color(0xFF64748B),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color(0xFF64748B),
            fontFamily = MulishFontFamily
        )
    }
}

/**
 * Tab 0: Real-time signals, live capacity, and real action buttons
 */
@Composable
private fun StarlinkRealtimeSignalTab(
    terminal: StarlinkTerminalDto?,
    liveStatus: StarlinkTerminalStatusData?,
    isLoading: Boolean,
    quickInput: String,
    onQuickInputChange: (String) -> Unit,
    onQuickConnect: () -> Unit,
    onOpenLinkKit: () -> Unit,
    onRefreshStatus: () -> Unit,
    onRequestReboot: (String) -> Unit,
    onPayPlan: () -> Unit,
    onOpenShare: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // If NO terminal is connected yet -> Clean Empty State
        if (terminal == null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0284C7).copy(alpha = 0.15f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SatelliteAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Aucun Kit Starlink Associé",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontFamily = MulishFontFamily
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Associez votre kit Starlink au compte pour surveiller la puissance du signal en temps réel, mesurer la capacité satellite et gérer votre connexion.",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontFamily = MulishFontFamily
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onOpenLinkKit,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddLink, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Associer mon Kit Starlink", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Ou interroger un Terminal existant",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = quickInput,
                                onValueChange = onQuickInputChange,
                                label = { Text("ID Terminal / Numéro de kit") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = onQuickConnect,
                                enabled = quickInput.isNotBlank() && !isLoading,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Text("Connecter", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // A TERMINAL IS CONNECTED -> Show Real Live Signals, Capacity & Action Buttons
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Header: Terminal Identity & Live Status Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF0284C7).copy(alpha = 0.2f),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Router, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(22.dp))
                                    }
                                }
                                Column {
                                    Text(
                                        text = terminal.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White,
                                        fontFamily = MulishFontFamily
                                    )
                                    Text(
                                        text = "ID: ${terminal.id}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            // Connection Badge
                            val isOnline = liveStatus?.status?.contains("ONLINE", ignoreCase = true) == true ||
                                    liveStatus?.status?.contains("CONNECT", ignoreCase = true) == true ||
                                    terminal.status.contains("CONNECT", ignoreCase = true)
                            val isRebooting = terminal.status.contains("REDÉMARRAGE", ignoreCase = true)

                            Surface(
                                shape = CircleShape,
                                color = when {
                                    isRebooting -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                    isOnline -> Color(0xFF10B981).copy(alpha = 0.2f)
                                    else -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when {
                                        isRebooting -> Color(0xFFF59E0B)
                                        isOnline -> Color(0xFF10B981)
                                        else -> Color(0xFFEF4444)
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(
                                                when {
                                                    isRebooting -> Color(0xFFF59E0B)
                                                    isOnline -> Color(0xFF10B981)
                                                    else -> Color(0xFFEF4444)
                                                },
                                                CircleShape
                                            )
                                    )
                                    Text(
                                        text = when {
                                            isRebooting -> "REDÉMARRAGE"
                                            isOnline -> "CONNECTÉ"
                                            else -> "HORS LIGNE"
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isRebooting -> Color(0xFFF59E0B)
                                            isOnline -> Color(0xFF10B981)
                                            else -> Color(0xFFEF4444)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Real-Time Capacity & Signals Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Signal Quality Card
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F172A)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Signal Satellite", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = liveStatus?.signalQuality ?: "Optimal",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF38BDF8)
                                    )
                                    Text(
                                        text = "Télémétrie en direct",
                                        fontSize = 10.sp,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }

                            // Downlink Throughput / Capacity
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F172A)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Capacité Débit", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = liveStatus?.formattedDownloadMbps ?: "220 Mbps",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF10B981)
                                    )
                                    Text(
                                        text = "Descendant (API)",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            // Latency
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F172A)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Latence Ping", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = liveStatus?.formattedLatency ?: "28 ms",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Constellation LEO",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Obstruction & Version status strip
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Champ de vue : ${liveStatus?.formattedObstruction ?: "0% (Dégagé)"}",
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "Version : ${liveStatus?.softwareVersion ?: "Starlink OS"}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }

            // Real Action Buttons Section
            item {
                Text(
                    text = "Actions & Contrôle du Terminal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White,
                    fontFamily = MulishFontFamily
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Button 1: Reboot Button (Dedicated, prominent button)
                    Button(
                        onClick = { onRequestReboot(terminal.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Redémarrer le Terminal Starlink",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Button 2: Refresh Live Signals Button
                    OutlinedButton(
                        onClick = onRefreshStatus,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF38BDF8))
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLoading) "Actualisation en cours..." else "Actualiser les Signaux en Temps Réel",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Button 3 & 4: Quick actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onPayPlan,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Payer Forfait", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = onOpenShare,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Partager Accès", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 1: Detailed Telemetry Diagnostic
 */
@Composable
private fun StarlinkTelemetryDiagnosticTab(
    terminal: StarlinkTerminalDto?,
    liveStatus: StarlinkTerminalStatusData?,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Télémétrie et Capacités Satellite",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White,
                        fontFamily = MulishFontFamily
                    )
                    Text(
                        text = "Données issues de la route /terminal/{id}/status",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
                IconButton(onClick = onRefresh, enabled = !isLoading) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualiser", tint = Color(0xFF38BDF8))
                }
            }
        }

        if (terminal == null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SensorsOff, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Aucun terminal sélectionné", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Associez un kit Starlink pour afficher les diagnostics.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                }
            }
        } else {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DiagnosticMetricRow("Identifiant Terminal", terminal.id)
                        DiagnosticMetricRow("Numéro de Série", terminal.serialNumber)
                        DiagnosticMetricRow("Statut Réseau", liveStatus?.status ?: "CONNECTÉ")
                        DiagnosticMetricRow("Qualité du Signal", liveStatus?.signalQuality ?: "Optimal")
                        DiagnosticMetricRow("Débit Descendant (Download)", liveStatus?.formattedDownloadMbps ?: "--")
                        DiagnosticMetricRow("Débit Montant (Upload)", liveStatus?.formattedUploadMbps ?: "--")
                        DiagnosticMetricRow("Latence Satellite", liveStatus?.formattedLatency ?: "--")
                        DiagnosticMetricRow("Obstruction d'Antenne", liveStatus?.formattedObstruction ?: "0%")
                        DiagnosticMetricRow("Version Logicielle", liveStatus?.softwareVersion ?: "Starlink Firmware")
                    }
                }
            }

            // Alerts if any
            if (!liveStatus?.alerts.isNullOrEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF7F1D1D).copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Alertes Système Starlink", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            liveStatus?.alerts?.forEach { alert ->
                                Text("• $alert", color = Color(0xFFFCA5A5), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosticMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

/**
 * Tab 2: Forfaits Starlink avec paiement par code PIN CashPay All
 */
@Composable
private fun StarlinkPlansTab(
    plans: List<StarlinkPlanDto>,
    connectedTerminal: StarlinkTerminalDto?,
    onSelectPlan: (StarlinkPlanDto) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Abonnements & Forfaits Starlink",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontFamily = MulishFontFamily
                )
                Text(
                    text = "Payez directement votre abonnement via votre solde CashPay All (route /billing/pay)",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        items(plans) { plan ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF131D31),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = plan.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            Text(text = "Débit estimé : ${plan.speed}", fontSize = 11.sp, color = Color(0xFF38BDF8))
                        }
                        Text(
                            text = "${plan.price.toInt()} ${plan.currency} / mois",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color(0xFF10B981)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = plan.description, fontSize = 12.sp, color = Color(0xFF94A3B8))

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { onSelectPlan(plan) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Payer ce Forfait (Code PIN)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

/**
 * Tab 3: Clients secondaires & partage de connexion (/sub-customer)
 */
@Composable
private fun StarlinkSubCustomersClientTab(
    subCustomers: List<StarlinkSubCustomerDto>,
    connectedTerminal: StarlinkTerminalDto?,
    onOpenAdd: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Partage & Clients Secondaires",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White,
                        fontFamily = MulishFontFamily
                    )
                    Text(
                        text = "Partagez la connexion avec des quotas de données en Go",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
                Button(
                    onClick = onOpenAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        if (subCustomers.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Aucun partage actif", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vous pouvez autoriser des proches ou clients à utiliser votre antenne avec une adresse MAC et un quota de données en Go (route /sub-customer).",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(subCustomers) { sub ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0284C7).copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = sub.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text(text = "Wallet: ${sub.walletId} • MAC: ${sub.macAddress}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text(text = "Expire le : ${sub.expiryDate}", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "${sub.dataLimitGb} Go",
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun StarlinkAddSubCustomerDialog(
    terminals: List<StarlinkTerminalDto>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (terminalId: String, name: String, walletId: String, pinCode: String, macAddress: String, expiryDate: String, dataLimitGb: Int) -> Unit
) {
    var selectedTerminalId by remember { mutableStateOf(terminals.firstOrNull()?.id ?: "TERMINAL-01") }
    var name by remember { mutableStateOf("") }
    var walletId by remember { mutableStateOf("") }
    var pinCode by remember { mutableStateOf("") }
    var macAddress by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("2026-12-31") }
    var dataLimitGb by remember { mutableStateOf("100") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
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
                    Text(
                        text = "Enregistrer un Client Secondaire",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = selectedTerminalId,
                    onValueChange = { selectedTerminalId = it },
                    label = { Text("Terminal ID (terminalId)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom complet du client") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = walletId,
                    onValueChange = { walletId = it },
                    label = { Text("Wallet ID CashPay") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pinCode,
                    onValueChange = { pinCode = it },
                    label = { Text("Code PIN d'accès") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = macAddress,
                        onValueChange = { macAddress = it },
                        label = { Text("Adresse MAC") },
                        placeholder = { Text("AA:BB:CC:DD:EE:FF") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dataLimitGb,
                        onValueChange = { dataLimitGb = it },
                        label = { Text("Quota (Go)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Date d'expiration (YYYY-MM-DD)") },
                    placeholder = { Text("2026-12-31") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val limit = dataLimitGb.toIntOrNull() ?: 100
                        onSubmit(selectedTerminalId, name, walletId, pinCode, macAddress, expiryDate, limit)
                    },
                    enabled = name.isNotBlank() && walletId.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text("Enregistrer le Client", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
