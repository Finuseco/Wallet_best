package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onAnimationFinished: () -> Unit
) {
    val fullText = "CashPay All"
    var visibleLettersCount by remember { mutableIntStateOf(0) }
    val glowScale = remember { Animatable(0.7f) }
    val glowAlpha = remember { Animatable(0.2f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val iconFloatY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Pulse glowing background circles
        glowScale.animateTo(
            targetValue = 1.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        glowAlpha.animateTo(
            targetValue = 0.85f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        iconFloatY.animateTo(
            targetValue = -15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(400)
        // Letter-by-letter animation
        for (i in 1..fullText.length) {
            visibleLettersCount = i
            delay(160)
        }
        // Subtitle fade in
        subtitleAlpha.animateTo(1f, tween(600))
        delay(1200)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070F22), // Midnight Blue
                        Color(0xFF0A1938),
                        Color(0xFF030814)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Radiant ambient glow orbs
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(glowScale.value)
                .alpha(glowAlpha.value * 0.45f)
                .blur(80.dp)
                .background(ToofanGreen, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(y = (-40).dp)
                .scale(glowScale.value * 0.9f)
                .alpha(glowAlpha.value * 0.35f)
                .blur(70.dp)
                .background(Color(0xFF38BDF8), CircleShape)
        )

        // Floating finance & transfer badges
        Box(
            modifier = Modifier
                .offset(x = (-110).dp, y = (iconFloatY.value - 90).dp)
                .alpha(0.65f)
        ) {
            Icon(
                imageVector = Icons.Default.CurrencyExchange,
                contentDescription = null,
                tint = ToofanGreen,
                modifier = Modifier.size(28.dp)
            )
        }

        Box(
            modifier = Modifier
                .offset(x = 115.dp, y = (-iconFloatY.value - 70).dp)
                .alpha(0.65f)
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(26.dp)
            )
        }

        Box(
            modifier = Modifier
                .offset(x = (-95).dp, y = (iconFloatY.value + 110).dp)
                .alpha(0.6f)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(24.dp)
            )
        }

        Box(
            modifier = Modifier
                .offset(x = 100.dp, y = (-iconFloatY.value + 115).dp)
                .alpha(0.6f)
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = ToofanGreen,
                modifier = Modifier.size(26.dp)
            )
        }

        // Central content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Emblème CashPay Favicon
            Image(
                painter = painterResource(id = R.drawable.cashpay_logo_white),
                contentDescription = "CashPay Logo",
                modifier = Modifier
                    .height(64.dp)
                    .padding(bottom = 12.dp)
            )

            // Dynamic Letter-by-Letter display
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentString = fullText.take(visibleLettersCount)
                Text(
                    text = currentString,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 44.sp,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                if (visibleLettersCount < fullText.length) {
                    Text(
                        text = "•",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = ToofanGreen,
                        modifier = Modifier.alpha(glowAlpha.value)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle Slogan
            Text(
                text = "Digital Financial ecosystem",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = ToofanGreen,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Powered by FINUSECO SA",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color.LightGray.copy(alpha = 0.7f),
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }
    }
}
