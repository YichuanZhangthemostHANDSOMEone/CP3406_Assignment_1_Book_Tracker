package com.example.assignment_1booktracker.data

import android.util.Log
import com.example.assignment_1booktracker.model.Book
import com.example.assignment_1booktracker.network.BookApiService
import retrofit2.Response

interface BookRepository {
    suspend fun getBookDetails(): List<Book> // 修正方法名拼写错误
    fun getBookById(imdbID: Int, books: List<Book>): Book? // 保持接口定义一致
    suspend fun getDbBooks(): List<dbBook>
    suspend fun getDbBookById(bookId: Int): dbBook?
    suspend fun addDbBook(book: dbBook)
    suspend fun updateDbBook(book: dbBook)
    suspend fun deleteDbBook(book: dbBook)
}

class BookRepositoryImpl(
    private val bookDao: BookDao,
    private val bookApiService: BookApiService
)    : BookRepository {
    override suspend fun getBookDetails(): List<Book> {
        return try {
            val response = bookApiService.fetchBooks()
            Log.d("Network", "请求URL: ${response.raw().request.url}")

            if (response.isSuccessful) {
                val booksResponse = response.body()
                val books = booksResponse?.books ?: emptyList()
                books.forEach {
                    Log.d("BookData", "解析成功: ${it.name} (ID: ${it.id})")
                }
                Log.d("Network", "获取到 ${books.size} 本书籍")
                books
            } else {
                Log.e("Network", "请求失败: HTTP ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("Network", "网络异常: ${e.localizedMessage}")
            emptyList()
        }
    }

    override fun getBookById(imdbID: Int, books: List<Book>): Book? {
        Log.d("Checking - A", "Enter function")
        return books.find { it.id == imdbID }
    }

    override suspend fun getDbBooks(): List<dbBook> {
        return try {
            bookDao.getdbBooks()
        } catch (e: Exception) {
            Log.e("BookRepository", "Error fetching books from DB: ${e.localizedMessage}")
            emptyList()
        }
    }

    override suspend fun getDbBookById(bookId: Int): dbBook? {
        return try {
            bookDao.getdbBookById(bookId)
        } catch (e: Exception) {
            Log.e("BookRepository", "Error fetching book by id from DB: ${e.localizedMessage}")
            null
        }
    }

    override suspend fun addDbBook(book: dbBook) {
        bookDao.insertBook(book)
    }

    override suspend fun updateDbBook(book: dbBook) {
        bookDao.updateBook(book)
    }

    override suspend fun deleteDbBook(book: dbBook) {
        bookDao.deleteBook(book)
    }
}


