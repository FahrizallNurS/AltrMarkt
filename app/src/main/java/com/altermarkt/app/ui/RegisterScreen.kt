package com.altermarkt.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.altermarkt.app.R
import com.altermarkt.app.ui.AuthViewModel
import com.altermarkt.app.ui.Routes

// Warna tema — sama dengan LoginScreen
private val BackgroundDark  = Color(0xFF0D1117)
private val CardDark        = Color(0xFF1A1A2E)
private val PurplePrimary   = Color(0xFF7B2FBE)
private val PurpleLight     = Color(0xFF9D4EDD)
private val TextWhite       = Color(0xFFFFFFFF)
private val TextGray        = Color(0xFFAAAAAA)
private val FieldBackground = Color(0xFF252540)

@Composable
fun RegisterScreen(navController: NavHostController) {

    // Ambil ViewModel yang sama dengan LoginScreen
    val viewModel: AuthViewModel = viewModel()

    // Collect state dari ViewModel
    // collectAsStateWithLifecycle = otomatis stop collect saat screen tidak aktif
    // lebih hemat resource dibanding collectAsState biasa
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMsg  by viewModel.errorMsg.collectAsStateWithLifecycle()
    val isSuccess by viewModel.isSuccess.collectAsStateWithLifecycle()

    // State lokal untuk menyimpan input user sementara
    // remember = nilai tidak hilang saat recomposition (UI refresh)
    var name     by remember { mutableStateOf("") }
    var phone    by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // LaunchedEffect — dijalankan sekali saat isSuccess berubah jadi true
    // Kalau register berhasil, navigasi ke LoginScreen
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            navController.navigate(Routes.LOGIN) {
                // popUpTo = hapus RegisterScreen dari backstack
                // supaya tombol Back tidak balik ke Register
                popUpTo(Routes.REGISTER) { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    // Box luar = background hitam penuh
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        // verticalScroll = supaya card bisa discroll
        // penting karena 4 field bisa melebihi tinggi layar HP kecil
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .verticalScroll(rememberScrollState())
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(PurplePrimary, PurpleLight)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .background(CardDark, RoundedCornerShape(24.dp))
                .padding(28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                // Logo — painterResource mengambil file dari folder drawable
                Image(
                    painter            = painterResource(id = R.drawable.altrmrktlogo),
                    contentDescription = "AlterMarket Logo",
                    modifier           = Modifier.size(72.dp)
                )

                // Judul halaman
                Text(
                    text       = "DAFTAR",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextWhite
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Field Nama Pengguna
                // KeyboardType.Text = keyboard huruf biasa
                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it },
                    placeholder   = { Text("Nama Pengguna", color = TextGray) },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    shape  = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = PurplePrimary,
                        unfocusedBorderColor    = Color(0xFF444466),
                        focusedTextColor        = TextWhite,
                        unfocusedTextColor      = TextWhite,
                        cursorColor             = PurplePrimary,
                        focusedContainerColor   = FieldBackground,
                        unfocusedContainerColor = FieldBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Field No HP
                // KeyboardType.Phone = keyboard angka khusus nomor HP
                OutlinedTextField(
                    value         = phone,
                    onValueChange = { phone = it },
                    placeholder   = { Text("No HP", color = TextGray) },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    shape  = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = PurplePrimary,
                        unfocusedBorderColor    = Color(0xFF444466),
                        focusedTextColor        = TextWhite,
                        unfocusedTextColor      = TextWhite,
                        cursorColor             = PurplePrimary,
                        focusedContainerColor   = FieldBackground,
                        unfocusedContainerColor = FieldBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Field Email
                // KeyboardType.Email = keyboard dengan tombol @ yang mudah diakses
                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it },
                    placeholder   = { Text("Email", color = TextGray) },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape  = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = PurplePrimary,
                        unfocusedBorderColor    = Color(0xFF444466),
                        focusedTextColor        = TextWhite,
                        unfocusedTextColor      = TextWhite,
                        cursorColor             = PurplePrimary,
                        focusedContainerColor   = FieldBackground,
                        unfocusedContainerColor = FieldBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Field Password
                // PasswordVisualTransformation = karakter ditampilkan sebagai titik (••••)
                OutlinedTextField(
                    value                = password,
                    onValueChange        = { password = it },
                    placeholder          = { Text("Password", color = TextGray) },
                    singleLine           = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions      = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape  = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = PurplePrimary,
                        unfocusedBorderColor    = Color(0xFF444466),
                        focusedTextColor        = TextWhite,
                        unfocusedTextColor      = TextWhite,
                        cursorColor             = PurplePrimary,
                        focusedContainerColor   = FieldBackground,
                        unfocusedContainerColor = FieldBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Tampilkan pesan error kalau ada
                // isNotEmpty() = hanya tampil kalau errorMsg tidak kosong
                if (errorMsg.isNotEmpty()) {
                    Text(
                        text     = errorMsg,
                        color    = Color(0xFFFF6B6B),
                        fontSize = 13.sp
                    )
                }

                // Tombol Daftar
                // enabled = !isLoading supaya tidak bisa diklik saat loading
                Button(
                    onClick = {
                        viewModel.register(
                            name     = name,
                            email    = email,
                            password = password,
                            whatsapp = phone
                        )
                    },
                    enabled = !isLoading,
                    shape   = RoundedCornerShape(12.dp),
                    colors  = ButtonDefaults.buttonColors(
                        containerColor         = PurplePrimary,
                        disabledContainerColor = PurplePrimary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    // Tampilkan spinner saat loading, teks saat tidak loading
                    if (isLoading) {
                        CircularProgressIndicator(
                            color       = TextWhite,
                            modifier    = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text       = "DAFTAR",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = TextWhite
                        )
                    }
                }

                // Link ke LoginScreen
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text     = "Sudah punya akun? ",
                        color    = TextGray,
                        fontSize = 14.sp
                    )
                    TextButton(
                        onClick        = {
                            viewModel.resetState()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.REGISTER) { inclusive = true }
                            }
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text       = "Login",
                            color      = PurpleLight,
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}