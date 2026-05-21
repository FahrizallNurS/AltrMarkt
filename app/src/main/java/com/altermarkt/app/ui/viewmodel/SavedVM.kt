package com.altermarkt.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.altermarkt.app.data.local.SavedProductEntity
import com.altermarkt.app.data.repository.SaveRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedVM(private val repository: SaveRepo) : ViewModel() {

    // Variabel ini akan otomatis memantau perubahan data di tabel Room
    // dan langsung mengirimkannya ke UI Jetpack Compose
    val savedProducts: StateFlow<List<SavedProductEntity>> = repository.getSavedProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveProduct(product: SavedProductEntity) {
        viewModelScope.launch {
            repository.saveProduct(product)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }
}

// Factory ini wajib dibuat agar Android tahu cara memasukkan SaveRepo ke dalam SavedVM
class SavedVMFactory(private val repository: SaveRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedVM::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavedVM(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}