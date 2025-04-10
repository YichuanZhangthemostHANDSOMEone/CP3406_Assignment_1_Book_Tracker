package com.example.assignment_1booktracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM dbBook")
    fun getdbBooksFlow(): Flow<List<dbBook>>

    @Query("SELECT * FROM dbBook")
    suspend fun getdbBooks(): List<dbBook>

    @Query("SELECT * FROM dbBook WHERE id = :bookId")
    suspend fun getdbBookById(bookId: Int): dbBook?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: dbBook)

    @Update
    suspend fun updateBook(book: dbBook)

    @Delete
    suspend fun deleteBook(book: dbBook)

    @Query("SELECT * FROM dbBook ORDER BY id DESC LIMIT 1")
    fun getLatestDbBook(): Flow<dbBook?>
}
