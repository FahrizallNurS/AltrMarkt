package com.altermarkt.app.data.repository

import com.altermarkt.app.data.local.AppDatabase
import com.altermarkt.app.data.local.HomeProductEntity
import com.altermarkt.app.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeProductRepository(private val db: AppDatabase) {

    private val dao = db.homeProductDao()

    fun getAllProducts(): Flow<List<Product>> =
        dao.getAllProducts().map { list -> list.map { it.toDomain() } }

    fun getProductsByCategory(category: String): Flow<List<Product>> =
        dao.getProductByCategory(category).map { list -> list.map { it.toDomain() } }

    fun searchProducts(query: String): Flow<List<Product>> =
        dao.searchProduct(query).map { list -> list.map { it.toDomain() } }

    suspend fun getProductById(id: String): Product? =
        dao.getProductById(id)?.toDomain()

    private fun HomeProductEntity.toDomain() = Product(
        id          = id,
        title       = title,
        price       = price,
        description = description,
        category    = category,
        imageUrl    = imageUrl,
        sellerId    = sellerId,
        sellerName  = sellerName,
        isAvailable = isAvailable,
        viewCount   = viewCount,
        likeCount   = likeCount,
        createdAt   = createdAt
    )
}