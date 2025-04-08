package com.example.assignment_1booktracker.data

import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.assignment_1booktracker.BookApplication
import com.example.assignment_1booktracker.model.Book

object SharedBookData {
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> get() = _books

    private var isDataLoaded = false

    suspend fun initialize(application: Application) {
        val networkRepo = (application as BookApplication).container.networkRepository
        fetchBooks(networkRepo)
    }

    private suspend fun fetchBooks(networkRepo: NetworkBookRepository) {
        if (isDataLoaded) return
        _books.value = networkRepo.getBookDetails()
        isDataLoaded = true
    }

    fun updateBooks(newBooks: List<Book>) {
        _books.value = newBooks
    }
}
