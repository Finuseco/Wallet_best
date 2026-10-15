package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.MulishFontFamily
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

enum class CameraCaptureMode {
    SELFIE_PROFILE,
    ID_DOCUMENT_FRONT,
    ID_DOCUMENT_BACK
}

@Composable
fun CashPayCameraCaptureDialog(
    mode: CameraCaptureMode,
    onDismissRequest: () -> Unit,
    onImageCaptured: (base64Result: String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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

    var cameraSelector by remember {
        mutableStateOf(
            if (mode == CameraCaptureMode.SELFIE_PROFILE) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
        )
    }

    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var cameraControl: CameraControl? by remember { mutableStateOf(null) }

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var previewQualityMessage by remember { mutableStateOf("Document cadré & netteté optimale") }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (capturedBitmap == null) {
                // Live Camera View
                if (hasCameraPermission) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val executor = ContextCompat.getMainExecutor(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val capture = ImageCapture.Builder()
                                    .setFlashMode(flashMode)
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                    .build()

                                imageCapture = capture

                                try {
                                    cameraProvider.unbindAll()
                                    val camera = cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        capture
                                    )
                                    cameraControl = camera.cameraControl
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, executor)
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay Guides
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                    ) {
                        when (mode) {
                            CameraCaptureMode.SELFIE_PROFILE -> {
                                // Oval Face guide
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 40.dp, vertical = 80.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 240.dp, height = 300.dp)
                                            .clip(CircleShape)
                                            .border(3.dp, Color(0xFFFF6600), CircleShape)
                                            .background(Color.Transparent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Face,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.3f),
                                            modifier = Modifier.size(80.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Surface(
                                        color = Color(0xFF000E38).copy(alpha = 0.85f),
                                        shape = RoundedCornerShape(16.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6600))
                                    ) {
                                        Text(
                                            text = "Positionnez votre visage au centre du cercle",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                            CameraCaptureMode.ID_DOCUMENT_FRONT, CameraCaptureMode.ID_DOCUMENT_BACK -> {
                                // Rectangle Document Guide
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 24.dp, vertical = 80.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .border(3.dp, Color(0xFFFF6600), RoundedCornerShape(16.dp))
                                            .background(Color.Transparent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Corner brackets
                                        Text(
                                            text = if (mode == CameraCaptureMode.ID_DOCUMENT_FRONT) "RECTO DE LA PIÈCE D'IDENTITÉ" else "VERSO DE LA PIÈCE D'IDENTITÉ",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.7f),
                                            letterSpacing = 1.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Surface(
                                        color = Color(0xFF000E38).copy(alpha = 0.85f),
                                        shape = RoundedCornerShape(16.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6600))
                                    ) {
                                        Text(
                                            text = "Document détecté • Maintenez l'appareil stable",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Top Bar Controls: Close, Flash, Switch Camera
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Flash Toggle
                            IconButton(
                                onClick = {
                                    flashMode = when (flashMode) {
                                        ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                                        ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                                        else -> ImageCapture.FLASH_MODE_AUTO
                                    }
                                    imageCapture?.flashMode = flashMode
                                },
                                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = when (flashMode) {
                                        ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                                        ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                                        else -> Icons.Default.FlashAuto
                                    },
                                    contentDescription = "Flash",
                                    tint = if (flashMode == ImageCapture.FLASH_MODE_ON) Color(0xFFFF6600) else Color.White
                                )
                            }

                            // Switch Camera (Front/Back)
                            IconButton(
                                onClick = {
                                    cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }
                                },
                                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(Icons.Default.Cameraswitch, contentDescription = "Changer Caméra", tint = Color.White)
                            }
                        }
                    }

                    // Bottom Shutter Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCapturing) {
                            CircularProgressIndicator(color = Color(0xFFFF6600))
                        } else {
                            IconButton(
                                onClick = {
                                    val capture = imageCapture ?: return@IconButton
                                    isCapturing = true
                                    val executor = Executors.newSingleThreadExecutor()
                                    capture.takePicture(
                                        executor,
                                        object : ImageCapture.OnImageCapturedCallback() {
                                            override fun onCaptureSuccess(image: ImageProxy) {
                                                val buffer = image.planes[0].buffer
                                                val bytes = ByteArray(buffer.remaining())
                                                buffer.get(bytes)
                                                var rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                                                // Rotate if needed
                                                val rotationDegrees = image.imageInfo.rotationDegrees
                                                if (rotationDegrees != 0 && rawBitmap != null) {
                                                    val matrix = Matrix()
                                                    matrix.postRotate(rotationDegrees.toFloat())
                                                    rawBitmap = Bitmap.createBitmap(
                                                        rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true
                                                    )
                                                }
                                                image.close()

                                                capturedBitmap = rawBitmap
                                                isCapturing = false
                                            }

                                            override fun onError(exception: ImageCaptureException) {
                                                isCapturing = false
                                                exception.printStackTrace()
                                            }
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .size(76.dp)
                                    .background(Color.White, CircleShape)
                                    .border(4.dp, Color(0xFFFF6600), CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(Color(0xFFFF6600), CircleShape)
                                )
                            }
                        }
                    }
                } else {
                    // No permission fallback
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color(0xFFFF6600),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Autorisation Caméra requise",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "CashPay a besoin de la caméra pour capturer votre photo de profil KYC et votre pièce d'identité.",
                            fontFamily = MulishFontFamily,
                            fontSize = 13.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600))
                        ) {
                            Text("Autoriser la caméra", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Post-Capture Mandatory Preview & Verification Screen
                val bitmap = capturedBitmap!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF000E38))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Contrôle de la photo capturée",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Vérifiez la clarté et la lisibilité avant de confirmer",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Image Card Preview
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = androidx.compose.material3.CardDefaults.cardElevation(8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Photo capturée",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )

                            // Quality check badge
                            Surface(
                                color = Color(0xFF000E38).copy(alpha = 0.85f),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00C853)),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00C853), modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Qualité & Cadrage Validés",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF00C853)
                                    )
                                }
                            }
                        }
                    }

                    // Action buttons: Retake or Confirm
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { capturedBitmap = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reprendre", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                // Optimize, resize, compress to JPEG and Base64 format: data:image/jpeg;base64,...
                                val maxDimension = if (mode == CameraCaptureMode.SELFIE_PROFILE) 600 else 1024
                                val scaledBitmap = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                                    val scale = maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
                                    Bitmap.createScaledBitmap(
                                        bitmap,
                                        (bitmap.width * scale).toInt(),
                                        (bitmap.height * scale).toInt(),
                                        true
                                    )
                                } else {
                                    bitmap
                                }

                                val stream = ByteArrayOutputStream()
                                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                                val base64String = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                                val finalDataUrl = "data:image/jpeg;base64,$base64String"
                                onImageCaptured(finalDataUrl)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Utiliser", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
