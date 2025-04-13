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
    //Obtain the current book data (asynchronously loaded) by observing dbUiState
    val dbState by viewModel.dbUiState.collectAsState()
    val book = (dbState as? DbBookUiState.Success)?.books?.find { it.id == bookId }
    //Search for the corresponding critical point from the obtained books and convert them into UI models.
    val existingPointUi = book?.criticalPoints?.find { it.id == pointId }?.toUiModel()

    var pointText by remember { mutableStateOf("") }
    var pageText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    //When the existingPointUi is updated, update the initial content of the input box.
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
                    //error checking
                    if (pointText.isBlank()) {
                        errorMessage = "Critical point cannot be empty."
                        return@Button
                    }
                    if (pageText.isBlank()) {
                        errorMessage = "Page cannot be empty."
                        return@Button
                    }
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
                    if (book != null && pageNumber > book.totalPages) {
                        errorMessage = "Page can't be greater than total pages (${book.totalPages})"
                        return@Button
                    }
                    //Construct the updated CriticalPoint UI model
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
