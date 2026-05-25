package com.altermarkt.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altermarkt.app.data.local.SavedProductEntity
import com.altermarkt.app.ui.viewmodel.SavedVM
import com.altermarkt.app.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,  // ViewModel untuk logika pencarian
    savedVM: SavedVM,            // ViewModel untuk simpan/hapus produk
    onProductClick: (String) -> Unit
) {
    // Ambil state dari SearchViewModel
    val query by viewModel.query.collectAsState(initial = "")
    val selectedCat by viewModel.selectedCategory.collectAsState(initial = "Semua")
    val results by viewModel.results.collectAsState(initial = emptyList<com.altermarkt.app.domain.model.Product>())

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

        // Input pencarian — ketik keyword untuk filter produk
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.onQueryChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari barang..", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurpleAccent,
                unfocusedBorderColor = PurpleAccent.copy(alpha = 0.4f),
                cursorColor = PurpleAccent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter kategori — kombinasi dengan keyword pencarian
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

        // Jumlah hasil pencarian
        Text("${results.size} item", color = TextMuted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Grid hasil pencarian
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(results) { product ->
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