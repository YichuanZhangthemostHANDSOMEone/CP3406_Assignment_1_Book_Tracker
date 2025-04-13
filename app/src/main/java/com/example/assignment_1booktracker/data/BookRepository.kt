package com.example.assignment_1booktracker.data

import android.content.Context
import android.util.Log
import com.example.assignment_1booktracker.model.Book
import com.example.assignment_1booktracker.network.BookApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


interface DatabaseBookRepository {
    suspend fun getDbBooks(): List<dbBook>
    fun getDbBooksFlow(): Flow<List<dbBook>>
    suspend fun getDbBookById(bookId: Int): dbBook?
    suspend fun addDbBook(book: dbBook)
    suspend fun updateDbBook(book: dbBook)
    suspend fun deleteDbBook(book: dbBook)
    fun getLatestDbBookFlow(): Flow<dbBook?>
}


interface NetworkBookRepository {
    suspend fun getBookDetails(): List<Book>
    fun getBookById(imdbID: Int, books: List<Book>): Book?
}

class DatabaseBookRepositoryImpl(
    private val bookDao: BookDao
) : DatabaseBookRepository {

    // Returns a flow of all books from the database
    override fun getDbBooksFlow(): Flow<List<dbBook>> = bookDao.getdbBooksFlow()

    // Retrieves all books from the database and handles exceptions
    override suspend fun getDbBooks(): List<dbBook> {
        return try {
            bookDao.getdbBooks()
        } catch (e: Exception) {
            Log.e("DatabaseRepo", "DB query error: ${e.localizedMessage}")
            emptyList()
        }
    }

    // Retrieves a book by its ID from the database and handles exceptions
    override suspend fun getDbBookById(bookId: Int): dbBook? {
        return try {
            bookDao.getdbBookById(bookId)
        } catch (e: Exception) {
            Log.e("DatabaseRepo", "Query error by ID: ${e.localizedMessage}")
            null
        }
    }

    // Adds a new book to the database and logs the process
    override suspend fun addDbBook(book: dbBook) {
        try {
            Log.d("DatabaseRepo", "Start inserting books: ${book.name}")
            bookDao.insertBook(book)
            Log.d("DatabaseRepo", "Successfully inserted books: ${book.name}")
        } catch (e: Exception) {
            Log.e("DatabaseRepo", "Insert books failed: ${book.name}", e)
        }
    }

    // Updates an existing book in the database and logs the process
    override suspend fun updateDbBook(book: dbBook) {
        try {
            Log.d("DatabaseRepo", "Start updating books: ID=${book.id}")
            bookDao.updateBook(book)
            Log.d("DatabaseRepo", "Successfully updated books: ID=${book.id}")
        } catch (e: Exception) {
            Log.e("DatabaseRepo", "Update books failed: ID=${book.id}", e)
        }
    }

    // Deletes a book from the database and logs the process
    override suspend fun deleteDbBook(book: dbBook) {
        try {
            Log.d("DatabaseRepo", "Start deleting books: ID=${book.id}")
            bookDao.deleteBook(book)
            Log.d("DatabaseRepo", "Successfully deleted books: ID=${book.id}")
        } catch (e: Exception) {
            Log.e("DatabaseRepo", "Delete books failed: ID=${book.id}", e)
        }
    }

    // Returns a flow of the latest book added to the database
    override fun getLatestDbBookFlow(): Flow<dbBook?> = bookDao.getLatestDbBook()
}


class NetworkBookRepositoryImpl(
    private val bookApiService: BookApiService
) : NetworkBookRepository {
    // Fetches book details from the network and handles exceptions
    override suspend fun getBookDetails(): List<Book> {
        return try {
            val response = bookApiService.fetchBooks()
            Log.d("NetworkRepo", "Request URL: ${response.raw().request.url}")

            if (response.isSuccessful) {
                response.body()?.books?.also {
                    Log.d("NetworkRepo", "Get ${it.size} books")
                } ?: emptyList()
            } else {
                Log.e("NetworkRepo", "HTTP error: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("NetworkRepo", "Network anomaly: ${e.localizedMessage}")
            emptyList()
        }
    }

    // Retrieves a book by its ID from the local list of books
    override fun getBookById(imdbID: Int, books: List<Book>): Book? {
        Log.d("NetworkRepo", "Local books list query")
        return books.find { it.id == imdbID }
    }
}



