package com.example.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.ui.viewmodel.*
import com.example.ui.theme.*
import com.example.util.formatLoanMoney
import com.example.util.formatLoanPercent

data class LoanConfirmationState(val loading:Boolean=false,val application:LoanApplicationQuote?=null,val payment:LoanPaymentQuote?=null,val error:String?=null,val receipt:String?=null)

@Composable
fun LoanConfirmationDialog(uiState:DashboardUiState,viewModel:DashboardViewModel) {
    val state by viewModel.loanConfirmation.collectAsState()
    var accepted by remember(state.application,state.payment) { mutableStateOf(false) }
    val busy=state.loading||uiState.isRequestingLoan||uiState.isRepayingLoan||uiState.isRepayInstallmentLoading
    if (!(state.loading||state.application!=null||state.payment!=null||state.error!=null||state.receipt!=null)) return
    AlertDialog(
        onDismissRequest={if(!busy)viewModel.closeLoanConfirmation()},
        containerColor=Color.White, titleContentColor=ToofanMainDark, textContentColor=ToofanBodyText,
        title={Text(if(state.receipt!=null) "Confirmation serveur" else "Vérifier les conditions")},
        text={Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement=Arrangement.spacedBy(12.dp)) {
            if(busy) CircularProgressIndicator(color=BrandPurple)
            state.application?.let { q ->
                Text(if(q.currency=="CDF") "Compte en francs congolais (CDF)" else "Compte en ${q.currency}", color=BrandPurple, fontWeight=FontWeight.Bold)
                QuoteLine("Montant demandé", formatLoanMoney(q.principal,q.currency))
                QuoteLine("Intérêt forfaitaire", formatLoanPercent(q.interestRate))
                QuoteLine("Total à rembourser", formatLoanMoney(q.totalRepayment,q.currency), true)
                QuoteLine("Garantie requise", formatLoanMoney(q.guaranteeAmount,q.currency))
                HorizontalDivider()
                QuoteLine("Durée", "${q.durationMonths} mois")
                QuoteLine("Fréquence", when(q.repaymentFrequency){"weekly"->"Hebdomadaire";"quarterly"->"Trimestrielle";else->"Mensuelle"})
                QuoteLine("${q.installmentCount} échéances", formatLoanMoney(q.installmentAmount,q.currency))
                QuoteLine("Dernière échéance", formatLoanMoney(q.lastInstallmentAmount,q.currency))
                Text("Les échéances sont calculées depuis la date d’approbation. Le recouvrement automatique commence après sept jours de grâce suivant chaque échéance. Les anciennes dettes restent recouvrables après la fin du contrat.")
                Text("Le reste impayé peut entraîner une pénalité selon le tarif configuré : ${formatLoanMoney(q.penaltyExample,q.currency)} sur une échéance de ${formatLoanMoney(q.installmentAmount,q.currency)}, au plus une fois tous les ${q.penaltyIntervalDays} jours. Le montant dépend du reste dû.")
            }
            state.payment?.let { q ->
                Text("Prêt #${q.loanId}${q.installmentId?.let{" · échéance $it"}?:""}")
                QuoteLine("Remboursement",formatLoanMoney(q.amount,q.currency))
                QuoteLine("Frais",formatLoanMoney(q.fee,q.currency))
                QuoteLine("Total débité",formatLoanMoney(q.totalDebit,q.currency),true)
                QuoteLine("Dette après paiement",formatLoanMoney(q.remaining,q.currency))
            }
            state.receipt?.let { Text(it) }
            val error=state.error?:uiState.loanRequestError?:uiState.loanRepayError?:uiState.repayInstallmentError
            error?.let { Text(it,color=MaterialTheme.colorScheme.error) }
            if(state.application!=null||state.payment!=null) Row {
                Checkbox(accepted,{accepted=it},enabled=!busy,colors=CheckboxDefaults.colors(checkedColor=BrandPurple))
                Text("J’ai lu et j’accepte ces conditions et ce montant.",modifier=Modifier.weight(1f))
            }
        }},
        confirmButton={if(state.application!=null||state.payment!=null) Button(enabled=accepted&&!busy,
            colors=ButtonDefaults.buttonColors(containerColor=BrandPurple),onClick={viewModel.confirmReviewedLoan()}) { Text("Confirmer") }},
        dismissButton={TextButton(enabled=!busy,onClick={viewModel.closeLoanConfirmation()}) { Text("Fermer",color=BrandPurple) }}
    )
}
@Composable
private fun QuoteLine(label:String,value:String,strong:Boolean=false) {
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        Text(label,modifier=Modifier.weight(1f),color=ToofanBodyText)
        Text(value,modifier=Modifier.weight(1f),textAlign=TextAlign.End,
            color=if(strong) BrandPurple else ToofanMainDark,fontWeight=FontWeight.SemiBold)
    }
}
