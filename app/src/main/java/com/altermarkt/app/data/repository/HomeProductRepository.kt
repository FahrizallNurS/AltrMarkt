package com.altermarkt.app.data.repository

import com.altermarkt.app.data.local.AppDatabase
import com.altermarkt.app.data.local.HomeProductEntity
import com.altermarkt.app.domain.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class HomeProductRepository(private val db: AppDatabase) {

    // Akses DAO untuk operasi database lokal
    private val dao = db.homeProductDao()

    // Akses Firestore untuk operasi database online
    private val firestore = FirebaseFirestore.getInstance()

    // Ambil semua produk dari Room sebagai Flow
    fun getAllProducts(): Flow<List<Product>> =
        dao.getAllProducts().map { list -> list.map { it.toDomain() } }

    // Ambil produk berdasarkan kategori dari Room
    fun getProductsByCategory(category: String): Flow<List<Product>> =
        dao.getProductByCategory(category).map { list -> list.map { it.toDomain() } }

    // Cari produk berdasarkan keyword dari Room
    fun searchProducts(query: String): Flow<List<Product>> =
        dao.searchProduct(query).map { list -> list.map { it.toDomain() } }

    // Ambil 1 produk berdasarkan ID dari Room
    suspend fun getProductById(id: String): Product? =
        dao.getProductById(id)?.toDomain()

    // Sync data dari Firestore ke Room (online → lokal)
    suspend fun syncFromFirestore() {
        val snapshot = firestore.collection("products").get().await()
        val entities = snapshot.documents.mapNotNull { doc ->
            HomeProductEntity(
                id          = doc.id,
                title       = doc.getString("title") ?: "",
                price       = doc.getLong("price")?.toInt() ?: 0,
                description = doc.getString("description") ?: "",
                category    = doc.getString("category") ?: "",
                imageUrl    = doc.getString("imageUrl") ?: "",
                sellerId    = doc.getString("sellerId") ?: "",
                sellerName  = doc.getString("sellerName") ?: "",
                sellerPhone = doc.getString("sellerPhone") ?: "",
                isAvailable = doc.getBoolean("isAvailable") ?: true,
                viewCount   = doc.getLong("viewCount")?.toInt() ?: 0,
                likeCount   = doc.getLong("likeCount")?.toInt() ?: 0,
                createdAt   = doc.getString("createdAt") ?: ""
            )
        }
        // Simpan ke Room (insert atau update jika sudah ada)
        dao.upsertProducts(entities)
    }

    // Konversi HomeProductEntity (Room) → Product (Domain Model)
    private fun HomeProductEntity.toDomain() = Product(
        id          = id,
        title       = title,
        price       = price,
        description = description,
        category    = category,
        imageUrl    = imageUrl,
        sellerId    = sellerId,
        sellerName  = sellerName,
        sellerPhone = sellerPhone,
        isAvailable = isAvailable,
        viewCount   = viewCount,
        likeCount   = likeCount,
        createdAt   = createdAt
    )
}