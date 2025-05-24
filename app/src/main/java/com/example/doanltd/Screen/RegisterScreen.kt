package com.example.doanltd.Screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doanltd.R
import com.example.doanltd.View.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController,viewModel: AuthViewModel = viewModel()) {
    var TenNgD by remember { mutableStateOf("") }
    var TKNgD by remember { mutableStateOf("") }
    var MatKhauNgD by remember { mutableStateOf("") }
    var SDT by remember { mutableStateOf("") }
    var Email by remember { mutableStateOf("") }
    val dangKyThanhCong by viewModel.dangKyThanhCong.collectAsState()
    val context = LocalContext.current

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
                        text = "Đăng Ký",
                        color = Color.Black,
                        modifier = Modifier.weight(0.9f) // Đẩy các thành phần khác sang bên phải
                    )
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).clip(CircleShape)
                    )
                }
            }
        )

        // Tab Layout
        TabRow(
            selectedTabIndex = 1,
            containerColor = Color(0xFF6D88F4),
            contentColor = Color.White
        ) {
            Tab(
                selected = false,
                onClick = { navController.navigate("login") },
                text = { Text("Đăng Nhập") }
            )
            Tab(
                selected = true,
                onClick = { },
                text = { Text("Đăng Ký") }
            )
        }

        // thao tac dang nhap
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "\t\tWelcome to\nÚt Khờ Snack",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFF6D88F4),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 24.dp)
            )

            OutlinedTextField(
                value = TenNgD,
                onValueChange = { TenNgD = it },
                label = { Text("Họ và Tên") },
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
                value = Email,
                onValueChange = { Email = it },
                label = { Text("Email") },
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
                value = SDT,
                onValueChange = { SDT = it },
                label = { Text("Số điện thoại") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                label = { Text("Mật khẩu") },
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
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Bạn đã có Tài Khoản? ")
                Text(
                    "Đăng Nhập",
                    color = Color(0xFFFF4B12),
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )
            }

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.dangKyNguoiDung(
                            tenNgD = TenNgD,
                            sdt = SDT,
                            tkNgD = TKNgD,
                            matKhauNgD = MatKhauNgD,
                            Email=Email
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D88F4)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Đăng Ký", color = Color.White)
            }
        }
    }
    val dangKyError by viewModel.dangKyError.collectAsState()

    LaunchedEffect(dangKyThanhCong, dangKyError) {
        if (dangKyError != null) {
            Toast.makeText(context, dangKyError, Toast.LENGTH_SHORT).show()
        } else if (dangKyThanhCong == true) {
            Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
            navController.navigate("login") // Chuyển về màn hình đăng nhập
        } else if (dangKyThanhCong == false) {
            Toast.makeText(context, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show()
        }
    }

}

