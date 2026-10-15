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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MobileMoneyOperator
import com.example.ui.theme.MulishFontFamily
import com.example.ui.viewmodel.DashboardUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositDialog(
    uiState: DashboardUiState,
    onDismiss: () -> Unit,
    onMethodChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onOperatorChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onCountryCodeChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedRef by remember { mutableStateOf(false) }
    var operatorExpanded by remember { mutableStateOf(false) }

    // Dynamic country detection for Mobile Money operators
    val operators = MobileMoneyOperator.getOperatorsForCountry(uiState.depositCountryCode)
    val currentOperatorObj = operators.find { it.code.lowercase() == uiState.depositOperator.lowercase() }
        ?: operators.firstOrNull()
        ?: MobileMoneyOperator.MPESA

    val presetAmounts = listOf("5", "10", "20", "50", "100", "250")

    val countryDisplayName = when (uiState.depositCountryCode.uppercase()) {
        "CD", "243" -> "République Démocratique du Congo"
        "CG", "242" -> "Congo Brazzaville"
        "CI", "225" -> "Côte d'Ivoire"
        "SN", "221" -> "Sénégal"
        "CM", "237" -> "Cameroun"
        "NG", "234" -> "Nigeria"
        "KE", "254" -> "Kenya"
        "GH", "233" -> "Ghana"
        else -> "International"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp)),
            color = Color(0xFF0F172A),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF10B981).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Recharger mon Compte",
                                color = Color.White,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Approvisionnement sécurisé MaxiCash",
                                color = Color(0xFF94A3B8),
                                fontFamily = MulishFontFamily,
                                fontSize = 12.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Method Tabs
                val selectedTabIndex = when (uiState.depositMethod) {
                    "mobile_money" -> 0
                    "card" -> 1
                    "paypal" -> 2
                    else -> 0
                }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color(0xFF00E676),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFF00E676),
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { onMethodChange("mobile_money") },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mobile Money", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = MulishFontFamily)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { onMethodChange("card") },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Carte CB", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = MulishFontFamily)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { onMethodChange("paypal") },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PayPal", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = MulishFontFamily)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FORM CONTENT ACCORDING TO METHOD
                when (uiState.depositMethod) {
                    "mobile_money" -> {
                        // Country & Operator Dropdown Option List
                        Text(
                            text = "Opérateur Mobile Money (RDC & International)",
                            color = Color(0xFFCBD5E1),
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        ExposedDropdownMenuBox(
                            expanded = operatorExpanded,
                            onExpandedChange = { operatorExpanded = !operatorExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                    .menuAnchor(),
                                color = Color(0xFF1E293B)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        OperatorLogo(operator = currentOperatorObj, size = 32.dp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = currentOperatorObj.displayName,
                                            color = Color.White,
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = operatorExpanded)
                                }
                            }

                            ExposedDropdownMenu(
                                expanded = operatorExpanded,
                                onDismissRequest = { operatorExpanded = false }
                            ) {
                                operators.forEach { op ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                OperatorLogo(operator = op, size = 26.dp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = op.displayName,
                                                    fontFamily = MulishFontFamily,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF0F172A)
                                                )
                                            }
                                        },
                                        onClick = {
                                            onOperatorChange(op.code)
                                            operatorExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Phone Number
                        Text(
                            text = "Numéro de téléphone Mobile Money *",
                            color = Color(0xFFCBD5E1),
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = uiState.depositPhoneNumber,
                            onValueChange = onPhoneChange,
                            placeholder = { Text("+243... ou 081...", color = Color(0xFF64748B), fontFamily = MulishFontFamily) },
                            leadingIcon = {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF00E676))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00E676),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    "card" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B), RoundedCornerShape(14.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Paiement par Carte 100% Sécurisé",
                                        color = Color.White,
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Les transactions par carte Visa et Mastercard sont traitées instantanément en toute sécurité. Vos données bancaires sont cryptées et ne sont jamais stockées.",
                                    color = Color(0xFF94A3B8),
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    "paypal" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B), RoundedCornerShape(14.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        tint = Color(0xFF3B82F6),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Recharge PayPal Sécurisée",
                                        color = Color.White,
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Vous serez redirigé vers l'interface sécurisée PayPal pour confirmer votre paiement en USD. Votre compte sera crédité dès confirmation.",
                                    color = Color(0xFF94A3B8),
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Montant USD
                Text(
                    text = "Montant à recharger (USD)",
                    color = Color(0xFFCBD5E1),
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.depositAmount,
                    onValueChange = onAmountChange,
                    placeholder = { Text("Ex: 50", color = Color(0xFF64748B), fontFamily = MulishFontFamily) },
                    trailingIcon = {
                        Text(
                            text = "USD",
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.Bold,
                            fontFamily = MulishFontFamily,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E676),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Presets
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetAmounts.forEach { preset ->
                        val isPresetSelected = uiState.depositAmount == preset
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isPresetSelected) Color(0xFF00E676) else Color(0xFF1E293B))
                                .clickable { onAmountChange(preset) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$$preset",
                                color = if (isPresetSelected) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security Note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Transaction cryptée et sécurisée MaxiCash. Vos fonds sont crédités dès validation par l'opérateur.",
                        color = Color(0xFF94A3B8),
                        fontFamily = MulishFontFamily,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = onSubmit,
                    enabled = !uiState.isDepositLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E676),
                        disabledContainerColor = Color(0xFF00E676).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when (uiState.depositMethod) {
                            "mobile_money" -> "Valider le Dépôt Mobile Money"
                            "card" -> "Procéder au Paiement par Carte"
                            "paypal" -> "Procéder au Paiement PayPal"
                            else -> "Valider la Recharge"
                        },
                        color = Color(0xFF0F172A),
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // 1. LARGE SPINNER POPUP WHEN PROCESSING REQUEST
    if (uiState.isDepositLoading) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(24.dp)),
                color = Color(0xFF0F172A),
                shadowElevation = 24.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF00E676),
                        modifier = Modifier.size(60.dp),
                        strokeWidth = 5.dp
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                    Text(
                        text = "Traitement du dépôt...",
                        color = Color.White,
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Envoi de la requête au serveur et attente du réseau MaxiCash...",
                        color = Color(0xFFCBD5E1),
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Veuillez valider le message d'autorisation USSD sur votre téléphone portable.",
                        color = Color(0xFF00E676),
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // 2. LARGE SUCCESS POPUP CARD UPON SUCCESS OR WEBHOOK NOTIFICATION
    if (!uiState.isDepositLoading && (uiState.depositSuccessMessage != null || uiState.depositPendingReference != null) && uiState.depositError == null) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .clip(RoundedCornerShape(24.dp)),
                color = Color(0xFF0F172A),
                shadowElevation = 24.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFF10B981).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Succès",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Dépôt Initié avec Succès !",
                        color = Color.White,
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.depositPendingReference != null) {
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Référence : ${uiState.depositPendingReference}",
                                    color = Color(0xFF00E676),
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(uiState.depositPendingReference!!))
                                        copiedRef = true
                                    }
                                ) {
                                    Text(
                                        text = if (copiedRef) "Copié" else "Copier",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontFamily = MulishFontFamily
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = uiState.depositSuccessMessage ?: "Votre demande de dépôt a été transmise. Dès confirmation de votre opérateur, votre compte wallet sera crédité.",
                        color = Color(0xFFD1FAE5),
                        fontFamily = MulishFontFamily,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Fermer & Voir Mon Solde",
                            color = Color.White,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // 3. LARGE FAILURE / ERROR POPUP CARD
    if (!uiState.isDepositLoading && uiState.depositError != null) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .clip(RoundedCornerShape(24.dp)),
                color = Color(0xFF0F172A),
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
                            .size(64.dp)
                            .background(Color(0xFFEF4444).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Échec",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Signalisation d'Échec !",
                        color = Color(0xFFEF4444),
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color(0xFF7F1D1D).copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.depositError ?: "L'opération de dépôt n'a pas pu aboutir.",
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
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Réessayer / Modifier",
                            color = Color.White,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OperatorLogo(operator: com.example.data.model.MobileMoneyOperator, size: androidx.compose.ui.unit.Dp = 28.dp) {
    val logoRes = when (operator) {
        com.example.data.model.MobileMoneyOperator.MPESA -> com.example.R.drawable.logo_mpesa
        com.example.data.model.MobileMoneyOperator.ORANGE_MONEY -> com.example.R.drawable.logo_orange_money
        com.example.data.model.MobileMoneyOperator.AFRIMONEY -> com.example.R.drawable.afrimoney
        com.example.data.model.MobileMoneyOperator.AIRTEL_MONEY -> com.example.R.drawable.logo_airtel_money
        com.example.data.model.MobileMoneyOperator.MTN_MOMO -> com.example.R.drawable.logo_mtn_money
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color.White,
        modifier = Modifier.size(size)
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = logoRes),
            contentDescription = operator.displayName,
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit
        )
    }
}
