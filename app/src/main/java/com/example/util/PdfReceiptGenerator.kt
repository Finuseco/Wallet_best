package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.*
import android.print.pdf.PrintedPdfDocument
import androidx.core.content.FileProvider
import com.example.data.local.TransactionEntity
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil

private inline fun <T : PdfDocument, R> T.withPdf(block: (T) -> R): R = try { block(this) } finally { close() }

object PdfReceiptGenerator {
    fun generateReceiptPdf(context: Context, bitmap: Bitmap, reference: String, paperMm: Int = 80): File {
        require(paperMm == 58 || paperMm == 80)
        val width = (paperMm * 72 / 25.4).toInt()
        val height = ceil(bitmap.height * width.toDouble() / bitmap.width).toInt()
        val folder = File(context.cacheDir,"receipts").apply{mkdirs()}
        folder.listFiles()?.filter { it.isFile && it.name.startsWith("CashPay_") && it.extension=="pdf" && it.lastModified()<System.currentTimeMillis()-86400000L }?.forEach { it.delete() }
        val safeName = reference.replace(Regex("[^a-zA-Z0-9_-]"),"_").take(80).ifBlank{"transaction"}
        val file = File.createTempFile("CashPay_${safeName}_", ".pdf",folder)
        try {
            PdfDocument().withPdf { document ->
                val page=document.startPage(PdfDocument.PageInfo.Builder(width,height,1).create())
                page.canvas.drawBitmap(bitmap,null,RectF(0f,0f,width.toFloat(),height.toFloat()),Paint(Paint.FILTER_BITMAP_FLAG))
                document.finishPage(page)
                FileOutputStream(file).use{document.writeTo(it)}
            }
        } catch(error:Exception) { file.delete();throw error }
        return file
    }
    fun share(context: Context, file: File) {
        val uri=FileProvider.getUriForFile(context,"${context.packageName}.fileprovider",file)
        val intent=Intent(Intent.ACTION_SEND).apply {
            type="application/pdf"
            putExtra(Intent.EXTRA_STREAM,uri)
            clipData=android.content.ClipData.newRawUri("Reçu CashPay",uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent,"Partager le reçu CashPay"))
    }
    // Kept for existing callers; transaction detail uses separate export/share/print controls.
    fun generateAndShareReceiptPdf(context: Context, transaction: TransactionEntity): File? = runCatching {
        val bitmap=PosReceipt.render(context,transaction)
        try { generateReceiptPdf(context,bitmap,transaction.reference).also{share(context,it)} }
        finally {bitmap.recycle()}
    }.getOrNull()

    fun print(context: Context, bitmap: Bitmap, paperMm: Int) {
        val manager=context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val initialHeight=ceil(bitmap.height.toDouble()/bitmap.width*paperMm/25.4*1000).toInt()
        val attributes=PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize("CASHPAY_POS_${paperMm}","CashPay POS $paperMm mm",(paperMm/25.4*1000).toInt(),initialHeight))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
            .setResolution(PrintAttributes.Resolution("POS203","POS 203 dpi",203,203)).build()
        // The print spooler can outlive the transaction dialog.
        val printable=bitmap.copy(Bitmap.Config.ARGB_8888,false)
        manager.print("Reçu CashPay",object: PrintDocumentAdapter() {
            var attrs=attributes
            var pageCount=1
            override fun onLayout(old:PrintAttributes?,new:PrintAttributes,cancellation:CancellationSignal,callback:LayoutResultCallback,extras:android.os.Bundle?) {
                if(cancellation.isCanceled){callback.onLayoutCancelled();return}
                attrs=new
                PrintedPdfDocument(context,attrs).withPdf { probe ->
                    val rect=probe.pageContentRect
                    if(rect.width()<=0 || rect.height()<=0){callback.onLayoutFailed("Format de papier invalide");return}
                    pageCount=ceil(printable.height.toDouble()*rect.width()/printable.width/rect.height()).toInt().coerceAtLeast(1)
                }
                callback.onLayoutFinished(PrintDocumentInfo.Builder("CashPay.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(pageCount).build(),true)
            }
            override fun onWrite(ranges:Array<out PageRange>,destination:ParcelFileDescriptor,cancellation:CancellationSignal,callback:WriteResultCallback) {
                try {
                    PrintedPdfDocument(context,attrs).withPdf { document ->
                        val rect=document.pageContentRect
                        val scale=rect.width().toFloat()/printable.width
                        for(index in 0 until pageCount) {
                            if(cancellation.isCanceled){callback.onWriteCancelled();return}
                            if(ranges.none{index in it.start..it.end})continue
                            val page=document.startPage(index)
                            page.canvas.save()
                            page.canvas.clipRect(rect)
                            page.canvas.translate(rect.left.toFloat(),rect.top.toFloat()-index*rect.height())
                            page.canvas.scale(scale,scale)
                            page.canvas.drawBitmap(printable,0f,0f,Paint(Paint.FILTER_BITMAP_FLAG))
                            page.canvas.restore()
                            document.finishPage(page)
                        }
                        FileOutputStream(destination.fileDescriptor).use{document.writeTo(it)}
                    }
                    callback.onWriteFinished(ranges)
                } catch(error:Exception){callback.onWriteFailed("Impression impossible. Réessayez.")}
            }
            override fun onFinish(){printable.recycle()}
        },attributes)
    }
}
