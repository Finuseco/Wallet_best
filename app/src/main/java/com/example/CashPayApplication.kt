package com.example

import android.app.Application
import com.example.data.local.CashPayDatabase
import com.example.data.remote.ApiClient
import com.example.util.CashPayNotificationHelper
import com.example.util.CashPayNotificationSyncManager

class CashPayApplication : Application() {

    companion object {
        lateinit var instance: CashPayApplication
            private set
    }

    val database by lazy { CashPayDatabase.getDatabase(this) }
    val notificationSyncManager by lazy {
        CashPayNotificationSyncManager(
            context = this,
            apiService = ApiClient.apiService,
            dao = database.cashPayDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Initialize Notification Channel immediately for background & real-time push
        CashPayNotificationHelper.initNotificationChannel(this)
        // Instantiate and warm up NotificationSyncManager
        notificationSyncManager
    }
}
