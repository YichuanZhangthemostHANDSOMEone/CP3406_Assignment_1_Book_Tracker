package com.example.assignment_1booktracker

import android.app.Application
import com.example.assignment_1booktracker.data.AppContainer
import com.example.assignment_1booktracker.data.DefaultAppContainer

class BookApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}