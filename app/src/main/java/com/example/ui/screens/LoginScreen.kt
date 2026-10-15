package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import android.widget.Toast
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CountryPickerBottomSheet
import com.example.ui.components.ToofanButton
import com.example.ui.components.ToofanInputField
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanBgColor
import com.example.ui.theme.ToofanBodyText
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanGrey1
import com.example.ui.theme.ToofanLinkPink
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite
import com.example.ui.viewmodel.AuthMethod
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    var rememberMe by remember { mutableStateOf(true) }

    if (uiState.isCountryPickerOpen) {
        CountryPickerBottomSheet(
            sheetState = bottomSheetState,
            countries = uiState.countries,
            selectedCountry = uiState.selectedCountry,
            searchQuery = uiState.countrySearchQuery,
            onSearchQueryChange = { viewModel.setCountrySearchQuery(it) },
            onCountrySelected = { viewModel.selectCountry(it) },
            onDismissRequest = { viewModel.setCountryPickerOpen(false) }
        )
    }

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
        // Subtle soft vignette so background image stays 100% clear and real
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
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // CashPay Brand Logo prominently centered near top
            Image(
                painter = painterResource(id = R.drawable.cashpay_logo_white),
                contentDescription = "CashPay All Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(52.dp)
                    .testTag("cashpay_header_logo")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Auth Method Selector Tab
            TabRow(
                selectedTabIndex = if (uiState.authMethod == AuthMethod.PHONE) 0 else 1,
                containerColor = ToofanWhite,
                contentColor = ToofanGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[if (uiState.authMethod == AuthMethod.PHONE) 0 else 1]),
                        color = ToofanGreen,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, ToofanGrey1.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = uiState.authMethod == AuthMethod.PHONE,
                    onClick = { viewModel.setAuthMethod(AuthMethod.PHONE) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = if (uiState.authMethod == AuthMethod.PHONE) ToofanGreen else ToofanBodyText,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Téléphone",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.authMethod == AuthMethod.PHONE) ToofanGreen else ToofanBodyText
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_phone")
                )
                Tab(
                    selected = uiState.authMethod == AuthMethod.WALLET_ID,
                    onClick = { viewModel.setAuthMethod(AuthMethod.WALLET_ID) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = if (uiState.authMethod == AuthMethod.WALLET_ID) ToofanGreen else ToofanBodyText,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Wallet ID",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.authMethod == AuthMethod.WALLET_ID) ToofanGreen else ToofanBodyText
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_wallet_id")
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (uiState.authMethod == AuthMethod.PHONE) {
                // Country Selection Box in Toofan InputField style
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, ToofanGrey1.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                        .clickable { viewModel.setCountryPickerOpen(true) }
                        .testTag("country_picker_button"),
                    color = ToofanWhite
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = uiState.selectedCountry.flagEmoji,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Text(
                                text = uiState.selectedCountry.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = ToofanMainDark,
                                maxLines = 1
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = uiState.selectedCountry.dialCode,
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = ToofanGreen
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Sélectionner",
                                tint = ToofanBodyText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Input Field (Strict 9-digit restriction)
                ToofanInputField(
                    value = uiState.localPhone,
                    onValueChange = { viewModel.onLocalPhoneChanged(it) },
                    placeholder = "812345678",
                    leadingIcon = {
                        Text(
                            text = uiState.selectedCountry.dialCode,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = ToofanGreen,
                            fontSize = 15.sp
                        )
                    },
                    trailingIcon = {
                        Text(
                            text = "${uiState.localPhone.length}/9",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.localPhone.length == 9) ToofanGreen else ToofanBodyText.copy(alpha = 0.5f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { if (uiState.isPhoneValid) viewModel.submitIdentification() }
                    ),
                    testTag = "phone_input_field"
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Full phone number preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Numéro complet : ${uiState.fullPhone}",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = ToofanBodyText
                    )
                }
            } else {
                // Wallet ID Input
                ToofanInputField(
                    value = uiState.walletId,
                    onValueChange = { viewModel.onWalletIdChanged(it) },
                    placeholder = "CD0100000001",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = ToofanGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { if (uiState.isWalletIdValid) viewModel.submitIdentification() }
                    ),
                    testTag = "wallet_id_input_field"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Remember me & Lost password row from Toofan
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(ToofanWhite)
                            .border(1.dp, Color(0xFF868698), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rememberMe) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF868698))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Remember me",
                        fontFamily = MulishFontFamily,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "Aide connexion ?",
                    fontFamily = MulishFontFamily,
                    fontSize = 14.sp,
                    color = ToofanLinkPink,
                    fontWeight = FontWeight.Medium
                )
            }

            // Error Message Banner
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
                        .padding(top = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Erreur",
                            tint = Color(0xFFFF4868),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            fontFamily = MulishFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFFFF4868)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Toofan Button "Sign in"
            ToofanButton(
                title = "Sign in / Continuer",
                onClick = { viewModel.submitIdentification() },
                enabled = uiState.isIdentificationReady && !uiState.isOtpLoading,
                isLoading = uiState.isOtpLoading,
                testTag = "submit_identification_button"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Register now row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.clickable { viewModel.goToRegister() }
            ) {
                Text(
                    text = "No account? ",
                    fontFamily = MulishFontFamily,
                    fontSize = 14.sp,
                    color = ToofanBodyText
                )
                Text(
                    text = "Register now",
                    fontFamily = MulishFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ToofanLinkPink
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Forgot PIN / Account Recovery link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openForgotPin() }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Code PIN oublié ? ",
                    fontFamily = MulishFontFamily,
                    fontSize = 13.sp,
                    color = ToofanBodyText
                )
                Text(
                    text = "Récupérer mon compte",
                    fontFamily = MulishFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ToofanGreen
                )
            }

            // Single Login with Google button
            Surface(
                onClick = {
                    Toast.makeText(context, "Connexion Google non disponible pour le moment", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(12.dp),
                color = ToofanWhite,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("google_signin_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "G",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEA4335)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continuer avec Google",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ToofanMainDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Text(
                text = "CashPay • Powered by FINUSECO SA",
                fontFamily = MulishFontFamily,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Forgot PIN Dialog for Account Recovery
        ForgotPinDialog(
            isOpen = uiState.isForgotPinOpen,
            step = uiState.forgotPinStep,
            phone = uiState.forgotPinPhone,
            channel = uiState.forgotPinChannel,
            otp = uiState.forgotPinOtp,
            newPin = uiState.forgotPinNewPin,
            confirmPin = uiState.forgotPinConfirmPin,
            isLoading = uiState.isForgotPinLoading,
            errorMessage = uiState.forgotPinError,
            successMessage = uiState.forgotPinSuccess,
            onPhoneChange = { viewModel.setForgotPinPhone(it) },
            onChannelChange = { viewModel.setForgotPinChannel(it) },
            onOtpChange = { viewModel.setForgotPinOtp(it) },
            onNewPinChange = { viewModel.setForgotPinNewPin(it) },
            onConfirmPinChange = { viewModel.setForgotPinConfirmPin(it) },
            onRequestSubmit = { viewModel.submitForgotPinRequest() },
            onVerifySubmit = { viewModel.submitForgotPinVerify() },
            onResetSubmit = { viewModel.submitForgotPinReset() },
            onDismiss = { viewModel.closeForgotPin() }
        )
    }
}
