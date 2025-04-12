// File: BookViewModelTest.kt

import android.app.Application
import com.example.assignment_1booktracker.data.DatabaseBookRepository
import com.example.assignment_1booktracker.data.DataCriticalPoint
import com.example.assignment_1booktracker.data.dbBook
import com.example.assignment_1booktracker.data.NetworkBookRepository
import com.example.assignment_1booktracker.model.Book
import com.example.assignment_1booktracker.ui.screens.BookViewModel
import com.example.assignment_1booktracker.ui.screens.DbBookUiState
import com.example.assignment_1booktracker.ui.screens.NetworkBookUiState
import com.example.assignment_1booktracker.ui.uiModels.CriticalPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class FakeDatabaseBookRepository : DatabaseBookRepository {
    private val books = mutableListOf<dbBook>()
    private val _booksFlow = MutableStateFlow<List<dbBook>>(emptyList())
    private val _latestBookFlow = MutableStateFlow<dbBook?>(null)

    override fun getDbBooksFlow(): Flow<List<dbBook>> = _booksFlow
    override fun getLatestDbBookFlow(): Flow<dbBook?> = _latestBookFlow

    override suspend fun getDbBooks(): List<dbBook> = _booksFlow.value

    override suspend fun addDbBook(book: dbBook) {
        books.add(book)
        _booksFlow.value = books.toList()
        _latestBookFlow.value = book
    }

    override suspend fun updateDbBook(book: dbBook) {
        val index = books.indexOfFirst { it.id == book.id }
        if (index != -1) {
            books[index] = book
            _booksFlow.value = books.toList()
            _latestBookFlow.value = book
        }
    }

    override suspend fun deleteDbBook(book: dbBook) {
        books.removeAll { it.id == book.id }
        _booksFlow.value = books.toList()
        _latestBookFlow.value = books.lastOrNull()
    }

    override suspend fun getDbBookById(bookId: Int): dbBook? {
        return books.find { it.id == bookId }
    }
}

class FakeNetworkBookRepository : NetworkBookRepository {
    override suspend fun getBookDetails(): List<Book> {
        return listOf(
            Book(
                id = 1,
                name = "Network Book",
                author = "Network Author",
                category = "Fiction",
                image = "network_image",
                rate = "5",
                reason = "Testing network data",
                baseon = "Test basis",
                totalPages = "120"
            )
        )
    }

    override fun getBookById(imdbID: Int, books: List<Book>): Book? {
        return books.find { it.id == imdbID }
    }
}


class FakeApplication : Application()


@OptIn(ExperimentalCoroutinesApi::class)
class BookViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDBRepo: FakeDatabaseBookRepository
    private lateinit var fakeNetworkRepo: FakeNetworkBookRepository
    private lateinit var viewModel: BookViewModel
    private lateinit var application: Application

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDBRepo = FakeDatabaseBookRepository()
        fakeNetworkRepo = FakeNetworkBookRepository()
        application = FakeApplication()
        viewModel = BookViewModel(application, fakeNetworkRepo, fakeDBRepo)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testFetchNetworkBooks() = runTest {
        val networkState = viewModel.networkUiState.first { it !is NetworkBookUiState.Loading }
        if (networkState is NetworkBookUiState.Success) {
            assertEquals(1, networkState.books.size)
            assertEquals("Network Book", networkState.books.first().name)
        } else {
            fail("网络加载失败，状态为：$networkState")
        }
    }

    @Test
    fun testAddBook() = runTest {
        val newBook = dbBook(
            id = 100,
            image = "image_100",
            name = "Test Book",
            author = "Test Author",
            category = "Test Category",
            readPages = 0,
            totalPages = 200,
            progress = 0,
            rating = 0,
            criticalPoints = emptyList(),
            cpPage = null,
            review = ""
        )
        viewModel.addBook(newBook)
        testDispatcher.scheduler.advanceUntilIdle()

        val bookFromVM = viewModel.getDbBookById(100)
        assertNotNull(bookFromVM)
        assertEquals("Test Book", bookFromVM?.name)
        val latest = viewModel.latestBook.first { it != null }
        assertEquals(100, latest?.id)
    }

    @Test
    fun testUpdateReadPages() = runTest {
        val book = dbBook(
            id = 101,
            image = "image_101",
            name = "Update Read Pages Book",
            author = "Author 101",
            category = "Category 101",
            readPages = 0,
            totalPages = 100,
            progress = 0,
            rating = 0,
            criticalPoints = emptyList(),
            cpPage = null,
            review = ""
        )
        viewModel.addBook(book)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateReadPages(101, 50)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedBook = viewModel.getDbBookById(101)
        assertNotNull(updatedBook)
        assertEquals(50, updatedBook?.readPages)
        assertEquals(50, updatedBook?.progress)
    }

    @Test
    fun testUpdateRating() = runTest {
        val book = dbBook(
            id = 102,
            image = "image_102",
            name = "Rating Book",
            author = "Author 102",
            category = "Category 102",
            readPages = 0,
            totalPages = 150,
            progress = 0,
            rating = 0,
            criticalPoints = emptyList(),
            cpPage = null,
            review = ""
        )
        viewModel.addBook(book)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateRating(102, 7)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedBook = viewModel.getDbBookById(102)
        assertNotNull(updatedBook)
        assertEquals(7, updatedBook?.rating)
    }

    @Test
    fun testAddCriticalPoint() = runTest {
        val book = dbBook(
            id = 103,
            image = "image_103",
            name = "Critical Point Book",
            author = "Author 103",
            category = "Category 103",
            readPages = 0,
            totalPages = 100,
            progress = 0,
            rating = 0,
            criticalPoints = emptyList(),
            cpPage = null,
            review = ""
        )
        viewModel.addBook(book)
        testDispatcher.scheduler.advanceUntilIdle()

        val cp = CriticalPoint(id = 0, text = "Important point", page = 25)
        viewModel.addCriticalPoint(103, cp)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedBook = viewModel.getDbBookById(103)
        assertNotNull(updatedBook)
        val cps = updatedBook?.criticalPoints
        assertNotNull(cps)
        assertEquals(1, cps?.size)
        assertEquals("Important point", cps?.first()?.text)
    }

    @Test
    fun testUpdateCriticalPoint() = runTest {
        val book = dbBook(
            id = 104,
            image = "image_104",
            name = "Update CriticalPoint Book",
            author = "Author 104",
            category = "Category 104",
            readPages = 0,
            totalPages = 100,
            progress = 0,
            rating = 0,
            criticalPoints = emptyList(),
            cpPage = null,
            review = ""
        )
        viewModel.addBook(book)
        testDispatcher.scheduler.advanceUntilIdle()

        val cp = CriticalPoint(id = 0, text = "Old critical point", page = 30)
        viewModel.addCriticalPoint(104, cp)
        testDispatcher.scheduler.advanceUntilIdle()

        val original = viewModel.getDbBookById(104)
        val cpId = original?.criticalPoints?.first()?.id ?: -1
        val updatedCp = CriticalPoint(id = cpId, text = "Updated critical point", page = 35)
        viewModel.updateCriticalPoint(104, updatedCp)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedBook = viewModel.getDbBookById(104)
        assertNotNull(updatedBook)
        val updatedList = updatedBook?.criticalPoints
        assertNotNull(updatedList)
        assertEquals(1, updatedList?.size)
        assertEquals("Updated critical point", updatedList?.first()?.text)
        assertEquals(35, updatedList?.first()?.page)
    }

    @Test
    fun testDeleteCriticalPoint() = runTest {
        val book = dbBook(
            id = 105,
            image = "image_105",
            name = "Delete CriticalPoint Book",
            author = "Author 105",
            category = "Category 105",
            readPages = 0,
            totalPages = 100,
            progress = 0,
            rating = 0,
            criticalPoints = emptyList(),
            cpPage = null,
            review = ""
        )
        viewModel.addBook(book)
        testDispatcher.scheduler.advanceUntilIdle()

        val cp1 = CriticalPoint(id = 0, text = "Point 1", page = 10)
        val cp2 = CriticalPoint(id = 0, text = "Point 2", page = 20)
        viewModel.addCriticalPoint(105, cp1)
        viewModel.addCriticalPoint(105, cp2)
        testDispatcher.scheduler.advanceUntilIdle()

        val beforeDeletion = viewModel.getDbBookById(105)
        assertNotNull(beforeDeletion)
        assertEquals(2, beforeDeletion?.criticalPoints?.size)

        val cpId = beforeDeletion?.criticalPoints?.first()?.id ?: -1
        viewModel.deleteCriticalPoint(105, cpId)
        testDispatcher.scheduler.advanceUntilIdle()

        val afterDeletion = viewModel.getDbBookById(105)
        assertNotNull(afterDeletion)
        assertEquals(1, afterDeletion?.criticalPoints?.size)
        assertNotEquals(cpId, afterDeletion?.criticalPoints?.first()?.id)
    }

    @Test
    fun testUpdateReview() = runTest {
        val book = dbBook(
            id = 106,
            image = "image_106",
            name = "Review Book",
            author = "Author 106",
            category = "Category 106",
            readPages = 0,
            totalPages = 100,
            progress = 0,
            rating = 0,
            criticalPoints = emptyList(),
            cpPage = null,
            review = ""
        )
        viewModel.addBook(book)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateReview(106, "Excellent read!")
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedBook = viewModel.getDbBookById(106)
        assertNotNull(updatedBook)
        assertEquals("Excellent read!", updatedBook?.review)
    }

    @Test
    fun testDeleteBook() = runTest {
        val book = dbBook(
            id = 107,
            image = "image_107",
            name = "Delete Book",
            author = "Author 107",
            category = "Category 107",
            readPages = 0,
            totalPages = 100,
            progress = 0,
            rating = 0,
            criticalPoints = emptyList(),
            cpPage = null,
            review = ""
        )
        viewModel.addBook(book)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.getDbBookById(107))
        viewModel.deleteBook(107)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.getDbBookById(107))
    }

    @Test
    fun testGetDbBookById() = runTest {
        val book = dbBook(
            id = 108,
            image = "image_108",
            name = "Get Book Test",
            author = "Author 108",
            category = "Category 108",
            readPages = 0,
            totalPages = 100,
            progress = 0,
            rating = 0,
            criticalPoints = emptyList(),
            cpPage = null,
            review = ""
        )
        viewModel.addBook(book)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = viewModel.getDbBookById(108)
        assertNotNull(result)
        assertEquals("Get Book Test", result?.name)
    }
}
