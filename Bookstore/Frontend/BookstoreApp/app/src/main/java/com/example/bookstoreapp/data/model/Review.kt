package com.example.bookstoreapp.data.model

data class Review(
    val id: Long,
    val bookId: Long,
    val reviewText: String,
    val rating: Int
)
