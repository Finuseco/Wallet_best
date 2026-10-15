package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.CountryDto
import com.example.data.model.ExpressMobileMoneyData
import com.example.data.model.MobileMoneyCountry
import com.example.data.model.MobileMoneyOperatorInfo
import com.example.ui.components.ToofanButton
import com.example.ui.components.ToofanInputField
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun WithdrawalDialog(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val isExpress = uiState.withdrawalType == "mobile_money"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isExpress) Color(0xFFFF6600).copy(alpha = 0.15f) else ToofanGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isExpress) Icons.Default.PhoneAndroid else Icons.Default.Storefront,
                            contentDescription = null,
                            tint = if (isExpress) Color(0xFFFF6600) else ToofanGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = if (isExpress) "Retrait Express Mobile Money" else "Retrait d'espèces",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = if (isExpress) "Vers SIM & Comptes Mobile Money Afrique" else "Auprès d'un Agent Agréé CashPay",
                        fontFamily = MulishFontFamily,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Stepper Indicator
                if (uiState.withdrawalStep < 5) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Étape ${uiState.withdrawalStep}/4",
                            fontFamily = MulishFontFamily,
                            fontSize = 11.sp,
                            color = if (isExpress) Color(0xFFFF6600) else ToofanGreen,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressBar(
                            step = uiState.withdrawalStep,
                            maxSteps = 4,
                            isOrange = isExpress,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp)
                        )
                    }
                }

                when (uiState.withdrawalStep) {
                    1 -> StepSelectTypeAndRecipient(uiState, viewModel)
                    2 -> StepEnterAmount(uiState, viewModel)
                    3 -> StepPreviewWithdrawal(uiState, viewModel)
                    4 -> StepConfirmPin(uiState, viewModel)
                    5 -> StepWithdrawalSuccess(uiState, viewModel, onDismiss)
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.withdrawalStep > 1 && uiState.withdrawalStep < 5) {
                    TextButton(
                        onClick = { viewModel.prevWithdrawalStep() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Précédent",
                            fontFamily = MulishFontFamily,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                when (uiState.withdrawalStep) {
                    1 -> {
                        ToofanButton(
                            title = if (uiState.withdrawalType == "agent_cash") "Rechercher" else "Suivant",
                            onClick = {
                                if (uiState.withdrawalType == "agent_cash") {
                                    viewModel.searchAgentAndProceed()
                                } else {
                                    if (uiState.withdrawalRecipient.trim().length >= 4) {
                                        viewModel.proceedToAmount()
                                    }
                                }
                            },
                            isLoading = uiState.isSearchingAgent,
                            enabled = if (uiState.withdrawalType == "agent_cash") {
                                uiState.withdrawalRecipient.isNotBlank()
                            } else {
                                uiState.withdrawalRecipient.trim().length >= 4
                            },
                            modifier = Modifier.width(130.dp)
                        )
                    }
                    2 -> {
                        ToofanButton(
                            title = "Suivant",
                            onClick = { viewModel.proceedToPreview() },
                            isLoading = uiState.isWithdrawalPreviewLoading,
                            enabled = uiState.withdrawalAmount.toDoubleOrNull() != null && uiState.withdrawalAmount.toDouble() > 0,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                    3 -> {
                        val canConfirm = uiState.withdrawalPreview?.canConfirm == true
                        ToofanButton(
                            title = "Continuer",
                            onClick = { viewModel.proceedToPinEntry() },
                            enabled = canConfirm,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                    4 -> {
                        // Handled inside StepConfirmPin directly
                        Spacer(modifier = Modifier.width(0.dp))
                    }
                    5 -> {
                        ToofanButton(
                            title = "Fermer",
                            onClick = onDismiss,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
        },
        dismissButton = {
            if (uiState.withdrawalStep < 5) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Annuler",
                        fontFamily = MulishFontFamily,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    )
}

@Composable
fun StepSelectTypeAndRecipient(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    var showCountryDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Toggle Method (Agent cash vs Mobile Money)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (uiState.withdrawalType == "mobile_money") Color(0xFFFF6600) else Color.Transparent)
                    .clickable { viewModel.setWithdrawalType("mobile_money") },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = if (uiState.withdrawalType == "mobile_money") Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Mobile Money",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (uiState.withdrawalType == "mobile_money") Color.White else Color(0xFF94A3B8)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (uiState.withdrawalType == "agent_cash") ToofanGreen else Color.Transparent)
                    .clickable { viewModel.setWithdrawalType("agent_cash") },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = if (uiState.withdrawalType == "agent_cash") Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Chez Agent Cash",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (uiState.withdrawalType == "agent_cash") Color.White else Color(0xFF94A3B8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        if (uiState.withdrawalType == "agent_cash") {
            Text(
                text = "Identifier le Portefeuille Agent",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )
            Text(
                text = "Saisissez le Wallet ID ou le numéro de téléphone de l'agent agréé CashPay.",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )

            ToofanInputField(
                value = uiState.withdrawalRecipient,
                onValueChange = { viewModel.setWithdrawalRecipient(it) },
                placeholder = "Ex: CP789012 ou 0812...",
                modifier = Modifier.testTag("withdrawal_agent_input")
            )

            if (uiState.agentSearchError == "CLIENT_FOUND") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF451A22)),
                    border = BorderStroke(1.dp, Color(0xFFFF4868).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF4868),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Portefeuille Client Détecté",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFFF8599)
                            )
                        }

                        Text(
                            text = "Le portefeuille de ${uiState.searchedAgentProfile?.fullName ?: "l'utilisateur"} possède le rôle 'Client'. Un portefeuille client ne peut pas effectuer de retrait agent.",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )

                        Button(
                            onClick = {
                                viewModel.closeWithdrawalDialog()
                                uiState.searchedAgentProfile?.let { profile ->
                                    viewModel.triggerPrefilledTransfer(profile.fullName, profile)
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF4868),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Faire un transfert d'argent",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else if (uiState.agentSearchError != null) {
                Text(
                    text = uiState.agentSearchError,
                    color = Color(0xFFFF4868),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // Mobile Money Flow
            val activeCountry = ExpressMobileMoneyData.getCountry(uiState.withdrawalCountryCode)

            // Country Selector Card
            Text(
                text = "1. Pays de destination (Mobile Money)",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                    .clickable { showCountryDialog = true },
                color = Color(0xFF1E293B)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = activeCountry.flagEmoji, fontSize = 22.sp)
                        Column {
                            Text(
                                text = activeCountry.name,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Indicatif : ${activeCountry.dialCode} • ${activeCountry.operators.size} opérateurs",
                                fontFamily = MulishFontFamily,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFF6600).copy(alpha = 0.18f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Changer",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFFFF6600)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color(0xFFFF6600),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Operators selector for the selected country
            Text(
                text = "2. Opérateur disponible en ${activeCountry.name}",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
            )

            // Dynamic grid of operators for selected country
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val operators = activeCountry.operators
                // Display in rows of 2 or 3
                val rows = operators.chunked(3)
                rows.forEach { rowOperators ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowOperators.forEach { op ->
                            val isSelected = (uiState.withdrawalOperator ?: "").equals(op.code, ignoreCase = true)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFFF6600) else Color(0xFF334155),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setWithdrawalOperator(op.code) },
                                color = if (isSelected) Color(0xFFFF6600).copy(alpha = 0.15f) else Color(0xFF1E293B)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (op.logoRes != null) {
                                        Image(
                                            painter = painterResource(id = op.logoRes),
                                            contentDescription = op.name,
                                            modifier = Modifier.height(22.dp),
                                            contentScale = ContentScale.Inside
                                        )
                                    } else {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(op.brandColor),
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = (op.badge ?: op.name).take(2).uppercase(),
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = op.name,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = if (isSelected) Color(0xFFFF6600) else Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Beneficiary phone number
            Text(
                text = "3. Numéro de téléphone bénéficiaire (${activeCountry.dialCode}) *",
                fontFamily = MulishFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            ToofanInputField(
                value = uiState.withdrawalRecipient,
                onValueChange = { viewModel.setWithdrawalRecipient(it) },
                placeholder = "Ex: ${activeCountry.dialCode} 812 345 678",
                modifier = Modifier.testTag("withdrawal_phone_input")
            )
        }
    }

    // Modal Country Selection Dialog
    if (showCountryDialog) {
        CountrySelectionDialog(
            currentCountryCode = uiState.withdrawalCountryCode,
            onCountrySelected = { selected ->
                viewModel.setWithdrawalCountry(
                    countryCode = selected.code,
                    countryName = selected.name,
                    dialCode = selected.dialCode
                )
                showCountryDialog = false
            },
            onDismiss = { showCountryDialog = false }
        )
    }
}

@Composable
fun StepEnterAmount(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    val isExpress = uiState.withdrawalType == "mobile_money"
    val country = ExpressMobileMoneyData.getCountry(uiState.withdrawalCountryCode)
    val op = country.operators.find { it.code.equals(uiState.withdrawalOperator, ignoreCase = true) }
        ?: country.operators.firstOrNull()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (!isExpress) {
            // Show selected agent details
            uiState.searchedAgentProfile?.let { agent ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ToofanGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column {
                            Text(
                                text = agent.fullName,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            val boutiqueName = agent.boutiques?.firstOrNull()?.name ?: "Boutique Agent"
                            Text(
                                text = "Agent Agréé • $boutiqueName",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        } else {
            // Show selected Mobile Money Operator & Country details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFF6600).copy(alpha = 0.18f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = country.flagEmoji, fontSize = 20.sp)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${country.name} • ${op?.name ?: uiState.withdrawalOperator}",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Bénéficiaire : ${uiState.withdrawalRecipient}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        Text(
            text = "Montant du Retrait",
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color.White
        )

        ToofanInputField(
            value = uiState.withdrawalAmount,
            onValueChange = { viewModel.setWithdrawalAmount(it) },
            placeholder = "Saisir le montant",
            modifier = Modifier.testTag("withdrawal_amount_input")
        )

        // Quick amount chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val chips = listOf("10", "25", "50", "100", "200")
            chips.forEach { chipVal ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setWithdrawalAmount(chipVal) }
                ) {
                    Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "$chipVal",
                            fontFamily = MulishFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Text(
            text = "Devise de facturation",
            fontFamily = MulishFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Dynamic Currencies based on user request:
        // Local DRC supports CDF & USD. International transfers to other African countries use USD & EUR.
        val availableCurrencies = country.supportedCurrencies

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableCurrencies.forEach { curr ->
                val isSelected = uiState.withdrawalCurrency == curr
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFFFF6600) else Color(0xFF334155),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { viewModel.setWithdrawalCurrency(curr) },
                    color = if (isSelected) Color(0xFFFF6600).copy(alpha = 0.15f) else Color(0xFF1E293B)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = curr,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isSelected) Color(0xFFFF6600) else Color.White
                        )
                    }
                }
            }
        }

        // Informative guidance note based on country & currency
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (country.isLocalDrc) ToofanGreen.copy(alpha = 0.12f) else Color(0xFF0284C7).copy(alpha = 0.12f),
            border = BorderStroke(1.dp, if (country.isLocalDrc) ToofanGreen.copy(alpha = 0.3f) else Color(0xFF0284C7).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = if (country.isLocalDrc) ToofanGreen else Color(0xFF38BDF8),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = country.infoNotice ?: if (country.isLocalDrc) {
                        "Pour les retraits locaux en RDC, le Franc Congolais (CDF) et le Dollar (USD) sont acceptés."
                    } else {
                        "Pour les retraits internationaux vers ce pays, les devises traitées sont l'USD et l'EUR."
                    },
                    fontFamily = MulishFontFamily,
                    fontSize = 10.sp,
                    color = if (country.isLocalDrc) ToofanGreen else Color(0xFF38BDF8)
                )
            }
        }

        if (uiState.withdrawalPreviewError != null) {
            Text(
                text = uiState.withdrawalPreviewError,
                color = Color(0xFFFF4868),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StepPreviewWithdrawal(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    val preview = uiState.withdrawalPreview
    val isExpress = uiState.withdrawalType == "mobile_money"
    val country = ExpressMobileMoneyData.getCountry(uiState.withdrawalCountryCode)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Récapitulatif & Prévisualisation",
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
        )

        preview?.let { p ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WithdrawalRecapRow("Mode de Retrait :", if (isExpress) "Retrait Express Mobile Money" else "Retrait chez Agent")
                    if (isExpress) {
                        WithdrawalRecapRow("Pays de destination :", "${country.flagEmoji} ${country.name}")
                        WithdrawalRecapRow("Opérateur :", uiState.withdrawalOperator ?: "")
                        WithdrawalRecapRow("Numéro bénéficiaire :", uiState.withdrawalRecipient)
                    } else {
                        WithdrawalRecapRow("Agent agréé :", uiState.searchedAgentProfile?.fullName ?: uiState.withdrawalRecipient)
                    }

                    HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)

                    WithdrawalRecapRow("Montant demandé :", "${String.format("%.2f", p.amount)} ${p.currency}")
                    WithdrawalRecapRow("Frais estimés :", "${String.format("%.2f", p.fee)} ${p.currency}")
                    WithdrawalRecapRow("Total à débiter :", "${String.format("%.2f", p.total)} ${p.currency}", isBold = true, customColor = if (isExpress) Color(0xFFFF6600) else ToofanGreen)

                    HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)

                    WithdrawalRecapRow("Délai de réception :", "Instantané (Temps réel)")
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = if (isExpress) Color(0xFFFF6600).copy(alpha = 0.08f) else ToofanGreen.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, if (isExpress) Color(0xFFFF6600).copy(alpha = 0.3f) else ToofanGreen.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isExpress) Color(0xFFFF6600) else ToofanGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Opération prête à être validée avec votre code PIN.",
                        fontFamily = MulishFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExpress) Color(0xFFFF6600) else ToofanGreen
                    )
                }
            }
        }
    }
}

@Composable
fun StepConfirmPin(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    var pinValue by remember { mutableStateOf("") }
    val isExpress = uiState.withdrawalType == "mobile_money"

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Saisir votre Code PIN CashPay",
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Afin de valider le retrait de ${uiState.withdrawalAmount} ${uiState.withdrawalCurrency}, veuillez saisir votre code confidentiel à 4 chiffres.",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        ToofanInputField(
            value = pinValue,
            onValueChange = { input ->
                pinValue = input.filter { it.isDigit() }.take(4)
            },
            placeholder = "PIN à 4 chiffres",
            modifier = Modifier
                .width(180.dp)
                .testTag("withdrawal_pin_input")
        )

        if (uiState.withdrawalConfirmError != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF7F1D1D).copy(alpha = 0.35f),
                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = uiState.withdrawalConfirmError,
                        color = Color(0xFFFECACA),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ToofanButton(
            title = if (isExpress) "Confirmer le Retrait Express" else "Confirmer le Retrait",
            onClick = { viewModel.confirmWithdrawal(userId = 1, pin = pinValue) },
            isLoading = uiState.isWithdrawalConfirmLoading,
            enabled = pinValue.length == 4,
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

@Composable
fun StepWithdrawalSuccess(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val isExpress = uiState.withdrawalType == "mobile_money"
    val country = ExpressMobileMoneyData.getCountry(uiState.withdrawalCountryCode)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (isExpress) Color(0xFFFF6600) else ToFocusBorderColor,
            modifier = Modifier.size(60.dp)
        )

        Text(
            text = if (isExpress) "Retrait Express Envoyé !" else "Retrait Réussi !",
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )

        Text(
            text = if (isExpress) {
                "Votre demande de retrait express vers le compte Mobile Money a été enregistrée avec succès."
            } else {
                "Votre retrait de ${uiState.withdrawalAmount} ${uiState.withdrawalCurrency} a été traité avec succès par CashPay."
            },
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                WithdrawalRecapRow("Type d'opération :", if (isExpress) "Retrait Express (Mobile Money)" else "Retrait Agent (Cash)")
                WithdrawalRecapRow("Montant transféré :", "${uiState.withdrawalAmount} ${uiState.withdrawalCurrency}")
                if (isExpress) {
                    WithdrawalRecapRow("Destination :", "${country.flagEmoji} ${country.name}")
                    WithdrawalRecapRow("Opérateur :", uiState.withdrawalOperator ?: "")
                    WithdrawalRecapRow("Bénéficiaire :", uiState.withdrawalRecipient)
                } else {
                    WithdrawalRecapRow("Point de retrait :", uiState.searchedAgentProfile?.fullName ?: "")
                }
                WithdrawalRecapRow("Statut :", "SUCCÈS", isBold = true, customColor = if (isExpress) Color(0xFFFF6600) else ToofanGreen)
            }
        }
    }
}

@Composable
fun CountrySelectionDialog(
    currentCountryCode: String,
    onCountrySelected: (MobileMoneyCountry) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val allCountries = ExpressMobileMoneyData.AFRICAN_COUNTRIES

    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allCountries
        } else {
            val q = searchQuery.trim().lowercase()
            allCountries.filter {
                it.name.lowercase().contains(q) ||
                it.code.lowercase().contains(q) ||
                it.dialCode.contains(q) ||
                it.operators.any { op -> op.name.lowercase().contains(q) }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(18.dp)),
            color = Color(0xFF0F172A),
            shadowElevation = 16.dp,
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = Color(0xFFFF6600),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Pays Mobile Money",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color(0xFF94A3B8))
                    }
                }

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Rechercher pays ou indicatif (+243, +225...)",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6600),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B)
                    )
                )

                Text(
                    text = "${filtered.size} pays africains supportés pour le retrait Mobile Money :",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                // List of countries
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered) { country ->
                        val isSelected = country.code.equals(currentCountryCode, ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFFFF6600) else Color(0xFF334155),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onCountrySelected(country) },
                            color = if (isSelected) Color(0xFFFF6600).copy(alpha = 0.15f) else Color(0xFF1E293B)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(text = country.flagEmoji, fontSize = 24.sp)
                                    Column {
                                        Text(
                                            text = country.name,
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${country.dialCode} • ${country.operators.joinToString { it.name }}",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 10.sp,
                                            color = Color(0xFF94A3B8),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFFFF6600),
                                        modifier = Modifier.size(18.dp)
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

@Composable
private fun LinearProgressBar(
    step: Int,
    maxSteps: Int,
    isOrange: Boolean = false,
    modifier: Modifier = Modifier
) {
    val progress = (step.toFloat() / maxSteps.toFloat()).coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xFF334155))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(RoundedCornerShape(3.dp))
                .background(if (isOrange) Color(0xFFFF6600) else ToofanGreen)
        )
    }
}

@Composable
private fun WithdrawalRecapRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    customColor: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = MulishFontFamily,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )
        Text(
            text = value,
            fontFamily = MulishFontFamily,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 12.sp,
            color = customColor ?: Color.White
        )
    }
}
