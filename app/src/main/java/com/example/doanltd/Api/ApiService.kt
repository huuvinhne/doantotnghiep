import com.example.doanltd.data.ChiTietHoaDon
import com.example.doanltd.data.HoaDon
import com.example.doanltd.data.SanPham
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService{
    // gọi dường dẫn ?
    @GET("sanpham/dssanpham.php")
    suspend fun getSanPham():List<SanPham>

    @GET("loaisp/dsloaisp.php")
    suspend fun getLoaiSP():List<LoaiSP>

    @POST("ngdung/capnhapmatkhau.php")
    suspend fun capnhatmatkhau(@Body request: UpdatePasswordRequest): UpdatePasswordResponse

    @Headers("Content-Type: application/json")
    @POST("ngdung/dangky.php")
    suspend fun dangky(@Body request: RegisterRequest):RegisterResponse

    @POST("ngdung/dangnhap.php")
    suspend fun dangnhap(@Body request: LoginRequest):LoginReponse

    @GET("sanpham/laySanPhamTheoMaSP.php")
    suspend fun getChiTietSanPham(@Query("id") productId: String): SanPham

    @GET("hoadon/dshoadon.php")
    suspend fun  getHoaDon():List<HoaDon>

    @Headers("Content-Type: application/json")
    @POST("hoadon/themhoadon.php")
    suspend fun themhoadon(@Body request: HoaDonRequest):HoaDonReponse

    @Headers("Content-Type: application/json")
    @POST("chitiethoadon/themchitiet.php")
    suspend fun themchitiethoadon(@Body request: HoaDonChiTietRequest):HoaDonChiTietReponse

    @GET("chitiethoadon/dschitiethoadon.php")
    suspend fun getChiTietHoaDon(@Query("MaHD") maHD: String): List<ChiTietHoaDon>

    @Headers("Content-Type: application/json")
    @POST("hoadon/capnhaptrangthai.php")
    suspend fun capnhapdonhang(@Body request: CapNhapDonHangRequest):CapNhapDonHangReponse

    @POST("ngdung/capnhapnguoidung.php")
    suspend fun capnhapnguoidung(@Body request: CapNhapNguoiDungRequest):CapnhapNguoiDungReponse
}

object RetrofitInstance{
    val api:ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.5/restful_api_php/api/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(ApiService::class.java)
    }
}