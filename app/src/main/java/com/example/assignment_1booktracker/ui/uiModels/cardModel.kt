package com.example.assignment_1booktracker.ui.uiModels

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.assignment_1booktracker.R

// 定义一个用于 UI 展示的书籍数据模型（用于转换 model.Book 至 UI 显示）
data class UIBook(
    val imageUrl: String,
    val leftText: String,
    val rightText: String
)


// 占位函数：将 model.Book 中的 image 字段转换为 Painter
@Composable
fun getImagePainter(imageUrl: String): Painter {
    // 暂时返回一个占位图片，后续根据实际情况处理网络/数据库图片加载
    return painterResource(id = R.drawable.wolf_hall_historical_fiction)
}
