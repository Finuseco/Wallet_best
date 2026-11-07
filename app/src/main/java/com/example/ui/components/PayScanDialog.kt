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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanWhite
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.PayScanMode
import com.example.util.QrCodeGenerator
import java.util.concurrent.Executors

@Composable
fun PayScanDialog(
    dashboardViewModel: DashboardViewModel,
    uiState: DashboardUiState,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val payscanState = uiState.payscan
    val basketItems = payscanState.basketItems
    val currentMode = payscanState.currentMode
    val paymentCurrency = payscanState.paymentCurrency

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

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var lastScannedCode by remember { mutableStateOf<String?>(null) }
    var lastScannedTime by remember { mutableLongStateOf(0L) }
    var showManualInputDialog by remember { mutableStateOf(false) }
    var manualBarcodeValue by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            dashboardViewModel.updatePayScan {
                copy(
                    isInterpreting = true,
                    scanError = null,
                    scanSuccessMessage = "Analyse de la capture d'écran en cours..."
                )
            }
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    // Check bounds to determine if sample size scaling is needed
                    val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, boundsOptions)
                    }

                    var sampleSize = 1
                    val maxDim = maxOf(boundsOptions.outWidth, boundsOptions.outHeight)
                    while (maxDim / sampleSize > 2048) sampleSize *= 2

                    val decodeOptions = BitmapFactory.Options().apply {
                        inSampleSize = sampleSize
                        inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888
                    }
                    val bitmap = context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, decodeOptions)
                    }

                    if (bitmap != null) {
                        val decoded = QrCodeGenerator.decodeQrBitmap(bitmap)
                        withContext(Dispatchers.Main) {
                            if (!decoded.isNullOrBlank()) {
                                dashboardViewModel.updatePayScan {
                                    copy(
                                        isInterpreting = false,
                                        scanSuccessMessage = "Code détecté sur l'image !"
                                    )
                                }
                                dashboardViewModel.processPayScanInput(decoded)
                            } else {
                                dashboardViewModel.updatePayScan {
                                    copy(
                                        isInterpreting = false,
                                        scanError = "Aucun QR code ou code-barres exploitable détecté sur cette capture. Assurez-vous que le code est net et non coupé."
                                    )
                                }
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            dashboardViewModel.updatePayScan {
                                copy(
                                    isInterpreting = false,
                                    scanError = "Impossible de charger la photo sélectionnée."
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        dashboardViewModel.updatePayScan {
                            copy(
                                isInterpreting = false,
                                scanError = "Erreur d'analyse de l'image : ${e.localizedMessage ?: "Format non supporté"}"
                            )
                        }
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0F1D))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                // Top Action & Title Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF1E293B), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer PayScan",
                            tint = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CashPay PayScan",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Scanner de paiement & supermarché",
                            fontFamily = MulishFontFamily,
                            fontSize = 11.sp,
                            color = ToofanGreen
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF1E293B), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Galerie",
                                tint = ToofanGreen
                            )
                        }

                        IconButton(
                            onClick = { showManualInputDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF1E293B), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Saisie manuelle",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Mode Selector Bar (Panier / Payer POS / Envoyer / Auto)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .background(Color(0xFF162032), RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PayScanModeChip(
                        title = "Panier",
                        icon = Icons.Default.ShoppingCart,
                        isSelected = currentMode == PayScanMode.SUPERMARKET_BASKET,
                        badgeCount = basketItems.sumOf { it.quantity },
                        modifier = Modifier.weight(1f),
                        onClick = { dashboardViewModel.setPayScanMode(PayScanMode.SUPERMARKET_BASKET) }
                    )

                    PayScanModeChip(
                        title = "Payer POS",
                        icon = Icons.Default.Payment,
                        isSelected = currentMode == PayScanMode.PAYMENT_REQUEST,
                        modifier = Modifier.weight(1f),
                        onClick = { dashboardViewModel.setPayScanMode(PayScanMode.PAYMENT_REQUEST) }
                    )

                    PayScanModeChip(
                        title = "Envoyer",
                        icon = Icons.AutoMirrored.Filled.Send,
                        isSelected = currentMode == PayScanMode.WALLET_TRANSFER,
                        modifier = Modifier.weight(1f),
                        onClick = { dashboardViewModel.setPayScanMode(PayScanMode.WALLET_TRANSFER) }
                    )

                    PayScanModeChip(
                        title = "Auto",
                        icon = Icons.Default.QrCodeScanner,
                        isSelected = currentMode == PayScanMode.AUTO,
                        modifier = Modifier.weight(0.8f),
                        onClick = { dashboardViewModel.setPayScanMode(PayScanMode.AUTO) }
                    )
                }

                // Camera Scanner Viewport
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black),
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

                                    // Support complete formats: QR, EAN-13, EAN-8, Code 128, Code 39, UPC-A, UPC-E, Data Matrix, ITF
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
                                            val now = System.currentTimeMillis()
                                            // 1.8 second debounce to avoid accidental multiple scans of the same item
                                            if (now - lastScannedTime > 1800L && !payscanState.isInterpreting && !payscanState.isProcessingPayment) {
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

                                                if (result != null && !result.text.isNullOrBlank()) {
                                                    val rawText = result.text.trim()
                                                    if (rawText != lastScannedCode || now - lastScannedTime > 3000L) {
                                                        lastScannedCode = rawText
                                                        lastScannedTime = now
                                                        dashboardViewModel.processPayScanInput(rawText)
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        } finally {
                                            imageProxy.close()
                                        }
                                    }

                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            imageAnalysis
                                        )
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
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Autorisation Caméra Requise",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "PayScan utilise la caméra pour scanner les QR codes, factures et codes-barres de produits.",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)
                            ) {
                                Text("Activer la Caméra", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Laser Reticle & Guides
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .border(2.dp, ToofanGreen, RoundedCornerShape(20.dp))
                    )

                    // Interpreting or Loading Overlay
                    if (payscanState.isInterpreting) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.8f),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = ToofanGreen
                                )
                                Text(
                                    text = "Analyse PayScan en cours...",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }
                    }
                }

                // Error or Success Banner
                AnimatedVisibility(visible = payscanState.scanError != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                            Text(
                                text = payscanState.scanError ?: "",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = payscanState.scanSuccessMessage != null && payscanState.scanError == null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = ToofanGreen.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ToofanGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(18.dp))
                            Text(
                                text = payscanState.scanSuccessMessage ?: "",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Supermarket Basket Live Summary Card
                val basketTotalInSelected = dashboardViewModel.calculateBasketTotalInCurrency(paymentCurrency)
                val totalCdf = dashboardViewModel.calculateBasketTotalInCurrency("CDF")
                val totalUsd = dashboardViewModel.calculateBasketTotalInCurrency("USD")

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26354D))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Panier : ${basketItems.sumOf { it.quantity }} article(s)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }

                            // Currency Chips Selector (USD, CDF, EUR)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("USD", "CDF", "EUR").forEach { curr ->
                                    val isCurrSelected = paymentCurrency.equals(curr, ignoreCase = true)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isCurrSelected) ToofanGreen else Color(0xFF1E293B))
                                            .clickable { dashboardViewModel.setPayScanCurrency(curr) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = curr,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrSelected) Color.Black else Color.LightGray
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Real-time total calculation and conversion
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Total à payer en $paymentCurrency :",
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                                Text(
                                    text = if(basketTotalInSelected.isFinite()) String.format(java.util.Locale.US, "%.2f %s", basketTotalInSelected, paymentCurrency) else "Devis serveur requis",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ToofanGreen
                                )
                            }

                            // Secondary exchange rate conversion preview
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Contre-valeur :",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = if (!totalCdf.isFinite() || !totalUsd.isFinite()) "Taux indisponible" else if (paymentCurrency == "USD") "≈ ${String.format(java.util.Locale.US, "%,.0f CDF", totalCdf)}"
                                           else "≈ ${String.format(java.util.Locale.US, "%.2f USD", totalUsd)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bottom Actions (Voir Panier / Valider le Paiement POS)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { dashboardViewModel.toggleBasketDrawer(true) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Détails (${basketItems.size})", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    dashboardViewModel.openConfirmPosPayment()
                                },
                                enabled = basketItems.isNotEmpty() && !payscanState.isProcessingPayment,
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Payer Panier",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Drawer for Basket Items management (Add/Remove/Quantities)
    if (payscanState.showBasketDrawer) {
        Dialog(
            onDismissRequest = { dashboardViewModel.toggleBasketDrawer(false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A)),
                color = Color(0xFF0F172A)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .systemBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { dashboardViewModel.toggleBasketDrawer(false) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                        }

                        Text(
                            text = "Mon Panier PayScan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )

                        TextButton(onClick = { dashboardViewModel.clearBasket() }) {
                            Text("Vider", color = Color(0xFFFF5252), fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (basketItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.RemoveShoppingCart, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Votre panier est vide", color = Color.LightGray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Scannez un code-barres de produit avec la caméra pour l'ajouter.", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(basketItems) { item ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF1E293B)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.name,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (!item.barcode.isNullOrBlank()) {
                                                Text(
                                                    text = "Code: ${item.barcode}",
                                                    fontSize = 11.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                            Text(
                                                text = "${item.unitPrice} ${item.currency} / unité",
                                                fontSize = 12.sp,
                                                color = ToofanGreen,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            if (item.isApparel) {
                                                Row(
                                                    modifier = Modifier.padding(top = 4.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = Color(0xFF8B5CF6).copy(alpha = 0.2f),
                                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF8B5CF6))
                                                    ) {
                                                        Text(
                                                            text = "👗 Taille ${item.size ?: "M"}",
                                                            color = Color(0xFFC4B5FD),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    Text(
                                                        text = "⚙️ Modifier",
                                                        fontSize = 10.sp,
                                                        color = ToofanGreen,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.clickable {
                                                            dashboardViewModel.openApparelTreatmentForItem(item)
                                                        }
                                                    )
                                                }
                                                if (!item.treatmentInfo.isNullOrBlank()) {
                                                    Text(
                                                        text = item.treatmentInfo,
                                                        fontSize = 10.sp,
                                                        color = Color(0xFF94A3B8),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.padding(top = 2.dp)
                                                    )
                                                }
                                            } else {
                                                Text(
                                                    text = "👔 Définir comme vêtement",
                                                    fontSize = 10.sp,
                                                    color = Color.Gray,
                                                    modifier = Modifier
                                                        .padding(top = 4.dp)
                                                        .clickable {
                                                            dashboardViewModel.openApparelTreatmentForItem(item)
                                                        }
                                                )
                                            }
                                        }

                                        // Quantity Controls (- / count / +)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            IconButton(
                                                onClick = { dashboardViewModel.updateBasketItemQuantity(item.id, item.quantity - 1) },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .background(Color(0xFF334155), CircleShape)
                                            ) {
                                                Icon(Icons.Default.Remove, contentDescription = "Moins", tint = Color.White, modifier = Modifier.size(16.dp))
                                            }

                                            Text(
                                                text = "${item.quantity}",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )

                                            IconButton(
                                                onClick = { dashboardViewModel.updateBasketItemQuantity(item.id, item.quantity + 1) },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .background(ToofanGreen, CircleShape)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = "Plus", tint = Color.Black, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Total & Checkout Button
                    val basketTotal = dashboardViewModel.calculateBasketTotalInCurrency(paymentCurrency)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF131D31),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Panier :", color = Color.LightGray, fontSize = 14.sp)
                                Text(
                                    text = if(basketTotal.isFinite()) String.format(java.util.Locale.US, "%.2f %s", basketTotal, paymentCurrency) else "Devis serveur requis",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = ToofanGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    dashboardViewModel.toggleBasketDrawer(false)
                                    dashboardViewModel.openConfirmPosPayment()
                                },
                                enabled = basketItems.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)
                            ) {
                                Text("Valider et Saisir le PIN", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Obtain and display the authoritative server quote before requesting a PIN.
    if (payscanState.showConfirmPaymentDialog) {
        LaunchedEffect(Unit) { dashboardViewModel.reviewPosPayment() }
    }

    // Modal: Reçu de succès après paiement POS réel
    if (payscanState.showSuccessReceiptDialog) {
        val trans = payscanState.completedTransaction
        Dialog(
            onDismissRequest = {
                dashboardViewModel.closeSuccessReceiptDialog()
                onDismissRequest()
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF070B14)),
                color = Color(0xFF070B14)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .systemBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(ToofanGreen.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ToofanGreen,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Paiement POS Confirmé !",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White,
                        fontFamily = MulishFontFamily
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "La transaction a été validée par le serveur CashPay",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Receipt ticket design
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF131D31),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26354D))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Réf. Vente :", color = Color.Gray, fontSize = 12.sp)
                                Text(trans?.saleReference ?: "POS-VALIDE", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Boutique / Comptoir :", color = Color.Gray, fontSize = 12.sp)
                                Text(trans?.boutiqueName ?: "Commerce CashPay", color = Color.White, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Portefeuille Vendeur :", color = Color.Gray, fontSize = 12.sp)
                                Text(trans?.sellerWalletId ?: "", color = ToofanGreen, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Date & Heure :", color = Color.Gray, fontSize = 12.sp)
                                Text(trans?.createdAt ?: "Instantané", color = Color.White, fontSize = 12.sp)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF26354D))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Débité :", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                Text(
                                    text = String.format(java.util.Locale.US, "%.2f %s", trans?.amount ?: 0.0, trans?.paymentCurrency ?: trans?.currency ?: "USD"),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = ToofanGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            dashboardViewModel.closeSuccessReceiptDialog()
                            onDismissRequest()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)
                    ) {
                        Text("Terminer", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal: Manual Barcode / Code entry
    if (showManualInputDialog) {
        Dialog(onDismissRequest = { showManualInputDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF131D31),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26354D))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Saisie manuelle d'un code",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Saisissez un code-barres produit, une référence POS ou un ID Wallet :",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = manualBarcodeValue,
                        onValueChange = { manualBarcodeValue = it },
                        placeholder = { Text("Ex: 3760123456789 ou CD0100000001", color = Color.Gray, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ToofanGreen,
                            unfocusedBorderColor = Color(0xFF334155)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showManualInputDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Annuler", color = Color.LightGray)
                        }

                        Button(
                            onClick = {
                                if (manualBarcodeValue.isNotBlank()) {
                                    val code = manualBarcodeValue.trim()
                                    showManualInputDialog = false
                                    manualBarcodeValue = ""
                                    dashboardViewModel.processPayScanInput(code)
                                }
                            },
                            enabled = manualBarcodeValue.isNotBlank(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)
                        ) {
                            Text("Valider", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Dedicated Apparel & Clothing Treatment Modal Sheet
    if (payscanState.showApparelTreatmentSheet && payscanState.lastScannedApparelItem != null) {
        val apparelItem = payscanState.lastScannedApparelItem
        ApparelTreatmentDialog(
            item = apparelItem,
            onApply = { size, antiTheft, note ->
                dashboardViewModel.applyApparelTreatment(
                    itemId = apparelItem.id,
                    size = size,
                    antiTheftConfirmed = antiTheft,
                    treatmentNote = note
                )
            },
            onDismiss = { dashboardViewModel.closeApparelTreatmentSheet() }
        )
    }
}

@Composable
fun ApparelTreatmentDialog(
    item: com.example.ui.viewmodel.PayScanCartItem,
    onApply: (size: String, antiTheftConfirmed: Boolean, treatmentNote: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSize by remember { mutableStateOf(item.size ?: "M") }
    var antiTheftEnabled by remember { mutableStateOf(true) }

    val sizes = listOf("XS", "S", "M", "L", "XL", "XXL", "Sur-mesure")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF8B5CF6).copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👗", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Traitement Vêtements & Mode",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = MulishFontFamily
                            )
                            Text(
                                text = "Prêt-à-porter CashPay POS",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Item info card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontFamily = MulishFontFamily
                            )
                            if (!item.barcode.isNullOrBlank()) {
                                Text(
                                    text = "Code: ${item.barcode}",
                                    color = Color.Gray,
                                    fontSize = 11.sp,
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }
                        Text(
                            text = "${item.unitPrice} ${item.currency}",
                            fontWeight = FontWeight.Bold,
                            color = ToofanGreen,
                            fontSize = 15.sp,
                            fontFamily = MulishFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Size selection
                Text(
                    text = "Taille sélectionnée :",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontFamily = MulishFontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sizes.take(4).forEach { sz ->
                        val isSel = selectedSize == sz
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) ToofanGreen else Color(0xFF1E293B))
                                .border(1.dp, if (isSel) ToofanGreen else Color(0xFF334155), RoundedCornerShape(8.dp))
                                .clickable { selectedSize = sz }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sz,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.Black else Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sizes.drop(4).forEach { sz ->
                        val isSel = selectedSize == sz
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) ToofanGreen else Color(0xFF1E293B))
                                .border(1.dp, if (isSel) ToofanGreen else Color(0xFF334155), RoundedCornerShape(8.dp))
                                .clickable { selectedSize = sz }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sz,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.Black else Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Caisse & Anti-theft Treatment
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(20.dp))
                                Column {
                                    Text("Démagnétisation Antivol Caisse", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp, fontFamily = MulishFontFamily)
                                    Text("Désactivation RF au paiement", color = Color.LightGray, fontSize = 10.sp, fontFamily = MulishFontFamily)
                                }
                            }
                            Switch(
                                checked = antiTheftEnabled,
                                onCheckedChange = { antiTheftEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = ToofanGreen
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(16.dp))
                            Text(
                                text = "Conseils d'entretien : Lavage 30°C doux • Textile certifié CashPay",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Apply button
                Button(
                    onClick = {
                        val treatmentSummary = buildString {
                            append("Prêt-à-porter vérifié")
                            if (antiTheftEnabled) append(" • Antivol désactivé")
                        }
                        onApply(selectedSize, antiTheftEnabled, treatmentSummary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Valider Traitement & Taille",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = MulishFontFamily
                    )
                }
            }
        }
    }
}

@Composable
private fun PayScanModeChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) ToofanGreen else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else Color.LightGray,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.Black else Color.LightGray,
                maxLines = 1
            )
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(if (isSelected) Color.Black else ToofanGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$badgeCount",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color.Black
                    )
                }
            }
        }
    }
}
