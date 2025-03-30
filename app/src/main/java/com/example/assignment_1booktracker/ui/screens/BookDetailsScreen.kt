package com.example.assignment_1booktracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment_1booktracker.data.dbBook
import com.example.assignment_1booktracker.ui.uiModels.MarkedPoints
@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: Int,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    // 这里直接通过 ViewModel 的 getDbBookById 获取数据库中的书籍数据
    val book = viewModel.getDbBookById(bookId)
    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading book details...")
        }
    } else {
        BookDetailContent(book = book, navController = navController, viewModel = viewModel)
    }
}

@Composable
fun BookDetailContent(book: dbBook, navController: NavController, viewModel: BookViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item { PercentageCard(book = book, viewModel = viewModel) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { RatingView(book = book, viewModel = viewModel) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            MarkedPoints(
                book = book,
                onDelete = { viewModel.deleteBook(book.id) },
                onAddPoint = { navController.navigate("AddPoint/${book.id}") }
            )
        }
        item { Spacer(modifier = Modifier.height(35.dp)) }
        item { PersonalReview(book = book, viewModel = viewModel) }
    }
}

@Composable
fun PercentageCard(book: dbBook, viewModel: BookViewModel) {
    val configuration = LocalConfiguration.current
    val cardHeight = (configuration.screenHeightDp * 0.2f).dp

    var readPagesInput by remember { mutableStateOf(book.readPages.toString()) }
    val totalPagesText = book.totalPages.toString()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = readPagesInput,
                        onValueChange = { readPagesInput = it },
                        label = { Text("Read pages") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = totalPagesText,
                        onValueChange = {},
                        label = { Text("Total pages") },
                        enabled = false,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val readPages = readPagesInput.toIntOrNull() ?: 0
                        viewModel.updateReadPages(book.id, readPages)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Update Progress")
                }
                LinearProgressIndicator(
                    progress = (book.progress / 100f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun RatingView(book: dbBook, viewModel: BookViewModel) {
    var ratingInput by remember { mutableStateOf(book.rating.toString()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Rating:",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = ratingInput,
            onValueChange = { ratingInput = it },
            modifier = Modifier.width(60.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "/10")
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {
            val rating = ratingInput.toIntOrNull() ?: 0
            viewModel.updateRating(book.id, rating)
        }) {
            Text("Update Rating")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalReview(book: dbBook, viewModel: BookViewModel) {
    var reviewInput by remember { mutableStateOf(book.review ?: "") }
    var showSheet by remember { mutableStateOf(false) }

    Column {
        if (reviewInput.isNotBlank()) {
            Text("Review: $reviewInput", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(onClick = { showSheet = true }) {
            Text("Add/Edit Review")
        }
    }
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TextField(
                    value = reviewInput,
                    onValueChange = { reviewInput = it },
                    label = { Text("Your Review") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.updateReview(book.id, reviewInput)
                        showSheet = false
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Submit")
                }
            }
        }
    }
}


