package com.example.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.R
import com.example.data.local.CashPayDatabase
import com.example.data.local.NotificationEntity

class NotificationWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return NotificationRemoteViewsFactory(applicationContext)
    }
}

class NotificationRemoteViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private var items: List<NotificationEntity> = emptyList()

    override fun onCreate() {
        loadData()
    }

    override fun onDataSetChanged() {
        loadData()
    }

    private fun loadData() {
        try {
            val db = CashPayDatabase.getDatabase(context)
            val notifs = db.cashPayDao().getNotificationsList()
            if (notifs.isNotEmpty()) {
                items = notifs
            } else {
                val txs = db.cashPayDao().getTransactionsList(15)
                items = txs.map { tx ->
                    val isIncoming = tx.direction.lowercase() in listOf("incoming", "credit")
                    val isBtc = tx.currency.equals("BTC", ignoreCase = true)
                    val prefix = if (isIncoming) "+" else "-"
                    val amountStr = if (isBtc) {
                        String.format(java.util.Locale.US, "%.8f", tx.amount).trimEnd('0').trimEnd('.') + " BTC"
                    } else {
                        String.format(java.util.Locale.US, "%,.2f", tx.amount) + " " + tx.currency
                    }
                    val title = if (isBtc) {
                        if (isIncoming) "Bitcoin reçu (Crédit)" else "Transfert Bitcoin envoyé (Débit)"
                    } else {
                        tx.displayName.ifBlank { tx.title }
                    }
                    NotificationEntity(
                        id = tx.id,
                        title = title,
                        description = "$prefix$amountStr • ${tx.status.uppercase()}",
                        timestamp = tx.date,
                        read = false,
                        type = "general"
                    )
                }
            }
        } catch (_: Exception) {
            items = emptyList()
        }
    }

    override fun onDestroy() {
        items = emptyList()
    }

    override fun getCount(): Int = items.size

    override fun getViewAt(position: Int): RemoteViews? {
        if (position < 0 || position >= items.size) return null
        val notif = items[position]

        val views = RemoteViews(context.packageName, R.layout.widget_notification_item)
        views.setTextViewText(R.id.widget_item_title, notif.title.ifBlank { "Notification CashPay" })
        views.setTextViewText(R.id.widget_item_desc, notif.description)

        val formattedTime = if (notif.timestamp.isNotBlank()) {
            if (notif.timestamp.contains("T")) {
                val timePart = notif.timestamp.substringAfter("T").take(5)
                timePart
            } else notif.timestamp.takeLast(8)
        } else ""
        views.setTextViewText(R.id.widget_item_time, formattedTime)

        // FillInIntent for row click
        val fillInIntent = Intent().apply {
            putExtra("notification_id", notif.id)
            putExtra("opened_from_push", true)
        }
        views.setOnClickFillInIntent(R.id.widget_item_container, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long {
        return if (position in items.indices) items[position].id else position.toLong()
    }

    override fun hasStableIds(): Boolean = true
}
