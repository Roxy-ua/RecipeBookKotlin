package com.example.tapasoft.recipekotlin.repository

import android.content.Context
import com.example.tapasoft.recipekotlin.Config
import com.example.tapasoft.recipekotlin.activity.RecipeDetailsActivity.Companion.FAVOURITE_ON
import com.example.tapasoft.recipekotlin.database.RecipeDao
import com.example.tapasoft.recipekotlin.db
import com.example.tapasoft.recipekotlin.model.CookingStep
import com.example.tapasoft.recipekotlin.model.Recipe
import com.example.tapasoft.recipekotlin.model.RecipeIngredient
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class RecipeRepositoryImpl() : RecipeRepository() {
    private val recipeDao: RecipeDao = db.recipeDao()

    override fun selectRecipesByGrpSgpId(groupId: Int, subgroupId: Int): List<Recipe> {
        if(subgroupId == 0) return recipeDao.findRecipesByGrpId(groupId)
        return recipeDao.findRecipesByGrpSgpId(groupId, subgroupId)
    }

    override fun selectRecipesByFavouriteFlag(): List<Recipe> {
        return recipeDao.findRecipesByFavouriteFlag(FAVOURITE_ON)
    }

    override fun selectIngredientsByRcpId(recipeId: Int): List<RecipeIngredient> {
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

    override fun selectCookingSteps(rcpId: Int): List<CookingStep> {
        return recipeDao.findCookingStepsByRcpId(rcpId)
    }

    override suspend fun loadCookingSteps(rcpId: Int): List<CookingStep>? {
        return safeApiCall(
                call = { api.getCookingStepsAsync(rcpId).await() },
                errorMessage = "Error Fetching CookingSteps"
        )
    }

    override fun insertCookingStepsIntoDB(cookingSteps: List<CookingStep>) {
        recipeDao.insertCookingSteps(cookingSteps)
    }

    override fun deleteCookingStepsByRecId(recId: Int) {
        recipeDao.deleteByRecipeId(recId)
    }

    override fun loadCookingStepImage(context: Context, fileName: String) {
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

    override fun setRecipeFavourite(favour: Int, recId: Int) {
        recipeDao.updateRecipeFavourite(favour, recId)
    }

    override fun searchRecipeByQueryString(query: String): List<Recipe> {
        return recipeDao.searchRecipesByName(query)
    }
}