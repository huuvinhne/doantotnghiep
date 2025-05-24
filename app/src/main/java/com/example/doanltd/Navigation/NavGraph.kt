package com.example.doanltd.Navigation


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doanltd.Screen.*
import com.example.doanltd.Screen.CategoryScreen
import com.example.doanltd.Screen.ChatScreen
import com.example.doanltd.Screen.HomeScreen
import com.example.doanltd.Screen.LoginScreen
import com.example.doanltd.Screen.MessageScreen
import com.example.doanltd.Screen.OrderHistoryScreen
import com.example.doanltd.Screen.ProductDetailScreen
import com.example.doanltd.Screen.ProfileScreen
import com.example.doanltd.Screen.RegisterScreen
import com.example.doanltd.Screen.ReviewScreen
import com.example.doanltd.Screen.SettingScreen
import com.google.gson.Gson
import com.example.doanltd.AppDatabase
import com.example.doanltd.RoomDatabase.NgDungRoom.NgDungEntity
import com.example.doanltd.Screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Message : Screen("message")
    object Chat : Screen("chat")
    object Setting : Screen("setting")
    object Cart : Screen("cart")
    object OrderDetails : Screen("order_details")
    object ProductDetail : Screen("productdetail")
    object OrderHistory : Screen("order_history")
    object XemDonHang:Screen("xem_don_hang")
    object Review : Screen("review/{productId}")
    object CategoryScreen : Screen("category/{categoryId}") {
        fun createRoute(categoryId: String): String = "category/$categoryId"
    }
    object  Admin:Screen("admin")
}

@Composable
fun AuthNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current // Lấy context tại đây để truyền vào nếu cần

    // Lấy SharedPreferences để kiểm tra trạng thái đăng nhập
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

    // Lấy danh sách người dùng từ cơ sở dữ liệu Room
    val db = AppDatabase.getDatabase(context) // Sử dụng AppDatabase của Room

    // State lưu trữ danh sách người dùng
    var userList by remember { mutableStateOf<List<NgDungEntity>>(emptyList()) }

    // Sử dụng LaunchedEffect để thực hiện điều hướng khi trạng thái đăng nhập thay đổi
    LaunchedEffect(Unit) {
        // Thực hiện truy vấn cơ sở dữ liệu trong background thread
        userList = withContext(Dispatchers.IO) {
            db.ngDungDao().getAll()
        }

        // Kiểm tra nếu người dùng đã đăng nhập và có người dùng hợp lệ trong DB
        if (userList.isNotEmpty()) {
            // Nếu người dùng đã đăng nhập và có người dùng hợp lệ trong DB, điều hướng đến HomeScreen
            val user = userList[0]
            if(user.ChucVu.equals("NguoiDung"))
            {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }

            if(user.ChucVu.equals("QuanLy"))
            {
                navController.navigate(Screen.Admin.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }

        } else {
            // Nếu không có người dùng hợp lệ, điều hướng đến LoginScreen
            navController.navigate(Screen.Login.route)
        }
    }

    // Thiết lập NavHost và khai báo các màn hình
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.Message.route) {
            MessageScreen(navController = navController)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController = navController)
        }
        composable(Screen.Setting.route) {
            SettingScreen(navController = navController)
        }
        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
        }
        composable(Screen.OrderDetails.route) {
            OrderDetailsScreen(navController = navController)
        }

        composable(Screen.Admin.route) {
            AdminScreen(navController = navController)
        }


        composable(
            route = "${Screen.ProductDetail.route}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            ProductDetailScreen(navController = navController, productId = id, context = context)
        }
        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(navController = navController)
        }
        composable(Screen.XemDonHang.route){
            XemDonHangScreen(navController)
        }
        composable(
            route = Screen.Review.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ReviewScreen(navController = navController, productId = productId)
        }
        composable(
            route = Screen.CategoryScreen.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            CategoryScreen(
                navController = navController,
                categoryId = categoryId
            )
        }
    }
}

