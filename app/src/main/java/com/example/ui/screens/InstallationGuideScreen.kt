package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

// Exact primary color verified in cashpay-main css/style.css, origin/main 3831815.
private val GuideViolet = Color(0xFF7A22D4)
private data class GuidePage(val image: Int, val title: String, val description: String)
private val guidePages = listOf(
    GuidePage(R.drawable.guide_illustration_welcome, "Bienvenue chez CashPay",
        "Votre portefeuille et vos services réunis dans une seule application. Découvrez comment utiliser votre compte."),
    GuidePage(R.drawable.guide_illustration_account, "Créez ou retrouvez votre compte",
        "Renseignez vos informations ou votre identifiant, puis suivez les étapes de vérification proposées par CashPay."),
    GuidePage(R.drawable.guide_illustration_security, "Protégez votre accès",
        "Gardez votre code PIN confidentiel. Vérifiez les informations de chaque opération avant de confirmer."),
    GuidePage(R.drawable.guide_illustration_wallet, "Votre portefeuille et vos cartes",
        "Consultez vos soldes par devise et vos opérations. Retrouvez vos cartes après leur attribution à votre compte."),
    GuidePage(R.drawable.guide_illustration_services, "Découvrez vos services",
        "Transferts, dépôts et retraits, scan, factures, achats, cartes et demandes de crédit. L’espace agent est réservé aux comptes autorisés. Disponibilité selon votre compte et votre pays.")
)

@Composable
fun InstallationGuideScreen(onFinished: () -> Unit) {
    var savedIndex by rememberSaveable { mutableIntStateOf(0) }
    val index = savedIndex.coerceIn(guidePages.indices)
    val page = guidePages[index]
    // Black is drawn before safe-inset padding, including behind the system bars.
    Column(Modifier.fillMaxSize().background(Color.Black).statusBarsPadding().navigationBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.cashpay_logo_white), contentDescription = "CashPay",
                modifier = Modifier.width(105.dp).height(30.dp), contentScale = ContentScale.Fit)
            Text("${index + 1} / ${guidePages.size}", color = Color(0xFFA66BE0), fontSize = 13.sp)
        }
        // Entire transparent illustration is visible, never stretched or cropped into a background.
        Image(painterResource(page.image), contentDescription = page.title,
            modifier = Modifier.fillMaxWidth().weight(1f).testTag("guide_artwork"),
            contentScale = ContentScale.Fit)
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            key(index) {
                Column(Modifier.heightIn(max = 160.dp).verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(page.title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text(page.description, color = Color(0xFFD8D0DE), fontSize = 14.sp, lineHeight = 21.sp, textAlign = TextAlign.Center)
                }
            }
            Row(Modifier.padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                guidePages.indices.forEach { step ->
                    Box(Modifier.size(if (step == index) 9.dp else 6.dp).clip(CircleShape)
                        .background(if (step == index) GuideViolet else Color(0xFF44344F)))
                }
            }
            Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (index > 0) OutlinedButton(onClick = { savedIndex = index - 1 },
                    modifier = Modifier.heightIn(min = 48.dp).testTag("guide_prev_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("Retour") }
                Button(onClick = { if (index == guidePages.lastIndex) onFinished() else savedIndex = index + 1 },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("guide_next_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = GuideViolet, contentColor = Color.White)) {
                    Text(if (index == guidePages.lastIndex) "Commencer" else "Suivant")
                }
            }
        }
    }
}
