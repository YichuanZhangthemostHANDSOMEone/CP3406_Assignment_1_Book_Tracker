package com.example.assignment_1booktracker.data

import android.app.Application
import androidx.room.Room
import com.example.assignment_1booktracker.network.BookApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val databaseRepository: DatabaseBookRepository
    val networkRepository: NetworkBookRepository
}

class DefaultAppContainer(private val application: Application) : AppContainer {
    // Base URL for network requests
    private val baseUrl = "https://yichuanzhangthemosthandsomeone.github.io/Assignment/"

    // JSON format configuration for Retrofit
    private val jsonFormat = Json { ignoreUnknownKeys = true }

    // Retrofit instance for network operations
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(jsonFormat.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    // Book API service instance
    private val bookApiService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }

    // Database instance
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(application, CoroutineScope(Dispatchers.IO))
    }

    // Repository for database operations
    override val databaseRepository: DatabaseBookRepository by lazy {
        DatabaseBookRepositoryImpl(database.bookDao())
    }

    // Repository for network operations
    override val networkRepository: NetworkBookRepository by lazy {
        NetworkBookRepositoryImpl(bookApiService)
    }
}
