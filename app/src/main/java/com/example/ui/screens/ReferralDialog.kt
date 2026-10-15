package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserProfileEntity
import com.example.data.model.ReferralItem
import com.example.data.model.ReferralResponse
import com.example.data.model.WalletResponse
import com.example.ui.theme.MulishFontFamily

private val ToofanGreen = Color(0xFF00C48C)
private val DarkCardBg = Color(0xFF1E293B)
private val DarkBorderColor = Color(0xFF334155)

@Composable
fun ReferralDialog(
    isOpen: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    referralResponse: ReferralResponse?,
    walletResponse: WalletResponse? = null,
    userProfile: UserProfileEntity? = null,
    isClaimingReward: Boolean,
    claimingReferralId: Long?,
    claimSuccessMessage: String?,
    claimErrorMessage: String?,
    onRefresh: () -> Unit,
    onClaimReward: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val referral = referralResponse?.referral
    val stats = referralResponse?.stats
    val referralsList = referralResponse?.referrals ?: emptyList()

    val refCode = referral?.code?.takeIf { it.isNotBlank() } ?: "VOTRE_CODE"
    val rawLink = referral?.link?.trim().orEmpty()
    val refLink = when {
        rawLink.startsWith("https://app.cashpay-all.com/") -> rawLink
        rawLink.startsWith("https://cashpay-all.com/") -> rawLink.replace("https://cashpay-all.com/", "https://app.cashpay-all.com/")
        rawLink.startsWith("http://cashpay-all.com/") -> rawLink.replace("http://cashpay-all.com/", "https://app.cashpay-all.com/")
        rawLink.startsWith("http://app.cashpay-all.com/") -> rawLink.replace("http://app.cashpay-all.com/", "https://app.cashpay-all.com/")
        rawLink.startsWith("/") -> "https://app.cashpay-all.com$rawLink"
        rawLink.isNotBlank() -> "https://app.cashpay-all.com/$rawLink"
        else -> "https://app.cashpay-all.com/referral/$refCode"
    }

    // 1. Détection stricte de la devise nationale / locale (JAMAIS de devise internationale USD/EUR)
    val rawCurrency = referralResponse?.stats?.currency
        ?: referralResponse?.stats?.devise
        ?: referralResponse?.currency
        ?: referralResponse?.devise

    val userCountry = userProfile?.country?.lowercase() ?: ""
    val walletNatCode = walletResponse?.nationalCurrency?.code

    val natCurrencyCode = when {
        !rawCurrency.isNullOrBlank() && !rawCurrency.equals("USD", ignoreCase = true) && !rawCurrency.equals("EUR", ignoreCase = true) -> rawCurrency.uppercase()
        walletNatCode != null && !walletNatCode.equals("USD", ignoreCase = true) && !walletNatCode.equals("EUR", ignoreCase = true) -> walletNatCode.uppercase()
        refCode.startsWith("CD", ignoreCase = true) || userCountry.contains("congo") || userCountry.contains("rdc") -> "CDF"
        userCountry.contains("ivoire") || userCountry.contains("senegal") || userCountry.contains("benin") || userCountry.contains("togo") || userCountry.contains("mali") || userCountry.contains("burkina") || userCountry.contains("niger") -> "XOF"
        userCountry.contains("cameroun") || userCountry.contains("gabon") || userCountry.contains("tchad") || userCountry.contains("centrafrique") || userCountry.contains("guinée équatoriale") -> "XAF"
        userCountry.contains("guinée") || userCountry.contains("guinea") -> "GNF"
        userCountry.contains("rwanda") -> "RWF"
        userCountry.contains("kenya") -> "KES"
        else -> "CDF" // Devise nationale par défaut CashPay All (RDC)
    }

    val natCurrencySymbol = when (natCurrencyCode) {
        "CDF" -> "FC"
        "XOF", "XAF" -> "CFA"
        "GNF" -> "FG"
        "RWF" -> "FRw"
        "KES" -> "KSh"
        "UGX" -> "USh"
        "TZS" -> "TSh"
        else -> natCurrencyCode
    }

    val natCurrencyName = when (natCurrencyCode) {
        "CDF" -> "Franc Congolais (CDF)"
        "XOF" -> "Franc CFA (XOF)"
        "XAF" -> "Franc CFA (XAF)"
        "GNF" -> "Franc Guinéen (GNF)"
        "RWF" -> "Franc Rwandais (RWF)"
        "KES" -> "Shilling Kenyan (KES)"
        else -> "Devise locale ($natCurrencyCode)"
    }

    // 2. Vérification de la configuration du pays dans referral_settings de la base de données
    val pointRate = stats?.pointConversionRate ?: 0.0
    val isCountryConfigured = (pointRate > 0.0 || referralResponse?.isConfigured == true || stats?.isConfigured == true)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = Color(0xFF0F172A),
            shadowElevation = 24.dp,
            border = BorderStroke(1.dp, DarkBorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFFF9900).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Parrainage",
                                tint = Color(0xFFFF9900),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Programme de Parrainage",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Invitez et gagnez des récompenses CashPay",
                                fontFamily = MulishFontFamily,
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onRefresh, modifier = Modifier.size(34.dp)) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualiser",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(34.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bannière de configuration par pays & Devise nationale
                if (isCountryConfigured) {
                    Surface(
                        color = Color(0xFF0F291E),
                        border = BorderStroke(1.dp, ToofanGreen.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(16.dp))
                            Column {
                                Text(
                                    text = "Programme actif en Devise Nationale : $natCurrencyName",
                                    color = ToofanGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = MulishFontFamily
                                )
                                Text(
                                    text = "Taux configuré : 1 point = ${formatCleanAmount(pointRate)} $natCurrencySymbol crédités sur votre portefeuille",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 10.sp,
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        color = Color(0xFF332005),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                            Column {
                                Text(
                                    text = "Configuration pays en attente",
                                    color = Color(0xFFF59E0B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = MulishFontFamily
                                )
                                Text(
                                    text = "Vous pouvez parrainer et cumuler des points. La conversion financière en $natCurrencyName sera débloquée dès configuration des paramètres de votre pays.",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 10.sp,
                                    fontFamily = MulishFontFamily,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }

                // Claim Feedback Messages
                claimSuccessMessage?.let { msg ->
                    Surface(
                        color = ToofanGreen.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, ToofanGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(18.dp))
                            Text(msg, color = ToofanGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily)
                        }
                    }
                }

                claimErrorMessage?.let { err ->
                    Surface(
                        color = Color(0xFFFF4868).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFFF4868)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFFFF4868), modifier = Modifier.size(18.dp))
                            Text(err, color = Color(0xFFFF4868), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily)
                        }
                    }
                }

                if (isLoading && referralResponse == null) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ToofanGreen)
                    }
                } else if (errorMessage != null && referralResponse == null) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(errorMessage, color = Color(0xFFFF4868), textAlign = TextAlign.Center, fontSize = 13.sp)
                            Button(onClick = onRefresh, colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)) {
                                Text("Réessayer", color = Color.White)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Referral Link & Code Card
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DarkCardBg,
                                border = BorderStroke(1.dp, DarkBorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Votre lien & code de parrainage",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White,
                                            fontFamily = MulishFontFamily
                                        )
                                        Text(
                                            text = "Partage illimité",
                                            fontSize = 11.sp,
                                            color = ToofanGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Referral Code Pill
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Code de parrainage", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                            Text(refCode, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFF38BDF8))
                                        }

                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(refCode))
                                                Toast.makeText(context, "Code de parrainage copié !", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copier le code", tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    // Action buttons row: Copier le lien & Partager
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(refLink))
                                                Toast.makeText(context, "Lien copié dans le presse-papiers !", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, ToofanGreen),
                                            modifier = Modifier.weight(1f).height(42.dp)
                                        ) {
                                            Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Copier le lien", color = ToofanGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily)
                                        }

                                        Button(
                                            onClick = {
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(
                                                        Intent.EXTRA_TEXT,
                                                        "Rejoignez CashPay All et gérez votre argent sans frontières ! Utilisez mon code parrain $refCode ou inscrivez-vous ici : $refLink"
                                                    )
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Partager mon code de parrainage"))
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(42.dp)
                                        ) {
                                            Icon(Icons.Outlined.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Partager", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily)
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Stats Summary Grid (Entièrement en devise nationale)
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Performances & Récompenses",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color.White,
                                    fontFamily = MulishFontFamily
                                )

                                val claimableSubtitle = if (isCountryConfigured) {
                                    "${formatCleanAmount(stats?.claimableAmount ?: 0.0)} $natCurrencySymbol dispo"
                                } else {
                                    "${stats?.claimableCount ?: 0} pts en attente"
                                }

                                val earnedDisplay = if (isCountryConfigured) {
                                    "${formatCleanAmount(stats?.totalEarnedAmount ?: 0.0)} $natCurrencySymbol"
                                } else {
                                    "0 $natCurrencySymbol"
                                }

                                val rateDisplay = if (isCountryConfigured) {
                                    formatCleanAmount(pointRate)
                                } else {
                                    "En attente"
                                }

                                val rateSubtitle = if (isCountryConfigured) {
                                    "$natCurrencySymbol par point"
                                } else {
                                    "Config requise"
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ReferralStatCard(
                                        title = "Filleuls inscrits",
                                        value = "${stats?.totalReferrals ?: 0}",
                                        subtitle = "${stats?.pendingCount ?: 0} en attente",
                                        accentColor = Color(0xFF38BDF8),
                                        modifier = Modifier.weight(1f)
                                    )
                                    ReferralStatCard(
                                        title = "À réclamer",
                                        value = "${stats?.claimableCount ?: 0}",
                                        subtitle = claimableSubtitle,
                                        accentColor = ToofanGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ReferralStatCard(
                                        title = "Gains cumulés",
                                        value = earnedDisplay,
                                        subtitle = "${stats?.claimedCount ?: 0} réclamés",
                                        accentColor = Color(0xFFFF9900),
                                        modifier = Modifier.weight(1f)
                                    )
                                    ReferralStatCard(
                                        title = "Taux par point",
                                        value = rateDisplay,
                                        subtitle = rateSubtitle,
                                        accentColor = Color(0xFFA855F7),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // 3. Referral List Section Header
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Liste de vos filleuls (${referralsList.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    fontFamily = MulishFontFamily
                                )
                                if (referralsList.isNotEmpty()) {
                                    Text(
                                        text = "${stats?.claimableCount ?: 0} admissible(s)",
                                        fontSize = 11.5.sp,
                                        color = ToofanGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // 4. Referral Items or Empty State
                        if (referralsList.isEmpty()) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = DarkCardBg,
                                    border = BorderStroke(1.dp, DarkBorderColor),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GroupAdd,
                                            contentDescription = null,
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Text(
                                            text = "Aucun filleul pour l'instant",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Partagez votre code ou votre lien avec vos amis. Dès qu'ils effectuent une transaction, vous recevrez vos récompenses automatiquement en $natCurrencyName !",
                                            textAlign = TextAlign.Center,
                                            fontSize = 12.sp,
                                            color = Color(0xFF94A3B8),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            items(referralsList) { item ->
                                ReferralItemRow(
                                    item = item,
                                    natCurrencySymbol = natCurrencySymbol,
                                    pointRate = pointRate,
                                    isCountryConfigured = isCountryConfigured,
                                    isClaiming = isClaimingReward && claimingReferralId == item.id,
                                    onClaim = {
                                        if (isCountryConfigured) {
                                            onClaimReward(item.id)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "La récompense financière en devise nationale n'est pas encore configurée pour votre pays.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Fermer", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily)
                }
            }
        }
    }
}

private fun formatCleanAmount(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        String.format(java.util.Locale.US, "%,.0f", amount)
    } else {
        String.format(java.util.Locale.US, "%,.2f", amount)
    }
}

@Composable
private fun ReferralStatCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCardBg,
        border = BorderStroke(1.dp, DarkBorderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = MulishFontFamily)
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = accentColor, fontFamily = MulishFontFamily)
            Text(subtitle, fontSize = 10.sp, color = Color(0xFF64748B), fontFamily = MulishFontFamily)
        }
    }
}

@Composable
private fun ReferralItemRow(
    item: ReferralItem,
    natCurrencySymbol: String,
    pointRate: Double,
    isCountryConfigured: Boolean,
    isClaiming: Boolean,
    onClaim: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCardBg,
        border = BorderStroke(1.dp, DarkBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Initial circle
                val initial = item.fullName.take(1).uppercase().ifBlank { "U" }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF0F172A), CircleShape)
                        .border(1.dp, Color(0xFF38BDF8), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initial, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = item.fullName.ifBlank { "Filleul #${item.id}" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        fontFamily = MulishFontFamily
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.date,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontFamily = MulishFontFamily
                        )

                        val isActive = item.status.equals("Active", ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isActive) ToofanGreen.copy(alpha = 0.15f) else Color(0xFF64748B).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isActive) "Actif" else item.status,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) ToofanGreen else Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }

                        // Récompense prévue en devise nationale
                        if (isActive && !item.isClaimed) {
                            val rewardText = if (isCountryConfigured && pointRate > 0.0) {
                                "+ ${formatCleanAmount(pointRate)} $natCurrencySymbol"
                            } else {
                                "+ 1 pt"
                            }
                            Text(
                                text = rewardText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ToofanGreen,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action / Status Badge
            if (item.isClaimed) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ToofanGreen.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ToofanGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(13.dp))
                        Text("Réclamé", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ToofanGreen)
                    }
                }
            } else {
                val isActive = item.status.equals("Active", ignoreCase = true)
                Button(
                    onClick = onClaim,
                    enabled = !isClaiming && isActive,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) ToofanGreen else Color(0xFF334155),
                        disabledContainerColor = Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    if (isClaiming) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (isActive) "Réclamer" else "En attente",
                            color = if (isActive) Color.White else Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = MulishFontFamily
                        )
                    }
                }
            }
        }
    }
}
