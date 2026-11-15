package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.example.R

private data class GuidePage(val image: Int, val title: String, val description: String)

@Composable
fun InstallationGuideScreen(onFinished: () -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    val pages = remember {
        listOf(
            GuidePage(R.drawable.guide_cashpay_access, "Bienvenue chez CashPay",
                "Connectez-vous avec votre identifiant et suivez les étapes de vérification de votre compte."),
            GuidePage(R.drawable.guide_cashpay_pin, "Votre code PIN, votre sécurité",
                "Gardez votre code PIN confidentiel. Il protège l’accès à votre compte et la validation de vos opérations."),
            GuidePage(R.drawable.guide_cashpay_agent, "Votre espace agent",
                "Les agents autorisés disposent d’un espace pour les dépôts, retraits et le suivi de leurs activités."),
            GuidePage(R.drawable.guide_cashpay_services, "Vos services CashPay",
                "Portefeuille multi-devises • Transferts • Dépôts et retraits • Paiement de factures • PayScan et achats • Cartes • Crédits et prêts.\n\nDisponibles selon votre compte, votre pays et les conditions du service. Les images présentent l’application ; les soldes et cartes affichés ne représentent pas votre compte.")
        )
    }
    val page = pages[index]
    Box(Modifier.fillMaxSize()) {
        Image(painterResource(page.image), contentDescription = page.title,
            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        // The photo reaches all edges; only the controls respect system insets.
        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .9f))))
            .navigationBarsPadding().statusBarsPadding().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.heightIn(max = 230.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${index + 1} / ${pages.size}", color = Color.White, fontSize = 12.sp)
                Text(page.title, color = Color.White, style = MaterialTheme.typography.titleLarge)
                Text(page.description, color = Color.White, fontSize = 14.sp)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (index > 0) OutlinedButton(onClick = { index-- }, modifier = Modifier.testTag("guide_prev_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("Retour") }
                Button(onClick = { if (index == pages.lastIndex) onFinished() else index++ },
                    modifier = Modifier.weight(1f).testTag("guide_next_btn")) {
                    Text(if (index == pages.lastIndex) "Commencer" else "Suivant")
                }
            }
            if (index < pages.lastIndex) TextButton(onClick = onFinished,
                modifier = Modifier.align(Alignment.End).testTag("guide_skip_btn")) {
                Text("Passer", color = Color.White)
            }
        }
    }
}
