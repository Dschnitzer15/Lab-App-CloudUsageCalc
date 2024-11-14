package com.example.bookstoreapp.data.network

import com.example.bookstoreapp.data.model.Book
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Schnittstelle zur Definition der API-Endpunkte
interface BookApiService {
    // Endpunkt, um alle Bücher abzurufen
    @GET("list")
    suspend fun getBooks(): List<Book>

    // Endpunkt, um die Details eines spezifischen Buchs abzurufen
    @GET("details/{bookId}")
    suspend fun getBookDetails(@Path("bookId") bookId: Long): Book
}

// BookApiClient: Initialisiert Retrofit und stellt die BookApiService-Instanz bereit
object BookApiClient {
    private const val BASE_URL = "http://127.0.0.1:8080" // Basis-URL des Backends

    // Retrofit-Instanz
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Zugriff auf die API-Schnittstelle
    val apiService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }
}
