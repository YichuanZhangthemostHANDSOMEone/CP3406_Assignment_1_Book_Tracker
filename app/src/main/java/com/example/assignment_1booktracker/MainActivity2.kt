package com.example.assignment_1booktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignment_1booktracker.ui.theme.Assignment_1BookTrackerTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 确保 enableEdgeToEdge() 方法存在
        // enableEdgeToEdge()
        setContent {
            Assignment_1BookTrackerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { PersonalReview() }
                ) { innerPadding ->
                    BookDetails(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class CardContent(
    val story: String,
    val page: String
)

@Composable
fun BookDetails(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            PercentageCard()
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            RatingView()
        }
        item{
            MarkedPoints()
        }
        item {
            Spacer(modifier = Modifier.height(35.dp))
        }
    }
}

@Composable
fun PercentageCard() {
    val configuration = LocalConfiguration.current
    val cardHeight = (configuration.screenHeightDp * 0.2f).dp

    var leftInput by remember { mutableStateOf("93") }
    var rightInput by remember { mutableStateOf("168") }

    val leftValue = leftInput.toFloatOrNull() ?: 0f
    val rightValue = rightInput.toFloatOrNull() ?: 0f
    val progress = if (rightValue != 0f) (leftValue / rightValue).coerceIn(0f, 1f) else 0f

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(cardHeight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = leftInput,
                        onValueChange = { leftInput = it },
                        label = { Text("Read pages") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    VerticalDivider(color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(2.dp))
                    TextField(
                        value = rightInput,
                        onValueChange = { rightInput = it },
                        label = { Text("Total pages") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingView() {
    val configuration = LocalConfiguration.current
    val containerHeight = (configuration.screenHeightDp * 0.05f).dp
    var rating by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.Right
        ) {
            Text(
                text = "Rating:",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = rating,
                onValueChange = { rating = it },
                modifier = Modifier.width(60.dp).height(15.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "/10")
        }
    }
}


@Composable
fun MarkedPoints(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.BottomStart
    ){
        Text(
            text = "Critical points",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 40.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
    val cardContents = listOf(
        CardContent(story = "Revolution Outreach", page = "Page: 30"),
        CardContent(story = "Contacts Alien", page = "Page: 78"),
        CardContent(story = "Unstable World", page = "Page: 106"),
        CardContent(story = "Three-Body Chaos", page = "Page: 123"),
        CardContent(story = "Alien Invasion", page = "Page: 145"),
        CardContent(story = "Cosmic Enigma", page = "Page: 159")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        cardContents.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(0.1f))

                    Box(
                        modifier = Modifier.weight(0.35f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = item.story,
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 20.sp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.1f))

                    Box(
                        modifier = Modifier.weight(0.35f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = item.page,
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 20.sp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.1f))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalReview() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalConfiguration.current.screenHeightDp.dp * 0.1f),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showBottomSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Personal Review")
            }
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false }
        ) {
            Text(
                "Write your personal review here.",
                modifier = Modifier.padding(16.dp)
            )
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

