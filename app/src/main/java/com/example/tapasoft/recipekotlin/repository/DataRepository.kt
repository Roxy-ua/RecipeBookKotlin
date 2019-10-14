package com.example.tapasoft.recipekotlin.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tapasoft.recipekotlin.Config
import com.example.tapasoft.recipekotlin.api.ApiInterface
import com.example.tapasoft.recipekotlin.database.DataDao
import com.example.tapasoft.recipekotlin.model.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by udav on 27-Jul-19.
 */
class DataRepository(private val api: ApiInterface, private val dataDao: DataDao) : BaseRepository() {
    private val isLoadedLiveData = MutableLiveData<Boolean>()

    suspend fun getGroups(): List<Group>? {
        return safeApiCall(
                call = { api.getGroupsAsync().await() },
                errorMessage = "Error Fetching Groups"
        )
    }

    suspend fun getSubgroups(): List<Subgroup>? {
        return safeApiCall(
                call = { api.getSubGroupsAsync().await() },
                errorMessage = "Error Fetching Subgroups"
        )
    }

    suspend fun getHeaders(): List<Header>? {
        return safeApiCall(
                call = { api.getHeadersAsync().await() },
                errorMessage = "Error Fetching Headers"
        )
    }

    suspend fun getIngredients(): List<Ingredient>? {
        return safeApiCall(
                call = { api.getIngredientsAsync().await() },
                errorMessage = "Error Fetching Ingredients"
        )
    }

    suspend fun getMeasureUnits(): List<MeasureUnit>? {
        return safeApiCall(
                call = { api.getMeasureUnitsAsync().await() },
                errorMessage = "Error Fetching MeasureUnits"
        )
    }

    suspend fun getRecipeGroupSubgroups(): List<RecipeGroupSubgroup>? {
        return safeApiCall(
                call = { api.getGroupSubgroupsAsync().await() },
                errorMessage = "Error Fetching GroupSubgroup"
        )
    }

    suspend fun getRecipes(): List<Recipe>? {
        return safeApiCall(
                call = { api.getRecipesAsync().await() },
                errorMessage = "Error Fetching Recipes"
        )
    }

    suspend fun getRecipeAltIngredient(): List<RecipeAltIngredient>? {
        return safeApiCall(
                call = { api.getAltIngredientsAsync().await() },
                errorMessage = "Error Fetching RecipeAltIngredients"
        )
    }

    suspend fun getRecipeIngredients(): List<RecipeIngredient>? {
        return safeApiCall(
                call = { api.getRecIngredientsAsync().await() },
                errorMessage = "Error Fetching RecipeIngredient"
        )
    }

    fun insertGroups(groups: List<Group>) {
        dataDao.insertAllGroups(groups)
    }

    fun insertSubgroups(subgroups: List<Subgroup>) {
        dataDao.insertAllSubgroups(subgroups)
    }

    fun insertHeaders(headers: List<Header>) {
        dataDao.insertAllHeaders(headers)
    }

    fun insertIngredients(ingredients: List<Ingredient>) {
        dataDao.insertAllIngredients(ingredients)
    }

    fun insertMeasureUnits(measureUnits: List<MeasureUnit>) {
        dataDao.insertAllMeasureUnits(measureUnits)
    }

    fun insertRecipes(recipes: List<Recipe>) {
        dataDao.insertAllRecipes(recipes)
    }

    fun insertRecipeAltIngredients(recipeAltIngredients: List<RecipeAltIngredient>) {
        dataDao.insertAllRecipeAltIngredients(recipeAltIngredients)
    }

    fun insertRecipeGroupSubgroups(recipeGroupSubgroups: List<RecipeGroupSubgroup>) {
        dataDao.insertAllRecipeGroupSubgroups(recipeGroupSubgroups)
    }

    fun insertRecipeIngredients(recipeIngredients: List<RecipeIngredient>) {
        dataDao.insertAllRecipeIngredients(recipeIngredients)
    }

    fun selectAllGroups(): List<Group> {
        return dataDao.getAllGroups()
    }

    fun selectSubgroupsByGroupId(groupId: Int): List<Subgroup> {
        return dataDao.getSubgroupsByGroupId(groupId)
    }

    fun loadImage(context: Context, fileName: String, isLastImg: Boolean) {
        val conn = URL(Config.SERVER_API_IMG_URL + fileName).openConnection() as HttpURLConnection
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

        if (isLastImg) isLoadedLiveData.postValue(true)
    }

    fun isImgLoaded(): LiveData<Boolean> {
        return isLoadedLiveData
    }

}