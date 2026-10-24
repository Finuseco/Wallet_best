package com.example.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.MulishFontFamily
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

private val UsdtTeal = Color(0xFF26A17B)
private val UsdtDarkBg = Color(0xFF0F172A)
private val UsdtSurface = Color(0xFF1E293B)

@Composable
fun ReceiveUsdtDialog(
    isOpen: Boolean,
    usdtAddress: String?,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRefreshAddress: () -> Unit = {},
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val displayAddress = usdtAddress?.trim().orEmpty()

    val qrBitmap = remember(displayAddress) {
        if (displayAddress.isBlank()) null
        else {
            try {
                val writer = QRCodeWriter()
                val bitMatrix = writer.encode(displayAddress, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                    }
                }
                bmp
            } catch (_: Exception) {
                null
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 310.dp, max = 360.dp)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(22.dp),
            color = UsdtDarkBg,
            border = BorderStroke(1.2.dp, UsdtTeal.copy(alpha = 0.5f)),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Logo/Badge + Title + Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(UsdtTeal.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "₮",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = UsdtTeal,
                                fontFamily = MulishFontFamily
                            )
                        }
                        Column {
                            Text(
                                text = "Recevoir USDT",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = MulishFontFamily
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = UsdtTeal.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Réseau TRON (TRC20)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UsdtTeal,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontFamily = MulishFontFamily
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = UsdtTeal)
                    }
                } else if (displayAddress.isBlank()) {
                    // Empty or error state
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "Adresse USDT non configurée ou en attente d'initialisation.",
                            color = Color(0xFFFCA5A5),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = MulishFontFamily
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onRefreshAddress,
                            colors = ButtonDefaults.buttonColors(containerColor = UsdtTeal),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Générer mon adresse", fontFamily = MulishFontFamily, fontSize = 12.sp)
                        }
                    }
                } else {
                    // QR Code Card
                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        qrBitmap?.let { bmp ->
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "QR Code USDT TRC-20",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Address Display with Copy Button
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = UsdtSurface,
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = displayAddress,
                                color = UsdtTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = MulishFontFamily,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(displayAddress))
                                    Toast.makeText(context, "Adresse USDT TRC20 copiée !", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copier",
                                    tint = UsdtTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Copy Address Full Action Button
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(displayAddress))
                            Toast.makeText(context, "Adresse USDT copiée dans le presse-papier !", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UsdtTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Copier l'adresse TRC20",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Important Warning Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "⚠️ Réseau TRON (TRC-20) uniquement",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B),
                            fontFamily = MulishFontFamily
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Envoyez uniquement des USDT sur le protocole TRC20 à cette adresse. Les dépôts sur d'autres réseaux (ERC20, BEP20) ou d'autres jetons ne pourront pas être crédités.",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8),
                            fontFamily = MulishFontFamily,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
