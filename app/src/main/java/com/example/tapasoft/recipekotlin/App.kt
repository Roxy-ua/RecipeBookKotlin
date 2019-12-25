package com.example.tapasoft.recipekotlin

import android.app.Application
import com.example.tapasoft.recipekotlin.database.RecipeRoomDatabase

lateinit var db: RecipeRoomDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        db = RecipeRoomDatabase.getInstance(this)
    }
}