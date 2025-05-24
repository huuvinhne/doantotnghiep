package com.example.doanltd.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doanltd.Navigation.Screen

data class Order(
    val orderId: String,
    val status: String,
    val value: Int,
    val dateRecorded: String,
    val lastUpdate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController) {
    val orders = remember {
        mutableStateOf(
            listOf(
                Order("SGN00599", "Hoàn thành", 180000, "27/11/2019 11:52", "01/12/2019 11:06"),
                Order("SGN00597", "Đang giao dịch", 180000, "24/11/2019 19:14", "24/11/2019 19:14"),
                Order("SGN00596", "Đang giao dịch", 180000, "23/11/2019 14:59", "23/11/2019 14:59")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử mua hàng") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(Screen.Review.route.replace("{productId}", "default"))
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(50),
                icon = {
                    Icon(Icons.Default.Star, contentDescription = "Đánh giá")
                },
                text = {
                    Text("Đánh giá")
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                OrderTableHeader()
                Divider(thickness = 1.dp, color = Color.LightGray)
            }
            items(orders.value) { order ->
                OrderRow(order)
                Divider(thickness = 0.5.dp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun OrderTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Mã đơn hàng",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
            modifier = Modifier.weight(1f)
        )
        Text(
            "Trạng thái",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
            modifier = Modifier.weight(1f)
        )
        Text(
            "Thanh toán",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
            modifier = Modifier.weight(1f)
        )
        Text(
            "Ngày ghi nhận",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
            modifier = Modifier.weight(1.5f)
        )
        Text(
            "Cập nhật cuối",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun OrderRow(order: Order) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(order.orderId, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(
            order.status,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (order.status == "Hoàn thành") Color.Green else Color.Blue
            ),
            modifier = Modifier.weight(1f)
        )
        Text("${order.value}₫", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(order.dateRecorded, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.5f))
        Text(order.lastUpdate, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.5f))
    }
}
