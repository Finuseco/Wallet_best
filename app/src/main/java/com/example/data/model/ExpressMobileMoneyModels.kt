package com.example.data.model

import com.example.R

data class MobileMoneyOperatorInfo(
    val code: String,
    val name: String,
    val brandColor: Long,
    val logoRes: Int? = null,
    val badge: String? = null
)

data class MobileMoneyCountry(
    val code: String, // e.g. "CD", "CG", "CI", "SN", "CM", "KE", "RW", etc.
    val name: String,
    val dialCode: String,
    val flagEmoji: String,
    val operators: List<MobileMoneyOperatorInfo>,
    val supportedCurrencies: List<String>,
    val isLocalDrc: Boolean = false,
    val infoNotice: String? = null
)

object ExpressMobileMoneyData {

    val DRC = MobileMoneyCountry(
        code = "CD",
        name = "RD Congo",
        dialCode = "+243",
        flagEmoji = "🇨🇩",
        isLocalDrc = true,
        operators = listOf(
            MobileMoneyOperatorInfo("MPESA", "M-Pesa (Vodacom)", 0xFFE60000, R.drawable.logo_mpesa),
            MobileMoneyOperatorInfo("ORANGE", "Orange Money", 0xFFFF6600, R.drawable.logo_orange_money),
            MobileMoneyOperatorInfo("AIRTEL", "Airtel Money", 0xFFED1C24, R.drawable.logo_airtel_money),
            MobileMoneyOperatorInfo("AFRI", "Afrimoney", 0xFF009639, R.drawable.afrimoney)
        ),
        supportedCurrencies = listOf("USD", "CDF", "EUR"),
        infoNotice = "Pour la RDC, le Franc Congolais (CDF) et le Dollar (USD) sont pleinement supportés pour les retraits locaux."
    )

    val AFRICAN_COUNTRIES: List<MobileMoneyCountry> = listOf(
        DRC,
        MobileMoneyCountry(
            code = "CG",
            name = "Congo-Brazzaville",
            dialCode = "+242",
            flagEmoji = "🇨🇬",
            operators = listOf(
                MobileMoneyOperatorInfo("MTN", "MTN MoMo", 0xFFFFCC00, R.drawable.logo_mtn_money),
                MobileMoneyOperatorInfo("AIRTEL", "Airtel Money", 0xFFED1C24, R.drawable.logo_airtel_money)
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : débité en USD ou EUR et reversé sur le compte Mobile Money local."
        ),
        MobileMoneyCountry(
            code = "CI",
            name = "Côte d'Ivoire",
            dialCode = "+225",
            flagEmoji = "🇨🇮",
            operators = listOf(
                MobileMoneyOperatorInfo("ORANGE", "Orange Money", 0xFFFF6600, R.drawable.logo_orange_money),
                MobileMoneyOperatorInfo("MTN", "MTN MoMo", 0xFFFFCC00, R.drawable.logo_mtn_money),
                MobileMoneyOperatorInfo("WAVE", "Wave", 0xFF1BA9E1, badge = "Wave"),
                MobileMoneyOperatorInfo("MOOV", "Moov Money", 0xFF004F9F, badge = "Moov")
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "SN",
            name = "Sénégal",
            dialCode = "+221",
            flagEmoji = "🇸🇳",
            operators = listOf(
                MobileMoneyOperatorInfo("WAVE", "Wave", 0xFF1BA9E1, badge = "Wave"),
                MobileMoneyOperatorInfo("ORANGE", "Orange Money", 0xFFFF6600, R.drawable.logo_orange_money),
                MobileMoneyOperatorInfo("FREE", "Free Money", 0xFFE02B20, badge = "Free")
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "CM",
            name = "Cameroun",
            dialCode = "+237",
            flagEmoji = "🇨🇲",
            operators = listOf(
                MobileMoneyOperatorInfo("MTN", "MTN MoMo", 0xFFFFCC00, R.drawable.logo_mtn_money),
                MobileMoneyOperatorInfo("ORANGE", "Orange Money", 0xFFFF6600, R.drawable.logo_orange_money)
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "KE",
            name = "Kenya",
            dialCode = "+254",
            flagEmoji = "🇰🇪",
            operators = listOf(
                MobileMoneyOperatorInfo("MPESA", "M-Pesa (Safaricom)", 0xFF00A859, R.drawable.logo_mpesa),
                MobileMoneyOperatorInfo("AIRTEL", "Airtel Money", 0xFFED1C24, R.drawable.logo_airtel_money)
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "RW",
            name = "Rwanda",
            dialCode = "+250",
            flagEmoji = "🇷🇼",
            operators = listOf(
                MobileMoneyOperatorInfo("MTN", "MTN MoMo", 0xFFFFCC00, R.drawable.logo_mtn_money),
                MobileMoneyOperatorInfo("AIRTEL", "Airtel Money", 0xFFED1C24, R.drawable.logo_airtel_money)
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "UG",
            name = "Ouganda",
            dialCode = "+256",
            flagEmoji = "🇺🇬",
            operators = listOf(
                MobileMoneyOperatorInfo("MTN", "MTN MoMo", 0xFFFFCC00, R.drawable.logo_mtn_money),
                MobileMoneyOperatorInfo("AIRTEL", "Airtel Money", 0xFFED1C24, R.drawable.logo_airtel_money)
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "TZ",
            name = "Tanzanie",
            dialCode = "+255",
            flagEmoji = "🇹🇿",
            operators = listOf(
                MobileMoneyOperatorInfo("MPESA", "Vodacom M-Pesa", 0xFFE60000, R.drawable.logo_mpesa),
                MobileMoneyOperatorInfo("AIRTEL", "Airtel Money", 0xFFED1C24, R.drawable.logo_airtel_money),
                MobileMoneyOperatorInfo("TIGO", "Tigo Pesa", 0xFF00377B, badge = "Tigo")
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "GA",
            name = "Gabon",
            dialCode = "+241",
            flagEmoji = "🇬🇦",
            operators = listOf(
                MobileMoneyOperatorInfo("AIRTEL", "Airtel Money", 0xFFED1C24, R.drawable.logo_airtel_money),
                MobileMoneyOperatorInfo("MOOV", "Moov Money", 0xFF004F9F, badge = "Moov")
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "BJ",
            name = "Bénin",
            dialCode = "+229",
            flagEmoji = "🇧🇯",
            operators = listOf(
                MobileMoneyOperatorInfo("MTN", "MTN MoMo", 0xFFFFCC00, R.drawable.logo_mtn_money),
                MobileMoneyOperatorInfo("MOOV", "Moov Money", 0xFF004F9F, badge = "Moov")
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "TG",
            name = "Togo",
            dialCode = "+228",
            flagEmoji = "🇹🇬",
            operators = listOf(
                MobileMoneyOperatorInfo("TMONEY", "T-Money", 0xFF008751, badge = "T-Money"),
                MobileMoneyOperatorInfo("MOOV", "Moov Money", 0xFF004F9F, badge = "Moov")
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "BF",
            name = "Burkina Faso",
            dialCode = "+226",
            flagEmoji = "🇧🇫",
            operators = listOf(
                MobileMoneyOperatorInfo("ORANGE", "Orange Money", 0xFFFF6600, R.drawable.logo_orange_money),
                MobileMoneyOperatorInfo("MOOV", "Moov Money", 0xFF004F9F, badge = "Moov")
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "ML",
            name = "Mali",
            dialCode = "+223",
            flagEmoji = "🇲🇱",
            operators = listOf(
                MobileMoneyOperatorInfo("ORANGE", "Orange Money", 0xFFFF6600, R.drawable.logo_orange_money),
                MobileMoneyOperatorInfo("MOOV", "Moov Money", 0xFF004F9F, badge = "Moov")
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "GN",
            name = "Guinée Conakry",
            dialCode = "+224",
            flagEmoji = "🇬🇳",
            operators = listOf(
                MobileMoneyOperatorInfo("ORANGE", "Orange Money", 0xFFFF6600, R.drawable.logo_orange_money),
                MobileMoneyOperatorInfo("MTN", "MTN MoMo", 0xFFFFCC00, R.drawable.logo_mtn_money)
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "GH",
            name = "Ghana",
            dialCode = "+233",
            flagEmoji = "🇬🇭",
            operators = listOf(
                MobileMoneyOperatorInfo("MTN", "MTN MoMo", 0xFFFFCC00, R.drawable.logo_mtn_money),
                MobileMoneyOperatorInfo("TELECEL", "Telecel Cash", 0xFFE60000, badge = "Telecel"),
                MobileMoneyOperatorInfo("AT", "AT Money", 0xFF002B49, badge = "AT Money")
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "NG",
            name = "Nigeria",
            dialCode = "+234",
            flagEmoji = "🇳🇬",
            operators = listOf(
                MobileMoneyOperatorInfo("OPAY", "OPay", 0xFF14B8A6, badge = "OPay"),
                MobileMoneyOperatorInfo("PALMPAY", "PalmPay", 0xFF7C3AED, badge = "PalmPay"),
                MobileMoneyOperatorInfo("MTN", "MTN MoMo PSB", 0xFFFFCC00, R.drawable.logo_mtn_money),
                MobileMoneyOperatorInfo("AIRTEL", "SmartCash PSB", 0xFFED1C24, R.drawable.logo_airtel_money)
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "ZA",
            name = "Afrique du Sud",
            dialCode = "+27",
            flagEmoji = "🇿🇦",
            operators = listOf(
                MobileMoneyOperatorInfo("MPESA", "Vodapay / M-Pesa", 0xFFE60000, R.drawable.logo_mpesa),
                MobileMoneyOperatorInfo("MTN", "MTN MoMo", 0xFFFFCC00, R.drawable.logo_mtn_money)
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "AO",
            name = "Angola",
            dialCode = "+244",
            flagEmoji = "🇦🇴",
            operators = listOf(
                MobileMoneyOperatorInfo("UNITEL", "Unitel Money", 0xFFFF6600, badge = "Unitel"),
                MobileMoneyOperatorInfo("MULTICAIXA", "Multicaixa Express", 0xFF00377B, badge = "MCX")
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        ),
        MobileMoneyCountry(
            code = "ZM",
            name = "Zambie",
            dialCode = "+260",
            flagEmoji = "🇿🇲",
            operators = listOf(
                MobileMoneyOperatorInfo("AIRTEL", "Airtel Money", 0xFFED1C24, R.drawable.logo_airtel_money),
                MobileMoneyOperatorInfo("MTN", "MTN MoMo", 0xFFFFCC00, R.drawable.logo_mtn_money)
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international : traité en USD ou EUR."
        )
    )

    fun getCountry(codeOrDial: String?): MobileMoneyCountry {
        if (codeOrDial.isNullOrBlank()) return DRC
        val clean = codeOrDial.trim().uppercase()
        val byCode = AFRICAN_COUNTRIES.find { it.code.uppercase() == clean }
        if (byCode != null) return byCode

        val cleanDial = if (clean.startsWith("+")) clean else "+$clean"
        val byDial = AFRICAN_COUNTRIES.find { it.dialCode == cleanDial }
        if (byDial != null) return byDial

        // Default or generic country
        return DRC
    }

    fun getCountryFromCountryDto(dto: CountryDto): MobileMoneyCountry {
        val found = AFRICAN_COUNTRIES.find {
            it.code.equals(dto.code, ignoreCase = true) || it.dialCode == dto.dialCode
        }
        if (found != null) return found

        // Create a generic representation for international countries
        return MobileMoneyCountry(
            code = dto.code,
            name = dto.name,
            dialCode = dto.dialCode,
            flagEmoji = dto.flagEmoji.ifBlank { "🌍" },
            operators = listOf(
                MobileMoneyOperatorInfo("MPESA", "M-Pesa International", 0xFFE60000, R.drawable.logo_mpesa),
                MobileMoneyOperatorInfo("ORANGE", "Orange Money", 0xFFFF6600, R.drawable.logo_orange_money),
                MobileMoneyOperatorInfo("AIRTEL", "Airtel Money", 0xFFED1C24, R.drawable.logo_airtel_money),
                MobileMoneyOperatorInfo("MTN", "MTN MoMo", 0xFFFFCC00, R.drawable.logo_mtn_money),
                MobileMoneyOperatorInfo("WAVE", "Wave Transfer", 0xFF1BA9E1, badge = "Wave")
            ),
            supportedCurrencies = listOf("USD", "EUR"),
            infoNotice = "Envoi international vers ${dto.name} : Traité en devises USD ou EUR."
        )
    }
}
