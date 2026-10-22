package com.example.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.local.TransactionEntity

data class TransactionTypeInfo(
    val code: String,
    val translationKey: String,
    val nameFr: String,
    val shortName: String,
    val description: String,
    val nature: String, // "P2P", "AGENT", "SERVICE", "LOAN", "B2B", "SECURITY", "INVESTMENT"
    val icon: ImageVector,
    val isPersonToPerson: Boolean
)

object TransactionTypeMapper {

    val MAPPINGS: Map<String, TransactionTypeInfo> = mapOf(
        "TR" to TransactionTypeInfo(
            code = "TR",
            translationKey = "transaction.transfer",
            nameFr = "Transfert",
            shortName = "Transfert",
            description = "Transfert d'argent entre utilisateurs CashPay",
            nature = "P2P",
            icon = Icons.Default.SwapHoriz,
            isPersonToPerson = true
        ),
        "BTC_TRANSFER" to TransactionTypeInfo(
            code = "BTC_TRANSFER",
            translationKey = "transaction.btc_transfer",
            nameFr = "Transfert Bitcoin",
            shortName = "Transfert BTC",
            description = "Transfert de Bitcoin vers une adresse Bitcoin",
            nature = "CRYPTO",
            icon = Icons.Default.CurrencyBitcoin,
            isPersonToPerson = false
        ),
        "WD_CRYPTO_MONEY" to TransactionTypeInfo(
            code = "WD_CRYPTO_MONEY",
            translationKey = "transaction.wd_crypto_money",
            nameFr = "Retrait Bitcoin",
            shortName = "Retrait BTC",
            description = "Retrait de cryptomonnaie Bitcoin",
            nature = "CRYPTO",
            icon = Icons.Default.CurrencyBitcoin,
            isPersonToPerson = false
        ),
        "WD_CRYPTO" to TransactionTypeInfo(
            code = "WD_CRYPTO",
            translationKey = "transaction.wd_crypto",
            nameFr = "Retrait Bitcoin",
            shortName = "Retrait BTC",
            description = "Retrait de cryptomonnaie Bitcoin",
            nature = "CRYPTO",
            icon = Icons.Default.CurrencyBitcoin,
            isPersonToPerson = false
        ),
        "INTERNAL_CASHPAY" to TransactionTypeInfo(
            code = "INTERNAL_CASHPAY",
            translationKey = "transaction.internal_cashpay",
            nameFr = "Transfert BTC interne",
            shortName = "Transfert interne",
            description = "Transfert Bitcoin interne entre comptes CashPay All",
            nature = "CRYPTO",
            icon = Icons.Default.CurrencyBitcoin,
            isPersonToPerson = true
        ),
        "CRYPTO_WITHDRAWAL" to TransactionTypeInfo(
            code = "CRYPTO_WITHDRAWAL",
            translationKey = "transaction.crypto_withdrawal",
            nameFr = "Retrait Bitcoin",
            shortName = "Retrait BTC",
            description = "Retrait de cryptomonnaie vers un portefeuille externe",
            nature = "CRYPTO",
            icon = Icons.Default.CurrencyBitcoin,
            isPersonToPerson = false
        ),
        "CRYPTO_TRANSFER" to TransactionTypeInfo(
            code = "CRYPTO_TRANSFER",
            translationKey = "transaction.crypto_transfer",
            nameFr = "Transfert de cryptomonnaie",
            shortName = "Transfert crypto",
            description = "Transfert de cryptomonnaie CashPay",
            nature = "CRYPTO",
            icon = Icons.Default.CurrencyBitcoin,
            isPersonToPerson = false
        ),
        "EXCHANGE" to TransactionTypeInfo(
            code = "EXCHANGE",
            translationKey = "transaction.currency_exchange",
            nameFr = "Échange de devises",
            shortName = "Change",
            description = "Conversion d'une devise vers une autre",
            nature = "SERVICE",
            icon = Icons.Default.CurrencyExchange,
            isPersonToPerson = false
        ),
        "DP_AGENT" to TransactionTypeInfo(
            code = "DP_AGENT",
            translationKey = "transaction.agent_deposit",
            nameFr = "Dépôt via agent",
            shortName = "Dépôt",
            description = "Dépôt d'argent effectué via un agent CashPay",
            nature = "AGENT",
            icon = Icons.Default.Storefront,
            isPersonToPerson = false
        ),
        "DP" to TransactionTypeInfo(
            code = "DP",
            translationKey = "transaction.agent_deposit",
            nameFr = "Dépôt via agent",
            shortName = "Dépôt",
            description = "Dépôt d'argent effectué auprès d'un agent ou point CashPay",
            nature = "AGENT",
            icon = Icons.Default.Storefront,
            isPersonToPerson = false
        ),
        "LR_AGENT" to TransactionTypeInfo(
            code = "LR_AGENT",
            translationKey = "transaction.loan_repayment_agent",
            nameFr = "Remboursement de prêt via agent",
            shortName = "Remboursement de prêt",
            description = "Remboursement d'un prêt effectué par l'intermédiaire d'un agent",
            nature = "LOAN",
            icon = Icons.Default.AccountBalance,
            isPersonToPerson = false
        ),
        "LR" to TransactionTypeInfo(
            code = "LR",
            translationKey = "transaction.loan_repayment",
            nameFr = "Remboursement de prêt",
            shortName = "Remboursement",
            description = "Remboursement d'un prêt ou d'une échéance de prêt",
            nature = "LOAN",
            icon = Icons.Default.AccountBalance,
            isPersonToPerson = false
        ),
        "WD" to TransactionTypeInfo(
            code = "WD",
            translationKey = "transaction.cash_withdrawal",
            nameFr = "Retrait",
            shortName = "Retrait",
            description = "Retrait d'argent CashPay, notamment auprès d'un agent",
            nature = "AGENT",
            icon = Icons.Default.LocalAtm,
            isPersonToPerson = false
        ),
        "WD_AGENT" to TransactionTypeInfo(
            code = "WD_AGENT",
            translationKey = "transaction.agent_withdrawal",
            nameFr = "Retrait via agent",
            shortName = "Retrait",
            description = "Retrait effectué avec intervention d'un agent CashPay",
            nature = "AGENT",
            icon = Icons.Default.LocalAtm,
            isPersonToPerson = false
        ),
        "FE" to TransactionTypeInfo(
            code = "FE",
            translationKey = "transaction.fee",
            nameFr = "Frais de service",
            shortName = "Frais",
            description = "Frais facturés pour l'utilisation d'un service CashPay",
            nature = "SERVICE",
            icon = Icons.Default.ReceiptLong,
            isPersonToPerson = false
        ),
        "HSM_LOCK" to TransactionTypeInfo(
            code = "HSM_LOCK",
            translationKey = "transaction.hsm_lock",
            nameFr = "Mise en coffre sécurisé",
            shortName = "Coffre sécurisé",
            description = "Opération de sécurisation ou de mise en coffre d'une clé/support matériel",
            nature = "SECURITY",
            icon = Icons.Default.Lock,
            isPersonToPerson = false
        ),
        "AGENT_SUB" to TransactionTypeInfo(
            code = "AGENT_SUB",
            translationKey = "transaction.agent_subscription",
            nameFr = "Abonnement agent",
            shortName = "Abonnement",
            description = "Souscription ou régularisation d'un abonnement agent",
            nature = "SERVICE",
            icon = Icons.Default.Storefront,
            isPersonToPerson = false
        ),
        "TICKET_BUY" to TransactionTypeInfo(
            code = "TICKET_BUY",
            translationKey = "transaction.ticket_purchase",
            nameFr = "Achat de billet",
            shortName = "Billet",
            description = "Achat d'un ou plusieurs billets pour un événement",
            nature = "SERVICE",
            icon = Icons.Default.ConfirmationNumber,
            isPersonToPerson = false
        ),
        "MKTG_FEE" to TransactionTypeInfo(
            code = "MKTG_FEE",
            translationKey = "transaction.marketing_fee",
            nameFr = "Frais marketing",
            shortName = "Frais marketing",
            description = "Paiement associé à une campagne ou à une opération marketing",
            nature = "SERVICE",
            icon = Icons.Default.Campaign,
            isPersonToPerson = false
        ),
        "POS_SALE" to TransactionTypeInfo(
            code = "POS_SALE",
            translationKey = "transaction.pos_purchase",
            nameFr = "Achat marchand",
            shortName = "Achat",
            description = "Paiement d'un achat effectué auprès d'un marchand / boutique",
            nature = "SERVICE",
            icon = Icons.Default.ShoppingBag,
            isPersonToPerson = false
        ),
        "RW" to TransactionTypeInfo(
            code = "RW",
            translationKey = "transaction.fund_recovery",
            nameFr = "Récupération de fonds",
            shortName = "Récupération",
            description = "Récupération de fonds à partir d'un mécanisme de sécurité / coffre",
            nature = "SECURITY",
            icon = Icons.Default.Shield,
            isPersonToPerson = false
        ),
        "LP" to TransactionTypeInfo(
            code = "LP",
            translationKey = "transaction.loan_disbursement",
            nameFr = "Décaissement de prêt",
            shortName = "Décaissement",
            description = "Versement des fonds correspondant à un prêt approuvé",
            nature = "LOAN",
            icon = Icons.Default.Payments,
            isPersonToPerson = false
        ),
        "B2B_SMS" to TransactionTypeInfo(
            code = "B2B_SMS",
            translationKey = "transaction.sms_service_purchase",
            nameFr = "Achat de services SMS",
            shortName = "Service SMS",
            description = "Achat de crédits ou services SMS pour un compte B2B",
            nature = "B2B",
            icon = Icons.Default.Sms,
            isPersonToPerson = false
        ),
        "B2B_WHATSAPP" to TransactionTypeInfo(
            code = "B2B_WHATSAPP",
            translationKey = "transaction.whatsapp_service_purchase",
            nameFr = "Achat de services WhatsApp",
            shortName = "Service WhatsApp",
            description = "Achat de services WhatsApp pour un compte B2B",
            nature = "B2B",
            icon = Icons.Default.Chat,
            isPersonToPerson = false
        ),
        "DAT_SUBMIT" to TransactionTypeInfo(
            code = "DAT_SUBMIT",
            translationKey = "transaction.term_deposit_subscription",
            nameFr = "Souscription à un dépôt à terme",
            shortName = "Dépôt à terme",
            description = "Souscription à un placement sous forme de dépôt à terme",
            nature = "INVESTMENT",
            icon = Icons.Default.Savings,
            isPersonToPerson = false
        ),
        "MLM_COMM_CLAIM" to TransactionTypeInfo(
            code = "MLM_COMM_CLAIM",
            translationKey = "transaction.mlm_commission_claim",
            nameFr = "Réclamation de commission MLM",
            shortName = "Commission MLM",
            description = "Récupération ou bascule d'une commission provenant du système MLM",
            nature = "SERVICE",
            icon = Icons.Default.MonetizationOn,
            isPersonToPerson = false
        ),
        "CPK_RECOVER" to TransactionTypeInfo(
            code = "CPK_RECOVER",
            translationKey = "transaction.cpk_recover",
            nameFr = "Encaissement clé CPK (Dépôt)",
            shortName = "Encaissement CPK",
            description = "Encaissement des fonds d'une clé matérielle/USB CPK sur le compte",
            nature = "SECURITY",
            icon = Icons.Default.CallReceived,
            isPersonToPerson = false
        ),
        "CPK_CLAIM" to TransactionTypeInfo(
            code = "CPK_CLAIM",
            translationKey = "transaction.cpk_claim",
            nameFr = "Encaissement clé CPK (Dépôt)",
            shortName = "Encaissement CPK",
            description = "Fonds d'une clé sécurisée crédités sur le compte",
            nature = "SECURITY",
            icon = Icons.Default.CallReceived,
            isPersonToPerson = false
        ),
        "ENCAISSEMENT" to TransactionTypeInfo(
            code = "ENCAISSEMENT",
            translationKey = "transaction.encaissement",
            nameFr = "Encaissement clé USB (Dépôt)",
            shortName = "Encaissement",
            description = "Approvisionnement du compte par clé matérielle",
            nature = "SECURITY",
            icon = Icons.Default.CallReceived,
            isPersonToPerson = false
        ),
        "CPK_ISSUE" to TransactionTypeInfo(
            code = "CPK_ISSUE",
            translationKey = "transaction.cpk_issue",
            nameFr = "Création de clé CPK (Mise sous clé)",
            shortName = "Création clé",
            description = "Immobilisation de fonds sous forme de clé sécurisée hors-ligne",
            nature = "SECURITY",
            icon = Icons.Default.Lock,
            isPersonToPerson = false
        ),
        "CPK_CANCEL" to TransactionTypeInfo(
            code = "CPK_CANCEL",
            translationKey = "transaction.cpk_cancel",
            nameFr = "Remboursement clé CPK",
            shortName = "Remboursement clé",
            description = "Annulation de clé matérielle et remboursement des fonds restants",
            nature = "SECURITY",
            icon = Icons.Default.CallReceived,
            isPersonToPerson = false
        )
    )

    fun getTypeInfo(typeCode: String): TransactionTypeInfo {
        val clean = typeCode.uppercase().trim()
        val mapped = MAPPINGS[clean]
        if (mapped != null) return mapped

        val translatedName = CashPayMessageTranslator.translateTransactionType(clean)
        return TransactionTypeInfo(
            code = clean,
            translationKey = "transaction.generic",
            nameFr = translatedName,
            shortName = translatedName,
            description = translatedName,
            nature = if (clean.contains("CRYPTO") || clean.contains("BTC")) "CRYPTO" else "SERVICE",
            icon = if (clean.contains("CRYPTO") || clean.contains("BTC")) Icons.Default.CurrencyBitcoin else Icons.Default.AccountBalanceWallet,
            isPersonToPerson = clean.contains("P2P") || clean.contains("INTERNAL")
        )
    }

    fun getDisplayTitle(typeCode: String, direction: String): String {
        val clean = typeCode.uppercase().trim()
        val isIncoming = direction.lowercase() == "incoming"

        return when (clean) {
            "TR" -> if (isIncoming) "Transfert reçu" else "Transfert envoyé"
            "BTC_TRANSFER" -> if (isIncoming) "Bitcoin reçu" else "Transfert Bitcoin envoyé"
            "INTERNAL_CASHPAY", "INTERNAL_BTC" -> if (isIncoming) "Transfert BTC interne reçu" else "Transfert BTC interne envoyé"
            "WD_CRYPTO_MONEY", "WD_CRYPTO", "WITHDRAW_CRYPTO", "CRYPTO_WITHDRAWAL" -> if (isIncoming) "Crédit Bitcoin reçu" else "Retrait Bitcoin"
            "DP_AGENT", "DP", "DEPOSIT" -> if (isIncoming) "Dépôt d'argent reçu" else "Dépôt d'argent"
            "WD", "WD_AGENT", "WITHDRAW" -> if (isIncoming) "Retrait annulé" else "Retrait auprès d'un agent"
            "LR", "LR_AGENT" -> "Remboursement de prêt"
            "LP" -> "Décaissement de prêt"
            "CPK_RECOVER", "CPK_CLAIM", "ENCAISSEMENT" -> "Encaissement clé CPK (Dépôt)"
            "CPK_ISSUE", "HSM_LOCK" -> "Création de clé CPK (Mise sous clé)"
            "CPK_CANCEL" -> "Remboursement de clé CPK (Crédit)"
            else -> CashPayMessageTranslator.translateTransactionType(clean, isIncoming)
        }
    }
}

fun TransactionEntity.isCredit(): Boolean {
    val isFailed = status.lowercase() in listOf("failed", "cancelled", "rejected", "error", "echoue", "échec")
    if (isFailed) return false

    val isDepositOrRecover = type.uppercase() in listOf(
        "DP", "DP_AGENT", "DEPOSIT", "RECHARGE", "MOMO_DEPOSIT",
        "CPK_RECOVER", "CPK_CLAIM", "CPK_RECOVERY", "ENCAISSEMENT", "CPK_CASHIN", "RW", "CLAIM", "CPK_CANCEL"
    ) ||
    title.contains("recharge", ignoreCase = true) ||
    title.contains("dépôt", ignoreCase = true) ||
    title.contains("encaissement", ignoreCase = true) ||
    title.contains("récupération", ignoreCase = true) ||
    displayName.contains("encaissement", ignoreCase = true) ||
    displayName.contains("dépôt", ignoreCase = true) ||
    displayName.contains("recharge", ignoreCase = true) ||
    (description?.contains("encaissement", ignoreCase = true) == true) ||
    (description?.contains("dépôt", ignoreCase = true) == true)

    return isDepositOrRecover || direction.lowercase() in listOf("incoming", "credit")
}
