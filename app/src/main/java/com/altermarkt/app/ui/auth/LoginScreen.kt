package com.altermarkt.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altermarkt.app.R
import com.altermarkt.app.ui.AuthViewModel
import com.altermarkt.app.ui.Routes

// Warna tema
private val BackgroundDark = Color(0xFF0D1117)
private val CardDark        = Color(0xFF1A1A2E)
private val PurplePrimary   = Color(0xFF7B2FBE)
private val PurpleLight     = Color(0xFF9D4EDD)
private val TextWhite       = Color(0xFFFFFFFF)
private val TextGray        = Color(0xFFAAAAAA)
private val FieldBackground = Color(0xFF252540)

@Composable
fun LoginScreen(navController: NavHostController) {

    val viewModel: AuthViewModel = viewModel()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMsg  by viewModel.errorMsg.collectAsStateWithLifecycle()
    val isSuccess by viewModel.isSuccess.collectAsStateWithLifecycle()

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
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

                Image(
                    painter            = painterResource(id = R.drawable.altrmrktlogo),
                    contentDescription = "AlterMarket Logo",
                    modifier           = Modifier.size(72.dp)
                )

                Text(
                    text       = "LOGIN",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextWhite
                )

                Spacer(modifier = Modifier.height(4.dp))

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
                        focusedBorderColor   = PurplePrimary,
                        unfocusedBorderColor = Color(0xFF444466),
                        focusedTextColor     = TextWhite,
                        unfocusedTextColor   = TextWhite,
                        cursorColor          = PurplePrimary,
                        focusedContainerColor   = FieldBackground,
                        unfocusedContainerColor = FieldBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value         = password,
                    onValueChange = { password = it },
                    placeholder   = { Text("Password", color = TextGray) },
                    singleLine    = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape  = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = PurplePrimary,
                        unfocusedBorderColor = Color(0xFF444466),
                        focusedTextColor     = TextWhite,
                        unfocusedTextColor   = TextWhite,
                        cursorColor          = PurplePrimary,
                        focusedContainerColor   = FieldBackground,
                        unfocusedContainerColor = FieldBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg.isNotEmpty()) {
                    Text(
                        text     = errorMsg,
                        color    = Color(0xFFFF6B6B),
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { viewModel.login(email, password) },
                    enabled = !isLoading,
                    shape   = RoundedCornerShape(12.dp),
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        disabledContainerColor = PurplePrimary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color  = TextWhite,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text       = "LOGIN",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = TextWhite
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text     = "Belum punya akun? ",
                        color    = TextGray,
                        fontSize = 14.sp
                    )
                    TextButton(
                        onClick      = {
                            viewModel.resetState()
                            navController.navigate(Routes.REGISTER)
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text       = "Daftar",
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