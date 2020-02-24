package com.example.tapasoft.recipekotlin.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tapasoft.recipekotlin.Config
import com.example.tapasoft.recipekotlin.database.DataDao
import com.example.tapasoft.recipekotlin.db
import com.example.tapasoft.recipekotlin.model.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by udav on 27-Jul-19.
 */
class DataRepositoryImpl() : DataRepository() {
    private val dataDao: DataDao = db.dataDao()
    private val isLoadedLiveData = MutableLiveData<Boolean>()

    override suspend fun getGroups(): List<Group>? {
        return safeApiCall(
                call = { api.getGroupsAsync().await() },
                errorMessage = "Error Fetching Groups"
        )
    }

    override suspend fun getSubgroups(): List<Subgroup>? {
        return safeApiCall(
                call = { api.getSubGroupsAsync().await() },
                errorMessage = "Error Fetching Subgroups"
        )
    }

    override suspend fun getHeaders(): List<Header>? {
        return safeApiCall(
                call = { api.getHeadersAsync().await() },
                errorMessage = "Error Fetching Headers"
        )
    }

    override suspend fun getIngredients(): List<Ingredient>? {
        return safeApiCall(
                call = { api.getIngredientsAsync().await() },
                errorMessage = "Error Fetching Ingredients"
        )
    }

    override suspend fun getMeasureUnits(): List<MeasureUnit>? {
        return safeApiCall(
                call = { api.getMeasureUnitsAsync().await() },
                errorMessage = "Error Fetching MeasureUnits"
        )
    }

    override suspend fun getRecipeGroupSubgroups(): List<RecipeGroupSubgroup>? {
        return safeApiCall(
                call = { api.getGroupSubgroupsAsync().await() },
                errorMessage = "Error Fetching GroupSubgroup"
        )
    }

    override suspend fun getRecipes(): List<Recipe>? {
        return safeApiCall(
                call = { api.getRecipesAsync().await() },
                errorMessage = "Error Fetching Recipes"
        )
    }

    override suspend fun getRecipeAltIngredient(): List<RecipeAltIngredient>? {
        return safeApiCall(
                call = { api.getAltIngredientsAsync().await() },
                errorMessage = "Error Fetching RecipeAltIngredients"
        )
    }

    override suspend fun getRecipeIngredients(): List<RecipeIngredient>? {
        return safeApiCall(
                call = { api.getRecIngredientsAsync().await() },
                errorMessage = "Error Fetching RecipeIngredient"
        )
    }

    override fun insertGroups(groups: List<Group>) {
        dataDao.insertAllGroups(groups)
    }

    override fun insertSubgroups(subgroups: List<Subgroup>) {
        dataDao.insertAllSubgroups(subgroups)
    }

    override fun insertHeaders(headers: List<Header>) {
        dataDao.insertAllHeaders(headers)
    }

    override fun insertIngredients(ingredients: List<Ingredient>) {
        dataDao.insertAllIngredients(ingredients)
    }

    override fun insertMeasureUnits(measureUnits: List<MeasureUnit>) {
        dataDao.insertAllMeasureUnits(measureUnits)
    }

    override fun insertRecipes(recipes: List<Recipe>) {
        dataDao.insertAllRecipes(recipes)
    }

    override fun insertRecipeAltIngredients(recipeAltIngredients: List<RecipeAltIngredient>) {
        dataDao.insertAllRecipeAltIngredients(recipeAltIngredients)
    }

    override fun insertRecipeGroupSubgroups(recipeGroupSubgroups: List<RecipeGroupSubgroup>) {
        dataDao.insertAllRecipeGroupSubgroups(recipeGroupSubgroups)
    }

    override fun insertRecipeIngredients(recipeIngredients: List<RecipeIngredient>) {
        dataDao.insertAllRecipeIngredients(recipeIngredients)
    }

    override fun selectAllGroups(): List<Group> {
        return dataDao.getAllGroups()
    }

    override fun selectSubgroupsByGroupId(groupId: Int): List<Subgroup> {
        return dataDao.getSubgroupsByGroupId(groupId)
    }

    override fun loadImage(context: Context, fileName: String, isLastImg: Boolean) {
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

    override fun isImgLoaded(): LiveData<Boolean> {
        return isLoadedLiveData
    }
}