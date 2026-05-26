package com.altermarkt.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altermarkt.app.data.repository.AuthRepository
import com.altermarkt.app.domain.model.Product
import com.altermarkt.app.domain.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProfileVM : ViewModel() {

    private val authRepo = AuthRepository()
    private val firestore = FirebaseFirestore.getInstance()

    // State untuk data User
    var currentUser by mutableStateOf<User?>(null)
        private set

    // State untuk daftar produk milik user sendiri
    var myProducts by mutableStateOf<List<Product>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadProfileAndProducts()
    }

    // Tambahkan fungsi ini di dalam class ProfileVM
    fun updateProfile(
        newName: String,
        newWhatsapp: String,
        newBio: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Ambil ID pengguna yang sedang login (sesuaikan dengan logic Auth tim Anda)
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return

        val updatedData = mapOf(
            "name" to newName,
            "whatsapp" to newWhatsapp,
            "bio" to newBio
        )

        isLoading = true
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        firestore.collection("users").document(userId)
            .update(updatedData)
            .addOnSuccessListener {
                isLoading = false
                // Muat ulang profil agar data di layar langsung sinkron yang baru
                loadProfileAndProducts()
                onSuccess()
            }
            .addOnFailureListener { e ->
                isLoading = false
                onFailure(e.localizedMessage ?: "Gagal memperbarui profil")
            }
    }

    fun loadProfileAndProducts() {
        viewModelScope.launch {
            isLoading = true

            // 1. Ambil data profil dari Firestore melalui AuthRepository
            val user = authRepo.getCurrentUser()
            currentUser = user

            // 2. Jika user ditemukan, cari barang miliknya di collection "products"
            if (user != null && user.uid.isNotEmpty()) {
                firestore.collection("products")
                    .whereEqualTo("sellerId", user.uid) // Filter berdasarkan UID sendiri
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val products = snapshot.toObjects(Product::class.java)
                        myProducts = products
                        isLoading = false
                    }
                    .addOnFailureListener {
                        isLoading = false
                    }
            } else {
                isLoading = false
            }
        }
    }
}