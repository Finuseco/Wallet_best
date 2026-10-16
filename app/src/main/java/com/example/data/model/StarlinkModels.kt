package com.example.data.model

import com.squareup.moshi.Json

data class StarlinkClientLinkRequest(
    @Json(name = "kitSerialNumber") val kitSerialNumber: String
)

data class StarlinkSubCustomerRequest(
    @Json(name = "terminalId") val terminalId: String,
    @Json(name = "name") val name: String,
    @Json(name = "walletId") val walletId: String,
    @Json(name = "pinCode") val pinCode: String,
    @Json(name = "macAddress") val macAddress: String,
    @Json(name = "expiryDate") val expiryDate: String,
    @Json(name = "dataLimitGb") val dataLimitGb: Int
)

data class StarlinkBillingPayRequest(
    @Json(name = "terminalId") val terminalId: String,
    @Json(name = "planName") val planName: String,
    @Json(name = "pin") val pin: String
)

data class StarlinkSaveResellerConfigRequest(
    @Json(name = "companyName") val companyName: String,
    @Json(name = "merchantWalletId") val merchantWalletId: String,
    @Json(name = "starlinkClientId") val starlinkClientId: String,
    @Json(name = "starlinkClientSecret") val starlinkClientSecret: String
)

data class StarlinkRegisterTerminalRequest(
    @Json(name = "resellerId") val resellerId: String,
    @Json(name = "deviceId") val deviceId: String,
    @Json(name = "nickname") val nickname: String,
    @Json(name = "ownerWalletId") val ownerWalletId: String,
    @Json(name = "serialNumber") val serialNumber: String
)

data class StarlinkBaseResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

data class StarlinkResellerLinkResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "accountName") val accountName: String? = null,
    @Json(name = "config") val config: Map<String, Any>? = null,
    @Json(name = "error") val error: String? = null
)

data class StarlinkResellerConfigResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "configured") val configured: Boolean = false,
    @Json(name = "data") val data: Map<String, Any>? = null,
    @Json(name = "error") val error: String? = null
)

data class StarlinkTerminalDetailDto(
    @Json(name = "userTerminalId") val userTerminalId: String = "",
    @Json(name = "nickname") val nickname: String? = null,
    @Json(name = "serialNumber") val serialNumber: String? = null,
    @Json(name = "status") val status: String? = "ONLINE",
    @Json(name = "ownerWalletId") val ownerWalletId: String? = null,
    @Json(name = "resellerId") val resellerId: String? = null
)

data class StarlinkResellerTerminalsResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "data") val data: List<StarlinkTerminalDetailDto> = emptyList(),
    @Json(name = "error") val error: String? = null
)

data class StarlinkTerminalStatusData(
    @Json(name = "userTerminalId") val userTerminalId: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "signalQuality") val signalQuality: String? = null,
    @Json(name = "downlinkThroughputBps") val downlinkThroughputBps: Double? = null,
    @Json(name = "uplinkThroughputBps") val uplinkThroughputBps: Double? = null,
    @Json(name = "pingLatencyMs") val pingLatencyMs: Double? = null,
    @Json(name = "obstructionFraction") val obstructionFraction: Double? = null,
    @Json(name = "softwareVersion") val softwareVersion: String? = null,
    @Json(name = "alerts") val alerts: List<String> = emptyList(),
    @Json(name = "capacity") val capacity: String? = null,
    @Json(name = "downloadSpeed") val downloadSpeed: String? = null,
    @Json(name = "uploadSpeed") val uploadSpeed: String? = null
) {
    val formattedDownloadMbps: String
        get() {
            if (!downloadSpeed.isNullOrBlank()) return downloadSpeed
            if (downlinkThroughputBps != null && downlinkThroughputBps > 0) {
                val mbps = downlinkThroughputBps / 1_000_000.0
                return String.format(java.util.Locale.US, "%.1f Mbps", mbps)
            }
            if (!capacity.isNullOrBlank()) return capacity
            return "--"
        }

    val formattedUploadMbps: String
        get() {
            if (!uploadSpeed.isNullOrBlank()) return uploadSpeed
            if (uplinkThroughputBps != null && uplinkThroughputBps > 0) {
                val mbps = uplinkThroughputBps / 1_000_000.0
                return String.format(java.util.Locale.US, "%.1f Mbps", mbps)
            }
            return "--"
        }

    val formattedLatency: String
        get() {
            if (pingLatencyMs != null && pingLatencyMs > 0) {
                return "${pingLatencyMs.toInt()} ms"
            }
            return "--"
        }

    val formattedObstruction: String
        get() {
            if (obstructionFraction != null) {
                val pct = (obstructionFraction * 100).toInt()
                return if (pct == 0) "0% (Dégagé)" else "$pct% obstruction"
            }
            return "Normal"
        }
}

data class StarlinkTerminalStatusResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "data") val data: StarlinkTerminalStatusData? = null,
    @Json(name = "error") val error: String? = null
)
