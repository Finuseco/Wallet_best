package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.components.OtpInputField
import com.example.ui.components.ToofanButton
import com.example.ui.components.ToofanHeader
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanBgColor
import com.example.ui.theme.ToofanBodyText
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanGrey1
import com.example.ui.theme.ToofanLinkPink
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite
import com.example.ui.viewmodel.AuthStep
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun OtpScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ToofanBgColor)
    ) {
        // Full screen vivid fintech background image 100% visible
        Image(
            painter = painterResource(id = R.drawable.fintech_bg_person_1790609860029),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 1.0f
        )
        // Soft vignette so background photo remains 100% clear
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x22070F22),
                            Color(0x330F1E3D),
                            Color(0x55050B17)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // CashPay Brand Logo prominently and cleanly lowered & centered
            Image(
                painter = painterResource(id = R.drawable.cashpay_logo_white),
                contentDescription = "CashPay Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(50.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = ToofanWhite,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.authStep == AuthStep.OTP_CHANNEL_SELECT) {
                        Text(
                            text = "Choisissez le canal de réception",
                            fontSize = 20.sp,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = ToofanMainDark,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Sélectionnez où vous souhaitez recevoir le code de sécurité pour autoriser l'installation.",
                            fontSize = 13.sp,
                            fontFamily = MulishFontFamily,
                            color = ToofanBodyText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
                        )

                        val channels = uiState.channels.map { channelId ->
                            when (channelId.lowercase()) {
                                "sms" -> Triple("sms", "SMS sécurisé", uiState.maskedPhone)
                                "whatsapp" -> Triple("whatsapp", "WhatsApp Direct", uiState.maskedPhone)
                                "email" -> Triple("email", "Courrier électronique", uiState.maskedEmail)
                                else -> Triple(channelId, channelId.replaceFirstChar { it.uppercase() }, uiState.maskedPhone)
                            }
                        }

                        channels.forEach { (channelId, channelTitle, maskedRecipient) ->
                            val isSelected = uiState.selectedChannel == channelId
                            val iconVector = when (channelId) {
                                "sms" -> Icons.Default.Sms
                                "whatsapp" -> Icons.Default.Chat
                                else -> Icons.Default.Email
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = if (isSelected) 1.8.dp else 1.dp,
                                        color = if (isSelected) ToofanGreen else ToofanGrey1.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.selectOtpChannel(channelId) }
                                    .testTag("otp_channel_$channelId"),
                                color = ToofanWhite,
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isSelected) ToofanGreen.copy(alpha = 0.15f) else ToofanBgColor,
                                            modifier = Modifier.size(38.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = iconVector,
                                                    contentDescription = null,
                                                    tint = if (isSelected) ToofanGreen else ToofanBodyText,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Column {
                                            Text(
                                                text = channelTitle,
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = ToofanMainDark
                                            )
                                            Text(
                                                text = maskedRecipient,
                                                fontFamily = MulishFontFamily,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = ToofanGreen
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Sélectionné",
                                            tint = ToofanGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        ToofanButton(
                            title = "Envoyer le code OTP",
                            onClick = { viewModel.requestOtpSend() },
                            isLoading = uiState.isOtpLoading,
                            testTag = "send_otp_button"
                        )
                    } else {
                        Text(
                            text = "Vérification du code de sécurité",
                            fontSize = 20.sp,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = ToofanMainDark,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Code envoyé à ${if (uiState.selectedChannel == "email") uiState.maskedEmail else uiState.maskedPhone}",
                            fontFamily = MulishFontFamily,
                            fontSize = 13.sp,
                            color = ToofanGreen,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                        )

                        // 6-digit OTP Box
                        OtpInputField(
                            otpValue = uiState.otpCode,
                            onOtpChange = { viewModel.onOtpChanged(it) },
                            isError = uiState.errorMessage != null,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Confirm OTP button
                        ToofanButton(
                            title = "Confirmer le code OTP",
                            onClick = { viewModel.submitOtpVerification() },
                            enabled = uiState.otpCode.length == 6 && !uiState.isOtpLoading,
                            isLoading = uiState.isOtpLoading,
                            testTag = "confirm_otp_button"
                        )

                        // Error Banner
                        AnimatedVisibility(
                            visible = uiState.errorMessage != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFECEF),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Erreur",
                                        tint = Color(0xFFFF4868),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = uiState.errorMessage ?: "",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        color = Color(0xFFFF4868)
                                    )
                                }
                            }
                        }

                        if (uiState.isOtpLoading) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = ToofanGreen,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Validation en cours...",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    color = ToofanGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Pas reçu le code ? ",
                                fontFamily = MulishFontFamily,
                                fontSize = 13.sp,
                                color = ToofanBodyText
                            )
                            Text(
                                text = "Renvoyer",
                                fontFamily = MulishFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ToofanLinkPink,
                                modifier = Modifier.clickable { viewModel.requestOtpSend() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Shortcuts & Redirections
                    Text(
                        text = "Ce n'est pas votre numéro ? Changer",
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ToofanLinkPink,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateBack() }
                            .padding(vertical = 4.dp)
                    )

                    Text(
                        text = "Abandonner et retour",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = ToofanBodyText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.resetToLogin() }
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
