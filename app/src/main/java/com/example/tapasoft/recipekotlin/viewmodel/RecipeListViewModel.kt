package com.example.tapasoft.recipekotlin.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.tapasoft.recipekotlin.R
import com.example.tapasoft.recipekotlin.api.ApiClient
import com.example.tapasoft.recipekotlin.database.RecipeRoomDatabase
import com.example.tapasoft.recipekotlin.model.CookingStep
import com.example.tapasoft.recipekotlin.model.Recipe
import com.example.tapasoft.recipekotlin.repository.RecipeRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RecipeListViewModel(application: Application) : AndroidViewModel(application) {
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO

    private val scope = CoroutineScope(coroutineContext)

    val recipeDao = RecipeRoomDatabase.getDatabase(application).recipeDao()
    private val repository = RecipeRepository(ApiClient.clientApi, recipeDao)
    val recipeListLiveData = MutableLiveData<List<Recipe>>()

    fun fetchRecipeListFromDB(groupId: Int, subgroupId: Int, context: Context) {
        scope.launch {
            val recipes = repository.selectRecipesByGrpSgpId(groupId, subgroupId)
            handleRecipeList(recipes, context)
        }
    }

    fun fetchFavouriteListFromDB(context: Context) {
        scope.launch {
            val recipes = repository.selectRecipesByFavouriteFlag()
            handleRecipeList(recipes, context)
        }
    }

    fun getSearchResultListFromDB(context: Context, query: String) {
        scope.launch {
            val recipes = repository.searchRecipeByQueryString(query)
            handleRecipeList(recipes, context)
        }
    }

    private fun handleRecipeList(recipes: List<Recipe>, context: Context) {
        for (recipe in recipes) {
            var ingrStr = ""
            var ingrStrDtl = ""

            val ingredients = repository.selectIngredientsByRcpId(recipe.id)

            for (ingredient in ingredients) {
                var ingrMsr = ""
                //======== form measure string ========
                if (ingredient.measureFlag == "+") {
                    if (ingredient.value2 == 0) {
                        ingrMsr = " - " + ingredient.value1 + " " + ingredient.measureName
                    } else {
                        ingrMsr = " - " + ingredient.value1 + "-" + ingredient.value2 + " " + ingredient.measureName
                    }
                }

                ingrStr = ingrStr + ", " + ingredient.name + ingrMsr
                ingrStrDtl = ingrStrDtl + ", <br>" + "\u25C8 " + ingredient.name + ingrMsr
            }
            //Cut 2 symbols of the String
            ingrStr = ingrStr.substring(2)
            ingrStrDtl = ingrStrDtl.substring(6)

            //======== form image File name ========
            recipe.imgFileName = getFileName(recipe.id)

            recipe.ingredients = ingrStr
            recipe.ingredientsDtl = ingrStrDtl

            val duration = recipe.duration.toString()
            val cookTime = context.getString(R.string.cook_time, duration)
            recipe.summary = "( $cookTime - 6 порций - 120 грн. )"

            //======== select cooking steps ========
            recipe.cookingSteps = fetchCookingStepsFromDB(recipe.id)
        }
        recipeListLiveData.postValue(recipes)
    }

    fun fetchCookingStepsFromDB(rcpId: Int): List<CookingStep> {
            val cookingStepsDB = repository.selectCookingSteps(rcpId)
            var step = 1
            for (cookingStep in cookingStepsDB) {
                val fileName = getStepFileName(rcpId, step)
                cookingStep.imgFileName = fileName
                step++
            }
            return cookingStepsDB
    }

    private fun getStepFileName(recId: Int, stepId: Int): String {
        var prefix = "0"
        var suffix = ""

        when (recId) {
            in 1..9 -> prefix = "000"
            in 10..99 -> prefix = "00"
        }

        when (stepId) {
            in 1..9 -> suffix = "0"
        }

        val sb = StringBuilder(prefix + recId + "_" + suffix + stepId + ".png")
        return sb.toString()
    }

    private fun getFileName(recId: Int): String {
        var prefix = "0"

        when (recId) {
            in 1..9 -> prefix = "000"
            in 10..99 -> prefix = "00"
        }

        val sb = StringBuilder()
        return sb.append(prefix).append(recId).append(".png").toString()
    }

    fun cancelAllRequests() = coroutineContext.cancel()
}