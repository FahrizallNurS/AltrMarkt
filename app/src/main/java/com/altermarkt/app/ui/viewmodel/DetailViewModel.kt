package com.altermarkt.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altermarkt.app.data.repository.HomeProductRepository
import com.altermarkt.app.domain.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DetailViewModel(private val repo: HomeProductRepository) : ViewModel() {

    // Inisialisasi Firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // State produk yang sedang dibuka
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    // State loading saat ambil data
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State apakah user sudah like produk ini
    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked

    // State jumlah like produk
    private val _likeCount = MutableStateFlow(0)
    val likeCount: StateFlow<Int> = _likeCount

    // Ambil data produk dari Room berdasarkan ID
    fun loadProduct(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            android.util.Log.d("DetailVM", "Loading product id: $id")
            val result = repo.getProductById(id)
            android.util.Log.d("DetailVM", "Result: $result")
            _product.value = result
            _likeCount.value = result?.likeCount ?: 0

            // Cek apakah user sudah like produk ini di Firestore
            val uid = auth.currentUser?.uid ?: ""
            if (uid.isNotEmpty()) {
                val doc = firestore.collection("likes")
                    .document("${uid}_${id}")
                    .get()
                    .await()
                _isLiked.value = doc.exists()
            }
            _isLoading.value = false
        }
    }

    // Toggle like — jika sudah like maka unlike, jika belum maka like
    fun toggleLike(productId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            if (_isLiked.value) {
                // Unlike — hapus dari collection likes dan kurangi likeCount di Firestore
                firestore.collection("likes")
                    .document("${uid}_${productId}")
                    .delete()
                    .await()
                firestore.collection("products")
                    .document(productId)
                    .update("likeCount", FieldValue.increment(-1))
                    .await()
                _isLiked.value = false
                _likeCount.value -= 1
            } else {
                // Like — simpan ke collection likes dan tambah likeCount di Firestore
                firestore.collection("likes")
                    .document("${uid}_${productId}")
                    .set(mapOf("userId" to uid, "productId" to productId))
                    .await()
                firestore.collection("products")
                    .document(productId)
                    .update("likeCount", FieldValue.increment(1))
                    .await()
                _isLiked.value = true
                _likeCount.value += 1
            }
        }
    }
}