package com.example.tapasoft.recipekotlin.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.tapasoft.recipekotlin.model.*

abstract class DataRepository : BaseRepository() {
    abstract suspend fun getGroups(): List<Group>?
    abstract suspend fun getSubgroups(): List<Subgroup>?
    abstract suspend fun getHeaders(): List<Header>?
    abstract suspend fun getIngredients(): List<Ingredient>?
    abstract suspend fun getMeasureUnits(): List<MeasureUnit>?
    abstract suspend fun getRecipeGroupSubgroups(): List<RecipeGroupSubgroup>?
    abstract suspend fun getRecipes(): List<Recipe>?
    abstract suspend fun getRecipeAltIngredient(): List<RecipeAltIngredient>?
    abstract suspend fun getRecipeIngredients(): List<RecipeIngredient>?

    abstract fun insertGroups(groups: List<Group>)
    abstract fun insertSubgroups(subgroups: List<Subgroup>)
    abstract fun insertHeaders(headers: List<Header>)
    abstract fun insertIngredients(ingredients: List<Ingredient>)
    abstract fun insertMeasureUnits(measureUnits: List<MeasureUnit>)
    abstract fun insertRecipes(recipes: List<Recipe>)
    abstract fun insertRecipeAltIngredients(recipeAltIngredients: List<RecipeAltIngredient>)
    abstract fun insertRecipeGroupSubgroups(recipeGroupSubgroups: List<RecipeGroupSubgroup>)
    abstract fun insertRecipeIngredients(recipeIngredients: List<RecipeIngredient>)
    abstract fun selectAllGroups(): List<Group>
    abstract fun selectSubgroupsByGroupId(groupId: Int): List<Subgroup>

    abstract fun loadImage(context: Context, fileName: String, isLastImg: Boolean)
    abstract fun isImgLoaded(): LiveData<Boolean>
}