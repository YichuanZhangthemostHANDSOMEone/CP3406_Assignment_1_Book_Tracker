package com.example.assignment_1booktracker.ui.screens

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assignment_1booktracker.BookApplication
import com.example.assignment_1booktracker.data.DatabaseBookRepository
import com.example.assignment_1booktracker.data.NetworkBookRepository
import com.example.assignment_1booktracker.data.dbBook
import com.example.assignment_1booktracker.data.DataCriticalPoint
import com.example.assignment_1booktracker.ui.uiModels.CriticalPoint  // UI模型
import com.example.assignment_1booktracker.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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

class BookViewModel(
    application: Application,
    private val networkRepo: NetworkBookRepository,
    private val databaseRepo: DatabaseBookRepository
) : AndroidViewModel(application) {

    private val _dbUiState = MutableStateFlow<DbBookUiState>(DbBookUiState.Loading)
    val dbUiState: StateFlow<DbBookUiState> = _dbUiState

    private val _networkUiState = MutableStateFlow<NetworkBookUiState>(NetworkBookUiState.Loading)
    val networkUiState: StateFlow<NetworkBookUiState> = _networkUiState

    init {
        viewModelScope.launch {
            databaseRepo.getDbBooksFlow().collect { books ->
                _dbUiState.value = DbBookUiState.Success(books)
                Log.d("BookViewModel", "Database books loaded: ${books.size}")
            }
        }
        fetchNetworkBooks()
    }

    private fun fetchNetworkBooks() {
        viewModelScope.launch {
            try {
                val books = networkRepo.getBookDetails()
                _networkUiState.value = NetworkBookUiState.Success(books)
                Log.d("BookViewModel", "Network books loaded: ${books.size}")
            } catch (e: Exception) {
                _networkUiState.value = NetworkBookUiState.Error(e.message ?: "Network error")
                Log.e("BookViewModel", "Network error", e)
            }
        }
    }

    fun addBook(book: dbBook) = viewModelScope.launch {
        try {
            databaseRepo.addDbBook(book)
        } catch (e: Exception) {
            _dbUiState.value = DbBookUiState.Error("Add failed: ${e.message}")
        }
    }

    fun updateReadPages(bookId: Int, readPages: Int) = viewModelScope.launch {
        try {
            databaseRepo.getDbBookById(bookId)?.let { book ->
                val progress = calculateProgress(readPages, book.totalPages)
                val updatedBook = book.copy(
                    readPages = readPages,
                    progress = progress
                )
                databaseRepo.updateDbBook(updatedBook)
            }
        } catch (e: Exception) {
            _dbUiState.value = DbBookUiState.Error("Update failed: ${e.message}")
        }
    }

    fun updateRating(bookId: Int, rating: Int) = viewModelScope.launch {
        try {
            databaseRepo.getDbBookById(bookId)?.let { book ->
                val updatedBook = book.copy(rating = rating)
                databaseRepo.updateDbBook(updatedBook)
            }
        } catch (e: Exception) {
            _dbUiState.value = DbBookUiState.Error("Rating update failed: ${e.message}")
        }
    }

    // 修改后的新增关键点方法：
    // 接收 UI 层的 CriticalPoint 类型，然后转换成 DataCriticalPoint
    fun addCriticalPoint(bookId: Int, newPoint: CriticalPoint) = viewModelScope.launch {
        databaseRepo.getDbBookById(bookId)?.let { book ->
            val newList = book.criticalPoints?.toMutableList() ?: mutableListOf()
            // 自动生成新的关键点 id，取现有最大 id+1
            val newId = (newList.maxOfOrNull { it.id } ?: 0) + 1
            // 使用 UI 层的 newPoint 并设置新的 id
            val pointWithId = newPoint.copy(id = newId)
            // 转换为数据层的 DataCriticalPoint
            val dataPoint = DataCriticalPoint(
                id = pointWithId.id,
                text = pointWithId.text,
                page = pointWithId.page
            )
            newList.add(dataPoint)
            val updatedBook = book.copy(criticalPoints = newList)
            databaseRepo.updateDbBook(updatedBook)
        }
    }

    // 修改后的更新关键点方法：
    // 接收 UI 层的 CriticalPoint 类型，并转换成 DataCriticalPoint
    fun updateCriticalPoint(bookId: Int, updatedPoint: CriticalPoint) = viewModelScope.launch {
        databaseRepo.getDbBookById(bookId)?.let { book ->
            val newList = book.criticalPoints?.map {
                if (it.id == updatedPoint.id) {
                    DataCriticalPoint(
                        id = updatedPoint.id,
                        text = updatedPoint.text,
                        page = updatedPoint.page
                    )
                } else it
            } ?: listOf(
                DataCriticalPoint(
                    id = updatedPoint.id,
                    text = updatedPoint.text,
                    page = updatedPoint.page
                )
            )
            val updatedBook = book.copy(criticalPoints = newList)
            databaseRepo.updateDbBook(updatedBook)
        }
    }

    fun deleteCriticalPoint(bookId: Int, pointId: Int) = viewModelScope.launch {
        databaseRepo.getDbBookById(bookId)?.let { book ->
            val newList = book.criticalPoints?.filter { it.id != pointId } ?: emptyList()
            val updatedBook = book.copy(criticalPoints = newList)
            databaseRepo.updateDbBook(updatedBook)
        }
    }

    fun updateReview(bookId: Int, review: String) = viewModelScope.launch {
        try {
            databaseRepo.getDbBookById(bookId)?.let { book ->
                val updatedBook = book.copy(review = review)
                databaseRepo.updateDbBook(updatedBook)
            }
        } catch (e: Exception) {
            _dbUiState.value = DbBookUiState.Error("Review update failed: ${e.message}")
        }
    }

    fun deleteBook(bookId: Int) = viewModelScope.launch {
        try {
            databaseRepo.getDbBookById(bookId)?.let {
                databaseRepo.deleteDbBook(it)
            }
        } catch (e: Exception) {
            _dbUiState.value = DbBookUiState.Error("Delete failed: ${e.message}")
        }
    }

    fun getDbBookById(bookId: Int): dbBook? {
        return when (val state = _dbUiState.value) {
            is DbBookUiState.Success -> state.books.find { it.id == bookId }
            else -> null
        }
    }

    private fun calculateProgress(read: Int, total: Int): Int {
        return if (total > 0) ((read.toFloat() / total) * 100).toInt() else 0
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as BookApplication
                BookViewModel(
                    application = application,
                    networkRepo = application.container.networkRepository,
                    databaseRepo = application.container.databaseRepository
                )
            }
        }
    }
}
