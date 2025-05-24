package com.example.doanltd


import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.doanltd.RoomDatabase.CartRoom.CartItemEntity
import com.example.doanltd.RoomDatabase.NgDungRoom.NgDungEntity


@Database(entities = [CartItemEntity::class, NgDungEntity::class], version = 3, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun ngDungDao(): NgDungDao
    abstract fun cartDao(): CartDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @SuppressLint("SuspiciousIndentation")
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,

                    "cart_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    "app_database"
             INSTANCE = instance
                instance
            }
        }
    }
}


