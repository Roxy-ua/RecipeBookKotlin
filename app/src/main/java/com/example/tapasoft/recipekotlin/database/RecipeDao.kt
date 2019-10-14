package com.example.tapasoft.recipekotlin.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.tapasoft.recipekotlin.model.*

@Dao
interface RecipeDao {

    @Query("SELECT * from groups ORDER BY id")
    fun getAllGroups(): List<Group>

    @Query("SELECT rcp.id, rcp.name, rcp.duration, rcp.grpId, rcp.description, " +
            "rcp.favourite FROM recipes AS rcp " +
            "INNER JOIN recipe_group_subgroups AS grpSgp ON rcp.id = grpSgp.recId " +
            "WHERE grpSgp.grpId = :grpId AND grpSgp.subGrpId = :sgpId ORDER BY rcp.name")
    fun findRecipesByGrpSgpId(grpId: Int, sgpId: Int): List<Recipe>

    @Query("SELECT rcp.id, rcp.name, rcp.duration, rcp.grpId, rcp.description, " +
            "rcp.favourite FROM recipes AS rcp " +
            //"INNER JOIN recipe_group_subgroups AS grpSgp ON rcp.id = grpSgp.recId " +
            "WHERE rcp.grpId = :grpId ORDER BY rcp.name")
    fun findRecipesByGrpId(grpId: Int): List<Recipe>

    @Query("SELECT id, name, duration, grpId, description, favourite FROM recipes " +
            "WHERE favourite = :fvrFlag ORDER BY grpId")
    fun findRecipesByFavouriteFlag(fvrFlag: Int): List<Recipe>

    @Query("SELECT * FROM recipe_ingredients AS ingr " +
            "WHERE ingr.recId = :rcpId AND ingr.ingrFlag LIKE 'I'")
    fun findIngredientsByRcpId(rcpId: Int): List<RecipeIngredient>

    @Query("SELECT name from ingredients WHERE id = :ingrId")
    fun selectIngredientName(ingrId: Int): String

    @Query("SELECT name from measure_units WHERE id = :measureId")
    fun selectMeasureName(measureId: Int): String

    @Query("SELECT * FROM cooking_steps WHERE recId = :rcpId ORDER BY ob")
    fun findCookingStepsByRcpId(rcpId: Int): List<CookingStep>

    @Insert
    fun insertCookingSteps(cookingSteps: List<CookingStep>)

    @Delete
    fun deleteCookingSteps(cookingSteps: List<CookingStep>)

    @Query("DELETE FROM cooking_steps WHERE recId = :recId")
    fun deleteByRecipeId(recId: Int)

    @Query("UPDATE recipes SET favourite = :favour WHERE id = :rec_id")
    fun updateRecipeFavourite(favour: Int, rec_id: Int)

    @Query("SELECT rcp.id, rcp.name, rcp.duration, rcp.grpId, rcp.description, " +
            "rcp.favourite FROM recipes AS rcp " +
            //"INNER JOIN recipe_group_subgroups AS grpSgp ON rcp.id = grpSgp.recId " +
            "WHERE rcp.name like :queryString ORDER BY rcp.name")
    fun searchRecipesByName(queryString: String): List<Recipe>
}