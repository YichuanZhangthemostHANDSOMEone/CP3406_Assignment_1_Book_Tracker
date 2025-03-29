package com.example.assignment_1booktracker.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.assignment_1booktracker.ui.theme.Assignment_1BookTrackerTheme
import com.example.assignment_1booktracker.R
import com.example.assignment_1booktracker.routes.routes
import com.example.assignment_1booktracker.ui.uiModels.BottomNavigationBar
import com.example.assignment_1booktracker.ui.uiModels.UIBook
import com.example.assignment_1booktracker.ui.uiModels.getImagePainter

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
        val uiState by viewModel.uiState.collectAsState()
        when (uiState) {
            is BookUiState.Loading -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is BookUiState.Error -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = (uiState as BookUiState.Error).message)
                }
            }
            is BookUiState.Success -> {
                val recommendedBooks = (uiState as BookUiState.Success).books
                LazyColumn(
                    contentPadding = PaddingValues(
                        paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        8.dp + paddingValues.calculateTopPadding(),
                        paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                        8.dp + paddingValues.calculateBottomPadding()
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
                    model = book.imageUrl, // 改用 URL 或资源 ID
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
