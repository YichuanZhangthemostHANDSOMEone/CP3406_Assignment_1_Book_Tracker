package com.example.assignment_1booktracker.data

import android.content.Context
import android.util.Log
import com.example.assignment_1booktracker.model.Book
import com.example.assignment_1booktracker.network.BookApiService
import kotlinx.coroutines.CoroutineScope
import retrofit2.Response

// Database相关操作接口
interface DatabaseBookRepository {
    suspend fun getDbBooks(): List<dbBook>
    suspend fun getDbBookById(bookId: Int): dbBook?
    suspend fun addDbBook(book: dbBook)
    suspend fun updateDbBook(book: dbBook)
    suspend fun deleteDbBook(book: dbBook)
}

// Network相关操作接口
interface NetworkBookRepository {
    suspend fun getBookDetails(): List<Book>
    fun getBookById(imdbID: Int, books: List<Book>): Book?
}
// 数据库Repository实现
class DatabaseBookRepositoryImpl(
    private val bookDao: BookDao
) : DatabaseBookRepository {
    override suspend fun getDbBooks(): List<dbBook> {
        return try {
            bookDao.getdbBooks()
        } catch (e: Exception) {
            Log.e("DatabaseRepo", "DB查询错误: ${e.localizedMessage}")
            emptyList()
        }
    }

    override suspend fun getDbBookById(bookId: Int): dbBook? {
        return try {
            bookDao.getdbBookById(bookId)
        } catch (e: Exception) {
            Log.e("DatabaseRepo", "按ID查询错误: ${e.localizedMessage}")
            null
        }
    }

    override suspend fun addDbBook(book: dbBook) {
        try {
            Log.d("DatabaseRepo", "开始插入书籍: ${book.name}")
            bookDao.insertBook(book)
            Log.d("DatabaseRepo", "成功插入书籍: ${book.name}")
        } catch (e: Exception) {
            Log.e("DatabaseRepo", "插入书籍失败: ${book.name}", e)
        }
    }

    override suspend fun updateDbBook(book: dbBook) {
        try {
            Log.d("DatabaseRepo", "开始更新书籍: ID=${book.id}")
            bookDao.updateBook(book)
            Log.d("DatabaseRepo", "成功更新书籍: ID=${book.id}")
        } catch (e: Exception) {
            Log.e("DatabaseRepo", "更新书籍失败: ID=${book.id}", e)
        }
    }

    override suspend fun deleteDbBook(book: dbBook) {
        try {
            Log.d("DatabaseRepo", "开始删除书籍: ID=${book.id}")
            bookDao.deleteBook(book)
            Log.d("DatabaseRepo", "成功删除书籍: ID=${book.id}")
        } catch (e: Exception) {
            Log.e("DatabaseRepo", "删除书籍失败: ID=${book.id}", e)
        }
    }
}

// 网络Repository实现
class NetworkBookRepositoryImpl(
    private val bookApiService: BookApiService
) : NetworkBookRepository {
    override suspend fun getBookDetails(): List<Book> {
        return try {
            val response = bookApiService.fetchBooks()
            Log.d("NetworkRepo", "请求URL: ${response.raw().request.url}")

            if (response.isSuccessful) {
                response.body()?.books?.also {
                    Log.d("NetworkRepo", "获取到${it.size}本书籍")
                } ?: emptyList()
            } else {
                Log.e("NetworkRepo", "HTTP错误: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("NetworkRepo", "网络异常: ${e.localizedMessage}")
            emptyList()
        }
    }

    override fun getBookById(imdbID: Int, books: List<Book>): Book? {
        Log.d("NetworkRepo", "本地书籍列表查询")
        return books.find { it.id == imdbID }
    }
}



