package com.example.doanltd.View

import CapNhapDonHangRequest
import HoaDonChiTietRequest
import HoaDonRequest
import LoaiSP
import RetrofitInstance
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltd.data.ChiTietHoaDon
import com.example.doanltd.data.HoaDon
import com.example.doanltd.data.SanPham
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SanPhamViewModel :ViewModel(){

    // danh sach post
    var posts by mutableStateOf<List<SanPham>>(emptyList())
        private set

    var loaisanphams by mutableStateOf<List<LoaiSP>>(emptyList())
        private set

    var productDetail by mutableStateOf<SanPham?>(null)
        private set

    var hoadons by mutableStateOf<List<HoaDon>>(emptyList())
        private set

    var chitiethoadons by mutableStateOf<List<ChiTietHoaDon>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchPosts()
        fetchLoaiSanPham()
        fetchHoaDon()
    }

    // ham gọi ds post
    private fun fetchPosts() {
        viewModelScope.launch {
            try {
                isLoading = true
                // Lấy dữ liệu từ API
                val response = RetrofitInstance.api.getSanPham()
                posts = response
                isLoading = false
            } catch (e: Exception) {
                // Xử lý lỗi và cập nhật thông báo lỗi
                isLoading = false
                errorMessage = e.message
                e.printStackTrace()
            }
        }
    }

    private fun fetchLoaiSanPham() {
        viewModelScope.launch {
            try {
                isLoading = true
                // Lấy dữ liệu từ API
                val response = RetrofitInstance.api.getLoaiSP()
                loaisanphams = response
                isLoading = false
            } catch (e: Exception) {
                // Xử lý lỗi và cập nhật thông báo lỗi
                isLoading = false
                errorMessage = e.message
                e.printStackTrace()
            }
        }
    }

    fun fetchProductDetail(productId: String) {
        viewModelScope.launch {
            try {
                productDetail = RetrofitInstance.api.getChiTietSanPham(productId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchHoaDon() {
        viewModelScope.launch {
            try {
                isLoading = true
                // Lấy dữ liệu từ API
                val response = RetrofitInstance.api.getHoaDon()
                hoadons = response
                isLoading = false
            } catch (e: Exception) {
                // Xử lý lỗi và cập nhật thông báo lỗi
                isLoading = false
                errorMessage = e.message
                e.printStackTrace()
            }
        }
    }


    fun fetchChiTietHoaDon(maHD:String) {
        viewModelScope.launch {
            try {
                isLoading = true
                // Lấy dữ liệu từ API
                val response = RetrofitInstance.api.getChiTietHoaDon(maHD)
                chitiethoadons = response
                isLoading = false
            } catch (e: Exception) {
                // Xử lý lỗi và cập nhật thông báo lỗi
                isLoading = false
                errorMessage = e.message
                e.printStackTrace()
            }
        }
    }

    // success
    private val _hoadonthanhcong = MutableStateFlow<Boolean?>(null)
    val hoadonthanhcong: StateFlow<Boolean?> = _hoadonthanhcong

    // message
    private val _hoadonthongbao = MutableStateFlow<String?>(null)
    val hoadonthongbao: StateFlow<String?> = _hoadonthongbao

    // mahd
    private val _MaHd = MutableStateFlow<String?>(null)
    val MaHd: StateFlow<String?> = _MaHd


    suspend fun themhoadon(MaNgD:String,TongTien:Double,DiaChi:String) {
        try {
            val response = RetrofitInstance.api.themhoadon(HoaDonRequest(MaNgD,TongTien,DiaChi))
            _hoadonthanhcong.value = response.success
            _hoadonthongbao.value = response.message
            _MaHd.value = response.MaHD
        } catch (e: Exception) {
            Log.e("API", "Lỗi hoa don: ${e.message}")
        }
    }

    // success
    private val _chitiethoadonthanhcong = MutableStateFlow<Boolean?>(null)
    val chitiethoadonthanhcong: StateFlow<Boolean?> = _chitiethoadonthanhcong

    // message
    private val _chitiethoadonthongbao = MutableStateFlow<String?>(null)
    val chitiethoadonthongbao: StateFlow<String?> = _chitiethoadonthongbao

    suspend fun themchitiethoadon(MaHD:String,MaSp:String,DonGia:Double,SLMua:Double) {
        try {
            val response = RetrofitInstance.api.themchitiethoadon(HoaDonChiTietRequest(MaHD,DonGia,MaSp,SLMua))
            _chitiethoadonthanhcong.value = response.success
            _chitiethoadonthongbao.value = response.message

        } catch (e: Exception) {
            Log.e("API", "Lỗi đăng ký: ${e.message}")
        }
    }


    // success
    private val _capnhaphoadonthanhcong = MutableStateFlow<Boolean?>(null)
    val capnhaphoadonthanhcong: StateFlow<Boolean?> = _capnhaphoadonthanhcong

    // message
    private val _capnhaphoadonthongbao = MutableStateFlow<String?>(null)
    val capnhaphoadonthongbao: StateFlow<String?> = _capnhaphoadonthongbao

    suspend fun capnhaphoadon(MaHD:String,TrangThai:String) {
        try {
            val response = RetrofitInstance.api.capnhapdonhang(CapNhapDonHangRequest(MaHD,TrangThai))
            _capnhaphoadonthanhcong.value = response.success
            _capnhaphoadonthongbao.value = response.message

        } catch (e: Exception) {
            Log.e("API", "Lỗi đăng ký: ${e.message}")
        }
    }
}