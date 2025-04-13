package com.example.assignment_1booktracker.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [dbBook::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private class BookDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d("DatabaseInit", "Start creating the structure of the database table.")

                INSTANCE?.let { database ->
                    scope.launch {
                        try {
                            Log.d("DatabaseInit", "Start inserting the initial data.")
                            val bookDao = database.bookDao()

                            // 使用预设书籍数据
                            PresetBooks.list.forEach { preset ->
                                Log.d("DatabaseInit", "Insert preset books: ${preset.name}")
                                bookDao.insertBook(
                                    dbBook(
                                        name = preset.name,
                                        author = preset.author,
                                        image = preset.image,
                                        category = preset.category,
                                        totalPages = preset.totalPages,
                                        readPages = 0,
                                        rating = 0,
                                        criticalPoints = preset.criticalPoints,
                                        cpPage = 0,
                                        review = preset.review
                                    )
                                )
                            }
                            Log.d("DatabaseInit", "Successfully insert${PresetBooks.list.size}preset books")
                        } catch (e: Exception) {
                            Log.e("DatabaseInit", "Failed inserting preset books", e)
                        }
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Log.d("DatabaseInit", "Database is open")
            }
        }

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "book_database"
                )
                    .addCallback(BookDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance.openHelper.writableDatabase
                instance
            }
        }
    }
}
