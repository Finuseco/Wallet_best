package com.example.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanWhite
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.StarlinkTerminalDto
import com.example.ui.viewmodel.StarlinkPlanDto

@Composable
fun StarlinkManagerDialog(
    dashboardViewModel: DashboardViewModel,
    uiState: DashboardUiState,
    userProfile: UserProfileEntity?,
    onDismissRequest: () -> Unit
) {
    val starlinkState = uiState.starlink
    val activeTab = starlinkState.activeTab

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF070B14) // Deep Starlink dark mode background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                // Top Bar (Starlink Brand & User Profile)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0F172A),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.SatelliteAlt, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                        color = Color(0xFF1E293B)
                                    ) {
                                        Text(
                                            text = "MANAGER",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF38BDF8),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = userProfile?.fullName ?: userProfile?.phone ?: "Compte CashPay All",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { dashboardViewModel.openStarlinkLinkKitDialog() }) {
                                Icon(Icons.Default.AddLink, contentDescription = "Associer Kit", tint = Color(0xFF38BDF8))
                            }
                            IconButton(onClick = onDismissRequest) {
                                Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
                            }
                        }
                    }
                }

                // Feedback banner
                if (starlinkState.error != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF7F1D1D)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color.White)
                            Text(text = starlinkState.error ?: "", color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { dashboardViewModel.clearStarlinkMessages() }) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                if (starlinkState.successMessage != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF065F46)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                            Text(text = starlinkState.successMessage ?: "", color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { dashboardViewModel.clearStarlinkMessages() }) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Main Content Workspace
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (activeTab) {
                        0 -> StarlinkDashboardTab(dashboardViewModel, starlinkState)
                        1 -> StarlinkTerminalsTab(dashboardViewModel, starlinkState)
                        2 -> StarlinkConnectivityTab(dashboardViewModel, starlinkState)
                        3 -> StarlinkBillingTab(dashboardViewModel, starlinkState)
                        4 -> StarlinkSubCustomersTab(dashboardViewModel, starlinkState)
                    }
                }

                // Bottom Navigation Bar (Footer)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0F172A),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StarlinkFooterTab(
                            title = "Accueil",
                            icon = Icons.Default.Dashboard,
                            isSelected = activeTab == 0,
                            onClick = { dashboardViewModel.setStarlinkTab(0) }
                        )
                        StarlinkFooterTab(
                            title = "Équipements",
                            icon = Icons.Default.Router,
                            isSelected = activeTab == 1,
                            onClick = { dashboardViewModel.setStarlinkTab(1) }
                        )
                        StarlinkFooterTab(
                            title = "Réseau",
                            icon = Icons.Default.SignalCellularAlt,
                            isSelected = activeTab == 2,
                            onClick = { dashboardViewModel.setStarlinkTab(2) }
                        )
                        StarlinkFooterTab(
                            title = "Abonnements",
                            icon = Icons.Default.Payment,
                            isSelected = activeTab == 3,
                            onClick = { dashboardViewModel.setStarlinkTab(3) }
                        )
                        StarlinkFooterTab(
                            title = "Clients",
                            icon = Icons.Default.Group,
                            isSelected = activeTab == 4,
                            onClick = { dashboardViewModel.setStarlinkTab(4) }
                        )
                    }
                }
            }
        }
    }

    // Terminal Detail Dialog
    if (starlinkState.isTerminalDetailOpen && starlinkState.selectedTerminal != null) {
        StarlinkTerminalDetailDialog(
            terminal = starlinkState.selectedTerminal,
            onDismiss = { dashboardViewModel.closeTerminalDetail() },
            onReboot = { dashboardViewModel.rebootTerminal(it.id) }
        )
    }

    // PIN Payment Dialog for Starlink subscription plans
    if (starlinkState.showPinDialog && starlinkState.pendingActionPlan != null) {
        StarlinkBillingPinDialog(
            plan = starlinkState.pendingActionPlan,
            pin = starlinkState.pinInput,
            onPinChange = { dashboardViewModel.onStarlinkPinChanged(it) },
            isLoading = starlinkState.isLoading,
            onDismiss = { dashboardViewModel.closeStarlinkPinDialog() },
            onSubmit = { pin ->
                starlinkState.selectedTerminal?.let { terminal ->
                    dashboardViewModel.payStarlinkPlan(terminal.id, starlinkState.pendingActionPlan.name, pin)
                } ?: run {
                    if (starlinkState.terminals.isNotEmpty()) {
                        dashboardViewModel.payStarlinkPlan(starlinkState.terminals.first().id, starlinkState.pendingActionPlan.name, pin)
                    }
                }
            }
        )
    }

    // Link Kit Serial Number Dialog
    if (starlinkState.showLinkKitDialog) {
        StarlinkLinkKitDialog(
            serialInput = starlinkState.linkKitSerialNumberInput,
            onSerialChange = { dashboardViewModel.onStarlinkKitSerialChanged(it) },
            isLoading = starlinkState.isLoading,
            onDismiss = { dashboardViewModel.closeStarlinkLinkKitDialog() },
            onSubmit = { serial ->
                dashboardViewModel.linkStarlinkKit(serial)
            }
        )
    }
}

@Composable
private fun StarlinkFooterTab(
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
            modifier = Modifier.size(22.dp)
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

@Composable
fun StarlinkDashboardTab(
    viewModel: DashboardViewModel,
    state: com.example.ui.viewmodel.StarlinkUiState
) {
    val onlineCount = state.terminals.count { it.status == "ONLINE" }
    val totalCount = state.terminals.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Welcome & Status Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF131D31),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "État du Réseau Starlink",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White,
                                fontFamily = MulishFontFamily
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Connexion haut débit par satellite active",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                fontFamily = MulishFontFamily
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
                                Text("Opérationnel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F172A)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Terminaux", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$onlineCount / $totalCount", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                                Text("En ligne", fontSize = 10.sp, color = Color(0xFF10B981))
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F172A)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Latence Moy.", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("28 ms", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF38BDF8))
                                Text("Satellite LEO", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F172A)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Débit Desc.", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("210 Mbps", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF10B981))
                                Text("Optimal", fontSize = 10.sp, color = Color(0xFF10B981))
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Équipements Starlink Associés",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White,
                    fontFamily = MulishFontFamily
                )
                TextButton(onClick = { viewModel.openStarlinkLinkKitDialog() }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Associer Kit", color = Color(0xFF38BDF8), fontSize = 12.sp)
                }
            }
        }

        items(state.terminals) { term ->
            StarlinkTerminalCard(
                terminal = term,
                onClick = { viewModel.openTerminalDetail(term) },
                onReboot = { viewModel.rebootTerminal(term.id) }
            )
        }
    }
}

@Composable
fun StarlinkTerminalsTab(
    viewModel: DashboardViewModel,
    state: com.example.ui.viewmodel.StarlinkUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gestion des Terminaux (${state.terminals.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontFamily = MulishFontFamily
                )
                Button(
                    onClick = { viewModel.openStarlinkLinkKitDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nouveau Kit", fontSize = 12.sp)
                }
            }
        }

        items(state.terminals) { term ->
            StarlinkTerminalCard(
                terminal = term,
                onClick = { viewModel.openTerminalDetail(term) },
                onReboot = { viewModel.rebootTerminal(term.id) }
            )
        }
    }
}

@Composable
fun StarlinkTerminalCard(
    terminal: StarlinkTerminalDto,
    onClick: () -> Unit,
    onReboot: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF131D31),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Router, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                    Text(
                        text = terminal.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontFamily = MulishFontFamily
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (terminal.status == "ONLINE") Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFFF5252).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (terminal.status == "ONLINE") "CONNECTÉ" else "HORS LIGNE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (terminal.status == "ONLINE") Color(0xFF10B981) else Color(0xFFFF5252),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "N° de Série : ${terminal.serialNumber}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Text(text = "Signal : ${terminal.signalQuality}", fontSize = 11.sp, color = Color(0xFF38BDF8))
                    Text(text = "Forfait : ${terminal.planName}", fontSize = 11.sp, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "IP : ${terminal.ipAddress}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Text(text = "Expiration : ${terminal.expiryDate}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReboot,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Redémarrer", fontSize = 12.sp)
                }

                Button(
                    onClick = onClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Paramètres", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StarlinkConnectivityTab(
    viewModel: DashboardViewModel,
    state: com.example.ui.viewmodel.StarlinkUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Diagnostic & Signal Satellite",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                fontFamily = MulishFontFamily
            )
        }

        item {
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.SignalWifi4Bar, contentDescription = null, tint = Color(0xFF10B981))
                            Text("Qualité du Champ LEO", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }
                        Text("98% Optimal", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { 0.98f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Obstruction", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("0.0% (Aucune)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Orientation Antenne", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("Automatique (Motorisée)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF131D31),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Paramètres Réseau Avancés", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    DiagnosticRow("Passerelle par défaut", "192.168.1.1")
                    DiagnosticRow("Mode Routeur", "Actif (NAT Natif)")
                    DiagnosticRow("IPv6", "Activé (Global Unicast)")
                    DiagnosticRow("Bande Wi-Fi", "Wi-Fi 6 (5 GHz / 2.4 GHz Mixte)")
                }
            }
        }
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun StarlinkBillingTab(
    viewModel: DashboardViewModel,
    state: com.example.ui.viewmodel.StarlinkUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Forfaits & Abonnements Starlink",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                fontFamily = MulishFontFamily
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Payez directement vos abonnements avec votre solde CashPay All.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                fontFamily = MulishFontFamily
            )
        }

        items(state.plans) { plan ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = plan.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White,
                                fontFamily = MulishFontFamily
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = plan.speed,
                                fontSize = 12.sp,
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${plan.price} ${plan.currency}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF10B981)
                            )
                            Text("Par mois", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = plan.description,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        fontFamily = MulishFontFamily
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.openStarlinkPinDialog(plan) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Payer l'Abonnement",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StarlinkSubCustomersTab(
    viewModel: DashboardViewModel,
    state: com.example.ui.viewmodel.StarlinkUiState
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
                        text = "Clients Secondaires / Partage",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White,
                        fontFamily = MulishFontFamily
                    )
                    Text(
                        text = "Gérez les accès secondaires à vos terminaux Starlink",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF131D31),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Aucun client secondaire enregistré", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Vous pouvez partager l'accès à un terminal avec d'autres utilisateurs CashPay All avec quotas de données.", textAlign = TextAlign.Center, color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StarlinkTerminalDetailDialog(
    terminal: StarlinkTerminalDto,
    onDismiss: () -> Unit,
    onReboot: (StarlinkTerminalDto) -> Unit
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Router, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                        Column {
                            Text(text = terminal.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                            Text(text = "ID: ${terminal.id}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailRow("N° de Série", terminal.serialNumber)
                        DetailRow("Statut", terminal.status)
                        DetailRow("Qualité Signal", terminal.signalQuality)
                        DetailRow("Forfait Actif", terminal.planName)
                        DetailRow("Adresse IP", terminal.ipAddress)
                        DetailRow("Firmware", terminal.firmwareVersion)
                        DetailRow("Expiration", terminal.expiryDate)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onReboot(terminal)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Redémarrer le Terminal", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun StarlinkBillingPinDialog(
    plan: StarlinkPlanDto,
    pin: String,
    onPinChange: (String) -> Unit,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
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
                    value = pin,
                    onValueChange = onPinChange,
                    label = { Text("Code PIN CashPay (4 chiffres)") },
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Annuler", color = Color.LightGray)
                    }

                    Button(
                        onClick = { onSubmit(pin) },
                        enabled = pin.length == 4 && !isLoading,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black)
                        } else {
                            Text("Valider", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StarlinkLinkKitDialog(
    serialInput: String,
    onSerialChange: (String) -> Unit,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
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
                    text = "Entrez le numéro de série du kit (ex: KIT2026-...)",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = serialInput,
                    onValueChange = onSerialChange,
                    label = { Text("Numéro de série du kit / Terminal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Annuler", color = Color.LightGray)
                    }

                    Button(
                        onClick = { onSubmit(serialInput) },
                        enabled = serialInput.isNotBlank() && !isLoading,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        if (isLoading) {
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
