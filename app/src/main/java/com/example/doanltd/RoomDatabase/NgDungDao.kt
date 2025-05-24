package com.example.doanltd

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.doanltd.RoomDatabase.NgDungRoom.NgDungEntity

@Dao
interface NgDungDao {
    @Query("SELECT * FROM ngDung_item")
    fun getAll(): List<NgDungEntity>

    @Query("SELECT * FROM ngDung_item WHERE MaNgD IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<NgDungEntity>

    @Insert
    fun insertUser(user: NgDungEntity)

    // Chèn từng phần tử (Không sử dụng @Insert mà viết một phương thức riêng)
    @Transaction
    suspend fun insertUserByFields(
        MaNgD: String,
        TenNgD: String,
        Email: String,
        SDT: String,
        TKNgD: String,
        TrangThai: Int,
        ChucVu: String
    ) {
        val user = NgDungEntity(
            MaNgD = MaNgD,
            TenNgD = TenNgD,
            Email = Email,
            SDT = SDT,
            TKNgD = TKNgD,
            TrangThai = TrangThai,
            ChucVu = ChucVu
        )
        insertUser(user)
    }


    @Delete
    suspend fun delete(user: NgDungEntity)
    @Update
    suspend fun  update(user: NgDungEntity)
}