package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BrandPurple

@Composable
fun PinDots(
    pinLength: Int,
    maxDigits: Int = 4,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isDark: Boolean = false
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxDigits) { index ->
            val isFilled = index < pinLength
            val size by animateDpAsState(
                targetValue = if (isFilled) 12.dp else 10.dp,
                animationSpec = spring(),
                label = "toofan_pin_size"
            )
            val defaultEmptyColor = BrandPurple.copy(alpha = if (isDark) 0.35f else 0.2f)
            val defaultFilledColor = if (isDark) Color(0xFFA66BE0) else BrandPurple
            val color by animateColorAsState(
                targetValue = when {
                    isError -> Color(0xFFFF4868)
                    isFilled -> defaultFilledColor
                    else -> defaultEmptyColor
                },
                label = "toofan_pin_color"
            )

            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
