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
                    //Use "remember" to store the initialization state.
                    var isInitialized by remember { mutableStateOf(false) }

                    //Use LaunchedEffect and switch to the IO scheduler to execute time-consuming operations
                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            SharedBookData.initialize(application)
                        }
                        isInitialized = true
                    }

                    //Once the data loading is completed, the main interface will be displayed; otherwise, the LoadingScreen will be shown.
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
