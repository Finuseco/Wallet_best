package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.BoutiqueDto
import com.example.data.model.ProductDetailsDto
import com.example.data.model.ProductDto
import com.example.data.model.ProductVariationDto
import com.example.data.model.PromotionalPackageDto
import com.example.data.model.PublishProductRequest
import com.example.data.model.SHOPPING_CATEGORIES
import com.example.data.model.ShippingRateDto
import com.example.ui.theme.MulishFontFamily
import com.example.ui.viewmodel.DashboardUiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentShoppingDialog(
    isOpen: Boolean,
    uiState: DashboardUiState,
    onDismiss: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onRefresh: () -> Unit,
    onCreateBoutique: (name: String, address: String?, whatsapp: String?) -> Unit,
    onUpdateBoutique: (boutiqueId: String, name: String, address: String?, whatsapp: String?) -> Unit,
    onDeleteBoutique: (boutiqueId: String) -> Unit,
    onPublishProduct: (PublishProductRequest) -> Unit,
    onUpdateProduct: (productId: String, PublishProductRequest) -> Unit,
    onDeleteProduct: (productId: String) -> Unit,
    onEditProduct: (ProductDto) -> Unit,
    onFetchProductReference: (productId: String) -> Unit,
    onFetchPublicCatalog: (userId: String?, storeId: String?) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // State for creating/editing boutique dialog
    var showBoutiqueModal by remember { mutableStateOf(false) }
    var boutiqueToEdit by remember { mutableStateOf<BoutiqueDto?>(null) }
    var boutiqueNameInput by remember { mutableStateOf("") }
    var boutiqueAddressInput by remember { mutableStateOf("") }
    var boutiqueWhatsappInput by remember { mutableStateOf("") }

    // State for reference dialog
    var showReferenceModal by remember { mutableStateOf(false) }
    var referenceProductTarget by remember { mutableStateOf<ProductDto?>(null) }

    // Delete confirmation dialog
    var itemToDeleteType by remember { mutableStateOf<String?>(null) } // "boutique" or "product"
    var itemToDeleteId by remember { mutableStateOf<String?>(null) }
    var itemToDeleteName by remember { mutableStateOf<String?>(null) }

    // Filter products by boutique in products tab
    var selectedFilterBoutiqueId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.shoppingError) {
        uiState.shoppingError?.let { err ->
            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(uiState.shoppingSuccessMessage) {
        uiState.shoppingSuccessMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFF0F172A),
            topBar = {
                // Header
                Surface(
                    color = Color(0xFF1E293B),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
                    ) {
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
                                        .size(42.dp)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFFFF6600), Color(0xFFFF9900))
                                            ),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = "Shopping Agent",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Espace Shopping Agent",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Publication & Gestion des Boutiques",
                                        fontFamily = MulishFontFamily,
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onRefresh) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Actualiser",
                                        tint = Color(0xFF00E676)
                                    )
                                }
                                IconButton(onClick = onDismiss) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Fermer",
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Success / Error Banners
                        uiState.shoppingSuccessMessage?.let { msg ->
                            Surface(
                                color = Color(0xFF059669).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF10B981)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                    Text(msg, color = Color(0xFF6EE7B7), fontSize = 12.sp, fontFamily = MulishFontFamily)
                                }
                            }
                        }

                        uiState.shoppingError?.let { err ->
                            Surface(
                                color = Color(0xFFDC2626).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                    Text(err, color = Color(0xFFFCA5A5), fontSize = 12.sp, fontFamily = MulishFontFamily)
                                }
                            }
                        }

                        // Tab Row
                        val tabItems = listOf(
                            Triple("Boutiques (${uiState.shoppingBoutiques.size})", Icons.Default.Store, "boutiques"),
                            Triple("Produits (${uiState.shoppingProducts.size})", Icons.Default.Inventory2, "produits"),
                            Triple(if (uiState.editingProduct != null) "Modifier" else "Publier", if (uiState.editingProduct != null) Icons.Default.Edit else Icons.Default.Add, "publier"),
                            Triple("Catalogue", Icons.Default.Public, "catalogue")
                        )

                        TabRow(
                            selectedTabIndex = uiState.shoppingActiveTab,
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFFFF6600),
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.shoppingActiveTab]),
                                    color = Color(0xFFFF6600),
                                    height = 3.dp
                                )
                            },
                            divider = {}
                        ) {
                            tabItems.forEachIndexed { index, item ->
                                Tab(
                                    selected = uiState.shoppingActiveTab == index,
                                    onClick = { onTabSelected(index) },
                                    icon = {
                                        Icon(
                                            imageVector = item.second,
                                            contentDescription = null,
                                            tint = if (uiState.shoppingActiveTab == index) Color(0xFFFF6600) else Color(0xFF94A3B8),
                                            modifier = Modifier.size(17.dp)
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = item.first,
                                            fontFamily = MulishFontFamily,
                                            fontWeight = if (uiState.shoppingActiveTab == index) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = if (uiState.shoppingActiveTab == index) Color(0xFFFF6600) else Color(0xFF94A3B8)
                                        )
                                    }
                                )
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
                    .background(Color(0xFF0B1120))
            ) {
                if (uiState.isShoppingLoading && uiState.shoppingBoutiques.isEmpty() && uiState.shoppingProducts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFFF6600))
                    }
                } else {
                    when (uiState.shoppingActiveTab) {
                        0 -> BoutiquesTabContent(
                            boutiques = uiState.shoppingBoutiques,
                            products = uiState.shoppingProducts,
                            onCreateClick = {
                                boutiqueToEdit = null
                                boutiqueNameInput = ""
                                boutiqueAddressInput = ""
                                boutiqueWhatsappInput = ""
                                showBoutiqueModal = true
                            },
                            onEditClick = { b ->
                                boutiqueToEdit = b
                                boutiqueNameInput = b.name
                                boutiqueAddressInput = b.address ?: ""
                                boutiqueWhatsappInput = b.whatsappPublicNumber ?: ""
                                showBoutiqueModal = true
                            },
                            onDeleteClick = { b ->
                                itemToDeleteType = "boutique"
                                itemToDeleteId = b.id
                                itemToDeleteName = b.name
                            },
                            onViewProducts = { b ->
                                selectedFilterBoutiqueId = b.id
                                onTabSelected(1)
                            }
                        )

                        1 -> ProductsTabContent(
                            products = uiState.shoppingProducts,
                            boutiques = uiState.shoppingBoutiques,
                            selectedBoutiqueId = selectedFilterBoutiqueId,
                            onSelectBoutiqueFilter = { selectedFilterBoutiqueId = it },
                            onPublishNewClick = { onTabSelected(2) },
                            onEditProduct = onEditProduct,
                            onDeleteProduct = { p ->
                                itemToDeleteType = "product"
                                itemToDeleteId = p.id
                                itemToDeleteName = p.name
                            },
                            onViewReference = { p ->
                                referenceProductTarget = p
                                onFetchProductReference(p.id)
                                showReferenceModal = true
                            }
                        )

                        2 -> PublishProductTabContent(
                            boutiques = uiState.shoppingBoutiques,
                            editingProduct = uiState.editingProduct,
                            isLoading = uiState.isShoppingLoading,
                            onCreateBoutiqueClick = {
                                boutiqueToEdit = null
                                boutiqueNameInput = ""
                                boutiqueAddressInput = ""
                                boutiqueWhatsappInput = ""
                                showBoutiqueModal = true
                            },
                            onSubmitPublish = { req ->
                                if (uiState.editingProduct != null) {
                                    onUpdateProduct(uiState.editingProduct.id, req)
                                } else {
                                    onPublishProduct(req)
                                }
                            }
                        )

                        3 -> PublicCatalogTabContent(
                            publicCatalog = uiState.publicCatalog,
                            isLoading = uiState.isPublicCatalogLoading,
                            boutiques = uiState.shoppingBoutiques,
                            onRefreshCatalog = { storeId ->
                                onFetchPublicCatalog(null, storeId)
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal: Créer / Modifier Boutique
    if (showBoutiqueModal) {
        AlertDialog(
            onDismissRequest = { showBoutiqueModal = false },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(18.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        if (boutiqueToEdit != null) Icons.Default.Edit else Icons.Default.AddBusiness,
                        contentDescription = null,
                        tint = Color(0xFFFF6600)
                    )
                    Text(
                        text = if (boutiqueToEdit != null) "Modifier la Boutique" else "Créer une Boutique",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Renseignez les informations de votre point de vente. Les clients pourront contacter ce numéro pour passer commande.",
                        fontSize = 12.5.sp,
                        color = Color(0xFF94A3B8),
                        fontFamily = MulishFontFamily
                    )

                    OutlinedTextField(
                        value = boutiqueNameInput,
                        onValueChange = { boutiqueNameInput = it },
                        label = { Text("Nom de la boutique *", color = Color(0xFF94A3B8)) },
                        placeholder = { Text("Ex : Mode Élegance Kinshasa", color = Color(0xFF64748B)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6600),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = boutiqueAddressInput,
                        onValueChange = { boutiqueAddressInput = it },
                        label = { Text("Adresse physique", color = Color(0xFF94A3B8)) },
                        placeholder = { Text("Ex : Av. Kasa-Vubu 12, Kinshasa", color = Color(0xFF64748B)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6600),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = boutiqueWhatsappInput,
                        onValueChange = { boutiqueWhatsappInput = it },
                        label = { Text("Numéro WhatsApp Public", color = Color(0xFF94A3B8)) },
                        placeholder = { Text("Ex : +243812345678", color = Color(0xFF64748B)) },
                        leadingIcon = {
                            Icon(Icons.Default.Whatsapp, contentDescription = null, tint = Color(0xFF25D366))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6600),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmedName = boutiqueNameInput.trim()
                        if (trimmedName.isBlank()) {
                            Toast.makeText(context, "Le nom de la boutique est obligatoire.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val trimmedAddr = boutiqueAddressInput.trim().ifBlank { null }
                        val trimmedWa = boutiqueWhatsappInput.trim().ifBlank { null }
                        if (boutiqueToEdit != null) {
                            onUpdateBoutique(
                                boutiqueToEdit!!.id,
                                trimmedName,
                                trimmedAddr,
                                trimmedWa
                            )
                        } else {
                            onCreateBoutique(
                                trimmedName,
                                trimmedAddr,
                                trimmedWa
                            )
                        }
                        showBoutiqueModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        if (boutiqueToEdit != null) "Mettre à jour" else "Créer la boutique",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showBoutiqueModal = false }) {
                    Text("Annuler", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    // Modal: Référence Produit
    if (showReferenceModal && referenceProductTarget != null) {
        val p = referenceProductTarget!!
        val refInfo = uiState.productReferenceInfo
        AlertDialog(
            onDismissRequest = { showReferenceModal = false },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(18.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null, tint = Color(0xFF00E676))
                    Text(
                        text = "Référence Produit",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(p.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("ID Produit :", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                Text(refInfo?.productId ?: p.id, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Code-barres / SKU :", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                Text(refInfo?.barcode ?: (p.barcode ?: "N/A"), color = Color(0xFFFDE68A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Référence Serveur :", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                Text(refInfo?.reference ?: p.id, color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val copyText = "Produit : ${p.name}\nSKU : ${p.barcode}\nRef : ${refInfo?.reference ?: p.id}"
                            clipboardManager.setText(AnnotatedString(copyText))
                            Toast.makeText(context, "Référence copiée dans le presse-papier !", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copier la référence", color = Color.White, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReferenceModal = false }) {
                    Text("Fermer", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Modal: Confirmation de suppression
    if (itemToDeleteType != null && itemToDeleteId != null) {
        AlertDialog(
            onDismissRequest = {
                itemToDeleteType = null
                itemToDeleteId = null
                itemToDeleteName = null
            },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = if (itemToDeleteType == "boutique") "Supprimer la boutique ?" else "Supprimer le produit ?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Voulez-vous vraiment supprimer « ${itemToDeleteName ?: ""} » ? Cette action est irréversible.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (itemToDeleteType == "boutique") {
                            onDeleteBoutique(itemToDeleteId!!)
                        } else {
                            onDeleteProduct(itemToDeleteId!!)
                        }
                        itemToDeleteType = null
                        itemToDeleteId = null
                        itemToDeleteName = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Supprimer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        itemToDeleteType = null
                        itemToDeleteId = null
                        itemToDeleteName = null
                    }
                ) {
                    Text("Annuler", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 0: LISTE DES BOUTIQUES
// -------------------------------------------------------------
@Composable
private fun BoutiquesTabContent(
    boutiques: List<BoutiqueDto>,
    products: List<ProductDto>,
    onCreateClick: () -> Unit,
    onEditClick: (BoutiqueDto) -> Unit,
    onDeleteClick: (BoutiqueDto) -> Unit,
    onViewProducts: (BoutiqueDto) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Vos Boutiques Shopping",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Chaque produit publié doit être rattaché à une boutique",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = onCreateClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nouvelle", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }

        if (boutiques.isEmpty()) {
            item {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFFFF6600).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = Color(0xFFFF6600), modifier = Modifier.size(32.dp))
                        }
                        Text(
                            text = "Aucune boutique créée",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Créez votre première boutique pour commencer à publier des articles sur la marketplace CashPay Shopping.",
                            fontFamily = MulishFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = onCreateClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Créer ma première boutique", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(boutiques) { b ->
                val countProducts = products.count { it.boutiqueId == b.id }
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                        .size(38.dp)
                                        .background(Color(0xFF0F172A), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Store, contentDescription = null, tint = Color(0xFFFF6600), modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text(
                                        text = b.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White,
                                        fontFamily = MulishFontFamily
                                    )
                                    Text(
                                        text = "ID: ${b.id}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        fontFamily = MulishFontFamily
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFF059669).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "$countProducts produit(s)",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF34D399),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        b.address?.let { addr ->
                            if (addr.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color(0xFFFF6600),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = addr,
                                        fontSize = 12.5.sp,
                                        color = Color(0xFFCBD5E1),
                                        fontFamily = MulishFontFamily
                                    )
                                }
                            }
                        }

                        b.whatsappPublicNumber?.let { wa ->
                            if (wa.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Whatsapp, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(15.dp))
                                    Text(
                                        text = wa,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF25D366),
                                        fontFamily = MulishFontFamily
                                    )
                                }
                            }
                        }

                        Divider(color = Color(0xFF334155), thickness = 0.8.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { onViewProducts(b) },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                            ) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Voir les produits", color = Color(0xFF38BDF8), fontSize = 12.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { onEditClick(b) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = Color(0xFFFF9900), modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { onDeleteClick(b) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: LISTE DES PRODUITS DE L'AGENT
// -------------------------------------------------------------
@Composable
private fun ProductsTabContent(
    products: List<ProductDto>,
    boutiques: List<BoutiqueDto>,
    selectedBoutiqueId: String?,
    onSelectBoutiqueFilter: (String?) -> Unit,
    onPublishNewClick: () -> Unit,
    onEditProduct: (ProductDto) -> Unit,
    onDeleteProduct: (ProductDto) -> Unit,
    onViewReference: (ProductDto) -> Unit
) {
    val filteredProducts = if (selectedBoutiqueId == null) {
        products
    } else {
        products.filter { it.boutiqueId == selectedBoutiqueId }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Catalogue de vos Produits",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "${filteredProducts.size} produit(s) répertorié(s)",
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = onPublishNewClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Publier", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }

        // Filter chips row
        if (boutiques.isNotEmpty()) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChipShopping(
                            label = "Toutes les boutiques",
                            isSelected = selectedBoutiqueId == null,
                            onClick = { onSelectBoutiqueFilter(null) }
                        )
                    }
                    items(boutiques) { b ->
                        FilterChipShopping(
                            label = b.name,
                            isSelected = selectedBoutiqueId == b.id,
                            onClick = { onSelectBoutiqueFilter(b.id) }
                        )
                    }
                }
            }
        }

        if (filteredProducts.isEmpty()) {
            item {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFF38BDF8).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(32.dp))
                        }
                        Text(
                            text = "Aucun produit trouvé",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Publiez un nouveau produit avec prix d'achat, prix de vente, images et variations.",
                            fontFamily = MulishFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = onPublishNewClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publier un produit maintenant", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(filteredProducts) { product ->
                ProductAgentCard(
                    product = product,
                    boutiques = boutiques,
                    onEdit = { onEditProduct(product) },
                    onDelete = { onDeleteProduct(product) },
                    onViewReference = { onViewReference(product) }
                )
            }
        }
    }
}

@Composable
private fun FilterChipShopping(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color(0xFFFF6600) else Color(0xFF1E293B),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFFFF6600) else Color(0xFF334155))
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFFCBD5E1),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun ProductAgentCard(
    product: ProductDto,
    boutiques: List<BoutiqueDto>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewReference: () -> Unit
) {
    val boutiqueName = boutiques.find { it.id == product.boutiqueId }?.name
        ?: product.boutiqueName
        ?: product.boutique?.name
        ?: "Boutique #${product.boutiqueId ?: "?"}"

    val category = SHOPPING_CATEGORIES.find { it.id == product.categoryId }?.nameFr ?: product.categoryId

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image or Placeholder
                val imgUrl = product.images.firstOrNull() ?: product.image
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!imgUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = imgUrl,
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(32.dp))
                    }
                }

                // Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (product.isPublic) {
                            Surface(color = Color(0xFF059669).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text("Public", fontSize = 10.5.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        } else {
                            Surface(color = Color(0xFF64748B).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text("Privé", fontSize = 10.5.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "$boutiqueName • $category",
                            fontSize = 11.5.sp,
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${product.sellingPrice} ${product.currency}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color(0xFF00E676)
                        )

                        Text(
                            text = "Stock : ${product.stock}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (product.stock > 0) Color(0xFF38BDF8) else Color(0xFFEF4444)
                        )
                    }
                }
            }

            // Margin & SKU Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SKU: ${product.barcode ?: "N/A"}",
                    fontSize = 11.sp,
                    color = Color(0xFFFDE68A),
                    fontWeight = FontWeight.Medium
                )

                if (product.purchasePrice != null && product.purchasePrice > 0) {
                    val margin = product.sellingPrice - product.purchasePrice
                    val marginPercent = if (product.purchasePrice > 0) (margin / product.purchasePrice) * 100 else 0.0
                    Text(
                        text = "Marge : +${String.format(Locale.US, "%.1f", margin)} $ (%.0f%%)".format(marginPercent),
                        fontSize = 11.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(12.dp)
                        )
                        Text("${product.viewCount} vues", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(12.dp)
                        )
                        Text("${product.orderCount} cmd", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                    if (product.ratingAverage > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(12.dp))
                            Text(String.format(Locale.US, "%.1f", product.ratingAverage), fontSize = 11.sp, color = Color(0xFFFDE68A), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = onViewReference, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.QrCode, contentDescription = "Référence", tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = Color(0xFFFF9900), modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: PUBLIER / MODIFIER UN PRODUIT
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PublishProductTabContent(
    boutiques: List<BoutiqueDto>,
    editingProduct: ProductDto?,
    isLoading: Boolean,
    onCreateBoutiqueClick: () -> Unit,
    onSubmitPublish: (PublishProductRequest) -> Unit
) {
    val context = LocalContext.current

    // Form fields
    var name by remember(editingProduct) { mutableStateOf(editingProduct?.name ?: "") }
    var description by remember(editingProduct) { mutableStateOf(editingProduct?.description ?: "") }
    var categoryId by remember(editingProduct) { mutableStateOf(editingProduct?.categoryId ?: "cat-vetements") }
    var boutiqueId by remember(editingProduct, boutiques) {
        mutableStateOf(editingProduct?.boutiqueId ?: boutiques.firstOrNull()?.id ?: "")
    }

    LaunchedEffect(boutiques) {
        if (boutiqueId.isBlank() && boutiques.isNotEmpty()) {
            boutiqueId = boutiques.first().id
        }
    }
    var barcode by remember(editingProduct) { mutableStateOf(editingProduct?.barcode ?: "") }
    var purchasePrice by remember(editingProduct) { mutableStateOf(editingProduct?.purchasePrice?.toString() ?: "") }
    var sellingPrice by remember(editingProduct) { mutableStateOf(editingProduct?.sellingPrice?.let { if (it > 0) it.toString() else "" } ?: "") }
    var weight by remember(editingProduct) { mutableStateOf(editingProduct?.weight?.toString() ?: "0.5") }
    var stock by remember(editingProduct) { mutableStateOf(editingProduct?.stock?.toString() ?: "10") }
    var isPublic by remember(editingProduct) { mutableStateOf(editingProduct?.isPublic ?: true) }

    // Images URLs
    var imageUrlInput by remember { mutableStateOf("") }
    val imagesList = remember(editingProduct) {
        mutableStateListOf<String>().apply {
            if (editingProduct != null && editingProduct.images.isNotEmpty()) {
                addAll(editingProduct.images)
            } else if (editingProduct?.image != null) {
                add(editingProduct.image)
            }
        }
    }

    // Variations
    val variationsList = remember(editingProduct) {
        mutableStateListOf<ProductVariationDto>().apply {
            if (editingProduct != null && editingProduct.variations.isNotEmpty()) {
                addAll(editingProduct.variations)
            } else {
                add(ProductVariationDto(color = "Standard", stock = 10))
            }
        }
    }
    var newVarColor by remember { mutableStateOf("") }
    var newVarStock by remember { mutableStateOf("5") }

    // Shipping Rates
    val shippingRatesList = remember(editingProduct) {
        mutableStateListOf<ShippingRateDto>().apply {
            if (editingProduct != null && editingProduct.shippingRates.isNotEmpty()) {
                addAll(editingProduct.shippingRates)
            }
        }
    }
    var newShipCountry by remember { mutableStateOf("CD") }
    var newShipProvince by remember { mutableStateOf("Kinshasa") }
    var newShipRate by remember { mutableStateOf("5.0") }

    // Promotional packages
    val promoPackagesList = remember(editingProduct) {
        mutableStateListOf<PromotionalPackageDto>().apply {
            if (editingProduct != null && editingProduct.promotionalPackages.isNotEmpty()) {
                addAll(editingProduct.promotionalPackages)
            }
        }
    }
    var newPromoQty by remember { mutableStateOf("2") }
    var newPromoPrice by remember { mutableStateOf("") }

    // Custom Details (Category specific: Book, electronics, etc.)
    var brand by remember(editingProduct) { mutableStateOf(editingProduct?.details?.brand ?: "") }
    var model by remember(editingProduct) { mutableStateOf(editingProduct?.details?.model ?: "") }
    var memory by remember(editingProduct) { mutableStateOf(editingProduct?.details?.memory ?: "") }
    var isbn10 by remember(editingProduct) { mutableStateOf(editingProduct?.details?.isbn10 ?: "") }
    var isbn13 by remember(editingProduct) { mutableStateOf(editingProduct?.details?.isbn13 ?: "") }
    var pdfUrl by remember(editingProduct) { mutableStateOf(editingProduct?.details?.pdfUrl ?: "") }
    var sourceUrl by remember(editingProduct) { mutableStateOf(editingProduct?.details?.sourceUrl ?: "") }
    var audioUrl by remember(editingProduct) { mutableStateOf(editingProduct?.details?.audioUrl ?: "") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var boutiqueDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (editingProduct != null) "Modifier le Produit #${editingProduct.id}" else "Publier un Nouveau Produit",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.White
            )
            Text(
                text = "Remplissez les détails financiers, techniques et logistiques.",
                fontFamily = MulishFontFamily,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        // Section: Boutique Selector
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Boutique de Rattachement *", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.5.sp)
                    
                    if (boutiques.isEmpty()) {
                        Surface(color = Color(0xFFF59E0B).copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                Text("Vous devez d'abord créer une boutique.", color = Color(0xFFFDE68A), fontSize = 12.sp)
                            }
                        }
                        Button(
                            onClick = onCreateBoutiqueClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Créer une boutique", fontSize = 12.sp)
                        }
                    } else {
                        val selectedBoutique = boutiques.find { it.id == boutiqueId } ?: boutiques.firstOrNull()
                        ExposedDropdownMenuBox(
                            expanded = boutiqueDropdownExpanded,
                            onExpandedChange = { boutiqueDropdownExpanded = !boutiqueDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedBoutique?.name ?: "Sélectionnez une boutique",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Sélectionnez la boutique") },
                                leadingIcon = {
                                    Icon(Icons.Default.Store, contentDescription = null, tint = Color(0xFFFF6600), modifier = Modifier.size(18.dp))
                                },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = boutiqueDropdownExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFFFF6600),
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedContainerColor = Color(0xFF0F172A),
                                    unfocusedContainerColor = Color(0xFF0F172A)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = boutiqueDropdownExpanded,
                                onDismissRequest = { boutiqueDropdownExpanded = false },
                                modifier = Modifier.background(Color(0xFF1E293B))
                            ) {
                                boutiques.forEach { b ->
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(Icons.Default.Store, contentDescription = null, tint = Color(0xFFFF6600), modifier = Modifier.size(16.dp))
                                        },
                                        text = { Text(b.name, color = Color.White) },
                                        onClick = {
                                            boutiqueId = b.id
                                            boutiqueDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Informations Générales
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Informations Générales", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.5.sp)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nom du produit (min 3 car.) *") },
                        placeholder = { Text("Ex : Smartphone XYZ 256GB") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF6600),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description complète du produit") },
                        placeholder = { Text("Détails, spécifications techniques, garantie...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF6600),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Category Selector
                    val selectedCat = SHOPPING_CATEGORIES.find { it.id == categoryId } ?: SHOPPING_CATEGORIES.first()
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCat.nameFr,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Catégorie *") },
                            leadingIcon = {
                                Icon(getCategoryIcon(selectedCat.id), contentDescription = null, tint = Color(0xFFFF6600), modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF6600),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false },
                            modifier = Modifier.background(Color(0xFF1E293B))
                        ) {
                            SHOPPING_CATEGORIES.forEach { cat ->
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(getCategoryIcon(cat.id), contentDescription = null, tint = Color(0xFFFF6600), modifier = Modifier.size(18.dp))
                                    },
                                    text = { Text(cat.nameFr, color = Color.White) },
                                    onClick = {
                                        categoryId = cat.id
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Code-barres / SKU *") },
                        placeholder = { Text("Ex : SKU-XYZ-001") },
                        leadingIcon = {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF6600),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // Section: Prix & Stock
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Prix & Stock", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.5.sp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = purchasePrice,
                            onValueChange = { purchasePrice = it },
                            label = { Text("Prix d'achat ($)") },
                            placeholder = { Text("Ex : 20.0") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF6600),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = sellingPrice,
                            onValueChange = { sellingPrice = it },
                            label = { Text("Prix vente ($) *") },
                            placeholder = { Text("Ex : 30.0") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF00E676),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Real-time margin preview
                    val pBuy = purchasePrice.toDoubleOrNull() ?: 0.0
                    val pSell = sellingPrice.toDoubleOrNull() ?: 0.0
                    if (pSell > 0) {
                        val marginVal = pSell - pBuy
                        val marginPct = if (pBuy > 0) (marginVal / pBuy) * 100 else 100.0
                        Surface(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Marge brute estimée :", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                Text(
                                    "${String.format(Locale.US, "%.2f", marginVal)} $ (${String.format(Locale.US, "%.0f", marginPct)}%)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (marginVal >= 0) Color(0xFF00E676) else Color(0xFFEF4444)
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = stock,
                            onValueChange = { stock = it },
                            label = { Text("Stock total *") },
                            placeholder = { Text("Ex : 10") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF6600),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text("Poids (kg)") },
                            placeholder = { Text("Ex : 0.8") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF6600),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Section: Images (1 à 5)
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Images du Produit (${imagesList.size}/5) *", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.5.sp)
                        Text("Min 1, Max 5", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = imageUrlInput,
                            onValueChange = { imageUrlInput = it },
                            placeholder = { Text("URL de l'image (https://...)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF6600),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = {
                                if (imageUrlInput.isNotBlank() && imagesList.size < 5) {
                                    imagesList.add(imageUrlInput.trim())
                                    imageUrlInput = ""
                                }
                            },
                            enabled = imageUrlInput.isNotBlank() && imagesList.size < 5,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ajouter")
                        }
                    }

                    // Predefined sample images helper for quick agent publishing
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(
                            onClick = {
                                if (imagesList.size < 5) imagesList.add("https://picsum.photos/400/400?random=${System.currentTimeMillis() % 100}")
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("+ Image Démo", color = Color(0xFF38BDF8), fontSize = 11.5.sp)
                        }
                    }

                    // Display added images thumbnails
                    if (imagesList.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(imagesList) { img ->
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF0F172A))
                                ) {
                                    AsyncImage(
                                        model = img,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { imagesList.remove(img) },
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.TopEnd)
                                            .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Variations (Couleurs / Tailles & Stock)
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Variations (Couleur & Stock) *", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.5.sp)

                    // Add variation row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newVarColor,
                            onValueChange = { newVarColor = it },
                            placeholder = { Text("Ex : Noir / XL") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF6600),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = newVarStock,
                            onValueChange = { newVarStock = it },
                            placeholder = { Text("Stock") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF6600),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = {
                                if (newVarColor.isNotBlank()) {
                                    val st = newVarStock.toIntOrNull() ?: 0
                                    variationsList.add(ProductVariationDto(color = newVarColor.trim(), stock = st))
                                    newVarColor = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ajouter", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Display variations
                    variationsList.forEachIndexed { idx, v ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${v.color} • ${v.stock} en stock", color = Color.White, fontSize = 12.5.sp)
                            if (variationsList.size > 1) {
                                IconButton(onClick = { variationsList.removeAt(idx) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Tarifs de Livraison (Optionnel)
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Tarifs de Livraison par Province", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.5.sp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newShipProvince,
                            onValueChange = { newShipProvince = it },
                            placeholder = { Text("Province") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF6600),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = newShipRate,
                            onValueChange = { newShipRate = it },
                            placeholder = { Text("Tarif ($)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF6600),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = {
                                if (newShipProvince.isNotBlank()) {
                                    val r = newShipRate.toDoubleOrNull() ?: 0.0
                                    shippingRatesList.add(ShippingRateDto(country = newShipCountry, province = newShipProvince.trim(), rate = r))
                                    newShipProvince = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+", fontWeight = FontWeight.Bold)
                        }
                    }

                    shippingRatesList.forEachIndexed { idx, s ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(15.dp)
                                )
                                Text("${s.province} (${s.country}) : ${s.rate} $", color = Color.White, fontSize = 12.sp)
                            }
                            IconButton(onClick = { shippingRatesList.removeAt(idx) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }

        // Section: Détails Spécifiques / Livres / Électronique
        if (categoryId == "cat-electronique" || categoryId == "cat-livre") {
            item {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (categoryId == "cat-livre") "Détails Livre & Média" else "Spécifications Techniques",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.5.sp
                        )

                        if (categoryId == "cat-electronique") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = brand,
                                    onValueChange = { brand = it },
                                    label = { Text("Marque") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF0F172A), unfocusedContainerColor = Color(0xFF0F172A)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                OutlinedTextField(
                                    value = model,
                                    onValueChange = { model = it },
                                    label = { Text("Modèle") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF0F172A), unfocusedContainerColor = Color(0xFF0F172A)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                            OutlinedTextField(
                                value = memory,
                                onValueChange = { memory = it },
                                label = { Text("Mémoire / Stockage") },
                                placeholder = { Text("Ex : 256GB / 8GB RAM") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF0F172A), unfocusedContainerColor = Color(0xFF0F172A)),
                                shape = RoundedCornerShape(8.dp)
                            )
                        } else if (categoryId == "cat-livre") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = isbn10,
                                    onValueChange = { isbn10 = it },
                                    label = { Text("ISBN-10") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF0F172A), unfocusedContainerColor = Color(0xFF0F172A)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                OutlinedTextField(
                                    value = isbn13,
                                    onValueChange = { isbn13 = it },
                                    label = { Text("ISBN-13") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF0F172A), unfocusedContainerColor = Color(0xFF0F172A)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                            OutlinedTextField(
                                value = pdfUrl,
                                onValueChange = { pdfUrl = it },
                                label = { Text("Lien PDF (Optionnel)") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF0F172A), unfocusedContainerColor = Color(0xFF0F172A)),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section: Visibilité publique
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Publication Publique", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.5.sp)
                        Text(
                            "Le produit sera immédiatement visible sur le catalogue public.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00E676),
                            checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.3f),
                            uncheckedThumbColor = Color(0xFF64748B),
                            uncheckedTrackColor = Color(0xFF334155)
                        )
                    )
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    if (boutiques.isEmpty() || boutiqueId.isBlank()) {
                        Toast.makeText(context, "Veuillez d'abord créer ou sélectionner une boutique.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (name.trim().length < 3) {
                        Toast.makeText(context, "Le nom du produit doit contenir au moins 3 caractères.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (barcode.trim().isBlank()) {
                        Toast.makeText(context, "Le code-barres / SKU est obligatoire.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val sellPr = sellingPrice.toDoubleOrNull() ?: 0.0
                    if (sellPr < 0.01) {
                        Toast.makeText(context, "Le prix de vente doit être supérieur ou égal à 0.01 $.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (imagesList.isEmpty()) {
                        imagesList.add("https://picsum.photos/400/400?random=${System.currentTimeMillis() % 100}")
                    }
                    if (variationsList.isEmpty()) {
                        variationsList.add(ProductVariationDto(color = "Standard", stock = stock.toIntOrNull() ?: 1))
                    }

                    val detailsObj = ProductDetailsDto(
                        brand = brand.trim().ifBlank { null },
                        model = model.trim().ifBlank { null },
                        memory = memory.trim().ifBlank { null },
                        isbn10 = isbn10.trim().ifBlank { null },
                        isbn13 = isbn13.trim().ifBlank { null },
                        pdfUrl = pdfUrl.trim().ifBlank { null },
                        sourceUrl = sourceUrl.trim().ifBlank { null },
                        audioUrl = audioUrl.trim().ifBlank { null }
                    )

                    val req = PublishProductRequest(
                        name = name.trim(),
                        description = description.trim(),
                        categoryId = categoryId,
                        boutiqueId = boutiqueId,
                        purchasePrice = purchasePrice.toDoubleOrNull(),
                        sellingPrice = sellPr,
                        barcode = barcode.trim(),
                        images = imagesList.toList(),
                        isPublic = isPublic,
                        weight = weight.toDoubleOrNull() ?: 0.5,
                        stock = stock.toIntOrNull() ?: 1,
                        variations = variationsList.toList(),
                        shippingRates = shippingRatesList.toList(),
                        promotionalPackages = promoPackagesList.toList(),
                        details = detailsObj
                    )

                    onSubmitPublish(req)
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Icon(if (editingProduct != null) Icons.Default.Check else Icons.Default.Store, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (editingProduct != null) "Mettre à jour le Produit" else "Publier le Produit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// -------------------------------------------------------------
// TAB 3: APERÇU CATALOGUE PUBLIC & STATISTIQUES
// -------------------------------------------------------------
@Composable
private fun PublicCatalogTabContent(
    publicCatalog: com.example.data.model.PublicCatalogResponse?,
    isLoading: Boolean,
    boutiques: List<BoutiqueDto>,
    onRefreshCatalog: (storeId: String?) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedStoreId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        onRefreshCatalog(null)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Aperçu Catalogue Public", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            Text("Ce que vos acheteurs découvrent en ligne", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        }

                        IconButton(onClick = { onRefreshCatalog(selectedStoreId) }) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF38BDF8))
                        }
                    }

                    // Boutique Filter
                    if (boutiques.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChipShopping(
                                label = "Tous les magasins",
                                isSelected = selectedStoreId == null,
                                onClick = {
                                    selectedStoreId = null
                                    onRefreshCatalog(null)
                                }
                            )
                            boutiques.forEach { b ->
                                FilterChipShopping(
                                    label = b.name,
                                    isSelected = selectedStoreId == b.id,
                                    onClick = {
                                        selectedStoreId = b.id
                                        onRefreshCatalog(b.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF6600))
                }
            }
        } else if (publicCatalog == null || publicCatalog.products.isEmpty()) {
            item {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(32.dp))
                        Text("Aucun article public dans cette sélection", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(publicCatalog.products) { p ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val img = p.images.firstOrNull() ?: p.image
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!img.isNullOrBlank()) {
                                AsyncImage(model = img, contentDescription = null, modifier = Modifier.fillMaxSize())
                            } else {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF64748B))
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text("${p.sellingPrice} ${p.currency}", fontWeight = FontWeight.ExtraBold, color = Color(0xFF00E676), fontSize = 13.5.sp)
                            Text("En stock : ${p.stock}", color = Color(0xFF94A3B8), fontSize = 11.5.sp)
                        }

                        p.seller?.let { sel ->
                            sel.walletId?.let { wid ->
                                Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(6.dp)) {
                                    Text(wid.take(8) + "...", color = Color(0xFF38BDF8), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getCategoryIcon(catId: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (catId) {
        "cat-vetements" -> Icons.Default.Checkroom
        "cat-electronique" -> Icons.Default.Devices
        "cat-alimentaire" -> Icons.Default.Fastfood
        "cat-beaute" -> Icons.Default.Spa
        "cat-maison" -> Icons.Default.Home
        "cat-livre" -> Icons.Default.MenuBook
        else -> Icons.Default.Category
    }
}
