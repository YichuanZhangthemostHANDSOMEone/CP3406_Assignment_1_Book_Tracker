package com.example.assignment_1booktracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [dbBook::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
