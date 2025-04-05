package com.example.assignment_1booktracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dbBook")
data class dbBook(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val image: String,              // 封面图片路径或 URL
    val name: String,               // 书名
    val author: String,             // 作者
    val category: String,           // 种类
    val readPages: Int? = 0,         // 已读页数
    val totalPages: Int,            // 总页数
    val progress: Int? = 0,          // 进度百分比（整数）
    val rating: Int? = null,            // 评分（满分 10）
    val criticalPoints: String? = null, // 关键点文本
    val cpPage: Int? = null,        // 关键点所在页数
    val review: String? = null      // 用户评论
)