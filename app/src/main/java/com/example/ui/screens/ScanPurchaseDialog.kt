package com.example.ui.screens
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch
@Composable
fun ScanPurchaseDialog(raw:String,viewModel:DashboardViewModel,onClose:()->Unit){
 val scope=rememberCoroutineScope()
 var result by remember(raw){mutableStateOf<ScanResult?>(null)}
 var input by remember(raw){mutableStateOf<ScanInput?>(null)}
 var quote by remember(raw){mutableStateOf<ScanQuote?>(null)}
 var currency by remember(raw){mutableStateOf("USD")}
 var quantity by remember(raw){mutableStateOf("1")}
 var error by remember(raw){mutableStateOf<String?>(null)}
 var receipt by remember(raw){mutableStateOf<String?>(null)}
 var busy by remember(raw){mutableStateOf(true)}
 var pin by remember(raw){mutableStateOf("")}
 suspend fun price(scan:ScanInput){busy=true;error=null;quote=null;input=scan;viewModel.resolveCommerceScan(ScanRequest(action="quote",scan=scan,paymentCurrency=currency)).onSuccess{quote=it.scan?.quote}.onFailure{error=it.message};busy=false}
 LaunchedEffect(raw){viewModel.resolveCommerceScan(ScanRequest(value=raw)).onSuccess{response->result=response.scan;response.scan?.scan?.let{price(it)}}.onFailure{error=it.message};busy=false}
 AlertDialog(onDismissRequest={if(!busy)onClose()},title={Text("Vérifier le paiement")},text={Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)){
  if(busy)CircularProgressIndicator()
  error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
  receipt?.let{Text(it)}
  if(receipt==null){
   Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){listOf("USD","EUR","CDF").forEach{c->FilterChip(selected=currency==c,label={Text(c)},enabled=!busy,onClick={currency=c;quote=null;pin=""})}}
   if(quote==null&&result?.products?.isNotEmpty()==true){
    Text("Choisissez le magasin et l’article. Les prix ci-dessous sont ceux du catalogue du magasin.")
    OutlinedTextField(quantity,{quantity=it},label={Text("Quantité")},enabled=!busy)
    result!!.products.forEach{product->OutlinedButton(enabled=!busy&&(quantity.toIntOrNull()?:0)>0,onClick={scope.launch{price(ScanInput("POS_SALE",sellerWalletId=product.sellerWalletId,items=listOf(ScanItem(product.id,quantity.toInt()))))}}){Text("${product.name} · ${product.boutiqueName?:product.sellerName}\n${product.price} ${product.currency} · stock ${product.stock}")}}
   }
   quote?.let{q->
    Text(if(q.sale.type=="POS_SALE")"Prix catalogue vérifié" else "Montant demandé par QR — à vérifier avec le bénéficiaire")
    Text("${q.sale.sellerName}\nWallet : ${q.sale.sellerWalletId}")
    q.sale.items.forEach{Text("${it.quantity} × ${it.name} : ${it.sellingPrice} ${q.sale.currency}")}
    Text("Montant : ${q.sale.amount} ${q.sale.currency}\nConversion : ${q.rate}\nFrais : ${q.fee} ${q.paymentCurrency}\nTotal débité : ${q.totalDebit} ${q.paymentCurrency}")
    Text("Le PIN autorise ce montant exact. Un prix ou taux modifié impose un nouveau devis.")
    OutlinedTextField(pin,{pin=it.filter(Char::isDigit).take(4)},label={Text("Votre PIN")},visualTransformation=PasswordVisualTransformation(),enabled=!busy)
    Button(enabled=!busy&&pin.length==4,onClick={scope.launch{busy=true;error=null;viewModel.payCommerceScan(q.quoteToken,pin).onSuccess{receipt="Paiement confirmé. Référence : ${it.reference?:"Consultez votre historique"}";pin="";viewModel.refreshAfterCommercePayment()}.onFailure{error=it.message};busy=false}}){Text("Confirmer le paiement")}
   }
   if(input!=null)OutlinedButton(enabled=!busy,onClick={scope.launch{price(input!!)}}){Text("Actualiser le devis")}
  }
 }},confirmButton={TextButton(onClick=onClose,enabled=!busy){Text("Fermer")}})
}
