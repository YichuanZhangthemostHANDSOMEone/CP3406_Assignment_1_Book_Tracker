package com.example.assignment_1booktracker.ui.uiModels

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment_1booktracker.data.DataCriticalPoint

//The CriticalPoint model of the UI layer
data class CriticalPoint(
    val id: Int,
    val text: String,
    val page: Int
)

//Extension function: Convert DataCriticalPoint to UI model CriticalPoint
fun DataCriticalPoint.toUiModel(): CriticalPoint {
    return CriticalPoint(
        id = this.id,
        text = this.text,
        page = this.page
    )
}

@Composable
fun CriticalPointCard(
    criticalPoint: CriticalPoint,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            //Left side: Display text (key points content and page numbers)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = criticalPoint.text, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Page: ${criticalPoint.page}", style = MaterialTheme.typography.bodyMedium)
            }
            //On the right side: Display the delete and edit buttons
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Critical Point",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Critical Point"
                    )
                }
            }
        }
    }
}
