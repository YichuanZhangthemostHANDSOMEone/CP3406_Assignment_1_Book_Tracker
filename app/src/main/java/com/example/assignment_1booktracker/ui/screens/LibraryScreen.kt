package com.example.assignment_1booktracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
    //Search status: Search text, whether to display the floating layer, whether the search button has been clicked.
    var searchText by remember { mutableStateOf("") }
    var isSearchOverlayVisible by remember { mutableStateOf(false) }
    var searchPerformed by remember { mutableStateOf(false) }

    val dbState by viewModel.dbUiState.collectAsState()
    val allBooks = when (dbState) {
        is DbBookUiState.Success -> (dbState as DbBookUiState.Success).books
        else -> emptyList()
    }
    val filteredBooks = filterBooks(allBooks, searchText)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = navController.currentBackStackEntry?.destination?.route
            )
        },
        floatingActionButton = {
            SmallButton {
                navController.navigate(routes.AddBook.name)
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            //Keep the display categorized by category.
            when (dbState) {
                is DbBookUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is DbBookUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = (dbState as DbBookUiState.Error).message)
                    }
                }
                is DbBookUiState.Success -> {
                    val groupedBooks: Map<String, List<dbBook>> = allBooks.groupBy { it.category }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {

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
                        groupedBooks.forEach { (category, booksInCategory) ->
                            item {
                                Text(
                                    text = category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 0.dp)
                                )
                            }
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
                        item{Spacer(modifier = Modifier.height(50.dp))}
                    }
                }
            }

            if (!isSearchOverlayVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = { isSearchOverlayVisible = true },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "展开搜索"
                        )
                    }
                }
            }

            if (isSearchOverlayVisible) {
                SearchOverlay(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    searchPerformed = searchPerformed,
                    onSearchButtonClick = {
                        if (searchText.isNotEmpty()) {
                            searchPerformed = true
                        }
                    },
                    filteredBooks = filteredBooks,
                    onDismiss = {
                        isSearchOverlayVisible = false
                        searchText = ""
                        searchPerformed = false
                    },
                    navController = navController,
                    onSuggestionClicked = { book ->
                        navController.navigate("${routes.BookDetails.name}/${book.id}")
                        // 点击建议后收回搜索悬浮层
                        isSearchOverlayVisible = false
                        searchText = ""
                        searchPerformed = false
                    }
                )
            }
        }
    }
}


fun filterBooks(books: List<dbBook>, query: String): List<dbBook> {
    val lowerQuery = query.lowercase().trim()
    if (lowerQuery == "recent added") {
        return books.sortedByDescending { it.id ?: 0 }.take(5)
    }
    val matched = books.filter { book ->
        book.name.lowercase().contains(lowerQuery) ||
                book.author.lowercase().contains(lowerQuery) ||
                book.category.lowercase().contains(lowerQuery) ||
                (book.criticalPoints?.any { cp ->
                    cp.text.lowercase().contains(lowerQuery)
                } ?: false)
    }
    if (matched.isNotEmpty()) {
        return matched
    }
    return when {
        lowerQuery.contains("unfinish") -> books.filter { (it.progress ?: 0) in 1..99 }
        lowerQuery.contains("unread") -> books.filter { (it.progress ?: 0) == 0 }
        lowerQuery.contains("finish") -> books.filter { (it.progress ?: 0) == 100 }
        else -> emptyList()
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
                navController.navigate("${routes.BookDetails.name}/${uiBook.id}")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                Text(
                    text = uiBook.leftText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = uiBook.rightText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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


@Composable
fun SearchOverlay(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    searchPerformed: Boolean,
    onSearchButtonClick: () -> Unit,
    filteredBooks: List<dbBook>,
    onDismiss: () -> Unit,
    navController: NavController,
    onSuggestionClicked: (dbBook) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable { onDismiss() }
    ) {
        val verticalOffset by animateDpAsState(
            targetValue = 60.dp,
            animationSpec = tween(durationMillis = 500)
        )
        AnimatedVisibility(visible = true) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = verticalOffset)
                    .padding(horizontal = 16.dp)
                    .clickable(enabled = false) { },
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    // 标签区域
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "TAGS:", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(8.dp))
                        TagButton(label = "recent added") { onSearchTextChange("recent added") }
                        Spacer(modifier = Modifier.width(4.dp))
                        TagButton(label = "unfinish") { onSearchTextChange("unfinish") }
                        Spacer(modifier = Modifier.width(4.dp))
                        TagButton(label = "unread") { onSearchTextChange("unread") }
                        Spacer(modifier = Modifier.width(4.dp))
                        TagButton(label = "finish") { onSearchTextChange("finish") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // 搜索输入行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = searchText,
                            onValueChange = onSearchTextChange,
                            placeholder = { Text("Search by book name, author, category, critical points or preset tags") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onSearchButtonClick) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "search"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // 展示搜索结果区域
                    if (searchText.isNotEmpty()) {
                        if (filteredBooks.isEmpty()) {
                            // 当无匹配结果时显示提示文本
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No matched book found")
                            }
                        } else {
                            if (!searchPerformed) {
                                LazyColumn {
                                    items(filteredBooks) { book ->
                                        Text(
                                            text = book.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onSuggestionClicked(book) }
                                                .padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            } else {
                                //After clicking the search button: The search results are displayed in two-column card format.
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 500.dp),
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    items(filteredBooks) { book ->
                                        val uiBook = UIBook(
                                            id = book.id ?: 0,
                                            imageUrl = book.image,
                                            leftText = book.name,
                                            rightText = ""
                                        )
                                        LibraryCardView(
                                            uiBook = uiBook,
                                            navController = navController,
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .fillMaxWidth()
                                        )
                                    }
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
fun TagButton(label: String, onTagClick: () -> Unit) {
    TextButton(
        onClick = onTagClick,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label)
    }
}
