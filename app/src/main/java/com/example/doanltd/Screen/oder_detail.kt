package com.example.doanltd.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
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
    return "${formatter.format(amount)} VND"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(navController: NavController, viewModel: SanPhamViewModel = viewModel()) {
    var selectedPaymentMethod by remember { mutableStateOf<String?>(null) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var customerNote by remember { mutableStateOf("") }

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val cartDao = remember { db.cartDao() }
    val cartItems = remember { mutableStateOf<List<CartItemEntity>>(emptyList()) }
    val totalAmount = remember { mutableStateOf(0.0) }
    var showError by remember { mutableStateOf(false) }

    var user by remember { mutableStateOf<NgDungEntity?>(null) }
    val dbdao = AppDatabase.getDatabase(context).ngDungDao()

    val hoadonthanhcong by viewModel.hoadonthanhcong.collectAsState()
    val hoadonthongbao by viewModel.hoadonthongbao.collectAsState()
    val MaHd by viewModel.MaHd.collectAsState()

    LaunchedEffect(Unit) {
        val items = cartDao.getAllCartItems()
        cartItems.value = items
        totalAmount.value = items.sumOf { it.price * it.quantity }
        CoroutineScope(Dispatchers.IO).launch {
            val userList = dbdao.getAll()
            if (userList.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    user = userList[0]
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin đơn hàng") },
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
                .padding(16.dp)
        ) {

            // Order Details
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems.value) { cartItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = cartItem.imageUrl,
                            contentDescription = "Hình ảnh sản phẩm",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        ) {
                            Text(text = cartItem.name, fontWeight = FontWeight.Bold)
                            Text(text = "Giá: ${formatCurrency(cartItem.price)}")
                            Text(text = "Số lượng: ${cartItem.quantity}")
                        }
                    }
                }
            }

            // Order Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Tóm tắt đơn hàng",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tiền sản phẩm:")
                        Text(formatCurrency(totalAmount.value))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Vận chuyển:")
                        Text(
                            "0đ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough
                            )
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tổng:",
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Text(formatCurrency(totalAmount.value),
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                }
            }

            // Payment Methods
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Hình thức thanh toán: khi nhận hàng",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Customer Note
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Địa chỉ :",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = customerNote,
                        onValueChange = {
                            customerNote = it
                            showError = false
                        },
                        label = { Text("Nhập địa chỉ.......") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = showError
                    )
                    if (showError) {
                        Text(
                            text = "Vui lòng nhập địa chỉ!",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Order Button
            Button(
                onClick = {
                    if (customerNote.isBlank()) {
                        showError = true // Hiển thị lỗi nếu chưa nhập địa chỉ
                        return@Button
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.themhoadon(user!!.MaNgD, totalAmount.value, customerNote)
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text("Đặt hàng")
            }
        }
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
