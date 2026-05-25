package com.altermarkt.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.altermarkt.app.data.local.SavedProductEntity
import com.altermarkt.app.domain.model.Product
import com.altermarkt.app.ui.viewmodel.HomeViewModel
import com.altermarkt.app.ui.viewmodel.SavedVM

// Warna tema AlterMarkt
val DarkBg = Color(0xFF0D0D0D)
val CardBg = Color(0xFF1A1A1A)
val PurpleAccent = Color(0xFF7C5CFC)
val TextMuted = Color(0xFF888888)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    savedVM: SavedVM,
    onProductClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    // Ambil state dari ViewModel
    val products by viewModel.products.collectAsState(initial = emptyList())
    val selectedCat by viewModel.selectedCategory.collectAsState(initial = "Semua")
    val isLoading by viewModel.isLoading.collectAsState(initial = false)

    // Ambil daftar produk tersimpan dari Room, konversi ke Set ID
    val savedProducts by savedVM.savedProducts.collectAsState()
    val savedIds = savedProducts.map { it.productId }.toSet()

    val categories = listOf("Semua", "Elektronik", "Buku", "Pakaian")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Judul aplikasi
        Text("AlterMarkt", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Medium)
        Text("Your Campus Marketplace", color = TextMuted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Search bar — klik untuk pindah ke SearchScreen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBg)
                .clickable { onSearchClick() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
            Text("Cari barang..", color = TextMuted, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter kategori — klik chip untuk filter produk
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCat == cat,
                    onClick = { viewModel.selectCategory(cat) },
                    label = { Text(cat, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PurpleAccent,
                        selectedLabelColor = Color.White,
                        containerColor = CardBg,
                        labelColor = TextMuted
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Jumlah produk yang tampil
        Text("${products.size} item", color = TextMuted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Loading indicator saat sync dari Firestore
        if (isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PurpleAccent)
            }
        }

        // Grid 2 kolom untuk menampilkan produk
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(products) { product ->
                ProductCard(
                    product     = product,
                    isSaved     = savedIds.contains(product.id),
                    onSaveClick = {
                        // Jika sudah disimpan → hapus, jika belum → simpan ke Room
                        if (savedIds.contains(product.id)) {
                            savedVM.deleteProduct(product.id)
                        } else {
                            savedVM.saveProduct(
                                SavedProductEntity(
                                    productId  = product.id,
                                    title      = product.title,
                                    price      = product.price,
                                    imageUrl   = product.imageUrl,
                                    sellerName = product.sellerName,
                                    savedAt    = System.currentTimeMillis().toString()
                                )
                            )
                        }
                    },
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

// Komponen card untuk menampilkan 1 produk
@Composable
fun ProductCard(
    product: Product,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)  // tinggi tetap supaya semua card sama
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column {
            // Area gambar produk + tombol save
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                // Gambar produk dari URL
                AsyncImage(
                    model = product.imageUrl.ifEmpty { null },
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Tombol save pojok kanan atas
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isSaved) PurpleAccent
                            else Color.Black.copy(alpha = 0.5f)
                        )
                        .clickable { onSaveClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark
                        else Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Info produk — nama dan harga
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Text(
                    text = product.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis  // teks terpotong jika terlalu panjang
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rp. ${"%,d".format(product.price)}",
                    color = PurpleAccent,
                    fontSize = 12.sp
                )
            }
        }
    }
}