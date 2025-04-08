package com.example.assignment_1booktracker.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromCriticalPointList(value: List<DataCriticalPoint>?): String? {
        return if (value == null) null else Gson().toJson(value)
    }

    @TypeConverter
    fun toCriticalPointList(value: String?): List<DataCriticalPoint>? {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<DataCriticalPoint>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
