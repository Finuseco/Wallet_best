package com.example.ui.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.TransactionEntity
import com.example.util.PdfReceiptGenerator
import com.example.util.PosReceipt
import com.example.util.NetworkPosPrinter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun TransactionReceiptDialog(transaction: TransactionEntity, onDismiss: () -> Unit) {
    val context=LocalContext.current
    val scope=rememberCoroutineScope()
    var paperMm by rememberSaveable { mutableIntStateOf(80) }
    var attempt by remember { mutableIntStateOf(0) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var networkOpen by remember { mutableStateOf(false) }
    var printerIp by rememberSaveable { mutableStateOf("") }
    var printerPort by rememberSaveable { mutableStateOf("9100") }
    var printNotice by remember { mutableStateOf<String?>(null) }
    var downloadPath by rememberSaveable { mutableStateOf<String?>(null) }
    val download=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val file=downloadPath?.let{File(it)}
        downloadPath=null
        if(uri!=null && file!=null) scope.launch {
            busy=true
            try {
                withContext(Dispatchers.IO) {
                    val output=context.contentResolver.openOutputStream(uri) ?: error("Destination indisponible")
                    output.use{target->file.inputStream().use{it.copyTo(target)}}
                }
            } catch(exception:Exception) { error="Enregistrement impossible. Réessayez." }
            finally { busy=false;file.delete() }
        } else { file?.delete();busy=false }
    }
    LaunchedEffect(transaction,paperMm,attempt) {
        bitmap=null;error=null
        try {bitmap=withContext(Dispatchers.Default){PosReceipt.render(context,transaction,paperMm)}}
        catch(exception:Exception){error="Le bordereau n’a pas pu être préparé."}
    }
    fun export(share:Boolean) {
        if(busy)return
        val image=bitmap ?: return
        scope.launch {
            busy=true;error=null
            try {
                val file=withContext(Dispatchers.IO){PdfReceiptGenerator.generateReceiptPdf(context,image,transaction.reference,paperMm)}
                if(share) PdfReceiptGenerator.share(context,file)
                else {downloadPath=file.absolutePath;download.launch("CashPay_${transaction.reference.replace(Regex("[^a-zA-Z0-9_-]"),"_").take(60)}.pdf")}
            } catch(exception:Exception){
                downloadPath?.let{File(it).delete()};downloadPath=null
                error="Export impossible. Réessayez."
            }
            finally {if(downloadPath==null)busy=false}
        }
    }
    if(networkOpen) AlertDialog(
        onDismissRequest={if(!busy)networkOpen=false},
        title={Text("Imprimante réseau ESC/POS")},
        text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
            Text("Connectez le téléphone au réseau de l’imprimante. Le ticket sera envoyé directement à l’adresse indiquée, sans confirmation de réception matérielle.")
            OutlinedTextField(value=printerIp,onValueChange={printerIp=it},label={Text("Adresse IP")},singleLine=true,enabled=!busy)
            OutlinedTextField(value=printerPort,onValueChange={printerPort=it.filter(Char::isDigit).take(5)},label={Text("Port")},singleLine=true,enabled=!busy)
            if(busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        }},
        confirmButton={TextButton(enabled=!busy&&printerIp.isNotBlank()&&(printerPort.toIntOrNull() ?: 0) in 1..65535,onClick={
            val image=bitmap
            if(image!=null && !busy) scope.launch {
                busy=true;error=null;printNotice=null
                try {withContext(Dispatchers.IO){NetworkPosPrinter.print(image,printerIp,printerPort.toInt())};networkOpen=false;printNotice="Ticket envoyé à l’imprimante. Vérifiez la sortie papier avant de réessayer."}
                catch(exception:Exception){error="Envoi non confirmé. Vérifiez le papier et l’adresse ; un nouvel essai peut imprimer un doublon."}
                finally {busy=false}
            }
        }){Text("Envoyer le ticket")}},
        dismissButton={TextButton(enabled=!busy,onClick={networkOpen=false;error=null}){Text("Annuler")}}
    )
    if(!networkOpen) Dialog(onDismissRequest={if(!busy)onDismiss()}) {
        Surface(shape=MaterialTheme.shapes.large, modifier=Modifier.fillMaxWidth().fillMaxHeight(0.94f)) {
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                Text("Bordereau CashPay · POS",style=MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    listOf(58,80).forEach { mm -> FilterChip(selected=paperMm==mm,onClick={paperMm=mm},enabled=!busy,label={Text("$mm mm")}) }
                }
                Box(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())) {
                    bitmap?.let { image ->
                        Image(image.asImageBitmap(),contentDescription="Bordereau ${transaction.reference}. ${PosReceipt.status(transaction.status)}. ${PosReceipt.money(transaction.amount,transaction.currency)}. ${transaction.date}",modifier=Modifier.fillMaxWidth().aspectRatio(image.width.toFloat()/image.height))
                    } ?: if(error==null) CircularProgressIndicator() else Text("Aperçu indisponible")
                }
                error?.let{Text(it,color=MaterialTheme.colorScheme.error,style=MaterialTheme.typography.bodySmall)}
                if(error!=null && bitmap==null) TextButton(onClick={attempt++}){Text("Réessayer")}
                if(busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick={export(false)},enabled=bitmap!=null&&!busy,modifier=Modifier.weight(1f)){Text("Télécharger")}
                    Button(onClick={export(true)},enabled=bitmap!=null&&!busy,modifier=Modifier.weight(1f)){Text("Partager")}
                }
                OutlinedButton(onClick={bitmap?.let{image->runCatching{PdfReceiptGenerator.print(context,image,paperMm)}.onFailure{error="Service d’impression indisponible."}}},enabled=bitmap!=null&&!busy,modifier=Modifier.fillMaxWidth()){Text("Imprimer sur POS")}
                TextButton(onClick={networkOpen=true},enabled=bitmap!=null&&!busy,modifier=Modifier.fillMaxWidth()){Text("POS réseau · ESC/POS")}
                printNotice?.let{Text(it,style=MaterialTheme.typography.bodySmall)}
                TextButton(onClick=onDismiss,enabled=!busy,modifier=Modifier.fillMaxWidth()){Text("Fermer")}
            }
        }
    }
}
