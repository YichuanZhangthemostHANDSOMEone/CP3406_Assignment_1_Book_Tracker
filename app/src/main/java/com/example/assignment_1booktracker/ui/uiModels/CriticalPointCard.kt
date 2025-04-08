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

// UI 层的 CriticalPoint 模型
data class CriticalPoint(
    val id: Int,
    val text: String,
    val page: Int
)

// 扩展函数：将 DataCriticalPoint 转换为 UI 模型 CriticalPoint
fun DataCriticalPoint.toUiModel(): CriticalPoint {
    return CriticalPoint(
        id = this.id,
        text = this.text,
        page = this.page
    )
}

@Composable
fun CriticalPointCard(
    criticalPoint: CriticalPoint,  // 使用 UI 模型类型
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
            // 左侧：显示文本（关键点内容及页码）
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = criticalPoint.text, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Page: ${criticalPoint.page}", style = MaterialTheme.typography.bodyMedium)
            }
            // 右侧：显示删除和编辑按钮
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
