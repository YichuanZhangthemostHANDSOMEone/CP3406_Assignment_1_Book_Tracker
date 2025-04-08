package com.example.assignment_1booktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.assignment_1booktracker.data.SharedBookData
import com.example.assignment_1booktracker.ui.BookAppNavigation
import com.example.assignment_1booktracker.ui.theme.Assignment_1BookTrackerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment_1BookTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // 使用 remember 来存储初始化状态
                    var isInitialized by remember { mutableStateOf(false) }

                    // 使用 LaunchedEffect 并切换到 IO 调度器执行耗时操作
                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            SharedBookData.initialize(application)
                        }
                        isInitialized = true
                    }

                    // 数据加载完成后显示主界面，否则显示 LoadingScreen
                    if (isInitialized) {
                        BookAppNavigation(navController = rememberNavController())
                    } else {
                        LoadingScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Loading books...")
        }
    }
}
