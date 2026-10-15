package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanBgColor
import com.example.ui.theme.ToofanBodyText
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanGrey1
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun ProfileInstallScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    modifier: Modifier = Modifier
) {
    val progressFloat by animateFloatAsState(
        targetValue = uiState.installProgressPercent / 100f,
        label = "install_progress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ToofanBgColor)
    ) {
        // Curved background header
        Image(
            painter = painterResource(id = R.drawable.toofan_bg_01),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // CashPay Brand Logo at the top header
            Image(
                painter = painterResource(id = R.drawable.cashpay_logo),
                contentDescription = "CashPay All",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(38.dp)
                    .padding(bottom = 16.dp)
            )

            // User Avatar
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(ToofanWhite)
                    .border(2.5.dp, ToofanGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!uiState.installedUserProfile?.profilePhotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = uiState.installedUserProfile?.profilePhotoUrl,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    val initials = (uiState.installedUserProfile?.fullName ?: uiState.rawPhone)
                        .trim()
                        .split(" ")
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2)
                        .joinToString("")
                        .uppercase()
                        .ifBlank { "CP" }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF000E38),
                                        Color(0xFF00E5FF)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val displayName = uiState.installedUserProfile?.fullName
                ?: (if (uiState.rawPhone.isNotBlank()) uiState.rawPhone else "Installation du compte")

            // User Identity & Greeting with blue certified badge when profile ready
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = displayName,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = ToofanMainDark
                )
                if (uiState.installProgressPercent >= 90) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Image(
                        painter = painterResource(id = R.drawable.badge_verified),
                        contentDescription = "Compte Vérifié",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = "Wallet ID : ${uiState.profileInstallation?.walletId ?: uiState.walletId}",
                fontFamily = MulishFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ToofanGreen,
                modifier = Modifier.padding(top = 2.dp, bottom = 24.dp)
            )

            // System Installation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ToofanWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Installation du profil système",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ToofanMainDark
                        )
                        Text(
                            text = "${uiState.installProgressPercent}%",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = ToofanGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { progressFloat },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = ToofanGreen,
                        trackColor = ToofanGrey1.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic Stage Log
                    Text(
                        text = uiState.installStageText,
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = ToofanBodyText,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Step breakdown pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SystemStepBadge(
                            label = "Certificats",
                            isDone = uiState.installProgressPercent >= 30,
                            modifier = Modifier.weight(1f)
                        )
                        SystemStepBadge(
                            label = "Données KYC",
                            isDone = uiState.installProgressPercent >= 65,
                            modifier = Modifier.weight(1f)
                        )
                        SystemStepBadge(
                            label = "Clés Sécurisées",
                            isDone = uiState.installProgressPercent >= 99,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemStepBadge(
    label: String,
    isDone: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isDone) ToofanGreen.copy(alpha = 0.12f) else ToofanBgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Security,
                contentDescription = null,
                tint = if (isDone) ToofanGreen else ToofanBodyText.copy(alpha = 0.5f),
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontFamily = MulishFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDone) ToofanGreen else ToofanBodyText.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}
