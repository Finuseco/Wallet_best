package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.NotificationDto

object CashPayNotificationHelper {

    const val CHANNEL_ID = "cashpay_push_notifications"
    private const val CHANNEL_NAME = "Transactions et Alertes CashPay All"
    private const val CHANNEL_DESCRIPTION = "Notifications push en arrière-plan pour les transferts, paiements, dépôts et alertes de compte"
    private const val PREFS_NOTIFS = "cashpay_push_prefs"
    private const val KEY_LAST_SHOWN_ID = "last_shown_notification_id"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = Color.parseColor("#0066FF")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setShowBadge(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun showPushNotification(
        context: Context,
        title: String?,
        message: String?,
        notificationId: Long = System.currentTimeMillis(),
        type: String? = "general"
    ) {
        initNotificationChannel(context)

        // Check POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("CashPayNotification", "Permission POST_NOTIFICATIONS non accordée.")
                return
            }
        }

        val cleanTitle = when {
            !title.isNullOrBlank() -> title.trim()
            type == "pos_sale" -> "Vente POS Encaissée"
            type == "deposit" -> "Dépôt Confirmé"
            type == "withdraw" -> "Retrait Validé"
            type == "loan" -> "Notification Prêt / Crédit"
            type == "starlink" -> "Notification Starlink"
            type == "new_message" -> "Nouveau Message CashPay"
            else -> "Notification CashPay All"
        }

        val cleanMessage = if (!message.isNullOrBlank()) message.trim() else "Vous avez une nouvelle notification."

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("opened_from_push", true)
            putExtra("notification_id", notificationId)
            putExtra("notification_type", type)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            (notificationId % 100000).toInt(),
            intent,
            pendingIntentFlags
        )

        val smallIcon = R.drawable.ic_cashpay_favicon_white

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIcon)
            .setContentTitle(cleanTitle)
            .setContentText(cleanMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cleanMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(Color.parseColor("#000E38"))
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        try {
            val safeId = (notificationId % 100000).toInt()
            NotificationManagerCompat.from(context).notify(safeId, builder.build())
            markNotificationAsShown(context, notificationId)
            Log.d("CashPayNotification", "Push notification affichée avec succès: $cleanTitle")
        } catch (e: SecurityException) {
            Log.w("CashPayNotification", "SecurityException lors de l'affichage de la notification: ${e.message}")
        } catch (e: Exception) {
            Log.e("CashPayNotification", "Erreur affichage notification: ${e.message}", e)
        }
    }

    fun showPushNotification(context: Context, dto: NotificationDto) {
        showPushNotification(
            context = context,
            title = dto.title,
            message = dto.description,
            notificationId = if (dto.id > 0) dto.id else System.currentTimeMillis(),
            type = dto.type
        )
    }

    fun markNotificationAsShown(context: Context, id: Long) {
        val prefs = context.getSharedPreferences(PREFS_NOTIFS, Context.MODE_PRIVATE)
        val shownSet = prefs.getStringSet("shown_ids", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        shownSet.add(id.toString())
        prefs.edit().putStringSet("shown_ids", shownSet).apply()
    }

    fun isNotificationAlreadyShown(context: Context, id: Long): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NOTIFS, Context.MODE_PRIVATE)
        val shownSet = prefs.getStringSet("shown_ids", emptySet()) ?: emptySet()
        return shownSet.contains(id.toString())
    }
}
