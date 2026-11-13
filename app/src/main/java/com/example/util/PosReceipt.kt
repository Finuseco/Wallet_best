package com.example.util

import android.content.Context
import android.graphics.*
import com.example.R
import com.example.data.local.TransactionEntity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

/** A reference is an identifier, not a payment request or a proof of authenticity. */
object PosReceipt {
    fun money(amount: Double, currency: String): String {
        if (!amount.isFinite()) return "— $currency"
        val scale = when (currency.uppercase(Locale.ROOT)) { "BTC" -> 8; "USDT", "USDC" -> 6; else -> runCatching { java.util.Currency.getInstance(currency.uppercase(Locale.ROOT)).defaultFractionDigits.coerceAtLeast(0) }.getOrDefault(2) }
        return "${BigDecimal.valueOf(amount).setScale(scale, RoundingMode.HALF_UP).toPlainString()} $currency"
    }
    fun total(transaction: TransactionEntity): Double =
        if (transaction.direction.equals("incoming", true)) transaction.amount else transaction.amount + transaction.fee
    fun status(raw: String): String = when (raw.lowercase(Locale.ROOT)) {
        "completed", "success", "successful" -> "CONFIRMÉE"
        "pending", "processing" -> "EN COURS"
        "failed", "error" -> "ÉCHOUÉE"
        "cancelled", "canceled", "rejected" -> "ANNULÉE / REJETÉE"
        else -> raw.uppercase(Locale.ROOT).ifBlank { "NON RENSEIGNÉ" }
    }
    fun render(context: Context, tx: TransactionEntity, paperMm: Int = 80): Bitmap {
        require(paperMm == 58 || paperMm == 80)
        val width = if (paperMm == 58) 384 else 576 // 203 dpi thermal heads
        val margin = 24f
        val usable = width - 2 * margin
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; textSize = 22f }
        val logo = BitmapFactory.decodeResource(context.resources, R.drawable.cashpay_logo)
        fun code(format: BarcodeFormat, w: Int, h: Int): Bitmap? = runCatching {
            require(tx.reference.isNotBlank() && tx.reference.length <= 512)
            val matrix = MultiFormatWriter().encode(tx.reference, format, w, h, mapOf(EncodeHintType.MARGIN to if (format == BarcodeFormat.QR_CODE) 4 else 12, EncodeHintType.CHARACTER_SET to "UTF-8"))
            require(matrix.width == w && matrix.height == h) // Never crop an oversized barcode.
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
                val pixels = IntArray(w*h) { i -> if(matrix[i%w,i/w]) Color.BLACK else Color.WHITE }
                setPixels(pixels,0,w,0,0,w,h)
            }
        }.getOrNull()
        val qr = code(BarcodeFormat.QR_CODE, 216, 216)
        val barcode = code(BarcodeFormat.CODE_128, usable.toInt(), 76)
        fun layout(canvas: Canvas?): Int {
            var y = 24f
            fun text(value: String, size: Float = 22f, bold: Boolean = false, centered: Boolean = false) {
                paint.textSize = size
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, if (bold) Typeface.BOLD else Typeface.NORMAL)
                // breakText handles long references and words without spaces as well as names.
                value.replace('\r',' ').split('\n').forEach { paragraph ->
                    var rest = paragraph
                    do {
                        val count = paint.breakText(rest, true, usable, null).coerceAtLeast(1).coerceAtMost(rest.length)
                        val line = rest.take(count)
                        canvas?.drawText(line, if(centered) (width-paint.measureText(line))/2 else margin, y + size, paint)
                        y += size * 1.4f
                        rest = rest.drop(count)
                    } while(rest.isNotEmpty())
                }
            }
            fun line() { y+=12; paint.strokeWidth=1f; canvas?.drawLine(margin,y,width-margin,y,paint);y+=16 }
            fun row(label:String,value:String) { text(label,18f);text(value.ifBlank{"—"},22f,true);y+=10 }
            val logoWidth = minOf(usable, 250f)
            val logoHeight = logoWidth * logo.height / logo.width
            canvas?.drawBitmap(logo,null,RectF((width-logoWidth)/2,y,(width+logoWidth)/2,y+logoHeight),Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
            y+=logoHeight+18
            text("BORDEREAU DE TRANSACTION",20f,true,true)
            text(TransactionTypeMapper.getTypeInfo(tx.type).nameFr,22f,true,true)
            text(status(tx.status),20f,true,true)
            line()
            val incoming = tx.direction.equals("incoming",true)
            text(money(tx.amount,tx.currency),if(paperMm==58) 26f else 34f,true,true)
            text(if(incoming) "ENTRÉE DE FONDS" else "SORTIE DE FONDS",18f,false,true)
            line()
            row("Référence",tx.reference)
            row("Date et heure",tx.date)
            row("Service / libellé",tx.displayName.ifBlank{tx.title})
            val other=tx.otherUserFullName?.takeIf{it.isNotBlank()} ?: tx.recipient
            row(if(incoming) "Expéditeur / source" else "Bénéficiaire / destination",other)
            tx.senderWalletId?.takeIf{it.isNotBlank()}?.let{row("Wallet émetteur",it)}
            tx.receiverWalletId?.takeIf{it.isNotBlank()}?.let{row("Wallet destinataire",it)}
            tx.description?.takeIf{it.isNotBlank()}?.let{row("Détail",it.take(2000))}
            line()
            row("Montant",money(tx.amount,tx.currency))
            if(!incoming) row("Frais",money(tx.fee,tx.currency))
            val confirmed = tx.status.lowercase(Locale.ROOT) in setOf("completed","success","successful")
            row(if(!confirmed) "Total indiqué (non confirmé)" else if(incoming) "Total crédité" else "Total débité",money(total(tx),tx.currency))
            line()
            qr?.let { canvas?.drawBitmap(it,((width-it.width)/2).toFloat(),y,null);y+=it.height+10 }
            text("Référence de transaction",17f,false,true)
            barcode?.let { y+=12;canvas?.drawBitmap(it,margin,y,null);y+=it.height+12 }
            text("À rapprocher de votre historique CashPay.",17f,false,true)
            line()
            text("CashPay All",24f,true,true)
            text("Bordereau généré électroniquement",18f,false,true)
            text("Conservez votre référence de transaction",17f,false,true)
            text("info@cashpay-all.com",19f,false,true)
            text("+243 85 386 80 21",19f,false,true)
            text("www.cashpay-all.com",19f,false,true)
            if(!confirmed) { y+=10;text("Ce document ne confirme pas un paiement.",17f,true,true) }
            return (y+28).toInt()
        }
        val height = layout(null)
        return Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888).apply {
            val canvas=Canvas(this);canvas.drawColor(Color.WHITE);layout(canvas)
        }
    }
}
