package com.example.doanltd.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.doanltd.AppDatabase
import com.example.doanltd.Navigation.Screen
import com.example.doanltd.RoomDatabase.CartRoom.CartItemEntity
import com.example.doanltd.RoomDatabase.NgDungRoom.NgDungEntity
import com.example.doanltd.View.SanPhamViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

// Hàm định dạng số tiền
fun formatCurrency(amount: Double): String {
    val formatter = DecimalFormat("#,###")
    return "${formatter.format(amount)}đ"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(navController: NavController, viewModel: SanPhamViewModel = viewModel()) {
    var selectedPaymentMethod by remember { mutableStateOf("Thanh toán bằng tiền mặt") }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var customerNote by remember { mutableStateOf("19 Trần Xuân Soạn Tân Kiểng Quận 7 TPHCM") }

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val cartDao = remember { db.cartDao() }
    val cartItems = remember { mutableStateOf<List<CartItemEntity>>(emptyList()) }
    val totalAmount = remember { mutableStateOf(0.0) }
    val shippingFee = 15000.0
    val productTotal = remember { mutableStateOf(0.0) }
    var showError by remember { mutableStateOf(false) }

    var user by remember { mutableStateOf<NgDungEntity?>(null) }
    val dbdao = AppDatabase.getDatabase(context).ngDungDao()

    val hoadonthanhcong by viewModel.hoadonthanhcong.collectAsState()
    val hoadonthongbao by viewModel.hoadonthongbao.collectAsState()
    val MaHd by viewModel.MaHd.collectAsState()

    LaunchedEffect(Unit) {
        val items = cartDao.getAllCartItems()
        cartItems.value = items
        productTotal.value = items.sumOf { it.price * it.quantity }
        totalAmount.value = productTotal.value + shippingFee
        CoroutineScope(Dispatchers.IO).launch {
            val userList = dbdao.getAll()
            if (userList.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    user = userList[0]
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB3E5FC))
    ) {
        // Custom Top Bar with back to cart navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB3E5FC))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    // Navigate back to cart screen specifically
                    navController.navigate(Screen.Cart.route) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                }
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back to Cart",
                    tint = Color.Black
                )
            }

            Text(
                text = "Xác nhận đơn hàng",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            Icon(
                Icons.Default.LocalShipping,
                contentDescription = "Delivery",
                tint = Color(0xFFFF6B35),
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Delivery Address Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(0xFF009966),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = customerNote,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { showAddressDialog = true }
                        ) {
                            Text(
                                "Chỉnh sửa",
                                color = Color(0xFF009966),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Thứ ba - 03976173341",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Text(
                        "Giao ngay",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            "Tiêu chuẩn - 18:00",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = { /* Handle delivery time change */ }
                        ) {
                            Text(
                                "Đổi sang tùy chọn",
                                color = Color(0xFF009966),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Restaurant Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Restaurant,
                    contentDescription = "Restaurant",
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Cửa hàng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Order Items
            cartItems.value.forEach { cartItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = cartItem.imageUrl,
                            contentDescription = "Product image",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = cartItem.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Text(
                                text = formatCurrency(cartItem.price),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFF6B35),
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Giao hàng",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        // Quantity Controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (cartItem.quantity > 1) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            cartDao.updateCartItem(cartItem.copy(quantity = cartItem.quantity - 1))
                                            val items = cartDao.getAllCartItems()
                                            withContext(Dispatchers.Main) {
                                                cartItems.value = items
                                                productTotal.value = items.sumOf { it.price * it.quantity }
                                                totalAmount.value = productTotal.value + shippingFee
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = Color(0xFF009966)
                                )
                            }

                            Text(
                                text = cartItem.quantity.toString(),
                                modifier = Modifier
                                    .background(
                                        Color(0xFFE8F5E8),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        cartDao.updateCartItem(cartItem.copy(quantity = cartItem.quantity + 1))
                                        val items = cartDao.getAllCartItems()
                                        withContext(Dispatchers.Main) {
                                            cartItems.value = items
                                            productTotal.value = items.sumOf { it.price * it.quantity }
                                            totalAmount.value = productTotal.value + shippingFee
                                        }
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = Color(0xFF009966)
                                )
                            }
                        }
                    }
                }
            }

            // Order Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Chi tiết thanh toán",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Tổng giá món (${cartItems.value.sumOf { it.quantity }} món)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            formatCurrency(productTotal.value),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Phí giao hàng (2km)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            formatCurrency(shippingFee),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Tổng thanh toán",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            formatCurrency(totalAmount.value),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Payment Methods
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Phương thức thanh toán",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val paymentMethods = listOf(
                        "Thanh toán bằng tiền mặt",
                        "Thanh toán bằng ví điện tử",
                        "Thanh toán bằng tài khoản ngân hàng"
                    )

                    paymentMethods.forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = method }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPaymentMethod == method,
                                onClick = { selectedPaymentMethod = method },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF009966)
                                )
                            )
                            Text(
                                text = method,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            // Order Button
            Button(
                onClick = {
                    if (customerNote.isBlank()) {
                        showError = true
                        return@Button
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.themhoadon(user!!.MaNgD, totalAmount.value, customerNote)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    "Đặt hàng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Address Edit Dialog
    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Chỉnh sửa địa chỉ") },
            text = {
                TextField(
                    value = customerNote,
                    onValueChange = { customerNote = it },
                    label = { Text("Địa chỉ giao hàng") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showAddressDialog = false }
                ) {
                    Text("Xác nhận", color = Color(0xFF009966))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddressDialog = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    LaunchedEffect(hoadonthanhcong) {
        hoadonthanhcong?.let {
            if (it) {
                val cartItems = cartDao.getAllCartItems()
                cartItems.forEach { cartItem ->
                    viewModel.themchitiethoadon(
                        MaHD = MaHd.toString(),
                        DonGia = cartItem.price,
                        MaSp = cartItem.MaSp,
                        SLMua = cartItem.quantity.toDouble()
                    )
                }

                CoroutineScope(Dispatchers.IO).launch {
                    cartDao.deleteAllCartItems()
                }

                navController.navigate(Screen.XemDonHang.route) {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                Toast.makeText(context, "$hoadonthongbao", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
