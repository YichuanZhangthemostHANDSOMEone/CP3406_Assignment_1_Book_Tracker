package com.example.assignment_1booktracker.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.safe.args.generator.ErrorMessage
import com.example.assignment_1booktracker.routes.routes
import android.os.Bundle
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.assignment_1booktracker.ui.theme.Assignment_1BookTrackerTheme
import com.example.assignment_1booktracker.R
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
        }
    ) { paddingValues ->
        // 将原有的 UI 内容加上 paddingValues 以避免被底部导航栏遮挡
        val uiState by viewModel.uiState.collectAsState()

        when (uiState) {
            is BookUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is BookUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = (uiState as BookUiState.Error).message)
                }
            }
            is BookUiState.Success -> {
                val books = (uiState as BookUiState.Success).books
                val booksByCategory = books.groupBy { it.category }
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
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    booksByCategory.forEach { (category, booksInCategory) ->
                        item {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                            )
//                            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
//                                items(booksInCategory) { book ->
//                                    val uiBook = UIBook(
//                                        image = getImagePainter(book.image),
//                                        leftText = book.name,
//                                        rightText = book.author
//                                    )
//                                    CardView(
//                                        book = uiBook,
//                                        onClick = {
//                                            navController.navigate("BookDetails/${book.id}")
//                                        }
//                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
//    }
//}

//@Composable
//fun BottomNavigationBar(navController: NavController) {
//    val items = listOf("Library", "Recommendations")
//    var selectedIndex by remember { mutableStateOf(0) }
//
//    NavigationBar(
//        containerColor = MaterialTheme.colorScheme.primaryContainer,
//        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//    ) {
//        items.forEachIndexed { index, item ->
//            NavigationBarItem(
//                icon = {
//                    Icon(
//                        imageVector = if (index == 0) Icons.Filled.Home else Icons.Filled.Favorite,
//                        contentDescription = item
//                    )
//                },
//                label = { Text(text = item) },
//                selected = selectedIndex == index,
//                onClick = {
//                    selectedIndex = index
//                    when (index) {
//                        0 -> navController.navigate("Library")
//                        1 -> navController.navigate("Recommendations")
//                    }
//                }
//            )
//        }
//    }
//}


@Composable
fun SmallButton(onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(Icons.Filled.Add, "Small floating action button.")
    }
}
