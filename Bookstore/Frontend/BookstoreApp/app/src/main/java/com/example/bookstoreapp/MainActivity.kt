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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
        // Check for permission if API level is 33 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permission if it is not granted
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
                return  // Exit if permission is not yet granted
            }
        }

        // Set up Notification Manager and Channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "notification_channel"

        // Create Notification Channel if API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Bookstore Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build and display the notification
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
        var isBottomSheetVisible by remember { mutableStateOf(false) }
        var isSettingsVisible by remember { mutableStateOf(false) }
        var notificationsEnabled by remember { mutableStateOf(false) }
        var darkThemeEnabled by remember { mutableStateOf(false) } // Dark mode state

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        // Fetch books from API
        LaunchedEffect(Unit) {
            try {
                books = BookApiClient.apiService.getBooks()
            } catch (e: Exception) {
                // Handle error (e.g., show a toast or a retry button)
                println("Error loading books: ${e.message}")
            }
        }

        // Dynamischer Theme-Wechsel basierend auf darkThemeEnabled
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

                // Settings overlay blocker
                if (isSettingsVisible) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .clickable { }
                    )
                }

                // Custom Bottom Sheet (Overlaid above the books)
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
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                BookDetailsContent(book)

                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Close Button for the Bottom Sheet
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

                // Settings Button (Fixed at top right corner)
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

                // Settings Screen (Slide-in from left)
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

                // Größerer "Schließen"-Button
                Button(
                    onClick = { onClose() },
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(50.dp)      // Höhe des Buttons
                        .fillMaxWidth()      // Button nimmt die gesamte Breite ein
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
    fun BookDetailsContent(book: Book) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
                .padding(bottom = 20.dp) // Padding am unteren Rand für den Button
        ) {
            Text(
                text = "Buchdetails",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface // Farbe aus dem Theme
            )
            Spacer(modifier = Modifier.padding(8.dp))

            // Beschreibungen mit dynamischer Farbe und Scrollbarkeit
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "Titel: ${book.title}",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface // Farbe aus dem Theme
                )
                Text(
                    text = "Autor: ${book.author}",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface // Farbe aus dem Theme
                )
                Text(
                    text = "Beschreibung: ${book.description}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface // Farbe aus dem Theme
                )
            }
        }
    }
















