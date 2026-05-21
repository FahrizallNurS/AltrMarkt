package com.altermarkt.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altermarkt.app.data.repository.HomeProductRepository
import com.altermarkt.app.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: HomeProductRepository) : ViewModel() {
    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadProducts()
    }

    private fun loadProducts(){
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
}