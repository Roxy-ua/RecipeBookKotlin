package com.example.tapasoft.recipekotlin.repository

import android.content.Context
import com.example.tapasoft.recipekotlin.Config
import com.example.tapasoft.recipekotlin.activity.RecipeDetailsActivity.Companion.FAVOURITE_ON
import com.example.tapasoft.recipekotlin.api.ApiInterface
import com.example.tapasoft.recipekotlin.database.RecipeDao
import com.example.tapasoft.recipekotlin.model.CookingStep
import com.example.tapasoft.recipekotlin.model.MeasureUnit
import com.example.tapasoft.recipekotlin.model.Recipe
import com.example.tapasoft.recipekotlin.model.RecipeIngredient
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class RecipeRepository(private val api: ApiInterface, private val recipeDao: RecipeDao) : BaseRepository() {

    fun selectRecipesByGrpSgpId(groupId: Int, subgroupId: Int): List<Recipe> {
        if(subgroupId == 0) return recipeDao.findRecipesByGrpId(groupId)
        return recipeDao.findRecipesByGrpSgpId(groupId, subgroupId)
    }

    fun selectRecipesByFavouriteFlag(): List<Recipe> {
        return recipeDao.findRecipesByFavouriteFlag(FAVOURITE_ON)
    }

    fun selectIngredientsByRcpId(recipeId: Int): List<RecipeIngredient> {
        val ingredients = recipeDao.findIngredientsByRcpId(recipeId)
        //for (i in ingredients.indices) {
        for (ingredient in ingredients) {

            //select ingredient name
            val ingredientName = recipeDao.selectIngredientName(ingredient.ingrId)
            ingredient.name = ingredientName

            //select measure name
            if(ingredient.measureFlag == "+") {
                val measureName = recipeDao.selectMeasureName(ingredient.measureId)
                ingredient.measureName = measureName
            }
        }
        return ingredients
    }

    fun selectCookingSteps(rcpId: Int): List<CookingStep> {
        return recipeDao.findCookingStepsByRcpId(rcpId)
    }

    suspend fun loadCookingSteps(rcpId: Int): List<CookingStep>? {
        return safeApiCall(
                call = { api.getCookingStepsAsync(rcpId).await() },
                errorMessage = "Error Fetching CookingSteps"
        )
    }

    fun insertCookingStepsIntoDB(cookingSteps: List<CookingStep>) {
        recipeDao.insertCookingSteps(cookingSteps)
    }

    fun deleteCookingStepsByRecId(recId: Int) {
        recipeDao.deleteByRecipeId(recId)
    }

    fun loadCookingStepImage(context: Context, fileName: String) {
        val conn = URL(Config.SERVER_API_IMG_PRP_URL + fileName).openConnection() as HttpURLConnection
        val tmpdir = context.getDir(Config.INTERNAL_IMG_FOLDER, Context.MODE_PRIVATE)
        val input = conn.inputStream

        val fileInTmpDir = File(tmpdir, fileName)
        val output = FileOutputStream(fileInTmpDir)

        var bytesRead: Int
        val buffer = ByteArray(1024)
        while (input.read(buffer).apply { bytesRead = this } > 0) {
            output.write(buffer, 0, bytesRead); }

        input.close()
        output.close()
        conn.disconnect()
    }

    fun setRecipeFavourite(favour: Int, recId: Int) {
        recipeDao.updateRecipeFavourite(favour, recId)
    }

    fun searchRecipeByQueryString(query: String): List<Recipe> {
        return recipeDao.searchRecipesByName(query)
    }

}