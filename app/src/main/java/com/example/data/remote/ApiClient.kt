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
    // Format filter only. Authentication and signature validation remain on CashPay Web.
    fun isCashPayToken(value: String?): Boolean = value != null &&
        Regex("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+").matches(value)

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
            if (url.host != "app.cashpay-all.com" || !url.isHttps) return
            val hostMap = cookieStore.getOrPut(url.host) { ConcurrentHashMap() }
            for (c in cookies) {
                hostMap[c.name] = c
                val nameLower = c.name.lowercase()
                if (nameLower == "cashpay-token" || nameLower == "token") {
                    val tokenVal = c.value
                    if (c.expiresAt > System.currentTimeMillis() && isCashPayToken(tokenVal)) {
                        sessionToken = tokenVal
                        onTokenUpdated?.invoke(tokenVal)
                    }
                }
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            if (url.host != "app.cashpay-all.com" || !url.isHttps) return emptyList()
            val hostMap = cookieStore[url.host] ?: return emptyList()
            val now = System.currentTimeMillis()
            return hostMap.values.filter { it.expiresAt > now }
        }
    }

    fun getStoredCookieToken(): String? {
        val hostMap = cookieStore["app.cashpay-all.com"] ?: return null
        val now = System.currentTimeMillis()
        return listOf("cashpay-token", "token").firstNotNullOfOrNull { name ->
            hostMap[name]?.takeIf { it.expiresAt > now }?.value?.takeIf { isCashPayToken(it) }
        }
    }

    fun clearSession() {
        sessionToken = null
        currentWalletId = null
        currentUserId = null
        currentPhone = null
        currentUserRole = null
        cookieStore.clear()
    }

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (com.example.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        if (original.url.host != "app.cashpay-all.com" || !original.url.isHttps) return@Interceptor chain.proceed(original)
        val builder = original.newBuilder()

        val rawToken = sessionToken ?: tokenProvider?.invoke()
        val isValidToken = isCashPayToken(rawToken)

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

        if (isCashPayToken(headerToken)) {
            sessionToken = headerToken
            onTokenUpdated?.invoke(headerToken!!)
        }

        // CookieJar handles exact CashPay cookie names; unrelated session cookies are never credentials.

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
