package com.altermarkt.app.ui

import android.net.Uri
import android.widget.Toast
import com.altermarkt.app.ui.viewmodel.PostingVM
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.altermarkt.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostingScreen(
    viewModel: PostingVM = viewModel(),
    onBackClick: () -> Unit = {} // Parameter untuk tombol kembali
) {
    val context = LocalContext.current

    // State untuk form input
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val kategoriList = listOf("Elektronik", "Fashion", "Buku & Alat Tulis", "Perlengkapan Kos", "Lainnya")
    var kategoriTerpilih by remember { mutableStateOf(kategoriList[0]) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Fitur Pembuka Galeri HP
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Memantau pesan dari ViewModel (menampilkan Toast saat ada notifikasi)
    LaunchedEffect(viewModel.uploadMessage) {
        viewModel.uploadMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            // Jika sukses, kosongkan form agar siap untuk posting barang baru
            if (message == "Barang berhasil diposting!") {
                title = ""
                price = ""
                description = ""
                imageUri = null
            }
            viewModel.resetMessage() // Bersihkan pesan setelah ditampilkan
        }
    }

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
                    onClick = onBackClick,
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
                    Text(text = "Posting Barang", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- KOTAK UPLOAD FOTO ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, PrimaryPurple, RoundedCornerShape(16.dp))
                    .clickable {
                        // Memanggil galeri saat kotak diklik
                        imagePickerLauncher.launch("image/*")
                    },
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
                        Text(text = "Tambahkan Foto Barang", color = Color.White, fontSize = 14.sp)
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

        // --- FORM INPUT ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Nama Barang", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Masukkan nama barang...", color = TextGray) },
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

                Text(text = "Harga", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    placeholder = { Text("Rp. 0", color = TextGray) },
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

                Text(text = "Kategori", color = TextGray, fontSize = 14.sp)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
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
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(SurfaceDark)
                    ) {
                        kategoriList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item, color = Color.White) },
                                onClick = {
                                    kategoriTerpilih = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Text(text = "Deskripsi", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
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
            }
        }

        // --- TOMBOL POSTING ---
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    // Memicu fungsi upload di ViewModel saat ditekan
                    viewModel.uploadProduct(
                        imageUri = imageUri,
                        title = title,
                        priceString = price,
                        category = kategoriTerpilih,
                        description = description
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    // Memudarkan warna tombol saat sedang loading
                    disabledContainerColor = PrimaryPurple.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(25.dp),
                enabled = !viewModel.isLoading // Matikan tombol jika sedang proses upload
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Posting Barang", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}