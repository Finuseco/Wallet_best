package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.MulishFontFamily

enum class SquareStatusType {
    SUCCESS,
    INSUFFICIENT_BALANCE,
    ERROR,
    WARNING
}

data class StatusDetailItem(
    val label: String,
    val value: String,
    val isHighlight: Boolean = false
)

@Composable
fun CashPaySquareStatusDialog(
    isOpen: Boolean,
    type: SquareStatusType,
    title: String,
    message: String,
    details: List<StatusDetailItem> = emptyList(),
    primaryButtonText: String = "OK",
    onPrimaryAction: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val icon: ImageVector = when (type) {
        SquareStatusType.SUCCESS -> Icons.Default.CheckCircle
        SquareStatusType.INSUFFICIENT_BALANCE -> Icons.Default.AccountBalanceWallet
        SquareStatusType.ERROR -> Icons.Default.ErrorOutline
        SquareStatusType.WARNING -> Icons.Default.WarningAmber
    }

    val iconColor: Color = when (type) {
        SquareStatusType.SUCCESS -> Color(0xFF10B981)
        SquareStatusType.INSUFFICIENT_BALANCE -> Color(0xFFF59E0B)
        SquareStatusType.ERROR -> Color(0xFFEF4444)
        SquareStatusType.WARNING -> Color(0xFFF59E0B)
    }

    val iconBgColor: Color = iconColor.copy(alpha = 0.16f)

    val borderColor: Color = when (type) {
        SquareStatusType.SUCCESS -> Color(0xFF10B981).copy(alpha = 0.5f)
        SquareStatusType.INSUFFICIENT_BALANCE -> Color(0xFFF59E0B).copy(alpha = 0.5f)
        SquareStatusType.ERROR -> Color(0xFFEF4444).copy(alpha = 0.5f)
        SquareStatusType.WARNING -> Color(0xFFF59E0B).copy(alpha = 0.4f)
    }

    val primaryBtnColor: Color = when (type) {
        SquareStatusType.SUCCESS -> Color(0xFF10B981)
        SquareStatusType.INSUFFICIENT_BALANCE -> Color(0xFFF59E0B)
        SquareStatusType.ERROR -> Color(0xFFEF4444)
        SquareStatusType.WARNING -> Color(0xFFF59E0B)
    }

    val primaryBtnTextColor: Color = when (type) {
        SquareStatusType.SUCCESS -> Color.White
        SquareStatusType.INSUFFICIENT_BALANCE -> Color.Black
        SquareStatusType.ERROR -> Color.White
        SquareStatusType.WARNING -> Color.Black
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 300.dp, max = 340.dp)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF101827),
            border = BorderStroke(1.dp, borderColor),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Centered Circular Status Badge
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Centered Title
                Text(
                    text = title,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Centered Message
                Text(
                    text = message,
                    fontFamily = MulishFontFamily,
                    fontSize = 12.5.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                // Optional structured details card
                if (details.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            details.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.label,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = item.value,
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = if (item.isHighlight) FontWeight.Bold else FontWeight.Medium,
                                        color = if (item.isHighlight) iconColor else Color.White,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary Button
                Button(
                    onClick = onPrimaryAction,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBtnColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        text = primaryButtonText,
                        color = primaryBtnTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = MulishFontFamily
                    )
                }

                // Optional Secondary Button
                if (secondaryButtonText != null && onSecondaryAction != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = onSecondaryAction,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = secondaryButtonText,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.5.sp,
                            fontFamily = MulishFontFamily
                        )
                    }
                }
            }
        }
    }
}
