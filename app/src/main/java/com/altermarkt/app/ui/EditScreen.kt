package com.altermarkt.app.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen() {
    // State yang sudah terisi data lama barang (Contoh simulasi data MacBook dari mockup Anda)
    var namaBarang by remember { mutableStateOf("Iphone 14 Pro Max") }
    var hargaBarang by remember { mutableStateOf("Rp. 17000000") }
    var deskripsiBarang by remember { mutableStateOf("MacBook Pro 14\" dengan chip Apple M3, 16GB RAM, 512GB SSD. Kondisi 98% mulus, lengkap charger original. Garansi resmi masih aktif hingga Desember 2025.") }

    // State Dropdown Kategori
    var kategoriExpanded by remember { mutableStateOf(false) }
    val kategoriList = listOf("Elektronik", "Fashion", "Buku & Alat Tulis", "Perlengkapan Kos", "Lainnya")
    var kategoriTerpilih by remember { mutableStateOf(kategoriList[0]) }

    // --- FITUR BARU: State Dropdown Status (Saran Dosen) ---
    var statusExpanded by remember { mutableStateOf(false) }
    val statusList = listOf("Tersedia", "Terjual")
    var statusTerpilih by remember { mutableStateOf(statusList[1]) } // Default "Terjual" sesuai mockup Anda

    // State URI foto (sementara diset null, nanti akan memuat foto lama dari Firebase Storage)
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HEADER ---
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* TODO: Kembali */ },
                    modifier = Modifier
                        .background(SurfaceDark, RoundedCornerShape(12.dp))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Jual Sekarang", color = TextGray, fontSize = 14.sp)
                    Text(text = "Edit Barang", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- KOTAK UBAH FOTO ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, PrimaryPurple, RoundedCornerShape(16.dp))
                    .clickable { /* TODO: Ubah Foto */ },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Ubah Foto Barang", color = Color.White, fontSize = 14.sp)
                    }
                } else {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // --- FORM INPUT & EDIT DATA ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Edit Nama
                Text(text = "Nama Barang", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = namaBarang,
                    onValueChange = { namaBarang = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedIndicatorColor = PrimaryPurple,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Edit Harga
                Text(text = "Harga", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = hargaBarang,
                    onValueChange = { hargaBarang = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedIndicatorColor = PrimaryPurple,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Edit Kategori
                Text(text = "Kategori", color = TextGray, fontSize = 14.sp)
                ExposedDropdownMenuBox(
                    expanded = kategoriExpanded,
                    onExpandedChange = { kategoriExpanded = !kategoriExpanded }
                ) {
                    OutlinedTextField(
                        value = kategoriTerpilih,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = PrimaryPurple) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedIndicatorColor = PrimaryPurple,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = kategoriExpanded,
                        onDismissRequest = { kategoriExpanded = false },
                        modifier = Modifier.background(SurfaceDark)
                    ) {
                        kategoriList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item, color = Color.White) },
                                onClick = {
                                    kategoriTerpilih = item
                                    kategoriExpanded = false
                                }
                            )
                        }
                    }
                }

                // Edit Deskripsi
                Text(text = "Deskripsi", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = deskripsiBarang,
                    onValueChange = { deskripsiBarang = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedIndicatorColor = PrimaryPurple,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // --- FITUR BARU: Edit Status Ketersediaan (Saran Dosen) ---
                Text(text = "Status", color = TextGray, fontSize = 14.sp)
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = statusTerpilih,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = PrimaryPurple) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedIndicatorColor = PrimaryPurple,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false },
                        modifier = Modifier.background(SurfaceDark)
                    ) {
                        statusList.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(text = status, color = Color.White) },
                                onClick = {
                                    statusTerpilih = status
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- TOMBOL EDIT & SIMPAN SEJAJAR ---
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Edit", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Simpan", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}