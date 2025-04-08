package com.example.assignment_1booktracker.ui.uiModels

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.rememberAsyncImagePainter

// 定义一个用于 UI 展示的书籍数据模型（用于转换 model.Book 至 UI 显示）
data class UIBook(
    val id: Int,
    val imageUrl: String,
    val leftText: String,
    val rightText: String
)


@Composable
fun getImagePainter(imageUrl: String): Painter {
    val context = LocalContext.current
    return if (imageUrl.startsWith("android.resource://")) {
        // 解析 URI 格式：android.resource://[package]/drawable/[resource_name]
        val uri = Uri.parse(imageUrl)
        val segments = uri.pathSegments
        if (segments.size >= 2) {
            val resName = segments[1]  // 第二段为资源名称
            // 注意：使用当前应用包名来获取资源 id
            val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
            if (resId != 0) {
                painterResource(id = resId)
            } else {
                // 如果未能找到资源 id，则降级使用 Coil
                rememberAsyncImagePainter(model = imageUrl)
            }
        } else {
            // 格式不符合预期，直接使用 Coil
            rememberAsyncImagePainter(model = imageUrl)
        }
    } else {
        rememberAsyncImagePainter(model = imageUrl)
    }
}
