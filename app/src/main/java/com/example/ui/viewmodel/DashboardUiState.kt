package com.example.ui.viewmodel

import com.example.data.local.NotificationEntity
import com.example.data.local.TransactionEntity
import com.example.data.model.*

data class MainUiState(
    val selectedTab: Int = 0, // 0: Accueil / Wallet, 1: Profil Complet & Données, 2: Sécurité & PIN
    val isEditProfileOpen: Boolean = false,
    val editFullName: String = "",
    val editCity: String = "",
    val editProfession: String = "",
    val editAddress: String = "",
    val editPinConfirmation: String = "",
    val isEditingLoading: Boolean = false,
    val editErrorMessage: String? = null,
    val editSuccessMessage: String? = null,
    val isQuickTransferOpen: Boolean = false,
    val transferRecipient: String = "",
    val transferAmount: String = "",
    val transferCurrency: String = "USD",
    val transferSuccess: Boolean = false,
    val walletResponse: WalletResponse? = null,
    val isLoadingWallet: Boolean = false,
    val walletError: String? = null,
    val transfersMeta: TransfersMetaResponse? = null,
    val transferFee: Double? = null,
    val transferTotalDebit: Double? = null,
    val transferError: String? = null,
    val isTransferLoading: Boolean = false,
    val transferStep: Int = 1, // 1: Input details, 2: Preview & Fee, 3: PIN Confirmation, 4: Success
    val txDirectionFilter: String = "ALL", // "ALL", "INCOMING", "OUTGOING"
    val txDateFilter: String = "ALL", // "ALL", "TODAY", "WEEK", "MONTH", "CUSTOM"
    val txSearchQuery: String = "",
    val customFromDate: String = "",
    val customToDate: String = "",
    val isAllTransactionsOpen: Boolean = false,
    val selectedTransactionForDetail: TransactionEntity? = null,
    val isLoadingTransactions: Boolean = false,
    val transactionsError: String? = null,
    val isNotificationDialogOpen: Boolean = false,
    val isLoadingNotifications: Boolean = false,
    val notificationsError: String? = null,
    val isDarkMode: Boolean = false,
    val isActivateAgentDialogOpen: Boolean = false,
    val agentPlan: String = "promo",
    val agentPin: String = "",
    val isActivatingAgent: Boolean = false,
    val activateAgentError: String? = null,
    val activateAgentSuccessMessage: String? = null,
    val isBalanceVisible: Boolean = true,
    val currentUserId: Long? = null,
    val transferSearchMode: String = "wallet", // "wallet" or "phone"
    val isSearchingTransferRecipient: Boolean = false,
    val isReceiveBtcDialogOpen: Boolean = false,
    val isSendBtcDialogOpen: Boolean = false,
    val btcLiveRateUsd: Double = 82500.0,
    val isBtcRateLoading: Boolean = false,
    val isBtcTransferLoading: Boolean = false,
    val btcTransferError: String? = null,
    val btcTransferSuccess: Boolean = false,
    val btcTransferTxRef: String? = null,
    val isLoansScreenOpen: Boolean = false,
    val usdtAddress: String? = null,
    val usdtBalance: Double = 0.0,
    val usdtOnChainBalance: Double? = null,
    val isUsdtLoading: Boolean = false,
    val usdtError: String? = null,
    val usdtDeposits: List<com.example.data.model.UsdtDepositItem> = emptyList(),
    val isUsdtDepositsLoading: Boolean = false,
    val isReceiveUsdtDialogOpen: Boolean = false,
    val isSendUsdtDialogOpen: Boolean = false,
    val isUsdtDepositsDialogOpen: Boolean = false,
    val isUsdtTransferLoading: Boolean = false,
    val usdtTransferError: String? = null,
    val usdtTransferSuccess: Boolean = false,
    val usdtTransferPending: Boolean = false,
    val usdtTransferTxRef: String? = null
)

data class CardsUiState(
    val userCards: List<CardInfoDto> = emptyList(),
    val cardCatalog: List<CardCatalogDto> = emptyList(),
    val isLoadingCards: Boolean = false,
    val cardsError: String? = null,
    val catalogError: String? = null,
    val isBuyCardDialogOpen: Boolean = false,
    val purchaseBrand: String = "visa", // "visa", "mastercard"
    val purchaseType: String = "virtuelle", // "virtuelle", "physique"
    val purchaseCurrency: String = "USD", // "USD", "EUR"
    val purchasePin: String = "",
    val isPurchaseLoading: Boolean = false,
    val purchaseError: String? = null,
    val purchaseSuccess: Boolean = false,
    val purchaseStep: Int = 1,
    val selectedCardForManage: CardInfoDto? = null,
    val isManageCardDialogOpen: Boolean = false,
    val isChangePinDialogOpen: Boolean = false,
    val newCardPin: String = "",
    val revealedCardDetails: CardDetailsResponse? = null,
    val revealPin: String = "",
    val isRevealLoading: Boolean = false,
    val revealError: String? = null,
    val isRevealDetailsDialogOpen: Boolean = false,
    val fundingType: String = "wallet", // "wallet", "cpk"
    val cpkSignature: String = "",
    val cpkPin: String = "",
    val isFundingDialogOpen: Boolean = false,
    val purchaseFee: Double? = null,
    val purchasePrice: Double? = null,
    val virtualPrice: Double? = null,
    val physicalPrice: Double? = null,
    val purchaseFeeCode: String? = null,
    val isLoadingFee: Boolean = false
)

data class ContactsUiState(
    val contactsList: List<PhoneContact> = emptyList(),
    val recentCorrespondents: List<PhoneContact> = emptyList(),
    val isContactsDialogOpen: Boolean = false,
    val isContactsPageOpen: Boolean = false,
    val isAddContactDialogOpen: Boolean = false,
    val addContactPhone: String = "",
    val isSearchingAddContact: Boolean = false,
    val addContactFoundProfile: PublicProfileDto? = null,
    val addContactNotFound: Boolean = false,
    val addContactError: String? = null,
    val isLoadingContacts: Boolean = false,
    val contactsError: String? = null,
    val selectedPublicProfile: PublicProfileDto? = null,
    val selectedPublicContactName: String? = null,
    val isPublicProfileOpen: Boolean = false,
    val searchContactQuery: String = "",
    val prefilledRecipient: PublicProfileDto? = null,
    val prefilledContactName: String? = null
)

data class WithdrawalUiState(
    val isWithdrawalDialogOpen: Boolean = false,
    val withdrawalType: String = "agent_cash", // "agent_cash", "mobile_money"
    val withdrawalOperator: String? = null, // "MPESA", "ORANGE", "AFRIMONEY", "AIRTEL", "MTN"
    val withdrawalRecipient: String = "", // Agent Wallet ID or operator phone
    val withdrawalAmount: String = "",
    val withdrawalCurrency: String = "USD",
    val withdrawalCountryCode: String = "CD",
    val withdrawalCountryName: String = "RD Congo",
    val withdrawalCountryDialCode: String = "+243",
    val withdrawalPreview: WithdrawalPreviewDto? = null,
    val isWithdrawalPreviewLoading: Boolean = false,
    val withdrawalPreviewError: String? = null,
    val isWithdrawalConfirmLoading: Boolean = false,
    val withdrawalConfirmError: String? = null,
    val withdrawalSuccess: Boolean = false,
    val withdrawalStep: Int = 1,
    val searchedAgentProfile: PublicProfileDto? = null,
    val isSearchingAgent: Boolean = false,
    val agentSearchError: String? = null
)

data class LoansUiState(
    val loanCurrency: String = "USD",
    val isLoansDialogOpen: Boolean = false,
    val isLoadingLoanOffer: Boolean = false,
    val loanOffer: LoanOfferDto? = null,
    val loanOfferError: String? = null,
    val activeLoan: LoanDto? = null,
    val isLoadingActiveLoan: Boolean = false,
    val activeLoanError: String? = null,
    val loanHistory: List<LoanDto> = emptyList(),
    val isLoadingLoanHistory: Boolean = false,
    val loanHistoryError: String? = null,
    val loanRequestAmount: String = "",
    val loanRequestDuration: Int = 3,
    val isRequestingLoan: Boolean = false,
    val loanRequestError: String? = null,
    val loanRequestSuccess: Boolean = false,
    val loanRepayAmount: String = "",
    val isRepayingLoan: Boolean = false,
    val loanRepayError: String? = null,
    val loanRepaySuccess: Boolean = false,
    val isRepayInstallmentLoading: Boolean = false,
    val repayInstallmentError: String? = null,
    val loanInstallmentSuccess: Boolean = false,
    val loanSelectedTab: Int = 0
)

data class AgentSuiteUiState(
    val isAgentBalanceVisible: Boolean = true,
    val isAgentServicesDialogOpen: Boolean = false,
    val agentActiveTab: Int = 0,
    val agentCommissionUsd: Double = 0.0,
    val agentCommissionCdf: Double = 0.0,
    val agentCommissionEur: Double = 0.0,
    val isSweepCommissionDialogOpen: Boolean = false,
    val sweepCurrency: String = "USD",
    val sweepAmount: String = "",
    val sweepIsAll: Boolean = true,
    val sweepPin: String = "",
    val isSweepingCommission: Boolean = false,
    val sweepCommissionError: String? = null,
    val sweepCommissionSuccess: String? = null,
    val agentCommissionsMap: Map<String, Double> = emptyMap(),
    val agentBalancesMap: Map<String, Double> = emptyMap(),
    val agentDepositStep: String = "identify",
    val agentDepositClientRef: String = "",
    val agentDepositFoundClient: AgentDepositClientDto? = null,
    val agentDepositAgentBalances: Map<String, Double> = emptyMap(),
    val agentDepositAmount: String = "",
    val agentDepositCurrency: String = "CDF",
    val agentDepositPin: String = "",
    val isAgentDepositLoading: Boolean = false,
    val agentDepositError: String? = null,
    val agentDepositPreview: AgentDepositPreviewDto? = null,
    val agentDepositDetail: AgentDepositDetailDto? = null,
    val agentDepositSuccessDetail: AgentDepositDetailDto? = null,
    val agentDepositFinancialDetails: AgentDepositResponse? = null,
    val agentDepositSuccess: AgentDepositResponse? = null,
    val agentWithdrawStep: Int = 1,
    val agentWithdrawClientRef: String = "",
    val agentWithdrawFoundClient: PublicProfileDto? = null,
    val agentWithdrawAmount: String = "",
    val agentWithdrawCurrency: String = "USD",
    val agentWithdrawChannel: String = "sms",
    val agentWithdrawClientOtp: String = "",
    val isAgentWithdrawLoading: Boolean = false,
    val agentWithdrawError: String? = null,
    val agentWithdrawSuccess: AgentWithdrawResponse? = null,
    val agentWithdrawOperationId: String? = null,
    val agentWithdrawOtpChannels: List<String> = emptyList(),
    val agentLoanStep: Int = 1,
    val agentLoanClientRef: String = "",
    val agentLoanTarget: AgentLoanTargetResponse? = null,
    val isAgentSearchingLoan: Boolean = false,
    val agentLoanSearchError: String? = null,
    val agentLoanAmount: String = "",
    val agentLoanCurrency: String = "USD",
    val agentLoanPin: String = "",
    val isAgentLoanRepayLoading: Boolean = false,
    val agentLoanRepayError: String? = null,
    val agentLoanRepaySuccess: AgentLoanRepaymentResponse? = null,
    val agentOperationsHistory: List<AgentOperationRecord> = emptyList(),
    val agentDepositSearchMode: String = "wallet",
    val isAgentExternalMoMoOpen: Boolean = false,
    val agentExternalMoMoOperator: String = "mpesa",
    val agentExternalMoMoPhone: String = "",
    val agentExternalMoMoAmount: String = "",
    val isAgentExternalMoMoLoading: Boolean = false,
    val agentExternalMoMoError: String? = null,
    val agentExternalMoMoSuccess: String? = null,
    val agentExternalMoMoReference: String? = null,
    val isAgentCustomerSheetOpen: Boolean = false,
    val isAgentCustomerOptionsLoading: Boolean = false,
    val agentCustomerOptions: AgentCustomerOptionsResponse? = null,
    val agentCustomersList: List<AgentCustomerItemDto> = emptyList(),
    val isLoadingAgentCustomers: Boolean = false,
    val isAgentRegisteringCustomer: Boolean = false,
    val agentCustomerRegisterSuccess: AgentRegisterCustomerResponse? = null,
    val agentCustomerRegisterError: String? = null
)

data class ShoppingUiState(
    val isLoadingShoppingBoutiques: Boolean = false,
    val shoppingBoutiquesError: String? = null,
    val isAgentShoppingDialogOpen: Boolean = false,
    val shoppingActiveTab: Int = 0,
    val isShoppingLoading: Boolean = false,
    val shoppingError: String? = null,
    val shoppingSuccessMessage: String? = null,
    val shoppingBoutiques: List<BoutiqueDto> = emptyList(),
    val shoppingProducts: List<ProductDto> = emptyList(),
    val selectedBoutique: BoutiqueDto? = null,
    val selectedProduct: ProductDto? = null,
    val editingProduct: ProductDto? = null,
    val productReferenceInfo: ProductReferenceResponse? = null,
    val publicCatalog: PublicCatalogResponse? = null,
    val isPublicCatalogLoading: Boolean = false
)

enum class PayScanMode {
    AUTO,
    SUPERMARKET_BASKET,
    PAYMENT_REQUEST,
    WALLET_TRANSFER
}

data class PayScanCartItem(
    val id: String,
    val barcode: String? = null,
    val name: String,
    val unitPrice: Double,
    val currency: String = "USD",
    val quantity: Int = 1,
    val boutiqueName: String? = null,
    val sellerWalletId: String? = null,
    val categoryId: String? = "cat-divers",
    val categoryName: String? = "Article",
    val treatmentInfo: String? = null,
    val isApparel: Boolean = false,
    val size: String? = null
)

data class PayScanUiState(
    val isPayScanOpen: Boolean = false,
    val currentMode: PayScanMode = PayScanMode.SUPERMARKET_BASKET,
    val basketItems: List<PayScanCartItem> = emptyList(),
    val paymentCurrency: String = "USD",
    val isInterpreting: Boolean = false,
    val isProcessingPayment: Boolean = false,
    val scanError: String? = null,
    val scanSuccessMessage: String? = null,
    val lastInterpretedResult: com.example.data.model.ScanResultData? = null,
    val currentPosSaleData: com.example.data.model.PosSaleData? = null,
    val completedTransaction: com.example.data.model.PosTransactionDetails? = null,
    val showBasketDrawer: Boolean = false,
    val showConfirmPaymentDialog: Boolean = false,
    val showSuccessReceiptDialog: Boolean = false,
    val manualInputText: String = "",
    val defaultSellerWalletId: String = "CD0100000001",
    val defaultBoutiqueName: String = "Supermarché CashPay",
    val lastScannedApparelItem: PayScanCartItem? = null,
    val showApparelTreatmentSheet: Boolean = false
)

data class StarlinkTerminalDto(
    val id: String,
    val name: String,
    val serialNumber: String,
    val status: String,
    val signalQuality: String,
    val planName: String,
    val expiryDate: String,
    val ipAddress: String,
    val firmwareVersion: String
)

data class StarlinkPlanDto(
    val id: String,
    val name: String,
    val price: Double,
    val currency: String,
    val description: String,
    val speed: String
)

data class StarlinkSubCustomerDto(
    val terminalId: String,
    val name: String,
    val walletId: String,
    val macAddress: String,
    val expiryDate: String,
    val dataLimitGb: Int
)

data class StarlinkUiState(
    val isStarlinkManagerOpen: Boolean = false,
    val activeTab: Int = 0,
    val terminals: List<StarlinkTerminalDto> = emptyList(),
    val plans: List<StarlinkPlanDto> = listOf(
        StarlinkPlanDto("plan_std", "Starlink Standard", 99.0, "USD", "Idéal pour les foyers et petites entreprises (illimité)", "150-250 Mbps"),
        StarlinkPlanDto("plan_pri", "Starlink Priority 1TB", 250.0, "USD", "Priorité réseau élevée pour entreprises et commerces POS", "220-350 Mbps"),
        StarlinkPlanDto("plan_mob", "Starlink Mobile / Roam", 150.0, "USD", "Pour les déplacements et équipes terrain en mouvement", "130-220 Mbps")
    ),
    val selectedTerminal: StarlinkTerminalDto? = null,
    val isTerminalDetailOpen: Boolean = false,
    val isLoading: Boolean = false,
    val isRebooting: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val pinInput: String = "",
    val showPinDialog: Boolean = false,
    val pendingActionPlan: StarlinkPlanDto? = null,
    val linkKitSerialNumberInput: String = "",
    val showLinkKitDialog: Boolean = false,
    val subCustomers: List<StarlinkSubCustomerDto> = emptyList(),
    val showAddSubCustomerDialog: Boolean = false,
    val terminalLiveStatus: com.example.data.model.StarlinkTerminalStatusData? = null,
    val lastTelemetryUpdate: String? = null
)

data class ExchangeUiState(
    val isExchangeDialogOpen: Boolean = false,
    val exchangeAmount: String = "",
    val exchangeFromCurrency: String = "USD",
    val exchangeToCurrency: String = "CDF",
    val exchangePin: String = "",
    val isExchangeLoading: Boolean = false,
    val exchangeError: String? = null,
    val exchangeSuccessResponse: ExchangeResponse? = null,
    val exchangeRatesMap: Map<String, Double> = emptyMap(),
    val isExchangeRatesLoading: Boolean = false,
    val realtimeQuoteRate: Double? = null,
    val realtimeQuoteAmount: Double? = null,
    val isQuoteLoading: Boolean = false,
    val quoteError: String? = null
)

data class ClaimsUiState(
    val isClaimsScreenOpen: Boolean = false,
    val isNewClaimDialogOpen: Boolean = false,
    val claimsList: List<com.example.data.model.InternationalTransferClaimDto> = emptyList(),
    val isClaimsLoading: Boolean = false,
    val claimsError: String? = null,
    val selectedClaimDetail: com.example.data.model.InternationalTransferClaimDto? = null,
    val isClaimDetailDialogOpen: Boolean = false,
    val isClaimDetailLoading: Boolean = false,
    val claimDetailError: String? = null,
    val isSubmittingClaim: Boolean = false,
    val newClaimError: String? = null,
    val newClaimSuccessMessage: String? = null,
    val newClaimDuplicateClaim: com.example.data.model.InternationalTransferClaimDto? = null
)

data class ReferralsUiState(
    val isReferralDialogOpen: Boolean = false,
    val isLoadingReferrals: Boolean = false,
    val referralsError: String? = null,
    val referralResponse: com.example.data.model.ReferralResponse? = null,
    val isClaimingReward: Boolean = false,
    val claimingReferralId: Long? = null,
    val claimSuccessMessage: String? = null,
    val claimErrorMessage: String? = null
)

data class TvSubscriptionUiState(
    val isTvDialogOpen: Boolean = false,
    val selectedProvider: String = "CPLUS", // CPLUS, EASYTV, BLUS
    val isLoadingPackages: Boolean = false,
    val packages: List<com.example.data.model.TvPackageDto> = emptyList(),
    val packagesError: String? = null,
    val selectedPackage: com.example.data.model.TvPackageDto? = null,
    val smartcardNumber: String = "",
    val paymentCurrency: String = "CDF", // CDF, USD, EUR
    val quoteRate: Double? = null,
    val estimatedDebitAmount: Double? = null,
    val isCalculatingQuote: Boolean = false,
    val quoteError: String? = null,
    val pin: String = "",
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val subscriptionResult: com.example.data.model.TvSubscriptionResponse? = null,
    val recentSubscriptions: List<com.example.data.model.TvSubscriptionResponse> = emptyList()
)

data class RegidesoUiState(
    val isDialogOpen: Boolean = false,
    val abonnementId: String = "",
    val selectedProvince: String = "KIN",
    val isLoading: Boolean = false,
    val factures: List<com.example.data.model.RegidesoFactureDto> = emptyList(),
    val errorMessage: String? = null,
    val hasSearched: Boolean = false
)

data class CpkUiState(
    val isCpkDialogOpen: Boolean = false,
    val keys: List<com.example.data.local.CpkKeyEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val searchQuery: String = "",
    val selectedFilter: String = "ALL", // "ALL", "ACTIVE", "CLAIMED", "CANCELLED", "CLOUD", "LOCAL"
    val selectedKeyForDetail: com.example.data.local.CpkKeyEntity? = null,
    val isCheckingStatus: Boolean = false,
    val checkingSignature: String? = null,
    val isSyncingCloud: Boolean = false,
    val syncingSignature: String? = null,
    // Issuance modal state
    val isIssueModalOpen: Boolean = false,
    val issueAmount: String = "",
    val issueCurrency: String = "USD",
    val issueSecurityLevel: String = "none",
    val issueKeyPin: String = "",
    val issueUserPin: String = "",
    val isIssuing: Boolean = false,
    val issueError: String? = null,
    val issueSuccessResult: com.example.data.model.CpkIssueResponse? = null,
    // Recovery modal state
    val isRecoverModalOpen: Boolean = false,
    val recoverKey: com.example.data.local.CpkKeyEntity? = null,
    val recoverKeyPin: String = "",
    val recoverPartialAmount: String = "",
    val isRecovering: Boolean = false,
    val recoverError: String? = null,
    val recoverSuccessResult: com.example.data.model.CpkRecoverResponse? = null,
    // Cancel modal state
    val isCancelModalOpen: Boolean = false,
    val cancelKey: com.example.data.local.CpkKeyEntity? = null,
    val cancelUserPin: String = "",
    val isCancelling: Boolean = false,
    val cancelError: String? = null,
    val cancelSuccessResult: com.example.data.model.CpkCancelResponse? = null,
    // Cloud Drive state
    val isDriveConnected: Boolean = false,
    val connectedDriveEmail: String? = null,
    val lastCloudSyncTime: String = "Jamais synchronisé",
    val isRestoringFromCloud: Boolean = false,
    val isLinkingDriveDialogOpen: Boolean = false
)

data class DepositUiState(
    val isDepositDialogOpen: Boolean = false,
    val depositMethod: String = "mobile_money",
    val depositAmount: String = "",
    val depositCurrency: String = "USD",
    val depositOperator: String = "mpesa",
    val depositPhoneNumber: String = "",
    val depositCountryCode: String = "CD",
    val isDepositLoading: Boolean = false,
    val depositError: String? = null,
    val depositSuccessMessage: String? = null,
    val depositPendingReference: String? = null,
    val depositPaymentUrl: String? = null,
    val isDepositWebViewOpen: Boolean = false
)

data class ForgotPinUiState(
    val isForgotPinDialogOpen: Boolean = false,
    val forgotPinStep: Int = 1,
    val forgotPinPhone: String = "",
    val forgotPinChannel: String = "sms",
    val forgotPinUserId: Long? = null,
    val forgotPinOtp: String = "",
    val forgotPinNewPin: String = "",
    val forgotPinConfirmPin: String = "",
    val isForgotPinLoading: Boolean = false,
    val forgotPinError: String? = null,
    val forgotPinSuccessMessage: String? = null
)

data class AnalyticsUiState(
    val isAnalyticsOpen: Boolean = false,
    val selectedTab: Int = 0, // 0: Synthèse & Portefeuille, 1: Flux & Graphiques, 2: Marché BTC
    val selectedPeriod: String = "1M", // "1J", "1S", "1M", "3M", "1A", "ALL"
    val flowDirectionFilter: String = "ALL", // "ALL", "INCOMING", "OUTGOING"
    val btcTelemetry: com.example.data.model.BtcMarketTelemetry = com.example.data.model.BtcMarketTelemetry(),
    val isBtcMarketLoading: Boolean = false,
    val btcMarketError: String? = null,
    val budgetTargetUsd: Double = 5000.0,
    val liveActivePointIndex: Int = -1
)

/**
 * Root UI State composing focused modular states.
 * Eliminates JVM/DEX signature bloat and allows Kotlin compiler smart-casting.
 */
data class DashboardUiState(
    val main: MainUiState = MainUiState(),
    val cards: CardsUiState = CardsUiState(),
    val contacts: ContactsUiState = ContactsUiState(),
    val withdrawal: WithdrawalUiState = WithdrawalUiState(),
    val loans: LoansUiState = LoansUiState(),
    val agent: AgentSuiteUiState = AgentSuiteUiState(),
    val shopping: ShoppingUiState = ShoppingUiState(),
    val exchange: ExchangeUiState = ExchangeUiState(),
    val deposit: DepositUiState = DepositUiState(),
    val forgotPin: ForgotPinUiState = ForgotPinUiState(),
    val claims: ClaimsUiState = ClaimsUiState(),
    val referrals: ReferralsUiState = ReferralsUiState(),
    val tv: TvSubscriptionUiState = TvSubscriptionUiState(),
    val regideso: RegidesoUiState = RegidesoUiState(),
    val cpk: CpkUiState = CpkUiState(),
    val payscan: PayScanUiState = PayScanUiState(),
    val starlink: StarlinkUiState = StarlinkUiState(),
    val analytics: AnalyticsUiState = AnalyticsUiState()
) {
    // --- MAIN IMMUTABLE FIELDS ---
    val selectedTab: Int = main.selectedTab
    val isEditProfileOpen: Boolean = main.isEditProfileOpen
    val editFullName: String = main.editFullName
    val editCity: String = main.editCity
    val editProfession: String = main.editProfession
    val editAddress: String = main.editAddress
    val editPinConfirmation: String = main.editPinConfirmation
    val isEditingLoading: Boolean = main.isEditingLoading
    val editErrorMessage: String? = main.editErrorMessage
    val editSuccessMessage: String? = main.editSuccessMessage
    val isQuickTransferOpen: Boolean = main.isQuickTransferOpen
    val transferRecipient: String = main.transferRecipient
    val transferAmount: String = main.transferAmount
    val transferCurrency: String = main.transferCurrency
    val transferSuccess: Boolean = main.transferSuccess
    val walletResponse: WalletResponse? = main.walletResponse
    val isLoadingWallet: Boolean = main.isLoadingWallet
    val walletError: String? = main.walletError
    val transfersMeta: TransfersMetaResponse? = main.transfersMeta
    val transferFee: Double? = main.transferFee
    val transferTotalDebit: Double? = main.transferTotalDebit
    val transferError: String? = main.transferError
    val isTransferLoading: Boolean = main.isTransferLoading
    val transferStep: Int = main.transferStep
    val txDirectionFilter: String = main.txDirectionFilter
    val txDateFilter: String = main.txDateFilter
    val txSearchQuery: String = main.txSearchQuery
    val customFromDate: String = main.customFromDate
    val customToDate: String = main.customToDate
    val isAllTransactionsOpen: Boolean = main.isAllTransactionsOpen
    val selectedTransactionForDetail: TransactionEntity? = main.selectedTransactionForDetail
    val isLoadingTransactions: Boolean = main.isLoadingTransactions
    val transactionsError: String? = main.transactionsError
    val isNotificationDialogOpen: Boolean = main.isNotificationDialogOpen
    val isLoadingNotifications: Boolean = main.isLoadingNotifications
    val notificationsError: String? = main.notificationsError
    val isDarkMode: Boolean = main.isDarkMode
    val isActivateAgentDialogOpen: Boolean = main.isActivateAgentDialogOpen
    val agentPlan: String = main.agentPlan
    val agentPin: String = main.agentPin
    val isActivatingAgent: Boolean = main.isActivatingAgent
    val activateAgentError: String? = main.activateAgentError
    val activateAgentSuccessMessage: String? = main.activateAgentSuccessMessage
    val isBalanceVisible: Boolean = main.isBalanceVisible
    val currentUserId: Long? = main.currentUserId
    val transferSearchMode: String = main.transferSearchMode
    val isSearchingTransferRecipient: Boolean get() = main.isSearchingTransferRecipient
    val isReceiveBtcDialogOpen: Boolean get() = main.isReceiveBtcDialogOpen
    val isSendBtcDialogOpen: Boolean get() = main.isSendBtcDialogOpen
    val btcLiveRateUsd: Double get() = main.btcLiveRateUsd
    val isBtcRateLoading: Boolean get() = main.isBtcRateLoading
    val isBtcTransferLoading: Boolean get() = main.isBtcTransferLoading
    val btcTransferError: String? get() = main.btcTransferError
    val btcTransferSuccess: Boolean get() = main.btcTransferSuccess
    val btcTransferTxRef: String? get() = main.btcTransferTxRef
    val isLoansScreenOpen: Boolean get() = main.isLoansScreenOpen
    val usdtAddress: String? get() = main.usdtAddress
    val usdtBalance: Double get() = main.usdtBalance
    val usdtOnChainBalance: Double? get() = main.usdtOnChainBalance
    val isUsdtLoading: Boolean get() = main.isUsdtLoading
    val usdtError: String? get() = main.usdtError
    val usdtDeposits: List<com.example.data.model.UsdtDepositItem> get() = main.usdtDeposits
    val isUsdtDepositsLoading: Boolean get() = main.isUsdtDepositsLoading
    val isReceiveUsdtDialogOpen: Boolean get() = main.isReceiveUsdtDialogOpen
    val isSendUsdtDialogOpen: Boolean get() = main.isSendUsdtDialogOpen
    val isUsdtDepositsDialogOpen: Boolean get() = main.isUsdtDepositsDialogOpen
    val isUsdtTransferLoading: Boolean get() = main.isUsdtTransferLoading
    val usdtTransferError: String? get() = main.usdtTransferError
    val usdtTransferSuccess: Boolean get() = main.usdtTransferSuccess
    val usdtTransferPending: Boolean get() = main.usdtTransferPending
    val usdtTransferTxRef: String? get() = main.usdtTransferTxRef

    // --- CARDS IMMUTABLE FIELDS ---
    val userCards: List<CardInfoDto> = cards.userCards
    val cardCatalog: List<CardCatalogDto> = cards.cardCatalog
    val isLoadingCards: Boolean = cards.isLoadingCards
    val cardsError: String? = cards.cardsError
    val catalogError: String? = cards.catalogError
    val isBuyCardDialogOpen: Boolean = cards.isBuyCardDialogOpen
    val purchaseBrand: String = cards.purchaseBrand
    val purchaseType: String = cards.purchaseType
    val purchaseCurrency: String = cards.purchaseCurrency
    val purchasePin: String = cards.purchasePin
    val isPurchaseLoading: Boolean = cards.isPurchaseLoading
    val purchaseError: String? = cards.purchaseError
    val purchaseSuccess: Boolean = cards.purchaseSuccess
    val purchaseStep: Int = cards.purchaseStep
    val selectedCardForManage: CardInfoDto? = cards.selectedCardForManage
    val isManageCardDialogOpen: Boolean = cards.isManageCardDialogOpen
    val isChangePinDialogOpen: Boolean = cards.isChangePinDialogOpen
    val newCardPin: String = cards.newCardPin
    val revealedCardDetails: CardDetailsResponse? = cards.revealedCardDetails
    val revealPin: String = cards.revealPin
    val isRevealLoading: Boolean = cards.isRevealLoading
    val revealError: String? = cards.revealError
    val isRevealDetailsDialogOpen: Boolean = cards.isRevealDetailsDialogOpen
    val fundingType: String = cards.fundingType
    val cpkSignature: String = cards.cpkSignature
    val cpkPin: String = cards.cpkPin
    val isFundingDialogOpen: Boolean = cards.isFundingDialogOpen
    val purchaseFee: Double? = cards.purchaseFee
    val purchasePrice: Double? = cards.purchasePrice
    val virtualPrice: Double? = cards.virtualPrice
    val physicalPrice: Double? = cards.physicalPrice
    val purchaseFeeCode: String? = cards.purchaseFeeCode
    val isLoadingFee: Boolean = cards.isLoadingFee

    // --- CONTACTS IMMUTABLE FIELDS ---
    val contactsList: List<PhoneContact> = contacts.contactsList
    val recentCorrespondents: List<PhoneContact> = contacts.recentCorrespondents
    val isContactsDialogOpen: Boolean = contacts.isContactsDialogOpen
    val isContactsPageOpen: Boolean = contacts.isContactsPageOpen
    val isAddContactDialogOpen: Boolean = contacts.isAddContactDialogOpen
    val addContactPhone: String = contacts.addContactPhone
    val isSearchingAddContact: Boolean = contacts.isSearchingAddContact
    val addContactFoundProfile: PublicProfileDto? = contacts.addContactFoundProfile
    val addContactNotFound: Boolean = contacts.addContactNotFound
    val addContactError: String? = contacts.addContactError
    val isLoadingContacts: Boolean = contacts.isLoadingContacts
    val contactsError: String? = contacts.contactsError
    val selectedPublicProfile: PublicProfileDto? = contacts.selectedPublicProfile
    val selectedPublicContactName: String? = contacts.selectedPublicContactName
    val isPublicProfileOpen: Boolean = contacts.isPublicProfileOpen
    val searchContactQuery: String = contacts.searchContactQuery
    val prefilledRecipient: PublicProfileDto? = contacts.prefilledRecipient
    val prefilledContactName: String? = contacts.prefilledContactName

    // --- WITHDRAWAL IMMUTABLE FIELDS ---
    val isWithdrawalDialogOpen: Boolean = withdrawal.isWithdrawalDialogOpen
    val withdrawalType: String = withdrawal.withdrawalType
    val withdrawalOperator: String? = withdrawal.withdrawalOperator
    val withdrawalRecipient: String = withdrawal.withdrawalRecipient
    val withdrawalAmount: String = withdrawal.withdrawalAmount
    val withdrawalCurrency: String = withdrawal.withdrawalCurrency
    val withdrawalCountryCode: String = withdrawal.withdrawalCountryCode
    val withdrawalCountryName: String = withdrawal.withdrawalCountryName
    val withdrawalCountryDialCode: String = withdrawal.withdrawalCountryDialCode
    val withdrawalPreview: WithdrawalPreviewDto? = withdrawal.withdrawalPreview
    val isWithdrawalPreviewLoading: Boolean = withdrawal.isWithdrawalPreviewLoading
    val withdrawalPreviewError: String? = withdrawal.withdrawalPreviewError
    val isWithdrawalConfirmLoading: Boolean = withdrawal.isWithdrawalConfirmLoading
    val withdrawalConfirmError: String? = withdrawal.withdrawalConfirmError
    val withdrawalSuccess: Boolean = withdrawal.withdrawalSuccess
    val withdrawalStep: Int = withdrawal.withdrawalStep
    val searchedAgentProfile: PublicProfileDto? = withdrawal.searchedAgentProfile
    val isSearchingAgent: Boolean = withdrawal.isSearchingAgent
    val agentSearchError: String? = withdrawal.agentSearchError

    // --- LOANS IMMUTABLE FIELDS ---
    val isLoansDialogOpen: Boolean = loans.isLoansDialogOpen
    val isLoadingLoanOffer: Boolean = loans.isLoadingLoanOffer
    val loanCurrency: String = loans.loanCurrency
    val loanOffer: LoanOfferDto? = loans.loanOffer
    val loanOfferError: String? = loans.loanOfferError
    val activeLoan: LoanDto? = loans.activeLoan
    val isLoadingActiveLoan: Boolean = loans.isLoadingActiveLoan
    val activeLoanError: String? = loans.activeLoanError
    val loanHistory: List<LoanDto> = loans.loanHistory
    val isLoadingLoanHistory: Boolean = loans.isLoadingLoanHistory
    val loanHistoryError: String? = loans.loanHistoryError
    val loanRequestAmount: String = loans.loanRequestAmount
    val loanRequestDuration: Int = loans.loanRequestDuration
    val isRequestingLoan: Boolean = loans.isRequestingLoan
    val loanRequestError: String? = loans.loanRequestError
    val loanRequestSuccess: Boolean = loans.loanRequestSuccess
    val loanRepayAmount: String = loans.loanRepayAmount
    val isRepayingLoan: Boolean = loans.isRepayingLoan
    val loanRepayError: String? = loans.loanRepayError
    val loanRepaySuccess: Boolean = loans.loanRepaySuccess
    val isRepayInstallmentLoading: Boolean = loans.isRepayInstallmentLoading
    val repayInstallmentError: String? = loans.repayInstallmentError
    val loanInstallmentSuccess: Boolean = loans.loanInstallmentSuccess
    val loanSelectedTab: Int = loans.loanSelectedTab

    // --- AGENT SUITE IMMUTABLE FIELDS ---
    val isAgentBalanceVisible: Boolean = agent.isAgentBalanceVisible
    val isAgentServicesDialogOpen: Boolean = agent.isAgentServicesDialogOpen
    val agentActiveTab: Int = agent.agentActiveTab
    val agentCommissionUsd: Double = agent.agentCommissionUsd
    val agentCommissionCdf: Double = agent.agentCommissionCdf
    val agentCommissionEur: Double = agent.agentCommissionEur
    val isSweepCommissionDialogOpen: Boolean = agent.isSweepCommissionDialogOpen
    val sweepCurrency: String = agent.sweepCurrency
    val sweepAmount: String = agent.sweepAmount
    val sweepIsAll: Boolean = agent.sweepIsAll
    val sweepPin: String = agent.sweepPin
    val isSweepingCommission: Boolean = agent.isSweepingCommission
    val sweepCommissionError: String? = agent.sweepCommissionError
    val sweepCommissionSuccess: String? = agent.sweepCommissionSuccess
    val agentCommissionsMap: Map<String, Double> = agent.agentCommissionsMap
    val agentBalancesMap: Map<String, Double> = agent.agentBalancesMap
    val agentDepositStep: String = agent.agentDepositStep
    val agentDepositClientRef: String = agent.agentDepositClientRef
    val agentDepositFoundClient: AgentDepositClientDto? = agent.agentDepositFoundClient
    val agentDepositAgentBalances: Map<String, Double> = agent.agentDepositAgentBalances
    val agentDepositAmount: String = agent.agentDepositAmount
    val agentDepositCurrency: String = agent.agentDepositCurrency
    val agentDepositPin: String = agent.agentDepositPin
    val isAgentDepositLoading: Boolean = agent.isAgentDepositLoading
    val agentDepositError: String? = agent.agentDepositError
    val agentDepositPreview: AgentDepositPreviewDto? = agent.agentDepositPreview
    val agentDepositDetail: AgentDepositDetailDto? = agent.agentDepositDetail
    val agentDepositSuccessDetail: AgentDepositDetailDto? = agent.agentDepositSuccessDetail
    val agentDepositFinancialDetails: AgentDepositResponse? = agent.agentDepositFinancialDetails
    val agentDepositSuccess: AgentDepositResponse? = agent.agentDepositSuccess
    val agentWithdrawStep: Int = agent.agentWithdrawStep
    val agentWithdrawClientRef: String = agent.agentWithdrawClientRef
    val agentWithdrawFoundClient: PublicProfileDto? = agent.agentWithdrawFoundClient
    val agentWithdrawAmount: String = agent.agentWithdrawAmount
    val agentWithdrawCurrency: String = agent.agentWithdrawCurrency
    val agentWithdrawChannel: String = agent.agentWithdrawChannel
    val agentWithdrawClientOtp: String = agent.agentWithdrawClientOtp
    val isAgentWithdrawLoading: Boolean = agent.isAgentWithdrawLoading
    val agentWithdrawError: String? = agent.agentWithdrawError
    val agentWithdrawSuccess: AgentWithdrawResponse? = agent.agentWithdrawSuccess
    val agentWithdrawOperationId: String? = agent.agentWithdrawOperationId
    val agentWithdrawOtpChannels: List<String> = agent.agentWithdrawOtpChannels
    val agentLoanStep: Int = agent.agentLoanStep
    val agentLoanClientRef: String = agent.agentLoanClientRef
    val agentLoanTarget: AgentLoanTargetResponse? = agent.agentLoanTarget
    val isAgentSearchingLoan: Boolean = agent.isAgentSearchingLoan
    val agentLoanSearchError: String? = agent.agentLoanSearchError
    val agentLoanAmount: String = agent.agentLoanAmount
    val agentLoanCurrency: String = agent.agentLoanCurrency
    val agentLoanPin: String = agent.agentLoanPin
    val isAgentLoanRepayLoading: Boolean = agent.isAgentLoanRepayLoading
    val agentLoanRepayError: String? = agent.agentLoanRepayError
    val agentLoanRepaySuccess: AgentLoanRepaymentResponse? = agent.agentLoanRepaySuccess
    val agentOperationsHistory: List<AgentOperationRecord> = agent.agentOperationsHistory
    val agentDepositSearchMode: String = agent.agentDepositSearchMode
    val isAgentExternalMoMoOpen: Boolean = agent.isAgentExternalMoMoOpen
    val agentExternalMoMoOperator: String = agent.agentExternalMoMoOperator
    val agentExternalMoMoPhone: String = agent.agentExternalMoMoPhone
    val agentExternalMoMoAmount: String = agent.agentExternalMoMoAmount
    val isAgentExternalMoMoLoading: Boolean = agent.isAgentExternalMoMoLoading
    val agentExternalMoMoError: String? = agent.agentExternalMoMoError
    val agentExternalMoMoSuccess: String? = agent.agentExternalMoMoSuccess
    val agentExternalMoMoReference: String? = agent.agentExternalMoMoReference
    val isAgentCustomerSheetOpen: Boolean = agent.isAgentCustomerSheetOpen
    val isAgentCustomerOptionsLoading: Boolean = agent.isAgentCustomerOptionsLoading
    val agentCustomerOptions: AgentCustomerOptionsResponse? = agent.agentCustomerOptions
    val agentCustomersList: List<AgentCustomerItemDto> = agent.agentCustomersList
    val isLoadingAgentCustomers: Boolean = agent.isLoadingAgentCustomers
    val isAgentRegisteringCustomer: Boolean = agent.isAgentRegisteringCustomer
    val agentCustomerRegisterSuccess: AgentRegisterCustomerResponse? = agent.agentCustomerRegisterSuccess
    val agentCustomerRegisterError: String? = agent.agentCustomerRegisterError

    // --- SHOPPING IMMUTABLE FIELDS ---
    val isAgentShoppingDialogOpen: Boolean = shopping.isAgentShoppingDialogOpen
    val shoppingActiveTab: Int = shopping.shoppingActiveTab
    val isLoadingShoppingBoutiques: Boolean = shopping.isLoadingShoppingBoutiques
    val shoppingBoutiquesError: String? = shopping.shoppingBoutiquesError
    val isShoppingLoading: Boolean = shopping.isShoppingLoading
    val shoppingError: String? = shopping.shoppingError
    val shoppingSuccessMessage: String? = shopping.shoppingSuccessMessage
    val shoppingBoutiques: List<BoutiqueDto> = shopping.shoppingBoutiques
    val shoppingProducts: List<ProductDto> = shopping.shoppingProducts
    val selectedBoutique: BoutiqueDto? = shopping.selectedBoutique
    val selectedProduct: ProductDto? = shopping.selectedProduct
    val editingProduct: ProductDto? = shopping.editingProduct
    val productReferenceInfo: ProductReferenceResponse? = shopping.productReferenceInfo
    val publicCatalog: PublicCatalogResponse? = shopping.publicCatalog
    val isPublicCatalogLoading: Boolean = shopping.isPublicCatalogLoading

    // --- EXCHANGE IMMUTABLE FIELDS ---
    val isExchangeDialogOpen: Boolean = exchange.isExchangeDialogOpen
    val exchangeAmount: String = exchange.exchangeAmount
    val exchangeFromCurrency: String = exchange.exchangeFromCurrency
    val exchangeToCurrency: String = exchange.exchangeToCurrency
    val exchangePin: String = exchange.exchangePin
    val isExchangeLoading: Boolean = exchange.isExchangeLoading
    val exchangeError: String? = exchange.exchangeError
    val exchangeSuccessResponse: ExchangeResponse? = exchange.exchangeSuccessResponse
    val exchangeRatesMap: Map<String, Double> = exchange.exchangeRatesMap
    val isExchangeRatesLoading: Boolean = exchange.isExchangeRatesLoading

    // --- DEPOSIT IMMUTABLE FIELDS ---
    val isDepositDialogOpen: Boolean = deposit.isDepositDialogOpen
    val depositMethod: String = deposit.depositMethod
    val depositAmount: String = deposit.depositAmount
    val depositCurrency: String = deposit.depositCurrency
    val depositOperator: String = deposit.depositOperator
    val depositPhoneNumber: String = deposit.depositPhoneNumber
    val depositCountryCode: String = deposit.depositCountryCode
    val isDepositLoading: Boolean = deposit.isDepositLoading
    val depositError: String? = deposit.depositError
    val depositSuccessMessage: String? = deposit.depositSuccessMessage
    val depositPendingReference: String? = deposit.depositPendingReference
    val depositPaymentUrl: String? = deposit.depositPaymentUrl
    val isDepositWebViewOpen: Boolean = deposit.isDepositWebViewOpen

    // --- FORGOT PIN IMMUTABLE FIELDS ---
    val isForgotPinDialogOpen: Boolean = forgotPin.isForgotPinDialogOpen
    val forgotPinStep: Int = forgotPin.forgotPinStep
    val forgotPinPhone: String = forgotPin.forgotPinPhone
    val forgotPinChannel: String = forgotPin.forgotPinChannel
    val forgotPinUserId: Long? = forgotPin.forgotPinUserId
    val forgotPinOtp: String = forgotPin.forgotPinOtp
    val forgotPinNewPin: String = forgotPin.forgotPinNewPin
    val forgotPinConfirmPin: String = forgotPin.forgotPinConfirmPin
    val isForgotPinLoading: Boolean = forgotPin.isForgotPinLoading
    val forgotPinError: String? = forgotPin.forgotPinError
    val forgotPinSuccessMessage: String? = forgotPin.forgotPinSuccessMessage

    val realtimeQuoteRate: Double? = exchange.realtimeQuoteRate
    val realtimeQuoteAmount: Double? = exchange.realtimeQuoteAmount
    val isQuoteLoading: Boolean = exchange.isQuoteLoading
    val quoteError: String? = exchange.quoteError

    // --- INTERNATIONAL TRANSFER CLAIMS IMMUTABLE FIELDS ---
    val isClaimsScreenOpen: Boolean = claims.isClaimsScreenOpen
    val isNewClaimDialogOpen: Boolean = claims.isNewClaimDialogOpen
    val claimsList: List<com.example.data.model.InternationalTransferClaimDto> = claims.claimsList
    val isClaimsLoading: Boolean = claims.isClaimsLoading
    val claimsError: String? = claims.claimsError
    val selectedClaimDetail: com.example.data.model.InternationalTransferClaimDto? = claims.selectedClaimDetail
    val isClaimDetailDialogOpen: Boolean = claims.isClaimDetailDialogOpen
    val claimDetailError: String? = claims.claimDetailError
    val isClaimDetailLoading: Boolean = claims.isClaimDetailLoading
    val isSubmittingClaim: Boolean = claims.isSubmittingClaim
    val newClaimError: String? = claims.newClaimError
    val newClaimSuccessMessage: String? = claims.newClaimSuccessMessage
    val newClaimDuplicateClaim: com.example.data.model.InternationalTransferClaimDto? = claims.newClaimDuplicateClaim

    // --- REFERRALS & REWARDS IMMUTABLE FIELDS ---
    val isReferralDialogOpen: Boolean = referrals.isReferralDialogOpen
    val isLoadingReferrals: Boolean = referrals.isLoadingReferrals
    val referralsError: String? = referrals.referralsError
    val referralResponse: com.example.data.model.ReferralResponse? = referrals.referralResponse
    val isClaimingReward: Boolean = referrals.isClaimingReward
    val claimingReferralId: Long? = referrals.claimingReferralId
    val claimSuccessMessage: String? = referrals.claimSuccessMessage
    val claimErrorMessage: String? = referrals.claimErrorMessage

    // --- TV SUBSCRIPTIONS IMMUTABLE FIELDS ---
    val isTvDialogOpen: Boolean = tv.isTvDialogOpen
    val tvSelectedProvider: String = tv.selectedProvider
    val isTvLoadingPackages: Boolean = tv.isLoadingPackages
    val tvPackages: List<com.example.data.model.TvPackageDto> = tv.packages
    val tvPackagesError: String? = tv.packagesError
    val tvSelectedPackage: com.example.data.model.TvPackageDto? = tv.selectedPackage
    val tvSmartcardNumber: String = tv.smartcardNumber
    val tvPaymentCurrency: String = tv.paymentCurrency
    val tvQuoteRate: Double? = tv.quoteRate
    val tvEstimatedDebitAmount: Double? = tv.estimatedDebitAmount
    val isTvCalculatingQuote: Boolean = tv.isCalculatingQuote
    val tvQuoteError: String? = tv.quoteError
    val tvPin: String = tv.pin
    val isTvSubmitting: Boolean = tv.isSubmitting
    val tvSubmitError: String? = tv.submitError
    val tvSubscriptionResult: com.example.data.model.TvSubscriptionResponse? = tv.subscriptionResult
    val tvRecentSubscriptions: List<com.example.data.model.TvSubscriptionResponse> = tv.recentSubscriptions

    // --- REGIDESO PUBLIC INVOICES IMMUTABLE FIELDS ---
    val isRegidesoDialogOpen: Boolean = regideso.isDialogOpen
    val regidesoAbonnementId: String = regideso.abonnementId
    val regidesoSelectedProvince: String = regideso.selectedProvince
    val isRegidesoLoading: Boolean = regideso.isLoading
    val regidesoFactures: List<com.example.data.model.RegidesoFactureDto> = regideso.factures
    val regidesoErrorMessage: String? = regideso.errorMessage
    val regidesoHasSearched: Boolean = regideso.hasSearched

    // --- CPK FINANCIAL KEYS IMMUTABLE FIELDS ---
    val isCpkDialogOpen: Boolean = cpk.isCpkDialogOpen
    val cpkKeyList: List<com.example.data.local.CpkKeyEntity> = cpk.keys
    val isCpkLoading: Boolean = cpk.isLoading
    val cpkErrorMessage: String? = cpk.errorMessage
    val cpkSuccessMessage: String? = cpk.successMessage
    val cpkSearchQuery: String = cpk.searchQuery
    val cpkSelectedFilter: String = cpk.selectedFilter
    val cpkSelectedKeyForDetail: com.example.data.local.CpkKeyEntity? = cpk.selectedKeyForDetail
    val isCpkCheckingStatus: Boolean = cpk.isCheckingStatus
    val cpkCheckingSignature: String? = cpk.checkingSignature
    val isCpkSyncingCloud: Boolean = cpk.isSyncingCloud
    val cpkSyncingSignature: String? = cpk.syncingSignature
    val isCpkIssueModalOpen: Boolean = cpk.isIssueModalOpen
    val isCpkRecoverModalOpen: Boolean = cpk.isRecoverModalOpen
    val isCpkCancelModalOpen: Boolean = cpk.isCancelModalOpen
    val isCpkDriveConnected: Boolean = cpk.isDriveConnected
    val cpkConnectedDriveEmail: String? = cpk.connectedDriveEmail
    val cpkLastCloudSyncTime: String = cpk.lastCloudSyncTime
    val isCpkRestoringFromCloud: Boolean = cpk.isRestoringFromCloud
    val isCpkLinkingDriveDialogOpen: Boolean = cpk.isLinkingDriveDialogOpen

    // --- STARLINK MANAGER IMMUTABLE FIELDS ---
    val isStarlinkManagerOpen: Boolean = starlink.isStarlinkManagerOpen
    val starlinkActiveTab: Int = starlink.activeTab
    val starlinkTerminals: List<StarlinkTerminalDto> = starlink.terminals
    val starlinkPlans: List<StarlinkPlanDto> = starlink.plans
    val selectedStarlinkTerminal: StarlinkTerminalDto? = starlink.selectedTerminal
    val isStarlinkTerminalDetailOpen: Boolean = starlink.isTerminalDetailOpen
    val isStarlinkLoading: Boolean = starlink.isLoading
    val starlinkError: String? = starlink.error
    val starlinkSuccessMessage: String? = starlink.successMessage
    val starlinkPinInput: String = starlink.pinInput
    val showStarlinkPinDialog: Boolean = starlink.showPinDialog
    val pendingStarlinkPlan: StarlinkPlanDto? = starlink.pendingActionPlan
    val linkKitSerialNumberInput: String = starlink.linkKitSerialNumberInput
    val showStarlinkLinkKitDialog: Boolean = starlink.showLinkKitDialog

    // --- ANALYTICS IMMUTABLE FIELDS ---
    val isAnalyticsOpen: Boolean = analytics.isAnalyticsOpen
    val analyticsTab: Int = analytics.selectedTab
    val analyticsPeriod: String = analytics.selectedPeriod
    val analyticsFlowFilter: String = analytics.flowDirectionFilter
    val btcTelemetry: com.example.data.model.BtcMarketTelemetry = analytics.btcTelemetry
    val isBtcMarketLoading: Boolean = analytics.isBtcMarketLoading
    val btcMarketError: String? = analytics.btcMarketError
    val budgetTargetUsd: Double = analytics.budgetTargetUsd
    val liveActivePointIndex: Int = analytics.liveActivePointIndex
}

