package com.example.doanltd.data

data class SanPham(
    val id:Int,
    val MaSp :String,
    val MaLoai:String,
    val TenSp:String,
    val HinhSp:String,
    val MoTa:String,
    val DonGia: Double,
    val TrangThai:Int,
    val SoLuong:Int
)