package com.altermarkt.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altermarkt.app.data.repository.HomeProductRepository
import com.altermarkt.app.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(private val repo: HomeProductRepository) : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> =_product

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadProduct(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            android.util.Log.d("DetailVM", "Loading product id: $id")
            val result = repo.getProductById(id)
            android.util.Log.d("DetailVM", "Result: $result")
            _product.value = result
            _isLoading.value = false
        }
    }
}