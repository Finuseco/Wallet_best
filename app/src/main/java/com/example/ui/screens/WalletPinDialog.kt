package com.example.ui.screens
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch
@Composable
fun WalletPinDialog(viewModel:DashboardViewModel){
 var old by remember{mutableStateOf("")};var fresh by remember{mutableStateOf("")};var confirm by remember{mutableStateOf("")}
 var busy by remember{mutableStateOf(false)};var message by remember{mutableStateOf<String?>(null)};var complete by remember{mutableStateOf(false)}
 val scope=rememberCoroutineScope()
 AlertDialog(onDismissRequest={if(!busy)viewModel.closeWalletPin()},title={Text("Modifier le PIN du portefeuille")},text={Column(verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text("Le PIN CashPay reste distinct du PIN de votre carte bancaire.")
  if(!complete){
   OutlinedTextField(old,{old=it.filter(Char::isDigit).take(4)},label={Text("PIN actuel")},enabled=!busy,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.NumberPassword))
   OutlinedTextField(fresh,{fresh=it.filter(Char::isDigit).take(4)},label={Text("Nouveau PIN")},enabled=!busy,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.NumberPassword))
   OutlinedTextField(confirm,{confirm=it.filter(Char::isDigit).take(4)},label={Text("Confirmer le nouveau PIN")},enabled=!busy,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.NumberPassword))
  }
  if(busy)CircularProgressIndicator();message?.let{Text(it)}
 }},confirmButton={if(!complete)TextButton(enabled=!busy&&old.length==4&&fresh.length==4&&fresh==confirm&&old!=fresh,onClick={scope.launch{
  busy=true;message=null;viewModel.changeWalletPin(old,fresh,confirm).onSuccess{complete=true;message="PIN du portefeuille modifié.";old="";fresh="";confirm=""}.onFailure{message=it.message};busy=false
 }}){Text("Enregistrer")}},dismissButton={TextButton(enabled=!busy,onClick={viewModel.closeWalletPin()}){Text("Fermer")}})
}
