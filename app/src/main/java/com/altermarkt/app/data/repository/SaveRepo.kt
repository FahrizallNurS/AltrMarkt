package com.altermarkt.app.data.repository

import com.altermarkt.app.data.local.SavedProductDao
import com.altermarkt.app.data.local.SavedProductEntity
import kotlinx.coroutines.flow.Flow

class SaveRepo(private val savedProductDao: SavedProductDao) {

    // 1. Mengambil semua daftar favorit secara real-time
    fun getSavedProducts(): Flow<List<SavedProductEntity>> {
        return savedProductDao.getSavedProducts()
    }

    // 2. Menyimpan produk ke favorit
    suspend fun saveProduct(product: SavedProductEntity) {
        savedProductDao.insert(product)
    }

    // 3. Menghapus produk dari favorit
    suspend fun deleteProduct(productId: String) {
        savedProductDao.delete(productId)
    }

    // 4. Mengecek status apakah produk ini sudah difavoritkan
    suspend fun isSaved(productId: String): Int {
        return savedProductDao.isSaved(productId)
    }
}