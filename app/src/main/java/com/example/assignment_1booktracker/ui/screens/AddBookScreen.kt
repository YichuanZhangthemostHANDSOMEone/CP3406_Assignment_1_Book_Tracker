package com.example.assignment_1booktracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AddBookScreen(
    navController: NavController,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    // 目前仅修改数据源，不改变 UI 布局，预留调用添加书籍接口
    val bookName = remember { mutableStateOf("") }
    val author = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val totalPages = remember { mutableStateOf("") }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Image",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        InputField(
            label = "Book Name:",
            value = bookName.value,
            onValueChange = { bookName.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.1f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        InputField(
            label = "Author:",
            value = author.value,
            onValueChange = { author.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.1f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        InputField(
            label = "Category:",
            value = category.value,
            onValueChange = { category.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.1f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        InputField(
            label = "Total Pages:",
            value = totalPages.value,
            onValueChange = { totalPages.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.1f)
        )
        Spacer(modifier = Modifier.weight(1f))
        ElevatedButton(
            onClick = {
                // 预留调用 viewModel.addBook() 等添加书籍逻辑接口，后续接入数据层即可
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Add Book")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.Gray,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
