package com.example.tapasoft.recipekotlin.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tapasoft.recipekotlin.api.ApiClient
import com.example.tapasoft.recipekotlin.database.RecipeRoomDatabase
import com.example.tapasoft.recipekotlin.model.CookingStep
import com.example.tapasoft.recipekotlin.repository.RecipeRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class FavouritesViewModel(application: Application) : AndroidViewModel(application) {
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO

    private val scope = CoroutineScope(coroutineContext)

    val recipeDao = RecipeRoomDatabase.getDatabase(application).recipeDao()
    private val repository = RecipeRepository(ApiClient.clientApi, recipeDao)

    val cookingStepListLiveData = MutableLiveData<List<CookingStep>>()
    private val isDeletedLiveData = MutableLiveData<Boolean>()

    fun loadSaveCookingSteps(rcpId: Int, context: Context) {
        scope.launch {

            val cookingSteps = repository.loadCookingSteps(rcpId)

            //DB insertion
            if (cookingSteps != null && cookingSteps.isNotEmpty()) {
                repository.insertCookingStepsIntoDB(cookingSteps)

                //image loading
                var step = 1
                for (cookingStep in cookingSteps) {
                    val fileName = getFileName(rcpId, step)
                    repository.loadCookingStepImage(context, fileName)
                    step++
                }

                fetchCookingStepsFromDB(rcpId)
            }
        }
    }

    fun deleteCookingSteps(rcpId: Int) {
        scope.launch {
            repository.deleteCookingStepsByRecId(rcpId)
            isDeletedLiveData.postValue(true)
        }
    }

    fun fetchCookingStepsFromDB(rcpId: Int) {
        scope.launch {
            val cookingStepsDB = repository.selectCookingSteps(rcpId)
            var step = 1
            for (cookingStep in cookingStepsDB) {
                val fileName = getFileName(rcpId, step)
                cookingStep.imgFileName = fileName
                step++
            }
            cookingStepListLiveData.postValue(cookingStepsDB)
        }
    }

    private fun getFileName(recId: Int, stepId: Int): String {
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

    fun areStepsDeleted(): LiveData<Boolean> {
        return isDeletedLiveData
    }

    fun cancelAllRequests() = coroutineContext.cancel()

}