package com.example.assignment_1booktracker.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.assignment_1booktracker.data.dbBook
import com.example.assignment_1booktracker.model.Book
import com.example.assignment_1booktracker.ui.uiModels.getImagePainter
import com.example.assignment_1booktracker.routes.routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun RecDetailsScreen(
    navController: NavController,
    recId: Int?,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    // 观察网络状态
    val networkState by viewModel.networkUiState.collectAsState()
    when (networkState) {
        is NetworkBookUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is NetworkBookUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = (networkState as NetworkBookUiState.Error).message)
            }
        }
        is NetworkBookUiState.Success -> {
            val books = (networkState as NetworkBookUiState.Success).books
            // 根据 recId 查找对应书籍
            val book = recId?.let { id -> books.find { it.id == id } }
            if (book == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Recommendation not found")
                }
            } else {
                // 将 navController 与 viewModel 同时传入 RecDetailContent，
                // 便于在添加后进行跳转
                RecDetailContent(book = book, viewModel = viewModel, navController = navController)
            }
        }
    }
}

@Composable
fun RecDetailContent(book: Book, viewModel: BookViewModel, navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                RecDetailCard(book = book)
            }
        }
        item {
            RecDetailInformation(book = book)
        }
        // 新增按钮项：“Add to MyLibrary”
        item {
            Button(
                onClick = {
                    coroutineScope.launch {
                        // 下载图片并获取本地路径
                        val localImagePath = downloadImage(book.image, context)
                        if (localImagePath != null) {
                            // 将网络 Book 转换为数据库实体 dbBook，
                            // 同时使用下载后的本地图片路径
                            val dbBookToAdd = dbBook(
                                image = localImagePath,
                                name = book.name,
                                author = book.author,
                                category = book.category,
                                readPages = 0,
                                totalPages = book.totalPages.toIntOrNull() ?: 0,
                                progress = 0,
                                rating = 0,
                                criticalPoints = emptyList(),
                                cpPage = 0,
                                review = book.reason
                            )
                            viewModel.addBook(dbBookToAdd)
                            // 添加成功后，跳转到 LibraryScreen 页面
                            navController.navigate(routes.Library.name)
                        } else {
                            // 此处可以添加错误提示
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Add to MyLibrary")
            }
        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
    }
}

@Composable
fun RecDetailCard(book: Book) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(190.dp)
            .height(360.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
            ) {
                AsyncImage(
                    model = book.image,
                    contentDescription = "Book Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RecDetailInformation(book: Book) {
    val infoContainerHeight = LocalConfiguration.current.screenHeightDp.dp * 0.045f
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                "Author:",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                modifier = Modifier.alignByBaseline()
            )
            Text(
                book.author,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .alignByBaseline()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                "Category:",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                modifier = Modifier.alignByBaseline()
            )
            Text(
                book.category,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .alignByBaseline()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                "Recommend based on:",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                modifier = Modifier.alignByBaseline()
            )
            Text(
                book.baseon ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                modifier = Modifier
                    .padding(start = 5.dp, bottom = 5.dp)
                    .alignByBaseline()
            )
        }
        Divider(thickness = 2.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Reasons:", style = MaterialTheme.typography.headlineLarge)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = book.reason ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
            )
        }
    }
}


suspend fun downloadImage(imageUrl: String, context: Context): String? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            // 构造文件名（可根据需要修改为其它目录）
            val filename = "downloaded_${System.currentTimeMillis()}.jpg"
            val file = File(context.cacheDir, filename)
            val output = FileOutputStream(file)
            input.copyTo(output)
            output.close()
            input.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
