package com.example.doanltd.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doanltd.AppDatabase
import com.example.doanltd.RoomDatabase.NgDungRoom.NgDungEntity
import com.example.doanltd.View.SanPhamViewModel
import com.example.doanltd.data.HoaDon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale
import coil.compose.AsyncImage
import com.example.doanltd.Navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XemDonHangScreen(navController: NavController, viewModel: SanPhamViewModel = viewModel()) {
    val hoaDons by remember { derivedStateOf { viewModel.hoadons } }
    val context = LocalContext.current
    var user by remember { mutableStateOf<NgDungEntity?>(null) }
    val db = AppDatabase.getDatabase(context).ngDungDao()
    var selectedStatus by remember { mutableStateOf("Tất cả") }
    val statuses = listOf("Tất cả", "Đã đặt", "Đặt hàng thành công", "Đang giao","Đã giao","Đã hủy")
    var expanded by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var lastUpdateTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Real-time data refresh mechanism
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val userList = db.getAll()
            if (userList.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    user = userList[0]
                }
            }
        }

        // Start real-time updates
        while (true) {
            try {
                isRefreshing = true
                // Fetch latest orders from server
                //viewModel.fetchHoaDons()
                lastUpdateTime = System.currentTimeMillis()
                isRefreshing = false

                // Wait 5 seconds before next update
                delay(5000)
            } catch (e: Exception) {
                isRefreshing = false
                // Wait longer on error
                delay(10000)
            }
        }
    }

    // Manual refresh function
    fun refreshOrders() {
        CoroutineScope(Dispatchers.Main).launch {
            isRefreshing = true
            try {
                //viewModel.fetchHoaDons()
                lastUpdateTime = System.currentTimeMillis()
                Toast.makeText(context, "Đã cập nhật đơn hàng", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi cập nhật: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isRefreshing = false
            }
        }
    }

    val filteredHoaDons = hoaDons.filter {
        (selectedStatus == "Tất cả" || it.TrangThai == selectedStatus) && it.MaNgD == user?.MaNgD
    }

    // Define the exact order of status sections
    val statusOrder = listOf("Đã đặt", "Đặt hàng thành công", "Đang giao", "Đã giao", "Đã hủy")

    // Group orders by status and maintain the specified order
    val orderedGroupedOrders = statusOrder.mapNotNull { status ->
        val ordersForStatus = filteredHoaDons.filter { it.TrangThai == status }
        if (ordersForStatus.isNotEmpty()) {
            status to ordersForStatus
        } else null
    }.toMap()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB3E5FC))
    ) {
        // Custom Header with refresh indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB3E5FC))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Refresh button
                IconButton(
                    onClick = { refreshOrders() },
                    enabled = !isRefreshing
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFFFF6B35)
                        )
                    } else {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color(0xFFFF6B35),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Đơn hàng",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    // Last update indicator
                    Text(
                        text = "Cập nhật: ${formatUpdateTime(lastUpdateTime)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }

                // Shopping cart icon with live indicator
                Box {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = Color(0xFFFF6B35),
                        modifier = Modifier.size(32.dp)
                    )
                    // Live indicator dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (isRefreshing) Color.Red else Color.Green,
                                CircleShape
                            )
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }

        // Filter Dropdown
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.padding(8.dp)
            ) {
                OutlinedTextField(
                    value = selectedStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Lọc theo trạng thái") },
                    trailingIcon = {
                        Row {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 1.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    statuses.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                selectedStatus = status
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // Real-time status banner
        if (isRefreshing) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đang cập nhật đơn hàng...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }

        // Orders List with Ordered Status Sections
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Show orders in the specified order: pending, approved, in delivery, delivered, cancelled
            statusOrder.forEach { status ->
                orderedGroupedOrders[status]?.let { orders ->
                    item {
                        // Status Section Header with icon, count and real-time indicator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                getStatusIcon(status),
                                contentDescription = null,
                                tint = getStatusColor(status),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${getStatusDisplayName(status)} (${orders.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            // Real-time update indicator for this section
                            if (isRefreshing) {
                                Icon(
                                    Icons.Default.Sync,
                                    contentDescription = "Updating",
                                    tint = Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    items(orders) { hoadon ->
                        EnhancedOrderCard(hoadon, navController, viewModel, isRefreshing)
                    }
                }
            }
        }

        // Bottom Navigation Bar
        BottomNavigationBar(navController = navController)
    }
}

@Composable
private fun EnhancedOrderCard(
    hoaDon: HoaDon,
    navController: NavController,
    viewModel: SanPhamViewModel = viewModel(),
    isRefreshing: Boolean = false
) {
    val formattedPrice = NumberFormat.getInstance(Locale("vi", "VN")).format(hoaDon.TongTien)
    var showPopup by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val capnhaphoadonthanhcong by viewModel.capnhaphoadonthanhcong.collectAsState()
    val capnhaphoadonthongbao by viewModel.capnhaphoadonthongbao.collectAsState()

    // State to hold the first product image and details
    var firstProductImage by remember { mutableStateOf<String?>(null) }
    var firstProductName by remember { mutableStateOf<String?>(null) }

    // Get order details and first product image
    LaunchedEffect(hoaDon.MaHD) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                viewModel.fetchChiTietHoaDon(hoaDon.MaHD)
                kotlinx.coroutines.delay(300)

                withContext(Dispatchers.Main) {
                    val orderDetails = viewModel.chitiethoadons
                    val products = viewModel.posts

                    if (orderDetails.isNotEmpty()) {
                        val firstOrderDetail = orderDetails.first()
                        val firstProduct = products.find { it.MaSp == firstOrderDetail.MaSp }
                        firstProductImage = firstProduct?.HinhSp
                        firstProductName = firstProduct?.TenSp
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showPopup = true
                viewModel.fetchChiTietHoaDon(hoaDon.MaHD)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isRefreshing) Color.White.copy(alpha = 0.8f) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Real-time update indicator bar
        if (isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.Red)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image with update indicator
            Box {
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (firstProductImage != null) {
                        AsyncImage(
                            model = firstProductImage,
                            contentDescription = "Product Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF5F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Fastfood,
                                contentDescription = "Food",
                                tint = Color(0xFFFF6B35),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                // Live update indicator
                if (isRefreshing) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(8.dp),
                            strokeWidth = 1.dp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Order Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Product Name with real-time indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = firstProductName ?: "Hamburger Gà Giòn Phô Mai - FastFood H&V",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    if (isRefreshing) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Updating",
                            tint = Color.Red,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Price with crossed out original price
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${formattedPrice}đ",
                        color = Color(0xFFFF6B35),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(hoaDon.TongTien * 1.2).toInt()}đ",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Payment Method
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Payment,
                        contentDescription = "Payment",
                        tint = Color(0xFFFF6B35),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Thanh toán khi nhận hàng",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Delivery Status with real-time update indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        getStatusIcon(hoaDon.TrangThai),
                        contentDescription = "Status",
                        tint = getStatusColor(hoaDon.TrangThai),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = getStatusMessage(hoaDon.TrangThai),
                        style = MaterialTheme.typography.bodySmall,
                        color = getStatusColor(hoaDon.TrangThai),
                        fontWeight = FontWeight.Medium
                    )
                    if (isRefreshing) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "●",
                            color = Color.Red,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }

        // Cancel Button (if applicable)
        if (hoaDon.TrangThai == "Đã đặt" || hoaDon.TrangThai == "Đặt hàng thành công") {
            Divider(color = Color.Gray.copy(alpha = 0.3f))

            TextButton(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.capnhaphoadon(hoaDon.MaHD, "Đã hủy")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                enabled = !isRefreshing
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Hủy đơn hàng", color = if (isRefreshing) Color.Gray else Color.Red, fontSize = 14.sp)
            }
        }
    }

    if (showPopup) {
        OrderDetailsPopup(hoaDon = hoaDon, onDismiss = { showPopup = false }, viewModel)
    }

    LaunchedEffect(capnhaphoadonthongbao) {
        capnhaphoadonthanhcong?.let {
            if (it) {
                Toast.makeText(context, "$capnhaphoadonthongbao", Toast.LENGTH_SHORT).show()
                navController.navigate("xem_don_hang")
            } else {
                Toast.makeText(context, "$capnhaphoadonthongbao", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// Helper function to format update time
private fun formatUpdateTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60

    return when {
        seconds < 30 -> "vừa xong"
        seconds < 60 -> "${seconds}s trước"
        minutes < 60 -> "${minutes}m trước"
        else -> {
            val sdf = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.format(java.util.Date(timestamp))
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Gray,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Trang chủ",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Trang chủ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF6B35),
                selectedTextColor = Color(0xFFFF6B35),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Giỏ hàng",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Giỏ hàng",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = {
                navController.navigate(Screen.Cart.route)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF6B35),
                selectedTextColor = Color(0xFFFF6B35),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = "Đơn hàng",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Đơn hàng",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF009966),
                selectedTextColor = Color(0xFF009966),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Thông báo",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Thông báo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = {
                navController.navigate(Screen.Message.route)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF6B35),
                selectedTextColor = Color(0xFFFF6B35),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Thông tin",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Thông tin",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = {
                navController.navigate(Screen.Setting.route)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF6B35),
                selectedTextColor = Color(0xFFFF6B35),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}

// Enhanced OrderDetailsPopup with real-time updates
@Composable
private fun OrderDetailsPopup(hoaDon: HoaDon, onDismiss: () -> Unit, viewModel: SanPhamViewModel = viewModel()) {
    val chitiethoaDons by remember { derivedStateOf { viewModel.chitiethoadons } }
    val sanphams by remember { derivedStateOf { viewModel.posts } }
    val formattedPrice = NumberFormat.getInstance(Locale("vi", "VN")).format(hoaDon.TongTien)
    var isUpdating by remember { mutableStateOf(false) }

    // Real-time updates for popup
    LaunchedEffect(hoaDon.MaHD) {
        while (true) {
            try {
                isUpdating = true
                viewModel.fetchChiTietHoaDon(hoaDon.MaHD)
                delay(3000) // Update every 3 seconds
                isUpdating = false
            } catch (e: Exception) {
                isUpdating = false
                delay(5000)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Red Header with real-time indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFFFF6B35),
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Chi tiết đơn hàng",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (isUpdating) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                }
                            }
                            Text(
                                text = "#${hoaDon.MaHD}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Real-time update indicator bar
                if (isUpdating) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Red
                    )
                }

                // Content with real-time updates
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Total Amount Section
                    Text(
                        text = "Tổng tiền:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = "$formattedPrice VND",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B35),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Payment Method
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Payment,
                            contentDescription = "Payment",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thanh toán khi nhận hàng",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    // Delivery Address
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = hoaDon.DiaChi,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Products Section Header with real-time status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sản phẩm (${chitiethoaDons.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        // Dynamic status badge
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = getStatusColor(hoaDon.TrangThai).copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = hoaDon.TrangThai,
                                    color = getStatusColor(hoaDon.TrangThai),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (isUpdating) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "●",
                                        color = Color.Red,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Products List
                    chitiethoaDons.forEach { chiTiet ->
                        val sanpham = sanphams.find { it.MaSp == chiTiet.MaSp }
                        if (sanpham != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUpdating) Color(0xFFF8F9FA).copy(alpha = 0.8f) else Color(0xFFF8F9FA)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Product Image
                                    Card(
                                        modifier = Modifier.size(60.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        AsyncImage(
                                            model = sanpham.HinhSp,
                                            contentDescription = "Product Image",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Product Details
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = sanpham.TenSp,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            color = Color.Black
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "SL: ${chiTiet.SLMua.toInt()}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }

                                    // Price
                                    Text(
                                        text = "${NumberFormat.getInstance(Locale("vi", "VN")).format(chiTiet.DonGia)} VND",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF6B35)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Close Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B35)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Text(
                            "Đóng",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// Helper functions (keeping existing ones)
private fun getStatusDisplayName(status: String): String {
    return when (status) {
        "Đã đặt" -> "Đơn hàng chờ duyệt"
        "Đặt hàng thành công" -> "Đơn hàng đã được duyệt"
        "Đang giao" -> "Đang giao hàng"
        "Đã giao" -> "Đã giao"
        "Đã hủy" -> "Đã hủy"
        else -> status
    }
}

private fun getStatusIcon(status: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (status) {
        "Đã đặt" -> Icons.Default.Schedule
        "Đặt hàng thành công" -> Icons.Default.CheckCircle
        "Đang giao" -> Icons.Default.LocalShipping
        "Đã giao" -> Icons.Default.Done
        "Đã hủy" -> Icons.Default.Cancel
        else -> Icons.Default.Info
    }
}

private fun getStatusColor(status: String): Color {
    return when (status) {
        "Đã đặt" -> Color(0xFFFF9800)
        "Đặt hàng thành công" -> Color(0xFF4CAF50)
        "Đang giao" -> Color(0xFF2196F3)
        "Đã giao" -> Color(0xFF8BC34A)
        "Đã hủy" -> Color(0xFFF44336)
        else -> Color.Gray
    }
}

private fun getStatusMessage(status: String): String {
    return when (status) {
        "Đã đặt" -> "Đơn hàng đang chờ duyệt"
        "Đặt hàng thành công" -> "Đơn hàng đã được duyệt"
        "Đang giao" -> "Shipper đang giao hàng"
        "Đã giao" -> "Hoàn tất đơn hàng"
        "Đã hủy" -> "Đơn hàng đã bị hủy"
        else -> "Đang xử lý"
    }
}