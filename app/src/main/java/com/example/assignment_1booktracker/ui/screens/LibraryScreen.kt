package com.example.assignment_1booktracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.assignment_1booktracker.data.dbBook
import com.example.assignment_1booktracker.routes.routes
import com.example.assignment_1booktracker.ui.uiModels.BottomNavigationBar
import com.example.assignment_1booktracker.ui.uiModels.UIBook
import com.example.assignment_1booktracker.ui.uiModels.getImagePainter

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: BookViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = BookViewModel.Factory)
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
                // 跳转到添加书籍页面
                navController.navigate(routes.AddBook.name)
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
                // 根据 category 分组
                val groupedBooks: Map<String, List<dbBook>> = books.groupBy { it.category }
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // 整体标题区域
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "My Library",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                    // 遍历每个 category 分组
                    groupedBooks.forEach { (category, booksInCategory) ->
                        // 显示 category 标题
                        item {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 0.dp)
                            )
                        }
                        // 显示该 category 下的书籍卡片
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                items(booksInCategory) { book ->
                                    val uiBook = UIBook(
                                        id = book.id!!,
                                        imageUrl = book.image,
                                        leftText = book.name,
                                        rightText = "${book.progress ?: 0}%"
                                    )
                                    LibraryCardView(uiBook = uiBook, navController = navController)
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
fun LibraryCardView(
    uiBook: UIBook,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .width(200.dp)
            .height(300.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            ) {
                // 点击后跳转到 BookDetailsScreen，传入对应的 book id
                navController.navigate("${routes.BookDetails.name}/${uiBook.id}")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 图片区域占比约90%
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
            ) {
                Image(
                    painter = getImagePainter(uiBook.imageUrl),
                    contentDescription = "Book Cover",
                    modifier = Modifier.fillMaxSize()
                )
            }
            // 底部区域显示书名和进度
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = uiBook.leftText, style = MaterialTheme.typography.bodyMedium)
                Text(text = uiBook.rightText, style = MaterialTheme.typography.bodyMedium)
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
