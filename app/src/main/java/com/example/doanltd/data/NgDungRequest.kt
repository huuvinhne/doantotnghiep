import com.example.doanltd.RoomDatabase.NgDungRoom.NgDungEntity
import com.google.gson.annotations.SerializedName

// Dùng cho đăng ký
data class RegisterRequest(
    @SerializedName("TenNgD") val TenNgD: String,
    @SerializedName("SDT") val sdt: String,
    @SerializedName("TKNgD") val tkNgD: String,
    @SerializedName("MatKhauNgD") val matKhauNgD: String,
    @SerializedName("Email") val Email: String
)

// Dùng cho đăng nhập (chỉ cần tài khoản + mật khẩu)
data class LoginRequest(
    @SerializedName("TKNgD") val tkNgD: String,
    @SerializedName("MatKhauNgD") val matKhauNgD: String
)

// Phản hồi từ server (có thể dùng chung)
data class RegisterResponse(
    val status: Boolean,
    val message: String,
)

// phản hồi từ server login
data class LoginReponse(
    val status: Boolean,
    val user:NgDungEntity
)

// phan hoa them hoa don
data class HoaDonRequest(
    @SerializedName("MaNgD") val MaNgD: String,
    @SerializedName("TongTien") val TongTien: Double,
    @SerializedName("DiaChi") val DiaChi: String)

data class HoaDonReponse(
    val success:Boolean,
    val message:String,
    val MaHD:String
)

data class UpdatePasswordRequest(
    @SerializedName("MaNgD") val MaNgD: String,
    @SerializedName("MatKhauCu") val MatKhauCu: String,
    @SerializedName("MatKhauMoi") val MatKhauMoi: String
)

data class UpdatePasswordResponse(
    val message: String,
    val success: Boolean
)

// phan hoa them chi tiet hoa don
data class HoaDonChiTietRequest(
    @SerializedName("MaHD") val MaHD: String,
    @SerializedName("DonGia") val DonGia: Double,
    @SerializedName("MaSp") val MaSp: String,
    @SerializedName("SLMua") val SLMua: Double
)

data class HoaDonChiTietReponse(
    val success:Boolean,
    val message:String,
)

data class CapNhapDonHangRequest(
    @SerializedName("MaHD") val MaHD: String,
    @SerializedName("TrangThaiMoi") val TrangThai: String,
)

data class CapNhapDonHangReponse(
    val success:Boolean,
    val message:String,
)


data class CapNhapNguoiDungRequest(
    @SerializedName("MaNgD") val MaNgD: String,
    @SerializedName("TenNgD") val TenNgD: String,
    @SerializedName("Email") val Email: String,
    @SerializedName("SDT") val sdt: String
)
data class CapnhapNguoiDungReponse(
    val success: Boolean,
    val message: String
)