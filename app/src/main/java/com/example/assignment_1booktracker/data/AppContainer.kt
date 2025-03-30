package com.example.assignment_1booktracker.data

import android.app.Application
import androidx.room.Room
import com.example.assignment_1booktracker.network.BookApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val bookRepository: BookRepository
}

class DefaultAppContainer(private val application: Application) : AppContainer {
    private val baseUrl = "https://yichuanzhangthemosthandsomeone.github.io/Assignment/"

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }

    /**
     * DI implementation for images repository
     */

    // 初始化 Room 数据库
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "book_database"
        ).build()
    }

    // 创建 Repository 时传入数据库实例（并可结合网络数据）
    override val bookRepository: BookRepository by lazy {
        // 这里假设你修改后的 BookRepositoryImpl 构造函数接受 database 和 retrofitService 两个参数
        BookRepositoryImpl(database.bookDao(), retrofitService)
    }
}