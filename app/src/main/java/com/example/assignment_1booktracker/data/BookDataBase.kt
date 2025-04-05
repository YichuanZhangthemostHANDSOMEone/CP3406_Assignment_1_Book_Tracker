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

@Database(entities = [dbBook::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 将回调类移至伴生对象内部
        private class BookDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d("DatabaseInit", "开始创建数据库表结构")

                INSTANCE?.let { database ->
                    scope.launch {
                        try {
                            Log.d("DatabaseInit", "开始插入初始数据")
                            val bookDao = database.bookDao()

                            listOf(
                                Triple("A sea of unspoken things", "Mystery", 328),
                                Triple("Homeseeking", "Historical fiction", 234),
                                Triple("The Three Body Problem", "Dystopian", 305)
                            ).forEach { (name, category, pages) ->
                                Log.d("DatabaseInit", "插入默认书籍: $name")
                                bookDao.insertBook(
                                    dbBook(
                                        name = "Sea fo unspoken things",
                                        author = "Adrienne Young",
                                        image = "app/src/main/res/drawable-nodpi/a_sea_of_unspoken_things_mystery.jpg",
                                        category = "Mystery",
                                        totalPages = 328,
                                        readPages = 0,
                                        rating = 0,
                                        criticalPoints = "'Fourth Division'.",
                                        cpPage = 0,
                                        review = "This book is a great read for those who want to learn more about the Chinese military."
                                    )
                                )
                                bookDao.insertBook(
                                    dbBook(
                                        name = "Water in the world",
                                        author = "Karen Ranney",
                                        image = "app/src/main/res/drawable-nodpi/all_the_water_in_the_world_science_fiction.jpg",
                                        category = "Science fiction",
                                        totalPages = 456,
                                        readPages = 0,
                                        rating = 0,
                                        criticalPoints = "'Fourth Division'.",
                                        cpPage = 0,
                                        review = "This book is a great read for those who want to learn more about the Chinese military."
                                    )
                                )
                                bookDao.insertBook(
                                    dbBook(
                                        name = "Homeseeking",
                                        author = "Karen Ranney",
                                        image = "app/src/main/res/drawable-nodpi/homeseeking_historycal_fiction.jpg",
                                        category = "Historycal fiction",
                                        totalPages = 456,
                                        readPages = 0,
                                        rating = 0,
                                        criticalPoints = "'Fourth Division'.",
                                        cpPage = 0,
                                        review = "This book is a great read for those who want to learn more about the Chinese military."
                                    )
                                )

                            }
                            Log.d("DatabaseInit", "成功插入${3}本初始书籍")
                        } catch (e: Exception) {
                            Log.e("DatabaseInit", "初始数据插入失败", e)
                        }
                    }
                }
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
