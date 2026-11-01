package com.example.ui.screens

import androidx.compose.runtime.collectAsState
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SportsEsports
import com.example.data.local.NotificationEntity
import com.example.data.local.SessionEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.WalletResponse
import com.example.ui.components.ToofanButton
import com.example.ui.components.ToofanInputField
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueVibrant
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.BrandOrangeDark
import com.example.ui.theme.BrandOrangeLight
import com.example.ui.theme.GreenOnline
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.WarningYellow
import com.example.ui.theme.CancelGrey
import com.example.ui.theme.ToofanBgColor
import com.example.ui.theme.ToofanBlue
import com.example.ui.theme.ToofanBodyText
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanGrey1
import com.example.ui.theme.ToofanLinkPink
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanOrange
import com.example.ui.theme.ToofanWhite
import com.example.ui.theme.ToofanYellow
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.DashboardViewModel
import com.example.util.PdfReceiptGenerator
import com.example.util.QrCodeGenerator
import com.example.util.TransactionTypeMapper
import com.example.util.CashPayMessageTranslator
import com.example.ui.components.FloatingCapsuleBottomBar
import com.example.ui.components.QrScannerDialog
import com.example.ui.components.PayScanDialog
import com.example.ui.components.CashPaySquareStatusDialog
import com.example.ui.components.SquareStatusType
import com.example.ui.components.StatusDetailItem
import com.example.ui.viewmodel.PayScanMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val userProfile by dashboardViewModel.userProfile.collectAsStateWithLifecycle()
    val session by dashboardViewModel.session.collectAsStateWithLifecycle()
    val transactions by dashboardViewModel.transactions.collectAsStateWithLifecycle()
    val notifications by dashboardViewModel.notifications.collectAsStateWithLifecycle()

    var showActionPlusDialog by remember { mutableStateOf(false) }
    var showMyQrCodeModal by remember { mutableStateOf(false) }
    var showSettingsModal by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }
    var showQrScannerDialog by remember { mutableStateOf(false) }
    var commerceScan by remember { mutableStateOf<String?>(null) }
    var qrScanTarget by remember { mutableStateOf("transfer") } // "transfer", "agent_deposit", "agent_withdraw", "agent_loan"
    var selectedContactForProfile by remember { mutableStateOf<com.example.data.model.PhoneContact?>(null) }

    var showServicesDialog by remember { mutableStateOf(false) }
    var showStarlinkManager by remember { mutableStateOf(false) }
    var showDigitalServicesComingSoonDialog by remember { mutableStateOf(false) }
    var isAgentCustomerListOpen by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color(0xFF000E38),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            FloatingCapsuleBottomBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { dashboardViewModel.setSelectedTab(it) },
                onCentralActionClick = { showActionPlusDialog = true }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ToofanBgColor)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (uiState.selectedTab) {
                0 -> ToofanDashboardTab(
                    userProfile = userProfile,
                    transactions = transactions,
                    notifications = notifications,
                    walletResponse = uiState.walletResponse,
                    uiState = uiState,
                    dashboardViewModel = dashboardViewModel,
                    onOpenTransfer = { dashboardViewModel.openTransferDialog() },
                    onOpenServices = { showServicesDialog = true },
                    onOpenQr = { showQrDialog = true },
                    onLogout = { authViewModel.logout() },
                    onSettingsClick = { showSettingsModal = true },
                    onContactClick = { selectedContactForProfile = it },
                    onActionPlusClick = { showActionPlusDialog = true }
                )
                1 -> ToofanHistoryTab(
                    userProfile = userProfile,
                    notifications = notifications,
                    uiState = uiState,
                    transactions = transactions,
                    dashboardViewModel = dashboardViewModel
                )
                2 -> ToofanCardsAndSecurityTab(
                    uiState = uiState,
                    userProfile = userProfile,
                    notifications = notifications,
                    session = session,
                    dashboardViewModel = dashboardViewModel,
                    onLogout = { authViewModel.logout() }
                )
                3 -> ToofanProfileTab(
                    userProfile = userProfile,
                    notifications = notifications,
                    dashboardViewModel = dashboardViewModel,
                    onEditClick = { dashboardViewModel.openEditProfileDialog(userProfile) },
                    onNavigateSecurity = { dashboardViewModel.setSelectedTab(2) }
                )
            }
        }
    }

    // Quick Action Launcher Dialog (Triggered by bottom central "+")
    if (showActionPlusDialog) {
        ActionPlusModalDialog(
            userProfile = userProfile,
            onDismiss = { showActionPlusDialog = false },
            onSendScan = {
                showActionPlusDialog = false
                dashboardViewModel.openPayScan(PayScanMode.WALLET_TRANSFER)
            },
            onReceiveQr = {
                showActionPlusDialog = false
                showQrDialog = true
            },
            onPayPos = {
                showActionPlusDialog = false
                dashboardViewModel.openPayScan(PayScanMode.SUPERMARKET_BASKET)
            },
            onWithdraw = {
                showActionPlusDialog = false
                dashboardViewModel.openWithdrawalDialog()
            },
            onWithdrawExpress = {
                showActionPlusDialog = false
                dashboardViewModel.openWithdrawalExpress()
            },
            onDeposit = {
                showActionPlusDialog = false
                dashboardViewModel.openDepositDialog()
            },
            onExchange = {
                showActionPlusDialog = false
                dashboardViewModel.openExchangeDialog()
            },
            onInternationalClaims = {
                showActionPlusDialog = false
                dashboardViewModel.openClaimsScreen()
            },
            onReferrals = {
                showActionPlusDialog = false
                dashboardViewModel.openReferralDialog()
            },
            onCpkReader = {
                showActionPlusDialog = false
                dashboardViewModel.openCpkReader()
            },
            onStarlinkManager = {
                showActionPlusDialog = false
                dashboardViewModel.openStarlinkManager()
            },
            onAgentDeposit = {
                showActionPlusDialog = false
                dashboardViewModel.openAgentServicesScreen(tab = 0)
            },
            onAgentWithdraw = {
                showActionPlusDialog = false
                dashboardViewModel.openAgentServicesScreen(tab = 0)
            },
            onAgentExternalMoMo = {
                showActionPlusDialog = false
                dashboardViewModel.openAgentExternalMoMoDialog()
            },
            onAgentLoan = {
                showActionPlusDialog = false
                dashboardViewModel.openAgentServicesScreen(tab = 0)
            },
            onAgentHistory = {
                showActionPlusDialog = false
                dashboardViewModel.openAgentServicesScreen(tab = 1)
            },
            onAgentShopping = {
                showActionPlusDialog = false
                dashboardViewModel.openAgentShoppingDialog(0)
            }
        )
    }

    // Settings Modal (Triggered by Top Bar Settings Gear)
    if (showSettingsModal) {
        CashPaySettingsModal(
            onDismiss = { showSettingsModal = false },
            userProfile = userProfile,
            onLogout = { authViewModel.logout() },
            onOpenActivateAgent = {
                showSettingsModal = false
                dashboardViewModel.openActivateAgentDialog()
            },
            onOpenForgotPin = {
                showSettingsModal = false
                dashboardViewModel.openForgotPinDialog(userProfile?.phone)
            },
            onOpenReferrals = {
                showSettingsModal = false
                dashboardViewModel.openReferralDialog()
            }
        )
    }

    // Activate Agent Dialog
    ActivateAgentDialog(
        isOpen = uiState.isActivateAgentDialogOpen,
        plan = uiState.agentPlan,
        pin = uiState.agentPin,
        isLoading = uiState.isActivatingAgent,
        errorMessage = uiState.activateAgentError,
        successMessage = uiState.activateAgentSuccessMessage,
        onPlanChange = { dashboardViewModel.setAgentPlan(it) },
        onPinChange = { dashboardViewModel.setAgentPin(it) },
        onSubmit = { dashboardViewModel.submitActivateAgent(userProfile?.id ?: 1) },
        onDismiss = { dashboardViewModel.closeActivateAgentDialog() }
    )

    // Forgot PIN Dialog in Dashboard
    ForgotPinDialog(
        isOpen = uiState.isForgotPinDialogOpen,
        step = uiState.forgotPinStep,
        phone = uiState.forgotPinPhone,
        channel = uiState.forgotPinChannel,
        otp = uiState.forgotPinOtp,
        newPin = uiState.forgotPinNewPin,
        confirmPin = uiState.forgotPinConfirmPin,
        isLoading = uiState.isForgotPinLoading,
        errorMessage = uiState.forgotPinError,
        successMessage = uiState.forgotPinSuccessMessage,
        onPhoneChange = { dashboardViewModel.setForgotPinPhone(it) },
        onChannelChange = { dashboardViewModel.setForgotPinChannel(it) },
        onOtpChange = { dashboardViewModel.setForgotPinOtp(it) },
        onNewPinChange = { dashboardViewModel.setForgotPinNewPin(it) },
        onConfirmPinChange = { dashboardViewModel.setForgotPinConfirmPin(it) },
        onRequestSubmit = { dashboardViewModel.submitForgotPinRequest() },
        onVerifySubmit = { dashboardViewModel.submitForgotPinVerify() },
        onResetSubmit = { dashboardViewModel.submitForgotPinReset() },
        onDismiss = { dashboardViewModel.closeForgotPinDialog() }
    )

    // Complete Agent Services & Commissions Suite
    AgentServicesDialog(
        isOpen = uiState.isAgentServicesDialogOpen,
        uiState = uiState,
        userProfile = userProfile,
        onDismiss = { dashboardViewModel.closeAgentServicesScreen() },
        onOpenQrScanner = { target ->
            qrScanTarget = target
            showQrScannerDialog = true
        },
        onTabSelected = { dashboardViewModel.setAgentActiveTab(it) },
        onToggleAgentBalanceVisibility = { dashboardViewModel.toggleAgentBalanceVisibility() },
        onCentralActionClick = { showActionPlusDialog = true },
        onOpenSweepDialog = { dashboardViewModel.openSweepCommissionDialog(it) },
        onCloseSweepDialog = { dashboardViewModel.closeSweepCommissionDialog() },
        onSweepCurrencyChange = { dashboardViewModel.setSweepCurrency(it) },
        onSweepAmountChange = { dashboardViewModel.setSweepAmount(it) },
        onSweepIsAllChange = { dashboardViewModel.setSweepIsAll(it) },
        onSweepPinChange = { dashboardViewModel.setSweepPin(it) },
        onSubmitSweepCommission = { dashboardViewModel.submitSweepCommission(userProfile?.id ?: 1) },
        onDepositClientRefChange = { dashboardViewModel.setAgentDepositClientRef(it) },
        onSearchDepositClient = { dashboardViewModel.searchAndIdentifyClientForDeposit() },
        onConfirmDepositClient = { dashboardViewModel.confirmClientForDeposit() },
        onDepositAmountChange = { dashboardViewModel.setAgentDepositAmount(it) },
        onDepositCurrencyChange = { dashboardViewModel.setAgentDepositCurrency(it) },
        onSubmitDepositAmount = { dashboardViewModel.submitAmountForDeposit() },
        onDepositPinChange = { dashboardViewModel.setAgentDepositPin(it) },
        onSubmitDepositPin = { dashboardViewModel.submitPinForDeposit(userProfile?.id ?: 1) },
        onSubmitDeposit = { dashboardViewModel.submitAgentDeposit(userProfile?.id ?: 1) },
        onResetDeposit = { dashboardViewModel.resetAgentDeposit() },
        onWithdrawClientRefChange = { dashboardViewModel.setAgentWithdrawClientRef(it) },
        onWithdrawAmountChange = { dashboardViewModel.setAgentWithdrawAmount(it) },
        onWithdrawCurrencyChange = { dashboardViewModel.setAgentWithdrawCurrency(it) },
        onWithdrawChannelChange = { dashboardViewModel.setAgentWithdrawChannel(it) },
        onWithdrawClientOtpChange = { dashboardViewModel.setAgentWithdrawClientOtp(it) },
        onInitiateWithdraw = {
            when (uiState.agentWithdrawStep) {
                1 -> dashboardViewModel.searchAndIdentifyClientForWithdraw()
                2 -> dashboardViewModel.submitAmountForWithdraw()
                3 -> dashboardViewModel.initiateAgentWithdraw(userProfile?.id ?: 1)
            }
        },
        onSubmitWithdrawOtp = { dashboardViewModel.submitAgentWithdrawOtp(userProfile?.id ?: 1) },
        onResetWithdraw = { dashboardViewModel.resetAgentWithdraw() },
        onLoanClientRefChange = { dashboardViewModel.setAgentLoanClientRef(it) },
        onSearchLoanTarget = { dashboardViewModel.searchAgentLoanTarget() },
        onLoanAmountChange = { dashboardViewModel.setAgentLoanAmount(it) },
        onLoanCurrencyChange = { dashboardViewModel.setAgentLoanCurrency(it) },
        onLoanPinChange = { dashboardViewModel.setAgentLoanPin(it) },
        onSubmitLoanRepay = { dashboardViewModel.submitAgentLoanRepay(userProfile?.id ?: 1) },
        onResetLoanRepay = { dashboardViewModel.resetAgentLoanRepay() },
        onOpenRegisterCustomer = { dashboardViewModel.openAgentCustomerRegister() },
        onOpenCustomerList = {
            dashboardViewModel.loadAgentCustomersList()
            isAgentCustomerListOpen = true
        },
        onOpenShopping = { tab ->
            dashboardViewModel.openAgentShoppingDialog(tab)
        },
        onOpenStarlink = {
            dashboardViewModel.closeAgentServicesScreen()
            showStarlinkManager = true
        }
    )

    // Neumorphic Analytics & Real-time Bitcoin Stock Market Full-Screen Application
    AnalyticsAppScreen(
        isOpen = uiState.isAnalyticsOpen,
        uiState = uiState,
        userProfile = userProfile,
        transactions = transactions,
        notifications = notifications,
        walletResponse = uiState.walletResponse,
        onDismiss = { dashboardViewModel.closeAnalyticsScreen() },
        onTabSelected = { dashboardViewModel.setAnalyticsTab(it) },
        onPeriodSelected = { dashboardViewModel.setAnalyticsPeriod(it) },
        onFlowFilterSelected = { dashboardViewModel.setAnalyticsFlowFilter(it) },
        onRefreshMarket = { dashboardViewModel.fetchBtcMarketTelemetry(uiState.analyticsPeriod) },
        onTransactionClick = { tx -> dashboardViewModel.updateMain { copy(selectedTransactionForDetail = tx) } },
        onOpenSendBtc = { dashboardViewModel.openBtcTransferDialog() },
        onOpenReceiveBtc = { dashboardViewModel.openReceiveBtcDialog() },
        onOpenExchange = { dashboardViewModel.openExchangeDialog() },
        onOpenDeposit = { dashboardViewModel.openDepositDialog() },
        onOpenWithdraw = { dashboardViewModel.openWithdrawalDialog() },
        onOpenSendUsdt = { dashboardViewModel.openSendUsdtDialog() },
        onOpenReceiveUsdt = { dashboardViewModel.openReceiveUsdtDialog() },
        onOpenUsdtDeposits = { dashboardViewModel.openUsdtDepositsDialog() }
    )

    // Agent Shopping Suite (Boutiques, Publication, Catalogue Public)
    AgentShoppingDialog(
        isOpen = uiState.isAgentShoppingDialogOpen,
        uiState = uiState,
        onDismiss = { dashboardViewModel.closeAgentShoppingDialog() },
        onTabSelected = { dashboardViewModel.setShoppingActiveTab(it) },
        onRefresh = { dashboardViewModel.loadShoppingData() },
        onCreateBoutique = { name, addr, wa -> dashboardViewModel.createShoppingBoutique(name, addr, wa) },
        onUpdateBoutique = { bId, name, addr, wa -> dashboardViewModel.updateShoppingBoutique(bId, name, addr, wa) },
        onDeleteBoutique = { bId -> dashboardViewModel.deleteShoppingBoutique(bId) },
        onPublishProduct = { req -> dashboardViewModel.publishProduct(req) },
        onUpdateProduct = { pId, req -> dashboardViewModel.updateProduct(pId, req) },
        onDeleteProduct = { pId -> dashboardViewModel.deleteProduct(pId) },
        onEditProduct = { p -> dashboardViewModel.setEditingProduct(p) },
        onFetchProductReference = { pId -> dashboardViewModel.fetchProductReference(pId) },
        onFetchPublicCatalog = { uId, sId -> dashboardViewModel.fetchPublicCatalog(uId, sId) }
    )

    // Agent Customer Onboarding Dialog (KYC Registration Module)
    AgentCustomerOnboardingDialog(
        isOpen = uiState.isAgentCustomerSheetOpen,
        optionsResponse = uiState.agentCustomerOptions,
        isLoadingOptions = uiState.isAgentCustomerOptionsLoading,
        isRegistering = uiState.isAgentRegisteringCustomer,
        registerSuccess = uiState.agentCustomerRegisterSuccess,
        registerError = uiState.agentCustomerRegisterError,
        onDismiss = { dashboardViewModel.closeAgentCustomerRegister() },
        onSubmitRegister = { dashboardViewModel.registerAgentCustomer(it) },
        onRetryOptions = { dashboardViewModel.loadAgentCustomerOptions() }
    )

    // Agent Customer List Dialog (Mes Clients Parrainés)
    AgentCustomerListDialog(
        isOpen = isAgentCustomerListOpen,
        customersList = uiState.agentCustomersList,
        isLoading = uiState.isLoadingAgentCustomers,
        onDismiss = { isAgentCustomerListOpen = false },
        onRefresh = { dashboardViewModel.loadAgentCustomersList() },
        onOpenRegister = {
            isAgentCustomerListOpen = false
            dashboardViewModel.openAgentCustomerRegister()
        }
    )

    // Mini Public Profile Dialog
    selectedContactForProfile?.let { contact ->
        MiniPublicProfileDialog(
            contact = contact,
            onDismiss = { selectedContactForProfile = null },
            onSendMoney = {
                selectedContactForProfile = null
                if (contact.publicProfile != null) {
                    dashboardViewModel.triggerPrefilledTransfer(contact.name, contact.publicProfile)
                } else {
                    dashboardViewModel.onRecipientChanged(contact.name)
                    dashboardViewModel.openTransferDialog()
                }
            }
        )
    }

    // Services & Abonnements Dialog
    if (showServicesDialog) {
        ServicesDialog(
            onDismiss = { showServicesDialog = false },
            onServiceSelected = { service ->
                showServicesDialog = false
                when (service) {
                    "Analytique & Marché BTC" -> {
                        dashboardViewModel.openAnalyticsScreen()
                    }
                    "Réclamations internationales" -> {
                        dashboardViewModel.openClaimsScreen()
                    }
                    "Abonnements TV" -> {
                        dashboardViewModel.openTvSubscriptionDialog("CPLUS")
                    }
                    "Factures Publiques" -> {
                        dashboardViewModel.openRegidesoDialog("KIN")
                    }
                    "Starlink Manager" -> {
                        showStarlinkManager = true
                    }
                    "Clés FINUSECO CPK" -> {
                        dashboardViewModel.openCpkReader()
                    }
                    "Services Digitaux" -> {
                        showDigitalServicesComingSoonDialog = true
                    }
                    "Mobile Money" -> {
                        dashboardViewModel.onRecipientChanged("Mobile Money")
                        dashboardViewModel.openTransferDialog()
                    }
                    else -> {
                        dashboardViewModel.onRecipientChanged(service)
                        dashboardViewModel.openTransferDialog()
                    }
                }
            }
        )
    }

    if (showDigitalServicesComingSoonDialog) {
        AlertDialog(
            onDismissRequest = { showDigitalServicesComingSoonDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF8B5CF6))
                    Text("Services Digitaux", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Bientôt disponible sur CashPay All :",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        fontSize = 14.sp
                    )
                    Text(
                        "Les abonnements Netflix, Spotify, Apple Music, PlayStation et autres services digitaux seront disponibles très prochainement avec règlement direct depuis vos portefeuilles.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDigitalServicesComingSoonDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000E38)),
                    border = BorderStroke(1.2.dp, Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Compris", color = Color.White, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showStarlinkManager || uiState.isStarlinkManagerOpen) {
        StarlinkManagerScreen(
            dashboardViewModel = dashboardViewModel,
            uiState = uiState,
            userProfile = userProfile,
            onBack = {
                showStarlinkManager = false
                dashboardViewModel.closeStarlinkManager()
            }
        )
    }

    // Loans / Crédit Screen
    if (uiState.isLoansScreenOpen) {
        LoansScreen(
            uiState = uiState,
            viewModel = dashboardViewModel,
            onBack = { dashboardViewModel.closeLoansScreen() }
        )
    }

    // Withdrawal Dialog
    if (uiState.isWithdrawalDialogOpen) {
        WithdrawalDialog(
            uiState = uiState,
            viewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeWithdrawalDialog() }
        )
    }

    // Deposit Dialog (MaxiCash Gateway: Mobile Money, Card, PayPal)
    if (uiState.isDepositDialogOpen) {
        DepositDialog(
            uiState = uiState,
            onDismiss = { dashboardViewModel.closeDepositDialog() },
            onMethodChange = { dashboardViewModel.setDepositMethod(it) },
            onAmountChange = { dashboardViewModel.setDepositAmount(it) },
            onOperatorChange = { dashboardViewModel.setDepositOperator(it) },
            onPhoneChange = { dashboardViewModel.setDepositPhoneNumber(it) },
            onCountryCodeChange = { dashboardViewModel.setDepositCountryCode(it) },
            onSubmit = { dashboardViewModel.submitDeposit() }
        )
    }

    // Deposit WebView Dialog (Card and PayPal)
    if (uiState.isDepositWebViewOpen && !uiState.depositPaymentUrl.isNullOrBlank()) {
        DepositWebViewDialog(
            url = uiState.depositPaymentUrl!!,
            title = when (uiState.depositMethod) {
                "card" -> "Paiement Carte MaxiCash"
                "paypal" -> "Paiement PayPal MaxiCash"
                else -> "Paiement Sécurisé MaxiCash"
            },
            reference = uiState.depositPendingReference,
            onDismiss = { dashboardViewModel.closeDepositWebView() }
        )
    }

    // Exchange Dialog (Currency Conversion - Taux Temps Réel Base de Données)
    ExchangeDialog(
        isOpen = uiState.isExchangeDialogOpen,
        walletResponse = uiState.walletResponse,
        usdtBalance = uiState.usdtBalance,
        exchangeRatesMap = uiState.exchangeRatesMap,
        realtimeQuoteRate = uiState.realtimeQuoteRate,
        realtimeQuoteAmount = uiState.realtimeQuoteAmount,
        isQuoteLoading = uiState.isQuoteLoading,
        fromCurrency = uiState.exchangeFromCurrency,
        toCurrency = uiState.exchangeToCurrency,
        amount = uiState.exchangeAmount,
        pin = uiState.exchangePin,
        isLoading = uiState.isExchangeLoading,
        errorMessage = uiState.exchangeError,
        successResponse = uiState.exchangeSuccessResponse,
        onFromCurrencyChange = { dashboardViewModel.setExchangeFromCurrency(it) },
        onToCurrencyChange = { dashboardViewModel.setExchangeToCurrency(it) },
        onSwapCurrencies = { dashboardViewModel.swapExchangeCurrencies() },
        onAmountChange = { dashboardViewModel.setExchangeAmount(it) },
        onPinChange = { dashboardViewModel.setExchangePin(it) },
        onRefreshRates = { dashboardViewModel.fetchExchangeRates() },
        onSubmit = { dashboardViewModel.submitExchange() },
        onDismiss = { dashboardViewModel.closeExchangeDialog() },
        onResetSuccess = { dashboardViewModel.resetExchangeSuccess() }
    )

    ReceiveBtcDialog(
        isOpen = uiState.isReceiveBtcDialogOpen,
        btcAddress = uiState.walletResponse?.bitcoin?.address ?: "",
        onDismiss = { dashboardViewModel.closeReceiveBtcDialog() }
    )

    SendBtcDialog(
        isOpen = uiState.isSendBtcDialogOpen,
        btcBalance = uiState.walletResponse?.bitcoin?.balance ?: 0.0,
        liveBtcRateUsd = uiState.btcLiveRateUsd,
        isLoading = uiState.isBtcTransferLoading,
        errorMessage = uiState.btcTransferError,
        success = uiState.btcTransferSuccess,
        txRef = uiState.btcTransferTxRef,
        onDismiss = { dashboardViewModel.closeBtcTransferDialog() },
        onSendBtc = { addr, amt, pin ->
            dashboardViewModel.sendBtc(addr, amt, pin)
        }
    )

    ReceiveUsdtDialog(
        isOpen = uiState.isReceiveUsdtDialogOpen,
        usdtAddress = uiState.usdtAddress,
        isLoading = uiState.isUsdtLoading,
        errorMessage = uiState.usdtError,
        onRefreshAddress = { dashboardViewModel.fetchUsdtAddress(forceRefresh = true) },
        onDismiss = { dashboardViewModel.closeReceiveUsdtDialog() }
    )

    SendUsdtDialog(
        isOpen = uiState.isSendUsdtDialogOpen,
        usdtBalance = uiState.usdtBalance,
        onChainBalance = uiState.usdtOnChainBalance,
        isLoading = uiState.isUsdtTransferLoading,
        errorMessage = uiState.usdtTransferError,
        success = uiState.usdtTransferSuccess,
        isPending = uiState.usdtTransferPending,
        txRef = uiState.usdtTransferTxRef,
        onDismiss = { dashboardViewModel.closeSendUsdtDialog() },
        onSendUsdt = { addr, amt, pin ->
            dashboardViewModel.sendUsdt(addr, amt, pin)
        }
    )

    UsdtDepositsDialog(
        isOpen = uiState.isUsdtDepositsDialogOpen,
        deposits = uiState.usdtDeposits,
        isLoading = uiState.isUsdtDepositsLoading,
        onRefresh = { dashboardViewModel.fetchUsdtDeposits() },
        onDismiss = { dashboardViewModel.closeUsdtDepositsDialog() }
    )

    // International Transfer Claims Screen & Dialogs (Section Client)
    androidx.activity.compose.BackHandler(enabled = uiState.isClaimsScreenOpen) {
        dashboardViewModel.closeClaimsScreen()
    }
    if (uiState.isClaimsScreenOpen) {
        InternationalClaimsScreen(
            isOpen = true,
            claims = uiState.claimsList,
            isLoading = uiState.isClaimsLoading,
            errorMessage = uiState.claimsError,
            onBack = { dashboardViewModel.closeClaimsScreen() },
            onRefresh = { dashboardViewModel.loadInternationalClaims() },
            onOpenNewClaim = { dashboardViewModel.openNewClaimDialog() },
            onSelectClaim = { dashboardViewModel.openClaimDetail(it) }
        )
    }

    if (uiState.isNewClaimDialogOpen) {
        NewClaimDialog(
            isOpen = true,
            isSubmitting = uiState.isSubmittingClaim,
            errorMessage = uiState.newClaimError,
            successMessage = uiState.newClaimSuccessMessage,
            duplicateClaim = uiState.newClaimDuplicateClaim,
            onDismiss = { dashboardViewModel.closeNewClaimDialog() },
            onSubmit = { provider, trackingNumber, amount, expectedCurrency, senderCountry, receiveCurrency ->
                dashboardViewModel.submitInternationalClaim(
                    provider = provider,
                    trackingNumber = trackingNumber,
                    expectedAmount = amount,
                    expectedCurrency = expectedCurrency,
                    senderCountry = senderCountry,
                    receiveInCurrency = receiveCurrency
                )
            },
            onViewExistingClaim = { claim ->
                dashboardViewModel.closeNewClaimDialog()
                dashboardViewModel.openClaimDetail(claim)
            }
        )
    }

    if (uiState.isClaimDetailDialogOpen) {
        ClaimDetailDialog(
            isOpen = true,
            claim = uiState.selectedClaimDetail,
            isLoading = uiState.isClaimDetailLoading,
            onDismiss = { dashboardViewModel.closeClaimDetail() }
        )
    }

    // Programme de Parrainage & Récompenses (Section Client & Agent)
    ReferralDialog(
        isOpen = uiState.isReferralDialogOpen,
        isLoading = uiState.isLoadingReferrals,
        errorMessage = uiState.referralsError,
        referralResponse = uiState.referralResponse,
        walletResponse = uiState.walletResponse,
        userProfile = userProfile,
        isClaimingReward = uiState.isClaimingReward,
        claimingReferralId = uiState.claimingReferralId,
        claimSuccessMessage = uiState.claimSuccessMessage,
        claimErrorMessage = uiState.claimErrorMessage,
        onRefresh = { dashboardViewModel.fetchReferrals() },
        onClaimReward = { dashboardViewModel.claimReferralReward(it) },
        onDismiss = { dashboardViewModel.closeReferralDialog() }
    )

    // Abonnements TV (Canal+, EasyTV, BLUS)
    TvSubscriptionDialog(
        uiState = uiState,
        viewModel = dashboardViewModel,
        onDismiss = { dashboardViewModel.closeTvSubscriptionDialog() }
    )

    // Factures Publiques (REGIDESO & SNEL)
    PublicInvoicesDialog(
        isOpen = uiState.isRegidesoDialogOpen,
        uiState = uiState,
        viewModel = dashboardViewModel,
        userProfile = userProfile,
        onDismiss = { dashboardViewModel.closeRegidesoDialog() }
    )

    // Lecteur de Clés Financières CPK (FINUSECO USB & Google Drive Vault)
    androidx.activity.compose.BackHandler(enabled = uiState.cpk.isCpkDialogOpen) {
        dashboardViewModel.closeCpkReader()
    }
    CpkReaderDialog(
        isOpen = uiState.cpk.isCpkDialogOpen,
        uiState = uiState,
        viewModel = dashboardViewModel,
        userProfile = userProfile,
        onDismiss = { dashboardViewModel.closeCpkReader() }
    )

    // Agent External Mobile Money Pull Dialog
    AgentExternalMoMoDialog(
        uiState = uiState,
        onDismiss = { dashboardViewModel.closeAgentExternalMoMoDialog() },
        onOperatorChange = { dashboardViewModel.setAgentExternalMoMoOperator(it) },
        onPhoneChange = { dashboardViewModel.setAgentExternalMoMoPhone(it) },
        onAmountChange = { dashboardViewModel.setAgentExternalMoMoAmount(it) },
        onSubmit = { dashboardViewModel.submitAgentExternalMoMo() }
    )

    // Add Contact Dialog
    if (uiState.isAddContactDialogOpen) {
        AddContactDialog(
            uiState = uiState,
            dashboardViewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeAddContactDialog() }
        )
    }

    // Full Contacts Page Dialog
    if (uiState.isContactsPageOpen) {
        ContactsPageDialog(
            uiState = uiState,
            dashboardViewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeContactsPage() }
        )
    }

    // Transaction Detail Dialog
    uiState.selectedTransactionForDetail?.let { tx ->
        TransactionDetailDialog(
            transaction = tx,
            onDismiss = { dashboardViewModel.selectTransactionForDetail(null) }
        )
    }

    // All Transactions Full Page Dialog
    if (uiState.isAllTransactionsOpen) {
        AllTransactionsDialog(
            uiState = uiState,
            transactions = transactions,
            dashboardViewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeAllTransactionsPage() }
        )
    }

    // Notification Dialog
    if (uiState.isNotificationDialogOpen) {
        NotificationDialog(
            uiState = uiState,
            notifications = notifications,
            dashboardViewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeNotificationDialog() }
        )
    }

    // Edit Profile Dialog (with real CashPay PIN check)
    if (uiState.isEditProfileOpen) {
        ToofanEditProfileDialog(
            uiState = uiState,
            userProfile = userProfile,
            onDismiss = { dashboardViewModel.closeEditProfileDialog() },
            onFullNameChange = { dashboardViewModel.onEditFullNameChanged(it) },
            onCityChange = { dashboardViewModel.onEditCityChanged(it) },
            onProfessionChange = { dashboardViewModel.onEditProfessionChanged(it) },
            onAddressChange = { dashboardViewModel.onEditAddressChanged(it) },
            onPinChange = { dashboardViewModel.onEditPinChanged(it) },
            onSubmit = { userProfile?.phone?.let { dashboardViewModel.submitProfileUpdate(it) } }
        )
    }

    val pendingCommerceScan by dashboardViewModel.commerceScanReview.collectAsState()
    pendingCommerceScan?.let { raw -> ScanPurchaseDialog(raw, dashboardViewModel) { dashboardViewModel.closeCommerceScanReview() } }
    commerceScan?.let { raw -> ScanPurchaseDialog(raw,dashboardViewModel){commerceScan=null} }
    // Live Camera QR Scanner Dialog with instant automatic search
    if (showQrScannerDialog) {
        QrScannerDialog(
            onBarcodeScanned = if(qrScanTarget=="transfer") { raw -> commerceScan=org.json.JSONObject().put("type","BARCODE").put("barcode",raw).toString();showQrScannerDialog=false } else null,
            onDismissRequest = { 
                showQrScannerDialog = false 
                qrScanTarget = "transfer"
            },
            scanMode = when (qrScanTarget) {
                "pos_sale" -> "PRODUCT"
                "payment_request" -> "PAYMENT_REQUEST"
                else -> "TRANSFER"
            },
            onQrScanned = { result ->
                val commerce = try { org.json.JSONObject(result).optString("type") in listOf("PRODUCT","POS_SALE","PAYMENT_REQUEST") } catch(_:Exception) { false }
                if(qrScanTarget=="transfer" && commerce){commerceScan=result;showQrScannerDialog=false;return@QrScannerDialog}
                val cleaned = extractRecipientFromQr(result)
                when (qrScanTarget) {
                    "agent_withdraw" -> {
                        dashboardViewModel.setAgentWithdrawClientRef(cleaned)
                        dashboardViewModel.searchAndIdentifyClientForWithdraw()
                    }
                    "agent_deposit" -> {
                        dashboardViewModel.setAgentDepositClientRef(cleaned)
                        dashboardViewModel.searchAndIdentifyClientForDeposit()
                    }
                    "agent_loan" -> {
                        dashboardViewModel.setAgentLoanClientRef(cleaned)
                        dashboardViewModel.searchAgentLoanTarget()
                    }
                    "pos_sale" -> {
                        // Product Scan Mode
                        dashboardViewModel.processProductScan(cleaned)
                    }
                    "payment_request" -> {
                        // Payment Request Mode
                        dashboardViewModel.processPaymentRequestScan(cleaned)
                    }
                    else -> {
                        dashboardViewModel.onRecipientChanged(cleaned)
                        dashboardViewModel.openTransferDialog()
                        dashboardViewModel.searchTransferRecipient()
                    }
                }
                showQrScannerDialog = false
                qrScanTarget = "transfer"
            }
        )
    }

    // PayScan Comprehensive Visual Codes & POS System (All Users)
    if (uiState.payscan.isPayScanOpen) {
        PayScanDialog(
            dashboardViewModel = dashboardViewModel,
            uiState = uiState,
            onDismissRequest = { dashboardViewModel.closePayScan() }
        )
    }

    // Starlink Manager Module is handled by StarlinkManagerScreen above

    // QR Code Dialog (Genuine ZXing scannable code + Scanner launch shortcut)
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, tint = ToofanGreen)
                        Text("Mon QR Code CashPay", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val walletId = userProfile?.walletId ?: ""
                    val qrContent = "cashpay:$walletId?name=${userProfile?.fullName ?: ""}"
                    val qrBitmap = remember(qrContent) {
                        QrCodeGenerator.generateQrBitmap(qrContent, 280)
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ToofanWhite,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .size(210.dp)
                            .padding(4.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "QR Code Réel",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            )
                        }
                    }

                    val context = androidx.compose.ui.platform.LocalContext.current
                    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ID: $walletId",
                            fontSize = 18.sp,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            color = ToofanGreen
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(walletId))
                                Toast.makeText(context, "Wallet ID copié : $walletId", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copier Wallet ID",
                                tint = ToofanGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = userProfile?.fullName ?: "",
                        fontFamily = MulishFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ToofanMainDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scanner direct shortcut button requested by user
                    Button(
                        onClick = {
                            showQrDialog = false
                            dashboardViewModel.openPayScan(PayScanMode.AUTO)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E38)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ouvrir Scanner Caméra (Envoyer / Payer)",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            },
            confirmButton = {
                ToofanButton(
                    title = "Fermer",
                    onClick = { showQrDialog = false },
                    modifier = Modifier.width(100.dp)
                )
            }
        )
    }

    // Quick Transfer Dialog
    if (uiState.isQuickTransferOpen) {
        var pinInput by remember { mutableStateOf("") }
        var showBtcScanner by remember { mutableStateOf(false) }

        if (uiState.transferSuccess) {
            CashPaySquareStatusDialog(
                isOpen = true,
                type = SquareStatusType.SUCCESS,
                title = "Transfert Réussi !",
                message = "Votre transfert a été validé avec succès.",
                details = listOf(
                    StatusDetailItem("Montant", "${uiState.transferAmount} ${uiState.transferCurrency}", isHighlight = true),
                    StatusDetailItem("Frais", "${uiState.transferFee ?: 0.0} ${uiState.transferCurrency}"),
                    StatusDetailItem("Total débité", "${uiState.transferTotalDebit ?: uiState.transferAmount} ${uiState.transferCurrency}")
                ),
                primaryButtonText = "Terminer",
                onPrimaryAction = {
                    dashboardViewModel.closeTransferDialog()
                    dashboardViewModel.clearPrefilledTransfer()
                },
                onDismiss = {
                    dashboardViewModel.closeTransferDialog()
                    dashboardViewModel.clearPrefilledTransfer()
                }
            )
        } else {
            if (uiState.transferError != null) {
                val isSoldeErr = uiState.transferError!!.contains("solde", ignoreCase = true) ||
                        uiState.transferError!!.contains("insuffisant", ignoreCase = true)
                CashPaySquareStatusDialog(
                    isOpen = true,
                    type = if (isSoldeErr) SquareStatusType.INSUFFICIENT_BALANCE else SquareStatusType.ERROR,
                    title = if (isSoldeErr) "Solde Insuffisant" else "Échec du Transfert",
                    message = uiState.transferError!!,
                    primaryButtonText = if (isSoldeErr) "Compris" else "Fermer",
                    onPrimaryAction = { dashboardViewModel.clearTransferError() },
                    onDismiss = { dashboardViewModel.clearTransferError() }
                )
            }

            AlertDialog(
                onDismissRequest = { 
                    dashboardViewModel.closeTransferDialog()
                    dashboardViewModel.clearPrefilledTransfer()
                },
                shape = RoundedCornerShape(20.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = ToFocusBorderColor)
                    Text(
                        when (uiState.transferStep) {
                            1 -> "Nouveau Transfert"
                            2 -> "Confirmation & Frais"
                            3 -> "Saisir le Code PIN"
                            else -> "Transfert Réussi"
                        },
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (uiState.prefilledRecipient != null) {
                        // Display recipient confirmation card (Rule 12: Confirmation du correspondant)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ToofanGreen.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, ToofanGreen.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(1.5.dp, ToFocusBorderColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val photoUrl = uiState.prefilledRecipient!!.profilePhotoUrl ?: uiState.prefilledRecipient!!.profilePhoto
                                        val resolved = resolveAvatarUrl(photoUrl)
                                        if (resolved != null) {
                                            AsyncImage(
                                                model = resolved,
                                                contentDescription = "Avatar",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                                            )
                                        } else {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = ToFocusBorderColor)
                                        }
                                    }
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = uiState.prefilledContactName ?: uiState.prefilledRecipient!!.fullName,
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = ToofanMainDark
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = "Vérifié",
                                                tint = SuccessGreen,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Text(
                                            text = "ID Wallet : ${uiState.prefilledRecipient!!.walletId}",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 11.sp,
                                            color = ToofanBodyText
                                        )
                                        if (uiState.transferCurrency == "BTC" && uiState.prefilledRecipient!!.bitcoin?.available == true) {
                                            Text(
                                                text = "BTC : ${uiState.prefilledRecipient!!.bitcoin?.address?.take(12)}...",
                                                fontFamily = MulishFontFamily,
                                                fontSize = 11.sp,
                                                color = ToFocusBorderColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                TextButton(
                                    onClick = { dashboardViewModel.clearPrefilledTransfer() },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Changer", fontFamily = MulishFontFamily, fontSize = 11.sp, color = Color(0xFF0066FF))
                                }
                            }
                        }
                    }

                    if (uiState.transferSuccess) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ToofanGreen.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ToofanGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Transfert validé avec succès !", fontFamily = MulishFontFamily, color = ToofanGreen, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Montant : ${uiState.transferAmount} ${uiState.transferCurrency}", fontSize = 13.sp)
                                Text("Frais : ${uiState.transferFee ?: 0.0} ${uiState.transferCurrency}", fontSize = 13.sp)
                                Text("Total débité : ${uiState.transferTotalDebit ?: 0.0} ${uiState.transferCurrency}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    } else if (uiState.transferStep == 2) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF7F9FB),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Vérifiez les détails du transfert :", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Destinataire : ${uiState.transferRecipient}", fontSize = 13.sp)
                                Text("Montant : ${uiState.transferAmount} ${uiState.transferCurrency}", fontSize = 13.sp)
                                Text("Frais de réseau : ${uiState.transferFee ?: 1.0} ${uiState.transferCurrency}", fontSize = 13.sp)
                                Text("Total à débiter : ${uiState.transferTotalDebit ?: 0.0} ${uiState.transferCurrency}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ToofanGreen)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = { pinInput = it.filter { ch -> ch.isDigit() }.take(4) },
                            label = { Text("Code PIN CashPay (4 chiffres)") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        // Currency Selector with live user balances
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val fiatMap = uiState.walletResponse?.balances?.fiat ?: emptyMap()
                            val natCode = uiState.walletResponse?.nationalCurrency?.code ?: "CDF"
                            val availableCurrencies = listOf("USD", "CDF", "EUR", "BTC")
                            availableCurrencies.forEach { curr ->
                                val selected = uiState.transferCurrency == curr
                                val balText = when (curr) {
                                    "USD" -> "${String.format(java.util.Locale.US, "%.1f", fiatMap["USD"] ?: 0.0)}$"
                                    "CDF" -> "${String.format(java.util.Locale.US, "%,.0f", fiatMap["CDF"] ?: (fiatMap[natCode] ?: 0.0))}F"
                                    "EUR" -> "${String.format(java.util.Locale.US, "%.1f", fiatMap["EUR"] ?: 0.0)}€"
                                    "BTC" -> "${String.format(java.util.Locale.US, "%.3f", uiState.walletResponse?.bitcoin?.balance ?: 0.0)}"
                                    else -> ""
                                }
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { 
                                            if (curr == "BTC") {
                                                dashboardViewModel.closeTransferDialog()
                                                dashboardViewModel.openBtcTransferDialog()
                                            } else {
                                                dashboardViewModel.onCurrencyChanged(curr)
                                                // Auto-update recipient address if prefilledRecipient is available
                                                if (uiState.prefilledRecipient != null) {
                                                    dashboardViewModel.onRecipientChanged(uiState.prefilledRecipient!!.walletId)
                                                }
                                            }
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) ToofanGreen else Color.White,
                                    border = BorderStroke(1.dp, if (selected) ToofanGreen else Color.LightGray)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(curr, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selected) Color.White else ToofanMainDark)
                                        Text(balText, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = if (selected) Color.White.copy(alpha = 0.9f) else Color.Gray)
                                    }
                                }
                            }
                        }

                        if (uiState.prefilledRecipient == null) {
                            val isBtcTransfer = uiState.transferCurrency.uppercase() == "BTC"
                            if (isBtcTransfer) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedTextField(
                                        value = uiState.transferRecipient,
                                        onValueChange = { dashboardViewModel.onRecipientChanged(it) },
                                        label = { Text("Adresse Bitcoin externe (ex: bc1q...)") },
                                        placeholder = { Text("bc1q...") },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        trailingIcon = {
                                            IconButton(onClick = { showBtcScanner = true }) {
                                                Icon(
                                                    imageVector = Icons.Default.CameraAlt,
                                                    contentDescription = "Scanner QR Code",
                                                    tint = ToofanGreen
                                                )
                                            }
                                        }
                                    )
                                    Text(
                                        text = "Collez l'adresse Bitcoin ou scannez le QR code de destination.",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        fontFamily = MulishFontFamily
                                    )
                                }

                                if (showBtcScanner) {
                                    QrScannerDialog(
                                        onDismissRequest = { showBtcScanner = false },
                                        onQrScanned = { scanned ->
                                            dashboardViewModel.onRecipientChanged(scanned)
                                            showBtcScanner = false
                                        }
                                    )
                                }
                            } else {
                                // Dual Search Mode Selector (ID Wallet vs Phone)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                        .padding(3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val isWallet = uiState.transferSearchMode == "wallet"
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(34.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isWallet) Color(0xFF000E38) else Color.Transparent)
                                            .clickable { dashboardViewModel.setTransferSearchMode("wallet") },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccountBalanceWallet,
                                                contentDescription = null,
                                                tint = if (isWallet) Color.White else Color(0xFF475569),
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Text("ID Wallet", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (isWallet) Color.White else Color(0xFF475569))
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(34.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (!isWallet) Color(0xFF000E38) else Color.Transparent)
                                            .clickable { dashboardViewModel.setTransferSearchMode("phone") },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PhoneAndroid,
                                                contentDescription = null,
                                                tint = if (!isWallet) Color.White else Color(0xFF475569),
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Text("N° Téléphone", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (!isWallet) Color.White else Color(0xFF475569))
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = uiState.transferRecipient,
                                        onValueChange = { dashboardViewModel.onRecipientChanged(it) },
                                        label = {
                                            Text(if (uiState.transferSearchMode == "wallet") "Entrer l'ID Wallet (ex: WAL-XXX)" else "Entrer le numéro (ex: +243...)")
                                        },
                                        placeholder = {
                                            Text(if (uiState.transferSearchMode == "wallet") "WAL-123456" else "+243812345678")
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { dashboardViewModel.searchTransferRecipient() },
                                        enabled = !uiState.isSearchingTransferRecipient && uiState.transferRecipient.isNotBlank(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000E38)),
                                        modifier = Modifier.height(52.dp)
                                    ) {
                                        if (uiState.isSearchingTransferRecipient) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                        } else {
                                            Icon(Icons.Default.Search, contentDescription = "Vérifier", modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }

                        ToofanInputField(
                            value = uiState.transferAmount,
                            onValueChange = { dashboardViewModel.onAmountChanged(it) },
                            placeholder = "Montant à transférer"
                        )
                        
                        // Always show PIN field for validation
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = { pinInput = it.filter { ch -> ch.isDigit() }.take(4) },
                            label = { Text("Code PIN CashPay (4 chiffres)") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    if (uiState.transferError != null) {
                        Text(
                            text = uiState.transferError!!,
                            color = Color(0xFFFF4868),
                            fontSize = 12.sp,
                            fontFamily = MulishFontFamily
                        )
                    }
                }
            },
            confirmButton = {
                if (uiState.transferSuccess) {
                    ToofanButton(
                        title = "Terminer",
                        onClick = { 
                            dashboardViewModel.closeTransferDialog()
                            dashboardViewModel.clearPrefilledTransfer()
                        },
                        modifier = Modifier.width(100.dp)
                    )
                } else if (uiState.transferStep == 2) {
                    ToofanButton(
                        title = "Valider",
                        onClick = {
                            dashboardViewModel.confirmTransfer(pin = pinInput)
                        },
                        isLoading = uiState.isTransferLoading,
                        modifier = Modifier.width(100.dp)
                    )
                } else {
                    ToofanButton(
                        title = "Suivant",
                        onClick = { dashboardViewModel.previewTransfer() },
                        isLoading = uiState.isTransferLoading,
                        modifier = Modifier.width(100.dp)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    dashboardViewModel.closeTransferDialog()
                    dashboardViewModel.clearPrefilledTransfer()
                }) {
                    Text("Annuler", fontFamily = MulishFontFamily, color = ToofanBodyText)
                }
            }
        )
        }
    }
}
}

@Composable
fun CashPayTopHeader(
    userProfile: UserProfileEntity?,
    unreadNotifCount: Int,
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    height: androidx.compose.ui.unit.Dp? = 240.dp,
    onBackClick: (() -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val boxModifier = Modifier
        .fillMaxWidth()
        .then(
            if (height != null && content == null) {
                Modifier.height(height + statusBarHeight)
            } else {
                Modifier.wrapContentHeight()
            }
        )
        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0A1E5C),
                    Color(0xFF000E38)
                )
            )
        )

    Box(modifier = boxModifier) {
        // Subtle ambient blue/cyan highlights
        Box(
            modifier = Modifier
                .size(190.dp)
                .offset(x = (-30).dp, y = (-20).dp + statusBarHeight)
                .clip(CircleShape)
                .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-30).dp + statusBarHeight)
                .clip(CircleShape)
                .background(Color(0xFF38BDF8).copy(alpha = 0.15f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarHeight)
                .padding(vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button if subpage + User Avatar + Greeting
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Retour",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!userProfile?.profilePhotoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = userProfile?.profilePhotoUrl,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                val initials = (userProfile?.fullName ?: "CP")
                                    .trim()
                                    .split(" ")
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .take(2)
                                    .joinToString("")
                                    .uppercase()
                                    .ifBlank { "CP" }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    BrandBlueDark,
                                                    BrandBlue
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Connected / Online Presence Indicator (Vert foncé)
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(GreenOnline)
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }

                    Column {
                        Text(
                            text = "Bienvenue",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userProfile?.firstName ?: "",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Image(
                                painter = painterResource(id = R.drawable.badge_verified),
                                contentDescription = "Vérifié",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Right Action Bar: FR Language Indicator, Notification Bell, Settings Gear
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🇫🇷 FR",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }

                    Box {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onNotificationClick() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        if (unreadNotifCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF4868))
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadNotifCount > 9) "9+" else unreadNotifCount.toString(),
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onSettingsClick() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Paramètres",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            if (content != null) {
                Spacer(modifier = Modifier.height(14.dp))
                content()
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun BalanceCardsCarousel(
    pagerState: androidx.compose.foundation.pager.PagerState,
    walletResponse: WalletResponse?,
    userProfile: UserProfileEntity?,
    uiState: DashboardUiState,
    dashboardViewModel: DashboardViewModel,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    onOpenTransfer: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val natCode = walletResponse?.nationalCurrency?.code ?: "CDF"
    val natName = walletResponse?.nationalCurrency?.name ?: (if (natCode.contains("CDF")) "Franc Congolais" else if (natCode.contains("XOF") || natCode.contains("XAF")) "Franc CFA" else "Devise Nationale")
    val natSymbol = walletResponse?.nationalCurrency?.symbol ?: (if (natCode == "CDF") "FC" else if (natCode.contains("XOF") || natCode.contains("XAF")) "CFA" else natCode)
    val natBal = walletResponse?.balances?.fiat?.get(natCode) ?: (walletResponse?.balances?.fiat?.get("national") ?: 0.0)

    val usdBal = walletResponse?.balances?.fiat?.get("USD") ?: 0.0
    val eurBal = walletResponse?.balances?.fiat?.get("EUR") ?: 0.0
    val btcBal = walletResponse?.bitcoin?.balance ?: 0.0
    val btcAddress = walletResponse?.bitcoin?.address ?: ""

    val userWalletId = userProfile?.walletId ?: ""

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> {
                    // Card 1: Devise Nationale (CDF)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, Color.White.copy(alpha = 0.45f)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            BrandBlueVibrant,
                                            BrandBlue,
                                            BrandBlueDark
                                        )
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .size(110.dp)
                                    .align(Alignment.BottomEnd)
                            )

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Solde $natName ($natCode)",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { dashboardViewModel.toggleBalanceVisibility() },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (uiState.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Afficher/Masquer",
                                                tint = Color.White.copy(alpha = 0.85f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    val cdfCard = walletResponse?.cards?.firstOrNull { it.currency?.uppercase() == natCode.uppercase() }
                                    if (cdfCard != null) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(
                                                painter = painterResource(id = if (cdfCard.brand?.lowercase() == "visa") R.drawable.logo_visa else R.drawable.logo_mastercard),
                                                contentDescription = cdfCard.brand ?: "",
                                                modifier = Modifier.height(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = ".... ${cdfCard.last4 ?: ""}",
                                                fontFamily = MulishFontFamily,
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = if (uiState.isBalanceVisible) "${String.format("%,.2f", natBal)} $natSymbol" else "•••••••• $natSymbol",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 28.sp,
                                    color = Color.White
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "N° de compte CP : $userWalletId",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )

                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(userWalletId))
                                            Toast.makeText(context, "N° de compte copié !", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copier",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Card 2: Dollar Wallet (USD)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, Color.White.copy(alpha = 0.45f)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF0284C7),
                                            Color(0xFF0369A1),
                                            Color(0xFF082F49)
                                        )
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .size(110.dp)
                                    .align(Alignment.BottomEnd)
                            )

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Solde Dollar USD",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { dashboardViewModel.toggleBalanceVisibility() },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (uiState.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Afficher/Masquer",
                                                tint = Color.White.copy(alpha = 0.85f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    val usdCard = walletResponse?.cards?.firstOrNull { it.currency?.uppercase() == "USD" }
                                    if (usdCard != null) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(
                                                painter = painterResource(id = if (usdCard.brand?.lowercase() == "visa") R.drawable.logo_visa else R.drawable.logo_mastercard),
                                                contentDescription = usdCard.brand ?: "",
                                                modifier = Modifier.height(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = ".... ${usdCard.last4 ?: ""}",
                                                fontFamily = MulishFontFamily,
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = if (uiState.isBalanceVisible) "$${String.format("%,.2f", usdBal)}" else "•••••••• $",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 30.sp,
                                    color = Color.White
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "N° de compte CP : $userWalletId",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )

                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(userWalletId))
                                            Toast.makeText(context, "N° de compte copié !", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copier",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Card 3: Euro Wallet (€)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, Color.White.copy(alpha = 0.45f)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF6366F1),
                                            Color(0xFF4338CA),
                                            Color(0xFF1E1B4B)
                                        )
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .size(110.dp)
                                    .align(Alignment.BottomEnd)
                            )

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Solde Euro EUR",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { dashboardViewModel.toggleBalanceVisibility() },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (uiState.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Afficher/Masquer",
                                                tint = Color.White.copy(alpha = 0.85f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    val eurCard = walletResponse?.cards?.firstOrNull { it.currency?.uppercase() == "EUR" }
                                    if (eurCard != null) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(
                                                painter = painterResource(id = if (eurCard.brand?.lowercase() == "visa") R.drawable.logo_visa else R.drawable.logo_mastercard),
                                                contentDescription = eurCard.brand ?: "",
                                                modifier = Modifier.height(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = ".... ${eurCard.last4 ?: ""}",
                                                fontFamily = MulishFontFamily,
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = if (uiState.isBalanceVisible) "€${String.format("%,.2f", eurBal)}" else "•••••••• €",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 28.sp,
                                    color = Color.White
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "N° de compte CP : $userWalletId",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )

                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(userWalletId))
                                            Toast.makeText(context, "N° de compte copié !", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copier",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Card 4: Bitcoin Crypto Wallet (Mini Blockchain)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF181B24)),
                        border = BorderStroke(1.2.dp, Color(0xFFF59E0B).copy(alpha = 0.65f)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF2E2416),
                                            Color(0xFF1E160C),
                                            Color(0xFF100C06)
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyBitcoin,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier
                                    .size(120.dp)
                                    .align(Alignment.BottomEnd)
                            )

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Solde Bitcoin",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { dashboardViewModel.toggleBalanceVisibility() },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (uiState.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Afficher/Masquer",
                                                tint = Color.White.copy(alpha = 0.85f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF1E293B)
                                    ) {
                                        Text(
                                            text = "Mini Blockchain",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 10.sp,
                                            color = Color(0xFFF59E0B),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CurrencyBitcoin,
                                        contentDescription = "BTC",
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = if (uiState.isBalanceVisible) CashPayMessageTranslator.formatBtc(btcBal) else "•••••••• BTC",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp,
                                            color = Color.White
                                        )
                                        val liveBtcRate = if (uiState.btcLiveRateUsd > 1000.0) {
                                            uiState.btcLiveRateUsd
                                        } else {
                                            uiState.exchangeRatesMap["BTC_USD"] ?: 82000.0
                                        }
                                        val btcUsdValue = btcBal * liveBtcRate
                                        Text(
                                            text = if (uiState.isBalanceVisible) {
                                                "≈ $ ${String.format(java.util.Locale.US, "%,.2f", btcUsdValue)} USD"
                                            } else "•••• USD",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                        Text(
                                            text = "1 BTC = $ ${String.format(java.util.Locale.US, "%,.2f", liveBtcRate)} USD",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 10.sp,
                                            color = Color(0xFFF59E0B)
                                        )
                                    }
                                }

                                // Quick Action Buttons (Organized with icons and high contrast)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BtcCardActionButton(
                                        label = "Recevoir",
                                        icon = Icons.Default.CallReceived,
                                        modifier = Modifier.weight(1f)
                                    ) { dashboardViewModel.openReceiveBtcDialog() }

                                    BtcCardActionButton(
                                        label = "Envoyer",
                                        icon = Icons.Default.Send,
                                        modifier = Modifier.weight(1f)
                                    ) { dashboardViewModel.openBtcTransferDialog() }

                                    BtcCardActionButton(
                                        label = "Acheter",
                                        icon = Icons.Default.ShoppingCart,
                                        modifier = Modifier.weight(1f)
                                    ) { dashboardViewModel.openBuyBtcDialog() }

                                    BtcCardActionButton(
                                        label = "Vendre",
                                        icon = Icons.Default.CurrencyExchange,
                                        modifier = Modifier.weight(1f)
                                    ) { dashboardViewModel.openSellBtcDialog() }
                                }
                            }
                        }
                    }
                }
                4 -> {
                    // Card 5: USDT TRC-20 (Réseau TRON)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(188.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.2.dp, Color(0xFF26A17B).copy(alpha = 0.75f)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF064E3B),
                                            Color(0xFF042F2E),
                                            Color(0xFF021B17)
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "₮",
                                fontSize = 120.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White.copy(alpha = 0.05f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 10.dp, y = 20.dp)
                            )

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Solde USDT",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { dashboardViewModel.toggleBalanceVisibility() },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (uiState.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Afficher/Masquer",
                                                tint = Color.White.copy(alpha = 0.85f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF064E3B)
                                    ) {
                                        Text(
                                            text = "TRC20 • TRON",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF26A17B),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF26A17B).copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "₮",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF26A17B),
                                            fontFamily = MulishFontFamily
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (uiState.isBalanceVisible) {
                                                "${String.format(java.util.Locale.US, "%,.4f", uiState.usdtBalance)} USDT"
                                            } else "•••••••• USDT",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (uiState.isBalanceVisible) {
                                                "≈ $ ${String.format(java.util.Locale.US, "%,.2f", uiState.usdtBalance)} USD"
                                            } else "•••• USD",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                        val onChainStatus = if (uiState.usdtOnChainBalance != null) {
                                            "On-chain : ${String.format(java.util.Locale.US, "%,.2f", uiState.usdtOnChainBalance)} USDT"
                                        } else {
                                            "Solde blockchain indisponible"
                                        }
                                        Text(
                                            text = onChainStatus,
                                            fontFamily = MulishFontFamily,
                                            fontSize = 10.sp,
                                            color = if (uiState.usdtOnChainBalance != null) Color(0xFF26A17B) else Color(0xFFF59E0B)
                                        )
                                    }
                                }

                                // Quick Action Buttons (Recevoir, Envoyer, Acheter, Vendre, Dépôts)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    UsdtCardActionButton(
                                        label = "Recevoir",
                                        icon = Icons.Default.CallReceived,
                                        modifier = Modifier.weight(1f)
                                    ) { dashboardViewModel.openReceiveUsdtDialog() }

                                    UsdtCardActionButton(
                                        label = "Envoyer",
                                        icon = Icons.Default.Send,
                                        modifier = Modifier.weight(1f)
                                    ) { dashboardViewModel.openSendUsdtDialog() }

                                    UsdtCardActionButton(
                                        label = "Acheter",
                                        icon = Icons.Default.ShoppingCart,
                                        modifier = Modifier.weight(1f)
                                    ) { dashboardViewModel.openBuyUsdtDialog() }

                                    UsdtCardActionButton(
                                        label = "Vendre",
                                        icon = Icons.Default.CurrencyExchange,
                                        modifier = Modifier.weight(1f)
                                    ) { dashboardViewModel.openSellUsdtDialog() }

                                    UsdtCardActionButton(
                                        label = "Dépôts",
                                        icon = Icons.Default.History,
                                        modifier = Modifier.weight(1f)
                                    ) { dashboardViewModel.openUsdtDepositsDialog() }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Carousel Indicator Dots (5 dots for the 5 balance cards: CDF, USD, EUR, BTC, USDT)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { i ->
                val isSelected = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 22.dp else 6.dp, 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.45f))
                        .clickable {
                            coroutineScope.launch { pagerState.animateScrollToPage(i) }
                        }
                )
            }
        }
    }
}

@Composable
private fun ToofanDashboardTab(
    userProfile: UserProfileEntity?,
    transactions: List<TransactionEntity>,
    notifications: List<NotificationEntity>,
    walletResponse: WalletResponse?,
    uiState: DashboardUiState,
    dashboardViewModel: DashboardViewModel,
    onOpenTransfer: () -> Unit,
    onOpenServices: () -> Unit,
    onOpenQr: () -> Unit,
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onContactClick: (com.example.data.model.PhoneContact) -> Unit = {},
    onActionPlusClick: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()
    val unreadNotifCount = remember(notifications) { notifications.count { !it.read } }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val natCode = walletResponse?.nationalCurrency?.code ?: "CDF"
    val natName = walletResponse?.nationalCurrency?.name ?: (if (natCode.contains("CDF")) "Franc Congolais" else if (natCode.contains("XOF") || natCode.contains("XAF")) "Franc CFA" else "Devise Nationale")
    val natSymbol = walletResponse?.nationalCurrency?.symbol ?: (if (natCode == "CDF") "FC" else if (natCode.contains("XOF") || natCode.contains("XAF")) "CFA" else natCode)
    val natBal = walletResponse?.balances?.fiat?.get(natCode) ?: (walletResponse?.balances?.fiat?.get("national") ?: 0.0)

    val usdBal = walletResponse?.balances?.fiat?.get("USD") ?: 0.0
    val eurBal = walletResponse?.balances?.fiat?.get("EUR") ?: 0.0
    val btcBal = walletResponse?.bitcoin?.balance ?: 0.0
    val btcAddress = walletResponse?.bitcoin?.address ?: ""

    val userWalletId = userProfile?.walletId ?: ""

    // Derive up to 3 recent correspondents dynamically from real transfers & state (NO MOCK DATA)
    val recentCorrespondents = remember(transactions, uiState.recentCorrespondents) {
        val list = mutableListOf<com.example.data.model.PhoneContact>()
        // 1. Add any manually interacted or searched contacts
        uiState.recentCorrespondents.forEach { c ->
            if (list.none { it.name.equals(c.name, ignoreCase = true) || (c.publicProfile?.walletId != null && it.publicProfile?.walletId == c.publicProfile?.walletId) }) {
                list.add(c)
            }
        }
        // 2. Extract unique correspondents from real transfer transactions
        transactions.filter { tx ->
            tx.type in listOf("TR", "TRANSFER", "TRANSFER_RECEIVED", "TRANSFER_SENT") ||
            tx.displayKind == "person" ||
            !tx.otherUserFullName.isNullOrBlank()
        }.forEach { tx ->
            val name = tx.otherUserFullName ?: tx.displayName.ifBlank { tx.title }
            val walletId = if (tx.direction.lowercase() == "incoming") tx.senderWalletId else (tx.receiverWalletId ?: tx.recipient)
            val avatar = tx.otherUserProfilePhoto ?: tx.displayAvatar
            if (name.isNotBlank() && list.none { it.name.equals(name, ignoreCase = true) || (walletId != null && it.publicProfile?.walletId == walletId) }) {
                list.add(
                    com.example.data.model.PhoneContact(
                        name = name,
                        phone = "",
                        normalizedPhone = "",
                        lastNineDigits = "",
                        isCashPayUser = true,
                        publicProfile = com.example.data.model.PublicProfileDto(
                            walletId = walletId ?: "",
                            fullName = name,
                            profilePhotoUrl = avatar,
                            profilePhoto = avatar
                        )
                    )
                )
            }
        }
        list.take(3)
    }

    // Filter transactions for Dashboard (limit 5)
    val filteredDashboardTx = remember(transactions, uiState.txDirectionFilter) {
        val filtered = transactions.filter { tx ->
            when (uiState.txDirectionFilter) {
                "INCOMING" -> tx.direction.lowercase() == "incoming"
                "OUTGOING" -> tx.direction.lowercase() == "outgoing"
                else -> true
            }
        }
        filtered.take(5)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ToofanBgColor)
    ) {
        // Pinned Top Bar Header: Anchored at the top, hosting user info & balance cards carousel directly on the header layer
        CashPayTopHeader(
            userProfile = userProfile,
            unreadNotifCount = unreadNotifCount,
            onNotificationClick = { dashboardViewModel.openNotificationDialog() },
            onSettingsClick = onSettingsClick,
            height = null,
            content = {
                BalanceCardsCarousel(
                    pagerState = pagerState,
                    walletResponse = walletResponse,
                    userProfile = userProfile,
                    uiState = uiState,
                    dashboardViewModel = dashboardViewModel,
                    coroutineScope = coroutineScope,
                    onOpenTransfer = onOpenTransfer
                )
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
        ) {
            // Action Buttons: Agent (if role agent), Retrait, Crédit, Transfer, Payment, Services
            val isAgentUser = userProfile?.role?.lowercase()?.trim() == "agent"

        if (isAgentUser) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { dashboardViewModel.openAgentServicesScreen() },
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF000E38),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Espace Agent CashPay Agréé",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Commissions : ${if (uiState.isAgentBalanceVisible) "${String.format(java.util.Locale.US, "%.2f", uiState.agentCommissionUsd)} $ • ${String.format(java.util.Locale.US, "%,.0f", uiState.agentCommissionCdf)} FC" else "••••••••"}",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White
                        ) {
                            Text(
                                text = "Gérer",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF000E38),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Widget Installation Banner (Shown ONLY if the widget is NOT already configured on the home screen)
        item {
            val isWidgetConfigured = remember {
                com.example.widget.NotificationWidgetProvider.isWidgetInstalled(context)
            }
            var widgetBannerDismissed by remember { mutableStateOf(false) }

            if (!isWidgetConfigured && !widgetBannerDismissed) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF000E38),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF6600).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Widget",
                                    tint = Color(0xFFFF6600),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Widget Notifications CashPay",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Suivez vos alertes et transactions en temps réel depuis votre écran d'accueil.",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    val appWidgetManager = context.getSystemService(android.appwidget.AppWidgetManager::class.java)
                                    if (appWidgetManager?.isRequestPinAppWidgetSupported == true) {
                                        val myProvider = android.content.ComponentName(context, com.example.widget.NotificationWidgetProvider::class.java)
                                        appWidgetManager.requestPinAppWidget(myProvider, null, null)
                                        Toast.makeText(context, "Demande d'installation du widget envoyée !", Toast.LENGTH_SHORT).show()
                                        widgetBannerDismissed = true
                                    } else {
                                        Toast.makeText(context, "Maintenez l'écran d'accueil pour ajouter le widget CashPay.", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Maintenez l'écran d'accueil pour ajouter le widget CashPay.", Toast.LENGTH_LONG).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Installer",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Section Contacts Récents
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Contacts récents",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Voir tout",
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue,
                        modifier = Modifier.clickable { dashboardViewModel.openContactsPage() }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    val ringColors = listOf(BrandBlue, BrandOrange, BrandBlueVibrant)

                    // Up to 3 recent correspondents from transfers
                    recentCorrespondents.take(3).forEachIndexed { index, correspondent ->
                        RecentCorrespondentItem(
                            contact = correspondent,
                            ringColor = ringColors.getOrElse(index) { BrandBlue },
                            onClick = { onContactClick(correspondent) }
                        )
                    }

                    // Button 4: "+ Ajouter"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(62.dp)
                            .clickable { dashboardViewModel.openAddContactDialog() }
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = BrandOrange,
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Ajouter",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ajouter",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Button 5: "Contacts"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(62.dp)
                            .clickable { dashboardViewModel.openContactsPage() }
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = Color(0xFF000E38),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Contacts",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Contacts",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Ligne 1: 4 boutons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ToofanActionSquare(
                        title = "Envoyer\nTransfert",
                        bgColor = BrandOrange,
                        icon = Icons.Default.Send,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenTransfer
                    )
                    ToofanActionSquare(
                        title = "Retrait\nEspèces",
                        bgColor = BrandBlue,
                        icon = Icons.Default.CallReceived,
                        modifier = Modifier.weight(1f),
                        onClick = { dashboardViewModel.openWithdrawalDialog() }
                    )
                    ToofanActionSquare(
                        title = "Crédit\n& Prêts",
                        bgColor = BrandBlue,
                        icon = Icons.Default.MonetizationOn,
                        modifier = Modifier.weight(1f),
                        onClick = { dashboardViewModel.openLoansDialog() }
                    )
                    ToofanActionSquare(
                        title = if (isAgentUser) "Espace\nAgent" else "PayScan\nPanier/POS",
                        bgColor = BrandBlue,
                        icon = if (isAgentUser) Icons.Default.SupportAgent else Icons.Default.QrCode,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isAgentUser) dashboardViewModel.openAgentServicesScreen()
                            else dashboardViewModel.openPayScan(PayScanMode.SUPERMARKET_BASKET)
                        }
                    )
                }

                // Ligne 2: 4 boutons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ToofanActionSquare(
                        title = "Dépôt\nRecharger",
                        bgColor = BrandOrange,
                        icon = Icons.Default.Add,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            dashboardViewModel.openDepositDialog()
                        }
                    )
                    ToofanActionSquare(
                        title = "Paiement\nFactures",
                        bgColor = BrandBlue,
                        icon = Icons.Default.Receipt,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenServices
                    )
                    ToofanActionSquare(
                        title = "Mes\nCartes",
                        bgColor = BrandBlue,
                        icon = Icons.Default.CreditCard,
                        modifier = Modifier.weight(1f),
                        onClick = { dashboardViewModel.setSelectedTab(2) }
                    )
                    ToofanActionSquare(
                        title = "Voir\nPlus",
                        bgColor = BrandBlue,
                        icon = Icons.Default.FilterList,
                        modifier = Modifier.weight(1f),
                        onClick = onActionPlusClick
                    )
                }
            }
        }

        // Section Transactions & Filter Capsule
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transactions",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Voir tout",
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000E38),
                        modifier = Modifier.clickable { dashboardViewModel.setSelectedTab(1) }
                    )
                }

                // Filter Capsule Container
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("ALL" to "Toutes", "INCOMING" to "Entrants (+)", "OUTGOING" to "Sortants (-)").forEach { (code, label) ->
                            val selected = uiState.txDirectionFilter == code
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { dashboardViewModel.setTxDirectionFilter(code) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) Color(0xFF000E38) else Color.Transparent
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dashboard Recent Transactions List (limit 5)
        items(filteredDashboardTx, key = { it.id }) { tx ->
            TransactionItemRow(
                tx = tx,
                onClick = { dashboardViewModel.selectTransactionForDetail(tx) }
            )
        }

        // Official Partners & Payment Networks Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ToofanWhite),
                elevation = CardDefaults.cardElevation(1.dp)
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
                            text = "Réseaux & Partenaires Officiels",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ToofanMainDark
                        )
                        Image(
                            painter = painterResource(id = R.drawable.badge_verified),
                            contentDescription = "Partenaires certifiés",
                            modifier = Modifier.height(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Mobile Money pris en charge",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ToofanBodyText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PartnerLogoPill(drawableId = R.drawable.logo_mpesa, name = "M-Pesa")
                        PartnerLogoPill(drawableId = R.drawable.logo_orange_money, name = "Orange")
                        PartnerLogoPill(drawableId = R.drawable.logo_airtel_money, name = "Airtel")
                        PartnerLogoPill(drawableId = R.drawable.logo_mtn_money, name = "MTN")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Cartes & Transferts Internationaux",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ToofanBodyText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PartnerLogoPill(drawableId = R.drawable.logo_visa_blue, name = "Visa")
                        PartnerLogoPill(drawableId = R.drawable.logo_mastercard, name = "Mastercard")
                        PartnerLogoPill(drawableId = R.drawable.logo_paypal, name = "PayPal")
                        PartnerLogoPill(drawableId = R.drawable.logo_western_union, name = "Western Union")
                        PartnerLogoPill(drawableId = R.drawable.logo_moneygram, name = "MoneyGram")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = ToofanGreen.copy(alpha = 0.08f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ToofanGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Conformité de paiement sécurisé",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ToofanGreen
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.logo_pci_dss),
                                contentDescription = "PCI DSS Certified",
                                modifier = Modifier.height(20.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    }
}

@Composable
private fun BtcCardActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF261D13),
        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontFamily = MulishFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun UsdtCardActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF042F2E),
        border = BorderStroke(1.dp, Color(0xFF26A17B).copy(alpha = 0.65f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF26A17B),
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontFamily = MulishFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TransactionItemRow(
    tx: TransactionEntity,
    onClick: () -> Unit
) {
    val isFailed = tx.status.lowercase() in listOf("failed", "cancelled", "rejected", "error", "echoue", "échec")
    val isDeposit = tx.type.uppercase() in listOf("DP", "DP_AGENT", "DEPOSIT", "RECHARGE", "MOMO_DEPOSIT") || tx.title.contains("recharge", ignoreCase = true) || tx.title.contains("dépôt", ignoreCase = true)
    val isCredit = (tx.direction.lowercase() == "incoming" || isDeposit) && !isFailed
    val typeInfo = TransactionTypeMapper.getTypeInfo(tx.type)
    val isPerson = !isDeposit && (tx.displayKind == "person" || (typeInfo.isPersonToPerson && !tx.otherUserFullName.isNullOrEmpty()))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        shadowElevation = 0.5.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Avatar for Person or Styled Service Icon for Service
                if (isPerson) {
                    Box(
                        modifier = Modifier.size(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, if (isCredit) SuccessGreen else Color(0xFF3B82F6), CircleShape)
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            val avatarUrl = tx.displayAvatar ?: tx.otherUserProfilePhoto
                            if (!avatarUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                val initials = (tx.otherUserFullName ?: tx.displayName.ifBlank { tx.title })
                                    .take(2).uppercase()
                                Text(
                                    text = initials,
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isCredit) SuccessGreen else Color(0xFF3B82F6)
                                )
                            }
                        }
                        // Green status indicator
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                } else {
                    // Service Icon (Airtel Money, Netflix, etc.)
                    val isOrangeService = tx.title.contains("Airtel", ignoreCase = true) || tx.title.contains("Orange", ignoreCase = true)
                    val serviceBgColor = if (isOrangeService) Color(0xFFF59E0B) else Color(0xFF0D9488)
                    Surface(
                        shape = CircleShape,
                        color = serviceBgColor,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = typeInfo.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    val rawName = tx.otherUserFullName ?: tx.displayName.ifBlank { tx.title }
                    val heading = if (isPerson) {
                        if (isCredit) {
                            if (!rawName.startsWith("De", ignoreCase = true)) "De : $rawName" else rawName
                        } else {
                            if (!rawName.startsWith("À", ignoreCase = true) && !rawName.startsWith("Vous →")) "À : $rawName" else rawName
                        }
                    } else {
                        tx.displayName.ifBlank { tx.title }
                    }

                    Text(
                        text = heading,
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    val typeText = if (isDeposit && isFailed) {
                        "Échec de recharge • Non débité"
                    } else if (isFailed) {
                        "Transaction échouée"
                    } else if (isPerson) {
                        if (isCredit) "Transfert reçu • Crédit" else "Transfert envoyé • Débit"
                    } else {
                        TransactionTypeMapper.getDisplayTitle(tx.type, tx.direction)
                    }
                    Text(
                        text = typeText,
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = if (isFailed) Color(0xFFD97706) else Color(0xFF64748B),
                        fontWeight = if (isFailed) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            // Amount, Fee & Date/Time on Right
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val amountColor = when {
                    isFailed -> Color(0xFFD97706) // Amber for failed transactions (not debit)
                    isCredit -> SuccessGreen // Green
                    else -> Color(0xFFE11D48) // Red for true debit
                }
                val amountPrefix = when {
                    isFailed -> "" // Never minus sign on failed deposit/recharge
                    isCredit -> "+ "
                    else -> "- "
                }

                val isBtc = tx.currency.equals("BTC", ignoreCase = true)
                val displayAmount = if (isBtc) {
                    CashPayMessageTranslator.formatBtc(tx.amount)
                } else {
                    "${String.format(java.util.Locale.US, "%,.2f", tx.amount)} ${tx.currency}"
                }

                Text(
                    text = "$amountPrefix$displayAmount",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = amountColor
                )

                // Only show fees if outgoing (debit) and not failed.
                if (!isCredit && !isFailed && tx.fee > 0.0) {
                    val displayFee = if (isBtc) {
                        CashPayMessageTranslator.formatBtc(tx.fee)
                    } else {
                        "${String.format(java.util.Locale.US, "%,.2f", tx.fee)} ${tx.currency}"
                    }
                    Text(
                        text = "Frais : - $displayFee",
                        fontFamily = MulishFontFamily,
                        fontSize = 10.5.sp,
                        color = Color(0xFFE11D48).copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }

                if (isFailed) {
                    Text(
                        text = "Échoué",
                        fontFamily = MulishFontFamily,
                        fontSize = 10.sp,
                        color = Color(0xFFD97706),
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = tx.date,
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Détails",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun TransactionDetailDialog(
    transaction: TransactionEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isCredit = transaction.direction.lowercase() == "incoming"
    val isTxBtc = transaction.currency.equals("BTC", ignoreCase = true)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cashpay_logo),
                    contentDescription = "CashPay Logo",
                    modifier = Modifier
                        .height(32.dp)
                        .padding(bottom = 6.dp)
                )
                Text(
                    text = "Bordereau Officiel de Transaction",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ToofanMainDark
                )
                Text(
                    text = "CashPay • Powered by FINUSECO SA",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Status & Amount Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isCredit) Color(0xFFE8F5E9) else Color(0xFFFFECEF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (isCredit) ToofanGreen else Color(0xFFFF4868),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCredit) "CRÉDIT REÇU / TRANSACTION ENTRÉE" else "DÉBIT EFFECTUÉ / TRANSFERT ENVOYÉ",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = if (isCredit) ToofanGreen else Color(0xFFFF4868),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        val isTxBtc = transaction.currency.equals("BTC", ignoreCase = true)
                        val mainDispAmount = if (isTxBtc) CashPayMessageTranslator.formatBtc(transaction.amount) else "${String.format("%.2f", transaction.amount)} ${transaction.currency}"
                        Text(
                            text = "${if (isCredit) "+" else "-"} $mainDispAmount",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = ToofanMainDark
                        )

                        Text(
                            text = "Statut : ${CashPayMessageTranslator.translateStatus(transaction.status)} • CERTIFIÉ",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCredit) ToofanGreen else Color(0xFF2E7D32),
                            fontSize = 11.sp
                        )
                    }
                }

                // Financial Breakdown (Montant, Frais, Total)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val dispAmt = if (isTxBtc) CashPayMessageTranslator.formatBtc(transaction.amount) else "${String.format("%.2f", transaction.amount)} ${transaction.currency}"
                        val dispFee = if (isTxBtc) CashPayMessageTranslator.formatBtc(transaction.fee) else "${String.format("%.2f", transaction.fee)} ${transaction.currency}"
                        val dispTot = if (isTxBtc) CashPayMessageTranslator.formatBtc(transaction.amount + transaction.fee) else "${String.format("%.2f", transaction.amount + transaction.fee)} ${transaction.currency}"

                        if (!isCredit) {
                            DetailRow("Montant Transféré :", "- $dispAmt", isBold = true)
                            if (transaction.fee > 0.0) {
                                DetailRow("Frais Réseau / Service :", "- $dispFee")
                            }
                            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                            DetailRow("Total Débité :", "- $dispTot", isBold = true, customColor = Color(0xFFE11D48))
                        } else {
                            DetailRow("Montant Reçu :", "+ $dispAmt", isBold = true, customColor = SuccessGreen)
                            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                            DetailRow("Total Net Crédité :", "+ $dispAmt", isBold = true, customColor = SuccessGreen)
                        }
                    }
                }

                // Parties and Reference Details
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val typeInfo = TransactionTypeMapper.getTypeInfo(transaction.type)
                        DetailRow("Type Opération :", typeInfo.nameFr)
                        DetailRow("Référence Transaction :", transaction.reference, isBold = true)
                        DetailRow("Intitulé / Libellé :", transaction.displayName.ifBlank { transaction.title })
                        val otherName = transaction.otherUserFullName ?: transaction.displayName.ifBlank { transaction.title }
                        if (isCredit) {
                            DetailRow("Expéditeur (Source) :", otherName, isBold = true)
                            if (!transaction.senderWalletId.isNullOrEmpty()) {
                                DetailRow("Portefeuille Émetteur :", transaction.senderWalletId)
                            }
                        } else {
                            DetailRow("Bénéficiaire (Cible) :", otherName, isBold = true)
                            if (!transaction.receiverWalletId.isNullOrEmpty() || transaction.recipient.isNotBlank()) {
                                DetailRow("Portefeuille Cible :", transaction.receiverWalletId ?: transaction.recipient)
                            }
                        }
                        DetailRow("Horodatage :", transaction.date)
                    }
                }

                // Genuine ZXing QR Code Display for Authentication
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val qrBitmap = remember(transaction.reference) {
                            QrCodeGenerator.generateQrBitmap("https://cashpay-all.com/tx/${transaction.reference}", 240)
                        }
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Code Réel de Transaction",
                            modifier = Modifier
                                .size(130.dp)
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                .padding(6.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Scannez pour vérifier l'authenticité",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontFamily = MulishFontFamily
                        )
                    }
                }
            }
        },
        confirmButton = {
            ToofanButton(
                title = "Télécharger / Partager Reçu PDF",
                onClick = {
                    PdfReceiptGenerator.generateAndShareReceiptPdf(context, transaction)
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fermer", fontFamily = MulishFontFamily, color = ToofanBodyText, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String, isBold: Boolean = false, customColor: Color? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontFamily = MulishFontFamily)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = customColor ?: ToofanMainDark,
            fontFamily = MulishFontFamily
        )
    }
}

@Composable
private fun ToofanHistoryTab(
    userProfile: UserProfileEntity?,
    notifications: List<NotificationEntity>,
    uiState: DashboardUiState,
    transactions: List<TransactionEntity>,
    dashboardViewModel: DashboardViewModel
) {
    val unreadNotifCount = remember(notifications) { notifications.count { !it.read } }

    val filteredTx = remember(transactions, uiState.txDirectionFilter, uiState.txSearchQuery) {
        transactions.filter { tx ->
            val matchDir = when (uiState.txDirectionFilter) {
                "INCOMING" -> tx.direction.lowercase() == "incoming"
                "OUTGOING" -> tx.direction.lowercase() == "outgoing"
                else -> true
            }
            val matchQuery = if (uiState.txSearchQuery.isBlank()) true else {
                tx.title.contains(uiState.txSearchQuery, ignoreCase = true) ||
                        tx.reference.contains(uiState.txSearchQuery, ignoreCase = true) ||
                        tx.displayName.contains(uiState.txSearchQuery, ignoreCase = true)
            }
            matchDir && matchQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ToofanBgColor)
    ) {
        CashPayTopHeader(
            userProfile = userProfile,
            unreadNotifCount = unreadNotifCount,
            onNotificationClick = { dashboardViewModel.openNotificationDialog() },
            onSettingsClick = { dashboardViewModel.setSelectedTab(2) },
            height = 85.dp
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Historique des Transactions",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )

                ToofanInputField(
                    value = uiState.txSearchQuery,
                    onValueChange = { dashboardViewModel.onSearchQueryChanged(it) },
                    placeholder = "Rechercher par nom, référence ou montant..."
                )

                // Direction Filters Capsule
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("ALL" to "Toutes", "INCOMING" to "Entrants (+)", "OUTGOING" to "Sortants (-)").forEach { (code, label) ->
                            val selected = uiState.txDirectionFilter == code
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { dashboardViewModel.setTxDirectionFilter(code) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) Color(0xFF000E38) else Color.Transparent
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        items(filteredTx, key = { it.id }) { tx ->
            TransactionItemRow(
                tx = tx,
                onClick = { dashboardViewModel.selectTransactionForDetail(tx) }
            )
        }

        if (filteredTx.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune transaction trouvée",
                        fontFamily = MulishFontFamily,
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
    }
}

@Composable
private fun AllTransactionsDialog(
    uiState: DashboardUiState,
    transactions: List<TransactionEntity>,
    dashboardViewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    var fromInput by remember { mutableStateOf(uiState.customFromDate) }
    var toInput by remember { mutableStateOf(uiState.customToDate) }

    val filteredTx = remember(transactions, uiState.txDirectionFilter, uiState.txSearchQuery) {
        transactions.filter { tx ->
            val matchDir = when (uiState.txDirectionFilter) {
                "INCOMING" -> tx.direction.lowercase() == "incoming"
                "OUTGOING" -> tx.direction.lowercase() == "outgoing"
                else -> true
            }
            val matchQuery = if (uiState.txSearchQuery.isBlank()) true else {
                tx.title.contains(uiState.txSearchQuery, ignoreCase = true) ||
                        tx.reference.contains(uiState.txSearchQuery, ignoreCase = true) ||
                        tx.displayName.contains(uiState.txSearchQuery, ignoreCase = true)
            }
            matchDir && matchQuery
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ToofanBgColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Surface(
                color = ToofanWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = ToofanMainDark)
                    }
                    Text(
                        text = "Historique des Transactions",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ToofanMainDark
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Input
                ToofanInputField(
                    value = uiState.txSearchQuery,
                    onValueChange = { dashboardViewModel.onSearchQueryChanged(it) },
                    placeholder = "Rechercher par nom, référence ou montant..."
                )

                // Direction Filters
                Text("Sens de transaction :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALL" to "Toutes", "INCOMING" to "Entrants (+)", "OUTGOING" to "Sortants (-)").forEach { (code, label) ->
                        val selected = uiState.txDirectionFilter == code
                        Surface(
                            modifier = Modifier.clickable { dashboardViewModel.setTxDirectionFilter(code) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) ToofanGreen else Color.White,
                            border = BorderStroke(1.dp, if (selected) ToofanGreen else Color.LightGray)
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else ToofanMainDark
                            )
                        }
                    }
                }

                // Date Filters
                Text("Période :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALL" to "Toutes", "TODAY" to "Aujourd'hui", "WEEK" to "Cette semaine", "MONTH" to "Ce mois", "CUSTOM" to "Date").forEach { (code, label) ->
                        val selected = uiState.txDateFilter == code
                        Surface(
                            modifier = Modifier.clickable { dashboardViewModel.setTxDateFilter(code) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) ToofanBlue else Color.White,
                            border = BorderStroke(1.dp, if (selected) ToofanBlue else Color.LightGray)
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) Color.White else ToofanBodyText
                            )
                        }
                    }
                }

                if (uiState.txDateFilter == "CUSTOM") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = fromInput,
                            onValueChange = { fromInput = it },
                            label = { Text("Du (YYYY-MM-DD)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = toInput,
                            onValueChange = { toInput = it },
                            label = { Text("Au (YYYY-MM-DD)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        ToofanButton(
                            title = "Filtrer",
                            onClick = { dashboardViewModel.setCustomDates(fromInput, toInput) },
                            modifier = Modifier.width(80.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)
            ) {
                items(filteredTx, key = { it.id }) { tx ->
                    TransactionItemRow(
                        tx = tx,
                        onClick = { dashboardViewModel.selectTransactionForDetail(tx) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PartnerLogoPill(
    drawableId: Int,
    name: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, ToofanGrey1.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
        color = ToofanWhite,
        shadowElevation = 0.5.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = name,
                modifier = Modifier.height(22.dp)
            )
        }
    }
}

@Composable
private fun ToofanServiceSquare(
    title: String,
    bgColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = title,
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ToofanProfileTab(
    userProfile: UserProfileEntity?,
    notifications: List<NotificationEntity>,
    dashboardViewModel: DashboardViewModel,
    onEditClick: () -> Unit,
    onNavigateSecurity: () -> Unit = {}
) {
    val unreadNotifCount = remember(notifications) { notifications.count { !it.read } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ToofanBgColor)
    ) {
        CashPayTopHeader(
            userProfile = userProfile,
            unreadNotifCount = unreadNotifCount,
            onNotificationClick = { dashboardViewModel.openNotificationDialog() },
            onSettingsClick = onNavigateSecurity,
            height = 85.dp
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp)
        ) {
            item {
                // Profile Header Card with Avatar & Edit Action
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF000E38)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0A1E5C))
                            .border(2.dp, Color(0xFF00E5FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!userProfile?.profilePhotoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = userProfile?.profilePhotoUrl,
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            val initials = (userProfile?.fullName ?: "CP")
                                .trim()
                                .split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString("")
                                .uppercase()
                                .ifBlank { "CP" }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFF0A1E5C),
                                                Color(0xFF00E5FF)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = userProfile?.fullName ?: "",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Image(
                            painter = painterResource(id = R.drawable.badge_verified),
                            contentDescription = "Compte Certifié",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = userProfile?.profession ?: "—",
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ToofanButton(
                        title = "Modifier les informations (PIN)",
                        onClick = onEditClick,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        testTag = "edit_profile_button"
                    )
                }
            }
        }

        // Category 1: Identité & Contact
        item {
            ToofanProfileCategorySection(
                title = "Identité & Contact",
                icon = Icons.Default.Badge,
                items = listOf(
                    "Nom complet" to (userProfile?.fullName ?: "—"),
                    "Prénom" to (userProfile?.firstName ?: "—"),
                    "Nom" to (userProfile?.lastName ?: "—"),
                    "Post-nom" to (userProfile?.middleName ?: "—"),
                    "Genre" to (userProfile?.gender ?: "—"),
                    "État civil" to (userProfile?.maritalStatus ?: "—"),
                    "Date de naissance" to (userProfile?.birthDate ?: "—"),
                    "Lieu de naissance" to (userProfile?.birthPlace ?: "—"),
                    "Nationalité" to (userProfile?.nationality ?: "—"),
                    "Téléphone" to (userProfile?.phone ?: "—"),
                    "Email" to (userProfile?.email ?: "—")
                )
            )
        }

        // Category 2: Localisation & Résidence
        item {
            ToofanProfileCategorySection(
                title = "Localisation & Résidence",
                icon = Icons.Default.LocationOn,
                items = listOf(
                    "Pays" to (userProfile?.country ?: "—"),
                    "Province" to (userProfile?.province ?: "—"),
                    "Ville" to (userProfile?.city ?: "—"),
                    "Adresse" to (userProfile?.address ?: "—"),
                    "Agence" to (userProfile?.representative ?: "—")
                )
            )
        }

        // Category 3: Emploi & Finances
        item {
            ToofanProfileCategorySection(
                title = "Emploi & Finances",
                icon = Icons.Default.Work,
                items = listOf(
                    "Profession" to (userProfile?.profession ?: "—"),
                    "Activité" to (userProfile?.activityDescription ?: "—"),
                    "Revenu mensuel" to (userProfile?.incomePerMonth ?: "—"),
                    "Crypto actif" to (if (userProfile?.isCryptoActive == true) "Oui" else "Non")
                )
            )
        }

        // Category 4: Système & USSD
        item {
            ToofanProfileCategorySection(
                title = "Système & USSD",
                icon = Icons.Default.Shield,
                items = listOf(
                    "Wallet ID" to (userProfile?.walletId ?: "—"),
                    "Langue" to (userProfile?.language ?: "—"),
                    "Langue USSD" to (userProfile?.ussdLanguage ?: "—"),
                    "Canal de notif." to (userProfile?.notificationsChannel ?: "—"),
                    "Statut" to (userProfile?.status ?: "—"),
                    "KYC" to (userProfile?.verificationStatus ?: "—")
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    }
}

@Composable
private fun ToofanProfileCategorySection(
    title: String,
    icon: ImageVector,
    items: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = ToofanWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = ToofanGreen.copy(alpha = 0.12f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(16.dp))
                    }
                }
                Text(
                    text = title,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ToofanMainDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            items.forEachIndexed { index, (k, v) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = k, fontFamily = MulishFontFamily, fontSize = 13.sp, color = ToofanBodyText)
                    Text(text = v, fontFamily = MulishFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = ToofanMainDark)
                }
                if (index < items.size - 1) {
                    HorizontalDivider(color = ToofanGrey1.copy(alpha = 0.4f), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun ToofanSecurityTab(
    session: SessionEntity?,
    userProfile: UserProfileEntity?,
    onToggleBiometrics: (Boolean) -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    var isBiometricActive by remember { mutableStateOf(session?.biometricEnabled ?: true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Sécurité & Accès",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = ToofanMainDark
            )
        }

        // Biometrics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ToofanWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ToofanGreen.copy(alpha = 0.12f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(22.dp))
                            }
                        }
                        Column {
                            Text("Déverrouillage biométrique", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ToofanMainDark)
                            Text("Empreinte & Reconnaissance faciale", fontFamily = MulishFontFamily, fontSize = 12.sp, color = ToofanBodyText)
                        }
                    }

                    Switch(
                        checked = isBiometricActive,
                        onCheckedChange = {
                            isBiometricActive = it
                            onToggleBiometrics(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ToofanGreen
                        ),
                        modifier = Modifier.testTag("biometric_switch")
                    )
                }
            }
        }

        // PIN validation card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ToofanWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ToofanGreen.copy(alpha = 0.12f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(22.dp))
                            }
                        }
                        Column {
                            Text("Code PIN CashPay", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ToofanMainDark)
                            Text("Requis pour transactions & modifications", fontFamily = MulishFontFamily, fontSize = 12.sp, color = ToofanBodyText)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ToofanButton(
                        title = "Tester modification profil avec PIN",
                        onClick = onEditProfile
                    )
                }
            }
        }

        // Logout Button
        item {
            Button(
                onClick = onLogout,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFECEF),
                    contentColor = Color(0xFFFF4868)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_button")
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Se déconnecter", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ToofanEditProfileDialog(
    uiState: DashboardUiState,
    userProfile: UserProfileEntity?,
    onDismiss: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onProfessionChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = ToofanGreen)
                Text("Modifier le profil", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Mettez à jour vos informations personnelles en toute sécurité",
                    fontFamily = MulishFontFamily,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                ToofanInputField(
                    value = uiState.editFullName,
                    onValueChange = onFullNameChange,
                    placeholder = "Nom complet"
                )

                ToofanInputField(
                    value = uiState.editCity,
                    onValueChange = onCityChange,
                    placeholder = "Ville / Commune"
                )

                ToofanInputField(
                    value = uiState.editProfession,
                    onValueChange = onProfessionChange,
                    placeholder = "Profession"
                )

                ToofanInputField(
                    value = uiState.editAddress,
                    onValueChange = onAddressChange,
                    placeholder = "Adresse"
                )

                ToofanInputField(
                    value = uiState.editPinConfirmation,
                    onValueChange = onPinChange,
                    placeholder = "Code PIN CashPay"
                )

                if (uiState.editErrorMessage != null) {
                    Text(
                        text = uiState.editErrorMessage,
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFFFF4868)
                    )
                }
            }
        },
        confirmButton = {
            ToofanButton(
                title = "Valider",
                onClick = onSubmit,
                isLoading = uiState.isEditingLoading,
                modifier = Modifier.width(100.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", fontFamily = MulishFontFamily, color = ToofanBodyText)
            }
        }
    )
}

@Composable
private fun NotificationDialog(
    uiState: DashboardUiState,
    notifications: List<NotificationEntity>,
    dashboardViewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val unreadCount = remember(notifications) { notifications.count { !it.read } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = ToofanGreen)
                    Text("Notifications ($unreadCount non lues)", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                if (unreadCount > 0) {
                    Text(
                        text = "Tout marquer lu",
                        fontFamily = MulishFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ToofanGreen,
                        modifier = Modifier.clickable { dashboardViewModel.markAllNotificationsAsRead() }
                    )
                }
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune notification pour le moment",
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        color = ToofanBodyText
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        val (icon, tint) = when (notif.type.lowercase()) {
                            "new_message" -> Icons.Default.Chat to ToofanGreen
                            "missed_audio_call", "missed_video_call" -> Icons.Default.Phone to Color(0xFFFF4868)
                            "pos_sale" -> Icons.Default.ShoppingCart to ToofanBlue
                            "game_invite" -> Icons.Default.SportsEsports to ToofanOrange
                            "new_follower" -> Icons.Default.PersonAdd to Color(0xFF8B5CF6)
                            "meeting_invite" -> Icons.Default.Event to Color(0xFF14B8A6)
                            else -> Icons.Default.Notifications to ToofanBodyText
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (!notif.read) ToofanGreen.copy(alpha = 0.08f) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (!notif.read) ToofanGreen.copy(alpha = 0.3f) else ToofanGrey1.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (!notif.read) {
                                        dashboardViewModel.markNotificationAsRead(notif.id)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = tint.copy(alpha = 0.15f),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notif.title,
                                            fontFamily = MulishFontFamily,
                                            fontWeight = if (!notif.read) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = ToofanMainDark
                                        )
                                        if (!notif.read) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(ToofanGreen)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.description,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        color = ToofanBodyText
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notif.timestamp,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            ToofanButton(
                title = "Fermer",
                onClick = onDismiss,
                modifier = Modifier.width(100.dp)
            )
        }
    )
}

@Composable
fun ToofanCardsAndSecurityTab(
    uiState: DashboardUiState,
    userProfile: UserProfileEntity?,
    notifications: List<NotificationEntity>,
    session: SessionEntity?,
    dashboardViewModel: DashboardViewModel,
    onLogout: () -> Unit
) {
    val unreadNotifCount = remember(notifications) { notifications.count { !it.read } }
    var isCardsTabActive by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ToofanBgColor)
    ) {
        CashPayTopHeader(
            userProfile = userProfile,
            unreadNotifCount = unreadNotifCount,
            onNotificationClick = { dashboardViewModel.openNotificationDialog() },
            onSettingsClick = { isCardsTabActive = false },
            height = 190.dp
        )

        // Top Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .background(Color.White, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (isCardsTabActive) ToofanGreen else Color.Transparent)
                    .clickable { isCardsTabActive = true },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = if (isCardsTabActive) Color.White else ToofanMainDark, modifier = Modifier.size(16.dp))
                    Text(
                        "Mes Cartes",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isCardsTabActive) Color.White else ToofanMainDark
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (!isCardsTabActive) ToofanGreen else Color.Transparent)
                    .clickable { isCardsTabActive = false },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = if (!isCardsTabActive) Color.White else ToofanMainDark, modifier = Modifier.size(16.dp))
                    Text(
                        "Sécurité & PIN",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (!isCardsTabActive) Color.White else ToofanMainDark
                    )
                }
            }
        }

        if (isCardsTabActive) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Vos cartes de paiement",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = ToofanMainDark
                            )
                            Text(
                                "Visa et Mastercard prêtes à l'emploi",
                                fontFamily = MulishFontFamily,
                                fontSize = 12.sp,
                                color = ToofanBodyText
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ToofanGreen.copy(alpha = 0.12f),
                            modifier = Modifier.clickable { dashboardViewModel.openBuyCardDialog() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(16.dp))
                                Text("Commander", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ToofanGreen)
                            }
                        }
                    }
                }

                if (uiState.isLoadingCards) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ToofanGreen)
                        }
                    }
                } else if (uiState.userCards.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Sample Vertical Brand Card ("tout droit, pas en paysage")
                            CashPayCardComponent(
                                card = com.example.data.model.CardInfoDto(
                                    id = "preview",
                                    brand = "visa",
                                    cardType = "virtuelle",
                                    currency = "USD",
                                    last4 = "4820",
                                    status = "active"
                                ),
                                onFreeze = { dashboardViewModel.openBuyCardDialog() },
                                onUnfreeze = { dashboardViewModel.openBuyCardDialog() },
                                onManage = { dashboardViewModel.openBuyCardDialog() }
                            )

                            Text(
                                text = "Aperçu de votre modèle de carte CashPay Visa / Mastercard",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = ToofanBodyText,
                                textAlign = TextAlign.Center
                            )

                            ToofanButton(
                                title = "Commander votre première carte",
                                onClick = { dashboardViewModel.openBuyCardDialog() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            )
                        }
                    }
                } else {
                    items(uiState.userCards) { card ->
                        CashPayCardComponent(
                            card = card,
                            onFreeze = { dashboardViewModel.freezeCard(card.id ?: "") },
                            onUnfreeze = { dashboardViewModel.unfreezeCard(card.id ?: "") },
                            onManage = { dashboardViewModel.openManageCard(card) }
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dashboardViewModel.openCpkReader() },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Usb, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(22.dp))
                                    Text("Clés Financières CPK (USB)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (uiState.cpk.isDriveConnected) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (uiState.cpk.isDriveConnected) "Drive Lié" else "Local",
                                        color = if (uiState.cpk.isDriveConnected) Color(0xFF00E676) else Color(0xFFF59E0B),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                "Consultez, importez et sauvegardez vos fichiers .cpk et clés USB dans le coffre Drive. Récupération garantie en cas de perte de téléphone.",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Button(
                                onClick = { dashboardViewModel.openCpkReader() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000E38)),
                                border = BorderStroke(1.2.dp, Color.White),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ouvrir le Lecteur CPK (${uiState.cpk.keys.size} clés)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        } else {
            ToofanSecurityTab(
                session = session,
                userProfile = userProfile,
                onToggleBiometrics = { dashboardViewModel.toggleBiometrics(it) },
                onEditProfile = { dashboardViewModel.openEditProfileDialog(userProfile) },
                onLogout = onLogout
            )
        }
    }

    // Buy Card Dialog Flow (4 Steps)
    if (uiState.isBuyCardDialogOpen) {
        BuyCardDialog(
            uiState = uiState,
            viewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeBuyCardDialog() }
        )
    }

    // Manage Card Settings Dialog
    uiState.selectedCardForManage?.let { card ->
        ManageCardDialog(
            card = card,
            uiState = uiState,
            viewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeManageCard() }
        )
    }

    // Reveal Sensitive Card Details Challenge Modal
    if (uiState.isRevealDetailsDialogOpen) {
        uiState.selectedCardForManage?.let { card ->
            RevealCardDetailsDialog(
                card = card,
                uiState = uiState,
                viewModel = dashboardViewModel,
                onDismiss = { dashboardViewModel.closeRevealDetails() }
            )
        }
    }

    // Source de Financement Modal
    if (uiState.isFundingDialogOpen) {
        uiState.selectedCardForManage?.let { card ->
            FundingSourceDialog(
                card = card,
                uiState = uiState,
                viewModel = dashboardViewModel,
                onDismiss = { dashboardViewModel.closeFundingDialog() }
            )
        }
    }

    // PIN Update Modal
    if (uiState.isChangePinDialogOpen) {
        uiState.selectedCardForManage?.let { card ->
            ChangeCardPinDialog(
                card = card,
                uiState = uiState,
                viewModel = dashboardViewModel,
                onDismiss = { dashboardViewModel.closeChangePin() }
            )
        }
    }

    // Withdrawal Dialog Modal
    if (uiState.isWithdrawalDialogOpen) {
        WithdrawalDialog(
            uiState = uiState,
            viewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeWithdrawalDialog() }
        )
    }

    // Loans Dialog Modal
    if (uiState.isLoansDialogOpen) {
        LoansDialog(
            uiState = uiState,
            viewModel = dashboardViewModel,
            onDismiss = { dashboardViewModel.closeLoansDialog() }
        )
    }
}

@Composable
fun CashPayCardComponent(
    card: com.example.data.model.CardInfoDto,
    onFreeze: () -> Unit,
    onUnfreeze: () -> Unit,
    onManage: () -> Unit
) {
    val isVisa = card.brand?.lowercase() == "visa"
    val isFrozen = card.status?.lowercase() == "frozen" || card.status?.lowercase() == "inactive"
    val isPending = card.status?.lowercase() == "pending"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Vertical Card: strictly upright ("tout droit, pas en paysage")
        Card(
            modifier = Modifier
                .width(250.dp)
                .height(395.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.55f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
            ) {
                // Official Card Background image (waves & curves identified from design)
                Image(
                    painter = painterResource(id = R.drawable.cashpay_card_vertical_bg),
                    contentDescription = "Fond de carte CashPay",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // High-legibility subtle vignette to ensure crisp text reading
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.28f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.50f)
                                )
                            )
                        )
                )

                // Vertical Card Layout content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // TOP ROW: Brand Name + Logo (VISA / Mastercard)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CASHPAY",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.White,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(BrandOrange)
                            )
                        }

                        // Real Brand Logo: VISA or Mastercard
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.18f),
                            border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.4f)),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = if (isVisa) R.drawable.logo_visa else R.drawable.logo_mastercard
                                    ),
                                    contentDescription = if (isVisa) "VISA" else "Mastercard",
                                    modifier = Modifier.height(18.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }

                    // CHIP & CONTACTLESS & STATUS ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Metallic Gold EMV Chip
                            Box(
                                modifier = Modifier
                                    .size(width = 44.dp, height = 34.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFFFFDF7A),
                                                Color(0xFFE2B236),
                                                Color(0xFF9E781B)
                                            )
                                        )
                                    )
                                    .border(1.dp, Color(0xFFFFF0A6), RoundedCornerShape(6.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                        .border(0.6.dp, Color(0xFF6B4D08).copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                )
                            }

                            // Contactless wave indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.5.dp)
                            ) {
                                Box(modifier = Modifier.width(2.5.dp).height(10.dp).clip(RoundedCornerShape(1.dp)).background(Color.White.copy(alpha = 0.55f)))
                                Box(modifier = Modifier.width(2.5.dp).height(14.dp).clip(RoundedCornerShape(1.dp)).background(Color.White.copy(alpha = 0.75f)))
                                Box(modifier = Modifier.width(2.5.dp).height(18.dp).clip(RoundedCornerShape(1.dp)).background(Color.White.copy(alpha = 0.95f)))
                            }
                        }

                        // Status Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isPending -> Color.Gray.copy(alpha = 0.85f)
                                isFrozen -> BrandOrange.copy(alpha = 0.9f)
                                else -> Color(0xFF00E5FF).copy(alpha = 0.25f)
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    isPending -> Color.White.copy(alpha = 0.4f)
                                    isFrozen -> BrandOrange
                                    else -> Color(0xFF00E5FF).copy(alpha = 0.8f)
                                }
                            )
                        ) {
                            Text(
                                text = if (isFrozen) "GELÉE" else if (isPending) "EN ATTENTE" else "ACTIVE",
                                fontFamily = MulishFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // MASKED CARD NUMBER (PAN)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "NUMÉRO DE CARTE",
                            fontFamily = MulishFontFamily,
                            fontSize = 8.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "••••  ••••  ••••  ${card.last4 ?: "0000"}",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                    }

                    // EXPIRY & CURRENCY ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "EXPIRE FIN",
                                fontFamily = MulishFontFamily,
                                fontSize = 8.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "12/28",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "DEVISE",
                                fontFamily = MulishFontFamily,
                                fontSize = 8.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = card.currency?.uppercase() ?: "USD",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = BrandOrangeLight
                            )
                        }
                    }

                    // BOTTOM ROW: CARDHOLDER NAME & TYPE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "TITULAIRE",
                                fontFamily = MulishFontFamily,
                                fontSize = 8.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "TITULAIRE DU COMPTE",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        Text(
                            text = if (card.cardType?.contains("physique", ignoreCase = true) == true) "PHYSIQUE" else "VIRTUELLE",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Action Buttons right below the vertical card
        Row(
            modifier = Modifier.width(250.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Freeze / Unfreeze Button
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { if (isFrozen) onUnfreeze() else onFreeze() },
                shape = RoundedCornerShape(10.dp),
                color = if (isFrozen) BrandOrange.copy(alpha = 0.15f) else Color.White,
                border = BorderStroke(1.dp, if (isFrozen) BrandOrange else ToofanGrey1)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isFrozen) BrandOrange else ToofanMainDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isFrozen) "Dégeler" else "Geler",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isFrozen) BrandOrange else ToofanMainDark
                    )
                }
            }

            // Gérer Settings Button
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onManage),
                shape = RoundedCornerShape(10.dp),
                color = BrandOrange,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Gérer",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

val ToFocusBorderColor = Color(0xFFF59E0B)

@Composable
fun BuyCardDialog(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Achat d'une Carte CashPay",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Progress tracker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Étape ${uiState.purchaseStep}/5", fontFamily = MulishFontFamily, fontSize = 11.sp, color = ToofanGreen, fontWeight = FontWeight.Bold)
                    LinearProgressBar(step = uiState.purchaseStep, maxSteps = 5, modifier = Modifier.weight(1f).padding(horizontal = 10.dp))
                }

                when (uiState.purchaseStep) {
                    1 -> {
                        Text("Choisissez votre Marque", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Text("Les logos ci-dessous respectent les directives de marque Visa & Mastercard.", fontSize = 11.sp, color = Color(0xFF94A3B8))

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Visa Box
                            BrandSelectBox(
                                title = "VISA",
                                isSelected = uiState.purchaseBrand == "visa",
                                logoRes = R.drawable.logo_visa,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setPurchaseBrand("visa") }
                            )

                            // Mastercard Box
                            BrandSelectBox(
                                title = "Mastercard",
                                isSelected = uiState.purchaseBrand == "mastercard",
                                logoRes = R.drawable.logo_mastercard,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setPurchaseBrand("mastercard") }
                            )
                        }
                    }
                    2 -> {
                        Text("Choisissez le type de carte", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            val currSym = if (uiState.purchaseCurrency == "EUR") "€" else "$"

                            val vMatch = uiState.cardCatalog.firstOrNull {
                                it.type.equals("virtuelle", ignoreCase = true) &&
                                (it.currency.isEmpty() || it.currency.equals(uiState.purchaseCurrency, ignoreCase = true)) &&
                                (it.brand.isEmpty() || it.brand.equals(uiState.purchaseBrand, ignoreCase = true))
                            }
                            val pMatch = uiState.cardCatalog.firstOrNull {
                                it.type.equals("physique", ignoreCase = true) &&
                                (it.currency.isEmpty() || it.currency.equals(uiState.purchaseCurrency, ignoreCase = true)) &&
                                (it.brand.isEmpty() || it.brand.equals(uiState.purchaseBrand, ignoreCase = true))
                            }
                            val vPrice = uiState.virtualPrice ?: vMatch?.price?.takeIf { it > 0.0 } ?: (if (uiState.purchaseType == "virtuelle") uiState.purchasePrice else null)
                            val pPrice = uiState.physicalPrice ?: pMatch?.price?.takeIf { it > 0.0 } ?: (if (uiState.purchaseType == "physique") uiState.purchasePrice else null)

                            TypeSelectBox(
                                title = "Carte virtuelle",
                                description = "Disponible immédiatement, utilisation en ligne sur vos sites préférés.",
                                priceText = if (vPrice != null) "$currSym ${String.format(java.util.Locale.US, "%.2f", vPrice)}" else "Calcul des frais...",
                                isSelected = uiState.purchaseType == "virtuelle",
                                onClick = { viewModel.setPurchaseType("virtuelle") }
                            )

                            TypeSelectBox(
                                title = "Carte physique",
                                description = "Carte livrée à votre domicile, retraits USSD + paiements en magasin.",
                                priceText = if (pPrice != null) "$currSym ${String.format(java.util.Locale.US, "%.2f", pPrice)}" else "Calcul des frais...",
                                isSelected = uiState.purchaseType == "physique",
                                onClick = { viewModel.setPurchaseType("physique") }
                            )
                        }
                    }
                    3 -> {
                        Text("Choisissez la devise", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Text("La devise de facturation de la carte (USD ou EUR).", fontSize = 11.sp, color = Color(0xFF94A3B8))

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CurrencySelectPill(
                                symbol = "$",
                                code = "USD",
                                isSelected = uiState.purchaseCurrency == "USD",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setPurchaseCurrency("USD") }
                            )

                            CurrencySelectPill(
                                symbol = "€",
                                code = "EUR",
                                isSelected = uiState.purchaseCurrency == "EUR",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setPurchaseCurrency("EUR") }
                            )
                        }
                    }
                    4 -> {
                        Text("Récapitulatif de votre commande", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                val priceToDebit = uiState.purchasePrice
                                val currSymbol = if (uiState.purchaseCurrency == "EUR") "€" else "$"

                                RecapRow("Marque :", uiState.purchaseBrand.uppercase(), valueColor = Color.White)
                                RecapRow("Type :", uiState.purchaseType.replaceFirstChar { it.uppercase() }, valueColor = Color.White)
                                RecapRow("Devise :", uiState.purchaseCurrency, valueColor = Color.White)
                                HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)

                                if (uiState.isLoadingFee) {
                                    RecapRow("Frais d'achat :", "Calcul en cours...", valueColor = Color(0xFF94A3B8))
                                } else if (priceToDebit != null) {
                                    RecapRow("Frais d'achat de la carte :", "$currSymbol ${String.format(java.util.Locale.US, "%.2f", priceToDebit)} ${uiState.purchaseCurrency}", valueColor = Color.White)
                                    RecapRow("Total à débiter :", "$currSymbol ${String.format(java.util.Locale.US, "%.2f", priceToDebit)} ${uiState.purchaseCurrency}", isBold = true, customColor = ToofanGreen)
                                } else {
                                    RecapRow("Frais d'achat de la carte :", "Tarif standard", valueColor = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Entrez votre code PIN CashPay pour valider", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        ToofanInputField(
                            value = uiState.purchasePin,
                            onValueChange = { viewModel.setPurchasePin(it) },
                            placeholder = "Code PIN de validation",
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                            )
                        )
                        Text(
                            "Le PIN est strictement confidentiel : ne le partagez jamais.",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )

                        uiState.purchaseError?.let { err ->
                            Text(err, color = Color(0xFFFF4868), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    5 -> {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(54.dp))
                            Text("Achat réussi !", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            Text(
                                "Votre nouvelle carte CashPay ${uiState.purchaseBrand.uppercase()} ${uiState.purchaseType} a été créée avec succès et est prête à être utilisée.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (uiState.purchaseStep == 5) {
                ToofanButton(
                    title = "Fermer",
                    onClick = onDismiss,
                    modifier = Modifier.width(110.dp)
                )
            } else {
                ToofanButton(
                    title = if (uiState.purchaseStep == 4) "Confirmer" else "Suivant",
                    onClick = {
                        if (uiState.purchaseStep == 4) {
                            viewModel.executeCardPurchase()
                        } else {
                            viewModel.nextPurchaseStep()
                        }
                    },
                    isLoading = uiState.isPurchaseLoading,
                    modifier = Modifier.width(110.dp)
                )
            }
        },
        dismissButton = {
            if (uiState.purchaseStep > 1 && uiState.purchaseStep < 5) {
                TextButton(onClick = { viewModel.prevPurchaseStep() }) {
                    Text("Précédent", fontFamily = MulishFontFamily, color = Color(0xFF94A3B8))
                }
            } else if (uiState.purchaseStep < 5) {
                TextButton(onClick = onDismiss) {
                    Text("Annuler", fontFamily = MulishFontFamily, color = Color(0xFF94A3B8))
                }
            }
        }
    )
}

@Composable
fun BrandSelectBox(
    title: String,
    isSelected: Boolean,
    logoRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ToofanGreen else Color(0xFF334155),
                shape = RoundedCornerShape(14.dp)
            )
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = title,
                modifier = Modifier.height(34.dp),
                contentScale = ContentScale.Inside
            )
            Text(
                text = title,
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isSelected) Color.White else Color(0xFF94A3B8)
            )
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = ToofanGreen,
                    unselectedColor = Color(0xFF64748B)
                )
            )
        }
    }
}

@Composable
fun TypeSelectBox(
    title: String,
    description: String,
    priceText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ToofanGreen else Color(0xFF334155),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = ToofanGreen,
                    unselectedColor = Color(0xFF64748B)
                )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                Text(description, fontSize = 11.sp, color = Color(0xFF94A3B8))
                Text(priceText, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ToFocusBorderColor)
            }
        }
    }
}

@Composable
fun CurrencySelectPill(
    symbol: String,
    code: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ToofanGreen else Color(0xFF334155),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(symbol, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (isSelected) ToofanGreen else Color.White)
            Text(code, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun LinearProgressBar(step: Int, maxSteps: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(6.dp)
            .background(Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(3.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(step.toFloat() / maxSteps)
                .height(6.dp)
                .background(ToFocusBorderColor, RoundedCornerShape(3.dp))
        )
    }
}

@Composable
fun RecapRow(
    label: String,
    valText: String,
    isBold: Boolean = false,
    customColor: Color = Color.White,
    valueColor: Color = Color.White
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontFamily = MulishFontFamily, fontSize = 12.sp, color = Color(0xFF94A3B8))
        Text(valText, fontFamily = MulishFontFamily, fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold, fontSize = 12.sp, color = if (customColor != Color.White) customColor else valueColor)
    }
}

@Composable
fun ManageCardDialog(
    card: com.example.data.model.CardInfoDto,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Gestion de la Carte",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mini Visual representation of card brand
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (card.brand?.lowercase() == "visa") Color(0xFF1E3A8A) else Color(0xFF311045),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${card.brand?.uppercase() ?: ""} •••• ${card.last4 ?: ""}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(card.status?.uppercase() ?: "", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Actions List
                CardOptionItem(
                    title = "Voir les détails de la carte",
                    description = "Afficher le numéro complet (PAN), CVV et date d'expiration.",
                    onClick = { viewModel.openRevealDetails() }
                )

                CardOptionItem(
                    title = "Modifier le PIN",
                    description = "Mettre à jour le code de sécurité physique de votre carte.",
                    onClick = { viewModel.openChangePin() }
                )

                CardOptionItem(
                    title = "Source de financement",
                    description = "Associer cette carte à votre compte principal ou un Certificat CPK.",
                    onClick = { viewModel.openFundingDialog() }
                )

                CardOptionItem(
                    title = "Geler temporairement",
                    description = "Suspendre tous les paiements sur cette carte à tout moment.",
                    onClick = {
                        if (card.status?.lowercase() == "frozen") {
                            viewModel.unfreezeCard(card.id ?: "")
                        } else {
                            viewModel.freezeCard(card.id ?: "")
                        }
                    }
                )

                CardOptionItem(
                    title = "⚠ Résilier la carte",
                    description = "Désactiver et détruire définitivement cette carte de paiement.",
                    customColor = Color(0xFFFF4868),
                    onClick = { viewModel.terminateCard(card.id ?: "") }
                )
            }
        },
        confirmButton = {
            ToofanButton(title = "Fermer", onClick = onDismiss, modifier = Modifier.width(100.dp))
        }
    )
}

@Composable
fun CardOptionItem(
    title: String,
    description: String,
    customColor: Color = ToofanMainDark,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = ToofanGrey1.copy(alpha = 0.15f)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = customColor)
            Text(description, fontSize = 11.sp, color = ToofanBodyText)
        }
    }
}

@Composable
fun RevealCardDetailsDialog(
    card: com.example.data.model.CardInfoDto,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Détails Sensibles de la Carte", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.revealedCardDetails == null) {
                    Text("Veuillez saisir votre PIN CashPay à 4 chiffres pour révéler les détails.", fontSize = 12.sp)
                    ToofanInputField(
                        value = uiState.revealPin,
                        onValueChange = { viewModel.setRevealPin(it) },
                        placeholder = "PIN à 4 chiffres"
                    )

                    uiState.revealError?.let { err ->
                        Text(err, color = Color(0xFFFF4868), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    val details = uiState.revealedCardDetails
                    Text("Détails révélés de la carte sécurisée :", fontSize = 11.sp, color = ToofanGreen, fontWeight = FontWeight.Bold)

                    RevealDetailRow("Numéro de carte (PAN) :", details.pan ?: "Non disponible")
                    RevealDetailRow("Code CVV :", details.cvv ?: "Non disponible")
                    RevealDetailRow("Date d'expiration :", details.expiry ?: "Non disponible")
                }
            }
        },
        confirmButton = {
            if (uiState.revealedCardDetails == null) {
                ToofanButton(
                    title = "Révéler",
                    onClick = { viewModel.submitRevealDetails(card.id ?: "") },
                    isLoading = uiState.isRevealLoading,
                    modifier = Modifier.width(100.dp)
                )
            } else {
                ToofanButton(
                    title = "Fermer",
                    onClick = onDismiss,
                    modifier = Modifier.width(100.dp)
                )
            }
        },
        dismissButton = {
            if (uiState.revealedCardDetails == null) {
                TextButton(onClick = onDismiss) {
                    Text("Annuler", fontFamily = MulishFontFamily, color = ToofanBodyText)
                }
            }
        }
    )
}

@Composable
fun RevealDetailRow(label: String, value: String) {
    val clipboardManager = LocalClipboardManager.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = ToofanGrey1.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(label, fontSize = 10.sp, color = ToofanBodyText)
                Text(value, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ToofanMainDark)
            }

            IconButton(onClick = { clipboardManager.setText(AnnotatedString(value)) }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = ToofanGreen, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun ChangeCardPinDialog(
    card: com.example.data.model.CardInfoDto,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Modifier le Code PIN", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Saisissez le nouveau code PIN de 4 chiffres pour votre carte.", fontSize = 12.sp)
                ToofanInputField(
                    value = uiState.newCardPin,
                    onValueChange = { viewModel.setNewCardPin(it) },
                    placeholder = "Nouveau PIN"
                )
            }
        },
        confirmButton = {
            ToofanButton(
                title = "Enregistrer",
                onClick = { viewModel.submitChangePin(card.id ?: "") },
                enabled = uiState.newCardPin.length == 4,
                modifier = Modifier.width(110.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", fontFamily = MulishFontFamily, color = ToofanBodyText)
            }
        }
    )
}

@Composable
fun FundingSourceDialog(
    card: com.example.data.model.CardInfoDto,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Source de Financement", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FundingTypePill(
                    title = "◉ Wallet CashPay",
                    description = "Le solde de votre portefeuille finance la carte.",
                    isSelected = uiState.fundingType == "wallet",
                    onClick = { viewModel.setFundingType("wallet") }
                )

                FundingTypePill(
                    title = "◉ Certificat CPK",
                    description = "Utiliser un certificat CPK associé.",
                    isSelected = uiState.fundingType == "cpk",
                    onClick = { viewModel.setFundingType("cpk") }
                )

                if (uiState.fundingType == "cpk") {
                    Spacer(modifier = Modifier.height(4.dp))
                    ToofanInputField(
                        value = uiState.cpkSignature,
                        onValueChange = { viewModel.setCpkSignature(it) },
                        placeholder = "Signature du certificat CPK"
                    )
                    ToofanInputField(
                        value = uiState.cpkPin,
                        onValueChange = { viewModel.setCpkPin(it) },
                        placeholder = "PIN CPK"
                    )
                }
            }
        },
        confirmButton = {
            ToofanButton(
                title = "Associer",
                onClick = { viewModel.submitFundingConfig(card.id ?: "") },
                modifier = Modifier.width(100.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", fontFamily = MulishFontFamily, color = ToofanBodyText)
            }
        }
    )
}

@Composable
fun FundingTypePill(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 1.8.dp else 1.dp,
                color = if (isSelected) ToofanGreen else Color.LightGray,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) ToofanGreen.copy(alpha = 0.08f) else Color.White
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isSelected) ToofanGreen else ToofanMainDark)
            Text(description, fontSize = 11.sp, color = ToofanBodyText)
        }
    }
}

fun resolveAvatarUrl(raw: String?): Any? {
    if (raw.isNullOrBlank()) return null
    val trimmed = raw.trim()
    if (trimmed.startsWith("drawable:")) return trimmed
    if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed
    if (trimmed.startsWith("data:image")) {
        return try {
            val base64Data = trimmed.substringAfter("base64,")
            val decodedBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
            android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (_: Exception) { trimmed }
    }
    val clean = trimmed.removePrefix("/")
    return "https://app.cashpay-all.com/$clean"
}

@Composable
fun ToofanActionSquare(
    title: String,
    bgColor: Color = BrandBlue,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
        shadowElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = title,
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                lineHeight = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun RecentCorrespondentItem(
    contact: com.example.data.model.PhoneContact,
    ringColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(62.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.size(52.dp),
            contentAlignment = Alignment.Center
        ) {
            // Circular Avatar with Ring Border
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .border(2.dp, ringColor, CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                val rawPhoto = contact.publicProfile?.profilePhotoUrl ?: contact.publicProfile?.profilePhoto
                val resolved = resolveAvatarUrl(rawPhoto)
                if (resolved != null && !resolved.toString().startsWith("drawable:")) {
                    AsyncImage(
                        model = resolved,
                        contentDescription = contact.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    val initials = contact.name.trim().split(" ")
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2).joinToString("").uppercase()
                        .ifBlank { "CP" }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        BrandBlueDark,
                                        BrandBlue
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Connected Status indicator at bottom right (Vert foncé pour utilisateur connecté)
            Box(
                modifier = Modifier
                    .size(13.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(GreenOnline)
                    .border(1.5.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = contact.name,
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = Color(0xFF0F172A),
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}

@Composable
fun AddContactDialog(
    uiState: DashboardUiState,
    dashboardViewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0066FF))
                Text(
                    text = "Ajouter un contact",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Rechercher par Numéro de téléphone ou Wallet ID :",
                    fontSize = 13.sp,
                    fontFamily = MulishFontFamily,
                    color = Color(0xFF64748B)
                )

                ToofanInputField(
                    value = uiState.addContactPhone,
                    onValueChange = { dashboardViewModel.setAddContactPhone(it) },
                    placeholder = "Ex: +243899... ou WAL-XXXX"
                )

                if (uiState.isSearchingAddContact) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = SuccessGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                if (uiState.addContactError != null) {
                    Text(
                        text = uiState.addContactError!!,
                        color = Color(0xFFFF4868),
                        fontSize = 12.sp,
                        fontFamily = MulishFontFamily
                    )
                }

                if (uiState.addContactNotFound) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFF1F2),
                        border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ce numéro n'est pas encore associé à un compte CashPay actif.",
                            modifier = Modifier.padding(10.dp),
                            color = Color(0xFFBE123C),
                            fontSize = 12.sp,
                            fontFamily = MulishFontFamily
                        )
                    }
                }

                uiState.addContactFoundProfile?.let { profile ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.5.dp, SuccessGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val photoUrl = profile.profilePhotoUrl ?: profile.profilePhoto
                                if (!photoUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = "Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                                    )
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = SuccessGreen)
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile.fullName,
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Wallet ID : ${profile.walletId}",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Compte CashPay Actif",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (uiState.addContactFoundProfile != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToofanButton(
                        title = "Envoyer",
                        onClick = {
                            val prof = uiState.addContactFoundProfile!!
                            dashboardViewModel.confirmAddContact(prof)
                            dashboardViewModel.triggerPrefilledTransfer(prof.fullName, prof)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ToofanButton(
                        title = "Enregistrer",
                        onClick = {
                            dashboardViewModel.confirmAddContact(uiState.addContactFoundProfile!!)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                ToofanButton(
                    title = "Rechercher",
                    onClick = { dashboardViewModel.searchAddContact() },
                    isLoading = uiState.isSearchingAddContact,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", fontFamily = MulishFontFamily, color = Color(0xFF64748B))
            }
        }
    )
}

@Composable
fun ContactsPageDialog(
    uiState: DashboardUiState,
    dashboardViewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var hasContactPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactPermission = isGranted
        if (isGranted) {
            readAndSyncPhoneContacts(context, dashboardViewModel)
        }
    }

    LaunchedEffect(hasContactPermission) {
        if (hasContactPermission && uiState.contactsList.isEmpty()) {
            readAndSyncPhoneContacts(context, dashboardViewModel)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF070F22)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF070F22),
                            Color(0xFF0A1938),
                            Color(0xFF030814)
                        )
                    )
                )
        ) {
            // Header Top Bar
            Surface(
                color = Color(0xFF0F172A),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                        }
                        Column {
                            Text(
                                text = "Contacts & Répertoire",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Détection automatique des comptes CashPay",
                                fontFamily = MulishFontFamily,
                                fontSize = 11.sp,
                                color = SuccessGreen
                            )
                        }
                    }

                    // Button to manually add / verify a number or wallet
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SuccessGreen.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, SuccessGreen),
                        modifier = Modifier.clickable {
                            dashboardViewModel.openAddContactDialog()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Ajouter",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search Input with Dark theme styling
                OutlinedTextField(
                    value = uiState.searchContactQuery,
                    onValueChange = { dashboardViewModel.setContactSearchQuery(it) },
                    placeholder = { Text("Rechercher par nom, 9 chiffres ou Wallet ID...", color = Color(0xFF64748B), fontFamily = MulishFontFamily, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SuccessGreen,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                if (!hasContactPermission) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SuccessGreen.copy(alpha = 0.15f),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Accès aux contacts",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "CashPay analyse automatiquement les 9 derniers chiffres de vos contacts pour détecter instantanément ceux qui possèdent déjà un compte CashPay.",
                                fontFamily = MulishFontFamily,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF94A3B8)
                            )
                            Button(
                                onClick = {
                                    permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000E38)),
                                border = BorderStroke(1.2.dp, Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Autoriser l'accès aux contacts",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else if (uiState.isLoadingContacts) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = SuccessGreen)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Recherche en temps réel des comptes CashPay...",
                                fontFamily = MulishFontFamily,
                                fontSize = 13.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Analyse des 9 derniers chiffres des contacts",
                                fontFamily = MulishFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                } else {
                    val filtered = remember(uiState.contactsList, uiState.searchContactQuery) {
                        if (uiState.searchContactQuery.isBlank()) uiState.contactsList else {
                            val q = uiState.searchContactQuery.trim().lowercase()
                            val qDigits = q.filter { it.isDigit() }
                            uiState.contactsList.filter {
                                it.name.lowercase().contains(q) ||
                                        it.phone.contains(q) ||
                                        (qDigits.isNotBlank() && it.lastNineDigits.contains(qDigits)) ||
                                        (it.publicProfile?.walletId?.lowercase()?.contains(q) == true)
                            }
                        }
                    }

                    var selectedProfileContact by remember { mutableStateOf<com.example.data.model.PhoneContact?>(null) }

                    selectedProfileContact?.let { contact ->
                        MiniPublicProfileDialog(
                            contact = contact,
                            onDismiss = { selectedProfileContact = null },
                            onSendMoney = {
                                selectedProfileContact = null
                                onDismiss()
                                if (contact.publicProfile != null) {
                                    dashboardViewModel.triggerPrefilledTransfer(contact.name, contact.publicProfile)
                                } else {
                                    dashboardViewModel.onRecipientChanged(contact.name)
                                    dashboardViewModel.openTransferDialog()
                                }
                            }
                        )
                    }

                    val cashPayContacts = filtered.filter { it.isCashPayUser }
                    val otherContacts = filtered.filter { !it.isCashPayUser }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        if (cashPayContacts.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "⭐ Comptes CashPay Détectés (${cashPayContacts.size})",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SuccessGreen
                                    )
                                    Text(
                                        text = "Actifs",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 11.sp,
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            items(cashPayContacts) { contact ->
                                ContactItemCard(
                                    contact = contact,
                                    onViewProfileClick = {
                                        selectedProfileContact = contact
                                    },
                                    onSendClick = {
                                        onDismiss()
                                        if (contact.publicProfile != null) {
                                            dashboardViewModel.triggerPrefilledTransfer(contact.name, contact.publicProfile)
                                        } else {
                                            dashboardViewModel.onRecipientChanged(contact.name)
                                            dashboardViewModel.openTransferDialog()
                                        }
                                    }
                                )
                            }
                        }

                        if (otherContacts.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Autres contacts (${otherContacts.size})",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                )
                            }
                            items(otherContacts) { contact ->
                                OtherContactItemCard(
                                    contact = contact
                                )
                            }
                        }

                        if (filtered.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Aucun contact correspondant",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Utilisez le bouton Ajouter pour rechercher manuellement",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 12.sp,
                                            color = Color(0xFF94A3B8)
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

@Composable
private fun ContactItemCard(
    contact: com.example.data.model.PhoneContact,
    onViewProfileClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B1228),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onViewProfileClick),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .border(1.5.dp, SuccessGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val photoUrl = contact.publicProfile?.profilePhotoUrl ?: contact.publicProfile?.profilePhoto
                    if (!photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = contact.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = contact.name.take(2).uppercase(),
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen,
                            fontSize = 16.sp
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = contact.name,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        if (contact.isCashPayUser) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Vérifié",
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = "ID : ${contact.publicProfile?.walletId ?: contact.phone}",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SuccessGreen.copy(alpha = 0.2f),
                modifier = Modifier.clickable(onClick = onSendClick)
            ) {
                Text(
                    text = "Envoyer",
                    color = SuccessGreen,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun OtherContactItemCard(
    contact: com.example.data.model.PhoneContact
) {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B1228),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = contact.name.take(2).uppercase(),
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            fontSize = 16.sp
                        )
                    }
                }

                Column {
                    Text(
                        text = contact.name,
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = contact.phone,
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                modifier = Modifier.clickable {
                    Toast.makeText(context, "Invitation CashPay envoyée à ${contact.name}", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text(
                    text = "Inviter",
                    color = Color(0xFF3B82F6),
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ServicesDialog(
    onDismiss: () -> Unit,
    onServiceSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF8B5CF6))
                Text(
                    text = "Services & Abonnements",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Choisissez un service ou un abonnement à régler :",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )

                val services = listOf(
                    Triple("Analytique & Marché BTC", "Graphiques temps réel, BTC & Synthèse financière", Color(0xFF00E5FF)),
                    Triple("Réclamations internationales", "Western Union, MoneyGram, Ria", Color(0xFF00E676)),
                    Triple("Mobile Money", "M-Pesa, Orange Money, Airtel, MTN", Color(0xFFF59E0B)),
                    Triple("Abonnements TV", "Canal+ RDC, EasyTV, BLUS", Color(0xFF2563EB)),
                    Triple("Factures Publiques", "SNEL (Électricité), REGIDESO (Eau)", SuccessGreen),
                    Triple("Starlink Manager", "Gestion des équipements Starlink", Color(0xFF1E293B)),
                    Triple("Clés FINUSECO CPK", "Lecteur .cpk, Clés USB & Cloud Google Drive", Color(0xFF38BDF8)),
                    Triple("Services Digitaux", "Netflix, Spotify, Apple Music, PlayStation", Color(0xFF8B5CF6))
                )

                services.forEach { (name, desc, color) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onServiceSelected(name) },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = color.copy(alpha = 0.15f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                Text(desc, fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", fontFamily = MulishFontFamily, color = Color(0xFF64748B))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionPlusModalDialog(
    userProfile: com.example.data.local.UserProfileEntity?,
    onDismiss: () -> Unit,
    onSendScan: () -> Unit,
    onReceiveQr: () -> Unit,
    onPayPos: () -> Unit,
    onWithdraw: () -> Unit,
    onWithdrawExpress: () -> Unit = {},
    onDeposit: () -> Unit = {},
    onExchange: () -> Unit = {},
    onInternationalClaims: () -> Unit = {},
    onReferrals: () -> Unit = {},
    onCpkReader: () -> Unit = {},
    onStarlinkManager: () -> Unit = {},
    // Agent actions
    onAgentDeposit: () -> Unit = {},
    onAgentWithdraw: () -> Unit = {},
    onAgentExternalMoMo: () -> Unit = {},
    onAgentLoan: () -> Unit = {},
    onAgentHistory: () -> Unit = {},
    onAgentShopping: () -> Unit = {}
) {
    val isAgent = userProfile?.role?.lowercase()?.trim() == "agent"

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Text(
                    text = if (isAgent) "Menu Agent CashPay" else "Actions Rapides",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isAgent) "Services opérationnels pour agents agréés :" else "Choisissez l'opération que vous souhaitez effectuer :",
                    fontSize = 13.sp,
                    fontFamily = MulishFontFamily,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (isAgent) {
                    Text(
                        text = "SERVICES CLIENT & PORTEFEUILLE",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = SuccessGreen,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    ActionPlusTile(
                        title = "Recharger mon Compte",
                        subtitle = "Mobile Money, Carte bancaire ou PayPal",
                        icon = Icons.Default.Add,
                        iconBg = Color(0xFF10B981),
                        onClick = onDeposit
                    )
                    ActionPlusTile(
                        title = "Retrait Express",
                        subtitle = "Vers M-Pesa, Orange, Afrimoney, Airtel, MTN",
                        icon = Icons.Default.PhoneAndroid,
                        iconBg = Color(0xFFFF6600),
                        onClick = onWithdrawExpress
                    )
                    ActionPlusTile(
                        title = "Taux de change & Conversion",
                        subtitle = "Convertir USD, CDF, EUR, BTC instantanément",
                        icon = Icons.Default.CurrencyExchange,
                        iconBg = Color(0xFF00E5FF),
                        onClick = onExchange
                    )
                    ActionPlusTile(
                        title = "Programme de Parrainage",
                        subtitle = "Gagnez des commissions et récompenses parrainage",
                        icon = Icons.Default.CardGiftcard,
                        iconBg = Color(0xFFFF9900),
                        onClick = onReferrals
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SERVICES OPÉRATIONNELS AGENT",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    // Agent Actions
                    ActionPlusTile(
                        title = "Dépôt Espèces Client",
                        subtitle = "Effectuer un dépôt sur le compte d'un client",
                        icon = Icons.Default.ArrowDownward,
                        iconBg = Color(0xFF059669),
                        onClick = onAgentDeposit
                    )
                    ActionPlusTile(
                        title = "Retrait Mobile Money Direct",
                        subtitle = "Débiter la carte SIM du client (M-Pesa, Orange, etc.)",
                        icon = Icons.Default.PhoneAndroid,
                        iconBg = Color(0xFFFF6600),
                        onClick = onAgentExternalMoMo
                    )
                    ActionPlusTile(
                        title = "Retrait Espèces Client",
                        subtitle = "Initier un retrait pour un client CashPay",
                        icon = Icons.Default.ArrowUpward,
                        iconBg = Color(0xFFEA580C),
                        onClick = onAgentWithdraw
                    )
                    ActionPlusTile(
                        title = "Prêt Loan Me",
                        subtitle = "Rembourser le prêt d'un client",
                        icon = Icons.Default.Payments,
                        iconBg = Color(0xFF7C3AED),
                        onClick = onAgentLoan
                    )
                    ActionPlusTile(
                        title = "Historique Agent",
                        subtitle = "Voir mes opérations et commissions",
                        icon = Icons.Default.Receipt,
                        iconBg = Color(0xFF0066FF),
                        onClick = onAgentHistory
                    )
                    ActionPlusTile(
                        title = "Boutiques & Shopping Agent",
                        subtitle = "Gérer mes boutiques et publier des produits",
                        icon = Icons.Default.Store,
                        iconBg = Color(0xFFFF6600),
                        onClick = onAgentShopping
                    )
                    ActionPlusTile(
                        title = "Lecteur de Clés CPK",
                        subtitle = "FINUSECO • Clés USB .cpk & Google Drive",
                        icon = Icons.Default.Usb,
                        iconBg = Color(0xFF38BDF8),
                        onClick = onCpkReader
                    )
                } else {
                    // Standard Client Actions
                    ActionPlusTile(
                        title = "Recharger mon Compte",
                        subtitle = "Mobile Money, Carte bancaire ou PayPal",
                        icon = Icons.Default.Add,
                        iconBg = Color(0xFF10B981),
                        onClick = onDeposit
                    )
                    ActionPlusTile(
                        title = "Taux de change & Conversion",
                        subtitle = "Convertir USD, CDF, EUR, BTC instantanément",
                        icon = Icons.Default.CurrencyExchange,
                        iconBg = Color(0xFF00E5FF),
                        onClick = onExchange
                    )
                    ActionPlusTile(
                        title = "Programme de Parrainage",
                        subtitle = "Inviter des amis, gagner des récompenses & gains",
                        icon = Icons.Default.CardGiftcard,
                        iconBg = Color(0xFFFF9900),
                        onClick = onReferrals
                    )
                    ActionPlusTile(
                        title = "Retrait Express",
                        subtitle = "Vers M-Pesa, Orange, Afrimoney, Airtel, MTN",
                        icon = Icons.Default.PhoneAndroid,
                        iconBg = Color(0xFFFF6600),
                        onClick = onWithdrawExpress
                    )
                    ActionPlusTile(
                        title = "Retrait Cash (Agent)",
                        subtitle = "Retirer du cash auprès d'un agent agréé",
                        icon = Icons.Default.AccountBalanceWallet,
                        iconBg = Color(0xFF7C3AED),
                        onClick = onWithdraw
                    )
                    ActionPlusTile(
                        title = "Envoyer de l'argent",
                        subtitle = "QR Code, Wallet ID ou N°",
                        icon = Icons.Default.Send,
                        iconBg = SuccessGreen,
                        onClick = onSendScan
                    )
                    ActionPlusTile(
                        title = "PayScan (Supermarché & POS)",
                        subtitle = "Scanner articles, panier, codes-barres & payer",
                        icon = Icons.Default.QrCode,
                        iconBg = Color(0xFF00E676),
                        onClick = onPayPos
                    )
                    ActionPlusTile(
                        title = "Réclamations internationales",
                        subtitle = "Western Union, MoneyGram, Ria",
                        icon = Icons.Default.Public,
                        iconBg = Color(0xFF00E676),
                        onClick = onInternationalClaims
                    )
                    ActionPlusTile(
                        title = "Lecteur de Clés CPK",
                        subtitle = "FINUSECO • Clés USB .cpk & Google Drive",
                        icon = Icons.Default.Usb,
                        iconBg = Color(0xFF38BDF8),
                        onClick = onCpkReader
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        },
        containerColor = Color(0xFF000E38),
        shape = RoundedCornerShape(24.dp),
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.padding(bottom = 8.dp)) {
                Text("Fermer", fontFamily = MulishFontFamily, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    )
}

@Composable
fun CashPaySettingsModal(
    onDismiss: () -> Unit,
    userProfile: UserProfileEntity?,
    onLogout: () -> Unit,
    onOpenActivateAgent: () -> Unit,
    onOpenForgotPin: () -> Unit,
    onOpenReferrals: () -> Unit = {}
) {
    var selectedLang by remember { mutableStateOf("Français (RDC)") }
    var isBiometricEnabled by remember { mutableStateOf(true) }
    val isAgentUser = userProfile?.role?.lowercase()?.trim() == "agent"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = ToofanGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(20.dp))
                    }
                }
                Text(
                    text = "Paramètres de l'Application",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = ToofanMainDark
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 460.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // --- SECTION 1: COMPTE & SERVICES ---
                item {
                    Text(
                        text = "COMPTE & SERVICES",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = ToofanGreen,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = (userProfile?.fullName?.take(1) ?: "").uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    val profileName = userProfile?.fullName ?: ""
                                    Text(
                                        text = profileName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ToofanMainDark
                                    )
                                    Text(
                                        text = "Wallet ID: ${userProfile?.walletId ?: ""}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                // Removed redundant AGENT/CLIENT label per user feedback
                            }

                            HorizontalDivider(color = Color(0xFFE2E8F0))

                            // Programme de Parrainage
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onDismiss()
                                        onOpenReferrals()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFFFF9900), modifier = Modifier.size(20.dp))
                                        Column {
                                            Text(
                                                text = "Programme de Parrainage",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = ToofanMainDark
                                            )
                                            Text(
                                                text = "Suivre mes gains et réclamer mes récompenses",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                                }
                            }

                            HorizontalDivider(color = Color(0xFFE2E8F0))

                            // Devenir Agent Card
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onOpenActivateAgent() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Badge, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(20.dp))
                                        Column {
                                            Text(
                                                text = if (isAgentUser) "Gérer mon statut Agent" else "Devenir Agent CashPay",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = ToofanMainDark
                                            )
                                            Text(
                                                text = "Souscription aux formules Agent (Promo / Normal)",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                // --- SECTION 2: SÉCURITÉ & PIN ---
                item {
                    Text(
                        text = "SÉCURITÉ & AUTHENTIFICATION",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Forgot PIN Card
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onOpenForgotPin() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.LockReset, contentDescription = null, tint = Color(0xFFFF5B37), modifier = Modifier.size(20.dp))
                                        Column {
                                            Text(
                                                text = "PIN Oublié / Réinitialiser le PIN",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = ToofanMainDark
                                            )
                                            Text(
                                                text = "Réinitialisation par SMS ou WhatsApp OTP",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                                }
                            }

                            // Biometrics Switch
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(20.dp))
                                    Column {
                                        Text(
                                            text = "Authentification Biométrique",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = ToofanMainDark
                                        )
                                        Text(
                                            text = "Empreinte / Reconnaissance faciale",
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                                Switch(
                                    checked = isBiometricEnabled,
                                    onCheckedChange = { isBiometricEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = ToofanGreen)
                                )
                            }
                        }
                    }
                }

                // --- SECTION 3: PRÉFÉRENCES SYSTÈME ---
                item {
                    Text(
                        text = "PRÉFÉRENCES SYSTÈME",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Langue de l'application :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = ToofanMainDark,
                        fontFamily = MulishFontFamily
                    )
                    val languages = listOf("Français (RDC)", "English", "Swahili", "Lingala")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        languages.take(2).forEach { lang ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedLang = lang },
                                color = if (selectedLang == lang) ToofanGreen.copy(alpha = 0.15f) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, if (selectedLang == lang) ToofanGreen else Color(0xFFE2E8F0))
                            ) {
                                Text(
                                    text = lang,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedLang == lang) ToofanGreen else ToofanMainDark
                                )
                            }
                        }
                    }
                }

                // --- SECTION 4: DÉCONNEXION ---
                item {
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000E38)),
                        border = BorderStroke(1.2.dp, Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Se déconnecter",
                            color = Color.White,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", fontFamily = MulishFontFamily, color = Color(0xFF64748B))
            }
        }
    )
}

@Composable
fun MiniPublicProfileDialog(
    contact: com.example.data.model.PhoneContact,
    onDismiss: () -> Unit,
    onSendMoney: () -> Unit
) {
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val profile = contact.publicProfile

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Badge, contentDescription = null, tint = ToofanGreen)
                Text(
                    text = "Profil Public Correspondant",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF0F172A)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Large Avatar
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(2.dp, ToofanGreen),
                    modifier = Modifier.size(68.dp)
                ) {
                    val photoUrl = profile?.profilePhotoUrl ?: profile?.profilePhoto
                    if (!photoUrl.isNullOrBlank()) {
                        coil.compose.AsyncImage(
                            model = photoUrl,
                            contentDescription = contact.name,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = contact.name.take(2).uppercase(),
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = ToofanGreen
                            )
                        }
                    }
                }

                Text(
                    text = profile?.fullName ?: contact.name,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF0F172A)
                )

                if (!profile?.profession.isNullOrBlank()) {
                    Text(
                        text = profile.profession,
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Wallet ID Pill
                val walletId = profile?.walletId ?: contact.phone
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ID Wallet :", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text(walletId, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ToofanGreen)
                        }
                        IconButton(
                            onClick = {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(walletId))
                                Toast.makeText(context, "ID copié : $walletId", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = ToofanGreen, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // BTC Address if available
                profile?.bitcoin?.address?.let { btcAddr ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Adresse Bitcoin :", fontSize = 10.sp, color = Color(0xFF64748B))
                                Text(btcAddr, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color(0xFFF59E0B), maxLines = 1)
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(btcAddr))
                                    Toast.makeText(context, "Adresse BTC copiée !", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Activity / Location
                if (!profile?.city.isNullOrBlank() || !profile?.country.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                        Text(
                            text = listOfNotNull(profile.city, profile.country).joinToString(", "),
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Action: Envoyer de l'argent
                Button(
                    onClick = onSendMoney,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000E38)),
                    border = BorderStroke(1.2.dp, Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Envoyer de l'argent", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", fontFamily = MulishFontFamily, color = Color(0xFF64748B))
            }
        }
    )
}

@Composable
private fun ActionPlusTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    logoRes: Int? = null,
    iconBg: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color(0xFF000E38),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (logoRes != null) {
                        Image(
                            painter = painterResource(id = logoRes),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Inside
                        )
                    } else if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 15.sp
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private fun readAndSyncPhoneContacts(context: Context, dashboardViewModel: DashboardViewModel) {
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
        return
    }
    try {
        val resolver = context.contentResolver
        // Map to store phone -> name, deduplicating by normalized digit key
        val contactsMap = mutableMapOf<String, String>()

        // 1. Primary Android query across all accounts (Gmail / Google account, device phone storage, WhatsApp, etc.)
        val phoneProjection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
        )
        try {
            resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                phoneProjection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val normIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                while (cursor.moveToNext()) {
                    val name = if (nameIdx != -1) cursor.getString(nameIdx) ?: "Contact" else "Contact"
                    val num = if (numIdx != -1) cursor.getString(numIdx) ?: "" else ""
                    val norm = if (normIdx != -1) cursor.getString(normIdx) else null
                    val cleanDigits = (norm ?: num).replace(Regex("[^0-9+]"), "").trim()
                    if (cleanDigits.length >= 4) {
                        if (!contactsMap.containsKey(cleanDigits) || contactsMap[cleanDigits].isNullOrBlank() || contactsMap[cleanDigits] == "Contact") {
                            contactsMap[cleanDigits] = name
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        // 2. Query SIM card ADN directly for any SIM-only contacts that might not be synced to main database
        val simUris = listOf("content://icc/adn", "content://sim/adn", "content://icc/adn/subId/1", "content://icc/adn/subId/2")
        for (simUriStr in simUris) {
            try {
                val simUri = android.net.Uri.parse(simUriStr)
                resolver.query(simUri, null, null, null, null)?.use { simCursor ->
                    val nameIdx = simCursor.getColumnIndex("name").takeIf { it != -1 }
                        ?: simCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val numIdx = simCursor.getColumnIndex("number").takeIf { it != -1 }
                        ?: simCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (simCursor.moveToNext()) {
                        val name = if (nameIdx != -1) simCursor.getString(nameIdx) ?: "Contact SIM" else "Contact SIM"
                        val num = if (numIdx != -1) simCursor.getString(numIdx) ?: "" else ""
                        val cleanDigits = num.replace(Regex("[^0-9+]"), "").trim()
                        if (cleanDigits.length >= 4) {
                            if (!contactsMap.containsKey(cleanDigits)) {
                                contactsMap[cleanDigits] = name
                            }
                        }
                    }
                }
            } catch (_: Exception) {}
        }

        val rawList = contactsMap.map { (phone, name) -> name to phone }
        dashboardViewModel.syncContacts(rawList)
    } catch (e: Exception) {
        // Handled gracefully
    }
}

private fun extractRecipientFromQr(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
        try {
            val json = org.json.JSONObject(trimmed)
            val wallet = json.optString("walletId", json.optString("wallet", json.optString("receiverWalletId", json.optString("clientWalletId", ""))))
            if (wallet.isNotBlank()) return wallet
            val phone = json.optString("phone", json.optString("phoneNumber", ""))
            if (phone.isNotBlank()) return phone
            val target = json.optString("target", json.optString("recipient", ""))
            if (target.isNotBlank()) return target
        } catch (_: Exception) {}
    }
    var clean = trimmed
    if (clean.startsWith("cashpay://", ignoreCase = true)) clean = clean.substring(10)
    else if (clean.startsWith("cashpay:", ignoreCase = true)) clean = clean.substring(8)
    else if (clean.startsWith("bitcoin:", ignoreCase = true)) clean = clean.substring(8)

    if (clean.contains("wallet=")) {
        clean = clean.substringAfter("wallet=").substringBefore("&")
    } else if (clean.contains("receiver=")) {
        clean = clean.substringAfter("receiver=").substringBefore("&")
    } else if (clean.contains("?")) {
        clean = clean.substringBefore("?")
    }
    return clean.trim()
}

