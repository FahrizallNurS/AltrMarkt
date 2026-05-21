package com.altermarkt.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.altermarkt.app.ui.theme.*

// 1. Tambahkan DummyProduct sementara agar UI tidak error
data class DummyProduct(val id: String, val name: String, val price: String, val imageUrl: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    var namaLengkap by remember { mutableStateOf("Kimi Antonelli") }
    var nomorWhatsApp by remember { mutableStateOf("+6219221889") }
    var bioProfile by remember { mutableStateOf("Mahasiswa TI 4C") }

    // 2. Ubah pemanggilan menjadi DummyProduct
    val myProducts = listOf(
        DummyProduct("1", "Iphone 14 Pro Max", "Rp. 17.000.00,00", "https://via.placeholder.com/300"),
        DummyProduct("2", "Iphone 14 Pro Max", "Rp. 17.000.00,00", "https://via.placeholder.com/300")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HEADER & TOMBOL LOGOUT ---
        item {
            Spacer(modifier = Modifier.height(48.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(text = "Pengaturan", color = TextGray, fontSize = 16.sp)
                    Text(text = "Profil", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { /* TODO: Logout */ },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(text = "Logout", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- AVATAR PROFIL ---
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(120.dp)
                )
            }
        }

        // --- FORM INPUT DATA ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Nama Lengkap", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = namaLengkap,
                    onValueChange = { namaLengkap = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        disabledContainerColor = SurfaceDark,
                        focusedIndicatorColor = PrimaryPurple,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(text = "Nomor WhatsApp", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = nomorWhatsApp,
                    onValueChange = { nomorWhatsApp = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        disabledContainerColor = SurfaceDark,
                        focusedIndicatorColor = PrimaryPurple,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(text = "Bio", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = bioProfile,
                    onValueChange = { bioProfile = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        disabledContainerColor = SurfaceDark,
                        focusedIndicatorColor = PrimaryPurple,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // --- TOMBOL EDIT & SIMPAN ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    // Tambahkan color = Color.White di sini
                    Text(text = "Edit", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    // Tambahkan color = Color.White di sini
                    Text(text = "Simpan", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // --- JUDUL BAGIAN POSTINGAN ---
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Postingan Anda",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // --- DAFTAR POSTINGAN ---
        items(myProducts) { product ->
            MyProductRowItem(product)
        }
    }
}

// 3. Ubah parameter agar menerima DummyProduct
@Composable
fun MyProductRowItem(product: DummyProduct) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = product.price, color = PrimaryPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            IconButton(
                onClick = { /* TODO: Pindah ke Halaman Edit */ },
                modifier = Modifier
                    .background(PrimaryPurple, RoundedCornerShape(8.dp))
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Barang",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}