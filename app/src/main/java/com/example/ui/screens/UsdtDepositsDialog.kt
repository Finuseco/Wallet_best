package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UsdtDepositItem
import com.example.ui.theme.MulishFontFamily

private val UsdtTeal = Color(0xFF26A17B)
private val UsdtDarkBg = Color(0xFF0F172A)
private val UsdtSurface = Color(0xFF1E293B)

@Composable
fun UsdtDepositsDialog(
    isOpen: Boolean,
    deposits: List<UsdtDepositItem>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 320.dp, max = 400.dp)
                .fillMaxHeight(0.75f)
                .padding(16.dp),
            shape = RoundedCornerShape(22.dp),
            color = UsdtDarkBg,
            border = BorderStroke(1.2.dp, UsdtTeal.copy(alpha = 0.5f)),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(UsdtTeal.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = UsdtTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Dépôts USDT (TRC-20)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = MulishFontFamily
                            )
                            Text(
                                text = "Historique synchronisé",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                fontFamily = MulishFontFamily
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualiser",
                                tint = UsdtTeal,
                                modifier = Modifier.size(18.dp)
                            )
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
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = UsdtTeal)
                    }
                } else if (deposits.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "Aucun dépôt USDT enregistré",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = MulishFontFamily
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Vos dépôts entrants sur le réseau TRON (TRC20) apparaîtront ici après détection blockchain.",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(deposits, key = { it.id ?: it.txHash ?: it.hashCode().toLong() }) { deposit ->
                            val isConfirmed = deposit.status?.equals("confirmed", ignoreCase = true) == true
                            val statusColor = if (isConfirmed) UsdtTeal else Color(0xFFF59E0B)
                            val statusText = deposit.status ?: "pending"

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = UsdtSurface,
                                border = BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "+ ${deposit.amount ?: "0"} USDT",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = UsdtTeal,
                                            fontFamily = MulishFontFamily
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = statusColor.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = statusText,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontFamily = MulishFontFamily
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Confirmations & Date
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Confirmations : ${deposit.confirmations ?: 0}/${deposit.requiredConfirmations ?: 1}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF94A3B8),
                                            fontFamily = MulishFontFamily
                                        )
                                        deposit.createdAt?.let { dt ->
                                            Text(
                                                text = dt.take(19).replace("T", " "),
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B),
                                                fontFamily = MulishFontFamily
                                            )
                                        }
                                    }

                                    // Transaction Hash if available
                                    deposit.txHash?.let { hash ->
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "TX: ${hash.take(10)}...${hash.takeLast(8)}",
                                                fontSize = 10.sp,
                                                color = Color(0xFF38BDF8),
                                                fontFamily = MulishFontFamily
                                            )
                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(hash))
                                                    Toast.makeText(context, "Hash TX copié !", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copier le hash",
                                                    tint = Color(0xFF38BDF8),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
