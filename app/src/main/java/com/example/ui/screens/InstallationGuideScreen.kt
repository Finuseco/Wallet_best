package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBlue

private data class GuideAction(val icon: ImageVector, val label: String)
private data class GuidePage(val title: String, val description: String, val actions: List<GuideAction>)

private val guidePages = listOf(
    GuidePage("Connectez votre compte", "Renseignez votre identifiant, puis suivez la vérification proposée par CashPay.", listOf(
        GuideAction(Icons.Default.Person, "Votre identifiant"), GuideAction(Icons.Default.MailOutline, "Code de vérification"), GuideAction(Icons.Default.CheckCircle, "Compte reconnu"))),
    GuidePage("Protégez votre accès", "Saisissez votre code PIN confidentiel. Vérifiez toujours les informations avant de confirmer une opération.", listOf(
        GuideAction(Icons.Default.Lock, "Code PIN"), GuideAction(Icons.Default.VisibilityOff, "Gardez-le secret"), GuideAction(Icons.Default.VerifiedUser, "Accès sécurisé"))),
    GuidePage("Consultez votre portefeuille", "Retrouvez vos soldes par devise et l’historique de vos opérations. Le bouton œil permet de masquer les montants.", listOf(
        GuideAction(Icons.Default.AccountBalanceWallet, "Choisir une devise"), GuideAction(Icons.Default.Visibility, "Afficher le solde"), GuideAction(Icons.Default.History, "Voir l’historique"))),
    GuidePage("Envoyez de l’argent", "Choisissez le destinataire, indiquez le montant, puis vérifiez les frais et le total avant de valider avec votre PIN.", listOf(
        GuideAction(Icons.Default.Person, "Destinataire"), GuideAction(Icons.Default.Payments, "Montant et frais"), GuideAction(Icons.Default.CheckCircle, "Vérifier et confirmer"))),
    GuidePage("Scannez et vérifiez", "Un QR code peut identifier un compte, une demande de paiement ou un article. Contrôlez le résultat et le montant avant de payer.", listOf(
        GuideAction(Icons.Default.QrCodeScanner, "Scanner le code"), GuideAction(Icons.Default.Receipt, "Vérifier les détails"), GuideAction(Icons.Default.Lock, "Autoriser le paiement"))),
    GuidePage("Découvrez vos services", "Transferts, dépôts et retraits, factures, cartes, achats et demandes de crédit. L’espace agent est accessible aux comptes autorisés. Les services dépendent du pays et des conditions applicables.", listOf(
        GuideAction(Icons.Default.AccountBalanceWallet, "Dépôts et retraits"), GuideAction(Icons.Default.CreditCard, "Cartes et paiements"), GuideAction(Icons.Default.Store, "Achats et espace agent")))
)

@Composable
fun InstallationGuideScreen(onFinished: () -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    val page = guidePages[index]
    Column(Modifier.fillMaxSize().background(Color.Black).statusBarsPadding().navigationBarsPadding().padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("CashPay", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("GUIDE  ${index + 1} / ${guidePages.size}", color = Color(0xFFA66BE0), fontSize = 12.sp)
        }
        Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            guidePages.indices.forEach { step ->
                Box(Modifier.weight(1f).height(4.dp).clip(CircleShape)
                    .background(if (step <= index) BrandBlue else Color(0xFF29212F)))
            }
        }
        key(index) {
            Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                // A schematic, built from vector icons and containers, never a screenshot or a real account.
                Column(Modifier.widthIn(max = 340.dp).fillMaxWidth()
                    .border(1.dp, Color(0xFF59158D), RoundedCornerShape(28.dp))
                    .background(Color(0xFF130C19), RoundedCornerShape(28.dp)).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.width(42.dp).height(4.dp).clip(CircleShape).background(Color(0xFF59158D)))
                    page.actions.forEachIndexed { position, action ->
                        Row(Modifier.fillMaxWidth().background(Color(0xFF241230), RoundedCornerShape(14.dp)).padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(action.icon, null, tint = Color(0xFFA66BE0), modifier = Modifier.size(30.dp))
                            Column(Modifier.weight(1f)) {
                                Text("${position + 1}", color = Color(0xFFA66BE0), fontSize = 11.sp)
                                Text(action.label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        if (position < page.actions.lastIndex) Icon(Icons.Default.ArrowDownward, null,
                            tint = Color(0xFFA66BE0), modifier = Modifier.size(16.dp))
                    }
                }
                Text(page.title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(page.description, color = Color(0xFFD8D0DE), fontSize = 16.sp, lineHeight = 24.sp, textAlign = TextAlign.Center)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (index > 0) OutlinedButton(onClick = { index-- }, modifier = Modifier.testTag("guide_prev_btn"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("Retour") }
            Button(onClick = { if (index == guidePages.lastIndex) onFinished() else index++ },
                modifier = Modifier.weight(1f).testTag("guide_next_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White)) {
                Text(if (index == guidePages.lastIndex) "Commencer" else "Suivant")
            }
        }
    }
}
