package com.example.tapasoft.recipekotlin.repository

import android.content.Context
import com.example.tapasoft.recipekotlin.model.CookingStep
import com.example.tapasoft.recipekotlin.model.Recipe
import com.example.tapasoft.recipekotlin.model.RecipeIngredient

abstract class RecipeRepository : BaseRepository() {
    abstract fun selectRecipesByGrpSgpId(groupId: Int, subgroupId: Int): List<Recipe>
    abstract fun selectRecipesByFavouriteFlag(): List<Recipe>
    abstract fun selectIngredientsByRcpId(recipeId: Int): List<RecipeIngredient>
    abstract fun selectCookingSteps(rcpId: Int): List<CookingStep>
    abstract suspend fun loadCookingSteps(rcpId: Int): List<CookingStep>?
    abstract fun insertCookingStepsIntoDB(cookingSteps: List<CookingStep>)
    abstract fun deleteCookingStepsByRecId(recId: Int)
    abstract fun loadCookingStepImage(context: Context, fileName: String)
    abstract fun setRecipeFavourite(favour: Int, recId: Int)
    abstract fun searchRecipeByQueryString(query: String): List<Recipe>
}