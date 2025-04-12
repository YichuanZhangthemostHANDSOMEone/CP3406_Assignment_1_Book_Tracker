package com.example.assignment_1booktracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment_1booktracker.data.dbBook
import com.example.assignment_1booktracker.ui.uiModels.CriticalPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPointScreen(
    navController: NavController,
    bookId: Int,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    val context = LocalContext.current
    var pointText by remember { mutableStateOf("") }
    var pageText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val dbUiState by viewModel.dbUiState.collectAsState()
    val book = when (dbUiState) {
        is DbBookUiState.Success -> (dbUiState as DbBookUiState.Success).books.find { it.id == bookId }
        else -> null
    }

    if (dbUiState is DbBookUiState.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }


    if (book == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Book data not found", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Critical Point") },
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
                label = { Text("Page") },
                modifier = Modifier.fillMaxWidth()
            )
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // 清除之前的错误信息
                    errorMessage = ""

                    // 检查输入是否为空
                    if (pointText.isBlank()) {
                        errorMessage = "Critical point cannot be empty."
                        return@Button
                    }
                    if (pageText.isBlank()) {
                        errorMessage = "Page cannot be empty."
                        return@Button
                    }

                    // 校验单词数、页码格式及范围
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
                    if (pageNumber > book.totalPages) {
                        errorMessage = "Page can't be greater than total pages (${book.totalPages})"
                        return@Button
                    }

                    // 添加新的 CriticalPoint
                    val newPoint = CriticalPoint(id = 0, text = pointText, page = pageNumber)
                    viewModel.addCriticalPoint(bookId, newPoint)
                    Toast.makeText(context, "Critical point added successfully", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }
        }
    }
}