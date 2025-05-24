package com.example.doanltd.Screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.doanltd.AppDatabase
import com.example.doanltd.CartManager

import com.example.doanltd.Navigation.Screen
import com.example.doanltd.RoomDatabase.CartRoom.CartItemEntity
import com.example.doanltd.View.SanPhamViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String?,
    context: Context,
    sanPhamViewModel: SanPhamViewModel = viewModel()
) {
    // Kiểm tra productId có hợp lệ không
    if (productId == null) {
        Text("Lỗi: Không có Product ID")
        return
    }

    val product by remember { derivedStateOf { sanPhamViewModel.productDetail } }

    LaunchedEffect(productId) {
        sanPhamViewModel.fetchProductDetail(productId)
    }

    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = product!!.HinhSp,  // Ảnh sản phẩm
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "${product!!.DonGia.toInt()}đ",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF4B12)
                        )
                        Text(
                            "Giá gốc: 100.000đ",
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                    }
                    Text(
                        "-25%",
                        modifier = Modifier
                            .background(
                                color = Color(0xFFFF4B12),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    product!!.TenSp,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Mô tả sản phẩm:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    product!!.MoTa,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val cartItem = CartItemEntity(
                            MaSp = product!!.MaSp,
                            name = product!!.TenSp,
                            price = product!!.DonGia,
                            quantity = 1,
                            imageUrl = product!!.HinhSp,
                            SoLuongSP = product!!.SoLuong
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            CartManager(context).addToCart(cartItem)
                        }
                        navController.navigate(Screen.Cart.route)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B12))
                ) {
                    Text("Thêm vào giỏ hàng")
                }

            }
        }
    }
}

