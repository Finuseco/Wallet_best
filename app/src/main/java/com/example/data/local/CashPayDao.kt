package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CashPayDao {

    // Countries
    @Query("SELECT * FROM countries ORDER BY name ASC")
    fun getAllCountries(): Flow<List<CountryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<CountryEntity>)

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun getCountryCount(): Int

    // Profile
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()

    // Session
    @Query("SELECT * FROM auth_session WHERE id = 1 LIMIT 1")
    fun getSession(): Flow<SessionEntity?>

    @Query("SELECT * FROM auth_session WHERE id = 1 LIMIT 1")
    suspend fun getSessionOnce(): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: SessionEntity)

    @Query("UPDATE auth_session SET biometricEnabled = :enabled WHERE id = 1")
    suspend fun updateBiometric(enabled: Boolean)

    @Query("DELETE FROM auth_session")
    suspend fun clearSession()

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET read = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("UPDATE notifications SET read = 1")
    suspend fun markAllNotificationsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearNotifications()

    // --- CPK KEYS DAO ---
    @Query("SELECT * FROM cpk_keys ORDER BY createdAt DESC")
    fun getAllCpkKeys(): Flow<List<CpkKeyEntity>>

    @Query("SELECT * FROM cpk_keys WHERE signature = :signature LIMIT 1")
    suspend fun getCpkKeyBySignature(signature: String): CpkKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCpkKey(key: CpkKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCpkKeys(keys: List<CpkKeyEntity>)

    @Query("UPDATE cpk_keys SET status = :status, remainingAmount = :remainingAmount WHERE signature = :signature")
    suspend fun updateCpkKeyStatus(signature: String, status: String, remainingAmount: Double)

    @Query("UPDATE cpk_keys SET isCloudSynced = :isSynced, cloudDriveFileId = :driveFileId, lastSyncTimestamp = :timestamp WHERE signature = :signature")
    suspend fun updateCpkCloudSyncStatus(signature: String, isSynced: Boolean, driveFileId: String?, timestamp: Long)

    @Query("DELETE FROM cpk_keys WHERE signature = :signature")
    suspend fun deleteCpkKey(signature: String)

    @Query("DELETE FROM cpk_keys")
    suspend fun clearAllCpkKeys()
}
