package com.altermarkt.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database( //memberitahu Room ini adalah class database utama
    entities = [
        SavedProductEntity::class, // daftar semua tabel yang ada di database ini
        HomeProductEntity::class
    ],
    version = 2, // versi database, nanti kalau ada perubahan struktur tabel harus dinaikkan
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun savedProductDao(): SavedProductDao
    abstract fun homeProductDao(): HomeProductDao

    companion object { // pattern Singleton supaya database hanya dibuat satu kali selama aplikasi berjalan, tidak boros memory
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "altermarket_db"
                )
                    .fallbackToDestructiveMigration()   //ditambahkan supaya tidak crash saat versi naik.
                    .build().also { INSTANCE = it }
            }
        }
    }
}