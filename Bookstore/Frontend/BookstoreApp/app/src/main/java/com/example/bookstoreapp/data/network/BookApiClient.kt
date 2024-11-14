package com.example.bookstoreapp.data.network

import com.example.bookstoreapp.data.model.Book
import com.example.bookstoreapp.data.model.BookDetails
import com.example.bookstoreapp.data.model.Review
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient


// Schnittstelle zur Definition der API-Endpunkte
interface BookApiClient {
    // Endpunkt für alle Bücher
    @GET("books/list")
    suspend fun getBooks(): List<Book> // Ruft alle Bücher ab

    // Endpunkt für Details eines bestimmten Buchs
    @GET("details/{id}")
    suspend fun getBookDetails(@Path("id") id: Long): BookDetails // Holt Details wie Beschreibung, Veröffentlichungsjahr

    // Endpunkt für die Reviews eines bestimmten Buchs
    @GET("reviews/{id}")
    suspend fun getReviews(@Path("id") id: Long): List<Review> // Holt Reviewtext und Bewertung

    companion object {
        private const val BASE_URL = "http://10.0.2.2"

        fun create(): BookApiClient {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(BookApiClient::class.java)
        }
    }
}
