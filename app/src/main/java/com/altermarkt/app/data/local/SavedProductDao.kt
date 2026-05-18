package com.altermarkt.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedProductDao{

    @Query("SELECT * FROM saved_products ORDER BY savedAt DESC")
    fun getSavedProducts() : Flow<List<SavedProductEntity>>  //supaya kalau data berubah, UI otomatis update tanpa perlu refresh manual

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product : SavedProductEntity)

    @Query("DELETE FROM saved_products WHERE productId = :id")
    suspend fun delete(id:String)

    @Query("SELECT COUNT(*) FROM saved_products WHERE productId = :id")
    suspend fun isSaved(id:String) : Int
}

//suspend = fungsi yang berjalan di background thread, tidak membekukan UI