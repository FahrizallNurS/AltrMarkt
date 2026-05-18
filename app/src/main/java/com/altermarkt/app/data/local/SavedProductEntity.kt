package com.altermarkt.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_products") //memberitahu Room bahwa class ini adalah sebuah tabel dan tableName adalah nama tabel yang akan digunakan dalam database.
data class SavedProductEntity(
    @PrimaryKey
    val productId : String,
    val title     : String,
    val price     : Int,
    val imageUrl  : String,
    val sellerName: String,
    val savedAt   : String
)

