package com.example.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ToofanButton
import com.example.ui.components.ToofanInputField
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun LoansDialog(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = ToFocusBorderColor,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Prêts CashPay",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ToofanMainDark
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sub-Tab Selector (Offer/Request, Active, History)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ToofanGrey1.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf("Offres", "Prêt Actif", "Historique")
                    tabs.forEachIndexed { index, label ->
                        val isSelected = uiState.loanSelectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ToofanGreen else Color.Transparent)
                                .clickable { viewModel.setLoanSelectedTab(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else ToofanMainDark
                            )
                        }
                    }
                }

                Divider(color = Color.LightGray.copy(alpha = 0.3f))

                when (uiState.loanSelectedTab) {
                    0 -> SubTabLoanOffer(uiState, viewModel)
                    1 -> SubTabActiveLoan(uiState, viewModel)
                    2 -> SubTabLoanHistory(uiState, viewModel)
                }
            }
        },
        confirmButton = {
            ToofanButton(
                title = "Fermer",
                onClick = onDismiss,
                modifier = Modifier.width(100.dp)
            )
        }
    )
}

@Composable
fun SubTabLoanOffer(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    if (uiState.isLoadingLoanOffer) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ToofanGreen)
        }
    } else if (uiState.loanOfferError != null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFFF4868), modifier = Modifier.size(38.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = uiState.loanOfferError,
                fontFamily = MulishFontFamily,
                fontSize = 12.sp,
                color = Color(0xFFFF4868),
                textAlign = TextAlign.Center
            )
        }
    } else {
        val offer = uiState.loanOffer
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (offer != null) {
                item {
                    // Eligibility banner
                    if (offer.eligible) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = ToofanGreen.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, ToofanGreen.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "✓ Félicitations ! Vous êtes éligible à un prêt.",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ToofanGreen
                                )
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECEF)),
                            border = BorderStroke(1.dp, Color(0xFFFF4868).copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFFF4868), modifier = Modifier.size(20.dp))
                                    Text(
                                        text = "Inéligible au prêt",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF4868)
                                    )
                                }
                                Text(
                                    text = offer.reason ?: "Votre compte ne remplit pas les conditions minimales actuelles.",
                                    fontSize = 11.sp,
                                    color = ToofanBodyText
                                )
                            }
                        }
                    }
                }

                item {
                    // Parameters Display
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ToofanGrey1.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            RecapRow("Intérêt configuré :", "${offer.interestRate ?: 0.0}%")
                            RecapRow("Taux de garantie :", "${(offer.guaranteeRate ?: 0.0) * 100}%")
                            RecapRow("Solde maximum disponible :", "${String.format("%.2f", offer.balance)} ${offer.currency}")
                        }
                    }
                }

                if (offer.eligible) {
                    item {
                        Text(
                            text = "Montant demandé",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ToofanMainDark
                        )
                        ToofanInputField(
                            value = uiState.loanRequestAmount,
                            onValueChange = { viewModel.setLoanRequestAmount(it) },
                            placeholder = "Saisissez le montant"
                        )
                    }

                    item {
                        Text(
                            text = "Durée du prêt",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ToofanMainDark
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            offer.durations.forEach { dur ->
                                val isSelected = uiState.loanRequestDuration == dur
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) ToofanGreen else Color.LightGray,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setLoanRequestDuration(dur) },
                                    color = if (isSelected) ToofanGreen.copy(alpha = 0.08f) else Color.White
                                ) {
                                    Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$dur Mois",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) ToofanGreen else ToofanMainDark
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        if (uiState.loanRequestError != null) {
                            Text(
                                text = uiState.loanRequestError,
                                color = Color(0xFFFF4868),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (uiState.loanRequestSuccess) {
                            Text(
                                text = "✓ Demande de prêt soumise avec succès !",
                                color = ToofanGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        ToofanButton(
                            title = "Soumettre la demande",
                            onClick = { viewModel.submitLoanRequest() },
                            isLoading = uiState.isRequestingLoan,
                            enabled = uiState.loanRequestAmount.toDoubleOrNull() != null && uiState.loanRequestAmount.toDouble() > 0
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "Aucune offre de prêt disponible pour le moment.",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = ToofanBodyText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun SubTabActiveLoan(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    if (uiState.isLoadingActiveLoan) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ToFocusBorderColor)
        }
    } else if (uiState.activeLoanError != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(uiState.activeLoanError, color = Color(0xFFFF4868), fontSize = 12.sp)
        }
    } else {
        val loan = uiState.activeLoan
        if (loan == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.OfflinePin, contentDescription = null, tint = ToofanGrey1, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Aucun Prêt Actif",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ToofanMainDark
                )
                Text(
                    text = "Vous n'avez aucun prêt approuvé ou en cours de remboursement.",
                    fontSize = 11.sp,
                    color = ToofanBodyText,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Loan Card details
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ToFocusBorderColor.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, ToFocusBorderColor.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Prêt Actif #${loan.id}",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ToFocusBorderColor
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ToofanGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = (loan.status ?: "APPROVED").uppercase(),
                                        fontFamily = MulishFontFamily,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ToofanGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                            RecapRow("Principal :", "${String.format("%.2f", loan.principalAmount ?: loan.amount)} ${loan.currency}")
                            RecapRow("Total à rembourser :", "${String.format("%.2f", loan.totalRepayment ?: loan.amount)} ${loan.currency}")
                            RecapRow("Remboursé à ce jour :", "${String.format("%.2f", loan.repaidAmount)} ${loan.currency}")
                            RecapRow("Reste à payer :", "${String.format("%.2f", loan.remainingBalance)} ${loan.currency}", isBold = true, customColor = ToFocusBorderColor)

                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                            RecapRow("Date de début :", loan.startDate ?: "-")
                            RecapRow("Date de fin :", loan.endDate ?: "-")
                        }
                    }
                }

                item {
                    // Manual repayment input
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Remboursement Manuel",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ToofanMainDark
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                ToofanInputField(
                                    value = uiState.loanRepayAmount,
                                    onValueChange = { viewModel.setLoanRepayAmount(it) },
                                    placeholder = "Montant"
                                )
                            }
                            ToofanButton(
                                title = "Payer",
                                onClick = { viewModel.submitLoanRepayment(loan.id) },
                                isLoading = uiState.isRepayingLoan,
                                enabled = uiState.loanRepayAmount.toDoubleOrNull() != null && uiState.loanRepayAmount.toDouble() > 0,
                                modifier = Modifier.width(90.dp)
                            )
                        }
                        if (uiState.loanRepayError != null) {
                            Text(uiState.loanRepayError, color = Color(0xFFFF4868), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        if (uiState.loanRepaySuccess) {
                            Text("✓ Remboursement manuel effectué !", color = ToofanGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (loan.repayments.isNotEmpty()) {
                    item {
                        Text(
                            text = "Historique des paiements",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ToofanMainDark
                        )
                    }
                    items(loan.repayments) { rep ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ToofanWhite,
                            border = BorderStroke(1.dp, ToofanGrey1.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Surface(shape = CircleShape, color = ToofanGreen.copy(alpha = 0.1f), modifier = Modifier.size(32.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Column {
                                        Text(text = "Remboursement", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ToofanMainDark)
                                        Text(text = rep.date ?: "-", fontSize = 10.sp, color = ToofanBodyText)
                                    }
                                }
                                Text(
                                    text = "- ${String.format("%.2f", rep.amount)} ${loan.currency}",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFFF4868)
                                )
                            }
                        }
                    }
                }

                if (loan.schedule.isNotEmpty()) {
                    item {
                        Text(
                            text = "Plan d'échéances",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ToofanMainDark
                        )
                    }

                    items(loan.schedule) { inst ->
                        val isPaid = (inst.status ?: "").lowercase() == "paid"
                        val isOverdue = (inst.status ?: "").lowercase() == "overdue"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, if (isPaid) ToofanGreen.copy(alpha = 0.3f) else if (isOverdue) Color(0xFFFF4868).copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Échéance #${inst.number} — ${String.format("%.2f", inst.amount)} ${loan.currency}",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = ToofanMainDark
                                    )
                                    Text(
                                        text = "Date limite: ${inst.dueDate ?: "-"}",
                                        fontSize = 10.sp,
                                        color = ToFocusBorderColor
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isPaid) ToofanGreen.copy(alpha = 0.15f) else if (isOverdue) Color(0xFFFFECEF) else ToofanGrey1.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = (inst.status ?: "PENDING").uppercase(),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPaid) ToofanGreen else if (isOverdue) Color(0xFFFF4868) else ToofanBodyText,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }

                                    if (!isPaid) {
                                        ToofanButton(
                                            title = "Rembourser",
                                            onClick = { viewModel.submitInstallmentRepayment(loan.id, inst.id) },
                                            isLoading = uiState.isRepayInstallmentLoading,
                                            modifier = Modifier
                                                .height(28.dp)
                                                .width(84.dp)
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

@Composable
fun SubTabLoanHistory(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "ACTIVE", "REPAID", "FINISHED", "REJECTED"

    val filteredList = remember(uiState.loanHistory, selectedFilter) {
        when (selectedFilter) {
            "ACTIVE" -> uiState.loanHistory.filter { (it.status ?: "").lowercase() in listOf("approved", "active", "en_cours") }
            "REPAID" -> uiState.loanHistory.filter { (it.status ?: "").lowercase() in listOf("repaid", "remboursé") }
            "FINISHED" -> uiState.loanHistory.filter { it.isFinished || (it.status ?: "").lowercase() in listOf("finished", "terminé") }
            "REJECTED" -> uiState.loanHistory.filter { (it.status ?: "").lowercase() in listOf("rejected", "rejeté") }
            else -> uiState.loanHistory
        }
    }

    if (uiState.isLoadingLoanHistory) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ToofanGreen)
        }
    } else if (uiState.loanHistoryError != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(uiState.loanHistoryError, color = Color(0xFFFF4868), fontSize = 12.sp)
        }
    } else if (uiState.loanHistory.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.History, contentDescription = null, tint = ToofanGrey1, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Historique vide",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ToofanMainDark
            )
            Text(
                text = "Vous n'avez aucun dossier de prêt enregistré dans l'historique.",
                fontSize = 11.sp,
                color = ToofanBodyText,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Filter categories: Tous, En cours, Remboursés, Terminés, Rejetés
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val filters = listOf(
                    "ALL" to "Tous",
                    "ACTIVE" to "En cours",
                    "REPAID" to "Remboursés",
                    "FINISHED" to "Terminés",
                    "REJECTED" to "Rejetés"
                )
                filters.forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) ToofanGreen else ToofanGrey1.copy(alpha = 0.15f),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedFilter = key }
                    ) {
                        Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = if (isSelected) Color.White else ToofanMainDark
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { item ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Prêt #${item.id} — ${String.format("%.2f", item.amount)} ${item.currency}",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ToofanMainDark
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (item.status?.lowercase()) {
                                        "approved", "active", "en_cours" -> ToofanGreen.copy(alpha = 0.15f)
                                        "repaid", "remboursé" -> ToofanBlue.copy(alpha = 0.15f)
                                        "rejected", "rejeté" -> Color(0xFFFFECEF)
                                        else -> ToFocusBorderColor.copy(alpha = 0.15f)
                                    }
                                ) {
                                    Text(
                                        text = (item.status ?: "REQUESTED").uppercase(),
                                        fontFamily = MulishFontFamily,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (item.status?.lowercase()) {
                                            "approved", "active", "en_cours" -> ToofanGreen
                                            "repaid", "remboursé" -> ToofanBlue
                                            "rejected", "rejeté" -> Color(0xFFFF4868)
                                            else -> ToFocusBorderColor
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Restant : ${String.format("%.2f", item.remainingBalance)} ${item.currency} • Déjà remboursé : ${String.format("%.2f", item.repaidAmount)} ${item.currency}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ToFocusBorderColor
                            )
                            Text(
                                text = "Date de demande: ${item.startDate ?: "-"} • Échéance: ${item.endDate ?: "-"}",
                                fontSize = 9.sp,
                                color = ToofanBodyText
                            )
                        }
                    }
                }
            }
        }
    }
}
