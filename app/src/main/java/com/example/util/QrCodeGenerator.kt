package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap
import java.util.concurrent.TimeUnit

object QrCodeGenerator {

    /**
     * Generates a 100% standard, scannable ISO QR Code using ZXing.
     */
    fun generateQrBitmap(content: String, size: Int = 300): Bitmap {
        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.MARGIN, 1)
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
            }
            val bitMatrix = QRCodeWriter().encode(
                content.ifBlank { "CASHPAY-PAYMENT" },
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)
            val darkColor = Color.parseColor("#0B1B3D") // Midnight dark blue branding
            val whiteColor = Color.WHITE

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) darkColor else whiteColor
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: Exception) {
            // Fallback emergency bitmap
            val fallback = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            fallback.eraseColor(Color.WHITE)
            fallback
        }
    }

    /**
     * Decodes any QR code or Barcode from an imported Bitmap using:
     * 1. Google ML Kit Barcode Scanning (handles rotation, tilt, blurry screenshots, crops, all 1D/2D formats)
     * 2. Software Bitmap conversion to prevent Hardware Bitmap crashes on Android 9+
     * 3. Multi-angle ML Kit checks (0°, 90°, 270°)
     * 4. Multi-pass ZXing fallback (HybridBinarizer, GlobalHistogramBinarizer, Inverted, Multi-scale, Center/Upper Crops)
     */
    fun decodeQrBitmap(bitmap: Bitmap): String? {
        // Ensure bitmap is in software memory (ARGB_8888) to prevent crashes on hardware bitmaps
        val safeBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
            bitmap.copy(Bitmap.Config.ARGB_8888, false) ?: bitmap
        } else {
            bitmap
        }

        // --- Pass 1: Google ML Kit (Deep Learning Vision Scanner) ---
        decodeWithMlKit(safeBitmap)?.let { return it }

        // --- Pass 2: Multi-Pass ZXing Fallback Engine ---
        val formats = listOf(
            BarcodeFormat.QR_CODE,
            BarcodeFormat.EAN_13,
            BarcodeFormat.EAN_8,
            BarcodeFormat.CODE_128,
            BarcodeFormat.CODE_39,
            BarcodeFormat.UPC_A,
            BarcodeFormat.UPC_E,
            BarcodeFormat.DATA_MATRIX,
            BarcodeFormat.ITF,
            BarcodeFormat.AZTEC
        )

        val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
            put(DecodeHintType.POSSIBLE_FORMATS, formats)
            put(DecodeHintType.TRY_HARDER, true)
            put(DecodeHintType.CHARACTER_SET, "UTF-8")
        }

        // Try direct ZXing
        decodeSingleBitmap(safeBitmap, hints)?.let { return it }

        // Try normalized scaled versions (avoid OOM and resize huge screenshots where small features get blurred)
        val maxDim = maxOf(safeBitmap.width, safeBitmap.height)
        if (maxDim > 1200) {
            val scale = 1200f / maxDim
            val scaledWidth = (safeBitmap.width * scale).toInt()
            val scaledHeight = (safeBitmap.height * scale).toInt()
            val scaled = Bitmap.createScaledBitmap(safeBitmap, scaledWidth, scaledHeight, true)
            decodeSingleBitmap(scaled, hints)?.let { return it }
        }

        if (maxDim > 800) {
            val scale = 800f / maxDim
            val scaledWidth = (safeBitmap.width * scale).toInt()
            val scaledHeight = (safeBitmap.height * scale).toInt()
            val scaled = Bitmap.createScaledBitmap(safeBitmap, scaledWidth, scaledHeight, true)
            decodeSingleBitmap(scaled, hints)?.let { return it }
        }

        // Try Center-Crop (center 70% of screenshot)
        val centerBitmap = try {
            val cropW = (safeBitmap.width * 0.70).toInt()
            val cropH = (safeBitmap.height * 0.70).toInt()
            val startX = (safeBitmap.width - cropW) / 2
            val startY = (safeBitmap.height - cropH) / 2
            Bitmap.createBitmap(safeBitmap, startX, startY, cropW, cropH)
        } catch (_: Exception) { null }

        if (centerBitmap != null) {
            decodeSingleBitmap(centerBitmap, hints)?.let { return it }
        }

        // Try Upper-Half Crop (where QR codes often appear in payment receipts)
        val upperBitmap = try {
            val cropW = (safeBitmap.width * 0.85).toInt()
            val cropH = (safeBitmap.height * 0.55).toInt()
            val startX = (safeBitmap.width - cropW) / 2
            val startY = (safeBitmap.height * 0.10).toInt()
            Bitmap.createBitmap(safeBitmap, startX, startY, cropW, cropH)
        } catch (_: Exception) { null }

        if (upperBitmap != null) {
            decodeSingleBitmap(upperBitmap, hints)?.let { return it }
        }

        // Try 90 degree rotation
        val rotated = try {
            val matrix = Matrix().apply { postRotate(90f) }
            Bitmap.createBitmap(safeBitmap, 0, 0, safeBitmap.width, safeBitmap.height, matrix, true)
        } catch (_: Exception) { null }

        if (rotated != null) {
            decodeSingleBitmap(rotated, hints)?.let { return it }
        }

        return null
    }

    private fun decodeWithMlKit(bitmap: Bitmap): String? {
        return try {
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()
            val scanner = BarcodeScanning.getClient(options)

            // Try orientation 0°
            val image0 = InputImage.fromBitmap(bitmap, 0)
            val task0 = scanner.process(image0)
            val list0 = Tasks.await(task0, 2000, TimeUnit.MILLISECONDS)
            val code0 = list0.firstOrNull()?.rawValue?.ifBlank { null }
                ?: list0.firstOrNull()?.displayValue?.ifBlank { null }
            if (!code0.isNullOrBlank()) {
                scanner.close()
                return code0
            }

            // Try orientation 90°
            val image90 = InputImage.fromBitmap(bitmap, 90)
            val task90 = scanner.process(image90)
            val list90 = Tasks.await(task90, 1000, TimeUnit.MILLISECONDS)
            val code90 = list90.firstOrNull()?.rawValue?.ifBlank { null }
                ?: list90.firstOrNull()?.displayValue?.ifBlank { null }
            if (!code90.isNullOrBlank()) {
                scanner.close()
                return code90
            }

            // Try orientation 270°
            val image270 = InputImage.fromBitmap(bitmap, 270)
            val task270 = scanner.process(image270)
            val list270 = Tasks.await(task270, 1000, TimeUnit.MILLISECONDS)
            val code270 = list270.firstOrNull()?.rawValue?.ifBlank { null }
                ?: list270.firstOrNull()?.displayValue?.ifBlank { null }
            scanner.close()
            code270
        } catch (e: Exception) {
            null
        }
    }

    private fun decodeSingleBitmap(bmp: Bitmap, hints: Map<DecodeHintType, Any>): String? {
        return try {
            val width = bmp.width
            val height = bmp.height
            val pixels = IntArray(width * height)
            bmp.getPixels(pixels, 0, width, 0, 0, width, height)
            val source = RGBLuminanceSource(width, height, pixels)

            val reader = MultiFormatReader()

            // Pass A: HybridBinarizer
            try {
                val binBitmap = BinaryBitmap(HybridBinarizer(source))
                val result = reader.decode(binBitmap, hints)
                if (!result.text.isNullOrBlank()) return result.text
            } catch (_: Exception) {} finally { reader.reset() }

            // Pass B: GlobalHistogramBinarizer (better for flat / low-contrast screenshots)
            try {
                val binBitmap = BinaryBitmap(GlobalHistogramBinarizer(source))
                val result = reader.decode(binBitmap, hints)
                if (!result.text.isNullOrBlank()) return result.text
            } catch (_: Exception) {} finally { reader.reset() }

            // Pass C: Inverted source (for dark mode screens with white-on-dark codes)
            try {
                val invertedSource = source.invert()
                val binBitmap = BinaryBitmap(HybridBinarizer(invertedSource))
                val result = reader.decode(binBitmap, hints)
                if (!result.text.isNullOrBlank()) return result.text
            } catch (_: Exception) {} finally { reader.reset() }

            try {
                val invertedSource = source.invert()
                val binBitmap = BinaryBitmap(GlobalHistogramBinarizer(invertedSource))
                val result = reader.decode(binBitmap, hints)
                if (!result.text.isNullOrBlank()) return result.text
            } catch (_: Exception) {} finally { reader.reset() }

            null
        } catch (_: Exception) {
            null
        }
    }
}
