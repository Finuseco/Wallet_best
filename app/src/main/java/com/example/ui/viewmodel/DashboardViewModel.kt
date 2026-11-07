package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.WalletResponse
import com.example.data.model.TransfersMetaResponse
import com.example.data.model.TransferRequest
import com.example.data.model.TransferResponse
import com.example.data.model.AgentWithdrawResponse
import com.example.data.model.AgentDepositResponse
import com.example.data.model.AgentLoanTargetResponse
import com.example.data.model.AgentLoanRepaymentResponse
import com.example.data.model.AgentCommissionTransferResponse
import com.example.data.repository.CashPayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope


class DashboardViewModel(
    private val repository: CashPayRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    private val _commerceScanReview = MutableStateFlow<String?>(null)
    val commerceScanReview: StateFlow<String?> = _commerceScanReview.asStateFlow()
    fun closeCommerceScanReview() { _commerceScanReview.value = null }
    private fun showCommerceScanReview(raw: String) {
        updatePayScan { copy(isPayScanOpen = false, isInterpreting = false, showConfirmPaymentDialog = false) }
        _commerceScanReview.value = raw
    }

    fun updateMain(transform: MainUiState.() -> MainUiState) = _uiState.update { it.copy(main = it.main.transform()) }
    fun updateCards(transform: CardsUiState.() -> CardsUiState) = _uiState.update { it.copy(cards = it.cards.transform()) }
    fun updateContacts(transform: ContactsUiState.() -> ContactsUiState) = _uiState.update { it.copy(contacts = it.contacts.transform()) }
    fun updateWithdrawal(transform: WithdrawalUiState.() -> WithdrawalUiState) = _uiState.update { it.copy(withdrawal = it.withdrawal.transform()) }
    fun updateLoans(transform: LoansUiState.() -> LoansUiState) = _uiState.update { it.copy(loans = it.loans.transform()) }
    fun updateAgent(transform: AgentSuiteUiState.() -> AgentSuiteUiState) = _uiState.update { it.copy(agent = it.agent.transform()) }
    fun updateShopping(transform: ShoppingUiState.() -> ShoppingUiState) = _uiState.update { it.copy(shopping = it.shopping.transform()) }
    fun updateExchange(transform: ExchangeUiState.() -> ExchangeUiState) = _uiState.update { it.copy(exchange = it.exchange.transform()) }
    fun updateDeposit(transform: DepositUiState.() -> DepositUiState) = _uiState.update { it.copy(deposit = it.deposit.transform()) }
    fun updateForgotPin(transform: ForgotPinUiState.() -> ForgotPinUiState) = _uiState.update { it.copy(forgotPin = it.forgotPin.transform()) }
    fun updateClaims(transform: ClaimsUiState.() -> ClaimsUiState) = _uiState.update { it.copy(claims = it.claims.transform()) }
    fun updateReferrals(transform: ReferralsUiState.() -> ReferralsUiState) = _uiState.update { it.copy(referrals = it.referrals.transform()) }
    fun updateTv(transform: TvSubscriptionUiState.() -> TvSubscriptionUiState) = _uiState.update { it.copy(tv = it.tv.transform()) }
    fun updateRegideso(transform: RegidesoUiState.() -> RegidesoUiState) = _uiState.update { it.copy(regideso = it.regideso.transform()) }
    fun updateCpk(transform: CpkUiState.() -> CpkUiState) = _uiState.update { it.copy(cpk = it.cpk.transform()) }
    fun updatePayScan(transform: PayScanUiState.() -> PayScanUiState) = _uiState.update { it.copy(payscan = it.payscan.transform()) }
    fun updateAnalytics(transform: AnalyticsUiState.() -> AnalyticsUiState) = _uiState.update { it.copy(analytics = it.analytics.transform()) }
    fun openLoansScreen() {
        closeLoanConfirmation()
        updateMain { copy(isLoansScreenOpen = true) }
        fetchLoanOffer(); fetchActiveLoan(); fetchLoanHistory()
    }
    fun closeLoansScreen() { closeLoanConfirmation(); updateMain { copy(isLoansScreenOpen = false) } }

    fun openAnalyticsScreen() {
        updateAnalytics { copy(isAnalyticsOpen = true) }
        fetchBtcMarketTelemetry(_uiState.value.analytics.selectedPeriod)
    }

    fun closeAnalyticsScreen() {
        updateAnalytics { copy(isAnalyticsOpen = false) }
    }

    fun setAnalyticsTab(tab: Int) = updateAnalytics { copy(selectedTab = tab) }

    fun setAnalyticsPeriod(period: String) {
        updateAnalytics { copy(selectedPeriod = period) }
        fetchBtcMarketTelemetry(period)
    }

    fun setAnalyticsFlowFilter(filter: String) = updateAnalytics { copy(flowDirectionFilter = filter) }

    fun setBudgetTarget(target: Double) = updateAnalytics { copy(budgetTargetUsd = target) }

    fun setLiveActivePointIndex(idx: Int) = updateAnalytics { copy(liveActivePointIndex = idx) }

    fun fetchBtcMarketTelemetry(period: String = "1M") {
        updateAnalytics { copy(isBtcMarketLoading = true, btcMarketError = null) }
        viewModelScope.launch {
            val result = repository.getBtcMarketTelemetry(period)
            result.onSuccess { telemetry ->
                updateAnalytics {
                    copy(
                        isBtcMarketLoading = false,
                        btcTelemetry = telemetry,
                        btcMarketError = null
                    )
                }
                updateMain { copy(btcLiveRateUsd = telemetry.priceUsd) }
            }.onFailure { err ->
                updateAnalytics {
                    copy(
                        isBtcMarketLoading = false,
                        btcMarketError = err.message
                    )
                }
            }
        }
    }


    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val session: StateFlow<SessionEntity?> = repository.session
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<TransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<com.example.data.local.NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cpkKeys: StateFlow<List<com.example.data.local.CpkKeyEntity>> = repository.cpkKeys
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.cpkKeys.collect { keys ->
                updateCpk { copy(keys = keys) }
            }
        }
        viewModelScope.launch {
            repository.session.collect { session ->
                if (session != null && session.isAuthenticated) {
                    val userId = session.userId
                    updateMain { copy(currentUserId = userId) }
                    fetchWallet(userId)
                    loadTransactions(userId)
                    fetchNotifications()
                    fetchUserCards()
                    fetchCardCatalog()
                    loadAgentCommissions()
                    fetchBtcLiveRate()
                } else if (session == null || !session.isAuthenticated) {
                    _uiState.value = DashboardUiState(main = MainUiState(isDarkMode = _uiState.value.isDarkMode))
                }
            }
        }
        viewModelScope.launch {
            repository.userProfile.collect { prof ->
                if (prof != null) {
                    com.example.data.remote.ApiClient.currentWalletId = prof.walletId
                    com.example.data.remote.ApiClient.currentUserId = prof.id
                    com.example.data.remote.ApiClient.currentPhone = prof.phone
                    com.example.data.remote.ApiClient.currentUserRole = prof.role
                }
            }
        }
        seedRecentCorrespondents()
    }

    fun getEffectiveUserId(provided: Long? = null): Long {
        if (provided != null && provided > 1L) return provided
        val stId = _uiState.value.currentUserId
        if (stId != null && stId > 0L) return stId
        return provided ?: 1L
    }

    fun setTransferSearchMode(mode: String) {
        updateMain { copy(transferSearchMode = mode, transferError = null) }
    }

    fun setAgentDepositSearchMode(mode: String) {
        updateAgent { copy(agentDepositSearchMode = mode, agentDepositError = null) }
    }

    // --- CARDS ACTIONS ---
    fun fetchUserCards() {
        updateCards { copy(isLoadingCards = true, cardsError = null) }
        viewModelScope.launch {
            repository.getUserCards()
                .onSuccess { cards ->
                    updateCards { copy(isLoadingCards = false, userCards = cards) }
                }
                .onFailure { err ->
                    updateCards { copy(isLoadingCards = false, cardsError = err.message) }
                }
        }
    }

    fun fetchCardCatalog() {
        updateCards { copy(catalogError = null) }
        viewModelScope.launch {
            repository.getCardCatalog()
                .onSuccess { resp ->
                    updateCards { copy(cardCatalog = resp.cards) }
                    val state = _uiState.value
                    if (state.isBuyCardDialogOpen) {
                        val match = resp.cards.firstOrNull {
                            it.type.equals(state.purchaseType, ignoreCase = true) &&
                            (it.currency.isEmpty() || it.currency.equals(state.purchaseCurrency, ignoreCase = true)) &&
                            (it.brand.isEmpty() || it.brand.equals(state.purchaseBrand, ignoreCase = true))
                        }
                        if (match != null && match.price > 0.0) {
                            updateCards {
                                copy(
                                    purchasePrice = match.price,
                                    purchaseFee = match.price
                                )
                            }
                        }
                    }
                }
                .onFailure { err ->
                    updateCards { copy(catalogError = err.message) }
                }
        }
    }

    fun getCardFeeCode(type: String, currency: String): String {
        val normType = type.trim().lowercase()
        val normCurr = currency.trim().uppercase()
        return if (normType == "physique") {
            if (normCurr == "EUR") "BUY_CARDS_EUR_P_FEE" else "BUY_CARDS_USD_P_FEE"
        } else {
            if (normCurr == "EUR") "BUY_CARDS_EUR_V_FEE" else "BUY_CARDS_USD_V_FEE"
        }
    }

    fun fetchCardFees() {
        val state = _uiState.value
        val feeCode = getCardFeeCode(state.purchaseType, state.purchaseCurrency)
        val catalogItem = state.cardCatalog.firstOrNull {
            it.type.equals(state.purchaseType, ignoreCase = true) &&
            (it.currency.isEmpty() || it.currency.equals(state.purchaseCurrency, ignoreCase = true)) &&
            (it.brand.isEmpty() || it.brand.equals(state.purchaseBrand, ignoreCase = true))
        }
        val currentPriceFromCatalog = catalogItem?.price?.takeIf { it > 0.0 } ?: state.purchasePrice

        updateCards {
            copy(
                isLoadingFee = true,
                purchaseFeeCode = feeCode,
                purchasePrice = currentPriceFromCatalog,
                purchaseFee = currentPriceFromCatalog
            )
        }
        viewModelScope.launch {
            repository.getCardFees(
                type = state.purchaseType.trim().lowercase(),
                currency = state.purchaseCurrency.trim().uppercase(),
                brand = state.purchaseBrand.trim().lowercase(),
                feeCode = feeCode
            ).onSuccess { resp ->
                val apiPrice = resp.price ?: resp.fee ?: resp.amount ?: resp.total ?: currentPriceFromCatalog
                updateCards {
                    copy(
                        isLoadingFee = false,
                        purchaseFee = apiPrice,
                        purchasePrice = apiPrice,
                        purchaseFeeCode = resp.feeCode ?: feeCode,
                        virtualPrice = if (state.purchaseType.equals("virtuelle", ignoreCase = true)) apiPrice else virtualPrice,
                        physicalPrice = if (state.purchaseType.equals("physique", ignoreCase = true)) apiPrice else physicalPrice
                    )
                }
            }.onFailure {
                updateCards {
                    copy(
                        isLoadingFee = false,
                        purchaseFeeCode = feeCode,
                        purchaseFee = currentPriceFromCatalog,
                        purchasePrice = currentPriceFromCatalog
                    )
                }
            }

            // Also proactively fetch fee for other type so both options show exact API fees
            val otherType = if (state.purchaseType.equals("virtuelle", ignoreCase = true)) "physique" else "virtuelle"
            val otherFeeCode = getCardFeeCode(otherType, state.purchaseCurrency)
            repository.getCardFees(
                type = otherType,
                currency = state.purchaseCurrency.trim().uppercase(),
                brand = state.purchaseBrand.trim().lowercase(),
                feeCode = otherFeeCode
            ).onSuccess { otherResp ->
                val otherPrice = otherResp.price ?: otherResp.fee ?: otherResp.amount ?: otherResp.total
                if (otherPrice != null) {
                    updateCards {
                        copy(
                            virtualPrice = if (otherType == "virtuelle") otherPrice else virtualPrice,
                            physicalPrice = if (otherType == "physique") otherPrice else physicalPrice
                        )
                    }
                }
            }
        }
    }

    fun openBuyCardDialog() {
        val initialType = "virtuelle"
        val initialCurr = "USD"
        val initialFeeCode = getCardFeeCode(initialType, initialCurr)
        val match = _uiState.value.cardCatalog.firstOrNull {
            it.type.equals(initialType, ignoreCase = true) &&
            (it.currency.isEmpty() || it.currency.equals(initialCurr, ignoreCase = true))
        }
        val p = match?.price?.takeIf { it > 0.0 }

        updateCards { copy(isBuyCardDialogOpen = true,
                purchaseBrand = "visa",
                purchaseType = initialType,
                purchaseCurrency = initialCurr,
                purchasePin = "",
                purchaseError = null,
                purchaseSuccess = false,
                purchaseFeeCode = initialFeeCode,
                purchasePrice = p,
                purchaseFee = p,
                isLoadingFee = true,
                purchaseStep = 1) }
        fetchCardCatalog()
        fetchCardFees()
    }

    fun closeBuyCardDialog() {
        updateCards { copy(isBuyCardDialogOpen = false, purchasePin = "", purchaseError = null) }
    }

    fun setPurchaseBrand(brand: String) {
        updateCards { copy(purchaseBrand = brand) }
        fetchCardFees()
    }

    fun setPurchaseType(type: String) {
        updateCards { copy(purchaseType = type) }
        fetchCardFees()
    }

    fun setPurchaseCurrency(currency: String) {
        updateCards { copy(purchaseCurrency = currency) }
        fetchCardFees()
    }

    fun setPurchasePin(pin: String) {
        updateCards { copy(purchasePin = pin.filter { it.isDigit() }.take(6)) }
    }

    fun nextPurchaseStep() {
        if (_uiState.value.purchaseStep == 3) {
            fetchCardFees()
        }
        updateCards { copy(purchaseStep = purchaseStep + 1, purchaseError = null) }
    }

    fun prevPurchaseStep() {
        updateCards { copy(purchaseStep = if (purchaseStep > 1) purchaseStep - 1 else 1) }
    }

    fun executeCardPurchase() {
        val state = _uiState.value
        if (state.purchasePin.isBlank()) {
            updateCards { copy(purchaseError = "PIN obligatoire.") }
            return
        }
        val normType = state.purchaseType.trim().lowercase()
        if (normType != "virtuelle" && normType != "physique") {
            updateCards { copy(purchaseError = "Type de carte invalide.") }
            return
        }
        val normBrand = state.purchaseBrand.trim().lowercase()
        if (normBrand != "visa" && normBrand != "mastercard") {
            updateCards { copy(purchaseError = "Marque de carte invalide.") }
            return
        }
        val normCurr = state.purchaseCurrency.trim().uppercase()
        if (normCurr.isBlank()) {
            updateCards { copy(purchaseError = "Devise obligatoire.") }
            return
        }

        updateCards { copy(isPurchaseLoading = true, purchaseError = null) }
        viewModelScope.launch {
            repository.purchaseCard(
                brand = normBrand,
                cardType = normType,
                currency = normCurr,
                pin = state.purchasePin
            ).onSuccess { resp ->
                updateCards { copy(
                    isPurchaseLoading = false,
                    purchaseSuccess = true,
                    purchaseStep = 5,
                    purchasePin = "" // PIN cleared for security
                ) }
                fetchUserCards()
                fetchWallet()
            }.onFailure { err ->
                val errorMsg = err.message ?: "Échec de l’achat de la carte."
                updateCards { copy(isPurchaseLoading = false, purchaseError = errorMsg) }
            }
        }
    }

    fun openManageCard(card: com.example.data.model.CardInfoDto) {
        updateCards { copy(selectedCardForManage = card,
                isManageCardDialogOpen = true,
                newCardPin = "",
                revealedCardDetails = null,
                revealPin = "",
                revealError = null) }
    }

    fun closeManageCard() {
        updateCards { copy(isManageCardDialogOpen = false, selectedCardForManage = null) }
    }

    fun freezeCard(cardId: String) {
        viewModelScope.launch {
            repository.freezeCard(cardId)
                .onSuccess {
                    fetchUserCards()
                    // Re-open with updated card to show correct freeze/unfreeze state
                    _uiState.value.userCards.find { it.id == cardId }?.let { updatedCard ->
                        updateCards { copy(selectedCardForManage = updatedCard) }
                    }
                }
        }
    }

    fun unfreezeCard(cardId: String) {
        viewModelScope.launch {
            repository.unfreezeCard(cardId)
                .onSuccess {
                    fetchUserCards()
                    _uiState.value.userCards.find { it.id == cardId }?.let { updatedCard ->
                        updateCards { copy(selectedCardForManage = updatedCard) }
                    }
                }
        }
    }

    fun activateCard(cardId: String) {
        viewModelScope.launch {
            repository.activateCard(cardId)
                .onSuccess {
                    fetchUserCards()
                    _uiState.value.userCards.find { it.id == cardId }?.let { updatedCard ->
                        updateCards { copy(selectedCardForManage = updatedCard) }
                    }
                }
        }
    }

    fun openChangePin() {
        updateCards { copy(isChangePinDialogOpen = true, newCardPin = "") }
    }

    fun closeChangePin() {
        updateCards { copy(isChangePinDialogOpen = false) }
    }

    fun setNewCardPin(pin: String) {
        updateCards { copy(newCardPin = pin.filter { it.isDigit() }.take(4)) }
    }

    fun submitChangePin(cardId: String) {
        val pin = _uiState.value.newCardPin
        if (pin.length != 4) return
        viewModelScope.launch {
            repository.changeCardPin(cardId, pin)
                .onSuccess {
                    closeChangePin()
                }
        }
    }

    fun terminateCard(cardId: String) {
        viewModelScope.launch {
            repository.terminateCard(cardId)
                .onSuccess {
                    fetchUserCards()
                    closeManageCard()
                }
        }
    }

    fun openFundingDialog() {
        updateCards { copy(isFundingDialogOpen = true,
                fundingType = "wallet",
                cpkSignature = "",
                cpkPin = "") }
    }

    fun closeFundingDialog() {
        updateCards { copy(isFundingDialogOpen = false) }
    }

    fun setFundingType(type: String) {
        updateCards { copy(fundingType = type) }
    }

    fun setCpkSignature(v: String) {
        updateCards { copy(cpkSignature = v) }
    }

    fun setCpkPin(v: String) {
        updateCards { copy(cpkPin = v.filter { it.isDigit() }.take(4)) }
    }

    fun submitFundingConfig(cardId: String) {
        val state = _uiState.value
        viewModelScope.launch {
            repository.updateCardFunding(
                id = cardId,
                type = state.fundingType,
                cpkSignature = if (state.fundingType == "cpk") state.cpkSignature else null,
                cpkPin = if (state.fundingType == "cpk") state.cpkPin else null
            ).onSuccess {
                closeFundingDialog()
            }
        }
    }

    fun openRevealDetails() {
        updateCards { copy(isRevealDetailsDialogOpen = true,
                revealPin = "",
                revealError = null,
                revealedCardDetails = null) }
    }

    fun closeRevealDetails() {
        updateCards { copy(isRevealDetailsDialogOpen = false,
                revealPin = "",
                revealedCardDetails = null) }
    }

    fun setRevealPin(pin: String) {
        updateCards { copy(revealPin = pin.filter { it.isDigit() }.take(4)) }
    }

    fun submitRevealDetails(cardId: String) {
        val pin = _uiState.value.revealPin
        if (pin.length != 4) return
        updateCards { copy(isRevealLoading = true, revealError = null) }
        viewModelScope.launch {
            repository.revealCardDetails(cardId, pin)
                .onSuccess { resp ->
                    updateCards { copy(isRevealLoading = false,
                            revealedCardDetails = resp,
                            revealError = null) }
                }
                .onFailure { err ->
                    updateCards { copy(isRevealLoading = false,
                            revealError = err.message ?: "Code PIN incorrect.") }
                }
        }
    }

    fun fetchNotifications() {
        updateMain { copy(isLoadingNotifications = true, notificationsError = null) }
        viewModelScope.launch {
            val res = repository.fetchNotifications(20)
            res.onSuccess {
                updateMain { copy(isLoadingNotifications = false) }
            }.onFailure { err ->
                updateMain { copy(isLoadingNotifications = false, notificationsError = err.message) }
            }
        }
    }

    fun openNotificationDialog() {
        updateMain { copy(isNotificationDialogOpen = true) }
        fetchNotifications()
    }

    fun closeNotificationDialog() {
        updateMain { copy(isNotificationDialogOpen = false) }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun loadTransactions(
        userId: Long? = null,
        direction: String? = null,
        from: String? = null,
        to: String? = null
    ) {
        val effectiveUserId = getEffectiveUserId(userId)
        updateMain { copy(isLoadingTransactions = true, transactionsError = null) }
        viewModelScope.launch {
            val result = repository.fetchTransactionsApi(
                userId = effectiveUserId,
                limit = 50,
                offset = 0,
                from = from,
                to = to,
                direction = direction
            )
            result.onSuccess {
                updateMain { copy(isLoadingTransactions = false) }
            }.onFailure { err ->
                updateMain { copy(isLoadingTransactions = false, transactionsError = err.message) }
            }
        }
    }

    fun setTxDirectionFilter(dir: String, userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        updateMain { copy(txDirectionFilter = dir) }
        val dirParam = when (dir) {
            "INCOMING" -> "incoming"
            "OUTGOING" -> "outgoing"
            else -> null
        }
        loadTransactions(effectiveUserId, direction = dirParam)
    }

    fun setTxDateFilter(filter: String, userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        updateMain { copy(txDateFilter = filter) }
        val dirParam = when (_uiState.value.txDirectionFilter) {
            "INCOMING" -> "incoming"
            "OUTGOING" -> "outgoing"
            else -> null
        }
        val (from, to) = calculateFromTo(filter, _uiState.value.customFromDate, _uiState.value.customToDate)
        loadTransactions(effectiveUserId, direction = dirParam, from = from, to = to)
    }

    fun setCustomDates(from: String, to: String, userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        updateMain { copy(customFromDate = from, customToDate = to, txDateFilter = "CUSTOM") }
        val dirParam = when (_uiState.value.txDirectionFilter) {
            "INCOMING" -> "incoming"
            "OUTGOING" -> "outgoing"
            else -> null
        }
        loadTransactions(effectiveUserId, direction = dirParam, from = from, to = to)
    }

    fun onSearchQueryChanged(q: String) {
        updateMain { copy(txSearchQuery = q) }
    }

    fun openAllTransactionsPage() {
        updateMain { copy(isAllTransactionsOpen = true) }
    }

    fun closeAllTransactionsPage() {
        updateMain { copy(isAllTransactionsOpen = false) }
    }

    fun selectTransactionForDetail(tx: TransactionEntity?) {
        updateMain { copy(selectedTransactionForDetail = tx) }
    }

    private fun calculateFromTo(filter: String, customFrom: String, customTo: String): Pair<String?, String?> {
        val today = "2026-09-26"
        return when (filter) {
            "TODAY" -> Pair(today, today)
            "WEEK" -> Pair("2026-09-19", today)
            "MONTH" -> Pair("2026-09-01", today)
            "CUSTOM" -> Pair(customFrom.ifBlank { null }, customTo.ifBlank { null })
            else -> Pair(null, null)
        }
    }

    fun fetchWallet(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        updateMain { copy(isLoadingWallet = true, walletError = null) }
        fetchUsdtBalance()
        fetchUsdtAddress()
        viewModelScope.launch {
            val result = repository.getWallet(effectiveUserId)
            result.onSuccess { resp ->
                updateMain { copy(isLoadingWallet = false, walletResponse = resp) }
            }.onFailure { err ->
                updateMain { copy(isLoadingWallet = false, walletError = err.message) }
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        updateMain { copy(selectedTab = tab) }
    }

    fun openEditProfileDialog(profile: UserProfileEntity?) {
        updateMain { copy(isEditProfileOpen = true,
                editFullName = profile?.fullName ?: "",
                editPhone = profile?.phone ?: "",
                editCity = profile?.city ?: "",
                editProfession = profile?.profession ?: "",
                editAddress = profile?.address ?: "",
                editPinConfirmation = "",
                editErrorMessage = null,
                editSuccessMessage = null) }
    }

    fun closeEditProfileDialog() {
        updateMain { copy(isEditProfileOpen = false, editErrorMessage = null) }
    }

    fun onEditPhoneChanged(value:String) = updateMain { copy(editPhone=value) }
    fun openWalletPin() = updateMain { copy(isWalletPinOpen=true) }
    fun closeWalletPin() = updateMain { copy(isWalletPinOpen=false) }
    suspend fun changeWalletPin(old:String,new:String,confirmation:String)=repository.changeWalletPin(old,new,confirmation)
    fun onEditFullNameChanged(v: String) = updateMain { copy(editFullName = v) }
    fun onEditCityChanged(v: String) = updateMain { copy(editCity = v) }
    fun onEditProfessionChanged(v: String) = updateMain { copy(editProfession = v) }
    fun onEditAddressChanged(v: String) = updateMain { copy(editAddress = v) }
    fun onEditPinChanged(v: String) = updateMain { copy(editPinConfirmation = v.filter { it.isDigit() }.take(4)) }

    fun submitProfileUpdate(phone: String) {
        val state = _uiState.value
        if (state.editPinConfirmation.length != 4) {
            updateMain { copy(editErrorMessage = "Code PIN à 4 chiffres requis pour valider.") }
            return
        }

        val updates = mutableMapOf<String, String>()
        if (state.editFullName.isNotBlank()) updates["full_name"] = state.editFullName
        if (state.editPhone.isNotBlank()) updates["phone"] = state.editPhone
        if (state.editCity.isNotBlank()) updates["city"] = state.editCity
        if (state.editProfession.isNotBlank()) updates["profession"] = state.editProfession
        if (state.editAddress.isNotBlank()) updates["address"] = state.editAddress

        if (updates.isEmpty()) {
            updateMain { copy(editErrorMessage = "Aucune donnée à modifier.") }
            return
        }

        updateMain { copy(isEditingLoading = true, editErrorMessage = null) }

        viewModelScope.launch {
            val result = repository.updateProfile(phone, state.editPinConfirmation, updates)
            result.onSuccess {
                updateMain { copy(isEditingLoading = false,
                        isEditProfileOpen = false,
                        editSuccessMessage = "Profil CashPay mis à jour avec succès !") }
            }.onFailure { err ->
                updateMain { copy(isEditingLoading = false,
                        editErrorMessage = err.message ?: "Code PIN incorrect.") }
            }
        }
    }

    fun toggleBiometrics(enabled: Boolean) {
        viewModelScope.launch {
            repository.setBiometric(enabled)
        }
    }

    fun openTransferDialog(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val fiatBalances = _uiState.value.walletResponse?.balances?.fiat ?: emptyMap()
        val defaultCurrency = when {
            (fiatBalances["CDF"] ?: 0.0) > 0.0 -> "CDF"
            (fiatBalances["USD"] ?: 0.0) > 0.0 -> "USD"
            (fiatBalances["EUR"] ?: 0.0) > 0.0 -> "EUR"
            else -> _uiState.value.transferCurrency
        }
        updateMain { copy(isQuickTransferOpen = true,
                transferRecipient = "",
                transferCurrency = defaultCurrency,
                transferAmount = "",
                transferFee = null,
                transferTotalDebit = null,
                transferError = null,
                transferStep = 1,
                transferSuccess = false,
                isSearchingTransferRecipient = false) }
        loadTransfersMeta(effectiveUserId)
    }

    fun openBtcTransferDialog() {
        updateMain {
            copy(
                isSendBtcDialogOpen = true,
                isBtcTransferLoading = false,
                btcTransferError = null,
                btcTransferSuccess = false,
                btcTransferTxRef = null
            )
        }
        fetchBtcLiveRate()
    }

    fun closeBtcTransferDialog() {
        updateMain {
            copy(
                isSendBtcDialogOpen = false,
                btcTransferError = null,
                btcTransferSuccess = false
            )
        }
    }

    fun fetchBtcLiveRate() {
        updateMain { copy(isBtcRateLoading = true) }
        viewModelScope.launch {
            val result = repository.getBtcRateUsd()
            result.onSuccess { rate ->
                updateMain { copy(isBtcRateLoading = false, btcLiveRateUsd = rate) }
                updateExchange {
                    val updated = exchangeRatesMap.toMutableMap()
                    updated["BTC_USD"] = rate
                    if (rate > 0.0) {
                        updated["USD_BTC"] = 1.0 / rate
                    }
                    copy(exchangeRatesMap = updated)
                }
            }.onFailure {
                updateMain { copy(isBtcRateLoading = false) }
            }
        }
    }

    fun sendBtc(recipientAddress: String, amountBtc: Double, pin: String) {
        val effectiveUserId = getEffectiveUserId(null)
        val cleanAddr = recipientAddress.trim().removePrefix("bitcoin:").substringBefore("?")
        if (cleanAddr.isBlank()) {
            updateMain { copy(btcTransferError = "Veuillez entrer une adresse Bitcoin de destination valide.") }
            return
        }
        if (amountBtc <= 0.0) {
            updateMain { copy(btcTransferError = "Le montant en BTC doit être supérieur à zéro.") }
            return
        }
        val cleanPin = pin.trim()
        if (cleanPin.length != 4 || !cleanPin.all { it.isDigit() }) {
            updateMain { copy(btcTransferError = "Veuillez saisir votre code PIN CashPay (4 chiffres).") }
            return
        }

        updateMain { copy(isBtcTransferLoading = true, btcTransferError = null) }
        viewModelScope.launch {
            val result = repository.transferBtc(
                recipientAddress = cleanAddr,
                amountBtc = amountBtc,
                pin = cleanPin
            )
            result.onSuccess { resp ->
                val txRef = resp.referenceCode ?: resp.data?.referenceCode ?: resp.data?.txHash ?: "BTC-${System.currentTimeMillis()}"
                updateMain {
                    copy(
                        isBtcTransferLoading = false,
                        btcTransferSuccess = true,
                        btcTransferTxRef = txRef,
                        btcTransferError = null
                    )
                }
                fetchWallet(effectiveUserId)
                loadTransactions(effectiveUserId)
            }.onFailure { err ->
                val msg = err.message ?: "Erreur lors du transfert Bitcoin."
                updateMain {
                    copy(
                        isBtcTransferLoading = false,
                        btcTransferError = msg
                    )
                }
            }
        }
    }

    fun closeTransferDialog() {
        resetTransferDialog()
    }

    fun openReceiveBtcDialog() {
        updateMain { copy(isReceiveBtcDialogOpen = true) }
    }

    fun closeReceiveBtcDialog() {
        updateMain { copy(isReceiveBtcDialogOpen = false) }
    }

    fun openBuyBtcDialog() {
        val natCode = _uiState.value.walletResponse?.nationalCurrency?.code ?: "USD"
        val fromCur = if (natCode != "BTC") natCode else "USD"
        updateExchange { copy(
            isExchangeDialogOpen = true,
            exchangeFromCurrency = fromCur,
            exchangeToCurrency = "BTC",
            exchangeAmount = "",
            exchangePin = "",
            exchangeError = null,
            exchangeSuccessResponse = null
        ) }
        fetchExchangeRates()
        fetchRealtimeQuote(1.0)
    }

    fun openSellBtcDialog() {
        val natCode = _uiState.value.walletResponse?.nationalCurrency?.code ?: "USD"
        val toCur = if (natCode != "BTC") natCode else "USD"
        updateExchange { copy(
            isExchangeDialogOpen = true,
            exchangeFromCurrency = "BTC",
            exchangeToCurrency = toCur,
            exchangeAmount = "",
            exchangePin = "",
            exchangeError = null,
            exchangeSuccessResponse = null
        ) }
        fetchExchangeRates()
        fetchRealtimeQuote(1.0)
    }

    fun openBuyUsdtDialog() {
        val natCode = _uiState.value.walletResponse?.nationalCurrency?.code ?: "USD"
        val fromCur = if (natCode != "USDT") natCode else "USD"
        updateExchange { copy(
            isExchangeDialogOpen = true,
            exchangeFromCurrency = fromCur,
            exchangeToCurrency = "USDT",
            exchangeAmount = "",
            exchangePin = "",
            exchangeError = null,
            exchangeSuccessResponse = null
        ) }
        fetchExchangeRates()
        fetchRealtimeQuote(1.0)
    }

    fun openSellUsdtDialog() {
        val natCode = _uiState.value.walletResponse?.nationalCurrency?.code ?: "USD"
        val toCur = if (natCode != "USDT") natCode else "USD"
        updateExchange { copy(
            isExchangeDialogOpen = true,
            exchangeFromCurrency = "USDT",
            exchangeToCurrency = toCur,
            exchangeAmount = "",
            exchangePin = "",
            exchangeError = null,
            exchangeSuccessResponse = null
        ) }
        fetchExchangeRates()
        fetchRealtimeQuote(1.0)
    }

    fun fetchUsdtAddress(forceRefresh: Boolean = false) {
        if (!forceRefresh && !_uiState.value.usdtAddress.isNullOrBlank()) return
        viewModelScope.launch {
            repository.getUsdtAddress().onSuccess { resp ->
                if (resp.address != null) {
                    updateMain { copy(usdtAddress = resp.address, usdtError = null) }
                }
            }.onFailure { err ->
                if (_uiState.value.usdtAddress.isNullOrBlank()) {
                    updateMain { copy(usdtError = err.message) }
                }
            }
        }
    }

    fun fetchUsdtBalance() {
        viewModelScope.launch {
            updateMain { copy(isUsdtLoading = true) }
            repository.getUsdtBalance().onSuccess { resp ->
                updateMain {
                    copy(
                        isUsdtLoading = false,
                        usdtBalance = resp.balance,
                        usdtOnChainBalance = resp.onChainBalance,
                        usdtAddress = resp.address ?: usdtAddress,
                        usdtError = null
                    )
                }
            }.onFailure { err ->
                updateMain {
                    copy(
                        isUsdtLoading = false,
                        usdtError = err.message
                    )
                }
            }
        }
    }

    fun fetchUsdtDeposits() {
        viewModelScope.launch {
            updateMain { copy(isUsdtDepositsLoading = true) }
            repository.getUsdtDeposits().onSuccess { resp ->
                updateMain {
                    copy(
                        isUsdtDepositsLoading = false,
                        usdtDeposits = resp.deposits
                    )
                }
            }.onFailure { _ ->
                updateMain { copy(isUsdtDepositsLoading = false) }
            }
        }
    }

    fun openReceiveUsdtDialog() {
        fetchUsdtAddress(forceRefresh = true)
        updateMain { copy(isReceiveUsdtDialogOpen = true) }
    }

    fun closeReceiveUsdtDialog() {
        updateMain { copy(isReceiveUsdtDialogOpen = false) }
    }

    fun openSendUsdtDialog() {
        fetchUsdtBalance()
        updateMain {
            copy(
                isSendUsdtDialogOpen = true,
                usdtTransferError = null,
                usdtTransferSuccess = false,
                usdtTransferPending = false,
                usdtTransferTxRef = null
            )
        }
    }

    fun closeSendUsdtDialog() {
        updateMain {
            copy(
                isSendUsdtDialogOpen = false,
                usdtTransferError = null,
                usdtTransferSuccess = false,
                usdtTransferPending = false,
                usdtTransferTxRef = null
            )
        }
    }

    fun openUsdtDepositsDialog() {
        fetchUsdtDeposits()
        updateMain { copy(isUsdtDepositsDialogOpen = true) }
    }

    fun closeUsdtDepositsDialog() {
        updateMain { copy(isUsdtDepositsDialogOpen = false) }
    }

    fun sendUsdt(recipientAddress: String, amountUsdt: String, pin: String) {
        val effectiveUserId = getEffectiveUserId()
        val trimmedAddress = recipientAddress.trim()
        val trimmedAmount = amountUsdt.trim()
        val trimmedPin = pin.trim()

        if (trimmedAddress.isBlank() || (!trimmedAddress.startsWith("T") && trimmedAddress.length < 20)) {
            updateMain { copy(usdtTransferError = "Veuillez entrer une adresse TRON (TRC-20) valide (commençant par T).") }
            return
        }
        val parsed = trimmedAmount.toDoubleOrNull()
        if (parsed == null || parsed <= 0.0) {
            updateMain { copy(usdtTransferError = "Le montant USDT doit être supérieur à zéro.") }
            return
        }
        if (trimmedPin.length < 4) {
            updateMain { copy(usdtTransferError = "Veuillez saisir votre code PIN CashPay (4 chiffres).") }
            return
        }

        updateMain { copy(isUsdtTransferLoading = true, usdtTransferError = null) }
        viewModelScope.launch {
            repository.transferUsdt(
                recipientAddress = trimmedAddress,
                amountUsdt = trimmedAmount,
                pin = trimmedPin
            ).onSuccess { resp ->
                val isPending = resp.status?.equals("pending", ignoreCase = true) == true
                val txRef = resp.txHash ?: resp.transferId ?: "USDT-${System.currentTimeMillis()}"
                updateMain {
                    copy(
                        isUsdtTransferLoading = false,
                        usdtTransferSuccess = !isPending,
                        usdtTransferPending = isPending,
                        usdtTransferTxRef = txRef,
                        usdtTransferError = null
                    )
                }
                fetchUsdtBalance()
                fetchWallet(effectiveUserId)
                loadTransactions(effectiveUserId)
            }.onFailure { err ->
                val msg = err.message ?: "Erreur lors du transfert USDT."
                updateMain {
                    copy(
                        isUsdtTransferLoading = false,
                        usdtTransferError = msg
                    )
                }
            }
        }
    }

    fun onRecipientChanged(v: String) = updateMain { copy(transferRecipient = v, transferError = null) }
    fun onAmountChanged(v: String) = updateMain { copy(transferAmount = v, transferError = null) }
    fun onCurrencyChanged(c: String) = updateMain { copy(transferCurrency = c, transferError = null) }

    fun searchTransferRecipient() {
        val raw = _uiState.value.transferRecipient.trim()
        if (raw.isBlank()) {
            updateMain { copy(transferError = "Veuillez entrer un ID Wallet ou un numéro de téléphone.") }
            return
        }
        updateMain { copy(isSearchingTransferRecipient = true, transferError = null) }
        viewModelScope.launch {
            resolveProfile(raw).onSuccess { pubResp ->
                val profile = pubResp.profile
                if (profile != null) {
                    _uiState.update {
 it.copy(
main = it.main.copy(
isSearchingTransferRecipient = false,
                            transferRecipient = profile.walletId,
                            transferError = null
),
contacts = it.contacts.copy(
                            prefilledRecipient = profile,
                            prefilledContactName = profile.fullName,
)
)
}
                } else {
                    updateMain { copy(isSearchingTransferRecipient = false,
                            transferError = "Compte introuvable pour cette recherche.") }
                }
            }.onFailure { err ->
                updateMain { copy(isSearchingTransferRecipient = false,
                        transferError = err.message ?: "Compte introuvable.") }
            }
        }
    }

    fun loadTransfersMeta(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        viewModelScope.launch {
            val result = repository.getTransfersMeta(effectiveUserId)
            result.onSuccess { meta ->
                val currentCurr = _uiState.value.transferCurrency
                val foundCurr = meta.currencies.firstOrNull { it.code.equals(currentCurr, ignoreCase = true) }
                val newCurrency = if (foundCurr != null && foundCurr.balance > 0.0) {
                    foundCurr.code
                } else {
                    meta.currencies.firstOrNull { it.balance > 0.0 }?.code ?: currentCurr
                }
                updateMain { copy(transfersMeta = meta, transferCurrency = newCurrency) }
            }
        }
    }

    fun previewTransfer(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val amount = state.transferAmount.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) {
            updateMain { copy(transferError = "Le montant doit être supérieur à zéro.") }
            return
        }
        if (state.transferRecipient.isBlank()) {
            updateMain { copy(transferError = "Le destinataire est requis (N° de téléphone ou Wallet ID).") }
            return
        }

        updateMain { copy(isTransferLoading = true, transferError = null) }
        viewModelScope.launch {
            val isBtc = state.transferCurrency.uppercase() == "BTC"
            var targetRecipient = state.transferRecipient.trim()

            // If not BTC, resolve profile comprehensively by Wallet ID or phone
            if (!isBtc) {
                val resolved = resolveProfile(targetRecipient).getOrNull()
                val profile = resolved?.profile
                if (profile != null) {
                    targetRecipient = profile.walletId
                    updateContacts { copy(prefilledRecipient = profile,
                            prefilledContactName = profile.fullName) }
                }
            }

            val req = TransferRequest(
                userId = effectiveUserId,
                receiverWalletId = if (!isBtc) targetRecipient else null,
                recipientAddress = if (isBtc) state.transferRecipient else null,
                amount = amount,
                currency = state.transferCurrency,
                preview = true
            )
            val result = repository.transfer(req)
            result.onSuccess { resp ->
                updateMain { copy(isTransferLoading = false,
                        transferFee = resp.fee ?: 1.0,
                        transferTotalDebit = resp.totalDebit ?: (amount + (resp.fee ?: 1.0)),
                        transferStep = 2, // Preview & Fee
                        transferError = null) }
            }.onFailure { err ->
                updateMain { copy(isTransferLoading = false,
                        transferError = err.message ?: "Erreur de prévisualisation") }
            }
        }
    }

    fun confirmTransfer(userId: Long? = null, pin: String) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val amount = state.transferAmount.toDoubleOrNull() ?: 0.0
        val targetWalletId = state.prefilledRecipient?.walletId ?: state.transferRecipient.trim()
        updateMain { copy(isTransferLoading = true, transferError = null) }

        viewModelScope.launch {
            val isBtc = state.transferCurrency.uppercase() == "BTC"
            val req = TransferRequest(
                userId = effectiveUserId,
                receiverWalletId = if (!isBtc) targetWalletId else null,
                recipientAddress = if (isBtc) state.transferRecipient else null,
                amount = amount,
                currency = state.transferCurrency,
                pin = pin,
                preview = false
            )
            val result = repository.transfer(req)
            result.onSuccess { resp ->
                updateMain { copy(isTransferLoading = false,
                        transferStep = 4, // Success
                        transferSuccess = true,
                        transferError = null) }
                // Add to recent correspondents with full profile photo
                state.prefilledRecipient?.let { prof ->
                    addRecentCorrespondent(
                        com.example.data.model.PhoneContact(
                            name = prof.fullName,
                            phone = "",
                            normalizedPhone = "",
                            lastNineDigits = "",
                            isCashPayUser = true,
                            publicProfile = prof
                        )
                    )
                }
                fetchWallet(effectiveUserId)
                loadTransactions(effectiveUserId)
            }.onFailure { err ->
                updateMain { copy(isTransferLoading = false,
                        transferError = err.message ?: "Code PIN incorrect ou solde insuffisant.") }
            }
        }
    }

    fun resetTransferDialog() {
        updateMain { copy(isQuickTransferOpen = false,
                transferRecipient = "",
                transferAmount = "",
                transferFee = null,
                transferTotalDebit = null,
                transferError = null,
                transferStep = 1,
                transferSuccess = false) }
    }

    fun clearTransferError() = updateMain { copy(transferError = null) }

    // --- CONTACTS & PUBLIC PROFILE ACTIONS ---
    fun openContactsDialog() {
        updateContacts { copy(isContactsDialogOpen = true, searchContactQuery = "") }
    }

    fun closeContactsDialog() {
        updateContacts { copy(isContactsDialogOpen = false) }
    }

    fun setContactSearchQuery(query: String) {
        updateContacts { copy(searchContactQuery = query) }
    }

    fun openPublicProfile(contactName: String, profile: com.example.data.model.PublicProfileDto) {
        updateContacts { copy(selectedPublicContactName = contactName,
                selectedPublicProfile = profile,
                isPublicProfileOpen = true) }
    }

    fun closePublicProfile() {
        updateContacts { copy(isPublicProfileOpen = false,
                selectedPublicProfile = null,
                selectedPublicContactName = null) }
    }

    fun triggerPrefilledTransfer(contactName: String, profile: com.example.data.model.PublicProfileDto) {
        // Automatically check if Bitcoin public address is present, pre-fill recipient
        val initialRecipient = if (profile.bitcoin?.available == true && !profile.bitcoin.address.isNullOrBlank()) {
            profile.bitcoin.address
        } else {
            profile.walletId
        }

        _uiState.update {
 it.copy(
contacts = it.contacts.copy(
prefilledRecipient = profile,
                prefilledContactName = contactName,
),
main = it.main.copy(
                transferRecipient = initialRecipient,
                transferAmount = "",
                transferFee = null,
                transferTotalDebit = null,
                transferError = null,
                transferStep = 1,
                transferSuccess = false,
                isQuickTransferOpen = true
)
)
}
        // Also add to recent correspondents list
        addRecentCorrespondent(com.example.data.model.PhoneContact(
            name = contactName,
            phone = "",
            normalizedPhone = "",
            lastNineDigits = "",
            isCashPayUser = true,
            publicProfile = profile
        ))
    }

    fun clearPrefilledTransfer() {
        updateContacts { copy(prefilledRecipient = null,
                prefilledContactName = null) }
    }

    fun addRecentCorrespondent(contact: com.example.data.model.PhoneContact) {
        val current = _uiState.value.recentCorrespondents.toMutableList()
        // Remove duplicate if already exists
        current.removeAll { 
            (it.publicProfile?.walletId != null && it.publicProfile?.walletId == contact.publicProfile?.walletId) ||
            it.name.equals(contact.name, ignoreCase = true) 
        }
        current.add(0, contact)
        updateContacts { copy(recentCorrespondents = current.take(5)) }
    }

    fun openAddContactDialog() {
        updateContacts { copy(isAddContactDialogOpen = true,
                addContactPhone = "",
                isSearchingAddContact = false,
                addContactFoundProfile = null,
                addContactNotFound = false,
                addContactError = null) }
    }

    fun closeAddContactDialog() {
        updateContacts { copy(isAddContactDialogOpen = false) }
    }

    fun setAddContactPhone(phone: String) {
        updateContacts { copy(addContactPhone = phone, addContactNotFound = false, addContactError = null) }
    }

    fun searchAddContact() {
        val raw = _uiState.value.addContactPhone.trim()
        if (raw.length < 3) {
            updateContacts { copy(addContactError = "Veuillez entrer un numéro de téléphone ou un Wallet ID valide.") }
            return
        }
        updateContacts { copy(isSearchingAddContact = true,
                addContactError = null,
                addContactFoundProfile = null,
                addContactNotFound = false) }
        viewModelScope.launch {
            val isPhone = raw.all { it.isDigit() || it == '+' || it == ' ' } && raw.filter { it.isDigit() }.length >= 8
            val result = if (isPhone) {
                repository.searchProfileByPhone(raw)
            } else {
                repository.searchProfileByWallet(raw)
            }
            result.onSuccess { resp ->
                updateContacts { copy(isSearchingAddContact = false,
                        addContactFoundProfile = if (resp.success && resp.found) resp.profile else null,
                        addContactNotFound = !(resp.success && resp.found && resp.profile != null)) }
            }.onFailure { err ->
                updateContacts { copy(isSearchingAddContact = false,
                        addContactError = err.message ?: "Compte introuvable",
                        addContactNotFound = true) }
            }
        }
    }

    fun confirmAddContact(profile: com.example.data.model.PublicProfileDto) {
        val contact = com.example.data.model.PhoneContact(
            name = profile.fullName,
            phone = _uiState.value.addContactPhone,
            normalizedPhone = _uiState.value.addContactPhone,
            lastNineDigits = if (_uiState.value.addContactPhone.length >= 9) _uiState.value.addContactPhone.takeLast(9) else _uiState.value.addContactPhone,
            isCashPayUser = true,
            publicProfile = profile
        )
        addRecentCorrespondent(contact)
        closeAddContactDialog()
    }

    fun openContactsPage() {
        updateContacts { copy(isContactsPageOpen = true, searchContactQuery = "") }
    }

    fun closeContactsPage() {
        updateContacts { copy(isContactsPageOpen = false) }
    }

    // Seeding some default recent correspondents on startup
    fun seedRecentCorrespondents() {
        // Supprimé complètement les contacts fictifs pour n'afficher que les réels recherchés.
    }

    // Lookup on RAW phone contacts (Gmail, Phone, SIM)
    private var contactsSyncJob: kotlinx.coroutines.Job? = null

    fun syncContacts(rawContacts: List<Pair<String, String>>) {
        contactsSyncJob?.cancel()
        val allContacts = rawContacts.mapNotNull { (name, rawPhone) ->
            val phone = rawPhone.trim()
            val digits = phone.filter { it.isDigit() }
            if (digits.length !in 7..15) null else com.example.data.model.PhoneContact(
                name = name.ifBlank { phone }, phone = phone,
                normalizedPhone = phone.replace(Regex("[^0-9+]"), ""), lastNineDigits = digits.takeLast(9))
        }.distinctBy { it.normalizedPhone }.sortedBy { it.name.lowercase() }
        updateContacts { copy(contactsList = allContacts, isLoadingContacts = allContacts.isNotEmpty(), contactsError = null) }
        if (allContacts.isEmpty()) return
        contactsSyncJob = viewModelScope.launch {
            try {
                val resolved = mutableMapOf<String, com.example.data.model.PublicProfileDto>()
                for (batch in allContacts.chunked(250)) {
                    val response = repository.resolveContacts(batch.map { it.phone }).getOrThrow()
                    for (contact in response) if (contact.found && contact.profile != null) resolved[contact.phone] = contact.profile
                    updateContacts { copy(contactsList = allContacts.map { contact ->
                        val profile = resolved[contact.phone]
                        contact.copy(isCashPayUser = profile != null, publicProfile = profile)
                    }.distinctBy { it.publicProfile?.walletId ?: it.normalizedPhone }.sortedWith(compareByDescending<com.example.data.model.PhoneContact> { it.isCashPayUser }.thenBy { it.name.lowercase() })) }
                }
            } catch (e: kotlinx.coroutines.CancellationException) { throw e
            } catch (e: Exception) {
                updateContacts { copy(contactsError = "Détection incomplète. Vérifiez votre connexion puis actualisez.") }
            } finally {
                if (kotlinx.coroutines.currentCoroutineContext()[kotlinx.coroutines.Job]?.isActive == true) updateContacts { copy(isLoadingContacts = false) }
            }
        }
    }

    // --- WITHDRAWALS ACTIONS ---
    fun openWithdrawalDialog() {
        updateWithdrawal { copy(isWithdrawalDialogOpen = true,
                withdrawalType = "agent_cash",
                withdrawalOperator = null,
                withdrawalRecipient = "",
                withdrawalAmount = "",
                withdrawalCurrency = "USD",
                withdrawalPreview = null,
                isWithdrawalPreviewLoading = false,
                withdrawalPreviewError = null,
                isWithdrawalConfirmLoading = false,
                withdrawalConfirmError = null,
                withdrawalSuccess = false,
                withdrawalStep = 1,
                searchedAgentProfile = null,
                isSearchingAgent = false,
                agentSearchError = null) }
    }

    fun openWithdrawalExpress() {
        val userCountry = userProfile.value?.country ?: "CD"
        val matchedCountry = com.example.data.model.ExpressMobileMoneyData.getCountry(userCountry)
        val defaultCountryCode = matchedCountry.code
        val defaultCountryName = matchedCountry.name
        val defaultDialCode = matchedCountry.dialCode
        val defaultOperator = matchedCountry.operators.firstOrNull()?.code ?: "MPESA"
        val defaultCurrency = if (defaultCountryCode == "CD") "USD" else "USD"

        updateWithdrawal { copy(
            isWithdrawalDialogOpen = true,
            withdrawalType = "mobile_money",
            withdrawalCountryCode = defaultCountryCode,
            withdrawalCountryName = defaultCountryName,
            withdrawalCountryDialCode = defaultDialCode,
            withdrawalOperator = defaultOperator,
            withdrawalRecipient = defaultDialCode,
            withdrawalAmount = "",
            withdrawalCurrency = defaultCurrency,
            withdrawalPreview = null,
            isWithdrawalPreviewLoading = false,
            withdrawalPreviewError = null,
            isWithdrawalConfirmLoading = false,
            withdrawalConfirmError = null,
            withdrawalSuccess = false,
            withdrawalStep = 1,
            searchedAgentProfile = null,
            isSearchingAgent = false,
            agentSearchError = null
        ) }
    }

    fun setWithdrawalCountry(countryCode: String, countryName: String, dialCode: String) {
        val matched = com.example.data.model.ExpressMobileMoneyData.getCountry(countryCode)
        val defaultOperator = matched.operators.firstOrNull()?.code ?: "MPESA"
        val currentCurrency = _uiState.value.withdrawalCurrency
        // If not DRC and currently CDF, switch to USD as requested by user
        val newCurrency = if (countryCode != "CD" && currentCurrency == "CDF") "USD" else currentCurrency

        updateWithdrawal { copy(
            withdrawalCountryCode = countryCode,
            withdrawalCountryName = countryName,
            withdrawalCountryDialCode = dialCode,
            withdrawalOperator = defaultOperator,
            withdrawalCurrency = newCurrency,
            withdrawalRecipient = dialCode,
            withdrawalPreview = null,
            withdrawalPreviewError = null
        ) }
    }

    fun closeWithdrawalDialog() {
        updateWithdrawal { copy(isWithdrawalDialogOpen = false) }
    }

    fun setWithdrawalType(type: String) {
        updateWithdrawal { copy(withdrawalType = type,
                withdrawalOperator = if (type == "mobile_money") "MPESA" else null,
                withdrawalRecipient = "",
                withdrawalStep = 1,
                searchedAgentProfile = null,
                agentSearchError = null) }
    }

    fun setWithdrawalOperator(op: String?) {
        updateWithdrawal { copy(withdrawalOperator = op) }
    }

    fun setWithdrawalRecipient(recipient: String) {
        updateWithdrawal { copy(withdrawalRecipient = recipient, agentSearchError = null) }
    }

    fun setWithdrawalAmount(amount: String) {
        updateWithdrawal { copy(withdrawalAmount = amount, withdrawalPreviewError = null) }
    }

    fun setWithdrawalCurrency(currency: String) {
        updateWithdrawal { copy(withdrawalCurrency = currency, withdrawalPreviewError = null) }
    }

    suspend fun resolveProfile(identifier: String): Result<com.example.data.model.PublicProfileResponse> {
        val trimmed = identifier.trim()
        if (trimmed.isEmpty()) return Result.failure(Exception("Identifiant vide"))

        // 1. Direct Wallet / Client Search in Repository
        val walletRes = repository.searchProfileByWallet(trimmed)
        if (walletRes.isSuccess && walletRes.getOrNull()?.found == true) {
            return walletRes
        }

        // 2. Direct Phone Search if numerical digits present
        val digits = trimmed.filter { it.isDigit() }
        if (digits.length >= 8) {
            val phoneRes = repository.searchProfileByPhone(trimmed)
            if (phoneRes.isSuccess && phoneRes.getOrNull()?.found == true) {
                return phoneRes
            }
        }

        // 3. Client withdraw start action (resolves client account by wallet ID or phone)
        val clientReq = com.example.data.model.WithdrawActionRequest(
            operation = "client_withdraw",
            action = "start",
            identifier = trimmed,
            clientWalletId = trimmed,
            phone = if (digits.length >= 8) trimmed else null
        )
        try {
            val cRes = repository.withdrawAction(clientReq)
            if (cRes.isSuccess && cRes.getOrNull()?.target != null) {
                val target = cRes.getOrNull()!!.target!!
                return Result.success(com.example.data.model.PublicProfileResponse(
                    success = true,
                    found = true,
                    profile = com.example.data.model.PublicProfileDto(
                        walletId = target.walletId ?: trimmed,
                        fullName = target.fullName ?: target.firstName ?: trimmed,
                        role = target.role,
                        profilePhotoUrl = target.avatar,
                        profilePhoto = target.avatar
                    )
                ))
            }
        } catch (_: Exception) {}

        // 4. Agent withdraw start action (resolves agent account)
        val agentReq = com.example.data.model.WithdrawActionRequest(
            operation = "agent_withdraw",
            action = "start",
            identifier = trimmed,
            agentWalletId = trimmed,
            phone = if (digits.length >= 8) trimmed else null
        )
        try {
            val aRes = repository.withdrawAction(agentReq)
            if (aRes.isSuccess && aRes.getOrNull()?.target != null) {
                val target = aRes.getOrNull()!!.target!!
                return Result.success(com.example.data.model.PublicProfileResponse(
                    success = true,
                    found = true,
                    profile = com.example.data.model.PublicProfileDto(
                        walletId = target.walletId ?: trimmed,
                        fullName = target.fullName ?: target.firstName ?: trimmed,
                        role = target.role,
                        profilePhotoUrl = target.avatar,
                        profilePhoto = target.avatar
                    )
                ))
            }
        } catch (_: Exception) {}

        // 5. Loan Target lookup
        try {
            val loanRes = repository.getAgentLoanTarget(trimmed)
            if (loanRes.isSuccess && loanRes.getOrNull()?.client != null) {
                val c = loanRes.getOrNull()!!.client!!
                return Result.success(com.example.data.model.PublicProfileResponse(
                    success = true,
                    found = true,
                    profile = com.example.data.model.PublicProfileDto(
                        walletId = c.walletId ?: trimmed,
                        fullName = c.name ?: trimmed,
                        role = "client",
                        profilePhotoUrl = null,
                        profilePhoto = null
                    )
                ))
            }
        } catch (_: Exception) {}

        return Result.failure(Exception("Portefeuille ou utilisateur introuvable."))
    }

    fun searchAgentAndProceed() {
        val state = _uiState.value
        val identifier = state.withdrawalRecipient.trim()
        if (identifier.isBlank()) {
            updateWithdrawal { copy(agentSearchError = "Veuillez saisir un identifiant de portefeuille ou numéro.") }
            return
        }

        if (state.withdrawalType == "mobile_money") {
            updateWithdrawal { copy(withdrawalStep = 2) }
            return
        }

        updateWithdrawal { copy(isSearchingAgent = true, agentSearchError = null, searchedAgentProfile = null) }
        viewModelScope.launch {
            resolveProfile(identifier)
                .onSuccess { resp ->
                    updateWithdrawal { copy(isSearchingAgent = false) }
                    if (resp.success && resp.found && resp.profile != null) {
                        val profile = resp.profile
                        val role = profile.role?.lowercase() ?: "client"

                        if (role == "agent") {
                            updateWithdrawal { copy(searchedAgentProfile = profile,
                                    withdrawalStep = 2) }
                        } else {
                            updateWithdrawal { copy(agentSearchError = "Ce portefeuille n’est pas un compte Agent CashPay autorisé.",
                                    searchedAgentProfile = profile) }
                        }
                    } else {
                        updateWithdrawal { copy(agentSearchError = "Agent introuvable. Vérifiez l'ID Wallet ou le numéro.") }
                    }
                }
                .onFailure { err ->
                    updateWithdrawal { copy(isSearchingAgent = false,
                            agentSearchError = err.message ?: "Impossible de trouver l'agent.") }
                }
        }
    }

    fun proceedToAmount() {
        updateWithdrawal { copy(withdrawalStep = 2) }
    }

    fun proceedToPreview(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val amount = state.withdrawalAmount.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) {
            updateWithdrawal { copy(withdrawalPreviewError = "Le montant doit être supérieur à zéro.") }
            return
        }

        updateWithdrawal { copy(isWithdrawalPreviewLoading = true, withdrawalPreviewError = null) }
        viewModelScope.launch {
            // preview API call
            val isMm = state.withdrawalType == "mobile_money"
            repository.previewWithdrawal(
                method = state.withdrawalType,
                amount = amount,
                currency = state.withdrawalCurrency,
                operator = if (isMm) state.withdrawalOperator else null
            ).onSuccess { response ->
                if(response.success && response.preview!=null) updateWithdrawal{copy(isWithdrawalPreviewLoading=false,withdrawalPreview=response.preview,withdrawalStep=3)}
                else updateWithdrawal{copy(isWithdrawalPreviewLoading=false,withdrawalPreview=null,withdrawalPreviewError=response.error ?: "Devis de retrait non confirmé.")}
            }.onFailure { error ->
                updateWithdrawal{copy(isWithdrawalPreviewLoading=false,withdrawalPreview=null,withdrawalPreviewError=error.message ?: "Impossible de charger le devis de retrait.")}
            }
        }
    }

    fun proceedToPinEntry() {
        updateWithdrawal { copy(withdrawalStep = 4, withdrawalConfirmError = null) }
    }

    fun confirmWithdrawal(userId: Long? = null, pin: String) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val amount = state.withdrawalAmount.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0 || pin.length != 4) {
            updateWithdrawal { copy(withdrawalConfirmError = "Code PIN de 4 chiffres requis.") }
            return
        }

        updateWithdrawal { copy(isWithdrawalConfirmLoading = true, withdrawalConfirmError = null) }
        viewModelScope.launch {
            val agentId = if (state.withdrawalType == "mobile_money") {
                state.withdrawalOperator ?: ""
            } else {
                state.searchedAgentProfile?.walletId ?: state.withdrawalRecipient
            }

            repository.confirmWithdrawal(
                method = state.withdrawalType,
                agent = agentId,
                amount = amount,
                currency = state.withdrawalCurrency,
                pin = pin
            ).onSuccess { resp ->
                if (resp.success) {
                    updateWithdrawal { copy(
                        isWithdrawalConfirmLoading = false,
                        withdrawalSuccess = true,
                        withdrawalStep = 5
                    ) }
                    fetchWallet(effectiveUserId) // Update local balance
                } else if (state.withdrawalType == "mobile_money") {
                    // Transparent maintenance message as required by user
                    updateWithdrawal { copy(
                        isWithdrawalConfirmLoading = false,
                        withdrawalSuccess = false,
                        withdrawalConfirmError = "Service en cours de maintenance technique : Le reversement Mobile Money vers les opérateurs locaux est en cours de configuration. Veuillez réessayer plus tard ou utiliser le retrait Agent CashPay."
                    ) }
                } else {
                    updateWithdrawal { copy(
                        isWithdrawalConfirmLoading = false,
                        withdrawalConfirmError = resp.error ?: "Une erreur s'est produite lors du retrait."
                    ) }
                }
            }.onFailure { err ->
                if (state.withdrawalType == "mobile_money") {
                    updateWithdrawal { copy(
                        isWithdrawalConfirmLoading = false,
                        withdrawalSuccess = false,
                        withdrawalConfirmError = "Service en cours de maintenance : Le service de payout Mobile Money est temporairement indisponible. Veuillez réessayer plus tard."
                    ) }
                } else {
                    updateWithdrawal { copy(
                        isWithdrawalConfirmLoading = false,
                        withdrawalConfirmError = err.message ?: "PIN incorrect ou solde insuffisant."
                    ) }
                }
            }
        }
    }

    fun prevWithdrawalStep() {
        val state = _uiState.value
        if (state.withdrawalStep > 1) {
            updateWithdrawal { copy(withdrawalStep = state.withdrawalStep - 1) }
        }
    }

    // --- THEME ACTIONS ---
    fun toggleDarkMode() {
        updateMain { copy(isDarkMode = !isDarkMode) }
    }

    // --- LOANS MODULE ACTIONS ---
    private val _loanConfirmation=kotlinx.coroutines.flow.MutableStateFlow(com.example.ui.screens.LoanConfirmationState())
    val loanConfirmation=_loanConfirmation.asStateFlow()
    private var loanReviewVersion=0
    fun closeLoanConfirmation(){loanReviewVersion++;_loanConfirmation.value=com.example.ui.screens.LoanConfirmationState()}
    fun reviewLoanRequest(){
        val state=_uiState.value;val amount=state.loanRequestAmount.toDoubleOrNull()
        if(amount==null||!amount.isFinite()||amount<=0){updateLoans{copy(loanRequestError="Montant invalide.")};return}
        val version=++loanReviewVersion;_loanConfirmation.value=com.example.ui.screens.LoanConfirmationState(loading=true)
        updateLoans{copy(loanRequestError=null,loanRepayError=null,repayInstallmentError=null)}
        viewModelScope.launch{repository.loanApplicationQuote(state.loanCurrency,amount,state.loanRequestDuration).onSuccess{if(version==loanReviewVersion)_loanConfirmation.value=com.example.ui.screens.LoanConfirmationState(application=it)}.onFailure{if(version==loanReviewVersion)_loanConfirmation.value=com.example.ui.screens.LoanConfirmationState(error=it.message)}}
    }
    fun reviewLoanRepayment(loanId:Long,installmentId:Long?=null){
        val version=++loanReviewVersion;_loanConfirmation.value=com.example.ui.screens.LoanConfirmationState(loading=true)
        updateLoans{copy(loanRequestError=null,loanRepayError=null,repayInstallmentError=null)}
        val amount=if(installmentId==null)_uiState.value.loanRepayAmount.toDoubleOrNull() else null
        viewModelScope.launch{repository.loanPaymentQuote(loanId,amount,installmentId).onSuccess{if(version==loanReviewVersion)_loanConfirmation.value=com.example.ui.screens.LoanConfirmationState(payment=it)}.onFailure{if(version==loanReviewVersion)_loanConfirmation.value=com.example.ui.screens.LoanConfirmationState(error=it.message)}}
    }
    fun confirmReviewedLoan(){
        val state=_uiState.value;if(state.isRequestingLoan||state.isRepayingLoan||state.isRepayInstallmentLoading)return
        val review=_loanConfirmation.value
        review.application?.let{submitLoanRequest(acceptedTerms=it);return}
        review.payment?.let{if(it.installmentId==null)submitLoanRepayment(it.loanId,acceptedPayment=it) else submitInstallmentRepayment(it.loanId,it.installmentId,acceptedPayment=it)}
    }

    fun handleScannedCode(raw:String,target:String){
        viewModelScope.launch { repository.resolveScan(com.example.data.model.ScanRequest(value=raw)).onSuccess { response ->
            val result=response.scan
            if(result?.type=="WALLET" && !result.walletId.isNullOrBlank()){
                val wallet=result.walletId
                when(target){
                    "agent_withdraw"->{setAgentWithdrawClientRef(wallet);searchAndIdentifyClientForWithdraw()}
                    "agent_deposit"->{setAgentDepositClientRef(wallet);searchAndIdentifyClientForDeposit()}
                    "agent_loan"->{setAgentLoanClientRef(wallet);searchAgentLoanTarget()}
                    else->{onRecipientChanged(wallet);openTransferDialog();searchTransferRecipient()}
                }
            }else if(target.startsWith("agent_")){
                updateAgent{copy(agentWithdrawError="Scannez le QR d’un portefeuille CashPay.",agentDepositError="Scannez le QR d’un portefeuille CashPay.",agentLoanSearchError="Scannez le QR d’un portefeuille CashPay.")}
            }else if(result?.type=="PRODUCTS") {openPayScan();processPayScanInput(raw)}
            else showCommerceScanReview(raw)
        }.onFailure{error->
            if(target.startsWith("agent_"))updateAgent{copy(agentWithdrawError=error.message,agentDepositError=error.message,agentLoanSearchError=error.message)}
            else {openPayScan();updatePayScan{copy(scanError=error.message)}}
        } }
    }
    suspend fun resolveCommerceScan(request:com.example.data.model.ScanRequest)=repository.resolveScan(request)
    suspend fun payCommerceScan(token:String,pin:String)=repository.payScan(token,pin)
    fun refreshAfterCommercePayment() {
        updatePayScan { copy(basketItems = emptyList(), currentPosSaleData = null, showConfirmPaymentDialog = false, showSuccessReceiptDialog = false, completedTransaction = null) }
        fetchWallet(); loadTransactions()
    }

    fun openLoansDialog() {
        closeLoanConfirmation()
        updateLoans { copy(isLoansDialogOpen = true,
                loanSelectedTab = 0,
                loanRequestAmount = "",
                loanRequestDuration = 3,
                loanRequestError = null,
                loanRequestSuccess = false,
                loanRepayAmount = "",
                loanRepayError = null,
                loanRepaySuccess = false,
                repayInstallmentError = null,
                loanInstallmentSuccess = false) }
        fetchLoanOffer()
        fetchActiveLoan()
        fetchLoanHistory()
    }

    fun closeLoansDialog() {
        closeLoanConfirmation()
        updateLoans { copy(isLoansDialogOpen = false) }
    }

    fun setLoanSelectedTab(tab: Int) {
        updateLoans { copy(loanSelectedTab = tab) }
        if (tab == 0) fetchLoanOffer()
        if (tab == 1) fetchActiveLoan()
        if (tab == 2) fetchLoanHistory()
    }

    fun setLoanRequestAmount(amount: String) {
        updateLoans { copy(loanRequestAmount = amount, loanRequestError = null) }
    }

    fun setLoanRequestDuration(dur: Int) {
        updateLoans { copy(loanRequestDuration = dur) }
    }

    fun setLoanRepayAmount(amount: String) {
        updateLoans { copy(loanRepayAmount = amount, loanRepayError = null) }
    }

    fun setLoanCurrency(currency: String) {
        if(currency !in listOf("USD","EUR","CDF") || _uiState.value.isRequestingLoan)return
        updateLoans { copy(loanCurrency=currency,loanOffer=null,loanOfferError=null) }
        fetchLoanOffer()
    }

    fun fetchLoanOffer() {
        val currency = _uiState.value.loanCurrency
        updateLoans { copy(isLoadingLoanOffer = true, loanOfferError = null) }
        viewModelScope.launch {
            repository.getLoanOffer(currency)
                .onSuccess { resp ->
                    if(_uiState.value.loanCurrency!=currency)return@onSuccess
                    if (resp.success && resp.offer != null) {
                        updateLoans { copy(isLoadingLoanOffer = false,
                                loanOffer = resp.offer,
                                loanRequestDuration = if(resp.offer.durations.contains(loanRequestDuration)) loanRequestDuration else resp.offer.durations.firstOrNull() ?: 3,
                                loanOfferError = null) }
                    } else {
                        updateLoans { copy(isLoadingLoanOffer = false,
                                loanOfferError = resp.error ?: "Impossible d'obtenir l'offre.") }
                    }
                }
                .onFailure { err ->
                    if(_uiState.value.loanCurrency!=currency)return@onFailure
                    updateLoans { copy(isLoadingLoanOffer = false,
                            loanOfferError = err.message ?: "Erreur réseau.") }
                }
        }
    }

    fun fetchActiveLoan() {
        updateLoans { copy(isLoadingActiveLoan = true, activeLoanError = null) }
        viewModelScope.launch {
            repository.getLoanCurrent()
                .onSuccess { resp ->
                    updateLoans { copy(isLoadingActiveLoan = false,
                            activeLoan = if (resp.success) resp.loan else null,
                            activeLoanError = if(resp.success) null else resp.error ?: "Chargement du crédit non confirmé.") }
                }
                .onFailure { err ->
                    updateLoans { copy(isLoadingActiveLoan = false,
                            activeLoanError = err.message ?: "Erreur réseau.") }
                }
        }
    }

    fun fetchLoanHistory() {
        updateLoans { copy(isLoadingLoanHistory = true, loanHistoryError = null) }
        viewModelScope.launch {
            repository.getLoanHistory()
                .onSuccess { resp ->
                    updateLoans { copy(isLoadingLoanHistory = false,
                            loanHistory = if (resp.success) resp.loans else emptyList(),
                            loanHistoryError = if(resp.success) null else resp.error ?: "Historique indisponible.") }
                }
                .onFailure { err ->
                    updateLoans { copy(isLoadingLoanHistory = false,
                            loanHistoryError = err.message ?: "Erreur réseau.") }
                }
        }
    }

    fun submitLoanRequest(userId: Long? = null,acceptedTerms:com.example.data.model.LoanApplicationQuote?=null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        if(state.isRequestingLoan)return
        val offer=state.loanOffer
        if(offer==null || !offer.eligible || state.loanRequestDuration !in offer.durations){
            updateLoans { copy(loanRequestError="Chargez une offre valide et choisissez une durée proposée.") };return
        }
        val amount = state.loanRequestAmount.toDoubleOrNull() ?: 0.0
        if (!amount.isFinite() || amount <= 0.0) {
            updateLoans { copy(loanRequestError = "Le montant doit être supérieur à zéro.") }
            return
        }

        updateLoans { copy(isRequestingLoan = true, loanRequestError = null, loanRequestSuccess = false) }
        viewModelScope.launch {
            repository.requestLoan(
                amount = amount,
                currency = offer.currency,
                durationMonths = state.loanRequestDuration,
                repaymentFrequency = if(state.loanRequestDuration==1) "weekly" else if(state.loanRequestDuration==3) "monthly" else "quarterly",
                installmentCount = if(state.loanRequestDuration==1) 4 else if(state.loanRequestDuration==3) 3 else state.loanRequestDuration/3,
                acceptedTerms=acceptedTerms
            ).onSuccess { resp ->
                    if(getEffectiveUserId()!=effectiveUserId)return@onSuccess
                if (resp.success) {
                    updateLoans { copy(isRequestingLoan = false,
                            loanRequestSuccess = true,
                            loanRequestAmount = "",
                            loanRequestError = null) }
                    _loanConfirmation.value=com.example.ui.screens.LoanConfirmationState(receipt="Demande enregistrée. Le crédit sera versé uniquement après approbation.")
                    fetchActiveLoan()
                    fetchLoanHistory()
                    fetchWallet(effectiveUserId) // update wallet balance
                } else {
                    updateLoans { copy(isRequestingLoan = false,
                            loanRequestError = resp.error ?: "Erreur lors de la demande.") }
                }
            }.onFailure { err ->
                    if(getEffectiveUserId()!=effectiveUserId)return@onFailure
                updateLoans { copy(isRequestingLoan = false,
                        loanRequestError = err.message ?: "Erreur réseau.") }
            }
        }
    }

    fun submitLoanRepayment(loanId: Long, userId: Long? = null,acceptedPayment:com.example.data.model.LoanPaymentQuote?=null) {
        if(_uiState.value.isRepayingLoan || _uiState.value.isRepayInstallmentLoading)return
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val amount = acceptedPayment?.amount ?: state.loanRepayAmount.toDoubleOrNull() ?: 0.0
        if (!amount.isFinite() || amount <= 0.0) {
            updateLoans { copy(loanRepayError = "Le montant doit être supérieur à zéro.") }
            return
        }

        updateLoans { copy(isRepayingLoan = true, loanRepayError = null, loanRepaySuccess = false) }
        viewModelScope.launch {
            repository.repayLoan(loanId, amount,acceptedPayment)
                .onSuccess { resp ->
                    if(getEffectiveUserId()!=effectiveUserId)return@onSuccess
                    if (resp.success) {
                        updateLoans { copy(isRepayingLoan = false,
                                loanRepaySuccess = true,
                                loanRepayAmount = "",
                                loanRepayError = null) }
                        _loanConfirmation.value=com.example.ui.screens.LoanConfirmationState(receipt="Remboursement confirmé : ${resp.paid} ${resp.currency}. Frais : ${resp.fee}. Dette restante : ${resp.debtAfter}. Référence : ${resp.reference}.")
                        fetchActiveLoan()
                        fetchLoanHistory()
                        fetchWallet(effectiveUserId)
                    } else {
                        updateLoans { copy(isRepayingLoan = false,
                                loanRepayError = resp.error ?: "Erreur lors du remboursement.") }
                    }
                }
                .onFailure { err ->
                    if(getEffectiveUserId()!=effectiveUserId)return@onFailure
                    updateLoans { copy(isRepayingLoan = false,
                            loanRepayError = err.message ?: "Erreur réseau.") }
                }
        }
    }

    fun submitInstallmentRepayment(loanId: Long, installmentId: Long, userId: Long? = null,acceptedPayment:com.example.data.model.LoanPaymentQuote?=null) {
        if(_uiState.value.isRepayingLoan || _uiState.value.isRepayInstallmentLoading)return
        val effectiveUserId = getEffectiveUserId(userId)
        updateLoans { copy(isRepayInstallmentLoading = true, repayInstallmentError = null, loanInstallmentSuccess = false) }
        viewModelScope.launch {
            repository.repayLoanInstallment(loanId, installmentId,acceptedPayment)
                .onSuccess { resp ->
                    if(getEffectiveUserId()!=effectiveUserId)return@onSuccess
                    if (resp.success) {
                        updateLoans { copy(isRepayInstallmentLoading = false,
                                loanInstallmentSuccess = true,
                                repayInstallmentError = null) }
                        _loanConfirmation.value=com.example.ui.screens.LoanConfirmationState(receipt="Échéance remboursée : ${resp.paid} ${resp.currency}. Frais : ${resp.fee}. Dette restante : ${resp.remaining}. Référence : ${resp.reference}.")
                        fetchActiveLoan()
                        fetchLoanHistory()
                        fetchWallet(effectiveUserId)
                    } else {
                        updateLoans { copy(isRepayInstallmentLoading = false,
                                repayInstallmentError = resp.error ?: "Erreur lors du remboursement de l'échéance.") }
                    }
                }
                .onFailure { err ->
                    if(getEffectiveUserId()!=effectiveUserId)return@onFailure
                    updateLoans { copy(isRepayInstallmentLoading = false,
                            repayInstallmentError = err.message ?: "Erreur réseau.") }
                }
        }
    }

    // --- ACTIVATE AGENT METHODS ---
    fun openActivateAgentDialog() {
        updateMain { copy(isActivateAgentDialogOpen = true,
                agentPlan = "promo",
                agentPin = "",
                isActivatingAgent = false,
                activateAgentError = null,
                activateAgentSuccessMessage = null, agentSubscriptionFee=null) }
        fetchAgentSubscriptionFee()
    }

    fun closeActivateAgentDialog() {
        updateMain { copy(isActivateAgentDialogOpen = false) }
    }

    fun setAgentPlan(plan: String) {
        if(_uiState.value.isActivatingAgent || plan !in listOf("promo","normal")) return
        updateMain { copy(agentPlan = plan, agentSubscriptionFee=null, activateAgentError = null, agentPin="") }
        fetchAgentSubscriptionFee()
    }

    fun setAgentPin(pin: String) {
        updateMain { copy(agentPin = pin.filter(Char::isDigit).take(4), activateAgentError = null) }
    }

    fun fetchAgentSubscriptionFee() {
        val plan=_uiState.value.agentPlan
        updateMain { copy(isLoadingAgentFee=true,agentSubscriptionFee=null) }
        viewModelScope.launch { repository.agentSubscriptionFee(plan).onSuccess { result ->
            if(_uiState.value.agentPlan==plan) updateMain { copy(isLoadingAgentFee=false,agentSubscriptionFee=result.fee,activateAgentError=null) }
        }.onFailure { error -> if(_uiState.value.agentPlan==plan) updateMain { copy(isLoadingAgentFee=false,activateAgentError=error.message) } } }
    }
    fun submitActivateAgent(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        if(_uiState.value.isActivatingAgent) return
        val fee=_uiState.value.agentSubscriptionFee
        if(fee==null || _uiState.value.isLoadingAgentFee){ updateMain { copy(activateAgentError="Chargez le tarif avant de confirmer.") };return }
        val plan = _uiState.value.agentPlan
        val pin = _uiState.value.agentPin
        if (pin.length != 4) {
            updateMain { copy(activateAgentError = "Le PIN doit contenir exactement 4 chiffres.") }
            return
        }

        updateMain { copy(isActivatingAgent = true, activateAgentError = null) }

        viewModelScope.launch {
            val result = repository.activateAgent(plan, pin, fee)
            result.onSuccess { res ->
                if (res.success) {
                    updateMain { copy(isActivatingAgent = false,
                            activateAgentSuccessMessage = res.message ?: "Statut Agent activé avec succès.",
                            activateAgentError = null) }
                    fetchWallet(effectiveUserId)
                } else {
                    updateMain { copy(isActivatingAgent = false,
                            activateAgentError = res.error ?: "Activation Agent impossible.") }
                }
            }.onFailure { err ->
                updateMain { copy(isActivatingAgent = false,
                        activateAgentError = err.message ?: "Erreur d'activation.") }
            }
        }
    }

    // --- FORGOT PIN METHODS IN DASHBOARD ---
    fun openForgotPinDialog(defaultPhone: String? = null) {
        val phoneToUse = defaultPhone ?: _uiState.value.withdrawalRecipient.ifBlank { "+243812345678" }
        updateForgotPin { copy(isForgotPinDialogOpen = true,
                forgotPinStep = 1,
                forgotPinPhone = phoneToUse,
                forgotPinChannel = "sms",
                forgotPinUserId = null,
                forgotPinOtp = "",
                forgotPinNewPin = "",
                forgotPinConfirmPin = "",
                isForgotPinLoading = false,
                forgotPinError = null,
                forgotPinSuccessMessage = null) }
    }

    fun closeForgotPinDialog() {
        updateForgotPin { copy(isForgotPinDialogOpen = false) }
    }

    fun setForgotPinPhone(phone: String) {
        updateForgotPin { copy(forgotPinPhone = phone, forgotPinError = null) }
    }

    fun setForgotPinChannel(channel: String) {
        updateForgotPin { copy(forgotPinChannel = channel, forgotPinError = null) }
    }

    fun setForgotPinOtp(otp: String) {
        updateForgotPin { copy(forgotPinOtp = otp, forgotPinError = null) }
    }

    fun setForgotPinNewPin(pin: String) {
        updateForgotPin { copy(forgotPinNewPin = pin, forgotPinError = null) }
    }

    fun setForgotPinConfirmPin(pin: String) {
        updateForgotPin { copy(forgotPinConfirmPin = pin, forgotPinError = null) }
    }

    fun submitForgotPinRequest() {
        val phone = _uiState.value.forgotPinPhone
        val channel = _uiState.value.forgotPinChannel
        updateForgotPin { copy(isForgotPinLoading = true, forgotPinError = null) }

        viewModelScope.launch {
            val result = repository.forgotPinRequest(phone, channel)
            result.onSuccess { res ->
                if (res.success && res.userId != null) {
                    updateForgotPin { copy(isForgotPinLoading = false,
                            forgotPinStep = 2,
                            forgotPinUserId = res.userId,
                            forgotPinError = null) }
                } else {
                    updateForgotPin { copy(isForgotPinLoading = false,
                            forgotPinError = res.error ?: "Impossible d'envoyer le code.") }
                }
            }.onFailure { err ->
                updateForgotPin { copy(isForgotPinLoading = false,
                        forgotPinError = err.message ?: "Erreur réseau.") }
            }
        }
    }

    fun submitForgotPinVerify() {
        val userId = _uiState.value.forgotPinUserId ?: return
        val otp = _uiState.value.forgotPinOtp
        updateForgotPin { copy(isForgotPinLoading = true, forgotPinError = null) }

        viewModelScope.launch {
            val result = repository.forgotPinVerify(userId, otp)
            result.onSuccess { res ->
                if (res.success) {
                    updateForgotPin { copy(isForgotPinLoading = false,
                            forgotPinStep = 3,
                            forgotPinError = null) }
                } else {
                    updateForgotPin { copy(isForgotPinLoading = false,
                            forgotPinError = res.error ?: "Code invalide.") }
                }
            }.onFailure { err ->
                updateForgotPin { copy(isForgotPinLoading = false,
                        forgotPinError = err.message ?: "Code de vérification invalide ou expiré.") }
            }
        }
    }

    fun submitForgotPinReset() {
        val userId = _uiState.value.forgotPinUserId ?: return
        val otp = _uiState.value.forgotPinOtp
        val newPin = _uiState.value.forgotPinNewPin
        val confirmPin = _uiState.value.forgotPinConfirmPin

        if (newPin != confirmPin) {
            updateForgotPin { copy(forgotPinError = "Les nouveaux codes PIN ne correspondent pas.") }
            return
        }

        updateForgotPin { copy(isForgotPinLoading = true, forgotPinError = null) }

        viewModelScope.launch {
            val result = repository.forgotPinReset(userId, otp, newPin, confirmPin)
            result.onSuccess { res ->
                if (res.success) {
                    updateForgotPin { copy(isForgotPinLoading = false,
                            forgotPinStep = 4,
                            forgotPinSuccessMessage = res.message ?: "Votre PIN a été réinitialisé avec succès.",
                            forgotPinError = null) }
                } else {
                    updateForgotPin { copy(isForgotPinLoading = false,
                            forgotPinError = res.error ?: "Erreur de réinitialisation.") }
                }
            }.onFailure { err ->
                updateForgotPin { copy(isForgotPinLoading = false,
                        forgotPinError = err.message ?: "Échec de réinitialisation.") }
            }
        }
    }

    // ==========================================
    // --- AGENT SERVICES & COMMISSIONS SUITE ---
    // ==========================================

    fun toggleBalanceVisibility() {
        updateMain { copy(isBalanceVisible = !isBalanceVisible) }
    }

    fun toggleAgentBalanceVisibility() {
        updateAgent { copy(isAgentBalanceVisible = !isAgentBalanceVisible) }
    }

    fun openAgentServicesScreen(tab: Int = 0) {
        updateAgent { copy(isAgentServicesDialogOpen = true,
                agentActiveTab = tab,
                sweepCommissionError = null,
                sweepCommissionSuccess = null,
                agentDepositError = null,
                agentDepositPreview = null,
                agentDepositSuccess = null,
                agentWithdrawError = null,
                agentWithdrawSuccess = null,
                agentLoanRepayError = null,
                agentLoanRepaySuccess = null) }
        loadAgentCommissions()
    }

    fun loadAgentCommissions() {
        viewModelScope.launch {
            val result = repository.getAgentCommissions()
            result.onSuccess { res ->
                val balances = res.balances ?: emptyMap()
                val comms = res.commissionBalance ?: emptyMap()
                updateAgent {
                    copy(
                        agentBalancesMap = balances,
                        agentCommissionsMap = comms,
                        agentCommissionUsd = comms["USD"] ?: agentCommissionUsd,
                        agentCommissionCdf = comms["CDF"] ?: agentCommissionCdf,
                        agentCommissionEur = comms["EUR"] ?: agentCommissionEur
                    )
                }
            }
        }
    }

    fun closeAgentServicesScreen() {
        updateAgent { copy(isAgentServicesDialogOpen = false) }
    }

    fun setAgentActiveTab(tab: Int) {
        updateAgent { copy(agentActiveTab = tab) }
    }

    // --- AGENT CUSTOMER ONBOARDING (KYC MODULE) ---
    fun openAgentCustomerRegister() {
        updateAgent { copy(isAgentCustomerSheetOpen = true,
                agentCustomerRegisterSuccess = null,
                agentCustomerRegisterError = null) }
        loadAgentCustomerOptions()
    }

    fun closeAgentCustomerRegister() {
        updateAgent { copy(isAgentCustomerSheetOpen = false,
                agentCustomerRegisterError = null) }
    }

    fun loadAgentCustomerOptions() {
        val agentCountry = userProfile.value?.country
        updateAgent { copy(isAgentCustomerOptionsLoading = true, agentCustomerRegisterError = null) }
        viewModelScope.launch {
            val res = repository.getAgentCustomerOptions(agentCountry)
            res.onSuccess { opts ->
                val effectiveCountry = opts.country ?: if (!agentCountry.isNullOrBlank()) {
                    com.example.data.model.AgentCountryOptionDto(
                        code = agentCountry,
                        name = if (agentCountry.equals("CD", ignoreCase = true)) "RD Congo" else agentCountry,
                        dialCode = if (agentCountry.equals("CD", ignoreCase = true)) "+243" else null,
                        currencyCode = "USD"
                    )
                } else null
                val finalOpts = opts.copy(country = effectiveCountry)
                updateAgent { copy(isAgentCustomerOptionsLoading = false,
                        agentCustomerOptions = finalOpts,
                        agentCustomerRegisterError = null) }
            }.onFailure { err ->
                updateAgent { copy(isAgentCustomerOptionsLoading = false,
                        agentCustomerRegisterError = err.message) }
            }
        }
    }

    fun loadAgentCustomersList() {
        updateAgent { copy(isLoadingAgentCustomers = true) }
        viewModelScope.launch {
            val res = repository.getAgentCustomers()
            res.onSuccess { listResp ->
                updateAgent { copy(isLoadingAgentCustomers = false,
                        agentCustomersList = listResp.customers) }
            }.onFailure {
                updateAgent { copy(isLoadingAgentCustomers = false) }
            }
        }
    }

    fun registerAgentCustomer(
        request: com.example.data.model.AgentRegisterCustomerRequest,
        onSuccess: (com.example.data.model.AgentRegisterCustomerResponse) -> Unit = {}
    ) {
        updateAgent { copy(isAgentRegisteringCustomer = true, agentCustomerRegisterError = null) }
        viewModelScope.launch {
            val res = repository.registerAgentCustomer(request)
            res.onSuccess { resp ->
                updateAgent { copy(isAgentRegisteringCustomer = false,
                        agentCustomerRegisterSuccess = resp,
                        agentCustomerRegisterError = null) }
                onSuccess(resp)
                loadAgentCustomersList()
            }.onFailure { err ->
                updateAgent { copy(isAgentRegisteringCustomer = false,
                        agentCustomerRegisterError = err.message ?: "Échec de l'enregistrement du client.") }
            }
        }
    }

    // ==========================================
    // --- AGENT SHOPPING & BOUTIQUES (V1 API) ---
    // ==========================================

    fun openAgentShoppingDialog(tab: Int = 0) {
        val prof = userProfile.value
        if (prof != null) {
            com.example.data.remote.ApiClient.currentWalletId = prof.walletId
            com.example.data.remote.ApiClient.currentUserId = prof.id
            com.example.data.remote.ApiClient.currentPhone = prof.phone
            com.example.data.remote.ApiClient.currentUserRole = prof.role ?: "agent"
        }
        updateShopping { copy(isAgentShoppingDialogOpen = true,
                shoppingActiveTab = tab,
                shoppingError = null,
                shoppingSuccessMessage = null,
                editingProduct = null) }
        loadShoppingData()
    }

    fun closeAgentShoppingDialog() {
        updateShopping { copy(isAgentShoppingDialogOpen = false) }
    }

    fun setShoppingActiveTab(tab: Int) {
        updateShopping { copy(shoppingActiveTab = tab, shoppingError = null, shoppingSuccessMessage = null) }
        if (tab == 0) fetchShoppingBoutiques()
        else if (tab == 1) fetchShoppingProducts()
        else if (tab == 3) fetchPublicCatalog()
    }

    fun setEditingProduct(product: com.example.data.model.ProductDto?) {
        updateShopping { copy(editingProduct = product,
                shoppingActiveTab = 2,
                shoppingError = null,
                shoppingSuccessMessage = null) }
    }

    fun loadShoppingData() {
        fetchShoppingBoutiques()
        fetchShoppingProducts()
    }

    fun fetchShoppingBoutiques() {
        updateShopping { copy(isLoadingShoppingBoutiques = true, shoppingBoutiquesError = null) }
        viewModelScope.launch {
            repository.getShoppingBoutiques().onSuccess { res ->
                updateShopping { copy(isLoadingShoppingBoutiques = false, shoppingBoutiques = res.boutiques, shoppingBoutiquesError = null) }
            }.onFailure { err ->
                updateShopping { copy(isLoadingShoppingBoutiques = false, shoppingBoutiquesError = err.message ?: "Impossible de charger vos boutiques.") }
            }
        }
    }

    fun createShoppingBoutique(
        name: String,
        address: String? = null,
        whatsappNumber: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        if (name.isBlank()) {
            updateShopping { copy(shoppingError = "Le nom de la boutique est requis.") }
            return
        }
        updateShopping { copy(isShoppingLoading = true, shoppingError = null, shoppingSuccessMessage = null) }
        viewModelScope.launch {
            val result = repository.createBoutique(name, address, whatsappNumber)
            result.onSuccess { res ->
                val created = res.boutique
                updateShopping {
                    val currentList = shoppingBoutiques.toMutableList()
                    if (created != null && currentList.none { it.id == created.id }) {
                        currentList.add(0, created)
                    }
                    copy(
                        isShoppingLoading = false,
                        shoppingBoutiques = currentList,
                        shoppingSuccessMessage = res.message ?: "Boutique créée avec succès !",
                        shoppingError = null
                    )
                }
                fetchShoppingBoutiques()
                onSuccess()
            }.onFailure { err ->
                updateShopping { copy(isShoppingLoading = false,
                        shoppingError = err.message ?: "Impossible de créer la boutique.") }
            }
        }
    }

    fun updateShoppingBoutique(
        boutiqueId: String,
        name: String,
        address: String? = null,
        whatsappNumber: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        if (name.isBlank()) {
            updateShopping { copy(shoppingError = "Le nom de la boutique est requis.") }
            return
        }
        updateShopping { copy(isShoppingLoading = true, shoppingError = null, shoppingSuccessMessage = null) }
        viewModelScope.launch {
            val result = repository.updateBoutique(boutiqueId, name, address, whatsappNumber)
            result.onSuccess {
                updateShopping { copy(isShoppingLoading = false,
                        shoppingSuccessMessage = "Boutique mise à jour avec succès !") }
                fetchShoppingBoutiques()
                onSuccess()
            }.onFailure { err ->
                updateShopping { copy(isShoppingLoading = false,
                        shoppingError = err.message ?: "Impossible de modifier la boutique.") }
            }
        }
    }

    fun deleteShoppingBoutique(boutiqueId: String) {
        updateShopping { copy(isShoppingLoading = true, shoppingError = null, shoppingSuccessMessage = null) }
        viewModelScope.launch {
            val result = repository.deleteBoutique(boutiqueId)
            result.onSuccess {
                updateShopping { copy(isShoppingLoading = false,
                        shoppingSuccessMessage = "Boutique supprimée.") }
                fetchShoppingBoutiques()
            }.onFailure { err ->
                updateShopping { copy(isShoppingLoading = false,
                        shoppingError = err.message ?: "Impossible de supprimer la boutique.") }
            }
        }
    }

    fun fetchShoppingProducts() {
        val hadProducts = _uiState.value.shoppingProducts.isNotEmpty()
        if (!hadProducts) {
            updateShopping { copy(isShoppingLoading = true, shoppingError = null) }
        }
        viewModelScope.launch {
            val result = repository.getShoppingProducts()
            result.onSuccess { res ->
                updateShopping { copy(isShoppingLoading = false, shoppingProducts = res.products, shoppingError = null) }
            }.onFailure { err ->
                updateShopping { copy(
                    isShoppingLoading = false,
                    shoppingError = if (shoppingProducts.isEmpty() && shoppingActiveTab == 1) err.message else shoppingError
                ) }
            }
        }
    }

    fun fetchProductReference(productId: String) {
        viewModelScope.launch {
            val result = repository.getProductReference(productId)
            result.onSuccess { refRes ->
                updateShopping { copy(productReferenceInfo = refRes) }
            }
        }
    }

    fun publishProduct(
        request: com.example.data.model.PublishProductRequest,
        onSuccess: () -> Unit = {}
    ) {
        updateShopping { copy(isShoppingLoading = true, shoppingError = null, shoppingSuccessMessage = null) }
        viewModelScope.launch {
            val result = repository.publishProduct(request)
            result.onSuccess { res ->
                updateShopping { copy(isShoppingLoading = false,
                        shoppingSuccessMessage = "Produit publié avec succès ! (Réf : ${res.reference ?: res.product?.id ?: "OK"})") }
                fetchShoppingProducts()
                onSuccess()
            }.onFailure { err ->
                updateShopping { copy(isShoppingLoading = false,
                        shoppingError = err.message ?: "Erreur de publication du produit.") }
            }
        }
    }

    fun updateProduct(
        productId: String,
        request: com.example.data.model.PublishProductRequest,
        onSuccess: () -> Unit = {}
    ) {
        updateShopping { copy(isShoppingLoading = true, shoppingError = null, shoppingSuccessMessage = null) }
        viewModelScope.launch {
            val result = repository.updateProduct(productId, request)
            result.onSuccess {
                updateShopping { copy(isShoppingLoading = false,
                        shoppingSuccessMessage = "Produit mis à jour avec succès !",
                        editingProduct = null) }
                fetchShoppingProducts()
                onSuccess()
            }.onFailure { err ->
                updateShopping { copy(isShoppingLoading = false,
                        shoppingError = err.message ?: "Impossible de modifier le produit.") }
            }
        }
    }

    fun deleteProduct(productId: String) {
        updateShopping { copy(isShoppingLoading = true, shoppingError = null, shoppingSuccessMessage = null) }
        viewModelScope.launch {
            val result = repository.deleteProduct(productId)
            result.onSuccess {
                updateShopping { copy(isShoppingLoading = false,
                        shoppingSuccessMessage = "Produit supprimé.") }
                fetchShoppingProducts()
            }.onFailure { err ->
                updateShopping { copy(isShoppingLoading = false,
                        shoppingError = err.message ?: "Impossible de supprimer le produit.") }
            }
        }
    }

    fun fetchPublicCatalog(userId: String? = null, storeId: String? = null) {
        val targetId = userId ?: (_uiState.value.currentUserId?.toString() ?: userProfile.value?.id?.toString() ?: "")
        if (targetId.isBlank()) return
        updateShopping { copy(isPublicCatalogLoading = true) }
        viewModelScope.launch {
            val result = repository.getPublicUserProducts(targetId, storeId)
            result.onSuccess { res ->
                updateShopping { copy(isPublicCatalogLoading = false,
                        publicCatalog = res) }
            }.onFailure { err ->
                updateShopping { copy(isPublicCatalogLoading = false,
                        shoppingError = err.message) }
            }
        }
    }

    // --- BASCULER COMMISSION ---
    fun openSweepCommissionDialog(currency: String = "USD") {
        val available = when (currency.uppercase()) {
            "CDF" -> _uiState.value.agentCommissionCdf
            "EUR" -> _uiState.value.agentCommissionEur
            else -> _uiState.value.agentCommissionUsd
        }
        updateAgent { copy(isSweepCommissionDialogOpen = true,
                sweepCurrency = currency.uppercase(),
                sweepAmount = if (available > 0) String.format(java.util.Locale.US, "%.2f", available) else "",
                sweepIsAll = true,
                sweepPin = "",
                isSweepingCommission = false,
                sweepCommissionError = null,
                sweepCommissionSuccess = null) }
    }

    fun closeSweepCommissionDialog() {
        updateAgent { copy(isSweepCommissionDialogOpen = false) }
    }

    fun setSweepCurrency(currency: String) {
        val curr = currency.uppercase()
        val available = when (curr) {
            "CDF" -> _uiState.value.agentCommissionCdf
            "EUR" -> _uiState.value.agentCommissionEur
            else -> _uiState.value.agentCommissionUsd
        }
        updateAgent { copy(sweepCurrency = curr,
                sweepAmount = if (sweepIsAll && available > 0) String.format(java.util.Locale.US, "%.2f", available) else sweepAmount,
                sweepCommissionError = null) }
    }

    fun setSweepAmount(amount: String) {
        updateAgent { copy(sweepAmount = amount, sweepIsAll = false, sweepCommissionError = null) }
    }

    fun setSweepIsAll(isAll: Boolean) {
        val available = when (_uiState.value.sweepCurrency.uppercase()) {
            "CDF" -> _uiState.value.agentCommissionCdf
            "EUR" -> _uiState.value.agentCommissionEur
            else -> _uiState.value.agentCommissionUsd
        }
        updateAgent { copy(sweepIsAll = isAll,
                sweepAmount = if (isAll && available > 0) String.format(java.util.Locale.US, "%.2f", available) else sweepAmount,
                sweepCommissionError = null) }
    }

    fun setSweepPin(pin: String) {
        updateAgent { copy(sweepPin = pin, sweepCommissionError = null) }
    }

    fun submitSweepCommission(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val curr = state.sweepCurrency.uppercase()
        val available = when (curr) {
            "CDF" -> state.agentCommissionCdf
            "EUR" -> state.agentCommissionEur
            else -> state.agentCommissionUsd
        }

        val amountToSweep = if (state.sweepIsAll) {
            available
        } else {
            state.sweepAmount.toDoubleOrNull() ?: 0.0
        }

        if (amountToSweep <= 0.0) {
            updateAgent { copy(sweepCommissionError = "Veuillez indiquer un montant supérieur à 0.") }
            return
        }

        if (amountToSweep > available) {
            updateAgent { copy(sweepCommissionError = "Montant supérieur à la commission disponible ($available $curr).") }
            return
        }

        if (state.sweepPin.length != 4) {
            updateAgent { copy(sweepCommissionError = "Le code PIN doit comporter 4 chiffres.") }
            return
        }

        updateAgent { copy(isSweepingCommission = true, sweepCommissionError = null) }

        viewModelScope.launch {
            val result = repository.transferAgentCommission(
                currency = curr,
                amount = amountToSweep,
                pin = state.sweepPin
            )

            result.onSuccess { res ->
                // Apply update to commission balances and main balances
                val newUsdComm = if (curr == "USD") (state.agentCommissionUsd - amountToSweep).coerceAtLeast(0.0) else state.agentCommissionUsd
                val newCdfComm = if (curr == "CDF") (state.agentCommissionCdf - amountToSweep).coerceAtLeast(0.0) else state.agentCommissionCdf
                val newEurComm = if (curr == "EUR") (state.agentCommissionEur - amountToSweep).coerceAtLeast(0.0) else state.agentCommissionEur

                // Update walletResponse fiat balances
                val currentWallet = state.walletResponse
                val currentFiat = currentWallet?.balances?.fiat?.toMutableMap() ?: mutableMapOf<String, Double>()
                val currentCurrBal = currentFiat[curr] ?: 0.0
                currentFiat[curr] = currentCurrBal + amountToSweep
                val updatedBalances = currentWallet?.balances?.copy(fiat = currentFiat)
                val updatedWallet = currentWallet?.copy(balances = updatedBalances)

                // Add record to agent operations history
                val newOp = com.example.data.model.AgentOperationRecord(
                    type = "COMMISSION_SWEEP",
                    title = "Bascule Commission vers Compte Principal",
                    clientRef = null,
                    amount = amountToSweep,
                    currency = curr,
                    commission = 0.0,
                    reference = res.reference ?: "Consultez l’historique",
                    date = "Aujourd'hui, ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
                    status = "Complété"
                )

                _uiState.update {
 it.copy(
main = it.main.copy(
walletResponse = updatedWallet ?: it.main.walletResponse,
),
agent = it.agent.copy(
                        isSweepingCommission = false,
                        agentCommissionUsd = newUsdComm,
                        agentCommissionCdf = newCdfComm,
                        agentCommissionEur = newEurComm,
                        sweepCommissionSuccess = res.message ?: "Commission de ${String.format(java.util.Locale.US, "%.2f", amountToSweep)} $curr basculée avec succès dans votre solde principal.",
                        sweepCommissionError = null,
                        sweepPin = "",
                        agentOperationsHistory = listOf(newOp) + it.agent.agentOperationsHistory
)
)
}

                // Also reload wallet from backend to keep everything synchronized
                fetchWallet(effectiveUserId)
            }.onFailure { error ->
                updateAgent { copy(isSweepingCommission=false,sweepCommissionError=error.message ?: "Bascule non confirmée. Vérifiez votre historique avant de réessayer.",sweepPin="") }
            }
        }
    }

    // --- AGENT DEPOSIT (MULTI-STEP PROTOCOL) ---
    fun setAgentDepositClientRef(ref: String) {
        updateAgent { copy(agentDepositClientRef = ref,
                agentDepositError = null,
                agentDepositPreview = null,
                agentDepositFoundClient = if (agentDepositStep != "identify") null else agentDepositFoundClient,
                agentDepositStep = if (agentDepositStep != "identify" && agentDepositStep != "completed") "identify" else agentDepositStep) }
    }

    fun prefillClientFromPublicProfileOrScanner(clientRef: String) {
        updateAgent { copy(agentDepositClientRef = clientRef,
                agentDepositError = null,
                agentDepositPreview = null,
                agentDepositStep = "identify") }
        searchAndIdentifyClientForDeposit()
    }

    fun searchAndIdentifyClientForDeposit() {
        val clientRef = _uiState.value.agentDepositClientRef.trim()
        if (clientRef.isBlank()) {
            updateAgent { copy(agentDepositError = "Veuillez entrer le numéro de téléphone ou Wallet ID du client.") }
            return
        }

        updateAgent { copy(isAgentDepositLoading = true, agentDepositError = null, agentDepositPreview = null) }

        viewModelScope.launch {
            val isPhone = _uiState.value.agentDepositSearchMode == "phone" || (clientRef.all { it.isDigit() || it == '+' || it == ' ' } && clientRef.filter { it.isDigit() }.length >= 8)
            val req = com.example.data.model.AgentDepositRequest(
                step = "identify",
                clientWalletId = clientRef,
                phone = if (isPhone) clientRef else null,
                clientRef = clientRef
            )

            val result = repository.executeAgentDeposit(req)
            result.onSuccess { res ->
                if (res.success && res.client != null) {
                    val clientRole = res.client.role?.lowercase()?.trim()
                    if (clientRole == "agent") {
                        updateAgent { copy(isAgentDepositLoading = false,
                                agentDepositError = "Le compte destinataire saisi appartient à un Agent. Le dépôt d'espèces est réservé aux comptes Clients. Pour transférer des fonds entre agents, utilisez le menu Transfert.",
                                agentDepositFoundClient = null) }
                    } else {
                        updateAgent { copy(isAgentDepositLoading = false,
                                agentDepositStep = "confirm_client",
                                agentDepositFoundClient = res.client,
                                agentDepositError = null) }
                    }
                } else {
                    // Fallback to public profile search by phone or wallet ID
                    val profileRes = if (isPhone) repository.searchProfileByPhone(clientRef) else repository.searchProfileByWallet(clientRef)
                    profileRes.onSuccess { pubResp ->
                        val profile = pubResp.profile
                        if (profile != null) {
                            if (profile.role?.lowercase()?.trim() == "agent") {
                                updateAgent { copy(isAgentDepositLoading = false,
                                        agentDepositError = "Le compte destinataire saisi appartient à un Agent. Le dépôt d'espèces est réservé aux comptes Clients. Pour transférer des fonds entre agents, utilisez le menu Transfert.",
                                        agentDepositFoundClient = null) }
                            } else {
                                val clientDto = com.example.data.model.AgentDepositClientDto(
                                    walletId = profile.walletId,
                                    phone = clientRef,
                                    fullName = profile.fullName,
                                    role = profile.role,
                                    profilePhotoUrl = profile.profilePhotoUrl ?: profile.profilePhoto
                                )
                                updateAgent { copy(isAgentDepositLoading = false,
                                        agentDepositStep = "confirm_client",
                                        agentDepositFoundClient = clientDto,
                                        agentDepositError = null) }
                            }
                        } else {
                            updateAgent { copy(isAgentDepositLoading = false,
                                    agentDepositError = res.error ?: "Client introuvable.") }
                        }
                    }.onFailure {
                        updateAgent { copy(isAgentDepositLoading = false,
                                agentDepositError = res.error ?: "Client introuvable.") }
                    }
                }
            }.onFailure { err ->
                // Check public profile as second line
                val profileRes = if (isPhone) repository.searchProfileByPhone(clientRef) else repository.searchProfileByWallet(clientRef)
                profileRes.onSuccess { pubResp ->
                    val profile = pubResp.profile
                    if (profile != null && profile.role?.lowercase() != "agent") {
                        val clientDto = com.example.data.model.AgentDepositClientDto(
                            walletId = profile.walletId,
                            phone = clientRef,
                            fullName = profile.fullName,
                            role = profile.role,
                            profilePhotoUrl = profile.profilePhotoUrl ?: profile.profilePhoto
                        )
                        updateAgent { copy(isAgentDepositLoading = false,
                                agentDepositStep = "confirm_client",
                                agentDepositFoundClient = clientDto,
                                agentDepositError = null) }
                    } else {
                        updateAgent { copy(isAgentDepositLoading = false,
                                agentDepositError = err.message ?: "Client introuvable.") }
                    }
                }.onFailure {
                    updateAgent { copy(isAgentDepositLoading = false,
                            agentDepositError = err.message ?: "Client introuvable.") }
                }
            }
        }
    }

    fun confirmClientForDeposit() {
        val state=_uiState.value;val client=state.agentDepositFoundClient
        val wallet=client?.walletId ?: state.agentDepositClientRef.trim()
        if(wallet.isBlank())return
        updateAgent{copy(isAgentDepositLoading=true,agentDepositError=null)}
        viewModelScope.launch { repository.executeAgentDeposit(com.example.data.model.AgentDepositRequest(step="confirm_client",clientWalletId=wallet,clientRef=wallet,phone=client?.phone)).onSuccess{response->
            val balances=response.agentBalances
            if(balances==null){updateAgent{copy(isAgentDepositLoading=false,agentDepositError="Soldes agent non confirmés. Actualisez avant de continuer.")};return@onSuccess}
            updateAgent{copy(isAgentDepositLoading=false,agentDepositStep="amount",agentDepositAgentBalances=balances)}
        }.onFailure{error->updateAgent{copy(isAgentDepositLoading=false,agentDepositError=error.message)}} }
    }

    fun setAgentDepositAmount(amount: String) {
        updateAgent { copy(agentDepositAmount = amount, agentDepositError = null, agentDepositPreview = null) }
    }

    fun setAgentDepositCurrency(currency: String) {
        updateAgent { copy(agentDepositCurrency = currency, agentDepositError = null, agentDepositPreview = null) }
    }

    fun submitAmountForDeposit() {
        val state = _uiState.value
        val walletId = state.agentDepositFoundClient?.walletId ?: state.agentDepositClientRef.trim()
        val amount = state.agentDepositAmount.toDoubleOrNull() ?: 0.0
        val currency = state.agentDepositCurrency

        if (amount <= 0.0) {
            updateAgent { copy(agentDepositError = "Montant invalide.") }
            return
        }
        if (currency.isBlank()) {
            updateAgent { copy(agentDepositError = "Devise requise.") }
            return
        }

        updateAgent { copy(isAgentDepositLoading = true, agentDepositError = null, agentDepositPreview = null) }

        viewModelScope.launch {
            val req = com.example.data.model.AgentDepositRequest(
                step = "amount",
                clientWalletId = walletId,
                clientRef = walletId,
                phone = state.agentDepositFoundClient?.phone,
                currency = currency,
                amount = amount
            )
            val result = repository.executeAgentDeposit(req)
            result.onSuccess { res ->
                // Check if step succeeded, or provided preview/detail, or advanced step
                if (res.success || res.deposit != null || res.preview != null || res.nextStep == "pin" || res.step == "pin") {
                    updateAgent { copy(isAgentDepositLoading = false,
                            agentDepositStep = "pin",
                            agentDepositDetail = res.deposit,
                            agentDepositPreview = res.preview,
                            agentDepositPin = "",
                            agentDepositError = null) }
                } else {
                    // Transition to PIN step directly so the agent can authorize with their PIN
                    updateAgent { copy(isAgentDepositLoading = false,
                            agentDepositStep = "pin",
                            agentDepositDetail = res.deposit,
                            agentDepositPreview = res.preview,
                            agentDepositPin = "",
                            agentDepositError = null) }
                }
            }.onFailure { error ->
                updateAgent { copy(isAgentDepositLoading=false,agentDepositError=error.message ?: "Montant non confirmé par le serveur.") }
            }
        }
    }

    fun setAgentDepositPin(pin: String) {
        updateAgent { copy(agentDepositPin = pin, agentDepositError = null) }
    }

    fun submitAgentDeposit(userId: Long? = null) {
        submitPinForDeposit(userId)
    }

    fun submitPinForDeposit(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val walletId = state.agentDepositFoundClient?.walletId ?: state.agentDepositClientRef.trim()
        val amount = state.agentDepositAmount.toDoubleOrNull() ?: 0.0
        val currency = state.agentDepositCurrency
        val pin = state.agentDepositPin.trim()

        if (pin.length != 4) {
            updateAgent { copy(agentDepositError = "Le code PIN agent doit contenir 4 chiffres.") }
            return
        }

        updateAgent { copy(isAgentDepositLoading = true, agentDepositError = null) }

        viewModelScope.launch {
            val req = com.example.data.model.AgentDepositRequest(
                step = "pin",
                clientWalletId = walletId,
                clientRef = walletId,
                phone = state.agentDepositFoundClient?.phone,
                currency = currency,
                amount = amount,
                pin = pin
            )
            val result = repository.executeAgentDeposit(req)
            result.onSuccess { res ->
                if (res.success || res.status == "completed" || res.step == "pin" || res.deposit != null || res.transactionId != null) {
                    val clientName = res.deposit?.clientName ?: res.client?.fullName ?: state.agentDepositFoundClient?.fullName ?: walletId
                    val txId = res.deposit?.transactionId ?: res.transactionId ?: (System.currentTimeMillis() % 100000)
                    val op = com.example.data.model.AgentOperationRecord(
                        type = "DEPOSIT",
                        title = "Dépôt Client Espèces",
                        clientRef = clientName,
                        amount = res.deposit?.depositedAmount ?: amount,
                        currency = res.deposit?.currency ?: currency,
                        commission = res.commission ?: 0.0,
                        reference = "TX-$txId",
                        date = "Aujourd'hui, ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
                        status = "Complété"
                    )

                    updateAgent { copy(isAgentDepositLoading = false,
                            agentDepositStep = "completed",
                            agentDepositSuccessDetail = res.deposit,
                            agentDepositSuccess = res,
                            agentDepositError = null,
                            agentOperationsHistory = listOf(op) + agentOperationsHistory) }
                    fetchWallet(effectiveUserId)
                    loadAgentCommissions()
                } else {
                    updateAgent { copy(isAgentDepositLoading = false,
                            agentDepositError = res.error ?: res.message ?: "Code PIN incorrect ou solde insuffisant.",
                            agentDepositStep = "pin") }
                }
            }.onFailure { err ->
                updateAgent { copy(isAgentDepositLoading = false,
                        agentDepositError = err.message ?: "Code PIN incorrect ou solde insuffisant.",
                        agentDepositStep = "pin") }
            }
        }
    }

    fun resetAgentDeposit() {
        updateAgent { copy(agentDepositStep = "identify",
                agentDepositClientRef = "",
                agentDepositFoundClient = null,
                agentDepositAgentBalances = emptyMap(),
                agentDepositAmount = "",
                agentDepositPin = "",
                agentDepositError = null,
                agentDepositPreview = null,
                agentDepositDetail = null,
                agentDepositSuccessDetail = null,
                agentDepositSuccess = null,
                agentDepositFinancialDetails = null,
                isAgentDepositLoading = false) }
    }

    // --- AGENT WITHDRAW ---
    fun setAgentWithdrawClientRef(ref: String) {
        updateAgent { copy(agentWithdrawClientRef = ref, agentWithdrawError = null) }
    }

    fun setAgentWithdrawAmount(amount: String) {
        updateAgent { copy(agentWithdrawAmount = amount, agentWithdrawError = null) }
    }

    fun setAgentWithdrawCurrency(currency: String) {
        updateAgent { copy(agentWithdrawCurrency = currency, agentWithdrawError = null) }
    }

    fun setAgentWithdrawChannel(channel: String) {
        updateAgent { copy(agentWithdrawChannel = channel, agentWithdrawError = null) }
    }

    fun setAgentWithdrawClientOtp(otp: String) {
        updateAgent { copy(agentWithdrawClientOtp = otp, agentWithdrawError = null) }
    }

    fun resetAgentWithdraw() {
        updateAgent { copy(agentWithdrawStep = 1,
                agentWithdrawClientRef = "",
                agentWithdrawFoundClient = null,
                agentWithdrawAmount = "",
                agentWithdrawClientOtp = "",
                agentWithdrawError = null,
                agentWithdrawSuccess = null,
                isAgentWithdrawLoading = false,
                agentWithdrawOperationId = null,
                agentWithdrawOtpChannels = emptyList()) }
    }

    fun searchAndIdentifyClientForWithdraw() {
        val ref=_uiState.value.agentWithdrawClientRef.trim()
        if(ref.isBlank())return
        updateAgent{copy(isAgentWithdrawLoading=true,agentWithdrawError=null,agentWithdrawFoundClient=null,agentWithdrawOperationId=null,agentWithdrawPreview=null)}
        viewModelScope.launch { repository.withdrawAction(com.example.data.model.WithdrawActionRequest(operation="agent_withdraw",action="start",identifier=ref,clientWalletId=ref)).onSuccess{response->
            val client=response.target
            if(client==null || response.operationId==null){updateAgent{copy(isAgentWithdrawLoading=false,agentWithdrawError="Identification non confirmée")};return@onSuccess}
            updateAgent{copy(isAgentWithdrawLoading=false,agentWithdrawFoundClient=com.example.data.model.PublicProfileDto(walletId=client.walletId ?: ref,fullName=client.fullName ?: ref,role=client.role,profilePhotoUrl=client.avatar),agentWithdrawOperationId=response.operationId,agentWithdrawStep=2)}
        }.onFailure{error->updateAgent{copy(isAgentWithdrawLoading=false,agentWithdrawError=error.message)}} }
    }

    fun submitAmountForWithdraw() {
        val state=_uiState.value;val amount=state.agentWithdrawAmount.toDoubleOrNull()
        if(amount==null || !amount.isFinite() || amount<=0 || state.agentWithdrawOperationId==null){updateAgent{copy(agentWithdrawError="Identifiez le client et entrez un montant valide")};return}
        updateAgent{copy(isAgentWithdrawLoading=true,agentWithdrawError=null)}
        viewModelScope.launch { try {
            val identity=repository.withdrawAction(com.example.data.model.WithdrawActionRequest(operation="agent_withdraw",action="confirm_identity",operationId=state.agentWithdrawOperationId)).getOrThrow()
            val quote=repository.withdrawAction(com.example.data.model.WithdrawActionRequest(operation="agent_withdraw",action="amount",operationId=identity.operationId,amount=amount,currency=state.agentWithdrawCurrency)).getOrThrow()
            updateAgent{copy(isAgentWithdrawLoading=false,agentWithdrawOperationId=quote.operationId,agentWithdrawPreview=quote,agentWithdrawStep=3,agentWithdrawOtpChannels=listOf("sms","whatsapp","email"))}
        }catch(error:Exception){updateAgent{copy(isAgentWithdrawLoading=false,agentWithdrawError=error.message)}} }
    }

    fun initiateAgentWithdraw(userId:Long?=null) {
        val state=_uiState.value
        if(state.agentWithdrawOperationId==null || state.isAgentWithdrawLoading)return
        updateAgent{copy(isAgentWithdrawLoading=true,agentWithdrawError=null)}
        viewModelScope.launch { try {
            val confirmed=repository.withdrawAction(com.example.data.model.WithdrawActionRequest(operation="agent_withdraw",action="confirm_amount",operationId=state.agentWithdrawOperationId)).getOrThrow()
            val result=repository.withdrawAction(com.example.data.model.WithdrawActionRequest(operation="agent_withdraw",action="otp_channel",operationId=confirmed.operationId,channel=state.agentWithdrawChannel)).getOrThrow()
            updateAgent{copy(isAgentWithdrawLoading=false,agentWithdrawOperationId=result.operationId,agentWithdrawStep=4)}
        }catch(error:Exception){updateAgent{copy(isAgentWithdrawLoading=false,agentWithdrawError=error.message)}} }
    }

    fun submitAgentWithdrawOtp(userId: Long? = null) {
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val otp = state.agentWithdrawClientOtp.trim()
        val opId = state.agentWithdrawOperationId
        
        if (otp.length != 6) {
            updateAgent { copy(agentWithdrawError = "Veuillez entrer le code OTP client.") }
            return
        }

        updateAgent { copy(isAgentWithdrawLoading = true, agentWithdrawError = null) }

        viewModelScope.launch {
            val req = com.example.data.model.WithdrawActionRequest(
                operation = "agent_withdraw",
                action = "otp",
                operationId = opId,
                otp = otp
            )
            repository.withdrawAction(req)
                .onSuccess { resp ->
                    val amount = state.agentWithdrawAmount.toDoubleOrNull() ?: 0.0
                    val currency = state.agentWithdrawCurrency
                    val earnedComm = resp.commission ?: (amount * 0.015)
                    
                    val newUsdComm = if (currency == "USD") state.agentCommissionUsd + earnedComm else state.agentCommissionUsd
                    val newCdfComm = if (currency == "CDF") state.agentCommissionCdf + earnedComm else state.agentCommissionCdf
                    val newEurComm = if (currency == "EUR") state.agentCommissionEur + earnedComm else state.agentCommissionEur

                    val op = com.example.data.model.AgentOperationRecord(
                        type = "WITHDRAW",
                        title = "Retrait Client (OTP)",
                        clientRef = state.agentWithdrawClientRef,
                        amount = amount,
                        currency = currency,
                        commission = earnedComm,
                        reference = resp.reference ?: "Consultez l’historique",
                        date = "Aujourd'hui, ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
                        status = "Complété"
                    )

                    updateAgent { copy(isAgentWithdrawLoading = false,
                            agentWithdrawSuccess = com.example.data.model.AgentWithdrawResponse(
                                success = true,
                                reference = resp.reference,
                                commission = earnedComm
                            ),
                            agentWithdrawError = null,
                            agentCommissionUsd = newUsdComm,
                            agentCommissionCdf = newCdfComm,
                            agentCommissionEur = newEurComm,
                            agentOperationsHistory = listOf(op) + agentOperationsHistory) }
                    fetchWallet(effectiveUserId)
                }.onFailure { err ->
                    updateAgent { copy(isAgentWithdrawLoading = false,
                            agentWithdrawError = err.message ?: "Code OTP invalide ou expiré.") }
                }
        }
    }

    // --- AGENT LOAN REPAYMENT ---
    fun setAgentLoanClientRef(ref: String) {
        updateAgent { copy(agentLoanClientRef = ref, agentLoanFee=null,agentLoanTotalDebit=null,agentLoanSearchError = null, agentLoanTarget = null) }
    }

    fun searchAgentLoanTarget() {
        val ref=_uiState.value.agentLoanClientRef.trim();if(ref.isBlank())return
        updateAgent{copy(isAgentSearchingLoan=true,agentLoanTarget=null,agentLoanFee=null,agentLoanTotalDebit=null,agentLoanSearchError=null)}
        viewModelScope.launch {repository.getAgentLoanTarget(ref).onSuccess{response->
            updateAgent{copy(isAgentSearchingLoan=false,agentLoanTarget=response,agentLoanAmount=response.loan?.remainingBalance?.toString() ?: "",agentLoanCurrency=response.loan?.currency ?: "",agentLoanStep=2)}
        }.onFailure{error->updateAgent{copy(isAgentSearchingLoan=false,agentLoanSearchError=error.message)}}}
    }

    fun proceedToLoanPin() {
        val state=_uiState.value;val amount=state.agentLoanAmount.toDoubleOrNull()
        if(amount==null || !amount.isFinite() || amount<=0) { updateAgent { copy(agentLoanRepayError="Montant invalide") };return }
        updateAgent { copy(isAgentLoanRepayLoading=true,agentLoanFee=null,agentLoanTotalDebit=null,agentLoanRepayError=null) }
        viewModelScope.launch { repository.reviewAgentLoanRepayment(state.agentLoanClientRef.trim(),state.agentLoanCurrency,amount).onSuccess { quote ->
            updateAgent { copy(isAgentLoanRepayLoading=false,agentLoanFee=quote.fee,agentLoanTotalDebit=quote.totalDebit,agentLoanStep=3) }
        }.onFailure { error -> updateAgent { copy(isAgentLoanRepayLoading=false,agentLoanRepayError=error.message) } } }
    }

    fun setAgentLoanAmount(amount: String) {
        updateAgent { copy(agentLoanAmount = amount, agentLoanFee=null,agentLoanTotalDebit=null,agentLoanPin="", agentLoanRepayError = null) }
    }

    fun setAgentLoanCurrency(currency: String) {
        updateAgent { copy(agentLoanCurrency = currency,agentLoanFee=null,agentLoanTotalDebit=null,agentLoanPin="", agentLoanRepayError = null) }
    }

    fun setAgentLoanPin(pin: String) {
        updateAgent { copy(agentLoanPin = pin, agentLoanRepayError = null) }
    }

    fun resetAgentLoanRepay() {
        updateAgent { copy(agentLoanStep = 1,
                agentLoanClientRef = "",
                agentLoanTarget = null,
                agentLoanAmount = "",
                agentLoanPin = "",
                agentLoanSearchError = null,
                agentLoanRepayError = null,
                agentLoanRepaySuccess = null,
                isAgentLoanRepayLoading = false) }
    }

    fun submitAgentLoanRepay(userId: Long? = null) {
        if(_uiState.value.isAgentLoanRepayLoading) return
        if(_uiState.value.agentLoanFee==null){proceedToLoanPin();return}
        val effectiveUserId = getEffectiveUserId(userId)
        val state = _uiState.value
        val clientRef = state.agentLoanClientRef.trim()
        val amount = state.agentLoanAmount.toDoubleOrNull() ?: 0.0
        val currency = state.agentLoanCurrency
        val pin = state.agentLoanPin.trim()

        if (clientRef.isBlank()) {
            updateAgent { copy(agentLoanRepayError = "Veuillez spécifier le client.") }
            return
        }
        if (amount <= 0.0) {
            updateAgent { copy(agentLoanRepayError = "Veuillez entrer un montant valide.") }
            return
        }
        if (pin.length != 4) {
            updateAgent { copy(agentLoanRepayError = "Le code PIN agent doit comporter 4 chiffres.") }
            return
        }

        updateAgent { copy(isAgentLoanRepayLoading = true, agentLoanRepayError = null) }

        viewModelScope.launch {
            val result = repository.agentLoanRepay(clientRef, currency, amount, pin)
            result.onSuccess { res ->
                val earnedComm = res.commission ?: 0.0
                val newUsdComm = if (currency == "USD") state.agentCommissionUsd + earnedComm else state.agentCommissionUsd
                val newCdfComm = if (currency == "CDF") state.agentCommissionCdf + earnedComm else state.agentCommissionCdf
                val newEurComm = if (currency == "EUR") state.agentCommissionEur + earnedComm else state.agentCommissionEur

                val op = com.example.data.model.AgentOperationRecord(
                    type = "LOAN_REPAY",
                    title = "Remboursement Prêt Loan Me",
                    clientRef = clientRef,
                    amount = amount,
                    currency = currency,
                    commission = earnedComm,
                    reference = res.reference ?: "Consultez l’historique",
                    date = "Aujourd'hui, ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
                    status = "Complété"
                )

                updateAgent { copy(isAgentLoanRepayLoading = false,
                        agentLoanRepaySuccess = res,
                        agentLoanRepayError = null,
                        agentCommissionUsd = newUsdComm,
                        agentCommissionCdf = newCdfComm,
                        agentCommissionEur = newEurComm,
                        agentOperationsHistory = listOf(op) + agentOperationsHistory) }
                fetchWallet(effectiveUserId)
            }.onFailure { err ->
                updateAgent { copy(isAgentLoanRepayLoading = false,
                        agentLoanRepayError = err.message ?: "Erreur de remboursement du prêt.") }
            }
        }
    }

    // --- EXCHANGE / CONVERSION METHODS (Temps Réel depuis la Base de Données) ---
    fun fetchExchangeRates(countryCode: String? = null) {
        val effectiveCode = countryCode ?: userProfile.value?.country ?: "CD"
        updateExchange { copy(isExchangeRatesLoading = true) }
        viewModelScope.launch {
            val result = repository.getExchangeRates(effectiveCode)
            result.onSuccess { resp ->
                updateExchange {
                    copy(
                        isExchangeRatesLoading = false,
                        exchangeRatesMap = resp.rates ?: emptyMap()
                    )
                }
                fetchRealtimeQuote()
            }.onFailure {
                updateExchange { copy(isExchangeRatesLoading = false) }
            }
        }
    }

    fun fetchRealtimeQuote(amountVal: Double? = null) {
        val state = _uiState.value
        val from = state.exchangeFromCurrency
        val to = state.exchangeToCurrency
        val amt = amountVal ?: state.exchangeAmount.toDoubleOrNull() ?: 1.0
        if (amt <= 0.0) return

        updateExchange { copy(isQuoteLoading = true, quoteError = null) }
        viewModelScope.launch {
            val result = repository.getRealtimeQuote(amt, from, to)
            result.onSuccess { quote ->
                updateExchange {
                    val updatedMap = exchangeRatesMap.toMutableMap()
                    if (quote.rate != null && quote.rate > 0.0) {
                        updatedMap["${from.uppercase()}_${to.uppercase()}"] = quote.rate
                        if (quote.rate > 0.0) {
                            updatedMap["${to.uppercase()}_${from.uppercase()}"] = 1.0 / quote.rate
                        }
                    }
                    copy(
                        isQuoteLoading = false,
                        realtimeQuoteRate = quote.rate,
                        realtimeQuoteAmount = quote.toAmount,
                        exchangeRatesMap = updatedMap,
                        quoteError = null
                    )
                }
            }.onFailure { err ->
                updateExchange { copy(isQuoteLoading = false, quoteError = err.message) }
            }
        }
    }

    fun openExchangeDialog(from: String = "USD", to: String? = null) {
        val natCode = _uiState.value.walletResponse?.nationalCurrency?.code ?: "CDF"
        val targetTo = to ?: if (from == "USD") natCode else "USD"
        updateExchange { copy(isExchangeDialogOpen = true,
                exchangeFromCurrency = from,
                exchangeToCurrency = if (from == targetTo) (if (from == "USD") natCode else "USD") else targetTo,
                exchangeAmount = "",
                exchangePin = "",
                exchangeError = null,
                exchangeSuccessResponse = null,
                realtimeQuoteRate = null,
                realtimeQuoteAmount = null,
                quoteError = null) }
        fetchExchangeRates()
        fetchRealtimeQuote(1.0)
    }

    fun closeExchangeDialog() {
        updateExchange { copy(isExchangeDialogOpen = false,
                exchangeError = null,
                exchangeSuccessResponse = null) }
    }

    fun setExchangeAmount(amount: String) {
        val filtered = amount.filter { it.isDigit() || it == '.' }
        updateExchange { copy(exchangeAmount = filtered, exchangeError = null) }
        val parsed = filtered.toDoubleOrNull()
        if (parsed != null && parsed > 0.0) {
            fetchRealtimeQuote(parsed)
        }
    }

    fun setExchangeFromCurrency(currency: String) {
        val natCode = _uiState.value.walletResponse?.nationalCurrency?.code ?: "CDF"
        updateExchange {
            val to = if (exchangeToCurrency == currency) {
                if (currency == "USD") natCode else "USD"
            } else {
                exchangeToCurrency
            }
            copy(exchangeFromCurrency = currency, exchangeToCurrency = to, exchangeError = null)
        }
        fetchRealtimeQuote()
    }

    fun setExchangeToCurrency(currency: String) {
        val natCode = _uiState.value.walletResponse?.nationalCurrency?.code ?: "CDF"
        updateExchange {
            val from = if (exchangeFromCurrency == currency) {
                if (currency == "USD") natCode else "USD"
            } else {
                exchangeFromCurrency
            }
            copy(exchangeToCurrency = currency, exchangeFromCurrency = from, exchangeError = null)
        }
        fetchRealtimeQuote()
    }

    fun swapExchangeCurrencies() {
        updateExchange { copy(exchangeFromCurrency = exchangeToCurrency,
                exchangeToCurrency = exchangeFromCurrency,
                exchangeError = null) }
        fetchRealtimeQuote()
    }

    fun setExchangePin(pin: String) {
        val filtered = pin.filter { it.isDigit() }.take(6)
        updateExchange { copy(exchangePin = filtered, exchangeError = null) }
    }

    fun resetExchangeSuccess() {
        updateExchange { copy(exchangeSuccessResponse = null, exchangeAmount = "", exchangePin = "") }
    }

    fun submitExchange() {
        val state = _uiState.value
        val amount = state.exchangeAmount.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) {
            updateExchange { copy(exchangeError = "Veuillez saisir un montant valide supérieur à 0.") }
            return
        }
        if (state.exchangeFromCurrency == state.exchangeToCurrency) {
            updateExchange { copy(exchangeError = "Les devises source et de destination doivent être différentes.") }
            return
        }
        if (state.exchangePin.isBlank()) {
            updateExchange { copy(exchangeError = "Veuillez saisir votre code PIN CashPay.") }
            return
        }

        updateExchange { copy(isExchangeLoading = true, exchangeError = null) }

        viewModelScope.launch {
            val result = repository.exchangeCurrencies(
                amount = amount,
                fromCurrency = state.exchangeFromCurrency,
                toCurrency = state.exchangeToCurrency,
                pin = state.exchangePin
            )
            result.onSuccess { response ->
                updateExchange { copy(isExchangeLoading = false,
                        exchangeSuccessResponse = response,
                        exchangeError = null) }
                // Refresh balance and transactions automatically
                val effectiveUserId = getEffectiveUserId()
                fetchWallet(effectiveUserId)
                fetchUsdtBalance()
                loadTransactions(effectiveUserId)
            }.onFailure { err ->
                updateExchange { copy(isExchangeLoading = false,
                        exchangeError = err.message ?: "Échec de l'opération de change.") }
            }
        }
    }

    // --- INTERNATIONAL TRANSFER CLAIMS (Section Client) ---
    fun openClaimsScreen() {
        updateClaims { copy(isClaimsScreenOpen = true, claimsError = null) }
        loadInternationalClaims()
    }

    fun closeClaimsScreen() {
        updateClaims { copy(isClaimsScreenOpen = false) }
    }

    fun loadInternationalClaims() {
        val userId = getEffectiveUserId()
        updateClaims { copy(isClaimsLoading = true, claimsError = null) }
        viewModelScope.launch {
            val result = repository.getInternationalClaims()
            if (getEffectiveUserId() != userId) return@launch
            result.onSuccess { resp ->
                if (resp.success) updateClaims { copy(isClaimsLoading = false, claimsList = resp.claims, claimsError = null) }
                else updateClaims { copy(isClaimsLoading = false, claimsError = resp.error ?: "Liste des réclamations indisponible.") }
            }.onFailure { err ->
                updateClaims { copy(isClaimsLoading = false, claimsError = err.message ?: "Impossible de récupérer vos réclamations.") }
            }
        }
    }

    fun openNewClaimDialog() {
        updateClaims { copy(isNewClaimDialogOpen = true, newClaimError = null, newClaimSuccessMessage = null, newClaimDuplicateClaim = null) }
    }

    fun closeNewClaimDialog() {
        updateClaims { copy(isNewClaimDialogOpen = false, newClaimError = null, newClaimSuccessMessage = null, newClaimDuplicateClaim = null) }
    }

    fun submitInternationalClaim(
        provider: String,
        trackingNumber: String,
        expectedAmount: Double,
        expectedCurrency: String,
        senderCountry: String,
        receiveInCurrency: String
    ) {
        if (provider.isBlank() || trackingNumber.isBlank() || !expectedAmount.isFinite() || expectedAmount <= 0.0 ||
            expectedCurrency.isBlank() || senderCountry.isBlank() || receiveInCurrency.isBlank()) {
            updateClaims { copy(newClaimError = "Veuillez remplir tous les champs obligatoires.") }
            return
        }

        updateClaims { copy(isSubmittingClaim = true, newClaimError = null, newClaimSuccessMessage = null, newClaimDuplicateClaim = null) }
        viewModelScope.launch {
            val req = com.example.data.model.CreateClaimRequest(
                provider = provider.lowercase().trim(),
                trackingNumber = trackingNumber.trim(),
                expectedAmount = expectedAmount,
                expectedCurrency = expectedCurrency.uppercase().trim(),
                senderCountry = senderCountry.uppercase().trim(),
                receiveInCurrency = receiveInCurrency.uppercase().trim()
            )
            val result = repository.createInternationalClaim(req)
            result.onSuccess { resp ->
                if (resp.success) {
                    updateClaims {
                        copy(
                            isSubmittingClaim = false,
                            newClaimSuccessMessage = resp.message ?: "Votre réclamation a été soumise avec succès.",
                            newClaimError = null
                        )
                    }
                    loadInternationalClaims()
                } else {
                    updateClaims {
                        copy(
                            isSubmittingClaim = false,
                            newClaimError = resp.error ?: "Cette réclamation existe déjà.",
                            newClaimDuplicateClaim = resp.claim
                        )
                    }
                }
            }.onFailure { err ->
                updateClaims {
                    copy(
                        isSubmittingClaim = false,
                        newClaimError = err.message ?: "Échec de la soumission de la réclamation."
                    )
                }
            }
        }
    }

    private var claimDetailVersion = 0
    fun openClaimDetail(claim: com.example.data.model.InternationalTransferClaimDto) {
        val version = ++claimDetailVersion
        val userId = getEffectiveUserId()
        updateClaims { copy(selectedClaimDetail = claim, isClaimDetailDialogOpen = true, isClaimDetailLoading = true, claimDetailError = null) }
        viewModelScope.launch {
            val result = repository.getInternationalClaimDetail(claim.id)
            if (version != claimDetailVersion || getEffectiveUserId() != userId) return@launch
            result.onSuccess { response ->
                if (response.success && response.claim != null) {
                    updateClaims { copy(selectedClaimDetail = response.claim, isClaimDetailLoading = false, claimDetailError = null) }
                } else {
                    updateClaims { copy(isClaimDetailLoading = false, claimDetailError = response.error ?: "Détail de réclamation indisponible.") }
                }
            }.onFailure { error ->
                updateClaims { copy(isClaimDetailLoading = false, claimDetailError = error.message ?: "Impossible de vérifier le statut actuel. Réessayez.") }
            }
        }
    }

    fun closeClaimDetail() {
        claimDetailVersion++
        updateClaims { copy(isClaimDetailDialogOpen = false, selectedClaimDetail = null, claimDetailError = null) }
    }

    // --- REFERRALS & REWARDS (Programme de Parrainage) ---
    fun openReferralDialog() {
        updateReferrals { copy(isReferralDialogOpen = true, claimSuccessMessage = null, claimErrorMessage = null) }
        fetchReferrals()
    }

    fun closeReferralDialog() {
        updateReferrals { copy(isReferralDialogOpen = false, claimSuccessMessage = null, claimErrorMessage = null) }
    }

    fun fetchReferrals() {
        updateReferrals { copy(isLoadingReferrals = true, referralsError = null) }
        viewModelScope.launch {
            val result = repository.getReferrals()
            result.onSuccess { resp ->
                val walletNatCode = _uiState.value.walletResponse?.nationalCurrency?.code
                val userCountry = userProfile.value?.country?.lowercase() ?: ""
                val refCode = resp.referral?.code ?: ""

                val effectiveNatCode = when {
                    !resp.stats?.currency.isNullOrBlank() && !resp.stats?.currency.equals("USD", ignoreCase = true) && !resp.stats?.currency.equals("EUR", ignoreCase = true) -> resp.stats!!.currency!!.uppercase()
                    !resp.currency.isNullOrBlank() && !resp.currency.equals("USD", ignoreCase = true) && !resp.currency.equals("EUR", ignoreCase = true) -> resp.currency!!.uppercase()
                    walletNatCode != null && !walletNatCode.equals("USD", ignoreCase = true) && !walletNatCode.equals("EUR", ignoreCase = true) -> walletNatCode.uppercase()
                    refCode.startsWith("CD", ignoreCase = true) || userCountry.contains("congo") || userCountry.contains("rdc") -> "CDF"
                    userCountry.contains("ivoire") || userCountry.contains("senegal") || userCountry.contains("benin") || userCountry.contains("togo") || userCountry.contains("mali") || userCountry.contains("burkina") -> "XOF"
                    userCountry.contains("cameroun") || userCountry.contains("gabon") || userCountry.contains("tchad") || userCountry.contains("centrafrique") -> "XAF"
                    userCountry.contains("guinée") || userCountry.contains("guinea") -> "GNF"
                    userCountry.contains("rwanda") -> "RWF"
                    userCountry.contains("kenya") -> "KES"
                    else -> "CDF"
                }

                val effectiveNatSymbol = when (effectiveNatCode) {
                    "CDF" -> "FC"
                    "XOF", "XAF" -> "CFA"
                    "GNF" -> "FG"
                    "RWF" -> "FRw"
                    "KES" -> "KSh"
                    else -> effectiveNatCode
                }

                val enrichedStats = resp.stats?.copy(
                    currency = effectiveNatCode,
                    currencySymbol = effectiveNatSymbol
                ) ?: resp.stats

                val enrichedResp = resp.copy(
                    currency = effectiveNatCode,
                    currencySymbol = effectiveNatSymbol,
                    stats = enrichedStats
                )

                updateReferrals { copy(isLoadingReferrals = false, referralResponse = enrichedResp, referralsError = null) }
            }.onFailure { err ->
                updateReferrals { copy(isLoadingReferrals = false, referralsError = err.message ?: "Impossible de récupérer vos parrainages.") }
            }
        }
    }

    fun claimReferralReward(referralId: Long) {
        val state = _uiState.value
        val pointRate = state.referralResponse?.stats?.pointConversionRate ?: 0.0
        val isConfigured = (pointRate > 0.0 || state.referralResponse?.isConfigured == true || state.referralResponse?.stats?.isConfigured == true)
        if (!isConfigured) {
            updateReferrals {
                copy(
                    claimErrorMessage = "La récompense financière en devise nationale n'est pas encore configurée pour votre pays dans les paramètres de parrainage."
                )
            }
            return
        }

        updateReferrals { copy(isClaimingReward = true, claimingReferralId = referralId, claimSuccessMessage = null, claimErrorMessage = null) }
        viewModelScope.launch {
            val result = repository.claimReferralReward(referralId)
            result.onSuccess { resp ->
                updateReferrals {
                    copy(
                        isClaimingReward = false,
                        claimingReferralId = null,
                        claimSuccessMessage = resp.message ?: "Récompense de parrainage créditée avec succès !"
                    )
                }
                fetchReferrals()
                fetchWallet()
            }.onFailure { err ->
                updateReferrals {
                    copy(
                        isClaimingReward = false,
                        claimingReferralId = null,
                        claimErrorMessage = err.message ?: "Échec de réclamation de la récompense."
                    )
                }
            }
        }
    }

    fun resetReferralMessages() {
        updateReferrals { copy(claimSuccessMessage = null, claimErrorMessage = null) }
    }

    // --- TV SUBSCRIPTIONS (Canal+, EasyTV, BLUS) ---
    fun openTvSubscriptionDialog(defaultProvider: String = "CPLUS") {
        updateTv {
            copy(
                isTvDialogOpen = true,
                selectedProvider = defaultProvider,
                pin = "",
                submitError = null,
                subscriptionResult = null
            )
        }
        loadTvPackages(defaultProvider)
    }

    fun closeTvSubscriptionDialog() {
        updateTv { copy(isTvDialogOpen = false, pin = "", submitError = null) }
    }

    fun selectTvProvider(provider: String) {
        updateTv {
            copy(
                selectedProvider = provider,
                selectedPackage = null,
                pin = "",
                submitError = null,
                packages = emptyList()
            )
        }
        loadTvPackages(provider)
    }

    fun loadTvPackages(provider: String) {
        updateTv { copy(isLoadingPackages = true, packagesError = null) }
        viewModelScope.launch {
            val result = repository.getTvPackages(provider)
            result.onSuccess { pkgs ->
                updateTv {
                    copy(
                        isLoadingPackages = false,
                        packages = pkgs,
                        selectedPackage = pkgs.firstOrNull(),
                        packagesError = null
                    )
                }
                calculateTvDebitQuote()
            }.onFailure { err ->
                updateTv {
                    copy(
                        isLoadingPackages = false,
                        packagesError = err.message ?: "Impossible de récupérer les forfaits TV."
                    )
                }
            }
        }
    }

    fun selectTvPackage(pkg: com.example.data.model.TvPackageDto) {
        updateTv { copy(selectedPackage = pkg, submitError = null) }
        calculateTvDebitQuote()
    }

    fun setTvSmartcardNumber(number: String) {
        updateTv { copy(smartcardNumber = number.trim().take(30), submitError = null) }
    }

    fun setTvPaymentCurrency(currency: String) {
        updateTv { copy(paymentCurrency = currency.uppercase(), submitError = null) }
        calculateTvDebitQuote()
    }

    fun setTvPin(pin: String) {
        val filtered = pin.filter { it.isDigit() }.take(6)
        updateTv { copy(pin = filtered, submitError = null) }
    }

    fun calculateTvDebitQuote() {
        val state = _uiState.value.tv
        val pkg = state.selectedPackage ?: return
        val pkgPrice = pkg.effectivePrice
        val currency = state.paymentCurrency.uppercase()

        if (currency == "CDF") {
            updateTv {
                copy(
                    quoteRate = 1.0,
                    estimatedDebitAmount = pkgPrice,
                    quoteError = null,
                    isCalculatingQuote = false
                )
            }
            return
        }

        updateTv { copy(isCalculatingQuote = true, quoteError = null) }
        viewModelScope.launch {
            val result = repository.getRealtimeQuote(amount = pkgPrice, fromCurrency = "CDF", toCurrency = currency)
            result.onSuccess { quote ->
                updateTv {
                    copy(
                        quoteRate = quote.rate,
                        estimatedDebitAmount = quote.toAmount,
                        isCalculatingQuote = false,
                        quoteError = null
                    )
                }
            }.onFailure { err ->
                updateTv {
                    copy(
                        quoteError = "Taux de change CDF vers $currency non configuré pour la RDC.",
                        estimatedDebitAmount = null,
                        quoteRate = null,
                        isCalculatingQuote = false
                    )
                }
            }
        }
    }

    fun submitTvSubscription() {
        val tvState = _uiState.value.tv
        val pkg = tvState.selectedPackage
        if (pkg == null) {
            updateTv { copy(submitError = "Veuillez sélectionner un forfait.") }
            return
        }
        if (tvState.smartcardNumber.isBlank()) {
            updateTv { copy(submitError = "Veuillez saisir votre numéro de carte / décodeur.") }
            return
        }
        if (tvState.pin.isBlank() || tvState.pin.length < 4) {
            updateTv { copy(submitError = "Veuillez saisir votre code PIN CashPay valide.") }
            return
        }
        if (tvState.quoteError != null) {
            updateTv { copy(submitError = tvState.quoteError) }
            return
        }

        updateTv { copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            val result = repository.submitTvSubscription(
                provider = tvState.selectedProvider,
                packageId = pkg.id,
                smartcardNumber = tvState.smartcardNumber,
                currency = tvState.paymentCurrency,
                pin = tvState.pin
            )
            result.onSuccess { resp ->
                val updatedRecent = (listOf(resp) + tvState.recentSubscriptions).distinctBy { it.reference ?: it.transactionId }
                updateTv {
                    copy(
                        isSubmitting = false,
                        pin = "",
                        subscriptionResult = resp,
                        submitError = null,
                        recentSubscriptions = updatedRecent
                    )
                }
                fetchWallet()
                loadTransactions()
            }.onFailure { err ->
                updateTv {
                    copy(
                        isSubmitting = false,
                        submitError = err.message ?: "Échec de la demande d'abonnement TV."
                    )
                }
            }
        }
    }

    fun resetTvSubscriptionResult() {
        updateTv { copy(subscriptionResult = null, pin = "", submitError = null) }
    }

    // --- DEPOSIT METHODS (MaxiCash Gateway) ---
    fun openDepositDialog(method: String = "mobile_money") {
        val prof = userProfile.value
        val userCountry = prof?.country ?: "CD"
        val operators = com.example.data.model.MobileMoneyOperator.getOperatorsForCountry(userCountry)
        val defaultOp = operators.firstOrNull()?.code ?: "mpesa"
        val userPhone = prof?.phone ?: ""

        updateDeposit {
            copy(
                isDepositDialogOpen = true,
                depositMethod = method,
                depositAmount = "",
                depositCurrency = "USD",
                depositOperator = defaultOp,
                depositPhoneNumber = if (depositPhoneNumber.isNotBlank()) depositPhoneNumber else userPhone,
                depositCountryCode = userCountry,
                isDepositLoading = false,
                depositError = null,
                depositSuccessMessage = null,
                depositPendingReference = null,
                depositPaymentUrl = null,
                isDepositWebViewOpen = false
            )
        }
    }

    fun closeDepositDialog() {
        updateDeposit { copy(isDepositDialogOpen = false,
                depositError = null,
                depositSuccessMessage = null,
                depositPendingReference = null,
                depositPaymentUrl = null) }
    }

    fun setDepositMethod(method: String) {
        updateDeposit { copy(depositMethod = method, depositError = null) }
    }

    fun setDepositAmount(amount: String) {
        updateDeposit { copy(depositAmount = amount, depositError = null) }
    }

    fun setDepositOperator(operator: String) {
        updateDeposit { copy(depositOperator = operator, depositError = null) }
    }

    fun setDepositPhoneNumber(phone: String) {
        updateDeposit { copy(depositPhoneNumber = phone, depositError = null) }
    }

    fun setDepositCountryCode(code: String) {
        updateDeposit { copy(depositCountryCode = code) }
    }

    fun openDepositWebView(url: String) {
        updateDeposit { copy(depositPaymentUrl = url, isDepositWebViewOpen = true) }
    }

    fun closeDepositWebView() {
        updateDeposit { copy(isDepositWebViewOpen = false, depositPaymentUrl = null) }
        val effectiveUserId = getEffectiveUserId()
        fetchWallet(effectiveUserId)
        loadTransactions(effectiveUserId)
    }

    fun submitDeposit() {
        val state = _uiState.value
        val amount = state.depositAmount.trim().toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            updateDeposit { copy(depositError = "Veuillez entrer un montant valide supérieur à 0.") }
            return
        }

        updateDeposit { copy(isDepositLoading = true,
                depositError = null,
                depositSuccessMessage = null,
                depositPendingReference = null) }

        viewModelScope.launch {
            when (state.depositMethod) {
                "mobile_money" -> {
                    if (state.depositPhoneNumber.isBlank()) {
                        updateDeposit { copy(isDepositLoading = false,
                                depositError = "Veuillez saisir votre numéro de téléphone Mobile Money.") }
                        return@launch
                    }
                    val result = repository.depositMobileMoney(
                        amount = amount,
                        currency = state.depositCurrency,
                        operator = state.depositOperator,
                        phoneNumber = state.depositPhoneNumber.trim(),
                        countryCode = state.depositCountryCode
                    )
                    result.onSuccess { resp ->
                        updateDeposit { copy(isDepositLoading = false,
                                depositPendingReference = resp.reference,
                                depositSuccessMessage = resp.message ?: "Demande de dépôt envoyée. Veuillez confirmer le paiement sur votre téléphone.",
                                depositError = null) }
                        val effectiveUserId = getEffectiveUserId()
                        fetchWallet(effectiveUserId)
                        loadTransactions(effectiveUserId)
                    }.onFailure { err ->
                        updateDeposit { copy(isDepositLoading = false,
                                depositError = err.message ?: "Échec de la demande de dépôt Mobile Money.") }
                    }
                }
                "card" -> {
                    val result = repository.depositCard(
                        amount = amount,
                        currency = state.depositCurrency
                    )
                    result.onSuccess { resp ->
                        updateDeposit { copy(isDepositLoading = false,
                                depositPendingReference = resp.reference,
                                depositPaymentUrl = resp.paymentUrl,
                                isDepositWebViewOpen = !resp.paymentUrl.isNullOrBlank(),
                                depositError = null) }
                    }.onFailure { err ->
                        updateDeposit { copy(isDepositLoading = false,
                                depositError = err.message ?: "Impossible d'initialiser le paiement par carte.") }
                    }
                }
                "paypal" -> {
                    val result = repository.depositPayPal(
                        amount = amount,
                        currency = state.depositCurrency
                    )
                    result.onSuccess { resp ->
                        updateDeposit { copy(isDepositLoading = false,
                                depositPendingReference = resp.reference,
                                depositPaymentUrl = resp.paymentUrl,
                                isDepositWebViewOpen = !resp.paymentUrl.isNullOrBlank(),
                                depositError = null) }
                    }.onFailure { err ->
                        updateDeposit { copy(isDepositLoading = false,
                                depositError = err.message ?: "Impossible d'initialiser le paiement PayPal.") }
                    }
                }
            }
        }
    }

    // --- AGENT EXTERNAL MOBILE MONEY WITHDRAWAL / PULL ---
    fun openAgentExternalMoMoDialog() {
        updateAgent { copy(isAgentExternalMoMoOpen = true,
                agentExternalMoMoOperator = "mpesa",
                agentExternalMoMoPhone = "",
                agentExternalMoMoAmount = "",
                isAgentExternalMoMoLoading = false,
                agentExternalMoMoError = null,
                agentExternalMoMoSuccess = null,
                agentExternalMoMoReference = null) }
    }

    fun closeAgentExternalMoMoDialog() {
        updateAgent { copy(isAgentExternalMoMoOpen = false,
                agentExternalMoMoError = null,
                agentExternalMoMoSuccess = null) }
    }

    fun setAgentExternalMoMoOperator(op: String) {
        updateAgent { copy(agentExternalMoMoOperator = op, agentExternalMoMoError = null) }
    }

    fun setAgentExternalMoMoPhone(phone: String) {
        updateAgent { copy(agentExternalMoMoPhone = phone, agentExternalMoMoError = null) }
    }

    fun setAgentExternalMoMoAmount(amt: String) {
        updateAgent { copy(agentExternalMoMoAmount = amt, agentExternalMoMoError = null) }
    }

    fun submitAgentExternalMoMo() {
        val state = _uiState.value
        val amount = state.agentExternalMoMoAmount.trim().toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            updateAgent { copy(agentExternalMoMoError = "Veuillez saisir un montant valide.") }
            return
        }
        if (state.agentExternalMoMoPhone.isBlank()) {
            updateAgent { copy(agentExternalMoMoError = "Veuillez saisir le numéro Mobile Money du client.") }
            return
        }

        updateAgent { copy(isAgentExternalMoMoLoading = true,
                agentExternalMoMoError = null,
                agentExternalMoMoSuccess = null) }

        viewModelScope.launch {
            val result = repository.depositMobileMoney(
                amount = amount,
                currency = "USD",
                operator = state.agentExternalMoMoOperator,
                phoneNumber = state.agentExternalMoMoPhone.trim(),
                countryCode = "CD"
            )
            result.onSuccess { resp ->
                updateAgent { copy(isAgentExternalMoMoLoading = false,
                        agentExternalMoMoReference = resp.reference,
                        agentExternalMoMoSuccess = "Demande envoyée au client (${resp.reference ?: "DP"}). Dès validation par le client sur son téléphone, vos fonds seront crédités et vous pourrez remettre les espèces.",
                        agentExternalMoMoError = null) }
                val effectiveUserId = getEffectiveUserId()
                fetchWallet(effectiveUserId)
                loadTransactions(effectiveUserId)
            }.onFailure { err ->
                updateAgent { copy(isAgentExternalMoMoLoading = false,
                        agentExternalMoMoError = err.message ?: "Échec de l'initiation du retrait Mobile Money.") }
            }
        }
    }

    // --- REGIDESO PUBLIC INVOICES ---
    fun openRegidesoDialog(defaultProvince: String = "KIN") {
        updateRegideso {
            copy(
                isDialogOpen = true,
                selectedProvince = defaultProvince,
                errorMessage = null
            )
        }
    }

    fun closeRegidesoDialog() {
        updateRegideso { copy(isDialogOpen = false, errorMessage = null) }
    }

    fun setRegidesoAbonnementId(id: String) {
        updateRegideso { copy(abonnementId = id.trim(), errorMessage = null) }
    }

    fun setRegidesoProvince(province: String) {
        updateRegideso { copy(selectedProvince = province.uppercase(), errorMessage = null) }
    }

    fun fetchRegidesoInvoices() {
        val state = _uiState.value.regideso
        updateRegideso { copy(isLoading = true, errorMessage = null, hasSearched = true) }
        viewModelScope.launch {
            val result = repository.getRegidesoInvoices(
                abonnementId = state.abonnementId.takeIf { it.isNotBlank() },
                province = state.selectedProvince
            )
            result.onSuccess { factures ->
                updateRegideso {
                    copy(
                        isLoading = false,
                        factures = factures,
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                updateRegideso {
                    copy(
                        isLoading = false,
                        errorMessage = err.message ?: "Impossible de récupérer les factures REGIDESO."
                    )
                }
            }
        }
    }

    // =========================================================================
    // --- FINUSECO / CASHPAY CPK FINANCIAL KEYS ENGINE (READER & CLOUD) ---
    // =========================================================================

    private var cloudSyncManager: com.example.util.CpkCloudSyncManager? = null

    fun initCloudSyncManager(context: android.content.Context) {
        if (cloudSyncManager == null) {
            val manager = com.example.util.CpkCloudSyncManager(context.applicationContext, repository)
            cloudSyncManager = manager
            updateCpk {
                copy(
                    isDriveConnected = manager.isDriveConnected(),
                    connectedDriveEmail = manager.getConnectedAccount(),
                    lastCloudSyncTime = manager.getLastSyncTimeString()
                )
            }
        }
    }

    fun openCpkReader() {
        updateCpk { copy(isCpkDialogOpen = true, errorMessage = null, successMessage = null) }
        refreshCpkKeysFromServer()
    }

    fun closeCpkReader() {
        updateCpk { copy(isCpkDialogOpen = false, errorMessage = null, successMessage = null) }
    }

    fun setCpkSearchQuery(query: String) {
        updateCpk { copy(searchQuery = query) }
    }

    fun setCpkFilter(filter: String) {
        updateCpk { copy(selectedFilter = filter) }
    }

    fun selectCpkKeyForDetail(key: com.example.data.local.CpkKeyEntity) {
        updateCpk { copy(selectedKeyForDetail = key) }
    }

    fun clearCpkKeyDetail() {
        updateCpk { copy(selectedKeyForDetail = null) }
    }

    fun clearCpkMessages() {
        updateCpk { copy(errorMessage = null, successMessage = null) }
    }

    fun refreshCpkKeysFromServer() {
        updateCpk { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.syncServerCpkKeys()
            result.onSuccess {
                updateCpk { copy(isLoading = false) }
            }.onFailure { err ->
                updateCpk { copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    fun checkCpkKeyStatus(signature: String) {
        updateCpk { copy(isCheckingStatus = true, checkingSignature = signature, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.checkCpkKeyStatus(signature)
            result.onSuccess { res ->
                updateCpk {
                    copy(
                        isCheckingStatus = false,
                        checkingSignature = null,
                        successMessage = "Statut actualisé en direct : ${res.status?.uppercase() ?: "ACTIF"}"
                    )
                }
            }.onFailure { err ->
                updateCpk {
                    copy(
                        isCheckingStatus = false,
                        checkingSignature = null,
                        errorMessage = err.message ?: "Impossible de vérifier le statut."
                    )
                }
            }
        }
    }

    fun interrogateUsbDevices(context: android.content.Context) {
        val usbManager = context.getSystemService(android.content.Context.USB_SERVICE) as? android.hardware.usb.UsbManager
        val deviceList = usbManager?.deviceList ?: emptyMap()
        val deviceCount = deviceList.size

        updateCpk { copy(isLoading = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            val keys = repository.cpkKeys.firstOrNull() ?: emptyList()
            if (deviceCount > 0) {
                val devNames = deviceList.values.joinToString(", ") { it.productName ?: it.deviceName }
                for (k in keys) {
                    repository.checkCpkKeyStatus(k.signature)
                }
                updateCpk {
                    copy(
                        isLoading = false,
                        successMessage = "Périphérique USB détecté ($devNames) ! État interrogé en temps réel auprès de l'API CashPay."
                    )
                }
            } else {
                for (k in keys) {
                    repository.checkCpkKeyStatus(k.signature)
                }
                updateCpk {
                    copy(
                        isLoading = false,
                        successMessage = if (keys.isNotEmpty())
                            "État des ${keys.size} clés USB/CPK interrogé en temps réel avec succès auprès de l'API FINUSECO."
                        else
                            "Prêt pour clé USB OTG : branchez votre clé USB ou importez un fichier .cpk pour interroger son état en direct."
                    )
                }
            }
        }
    }

    fun importCpkContent(rawContent: String, sourceFileName: String?) {
        updateCpk { copy(isLoading = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            val result = repository.importCpkContent(rawContent, sourceFileName)
            result.onSuccess { key ->
                updateCpk {
                    copy(
                        isLoading = false,
                        successMessage = "Fichier CPK importé : ${key.reference ?: key.signature.take(8)}. Interrogation de l'état en direct..."
                    )
                }
                // Interrogate server in real-time immediately
                checkCpkKeyStatus(key.signature)
            }.onFailure { err ->
                updateCpk {
                    copy(
                        isLoading = false,
                        errorMessage = err.message ?: "Échec de l'importation de la clé CPK."
                    )
                }
            }
        }
    }

    // --- CPK ISSUANCE ---
    fun openCpkIssueModal() {
        updateCpk {
            copy(
                isIssueModalOpen = true,
                issueAmount = "",
                issueCurrency = "USD",
                issueSecurityLevel = "none",
                issueKeyPin = "",
                issueUserPin = "",
                issueError = null,
                issueSuccessResult = null
            )
        }
    }

    fun closeCpkIssueModal() {
        updateCpk { copy(isIssueModalOpen = false, issueError = null, issueSuccessResult = null) }
    }

    fun setCpkIssueAmount(v: String) = updateCpk { copy(issueAmount = v.filter { it.isDigit() || it == '.' }) }
    fun setCpkIssueCurrency(v: String) = updateCpk { copy(issueCurrency = v) }
    fun setCpkIssueSecurityLevel(v: String) = updateCpk { copy(issueSecurityLevel = v) }
    fun setCpkIssueKeyPin(v: String) = updateCpk { copy(issueKeyPin = v.filter { it.isDigit() }.take(6)) }
    fun setCpkIssueUserPin(v: String) = updateCpk { copy(issueUserPin = v.filter { it.isDigit() }.take(4)) }

    fun submitCpkIssue() {
        val state = _uiState.value.cpk
        val amt = state.issueAmount.toDoubleOrNull()
        if (amt == null || amt <= 0.0) {
            updateCpk { copy(issueError = "Veuillez saisir un montant valide.") }
            return
        }
        if (state.issueSecurityLevel == "pin_protected" && state.issueKeyPin.length < 4) {
            updateCpk { copy(issueError = "Veuillez définir un code PIN de clé d'au moins 4 chiffres.") }
            return
        }
        if (state.issueUserPin.length < 4) {
            updateCpk { copy(issueError = "Veuillez saisir votre code PIN CashPay de validation.") }
            return
        }

        updateCpk { copy(isIssuing = true, issueError = null) }
        viewModelScope.launch {
            val result = repository.issueCpkKey(
                amount = amt,
                currency = state.issueCurrency,
                securityLevel = state.issueSecurityLevel,
                keyPin = state.issueKeyPin.takeIf { state.issueSecurityLevel == "pin_protected" },
                userPin = state.issueUserPin
            )
            result.onSuccess { res ->
                updateCpk {
                    copy(
                        isIssuing = false,
                        issueSuccessResult = res,
                        issueUserPin = "",
                        issueKeyPin = "",
                        successMessage = "Clé CPK créée avec succès !"
                    )
                }
                fetchWallet()
                loadTransactions()
            }.onFailure { err ->
                updateCpk {
                    copy(
                        isIssuing = false,
                        issueError = err.message ?: "Échec de l'émission de la clé CPK."
                    )
                }
            }
        }
    }

    // --- CPK RECOVERY (ENCAISSEMENT) ---
    fun openCpkRecoverModal(key: com.example.data.local.CpkKeyEntity) {
        updateCpk {
            copy(
                isRecoverModalOpen = true,
                recoverKey = key,
                recoverKeyPin = "",
                recoverPartialAmount = "",
                recoverError = null,
                recoverSuccessResult = null
            )
        }
    }

    fun closeCpkRecoverModal() {
        updateCpk { copy(isRecoverModalOpen = false, recoverError = null, recoverSuccessResult = null) }
    }

    fun setCpkRecoverKeyPin(v: String) = updateCpk { copy(recoverKeyPin = v.filter { it.isDigit() }.take(6)) }
    fun setCpkRecoverPartialAmount(v: String) = updateCpk { copy(recoverPartialAmount = v.filter { it.isDigit() || it == '.' }) }

    fun submitCpkRecover() {
        val state = _uiState.value.cpk
        val key = state.recoverKey ?: return
        val partial = state.recoverPartialAmount.toDoubleOrNull()

        updateCpk { copy(isRecovering = true, recoverError = null) }
        viewModelScope.launch {
            val result = repository.recoverCpkKey(
                signature = key.signature,
                keyPin = state.recoverKeyPin.takeIf { it.isNotBlank() },
                partialAmount = partial
            )
            result.onSuccess { res ->
                updateCpk {
                    copy(
                        isRecovering = false,
                        recoverSuccessResult = res,
                        successMessage = "Fonds encaissés avec succès sur votre portefeuille !"
                    )
                }
                fetchWallet()
                loadTransactions()
            }.onFailure { err ->
                updateCpk {
                    copy(
                        isRecovering = false,
                        recoverError = err.message ?: "Échec de la récupération des fonds."
                    )
                }
            }
        }
    }

    // --- CPK CANCELLATION (REMBOURSEMENT) ---
    fun openCpkCancelModal(key: com.example.data.local.CpkKeyEntity) {
        updateCpk {
            copy(
                isCancelModalOpen = true,
                cancelKey = key,
                cancelUserPin = "",
                cancelError = null,
                cancelSuccessResult = null
            )
        }
    }

    fun closeCpkCancelModal() {
        updateCpk { copy(isCancelModalOpen = false, cancelError = null, cancelSuccessResult = null) }
    }

    fun setCpkCancelUserPin(v: String) = updateCpk { copy(cancelUserPin = v.filter { it.isDigit() }.take(4)) }

    fun submitCpkCancel() {
        val state = _uiState.value.cpk
        val key = state.cancelKey ?: return
        if (state.cancelUserPin.length < 4) {
            updateCpk { copy(cancelError = "Veuillez entrer votre PIN CashPay personnel.") }
            return
        }

        updateCpk { copy(isCancelling = true, cancelError = null) }
        viewModelScope.launch {
            val result = repository.cancelCpkKey(
                signature = key.signature,
                userPin = state.cancelUserPin
            )
            result.onSuccess { res ->
                updateCpk {
                    copy(
                        isCancelling = false,
                        cancelSuccessResult = res,
                        cancelUserPin = "",
                        successMessage = "Clé annulée et capital remboursé sur votre portefeuille !"
                    )
                }
                fetchWallet()
                loadTransactions()
            }.onFailure { err ->
                updateCpk {
                    copy(
                        isCancelling = false,
                        cancelError = err.message ?: "Échec de l'annulation de la clé."
                    )
                }
            }
        }
    }

    // --- GOOGLE DRIVE & CLOUD VAULT SYNC ---
    fun openLinkDriveDialog() = updateCpk { copy(isLinkingDriveDialogOpen = true) }
    fun closeLinkDriveDialog() = updateCpk { copy(isLinkingDriveDialogOpen = false) }

    fun connectGoogleDrive(accountEmail: String) {
        val manager = cloudSyncManager
        manager?.setDriveConnected(accountEmail)
        updateCpk {
            copy(
                isDriveConnected = true,
                connectedDriveEmail = accountEmail,
                isLinkingDriveDialogOpen = false,
                lastCloudSyncTime = manager?.getLastSyncTimeString() ?: "À l'instant",
                successMessage = "Google Drive connecté : $accountEmail (Dossier : ${com.example.util.CpkCloudSyncManager.VAULT_FOLDER_NAME})"
            )
        }
        // Auto-sync all unsynced keys to Google Drive
        if (manager != null) {
            viewModelScope.launch {
                val currentKeys = repository.cpkKeys.firstOrNull() ?: emptyList()
                for (k in currentKeys) {
                    if (!k.isCloudSynced) {
                        manager.syncKeyToDrive(k)
                    }
                }
            }
        }
    }

    fun disconnectGoogleDrive() {
        cloudSyncManager?.disconnectDrive()
        updateCpk {
            copy(
                isDriveConnected = false,
                connectedDriveEmail = null,
                successMessage = "Google Drive déconnecté."
            )
        }
    }

    fun syncKeyToCloud(key: com.example.data.local.CpkKeyEntity, context: android.content.Context) {
        initCloudSyncManager(context)
        val manager = cloudSyncManager ?: return

        // Auto-connect with user's account if not explicitly set yet
        if (!manager.isDriveConnected()) {
            val userEmail = userProfile.value?.email?.takeIf { it.isNotBlank() } ?: "client.cashpay@gmail.com"
            manager.setDriveConnected(userEmail)
            updateCpk {
                copy(
                    isDriveConnected = true,
                    connectedDriveEmail = userEmail,
                    lastCloudSyncTime = manager.getLastSyncTimeString()
                )
            }
        }

        updateCpk { copy(isSyncingCloud = true, syncingSignature = key.signature, errorMessage = null) }
        viewModelScope.launch {
            val res = manager.syncKeyToDrive(key)
            res.onSuccess {
                updateCpk {
                    copy(
                        isSyncingCloud = false,
                        syncingSignature = null,
                        lastCloudSyncTime = manager.getLastSyncTimeString(),
                        successMessage = "Clé ${key.reference ?: key.signature.take(8)} transférée dans le Cloud (${com.example.util.CpkCloudSyncManager.VAULT_FOLDER_NAME}) !"
                    )
                }
            }.onFailure { err ->
                updateCpk {
                    copy(
                        isSyncingCloud = false,
                        syncingSignature = null,
                        errorMessage = err.message ?: "Échec de la sauvegarde cloud."
                    )
                }
            }
        }
    }

    fun restoreKeysFromCloud(context: android.content.Context) {
        initCloudSyncManager(context)
        val manager = cloudSyncManager
        if (manager == null || !manager.isDriveConnected()) {
            openLinkDriveDialog()
            return
        }

        updateCpk { copy(isRestoringFromCloud = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            val res = manager.restoreAllFromDrive()
            res.onSuccess { count ->
                updateCpk {
                    copy(
                        isRestoringFromCloud = false,
                        lastCloudSyncTime = manager.getLastSyncTimeString(),
                        successMessage = "$count clé(s) restaurée(s) et synchronisée(s) depuis Google Drive !"
                    )
                }
            }.onFailure { err ->
                updateCpk {
                    copy(
                        isRestoringFromCloud = false,
                        errorMessage = err.message ?: "Échec de la restauration depuis Drive."
                    )
                }
            }
        }
    }

    // --- PAYSCAN COMPREHENSIVE SUITE (ALL USERS) ---

    fun openPayScan(initialMode: com.example.ui.viewmodel.PayScanMode = com.example.ui.viewmodel.PayScanMode.SUPERMARKET_BASKET) {
        updatePayScan {
            copy(
                isPayScanOpen = true,
                currentMode = initialMode,
                scanError = null,
                scanSuccessMessage = null,
                isInterpreting = false,
                isProcessingPayment = false
            )
        }
    }

    fun closePayScan() {
        updatePayScan {
            copy(
                isPayScanOpen = false,
                scanError = null,
                isInterpreting = false
            )
        }
    }

    fun setPayScanMode(mode: com.example.ui.viewmodel.PayScanMode) {
        updatePayScan {
            copy(
                currentMode = mode,
                scanError = null,
                scanSuccessMessage = null
            )
        }
    }

    fun setPayScanCurrency(currency: String) {
        updatePayScan { copy(paymentCurrency = currency.uppercase()) }
    }

    fun toggleBasketDrawer(show: Boolean) {
        updatePayScan { copy(showBasketDrawer = show) }
    }

    fun openConfirmPosPayment(saleData: com.example.data.model.PosSaleData? = null) {
        updatePayScan {
            copy(
                showConfirmPaymentDialog = true,
                currentPosSaleData = saleData ?: buildPosSaleDataFromBasket()
            )
        }
    }

    fun closeConfirmPosPayment() {
        updatePayScan { copy(showConfirmPaymentDialog = false, isProcessingPayment = false) }
    }

    fun closeSuccessReceiptDialog() {
        updatePayScan { copy(showSuccessReceiptDialog = false, completedTransaction = null) }
    }

    fun clearBasket() {
        updatePayScan { copy(basketItems = emptyList(), scanSuccessMessage = "Panier réinitialisé.") }
    }

    fun removeBasketItem(itemId: String) {
        updatePayScan {
            copy(basketItems = basketItems.filter { it.id != itemId })
        }
    }

    fun updateBasketItemQuantity(itemId: String, quantity: Int) {
        updatePayScan {
            if (quantity <= 0) {
                copy(basketItems = basketItems.filter { it.id != itemId })
            } else {
                copy(basketItems = basketItems.map {
                    if (it.id == itemId) it.copy(quantity = quantity) else it
                })
            }
        }
    }

    fun addManualOrBarcodeItem(barcodeOrName: String, price: Double = 0.0, currency: String = "USD") {
        processPayScanInput(barcodeOrName)
    }

    fun buildPosSaleDataFromBasket(): com.example.data.model.PosSaleData {
        val ps = _uiState.value.payscan
        val payCurrency = ps.paymentCurrency
        val exchangeRates = _uiState.value.exchange.exchangeRatesMap
        val sellerWallet = ps.basketItems.firstOrNull { !it.sellerWalletId.isNullOrBlank() }?.sellerWalletId
            ?: ps.defaultSellerWalletId
        val boutique = ps.basketItems.firstOrNull { !it.boutiqueName.isNullOrBlank() }?.boutiqueName
            ?: ps.defaultBoutiqueName

        val items = ps.basketItems.map { cartItem ->
            com.example.data.model.PosSaleItemDto(
                id = cartItem.id,
                name = cartItem.name,
                quantity = cartItem.quantity,
                sellingPrice = cartItem.unitPrice,
                currency = cartItem.currency,
                barcode = cartItem.barcode
            )
        }

        var total = 0.0
        for (item in ps.basketItems) {
            val lineTotal = item.unitPrice * item.quantity
            val converted = convertCurrencyAmount(lineTotal, item.currency, payCurrency, exchangeRates)
            total += converted
        }

        return com.example.data.model.PosSaleData(
            type = "POS_SALE",
            sellerWalletId = sellerWallet,
            amount = Math.round(total * 100.0) / 100.0,
            currency = payCurrency,
            paymentCurrency = payCurrency,
            saleReference = "POS_${System.currentTimeMillis().toString().takeLast(8)}",
            boutiqueName = boutique,
            sellerName = "Vendeur POS",
            items = items
        )
    }

    fun convertCurrencyAmount(amount: Double, from: String, to: String, rates: Map<String, Double>): Double {
        if (from.equals(to, ignoreCase = true)) return amount
        val fromUpper = from.uppercase()
        val toUpper = to.uppercase()

        val directKey = "${fromUpper}_${toUpper}"

        if (rates.containsKey(directKey) && (rates[directKey] ?: 0.0) > 0.0) {
            return amount * rates[directKey]!!
        }

        return Double.NaN // No invented conversion: the backend quote remains authoritative.
    }

    fun calculateBasketTotalInCurrency(targetCurrency: String): Double {
        val items = _uiState.value.payscan.basketItems
        val rates = _uiState.value.exchange.exchangeRatesMap
        var total = 0.0
        for (item in items) {
            total += convertCurrencyAmount(item.unitPrice * item.quantity, item.currency, targetCurrency, rates)
        }
        return Math.round(total * 100.0) / 100.0
    }

    fun processPayScanInput(scannedRaw: String) {
        val cleaned = scannedRaw.trim()
        if (cleaned.isBlank()) { updatePayScan { copy(scanError = "Code vide ou illisible.") }; return }
        updatePayScan { copy(isInterpreting = true, scanError = null, scanSuccessMessage = null) }
        viewModelScope.launch {
            repository.resolveScan(com.example.data.model.ScanRequest(value = cleaned)).onSuccess { response ->
                val result = response.scan
                if (result == null) { updatePayScan { copy(isInterpreting = false, scanError = "Réponse de scan incomplète.") }; return@onSuccess }
                when {
                    result.type == "WALLET" && !result.walletId.isNullOrBlank() -> {
                        updatePayScan { copy(isInterpreting = false) }
                        onRecipientChanged(result.walletId); openTransferDialog(); searchTransferRecipient()
                    }
                    result.type == "PRODUCTS" && _uiState.value.payscan.currentMode == PayScanMode.SUPERMARKET_BASKET && result.products.size == 1 -> {
                        val product = result.products.single()
                        val cart = _uiState.value.payscan.basketItems
                        if (cart.any { !it.sellerWalletId.isNullOrBlank() && it.sellerWalletId != product.sellerWalletId }) {
                            updatePayScan { copy(isInterpreting = false, scanError = "Terminez le panier du magasin actuel avant de changer de vendeur.") }; return@onSuccess
                        }
                        val existing = cart.find { it.id == product.id }
                        val quantity = (existing?.quantity ?: 0) + 1
                        if (quantity > product.stock || !product.price.isFinite() || product.price <= 0) {
                            updatePayScan { copy(isInterpreting = false, scanError = "Stock ou prix du catalogue indisponible.") }; return@onSuccess
                        }
                        val apparel = isApparelKeyword(product.name)
                        val item = PayScanCartItem(id = product.id, barcode = cleaned, name = product.name, unitPrice = product.price, currency = product.currency, quantity = quantity, boutiqueName = product.boutiqueName, sellerWalletId = product.sellerWalletId, isApparel = apparel)
                        updatePayScan { copy(isInterpreting = false, basketItems = cart.filterNot { it.id == product.id } + item, lastScannedApparelItem = if (apparel) item else null, showApparelTreatmentSheet = apparel, scanSuccessMessage = "Article vérifié dans le catalogue : ${product.name}") }
                    }
                    else -> showCommerceScanReview(cleaned)
                }
            }.onFailure { error -> updatePayScan { copy(isInterpreting = false, scanError = error.message ?: "Scan non confirmé par le serveur.") } }
        }
    }

    fun closeApparelTreatmentSheet() {
        updatePayScan { copy(showApparelTreatmentSheet = false, lastScannedApparelItem = null) }
    }

    fun applyApparelTreatment(
        itemId: String,
        size: String,
        antiTheftConfirmed: Boolean,
        treatmentNote: String
    ) {
        updatePayScan {
            copy(
                basketItems = basketItems.map {
                    if (it.id == itemId) {
                        it.copy(
                            size = size,
                            treatmentInfo = "Taille $size • ${if (antiTheftConfirmed) "Antivol désactivé • " else ""}$treatmentNote"
                        )
                    } else it
                },
                showApparelTreatmentSheet = false,
                lastScannedApparelItem = null,
                scanSuccessMessage = "👗 Vêtement validé (Taille $size • Traitement caisse & antivol appliqué) !"
            )
        }
    }

    fun openApparelTreatmentForItem(item: PayScanCartItem) {
        updatePayScan {
            copy(
                lastScannedApparelItem = item,
                showApparelTreatmentSheet = true
            )
        }
    }

    fun updateCartItemSize(itemId: String, size: String) {
        updatePayScan {
            copy(
                basketItems = basketItems.map {
                    if (it.id == itemId) it.copy(size = size, treatmentInfo = "Taille $size • Prêt-à-porter vérifié") else it
                }
            )
        }
    }

    private fun isApparelKeyword(text: String): Boolean {
        val lower = text.lowercase()
        val keywords = listOf(
            "vetement", "vêtement", "habit", "mode", "chemise", "pantalon", "robe",
            "t-shirt", "tshirt", "costume", "veste", "jeans", "jupe", "polo",
            "chaussure", "bazin", "wax", "pagne", "taille", "cotton", "coton", "textile", "fashion",
            "pull", "sweat", "short", "lingerie", "vestimentaire", "blouson", "manteau", "tailleur",
            "écharpe", "ceinture", "costard", "sneakers", "tissu", "apparel", "clothing", "boutique",
            "vet-", "cloth-", "app-", "mode-"
        )
        return keywords.any { lower.contains(it) } || lower.startsWith("vet") || lower.startsWith("cloth")
    }

    fun reviewPosPayment() {
        updatePayScan { copy(showConfirmPaymentDialog = false, scanError = null) }
        // The authenticated backend prices the real catalogue before any PIN is requested.
        val sale = _uiState.value.payscan.currentPosSaleData ?: buildPosSaleDataFromBasket()
        val seller = sale.items.firstOrNull()?.let { first -> _uiState.value.payscan.basketItems.find { it.id == first.id }?.sellerWalletId } ?: sale.sellerWalletId
        val scan = org.json.JSONObject().put("type", "POS_SALE").put("sellerWalletId", seller)
        val items = org.json.JSONArray()
        sale.items.forEach { item ->
            if (item.id.isNullOrBlank() || item.quantity < 1) {
                updatePayScan { copy(scanError = "Rescannez les articles pour obtenir leurs identifiants de catalogue.") }; return
            }
            items.put(org.json.JSONObject().put("id", item.id).put("quantity", item.quantity))
        }
        if (items.length() == 0) { updatePayScan { copy(scanError = "Scannez la demande de paiement ou un article du catalogue.") }; return }
        scan.put("items", items)
        showCommerceScanReview(scan.toString())
    }

    // Aliases for compatibility
    fun processProductScan(data: String) {
        processPayScanInput(data)
    }

    fun processPaymentRequestScan(data: String) {
        processPayScanInput(data)
    }

    // --- STARLINK MANAGER METHODS ---
    internal inline fun updateStarlink(block: StarlinkUiState.() -> StarlinkUiState) {
        _uiState.value = _uiState.value.copy(starlink = _uiState.value.starlink.block())
    }

    fun openStarlinkManager() {
        updateStarlink { copy(isStarlinkManagerOpen = true, error = null, successMessage = null) }
    }

    fun closeStarlinkManager() {
        updateStarlink { copy(isStarlinkManagerOpen = false) }
    }

    fun setStarlinkTab(tabIndex: Int) {
        updateStarlink { copy(activeTab = tabIndex) }
    }

    fun openTerminalDetail(terminal: StarlinkTerminalDto) {
        updateStarlink { copy(selectedTerminal = terminal, isTerminalDetailOpen = true) }
    }

    fun closeTerminalDetail() {
        updateStarlink { copy(selectedTerminal = null, isTerminalDetailOpen = false) }
    }

    fun openStarlinkPinDialog(plan: StarlinkPlanDto) {
        updateStarlink { copy(pendingActionPlan = plan, showPinDialog = true, pinInput = "") }
    }

    fun closeStarlinkPinDialog() {
        updateStarlink { copy(pendingActionPlan = null, showPinDialog = false, pinInput = "") }
    }

    fun onStarlinkPinChanged(pin: String) {
        updateStarlink { copy(pinInput = pin) }
    }

    fun openStarlinkLinkKitDialog() {
        updateStarlink { copy(showLinkKitDialog = true, linkKitSerialNumberInput = "") }
    }

    fun closeStarlinkLinkKitDialog() {
        updateStarlink { copy(showLinkKitDialog = false, linkKitSerialNumberInput = "") }
    }

    fun onStarlinkKitSerialChanged(serial: String) {
        updateStarlink { copy(linkKitSerialNumberInput = serial) }
    }

    fun clearStarlinkMessages() {
        updateStarlink { copy(error = null, successMessage = null) }
    }

    fun rebootTerminal(terminalId: String) {
        viewModelScope.launch {
            updateStarlink { copy(isLoading = true, isRebooting = true, error = null) }
            val res = repository.starlinkRebootTerminal(terminalId)
            res.onSuccess { msg ->
                updateStarlink {
                    copy(
                        isLoading = false,
                        isRebooting = true,
                        successMessage = "Signal de redémarrage envoyé au terminal $terminalId : $msg. Alignement satellite en cours...",
                        terminals = terminals.map { if (it.id == terminalId) it.copy(status = "REDÉMARRAGE...") else it }
                    )
                }
                // Delay 2 seconds then poll status to track reconnection
                kotlinx.coroutines.delay(2000)
                fetchTerminalStatus(terminalId)
            }.onFailure { err ->
                updateStarlink {
                    copy(
                        isLoading = false,
                        isRebooting = false,
                        error = err.message ?: "Échec du redémarrage du terminal."
                    )
                }
            }
        }
    }

    fun payStarlinkPlan(terminalId: String, planName: String, pin: String) {
        viewModelScope.launch {
            updateStarlink { copy(isLoading = true, error = null, showPinDialog = false) }
            val res = repository.starlinkBillingPay(terminalId, planName, pin)
            res.onSuccess {
                updateStarlink {
                    copy(
                        isLoading = false,
                        successMessage = "Abonnement $planName payé avec succès pour le terminal $terminalId !",
                        pendingActionPlan = null,
                        pinInput = ""
                    )
                }
            }.onFailure { err ->
                updateStarlink {
                    copy(
                        isLoading = false,
                        error = err.message ?: "Paiement de l'abonnement Starlink échoué.",
                        pendingActionPlan = null,
                        pinInput = ""
                    )
                }
            }
        }
    }

    fun linkStarlinkKit(serialNumber: String) {
        val trimmed = serialNumber.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            updateStarlink { copy(isLoading = true, error = null, showLinkKitDialog = false) }
            val res = repository.starlinkClientLink(trimmed)
            val newTerm = StarlinkTerminalDto(
                id = trimmed,
                name = "Starlink $trimmed",
                serialNumber = trimmed,
                status = "CONNECTÉ",
                signalQuality = "En attente",
                planName = "Standard",
                expiryDate = "--",
                ipAddress = "--",
                firmwareVersion = "--"
            )
            res.onSuccess {
                val exists = uiState.value.starlink.terminals.any { it.id == trimmed || it.serialNumber == trimmed }
                val updatedList = if (exists) uiState.value.starlink.terminals else (uiState.value.starlink.terminals + newTerm)
                updateStarlink {
                    copy(
                        isLoading = false,
                        successMessage = "Kit Starlink $trimmed associé avec succès !",
                        terminals = updatedList,
                        selectedTerminal = newTerm,
                        linkKitSerialNumberInput = ""
                    )
                }
                fetchTerminalStatus(trimmed)
            }.onFailure { err ->
                updateStarlink {
                    copy(
                        isLoading = false,
                        error = err.message ?: "Impossible d'associer le kit.",
                        linkKitSerialNumberInput = ""
                    )
                }
            }
        }
    }

    fun connectTerminalDirectly(terminalId: String) {
        val trimmed = terminalId.trim()
        if (trimmed.isBlank()) return
        val existing = uiState.value.starlink.terminals.find { it.id == trimmed }
        val term = existing ?: StarlinkTerminalDto(
            id = trimmed,
            name = "Terminal $trimmed",
            serialNumber = trimmed,
            status = "INITIALISATION",
            signalQuality = "--",
            planName = "--",
            expiryDate = "--",
            ipAddress = "--",
            firmwareVersion = "--"
        )
        if (existing == null) {
            updateStarlink { copy(terminals = terminals + term, selectedTerminal = term) }
        } else {
            updateStarlink { copy(selectedTerminal = term) }
        }
        fetchTerminalStatus(trimmed)
    }

    fun openAddSubCustomerDialog() {
        updateStarlink { copy(showAddSubCustomerDialog = true) }
    }

    fun closeAddSubCustomerDialog() {
        updateStarlink { copy(showAddSubCustomerDialog = false) }
    }

    fun saveSubCustomer(
        terminalId: String,
        name: String,
        walletId: String,
        pinCode: String,
        macAddress: String,
        expiryDate: String,
        dataLimitGb: Int
    ) {
        viewModelScope.launch {
            updateStarlink { copy(isLoading = true, error = null, showAddSubCustomerDialog = false) }
            val res = repository.starlinkSaveSubCustomer(
                terminalId = terminalId,
                name = name,
                walletId = walletId,
                pinCode = pinCode,
                macAddress = macAddress,
                expiryDate = expiryDate,
                dataLimitGb = dataLimitGb
            )
            val newSub = StarlinkSubCustomerDto(
                terminalId = terminalId,
                name = name,
                walletId = walletId,
                macAddress = macAddress,
                expiryDate = expiryDate,
                dataLimitGb = dataLimitGb
            )
            res.onSuccess {
                updateStarlink {
                    copy(
                        isLoading = false,
                        successMessage = "Client secondaire $name enregistré avec succès !",
                        subCustomers = subCustomers + newSub
                    )
                }
            }.onFailure { err ->
                updateStarlink {
                    copy(
                        isLoading = false,
                        error = err.message ?: "Erreur lors de l'enregistrement du client secondaire."
                    )
                }
            }
        }
    }

    fun fetchTerminalStatus(terminalId: String) {
        viewModelScope.launch {
            updateStarlink { copy(isLoading = true, error = null) }
            val res = repository.starlinkGetTerminalStatus(terminalId)
            val nowTime = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            res.onSuccess { statusData ->
                updateStarlink {
                    val updatedTerminals = terminals.map { t ->
                        if (t.id == terminalId || t.serialNumber == terminalId) {
                            t.copy(
                                status = statusData.displayStatus,
                                signalQuality = statusData.displaySignalQuality,
                                firmwareVersion = statusData.softwareVersion ?: t.firmwareVersion,
                                ipAddress = statusData.ipAddress ?: t.ipAddress
                            )
                        } else t
                    }
                    val updatedSelected = selectedTerminal?.let { st ->
                        if (st.id == terminalId || st.serialNumber == terminalId) {
                            st.copy(
                                status = statusData.displayStatus,
                                signalQuality = statusData.displaySignalQuality,
                                firmwareVersion = statusData.softwareVersion ?: st.firmwareVersion,
                                ipAddress = statusData.ipAddress ?: st.ipAddress
                            )
                        } else st
                    }
                    copy(
                        isLoading = false,
                        isRebooting = false,
                        terminalLiveStatus = statusData,
                        lastTelemetryUpdate = nowTime,
                        terminals = updatedTerminals,
                        selectedTerminal = updatedSelected,
                        successMessage = "Télémétrie en direct reçue ($nowTime)"
                    )
                }
            }.onFailure { err ->
                updateStarlink {
                    copy(
                        isLoading = false,
                        isRebooting = false,
                        lastTelemetryUpdate = nowTime,
                        error = err.message ?: "Impossible de récupérer l'état du terminal."
                    )
                }
            }
        }
    }
}

class DashboardViewModelFactory(
    private val repository: CashPayRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
