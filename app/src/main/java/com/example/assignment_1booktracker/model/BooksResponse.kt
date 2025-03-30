package com.example.assignment_1booktracker.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BooksResponse(
    @SerialName("books")  // 与实际JSON中的字段名对应
    val books: List<Book>
)
