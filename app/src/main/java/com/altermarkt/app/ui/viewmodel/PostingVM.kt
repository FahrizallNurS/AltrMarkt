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

    // Inisialisasi layanan Firebase
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // State untuk memantau proses loading di UI
    var isLoading by mutableStateOf(false)
        private set

    // State untuk menampilkan pesan sukses atau error ke pengguna
    var uploadMessage by mutableStateOf<String?>(null)
        private set

    fun resetMessage() {
        uploadMessage = null
    }

    // Fungsi utama yang akan dipanggil saat tombol "Posting Barang" ditekan
    fun uploadProduct(imageUri: Uri?, title: String, priceString: String, category: String, description: String) {
        // Validasi dasar
        if (title.isBlank() || priceString.isBlank() || category.isBlank() || imageUri == null) {
            uploadMessage = "Harap isi semua data dan pilih foto barang!"
            return
        }

        // Konversi harga dari String ke Int (menghapus karakter selain angka)
        val priceInt = priceString.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0

        val currentUser = auth.currentUser
        if (currentUser == null) {
            uploadMessage = "Anda harus login terlebih dahulu!"
            return
        }

        isLoading = true
        uploadMessage = "Mengunggah foto..."

        // 1. Siapkan nama file unik untuk gambar
        val imageFileName = UUID.randomUUID().toString() + ".jpg"
        val storageRef = storage.reference.child("product_images/$imageFileName")

        // 2. Mulai proses upload gambar ke Firebase Storage
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Jika gambar berhasil diupload, ambil URL publiknya
                uploadMessage = "Menyimpan data barang..."
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    // Ambil nama penjual dari profil akun, atau berikan nama default
                    val sellerName = currentUser.displayName ?: "Pengguna AlterMarkt"

                    // Ambil nomor WA dari Firestore dulu sebelum simpan produk
                    firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .addOnSuccessListener { userDoc ->
                            val sellerPhone = userDoc.getString("whatsapp") ?: ""

                            // 3. Simpan seluruh data ke Firestore
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
        // Buat ID unik untuk dokumen produk di Firestore
        val productId = firestore.collection("products").document().id

        // Buat stempel waktu otomatis untuk kolom createdAt
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Masukkan data ke dalam struktur Product buatan teman Anda
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