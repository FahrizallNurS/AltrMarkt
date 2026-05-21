package com.altermarkt.app.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<HomeProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): HomeProductEntity?

    @Query("SELECT * FROM products WHERE category = :category")
    fun getProductByCategory(category: String): Flow<List<HomeProductEntity>>

    @Query("SELECT * FROM products WHERE title LIKE '%' || :query || '%'")
    fun searchProduct(query: String): Flow<List<HomeProductEntity>>

    @Upsert
    suspend fun upsertProducts(products: List<HomeProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clearAll()
}