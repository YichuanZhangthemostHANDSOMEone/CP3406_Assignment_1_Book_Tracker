package com.example.assignment_1booktracker.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.assignment_1booktracker.ui.theme.Assignment_1BookTrackerTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.util.Logger
import com.example.assignment_1booktracker.R
import com.example.assignment_1booktracker.ui.uiModels.getImagePainter

@Composable
fun RecDetailsScreen(
    navController: NavController,
    recId: Int?,
    viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    val book = recId?.let { viewModel.getBookById(it) }
    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading recommendation details...")
        }
    } else {
        RecDetailContent(book = book)
    }
}

@Composable
fun RecDetailContent(book: com.example.assignment_1booktracker.model.Book) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                RecDetailCard(book = book)
            }
        }
        item {
            RecDetailInformation(book = book)
        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
    }
}

@Composable
fun RecDetailCard(book: com.example.assignment_1booktracker.model.Book) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(190.dp)
            .height(360.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
            ) {
                AsyncImage(
                    model = book.image,
                    contentDescription = "Book Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
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
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RecDetailInformation(book: com.example.assignment_1booktracker.model.Book) {
    val infoContainerHeight = LocalConfiguration.current.screenHeightDp.dp * 0.045f
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Author:", style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp))
            Text(book.author, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 5.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Category:", style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp))
            Text(book.category, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 5.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Recommend based on:", style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp))
            Text(book.baseon, style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic), modifier = Modifier.padding(start = 5.dp))
        }
        HorizontalDivider(thickness = 2.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoContainerHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Reasons:", style = MaterialTheme.typography.headlineLarge)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = book.reason,
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
            )
        }
    }
}
