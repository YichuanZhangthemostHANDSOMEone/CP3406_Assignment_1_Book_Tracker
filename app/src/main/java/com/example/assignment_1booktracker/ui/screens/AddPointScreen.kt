package com.example.assignment_1booktracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPointScreen(
    navController: NavController,
    bookId: Int,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    var criticalPointInput by remember { mutableStateOf("") }
    var cpPageInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Critical Point") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = criticalPointInput,
                onValueChange = { criticalPointInput = it },
                label = { Text("Critical Point (two words)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = cpPageInput,
                onValueChange = { cpPageInput = it },
                label = { Text("Point Page") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val cpPage = cpPageInput.toIntOrNull() ?: 0
                    viewModel.updateBookPoints(bookId, criticalPointInput, cpPage)
                    navController.popBackStack()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Submit")
            }
        }
    }
}