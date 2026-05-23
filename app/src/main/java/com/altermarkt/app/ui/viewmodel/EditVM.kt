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
            .addOnSuccessListener { document ->
                product = document.toObject(Product::class.java)
                isLoading = false
            }
            .addOnFailureListener { e ->
                isLoading = false
                updateMessage = "Gagal memuat data: ${e.localizedMessage}"
            }
    }

    // 2. Fungsi untuk memperbarui data barang (Teks & Foto Baru jika ada)
    fun updateProduct(productId: String, newTitle: String, newPriceString: String, newCategory: String, newDesc: String, newImageUri: Uri?) {
        val priceInt = newPriceString.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0

        if (newTitle.isBlank() || newPriceString.isBlank() || newCategory.isBlank()) {
            updateMessage = "Nama, harga, dan kategori wajib diisi!"
            return
        }

        isLoading = true

        if (newImageUri != null) {
            // Jika user memilih foto baru, upload foto baru terlebih dahulu ke Storage
            updateMessage = "Mengunggah foto baru..."
            val imageFileName = UUID.randomUUID().toString() + ".jpg"
            val storageRef = storage.reference.child("product_images/$imageFileName")

            storageRef.putFile(newImageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Jalankan fungsi update ke Firestore dengan URL gambar baru
                        saveUpdateToFirestore(productId, newTitle, priceInt, newCategory, newDesc, uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    updateMessage = "Gagal mengunggah foto baru: ${e.localizedMessage}"
                }
        } else {
            // Jika user tidak mengganti foto, gunakan URL gambar yang lama
            val oldImageUrl = product?.imageUrl ?: ""
            saveUpdateToFirestore(productId, newTitle, priceInt, newCategory, newDesc, oldImageUrl)
        }
    }

    private fun saveUpdateToFirestore(productId: String, title: String, price: Int, category: String, desc: String, imageUrl: String) {
        updateMessage = "Memperbarui data..."

        val updatedData = mapOf(
            "title" to title,
            "price" to price,
            "category" to category,
            "description" to desc,
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