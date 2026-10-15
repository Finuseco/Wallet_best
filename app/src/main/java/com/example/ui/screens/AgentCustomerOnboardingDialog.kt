package com.example.ui.screens

import android.graphics.Bitmap
import android.util.Base64
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.components.CameraCaptureMode
import com.example.ui.components.CashPayCameraCaptureDialog
import com.example.ui.theme.MulishFontFamily
import java.io.ByteArrayOutputStream

/**
 * Inscription Client complète en plusieurs étapes (1 à 6) depuis l'interface Agent.
 *
 * Utilise les données réelles du serveur :
 * - GET  /api/v1/agents/customers/options  (Pays officiel de l'Agent, provinces, types de compte)
 * - POST /api/v1/agents/customers/register (Validation et création finale)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentCustomerOnboardingDialog(
    isOpen: Boolean,
    optionsResponse: AgentCustomerOptionsResponse?,
    isLoadingOptions: Boolean,
    isRegistering: Boolean,
    registerSuccess: AgentRegisterCustomerResponse?,
    registerError: String?,
    onDismiss: () -> Unit,
    onSubmitRegister: (AgentRegisterCustomerRequest) -> Unit,
    onRetryOptions: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var currentStep by remember { mutableIntStateOf(1) } // 1..6

    // Options issues de l'API
    val provinces: List<AgentProvinceItem> = remember(optionsResponse) {
        optionsResponse?.getNormalizedProvinces() ?: emptyList()
    }
    val accountTypes: List<String> = remember(optionsResponse) {
        optionsResponse?.getAccountTypes() ?: listOf("national", "diaspora", "business")
    }
    val agentCountry = optionsResponse?.country
    val countryDialCode = agentCountry?.dialCode ?: "+243"
    val countryName = agentCountry?.name ?: "RD Congo"
    val countryCode = agentCountry?.code ?: "CD"

    // --- Étape 1 : Identité Personnelle ---
    var selectedAccountType by remember { mutableStateOf("national") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("male") }
    var maritalStatus by remember { mutableStateOf("single") }
    var birthPlace by remember { mutableStateOf("Kinshasa") }
    var nationality by remember { mutableStateOf("Congolaise") }
    var profession by remember { mutableStateOf("Commerçant") }
    var incomePerMonth by remember { mutableStateOf("300") }
    var birthDateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE) }

    // --- Étape 2 : Contact & Résidence ---
    var rawPhone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedProvince by remember { mutableStateOf<AgentProvinceItem?>(null) }
    var city by remember { mutableStateOf("Kinshasa") }
    var address by remember { mutableStateOf("") }
    var provinceMenuExpanded by remember { mutableStateOf(false) }

    // --- Étape 3 : Pièce d'Identité & Entreprise ---
    var idType by remember { mutableStateOf("national_id") }
    var idNumber by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var activityDescription by remember { mutableStateOf("") }

    // --- Étape 4 : Personne de Référence ---
    var repName by remember { mutableStateOf("") }
    var repContact by remember { mutableStateOf("") }
    var repRelation by remember { mutableStateOf("parent") }

    // --- Étape 5 : Captures & Signature ---
    var profilePhotoBase64 by remember { mutableStateOf("") }
    var idFrontBase64 by remember { mutableStateOf("") }
    var idBackBase64 by remember { mutableStateOf("") }
    var signatureBase64 by remember { mutableStateOf("") }
    var activeCameraMode by remember { mutableStateOf<CameraCaptureMode?>(null) }
    var showSignaturePad by remember { mutableStateOf(false) }

    // Auto-sélection par défaut si options chargées
    LaunchedEffect(accountTypes) {
        if (!accountTypes.contains(selectedAccountType) && accountTypes.isNotEmpty()) {
            selectedAccountType = accountTypes.first()
        }
    }
    LaunchedEffect(provinces) {
        if (selectedProvince == null && provinces.isNotEmpty()) {
            selectedProvince = provinces.first()
        }
    }

    // Modal Caméra
    activeCameraMode?.let { mode ->
        CashPayCameraCaptureDialog(
            mode = mode,
            onDismissRequest = { activeCameraMode = null },
            onImageCaptured = { b64 ->
                when (mode) {
                    CameraCaptureMode.SELFIE_PROFILE -> profilePhotoBase64 = b64
                    CameraCaptureMode.ID_DOCUMENT_FRONT -> idFrontBase64 = b64
                    CameraCaptureMode.ID_DOCUMENT_BACK -> idBackBase64 = b64
                }
                activeCameraMode = null
            }
        )
    }

    // Modal Signature Tactile
    if (showSignaturePad) {
        SignatureCaptureModal(
            onDismiss = { showSignaturePad = false },
            onSignatureConfirmed = { b64 ->
                signatureBase64 = b64
                showSignaturePad = false
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Surface(
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (currentStep > 1 && registerSuccess == null) {
                                    IconButton(
                                        onClick = { currentStep-- },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Précédent",
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Column {
                                    Text(
                                        text = "Création Compte Client",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (registerSuccess != null) "Compte créé avec succès"
                                        else "Étape $currentStep sur 6 • Espace Agent CashPay",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        color = Color(0xFF00E676)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fermer",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }

                        if (registerSuccess == null && !isLoadingOptions) {
                            Spacer(modifier = Modifier.height(10.dp))
                            // Barre de progression 6 segments
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(6) { index ->
                                    val stepNum = index + 1
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(
                                                if (stepNum <= currentStep) Color(0xFF00E676)
                                                else Color(0xFF334155)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF0F172A))
            ) {
                if (registerSuccess != null) {
                    // Écran de succès
                    CustomerRegistrationSuccessView(
                        response = registerSuccess,
                        onClose = onDismiss,
                        onCopy = { text, label ->
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, "$label copié !", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else if (isLoadingOptions) {
                    // Chargement des options
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF00E676),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Chargement des options de votre pays...",
                            color = Color.White,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Récupération des données depuis le serveur CashPay",
                            color = Color(0xFF94A3B8),
                            fontFamily = MulishFontFamily,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (optionsResponse == null && !registerError.isNullOrBlank()) {
                    // Erreur chargement options
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Impossible de charger les options",
                            color = Color.White,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = registerError,
                            color = Color(0xFFFECACA),
                            fontFamily = MulishFontFamily,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onRetryOptions,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Réessayer",
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontFamily = MulishFontFamily
                            )
                        }
                    }
                } else {
                    // Formulaire 6 étapes
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Bannière d'erreur si présente
                        AnimatedVisibility(visible = !registerError.isNullOrBlank()) {
                            Surface(
                                color = Color(0xFF7F1D1D).copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444)
                                    )
                                    Text(
                                        text = registerError ?: "",
                                        color = Color(0xFFFECACA),
                                        fontSize = 12.5.sp,
                                        fontFamily = MulishFontFamily,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        when (currentStep) {
                            1 -> {
                                // ----------------------------------------------------
                                // ÉTAPE 1 : IDENTITÉ PERSONNELLE
                                // ----------------------------------------------------
                                StepSectionTitle("1. Identité du Client", "Informations d'état civil du nouveau client.")

                                // Type de compte (options serveur)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Type de compte *",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.5.sp,
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        accountTypes.forEach { typeKey ->
                                            val isSelected = selectedAccountType.equals(typeKey, ignoreCase = true)
                                            val label = when (typeKey.lowercase()) {
                                                "national" -> "National"
                                                "diaspora" -> "Diaspora"
                                                "business" -> "Business"
                                                else -> typeKey.replaceFirstChar { it.uppercase() }
                                            }
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(42.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .border(
                                                        width = if (isSelected) 1.5.dp else 1.dp,
                                                        color = if (isSelected) Color(0xFF00E676) else Color(0xFF334155),
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .clickable { selectedAccountType = typeKey },
                                                color = if (isSelected) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFF1E293B)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = label,
                                                        color = if (isSelected) Color(0xFF00E676) else Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.5.sp,
                                                        fontFamily = MulishFontFamily
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                FormInputField("Prénom *", firstName, { firstName = it }, "Ex: Jean")
                                FormInputField("Nom de famille *", lastName, { lastName = it }, "Ex: Mukendi")
                                FormInputField("Post-nom (Optionnel)", middleName, { middleName = it }, "Ex: Ilunga")

                                // Genre
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Genre *", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        listOf("male" to "Homme", "female" to "Femme", "other" to "Autre").forEach { (code, label) ->
                                            val isSel = gender == code
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .border(1.dp, if (isSel) Color(0xFF00E676) else Color(0xFF334155), RoundedCornerShape(10.dp))
                                                    .clickable { gender = code },
                                                color = if (isSel) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFF1E293B)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(label, color = if (isSel) Color(0xFF00E676) else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                // État civil
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("État civil *", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        listOf("single" to "Célib.", "married" to "Marié(e)", "divorced" to "Divorcé(e)", "widowed" to "Veuf(ve)").forEach { (code, label) ->
                                            val isSel = maritalStatus == code
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .border(1.dp, if (isSel) Color(0xFF00E676) else Color(0xFF334155), RoundedCornerShape(10.dp))
                                                    .clickable { maritalStatus = code },
                                                color = if (isSel) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFF1E293B)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(label, color = if (isSel) Color(0xFF00E676) else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
                                                }
                                            }
                                        }
                                    }
                                }

                                 // Date de naissance
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Date de naissance *", color = Color(0xFFCBD5E1), fontSize = 12.5.sp, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                                    OutlinedTextField(
                                        value = birthDateMillis?.let { dateFormatter.format(Date(it)) } ?: "",
                                        onValueChange = {},
                                        readOnly = true,
                                        placeholder = { Text("Choisir une date", color = Color(0xFF64748B), fontSize = 13.5.sp, fontFamily = MulishFontFamily) },
                                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF00E676)) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { showDatePicker = true },
                                        enabled = false,
                                        colors = clientTextFieldColors(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                                
                                if (showDatePicker) {
                                    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = birthDateMillis)
                                    DatePickerDialog(
                                        onDismissRequest = { showDatePicker = false },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                birthDateMillis = datePickerState.selectedDateMillis
                                                showDatePicker = false
                                            }) { Text("OK") }
                                        },
                                        dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Annuler") } }
                                    ) {
                                        DatePicker(state = datePickerState)
                                    }
                                }
                                
                                FormInputField("Lieu de naissance *", birthPlace, { birthPlace = it }, "Kinshasa")
                                FormInputField("Nationalité *", nationality, { nationality = it }, "Congolaise")
                                FormInputField("Profession *", profession, { profession = it }, "Commerçant")
                                FormInputField("Revenu mensuel estimé (USD) *", incomePerMonth, { incomePerMonth = it }, "300", KeyboardType.Number)

                                NavigationButtonBar(
                                    nextLabel = "Suivant : Contact & Résidence",
                                    nextEnabled = firstName.isNotBlank() && lastName.isNotBlank() && birthDateMillis != null && birthPlace.isNotBlank(),
                                    onNext = { currentStep = 2 }
                                )
                            }

                            2 -> {
                                // ----------------------------------------------------
                                // ÉTAPE 2 : CONTACT & RÉSIDENCE (Pays de l'Agent & Provinces API)
                                // ----------------------------------------------------
                                StepSectionTitle("2. Contact & Résidence", "Coordonnées du client dans le pays de l'Agent.")

                                // Bannière Pays de l'Agent
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            color = Color(0xFF00E676).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Public,
                                                    contentDescription = null,
                                                    tint = Color(0xFF00E676),
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Pays : $countryName ($countryCode)",
                                                color = Color.White,
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "Indicatif officiel : $countryDialCode (Défini par votre compte Agent)",
                                                color = Color(0xFF94A3B8),
                                                fontFamily = MulishFontFamily,
                                                fontSize = 11.5.sp
                                            )
                                        }
                                    }
                                }

                                // Téléphone
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Numéro de Téléphone *",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.5.sp,
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                    OutlinedTextField(
                                        value = rawPhone,
                                        onValueChange = { input -> rawPhone = input.filter { it.isDigit() } },
                                        placeholder = {
                                            Text(
                                                text = "Ex: 833248614",
                                                color = Color(0xFF64748B),
                                                fontSize = 13.5.sp,
                                                fontFamily = MulishFontFamily
                                            )
                                        },
                                        leadingIcon = {
                                            Surface(
                                                color = Color(0xFF334155),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                                            ) {
                                                Text(
                                                    text = countryDialCode,
                                                    color = Color(0xFF00E676),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    fontFamily = MulishFontFamily,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = clientTextFieldColors(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Text(
                                        text = "Le serveur vérifie que le numéro appartient au pays de l'Agent",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.5.sp,
                                        fontFamily = MulishFontFamily
                                    )
                                }

                                FormInputField("Adresse Email (Optionnel)", email, { email = it }, "client@exemple.com", KeyboardType.Email, Icons.Default.Email)

                                // Province issue de l'API
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Province du Client *",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.5.sp,
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = provinceMenuExpanded,
                                        onExpandedChange = { provinceMenuExpanded = !provinceMenuExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedProvince?.name ?: "Sélectionner la province",
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinceMenuExpanded) },
                                            leadingIcon = {
                                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF00E676))
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            colors = clientTextFieldColors(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = provinceMenuExpanded,
                                            onDismissRequest = { provinceMenuExpanded = false },
                                            modifier = Modifier.background(Color(0xFF1E293B))
                                        ) {
                                            if (provinces.isEmpty()) {
                                                DropdownMenuItem(
                                                    text = { Text("Aucune province disponible", color = Color(0xFF94A3B8)) },
                                                    onClick = { provinceMenuExpanded = false }
                                                )
                                            } else {
                                                provinces.forEach { prov ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                text = prov.name,
                                                                color = Color.White,
                                                                fontWeight = if (selectedProvince?.code == prov.code) FontWeight.Bold else FontWeight.Normal
                                                            )
                                                        },
                                                        onClick = {
                                                            selectedProvince = prov
                                                            provinceMenuExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                FormInputField("Ville *", city, { city = it }, "Kinshasa")
                                FormInputField("Adresse physique / Quartier *", address, { address = it }, "Av. Kasa-Vubu N° 12, Gombe")

                                NavigationButtonBar(
                                    nextLabel = "Suivant : Pièce d'Identité",
                                    nextEnabled = rawPhone.isNotBlank() && selectedProvince != null && city.isNotBlank(),
                                    onPrev = { currentStep = 1 },
                                    onNext = { currentStep = 3 }
                                )
                            }

                            3 -> {
                                // ----------------------------------------------------
                                // ÉTAPE 3 : PIÈCE D'IDENTITÉ & ENTREPRISE
                                // ----------------------------------------------------
                                StepSectionTitle("3. Pièce d'Identité & Entreprise", "Document d'identification et activité du client.")

                                Text("Type de Pièce *", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("national_id" to "Carte Identité", "passport" to "Passeport", "driver_license" to "Permis").forEach { (code, label) ->
                                        val isSel = idType == code
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(1.dp, if (isSel) Color(0xFF00E676) else Color(0xFF334155), RoundedCornerShape(10.dp))
                                                .clickable { idType = code },
                                            color = if (isSel) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFF1E293B)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(label, color = if (isSel) Color(0xFF00E676) else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                            }
                                        }
                                    }
                                }

                                FormInputField("Numéro de la pièce *", idNumber, { idNumber = it }, "Ex: ID-98472918")

                                if (selectedAccountType.equals("business", ignoreCase = true)) {
                                    FormInputField("Nom Entreprise / Commercial *", businessName, { businessName = it }, "Ex: Éts Mukendi & Frères", leadingIcon = Icons.Default.Business)
                                    FormInputField("Activité Commerciale (Optionnel)", activityDescription, { activityDescription = it }, "Commerce général, import-export")
                                }

                                NavigationButtonBar(
                                    nextLabel = "Suivant : Personne de Référence",
                                    nextEnabled = idNumber.isNotBlank(),
                                    onPrev = { currentStep = 2 },
                                    onNext = { currentStep = 4 }
                                )
                            }

                            4 -> {
                                // ----------------------------------------------------
                                // ÉTAPE 4 : PERSONNE DE RÉFÉRENCE / REPRÉSENTANT
                                // ----------------------------------------------------
                                StepSectionTitle("4. Personne de Référence", "Contact d'un proche ou représentant de confiance.")

                                FormInputField("Nom complet du contact de référence *", repName, { repName = it }, "Ex: Marie Mukendi")
                                FormInputField("Téléphone de la référence *", repContact, { repContact = it }, "Ex: 243812345678", KeyboardType.Phone)

                                Text("Lien de parenté / Relation *", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("parent" to "Parent", "spouse" to "Conjoint", "sibling" to "Frère/Sœur", "friend" to "Proche").forEach { (code, label) ->
                                        val isSel = repRelation == code
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(1.dp, if (isSel) Color(0xFF00E676) else Color(0xFF334155), RoundedCornerShape(10.dp))
                                                .clickable { repRelation = code },
                                            color = if (isSel) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFF1E293B)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(label, color = if (isSel) Color(0xFF00E676) else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                            }
                                        }
                                    }
                                }

                                NavigationButtonBar(
                                    nextLabel = "Suivant : Captures & Signature",
                                    nextEnabled = repName.isNotBlank() && repContact.isNotBlank(),
                                    onPrev = { currentStep = 3 },
                                    onNext = { currentStep = 5 }
                                )
                            }

                            5 -> {
                                // ----------------------------------------------------
                                // ÉTAPE 5 : CAPTURES & SIGNATURE TACTILE
                                // ----------------------------------------------------
                                StepSectionTitle("5. Captures & Signature", "Photos de la pièce, portrait et signature du client.")

                                CaptureItemCard(
                                    title = "Photo de profil du Client",
                                    description = "Portrait net du client",
                                    isCaptured = profilePhotoBase64.isNotBlank(),
                                    icon = Icons.Default.Person,
                                    onAction = { activeCameraMode = CameraCaptureMode.SELFIE_PROFILE }
                                )

                                CaptureItemCard(
                                    title = "Pièce d'Identité (Recto)",
                                    description = "Face avant claire avec texte lisible",
                                    isCaptured = idFrontBase64.isNotBlank(),
                                    icon = Icons.Default.CreditCard,
                                    onAction = { activeCameraMode = CameraCaptureMode.ID_DOCUMENT_FRONT }
                                )

                                CaptureItemCard(
                                    title = "Pièce d'Identité (Verso - Optionnel)",
                                    description = "Face arrière du document",
                                    isCaptured = idBackBase64.isNotBlank(),
                                    icon = Icons.Default.CreditCard,
                                    onAction = { activeCameraMode = CameraCaptureMode.ID_DOCUMENT_BACK }
                                )

                                CaptureItemCard(
                                    title = "Signature tactile du Client",
                                    description = "Signature apposée sur l'écran tactile",
                                    isCaptured = signatureBase64.isNotBlank(),
                                    icon = Icons.Default.Draw,
                                    onAction = { showSignaturePad = true }
                                )

                                NavigationButtonBar(
                                    nextLabel = "Suivant : Récapitulatif & Validation",
                                    nextEnabled = true, // Permettre d'avancer au récapitulatif
                                    onPrev = { currentStep = 4 },
                                    onNext = { currentStep = 6 }
                                )
                            }

                            6 -> {
                                // ----------------------------------------------------
                                // ÉTAPE 6 : RÉCAPITULATIF FINAL & BOUTON DE CRÉATION
                                // ----------------------------------------------------
                                StepSectionTitle("6. Vérification & Validation", "Vérifiez attentivement les données avant de lancer la création.")

                                val computedFullName = if (selectedAccountType.equals("business", ignoreCase = true) && businessName.isNotBlank()) {
                                    businessName.trim()
                                } else {
                                    listOfNotNull(firstName.trim(), middleName.trim().ifBlank { null }, lastName.trim()).joinToString(" ")
                                }

                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        SummaryItemRow("Type de compte", selectedAccountType.uppercase())
                                        SummaryItemRow("Nom complet", computedFullName)
                                        SummaryItemRow("Téléphone", "$countryDialCode $rawPhone")
                                        SummaryItemRow("Pays & Province", "$countryName • ${selectedProvince?.name ?: "Non définie"}")
                                        SummaryItemRow("Ville & Adresse", "$city, $address")
                                        SummaryItemRow("Pièce d'identité", "$idType : $idNumber")
                                        SummaryItemRow("Référence", "$repName ($repRelation)")
                                        SummaryItemRow("Photo profil", if (profilePhotoBase64.isNotBlank()) "Capturée ✔" else "Non fournie")
                                        SummaryItemRow("Pièce Recto", if (idFrontBase64.isNotBlank()) "Capturée ✔" else "Non fournie")
                                        SummaryItemRow("Signature", if (signatureBase64.isNotBlank()) "Signée ✔" else "Non fournie")
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Bouton Précédent & Bouton FINAL DE CRÉATION
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { currentStep = 5 },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Modifier", fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily)
                                    }

                                    Button(
                                        onClick = {
                                            val cleanDigits = rawPhone.trim().replace("+", "")
                                            val dialDigits = countryDialCode.replace("+", "")
                                            val normalizedPhone = if (cleanDigits.startsWith(dialDigits)) {
                                                cleanDigits
                                            } else if (cleanDigits.startsWith("0")) {
                                                dialDigits + cleanDigits.drop(1)
                                            } else {
                                                dialDigits + cleanDigits
                                            }

                                            val req = AgentRegisterCustomerRequest(
                                                accountType = selectedAccountType.lowercase(),
                                                firstName = firstName.trim(),
                                                lastName = lastName.trim(),
                                                middleName = middleName.trim().ifBlank { null },
                                                fullName = computedFullName,
                                                phone = normalizedPhone,
                                                province = selectedProvince?.code ?: selectedProvince?.name ?: "",
                                                country = countryCode,
                                                countryCode = countryCode,
                                                email = email.trim().ifBlank { null },
                                                gender = gender.lowercase(),
                                                maritalStatus = maritalStatus.lowercase(),
                                                birthDate = birthDateMillis?.let { dateFormatter.format(Date(it)) },
                                                birthPlace = birthPlace.trim().ifBlank { null },
                                                city = city.trim().ifBlank { null },
                                                address = address.trim().ifBlank { null },
                                                idType = idType,
                                                idNumber = idNumber.trim().ifBlank { null },
                                                idIssuedDate = null,
                                                idExpiryDate = null,
                                                profession = profession.trim().ifBlank { null },
                                                nationality = nationality.trim().ifBlank { null },
                                                incomePerMonth = incomePerMonth.toDoubleOrNull(),
                                                companyName = if (businessName.isNotBlank()) businessName.trim() else null,
                                                activityDescription = activityDescription.trim().ifBlank { null },
                                                profilePhoto = profilePhotoBase64.ifBlank { null },
                                                idFrontImage = idFrontBase64.ifBlank { null },
                                                idBackImage = idBackBase64.ifBlank { null },
                                                signatureImage = signatureBase64.ifBlank { null },
                                                representative = if (repName.isNotBlank() && repContact.isNotBlank()) {
                                                    AgentCustomerRepresentativeDto(
                                                        name = repName.trim(),
                                                        contact = repContact.trim(),
                                                        relation = repRelation
                                                    )
                                                } else null
                                            )

                                            onSubmitRegister(req)
                                        },
                                        enabled = !isRegistering,
                                        modifier = Modifier
                                            .weight(2f)
                                            .height(52.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        if (isRegistering) {
                                            CircularProgressIndicator(
                                                color = Color(0xFF0F172A),
                                                strokeWidth = 2.5.dp,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "Création...",
                                                color = Color(0xFF0F172A),
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = MulishFontFamily,
                                                fontSize = 15.sp
                                            )
                                        } else {
                                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF0F172A))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Créer le Compte Client",
                                                color = Color(0xFF0F172A),
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = MulishFontFamily,
                                                fontSize = 14.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationButtonBar(
    nextLabel: String,
    nextEnabled: Boolean,
    onPrev: (() -> Unit)? = null,
    onNext: () -> Unit
) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (onPrev != null) {
            OutlinedButton(
                onClick = onPrev,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Précédent", fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily)
            }
        }

        Button(
            onClick = onNext,
            enabled = nextEnabled,
            modifier = Modifier
                .weight(if (onPrev != null) 2f else 1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00E676),
                disabledContainerColor = Color(0xFF1E293B)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = nextLabel,
                color = if (nextEnabled) Color(0xFF0F172A) else Color(0xFF64748B),
                fontWeight = FontWeight.Bold,
                fontFamily = MulishFontFamily,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = if (nextEnabled) Color(0xFF0F172A) else Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun CaptureItemCard(
    title: String,
    description: String,
    isCaptured: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onAction: () -> Unit
) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCaptured) Color(0xFF00E676) else Color(0xFF334155)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAction() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = if (isCaptured) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFF334155),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isCaptured) Color(0xFF00E676) else Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MulishFontFamily,
                        fontSize = 13.5.sp
                    )
                    Text(
                        text = if (isCaptured) "Document enregistré ✔" else description,
                        color = if (isCaptured) Color(0xFF00E676) else Color(0xFF94A3B8),
                        fontFamily = MulishFontFamily,
                        fontSize = 11.5.sp
                    )
                }
            }

            Surface(
                color = if (isCaptured) Color(0xFF00E676) else Color(0xFF334155),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = if (isCaptured) "Modifier" else "Capturer",
                    color = if (isCaptured) Color(0xFF0F172A) else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = MulishFontFamily,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 12.5.sp, fontFamily = MulishFontFamily)
        Text(text = value, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, fontFamily = MulishFontFamily)
    }
}

@Composable
private fun StepSectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontFamily = MulishFontFamily,
            fontSize = 16.sp
        )
        Text(
            text = subtitle,
            color = Color(0xFF94A3B8),
            fontFamily = MulishFontFamily,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun FormInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = Color(0xFFCBD5E1),
            fontSize = 12.5.sp,
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF64748B),
                    fontSize = 13.5.sp,
                    fontFamily = MulishFontFamily
                )
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = Color(0xFF00E676)
                    )
                }
            } else null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = clientTextFieldColors(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun clientTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF00E676),
    unfocusedBorderColor = Color(0xFF334155),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = Color(0xFF1E293B),
    unfocusedContainerColor = Color(0xFF1E293B),
    cursorColor = Color(0xFF00E676)
)

/**
 * Modal de capture de signature tactile sur Canvas.
 */
@Composable
fun SignatureCaptureModal(
    onDismiss: () -> Unit,
    onSignatureConfirmed: (String) -> Unit
) {
    val points = remember { mutableStateListOf<Offset>() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Signature Tactile du Client",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MulishFontFamily,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray)
                    }
                }

                Text(
                    text = "Faites signer le client au doigt directement sur la zone blanche ci-dessous.",
                    color = Color(0xFF94A3B8),
                    fontFamily = MulishFontFamily,
                    fontSize = 12.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset -> points.add(offset) },
                                onDrag = { change, _ ->
                                    points.add(change.position)
                                    change.consume()
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            if ((p1 - p2).getDistance() < 60f) {
                                drawLine(
                                    color = Color.Black,
                                    start = p1,
                                    end = p2,
                                    strokeWidth = 4f
                                )
                            }
                        }
                    }

                    if (points.isEmpty()) {
                        Text(
                            text = "✍️ Signez ici au doigt",
                            color = Color.LightGray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { points.clear() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Effacer", color = Color(0xFFEF4444), fontSize = 13.sp, fontFamily = MulishFontFamily)
                    }

                    Button(
                        onClick = {
                            val bmp = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888)
                            val canvas = android.graphics.Canvas(bmp)
                            canvas.drawColor(android.graphics.Color.WHITE)
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                strokeWidth = 5f
                                isAntiAlias = true
                                style = android.graphics.Paint.Style.STROKE
                            }
                            for (i in 0 until points.size - 1) {
                                val p1 = points[i]
                                val p2 = points[i + 1]
                                if ((p1 - p2).getDistance() < 60f) {
                                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
                                }
                            }
                            val outputStream = ByteArrayOutputStream()
                            bmp.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
                            val b64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                            onSignatureConfirmed("data:image/png;base64,$b64")
                        },
                        enabled = points.size > 5,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Confirmer", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = MulishFontFamily)
                    }
                }
            }
        }
    }
}

/**
 * Vue de succès après création du compte client.
 */
@Composable
private fun CustomerRegistrationSuccessView(
    response: AgentRegisterCustomerResponse,
    onClose: () -> Unit,
    onCopy: (String, String) -> Unit
) {
    val cust = response.customer
    val tempPass = response.temporaryPassword ?: response.tempPassword
    val pin = response.pin

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            color = Color(0xFF00E676).copy(alpha = 0.15f),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.size(76.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00E676))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        Text(
            text = "Compte Client Créé !",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontFamily = MulishFontFamily,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = response.message ?: "Le client a été inscrit avec succès dans votre pays d'Agent.",
            color = Color(0xFF94A3B8),
            fontFamily = MulishFontFamily,
            fontSize = 13.5.sp,
            textAlign = TextAlign.Center
        )

        Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                cust?.fullName?.let { SuccessDetailRow("Nom complet", it) }
                    ?: SuccessDetailRow("Client", "${cust?.firstName ?: ""} ${cust?.lastName ?: ""}".trim())

                cust?.phone?.let { SuccessDetailRow("Téléphone", it) }

                cust?.idWallet?.let {
                    SuccessDetailRow("ID Compte / Wallet", it, onCopy = { onCopy(it, "ID Wallet") })
                }

                cust?.province?.let { SuccessDetailRow("Province", it) }
                cust?.country?.let { SuccessDetailRow("Pays", it) }
                cust?.createdAt?.let { SuccessDetailRow("Date", it) }
            }
        }

        if (!tempPass.isNullOrBlank() || !pin.isNullOrBlank()) {
            Surface(
                color = Color(0xFF064E3B).copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Identifiants temporaires générés",
                        color = Color(0xFF34D399),
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )

                    if (!tempPass.isNullOrBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mot de passe : $tempPass",
                                color = Color.White,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            IconButton(onClick = { onCopy(tempPass, "Mot de passe") }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    if (!pin.isNullOrBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Code PIN : $pin",
                                color = Color.White,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            IconButton(onClick = { onCopy(pin, "Code PIN") }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Terminer",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
                fontFamily = MulishFontFamily,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun SuccessDetailRow(
    label: String,
    value: String,
    onCopy: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color(0xFF94A3B8), fontFamily = MulishFontFamily, fontSize = 13.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                color = Color.White,
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp
            )
            if (onCopy != null) {
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copier",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
