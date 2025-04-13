package com.example.assignment_1booktracker.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment_1booktracker.data.dbBook
import com.example.assignment_1booktracker.ui.uiModels.CriticalPointCard
import com.example.assignment_1booktracker.ui.uiModels.toUiModel

@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: Int,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    val context = LocalContext.current
    val dbState by viewModel.dbUiState.collectAsState()
    var currentBook by remember { mutableStateOf<dbBook?>(null) }

    LaunchedEffect(bookId, dbState) {
        currentBook = when (dbState) {
            is DbBookUiState.Success -> {
                (dbState as DbBookUiState.Success).books.find { it.id == bookId }
            }
            else -> null
        }
    }

    if (currentBook == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        BookDetailContent(
            book = currentBook!!,
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
fun BookDetailContent(book: dbBook, navController: NavController, viewModel: BookViewModel) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = book.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    maxLines = 2
                )
                IconButton(
                    onClick = {
                        viewModel.deleteBook(book.id!!)
                        Toast.makeText(context, "Book deleted successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Book",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            PercentageCard(
                book = book,
                onUpdate = { pages ->
                    viewModel.updateReadPages(book.id!!, pages)
                    Toast.makeText(context, "Progress updated successfully", Toast.LENGTH_SHORT).show()
                }
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            RatingView(
                book = book,
                onUpdate = { rating ->
                    viewModel.updateRating(book.id!!, rating)
                    Toast.makeText(context, "Rating updated successfully", Toast.LENGTH_SHORT).show()
                }
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { Divider(thickness = 1.dp) }
        item {
            CriticalPointsSection(book = book, navController = navController, viewModel = viewModel)
        }
        item { Divider(thickness = 1.dp) }
        item {
            PersonalReviewNew(book = book, onUpdate = { review ->
                viewModel.updateReview(book.id!!, review)
                Toast.makeText(context, "Review updated successfully", Toast.LENGTH_SHORT).show()
            })
        }
    }
}

@Composable
fun PercentageCard(book: dbBook, onUpdate: (Int) -> Unit) {
    val totalPages = if (book.totalPages <= 0) 1 else book.totalPages
    var readPagesInput by remember { mutableStateOf(book.readPages?.toString() ?: "") }
    var errorMessage by remember { mutableStateOf("") }
    val currentPages = readPagesInput.toIntOrNull() ?: 0
    val animatedProgress by animateFloatAsState(targetValue = currentPages.toFloat() / totalPages)

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TextField(
                        value = readPagesInput,
                        onValueChange = {
                            readPagesInput = it
                            errorMessage = ""
                        },
                        label = { Text("Read Pages") },
                        isError = errorMessage.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Total Pages: $totalPages",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val pages = readPagesInput.toIntOrNull() ?: 0
                    if (pages < 0) {
                        errorMessage = "Please input integer > 0"
                        return@Button
                    }
                    if (pages > totalPages) {
                        errorMessage = "Page can't be greater than total pages ($totalPages)"
                        return@Button
                    }
                    onUpdate(pages)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Update Progress")
            }
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun RatingView(book: dbBook, onUpdate: (Int) -> Unit) {
    var rating by remember { mutableStateOf((book.rating ?: 0).coerceIn(0, 10)) }

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Rating：", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(8.dp))
            Slider(
                value = rating.toFloat(),
                onValueChange = { rating = it.toInt() },
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "$rating/10",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = { onUpdate(rating) }) {
                Text("Save Rating")
            }
        }
    }
}

@Composable
fun CriticalPointsSection(book: dbBook, navController: NavController, viewModel: BookViewModel) {
    val criticalPoints = book.criticalPoints ?: emptyList()
    val context = LocalContext.current

    //Monitor changes in the deletion status
    var deleteMessageShown by remember { mutableStateOf(false) }

    LaunchedEffect(deleteMessageShown) {
        if (deleteMessageShown) {
            Toast.makeText(context, "Critical point deleted successfully", Toast.LENGTH_SHORT).show()
            deleteMessageShown = false
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Critical Points",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { navController.navigate("AddPoint/${book.id}") }) {
                Text("Add Point")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        criticalPoints.forEach { cp ->
            CriticalPointCard(
                criticalPoint = cp.toUiModel(),
                onDelete = {
                    viewModel.deleteCriticalPoint(book.id!!, cp.id)
                    deleteMessageShown = true // 触发 Toast 显示
                },
                onEdit = { navController.navigate("EditPoint/${book.id}/${cp.id}") }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalReviewNew(book: dbBook, onUpdate: (String) -> Unit) {
    var showEditor by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf(book.review ?: "") }
    Spacer(modifier = Modifier.height(8.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Review",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showEditor = true }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Review"
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (reviewText.isEmpty()) "Tap the edit button to add your review." else reviewText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
    if (showEditor) {
        ModalBottomSheet(onDismissRequest = { showEditor = false }) {
            val context = LocalContext.current
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Edit Your Review",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    placeholder = { Text("Enter your review here...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showEditor = false }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onUpdate(reviewText)
                        Toast.makeText(context, "Review updated successfully", Toast.LENGTH_SHORT).show()
                        showEditor = false
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
