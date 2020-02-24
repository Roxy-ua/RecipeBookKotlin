package com.example.tapasoft.recipekotlin.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tapasoft.recipekotlin.model.*

@Database(entities = [Group::class, Subgroup::class, Header::class, Ingredient::class,
    MeasureUnit::class, Recipe::class, RecipeAltIngredient::class, RecipeGroupSubgroup::class,
    RecipeIngredient::class, CookingStep::class], version = 2, exportSchema = false)

abstract class RecipeRoomDatabase : RoomDatabase() {

    abstract fun dataDao(): DataDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        private val lock = Any()
        private const val DB_NAME = "recipe_database"
        private var INSTANCE: RecipeRoomDatabase? = null

        fun getInstance(application: Application): RecipeRoomDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(application, RecipeRoomDatabase::class.java,
                                    DB_NAME)
                                    .build()
                }
            }
            return INSTANCE!!
        }
    }

}