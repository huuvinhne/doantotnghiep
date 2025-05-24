package com.example.doanltd.RoomDatabase.CartRoom

import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.*

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val MaSp:String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val SoLuongSP:Int,
    val imageUrl: String
)




