package com.example.data.remote

import com.example.data.model.CountriesResponse
import com.example.data.model.LoginRequest
import com.example.data.model.LoginResponse
import com.example.data.model.MeResponse
import com.example.data.model.RegisterRequest
import com.example.data.model.RegisterResponse
import com.example.data.model.WalletResponse
import com.example.data.model.TransfersMetaResponse
import com.example.data.model.TransferRequest
import com.example.data.model.TransferResponse
import com.example.data.model.TransactionsResponse
import com.example.data.model.NotificationsResponse
import com.example.data.model.MarkReadResponse
import com.example.data.model.MarkReadAllResponse
import com.example.data.model.SendOtpRequest
import com.example.data.model.SendOtpResponse
import com.example.data.model.UpdateProfileRequest
import com.example.data.model.VerifyOtpRequest
import com.example.data.model.VerifyOtpResponse
import com.example.data.model.VerifyPinRequest
import com.example.data.model.VerifyPinResponse
import com.example.data.model.ExchangeRequest
import com.example.data.model.ExchangeResponse
import com.example.data.model.CryptoExchangeRequest
import com.example.data.model.CryptoExchangeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query

interface CashPayApiService {

    @GET("api/v1/countries")
    suspend fun getCountries(): Response<CountriesResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/v1/auth/otp/send")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<SendOtpResponse>

    @POST("api/v1/auth/otp/verify")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<VerifyOtpResponse>

    @POST("api/v1/auth/pin/verify")
    suspend fun verifyPin(@Body request: VerifyPinRequest): Response<VerifyPinResponse>

    @GET("api/v1/auth/me")
    suspend fun getMe(@Query("phone") phone: String): Response<MeResponse>

    @PATCH("api/v1/auth/me")
    suspend fun updateMe(
        @Query("phone") phone: String,
        @Body request: UpdateProfileRequest
    ): Response<MeResponse>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/v1/wallet")
    suspend fun getWallet(@Query("userId") userId: Long): Response<WalletResponse>

    @GET("api/v1/transfers")
    suspend fun getTransfersMeta(@Query("userId") userId: Long): Response<TransfersMetaResponse>

    @POST("api/v1/transfers")
    suspend fun transfer(@Body request: TransferRequest): Response<TransferResponse>

    @POST("api/v1/exchange")
    suspend fun exchange(@Body request: ExchangeRequest): Response<ExchangeResponse>

    @POST("api/crypto/exchange")
    suspend fun cryptoExchange(@Body request: CryptoExchangeRequest): Response<CryptoExchangeResponse>

    @POST("api/v1/crypto/exchange")
    suspend fun cryptoExchangeV1(@Body request: CryptoExchangeRequest): Response<CryptoExchangeResponse>

    @GET("api/v1/transactions")
    suspend fun getTransactions(
        @Query("userId") userId: Long,
        @Query("limit") limit: Int? = 50,
        @Query("offset") offset: Int? = 0,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("direction") direction: String? = null
    ): Response<TransactionsResponse>

    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("limit") limit: Int? = 20
    ): Response<NotificationsResponse>

    @POST("api/v1/notifications/{id}/read")
    suspend fun markNotificationRead(
        @Path("id") id: Long
    ): Response<MarkReadResponse>

    @POST("api/v1/notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<MarkReadAllResponse>

    // --- CARDS API ---
    @GET("api/v1/cards/catalog")
    suspend fun getCardCatalog(): Response<okhttp3.ResponseBody>

    @GET("api/v1/cards/fees")
    suspend fun getCardFees(
        @Query("type") type: String? = null,
        @Query("card_type") cardType: String? = null,
        @Query("currency") currency: String? = null,
        @Query("brand") brand: String? = null,
        @Query("fee_code") feeCode: String? = null,
        @Query("code") code: String? = null
    ): Response<okhttp3.ResponseBody>

    @GET("api/v1/cards")
    suspend fun getUserCards(): Response<okhttp3.ResponseBody>

    @POST("api/v1/cards/purchase")
    suspend fun purchaseCard(@Body request: com.example.data.model.PurchaseCardRequest): Response<okhttp3.ResponseBody>

    @POST("api/v1/cards/{id}/activate")
    suspend fun activateCard(@Path("id") id: String): Response<com.example.data.model.CardActionResponse>

    @POST("api/v1/cards/{id}/freeze")
    suspend fun freezeCard(@Path("id") id: String): Response<com.example.data.model.CardActionResponse>

    @POST("api/v1/cards/{id}/unfreeze")
    suspend fun unfreezeCard(@Path("id") id: String): Response<com.example.data.model.CardActionResponse>

    @POST("api/v1/cards/{id}/pin")
    suspend fun changeCardPin(
        @Path("id") id: String,
        @Body request: Map<String, String> // {"pin": "1234"}
    ): Response<com.example.data.model.CardActionResponse>

    @POST("api/v1/cards/{id}/terminate")
    suspend fun terminateCard(@Path("id") id: String): Response<com.example.data.model.CardActionResponse>

    @POST("api/v1/cards/{id}/funding")
    suspend fun updateCardFunding(
        @Path("id") id: String,
        @Body request: com.example.data.model.FundingConfigRequest
    ): Response<com.example.data.model.CardActionResponse>

    @POST("api/v1/cards/{id}/reveal")
    suspend fun revealCardDetails(
        @Path("id") id: String,
        @Body request: Map<String, String> // {"pin": "1234"}
    ): Response<com.example.data.model.CardDetailsResponse>

    @GET("api/v1/profile/by-phone")
    suspend fun searchProfileByPhone(
        @Query("phone") phone: String
    ): Response<com.example.data.model.PublicProfileResponse>

    @GET("api/v1/profile/by-wallet")
    suspend fun searchProfileByWallet(
        @Query("walletId") walletId: String
    ): Response<com.example.data.model.PublicProfileResponse>

    @GET("api/v1/profile/search")
    suspend fun searchProfile(
        @Query("q") query: String
    ): Response<com.example.data.model.PublicProfileResponse>

    @GET("api/v1/withdrawals")
    suspend fun previewWithdrawal(
        @Query("method") method: String,
        @Query("amount") amount: Double,
        @Query("currency") currency: String,
        @Query("operator") operator: String? = null
    ): Response<com.example.data.model.WithdrawalPreviewResponse>

    @POST("api/v1/withdrawals")
    suspend fun confirmWithdrawal(
        @Body request: com.example.data.model.WithdrawalConfirmRequest
    ): Response<com.example.data.model.WithdrawalConfirmResponse>

    // --- LOANS API ---
    @GET("api/v1/loans/offer")
    suspend fun getLoanOffer(
        @Query("currency") currency: String
    ): Response<com.example.data.model.LoanOfferResponse>

    @GET("api/v1/loans/current")
    suspend fun getLoanCurrent(): Response<com.example.data.model.LoanCurrentResponse>

    @GET("api/v1/loans/history")
    suspend fun getLoanHistory(): Response<com.example.data.model.LoanHistoryResponse>

    @POST("api/v1/loans/request")
    suspend fun requestLoan(
        @Body request: com.example.data.model.LoanRequest
    ): Response<com.example.data.model.LoanRequestResponse>

    @POST("api/v1/loans/repay")
    suspend fun repayLoan(
        @Body request: com.example.data.model.LoanRepayRequest
    ): Response<com.example.data.model.LoanRepayResponse>

    @POST("api/v1/loans/repay-installment")
    suspend fun repayLoanInstallment(
        @Body request: com.example.data.model.LoanRepayInstallmentRequest
    ): Response<com.example.data.model.LoanRepayInstallmentResponse>

    @POST("api/v1/auth/biometric")
    suspend fun biometricAction(
        @Body request: com.example.data.model.BiometricActionRequest
    ): Response<com.example.data.model.BiometricActionResponse>

    // --- FORGOT PIN & AGENT ACTIVATION ENDPOINTS ---
    @POST("api/v1/auth/forgot-pin/request")
    suspend fun forgotPinRequest(
        @Body request: com.example.data.model.ForgotPinRequestDto
    ): Response<com.example.data.model.ForgotPinRequestResponse>

    @POST("api/v1/auth/forgot-pin/verify")
    suspend fun forgotPinVerify(
        @Body request: com.example.data.model.ForgotPinVerifyDto
    ): Response<com.example.data.model.ForgotPinVerifyResponse>

    @POST("api/v1/auth/forgot-pin/reset")
    suspend fun forgotPinReset(
        @Body request: com.example.data.model.ForgotPinResetDto
    ): Response<com.example.data.model.ForgotPinResetResponse>

    @POST("api/v1/agent/activate")
    suspend fun activateAgent(
        @Body request: com.example.data.model.ActivateAgentRequest
    ): Response<com.example.data.model.ActivateAgentResponse>

    @POST("api/v1/agent/deposit")
    suspend fun agentDeposit(
        @Body request: com.example.data.model.AgentDepositRequest
    ): Response<com.example.data.model.AgentDepositResponse>

    @POST("api/v1/agent/withdraw")
    suspend fun withdrawAction(
        @Body request: com.example.data.model.WithdrawActionRequest
    ): Response<com.example.data.model.WithdrawActionResponse>

    @POST("api/v1/agent/withdraw")
    suspend fun agentWithdraw(
        @Body request: com.example.data.model.AgentWithdrawRequest
    ): Response<com.example.data.model.AgentWithdrawResponse>

    @GET("api/v1/agent/loan-target")
    suspend fun getAgentLoanTarget(
        @Query("clientRef") clientRef: String
    ): Response<com.example.data.model.AgentLoanTargetResponse>

    @POST("api/v1/agent/loan-repay")
    suspend fun agentLoanRepay(
        @Body request: com.example.data.model.AgentLoanRepaymentRequest
    ): Response<com.example.data.model.AgentLoanRepaymentResponse>

    @GET("api/v1/agent/commissions")
    suspend fun getAgentCommissions(): Response<com.example.data.model.AgentCommissionsResponse>

    @POST("api/v1/agent/commissions/transfer")
    suspend fun transferAgentCommission(
        @Body request: com.example.data.model.AgentCommissionTransferRequest
    ): Response<com.example.data.model.AgentCommissionTransferResponse>

    // --- SHOPPING AGENT API ---
    @GET("api/v1/shopping/products")
    suspend fun getShoppingProducts(): Response<com.example.data.model.ShoppingContextResponse>

    @POST("api/v1/shopping/products")
    suspend fun publishProduct(
        @Body request: com.example.data.model.PublishProductRequest
    ): Response<com.example.data.model.ProductResponse>

    @GET("api/v1/shopping/products/{id}")
    suspend fun getProduct(
        @Path("id") productId: String
    ): Response<com.example.data.model.ProductResponse>

    @PUT("api/v1/shopping/products/{id}")
    suspend fun updateProduct(
        @Path("id") productId: String,
        @Body request: com.example.data.model.PublishProductRequest
    ): Response<com.example.data.model.ProductResponse>

    @DELETE("api/v1/shopping/products/{id}")
    suspend fun deleteProduct(
        @Path("id") productId: String
    ): Response<com.example.data.model.GenericShoppingResponse>

    @GET("api/v1/shopping/products/{id}/reference")
    suspend fun getProductReference(
        @Path("id") productId: String
    ): Response<com.example.data.model.ProductReferenceResponse>

    @GET("api/v1/shopping/boutiques")
    suspend fun getShoppingBoutiques(
        @Query("walletId") walletId: String? = null,
        @Query("agentId") agentId: String? = null
    ): Response<okhttp3.ResponseBody>

    @GET("api/v1/agent/boutiques")
    suspend fun getAgentBoutiques(
        @Query("walletId") walletId: String? = null
    ): Response<okhttp3.ResponseBody>

    @POST("api/v1/shopping/boutiques")
    suspend fun createBoutique(
        @Body request: com.example.data.model.CreateBoutiqueRequest
    ): Response<okhttp3.ResponseBody>

    @GET("api/v1/shopping/boutiques/{id}")
    suspend fun getBoutique(
        @Path("id") boutiqueId: String
    ): Response<okhttp3.ResponseBody>

    @PUT("api/v1/shopping/boutiques/{id}")
    suspend fun updateBoutique(
        @Path("id") boutiqueId: String,
        @Body request: com.example.data.model.CreateBoutiqueRequest
    ): Response<okhttp3.ResponseBody>

    @DELETE("api/v1/shopping/boutiques/{id}")
    suspend fun deleteBoutique(
        @Path("id") boutiqueId: String
    ): Response<okhttp3.ResponseBody>

    // --- PUBLIC SHOPPING CATALOG API ---
    @GET("api/public/shopping/user/{userId}/products")
    suspend fun getPublicUserProducts(
        @Path("userId") userId: String,
        @Query("storeId") storeId: String? = null
    ): Response<com.example.data.model.PublicCatalogResponse>

    // --- DEPOSIT API (MaxiCash Gateway) ---
    @POST("api/v1/deposit")
    suspend fun depositMobileMoney(
        @Body request: com.example.data.model.DepositMobileMoneyRequest
    ): Response<com.example.data.model.DepositMobileMoneyResponse>

    @POST("api/v1/deposit/card")
    suspend fun depositCard(
        @Body request: com.example.data.model.DepositGatewayRequest
    ): Response<com.example.data.model.DepositGatewayResponse>

    @POST("api/v1/deposit/paypal")
    suspend fun depositPayPal(
        @Body request: com.example.data.model.DepositGatewayRequest
    ): Response<com.example.data.model.DepositGatewayResponse>

    // --- RATES / EXCHANGE RATES API ---
    @GET("api/v1/rates")
    suspend fun getExchangeRates(
        @Query("countryCode") countryCode: String? = null
    ): Response<com.example.data.model.ExchangeRatesResponse>

    // --- AGENT CUSTOMER ONBOARDING (Cahier des charges) ---
    @GET("api/v1/agents/customers/options")
    suspend fun getAgentCustomerOptions(
        @Query("country") country: String? = null,
        @Query("countryCode") countryCode: String? = null
    ): Response<com.example.data.model.AgentCustomerOptionsResponse>

    @POST("api/v1/agents/customers/register")
    suspend fun registerAgentCustomer(
        @Body request: com.example.data.model.AgentRegisterCustomerRequest
    ): Response<com.example.data.model.AgentRegisterCustomerResponse>

    @GET("api/v1/agents/customers")
    suspend fun getAgentCustomers(): Response<com.example.data.model.AgentCustomerListResponse>

    // --- INTERNATIONAL TRANSFER CLAIMS (Section Client) ---
    @POST("api/v1/international-transfer/claims")
    suspend fun createInternationalClaim(
        @Body request: com.example.data.model.CreateClaimRequest
    ): Response<com.example.data.model.CreateClaimResponse>

    @GET("api/v1/international-transfer/claims")
    suspend fun getInternationalClaims(): Response<com.example.data.model.ClaimsListResponse>

    @GET("api/v1/international-transfer/claims/{id}")
    suspend fun getInternationalClaimDetail(
        @Path("id") id: Long
    ): Response<com.example.data.model.ClaimDetailResponse>

    // --- REFERRALS & REWARDS (Programme de Parrainage) ---
    @GET("api/v1/referrals")
    suspend fun getReferrals(): Response<com.example.data.model.ReferralResponse>

    @POST("api/v1/referrals")
    suspend fun claimReferralReward(
        @Body request: com.example.data.model.ClaimReferralRequest
    ): Response<com.example.data.model.ClaimReferralResponse>

    // --- TV SUBSCRIPTIONS API (Canal+, EasyTV, BLUS) ---
    @GET("api/v1/tv/subscriptions")
    suspend fun getTvPackages(
        @Query("provider") provider: String
    ): Response<okhttp3.ResponseBody>

    @POST("api/v1/tv/subscriptions")
    suspend fun submitTvSubscription(
        @Body request: com.example.data.model.TvSubscriptionRequest
    ): Response<okhttp3.ResponseBody>

    // --- REGIDESO PUBLIC INVOICES API ---
    @GET("api/v1/regideso/invoices")
    suspend fun getRegidesoInvoices(
        @Query("abonnementId") abonnementId: String? = null,
        @Query("province") province: String = "KIN"
    ): Response<okhttp3.ResponseBody>

    // --- CPK FINANCIAL KEYS API (FINUSECO / CashPay) ---
    @POST("api/v1/security/cpk/issue")
    suspend fun issueCpkKey(
        @Body request: com.example.data.model.CpkIssueRequest
    ): Response<okhttp3.ResponseBody>

    @GET("api/v1/security/cpk/list")
    suspend fun getCpkKeys(): Response<okhttp3.ResponseBody>

    @POST("api/v1/security/cpk/recover")
    suspend fun recoverCpkKey(
        @Body request: com.example.data.model.CpkRecoverRequest
    ): Response<okhttp3.ResponseBody>

    @POST("api/v1/security/cpk/cancel")
    suspend fun cancelCpkKey(
        @Body request: com.example.data.model.CpkCancelRequest
    ): Response<okhttp3.ResponseBody>

    @GET("api/v1/security/cpk/status/{signature}")
    suspend fun getCpkKeyStatus(
        @Path("signature") signature: String
    ): Response<okhttp3.ResponseBody>

    // --- PAYSCAN VISUAL CODES & POS PAYMENT API ---
    @POST("api/v1/scanpay/scan")
    suspend fun scanPayInterpret(
        @Body request: com.example.data.model.ScanPayInterpretRequest
    ): Response<com.example.data.model.ScanPayInterpretResponse>

    @POST("api/v1/scanpay/product")
    suspend fun lookupScannedProduct(
        @Body request: com.example.data.model.ScanProductLookupRequest
    ): Response<com.example.data.model.ScanProductLookupResponse>

    @POST("api/v1/scanpay")
    suspend fun processPosSale(
        @Body request: com.example.data.model.PosSalePaymentRequest
    ): Response<com.example.data.model.PosSalePaymentResponse>

    @GET("api/v1/scanpay")
    suspend fun getScanPayData(
        @Query("data") data: String
    ): Response<com.example.data.model.ScanPayInterpretResponse>

    // --- STARLINK MANAGER API (Official Documentation) ---
    @POST("api/v1/starlink/client/link")
    suspend fun starlinkClientLink(
        @Body request: com.example.data.model.StarlinkClientLinkRequest
    ): Response<com.example.data.model.StarlinkBaseResponse>

    @POST("api/v1/starlink/sub-customer")
    suspend fun starlinkSaveSubCustomer(
        @Body request: com.example.data.model.StarlinkSubCustomerRequest
    ): Response<com.example.data.model.StarlinkBaseResponse>

    @POST("api/v1/starlink/billing/pay")
    suspend fun starlinkBillingPay(
        @Body request: com.example.data.model.StarlinkBillingPayRequest
    ): Response<com.example.data.model.StarlinkBaseResponse>

    @POST("api/v1/starlink/reseller/link")
    suspend fun starlinkResellerLink(
        @Body request: Map<String, Any>
    ): Response<com.example.data.model.StarlinkResellerLinkResponse>

    @GET("api/v1/starlink/reseller/config")
    suspend fun starlinkGetResellerConfig(): Response<com.example.data.model.StarlinkResellerConfigResponse>

    @POST("api/v1/starlink/reseller/config")
    suspend fun starlinkSaveResellerConfig(
        @Body request: com.example.data.model.StarlinkSaveResellerConfigRequest
    ): Response<com.example.data.model.StarlinkBaseResponse>

    @GET("api/v1/starlink/reseller/terminals")
    suspend fun starlinkGetTerminals(): Response<com.example.data.model.StarlinkResellerTerminalsResponse>

    @POST("api/v1/starlink/terminal/register")
    suspend fun starlinkRegisterTerminal(
        @Body request: com.example.data.model.StarlinkRegisterTerminalRequest
    ): Response<com.example.data.model.StarlinkBaseResponse>

    @GET("api/v1/starlink/terminal/{id}/status")
    suspend fun starlinkGetTerminalStatus(
        @Path("id") terminalId: String
    ): Response<com.example.data.model.StarlinkTerminalStatusResponse>

    @POST("api/v1/starlink/terminal/{id}/reboot")
    suspend fun starlinkRebootTerminal(
        @Path("id") terminalId: String
    ): Response<com.example.data.model.StarlinkBaseResponse>
}
