package com.altermarkt.app.domain.model

data class Product(

    val id : String = "",
    val title : String = "",
    val price : Int = 0,
    val description : String = "",
    val category : String = "",
    val imageUrl : String = "",
    val sellerId : String = "",
    val sellerName : String = "",
    val isAvailable : Boolean = true,
    val viewCount : Int = 0,
    val likeCount : Int = 0,
    val createdAt : String = "",
)