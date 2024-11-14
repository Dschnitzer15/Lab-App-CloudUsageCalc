package com.example.bookstoreapp

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookstoreapp.data.model.Book
import com.example.bookstoreapp.data.model.BookDetails
import com.example.bookstoreapp.data.model.Review
import com.example.bookstoreapp.data.network.BookApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookstoreAppTheme {
                BookstoreScreen()
            }
        }
    }

    private fun showNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
                return
            }
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "notification_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Bookstore Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Benachrichtigung")
            .setContentText("Du hast die Benachrichtigungen aktiviert!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(1, notification)
        }
    }

    @Composable
    fun BookstoreScreen() {
        var books by remember { mutableStateOf<List<Book>>(emptyList()) }
        var selectedBook by remember { mutableStateOf<Book?>(null) }
        var bookDetails by remember { mutableStateOf<BookDetails?>(null) }
        var bookReviews by remember { mutableStateOf<List<Review>>(emptyList()) }
        var isBottomSheetVisible by remember { mutableStateOf(false) }
        var isSettingsVisible by remember { mutableStateOf(false) }
        var notificationsEnabled by remember { mutableStateOf(false) }
        var darkThemeEnabled by remember { mutableStateOf(false) }

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        // Fetch books
        LaunchedEffect(Unit) {
            try {
                books = BookApiClient.create().getBooks()
            } catch (e: Exception) {
                println("Error loading books: ${e.message}")
            }
        }

        // Fetch book details and reviews when a book is selected
        LaunchedEffect(selectedBook) {
            selectedBook?.let { book ->
                try {
                    bookDetails = BookApiClient.create().getBookDetails(book.id)
                    bookReviews = BookApiClient.create().getReviews(book.id)
                } catch (e: Exception) {
                    println("Error loading details or reviews: ${e.message}")
                }
            }
        }

        // Main layout for books
        BookstoreAppTheme(darkTheme = darkThemeEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(books.size) { index ->
                        val book = books[index]
                        BookItem(
                            book = book,
                            onClick = {
                                if (!isBottomSheetVisible && !isSettingsVisible) {
                                    selectedBook = book
                                    isBottomSheetVisible = true
                                }
                            },
                            isBottomSheetVisible = isBottomSheetVisible || isSettingsVisible
                        )
                    }
                }

                // Settings overlay
                if (isSettingsVisible) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .clickable { }
                    )
                }

                // Book details bottom sheet
                AnimatedVisibility(
                    visible = isBottomSheetVisible,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                ) {
                    selectedBook?.let { book ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            BookDetailsContent(book, bookDetails, bookReviews)
                        }
                    }
                }

                // Button to close bottom sheet
                if (isBottomSheetVisible) {
                    Button(
                        onClick = { isBottomSheetVisible = false },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Schließen", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Settings button
                FloatingActionButton(
                    onClick = { isSettingsVisible = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        "⚙️",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                // Settings screen visibility
                AnimatedVisibility(
                    visible = isSettingsVisible,
                    enter = slideInHorizontally(initialOffsetX = { -it }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    SettingsScreen(
                        notificationsEnabled = notificationsEnabled,
                        onNotificationToggle = {
                            notificationsEnabled = it
                            if (it) {
                                coroutineScope.launch {
                                    delay(3000)
                                    showNotification(context)
                                }
                            }
                        },
                        darkThemeEnabled = darkThemeEnabled,
                        onDarkThemeToggle = { darkThemeEnabled = it },
                        onClose = { isSettingsVisible = false }
                    )
                }
            }
        }
    }

    @Composable
    fun SettingsScreen(
        notificationsEnabled: Boolean,
        onNotificationToggle: (Boolean) -> Unit,
        darkThemeEnabled: Boolean,
        onDarkThemeToggle: (Boolean) -> Unit,
        onClose: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.7f)
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Benachrichtigungen aktivieren",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Checkbox(
                        checked = notificationsEnabled,
                        onCheckedChange = onNotificationToggle
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Dark Mode aktivieren",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Switch(
                        checked = darkThemeEnabled,
                        onCheckedChange = onDarkThemeToggle
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onClose() },
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(50.dp)
                        .fillMaxWidth()
                ) {
                    Text("Schließen", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }

    @Composable
    fun BookstoreAppTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
        val colorScheme = if (darkTheme) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }

    @Composable
    fun BookItem(book: Book, onClick: () -> Unit, isBottomSheetVisible: Boolean) {
        Button(
            onClick = onClick,
            modifier = Modifier.padding(8.dp).height(200.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            enabled = !isBottomSheetVisible
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = book.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(text = book.author, fontSize = 16.sp, color = Color.DarkGray)
            }
        }
    }

    @Composable
    fun BookDetailsContent(book: Book, details: BookDetails?, reviews: List<Review>) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
        ) {
            Text(text = "Buchdetails", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.padding(8.dp))

            Text(text = "Titel: ${book.title}", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "Autor: ${book.author}", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)

            details?.let {
                Text(text = "Beschreibung: ${it.description}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "Veröffentlichungsjahr: ${it.publicationYear}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.padding(8.dp))

            Text(text = "Bewertungen:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            reviews.forEach { review ->
                Text(text = "Rating: ${review.rating} - ${review.reviewText}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
















