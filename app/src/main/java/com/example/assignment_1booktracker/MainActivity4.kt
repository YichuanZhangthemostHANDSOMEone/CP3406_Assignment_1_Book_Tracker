package com.example.assignment_1booktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

class MainActivity4 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 确保 enableEdgeToEdge() 方法存在
        // enableEdgeToEdge()
        setContent {
            Assignment_1BookTrackerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    BookInfo(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class RecommendedBook(
    val image: Painter,
    val name: String,
    val bookCategory: String,
    val author: String,
)

@Composable
fun BookInfo(modifier: Modifier = Modifier) {
    val book = RecommendedBook(
            image = painterResource(id = R.drawable.wolf_hall_historical_fiction),
            name = "Wolf Hall",
            bookCategory = "Historical Fiction",
            author = "Margaret Atwood"
        )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BookCard(modifier = Modifier, book = book)
            }
        }
        item {
            Details(book = book)
        }
    }
}


@Composable
fun BookCard(modifier: Modifier = Modifier, book: RecommendedBook) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .width(190.dp)
            .height(320.dp),
    elevation = CardDefaults.cardElevation(5.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Yellow)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
            ) {
                Image(
                    painter = book.image,
                    contentDescription = "Card Image",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.offset(y = (-4).dp)
                )
            }
        }
    }
}


@Composable
fun Details(book: RecommendedBook){
    val configuration = LocalConfiguration.current
    val InfoContainerHeight = (configuration.screenHeightDp * 0.045f).dp

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            Column(modifier = Modifier.fillMaxSize()){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(InfoContainerHeight),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Author:",
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                        modifier = Modifier.offset(y = (-9).dp)
                    )
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(InfoContainerHeight),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Category:",
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                        modifier = Modifier.offset(y = (-8).dp)

                    )
                    Text(
                        text = book.bookCategory,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(InfoContainerHeight),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recommend base on:",
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                    Text(
                        text = "Homeseeking",
                        style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
                HorizontalDivider(thickness = 2.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(InfoContainerHeight),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reasons:",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "    Wolf Hall focuses on Thomas Cromwell and tells the story of his meteoric rise from the abused son of a blacksmith to self-made man to right-hand adviser to Henry VIII. Through his eyes we get to see many of the familiar faces of the era: Cardinal Wolsey, Sir Thomas More, Anne Boleyn, the Dukes of Suffolk and Norfolk, Thomas Cranmer, and of course, Henry himself. (It says something about that era that I think of that list 50% of them ended up beheaded.)",
                        style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "    The writing style of Wolf Hall is idiosyncratic and thus I found it hard to get into, at first. I’m a bit embarrassed to admit that I am terrible with identifying narrative styles unless they are pretty basic: first person present tense, first person past tense, etc. I just knew I found the style in this novel confusing. So I looked it up: Wolf Hall is written in third person limited present tense. The reader gets Cromwell’s POV only, though it takes a while for that to be totally clear.",
                        style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    )
                }

            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReasonPreview() {
    Assignment_1BookTrackerTheme {
        BookInfo()
    }
}

