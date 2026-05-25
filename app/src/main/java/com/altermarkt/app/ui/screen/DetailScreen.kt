package com.altermarkt.app.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.altermarkt.app.ui.viewmodel.DetailViewModel

@Composable
fun DetailScreen(
    productId: String,
    viewModel: DetailViewModel,
    onBack: () -> Unit
) {
    val product by viewModel.product.collectAsState(initial = null)
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val context = LocalContext.current

    LaunchedEffect(productId) { viewModel.loadProduct(productId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(top = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header — tombol back dan judul
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PurpleAccent)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Column {
                Text("Detail", color = TextMuted, fontSize = 11.sp)
                Text("Postingan Barang", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PurpleAccent)
            }
        }

        product?.let { p ->
            // Gambar produk
            AsyncImage(
                model = p.imageUrl.ifEmpty { null },
                contentDescription = p.title,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                contentScale = ContentScale.Crop
            )

            // Container utama untuk semua info produk
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Badge kategori dan tombol like dalam 1 baris — kiri dan kanan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge kategori produk di sebelah kiri
                    Surface(
                        color = PurpleAccent,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = p.category,
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }

                    // State like — hanya tersimpan selama session, tidak ke database
                    var isLiked by remember { mutableStateOf(false) }
                    var likeCount by remember { mutableStateOf(p.likeCount) }

                    // Tombol like di sebelah kanan
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardBg)
                            .clickable {
                                // Toggle like — tambah atau kurangi jumlah like
                                isLiked = !isLiked
                                likeCount = if (isLiked) likeCount + 1 else likeCount - 1
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        // Icon berubah saat di-like — merah jika liked, abu jika belum
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color.Red else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        // Jumlah like
                        Text(
                            text = "$likeCount",
                            color = if (isLiked) Color.Red else TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                // Nama dan harga produk
                Text(p.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text("Rp. ${"%,d".format(p.price)}", color = Color(0xFF00CFFF), fontSize = 16.sp)

                // Deskripsi produk
                Text("Deskripsi Produk", color = TextMuted, fontSize = 12.sp)
                Text(p.description, color = Color(0xFFAAAAAA), fontSize = 12.sp, lineHeight = 20.sp)

                // Info penjual
                Text("Informasi Penjual", color = TextMuted, fontSize = 12.sp)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar inisial nama penjual
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PurpleAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p.sellerName.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column {
                            Text(p.sellerName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(p.sellerPhone, color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }

                // Tombol WhatsApp — otomatis konversi format nomor HP
                Button(
                    onClick = {
                        var phone = p.sellerPhone.trim()
                        when {
                            phone.startsWith("+62") -> phone = phone.replace("+", "")
                            phone.startsWith("62")  -> { }
                            phone.startsWith("0")   -> phone = "62" + phone.substring(1)
                            else                    -> phone = "62$phone"
                        }
                        val uri = Uri.parse("https://wa.me/$phone")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Text("Hubungi Via WhatsApp", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}