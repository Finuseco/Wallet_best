package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- BOUTIQUE MODELS ---

@JsonClass(generateAdapter = true)
data class BoutiqueDto(
    @Json(name = "id") val rawId: Any? = null,
    @Json(name = "name") val directName: String? = null,
    @Json(name = "nom") val nom: String? = null,
    @Json(name = "boutiqueName") val boutiqueName: String? = null,
    @Json(name = "storeName") val storeName: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "adresse") val adresse: String? = null,
    @Json(name = "whatsapp_public_number") val whatsappPublicNumber: String? = null,
    @Json(name = "whatsappPublicNumber") val whatsappPublicNumberCamel: String? = null,
    @Json(name = "whatsapp") val whatsapp: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "telephone") val telephone: String? = null
) {
    val id: String
        get() = when (val v = rawId) {
            is Number -> if (v.toDouble() % 1.0 == 0.0) v.toLong().toString() else v.toString()
            is String -> v
            else -> v?.toString().orEmpty()
        }

    val name: String
        get() = directName?.ifBlank { null }
            ?: nom?.ifBlank { null }
            ?: boutiqueName?.ifBlank { null }
            ?: storeName?.ifBlank { null }
            ?: title?.ifBlank { null }
            ?: "Boutique"

    val displayAddress: String?
        get() = address?.ifBlank { null } ?: adresse?.ifBlank { null }

    val displayWhatsappNumber: String?
        get() = whatsappPublicNumber?.ifBlank { null }
            ?: whatsappPublicNumberCamel?.ifBlank { null }
            ?: whatsapp?.ifBlank { null }
            ?: phone?.ifBlank { null }
            ?: telephone?.ifBlank { null }

    constructor(
        rawId: Any? = null,
        name: String = "",
        address: String? = null,
        whatsappPublicNumber: String? = null
    ) : this(
        rawId = rawId,
        directName = name,
        address = address,
        whatsappPublicNumber = whatsappPublicNumber
    )
}

@JsonClass(generateAdapter = true)
data class BoutiquesResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "boutiques") val boutiquesList: List<BoutiqueDto>? = null,
    @Json(name = "data") val dataList: List<BoutiqueDto>? = null,
    @Json(name = "stores") val storesList: List<BoutiqueDto>? = null,
    @Json(name = "shops") val shopsList: List<BoutiqueDto>? = null,
    @Json(name = "items") val itemsList: List<BoutiqueDto>? = null,
    @Json(name = "boutique") val singleBoutique: BoutiqueDto? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
) {
    val boutiques: List<BoutiqueDto>
        get() = boutiquesList ?: dataList ?: storesList ?: shopsList ?: itemsList
            ?: singleBoutique?.let { listOf(it) } ?: emptyList()
}

fun parseSingleBoutiqueFromJson(raw: String?): BoutiqueDto? {
    if (raw.isNullOrBlank()) return null
    return try {
        val trimmed = raw.trim()
        if (trimmed.startsWith("{")) {
            val obj = org.json.JSONObject(trimmed)
            val inner = obj.optJSONObject("boutique")
                ?: obj.optJSONObject("data")
                ?: obj.optJSONObject("store")
                ?: obj
            parseSingleBoutiqueFromJsonObject(inner)
        } else null
    } catch (_: Exception) {
        null
    }
}

fun parseSingleBoutiqueFromJsonObject(obj: org.json.JSONObject): BoutiqueDto? {
    val id = obj.optString("id").ifBlank { obj.optString("_id") }.ifBlank {
        val num = obj.optLong("id", 0L)
        if (num > 0L) num.toString() else ""
    }
    val name = obj.optString("name").ifBlank { obj.optString("nom") }
        .ifBlank { obj.optString("boutiqueName") }.ifBlank { obj.optString("storeName") }
        .ifBlank { obj.optString("title") }
    if (id.isBlank() && name.isBlank()) return null
    val address = obj.optString("address").ifBlank { obj.optString("adresse") }.ifBlank { null }
    val whatsapp = obj.optString("whatsapp_public_number").ifBlank { obj.optString("whatsappPublicNumber") }
        .ifBlank { obj.optString("whatsapp") }.ifBlank { obj.optString("phone") }.ifBlank { null }
    return BoutiqueDto(
        rawId = id,
        directName = name.ifBlank { "Boutique #$id" },
        address = address,
        whatsappPublicNumber = whatsapp
    )
}

fun parseBoutiquesFromJson(rawJson: String?): List<BoutiqueDto> {
    if (rawJson.isNullOrBlank()) return emptyList()
    val list = mutableListOf<BoutiqueDto>()
    try {
        val trimmed = rawJson.trim()
        if (trimmed.startsWith("[")) {
            val array = org.json.JSONArray(trimmed)
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                parseSingleBoutiqueFromJsonObject(obj)?.let { list.add(it) }
            }
        } else if (trimmed.startsWith("{")) {
            val json = org.json.JSONObject(trimmed)
            val array = json.optJSONArray("boutiques")
                ?: json.optJSONArray("data")
                ?: json.optJSONArray("stores")
                ?: json.optJSONArray("shops")
                ?: json.optJSONArray("items")
                ?: json.optJSONArray("results")
            if (array != null) {
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i) ?: continue
                    parseSingleBoutiqueFromJsonObject(obj)?.let { list.add(it) }
                }
            } else {
                val singleObj = json.optJSONObject("boutique")
                    ?: json.optJSONObject("store")
                    ?: json.optJSONObject("data")
                if (singleObj != null) {
                    parseSingleBoutiqueFromJsonObject(singleObj)?.let { list.add(it) }
                } else if (json.has("name") || json.has("nom") || json.has("boutiqueName")) {
                    parseSingleBoutiqueFromJsonObject(json)?.let { list.add(it) }
                }
            }
        }
    } catch (_: Exception) {}
    return list
}

@JsonClass(generateAdapter = true)
data class BoutiqueResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "boutique") val boutiqueItem: BoutiqueDto? = null,
    @Json(name = "data") val dataItem: BoutiqueDto? = null,
    @Json(name = "id") val idRaw: Any? = null,
    @Json(name = "name") val directName: String? = null,
    @Json(name = "address") val directAddress: String? = null,
    @Json(name = "whatsapp_public_number") val directWhatsapp: String? = null,
    @Json(name = "whatsappPublicNumber") val directWhatsappCamel: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
) {
    val boutique: BoutiqueDto?
        get() = boutiqueItem ?: dataItem ?: if (!directName.isNullOrBlank()) {
            BoutiqueDto(
                rawId = idRaw,
                name = directName,
                address = directAddress,
                whatsappPublicNumber = directWhatsapp ?: directWhatsappCamel
            )
        } else null
}

@JsonClass(generateAdapter = true)
data class CreateBoutiqueRequest(
    @Json(name = "name") val name: String,
    @Json(name = "address") val address: String? = null,
    @Json(name = "whatsapp") val whatsapp: String? = null
)

// --- PRODUCT MODELS ---

@JsonClass(generateAdapter = true)
data class ProductVariationDto(
    @Json(name = "color") val color: String = "",
    @Json(name = "stock") val stock: Int = 0
)

@JsonClass(generateAdapter = true)
data class ShippingRateDto(
    @Json(name = "country") val country: String = "CD",
    @Json(name = "province") val province: String = "",
    @Json(name = "rate") val rate: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class PromotionalPackageDto(
    @Json(name = "quantity") val quantity: Int = 0,
    @Json(name = "price") val price: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class ProductDetailsDto(
    @Json(name = "brand") val brand: String? = null,
    @Json(name = "model") val model: String? = null,
    @Json(name = "memory") val memory: String? = null,
    @Json(name = "isbn10") val isbn10: String? = null,
    @Json(name = "isbn13") val isbn13: String? = null,
    @Json(name = "pdfUrl") val pdfUrl: String? = null,
    @Json(name = "sourceUrl") val sourceUrl: String? = null,
    @Json(name = "audioUrl") val audioUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class ProductSellerDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "walletId") val walletId: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "countryCode") val countryCode: String? = null
)

@JsonClass(generateAdapter = true)
data class ProductBoutiqueRefDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "whatsappPublicNumber") val whatsappPublicNumber: String? = null
)

@JsonClass(generateAdapter = true)
data class ProductDto(
    @Json(name = "id") val rawId: Any? = null,
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "image") val image: String? = null,
    @Json(name = "images") val images: List<String> = emptyList(),
    @Json(name = "purchasePrice") val purchasePrice: Double? = null,
    @Json(name = "sellingPrice") val sellingPrice: Double = 0.0,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "weight") val weight: Double = 1.0,
    @Json(name = "shippingRates") val shippingRates: List<ShippingRateDto> = emptyList(),
    @Json(name = "promotionalPackages") val promotionalPackages: List<PromotionalPackageDto> = emptyList(),
    @Json(name = "variations") val variations: List<ProductVariationDto> = emptyList(),
    @Json(name = "barcode") val barcode: String? = null,
    @Json(name = "stock") val stock: Int = 0,
    @Json(name = "categoryId") val categoryId: String = "cat-vetements",
    @Json(name = "boutiqueId") val rawBoutiqueId: Any? = null,
    @Json(name = "boutiqueName") val boutiqueName: String? = null,
    @Json(name = "boutique") val boutique: ProductBoutiqueRefDto? = null,
    @Json(name = "isPublic") val isPublic: Boolean = true,
    @Json(name = "details") val details: ProductDetailsDto? = null,
    @Json(name = "seller") val seller: ProductSellerDto? = null,
    @Json(name = "viewCount") val viewCount: Int = 0,
    @Json(name = "orderCount") val orderCount: Int = 0,
    @Json(name = "commentCount") val commentCount: Int = 0,
    @Json(name = "reviewCount") val reviewCount: Int = 0,
    @Json(name = "ratingAverage") val ratingAverage: Double = 0.0,
    @Json(name = "createdAt") val createdAt: String? = null
) {
    val id: String
        get() = when (val v = rawId) {
            is Number -> if (v.toDouble() % 1.0 == 0.0) v.toLong().toString() else v.toString()
            is String -> v
            else -> v?.toString().orEmpty()
        }

    val boutiqueId: String?
        get() = when (val v = rawBoutiqueId) {
            is Number -> if (v.toDouble() % 1.0 == 0.0) v.toLong().toString() else v.toString()
            is String -> v
            else -> v?.toString()
        } ?: boutique?.id
}

@JsonClass(generateAdapter = true)
data class ShoppingContextResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "country") val country: String? = null,
    @Json(name = "countryCode") val countryCode: String? = null,
    @Json(name = "boutiques") val boutiquesList: List<BoutiqueDto>? = null,
    @Json(name = "data") val dataList: List<BoutiqueDto>? = null,
    @Json(name = "products") val products: List<ProductDto> = emptyList(),
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
) {
    val boutiques: List<BoutiqueDto>
        get() = boutiquesList ?: dataList ?: emptyList()
}

@JsonClass(generateAdapter = true)
data class ProductResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "product") val product: ProductDto? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "countryCode") val countryCode: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ProductReferenceResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "reference") val reference: String = "",
    @Json(name = "productId") val productId: String = "",
    @Json(name = "name") val name: String? = null,
    @Json(name = "barcode") val barcode: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class PublishProductRequest(
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "categoryId") val categoryId: String,
    @Json(name = "boutiqueId") val boutiqueId: String,
    @Json(name = "purchasePrice") val purchasePrice: Double? = null,
    @Json(name = "sellingPrice") val sellingPrice: Double,
    @Json(name = "barcode") val barcode: String? = null,
    @Json(name = "images") val images: List<String> = emptyList(),
    @Json(name = "isPublic") val isPublic: Boolean = true,
    @Json(name = "weight") val weight: Double = 1.0,
    @Json(name = "stock") val stock: Int = 1,
    @Json(name = "variations") val variations: List<ProductVariationDto> = emptyList(),
    @Json(name = "shippingRates") val shippingRates: List<ShippingRateDto> = emptyList(),
    @Json(name = "promotionalPackages") val promotionalPackages: List<PromotionalPackageDto> = emptyList(),
    @Json(name = "details") val details: ProductDetailsDto? = null
)

@JsonClass(generateAdapter = true)
data class PublicCatalogResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "userId") val userId: String? = null,
    @Json(name = "storeId") val storeId: String? = null,
    @Json(name = "count") val count: Int = 0,
    @Json(name = "products") val products: List<ProductDto> = emptyList(),
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class GenericShoppingResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

// Categories catalog
data class ProductCategoryItem(
    val id: String,
    val nameFr: String,
    val iconName: String
)

val SHOPPING_CATEGORIES = listOf(
    ProductCategoryItem("cat-vetements", "Vêtements & Mode", "Checkroom"),
    ProductCategoryItem("cat-electronique", "Électronique & Téléphonie", "Devices"),
    ProductCategoryItem("cat-alimentaire", "Alimentation & Boissons", "Fastfood"),
    ProductCategoryItem("cat-beaute", "Beauté & Cosmétiques", "Spa"),
    ProductCategoryItem("cat-maison", "Maison & Décoration", "Home"),
    ProductCategoryItem("cat-livre", "Livres & Médias", "MenuBook"),
    ProductCategoryItem("cat-divers", "Divers & Accessoires", "Category")
)
