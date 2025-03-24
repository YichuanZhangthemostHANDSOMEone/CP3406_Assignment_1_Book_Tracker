package com.example.assignment_1booktracker.data

import android.util.Log
import com.example.assignment_1booktracker.model.Book
import com.example.assignment_1booktracker.network.BookApiService
import retrofit2.Response

interface BookRepository {
    suspend fun getBookDetails(): List<Book> // 修正方法名拼写错误
    fun getBookById(imdbID: String, books: List<Book>): Book? // 保持接口定义一致
}

class BookRepositoryImpl(private val bookApiService: BookApiService) : BookRepository {
    override suspend fun getBookDetails(): List<Book> {
        val response = bookApiService.fetchBooks()
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }

    override fun getBookById(imdbID: String, books: List<Book>): Book? {
        Log.d("Checking - A", "Enter function")
        return books.find { it.id == imdbID }
    }
}