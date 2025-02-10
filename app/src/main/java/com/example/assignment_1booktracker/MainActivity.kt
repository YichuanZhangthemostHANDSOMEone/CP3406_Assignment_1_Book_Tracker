package com.example.assignment_1booktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment_1booktracker.ui.theme.Assignment_1BookTrackerTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment_1BookTrackerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar() }
                ){ innerPadding ->
                    BookTracking(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class Book(
    val image: Painter,
    val leftText: String,
    val rightText: String
)


@Composable
fun BookTracking(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val titleHeight = screenHeight * 0.15f
    val bottomNavHeight = screenHeight * 0.1f

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(titleHeight)  // 使用计算后的高度
            ) {
                Text(
                    text = "My Library",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 16.dp)
                )
            }
        }
        item {
            FirstRowBooks(modifier = Modifier)
        }
        item {
            SecondRowBooks(modifier = Modifier)
        }
        item {
            ThirdRowBooks(modifier = Modifier)
        }
    }
}

@Composable
fun FirstRowBooks(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Science Fiction",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 0.dp)
        )
        val books = listOf(
            Book(
                image = painterResource(id = R.drawable.dune_science_fiction),
                leftText = "Dune",
                rightText = "50%"
            ),
            Book(
                image = painterResource(id = R.drawable.all_the_water_in_the_world_science_fiction),
                leftText = "The Water in The World",
                rightText = "63%"
            ),
            Book(
                image = painterResource(id = R.drawable.the_three_body_problem_science_fiction),
                leftText = "The Three-Body Problem",
                rightText = "70%"
            ),

            )

        LazyRow(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(books) { book ->
                CardView(modifier = Modifier, book = book)
            }
        }
    }
}

@Composable
fun SecondRowBooks(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Historical Fiction",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 0.dp)
        )
        val books = listOf(
            Book(
                image = painterResource(id = R.drawable.homeseeking_historycal_fiction),
                leftText = "Homeseeking",
                rightText = "55%"
            ),
            Book(
                image = painterResource(id = R.drawable.the_stolen_queen_historical_fiction),
                leftText = "The Stolen Queen",
                rightText = "65%"
            ),
            Book(
                image = painterResource(id = R.drawable.memoirs_of_a_geisha_historical_fiction),
                leftText = "Memoirs of Geisha",
                rightText = "45%"
            ),
        )
        LazyRow(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(books) { book ->
                CardView(modifier = Modifier, book = book)
            }
        }
    }
}

@Composable
fun ThirdRowBooks(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Mystery",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 0.dp)
        )
        val books = listOf(
            Book(
                image = painterResource(id = R.drawable.a_sea_of_unspoken_things_mystery),
                leftText = "Sea of Unspoken Things",
                rightText = "75%"
            ),
            Book(
                image = painterResource(id = R.drawable.sweet_fury_mystery),
                leftText = "Sweet Fury",
                rightText = "85%"
            ),
            Book(
                image = painterResource(id = R.drawable.the_note_mystery),
                leftText = "The Note",
                rightText = "90%"
            )
        )

        LazyRow(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(books) { book ->
                CardView(modifier = Modifier, book = book)
            }
        }
    }
}


@Composable
fun CardView(modifier: Modifier = Modifier, book: Book) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .width(200.dp)
            .height(300.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 上部 90%：图片区域
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
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.leftText,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = book.rightText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val bottomNavHeight = screenHeight * 0.1f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(bottomNavHeight)
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ElevatedButton(
            onClick = { /* Handle Library click */ },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(text = "Library")
        }
        ElevatedButton(
            onClick = { /* Handle Recommend click */ },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(text = "Recommend")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Assignment_1BookTrackerTheme {
        BookTracking()
    }
}