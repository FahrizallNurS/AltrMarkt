package com.altermarkt.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altermarkt.app.data.local.AppDatabase
import com.altermarkt.app.data.local.HomeProductEntity
import com.altermarkt.app.data.repository.HomeProductRepository
import com.altermarkt.app.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: HomeProductRepository,
    private val db: AppDatabase
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _savedIds = MutableStateFlow<Set<String>>(emptySet())
    val savedIds: StateFlow<Set<String>> = _savedIds

    init {
        loadDummyData()
        loadProducts()
    }

    private fun loadDummyData() {
        viewModelScope.launch {
            val dummy = listOf(
                HomeProductEntity(
                    id = "1",
                    title = "Rog Strix Scar 18",
                    price = 77000000,
                    description = "Laptop gaming ROG Strix Scar 18, kondisi 95% mulus.",
                    category = "Elektronik",
                    imageUrl = "https://picsum.photos/400/300?random=1",
                    sellerId = "user1",
                    sellerName = "Budi Santoso",
                    sellerPhone = "+6285755433151",
                    isAvailable = true,
                    viewCount = 10,
                    likeCount = 5,
                    createdAt = "2024-01-01"
                ),
                HomeProductEntity(
                    id = "2",
                    title = "Buku Linux Basics For Hackers",
                    price = 200000,
                    description = "Buku Linux Basics For Hackers, kondisi baru.",
                    category = "Buku",
                    imageUrl = "https://picsum.photos/400/300?random=1",
                    sellerId = "user2",
                    sellerName = "Ani Rahayu",
                    sellerPhone = "085755433151",
                    isAvailable = true,
                    viewCount = 5,
                    likeCount = 3,
                    createdAt = "2024-01-02"
                ),
                HomeProductEntity(
                    id = "3",
                    title = "BU!!! Mercurial Vapor Elite",
                    price = 500000,
                    description = "Sepatu bola Mercurial Vapor Elite, size 42.",
                    category = "Pakaian",
                    imageUrl = "https://picsum.photos/400/300?random=1",
                    sellerId = "user3",
                    sellerName = "Citra Dewi",
                    sellerPhone = "085755433151",
                    isAvailable = true,
                    viewCount = 8,
                    likeCount = 2,
                    createdAt = "2024-01-03"
                )
            )
            db.homeProductDao().upsertProducts(dummy)
            android.util.Log.d("DummyData", "Dummy inserted!")

            val check = db.homeProductDao().getProductById("1")
            android.util.Log.d("DummyData", "Check product: $check")
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _selectedCategory.collectLatest { cat ->
                val flow = if (cat == "Semua") repo.getAllProducts()
                else repo.getProductsByCategory(cat)
                flow.collect { _products.value = it }
            }
        }
    }

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun toggleSave(productId: String) {
        val current = _savedIds.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _savedIds.value = current
    }
}