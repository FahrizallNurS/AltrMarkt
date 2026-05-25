package com.altermarkt.app.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.altermarkt.app.domain.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PostingVM : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    var isLoading by mutableStateOf(false)
        private set

    var uploadMessage by mutableStateOf<String?>(null)
        private set

    fun resetMessage() {
        uploadMessage = null
    }

    fun uploadProduct(imageUri: Uri?, title: String, priceString: String, category: String, description: String) {
        if (title.isBlank() || priceString.isBlank() || category.isBlank() || imageUri == null) {
            uploadMessage = "Harap isi semua data dan pilih foto barang!"
            return
        }

        val priceInt = priceString.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0

        val currentUser = auth.currentUser
        if (currentUser == null) {
            uploadMessage = "Anda harus login terlebih dahulu!"
            return
        }

        isLoading = true
        uploadMessage = "Mengunggah foto..."

        val imageFileName = UUID.randomUUID().toString() + ".jpg"
        val storageRef = storage.reference.child("product_images/$imageFileName")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                uploadMessage = "Menyimpan data barang..."
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .addOnSuccessListener { userDoc ->
                            val sellerName = userDoc.getString("name") ?: "Pengguna AlterMarkt"
                            val sellerPhone = userDoc.getString("whatsapp") ?: ""
                            saveToFirestore(
                                title, priceInt, category, description,
                                downloadUrl, currentUser.uid, sellerName, sellerPhone
                            )
                        }
                }
            }
            .addOnFailureListener { e ->
                isLoading = false
                uploadMessage = "Gagal mengunggah gambar: ${e.localizedMessage}"
            }
    }

    private fun saveToFirestore(
        title: String, price: Int, category: String,
        desc: String, imageUrl: String, sellerId: String,
        sellerName: String, sellerPhone: String
    ) {
        val productId = firestore.collection("products").document().id
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val newProduct = Product(
            id          = productId,
            title       = title,
            price       = price,
            description = desc,
            category    = category,
            imageUrl    = imageUrl,
            sellerId    = sellerId,
            sellerName  = sellerName,
            sellerPhone = sellerPhone,
            isAvailable = true,
            viewCount   = 0,
            likeCount   = 0,
            createdAt   = currentDate
        )

        firestore.collection("products").document(productId)
            .set(newProduct)
            .addOnSuccessListener {
                isLoading = false
                uploadMessage = "Barang berhasil diposting!"
            }
            .addOnFailureListener { e ->
                isLoading = false
                uploadMessage = "Gagal menyimpan data: ${e.localizedMessage}"
            }
    }
}