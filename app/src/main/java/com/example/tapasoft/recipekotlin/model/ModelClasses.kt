package com.example.tapasoft.recipekotlin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by udav on 26-Jul-19.
 */
@Entity(tableName = "groups")
data class Group(@PrimaryKey(autoGenerate = true)
                 @SerializedName("id") var id: Int = 0,
                 @SerializedName("ob") val ob: Int,
                 @SerializedName("name") val name: String)

@Entity(tableName = "subgroups")
data class Subgroup(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                 @SerializedName("grp_id") val grpId: Int = 0,
                 @SerializedName("sgp_id") var sgpId: Int = 0,
                 @SerializedName("ob") val ob: Int,
                 @SerializedName("name") val name: String)

@Entity(tableName = "headers")
data class Header(@PrimaryKey(autoGenerate = true) @SerializedName("id") var id: Int = 0,
                  @SerializedName("name") val name: String)

@Entity(tableName = "ingredients")
data class Ingredient(@PrimaryKey(autoGenerate = true) @SerializedName("id") var id: Int = 0,
                  @SerializedName("name") val name: String)

@Entity(tableName = "measure_units")
data class MeasureUnit(@PrimaryKey(autoGenerate = true) @SerializedName("id") var id: Int = 0,
                      @SerializedName("name") val name: String)

@Entity(tableName = "recipe_group_subgroups")
data class RecipeGroupSubgroup(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                       @SerializedName("rec_id") val recId: Int = 0,
                       @SerializedName("grp_id") val grpId: Int = 0,
                       @SerializedName("sgr_id") val subGrpId: Int = 0)

@Entity(tableName = "recipes")
@Parcelize
data class Recipe(@PrimaryKey(autoGenerate = true) @SerializedName("id") var id: Int = 0,
                    @SerializedName("grp_id") var grpId: Int = 0,
                    @SerializedName("duration") var duration: Int = 0,
                    @SerializedName("name") var name: String = "",
                    @SerializedName("description") var description: String = "",
                    @Ignore var ingredients: String = "",
                    @Ignore var ingredientsDtl: String = "",
                    @Ignore var summary: String = "",
                    @Ignore var imgFileName: String = "",
                    @Ignore var cookingSteps: List<CookingStep>? = null,
                    var favourite: Int = 0): Parcelable

@Parcelize
@Entity(tableName = "cooking_steps")
data class CookingStep(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                               @SerializedName("rec_id") var recId: Int = 0,
                               @SerializedName("ob") var ob: Int = 0,
                               @SerializedName("dsc") var step: String = "",
                               @Ignore var imgFileName: String = ""): Parcelable

@Entity(tableName = "recipe_alt_ingredients")
data class RecipeAltIngredient(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                  @SerializedName("rec_id") val recId: Int = 0,
                  @SerializedName("ob") val ob: Int,
                  @SerializedName("dfl_f") val defaultFlag: String,
                  @SerializedName("ing_id") val ingrId: Int = 0,
                  @SerializedName("meu_flag") val measureFlag: String,
                  @SerializedName("meu_id") val measureId: Int = 0,
                  @SerializedName("meu_1") val value1: Int = 0,
                  @SerializedName("meu_2") val value2: Int = 0)

@Entity(tableName = "recipe_ingredients")
data class RecipeIngredient(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                               @SerializedName("rec_id") var recId: Int = 0,
                               @SerializedName("ob") var ob: Int = 0,
                               @SerializedName("inh_flag") var ingrFlag: String = "",
                               @SerializedName("inh_id") var ingrId: Int = 0,
                               @SerializedName("meu_flag") var measureFlag: String = "",
                               @SerializedName("meu_id") var measureId: Int = 0,
                               @SerializedName("meu_1") var value1: Int = 0,
                               @SerializedName("meu_2") var value2: Int = 0,
                               @SerializedName("alt_flag") var altFlag: String = "",
                               @SerializedName("remark") var remark: String = "",
                               @Ignore var name: String = "",
                               @Ignore var measureName: String = "")
