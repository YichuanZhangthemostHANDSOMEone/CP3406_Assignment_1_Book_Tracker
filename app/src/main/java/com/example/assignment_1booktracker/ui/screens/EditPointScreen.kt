package com.example.assignment_1booktracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment_1booktracker.ui.uiModels.CriticalPoint
import com.example.assignment_1booktracker.ui.uiModels.toUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPointScreen(
    navController: NavController,
    bookId: Int,
    pointId: Int,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    // 通过观察 dbUiState 来获取当前书本数据（异步加载）
    val dbState by viewModel.dbUiState.collectAsState()
    val book = (dbState as? DbBookUiState.Success)?.books?.find { it.id == bookId }
    // 从获取到的书本中查找对应 critical point，并转换为 UI 模型
    val existingPointUi = book?.criticalPoints?.find { it.id == pointId }?.toUiModel()

    // 使用可变状态保存输入框内容，初始值为空
    var pointText by remember { mutableStateOf("") }
    var pageText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // 当 existingPointUi 更新后，更新输入框的初始内容
    LaunchedEffect(existingPointUi) {
        if (existingPointUi != null) {
            pointText = existingPointUi.text
            pageText = existingPointUi.page.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Critical Point") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 输入框加载原始 critical point 文本，可编辑
            OutlinedTextField(
                value = pointText,
                onValueChange = { pointText = it },
                label = { Text("Critical Point") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // 输入框加载原始页码，可编辑
            OutlinedTextField(
                value = pageText,
                onValueChange = { pageText = it },
                label = { Text("Page (integer > 0)") },
                modifier = Modifier.fillMaxWidth()
            )
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // 校验：critical point 文本不得超过 5 个单词
                    val wordCount = pointText.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
                    val pageNumber = pageText.toIntOrNull()
                    if (wordCount > 5) {
                        errorMessage = "Critical point must not exceed 5 words."
                        return@Button
                    }
                    if (pageNumber == null || pageNumber <= 0) {
                        errorMessage = "Page must be an integer greater than 0."
                        return@Button
                    }
                    // 增加校验：输入页数不能大于书本 totalPages
                    if (book != null && pageNumber > book.totalPages) {
                        errorMessage = "Page can't be greater than total pages (${book.totalPages})"
                        return@Button
                    }
                    // 构造更新后的 CriticalPoint UI 模型
                    val updatedPointUi: CriticalPoint? = existingPointUi?.copy(
                        text = pointText,
                        page = pageNumber
                    )
                    if (updatedPointUi != null) {
                        viewModel.updateCriticalPoint(bookId, updatedPointUi)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }
        }
    }
}
