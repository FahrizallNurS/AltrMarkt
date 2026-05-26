package com.altermarkt.app.ui

import android.widget.Toast
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.altermarkt.app.domain.model.Product
import com.altermarkt.app.ui.theme.*
import com.altermarkt.app.ui.viewmodel.ProfileVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileVM = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onLogoutSuccess: () -> Unit = {},
    onEditProductClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val user = profileViewModel.currentUser
    val myProducts = profileViewModel.myProducts

    var namaLengkap by remember { mutableStateOf("") }
    var nomorWhatsApp by remember { mutableStateOf("") }
    var bioProfile by remember { mutableStateOf("") }

    // Sinkronisasi data user dari Firestore ke form input teks
    LaunchedEffect(user) {
        user?.let {
            namaLengkap = it.name
            nomorWhatsApp = it.whatsapp
        }
    }

    // Trigger muat ulang setiap kali halaman dibuka
    LaunchedEffect(Unit) {
        profileViewModel.loadProfileAndProducts()
    }

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        if (profileViewModel.isLoading) {
            CircularProgressIndicator(
                color = PrimaryPurple,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
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
                            onClick = {
                                authViewModel.logout()
                                Toast.makeText(context, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                                onLogoutSuccess()
                            },
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
                                focusedIndicatorColor = PrimaryPurple,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // --- TOMBOL SIMPAN PROFIL MOCKUP ---
                item {
                    Button(
                        onClick = {
                            if (namaLengkap.isBlank() || nomorWhatsApp.isBlank()) {
                                Toast.makeText(context, "Nama dan Nomor WhatsApp tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                            } else {
                                profileViewModel.updateProfile(
                                    newName = namaLengkap,
                                    newWhatsapp = nomorWhatsApp,
                                    newBio = bioProfile,
                                    onSuccess = {
                                        Toast.makeText(context, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                                    },
                                    onFailure = { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(45.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        if (profileViewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(text = "Simpan Profil", fontWeight = FontWeight.Bold, color = Color.White)
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

                // --- DAFTAR POSTINGAN RIIL ---
                if (myProducts.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                            Text(text = "Belum ada barang yang diposting", color = TextGray)
                        }
                    }
                } else {
                    items(myProducts) { product ->
                        MyProductRowItem(product, onEditClick = onEditProductClick)
                    }
                }
            }
        }
    }
}

@Composable
fun MyProductRowItem(product: Product, onEditClick: (String) -> Unit) {
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
                Text(text = product.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = formatRupiah(product.price), color = PrimaryPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(
                onClick = { onEditClick(product.id) },
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

    fun formatRupiah(nominal: Int): String {
        val localeID = Locale("in", "ID")
        val formatter = NumberFormat.getInstance(localeID)
        return "Rp ${formatter.format(nominal)}"
    }