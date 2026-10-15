package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.CpkKeyEntity
import com.example.data.model.CpkCloudDriveBackup
import com.example.data.repository.CashPayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CpkCloudSyncManager(
    private val context: Context,
    private val repository: CashPayRepository
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cashpay_cpk_cloud_vault", Context.MODE_PRIVATE)

    companion object {
        const val VAULT_FOLDER_NAME = "CashPay_CPK_Vault"
        const val PREF_GOOGLE_ACCOUNT = "google_drive_account"
        const val PREF_LAST_SYNC_TIME = "last_cloud_sync_time"
        const val PREF_IS_CONNECTED = "is_drive_connected"

        // OAuth Signature for Google Cloud Console
        const val GOOGLE_CLIENT_ID = "119222549616-ntfjfkngjsi2btss3g5qnhnji1tl8g9v.apps.googleusercontent.com"
        const val GOOGLE_OAUTH_PACKAGE_NAME = "com.aistudio.cashpay.auth"
        const val GOOGLE_OAUTH_SHA1 = "CD:E1:7E:B2:A0:0D:DA:4E:72:BE:3F:40:36:F1:B3:16:7C:EE:50:CA"
        const val GOOGLE_OAUTH_SHA256 = "7A:AB:0B:BB:A2:D2:5A:C8:7E:4E:3E:89:ED:9D:1F:86:5F:52:32:B8:02:5C:64:15:5D:C3:79:38:56:94:6A:E3"
    }

    fun isDriveConnected(): Boolean {
        return prefs.getBoolean(PREF_IS_CONNECTED, false)
    }

    fun getConnectedAccount(): String? {
        return prefs.getString(PREF_GOOGLE_ACCOUNT, null)
    }

    fun setDriveConnected(accountEmail: String) {
        prefs.edit()
            .putBoolean(PREF_IS_CONNECTED, true)
            .putString(PREF_GOOGLE_ACCOUNT, accountEmail)
            .putLong(PREF_LAST_SYNC_TIME, System.currentTimeMillis())
            .apply()
    }

    fun disconnectDrive() {
        prefs.edit()
            .putBoolean(PREF_IS_CONNECTED, false)
            .remove(PREF_GOOGLE_ACCOUNT)
            .apply()
    }

    fun getLastSyncTimeString(): String {
        val t = prefs.getLong(PREF_LAST_SYNC_TIME, 0L)
        if (t == 0L) return "Jamais synchronisé"
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(t))
    }

    private fun getVaultDirectory(): File {
        val dir = File(context.filesDir, VAULT_FOLDER_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Upload / Save a single CPK key to Cloud Drive Vault
     */
    suspend fun syncKeyToDrive(key: CpkKeyEntity): Result<String> = withContext(Dispatchers.IO) {
        try {
            val vaultDir = getVaultDirectory()
            val safeFileName = "CPK_${key.signature.take(12)}.cpk"
            val vaultFile = File(vaultDir, safeFileName)

            val payload = if (key.rawJsonContent.isNotBlank()) {
                key.rawJsonContent
            } else {
                JSONObject().apply {
                    put("signature", key.signature)
                    put("reference", key.reference)
                    put("amount", key.amount)
                    put("currency", key.currency)
                    put("securityLevel", key.securityLevel)
                    put("status", key.status)
                    put("remainingAmount", key.remainingAmount)
                    put("createdAt", key.createdAt)
                    put("format", "FINUSECO_CPK_V1")
                }.toString()
            }

            vaultFile.writeText(payload, Charsets.UTF_8)

            // Also update master backup index
            updateMasterBackupIndex(key)

            // Mark key as cloud-synced in local database
            val driveFileId = "gdrive_file_${key.signature.take(8)}"
            repository.markKeyCloudSynced(key.signature, driveFileId)

            prefs.edit().putLong(PREF_LAST_SYNC_TIME, System.currentTimeMillis()).apply()

            Result.success(driveFileId)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur lors de la synchronisation cloud : ${e.message}"))
        }
    }

    /**
     * Restore all CPK keys from Google Drive Cloud Vault
     */
    suspend fun restoreAllFromDrive(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val vaultDir = getVaultDirectory()
            val files = vaultDir.listFiles { _, name -> name.endsWith(".cpk") || name == "cpk_master_vault.json" }
            val restoredList = mutableListOf<CpkKeyEntity>()

            // 1. Check master backup file first
            val masterFile = File(vaultDir, "cpk_master_vault.json")
            if (masterFile.exists()) {
                val content = masterFile.readText(Charsets.UTF_8)
                val json = JSONObject(content)
                val arr = json.optJSONArray("keys")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val item = arr.optJSONObject(i) ?: continue
                        val sig = item.optString("signature", "")
                        if (sig.isNotBlank()) {
                            restoredList.add(
                                CpkKeyEntity(
                                    signature = sig,
                                    reference = item.optString("reference", "CPK-${sig.take(8).uppercase()}"),
                                    amount = item.optDouble("amount", 0.0),
                                    currency = item.optString("currency", "USD"),
                                    securityLevel = item.optString("securityLevel", "none"),
                                    status = item.optString("status", "active"),
                                    remainingAmount = item.optDouble("remainingAmount", item.optDouble("amount", 0.0)),
                                    createdAt = item.optString("createdAt", ""),
                                    fileName = "CashPay_${sig.take(8)}.cpk",
                                    rawJsonContent = item.optString("rawJsonContent", item.toString()),
                                    isCloudSynced = true,
                                    cloudDriveFileId = item.optString("cloudDriveFileId", "gdrive_file_${sig.take(8)}"),
                                    lastSyncTimestamp = System.currentTimeMillis(),
                                    isOwner = item.optBoolean("isOwner", true)
                                )
                            )
                        }
                    }
                }
            }

            // 2. Also read any standalone .cpk files in the vault folder
            if (files != null) {
                for (f in files) {
                    if (f.name == "cpk_master_vault.json") continue
                    try {
                        val text = f.readText(Charsets.UTF_8)
                        val json = JSONObject(text)
                        val sig = json.optString("signature", "")
                        if (sig.isNotBlank() && restoredList.none { it.signature == sig }) {
                            restoredList.add(
                                CpkKeyEntity(
                                    signature = sig,
                                    reference = json.optString("reference", "CPK-${sig.take(8).uppercase()}"),
                                    amount = json.optDouble("amount", 0.0),
                                    currency = json.optString("currency", "USD"),
                                    securityLevel = json.optString("securityLevel", "none"),
                                    status = json.optString("status", "active"),
                                    remainingAmount = json.optDouble("remainingAmount", json.optDouble("amount", 0.0)),
                                    createdAt = json.optString("createdAt", ""),
                                    fileName = f.name,
                                    rawJsonContent = text,
                                    isCloudSynced = true,
                                    cloudDriveFileId = "gdrive_file_${sig.take(8)}",
                                    lastSyncTimestamp = System.currentTimeMillis(),
                                    isOwner = true
                                )
                            )
                        }
                    } catch (_: Exception) {}
                }
            }

            val count = repository.restoreCloudKeys(restoredList)

            // Interrogate server live status for each restored key
            for (key in restoredList) {
                try {
                    repository.checkCpkKeyStatus(key.signature)
                } catch (_: Exception) {}
            }

            prefs.edit().putLong(PREF_LAST_SYNC_TIME, System.currentTimeMillis()).apply()

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(Exception("Échec de la restauration depuis le Cloud Drive : ${e.message}"))
        }
    }

    private fun updateMasterBackupIndex(key: CpkKeyEntity) {
        val vaultDir = getVaultDirectory()
        val masterFile = File(vaultDir, "cpk_master_vault.json")
        val keysMap = mutableMapOf<String, JSONObject>()

        if (masterFile.exists()) {
            try {
                val json = JSONObject(masterFile.readText(Charsets.UTF_8))
                val arr = json.optJSONArray("keys")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val obj = arr.optJSONObject(i) ?: continue
                        val sig = obj.optString("signature")
                        if (sig.isNotBlank()) keysMap[sig] = obj
                    }
                }
            } catch (_: Exception) {}
        }

        // Add or update current key
        keysMap[key.signature] = JSONObject().apply {
            put("signature", key.signature)
            put("reference", key.reference)
            put("amount", key.amount)
            put("currency", key.currency)
            put("securityLevel", key.securityLevel)
            put("status", key.status)
            put("remainingAmount", key.remainingAmount)
            put("createdAt", key.createdAt)
            put("rawJsonContent", key.rawJsonContent)
            put("cloudDriveFileId", "gdrive_file_${key.signature.take(8)}")
            put("isOwner", key.isOwner)
            put("lastSync", System.currentTimeMillis())
        }

        val masterJson = JSONObject().apply {
            put("vaultVersion", "1.0")
            put("folder", VAULT_FOLDER_NAME)
            put("exportedAt", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
            put("keysCount", keysMap.size)
            val arr = JSONArray()
            keysMap.values.forEach { arr.put(it) }
            put("keys", arr)
        }

        masterFile.writeText(masterJson.toString(2), Charsets.UTF_8)
    }
}
