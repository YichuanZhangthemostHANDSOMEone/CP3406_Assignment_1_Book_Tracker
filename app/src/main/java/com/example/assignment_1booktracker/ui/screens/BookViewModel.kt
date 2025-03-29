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
import com.example.assignment_1booktracker.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class BookUiState {
    object Loading : BookUiState()
    data class Success(val books: List<Book>) : BookUiState()
    data class Error(val message: String) : BookUiState()
}

/**
 * BookViewModel 接收 BookRepository 作为依赖，
 * 并通过 StateFlow 管理数据加载状态。
 */
class BookViewModel(private val bookRepository: BookRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<BookUiState>(BookUiState.Loading)
    val uiState: StateFlow<BookUiState> get() = _uiState

    init {
        fetchBooks()
    }

    /**
     * 从仓库中加载数据，并更新 UI 状态
     */
    private fun fetchBooks() {
        viewModelScope.launch {
            try {
                Log.d("BookViewModel", "开始请求书籍数据")
                val books = bookRepository.getBookDetails()
                Log.d("BookViewModel",
                    "获取到 ${books.size} 条数据，第一条书名：${books.firstOrNull()?.name ?: "无数据"}"
                )
                _uiState.value = BookUiState.Success(books)
            } catch (e: Exception) {
                Log.e("BookViewModel",
                    "数据加载失败: ${e.message ?: "未知错误"}",
                    e // 这里保留异常堆栈打印
                )
                _uiState.value = BookUiState.Error(e.message ?: "error")
            }
        }
    }

    /**
     * 提供刷新数据的方法
     */
    fun refreshBooks() {
        _uiState.value = BookUiState.Loading
        fetchBooks()
    }

    /**
     * 根据书籍 id 获取书籍详情
     */
    fun getBookById(bookId: Int): Book? {
        Log.d("BookViewModel", "Looking for bookID: $bookId")
        return when (val state = _uiState.value) {
            is BookUiState.Success -> {
                val targetBook = state.books.find { it.id == bookId }
                if (targetBook != null) {
                    Log.d("ImageLoading", "找到书籍ID: $bookId | 图片URL: ${targetBook.image ?: "无图片"}")
                } else {
                    Log.w("ImageLoading", "未找到对应ID的书籍: $bookId")
                }
                targetBook
            }
            else -> {
                Log.e("ImageLoading", "无法获取书籍列表，当前状态: ${state.javaClass.simpleName}")
                null
            }
        }
    }

    /**
     * 自定义 Factory，用于在创建 ViewModel 时注入依赖。
     *
     * 这里通过 APPLICATION_KEY 从创建上下文中获取 Application 对象，
     * 然后强转为 BookApplication，再从中获取 BookRepository 依赖，
     * 最后将依赖传递给 BookViewModel 构造函数。
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as BookApplication
                val repository = application.container.bookRepository
                BookViewModel(repository)
            }
        }
    }
}