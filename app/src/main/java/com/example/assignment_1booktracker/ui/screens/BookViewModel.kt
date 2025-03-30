package com.example.assignment_1booktracker.ui.screens

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assignment_1booktracker.BookApplication
import com.example.assignment_1booktracker.data.BookRepository
import com.example.assignment_1booktracker.data.dbBook
import com.example.assignment_1booktracker.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DbBookUiState {
    object Loading : DbBookUiState()
    data class Success(val books: List<dbBook>) : DbBookUiState()
    data class Error(val message: String) : DbBookUiState()
}

sealed class NetworkBookUiState {
    object Loading : NetworkBookUiState()
    data class Success(val books: List<Book>) : NetworkBookUiState()
    data class Error(val message: String) : NetworkBookUiState()
}

/**
 * BookViewModel 接收 BookRepository 作为依赖，
 * 并通过 StateFlow 管理数据加载状态。
 */
class BookViewModel(
    application: Application, // 直接接收 Application
    private val bookRepository: BookRepository
) : AndroidViewModel(application) {

    // 数据库数据状态
    private val _dbUiState = MutableStateFlow<DbBookUiState>(DbBookUiState.Loading)
    val dbUiState: StateFlow<DbBookUiState> get() = _dbUiState

    // 网络数据状态
    private val _networkUiState = MutableStateFlow<NetworkBookUiState>(NetworkBookUiState.Loading)
    val networkUiState: StateFlow<NetworkBookUiState> get() = _networkUiState

    init {
        // 页面一（如 LibraryScreen 等）只使用数据库数据
        fetchDbBooks()
        // 网络功能页面使用网络数据
        fetchNetworkBooks()
    }

    /**
     * 从数据库中加载数据，并更新 _dbUiState
     */
    private fun fetchDbBooks() {
        viewModelScope.launch {
            try {
                val localBooks = bookRepository.getDbBooks()
                Log.d("BookViewModel", "Fetched ${localBooks.size} books from DB")
                _dbUiState.value = DbBookUiState.Success(localBooks)
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching DB books: ${e.message ?: "unknown error"}", e)
                _dbUiState.value = DbBookUiState.Error(e.message ?: "error")
            }
        }
    }

    /**
     * 从网络中加载数据，并更新 _networkUiState
     * 网络数据可以在转换为 dbBook 后存入数据库，但如果希望网络与数据库独立展示，
     * 则这里只更新网络状态。
     */
    private fun fetchNetworkBooks() {
        viewModelScope.launch {
            try {
                val networkBooks: List<Book> = bookRepository.getBookDetails()  // 原有网络接口
                Log.d("BookViewModel", "Fetched ${networkBooks.size} books from Network")
                _networkUiState.value = NetworkBookUiState.Success(networkBooks)
                // 如果需要把网络数据写入数据库（例如合并更新），可以在此进行转换并写入：
                // val convertedBooks = networkBooks.map { net ->
                //     dbBook(
                //         image = net.image,
                //         name = net.name,
                //         author = net.author,
                //         category = net.category,
                //         readPages = net.readPages,
                //         totalPages = net.totalPages,
                //         progress = net.progress,
                //         rating = net.rating,
                //         criticalPoints = net.criticalPoints,
                //         cpPage = net.cpPage,
                //         review = net.review
                //     )
                // }
                // convertedBooks.forEach { bookRepository.addDbBook(it) }
                // fetchDbBooks()
            } catch (e: Exception) {
                Log.e("BookViewModel", "Network fetch error: ${e.message ?: "unknown error"}", e)
                _networkUiState.value = NetworkBookUiState.Error(e.message ?: "error")
            }
        }
    }

    // 以下数据库操作方法（针对 dbBook），供 LibraryScreen、BookDetail 等页面使用

    fun addBook(book: dbBook) = viewModelScope.launch {
        bookRepository.addDbBook(book)
        fetchDbBooks()
    }

    fun updateReadPages(bookId: Int, readPages: Int) = viewModelScope.launch {
        val book = bookRepository.getDbBookById(bookId) ?: return@launch
        val newProgress = if (book.totalPages > 0) ((readPages.toFloat() / book.totalPages) * 100).toInt() else 0
        val updatedBook = book.copy(readPages = readPages, progress = newProgress)
        bookRepository.updateDbBook(updatedBook)
        fetchDbBooks()
    }

    fun updateRating(bookId: Int, rating: Int) = viewModelScope.launch {
        val book = bookRepository.getDbBookById(bookId) ?: return@launch
        val updatedBook = book.copy(rating = rating)
        bookRepository.updateDbBook(updatedBook)
        fetchDbBooks()
    }

    fun updateBookPoints(bookId: Int, criticalPoints: String, cpPage: Int) = viewModelScope.launch {
        val book = bookRepository.getDbBookById(bookId) ?: return@launch
        val updatedBook = book.copy(criticalPoints = criticalPoints, cpPage = cpPage)
        bookRepository.updateDbBook(updatedBook)
        fetchDbBooks()
    }

    fun updateReview(bookId: Int, review: String) = viewModelScope.launch {
        val book = bookRepository.getDbBookById(bookId) ?: return@launch
        val updatedBook = book.copy(review = review)
        bookRepository.updateDbBook(updatedBook)
        fetchDbBooks()
    }

    fun deleteBook(bookId: Int) = viewModelScope.launch {
        val book = bookRepository.getDbBookById(bookId) ?: return@launch
        bookRepository.deleteDbBook(book)
        fetchDbBooks()
    }

    // 获取单本书籍详情（仅针对数据库数据）
    fun getDbBookById(bookId: Int): dbBook? {
        return when (val state = _dbUiState.value) {
            is DbBookUiState.Success -> state.books.find { it.id == bookId }
            else -> null
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as BookApplication
                BookViewModel(
                    application = application,
                    bookRepository = application.container.bookRepository
                )
            }
        }
    }
}
