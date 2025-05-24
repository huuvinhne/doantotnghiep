package com.example.doanltd.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doanltd.Navigation.Screen
import com.example.doanltd.View.SanPhamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    categoryId: String?,
    viewModel: SanPhamViewModel = viewModel()
) {
    val sanPhams by remember { derivedStateOf { viewModel.posts } }
    val loaiSps by remember { derivedStateOf { viewModel.loaisanphams } }

    // Filter products by category
    val categoryProducts = sanPhams.filter { it.MaLoai == categoryId }
    val categoryName = loaiSps.find { it.MaLoai == categoryId }?.TenLoai ?: "Danh mục"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryName) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            items(categoryProducts) { sanPham ->
                //SanPhamItem(sanPham = sanPham, navController = navController)
            }
        }
    }
}

