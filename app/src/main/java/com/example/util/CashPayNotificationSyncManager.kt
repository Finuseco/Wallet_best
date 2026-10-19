package com.example.util

import android.content.Context
import android.util.Log
import com.example.data.local.CashPayDao
import com.example.data.local.NotificationEntity
import com.example.data.model.NotificationDto
import com.example.data.remote.ApiClient
import com.example.data.remote.CashPayApiService
import com.example.data.remote.NotificationWebSocketClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class CashPayNotificationSyncManager(
    private val context: Context,
    private val apiService: CashPayApiService,
    private val dao: CashPayDao
) {
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var syncJob: Job? = null

    private val wsClient = NotificationWebSocketClient { dto ->
        handleIncomingNotification(dto)
    }

    init {
        CashPayNotificationHelper.initNotificationChannel(context)
        observeSession()
    }

    private fun observeSession() {
        syncScope.launch {
            dao.getSession().collectLatest { session ->
                val token = session?.sessionToken
                if (!token.isNullOrBlank() && !token.startsWith("token_")) {
                    ApiClient.sessionToken = token
                    wsClient.connect(token)
                    startPeriodicSync()
                    // Immediate sync when session is established or restored
                    syncNow()
                } else {
                    wsClient.disconnect()
                    stopPeriodicSync()
                }
            }
        }
    }

    fun syncNow(forcePushOnSessionUnlock: Boolean = false) {
        syncScope.launch {
            try {
                val token = ApiClient.sessionToken
                if (token.isNullOrBlank() || token.startsWith("token_")) return@launch

                val response = apiService.getNotifications(limit = 20)
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    val dtos = response.body()!!.notifications
                    val entities = dtos.map { dto ->
                        NotificationEntity(
                            id = dto.id,
                            title = dto.title ?: "Notification CashPay All",
                            description = dto.description ?: "",
                            timestamp = dto.timestamp ?: "",
                            read = dto.read,
                            type = dto.type ?: "general",
                            link = dto.link
                        )
                    }
                    dao.insertNotifications(entities)

                    // 1. Post push notifications only for genuinely new unshown and unread notifications
                    dtos.forEach { dto ->
                        if (!dto.read && !CashPayNotificationHelper.isNotificationAlreadyShown(context, dto.id)) {
                            CashPayNotificationHelper.showPushNotification(context, dto)
                        }
                    }

                    // Update Home Screen Widget with latest notifications
                    try {
                        com.example.widget.NotificationWidgetProvider.updateAllWidgets(context)
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                Log.w("NotificationSync", "Erreur lors de la synchronisation des notifications: ${e.message}")
            }
        }
    }

    private fun startPeriodicSync() {
        if (syncJob?.isActive == true) return
        syncJob = syncScope.launch {
            while (isActive) {
                delay(15_000) // Poll every 15 seconds as background fallback for real-time push
                syncNow(forcePushOnSessionUnlock = false)
            }
        }
    }

    private fun stopPeriodicSync() {
        syncJob?.cancel()
        syncJob = null
    }

    private fun handleIncomingNotification(dto: NotificationDto) {
        syncScope.launch {
            val entity = NotificationEntity(
                id = dto.id,
                title = dto.title ?: "Notification CashPay All",
                description = dto.description ?: "",
                timestamp = dto.timestamp ?: "",
                read = dto.read,
                type = dto.type ?: "general",
                link = dto.link
            )
            dao.insertNotification(entity)

            // Trigger real-time system push notification
            CashPayNotificationHelper.showPushNotification(context, dto)
            try {
                com.example.widget.NotificationWidgetProvider.updateAllWidgets(context)
            } catch (_: Exception) {}
        }
    }

    fun onPinConfirmed() {
        // Triggered immediately when PIN is entered and session is unlocked
        syncNow(forcePushOnSessionUnlock = true)
        val token = ApiClient.sessionToken
        if (!token.isNullOrBlank()) {
            wsClient.connect(token)
        }
    }
}
