package com.example.data.remote

import android.util.Log
import com.example.data.model.NotificationDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class NotificationWebSocketClient(
    private val onNotificationReceived: (NotificationDto) -> Unit
) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(NotificationDto::class.java)

    fun connect(tokenOverride: String? = null) {
        val token = tokenOverride ?: com.example.data.remote.ApiClient.sessionToken
        if (token.isNullOrBlank()) {
            Log.d("NotificationWS", "Pas de token de session actif, connexion WebSocket ignorée.")
            return
        }

        if (webSocket != null) return

        val request = Request.Builder()
            .url("wss://app.cashpay-all.com/ws")
            .addHeader("Cookie", "cashpay-token=$token")
            .addHeader("cashpay-token", token)
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("NotificationWS", "WebSocket connecté avec succès")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("NotificationWS", "Message WebSocket reçu: $text")
                try {
                    val notif = adapter.fromJson(text)
                    if (notif != null) {
                        onNotificationReceived(notif)
                    }
                } catch (e: Exception) {
                    Log.w("NotificationWS", "Erreur de parsing du message WebSocket: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.w("NotificationWS", "Notification WebSocket indisponible ou fermé: ${t.message}")
                this@NotificationWebSocketClient.webSocket = null
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("NotificationWS", "WebSocket fermé: $reason")
                this@NotificationWebSocketClient.webSocket = null
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }
}
