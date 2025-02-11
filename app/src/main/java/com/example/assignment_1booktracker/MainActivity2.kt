package com.example.assignment_1booktracker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignment_1booktracker.ui.theme.Assignment_1BookTrackerTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment_1BookTrackerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ){ innerPadding ->
                    BookTracking()

                }
            }
        }
    }
}

@Composable
fun PercentageCard() {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp

    var leftInput by remember { mutableStateOf("93") }
    var rightInput by remember { mutableStateOf("168") }

    val leftValue = leftInput.toFloatOrNull() ?: 0f
    val rightValue = rightInput.toFloatOrNull() ?: 0f
    val progress = if (rightValue != 0f) (leftValue / rightValue).coerceIn(0f, 1f) else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.15f)
                .align(Alignment.TopCenter)
                .offset(y = (screenHeightDp * 0.05f).dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxWidth()
                ) {
                    TextField(
                        value = leftInput,
                        onValueChange = { leftInput = it },
                        label = { Text("Read pages") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    )
                    VerticalDivider(color = MaterialTheme.colorScheme.secondary)
                    TextField(
                        value = rightInput,
                        onValueChange = { rightInput = it },
                        label = { Text("Total pages") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )
                }
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 10.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommentPreview() {
    Assignment_1BookTrackerTheme {
        PercentageCard()
    }
}