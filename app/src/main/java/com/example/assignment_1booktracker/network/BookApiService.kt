package com.example.assignment_1booktracker.network

import com.example.assignment_1booktracker.model.Book
import com.example.assignment_1booktracker.model.BooksResponse
import retrofit2.Response
import retrofit2.http.GET

interface BookApiService {
    @GET("data.json")
    suspend fun fetchBooks(): Response<BooksResponse>
}