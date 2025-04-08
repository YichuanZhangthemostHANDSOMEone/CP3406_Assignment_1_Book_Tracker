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
    // 从 ViewModel 获取当前书籍数据（书籍中 criticalPoints 为 List<DataCriticalPoint>）
    val book = viewModel.getDbBookById(bookId)
    // 查找对应的关键点，并转换为 UI 模型 CriticalPoint
    val existingPointUi = book?.criticalPoints?.find { it.id == pointId }?.toUiModel()
    // 初始化文本输入框的状态，若不存在则为空字符串
    var pointText by remember { mutableStateOf(existingPointUi?.text ?: "") }
    var pageText by remember { mutableStateOf(existingPointUi?.page?.toString() ?: "") }
    var errorMessage by remember { mutableStateOf("") }

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
            OutlinedTextField(
                value = pointText,
                onValueChange = { pointText = it },
                label = { Text("Critical Point") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
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
                    // 校验关键点文本的单词数不得超过5个
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
                    // 更新 UI 模型数据，注意：existingPointUi 为 UI 模型 CriticalPoint
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
