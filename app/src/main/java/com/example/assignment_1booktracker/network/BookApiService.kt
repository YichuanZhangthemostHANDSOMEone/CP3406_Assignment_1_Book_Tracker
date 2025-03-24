package com.example.assignment_1booktracker.network

import com.example.assignment_1booktracker.model.Book
import retrofit2.Response
import retrofit2.http.GET

interface BookApiService {
    @GET("books.json")
    suspend fun fetchBooks(): Response<List<Book>>
}