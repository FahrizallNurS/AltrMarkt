package com.altermarkt.app.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altermarkt.app.domain.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.UUID

class EditVM : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // State untuk menampung data produk lama
    var product by mutableStateOf<Product?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var updateMessage by mutableStateOf<String?>(null)
        private set

    fun resetMessage() {
        updateMessage = null
    }

    // 1. Ambil data awal barang dari Firestore saat halaman Edit dibuka
    fun loadProductDetail(productId: String) {
        if (productId.isEmpty()) return
        isLoading = true
        firestore.collection("products").document(productId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    // BACA MANUAL SEPERTI REPOSITORY AGAR isAvailable TERBACA AKURAT
                    product = Product(
                        id          = doc.id,
                        title       = doc.getString("title") ?: "",
                        price       = doc.getLong("price")?.toInt() ?: 0,
                        description = doc.getString("description") ?: "",
                        category    = doc.getString("category") ?: "",
                        imageUrl    = doc.getString("imageUrl") ?: "",
                        sellerId    = doc.getString("sellerId") ?: "",
                        sellerName  = doc.getString("sellerName") ?: "",
                        sellerPhone = doc.getString("sellerPhone") ?: "",
                        isAvailable = doc.getBoolean("isAvailable") ?: true,
                        viewCount   = doc.getLong("viewCount")?.toInt() ?: 0,
                        likeCount   = doc.getLong("likeCount")?.toInt() ?: 0,
                        createdAt   = doc.getString("createdAt") ?: ""
                    )
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                isLoading = false
                updateMessage = "Gagal memuat data: ${e.localizedMessage}"
            }
    }

    // 2. Fungsi untuk memperbarui data barang (Teks & Foto Baru jika ada)
    fun updateProduct(
        productId: String,
        newTitle: String,
        newPriceString: String,
        newCategory: String,
        newDesc: String,
        newIsAvailable: Boolean,
        newImageUri: Uri?
    ) {
        val priceInt = newPriceString.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0

        if (newTitle.isBlank() || newPriceString.isBlank() || newCategory.isBlank()) {
            updateMessage = "Nama, harga, dan kategori wajib diisi!"
            return
        }

        isLoading = true

        if (newImageUri != null) {
            updateMessage = "Mengunggah foto baru..."
            val imageFileName = java.util.UUID.randomUUID().toString() + ".jpg"
            val storageRef = storage.reference.child("product_images/$imageFileName")

            storageRef.putFile(newImageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveUpdateToFirestore(productId, newTitle, priceInt, newCategory, newDesc, newIsAvailable, uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    updateMessage = "Gagal mengunggah foto baru: ${e.localizedMessage}"
                }
        } else {
            val oldImageUrl = product?.imageUrl ?: ""
            saveUpdateToFirestore(productId, newTitle, priceInt, newCategory, newDesc, newIsAvailable, oldImageUrl)
        }
    }
    private fun saveUpdateToFirestore(
        productId: String,
        title: String,
        price: Int,
        category: String,
        desc: String,
        isAvailable: Boolean,
        imageUrl: String
    ) {
        updateMessage = "Memperbarui data..."

        val updatedData = mapOf(
            "title" to title,
            "price" to price,
            "category" to category,
            "description" to desc,
            "isAvailable" to isAvailable,
            "imageUrl" to imageUrl
        )

        firestore.collection("products").document(productId)
            .update(updatedData)
            .addOnSuccessListener {
                isLoading = false
                updateMessage = "Produk berhasil diperbarui!"
            }
            .addOnFailureListener { e ->
                isLoading = false
                updateMessage = "Gagal memperbarui: ${e.localizedMessage}"
            }
    }

    // 3. Fitur Hapus Barang Dagangan
    fun deleteProduct(productId: String, onSuccess: () -> Unit) {
        isLoading = true
        updateMessage = "Menghapus postingan..."

        firestore.collection("products").document(productId)
            .delete()
            .addOnSuccessListener {
                isLoading = false
                updateMessage = "Postingan berhasil dihapus!"
                onSuccess() // Callback untuk otomatis popBackStack (kembali ke profil)
            }
            .addOnFailureListener { e ->
                isLoading = false
                updateMessage = "Gagal menghapus: ${e.localizedMessage}"
            }
    }
}