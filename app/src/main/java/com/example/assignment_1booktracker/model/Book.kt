package com.example.assignment_1booktracker.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("author") val author: String,
    @SerialName("category") val category: String,
    @SerialName("image") val image: String,
    @SerialName("rate") val rate: String,
    @SerialName("reason") val reason: String,
    @SerialName("baseon") val baseon: String,
)
