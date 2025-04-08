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

                            // 使用预设书籍数据
                            PresetBooks.list.forEach { preset ->
                                Log.d("DatabaseInit", "插入默认书籍: ${preset.name}")
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
                            Log.d("DatabaseInit", "成功插入${PresetBooks.list.size}本初始书籍")
                        } catch (e: Exception) {
                            Log.e("DatabaseInit", "初始数据插入失败", e)
                        }
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Log.d("DatabaseInit", "数据库已打开")
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
