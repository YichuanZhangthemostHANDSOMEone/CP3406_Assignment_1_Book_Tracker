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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 确保 enableEdgeToEdge() 方法存在
        // enableEdgeToEdge()
        setContent {
            Assignment_1BookTrackerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    BookTracking(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BookDetails() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            PercentageCard()
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            RatingView()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingView(){
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp

    // 定义评分输入框的状态
    var ratingInput by remember { mutableStateOf("") }

    // 此 Box 占据父布局中 15% 的高度
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // "Rating:" 文本，加粗显示
            Text(
                text = "Rating:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // 输入框，不需要特别的下划线样式，满足输入需求即可
            TextField(
                value = ratingInput,
                onValueChange = { ratingInput = it },
                modifier = Modifier.width(40.dp).height(15.dp),
                colors = TextFieldDefaults.textFieldColors(
                    // 设置背景透明，让 TextField 看起来只有下划线
                    containerColor = Color.Transparent,
                    // 可聚焦状态下下划线颜色
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    // 未聚焦状态下下划线颜色
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
            // 固定文本 "/10"
            Text(text = "/10")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CommentPreview() {
    Assignment_1BookTrackerTheme {
        BookDetails()
    }
}