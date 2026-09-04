package com.face.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.face.bean.SearchHistoryBean
import com.zzkj.structure.base.BaseApp

@Database(
    entities = [
        SearchHistoryBean::class
    ],
    version = AppDatabase.VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        const val VERSION = 1

        val INSTANT: AppDatabase = Room
            .databaseBuilder(BaseApp.INSTANCE, AppDatabase::class.java, "db_swapai")
            .allowMainThreadQueries()
            .build()

    }
}