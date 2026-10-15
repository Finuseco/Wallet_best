package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.max

data class OptimizedImage(
    val uri: Uri,
    val bitmap: Bitmap?,
    val base64DataUri: String,
    val sizeKb: Int
)

object ImageOptimizer {

    suspend fun optimizeImageUri(
        context: Context,
        uri: Uri,
        maxDimension: Int = 1024,
        quality: Int = 80
    ): OptimizedImage? = withContext(Dispatchers.IO) {
        try {
            // 1. Decode bounds
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            } ?: return@withContext null

            val origWidth = options.outWidth
            val origHeight = options.outHeight
            if (origWidth <= 0 || origHeight <= 0) return@withContext null

            // 2. Calculate sample size
            var inSampleSize = 1
            val maxEdge = max(origWidth, origHeight)
            if (maxEdge > maxDimension) {
                inSampleSize = maxEdge / maxDimension
            }

            // 3. Decode scaled bitmap
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val originalBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            } ?: return@withContext null

            if (originalBitmap == null) return@withContext null

            // Resize if still slightly larger than maxDimension
            val scaleFactor = minOf(1.0f, maxDimension.toFloat() / max(originalBitmap.width, originalBitmap.height))
            val finalBitmap = if (scaleFactor < 1.0f) {
                val targetW = (originalBitmap.width * scaleFactor).toInt().coerceAtLeast(1)
                val targetH = (originalBitmap.height * scaleFactor).toInt().coerceAtLeast(1)
                Bitmap.createScaledBitmap(originalBitmap, targetW, targetH, true)
            } else {
                originalBitmap
            }

            // 4. Compress to JPEG
            val outputStream = ByteArrayOutputStream()
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val bytes = outputStream.toByteArray()
            val sizeKb = bytes.size / 1024

            val base64String = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val dataUri = "data:image/jpeg;base64,$base64String"

            OptimizedImage(
                uri = uri,
                bitmap = finalBitmap,
                base64DataUri = dataUri,
                sizeKb = sizeKb
            )
        } catch (_: Exception) {
            null
        }
    }
}
