package com.example.assignment_1booktracker.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("author") val author: String,
    @SerialName("category") val category: String,
    @SerialName("image") val image: String,
    @SerialName("reason") val reason: String,
)
