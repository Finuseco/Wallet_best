package com.example.util

import java.util.Locale

/**
 * Couche centralisée de traduction, formatage et harmonisation des transactions,
 * notifications et messages de CashPay All (FINUSECO SARL).
 *
 * Règle d'or : Ne jamais exposer de codes techniques bruts (WD_CRYPTO_MONEY,
 * internal_cashpay, typeCode, etc.) à l'utilisateur final.
 */
object CashPayMessageTranslator {

    /**
     * Formatage financier centralisé :
     * - Pour Bitcoin (BTC) : jusqu'à 8 décimales en retirant les zéros superflus :
     *   "0.01000000 BTC" -> "0.01 BTC"
     *   "0.00012000 BTC" -> "0.00012 BTC"
     *   "0.00000001 BTC" -> "0.00000001 BTC"
     * - Pour devises fiduciaires : 2 décimales avec séparateur de milliers.
     */
    fun formatAmount(amount: Double, currency: String): String {
        return if (currency.equals("BTC", ignoreCase = true)) {
            formatBtc(amount)
        } else {
            val currCode = currency.uppercase().trim().ifBlank { "USD" }
            String.format(Locale.US, "%,.2f %s", amount, currCode)
        }
    }

    /**
     * Formate un montant en Bitcoin sans zéros inutiles, avec au minimum 2 décimales.
     */
    fun formatBtc(amount: Double): String {
        val raw = String.format(Locale.US, "%.8f", amount)
        val parts = raw.split(".")
        if (parts.size == 1) return "${parts[0]}.00 BTC"
        val integerPart = parts[0]
        var decimalPart = parts[1].trimEnd('0')
        if (decimalPart.length < 2) {
            decimalPart = decimalPart.padEnd(2, '0')
        }
        return "$integerPart.$decimalPart BTC"
    }

    /**
     * Formate uniquement le nombre sans le suffixe "BTC".
     */
    fun formatBtcValueOnly(amount: Double): String {
        val raw = String.format(Locale.US, "%.8f", amount)
        val parts = raw.split(".")
        if (parts.size == 1) return "${parts[0]}.00"
        val integerPart = parts[0]
        var decimalPart = parts[1].trimEnd('0')
        if (decimalPart.length < 2) {
            decimalPart = decimalPart.padEnd(2, '0')
        }
        return "$integerPart.$decimalPart"
    }

    /**
     * Traduit les types d'opération techniques du backend en libellés clairs et compréhensibles.
     * Élimine des affichages comme "WD_CRYPTO_MONEY", "internal_cashpay", "TR", etc.
     */
    fun translateTransactionType(typeCode: String?, isCredit: Boolean = false): String {
        val clean = typeCode?.trim()?.uppercase() ?: ""
        return when (clean) {
            "WD_CRYPTO_MONEY", "WD_CRYPTO", "WITHDRAW_CRYPTO", "CRYPTO_WITHDRAWAL" -> "Retrait Bitcoin"
            "BTC_TRANSFER" -> if (isCredit) "Bitcoin reçu" else "Transfert Bitcoin envoyé"
            "INTERNAL_CASHPAY", "INTERNAL_BTC" -> if (isCredit) "Transfert BTC interne reçu" else "Transfert BTC interne envoyé"
            "CRYPTO_TRANSFER" -> if (isCredit) "Cryptomonnaie reçue" else "Transfert de cryptomonnaie"
            "CRYPTO_DEPOSIT", "BTC_DEPOSIT" -> "Dépôt Bitcoin"
            "BUY_BTC" -> "Achat de Bitcoin"
            "SELL_BTC" -> "Vente de Bitcoin"
            "TR" -> if (isCredit) "Transfert reçu" else "Transfert envoyé"
            "DP", "DP_AGENT", "DEPOSIT", "RECHARGE", "MOMO_DEPOSIT" -> "Dépôt d'argent"
            "WD", "WD_AGENT", "WITHDRAW" -> "Retrait d'argent"
            "EXCHANGE", "CRYPTO_EXCHANGE" -> "Conversion de devises"
            "FE", "FEE" -> "Frais de transaction"
            "LR", "LR_AGENT" -> "Remboursement de prêt"
            "LP" -> "Décaissement de prêt"
            "CPK_RECOVER", "CPK_CLAIM", "ENCAISSEMENT" -> "Encaissement clé CPK"
            "CPK_ISSUE", "HSM_LOCK" -> "Mise sous clé CPK"
            "CPK_CANCEL" -> "Remboursement clé CPK"
            "TICKET_BUY" -> "Achat de billet"
            "POS_SALE" -> "Achat marchand"
            else -> {
                when {
                    clean.contains("CRYPTO") || clean.contains("BTC") -> "Opération Bitcoin"
                    clean.contains("WITHDRAW") || clean.startsWith("WD") -> "Retrait"
                    clean.contains("DEPOSIT") || clean.startsWith("DP") -> "Dépôt"
                    clean.contains("TRANSFER") || clean.startsWith("TR") -> "Transfert d'argent"
                    clean.contains("EXCHANGE") -> "Conversion de devises"
                    else -> "Opération CashPay"
                }
            }
        }
    }

    /**
     * Traduit les statuts internes en libellés clairs.
     */
    fun translateStatus(status: String?): String {
        val clean = status?.trim()?.lowercase() ?: ""
        return when (clean) {
            "completed", "success", "succes", "valide", "validé", "confirme", "confirmé" -> "Validé"
            "pending", "en cours", "traitement", "processing", "submitted" -> "En cours de traitement"
            "failed", "cancelled", "rejected", "echoue", "échec", "error", "refuse", "refusé" -> "Échoué"
            else -> "En attente"
        }
    }

    /**
     * Traduit et filtre les messages d'erreurs en évitant les clés brutes de base de données.
     */
    fun translateErrorMessage(rawError: String?, fallback: String = "Votre transaction n’a pas pu être effectuée."): String {
        if (rawError.isNullOrBlank()) return fallback
        val lower = rawError.lowercase()

        return when {
            lower.contains("solde") || lower.contains("balance") || lower.contains("fonds") || lower.contains("insufficient") || lower.contains("insuffisant") ->
                "Votre solde disponible est insuffisant pour effectuer cette opération."
            lower.contains("pin") || lower.contains("unauthorized") || lower.contains("mot de passe") ->
                "Code PIN invalide ou opération non autorisée."
            lower.contains("address") || lower.contains("adresse") || lower.contains("destination") || lower.contains("recipient") ->
                "Veuillez vérifier l’adresse Bitcoin du destinataire."
            lower.contains("amount") || lower.contains("montant") ->
                "Le montant doit être supérieur à zéro."
            lower.contains("pending") ->
                "Votre transaction est en cours de traitement."
            lower.contains("network") || lower.contains("reseau") || lower.contains("réseau") || lower.contains("timeout") || lower.contains("connect") ->
                "Erreur de communication avec le réseau. Veuillez réessayer."
            lower.contains("internal") || lower.contains("500") ->
                "Erreur interne lors du traitement. Veuillez réessayer ultérieurement."
            lower.contains("not found") || lower.contains("introuvable") || lower.contains("404") ->
                "Compte ou portefeuille destinataire introuvable."
            else -> {
                if (rawError.contains("WD_") || rawError.contains("typeCode") || rawError.contains("internal_cashpay") || rawError.contains("NullPointerException")) {
                    fallback
                } else {
                    rawError
                }
            }
        }
    }

    /**
     * Génère les messages normalisés de notifications (Section 5 du cahier des charges).
     */
    fun formatSenderBtcNotification(amountBtc: Double, feeBtc: Double, referenceCode: String): String {
        val amt = formatBtc(amountBtc)
        val fee = formatBtc(feeBtc)
        return "Votre transfert de $amt a été effectué avec succès. Frais : $fee. Référence : $referenceCode."
    }

    fun formatReceiverBtcNotification(amountBtc: Double, referenceCode: String): String {
        val amt = formatBtc(amountBtc)
        return "Vous avez reçu $amt sur votre compte CashPay. Référence : $referenceCode."
    }

    fun formatPendingBtcNotification(amountBtc: Double, referenceCode: String): String {
        val amt = formatBtc(amountBtc)
        return "Votre transfert Bitcoin de $amt est en cours de traitement sur le réseau. Référence : $referenceCode."
    }
}
