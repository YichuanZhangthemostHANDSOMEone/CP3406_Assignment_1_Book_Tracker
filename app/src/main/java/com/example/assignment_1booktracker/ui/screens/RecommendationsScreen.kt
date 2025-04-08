package com.example.assignment_1booktracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment_1booktracker.routes.routes
import com.example.assignment_1booktracker.ui.uiModels.BottomNavigationBar
import com.example.assignment_1booktracker.ui.uiModels.UIBook
import com.example.assignment_1booktracker.ui.uiModels.getImagePainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import coil3.compose.AsyncImage

@Composable
fun RecommendationsScreen(
    navController: NavController,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = navController.currentBackStackEntry?.destination?.route
            )
        }
    ) { paddingValues ->
        val networkState by viewModel.networkUiState.collectAsState()
        when (networkState) {
            is NetworkBookUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is NetworkBookUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = (networkState as NetworkBookUiState.Error).message)
                }
            }
            is NetworkBookUiState.Success -> {
                val recommendedBooks = (networkState as NetworkBookUiState.Success).books
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        top = 8.dp + paddingValues.calculateTopPadding(),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                        bottom = 8.dp + paddingValues.calculateBottomPadding()
                    )
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Recommendations",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    // 将推荐书籍按两列展示
                    items(recommendedBooks.chunked(2)) { rowBooks ->
                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            rowBooks.forEach { book ->
                                val uiBook = UIBook(
                                    id = book.id,
                                    imageUrl = book.image,
                                    leftText = book.name,
                                    rightText = "Rate: ${book.rate}"
                                )
                                BookCard(
                                    book = uiBook,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            navController.navigate("${routes.RecDetails.name}/${book.id}")
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookCard(
    book: UIBook,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .width(190.dp)
            .height(300.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
            ) {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = "Book Card Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = book.leftText, style = MaterialTheme.typography.bodyMedium)
                Text(text = book.rightText, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
