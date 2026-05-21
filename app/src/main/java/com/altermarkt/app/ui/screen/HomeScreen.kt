package com.altermarkt.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.altermarkt.app.domain.model.Product
import com.altermarkt.app.ui.viewmodel.HomeViewModel


// Warna tema AlterMarkt
val DarkBg = Color(0xFF0D0D0D)
val CardBg = Color(0xFF1A1A1A)
val PurpleAccent = Color(0xFF7C5CFC)
val TextMuted = Color(0xFF888888)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onProductClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    val products by viewModel.products.collectAsState(initial = emptyList())
    val selectedCat by viewModel.selectedCategory.collectAsState(initial = "Semua")
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val categories = listOf("Semua", "Elektronik", "Buku", "Pakaian")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Header
        Text("AlterMarkt", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Medium)
        Text("Your Campus Marketplace", color = TextMuted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Search bar (klik → pindah ke SearchScreen)
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

        // Category chips
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
        Text("${products.size} item", color = TextMuted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Loading indicator
        if (isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PurpleAccent)
            }
        }

        // Grid produk
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl.ifEmpty { null },
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
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