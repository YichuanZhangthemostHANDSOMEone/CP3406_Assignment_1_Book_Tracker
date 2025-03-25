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
        val bookRepository = (application as BookApplication).container.bookRepository
        fetchBooks(bookRepository)
    }

    private suspend fun fetchBooks(bookRepository: BookRepository) {
        if (isDataLoaded) return
        _books.value = bookRepository.getBookDetails()
    }
}