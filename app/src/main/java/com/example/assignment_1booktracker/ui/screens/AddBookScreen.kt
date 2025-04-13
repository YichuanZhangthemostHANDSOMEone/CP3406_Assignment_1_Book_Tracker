package com.example.assignment_1booktracker.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.assignment_1booktracker.data.dbBook

@Composable
fun AddBookScreen(
    navController: NavController,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    val context = LocalContext.current


    val bookName = remember { mutableStateOf("") }
    val author = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val totalPages = remember { mutableStateOf("") }


    val imageUri = remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imageUri.value = it.toString() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 预留摄像头区域的空间
        Spacer(modifier = Modifier.height(50.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                // 只有当图片未选择时，才允许点击选择图片
                .clickable(enabled = imageUri.value == null) {
                    launcher.launch("image/*")
                },
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // 显示用户选择的图片或添加图标
                if (imageUri.value != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri.value),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize()
                    )
                    // 增加一个位于右上角的清除按钮，用于清除当前选择的图片
                    IconButton(
                        onClick = { imageUri.value = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Image"
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Image",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))


        InputField(
            label = "Book Name:",
            value = bookName.value,
            onValueChange = { bookName.value = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))


        InputField(
            label = "Author:",
            value = author.value,
            onValueChange = { author.value = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))


        InputField(
            label = "Category:",
            value = category.value,
            onValueChange = { category.value = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))


        InputField(
            label = "Total Pages:",
            value = totalPages.value,
            onValueChange = { totalPages.value = it },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.weight(1f))

        ElevatedButton(
            onClick = {
                val trimmedBookName = bookName.value.trim()
                val trimmedAuthor = author.value.trim()
                val trimmedCategory = category.value.trim()
                val total = totalPages.value.trim().toIntOrNull() ?: 0

                if (trimmedBookName.isNotEmpty() &&
                    trimmedAuthor.isNotEmpty() &&
                    trimmedCategory.isNotEmpty() &&
                    total > 0 &&
                    imageUri.value != null
                ) {
                    val newBook = dbBook(
                        image = imageUri.value!!,
                        name = trimmedBookName,
                        author = trimmedAuthor,
                        category = trimmedCategory,
                        totalPages = total
                    )
                    viewModel.addBook(newBook)
                    Toast.makeText(context, "Book added successfully", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    if (imageUri.value == null) {
                        Toast.makeText(context, "Please select a cover image", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please complete all information, total page must be greater than 0", Toast.LENGTH_SHORT).show()
                    }
                }
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
                containerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}
