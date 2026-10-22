package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
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

    // Auto-fetch real-time telemetry when a terminal is active
    LaunchedEffect(connectedTerminal?.id) {
        connectedTerminal?.let {
            dashboardViewModel.fetchTerminalStatus(it.id)
        }
    }

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // TOP BAR COMPACTE EN PLEIN ÉCRAN (Bande haute touchant le réseau/status bar, en BLEU PRIMAIRE SANS BANDE NOIRE)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF000E38),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0A1E5C),
                                    Color(0xFF000E38)
                                )
                            )
                        )
                        .padding(top = statusBarHeight)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Retour",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF0284C7).copy(alpha = 0.2f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SatelliteAlt,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(20.dp)
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(onClick = { dashboardViewModel.openStarlinkLinkKitDialog() }) {
                                Icon(
                                    imageVector = Icons.Default.AddLink,
                                    contentDescription = "Associer Kit",
                                    tint = Color(0xFF38BDF8)
                                )
                            }
                            if (connectedTerminal != null) {
                                IconButton(
                                    onClick = { dashboardViewModel.fetchTerminalStatus(connectedTerminal.id) },
                                    enabled = !starlinkState.isLoading
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Actualiser",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Body Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF070B14))
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

                    // Workspace tabs (Client Space Only)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (activeTab) {
                            0 -> StarlinkRealtimeSignalsAndControlsTab(
                                terminal = connectedTerminal,
                                liveStatus = liveStatus,
                                isLoading = starlinkState.isLoading,
                                isRebooting = starlinkState.isRebooting,
                                lastUpdate = starlinkState.lastTelemetryUpdate,
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
                            1 -> StarlinkPlansTab(
                                plans = starlinkState.plans,
                                connectedTerminal = connectedTerminal,
                                onSelectPlan = { plan -> dashboardViewModel.openStarlinkPinDialog(plan) }
                            )
                            2 -> StarlinkSubCustomersClientTab(
                                subCustomers = starlinkState.subCustomers,
                                connectedTerminal = connectedTerminal,
                                onOpenAdd = { dashboardViewModel.openAddSubCustomerDialog() }
                            )
                        }
                    }
                }
            }

            // Bottom Bar Footer Navigation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = Color(0xFF000E38),
                shadowElevation = 10.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2A44))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 6.dp)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScreenFooterTabItem(
                        title = "Signaux",
                        icon = Icons.Default.SatelliteAlt,
                        isSelected = activeTab == 0,
                        onClick = { dashboardViewModel.setStarlinkTab(0) },
                        modifier = Modifier.weight(1f)
                    )
                    ScreenFooterTabItem(
                        title = "Forfaits",
                        icon = Icons.Default.Payment,
                        isSelected = activeTab == 1,
                        onClick = { dashboardViewModel.setStarlinkTab(1) },
                        modifier = Modifier.weight(1f)
                    )
                    ScreenFooterTabItem(
                        title = "Partage",
                        icon = Icons.Default.Group,
                        isSelected = activeTab == 2,
                        onClick = { dashboardViewModel.setStarlinkTab(2) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

    // Reboot / Start Confirmation Dialog
    showRebootConfirmDialog?.let { terminalId ->
        AlertDialog(
            onDismissRequest = { showRebootConfirmDialog = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Démarrer le redémarrage du terminal ?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "Une commande de redémarrage à chaud sera envoyée au terminal Starlink ($terminalId) via la route POST /api/v1/starlink/terminal/{id}/reboot. L'antenne va réaligner ses signaux satellites.",
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
                    Text("Démarrer le Redémarrage", color = Color.White, fontWeight = FontWeight.Bold)
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
                        text = "Entrez le numéro de série figurant sur votre kit Starlink (ex: KIT2026-...) pour associer votre antenne.",
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

        // Bottom Bar Footer Navigation
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = Color(0xFF000E38),
            shadowElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2A44))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 6.dp)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScreenFooterTabItem(
                    title = "Signaux",
                    icon = Icons.Default.SatelliteAlt,
                    isSelected = activeTab == 0,
                    onClick = { dashboardViewModel.setStarlinkTab(0) },
                    modifier = Modifier.weight(1f)
                )
                ScreenFooterTabItem(
                    title = "Forfaits",
                    icon = Icons.Default.Payment,
                    isSelected = activeTab == 1,
                    onClick = { dashboardViewModel.setStarlinkTab(1) },
                    modifier = Modifier.weight(1f)
                )
                ScreenFooterTabItem(
                    title = "Partage",
                    icon = Icons.Default.Group,
                    isSelected = activeTab == 2,
                    onClick = { dashboardViewModel.setStarlinkTab(2) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Reboot / Start Confirmation Dialog
    showRebootConfirmDialog?.let { terminalId ->
        AlertDialog(
            onDismissRequest = { showRebootConfirmDialog = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Démarrer le redémarrage du terminal ?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "Une commande de redémarrage à chaud sera envoyée au terminal Starlink ($terminalId) via la route POST /api/v1/starlink/terminal/{id}/reboot. L'antenne va réaligner ses signaux satellites.",
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
                    Text("Démarrer le Redémarrage", color = Color.White, fontWeight = FontWeight.Bold)
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
                        text = "Entrez le numéro de série figurant sur votre kit Starlink (ex: KIT2026-...) pour associer votre antenne.",
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
}

@Composable
private fun ScreenFooterTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) Color(0xFF38BDF8).copy(alpha = 0.18f) else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF94A3B8),
            fontFamily = MulishFontFamily,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Tab 0: Real-time signals, capacity from API, and product action buttons (Démarrage, etc.)
 */
@Composable
private fun StarlinkRealtimeSignalsAndControlsTab(
    terminal: StarlinkTerminalDto?,
    liveStatus: StarlinkTerminalStatusData?,
    isLoading: Boolean,
    isRebooting: Boolean,
    lastUpdate: String?,
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
        // If NO terminal is connected yet -> Prompt clean association
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
                            text = "Connexion Antenne Starlink",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontFamily = MulishFontFamily
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Associez votre kit Starlink pour afficher les signaux réels, la capacité du réseau et contrôler votre antenne via les boutons de commande.",
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

                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Ou saisir l'identifiant de votre antenne",
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
                                label = { Text("Numéro / ID Terminal") },
                                placeholder = { Text("ex: ut01000000-...") },
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
            // TERMINAL CONNECTED -> Show Real Live Signals, Capacity & Action Buttons
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Header: Terminal Identity & Status Badge
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
                                        text = "Terminal : ${terminal.id}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            // Connection Badge
                            val statusStr = liveStatus?.status ?: terminal.status
                            val isOnline = statusStr.contains("ONLINE", ignoreCase = true) ||
                                    statusStr.contains("CONNECT", ignoreCase = true) ||
                                    statusStr.contains("ACTIF", ignoreCase = true)
                            val isCurrentlyRebooting = isRebooting || statusStr.contains("REDÉMARRAGE", ignoreCase = true)

                            Surface(
                                shape = CircleShape,
                                color = when {
                                    isCurrentlyRebooting -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                    isOnline -> Color(0xFF10B981).copy(alpha = 0.2f)
                                    else -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when {
                                        isCurrentlyRebooting -> Color(0xFFF59E0B)
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
                                                    isCurrentlyRebooting -> Color(0xFFF59E0B)
                                                    isOnline -> Color(0xFF10B981)
                                                    else -> Color(0xFFEF4444)
                                                },
                                                CircleShape
                                            )
                                    )
                                    Text(
                                        text = when {
                                            isCurrentlyRebooting -> "REDÉMARRAGE"
                                            isOnline -> "CONNECTÉ"
                                            else -> "HORS LIGNE"
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isCurrentlyRebooting -> Color(0xFFF59E0B)
                                            isOnline -> Color(0xFF10B981)
                                            else -> Color(0xFFEF4444)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Real-Time Capacity & Signals Grid (Real values directly from API)
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
                                        text = liveStatus?.displaySignalQuality ?: "--",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF38BDF8)
                                    )
                                    Text(
                                        text = if (isLoading) "Mesure..." else "Signal direct",
                                        fontSize = 10.sp,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }

                            // Downlink Throughput / Capacity from API
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F172A)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Capacité Débit", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = liveStatus?.formattedDownloadMbps ?: "--",
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

                            // Latency from API
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F172A)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Latence Ping", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = liveStatus?.formattedLatency ?: "--",
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

                        // Secondary Telemetry Row: Upload, Obstruction & Firmware
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Obstruction : ${liveStatus?.formattedObstruction ?: "--"}",
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "Montant : ${liveStatus?.formattedUploadMbps ?: "--"}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (lastUpdate != null) "Reçu à $lastUpdate" else "",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }

            // Real Action Buttons & Start / Reboot Section
            item {
                Text(
                    text = "Boutons de Contrôle & Démarrage",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White,
                    fontFamily = MulishFontFamily
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Button 1: BOUTON DE DÉMARRAGE / REDÉMARRAGE (Explicit requested Start/Reboot control)
                    Button(
                        onClick = { onRequestReboot(terminal.id) },
                        enabled = !isRebooting && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRebooting) Color(0xFFD97706) else Color(0xFFEF4444)
                        )
                    ) {
                        if (isRebooting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Démarrage & Alignement en cours...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Bouton de Démarrage / Redémarrage",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Button 2: Refresh Live Signals Button (Calls GET /api/v1/starlink/terminal/{id}/status)
                    OutlinedButton(
                        onClick = onRefreshStatus,
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF38BDF8))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mesure des signaux en cours...", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mesurer la Capacité & Signaux en Temps Réel",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Button 3 & 4: Payer l'abonnement & Partager la connexion
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onPayPlan,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Payer Abonnement", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = onOpenShare,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Partager Connexion", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 1: Forfaits Starlink avec paiement par code PIN CashPay All (POST /api/v1/starlink/billing/pay)
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
 * Tab 2: Clients secondaires & partage de connexion (POST /api/v1/starlink/sub-customer)
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
                            text = "Vous pouvez autoriser des proches ou clients à utiliser votre antenne avec une adresse MAC et un quota de données en Go (route POST /api/v1/starlink/sub-customer).",
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
