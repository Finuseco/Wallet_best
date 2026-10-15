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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserProfileEntity
import com.example.data.model.RegidesoProvince
import com.example.ui.theme.MulishFontFamily
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.DashboardViewModel

private val BrandGreen = Color(0xFF00C48C)
private val WaterBlue = Color(0xFF0284C7)
private val ElectricYellow = Color(0xFFEAB308)
private val DarkCardBg = Color(0xFF1E293B)
private val DarkBorderColor = Color(0xFF334155)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicInvoicesDialog(
    isOpen: Boolean,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    userProfile: UserProfileEntity?,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val regideso = uiState.regideso
    var activeTab by remember { mutableStateOf(0) } // 0 = REGIDESO (Eau), 1 = SNEL (Électricité)

    val userCountry = userProfile?.country?.trim()?.uppercase() ?: "CD"
    val isCongoUser = userCountry == "CD" ||
            userCountry.contains("CONGO", ignoreCase = true) ||
            userCountry.contains("RDC", ignoreCase = true)

    var provinceMenuExpanded by remember { mutableStateOf(false) }
    val currentProvinceObj = remember(regideso.selectedProvince) {
        RegidesoProvince.ALL_PROVINCES.find { it.code.equals(regideso.selectedProvince, ignoreCase = true) }
            ?: RegidesoProvince.ALL_PROVINCES.first()
    }

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
                                .background(WaterBlue.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ReceiptLong,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Factures Publiques",
                                fontFamily = MulishFontFamily,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Consultation REGIDESO & Services publics",
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

                // Tabs: REGIDESO vs SNEL
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCardBg)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == 0) WaterBlue else Color.Transparent)
                            .clickable { activeTab = 0 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.WaterDrop,
                                contentDescription = null,
                                tint = if (activeTab == 0) Color.White else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "REGIDESO (Eau)",
                                fontSize = 13.sp,
                                fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (activeTab == 0) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == 1) ElectricYellow.copy(alpha = 0.8f) else Color.Transparent)
                            .clickable { activeTab = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Bolt,
                                contentDescription = null,
                                tint = if (activeTab == 1) Color(0xFF0F172A) else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "SNEL (Électricité)",
                                fontSize = 13.sp,
                                fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (activeTab == 1) Color(0xFF0F172A) else Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (activeTab == 0) {
                    // REGIDESO Flow
                    if (!isCongoUser) {
                        // Country restriction notice
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = DarkCardBg,
                            border = BorderStroke(1.dp, DarkBorderColor),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Public,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = "Service disponible en RDC",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "La consultation des factures REGIDESO est actuellement configurée pour les abonnés en République Démocratique du Congo. L'ouverture pour d'autres pays sera prochainement disponible.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    } else {
                        // Invoices Consultation Form & List
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Province & Abonnement input card
                            item {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = DarkCardBg,
                                    border = BorderStroke(1.dp, DarkBorderColor),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "Paramètres de recherche de l'abonnement :",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFE2E8F0)
                                        )

                                        // Province selector
                                        Column {
                                            Text(
                                                text = "Province :",
                                                fontSize = 12.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Box {
                                                Surface(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .clickable { provinceMenuExpanded = true },
                                                    color = Color(0xFF0F172A),
                                                    border = BorderStroke(1.dp, DarkBorderColor)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column {
                                                            Text(
                                                                text = currentProvinceObj.name,
                                                                fontSize = 13.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                            )
                                                            Text(
                                                                text = currentProvinceObj.description,
                                                                fontSize = 11.sp,
                                                                color = Color(0xFF64748B)
                                                            )
                                                        }
                                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                                                    }
                                                }

                                                DropdownMenu(
                                                    expanded = provinceMenuExpanded,
                                                    onDismissRequest = { provinceMenuExpanded = false },
                                                    modifier = Modifier.background(Color(0xFF1E293B))
                                                ) {
                                                    RegidesoProvince.ALL_PROVINCES.forEach { prov ->
                                                        DropdownMenuItem(
                                                            text = {
                                                                Column {
                                                                    Text(prov.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                                    Text(prov.description, color = Color(0xFF94A3B8), fontSize = 11.sp)
                                                                }
                                                            },
                                                            onClick = {
                                                                viewModel.setRegidesoProvince(prov.code)
                                                                provinceMenuExpanded = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Abonnement ID input
                                        Column {
                                            Text(
                                                text = "Numéro d'abonnement / Référence client :",
                                                fontSize = 12.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            OutlinedTextField(
                                                value = regideso.abonnementId,
                                                onValueChange = { viewModel.setRegidesoAbonnementId(it) },
                                                placeholder = { Text("Ex: 83262", color = Color(0xFF64748B), fontSize = 12.sp) },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = WaterBlue,
                                                    unfocusedBorderColor = DarkBorderColor,
                                                    focusedTextColor = Color.White,
                                                    unfocusedTextColor = Color.White,
                                                    focusedContainerColor = Color(0xFF0F172A),
                                                    unfocusedContainerColor = Color(0xFF0F172A)
                                                ),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                        }

                                        // Search button
                                        Button(
                                            onClick = { viewModel.fetchRegidesoInvoices() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(46.dp),
                                            enabled = !regideso.isLoading,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = WaterBlue,
                                                disabledContainerColor = DarkBorderColor
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            if (regideso.isLoading) {
                                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Recherche en cours...", color = Color.White, fontSize = 13.sp)
                                            } else {
                                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Consulter les factures", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            // Error notice
                            if (regideso.errorMessage != null) {
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
                                            Text(regideso.errorMessage, color = Color(0xFFFCA5A5), fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            // Invoices results header
                            if (regideso.hasSearched) {
                                item {
                                    Text(
                                        text = "Factures trouvées (${regideso.factures.size}) :",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE2E8F0)
                                    )
                                }

                                if (regideso.factures.isEmpty() && !regideso.isLoading) {
                                    item {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = DarkCardBg,
                                            border = BorderStroke(1.dp, DarkBorderColor),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(20.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(36.dp))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text("Aucune facture impayée trouvée", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("Toutes vos factures pour cet abonnement sont à jour.", color = Color(0xFF94A3B8), fontSize = 12.sp, textAlign = TextAlign.Center)
                                            }
                                        }
                                    }
                                } else {
                                    items(regideso.factures) { facture ->
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = DarkCardBg,
                                            border = BorderStroke(1.dp, DarkBorderColor),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = facture.numeroFacture?.let { "Facture $it" } ?: "Facture REGIDESO",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = Color.White
                                                    )
                                                    val statusText = facture.statutFacture ?: "Impayée"
                                                    val isPaid = facture.isPaid
                                                    Surface(
                                                        color = if (isPaid) BrandGreen.copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f),
                                                        shape = RoundedCornerShape(6.dp)
                                                    ) {
                                                        Text(
                                                            text = statusText,
                                                            color = if (isPaid) BrandGreen else Color(0xFFEF4444),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }

                                                if (!facture.nomClient.isNullOrBlank()) {
                                                    Text(
                                                        text = "Abonné : ${facture.nomClient}",
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF94A3B8)
                                                    )
                                                }

                                                if (!facture.periode.isNullOrBlank()) {
                                                    Text(
                                                        text = "Période de facturation : ${facture.periode}",
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF94A3B8)
                                                    )
                                                }

                                                Divider(color = DarkBorderColor, thickness = 1.dp)

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("Montant à régler :", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                                    Text(
                                                        text = "${"%,d".format(facture.effectiveAmount.toLong()).replace(',', ' ')} ${facture.effectiveCurrency}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp,
                                                        color = if (facture.isPaid) BrandGreen else Color(0xFFFDE047)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Informative footer note
                            item {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFF0F172A),
                                    border = BorderStroke(1.dp, DarkBorderColor),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "Ce service permet de consulter vos factures réelles. Le règlement direct depuis votre portefeuille CashPay sera ouvert lors du prochain déploiement.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // SNEL tab (Electricity)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        color = DarkCardBg,
                        border = BorderStroke(1.dp, DarkBorderColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(ElectricYellow.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Bolt,
                                    contentDescription = null,
                                    tint = ElectricYellow,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "SNEL (Électricité)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = ElectricYellow.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Bientôt disponible",
                                    color = ElectricYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "L'interconnexion avec la Société Nationale d'Électricité (SNEL) pour le paiement et le rechargement de vos compteurs à prépaiement et post-paiement sera disponible très prochainement.",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
