package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ToofanGreen

@Composable
fun CustomKeypad(
    modifier: Modifier = Modifier,
    isDark: Boolean = true,
    isBiometricEnabled: Boolean = true,
    isPinLocked: Boolean = false,
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onBiometricClick: (() -> Unit)? = null
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { digit ->
                    ToofanKeypadKey(
                        text = digit,
                        isDark = isDark,
                        isEnabled = !isPinLocked,
                        onClick = { if (!isPinLocked) onDigitClick(digit) },
                        testTag = "keypad_digit_$digit"
                    )
                }
            }
        }

        // Bottom row: Biometric (Fingerprint) / 0 / Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left Action Key: Biometric / Fingerprint with dynamic touch/pulse animation
            if (onBiometricClick != null) {
                BiometricKeypadKey(
                    isEnabled = isBiometricEnabled,
                    isDark = isDark,
                    onClick = onBiometricClick,
                    testTag = "keypad_biometric_button"
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Zero Key
            ToofanKeypadKey(
                text = "0",
                isDark = isDark,
                isEnabled = !isPinLocked,
                onClick = { if (!isPinLocked) onDigitClick("0") },
                testTag = "keypad_digit_0"
            )

            // Right Action Key: Clean Compact Backspace
            BackspaceKeypadKey(
                isDark = isDark,
                isEnabled = !isPinLocked,
                onClick = { if (!isPinLocked) onBackspaceClick() },
                testTag = "keypad_backspace_button"
            )
        }
    }
}

@Composable
private fun RowScope.ToofanKeypadKey(
    text: String,
    isDark: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed && isEnabled) 0.92f else 1.0f, label = "key_scale")

    val bg = if (isDark) {
        if (isEnabled) Color(0xFF1E293B) else Color(0xFF1E293B).copy(alpha = 0.4f)
    } else {
        if (isEnabled) Color(0xFFF1F5F9) else Color(0xFFF1F5F9).copy(alpha = 0.4f)
    }
    val textColor = if (isDark) {
        if (isEnabled) Color.White else Color.White.copy(alpha = 0.3f)
    } else {
        if (isEnabled) Color(0xFF0F172A) else Color(0xFF0F172A).copy(alpha = 0.3f)
    }
    val border = if (isDark) {
        if (isEnabled) BorderStroke(1.dp, Color(0xFF334155)) else BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.3f))
    } else {
        if (isEnabled) BorderStroke(1.dp, Color(0xFFE2E8F0)) else BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.3f))
    }

    Surface(
        modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .let {
                if (isEnabled) {
                    it.clickable(
                        interactionSource = interactionSource,
                        indication = androidx.compose.material3.ripple(bounded = true),
                        onClick = onClick
                    )
                } else {
                    it
                }
            }
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = bg,
        border = border,
        shadowElevation = if (isDark || !isEnabled) 0.dp else 1.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun RowScope.BiometricKeypadKey(
    isEnabled: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Dynamic rotation & scale motion when touched as requested by user
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "biometric_scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isPressed) -12f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "biometric_rotation"
    )

    val tint = if (isEnabled) Color(0xFF00E676) else Color(0xFF64748B)
    val bg = if (isDark) {
        if (isEnabled) Color(0xFF00E676).copy(alpha = 0.16f) else Color(0xFF1E293B)
    } else {
        if (isEnabled) Color(0xFFE8F7F0) else Color(0xFFF1F5F9)
    }
    val border = if (isEnabled) {
        BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
    } else {
        BorderStroke(1.dp, Color(0xFF334155))
    }

    Surface(
        modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(bounded = true),
                onClick = onClick
            )
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = bg,
        border = border,
        shadowElevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Empreinte biométrique",
                tint = tint,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotation)
            )
        }
    }
}

@Composable
private fun RowScope.BackspaceKeypadKey(
    isDark: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed && isEnabled) 0.90f else 1.0f, label = "backspace_scale")

    val bg = if (isDark) {
        if (isEnabled) Color(0xFF1E293B) else Color(0xFF1E293B).copy(alpha = 0.4f)
    } else {
        if (isEnabled) Color(0xFFF1F5F9) else Color(0xFFF1F5F9).copy(alpha = 0.4f)
    }
    val border = if (isDark) {
        if (isEnabled) BorderStroke(1.dp, Color(0xFF334155)) else BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.3f))
    } else {
        if (isEnabled) BorderStroke(1.dp, Color(0xFFE2E8F0)) else BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.3f))
    }
    val tint = if (isDark) {
        if (isEnabled) Color.White else Color.White.copy(alpha = 0.3f)
    } else {
        if (isEnabled) Color(0xFF0F172A) else Color(0xFF0F172A).copy(alpha = 0.3f)
    }

    Surface(
        modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .let {
                if (isEnabled) {
                    it.clickable(
                        interactionSource = interactionSource,
                        indication = androidx.compose.material3.ripple(bounded = true),
                        onClick = onClick
                    )
                } else {
                    it
                }
            }
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = bg,
        border = border,
        shadowElevation = if (isDark || !isEnabled) 0.dp else 1.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Effacer",
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
