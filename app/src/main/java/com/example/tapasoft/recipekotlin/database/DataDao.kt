package com.example.tapasoft.recipekotlin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tapasoft.recipekotlin.model.*

@Dao
interface DataDao {
    @Insert
    fun insertAllGroups(groups: List<Group>)

    @Insert
    fun insertAllSubgroups(subgroups: List<Subgroup>)

    @Insert
    fun insertAllHeaders(headers: List<Header>)

    @Insert
    fun insertAllIngredients(ingredients: List<Ingredient>)

    @Insert
    fun insertAllMeasureUnits(measureUnits: List<MeasureUnit>)

    @Insert
    fun insertAllRecipes(recipes: List<Recipe>)

    @Insert
    fun insertAllRecipeAltIngredients(recipeAltIngredients: List<RecipeAltIngredient>)

    @Insert
    fun insertAllRecipeGroupSubgroups(recipeGroupSubgroups: List<RecipeGroupSubgroup>)

    @Insert
    fun insertAllRecipeIngredients(recipeIngredients: List<RecipeIngredient>)

    @Query("SELECT * from groups WHERE id!=7 ORDER BY id")
    fun getAllGroups(): List<Group>

    @Query("SELECT * from subgroups ORDER BY id")
    fun getAllSubgroups(): List<Subgroup>

    @Query("SELECT * from subgroups WHERE grpId = :groupId")
    fun getSubgroupsByGroupId(groupId: Int): List<Subgroup>

    @Query("SELECT * from headers ORDER BY id")
    fun getAllHeaders(): List<Header>

    @Query("SELECT * from recipes ORDER BY id")
    fun getAllRecipes(): List<Recipe>

    @Query("SELECT * from recipe_ingredients ORDER BY id")
    fun getAllRecipeIngredients(): List<RecipeIngredient>

}