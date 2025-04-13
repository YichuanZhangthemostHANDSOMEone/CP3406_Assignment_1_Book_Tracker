package com.example.assignment_1booktracker.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.assignment_1booktracker.data.dbBook
import com.example.assignment_1booktracker.model.Book
import com.example.assignment_1booktracker.ui.uiModels.getImagePainter
import com.example.assignment_1booktracker.routes.routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun RecDetailsScreen(
    navController: NavController,
    recId: Int?,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    //Monitor network status
    val networkState by viewModel.networkUiState.collectAsState()
    when (networkState) {
        is NetworkBookUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is NetworkBookUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = (networkState as NetworkBookUiState.Error).message)
            }
        }
        is NetworkBookUiState.Success -> {
            val books = (networkState as NetworkBookUiState.Success).books
            //Find the corresponding book based on recId.
            val book = recId?.let { id -> books.find { it.id == id } }
            if (book == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Recommendation not found")
                }
            } else {
                RecDetailContent(book = book, viewModel = viewModel, navController = navController)
            }
        }
    }
}

@Composable
fun RecDetailContent(book: Book, viewModel: BookViewModel, navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(50.dp)) }
        item {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                RecDetailCard(book = book)
            }
        }
        item {
            RecDetailInformation(book = book)
        }
        item {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val localImagePath = downloadImage(book.image, context)
                        if (localImagePath != null) {
                            val dbBookToAdd = dbBook(
                                image = localImagePath,
                                name = book.name,
                                author = book.author,
                                category = book.category,
                                readPages = 0,
                                totalPages = book.totalPages.toIntOrNull() ?: 0,
                                progress = 0,
                                rating = 0,
                                criticalPoints = emptyList(),
                                cpPage = 0,
                            )
                            viewModel.addBook(dbBookToAdd)
                            Toast.makeText(context, "Book added to MyLibrary successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate(routes.Library.name)
                        } else {
                            Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Add to MyLibrary")
            }
        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
    }
}

@Composable
fun RecDetailCard(book: Book) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(190.dp)
            .height(360.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
            ) {
                AsyncImage(
                    model = book.image,
                    contentDescription = "Book Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RecDetailInformation(book: Book) {
    val infoContainerHeight = LocalConfiguration.current.screenHeightDp.dp * 0.045f
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                "Author:",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                modifier = Modifier.alignByBaseline()
            )
            Text(
                book.author,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .alignByBaseline()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                "Category:",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                modifier = Modifier.alignByBaseline()
            )
            Text(
                book.category,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .alignByBaseline()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                "Recommend based on:",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                modifier = Modifier.alignByBaseline()
            )
            Text(
                book.baseon ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                modifier = Modifier
                    .padding(start = 5.dp, bottom = 5.dp)
                    .alignByBaseline()
            )
        }
        Divider(thickness = 2.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Reasons:", style = MaterialTheme.typography.headlineLarge)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = book.reason ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
            )
        }
    }
}

suspend fun downloadImage(imageUrl: String, context: Context): String? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            val filename = "downloaded_${System.currentTimeMillis()}.jpg"
            val file = File(context.cacheDir, filename)
            val output = FileOutputStream(file)
            input.copyTo(output)
            output.close()
            input.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
