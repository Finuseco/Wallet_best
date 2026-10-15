package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.data.local.CpkKeyEntity
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.MulishFontFamily
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.DashboardViewModel
import com.example.util.CpkCloudSyncManager
import java.io.File

private val BrandGreen = Color(0xFF00C48C)
private val DarkBg = Color(0xFF0F172A)
private val DarkCardBg = Color(0xFF1E293B)
private val DarkBorder = Color(0xFF334155)
private val CloudGreen = Color(0xFF00E676)
private val CloudAmber = Color(0xFFF59E0B)
private val AccentPurple = Color(0xFF8B5CF6)
private val UsbBlue = Color(0xFF38BDF8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CpkReaderDialog(
    isOpen: Boolean,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    userProfile: UserProfileEntity?,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val cpk = uiState.cpk

    LaunchedEffect(Unit) {
        viewModel.initCloudSyncManager(context)
    }

    // Android System File Picker for .cpk / USB keys
    val cpkFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val content = inputStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                var fileName: String? = null
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0) fileName = it.getString(nameIndex)
                    }
                }
                if (content.isNotBlank()) {
                    viewModel.importCpkContent(content, fileName ?: "Cle_USB.cpk")
                } else {
                    Toast.makeText(context, "Fichier vide ou illisible.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erreur de lecture du fichier : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.97f)
                .fillMaxHeight(0.95f)
                .clip(RoundedCornerShape(24.dp)),
            color = DarkBg,
            shadowElevation = 24.dp,
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Header (VLC-style CPK reader)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(UsbBlue.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Usb,
                                contentDescription = null,
                                tint = UsbBlue,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Lecteur de Clés CPK",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Surface(
                                    color = UsbBlue.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = ".cpk",
                                        color = UsbBlue,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "FINUSECO SARL • Clés USB, fichiers & Cloud Drive",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(DarkBorder.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cloud Drive Sync Bar (Google Drive integration & phone loss recovery)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCardBg,
                    border = BorderStroke(1.dp, if (cpk.isDriveConnected) CloudGreen.copy(alpha = 0.4f) else DarkBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (cpk.isDriveConnected) Icons.Default.CloudDone else Icons.Outlined.CloudQueue,
                                contentDescription = null,
                                tint = if (cpk.isDriveConnected) CloudGreen else CloudAmber,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = if (cpk.isDriveConnected) "Google Drive connecté" else "Google Drive non lié",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (cpk.isDriveConnected) "${cpk.connectedDriveEmail} • Dossier : ${CpkCloudSyncManager.VAULT_FOLDER_NAME}"
                                    else "En cas de perte du téléphone, synchronisez avec votre Drive",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Restore button
                            FilledTonalButton(
                                onClick = { viewModel.restoreKeysFromCloud(context) },
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF334155),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (cpk.isRestoringFromCloud) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color.White)
                                } else {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Restaurer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Link / Configure button
                            Button(
                                onClick = { viewModel.openLinkDriveDialog() },
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (cpk.isDriveConnected) CloudGreen.copy(alpha = 0.2f) else AccentPurple,
                                    contentColor = if (cpk.isDriveConnected) CloudGreen else Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (cpk.isDriveConnected) "Compte" else "Lier Drive", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions Bar: Importer USB / .cpk, Émettre nouvelle clé, Rafraîchir
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { cpkFilePicker.launch("*/*") },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UsbBlue),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Importer .cpk / USB", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }

                    Button(
                        onClick = { viewModel.openCpkIssueModal() },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Créer une clé", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    IconButton(
                        onClick = { viewModel.refreshCpkKeysFromServer() },
                        modifier = Modifier
                            .size(42.dp)
                            .background(DarkCardBg, RoundedCornerShape(10.dp))
                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                    ) {
                        if (cpk.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = BrandGreen)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualiser", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // USB OTG Real-time Interrogation Strip
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.interrogateUsbDevices(context) },
                    shape = RoundedCornerShape(10.dp),
                    color = UsbBlue.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, UsbBlue.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Sensors, contentDescription = null, tint = UsbBlue, modifier = Modifier.size(18.dp))
                            Column {
                                Text(
                                    text = "Interroger l'état Clé USB en temps réel",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Vérification en direct auprès de l'API FINUSECO (/status)",
                                    fontSize = 9.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        FilledTonalButton(
                            onClick = { viewModel.interrogateUsbDevices(context) },
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = UsbBlue,
                                contentColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Interroger", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search & Filter row
                OutlinedTextField(
                    value = cpk.searchQuery,
                    onValueChange = { viewModel.setCpkSearchQuery(it) },
                    placeholder = { Text("Rechercher par référence, signature, montant...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (cpk.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setCpkSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UsbBlue,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkCardBg,
                        unfocusedContainerColor = DarkCardBg
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Chips
                val filters = listOf(
                    "ALL" to "Toutes (${cpk.keys.size})",
                    "ACTIVE" to "Actives",
                    "CLAIMED" to "Encaissées",
                    "CLOUD" to "Cloud Sync (Vert)",
                    "LOCAL" to "Local uniquement"
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filters) { (key, label) ->
                        val isSelected = cpk.selectedFilter == key
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) UsbBlue else DarkCardBg,
                            border = BorderStroke(1.dp, if (isSelected) UsbBlue else DarkBorder),
                            modifier = Modifier.clickable { viewModel.setCpkFilter(key) }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Success / Error Banners
                AnimatedVisibility(visible = cpk.successMessage != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = BrandGreen.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, BrandGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(16.dp))
                                Text(cpk.successMessage ?: "", color = Color.White, fontSize = 12.sp)
                            }
                            IconButton(onClick = { viewModel.clearCpkMessages() }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = cpk.errorMessage != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = Color(0xFF7F1D1D).copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(16.dp))
                                Text(cpk.errorMessage ?: "", color = Color(0xFFFCA5A5), fontSize = 12.sp)
                            }
                            IconButton(onClick = { viewModel.clearCpkMessages() }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filtered Keys list
                val filteredKeys = remember(cpk.keys, cpk.searchQuery, cpk.selectedFilter) {
                    cpk.keys.filter { key ->
                        val matchesSearch = cpk.searchQuery.isBlank() ||
                                (key.reference?.contains(cpk.searchQuery, ignoreCase = true) == true) ||
                                key.signature.contains(cpk.searchQuery, ignoreCase = true) ||
                                key.amount.toString().contains(cpk.searchQuery) ||
                                key.currency.contains(cpk.searchQuery, ignoreCase = true)

                        val matchesFilter = when (cpk.selectedFilter) {
                            "ACTIVE" -> key.status.equals("active", ignoreCase = true)
                            "CLAIMED" -> key.status.equals("claimed", ignoreCase = true) || key.status.equals("partially_used", ignoreCase = true)
                            "CLOUD" -> key.isCloudSynced
                            "LOCAL" -> !key.isCloudSynced
                            else -> true
                        }
                        matchesSearch && matchesFilter
                    }
                }

                if (filteredKeys.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Usb, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(48.dp))
                            Text("Aucune clé financière CPK trouvée", fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), fontSize = 14.sp)
                            Text(
                                "Importez un fichier .cpk depuis votre stockage USB ou créez une nouvelle clé sécurisée.",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredKeys, key = { it.signature }) { key ->
                            CpkKeyCard(
                                key = key,
                                isCheckingStatus = cpk.isCheckingStatus && cpk.checkingSignature == key.signature,
                                isSyncingCloud = cpk.isSyncingCloud && cpk.syncingSignature == key.signature,
                                onCheckStatus = { viewModel.checkCpkKeyStatus(key.signature) },
                                onRecover = { viewModel.openCpkRecoverModal(key) },
                                onCancel = { viewModel.openCpkCancelModal(key) },
                                onCloudSync = { viewModel.syncKeyToCloud(key, context) },
                                onShareExport = {
                                    shareOrExportCpkFile(context, key)
                                },
                                onCopySignature = {
                                    clipboardManager.setText(AnnotatedString(key.signature))
                                    Toast.makeText(context, "Signature copiée !", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal: Issue New Key
    if (cpk.isIssueModalOpen) {
        CpkIssueModalDialog(
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { viewModel.closeCpkIssueModal() }
        )
    }

    // Modal: Recover Funds
    if (cpk.isRecoverModalOpen && cpk.recoverKey != null) {
        CpkRecoverModalDialog(
            key = cpk.recoverKey,
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { viewModel.closeCpkRecoverModal() }
        )
    }

    // Modal: Cancel Key
    if (cpk.isCancelModalOpen && cpk.cancelKey != null) {
        CpkCancelModalDialog(
            key = cpk.cancelKey,
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { viewModel.closeCpkCancelModal() }
        )
    }

    // Modal: Link Google Drive
    if (cpk.isLinkingDriveDialogOpen) {
        CpkLinkDriveModalDialog(
            uiState = uiState,
            viewModel = viewModel,
            userProfile = userProfile,
            onDismiss = { viewModel.closeLinkDriveDialog() }
        )
    }
}

@Composable
private fun CpkKeyCard(
    key: CpkKeyEntity,
    isCheckingStatus: Boolean,
    isSyncingCloud: Boolean,
    onCheckStatus: () -> Unit,
    onRecover: () -> Unit,
    onCancel: () -> Unit,
    onCloudSync: () -> Unit,
    onShareExport: () -> Unit,
    onCopySignature: () -> Unit
) {
    val statusColor = when (key.status.uppercase()) {
        "ACTIVE" -> Color(0xFF00E676)
        "CLAIMED" -> Color(0xFF38BDF8)
        "PARTIALLY_USED" -> Color(0xFFF59E0B)
        "CANCELLED" -> Color(0xFF94A3B8)
        else -> Color(0xFFF59E0B)
    }

    val statusLabel = when (key.status.uppercase()) {
        "ACTIVE" -> "ACTIF"
        "CLAIMED" -> "ENCAISSÉ"
        "PARTIALLY_USED" -> "PARTIEL"
        "CANCELLED" -> "ANNULÉ"
        else -> key.status.uppercase()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkCardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header Row: USB Icon, Reference, Amount & Cloud Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(UsbBlue.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Usb,
                            contentDescription = null,
                            tint = UsbBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = key.reference ?: "CPK-${key.signature.take(8).uppercase()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            IconButton(onClick = onCopySignature, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = Color(0xFF94A3B8), modifier = Modifier.size(12.dp))
                            }
                        }
                        Text(
                            text = if (key.securityLevel == "pin_protected") "Protégée par code PIN" else "Sans PIN spécifique",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Cloud Status Icon (CRITICAL UX REQUIREMENT)
                // Yellow when local, Green when synced to Drive. Clicking transfers to Google Drive!
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (key.isCloudSynced) CloudGreen.copy(alpha = 0.18f) else CloudAmber.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, if (key.isCloudSynced) CloudGreen else CloudAmber),
                    modifier = Modifier.clickable { onCloudSync() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (isSyncingCloud) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = if (key.isCloudSynced) CloudGreen else CloudAmber
                            )
                        } else {
                            Icon(
                                imageVector = if (key.isCloudSynced) Icons.Default.CloudDone else Icons.Default.CloudUpload,
                                contentDescription = if (key.isCloudSynced) "Clé sécurisée dans Google Drive" else "Clé locale - Cliquer pour transférer vers Google Drive",
                                tint = if (key.isCloudSynced) CloudGreen else CloudAmber,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (key.isCloudSynced) "Dans le Cloud" else "Sauvegarder",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (key.isCloudSynced) CloudGreen else CloudAmber
                        )
                    }
                }
            }

            Divider(color = DarkBorder, thickness = 1.dp)

            // Financial Balance & Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Montant disponible :",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "${"%,d".format(key.remainingAmount.toLong()).replace(',', ' ')} ${key.currency}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (key.status == "active") BrandGreen else Color.White
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Quick Actions: Interroger temps réel, Encaisser, Partager, Annuler
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Live Status check
                OutlinedButton(
                    onClick = onCheckStatus,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isCheckingStatus) {
                        CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp, color = UsbBlue)
                    } else {
                        Icon(Icons.Default.Sensors, contentDescription = null, tint = UsbBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("État direct", fontSize = 10.sp)
                    }
                }

                // Recover funds (if active)
                if (key.status.equals("active", ignoreCase = true) || key.status.equals("partially_used", ignoreCase = true)) {
                    Button(
                        onClick = onRecover,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Encaisser", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Share / Export file
                OutlinedButton(
                    onClick = onShareExport,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Exporter", fontSize = 10.sp)
                }

                // Cancel / Refund (if active and owner)
                if (key.isOwner && key.status.equals("active", ignoreCase = true)) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Annuler", fontSize = 10.sp, color = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// MODALS
// -----------------------------------------------------------------------------

@Composable
private fun CpkIssueModalDialog(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val cpk = uiState.cpk

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AddCard, contentDescription = null, tint = BrandGreen)
                Text("Émettre une clé financière CPK", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Immobilisez un montant sous forme de clé signée pour vos paiements hors-ligne, clés USB ou transferts sécurisés.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                // Montant
                OutlinedTextField(
                    value = cpk.issueAmount,
                    onValueChange = { viewModel.setCpkIssueAmount(it) },
                    label = { Text("Montant") },
                    placeholder = { Text("Ex: 100") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Devise (USD, CDF, EUR)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("USD", "CDF", "EUR").forEach { curr ->
                        val isSelected = cpk.issueCurrency == curr
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) BrandGreen else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setCpkIssueCurrency(curr) }
                        ) {
                            Text(
                                text = curr,
                                color = if (isSelected) Color.White else Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                // Niveau de sécurité (none vs pin_protected)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (cpk.issueSecurityLevel == "none") UsbBlue else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setCpkIssueSecurityLevel("none") }
                    ) {
                        Text(
                            text = "Sans PIN clé",
                            color = if (cpk.issueSecurityLevel == "none") Color(0xFF0F172A) else Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (cpk.issueSecurityLevel == "pin_protected") UsbBlue else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setCpkIssueSecurityLevel("pin_protected") }
                    ) {
                        Text(
                            text = "Protégée par PIN",
                            color = if (cpk.issueSecurityLevel == "pin_protected") Color(0xFF0F172A) else Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                if (cpk.issueSecurityLevel == "pin_protected") {
                    OutlinedTextField(
                        value = cpk.issueKeyPin,
                        onValueChange = { viewModel.setCpkIssueKeyPin(it) },
                        label = { Text("Code PIN de la clé (4 à 6 chiffres)") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // PIN personnel CashPay
                OutlinedTextField(
                    value = cpk.issueUserPin,
                    onValueChange = { viewModel.setCpkIssueUserPin(it) },
                    label = { Text("Votre code PIN CashPay (Validation débit)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (cpk.issueError != null) {
                    Text(cpk.issueError, color = Color(0xFFEF4444), fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.submitCpkIssue() },
                enabled = !cpk.isIssuing,
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                if (cpk.isIssuing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Valider et Émettre")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
private fun CpkRecoverModalDialog(
    key: CpkKeyEntity,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val cpk = uiState.cpk

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Download, contentDescription = null, tint = BrandGreen)
                Text("Encaisser la clé CPK", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Les fonds de cette clé seront crédités immédiatement sur votre portefeuille CashPay All.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Solde récupérable :", fontSize = 11.sp, color = Color(0xFF64748B))
                        Text("${"%,d".format(key.remainingAmount.toLong()).replace(',', ' ')} ${key.currency}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = BrandGreen)
                    }
                }

                if (key.securityLevel == "pin_protected") {
                    OutlinedTextField(
                        value = cpk.recoverKeyPin,
                        onValueChange = { viewModel.setCpkRecoverKeyPin(it) },
                        label = { Text("Code PIN secret de la clé") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = cpk.recoverPartialAmount,
                    onValueChange = { viewModel.setCpkRecoverPartialAmount(it) },
                    label = { Text("Montant partiel (Facultatif)") },
                    placeholder = { Text("Laissez vide pour encaisser la totalité") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (cpk.recoverError != null) {
                    Text(cpk.recoverError, color = Color(0xFFEF4444), fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.submitCpkRecover() },
                enabled = !cpk.isRecovering,
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                if (cpk.isRecovering) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Créditer mon portefeuille")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Fermer") }
        }
    )
}

@Composable
private fun CpkCancelModalDialog(
    key: CpkKeyEntity,
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val cpk = uiState.cpk

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Annulation & Remboursement", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFEF4444))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "L'annulation remboursera immédiatement le capital restant (${key.remainingAmount} ${key.currency}) sur votre portefeuille. Cette clé ne sera plus utilisable.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                OutlinedTextField(
                    value = cpk.cancelUserPin,
                    onValueChange = { viewModel.setCpkCancelUserPin(it) },
                    label = { Text("Votre code PIN CashPay personnel") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (cpk.cancelError != null) {
                    Text(cpk.cancelError, color = Color(0xFFEF4444), fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.submitCpkCancel() },
                enabled = !cpk.isCancelling,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
            ) {
                if (cpk.isCancelling) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Confirmer l'annulation", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Retour") }
        }
    )
}

@Composable
private fun CpkLinkDriveModalDialog(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    userProfile: UserProfileEntity?,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var emailInput by remember { mutableStateOf(userProfile?.email?.takeIf { it.isNotBlank() } ?: "client.cashpay@gmail.com") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.CloudSync, contentDescription = null, tint = AccentPurple)
                Text("Liaison Google Drive & Sauvegarde", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Ce système permet de synchroniser vos clés CPK dans un dossier dédié sur Google Drive (${CpkCloudSyncManager.VAULT_FOLDER_NAME}). En cas de perte ou changement de téléphone, vous retrouvez automatiquement toutes vos clés en reconnectant votre Drive.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                // Package name & SHA-1 / SHA-256 signatures for Google Cloud Console OAuth setup
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(16.dp))
                            Text("Signatures Google Cloud Console", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                        Text(
                            "À inscrire dans Google Cloud Console > Identifiants > ID Client OAuth 2.0 (Type: Android) :",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )

                        // Google Client ID
                        Surface(color = Color.White, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, Color(0xFFCBD5E1))) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("ID Client Google OAuth 2.0 :", fontSize = 9.sp, color = Color(0xFF64748B))
                                    Text(CpkCloudSyncManager.GOOGLE_CLIENT_ID, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF0F172A))
                                }
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(CpkCloudSyncManager.GOOGLE_CLIENT_ID))
                                        Toast.makeText(context, "ID Client Google copié !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copier Client ID", modifier = Modifier.size(14.dp), tint = AccentPurple)
                                }
                            }
                        }

                        // Package name
                        Surface(color = Color.White, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, Color(0xFFCBD5E1))) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Nom du Package :", fontSize = 9.sp, color = Color(0xFF64748B))
                                    Text(CpkCloudSyncManager.GOOGLE_OAUTH_PACKAGE_NAME, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFF0F172A))
                                }
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(CpkCloudSyncManager.GOOGLE_OAUTH_PACKAGE_NAME))
                                        Toast.makeText(context, "Package copié !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copier", modifier = Modifier.size(14.dp), tint = AccentPurple)
                                }
                            }
                        }

                        // SHA-1 Fingerprint
                        Surface(color = Color.White, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, Color(0xFFCBD5E1))) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Empreinte SHA-1 :", fontSize = 9.sp, color = Color(0xFF64748B))
                                    Text(CpkCloudSyncManager.GOOGLE_OAUTH_SHA1, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF0F172A))
                                }
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(CpkCloudSyncManager.GOOGLE_OAUTH_SHA1))
                                        Toast.makeText(context, "Empreinte SHA-1 copiée !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copier SHA-1", modifier = Modifier.size(14.dp), tint = AccentPurple)
                                }
                            }
                        }

                        // SHA-256 Fingerprint
                        Surface(color = Color.White, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, Color(0xFFCBD5E1))) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Empreinte SHA-256 :", fontSize = 9.sp, color = Color(0xFF64748B))
                                    Text(CpkCloudSyncManager.GOOGLE_OAUTH_SHA256, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF0F172A))
                                }
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(CpkCloudSyncManager.GOOGLE_OAUTH_SHA256))
                                        Toast.makeText(context, "Empreinte SHA-256 copiée !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copier SHA-256", modifier = Modifier.size(14.dp), tint = AccentPurple)
                                }
                            }
                        }

                        // Folder Name
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Dossier Google Drive cible :", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text(CpkCloudSyncManager.VAULT_FOLDER_NAME, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentPurple)
                        }
                    }
                }

                if (uiState.cpk.isDriveConnected) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF00E676).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFF00E676)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00C48C), modifier = Modifier.size(16.dp))
                                Text("Compte actuellement lié :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                            Text(uiState.cpk.connectedDriveEmail ?: "", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                            Text("Dernière synchronisation : ${uiState.cpk.lastCloudSyncTime ?: "À l'instant"}", fontSize = 10.sp, color = Color(0xFF64748B))
                        }
                    }

                    // Restore button (Phone loss recovery)
                    Button(
                        onClick = { viewModel.restoreKeysFromCloud(context) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restaurer mes clés depuis Drive")
                    }
                } else {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Adresse e-mail Google Drive") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (emailInput.isNotBlank()) {
                        viewModel.connectGoogleDrive(emailInput.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
            ) {
                Text(if (uiState.cpk.isDriveConnected) "Synchroniser à nouveau" else "Lier et Créer le dossier")
            }
        },
        dismissButton = {
            if (uiState.cpk.isDriveConnected) {
                TextButton(onClick = { viewModel.disconnectGoogleDrive() }) {
                    Text("Déconnecter", color = Color(0xFFEF4444))
                }
            } else {
                TextButton(onClick = onDismiss) { Text("Fermer") }
            }
        }
    )
}

private fun shareOrExportCpkFile(context: android.content.Context, key: CpkKeyEntity) {
    try {
        val fileName = key.fileName ?: "CashPay_${key.signature.take(8)}.cpk"
        val file = File(context.cacheDir, fileName)
        val content = if (key.rawJsonContent.isNotBlank()) key.rawJsonContent else {
            org.json.JSONObject().apply {
                put("format", "FINUSECO_CPK_V1")
                put("signature", key.signature)
                put("reference", key.reference)
                put("amount", key.amount)
                put("currency", key.currency)
                put("securityLevel", key.securityLevel)
                put("createdAt", key.createdAt)
            }.toString(2)
        }
        file.writeText(content, Charsets.UTF_8)

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Clé financière CPK - ${key.reference ?: key.signature.take(8)}")
            putExtra(Intent.EXTRA_TEXT, "Voici votre clé financière CPK signée (${key.amount} ${key.currency}). Ouvrez-la avec l'application CashPay All.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Exporter ou partager la clé CPK"))
    } catch (e: Exception) {
        Toast.makeText(context, "Erreur d'exportation : ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
