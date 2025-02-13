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
import androidx.navigation.compose.rememberNavController
import com.example.assignment_1booktracker.ui.theme.Assignment_1BookTrackerTheme



class MainActivity3 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment_1BookTrackerTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ){ innerPadding ->
                    BookRecommendations(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun BookRecommendations(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val titleHeight = screenHeight * 0.1f
    val bottomNavHeight = screenHeight * 0.1f

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(titleHeight)
            ) {
                Text(
                    text = "Recommendations",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 16.dp)
                )
            }
        }
        item {
            RecommendedBooks_1(modifier = Modifier)
        }
        item {
            RecommendedBooks_2(modifier = Modifier)
        }
        item {
            RecommendedBooks_3(modifier = Modifier)
        }
    }
}

@Composable
fun RecommendedBooks_1(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val books = listOf(
            Book(
                image = painterResource(id = R.drawable.the_nightingal),
                leftText = "The Nightingale",
                rightText = "Rate:6/10"
            ),
            Book(
                image = painterResource(id = R.drawable.animal_farm_sci),
                leftText = "Animal Farm",
                rightText = "Rate:7/10"
            ),

            )

        Row(
            modifier = modifier.padding(horizontal = 2.dp, vertical = 8.dp)
        ) {
            books.forEach { book ->
                BookCard(modifier = Modifier, book = book)
            }
        }
    }
}

@Composable
fun RecommendedBooks_2(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val books = listOf(
            Book(
                image = painterResource(id = R.drawable.i_robot_sci),
                leftText = "I Robot",
                rightText = "Rate:9/10"
            ),
            Book(
                image = painterResource(id = R.drawable.the_help_historical_fiction),
                leftText = "The Help",
                rightText = "Rate:8/10"
            ),

            )

        Row(
            modifier = modifier.padding(horizontal = 3.dp, vertical = 8.dp)
        ) {
            books.forEach { book ->
                BookCard(modifier = Modifier, book = book)
            }
        }
    }
}

@Composable
fun RecommendedBooks_3(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val books = listOf(
            Book(
                image = painterResource(id = R.drawable.wolf_hall_historical_fiction),
                leftText = "Wolf Hall",
                rightText = "Rate:5/10"
            ),
            Book(
                image = painterResource(id = R.drawable.rebecca_mystery),
                leftText = "Rebecca",
                rightText = "Rate:8/10"
            ),

            )

        Row(
            modifier = modifier.padding(horizontal = 2.dp, vertical = 8.dp)
        ) {
            books.forEach { book ->
                BookCard(modifier = Modifier, book = book)
            }
        }
    }
}

@Composable
fun BookCard(modifier: Modifier = Modifier, book: Book) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .width(190.dp)
            .height(300.dp),
        elevation = CardDefaults.cardElevation(4.dp)
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



@Preview(showBackground = true)
@Composable
fun RecommendationPreview() {
    Assignment_1BookTrackerTheme {
        BookRecommendations()
    }
}