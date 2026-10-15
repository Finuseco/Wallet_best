package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReferralInfo(
    @Json(name = "code") val code: String = "",
    @Json(name = "link") val link: String = ""
)

@JsonClass(generateAdapter = true)
data class ReferralStats(
    @Json(name = "totalReferrals") val totalReferrals: Int = 0,
    @Json(name = "pendingCount") val pendingCount: Int = 0,
    @Json(name = "claimableCount") val claimableCount: Int = 0,
    @Json(name = "claimedCount") val claimedCount: Int = 0,
    @Json(name = "pointConversionRate") val pointConversionRate: Double = 0.0,
    @Json(name = "claimableAmount") val claimableAmount: Double = 0.0,
    @Json(name = "totalEarnedAmount") val totalEarnedAmount: Double = 0.0,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "devise") val devise: String? = null,
    @Json(name = "currencySymbol") val currencySymbol: String? = null,
    @Json(name = "countryCode") val countryCode: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "isConfigured") val isConfigured: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class ReferralItem(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "fullName") val fullName: String = "",
    @Json(name = "status") val status: String = "",
    @Json(name = "date") val date: String = "",
    @Json(name = "isClaimed") val isClaimed: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ReferralResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "referral") val referral: ReferralInfo? = null,
    @Json(name = "stats") val stats: ReferralStats? = null,
    @Json(name = "referrals") val referrals: List<ReferralItem> = emptyList(),
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "devise") val devise: String? = null,
    @Json(name = "currencySymbol") val currencySymbol: String? = null,
    @Json(name = "countryCode") val countryCode: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "isConfigured") val isConfigured: Boolean? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class ClaimReferralRequest(
    @Json(name = "referralId") val referralId: Long
)

@JsonClass(generateAdapter = true)
data class ClaimReferralResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "message") val message: String? = null,
    @Json(name = "referralId") val referralId: Long? = null,
    @Json(name = "error") val error: String? = null
)
