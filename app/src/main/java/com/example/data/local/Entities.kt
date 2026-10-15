package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val code: String,
    val name: String,
    val dialCode: String
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Long,
    val role: String?,
    val status: String?,
    val accountType: String?,
    val walletId: String?,
    val language: String?,
    val ussdLanguage: String?,
    val fullName: String?,
    val firstName: String?,
    val lastName: String?,
    val middleName: String?,
    val gender: String?,
    val maritalStatus: String?,
    val birthDate: String?,
    val birthPlace: String?,
    val nationality: String?,
    val email: String?,
    val phone: String?,
    val country: String?,
    val province: String?,
    val city: String?,
    val address: String?,
    val profession: String?,
    val activityDescription: String?,
    val incomePerMonth: String?,
    val profilePhoto: String?,
    val profilePhotoUrl: String?,
    val coverPhoto: String?,
    val coverPhotoUrl: String?,
    val signatureImage: String?,
    val googleId: String?,
    val zoomId: String?,
    val notificationsChannel: String?,
    val assistantSettings: String?,
    val availabilityStatus: String?,
    val schedule: String?,
    val representative: String?,
    val verificationStatus: String?,
    val tempPhotoExpiresAt: String?,
    val isCryptoActive: Boolean?,
    val createdAt: String?,
    val astroSign: String?,
    val astroElement: String?,
    val astroLuckyNumber: Int?,
    val astroFavorableDay: String?,
    val astroFinanceInsight: String?,
    val balanceUsd: Double = 0.0,
    val balanceCdf: Double = 0.0
)

@Entity(tableName = "auth_session")
data class SessionEntity(
    @PrimaryKey val id: Int = 1,
    val userId: Long,
    val walletId: String?,
    val phone: String?,
    val isAuthenticated: Boolean = false,
    val biometricEnabled: Boolean = false,
    val sessionToken: String? = null,
    val lastLoginTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val recipient: String,
    val amount: Double,
    val currency: String = "USD",
    val type: String = "TR", // "TR", "DP", etc.
    val date: String,
    val status: String = "completed",
    val reference: String,
    val direction: String = "outgoing", // "incoming" or "outgoing"
    val displayKind: String = "person", // "person" or "service"
    val displayName: String = "",
    val displayAvatar: String? = null,
    val displayIcon: String? = null,
    val fee: Double = 0.0,
    val createdAt: String = "",
    val otherUserFullName: String? = null,
    val otherUserProfilePhoto: String? = null,
    val description: String? = null,
    val senderWalletId: String? = null,
    val receiverWalletId: String? = null
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String,
    val timestamp: String,
    val read: Boolean,
    val type: String, // "new_message", "missed_audio_call", "missed_video_call", "meeting_invite", "general", "game_invite", "pos_sale", "new_follower"
    val link: String? = null
)

@Entity(tableName = "cpk_keys")
data class CpkKeyEntity(
    @PrimaryKey val signature: String,
    val reference: String? = null,
    val amount: Double = 0.0,
    val currency: String = "USD",
    val securityLevel: String = "none", // "none", "pin_protected"
    val status: String = "active", // "active", "claimed", "partially_used", "cancelled", "frozen"
    val remainingAmount: Double = 0.0,
    val createdAt: String = "",
    val fileName: String? = null,
    val localFilePath: String? = null,
    val rawJsonContent: String = "",
    val isCloudSynced: Boolean = false,
    val cloudDriveFileId: String? = null,
    val lastSyncTimestamp: Long = 0L,
    val isOwner: Boolean = true
)
