package com.example.ui.screens
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.ui.viewmodel.*
data class LoanConfirmationState(val loading:Boolean=false,val application:LoanApplicationQuote?=null,val payment:LoanPaymentQuote?=null,val error:String?=null,val receipt:String?=null)
@Composable
fun LoanConfirmationDialog(uiState:DashboardUiState,viewModel:DashboardViewModel){
 val state by viewModel.loanConfirmation.collectAsState()
 var accepted by remember(state.application,state.payment){mutableStateOf(false)}
 val busy=state.loading||uiState.isRequestingLoan||uiState.isRepayingLoan||uiState.isRepayInstallmentLoading
 if(state.loading||state.application!=null||state.payment!=null||state.error!=null||state.receipt!=null)AlertDialog(onDismissRequest={if(!busy)viewModel.closeLoanConfirmation()},title={Text(if(state.receipt!=null)"Confirmation serveur" else "Vérifier les conditions")},text={Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)){
  if(busy)CircularProgressIndicator()
  state.application?.let{q->Text("Montant demandé : ${q.principal} ${q.currency}\nIntérêt forfaitaire : ${q.interestRate} %\nTotal à rembourser : ${q.totalRepayment} ${q.currency}\nGarantie requise : ${q.guaranteeAmount} ${q.currency}\nDurée : ${q.durationMonths} mois\nFréquence : ${when(q.repaymentFrequency){"weekly"->"Hebdomadaire";"quarterly"->"Trimestrielle";else->"Mensuelle"}}\n${q.installmentCount} échéances de ${q.installmentAmount} ${q.currency} (dernière : ${q.lastInstallmentAmount}).")
   Text("Les échéances sont calculées depuis la date d’approbation. Les échéances peuvent être prélevées automatiquement à leur date. Le reste impayé peut entraîner une pénalité selon le tarif configuré : ${q.penaltyExample} ${q.currency} sur une échéance de ${q.installmentAmount}, au plus une fois tous les ${q.penaltyIntervalDays} jours. Le montant dépend du reste dû.")}
  state.payment?.let{q->Text("Prêt #${q.loanId}${q.installmentId?.let{" · échéance $it"}?:""}\nRemboursement : ${q.amount} ${q.currency}\nFrais : ${q.fee} ${q.currency}\nTotal débité : ${q.totalDebit} ${q.currency}\nDette après paiement : ${q.remaining} ${q.currency}")}
  state.receipt?.let{Text(it)}
  val error=state.error?:uiState.loanRequestError?:uiState.loanRepayError?:uiState.repayInstallmentError
  error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
  if(state.application!=null||state.payment!=null)Row{Checkbox(accepted,{accepted=it},enabled=!busy);Text("J’ai lu et j’accepte ces conditions et ce montant.")}
 }},confirmButton={if(state.application!=null||state.payment!=null)Button(enabled=accepted&&!busy,onClick={viewModel.confirmReviewedLoan()}){Text("Confirmer")}},dismissButton={TextButton(enabled=!busy,onClick={viewModel.closeLoanConfirmation()}){Text("Fermer")}})
}
