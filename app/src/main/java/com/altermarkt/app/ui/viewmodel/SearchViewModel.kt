package com.altermarkt.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altermarkt.app.data.repository.HomeProductRepository
import com.altermarkt.app.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class SearchViewModel(private val repo: HomeProductRepository) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val results: StateFlow<List<Product>> = combine(
        _query, _selectedCategory
    ) {
        q, cat -> Pair(q, cat) }
        .flatMapLatest { (q, cat) ->
            when {
                q.isNotBlank() -> repo.searchProducts(q)
                cat != "Semua" -> repo.getProductsByCategory(cat)
                else -> repo.getAllProducts()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(q: String) { _query.value= q }
    fun selectCategory(cat: String) { _selectedCategory.value = cat}
}