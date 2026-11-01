package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.InternationalTransferClaimDto
import com.example.ui.theme.MulishFontFamily
import java.text.NumberFormat
import java.util.Locale

// Custom modern colors for Claims Module matching CashPay theme
private val ClaimsDarkBg = Color(0xFF0F172A)
private val ClaimsCardBg = Color(0xFF1E293B)
private val ClaimsPrimary = Color(0xFF00E676)
private val ClaimsBlue = Color(0xFF38BDF8)
private val ClaimsPurple = Color(0xFFA855F7)
private val ClaimsYellow = Color(0xFFF59E0B)
private val ClaimsRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternationalClaimsScreen(
    isOpen: Boolean,
    claims: List<InternationalTransferClaimDto>,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onOpenNewClaim: () -> Unit,
    onSelectClaim: (InternationalTransferClaimDto) -> Unit
) {
    if (!isOpen) return

    var selectedTab by remember { mutableIntStateOf(0) } // 0: En cours, 1: Historique, 2: Toutes

    val inProgressClaims = remember(claims) {
        claims.filter { it.isPendingOrProcessing }
    }
    val historyClaims = remember(claims) {
        claims.filter { it.isTerminal }
    }
    val displayedClaims = remember(selectedTab, claims, inProgressClaims, historyClaims) {
        when (selectedTab) {
            0 -> inProgressClaims
            1 -> historyClaims
            else -> claims
        }
    }

    Scaffold(
        containerColor = ClaimsDarkBg,
        topBar = {
            Surface(
                color = ClaimsCardBg,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("claims_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Retour",
                                    tint = Color.White
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        tint = ClaimsPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "CashPay All",
                                        color = ClaimsPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = MulishFontFamily
                                    )
                                }
                                Text(
                                    text = "Réclamations internationales",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }

                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF334155))
                                .testTag("claims_refresh_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = ClaimsPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualiser",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // + Nouvelle Réclamation CTA Button
                    Button(
                        onClick = onOpenNewClaim,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("claims_new_claim_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClaimsPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nouvelle réclamation",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            fontFamily = MulishFontFamily
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab selector
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = ClaimsPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = ClaimsPrimary,
                                height = 3.dp
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Text(
                                    text = "En cours (${inProgressClaims.size})",
                                    color = if (selectedTab == 0) ClaimsPrimary else Color(0xFF94A3B8),
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = MulishFontFamily,
                                    fontSize = 13.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Text(
                                    text = "Historique (${historyClaims.size})",
                                    color = if (selectedTab == 1) ClaimsPrimary else Color(0xFF94A3B8),
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = MulishFontFamily,
                                    fontSize = 13.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = {
                                Text(
                                    text = "Toutes (${claims.size})",
                                    color = if (selectedTab == 2) ClaimsPrimary else Color(0xFF94A3B8),
                                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = MulishFontFamily,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (errorMessage != null && claims.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = ClaimsRed,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontFamily = MulishFontFamily
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRefresh,
                        colors = ButtonDefaults.buttonColors(containerColor = ClaimsPrimary)
                    ) {
                        Text("Réessayer", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (displayedClaims.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (selectedTab == 0) "Aucune réclamation en cours" else if (selectedTab == 1) "Historique vide" else "Aucune réclamation enregistrée",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MulishFontFamily
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Déclarez un transfert Western Union, MoneyGram ou Ria que vous attendez pour qu'il soit traité par CashPay All.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontFamily = MulishFontFamily
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onOpenNewClaim,
                        colors = ButtonDefaults.buttonColors(containerColor = ClaimsPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Déclarer un transfert", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedClaims, key = { it.id }) { claim ->
                        ClaimItemCard(
                            claim = claim,
                            onClick = { onSelectClaim(claim) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ClaimItemCard(
    claim: InternationalTransferClaimDto,
    onClick: () -> Unit
) {
    val statusColor = when ((claim.status ?: "").lowercase().trim()) {
        "pending" -> ClaimsYellow
        "processing" -> ClaimsBlue
        "approved" -> Color(0xFF06B6D4)
        "processing_payment" -> ClaimsPurple
        "completed" -> ClaimsPrimary
        "rejected", "failed" -> ClaimsRed
        else -> Color(0xFF94A3B8)
    }

    Surface(
        color = ClaimsCardBg,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("claim_card_${claim.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Provider Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF334155)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when ((claim.provider ?: "").lowercase().trim()) {
                                "western_union", "westernunion", "wu" -> "WU"
                                "moneygram", "money_gram", "mg" -> "MG"
                                "ria" -> "RIA"
                                else -> "INT"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = ClaimsPrimary
                        )
                    }
                    Column {
                        Text(
                            text = claim.providerDisplayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = MulishFontFamily
                        )
                        Text(
                            text = "Réf : ${claim.trackingNumber ?: "-"}",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontFamily = MulishFontFamily
                        )
                    }
                }

                // Amount
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${formatMoney(claim.expectedAmount)} ${claim.expectedCurrency ?: "USD"}",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        fontFamily = MulishFontFamily
                    )
                    Text(
                        text = "Reçu en : ${claim.receiveInCurrency ?: "USD"}",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontFamily = MulishFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF334155).copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Origin Country & Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Origine : ${claim.senderCountry ?: "International"}",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.5.sp,
                        fontFamily = MulishFontFamily
                    )
                }

                // Status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            text = claim.statusDisplayName,
                            color = statusColor,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewClaimDialog(
    isOpen: Boolean,
    isSubmitting: Boolean,
    errorMessage: String?,
    successMessage: String?,
    duplicateClaim: InternationalTransferClaimDto?,
    onDismiss: () -> Unit,
    onSubmit: (provider: String, trackingNumber: String, amount: Double, expectedCurrency: String, senderCountry: String, receiveCurrency: String) -> Unit,
    onViewExistingClaim: (InternationalTransferClaimDto) -> Unit
) {
    if (!isOpen) return

    var selectedProvider by remember { mutableStateOf("western_union") }
    var trackingNumber by remember { mutableStateOf("") }
    var expectedAmount by remember { mutableStateOf("") }
    var expectedCurrency by remember { mutableStateOf("USD") }
    var senderCountry by remember { mutableStateOf("US") }
    var receiveInCurrency by remember { mutableStateOf("USD") }
    var localValidationErr by remember { mutableStateOf<String?>(null) }

    val providers = listOf(
        "western_union" to "Western Union",
        "moneygram" to "MoneyGram",
        "ria" to "Ria"
    )
    val currencies = listOf("USD", "EUR", "CDF")
    val commonCountries = listOf(
        "US" to "États-Unis (US)",
        "FR" to "France (FR)",
        "BE" to "Belgique (BE)",
        "CA" to "Canada (CA)",
        "GB" to "Royaume-Uni (GB)",
        "ZA" to "Afrique du Sud (ZA)",
        "CD" to "RDC (CD)",
        "SN" to "Sénégal (SN)",
        "CI" to "Côte d'Ivoire (CI)",
        "CH" to "Suisse (CH)",
        "DE" to "Allemagne (DE)",
        "AE" to "Émirats Arabes Unis (AE)"
    )

    Dialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = ClaimsDarkBg,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Nouvelle réclamation",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = MulishFontFamily
                        )
                        Text(
                            text = "Déclarer un transfert international",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontFamily = MulishFontFamily
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSubmitting,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Important Legal / Functional Notice
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ClaimsPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Aucun débit sur votre portefeuille. Cette démarche permet au back-office CashPay All de traiter le transfert et d'en créditer le montant.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.5.sp,
                            fontFamily = MulishFontFamily,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Choix du fournisseur
                Text(
                    text = "Fournisseur du transfert *",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MulishFontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    providers.forEach { (code, label) ->
                        val isSelected = selectedProvider == code
                        Surface(
                            color = if (isSelected) ClaimsPrimary.copy(alpha = 0.15f) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) ClaimsPrimary else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedProvider = code }
                                .testTag("provider_choice_$code")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) ClaimsPrimary else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    fontSize = 12.sp,
                                    fontFamily = MulishFontFamily,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Numéro de référence (Tracking Number / MTCN)
                Text(
                    text = "Numéro de référence / Code de suivi *",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MulishFontFamily
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = trackingNumber,
                    onValueChange = {
                        trackingNumber = it
                        localValidationErr = null
                    },
                    placeholder = {
                        Text(
                            text = if (selectedProvider == "western_union") "Ex: 1234567890 (MTCN 10 chiffres)" else "Ex: Numéro de référence",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("claim_tracking_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ClaimsPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Montant attendu & Devise
                Text(
                    text = "Montant attendu & Devise du transfert *",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MulishFontFamily
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = expectedAmount,
                        onValueChange = {
                            expectedAmount = it.filter { ch -> ch.isDigit() || ch == '.' }
                            localValidationErr = null
                        },
                        placeholder = { Text("0.00", color = Color(0xFF64748B), fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1.8f)
                            .testTag("claim_amount_input"),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ClaimsPrimary,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B)
                        ),
                        singleLine = true
                    )

                    // Currency Chips
                    Row(
                        modifier = Modifier.weight(1.5f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        currencies.forEach { cur ->
                            val isSel = expectedCurrency == cur
                            Surface(
                                color = if (isSel) ClaimsPrimary else Color(0xFF1E293B),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isSel) ClaimsPrimary else Color(0xFF334155)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp)
                                    .clickable { expectedCurrency = cur }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = cur,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.Black else Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Pays d'origine
                Text(
                    text = "Pays d'origine de l'envoi *",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MulishFontFamily
                )
                Spacer(modifier = Modifier.height(6.dp))
                var countryExpanded by remember { mutableStateOf(false) }
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { countryExpanded = !countryExpanded }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = commonCountries.find { it.first == senderCountry }?.second ?: senderCountry,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.5.sp,
                            fontFamily = MulishFontFamily
                        )
                        Text(
                            text = "Modifier",
                            color = ClaimsPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                if (countryExpanded) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFF162032),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                            items(commonCountries) { (cCode, cName) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            senderCountry = cCode
                                            countryExpanded = false
                                        }
                                        .padding(vertical = 8.dp, horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = cName,
                                        color = if (senderCountry == cCode) ClaimsPrimary else Color.White,
                                        fontWeight = if (senderCountry == cCode) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.5.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Devise de réception souhaitée
                Text(
                    text = "Devise souhaitée pour la réception des fonds *",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MulishFontFamily
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currencies.forEach { cur ->
                        val isSel = receiveInCurrency == cur
                        Surface(
                            color = if (isSel) ClaimsBlue.copy(alpha = 0.2f) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, if (isSel) ClaimsBlue else Color(0xFF334155)),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clickable { receiveInCurrency = cur }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = cur,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) ClaimsBlue else Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error alert
                val displayErr = localValidationErr ?: errorMessage
                if (displayErr != null) {
                    Surface(
                        color = ClaimsRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ClaimsRed.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = ClaimsRed, modifier = Modifier.size(16.dp))
                            Text(text = displayErr, color = ClaimsRed, fontSize = 12.sp, fontFamily = MulishFontFamily)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Conflict: Duplicate claim
                if (duplicateClaim != null) {
                    Surface(
                        color = ClaimsYellow.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ClaimsYellow.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Une réclamation active pour ce numéro de référence existe déjà dans votre compte.",
                                color = ClaimsYellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = { onViewExistingClaim(duplicateClaim) }
                            ) {
                                Text("Voir la réclamation existante (Réf : ${duplicateClaim.trackingNumber})", color = ClaimsPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Success alert
                if (successMessage != null) {
                    Surface(
                        color = ClaimsPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ClaimsPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ClaimsPrimary, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Réclamation envoyée",
                                    color = ClaimsPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = successMessage,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Statut initial : En attente",
                                color = ClaimsYellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Submit Button
                Button(
                    onClick = {
                        val amt = expectedAmount.toDoubleOrNull() ?: 0.0
                        if (trackingNumber.isBlank()) {
                            localValidationErr = "Le numéro de référence du transfert est obligatoire."
                            return@Button
                        }
                        if (amt <= 0.0) {
                            localValidationErr = "Le montant attendu doit être supérieur à zéro."
                            return@Button
                        }
                        localValidationErr = null
                        onSubmit(
                            selectedProvider,
                            trackingNumber.trim(),
                            amt,
                            expectedCurrency,
                            senderCountry,
                            receiveInCurrency
                        )
                    },
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_claim_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ClaimsPrimary,
                        contentColor = Color.Black
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enregistrement en cours...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Soumettre la réclamation",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            fontFamily = MulishFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClaimDetailDialog(
    isOpen: Boolean,
    claim: InternationalTransferClaimDto?,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen || claim == null) return

    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    val statusColor = when ((claim.status ?: "").lowercase().trim()) {
        "pending" -> ClaimsYellow
        "processing" -> ClaimsBlue
        "approved" -> Color(0xFF06B6D4)
        "processing_payment" -> ClaimsPurple
        "completed" -> ClaimsPrimary
        "rejected", "failed" -> ClaimsRed
        else -> Color(0xFF94A3B8)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = ClaimsDarkBg,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ClaimsPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = ClaimsPrimary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "Réclamation internationale",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = MulishFontFamily
                            )
                            Text(
                                text = claim.providerDisplayName,
                                color = ClaimsPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Badge Banner
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Text(
                                text = "Statut : ${claim.statusDisplayName}",
                                color = statusColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main Info Card
                Surface(
                    color = ClaimsCardBg,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Tracking Number
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Numéro de référence :", color = Color(0xFF94A3B8), fontSize = 12.5.sp, fontFamily = MulishFontFamily)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = claim.trackingNumber ?: "-",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    fontFamily = MulishFontFamily
                                )
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(claim.trackingNumber ?: ""))
                                        copied = true
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copier",
                                        tint = if (copied) ClaimsPrimary else Color(0xFF94A3B8),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        // Expected Amount
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Montant attendu :", color = Color(0xFF94A3B8), fontSize = 12.5.sp, fontFamily = MulishFontFamily)
                            Text(
                                text = "${formatMoney(claim.expectedAmount)} ${claim.expectedCurrency ?: "USD"}",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                fontFamily = MulishFontFamily
                            )
                        }

                        // Sender Country
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Pays d'origine :", color = Color(0xFF94A3B8), fontSize = 12.5.sp, fontFamily = MulishFontFamily)
                            Text(
                                text = claim.senderCountry ?: "International",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = MulishFontFamily
                            )
                        }

                        // Receive Currency
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Devise de réception :", color = Color(0xFF94A3B8), fontSize = 12.5.sp, fontFamily = MulishFontFamily)
                            Text(
                                text = claim.receiveInCurrency ?: "USD",
                                color = ClaimsBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = MulishFontFamily
                            )
                        }

                        // Submission Date
                        if (!claim.createdAt.isNullOrBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Date de soumission :", color = Color(0xFF94A3B8), fontSize = 12.5.sp, fontFamily = MulishFontFamily)
                                Text(
                                    text = claim.createdAt.take(10),
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.5.sp,
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }

                        claim.netAmount?.let { Text("Montant net : ${formatMoney(it)} ${claim.receiveInCurrency}", color = Color.White) }
                        if (!claim.rejectionReason.isNullOrBlank()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Motif et consignes de l’agence", color = ClaimsRed, fontWeight = FontWeight.Bold)
                                Text(claim.rejectionReason, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Functional Stage Explanation
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Progression du traitement",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = MulishFontFamily
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when ((claim.status ?: "").lowercase().trim()) {
                                "pending" -> "Votre demande est actuellement en attente de prise en charge par l'équipe back-office CashPay All."
                                "processing" -> "Votre réclamation est en cours d'examen et de vérification auprès de l'opérateur international."
                                "approved" -> "Votre réclamation a été approuvée par le back-office. Le traitement financier va être initié."
                                "processing_payment" -> "Le virement des fonds vers votre portefeuille CashPay All est en cours d'exécution financière."
                                "completed" -> "Le traitement est terminé avec succès. Les fonds ont été crédités sur votre compte."
                                "rejected" -> "La demande n'a pas pu être validée par les services de conformité."
                                "failed" -> "L'opération financière n'a pas pu aboutir. Veuillez contacter le support CashPay."
                                else -> "Traitement en cours selon les procédures CashPay All."
                            },
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            fontFamily = MulishFontFamily,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Fermer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun formatMoney(value: Double): String {
    return try {
        val format = NumberFormat.getNumberInstance(Locale.FRANCE)
        format.minimumFractionDigits = 2
        format.maximumFractionDigits = 2
        format.format(value)
    } catch (_: Exception) {
        String.format(Locale.US, "%.2f", value)
    }
}
