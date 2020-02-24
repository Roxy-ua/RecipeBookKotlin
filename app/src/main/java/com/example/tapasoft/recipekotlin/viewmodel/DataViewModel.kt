package com.example.tapasoft.recipekotlin.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tapasoft.recipekotlin.model.Group
import com.example.tapasoft.recipekotlin.model.Subgroup
import com.example.tapasoft.recipekotlin.repository.DataRepository
import com.example.tapasoft.recipekotlin.repository.DataRepositoryImpl
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by udav on 27-Jul-19.
 */
class DataViewModel(private val repository: DataRepository = DataRepositoryImpl()) : ViewModel() {

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO

    private val scope = CoroutineScope(coroutineContext)

    val expandableListLiveData = MutableLiveData<LinkedHashMap<Group, List<Subgroup>>>()
    val imgFileNameLiveData = MutableLiveData<ArrayList<String>>()

    fun fetchDataFromServer() {
        scope.launch {
            val groups = repository.getGroups()
            if (groups != null && groups.isNotEmpty()) {
                repository.insertGroups(groups)
            }

            val subgroups = repository.getSubgroups()
            if (subgroups != null && subgroups.isNotEmpty()) {
                repository.insertSubgroups(subgroups)
            }

            val headers = repository.getHeaders()
            if (headers != null && headers.isNotEmpty()) {
                repository.insertHeaders(headers)
            }

            val ingredients = repository.getIngredients()
            if (ingredients != null && ingredients.isNotEmpty()) {
                repository.insertIngredients(ingredients)
            }

            val measureUnits = repository.getMeasureUnits()
            if (measureUnits != null && measureUnits.isNotEmpty()) {
                repository.insertMeasureUnits(measureUnits)
            }

            val altIngredients = repository.getRecipeAltIngredient()
            if (altIngredients != null && altIngredients.isNotEmpty()) {
                repository.insertRecipeAltIngredients(altIngredients)
            }

            val recipeIngredients = repository.getRecipeIngredients()
            if (recipeIngredients != null && recipeIngredients.isNotEmpty()) {
                repository.insertRecipeIngredients(recipeIngredients)
            }

            val recipeGroupSubgroups = repository.getRecipeGroupSubgroups()
            if (recipeGroupSubgroups != null && recipeGroupSubgroups.isNotEmpty()) {
                repository.insertRecipeGroupSubgroups(recipeGroupSubgroups)
            }

            val recipes = repository.getRecipes()
            if (recipes != null && recipes.isNotEmpty()) {
                repository.insertRecipes(recipes)
            }

            //Load Images & Save them
            if (recipes != null && recipes.isNotEmpty()) {
                val namesList: ArrayList<String> = ArrayList()

                for (recipe in recipes) {

                    val recId = recipe.id
                    val fileName = getFileName(recId)
                    namesList.add(fileName)
                }
                imgFileNameLiveData.postValue(namesList)
            }

            fetchGroupsSubgroupsFromDB()
        }
    }

    fun isImgLoaded(): LiveData<Boolean> {
        return repository.isImgLoaded()
    }

    fun loadAllImages(context: Context, fileNames: ArrayList<String>) {
        var isLastImg = false
        for (i in 0 until fileNames.size) {
            if(i == fileNames.size - 1) isLastImg = true
            startImgLoading(context, fileNames[i], isLastImg)
        }
    }

    private fun startImgLoading(context: Context, fileName: String, isLastImg: Boolean) {
        scope.launch {
            repository.loadImage(context, fileName, isLastImg)
        }
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

    fun fetchGroupsSubgroupsFromDB() {
        scope.launch {
            val expandableList = LinkedHashMap<Group, List<Subgroup>>()
            val groups = repository.selectAllGroups()

            for (group in groups) {
                val subgroupsById = repository.selectSubgroupsByGroupId(group.id)
                expandableList[group] = subgroupsById
            }
            expandableListLiveData.postValue(expandableList)
        }
    }

    fun cancelAllRequests() = coroutineContext.cancel()

}