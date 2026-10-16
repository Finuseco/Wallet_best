package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import com.example.ui.components.*
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
                                    color = Color(0xFF0284C7).copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "ENTERPRISE",
                                        fontSize = 8.sp,
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
                    IconButton(onClick = { dashboardViewModel.openResellerConfigDialog() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuration Revendeur",
                            tint = Color(0xFF94A3B8)
                        )
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
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScreenFooterTab(
                        title = "Accueil",
                        icon = Icons.Default.Dashboard,
                        isSelected = activeTab == 0,
                        onClick = { dashboardViewModel.setStarlinkTab(0) }
                    )
                    ScreenFooterTab(
                        title = "Terminaux",
                        icon = Icons.Default.Router,
                        isSelected = activeTab == 1,
                        onClick = { dashboardViewModel.setStarlinkTab(1) }
                    )
                    ScreenFooterTab(
                        title = "Réseau",
                        icon = Icons.Default.SignalCellularAlt,
                        isSelected = activeTab == 2,
                        onClick = { dashboardViewModel.setStarlinkTab(2) }
                    )
                    ScreenFooterTab(
                        title = "Forfaits",
                        icon = Icons.Default.Payment,
                        isSelected = activeTab == 3,
                        onClick = { dashboardViewModel.setStarlinkTab(3) }
                    )
                    ScreenFooterTab(
                        title = "Clients",
                        icon = Icons.Default.Group,
                        isSelected = activeTab == 4,
                        onClick = { dashboardViewModel.setStarlinkTab(4) }
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
                    0 -> StarlinkDashboardTab(dashboardViewModel, starlinkState)
                    1 -> StarlinkTerminalsTab(dashboardViewModel, starlinkState)
                    2 -> StarlinkConnectivityTab(dashboardViewModel, starlinkState)
                    3 -> StarlinkBillingTab(dashboardViewModel, starlinkState)
                    4 -> StarlinkSubCustomersScreenTab(dashboardViewModel, starlinkState)
                }
            }
        }
    }

    // Terminal Detail Dialog with status and reboot
    if (starlinkState.isTerminalDetailOpen && starlinkState.selectedTerminal != null) {
        val selected = starlinkState.selectedTerminal
        LaunchedEffect(selected.id) {
            dashboardViewModel.fetchTerminalStatus(selected.id)
        }
        StarlinkTerminalDetailDialog(
            terminal = selected,
            onDismiss = { dashboardViewModel.closeTerminalDetail() },
            onReboot = { dashboardViewModel.rebootTerminal(it.id) }
        )
    }

    // PIN Payment Dialog
    if (starlinkState.showPinDialog && starlinkState.pendingActionPlan != null) {
        StarlinkBillingPinDialog(
            plan = starlinkState.pendingActionPlan,
            pin = starlinkState.pinInput,
            onPinChange = { dashboardViewModel.onStarlinkPinChanged(it) },
            isLoading = starlinkState.isLoading,
            onDismiss = { dashboardViewModel.closeStarlinkPinDialog() },
            onSubmit = { pin ->
                val plan = starlinkState.pendingActionPlan
                val targetTerminalId = starlinkState.selectedTerminal?.id
                    ?: starlinkState.terminals.firstOrNull()?.id
                    ?: "TERMINAL-DEFAULT"
                dashboardViewModel.payStarlinkPlan(targetTerminalId, plan.name, pin)
            }
        )
    }

    // Link Kit Serial Dialog
    if (starlinkState.showLinkKitDialog) {
        StarlinkLinkKitDialog(
            serialInput = starlinkState.linkKitSerialNumberInput,
            onSerialChange = { dashboardViewModel.onStarlinkKitSerialChanged(it) },
            isLoading = starlinkState.isLoading,
            onDismiss = { dashboardViewModel.closeStarlinkLinkKitDialog() },
            onSubmit = { serial -> dashboardViewModel.linkStarlinkKit(serial) }
        )
    }

    // Add Sub-Customer Dialog
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

    // Register Terminal Dialog
    if (starlinkState.showRegisterTerminalDialog) {
        StarlinkRegisterTerminalDialog(
            userWalletId = userProfile?.walletId ?: "WALLET-ME",
            isLoading = starlinkState.isLoading,
            onDismiss = { dashboardViewModel.closeRegisterTerminalDialog() },
            onSubmit = { resellerId, devId, nick, ownerWallet, serial ->
                dashboardViewModel.registerStarlinkTerminal(resellerId, devId, nick, ownerWallet, serial)
            }
        )
    }

    // Reseller Config Dialog
    if (starlinkState.showResellerConfigDialog) {
        StarlinkResellerConfigDialog(
            merchantWalletId = userProfile?.walletId ?: "WALLET-ME",
            isLoading = starlinkState.isLoading,
            onDismiss = { dashboardViewModel.closeResellerConfigDialog() },
            onSubmit = { comp, wallet, client, secret ->
                dashboardViewModel.saveResellerConfig(comp, wallet, client, secret)
            }
        )
    }
}

@Composable
private fun ScreenFooterTab(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
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

@Composable
fun StarlinkSubCustomersScreenTab(
    viewModel: DashboardViewModel,
    state: StarlinkUiState
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
                        text = "Gérez les quotas de données et accès aux terminaux",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
                Button(
                    onClick = { viewModel.openAddSubCustomerDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (state.subCustomers.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aucun client secondaire enregistré",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vous pouvez associer des clients CashPay All à vos terminaux Starlink avec des limites de données en Go (API /sub-customer).",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = { viewModel.openAddSubCustomerDialog() },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Enregistrer un premier client", color = Color(0xFF38BDF8), fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(state.subCustomers) { sub ->
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
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = sub.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text(text = "Wallet: ${sub.walletId} • MAC: ${sub.macAddress}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text(text = "Expire: ${sub.expiryDate}", color = Color(0xFF64748B), fontSize = 10.sp)
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

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Gestion Revendeur & Terminaux", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "En tant que revendeur agréé, vous pouvez enregistrer de nouveaux terminaux sur votre flotte et synchroniser les profils.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.openRegisterTerminalDialog() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Enregistrer Terminal", fontSize = 11.sp, color = Color.White)
                        }
                        Button(
                            onClick = { viewModel.openResellerConfigDialog() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Config Entreprise", fontSize = 11.sp, color = Color(0xFF38BDF8))
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
    var selectedTerminalId by remember { mutableStateOf(terminals.firstOrNull()?.id ?: "") }
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
                    label = { Text("Terminal ID") },
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
                    label = { Text("Date d'expiration") },
                    placeholder = { Text("YYYY-MM-DD") },
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
                        Text("Enregistrer", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StarlinkRegisterTerminalDialog(
    userWalletId: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (resellerId: String, deviceId: String, nickname: String, ownerWalletId: String, serialNumber: String) -> Unit
) {
    var resellerId by remember { mutableStateOf("RESELLER-01") }
    var deviceId by remember { mutableStateOf("UT-${System.currentTimeMillis().toString().takeLast(6)}") }
    var nickname by remember { mutableStateOf("") }
    var ownerWalletId by remember { mutableStateOf(userWalletId) }
    var serialNumber by remember { mutableStateOf("") }

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
                        text = "Enregistrer un Terminal Starlink",
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
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Nom / Surnom (ex: Starlink Kinshasa)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = serialNumber,
                    onValueChange = { serialNumber = it },
                    label = { Text("Numéro de Série (ex: KIT2026-...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = deviceId,
                    onValueChange = { deviceId = it },
                    label = { Text("Device ID (userTerminalId)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ownerWalletId,
                    onValueChange = { ownerWalletId = it },
                    label = { Text("Owner Wallet ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onSubmit(resellerId, deviceId, nickname, ownerWalletId, serialNumber) },
                    enabled = nickname.isNotBlank() && serialNumber.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black)
                    } else {
                        Text("Enregistrer le Terminal", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StarlinkResellerConfigDialog(
    merchantWalletId: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (companyName: String, merchantWalletId: String, clientId: String, clientSecret: String) -> Unit
) {
    var companyName by remember { mutableStateOf("") }
    var walletId by remember { mutableStateOf(merchantWalletId) }
    var starlinkClientId by remember { mutableStateOf("") }
    var starlinkClientSecret by remember { mutableStateOf("") }

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
                        text = "Configuration Revendeur Entreprise",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sauvegardez les identifiants revendeur Starlink pour administrer votre parc d'antennes.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Nom de l'entreprise") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = walletId,
                    onValueChange = { walletId = it },
                    label = { Text("Merchant Wallet ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = starlinkClientId,
                    onValueChange = { starlinkClientId = it },
                    label = { Text("Starlink Client ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = starlinkClientSecret,
                    onValueChange = { starlinkClientSecret = it },
                    label = { Text("Starlink Client Secret") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onSubmit(companyName, walletId, starlinkClientId, starlinkClientSecret) },
                    enabled = companyName.isNotBlank() && starlinkClientId.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text("Enregistrer Configuration", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
