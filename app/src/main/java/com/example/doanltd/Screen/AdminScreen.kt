package com.example.doanltd.Screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.doanltd.AppDatabase
import com.example.doanltd.Navigation.Screen

import com.example.doanltd.R
import com.example.doanltd.RoomDatabase.NgDungRoom.NgDungEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

import com.example.doanltd.View.SanPhamViewModel
import com.example.doanltd.data.HoaDon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController, viewModel: SanPhamViewModel = viewModel()) {
    val hoaDons by remember { derivedStateOf { viewModel.hoadons } }
    var user by remember { mutableStateOf<NgDungEntity?>(null) }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context).ngDungDao()

    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("Tất cả") }
    val statusList = listOf("Tất cả", "Đã đặt", "Đặt hàng thành công", "Đang giao","Đã giao","Đã hủy")

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

    val filteredHoaDons = if (selectedStatus == "Tất cả") hoaDons else hoaDons.filter { it.TrangThai == selectedStatus }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Adjust, contentDescription = "Home") },
                    label = { Text("Đăng xuất") },
                    selected = true,
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            CoroutineScope(Dispatchers.IO).launch { user?.let { db.delete(it) } }
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Lọc theo trạng thái") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    statusList.forEach { status ->
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
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredHoaDons) { hoadon ->
                    OrderItemA(hoadon, navController, viewModel)
                }
            }
        }
    }
}

@Composable
private fun OrderItemA(
    hoaDon: HoaDon,
    navController: NavController,
    viewModel: SanPhamViewModel = viewModel()
) {
    val formattedPrice = NumberFormat.getInstance(Locale("vi", "VN")).format(hoaDon.TongTien)
    var showPopup by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val capnhaphoadonthanhcong by viewModel.capnhaphoadonthanhcong.collectAsState()
    val capnhaphoadonthongbao by viewModel.capnhaphoadonthongbao.collectAsState()
    if (showPopup) {
        oderPopup(hoaDon = hoaDon, onDismiss = { showPopup = false },viewModel)
    }

    if (showPopup) {
        oderPopup(hoaDon = hoaDon, onDismiss = { showPopup = false },viewModel)
    }
    LaunchedEffect(capnhaphoadonthongbao){
        capnhaphoadonthanhcong?.let {
            if(it)
            {
                // thành công
                Toast.makeText( context,"$capnhaphoadonthongbao", Toast.LENGTH_SHORT).show()
                //reaload
                navController.navigate("admin")
            }
            else
            {
                //faild
                Toast.makeText( context,"$capnhaphoadonthongbao", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                showPopup = true
                viewModel.fetchChiTietHoaDon(hoaDon.MaHD) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "Mã đơn: ${hoaDon.MaHD}",
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "Tổng tiền: $formattedPrice đ",
                    color = Color.Red
                )
                Text(
                    text = "Trạng thái: ${hoaDon.TrangThai}",
                    fontWeight = FontWeight.Medium,
                    color = Color.Blue
                )

                // Hiển thị nút dựa vào trạng thái đơn hàng
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    // Nút Hủy đơn (chỉ hiển thị khi đơn ở trạng thái "Đã đặt" hoặc "Đặt hàng thành công")
                    if (hoaDon.TrangThai == "Đã đặt" || hoaDon.TrangThai == "Đặt hàng thành công") {
                        Button(
                            onClick = { CoroutineScope(Dispatchers.IO).launch {
                                viewModel.capnhaphoadon(hoaDon.MaHD,"Đã hủy")
                            } },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Hủy đơn")
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Nút Cập nhật trạng thái (Chỉ hiển thị nếu chưa "Đã giao" và chưa "Đã hủy" và đơn chờ duyệt "Đã đặt")
                    if (hoaDon.TrangThai != "Đã giao" && hoaDon.TrangThai != "Đã hủy" && hoaDon.TrangThai != "Đã đặt") {
                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val nextStatus = when (hoaDon.TrangThai) {
                                        "Đặt hàng thành công" -> "Đang giao"
                                        "Đang giao" -> "Đã giao"
                                        else -> hoaDon.TrangThai // Giữ nguyên nếu không nằm trong điều kiện
                                    }
                                    viewModel.capnhaphoadon(hoaDon.MaHD, nextStatus)
                                }}
                        ) {
                            Text("Cập nhật")
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Nút Duyệt đơn (chỉ hiển thị nếu đơn hàng đang ở trạng thái "Đã đặt")
                    if (hoaDon.TrangThai == "Đã đặt") {
                        Button(
                            onClick = { CoroutineScope(Dispatchers.IO).launch {
                                viewModel.capnhaphoadon(hoaDon.MaHD,"Đặt hàng thành công")
                            } },
                        ) {
                            Text("Duyệt đơn")
                        }
                    }
                }

            }
        }
    }

}


@Composable
private fun oderPopup(hoaDon: HoaDon, onDismiss: () -> Unit,viewModel: SanPhamViewModel = viewModel()) {
    val chitiethoaDons by remember { derivedStateOf { viewModel.chitiethoadons } }
    val sanphams by remember { derivedStateOf { viewModel.posts } }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Chi tiết đơn hàng ${hoaDon.MaHD}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                LazyColumn {
                    items(chitiethoaDons) { chiTiet ->
                        val sanpham = sanphams.find { it.MaSp == chiTiet.MaSp }
                        if (sanpham != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = sanpham.HinhSp,
                                    contentDescription = "Hình ảnh sản phẩm",
                                    modifier = Modifier.size(80.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp)
                                ) {
                                    Text(text = sanpham.TenSp, fontWeight = FontWeight.Bold)
                                    Text(text = "Giá: ${chiTiet.DonGia} VND")
                                    Text(text = "Số lượng: ${chiTiet.SLMua}")
                                }
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Đóng")
                }
            }
        }
    }
}

