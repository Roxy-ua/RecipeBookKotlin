package com.example.tapasoft.recipekotlin.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tapasoft.recipekotlin.model.CookingStep
import com.example.tapasoft.recipekotlin.repository.RecipeRepository
import com.example.tapasoft.recipekotlin.repository.RecipeRepositoryImpl
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RecipeDetailsViewModel(private val repository: RecipeRepository = RecipeRepositoryImpl()) :
        ViewModel() {
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO

    private val scope = CoroutineScope(coroutineContext)

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
        //isDeletedLiveData.value = false
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

    fun setFavouriteInDB(favour: Int, recId: Int) {
        scope.launch {
            repository.setRecipeFavourite(favour, recId)
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

    fun areStepsDeleted(): LiveData<Boolean> = isDeletedLiveData

    fun cancelAllRequests() = coroutineContext.cancel()
}