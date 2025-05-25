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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB3E5FC)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    // Light blue background matching the image
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB3E5FC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Custom top bar with light blue background
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Logo placeholder - replace with your actual logo
                Text(
                    text = "🍔H",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF4B12)
                )
            }

            // Product image with rounded corners
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 16.dp)
            ) {
                AsyncImage(
                    model = product!!.HinhSp,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Product details section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Price section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${product!!.DonGia.toInt()}.000đ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF4B12)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "60.000đ",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Product name
                Text(
                    text = product!!.TenSp,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2),
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Product description
                Text(
                    text = "Chi tiết món ăn gồm có:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product!!.MoTa,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Add to cart button
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
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4FC3F7)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            "Thêm vào giỏ hàng",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Order button - Updated to navigate to OrderDetailsScreen
                    Button(
                        onClick = {
                            // Add product to cart first
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
                            // Navigate to OrderDetailsScreen
                            navController.navigate(Screen.OrderDetails.route)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF26A69A)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            "Đặt hàng",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}