package com.royser.stetho_demo.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by Royser on 21/5/2020 AD.
 */
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}