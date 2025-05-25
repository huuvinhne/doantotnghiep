package com.example.doanltd.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.doanltd.AppDatabase
import com.example.doanltd.Navigation.Screen
import com.example.doanltd.RoomDatabase.NgDungRoom.NgDungEntity
import com.example.doanltd.View.SanPhamViewModel
import com.example.doanltd.data.HoaDon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

data class OrderNotification(
    val id: String,
    val orderId: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: NotificationType,
    val isRead: Boolean = false,
    val hoaDon: HoaDon,
    val notificationNumber: Int = 0 // Số thứ tự thông báo
)

enum class NotificationType {
    ORDER_PLACED,
    ORDER_APPROVED,
    ORDER_SHIPPING,
    ORDER_DELIVERED,
    ORDER_CANCELLED
}

data class NotificationTypeInfo(
    val icon: ImageVector,
    val backgroundColor: Color,
    val senderName: String,
    val senderAvatar: String, // URL hoặc resource cho avatar
    val badgeColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController, viewModel: SanPhamViewModel = viewModel()) {
    val hoaDons by remember { derivedStateOf { viewModel.hoadons } }
    val context = LocalContext.current
    var user by remember { mutableStateOf<NgDungEntity?>(null) }
    val db = AppDatabase.getDatabase(context).ngDungDao()
    var notifications by remember { mutableStateOf<List<OrderNotification>>(emptyList()) }
    var showNotificationPopup by remember { mutableStateOf(false) }
    var selectedNotification by remember { mutableStateOf<OrderNotification?>(null) }

    // Đếm số thông báo chưa đọc
    val unreadCount = notifications.count { !it.isRead }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val userList = db.getAll()
            if (userList.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    user = userList[0]
                }
            }
        }
    }

    // Generate notifications from order data with numbering
    LaunchedEffect(hoaDons, user) {
        if (user != null) {
            val userOrders = hoaDons.filter { it.MaNgD == user?.MaNgD }
            notifications = generateNotificationsFromOrders(userOrders)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Thông báo",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = Color.Black
                        )

                        // Badge hiển thị số thông báo chưa đọc
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.Red,
                                        CircleShape
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Nút đánh dấu tất cả đã đọc
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = {
                                notifications = notifications.map { it.copy(isRead = true) }
                                Toast.makeText(context, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text(
                                "Đọc tất cả",
                                color = Color(0xFF007AFF),
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(onClick = { /* Handle notifications settings */ }) {
                        Icon(
                            Icons.Default.Settings,
                            "Notification Settings",
                            tint = Color(0xFFFF6B35)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB3E5FC)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, unreadCount)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB3E5FC))
                .padding(paddingValues)
        ) {
            // Thống kê thông báo
            if (notifications.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NotificationStat("Tổng", notifications.size, Color(0xFF2196F3))
                        NotificationStat("Chưa đọc", unreadCount, Color.Red)
                        NotificationStat("Đã đọc", notifications.size - unreadCount, Color(0xFF4CAF50))
                    }
                }
            }

            if (notifications.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Chưa có thông báo nào",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sort notifications by timestamp (most recent first)
                    val sortedNotifications = notifications
                        .sortedWith(compareByDescending<OrderNotification> { it.timestamp }
                            .thenByDescending { it.orderId })

                    items(sortedNotifications) { notification ->
                        EnhancedNotificationItem(
                            notification = notification,
                            onClick = {
                                selectedNotification = notification
                                showNotificationPopup = true
                                // Mark as read
                                notifications = notifications.map {
                                    if (it.id == notification.id) it.copy(isRead = true) else it
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Notification popup
    if (showNotificationPopup && selectedNotification != null) {
        EnhancedNotificationPopup(
            notification = selectedNotification!!,
            onDismiss = {
                showNotificationPopup = false
                selectedNotification = null
            },
            onViewOrder = {
                showNotificationPopup = false
                selectedNotification = null
                navController.navigate("xem_don_hang")
            },
            viewModel = viewModel
        )
    }
}

@Composable
private fun NotificationStat(label: String, count: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun EnhancedNotificationItem(
    notification: OrderNotification,
    onClick: () -> Unit
) {
    val typeInfo = getNotificationTypeInfo(notification.type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFF0F8FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 2.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar với logo đại diện
            Box {
                // Avatar background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(typeInfo.backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    // Logo chính
                    Icon(
                        typeInfo.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Số thông báo badge
                if (notification.notificationNumber > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(typeInfo.badgeColor)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notification.notificationNumber.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Notification content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Sender name với icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = typeInfo.senderName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Verified badge cho hệ thống
                    if (typeInfo.senderName != "Người dùng") {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF1DA1F2),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Message content
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = Color(0xFF666666),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Timestamp với order ID
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatRelativeTime(notification.timestamp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = Color.Gray
                    )

                    Text(
                        text = " • #${notification.orderId}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = Color(0xFF007AFF)
                    )
                }
            }

            // Right side elements
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Status indicator
                Box(
                    modifier = Modifier
                        .background(
                            if (notification.isRead) Color.Gray.copy(alpha = 0.3f) else Color.Red,
                            CircleShape
                        )
                        .size(8.dp)
                )

                // Unread indicator
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color.Red,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "MỚI",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Arrow indicator
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EnhancedNotificationPopup(
    notification: OrderNotification,
    onDismiss: () -> Unit,
    onViewOrder: () -> Unit,
    viewModel: SanPhamViewModel
) {
    val formattedPrice = NumberFormat.getInstance(Locale("vi", "VN")).format(notification.hoaDon.TongTien)
    val typeInfo = getNotificationTypeInfo(notification.type)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header với avatar và thông tin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Enhanced avatar
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(typeInfo.backgroundColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    typeInfo.icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Notification number badge
                            if (notification.notificationNumber > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(typeInfo.badgeColor)
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = notification.notificationNumber.toString(),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = typeInfo.senderName,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = Color.Black
                                )

                                if (typeInfo.senderName != "Người dùng") {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = Color(0xFF1DA1F2),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Text(
                                text = formatTimestamp(notification.timestamp),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Notification title
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Message
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    ),
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Order details card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F9FA)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                tint = Color(0xFF007AFF),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Chi tiết đơn hàng",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OrderDetailRow("Mã đơn:", "#${notification.hoaDon.MaHD}")
                        OrderDetailRow("Trạng thái:", notification.hoaDon.TrangThai)
                        OrderDetailRow("Địa chỉ:", notification.hoaDon.DiaChi)
                        OrderDetailRow("Tổng tiền:", "$formattedPrice VND", isHighlight = true)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Đóng")
                    }

                    Button(
                        onClick = onViewOrder,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF)
                        )
                    ) {
                        Text("Xem đơn hàng")
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderDetailRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isHighlight) Color(0xFFFF6B35) else Color.Black
        )
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController, unreadCount: Int = 0) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
            label = { Text("Trang chủ", fontSize = 10.sp) },
            selected = false,
            onClick = { navController.navigate(Screen.Home.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF009966),
                selectedTextColor = Color(0xFF009966),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng") },
            label = { Text("Giỏ hàng", fontSize = 10.sp) },
            selected = false,
            onClick = { navController.navigate(Screen.Cart.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF009966),
                selectedTextColor = Color(0xFF009966),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Đơn hàng") },
            label = { Text("Đơn hàng", fontSize = 10.sp) },
            selected = false,
            onClick = { navController.navigate(Screen.XemDonHang.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF009966),
                selectedTextColor = Color(0xFF009966),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = {
                Box {
                    Icon(Icons.Default.Notifications, contentDescription = "Thông báo")
                    // Badge cho thông báo chưa đọc
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            label = { Text("Thông báo", fontSize = 10.sp) },
            selected = true,
            onClick = { /* Current screen */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF009966),
                selectedTextColor = Color(0xFF009966),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Thông tin") },
            label = { Text("Thông tin", fontSize = 10.sp) },
            selected = false,
            onClick = { navController.navigate(Screen.Setting.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF009966),
                selectedTextColor = Color(0xFF009966),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}

// Helper functions
private fun generateNotificationsFromOrders(orders: List<HoaDon>): List<OrderNotification> {
    val notifications = mutableListOf<OrderNotification>()
    val currentTime = System.currentTimeMillis()
    var notificationCounter = 1

    orders.forEach { order ->
        val baseTimestamp = when (order.TrangThai) {
            "Đã giao" -> currentTime - 10000
            "Đang giao" -> currentTime - 20000
            "Đặt hàng thành công" -> currentTime - 30000
            "Đã đặt" -> currentTime - 40000
            "Đã hủy" -> currentTime - 50000
            else -> currentTime - 60000
        }

        when (order.TrangThai) {
            "Đã đặt" -> {
                notifications.add(
                    OrderNotification(
                        id = "${order.MaHD}_placed",
                        orderId = order.MaHD,
                        title = "Đơn hàng đã được đặt",
                        message = "Đơn hàng ${order.MaHD} đã được đặt thành công và đang chờ xử lý.",
                        timestamp = baseTimestamp,
                        type = NotificationType.ORDER_PLACED,
                        hoaDon = order,
                        notificationNumber = notificationCounter++
                    )
                )
            }
            "Đặt hàng thành công" -> {
                notifications.add(
                    OrderNotification(
                        id = "${order.MaHD}_approved",
                        orderId = order.MaHD,
                        title = "Đơn hàng đã được phê duyệt",
                        message = "Đơn hàng ${order.MaHD} đã được phê duyệt và đang chuẩn bị giao hàng.",
                        timestamp = baseTimestamp,
                        type = NotificationType.ORDER_APPROVED,
                        hoaDon = order,
                        notificationNumber = notificationCounter++
                    )
                )
            }
            "Đang giao" -> {
                notifications.add(
                    OrderNotification(
                        id = "${order.MaHD}_shipping",
                        orderId = order.MaHD,
                        title = "Đơn hàng đang được giao",
                        message = "Đơn hàng ${order.MaHD} đang trên đường giao đến bạn.",
                        timestamp = baseTimestamp,
                        type = NotificationType.ORDER_SHIPPING,
                        hoaDon = order,
                        notificationNumber = notificationCounter++
                    )
                )
            }
            "Đã giao" -> {
                notifications.add(
                    OrderNotification(
                        id = "${order.MaHD}_delivered",
                        orderId = order.MaHD,
                        title = "Đơn hàng đã được giao",
                        message = "Đơn hàng ${order.MaHD} đã được giao thành công. Cảm ơn bạn đã mua hàng!",
                        timestamp = baseTimestamp,
                        type = NotificationType.ORDER_DELIVERED,
                        hoaDon = order,
                        notificationNumber = notificationCounter++
                    )
                )
            }
            "Đã hủy" -> {
                notifications.add(
                    OrderNotification(
                        id = "${order.MaHD}_cancelled",
                        orderId = order.MaHD,
                        title = "Đơn hàng đã bị hủy",
                        message = "Đơn hàng ${order.MaHD} đã bị hủy. Nếu có thắc mắc, vui lòng liên hệ với chúng tôi.",
                        timestamp = baseTimestamp,
                        type = NotificationType.ORDER_CANCELLED,
                        hoaDon = order,
                        notificationNumber = notificationCounter++
                    )
                )
            }
        }
    }

    return notifications.sortedByDescending { it.timestamp }
}

private fun getNotificationTypeInfo(type: NotificationType): NotificationTypeInfo = when (type) {
    NotificationType.ORDER_PLACED -> NotificationTypeInfo(
        icon = Icons.Default.ShoppingCart,
        backgroundColor = Color(0xFFE91E63),
        senderName = "Người dùng",
        senderAvatar = "",
        badgeColor = Color(0xFFFF4081)
    )
    NotificationType.ORDER_APPROVED -> NotificationTypeInfo(
        icon = Icons.Default.CheckCircle,
        backgroundColor = Color(0xFF4CAF50),
        senderName = "Cửa hàng",
        senderAvatar = "",
        badgeColor = Color(0xFF66BB6A)
    )
    NotificationType.ORDER_SHIPPING -> NotificationTypeInfo(
        icon = Icons.Default.LocalShipping,
        backgroundColor = Color(0xFFFF9800),
        senderName = "Giao hàng",
        senderAvatar = "",
        badgeColor = Color(0xFFFFB74D)
    )
    NotificationType.ORDER_DELIVERED -> NotificationTypeInfo(
        icon = Icons.Default.Done,
        backgroundColor = Color(0xFF8BC34A),
        senderName = "Giao hàng",
        senderAvatar = "",
        badgeColor = Color(0xFF9CCC65)
    )
    NotificationType.ORDER_CANCELLED -> NotificationTypeInfo(
        icon = Icons.Default.Cancel,
        backgroundColor = Color(0xFFF44336),
        senderName = "Cửa hàng",
        senderAvatar = "",
        badgeColor = Color(0xFFEF5350)
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN"))
    return sdf.format(Date(timestamp))
}

private fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        minutes < 1 -> "Vừa xong"
        minutes < 60 -> "${minutes} phút trước"
        hours < 24 -> "${hours} giờ trước"
        days < 7 -> "${days} ngày trước"
        else -> formatTimestamp(timestamp)
    }
}
