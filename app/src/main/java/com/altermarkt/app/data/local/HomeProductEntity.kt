package com.altermarkt.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "products")
data class HomeProductEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val price: Int,
    val description: String,
    val category: String,
    val imageUrl: String,
    val sellerId: String,
    val sellerName: String,
    val sellerPhone: String,
    val isAvailable: Boolean,
    val viewCount: Int,
    val likeCount: Int,
    val createdAt: String
)