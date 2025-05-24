package com.example.doanltd.Screen

import LoaiSP
import androidx.compose.foundation.BorderStroke
import com.example.doanltd.data.SanPham
import com.example.doanltd.View.SanPhamViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.doanltd.Navigation.Screen
import com.example.doanltd.Navigation.Screen.CategoryScreen
import com.example.doanltd.R
import java.text.NumberFormat
import java.util.*

//// Shared address state object
//object AddressManager {
//    var currentAddress by mutableStateOf("192, đường Phạm Đức Sơn, Phường 2, Quận 8, Hồ Chí Minh")
//        private set
//
//    fun updateAddress(newAddress: String) {
//        currentAddress = newAddress
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: SanPhamViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var resetHome by remember { mutableStateOf(false) }
    var diaChiTinh by remember { mutableStateOf("TP.HCM") }
    var showDialog by remember { mutableStateOf(false) }

    val sanPhams by remember { derivedStateOf { viewModel.posts } }
    val loaiSps by remember { derivedStateOf { viewModel.loaisanphams } }

    // Complete Ho Chi Minh City address data
    val hcmAddressData = mapOf(
        "Quận 1" to listOf(
            "Phường Bến Nghé", "Phường Bến Thành", "Phường Cầu Kho", "Phường Cầu Ông Lãnh",
            "Phường Cô Giang", "Phường Đa Kao", "Phường Nguyễn Cư Trinh", "Phường Nguyễn Thái Bình",
            "Phường Phạm Ngũ Lão", "Phường Tân Định"
        ),
        "Quận 3" to listOf(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6",
            "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12",
            "Phường 13", "Phường 14"
        ),
        "Quận 4" to listOf(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 6", "Phường 8",
            "Phường 9", "Phường 10", "Phường 13", "Phường 14", "Phường 15", "Phường 16", "Phường 18"
        ),
        "Quận 5" to listOf(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6",
            "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12",
            "Phường 13", "Phường 14", "Phường 15"
        ),
        "Quận 6" to listOf(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6",
            "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12",
            "Phường 13", "Phường 14"
        ),
        "Quận 7" to listOf(
            "Phường Bình Thuận", "Phường Phú Mỹ", "Phường Phú Thuận", "Phường Tân Hưng",
            "Phường Tân Kiểng", "Phường Tân Phong", "Phường Tân Phú", "Phường Tân Quy"
        ),
        "Quận 8" to listOf(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6",
            "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12",
            "Phường 13", "Phường 14", "Phường 15", "Phường 16"
        ),
        "Quận 10" to listOf(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6",
            "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12",
            "Phường 13", "Phường 14", "Phường 15"
        ),
        "Quận 11" to listOf(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6",
            "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12",
            "Phường 13", "Phường 14", "Phường 15", "Phường 16"
        ),
        "Quận 12" to listOf(
            "Phường An Phú Đông", "Phường Đông Hưng Thuận", "Phường Hiệp Thành",
            "Phường Tân Chánh Hiệp", "Phường Tân Hưng Thuận", "Phường Tân Thới Hiệp",
            "Phường Tân Thới Nhất", "Phường Thạnh Lộc", "Phường Thạnh Xuân",
            "Phường Thới An", "Phường Trung Mỹ Tây"
        ),
        "Quận Bình Thạnh" to listOf(
            "Phường 1", "Phường 2", "Phường 3", "Phường 5", "Phường 6", "Phường 7",
            "Phường 11", "Phường 12", "Phường 13", "Phường 14", "Phường 15", "Phường 17",
            "Phường 19", "Phường 21", "Phường 22", "Phường 24", "Phường 25", "Phường 26",
            "Phường 27", "Phường 28"
        ),
        "Quận Gò Vấp" to listOf(
            "Phường 1", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7",
            "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13",
            "Phường 14", "Phường 15", "Phường 16", "Phường 17"
        ),
        "Quận Phú Nhuận" to listOf(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 7",
            "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 13", "Phường 15", "Phường 17"
        ),
        "Quận Tân Bình" to listOf(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6",
            "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12",
            "Phường 13", "Phường 14", "Phường 15"
        ),
        "Quận Tân Phú" to listOf(
            "Phường Hiệp Tân", "Phường Hòa Thạnh", "Phường Phú Thạnh", "Phường Phú Trung",
            "Phường Sơn Kỳ", "Phường Tân Quy", "Phường Tân Sơn Nhì", "Phường Tân Thành",
            "Phường Tân Thới Hòa", "Phường Tây Thạnh"
        ),
        "Quận Thủ Đức" to listOf(
            "Phường Bình Chiểu", "Phường Bình Thọ", "Phường Hiệp Bình Chánh",
            "Phường Hiệp Bình Phước", "Phường Linh Chiểu", "Phường Linh Đông",
            "Phường Linh Tây", "Phường Linh Trung", "Phường Linh Xuân", "Phường Tam Bình",
            "Phường Tam Phú", "Phường Trường Thọ"
        ),
        "Huyện Bình Chánh" to listOf(
            "Xã Bình Chánh", "Xã Bình Hưng", "Xã Đa Phước", "Xã Hưng Long",
            "Xã Lê Minh Xuân", "Xã Phạm Văn Hai", "Xã Phong Phú", "Xã Quy Đức",
            "Xã Tân Kiên", "Xã Tân Nhựt", "Xã Tân Quý Tây", "Xã Vĩnh Lộc A", "Xã Vĩnh Lộc B"
        ),
        "Huyện Cần Giờ" to listOf(
            "Xã An Thới Đông", "Xã Bình Khánh", "Xã Cần Thạnh", "Xã Long Hòa",
            "Xã Lý Nhơn", "Xã Tam Thôn Hiệp"
        ),
        "Huyện Củ Chi" to listOf(
            "Xã An Nhơn Tây", "Xã An Phú", "Xã Bình Mỹ", "Xã Hòa Phú", "Xã Nhuận Đức",
            "Xã Phạm Văn Cội", "Xã Phú Hòa Đông", "Xã Phú Mỹ Hưng", "Xã Phước Hiệp",
            "Xã Phước Thạnh", "Xã Phước Vĩnh An", "Xã Tân An Hội", "Xã Tân Phú Trung",
            "Xã Tân Thạnh Đông", "Xã Tân Thạnh Tây", "Xã Tân Thông Hội", "Xã Thái Mỹ",
            "Xã Trung An", "Xã Trung Lập Hạ", "Xã Trung Lập Thượng"
        ),
        "Huyện Hóc Môn" to listOf(
            "Xã Bà Điểm", "Xã Đông Thạnh", "Xã Nhị Bình", "Xã Tân Hiệp", "Xã Tân Thới Nhì",
            "Xã Tân Xuân", "Xã Thới Tam Thôn", "Xã Trung Chánh", "Xã Xuân Thới Đông",
            "Xã Xuân Thới Sơn", "Xã Xuân Thới Thượng"
        ),
        "Huyện Nhà Bè" to listOf(
            "Xã Hiệp Phước", "Xã Long Thới", "Xã Nhơn Đức", "Xã Phú Xuân",
            "Xã Phước Kiển", "Xã Phước Lộc"
        )
    )

    var selectedQuan by remember { mutableStateOf("Quận 8") }
    var selectedPhuong by remember { mutableStateOf("Phường 2") }
    var houseNumber by remember { mutableStateOf("192") }
    var streetName by remember { mutableStateOf("đường Phạm Đức Sơn") }
    var previewAddress by remember { mutableStateOf("") }

    // Update address when selections change
    LaunchedEffect(selectedQuan, selectedPhuong, houseNumber, streetName) {
        if (selectedQuan.isNotEmpty() && selectedPhuong.isNotEmpty()) {
            val fullAddress = if (houseNumber.isNotEmpty() && streetName.isNotEmpty()) {
                "$houseNumber, $streetName, $selectedPhuong, $selectedQuan, Hồ Chí Minh"
            } else {
                "$selectedPhuong, $selectedQuan, Hồ Chí Minh"
            }
            previewAddress = fullAddress
        }
    }

    if (resetHome) {
        searchQuery = ""
        isSearching = false
        resetHome = false
    }

    fun normalize(text: String): String {
        val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        val temp = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
        return regex.replace(temp, "").lowercase()
    }

    val filteredSanPhams = sanPhams.filter {
        normalize(it.TenSp).contains(normalize(searchQuery)) ||
                normalize(it.MoTa).contains(normalize(searchQuery))
    }

    // Address Selection Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    AddressManager.updateAddress(previewAddress)
                    showDialog = false
                }) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Huỷ")
                }
            },
            title = { Text("Chọn địa chỉ giao hàng tại TP.HCM") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // District selection
                    Text("Quận/Huyện", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    EnhancedDropdownMenuBox(
                        options = hcmAddressData.keys.toList().sorted(),
                        selected = selectedQuan,
                        onSelectedChange = {
                            selectedQuan = it
                            selectedPhuong = hcmAddressData[it]?.firstOrNull() ?: ""
                        },
                        placeholder = "Chọn quận/huyện"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ward selection
                    Text("Phường/Xã", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    EnhancedDropdownMenuBox(
                        options = hcmAddressData[selectedQuan]?.sorted() ?: emptyList(),
                        selected = selectedPhuong,
                        onSelectedChange = { selectedPhuong = it },
                        placeholder = "Chọn phường/xã"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // House number and street input
                    Text("Số nhà và tên đường", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = houseNumber,
                            onValueChange = { houseNumber = it },
                            label = { Text("Số nhà") },
                            placeholder = { Text("192") },
                            modifier = Modifier.weight(0.3f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF009966),
                                focusedLabelColor = Color(0xFF009966)
                            )
                        )

                        OutlinedTextField(
                            value = streetName,
                            onValueChange = { streetName = it },
                            label = { Text("Tên đường") },
                            placeholder = { Text("đường Phạm Đức Sơn") },
                            modifier = Modifier.weight(0.7f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF009966),
                                focusedLabelColor = Color(0xFF009966)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preview address display
                    if (previewAddress.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE8F5E8)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    "Địa chỉ hoàn chỉnh:",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Color(0xFF2E7D32)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    previewAddress,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color.Gray
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
                    label = { Text("Trang chủ", fontSize = 10.sp) },
                    selected = true,
                    onClick = { resetHome = true },
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
                    onClick = { navController.navigate(Screen.OrderHistory.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF009966),
                        selectedTextColor = Color(0xFF009966),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
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
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Thông tin") },
                    label = { Text("Thông tin", fontSize = 10.sp) },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.Setting.route)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF009966),
                        selectedTextColor = Color(0xFF009966),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB3E5FC))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header with promotional text and delivery icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Đổi một cái, bật app ngay!",
                    color = Color(0xFF009966),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Icon(
                    Icons.Default.LocalShipping,
                    contentDescription = "Delivery",
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Location section - clickable to show address popup
            Text(
                "Giao đến:",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier
                    .clickable { showDialog = true }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = AddressManager.currentAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar - clickable to search for products
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isSearching = true
                    },
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                if (isSearching) {
                    // Active search mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Tìm món ăn mà bạn đang cần ...") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                        IconButton(onClick = {
                            isSearching = false
                            searchQuery = ""
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close search")
                        }
                    }
                } else {
                    // Inactive search bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Tìm món ăn mà bạn đang cần ...",
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "TP.HCM",
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Categories section
            Text(
                "Danh mục",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(loaiSps.take(4)) { loaiSp ->
                    CategoryItemNew(loaiSP = loaiSp, navController = navController)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Products section
            Text(
                if (searchQuery.isNotEmpty()) "Kết quả tìm kiếm" else "Dành cho bạn",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(if (searchQuery.isNotEmpty()) filteredSanPhams else sanPhams) { sanPham ->
                    SanPhamItemNew(sanPham = sanPham, navController = navController)
                }
            }
        }
    }
}

// Rest of the composable functions remain the same...
@Composable
fun EnhancedDropdownMenuBox(
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected.ifEmpty { placeholder },
                    color = if (selected.isEmpty()) Color.Gray else Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            color = if (option == selected) Color(0xFF009966) else Color.Black
                        )
                    },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = if (option == selected) Color(0xFF009966) else Color.Black
                    )
                )
            }
        }
    }
}

@Composable
fun CategoryItemNew(loaiSP: LoaiSP, navController: NavController) {
    val maLoai = loaiSP.MaLoai ?: return

    Card(
        modifier = Modifier
            .size(80.dp)
            .clickable {
                navController.navigate(CategoryScreen.createRoute(maLoai))
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = loaiSP.HinhLoai,
                contentDescription = loaiSP.TenLoai,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun SanPhamItemNew(sanPham: SanPham, navController: NavController) {
    val formattedPrice = NumberFormat.getInstance(Locale("vi", "VN")).format(sanPham.DonGia)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("${Screen.ProductDetail.route}/${sanPham.MaSp}") },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = sanPham.HinhSp,
                contentDescription = sanPham.TenSp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = sanPham.TenSp,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rating stars
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < 4) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "4.5",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Đã bán 32 phần",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formattedPrice}đ",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B35)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "60.000đ",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    style = androidx.compose.ui.text.TextStyle(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "HỒ CHÍ MINH",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}
