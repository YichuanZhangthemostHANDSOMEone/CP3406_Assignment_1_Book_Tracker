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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment_1BookTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // 控制数据是否加载完成
                    val isInitialized = remember { mutableStateOf(false) }
                    val coroutineScope = rememberCoroutineScope()

                    // 异步初始化 SharedBookData（加载书籍数据）
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            SharedBookData.initialize(application)
                            isInitialized.value = true
                        }
                    }

                    // 数据加载完成后显示主界面，否则显示 LoadingScreen
                    if (isInitialized.value) {
                        // 使用导航组件组织各个 Screen
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
