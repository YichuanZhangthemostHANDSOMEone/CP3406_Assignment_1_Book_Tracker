package com.example.assignment_1booktracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment_1booktracker.ui.uiModels.BottomNavigationBar
import com.example.assignment_1booktracker.ui.uiModels.UIBook
import com.example.assignment_1booktracker.ui.uiModels.getImagePainter

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = navController.currentBackStackEntry?.destination?.route
            )
        },
        floatingActionButton = {
            SmallButton {
                // 点击小按钮时跳转到 AddBookScreen
                navController.navigate("AddBook")
            }
        }
    ) { paddingValues ->
        val dbState by viewModel.dbUiState.collectAsState()

        when (dbState) {
            is DbBookUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DbBookUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = (dbState as DbBookUiState.Error).message)
                }
            }
            is DbBookUiState.Success -> {
                val books = (dbState as DbBookUiState.Success).books
                val uiBooks = books.map {
                    UIBook(
                        imageUrl = it.image,
                        leftText = it.name,
                        rightText = "${it.progress}%"
                    )
                }
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "My Library",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                    items(uiBooks) { book ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    // 可在此处传递书籍 id 跳转到详情页
                                },
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = getImagePainter(book.imageUrl),
                                    contentDescription = "Book Cover",
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = book.leftText, style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Progress: ${book.rightText}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmallButton(onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add Book")
    }
}
