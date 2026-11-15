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
        val qrSize = if (paperMm == 58) 136 else 176
        val qr = code(BarcodeFormat.QR_CODE, qrSize, qrSize)
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
            fun block(value: String, x: Float, top: Float, maxWidth: Float,
                      size: Float, bold: Boolean = false, right: Boolean = false): Float {
                paint.textSize = size
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, if (bold) Typeface.BOLD else Typeface.NORMAL)
                var bottom = top
                value.replace('\r', ' ').split('\n').forEach { paragraph ->
                    var rest = paragraph
                    do {
                        val fits = paint.breakText(rest, true, maxWidth, null).coerceAtLeast(1).coerceAtMost(rest.length)
                        val boundary = if (fits < rest.length) rest.lastIndexOf(' ', fits) else -1
                        val count = if (boundary > 0) boundary else fits
                        val part = rest.take(count)
                        canvas?.drawText(part, if (right) x + maxWidth - paint.measureText(part) else x,
                            bottom + size, paint)
                        bottom += size * 1.35f
                        rest = rest.drop(count).trimStart()
                    } while (rest.isNotEmpty())
                }
                return bottom
            }
            fun row(label: String, value: String, bold: Boolean = false) {
                val gap = 14f
                val labelWidth = usable * .39f
                val valueX = margin + labelWidth + gap
                val labelBottom = block(label, margin, y, labelWidth, 17f)
                val valueBottom = block(value.ifBlank { "—" }, valueX, y,
                    width - margin - valueX, 19f, bold, right = true)
                y = maxOf(labelBottom, valueBottom) + 12f
            }
            val logoWidth = minOf(usable, 250f)
            val logoHeight = logoWidth * logo.height / logo.width
            canvas?.drawBitmap(logo,null,RectF((width-logoWidth)/2,y,(width+logoWidth)/2,y+logoHeight),Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
            y+=logoHeight+18
            text("BORDEREAU CASHPAY",19f,true,true)
            y += 8
            barcode?.let { canvas?.drawBitmap(it, margin, y, null); y += it.height + 10 }
            text(tx.reference, 17f, true, true)
            line()
            row("Opération", TransactionTypeMapper.getTypeInfo(tx.type).nameFr, true)
            row("Statut", status(tx.status), true)
            line()
            val incoming = tx.direction.equals("incoming",true)
            text(money(tx.amount,tx.currency),if(paperMm==58) 26f else 34f,true,true)
            text(if(incoming) "ENTRÉE DE FONDS" else "SORTIE DE FONDS",18f,false,true)
            line()
            val date = runCatching {
                java.time.Instant.parse(tx.date).atZone(java.time.ZoneId.systemDefault())
                    .format(java.time.format.DateTimeFormatter.ofPattern("EEE dd/MM/yyyy HH:mm", Locale.FRENCH))
            }.getOrDefault(tx.date)
            row("Date et heure", date)
            row("Service / libellé",tx.displayName.ifBlank{tx.title})
            val other=tx.otherUserFullName?.takeIf{it.isNotBlank()} ?: tx.recipient
            row(if(incoming) "Expéditeur / source" else "Bénéficiaire / destination",other)
            tx.senderWalletId?.takeIf{it.isNotBlank()}?.let{row("Wallet émetteur",it)}
            tx.receiverWalletId?.takeIf{it.isNotBlank()}?.let{row("Wallet destinataire",it)}
            tx.description?.takeIf{it.isNotBlank()}?.let{row("Détail",it.take(2000))}
            line()
            row("Montant",money(tx.amount,tx.currency),true)
            if(!incoming) row("Frais",money(tx.fee,tx.currency))
            val confirmed = tx.status.lowercase(Locale.ROOT) in setOf("completed","success","successful")
            row(if(!confirmed) "Total indiqué (non confirmé)" else if(incoming) "Total crédité" else "Total débité",money(total(tx),tx.currency),true)
            line()
            // One footer container: QR on the left, support details on the right.
            val footerTop = y
            qr?.let { canvas?.drawBitmap(it, margin, footerTop, null) }
            val footerX = if (qr != null) margin + qrSize + 16f else margin
            val footerWidth = width - margin - footerX
            var footerY = block("CashPay All", footerX, footerTop, footerWidth, 19f, true) + 5f
            for (value in listOf("Bordereau électronique", "Conservez votre référence", "info@cashpay-all.com",
                "+243 85 386 80 21", "www.cashpay-all.com")) {
                footerY = block(value, footerX, footerY, footerWidth, 15f) + 4f
            }
            y = maxOf(footerY, footerTop + (qr?.height ?: 0)) + 10f
            if(!confirmed) { line(); text("Ce document ne confirme pas un paiement.",17f,true) }
            return (y+28).toInt()
        }
        val height = layout(null)
        return Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888).apply {
            val canvas=Canvas(this);canvas.drawColor(Color.WHITE);layout(canvas)
        }
    }
}
