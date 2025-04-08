package com.example.assignment_1booktracker.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            .padding(16.dp)
    ) {
        // 第一部分：删除书籍按钮移至顶部，并留有顶部间距
        item {
            Button(
                onClick = {
                    viewModel.deleteBook(book.id!!)
                    // 删除成功后返回上一界面
                    navController.popBackStack()
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                Text("Delete Book")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        // 第二部分：进度卡
        item {
            PercentageCard(
                book = book,
                onUpdate = { pages ->
                    viewModel.updateReadPages(book.id!!, pages)
                }
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
        // 第三部分：评分卡
        item {
            RatingView(
                book = book,
                onUpdate = { rating ->
                    viewModel.updateRating(book.id!!, rating)
                }
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
        // 第四部分：关键点区域（包括Critical Points标题、Add Point按钮和关键点列表）
        item {
            CriticalPointsSection(book = book, navController = navController, viewModel = viewModel)
        }
        item { Spacer(modifier = Modifier.height(35.dp)) }
        // 第五部分：个人书评
        item {
            PersonalReview(
                book = book,
                onUpdate = { review ->
                    viewModel.updateReview(book.id!!, review)
                }
            )
        }
    }
}

@Composable
fun PercentageCard(book: dbBook, onUpdate: (Int) -> Unit) {
    var readPages by remember { mutableStateOf(book.readPages ?: 0) }
    val totalPages = if (book.totalPages <= 0) 1 else book.totalPages // 防止除0

    // 动画进度值
    val animatedProgress by animateFloatAsState(targetValue = (readPages.toFloat() / totalPages))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = readPages.toString(),
                    onValueChange = {
                        readPages = it.toIntOrNull() ?: 0
                    },
                    label = { Text("已读页数") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "总页数: $totalPages",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    val validPages = readPages.coerceIn(0, totalPages)
                    onUpdate(validPages)
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
fun PersonalReview(book: dbBook, onUpdate: (String) -> Unit) {
    var showEditor by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf(book.review ?: "") }

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            if (reviewText.isNotEmpty()) {
                Text(
                    text = reviewText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Button(
                onClick = { showEditor = true },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (reviewText.isEmpty()) "添加书评" else "编辑书评")
            }
        }
    }

    if (showEditor) {
        ModalBottomSheet(onDismissRequest = { showEditor = false }) {
            Column(Modifier.padding(16.dp)) {
                TextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("输入书评") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { showEditor = false }) {
                        Text("取消")
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(onClick = {
                        onUpdate(reviewText)
                        showEditor = false
                    }) {
                        Text("保存")
                    }
                }
            }
        }
    }
}

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
                "Critical Points",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            // 将 Add Point 按钮移动到右侧，点击后跳转至 AddPointScreen（带动画）
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