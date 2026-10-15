package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Paint
import android.util.Base64
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite
import com.example.ui.viewmodel.AuthStep
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel
import java.io.ByteArrayOutputStream

import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.CameraCaptureMode
import com.example.ui.components.CashPayCameraCaptureDialog
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerInputChange

private val BrandOrange = Color(0xFFF97316)
private val BrandOrangeLight = Color(0xFFFB923C)
private val BrandGreen = Color(0xFF10B981)
private val BrandBlueMidnight = Color(0xFF0F1E3D)
private val BrandBlueDark = Color(0xFF070F22)

private data class SelectOption(val key: String, val label: String)

private data class InfoContent(
    val title: String,
    val role: String,
    val rules: String = "",
    val faq: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAccountScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    modifier: Modifier = Modifier
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Active multi-step wizard step (0 to 4 => 5 steps total)
    var regStep by remember { mutableIntStateOf(0) }

    // Info dialog state
    var activeInfo by remember { mutableStateOf<InfoContent?>(null) }

    // Channel selection for OTP (SMS or WhatsApp only)
    var selectedOtpChannel by remember { mutableStateOf("sms") }

    // Step Form Fields
    var accountType by remember { mutableStateOf("national") }
    var companyName by remember { mutableStateOf("") }
    var phoneOtpInput by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("fr") }

    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("male") }
    var birthDate by remember { mutableStateOf("") }
    var maritalStatus by remember { mutableStateOf("single") }
    var nationality by remember { mutableStateOf(uiState.selectedCountry.code) }
    var profession by remember { mutableStateOf("") }
    var emailOptional by remember { mutableStateOf("") }

    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var idType by remember { mutableStateOf("carte_identite") }
    var idNumber by remember { mutableStateOf("") }

    var pinCode by remember { mutableStateOf("") }
    var pinConfirm by remember { mutableStateOf("") }

    // Camera captures
    var activeCameraMode by remember { mutableStateOf<CameraCaptureMode?>(null) }
    var idFrontBase64 by remember { mutableStateOf<String?>(null) }
    var profilePhotoBase64 by remember { mutableStateOf<String?>(null) }
    var signatureBase64 by remember { mutableStateOf<String?>(null) }

    // Signature drawing state
    val signaturePaths = remember { mutableStateListOf<List<Offset>>() }
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }

    // Dropdown lists
    val genderOptions = remember {
        listOf(
            SelectOption("male", "Masculin"),
            SelectOption("female", "Féminin"),
            SelectOption("other", "Autre")
        )
    }

    val maritalOptions = remember {
        listOf(
            SelectOption("single", "Célibataire"),
            SelectOption("married", "Marié(e)"),
            SelectOption("divorced", "Divorcé(e)"),
            SelectOption("widowed", "Veuf / Veuve")
        )
    }

    val languageOptions = remember {
        listOf(
            SelectOption("fr", "Français"),
            SelectOption("en", "English"),
            SelectOption("sw", "Kiswahili"),
            SelectOption("ln", "Lingala"),
            SelectOption("es", "Español"),
            SelectOption("pt", "Português"),
            SelectOption("ar", "العربية")
        )
    }

    val idTypeOptions = remember {
        listOf(
            SelectOption("carte_identite", "Carte d'identité nationale"),
            SelectOption("passeport", "Passeport international"),
            SelectOption("permis_conduire", "Permis de conduire"),
            SelectOption("carte_electeur", "Carte d'électeur")
        )
    }

    // Camera Dialog
    activeCameraMode?.let { mode ->
        CashPayCameraCaptureDialog(
            mode = mode,
            onDismissRequest = { activeCameraMode = null },
            onImageCaptured = { base64Url ->
                when (mode) {
                    CameraCaptureMode.SELFIE_PROFILE -> profilePhotoBase64 = base64Url
                    CameraCaptureMode.ID_DOCUMENT_FRONT -> idFrontBase64 = base64Url
                    CameraCaptureMode.ID_DOCUMENT_BACK -> {}
                }
                activeCameraMode = null
            }
        )
    }

    // Country Picker Sheet (Exact same as Login)
    if (uiState.isCountryPickerOpen) {
        CountryPickerBottomSheet(
            sheetState = bottomSheetState,
            countries = uiState.countries,
            selectedCountry = uiState.selectedCountry,
            searchQuery = uiState.countrySearchQuery,
            onSearchQueryChange = { viewModel.setCountrySearchQuery(it) },
            onCountrySelected = {
                viewModel.selectCountry(it)
                nationality = it.code
            },
            onDismissRequest = { viewModel.setCountryPickerOpen(false) }
        )
    }

    // Helpful Field Info Dialog
    activeInfo?.let { info ->
        FieldInfoDialog(
            content = info,
            onDismiss = { activeInfo = null }
        )
    }

    // BIG CENTER POP-UP DIALOG FOR EXISTING ACCOUNTS (ONCHAT RED PROHIBITION DESIGN)
    val existingAccModalMsg = uiState.existingAccountErrorModalMessage
    if (!existingAccModalMsg.isNullOrBlank()) {
        Dialog(
            onDismissRequest = { viewModel.clearExistingAccountErrorModal() },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(24.dp)),
                color = Color(0xFF0B0F19), // OnChat dark navy background
                shadowElevation = 24.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(Color(0xFFEF4444).copy(alpha = 0.2f), CircleShape)
                            .border(1.5.dp, Color(0xFFEF4444), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Inscription Interdite",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Inscription Interdite",
                        color = Color(0xFFEF4444),
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Compte Déjà Existant !",
                        color = Color.White,
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = Color(0xFF7F1D1D).copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = existingAccModalMsg,
                            color = Color(0xFFFECACA),
                            fontFamily = MulishFontFamily,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(14.dp),
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.clearExistingAccountErrorModal()
                            viewModel.navigateToLoginWithPhone(uiState.fullPhone)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Se Connecter avec ce Numéro",
                            color = Color.White,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { viewModel.clearExistingAccountErrorModal() }
                    ) {
                        Text(
                            text = "Modifier le numéro de téléphone",
                            color = Color(0xFF94A3B8),
                            fontFamily = MulishFontFamily,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // If Registration Successful -> Display Official Success Screen
    if (uiState.authStep == AuthStep.REGISTER_SUCCESS) {
        RegistrationSuccessView(
            fullName = uiState.registrationSuccessFullName ?: "$firstName $lastName".trim(),
            walletId = uiState.registrationSuccessWalletId ?: "CD0100000001",
            phone = uiState.registrationSuccessPhone ?: uiState.fullPhone,
            onGoToLogin = { viewModel.finishRegistrationToLogin() }
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ToofanBgColor)
    ) {
        // Full screen vivid background 100% visible
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
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (regStep > 0) regStep-- else viewModel.navigateBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.White
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.cashpay_logo_white),
                    contentDescription = "CashPay Logo",
                    modifier = Modifier.height(44.dp)
                )

                Surface(
                    color = ToofanGreen.copy(alpha = 0.20f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ToofanGreen.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Étape ${regStep + 1} / 5",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = ToofanGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step Progress Indicator Bars (5 Steps)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in 0..4) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when {
                                    i == regStep -> ToofanGreen
                                    i < regStep -> ToofanGreen.copy(alpha = 0.5f)
                                    else -> Color.White.copy(alpha = 0.2f)
                                }
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Error banner if any
            uiState.errorMessage?.let { errorMsg ->
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    Text(
                        text = errorMsg,
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Main White Card in Exact Toofan Design
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = ToofanWhite,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    AnimatedContent(
                        targetState = regStep,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "reg_step_content"
                    ) { targetStep ->
                        when (targetStep) {
                            0 -> {
                                // STEP 1: Type de compte, Pays, Téléphone et Vérification OTP (SMS / WhatsApp)
                                Column {
                                    Text(
                                        text = "1. Numéro de Téléphone & OTP",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = ToofanMainDark
                                    )
                                    Text(
                                        text = "Vérifiez d'abord votre numéro pour autoriser votre pays et débloquer la suite :",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 13.sp,
                                        color = ToofanBodyText,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                                    )

                                    // Account Type Selector (National, Diaspora, Business)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Type de compte *",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = ToofanMainDark
                                        )
                                        IconButton(
                                            onClick = {
                                                activeInfo = InfoContent(
                                                    title = "Type de Compte",
                                                    role = "Définit la portée juridique, les plafonds de transfert et les services adaptés à votre profil.",
                                                    rules = "National : pour résidents locaux. Diaspora : pour les non-résidents ou expatriés. Business : pour commerces et personnes morales.",
                                                    faq = "Puis-je changer plus tard ? Oui, vous pouvez faire évoluer votre compte vers Business sur présentation des statuts d'entreprise."
                                                )
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Info,
                                                contentDescription = "Info",
                                                tint = ToofanGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        AccountTypeOption("National", "national", accountType, modifier = Modifier.weight(1f)) { accountType = it }
                                        AccountTypeOption("Diaspora", "diaspora", accountType, modifier = Modifier.weight(1f)) { accountType = it }
                                        AccountTypeOption("Business", "business", accountType, modifier = Modifier.weight(1f)) { accountType = it }
                                    }

                                    if (accountType == "business") {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Nom légal de l'entreprise *",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = ToofanMainDark
                                            )
                                            IconButton(
                                                onClick = {
                                                    activeInfo = InfoContent(
                                                        title = "Nom de l'Entreprise",
                                                        role = "Raison sociale exacte qui figurera sur vos factures de paiement et QR Codes marchands.",
                                                        rules = "Indiquez la raison sociale enregistrée au registre de commerce / RCCM.",
                                                        faq = "Est-ce visible par les clients ? Oui, lors de chaque paiement ou transfert."
                                                    )
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Info,
                                                    contentDescription = "Info",
                                                    tint = ToofanGreen,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        ToofanInputField(
                                            value = companyName,
                                            onValueChange = { companyName = it },
                                            placeholder = "Ex: FINUSECO SA / Boutique Express",
                                            testTag = "input_company_name"
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // 1. Country Selection (Exact same component as Login)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Pays de résidence *",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = ToofanMainDark
                                        )
                                        IconButton(
                                            onClick = {
                                                activeInfo = InfoContent(
                                                    title = "Sélection du Pays",
                                                    role = "Définit votre indicatif international et la devise nationale principale rattachée à votre compte.",
                                                    rules = "Touchez le sélecteur pour choisir parmi tous les pays supportés par CashPay.",
                                                    faq = "L'indicatif est-il automatiquement ajouté ? Oui, le préfixe téléphonique s'adapte immédiatement."
                                                )
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Info,
                                                contentDescription = "Info",
                                                tint = ToofanGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.dp, ToofanGrey1.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                                            .clickable { viewModel.setCountryPickerOpen(true) }
                                            .testTag("register_country_picker_button"),
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

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // 2. Phone Number Input (Exact same component as Login)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Numéro de téléphone portable *",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = ToofanMainDark
                                        )
                                        IconButton(
                                            onClick = {
                                                activeInfo = InfoContent(
                                                    title = "Numéro de Téléphone",
                                                    role = "Votre identifiant de portefeuille principal CashPay pour recevoir de l'argent et autoriser vos transactions.",
                                                    rules = "Saisissez les 9 chiffres de votre numéro sans le zéro initial (ex: 812345678).",
                                                    faq = "Pourquoi vérifier par OTP ? Pour garantir que vous êtes le détenteur légitime de la carte SIM."
                                                )
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Info,
                                                contentDescription = "Info",
                                                tint = ToofanGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

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
                                        testTag = "register_phone_input_field"
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Numéro complet : ${uiState.fullPhone}",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        color = ToofanBodyText
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // 3. OTP Verification Section (Strictly SMS / WhatsApp only, NO EMAIL)
                                    Surface(
                                        color = if (uiState.isPhoneVerified) ToofanGreen.copy(alpha = 0.08f) else ToofanBgColor,
                                        shape = RoundedCornerShape(14.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (uiState.isPhoneVerified) ToofanGreen else ToofanGrey1.copy(alpha = 0.6f)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            if (uiState.isPhoneVerified) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = "Vérifié", tint = ToofanGreen)
                                                    Text(
                                                        text = "Numéro de téléphone vérifié par OTP avec succès !",
                                                        fontFamily = MulishFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = ToofanGreen
                                                    )
                                                }
                                            } else {
                                                // Channel selection: SMS vs WhatsApp
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Canal de réception du code OTP :",
                                                        fontFamily = MulishFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = ToofanMainDark
                                                    )
                                                    IconButton(
                                                        onClick = {
                                                            activeInfo = InfoContent(
                                                                title = "Canal de Réception OTP",
                                                                role = "Permet de recevoir le code d'activation temporaire soit par SMS classique, soit directement par WhatsApp.",
                                                                rules = "L'e-mail n'est pas autorisé pour certifier une ligne téléphonique cliente.",
                                                                faq = "Si vous n'avez pas de réseau cellulaire SMS, choisissez 'WhatsApp Direct' pour recevoir le code par internet."
                                                            )
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Outlined.Info,
                                                            contentDescription = "Info",
                                                            tint = ToofanGreen,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    OtpChannelCard(
                                                        title = "SMS sécurisé",
                                                        channel = "sms",
                                                        icon = Icons.Default.Sms,
                                                        selected = selectedOtpChannel == "sms",
                                                        modifier = Modifier.weight(1f)
                                                    ) { selectedOtpChannel = "sms" }

                                                    OtpChannelCard(
                                                        title = "WhatsApp Direct",
                                                        channel = "whatsapp",
                                                        icon = Icons.Default.Chat,
                                                        selected = selectedOtpChannel == "whatsapp",
                                                        modifier = Modifier.weight(1f)
                                                    ) { selectedOtpChannel = "whatsapp" }
                                                }

                                                Spacer(modifier = Modifier.height(14.dp))

                                                ToofanButton(
                                                    title = if (uiState.isPhoneOtpSending) "Envoi en cours..." else "Envoyer le code OTP",
                                                    onClick = {
                                                        viewModel.sendRegisterPhoneOtp(
                                                            phone = uiState.fullPhone,
                                                            accountType = accountType,
                                                            channel = selectedOtpChannel
                                                        )
                                                    },
                                                    enabled = uiState.isPhoneValid && !uiState.isPhoneOtpSending,
                                                    isLoading = uiState.isPhoneOtpSending,
                                                    testTag = "send_register_otp_btn"
                                                )

                                                uiState.phoneOtpSuccess?.let {
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Text(
                                                        text = it,
                                                        fontSize = 12.sp,
                                                        color = ToofanGreen,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontFamily = MulishFontFamily
                                                    )
                                                    Spacer(modifier = Modifier.height(10.dp))

                                                    ToofanInputField(
                                                        value = phoneOtpInput,
                                                        onValueChange = {
                                                            val digits = it.filter { ch -> ch.isDigit() }.take(6)
                                                            phoneOtpInput = digits
                                                            if (digits.length == 6) {
                                                                viewModel.verifyRegisterPhoneOtp(
                                                                    phone = uiState.fullPhone,
                                                                    countryCode = uiState.selectedCountry.code,
                                                                    country = uiState.selectedCountry.name,
                                                                    otp = digits
                                                                )
                                                            }
                                                        },
                                                        placeholder = "Code OTP à 6 chiffres",
                                                        leadingIcon = {
                                                            Icon(Icons.Default.Security, contentDescription = null, tint = ToofanGreen)
                                                        },
                                                        trailingIcon = {
                                                            Text(
                                                                text = "${phoneOtpInput.length}/6",
                                                                fontSize = 12.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (phoneOtpInput.length == 6) ToofanGreen else ToofanBodyText
                                                            )
                                                        },
                                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                        testTag = "register_otp_code_input"
                                                    )

                                                    Spacer(modifier = Modifier.height(10.dp))

                                                    ToofanButton(
                                                        title = "Valider le code OTP",
                                                        onClick = {
                                                            viewModel.verifyRegisterPhoneOtp(
                                                                phone = uiState.fullPhone,
                                                                countryCode = uiState.selectedCountry.code,
                                                                country = uiState.selectedCountry.name,
                                                                otp = phoneOtpInput
                                                            )
                                                        },
                                                        enabled = phoneOtpInput.length == 6 && !uiState.isPhoneOtpVerifying,
                                                        isLoading = uiState.isPhoneOtpVerifying,
                                                        containerColor = ToofanGreen,
                                                        testTag = "validate_register_otp_btn"
                                                    )
                                                }

                                                uiState.phoneOtpError?.let {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = it,
                                                        fontSize = 12.sp,
                                                        color = Color(0xFFEF4444),
                                                        fontFamily = MulishFontFamily
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Preferred language
                                    SelectDropdownField(
                                        label = "Langue de l'interface",
                                        selectedKey = language,
                                        options = languageOptions,
                                        onOptionSelected = { language = it },
                                        onInfoClick = {
                                            activeInfo = InfoContent(
                                                title = "Langue de l'Interface",
                                                role = "Langue utilisée pour les notifications, reçus de transfert et l'assistant CashPay.",
                                                rules = "Choisissez parmi les langues supportées (Français, English, Swahili, Lingala...)."
                                            )
                                        }
                                    )
                                }
                            }

                            1 -> {
                                // STEP 2: Identité Personnelle
                                Column {
                                    Text(
                                        text = "2. Identité Personnelle",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = ToofanMainDark
                                    )
                                    Text(
                                        text = "Renseignez vos informations d'état civil officielles :",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 13.sp,
                                        color = ToofanBodyText,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                                    )

                                    FieldWithInfoLabel(title = "Prénom *", onInfoClick = {
                                        activeInfo = InfoContent(
                                            title = "Prénom",
                                            role = "Votre premier prénom officiel tel qu'inscrit sur votre carte d'identité ou passeport.",
                                            rules = "Sans abréviation, caractères alphabétiques uniquement."
                                        )
                                    })
                                    ToofanInputField(value = firstName, onValueChange = { firstName = it }, placeholder = "Ex: Patrick")

                                    Spacer(modifier = Modifier.height(10.dp))

                                    FieldWithInfoLabel(title = "Deuxième prénom (Post-nom)", onInfoClick = {
                                        activeInfo = InfoContent(
                                            title = "Post-nom",
                                            role = "Deuxième prénom ou post-nom selon vos documents d'état civil.",
                                            rules = "Optionnel si vous n'en possédez pas."
                                        )
                                    })
                                    ToofanInputField(value = middleName, onValueChange = { middleName = it }, placeholder = "Ex: Lubanda")

                                    Spacer(modifier = Modifier.height(10.dp))

                                    FieldWithInfoLabel(title = "Nom de famille *", onInfoClick = {
                                        activeInfo = InfoContent(
                                            title = "Nom de Famille",
                                            role = "Nom patronymique officiel rattaché à votre compte bancaire / portefeuille.",
                                            rules = "Conforme à la pièce d'identité."
                                        )
                                    })
                                    ToofanInputField(value = lastName, onValueChange = { lastName = it }, placeholder = "Ex: Mpoyi")

                                    Spacer(modifier = Modifier.height(10.dp))

                                    SelectDropdownField(
                                        label = "Genre",
                                        selectedKey = gender,
                                        options = genderOptions,
                                        onOptionSelected = { gender = it },
                                        onInfoClick = {
                                            activeInfo = InfoContent(
                                                title = "Genre",
                                                role = "Information d'identification civile requise pour la conformité bancaire."
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    FieldWithInfoLabel(title = "Date de naissance (AAAA-MM-JJ)", onInfoClick = {
                                        activeInfo = InfoContent(
                                            title = "Date de Naissance",
                                            role = "Permet de vérifier la majorité légale pour l'ouverture d'un compte financier.",
                                            rules = "Format attendu : AAAA-MM-JJ (ex: 1990-05-14)."
                                        )
                                    })
                                    ToofanInputField(value = birthDate, onValueChange = { birthDate = it }, placeholder = "1990-05-14")

                                    Spacer(modifier = Modifier.height(10.dp))

                                    SelectDropdownField(
                                        label = "État civil",
                                        selectedKey = maritalStatus,
                                        options = maritalOptions,
                                        onOptionSelected = { maritalStatus = it }
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    FieldWithInfoLabel(title = "Profession / Activité", onInfoClick = {
                                        activeInfo = InfoContent(
                                            title = "Profession",
                                            role = "Nature de votre activité professionnelle principale (commerçant, ingénieur, consultant, étudiant...)."
                                        )
                                    })
                                    ToofanInputField(value = profession, onValueChange = { profession = it }, placeholder = "Ex: Commerçant / Ingénieur")

                                    Spacer(modifier = Modifier.height(10.dp))

                                    FieldWithInfoLabel(title = "Adresse e-mail (Optionnelle)", onInfoClick = {
                                        activeInfo = InfoContent(
                                            title = "Adresse E-mail",
                                            role = "Pour recevoir vos relevés mensuels au format PDF et les alertes de connexion.",
                                            rules = "Optionnelle. La vérification principale s'effectue par téléphone/WhatsApp."
                                        )
                                    })
                                    ToofanInputField(value = emailOptional, onValueChange = { emailOptional = it }, placeholder = "contact@example.com")
                                }
                            }

                            2 -> {
                                // STEP 3: Localisation & Résidence
                                Column {
                                    Text(
                                        text = "3. Localisation & Résidence",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = ToofanMainDark
                                    )
                                    Text(
                                        text = "Indiquez votre adresse de résidence actuelle :",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 13.sp,
                                        color = ToofanBodyText,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                                    )

                                    FieldWithInfoLabel(title = "Ville *", onInfoClick = {
                                        activeInfo = InfoContent(
                                            title = "Ville de Résidence",
                                            role = "Permet de situer les points de retrait et agences CashPay à proximité.",
                                            rules = "Nom officiel de votre ville ou commune."
                                        )
                                    })
                                    ToofanInputField(value = city, onValueChange = { city = it }, placeholder = "Ex: Kinshasa, Lubumbashi, Paris...")

                                    Spacer(modifier = Modifier.height(12.dp))

                                    FieldWithInfoLabel(title = "Adresse physique complète *", onInfoClick = {
                                        activeInfo = InfoContent(
                                            title = "Adresse Physique",
                                            role = "Adresse de domiciliation physique nécessaire à la vérification KYC.",
                                            rules = "Numéro, Avenue, Quartier ou Rue."
                                        )
                                    })
                                    ToofanInputField(value = address, onValueChange = { address = it }, placeholder = "Ex: 12 Av. de la Paix, Gombe")
                                }
                            }

                            3 -> {
                                // STEP 4: Pièce d'Identité & Documents
                                Column {
                                    Text(
                                        text = "4. Pièce d'Identité & Sécurité",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = ToofanMainDark
                                    )
                                    Text(
                                        text = "Certifiez votre identité pour sécuriser votre compte :",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 13.sp,
                                        color = ToofanBodyText,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                                    )

                                    SelectDropdownField(
                                        label = "Type de pièce officielle",
                                        selectedKey = idType,
                                        options = idTypeOptions,
                                        onOptionSelected = { idType = it },
                                        onInfoClick = {
                                            activeInfo = InfoContent(
                                                title = "Type de Pièce d'Identité",
                                                role = "Document officiel émis par l'État pour certifier votre identité.",
                                                rules = "Passeport, Carte nationale, Permis de conduire ou Carte d'électeur."
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    FieldWithInfoLabel(title = "Numéro de la pièce *", onInfoClick = {
                                        activeInfo = InfoContent(
                                            title = "Numéro de Pièce",
                                            role = "Identifiant unique inscrit sur votre document officiel.",
                                            rules = "Saisissez les chiffres et lettres exacts sans espaces superflus."
                                        )
                                    })
                                    ToofanInputField(value = idNumber, onValueChange = { idNumber = it }, placeholder = "Ex: AB1234567")

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "Photos justificatives (Recommandé) :",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ToofanMainDark
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        CameraCaptureTile(
                                            title = "Photo Pièce",
                                            hasPhoto = idFrontBase64 != null,
                                            icon = Icons.Default.CameraAlt,
                                            modifier = Modifier.weight(1f),
                                            onClick = { activeCameraMode = CameraCaptureMode.ID_DOCUMENT_FRONT }
                                        )

                                        CameraCaptureTile(
                                            title = "Selfie Portrait",
                                            hasPhoto = profilePhotoBase64 != null,
                                            icon = Icons.Default.Face,
                                            modifier = Modifier.weight(1f),
                                            onClick = { activeCameraMode = CameraCaptureMode.SELFIE_PROFILE }
                                        )
                                    }
                                }
                            }

                            4 -> {
                                // STEP 5: Code PIN Secret & Finalisation
                                Column {
                                    Text(
                                        text = "5. Code PIN & Finalisation",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = ToofanMainDark
                                    )
                                    Text(
                                        text = "Créez votre code PIN secret et signez pour ouvrir votre compte :",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 13.sp,
                                        color = ToofanBodyText,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                                    )

                                    FieldWithInfoLabel(title = "Code PIN Secret (4 à 6 chiffres) *", onInfoClick = {
                                        activeInfo = InfoContent(
                                            title = "Code PIN Secret",
                                            role = "Votre clé de sécurité personnelle exigée pour valider les virements, paiements et retraits.",
                                            rules = "4 à 6 chiffres confidentiels. Ne le partagez jamais.",
                                            faq = "Que faire si je l'oublie ? Vous pourrez le réinitialiser avec votre numéro et OTP."
                                        )
                                    })
                                    ToofanInputField(
                                        value = pinCode,
                                        onValueChange = { pinCode = it.filter { ch -> ch.isDigit() }.take(6) },
                                        placeholder = "••••",
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    FieldWithInfoLabel(title = "Confirmer le Code PIN *", onInfoClick = {
                                        activeInfo = InfoContent(
                                            title = "Confirmation du PIN",
                                            role = "Vérifie que vous n'avez pas commis de faute de frappe."
                                        )
                                    })
                                    ToofanInputField(
                                        value = pinConfirm,
                                        onValueChange = { pinConfirm = it.filter { ch -> ch.isDigit() }.take(6) },
                                        placeholder = "••••",
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                                    )

                                    if (pinCode.isNotEmpty() && pinConfirm.isNotEmpty() && pinCode != pinConfirm) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Les codes PIN ne correspondent pas.",
                                            fontSize = 12.sp,
                                            color = Color(0xFFEF4444),
                                            fontFamily = MulishFontFamily
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "Signature manuscrite :",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ToofanMainDark
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Signature Drawing Pad
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.dp, ToofanGrey1.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
                                        color = Color(0xFFFAFAFA)
                                    ) {
                                        Box {
                                            Canvas(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .pointerDragGestures(
                                                        onDragStart = { offset ->
                                                            currentPath = listOf(offset)
                                                        },
                                                        onDrag = { change, _ ->
                                                            change.consume()
                                                            currentPath = currentPath + change.position
                                                        },
                                                        onDragEnd = {
                                                            if (currentPath.isNotEmpty()) {
                                                                signaturePaths.add(currentPath)
                                                                currentPath = emptyList()
                                                                signatureBase64 = exportPathsToBase64(signaturePaths)
                                                            }
                                                        }
                                                    )
                                            ) {
                                                signaturePaths.forEach { pathPoints ->
                                                    if (pathPoints.size > 1) {
                                                        val path = Path().apply {
                                                            moveTo(pathPoints.first().x, pathPoints.first().y)
                                                            for (p in pathPoints.drop(1)) {
                                                                lineTo(p.x, p.y)
                                                            }
                                                        }
                                                        drawPath(
                                                            path = path,
                                                            color = ToofanMainDark,
                                                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                                        )
                                                    }
                                                }

                                                if (currentPath.size > 1) {
                                                    val path = Path().apply {
                                                        moveTo(currentPath.first().x, currentPath.first().y)
                                                        for (p in currentPath.drop(1)) {
                                                            lineTo(p.x, p.y)
                                                        }
                                                    }
                                                    drawPath(
                                                        path = path,
                                                        color = ToofanGreen,
                                                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                                    )
                                                }
                                            }

                                            if (signaturePaths.isEmpty() && currentPath.isEmpty()) {
                                                Text(
                                                    text = "Signez ici avec votre doigt",
                                                    fontFamily = MulishFontFamily,
                                                    fontSize = 12.sp,
                                                    color = ToofanBodyText.copy(alpha = 0.5f),
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }

                                            if (signaturePaths.isNotEmpty()) {
                                                IconButton(
                                                    onClick = {
                                                        signaturePaths.clear()
                                                        currentPath = emptyList()
                                                        signatureBase64 = null
                                                    },
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(4.dp)
                                                        .size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Effacer",
                                                        tint = Color(0xFFEF4444),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Summary overview
                                    Surface(
                                        color = ToofanBgColor,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            SummaryRow("Nom complet", "$firstName ${middleName.ifBlank { "" }} $lastName".trim())
                                            SummaryRow("Téléphone", "${uiState.selectedCountry.dialCode} ${uiState.localPhone}")
                                            SummaryRow("Type de compte", accountType.replaceFirstChar { it.uppercase() })
                                            SummaryRow("Ville", city)
                                            SummaryRow("Pièce d'identité", idNumber.ifBlank { "Renseignée" })
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    val isPinValid = pinCode.length >= 4 && pinCode == pinConfirm
                                    ToofanButton(
                                        title = if (uiState.isRegisterLoading) "Création en cours..." else "Créer mon Compte CashPay",
                                        onClick = {
                                            viewModel.submitRegistration(
                                                accountType = accountType,
                                                firstName = firstName,
                                                middleName = middleName,
                                                lastName = lastName,
                                                gender = gender,
                                                birthDate = birthDate,
                                                maritalStatus = maritalStatus,
                                                nationality = nationality,
                                                city = city,
                                                address = address,
                                                profession = profession,
                                                idType = idType,
                                                idNumber = idNumber,
                                                companyName = companyName,
                                                idFrontBase64 = idFrontBase64,
                                                profilePhotoBase64 = profilePhotoBase64,
                                                signatureBase64 = signatureBase64,
                                                language = language
                                            )
                                        },
                                        enabled = isPinValid && !uiState.isRegisterLoading,
                                        isLoading = uiState.isRegisterLoading,
                                        testTag = "submit_registration_button"
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Navigation Button for intermediate steps (0 to 3)
                    if (regStep < 4) {
                        val canProceed = when (regStep) {
                            0 -> uiState.isPhoneVerified
                            1 -> firstName.isNotBlank() && lastName.isNotBlank()
                            2 -> city.isNotBlank() && address.isNotBlank()
                            3 -> idNumber.isNotBlank() && !(idNumber.startsWith("0000") || idNumber == "123456789")
                            else -> true
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        if (!canProceed) {
                            val hintText = when (regStep) {
                                0 -> "Veuillez valider le code OTP envoyé à votre numéro pour continuer."
                                1 -> "Veuillez renseigner votre Prénom et votre Nom de famille."
                                2 -> "Veuillez indiquer votre Ville et votre Adresse physique."
                                3 -> "Veuillez entrer un numéro de pièce d'identité valide."
                                else -> "Veuillez compléter les informations requises."
                            }
                            Text(
                                text = hintText,
                                fontFamily = MulishFontFamily,
                                fontSize = 12.sp,
                                color = Color(0xFFE11D48),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            )
                        }

                        ToofanButton(
                            title = "Étape Suivante ➔",
                            onClick = {
                                if (canProceed && regStep < 4) regStep++
                            },
                            enabled = canProceed,
                            testTag = "next_step_button"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "CashPay • Propulsé par FINUSECO SA",
                fontFamily = MulishFontFamily,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun OtpChannelCard(
    title: String,
    channel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .border(
                width = if (selected) 1.8.dp else 1.dp,
                color = if (selected) ToofanGreen else ToofanGrey1.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp)
            ),
        color = if (selected) ToofanGreen.copy(alpha = 0.08f) else ToofanWhite
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected) ToofanGreen else ToofanBodyText,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                fontFamily = MulishFontFamily,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) ToofanGreen else ToofanMainDark
            )
        }
    }
}

@Composable
private fun FieldWithInfoLabel(
    title: String,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = ToofanMainDark
        )
        IconButton(
            onClick = onInfoClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Info",
                tint = ToofanGreen,
                modifier = Modifier.size(18.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun FieldInfoDialog(
    content: InfoContent,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("J'ai compris", color = ToofanGreen, fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = ToofanGreen
                )
                Text(
                    text = content.title,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ToofanMainDark
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🎯 Rôle & Importance :",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ToofanMainDark
                )
                Text(
                    text = content.role,
                    fontSize = 13.sp,
                    color = ToofanBodyText,
                    lineHeight = 18.sp
                )
                Text(
                    text = "📋 Règles & Format attendu :",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ToofanMainDark
                )
                Text(
                    text = content.rules,
                    fontSize = 13.sp,
                    color = ToofanBodyText,
                    lineHeight = 18.sp
                )
                content.faq?.let {
                    Text(
                        text = "💡 Question fréquente :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ToofanMainDark
                    )
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = ToofanBodyText,
                        lineHeight = 18.sp
                    )
                }
            }
        },
        shape = RoundedCornerShape(18.dp),
        containerColor = ToofanWhite
    )
}

@Composable
private fun CameraCaptureTile(
    title: String,
    hasPhoto: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(
                1.dp,
                if (hasPhoto) ToofanGreen else ToofanGrey1.copy(alpha = 0.8f),
                RoundedCornerShape(12.dp)
            ),
        color = if (hasPhoto) ToofanGreen.copy(alpha = 0.08f) else ToofanBgColor
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (hasPhoto) Icons.Default.Check else icon,
                contentDescription = title,
                tint = if (hasPhoto) ToofanGreen else ToofanBodyText,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (hasPhoto) "$title ✓" else title,
                fontFamily = MulishFontFamily,
                fontSize = 12.sp,
                fontWeight = if (hasPhoto) FontWeight.Bold else FontWeight.Medium,
                color = if (hasPhoto) ToofanGreen else ToofanMainDark,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontFamily = MulishFontFamily, fontSize = 12.sp, color = ToofanBodyText)
        Text(text = value, fontFamily = MulishFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ToofanMainDark)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectDropdownField(
    label: String,
    selectedKey: String,
    options: List<SelectOption>,
    onOptionSelected: (String) -> Unit,
    onInfoClick: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLabel = options.find { it.key == selectedKey }?.label ?: selectedKey

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ToofanMainDark
            )
            if (onInfoClick != null) {
                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        tint = ToofanGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, ToofanGrey1.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                    .menuAnchor(),
                color = ToofanWhite
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentLabel,
                        fontFamily = MulishFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ToofanMainDark
                    )
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt.label, fontFamily = MulishFontFamily, fontWeight = FontWeight.Medium) },
                        onClick = {
                            onOptionSelected(opt.key)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountTypeOption(
    label: String,
    typeValue: String,
    selectedType: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit
) {
    val isSelected = selectedType == typeValue
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onSelect(typeValue) }
            .border(
                1.5.dp,
                if (isSelected) ToofanGreen else ToofanGrey1.copy(alpha = 0.6f),
                RoundedCornerShape(10.dp)
            ),
        color = if (isSelected) ToofanGreen.copy(alpha = 0.10f) else ToofanWhite
    ) {
        Text(
            text = label,
            fontFamily = MulishFontFamily,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ToofanGreen else ToofanMainDark,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
        )
    }
}

private fun Modifier.pointerDragGestures(
    onDragStart: (Offset) -> Unit,
    onDrag: (PointerInputChange, Offset) -> Unit,
    onDragEnd: () -> Unit
): Modifier = this.pointerInput(Unit) {
    detectDragGestures(
        onDragStart = { offset -> onDragStart(offset) },
        onDragEnd = { onDragEnd() },
        onDragCancel = { onDragEnd() },
        onDrag = { change, dragAmount -> onDrag(change, dragAmount) }
    )
}

private fun exportPathsToBase64(paths: List<List<Offset>>): String? {
    if (paths.isEmpty()) return null
    return try {
        val width = 400
        val height = 200
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)

        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 4f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }

        paths.forEach { points ->
            if (points.size > 1) {
                val p = android.graphics.Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (pt in points.drop(1)) {
                        lineTo(pt.x, pt.y)
                    }
                }
                canvas.drawPath(p, paint)
            }
        }

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val byteArray = stream.toByteArray()
        val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        "data:image/png;base64,$base64"
    } catch (_: Exception) {
        null
    }
}

@Composable
fun RegistrationSuccessView(
    fullName: String,
    walletId: String,
    phone: String,
    onGoToLogin: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ToofanBgColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cashpay_logo_white),
                    contentDescription = "CashPay Logo",
                    modifier = Modifier.height(48.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Success check badge
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(ToofanGreen.copy(alpha = 0.18f), CircleShape)
                        .border(3.dp, ToofanGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Succès",
                        tint = ToofanGreen,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Compte créé avec succès !",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Bienvenue, $fullName",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ToofanGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Official Wallet ID Card returned by server
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0C1938),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ToofanGreen)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "VOTRE ID WALLET CASHPAY OFFICIEL",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = walletId,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (copied) ToofanGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.clickable {
                                clipboardManager.setText(AnnotatedString(walletId))
                                copied = true
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copier",
                                    tint = if (copied) ToofanGreen else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (copied) "ID copié dans le presse-papier" else "Copier mon ID Wallet",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (copied) ToofanGreen else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Color.White.copy(alpha = 0.07f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = ToofanGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Votre session a été initialisée. Vous pouvez maintenant vous connecter directement avec votre identifiant.",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.5.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ToofanButton(
                    title = "Accéder à la Connexion",
                    onClick = onGoToLogin,
                    testTag = "success_login_button"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "CashPay • Propulsé par FINUSECO SA",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
