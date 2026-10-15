package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.local.TransactionEntity
import java.io.File
import java.io.FileOutputStream

object PdfReceiptGenerator {

    fun generateAndShareReceiptPdf(context: Context, transaction: TransactionEntity): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size (595x842 pt)
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintHeaderBg = Paint().apply { color = Color.parseColor("#0B1E36") }
        val paintTextDark = Paint().apply {
            color = Color.parseColor("#1E293B")
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val paintTextValue = Paint().apply {
            color = Color.parseColor("#334155")
            textSize = 13f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        val paintTextMuted = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 11f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        val paintLine = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            strokeWidth = 1.5f
        }

        // Top Header Banner
        canvas.drawRect(0f, 0f, 595f, 130f, paintHeaderBg)

        // Logo Emblem Badge
        val paintLogoBg = Paint().apply { color = Color.parseColor("#10B981") }
        canvas.drawRoundRect(40f, 25f, 95f, 80f, 12f, 12f, paintLogoBg)
        val paintLogoText = Paint().apply {
            color = Color.WHITE
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("CP", 53f, 62f, paintLogoText)

        val paintTitle = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("CashPay", 110f, 55f, paintTitle)

        val paintSubtitle = Paint().apply {
            color = Color.parseColor("#34D399")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("REÇU OFFICIEL DE TRANSACTION • SECURE POS RECEIPT", 110f, 78f, paintSubtitle)

        // Draw QR Code in top right header
        val qrBitmap = QrCodeGenerator.generateQrBitmap(transaction.reference, 85)
        canvas.drawBitmap(qrBitmap, 465f, 22f, null)

        var y = 160f

        val isCredit = transaction.direction.lowercase() == "incoming"
        val directionLabel = if (isCredit) "CRÉDIT (+)" else "DÉBIT (-)"
        val typeLabel = if (transaction.type == "TR") {
            if (isCredit) "Transfert Reçu" else "Transfert Envoyé"
        } else {
            if (isCredit) "Dépôt Reçu" else "Retrait / Paiement Service"
        }

        canvas.drawText("Détails du Transfert / Facture", 40f, y, paintTextDark)
        y += 18f
        canvas.drawLine(40f, y, 555f, y, paintLine)
        y += 25f

        fun drawRow(label: String, value: String, isBold: Boolean = false, customColor: Int? = null) {
            canvas.drawText(label, 40f, y, paintTextMuted)
            val p = Paint(paintTextValue).apply {
                if (isBold) typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                if (customColor != null) color = customColor
            }
            canvas.drawText(value, 260f, y, p)
            y += 24f
        }

        val senderName = if (isCredit) (transaction.otherUserFullName ?: transaction.recipient) else "Moi (Utilisateur CashPay)"
        val receiverName = if (isCredit) "Moi (Utilisateur CashPay)" else (transaction.otherUserFullName ?: transaction.recipient)

        drawRow("Référence :", transaction.reference, isBold = true)
        drawRow("Statut :", transaction.status.uppercase(), isBold = true, customColor = Color.parseColor("#10B981"))
        drawRow("Type d'Opération :", typeLabel)
        drawRow("Sens Financement :", directionLabel, customColor = if (isCredit) Color.parseColor("#10B981") else Color.parseColor("#EF4444"))
        drawRow("Intitulé / Service :", transaction.displayName.ifBlank { transaction.title })
        drawRow("Nom Expéditeur :", senderName, isBold = true)
        drawRow("Nom Destinataire :", receiverName, isBold = true)
        drawRow("Date & Heure :", transaction.date)

        y += 10f
        canvas.drawLine(40f, y, 555f, y, paintLine)
        y += 25f

        val paintBoxBg = Paint().apply { color = Color.parseColor("#F8FAFC") }
        canvas.drawRect(40f, y, 555f, y + 115f, paintBoxBg)

        var boxY = y + 30f
        val amountStr = "${if (isCredit) "+" else "-"}${String.format("%.2f", transaction.amount)} ${transaction.currency}"
        val feeStr = "${String.format("%.2f", transaction.fee)} ${transaction.currency}"
        val totalStr = "${String.format("%.2f", transaction.amount + transaction.fee)} ${transaction.currency}"

        canvas.drawText("Montant principal :", 60f, boxY, paintTextMuted)
        canvas.drawText(amountStr, 380f, boxY, Paint(paintTextDark).apply { textSize = 15f; color = if (isCredit) Color.parseColor("#10B981") else Color.parseColor("#0B1E36") })
        boxY += 26f

        canvas.drawText("Frais de traitement :", 60f, boxY, paintTextMuted)
        canvas.drawText(feeStr, 380f, boxY, paintTextValue)
        boxY += 26f

        canvas.drawText("Total Opération :", 60f, boxY, Paint(paintTextDark).apply { textSize = 15f })
        canvas.drawText(totalStr, 380f, boxY, Paint(paintTextDark).apply { textSize = 16f; color = Color.parseColor("#10B981") })

        // Footer Blue Band "Bleu de Nuit"
        val paintFooterBg = Paint().apply { color = Color.parseColor("#0B1E36") }
        canvas.drawRect(0f, 770f, 595f, 842f, paintFooterBg)

        val paintFooterText = Paint().apply {
            color = Color.WHITE
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("CashPay • Reçu Officiel de Paiement Sécurisé", 40f, 802f, paintFooterText)

        val paintFooterSub = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 10f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        canvas.drawText("Support: support@cashpay-all.com • www.cashpay-all.com", 40f, 822f, paintFooterSub)

        pdfDocument.finishPage(page)

        return try {
            val file = File(context.cacheDir, "recu_${transaction.reference}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Partager le Reçu PDF CashPay"))
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
