package com.example.assignment_1booktracker.ui.uiModels

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment_1booktracker.data.dbBook

@Composable
fun MarkedPoints(
    book: dbBook,
    onDelete: () -> Unit,
    onAddPoint: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Button(onClick = onAddPoint) {
                Text("Add Point")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onDelete) {
                Text("Delete")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Critical Point: ${book.criticalPoints ?: "None"}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Point Page: ${book.cpPage ?: "N/A"}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}