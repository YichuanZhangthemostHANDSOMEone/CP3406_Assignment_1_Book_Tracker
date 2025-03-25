package com.example.assignment_1booktracker.data

import com.example.assignment_1booktracker.network.BookApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val bookRepository: BookRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://yichuanzhangthemosthandsomeone.github.io/Asignment2/"

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
    override val bookRepository: BookRepository by lazy {
        BookRepositoryImpl(retrofitService)
    }
}