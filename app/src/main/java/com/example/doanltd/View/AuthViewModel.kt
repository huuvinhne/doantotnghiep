package com.example.doanltd.View

import ApiService
import CapNhapNguoiDungRequest
import LoginRequest
import NgDung
import RegisterRequest
import UpdatePasswordRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltd.RoomDatabase.NgDungRoom.NgDungEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel(){
    val apiService: ApiService = RetrofitInstance.api

    private val _dangKyThanhCong = MutableStateFlow<Boolean?>(null)
    val dangKyThanhCong: StateFlow<Boolean?> get() = _dangKyThanhCong

    private val _dangKyError = MutableStateFlow<String?>(null)
    val dangKyError: StateFlow<String?> get() = _dangKyError

    fun dangKyNguoiDung(
        tenNgD: String,
        sdt: String,
        tkNgD: String,
        matKhauNgD: String,
        Email: String
    ) {
        viewModelScope.launch {
            try {
                // Giả lập kiểm tra trùng lặp tài khoản/email (nếu cần)
                if (tkNgD == "existing_user" || Email == "existing_email@example.com") {
                    _dangKyError.value = if (tkNgD == "existing_user") {
                        "Tài khoản đã tồn tại!"
                    } else {
                        "Email đã tồn tại!"
                    }
                    _dangKyThanhCong.value = false
                    return@launch
                }

                // Gọi API đăng ký
                val response = apiService.dangky(RegisterRequest(tenNgD, sdt, tkNgD, matKhauNgD, Email))
                if (response.status) {
                    _dangKyThanhCong.value = true
                    _dangKyError.value = null
                } else {
                    _dangKyThanhCong.value = false
                    _dangKyError.value = response.message ?: "Đăng ký thất bại!"
                }
            } catch (e: Exception) {
                Log.e("API", "Lỗi đăng ký: ${e.message}")
                _dangKyThanhCong.value = false
                _dangKyError.value = "Lỗi kết nối, vui lòng thử lại sau!"
            }
        }
    }



    private  val _dangNhapThanhCong=MutableStateFlow<Boolean?>(null)
    val dangNhapThanhCong:StateFlow<Boolean?> = _dangNhapThanhCong

    private  val _duLieuNguoiDung=MutableStateFlow<NgDungEntity?>(null)
    val duLieuNguoiDung:StateFlow<NgDungEntity?> = _duLieuNguoiDung

    suspend fun dangNhapNguoiDung( tkNgD: String, matKhauNgD: String) {
        try {
            val response = apiService.dangnhap(LoginRequest(tkNgD, matKhauNgD))
            Log.d("API", "Đăng nhap thành công: ${response}")
            _dangNhapThanhCong.value=response.status
            _duLieuNguoiDung.value= response.user
        } catch (e: Exception) {
            Log.e("API", "Lỗi đăng nhap: ${e.message}")
        }
    }


    private val _capNhatMatKhauThanhCong = MutableStateFlow<Boolean?>(null)
    val capNhatMatKhauThanhCong: StateFlow<Boolean?> = _capNhatMatKhauThanhCong

    private val _thongbaocapnhatmatkhau = MutableStateFlow<String?>(null)
    val thongbaocapnhatmatkhau: StateFlow<String?> = _thongbaocapnhatmatkhau

    suspend fun capNhatMatKhau(MaNgD: String, MatKhauCu: String, MatKhauMoi: String) {
        try {
            val response = apiService.capnhatmatkhau(UpdatePasswordRequest(MaNgD, MatKhauCu, MatKhauMoi))
            Log.d("API", "Cập nhật mật khẩu: ${response}")
            _capNhatMatKhauThanhCong.value=response.success
            _thongbaocapnhatmatkhau.value= response.message
        } catch (e: Exception) {
            Log.e("API", "Lỗi cập nhật mật khẩu: ${e.message}")
            _capNhatMatKhauThanhCong.value = false // Lỗi ngoại lệ
        }
    }

    fun resetPasswordChangeState() {
        _capNhatMatKhauThanhCong.value = null
        _thongbaocapnhatmatkhau.value = null
    }


//update ngdung
    private val _capnhapngdthanhcong = MutableStateFlow<Boolean?>(null)
    val capnhapngdthanhcong: StateFlow<Boolean?> = _capnhapngdthanhcong

    private val _thongbaocapnhapngdthanhcong = MutableStateFlow<String?>(null)
    val thongbaocapnhapngdthanhcong: StateFlow<String?> = _thongbaocapnhapngdthanhcong


    suspend fun CapNhapNgDung( MaNgD: String,tkNgD: String,Email: String,sdt: String) {
        try {
            val response = apiService.capnhapnguoidung(CapNhapNguoiDungRequest(MaNgD,tkNgD,Email,sdt))
            Log.d("API", "Cập nhập thành công: ${response}")
            _capnhapngdthanhcong.value=response.success
            _thongbaocapnhapngdthanhcong.value= response.message
        } catch (e: Exception) {
            Log.e("API", "Lỗi cập nhập: ${e.message}")
        }
    }
}

