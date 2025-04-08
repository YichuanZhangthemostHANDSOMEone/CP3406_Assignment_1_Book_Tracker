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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    // 监听数据库状态变化
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // 顶部预留安全区域
            .padding(16.dp)
    ) {
        // 顶部区域：居中显示书名，右上角显示删除按钮
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
                        navController.popBackStack()
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Book",
                        tint = MaterialTheme.colorScheme.error // 红色
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        // PercentageCard：显示书的进度和已读页数
        item {
            PercentageCard(
                book = book,
                onUpdate = { pages ->
                    viewModel.updateReadPages(book.id!!, pages)
                }
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        // Rating区域
        item {
            RatingView(
                book = book,
                onUpdate = { rating ->
                    viewModel.updateRating(book.id!!, rating)
                }
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        // 分隔线：Rating和Critical Points之间
        item {
            Divider(thickness = 1.dp)
        }
        // Critical Points部分
        item {
            CriticalPointsSection(book = book, navController = navController, viewModel = viewModel)
        }
        // 分隔线：Critical Points和Review之间
        item {
            Divider(thickness = 1.dp)
        }
        // 重新设计的书评区域
        item {
            PersonalReviewNew(book = book, onUpdate = { review ->
                viewModel.updateReview(book.id!!, review)
            })
        }
    }
}

@Composable
fun PercentageCard(book: dbBook, onUpdate: (Int) -> Unit) {
    val totalPages = if (book.totalPages <= 0) 1 else book.totalPages
    // 使用字符串状态，方便直接在 TextField 中编辑
    var readPagesInput by remember { mutableStateOf(book.readPages?.toString() ?: "") }
    var inputError by remember { mutableStateOf(false) }
    // 将输入的数字转换为进度值
    val currentPages = readPagesInput.toIntOrNull() ?: 0
    val animatedProgress by animateFloatAsState(targetValue = currentPages.toFloat() / totalPages)

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
                            val parsed = it.toIntOrNull() ?: 0
                            inputError = parsed <= 0
                        },
                        label = { Text("已读页数") },
                        isError = inputError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (inputError) {
                        Text(
                            "请输入大于0的整数",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "总页数: $totalPages",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val pages = readPagesInput.toIntOrNull() ?: 0
                    if (!inputError && pages > 0) {
                        onUpdate(pages)
                    }
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
            Text("评分：", style = MaterialTheme.typography.titleLarge)
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
                Text("保存评分")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriticalPointsSection(book: dbBook, navController: NavController, viewModel: BookViewModel) {
    val criticalPoints = book.criticalPoints ?: emptyList()
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
        // 遍历显示每个关键点的 Card，并转换为 UI 模型
        criticalPoints.forEach { cp ->
            CriticalPointCard(
                criticalPoint = cp.toUiModel(),
                onDelete = { viewModel.deleteCriticalPoint(book.id!!, cp.id) },
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
                        showEditor = false
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
