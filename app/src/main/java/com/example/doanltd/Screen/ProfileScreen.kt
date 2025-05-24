package com.example.doanltd.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doanltd.AppDatabase
import com.example.doanltd.RoomDatabase.NgDungRoom.NgDungEntity
import com.example.doanltd.View.AuthViewModel
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var user by remember { mutableStateOf<NgDungEntity?>(null) }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context).ngDungDao()

    var showEditDialog by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf("") }
    var editingValue by remember { mutableStateOf("") }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Thông tin cá nhân", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB3E5FC),
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB3E5FC))
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar + Name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "FASTFOOD H&V",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "FASTFOOD_ID",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Editable fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileField(
                    label = "Họ và Tên",
                    value = user?.TenNgD ?: "FASTFOOD H&V",
                    onEditClick = {
                        editingField = "name"
                        editingValue = user?.TenNgD ?: ""
                        showEditDialog = true
                    }
                )

                ProfileField(
                    label = "Tên đăng nhập",
                    value = "FASTFOOD_ID",
                    onEditClick = {
                        editingField = "username"
                        editingValue = "FASTFOOD_ID"
                        showEditDialog = true
                    }
                )

                ProfileField(
                    label = "Địa chỉ",
                    value = AddressManager.currentAddress,
                    onEditClick = {
                        editingField = "address"
                        editingValue = AddressManager.currentAddress
                        showEditDialog = true
                    }
                )

                ProfileField(
                    label = "Số điện thoại",
                    value = user?.SDT ?: "0335487716",
                    onEditClick = {
                        editingField = "phone"
                        editingValue = user?.SDT ?: ""
                        showEditDialog = true
                    }
                )
            }
        }

        // Dialog chỉnh sửa
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = {
                    Text(
                        when (editingField) {
                            "name" -> "Chỉnh sửa Họ và Tên"
                            "username" -> "Chỉnh sửa Tên đăng nhập"
                            "address" -> "Chỉnh sửa Địa chỉ"
                            "phone" -> "Chỉnh sửa Số điện thoại"
                            else -> "Chỉnh sửa"
                        }
                    )
                },
                text = {
                    OutlinedTextField(
                        value = editingValue,
                        onValueChange = { editingValue = it },
                        label = {
                            Text(
                                when (editingField) {
                                    "name" -> "Họ và Tên"
                                    "username" -> "Tên đăng nhập"
                                    "address" -> "Địa chỉ"
                                    "phone" -> "Số điện thoại"
                                    else -> "Giá trị"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                user?.let { currentUser ->
                                    when (editingField) {
                                        "name" -> {
                                            currentUser.TenNgD = editingValue
                                            try {
                                                db.update(currentUser)
                                                withContext(Dispatchers.Main) {
                                                    viewModel.CapNhapNgDung(
                                                        currentUser.MaNgD,
                                                        currentUser.TenNgD,
                                                        currentUser.Email,
                                                        currentUser.SDT
                                                    )
                                                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                                                    showEditDialog = false
                                                }
                                            } catch (e: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                        "phone" -> {
                                            currentUser.SDT = editingValue
                                            try {
                                                db.update(currentUser)
                                                withContext(Dispatchers.Main) {
                                                    viewModel.CapNhapNgDung(
                                                        currentUser.MaNgD,
                                                        currentUser.TenNgD,
                                                        currentUser.Email,
                                                        currentUser.SDT
                                                    )
                                                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                                                    showEditDialog = false
                                                }
                                            } catch (e: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                        "address" -> {
                                            AddressManager.updateAddress(editingValue)
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "Cập nhật địa chỉ thành công!", Toast.LENGTH_SHORT).show()
                                                showEditDialog = false
                                            }
                                        }
                                        "username" -> {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "Tên đăng nhập không thể thay đổi!", Toast.LENGTH_SHORT).show()
                                                showEditDialog = false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Lưu", color = Color(0xFF009966))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Hủy", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    onEditClick: () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onEditClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Chỉnh sửa $label",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
