package com.example.data.model

data class BtcMarketTelemetry(
    val priceUsd: Double = 88450.0,
    val change24hPercent: Double = 2.45,
    val high24hUsd: Double = 89200.0,
    val low24hUsd: Double = 86100.0,
    val volumeBtc: Double = 28450.0,
    val trend: String = "BULLISH",
    val chartPoints: List<Float> = emptyList(),
    val lastUpdated: String = ""
)

data class PeriodAnalyticsSummary(
    val periodLabel: String = "1M",
    val totalIncomingUsd: Double = 0.0,
    val totalOutgoingUsd: Double = 0.0,
    val netBalanceUsd: Double = 0.0,
    val totalFeesUsd: Double = 0.0,
    val totalBtcSent: Double = 0.0,
    val totalBtcReceived: Double = 0.0,
    val transactionCount: Int = 0,
    val chartPoints: List<Float> = emptyList(),
    val dateLabels: List<String> = emptyList()
)
