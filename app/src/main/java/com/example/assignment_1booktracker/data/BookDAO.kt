package com.example.assignment_1booktracker.data

import androidx.room.*

@Dao
interface BookDao {

    @Query("SELECT * FROM books")
    suspend fun getdbBooks(): List<dbBook>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getdbBookById(bookId: Int): dbBook?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: dbBook)

    @Update
    suspend fun updateBook(book: dbBook)

    @Delete
    suspend fun deleteBook(book: dbBook)
}
