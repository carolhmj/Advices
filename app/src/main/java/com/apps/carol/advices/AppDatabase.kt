package com.apps.carol.advices

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Advice::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun adviceDao() : AdviceDao
    companion object {
        private var INSTANCE : AppDatabase? = null

        fun getInstance(context : Context) : AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java,
                                "app.db").build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}