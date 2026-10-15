package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://app.cashpay-all.com/"

    @Volatile
    var sessionToken: String? = null

    @Volatile
    var tokenProvider: (() -> String?)? = null

    @Volatile
    var currentWalletId: String? = null

    @Volatile
    var currentUserId: Long? = null

    @Volatile
    var currentPhone: String? = null

    @Volatile
    var currentUserRole: String? = null

    @Volatile
    var onTokenUpdated: ((String) -> Unit)? = null

    private val cookieStore = ConcurrentHashMap<String, MutableMap<String, Cookie>>()

    val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val hostMap = cookieStore.getOrPut(url.host) { mutableMapOf() }
            for (c in cookies) {
                hostMap[c.name] = c
                val nameLower = c.name.lowercase()
                if (nameLower == "cashpay-token" || nameLower == "token" || nameLower.contains("session")) {
                    val tokenVal = c.value
                    if (tokenVal.isNotBlank() && !tokenVal.startsWith("token_")) {
                        sessionToken = tokenVal
                        onTokenUpdated?.invoke(tokenVal)
                    }
                }
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            val hostMap = cookieStore[url.host] ?: return emptyList()
            val now = System.currentTimeMillis()
            return hostMap.values.filter { it.expiresAt > now }
        }
    }

    fun getStoredCookieToken(): String? {
        val hostMap = cookieStore["app.cashpay-all.com"] ?: return null
        return hostMap["cashpay-token"]?.value
            ?: hostMap["token"]?.value
            ?: hostMap["next-auth.session-token"]?.value
            ?: hostMap.values.firstOrNull { it.name.contains("token", ignoreCase = true) }?.value
    }

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()

        val rawToken = sessionToken ?: tokenProvider?.invoke()
        val isValidToken = !rawToken.isNullOrBlank() && !rawToken.startsWith("token_")

        builder.addHeader("Accept", "application/json")
        builder.addHeader("User-Agent", "CashPay-Android/2.0")

        if (isValidToken) {
            val token = rawToken!!
            if (sessionToken == null) {
                sessionToken = token
            }
            builder.addHeader("Cookie", "cashpay-token=$token; token=$token")
            builder.addHeader("cashpay-token", token)
            builder.addHeader("Authorization", "Bearer $token")
            builder.addHeader("x-access-token", token)
        }

        // Inject Agent & User identification headers
        currentWalletId?.takeIf { it.isNotBlank() }?.let { wallet ->
            builder.addHeader("x-wallet-id", wallet)
        }
        currentUserId?.takeIf { it > 0 }?.let { uid ->
            builder.addHeader("x-user-id", uid.toString())
        }
        currentPhone?.takeIf { it.isNotBlank() }?.let { phone ->
            builder.addHeader("x-phone", phone)
        }
        currentUserRole?.takeIf { it.isNotBlank() }?.let { role ->
            builder.addHeader("x-role", role)
            if (role.equals("agent", ignoreCase = true)) {
                builder.addHeader("x-is-agent", "true")
                builder.addHeader("x-agent", "true")
            }
        }

        val response = chain.proceed(builder.build())

        // Capture token from response headers
        val headerToken = response.header("cashpay-token")
            ?: response.header("token")
            ?: response.header("x-access-token")
            ?: response.header("Authorization")?.removePrefix("Bearer ")?.trim()

        if (!headerToken.isNullOrBlank() && !headerToken.startsWith("token_")) {
            sessionToken = headerToken
            onTokenUpdated?.invoke(headerToken)
        }

        // Capture cookie if the server sends Set-Cookie
        val cookies = response.headers("Set-Cookie")
        for (cookie in cookies) {
            if (cookie.contains("cashpay-token=")) {
                val extracted = cookie.substringAfter("cashpay-token=").substringBefore(";")
                if (extracted.isNotBlank() && !extracted.startsWith("token_")) {
                    sessionToken = extracted
                    onTokenUpdated?.invoke(extracted)
                }
            } else if (cookie.contains("token=")) {
                val extracted = cookie.substringAfter("token=").substringBefore(";")
                if (extracted.isNotBlank() && !extracted.startsWith("token_") && sessionToken == null) {
                    sessionToken = extracted
                    onTokenUpdated?.invoke(extracted)
                }
            }
        }

        response
    }

    val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    val apiService: CashPayApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CashPayApiService::class.java)
    }
}
