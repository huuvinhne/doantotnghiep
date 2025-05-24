package com.example.doanltd.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import com.example.doanltd.AppDatabase
import com.example.doanltd.Navigation.Screen
import com.example.doanltd.NgDungDao
import com.example.doanltd.RoomDatabase.NgDungRoom.NgDungEntity
import com.example.doanltd.View.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Shared address state object
object AddressManager {
    var currentAddress by mutableStateOf("192, đường Phạm Đức Sơn, Phường 2, Quận 8, Hồ Chí Minh")
        private set

    fun updateAddress(newAddress: String) {
        currentAddress = newAddress
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    var isDarkMode by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }
    var showChangePasswordPopup by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showSoftwareInfoDialog by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf<NgDungEntity?>(null) }
    val db = AppDatabase.getDatabase(context).ngDungDao()

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

    MaterialTheme(
        colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()
    ) {
        val backgroundColor = if (isDarkMode) MaterialTheme.colorScheme.background else Color(0xFFB3E5FC)
        val textColor = MaterialTheme.colorScheme.onBackground
        val navBarColor = if (isDarkMode) MaterialTheme.colorScheme.surface else Color.White

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = navBarColor,
                    contentColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color.Gray
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
                        label = { Text("Trang chủ", fontSize = 10.sp) },
                        selected = false,
                        onClick = { navController.navigate(Screen.Home.route) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF009966),
                            selectedTextColor = Color(0xFF009966),
                            unselectedIconColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            unselectedTextColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color.Gray
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
                            unselectedIconColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            unselectedTextColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Đơn hàng") },
                        label = { Text("Đơn hàng", fontSize = 10.sp) },
                        selected = false,
                        onClick = { navController.navigate(Screen.OrderHistory.route) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF009966),
                            selectedTextColor = Color(0xFF009966),
                            unselectedIconColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            unselectedTextColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Notifications, contentDescription = "Thông báo") },
                        label = { Text("Thông báo", fontSize = 10.sp) },
                        selected = false,
                        onClick = { navController.navigate(Screen.Message.route) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF009966),
                            selectedTextColor = Color(0xFF009966),
                            unselectedIconColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            unselectedTextColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Thông tin") },
                        label = { Text("Thông tin", fontSize = 10.sp) },
                        selected = true,
                        onClick = { navController.navigate(Screen.Setting.route) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF009966),
                            selectedTextColor = Color(0xFF009966),
                            unselectedIconColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            unselectedTextColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color.Gray
                        )
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Header section with profile info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            "FASTFOOD_ID",
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Xem chi tiết",
                            color = textColor,
                            fontSize = 14.sp
                        )
                        Icon(
                            Icons.Default.ArrowForwardIos,
                            contentDescription = "View details",
                            tint = textColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Settings menu items
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingMenuItem(
                        icon = Icons.Default.Person,
                        title = "Hồ sơ",
                        onClick = { navController.navigate(Screen.Profile.route) },
                        isDarkMode = isDarkMode
                    )

                    SettingMenuItemWithSwitch(
                        icon = Icons.Default.DarkMode,
                        title = "Chế độ tối",
                        isChecked = isDarkMode,
                        onCheckedChange = { isDarkMode = it },
                        isDarkMode = isDarkMode
                    )

                    SettingMenuItem(
                        icon = Icons.Default.LocationOn,
                        title = "Địa chỉ",
                        onClick = { showAddressDialog = true },
                        isDarkMode = isDarkMode
                    )

                    SettingMenuItem(
                        icon = Icons.Default.Lock,
                        title = "Đổi mật khẩu",
                        onClick = { showChangePasswordPopup = true },
                        isDarkMode = isDarkMode
                    )

                    SettingMenuItem(
                        icon = Icons.Default.Info,
                        title = "Thông tin về phần mềm",
                        onClick = { showSoftwareInfoDialog = true },
                        isDarkMode = isDarkMode
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            CoroutineScope(Dispatchers.IO).launch { user?.let { db.delete(it) } }
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B12))
                ) {
                    Text("Đăng Xuất", color = Color.White)
                }

                // Dialogs
                if (showChangePasswordPopup) {
                    user?.let {
                        ChangePasswordDialog(
                            it,
                            onDismiss = { showChangePasswordPopup = false },
                            onMessageChange = { msg, success ->
                                message = msg
                                isSuccess = success
                            },
                            authViewModel = AuthViewModel(),
                            navController = navController,
                            db = db
                        )
                    }
                }

                if (showAddressDialog) {
                    AddressChangeDialog(
                        onDismiss = { showAddressDialog = false }
                    )
                }

                if (showSoftwareInfoDialog) {
                    SoftwareInfoDialog(
                        onDismiss = { showSoftwareInfoDialog = false }
                    )
                }

                // Display Message
                if (message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (isSuccess) Color.Green else Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message,
                            color = if (isSuccess) Color.Green else Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val cardColor = if (isDarkMode) MaterialTheme.colorScheme.surface else Color.White
    val textColor = MaterialTheme.colorScheme.onSurface
    val iconColor = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                title,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = "Navigate",
                tint = if (isDarkMode) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SettingMenuItemWithSwitch(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDarkMode: Boolean
) {
    val cardColor = if (isDarkMode) MaterialTheme.colorScheme.surface else Color.White
    val textColor = MaterialTheme.colorScheme.onSurface
    val iconColor = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                title,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF009966),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun AddressChangeDialog(
    onDismiss: () -> Unit
) {
    var newAddress by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                "Đổi địa chỉ",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Old address field - read-only, auto-populated from HomeScreen
                Text(
                    "Địa chỉ hiện tại:",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = AddressManager.currentAddress,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // New address field - user can type here
                OutlinedTextField(
                    value = newAddress,
                    onValueChange = { newAddress = it },
                    label = { Text("Địa chỉ mới") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nhập địa chỉ mới của bạn...") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newAddress.isNotEmpty()) {
                        AddressManager.updateAddress(newAddress)
                        Toast.makeText(context, "Địa chỉ đã được cập nhật thành công!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    } else {
                        Toast.makeText(context, "Vui lòng nhập địa chỉ mới!", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Lưu", color = Color(0xFF009966))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Hủy", color = Color(0xFF9C27B0))
            }
        }
    )
}

@Composable
fun SoftwareInfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                "Thông tin phần mềm",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("Tên ứng dụng: Út Khờ Snack")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Phiên bản: 1.0.0")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nhà phát triển: FastFood Team")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ngày phát hành: 2024")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Mô tả: Ứng dụng đặt đồ ăn nhanh trực tuyến")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Liên hệ: support@fastfood.com")
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Đóng", color = Color(0xFF009966))
            }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    user: NgDungEntity,
    navController: NavController,
    db: NgDungDao,
    onDismiss: () -> Unit,
    onMessageChange: (String, Boolean) -> Unit,
    authViewModel: AuthViewModel
) {
    var MatKhauNgD by remember { mutableStateOf("") }
    var MatKhauMoi by remember { mutableStateOf("") }
    var NhapLaiMatKhau by remember { mutableStateOf("") }

    var isPasswordVisibleNgD by remember { mutableStateOf(false) }
    var isPasswordVisibleMoi by remember { mutableStateOf(false) }
    var isPasswordVisibleNhapLai by remember { mutableStateOf(false) }

    val capNhatMatKhauThanhCong by authViewModel.capNhatMatKhauThanhCong.collectAsState()
    val thongBaoCapNhatMatKhau by authViewModel.thongbaocapnhatmatkhau.collectAsState()

    LaunchedEffect(capNhatMatKhauThanhCong, thongBaoCapNhatMatKhau) {
        val isSuccessful = capNhatMatKhauThanhCong
        if (isSuccessful != null) {
            onMessageChange(thongBaoCapNhatMatKhau ?: "", isSuccessful)
            if (isSuccessful) {
                onDismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    user.let { db.delete(it) }
                }
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            authViewModel.resetPasswordChangeState()
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Đổi Mật Khẩu") },
        text = {
            Column {
                OutlinedTextField(
                    value = MatKhauNgD,
                    onValueChange = { MatKhauNgD = it },
                    label = { Text("Mật khẩu cũ") },
                    visualTransformation = if (isPasswordVisibleNgD) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisibleNgD = !isPasswordVisibleNgD }) {
                            Icon(
                                imageVector = if (isPasswordVisibleNgD) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = MatKhauMoi,
                    onValueChange = { MatKhauMoi = it },
                    label = { Text("Mật khẩu mới") },
                    visualTransformation = if (isPasswordVisibleMoi) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisibleMoi = !isPasswordVisibleMoi }) {
                            Icon(
                                imageVector = if (isPasswordVisibleMoi) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = NhapLaiMatKhau,
                    onValueChange = { NhapLaiMatKhau = it },
                    label = { Text("Nhập lại mật khẩu mới") },
                    visualTransformation = if (isPasswordVisibleNhapLai) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisibleNhapLai = !isPasswordVisibleNhapLai }) {
                            Icon(
                                imageVector = if (isPasswordVisibleNhapLai) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            val context = LocalContext.current
            TextButton(onClick = {
                when {
                    MatKhauNgD.isEmpty() || MatKhauMoi.isEmpty() || NhapLaiMatKhau.isEmpty() -> {
                        Toast.makeText(context, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show()
                    }
                    MatKhauMoi != NhapLaiMatKhau -> {
                        Toast.makeText(context, "Mật khẩu mới không khớp.", Toast.LENGTH_SHORT).show()
                    }
                    MatKhauNgD == MatKhauMoi -> {
                        Toast.makeText(context, "Mật khẩu cũ và mới không được giống nhau.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            authViewModel.capNhatMatKhau(
                                MaNgD = user.MaNgD,
                                MatKhauCu = MatKhauNgD,
                                MatKhauMoi = MatKhauMoi
                            )
                        }
                    }
                }
            }) {
                Text("Xác Nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Hủy")
            }
        }
    )
}
