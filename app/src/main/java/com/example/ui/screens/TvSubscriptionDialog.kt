package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TvPackageDto
import com.example.data.model.TvProviderInfo
import com.example.data.model.TvSubscriptionResponse
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.DashboardViewModel

private val BrandGreen = Color(0xFF00C48C)
private val DarkCardBg = Color(0xFF1E293B)
private val DarkBorderColor = Color(0xFF334155)
private val AccentBlue = Color(0xFF2563EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvSubscriptionDialog(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val tv = uiState.tv
    if (!tv.isTvDialogOpen) return

    val currentProviderInfo = remember(tv.selectedProvider) {
        TvProviderInfo.ALL_PROVIDERS.find { it.code.equals(tv.selectedProvider, ignoreCase = true) }
            ?: TvProviderInfo.ALL_PROVIDERS.first()
    }

    var selectedTab by remember { mutableStateOf(0) } // 0 = Souscrire, 1 = Historique

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
                // Header
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
                                .size(44.dp)
                                .background(AccentBlue.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = null,
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Abonnements TV",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Canal+ RDC, EasyTV, BLUS",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(DarkBorderColor.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs: Souscrire vs Historique
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 0) AccentBlue else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nouveau réabonnement",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 0) Color.White else Color(0xFF94A3B8)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 1) AccentBlue else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Historique (${tv.recentSubscriptions.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == 0) {
                    // Subscription Flow Form
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Provider Selector
                        item {
                            Text(
                                text = "1. Choisissez votre fournisseur TV :",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE2E8F0)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TvProviderInfo.ALL_PROVIDERS.forEach { p ->
                                    val isSelected = tv.selectedProvider.equals(p.code, ignoreCase = true)
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { viewModel.selectTvProvider(p.code) },
                                        color = if (isSelected) AccentBlue.copy(alpha = 0.25f) else DarkCardBg,
                                        border = BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) AccentBlue else DarkBorderColor
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = p.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Package Selector
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "2. Sélectionnez un forfait (${currentProviderInfo.name}) :",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFE2E8F0)
                                )
                                if (tv.isLoadingPackages) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = AccentBlue
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            if (tv.packagesError != null) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFF7F1D1D).copy(alpha = 0.3f),
                                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(18.dp))
                                        Text(tv.packagesError, color = Color(0xFFFCA5A5), fontSize = 12.sp)
                                    }
                                }
                            } else if (tv.packages.isEmpty() && !tv.isLoadingPackages) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = DarkCardBg,
                                    border = BorderStroke(1.dp, DarkBorderColor),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "Aucun forfait disponible pour ce fournisseur.",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    tv.packages.forEach { pkg ->
                                        val isSelected = tv.selectedPackage?.id == pkg.id
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { viewModel.selectTvPackage(pkg) },
                                            color = if (isSelected) AccentBlue.copy(alpha = 0.2f) else DarkCardBg,
                                            border = BorderStroke(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) AccentBlue else DarkBorderColor
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = pkg.effectiveName,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = Color.White
                                                    )
                                                    if (!pkg.description.isNullOrBlank()) {
                                                        Text(
                                                            text = pkg.description,
                                                            fontSize = 11.sp,
                                                            color = Color(0xFF94A3B8)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = "${"%,d".format(pkg.effectivePrice.toLong()).replace(',', ' ')} ${pkg.effectiveCurrency}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFF60A5FA)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Smartcard / Decoder number
                        item {
                            Text(
                                text = "3. ${currentProviderInfo.smartcardLabel} :",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE2E8F0)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = tv.smartcardNumber,
                                onValueChange = { viewModel.setTvSmartcardNumber(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(currentProviderInfo.smartcardPlaceholder, color = Color(0xFF64748B), fontSize = 12.sp)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentBlue,
                                    unfocusedBorderColor = DarkBorderColor,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = DarkCardBg,
                                    unfocusedContainerColor = DarkCardBg
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        // 4. Payment Currency selection (CDF, USD, EUR)
                        item {
                            Text(
                                text = "4. Portefeuille de débit :",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE2E8F0)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("CDF", "USD", "EUR").forEach { curr ->
                                    val isSelected = tv.paymentCurrency.equals(curr, ignoreCase = true)
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { viewModel.setTvPaymentCurrency(curr) },
                                        color = if (isSelected) BrandGreen.copy(alpha = 0.2f) else DarkCardBg,
                                        border = BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) BrandGreen else DarkBorderColor
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = curr,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (isSelected) Color.White else Color(0xFF94A3B8)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 5. Financial breakdown & Conversion Quote
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, DarkBorderColor),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Détail financier de l'opération",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )

                                    val catalogPrice = tv.selectedPackage?.effectivePrice ?: 0.0
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Tarif catalogue (Référence)", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                        Text("${"%,d".format(catalogPrice.toLong()).replace(',', ' ')} CDF", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Frais de service CashPay TV", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                        Text("0,00 ${tv.paymentCurrency} (Gratuit)", fontSize = 12.sp, color = BrandGreen, fontWeight = FontWeight.SemiBold)
                                    }

                                    if (tv.paymentCurrency != "CDF") {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Taux de change RDC appliqué", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                            if (tv.isCalculatingQuote) {
                                                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp, color = AccentBlue)
                                            } else if (tv.quoteRate != null) {
                                                Text("1 CDF = ${tv.quoteRate} ${tv.paymentCurrency}", fontSize = 12.sp, color = Color(0xFFFDE047), fontWeight = FontWeight.Medium)
                                            } else {
                                                Text("Indisponible", fontSize = 12.sp, color = Color(0xFFEF4444))
                                            }
                                        }
                                    }

                                    Divider(color = DarkBorderColor, thickness = 1.dp)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Montant net à débiter", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        val finalAmount = tv.estimatedDebitAmount
                                        if (finalAmount != null) {
                                            Text(
                                                text = if (tv.paymentCurrency == "CDF") {
                                                    "${"%,d".format(finalAmount.toLong()).replace(',', ' ')} CDF"
                                                } else {
                                                    String.format("%.2f %s", finalAmount, tv.paymentCurrency)
                                                },
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandGreen
                                            )
                                        } else {
                                            Text("--", fontSize = 14.sp, color = Color(0xFF94A3B8))
                                        }
                                    }

                                    if (tv.quoteError != null) {
                                        Text(
                                            text = tv.quoteError,
                                            fontSize = 11.sp,
                                            color = Color(0xFFEF4444),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        // 6. PIN Entry
                        item {
                            Text(
                                text = "5. Code PIN CashPay (Validation) :",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE2E8F0)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = tv.pin,
                                onValueChange = { viewModel.setTvPin(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Code PIN secret à 4 chiffres", color = Color(0xFF64748B), fontSize = 12.sp) },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandGreen,
                                    unfocusedBorderColor = DarkBorderColor,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = DarkCardBg,
                                    unfocusedContainerColor = DarkCardBg
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        // Submit error message
                        if (tv.submitError != null) {
                            item {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFF7F1D1D).copy(alpha = 0.35f),
                                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(18.dp))
                                        Text(tv.submitError, color = Color(0xFFFCA5A5), fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // Submit Success Banner / Result
                        if (tv.subscriptionResult != null) {
                            item {
                                val res = tv.subscriptionResult
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = BrandGreen.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, BrandGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                                            Text(
                                                text = "Demande enregistrée",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.White
                                            )
                                        }
                                        Text(
                                            text = res.message ?: "Votre demande d'abonnement a été enregistrée. Elle est actuellement en attente de traitement administratif.",
                                            fontSize = 12.sp,
                                            color = Color(0xFFD1FAE5)
                                        )
                                        if (res.reference != null || res.transactionId != null) {
                                            Text(
                                                text = "Réf : ${res.reference ?: res.transactionId}",
                                                fontSize = 11.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom Action Button
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { viewModel.submitTvSubscription() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                enabled = !tv.isSubmitting && tv.selectedPackage != null && tv.smartcardNumber.isNotBlank() && tv.pin.length >= 4 && tv.quoteError == null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandGreen,
                                    disabledContainerColor = DarkBorderColor
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (tv.isSubmitting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Transmission sécurisée...", color = Color.White, fontSize = 14.sp)
                                } else {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Confirmer et Payer le réabonnement",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Paiement direct sécurisé. Le montant de votre bouquet sera débité après validation.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    // History Tab
                    if (tv.recentSubscriptions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.History, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Aucun abonnement TV récent", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Vos réabonnements TV apparaîtront ici avec leur statut.", color = Color(0xFF64748B), fontSize = 12.sp)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(tv.recentSubscriptions) { sub ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = DarkCardBg,
                                    border = BorderStroke(1.dp, DarkBorderColor),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${sub.provider ?: "TV"} - ${sub.packageName ?: "Forfait"}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.White
                                            )
                                            val statusText = sub.status ?: "EN ATTENTE"
                                            val statusColor = when (statusText.uppercase()) {
                                                "ACTIVE", "APPROUVE", "SUCCESS" -> BrandGreen
                                                "REJETE", "FAILED" -> Color(0xFFEF4444)
                                                else -> Color(0xFFF59E0B)
                                            }
                                            Surface(
                                                color = statusColor.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = statusText,
                                                    color = statusColor,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        if (!sub.smartcardNumber.isNullOrBlank()) {
                                            Text(
                                                text = "Carte / Décodeur : ${sub.smartcardNumber}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            val debAmt = sub.debitedAmount ?: sub.amount ?: 0.0
                                            val debCur = sub.debitedCurrency ?: sub.currency ?: "CDF"
                                            Text(
                                                text = "Débit : ${"%.2f".format(debAmt)} $debCur",
                                                fontSize = 12.sp,
                                                color = Color(0xFFE2E8F0),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            if (!sub.reference.isNullOrBlank()) {
                                                Text(
                                                    text = "Réf: ${sub.reference}",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
