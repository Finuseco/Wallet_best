package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MulishFontFamily

// Immersive Premium Palette (OnChat Violet & Cyan Theme)
private val BrandBlueMidnight = Color(0xFF0B0F19)
private val BrandBlueDark = Color(0xFF13182E)
private val BrandPurple = Color(0xFF8B5CF6)
private val BrandCyan = Color(0xFF00E5FF)
private val BrandGreen = Color(0xFF00C853)

data class GuideStepData(
    val title: String,
    val subtitle: String,
    val imageRes: Int,
    val badges: List<String>,
    val description: String,
    val highlights: List<Pair<String, String>>,
    val isPermissionStep: Boolean = false
)

@Composable
fun InstallationGuideScreen(
    onFinished: () -> Unit
) {
    var stepIndex by remember { mutableIntStateOf(0) }
    var permissionsRequested by remember { mutableStateOf(false) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionsRequested = true
    }

    val steps = remember {
        listOf(
            GuideStepData(
                title = "Solde Multi-Devises en Temps Réel",
                subtitle = "Portefeuille Numérique Intelligent",
                imageRes = R.drawable.img_guide_balances,
                badges = listOf("USD ($)", "EUR (€)", "Devise Locale"),
                description = "Consultez vos balances en direct avec mise à jour instantanée à chaque transaction. Basculez en un clic entre le Dollar US ($), l'Euro (€) et votre Devise Nationale au meilleur taux garanti.",
                highlights = listOf(
                    "Actualisation dynamique" to "Le solde réagit immédiatement à chaque crédit et débit.",
                    "Gestion multi-comptes" to "Gardez vos avoirs en Dollar US ($), Euro (€) et Devise Locale sous le même portefeuille sécurisé.",
                    "Visibilité & Confidentialité" to "Masquez ou affichez votre solde d'un simple geste."
                )
            ),
            GuideStepData(
                title = "Transferts & Envois Instantanés",
                subtitle = "Envoyez sans frontières à 0% de tracas",
                imageRes = R.drawable.img_guide_transfers,
                badges = listOf("P2P Gratuit", "Vers Téléphone", "Wallet ID"),
                description = "Envoyez de l'argent instantanément à vos proches par numéro de téléphone ou Wallet ID. Recevez des notifications en temps réel et partagez vos reçus certifiés.",
                highlights = listOf(
                    "Recherche intelligente" to "Reconnaissance automatique des bénéficiaires et contacts du téléphone.",
                    "Confirmation sécurisée" to "Aperçu clair des montants et frais avant chaque validation.",
                    "Historique complet" to "Suivez toutes vos entrées et sorties avec reçus téléchargeables."
                )
            ),
            GuideStepData(
                title = "Scanner & Payer en Supermarché",
                subtitle = "Paiement Instantané par QR Code",
                imageRes = R.drawable.img_guide_supermarket,
                badges = listOf("Supermarché", "Sans Contact", "Zéro Commission"),
                description = "Faites vos courses l'esprit tranquille. Notre scanner intégré haute performance vous permet de scanner le QR code à la caisse de vos supermarchés et boutiques partenaires pour régler vos achats instantanément.",
                highlights = listOf(
                    "Rapidité absolue" to "Ouvrez, scannez, validez avec votre PIN et c'est payé en 2 secondes.",
                    "100% Gratuit" to "Aucun frais de transaction lors de vos achats chez nos partenaires.",
                    "Plus de liasses de billets" to "Oubliez le transport d'espèces encombrantes ou les soucis de monnaie."
                )
            ),
            GuideStepData(
                title = "Sécurité Maximale & Biométrie",
                subtitle = "Protection Bancaire de Niveau Supérieur",
                imageRes = R.drawable.img_guide_security,
                badges = listOf("Empreinte Digitale", "PIN à 4 chiffres", "Chiffrement AES"),
                description = "Vos fonds et vos données sont protégés par le chiffrement le plus robuste du secteur fintech. Déverrouillez l'application avec votre empreinte ou votre code PIN confidentiel.",
                highlights = listOf(
                    "Déverrouillage biométrique" to "Accès instantané et sécurisé via le capteur d'empreinte digitale.",
                    "Isolation des sessions" to "La déconnexion efface intégralement vos clés de session et caches locaux.",
                    "Code PIN confidentiel" to "Chaque transfert ou action sensible requiert votre validation finale."
                )
            ),
            GuideStepData(
                title = "Espace Agent & Points Agréés",
                subtitle = "Dépôts, Retraits & Cash-in / Cash-out",
                imageRes = R.drawable.img_guide_agent_qr,
                badges = listOf("Dépôt Immédiat", "Scan & Pay", "Points Agréés"),
                description = "Effectuez facilement des dépôts et retraits d'espèces auprès de nos agents partenaires ou gérez vos activités professionnelles d'agent avec suivi en direct.",
                highlights = listOf(
                    "Dépôt sans frais client" to "Créditez votre compte CashPay chez n'importe quel agent agréé.",
                    "Scanner QR Code" to "Paiement ultra-rapide sans contact en magasin.",
                    "Espace professionnel" to "Outils dédiés pour les agents agréés avec suivi de commissions."
                ),
                isPermissionStep = true
            )
        )
    }

    val currentStep = steps[stepIndex]

    Box(modifier = Modifier.fillMaxSize()) {
        // Full screen background image - real scale (ContentScale.Crop)
        Image(
            painter = painterResource(id = currentStep.imageRes),
            contentDescription = currentStep.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Soft transparent gradient overlay so background image is 100% crisp and visible
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.55f)
                        ),
                        startY = 0f
                    )
                )
        )

        // Floating Centered Logo at the very top (No top bar background per user feedback)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Floating Centered Logo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cashpay_logo_white),
                    contentDescription = "CashPay Logo",
                    modifier = Modifier.height(38.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Centered modern step pill
                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = "Étape ${stepIndex + 1} sur ${steps.size}",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            // Scrollable Content overlayed elegantly
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Step Text area
                    AnimatedContent(
                        targetState = stepIndex,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { width -> width } + fadeIn(animationSpec = tween(300)) togetherWith
                                        slideOutHorizontally { width -> -width } + fadeOut(animationSpec = tween(300))
                            } else {
                                slideInHorizontally { width -> -width } + fadeIn(animationSpec = tween(300)) togetherWith
                                        slideOutHorizontally { width -> width } + fadeOut(animationSpec = tween(300))
                            }
                        },
                        label = "text_animation"
                    ) { index ->
                        val step = steps[index]
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = step.subtitle.uppercase(),
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.5.sp,
                                color = BrandCyan,
                                letterSpacing = 1.5.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = step.title,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 23.sp,
                                color = Color.White,
                                lineHeight = 30.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Badges Row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                step.badges.forEach { badge ->
                                    Surface(
                                        color = BrandPurple.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = badge,
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = step.description,
                                fontFamily = MulishFontFamily,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 21.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Highlights Box - elegant transparent card matching full screen backgrounds
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                step.highlights.forEach { (boldPart, descPart) ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 3.dp)
                                                .size(16.dp)
                                                .background(BrandPurple.copy(alpha = 0.3f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = BrandCyan,
                                                modifier = Modifier.size(11.dp)
                                            )
                                        }
                                        Text(
                                            text = androidx.compose.ui.text.buildAnnotatedString {
                                                pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold, color = Color.White))
                                                append(boldPart)
                                                append(" : ")
                                                pop()
                                                pushStyle(androidx.compose.ui.text.SpanStyle(color = Color.White.copy(alpha = 0.8f)))
                                                append(descPart)
                                                pop()
                                            },
                                            fontFamily = MulishFontFamily,
                                            fontSize = 12.5.sp,
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                            }

                            // Camera / QR Code permission integration
                            if (step.isPermissionStep) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        val perms = mutableListOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.READ_CONTACTS
                                        )
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            perms.add(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                        permissionsLauncher.launch(perms.toTypedArray())
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (permissionsRequested) BrandGreen else BrandPurple
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("guide_permission_btn")
                                ) {
                                    Icon(
                                        imageVector = if (permissionsRequested) Icons.Default.Check else Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (permissionsRequested) "Autorisations accordées avec succès" else "Activer la Caméra pour Scanner",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation & Stepper Indicator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Visual indicators dots
                Row(
                    modifier = Modifier.padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in steps.indices) {
                        Box(
                            modifier = Modifier
                                .size(if (i == stepIndex) 10.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (i == stepIndex) BrandPurple else Color.White.copy(alpha = 0.4f))
                                .clickable { stepIndex = i }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (stepIndex > 0) {
                        Button(
                            onClick = { stepIndex-- },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(0.35f)
                                .height(50.dp)
                                .testTag("guide_prev_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Précédent",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Next / Finish Button
                    Button(
                        onClick = {
                            if (stepIndex < steps.size - 1) {
                                stepIndex++
                            } else {
                                onFinished()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(if (stepIndex > 0) 0.65f else 1f)
                            .height(50.dp)
                            .testTag("guide_next_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (stepIndex < steps.size - 1) "Suivant" else "Commencer avec CashPay",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = if (stepIndex < steps.size - 1) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (stepIndex < steps.size - 1) {
                    TextButton(
                        onClick = onFinished,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .testTag("guide_skip_btn")
                    ) {
                        Text(
                            text = "Passer le guide et continuer",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.5.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text(
                    text = "CashPay • Propulsé par FINUSECO SA",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
