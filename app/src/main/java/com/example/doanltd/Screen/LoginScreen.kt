package com.example.doanltd.Screen

import NgDung
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.doanltd.AppDatabase
import com.example.doanltd.Navigation.Screen
import com.example.doanltd.R
import com.example.doanltd.View.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    initialTabIndex: Int = 0 // 0 for login, 1 for register
) {
    var selectedTabIndex by remember { mutableStateOf(initialTabIndex) }
    var TKNgD by remember { mutableStateOf("") }
    var MatKhauNgD by remember { mutableStateOf("") }

    val dangNhapThanhCong by viewModel.dangNhapThanhCong.collectAsState()
    val dulieunguoidung by viewModel.duLieuNguoiDung.collectAsState()

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context).ngDungDao()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedTabIndex == 0) "Đăng Nhập" else "Đăng Ký",
                        color = Color.Black,
                        modifier = Modifier.weight(0.9f) // Đẩy các thành phần khác sang bên phải
                    )
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                }
            }
        )

        // Tab Layout
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF6D88F4),
            contentColor = Color.White
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Đăng Nhập") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Đăng Ký") }
            )
        }

        if (selectedTabIndex == 0) {
            // Login Tab Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "\t\tWelcome to \n Út Khờ Snack",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color(0xFF6D88F4),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                OutlinedTextField(
                    value = TKNgD,
                    onValueChange = { TKNgD = it },
                    label = { Text("Tên đăng nhập") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color(0xFF6D88F4)
                        )
                    }
                )

                OutlinedTextField(
                    value = MatKhauNgD,
                    onValueChange = { MatKhauNgD = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color(0xFF6D88F4)
                        )
                    }
                )

                Row(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bạn chưa có tài khoản? ")
                    Text(
                        "Đăng ký ngay",
                        color = Color(0xFFFF4B12),
                        modifier = Modifier.clickable {
                            selectedTabIndex = 1
                        }
                    )
                }

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.dangNhapNguoiDung(
                                tkNgD = TKNgD,
                                matKhauNgD = MatKhauNgD
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D88F4)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ĐĂNG NHẬP", color = Color.White)
                }
            }
        } else {
            // Register Tab Content
            RegisterContent(navController = navController)
        }
    }

    // ⬇️ Đặt LaunchedEffect bên ngoài Column ⬇️
    LaunchedEffect(dangNhapThanhCong) {
        dangNhapThanhCong?.let {
            if (it) {
                dulieunguoidung?.let { user ->
                    db.insertUserByFields(
                        user.MaNgD,
                        user.TenNgD,
                        user.Email,
                        user.SDT,
                        user.TKNgD,
                        user.TrangThai,
                        user.ChucVu
                    )
                    if (user.ChucVu.equals("NguoiDung") && user.TrangThai == 1) {
                        navController.navigate("home")
                    } else {
                        navController.navigate("admin")
                    }
                }

                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(navController: NavController) {
    var tenNgD by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var sdt by remember { mutableStateOf("") }
    var tkNgD by remember { mutableStateOf("") }
    var matKhau by remember { mutableStateOf("") }
    var xacNhanMatKhau by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "\t\tTạo tài khoản \n Út Khờ Snack",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color(0xFF6D88F4),
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        OutlinedTextField(
            value = tenNgD,
            onValueChange = { tenNgD = it },
            label = { Text("Họ và tên") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            trailingIcon = {
                if (tenNgD.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color(0xFF6D88F4)
                    )
                }
            }
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            trailingIcon = {
                if (email.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color(0xFF6D88F4)
                    )
                }
            }
        )

        OutlinedTextField(
            value = sdt,
            onValueChange = { sdt = it },
            label = { Text("Số điện thoại") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            trailingIcon = {
                if (sdt.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color(0xFF6D88F4)
                    )
                }
            }
        )

        OutlinedTextField(
            value = tkNgD,
            onValueChange = { tkNgD = it },
            label = { Text("Tên đăng nhập") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            trailingIcon = {
                if (tkNgD.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color(0xFF6D88F4)
                    )
                }
            }
        )

        OutlinedTextField(
            value = matKhau,
            onValueChange = { matKhau = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            trailingIcon = {
                if (matKhau.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color(0xFF6D88F4)
                    )
                }
            }
        )

        OutlinedTextField(
            value = xacNhanMatKhau,
            onValueChange = { xacNhanMatKhau = it },
            label = { Text("Xác nhận mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            trailingIcon = {
                if (xacNhanMatKhau.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color(0xFF6D88F4)
                    )
                }
            }
        )

        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Đã có tài khoản? ")
            Text(
                "Đăng nhập",
                color = Color(0xFFFF4B12),
                modifier = Modifier.clickable {
                    navController.navigate("login_screen?tab=login") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }
            )
        }

        Button(
            onClick = {
                // Handle registration logic here
                // For now, just navigate back to login
                navController.navigate("login_screen?tab=login") {
                    popUpTo("login_screen") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D88F4)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("ĐĂNG KÝ", color = Color.White)
        }
    }
}
