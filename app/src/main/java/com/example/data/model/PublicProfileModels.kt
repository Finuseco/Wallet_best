package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BitcoinPublicDto(
    @Json(name = "available") val available: Boolean,
    @Json(name = "address") val address: String? = null
)

@JsonClass(generateAdapter = true)
data class PublicProfileDto(
    @Json(name = "walletId") val walletId: String,
    @Json(name = "fullName") val fullName: String,
    @Json(name = "role") val role: String? = null, // "client" or "agent"
    @Json(name = "profilePhoto") val profilePhoto: String? = null,
    @Json(name = "profilePhotoUrl") val profilePhotoUrl: String? = null,
    @Json(name = "coverPhoto") val coverPhoto: String? = null,
    @Json(name = "coverPhotoUrl") val coverPhotoUrl: String? = null,
    @Json(name = "profession") val profession: String? = null,
    @Json(name = "activityDescription") val activityDescription: String? = null,
    @Json(name = "birthDate") val birthDate: String? = null,
    @Json(name = "nationality") val nationality: String? = null,
    @Json(name = "language") val language: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "province") val province: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "verificationStatus") val verificationStatus: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "bitcoin") val bitcoin: BitcoinPublicDto? = null,
    @Json(name = "boutiques") val boutiques: List<BoutiqueDto>? = null
)

@JsonClass(generateAdapter = true)
data class PublicProfileResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "found") val found: Boolean = false,
    @Json(name = "profile") val profile: PublicProfileDto? = null,
    @Json(name = "error") val error: String? = null
)

data class PhoneContact(
    val name: String,
    val phone: String,
    val normalizedPhone: String,
    val lastNineDigits: String,
    val isCashPayUser: Boolean = false,
    val publicProfile: PublicProfileDto? = null
)
