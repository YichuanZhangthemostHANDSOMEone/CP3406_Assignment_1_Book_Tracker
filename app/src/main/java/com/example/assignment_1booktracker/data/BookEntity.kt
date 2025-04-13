package com.example.assignment_1booktracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dbBook")
data class dbBook(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val image: String,
    val name: String,
    val author: String,
    val category: String,
    val readPages: Int? = 0,
    val totalPages: Int,
    val progress: Int? = 0,
    val rating: Int? = null,
    val criticalPoints: List<DataCriticalPoint>? = emptyList(),
    val cpPage: Int? = null,
    val review: String? = null
)