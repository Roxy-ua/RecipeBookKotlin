package com.example.tapasoft.recipekotlin.database

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.tapasoft.recipekotlin.model.*
import kotlinx.coroutines.CoroutineScope

/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.
 */
@Database(entities = [Group::class, Subgroup::class, Header::class, Ingredient::class,
    MeasureUnit::class, Recipe::class, RecipeAltIngredient::class, RecipeGroupSubgroup::class,
    RecipeIngredient::class, CookingStep::class], version = 2, exportSchema = false)

abstract class RecipeRoomDatabase : RoomDatabase() {
    abstract fun dataDao(): DataDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeRoomDatabase? = null

        fun getDatabase(
                context: Context
                //scope: CoroutineScope
        ): RecipeRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecipeRoomDatabase::class.java,
                        "recipe_database"
                )
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        // Migration is not part of this codelab.
                        .fallbackToDestructiveMigration()
                        //.addCallback(RecDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class RecDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
//                INSTANCE?.let { database ->
//                    scope.launch {
//                        //populateDatabase(database.wordDao())
//                    }
//                }
            }
        }

    }

}