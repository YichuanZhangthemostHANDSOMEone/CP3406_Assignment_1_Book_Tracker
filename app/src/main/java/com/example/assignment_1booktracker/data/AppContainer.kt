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
    private val baseUrl = "https://yichuanzhangthemosthandsomeone.github.io/Assignment/"

    private val jsonFormat = Json { ignoreUnknownKeys = true }

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(jsonFormat.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val bookApiService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(application, CoroutineScope(Dispatchers.IO))
    }

    override val databaseRepository: DatabaseBookRepository by lazy {
        DatabaseBookRepositoryImpl(database.bookDao())
    }

    override val networkRepository: NetworkBookRepository by lazy {
        NetworkBookRepositoryImpl(bookApiService)
    }
}
