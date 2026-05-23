package com.altermarkt.app.ui

import android.net.Uri
import android.widget.Toast
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
import com.altermarkt.app.ui.viewmodel.EditVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    productId: String,
    onBack: () -> Unit,
    viewModel: EditVM = viewModel()
) {
    val context = LocalContext.current
    val loadedProduct = viewModel.product

    var namaBarang by remember { mutableStateOf("") }
    var hargaBarang by remember { mutableStateOf("") }
    var deskripsiBarang by remember { mutableStateOf("") }

    var kategoriExpanded by remember { mutableStateOf(false) }
    val kategoriList = listOf("Elektronik", "Fashion", "Buku & Alat Tulis", "Perlengkapan Kos", "Lainnya")
    var kategoriTerpilih by remember { mutableStateOf(kategoriList[0]) }

    var statusExpanded by remember { mutableStateOf(false) }
    val statusList = listOf("Tersedia", "Terjual")
    var statusTerpilih by remember { mutableStateOf(statusList[0]) }

    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    // Ambil data detail barang dari Firestore
    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }

    val formatter = java.text.NumberFormat.getInstance(java.util.Locale("in", "ID"))
    // Tarik data lama ke dalam form input
    LaunchedEffect(loadedProduct) {
        loadedProduct?.let {
            namaBarang = it.title
            hargaBarang = formatter.format(it.price)
            deskripsiBarang = it.description
            if (it.category in kategoriList) kategoriTerpilih = it.category
            statusTerpilih = if (it.isAvailable) "Tersedia" else "Terjual"
        }
    }

    // Tangani notifikasi pop-up Toast
    LaunchedEffect(viewModel.updateMessage) {
        viewModel.updateMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (message == "Produk berhasil diperbarui!" || message == "Postingan berhasil dihapus!") {
                onBack()
            }
            viewModel.resetMessage()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) newImageUri = uri
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
                    onClick = onBack,
                    modifier = Modifier
                        .background(SurfaceDark, RoundedCornerShape(12.dp))
                        .size(40.dp)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Jual Sekarang", color = TextGray, fontSize = 14.sp)
                    Text(text = "Edit Barang", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- KOTAK UBAH FOTO BARANG ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, PrimaryPurple, RoundedCornerShape(16.dp))
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (newImageUri != null) {
                    AsyncImage(model = newImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else if (loadedProduct?.imageUrl != null) {
                    AsyncImage(model = loadedProduct.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Ubah Foto Barang", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }

        // --- FORM DATA INPUT ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Nama Barang", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = namaBarang, onValueChange = { namaBarang = it }, modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = SurfaceDark, unfocusedContainerColor = SurfaceDark, focusedIndicatorColor = PrimaryPurple, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(text = "Harga", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = hargaBarang, onValueChange = { hargaBarang = it }, modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = SurfaceDark, unfocusedContainerColor = SurfaceDark, focusedIndicatorColor = PrimaryPurple, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(text = "Kategori", color = TextGray, fontSize = 14.sp)
                ExposedDropdownMenuBox(expanded = kategoriExpanded, onExpandedChange = { kategoriExpanded = !kategoriExpanded }) {
                    OutlinedTextField(
                        value = kategoriTerpilih, onValueChange = {}, readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = PrimaryPurple) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = SurfaceDark, unfocusedContainerColor = SurfaceDark, focusedIndicatorColor = PrimaryPurple, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(expanded = kategoriExpanded, onDismissRequest = { kategoriExpanded = false }, modifier = Modifier.background(SurfaceDark)) {
                        kategoriList.forEach { item ->
                            DropdownMenuItem(text = { Text(text = item, color = Color.White) }, onClick = { kategoriTerpilih = item; kategoriExpanded = false })
                        }
                    }
                }

                Text(text = "Deskripsi", color = TextGray, fontSize = 14.sp)
                OutlinedTextField(
                    value = deskripsiBarang, onValueChange = { deskripsiBarang = it }, modifier = Modifier.fillMaxWidth().height(150.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = SurfaceDark, unfocusedContainerColor = SurfaceDark, focusedIndicatorColor = PrimaryPurple, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(text = "Status", color = TextGray, fontSize = 14.sp)
                ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = !statusExpanded }) {
                    OutlinedTextField(
                        value = statusTerpilih, onValueChange = {}, readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = PrimaryPurple) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = SurfaceDark, unfocusedContainerColor = SurfaceDark, focusedIndicatorColor = PrimaryPurple, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }, modifier = Modifier.background(SurfaceDark)) {
                        statusList.forEach { status ->
                            DropdownMenuItem(text = { Text(text = status, color = Color.White) }, onClick = { statusTerpilih = status; statusExpanded = false })
                        }
                    }
                }
            }
        }

        // --- TOMBOL HAPUS & SIMPAN SEJAJAR (MOCKUP ACCURATE) ---
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tombol Hapus (Kiri)
                Button(
                    onClick = { viewModel.deleteProduct(productId, onSuccess = onBack) },
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading
                ) {
                    Text(text = "Hapus", fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Tombol Simpan Perubahan (Kanan)
                Button(
                    onClick = {
                        // Jalankan fungsi update ke ViewModel
                        viewModel.updateProduct(
                            productId = productId,
                            newTitle = namaBarang,
                            newPriceString = hargaBarang,
                            newCategory = kategoriTerpilih,
                            newDesc = deskripsiBarang,
                            newImageUri = newImageUri
                        )
                    },
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(text = "Simpan", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}