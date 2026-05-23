package com.altermarkt.app.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.altermarkt.app.ui.viewmodel.DetailViewModel

@Composable
fun DetailScreen(
    productId: String,
    viewModel: DetailViewModel,
    onBack: () -> Unit
) {
    val product by viewModel.product.collectAsState(initial = null)
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val context = LocalContext.current

    LaunchedEffect(productId) { viewModel.loadProduct(productId) }

    LaunchedEffect(Unit) {
        android.util.Log.d("DetailScreen", "productId: $productId")
        android.util.Log.d("DetailScreen", "product: ${viewModel.product.value}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PurpleAccent)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Column {
                Text("Detail", color = TextMuted, fontSize = 11.sp)
                Text("Postingan Barang", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PurpleAccent)
            }
        }

        product?.let { p ->
            AsyncImage(
                model = p.imageUrl.ifEmpty { null },
                contentDescription = p.title,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = PurpleAccent,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = p.category,
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
                Text(p.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text("Rp. ${"%,d".format(p.price)}", color = Color(0xFF00CFFF), fontSize = 16.sp)

                Text("Deskripsi Produk", color = TextMuted, fontSize = 12.sp)
                Text(p.description, color = Color(0xFFAAAAAA), fontSize = 12.sp, lineHeight = 20.sp)

                Text("Informasi Penjual", color = TextMuted, fontSize = 12.sp)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PurpleAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p.sellerName.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column {
                            Text(p.sellerName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(p.sellerPhone, color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }

                Button(
                    onClick = {
                        val phone = p.sellerPhone.replace("+", "").replace("-", "")
                        val uri = Uri.parse("https://wa.me/$phone")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Text("Hubungi Via WhatsApp", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}