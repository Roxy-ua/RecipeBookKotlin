package com.example.tapasoft.recipekotlin.api

import com.example.tapasoft.recipekotlin.model.*
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import com.google.gson.JsonObject
import retrofit2.http.Query


/**
 * Created by udav on 27-Jul-19.
 */
interface ApiInterface {

    @GET("groups.php")
    //fun getGroupsAsync(): Call<List<Group>>
    fun getGroupsAsync() : Deferred<Response<List<Group>>>

    @GET("subgroups.php")
    fun getSubGroupsAsync() : Deferred<Response<List<Subgroup>>>

    @GET("headers.php")
    fun getHeadersAsync() : Deferred<Response<List<Header>>>

    @GET("ingredients.php")
    fun getIngredientsAsync() : Deferred<Response<List<Ingredient>>>

    @GET("measure_units.php")
    fun getMeasureUnitsAsync() : Deferred<Response<List<MeasureUnit>>>

    @GET("rec_alt_ingredients.php")
    fun getAltIngredientsAsync() : Deferred<Response<List<RecipeAltIngredient>>>

    @GET("rec_ingredients.php")
    fun getRecIngredientsAsync() : Deferred<Response<List<RecipeIngredient>>>

    @GET("rec_subgroups.php")
    fun getGroupSubgroupsAsync() : Deferred<Response<List<RecipeGroupSubgroup>>>

    @GET("recipes.php")
    fun getRecipesAsync() : Deferred<Response<List<Recipe>>>

    @GET("dtl.php")
    fun getCookingStepsAsync(@Query("recId") recId: Int) : Deferred<Response<List<CookingStep>>>
}
