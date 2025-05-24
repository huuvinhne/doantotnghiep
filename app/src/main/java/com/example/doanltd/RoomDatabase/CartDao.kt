package com.example.doanltd

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import androidx.room.Update

import com.example.doanltd.RoomDatabase.CartRoom.CartItemEntity


@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE MaSp=:MaSp")
    suspend fun getCartItemById(MaSp: String): CartItemEntity?

    @Query("SELECT * FROM cart_items")
    suspend fun getAllCartItems(): List<CartItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItemEntity)

    @Update
    suspend fun updateCartItem(cartItem: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItemEntity)

    @Query("DELETE FROM cart_items")
    suspend fun deleteAllCartItems()
}

