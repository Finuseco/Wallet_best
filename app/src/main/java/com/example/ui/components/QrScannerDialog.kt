package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite
import com.example.util.QrCodeGenerator
import java.util.concurrent.Executors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun QrScannerDialog(
    onDismissRequest: () -> Unit,
    onQrScanned: (result: String) -> Unit,
    scanMode: String = "TRANSFER", // "TRANSFER", "PRODUCT", "PAYMENT_REQUEST"
    prefillTarget: String? = null
) {
    val title = when (scanMode) {
        "PRODUCT" -> "Scanner un produit"
        "PAYMENT_REQUEST" -> "Scanner une demande de paiement"
        else -> "Scanner un QR Code CashPay / Crypto"
    }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val clipboardManager = LocalClipboardManager.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    var scannedResult by remember { mutableStateOf<String?>(null) }
    var manualInput by remember { mutableStateOf(prefillTarget ?: "") }
    var isFlashlightOn by remember { mutableStateOf(false) }
    var isAnalyzingImage by remember { mutableStateOf(false) }
    var imageAnalysisError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isAnalyzingImage = true
            imageAnalysisError = null
            coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, boundsOptions)
                    }

                    var sampleSize = 1
                    val maxDim = maxOf(boundsOptions.outWidth, boundsOptions.outHeight)
                    if (maxDim > 2400) {
                        sampleSize = 2
                    }

                    val decodeOptions = BitmapFactory.Options().apply {
                        inSampleSize = sampleSize
                        inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888
                    }
                    val bitmap = context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, decodeOptions)
                    }

                    if (bitmap != null) {
                        val decoded = QrCodeGenerator.decodeQrBitmap(bitmap)
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            isAnalyzingImage = false
                            if (!decoded.isNullOrBlank()) {
                                scannedResult = decoded
                                onQrScanned(decoded)
                                onDismissRequest()
                            } else {
                                imageAnalysisError = "Aucun code détecté sur cette capture. Assurez-vous que le QR code ou code-barres est net et visible."
                            }
                        }
                    } else {
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            isAnalyzingImage = false
                            imageAnalysisError = "Impossible de charger l'image sélectionnée."
                        }
                    }
                } catch (e: Exception) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        isAnalyzingImage = false
                        imageAnalysisError = "Erreur de lecture : ${e.localizedMessage ?: "Image non supportée"}"
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f)),
            color = Color.Black.copy(alpha = 0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = title,
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )

                    IconButton(onClick = {
                        photoPickerLauncher.launch("image/*")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Galerie",
                            tint = ToofanGreen
                        )
                    }
                }

                // Image Analysis Feedback Banner
                if (isAnalyzingImage) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = ToofanGreen
                            )
                            Text(
                                text = "Détection automatique du QR code / code-barres dans la capture...",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                } else if (imageAnalysisError != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF7F1D1D)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Text(
                                text = imageAnalysisError ?: "",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontFamily = MulishFontFamily,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scan viewfinder
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(2.dp, ToofanGreen, RoundedCornerShape(24.dp))
                        .background(Color.DarkGray.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasCameraPermission) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx)
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }
                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                    val imageAnalysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()

                                    // Real camera analyzer with complete 1D & 2D formats (QR, EAN-13, EAN-8, Code 128, Code 39, UPC-A, UPC-E, Data Matrix, ITF)
                                    val reader = com.google.zxing.MultiFormatReader().apply {
                                        setHints(
                                            mapOf(
                                                com.google.zxing.DecodeHintType.POSSIBLE_FORMATS to listOf(
                                                    com.google.zxing.BarcodeFormat.QR_CODE,
                                                    com.google.zxing.BarcodeFormat.EAN_13,
                                                    com.google.zxing.BarcodeFormat.EAN_8,
                                                    com.google.zxing.BarcodeFormat.CODE_128,
                                                    com.google.zxing.BarcodeFormat.CODE_39,
                                                    com.google.zxing.BarcodeFormat.UPC_A,
                                                    com.google.zxing.BarcodeFormat.UPC_E,
                                                    com.google.zxing.BarcodeFormat.DATA_MATRIX,
                                                    com.google.zxing.BarcodeFormat.ITF
                                                ),
                                                com.google.zxing.DecodeHintType.TRY_HARDER to true
                                            )
                                        )
                                    }

                                    imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                                        try {
                                            if (scannedResult == null) {
                                                val plane = imageProxy.planes[0]
                                                val buffer = plane.buffer
                                                val data = ByteArray(buffer.remaining())
                                                buffer.get(data)
                                                val rowStride = plane.rowStride
                                                val width = imageProxy.width
                                                val height = imageProxy.height

                                                val source = com.google.zxing.PlanarYUVLuminanceSource(
                                                    data, rowStride, height, 0, 0, width, height, false
                                                )
                                                val binaryBitmap = com.google.zxing.BinaryBitmap(
                                                    com.google.zxing.common.HybridBinarizer(source)
                                                )
                                                val result = try {
                                                    reader.decodeWithState(binaryBitmap)
                                                } catch (_: Exception) {
                                                    null
                                                } finally {
                                                    reader.reset()
                                                }

                                                if (result != null && scannedResult == null) {
                                                    val text = result.text
                                                    scannedResult = text
                                                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                                                        onQrScanned(text)
                                                        onDismissRequest()
                                                    }
                                                }
                                            }
                                        } catch (_: Exception) {
                                        } finally {
                                            imageProxy.close()
                                        }
                                    }

                                    try {
                                        cameraProvider.unbindAll()
                                        val camera = cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            imageAnalysis
                                        )
                                        camera.cameraControl.enableTorch(isFlashlightOn)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, ContextCompat.getMainExecutor(ctx))
                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Autorisation Caméra requise pour le scan en direct.",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)
                            ) {
                                Text("Activer la Caméra")
                            }
                        }
                    }

                    // Viewfinder reticle overlay
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .border(1.5.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Scanned outcome or manual input
                if (scannedResult != null) {
                    val resultText = scannedResult ?: ""
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Code QR Détecté !",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = ToofanGreen,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = resultText,
                                fontFamily = MulishFontFamily,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(resultText))
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copier", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = {
                                        onQrScanned(resultText)
                                        onDismissRequest()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Utiliser", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Manual ID / Phone / Wallet fallback
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF131D31),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ou saisir directement le Wallet / N° / Adresse :",
                                color = Color.LightGray,
                                fontSize = 13.sp,
                                fontFamily = MulishFontFamily
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = manualInput,
                                onValueChange = { manualInput = it },
                                placeholder = { Text("Ex: CP-800001234, +243..., bc1...", color = Color.Gray, fontSize = 13.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (manualInput.isNotBlank()) {
                                        onQrScanned(manualInput.trim())
                                        onDismissRequest()
                                    }
                                },
                                enabled = manualInput.isNotBlank(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)
                            ) {
                                Text("Valider l'identifiant", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
