package com.example.tapasoft.recipekotlin.activity

import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.tapasoft.recipekotlin.Config
import com.example.tapasoft.recipekotlin.R
import com.example.tapasoft.recipekotlin.messages.MessageEvent
import com.example.tapasoft.recipekotlin.model.CookingStep
import com.example.tapasoft.recipekotlin.model.Recipe
import com.example.tapasoft.recipekotlin.utils.Utils
import com.example.tapasoft.recipekotlin.viewmodel.RecipeDetailsViewModel
import kotlinx.android.synthetic.main.activity_recipe_details.*
import kotlinx.android.synthetic.main.content_details_scrolling.*
import kotlinx.android.synthetic.main.recipe_list_item.recIng
import kotlinx.android.synthetic.main.recipe_list_item.recName
import kotlinx.android.synthetic.main.recipe_list_item.recSmr
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import java.io.File


class RecipeDetailsActivity : AppCompatActivity() {
    companion object {
        const val FAVOURITE_OFF = 0
        const val FAVOURITE_ON = 1
    }
    private lateinit var viewModel: RecipeDetailsViewModel
    private var toolbar: Toolbar? = null
    private var isStepsLoaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        val cw = ContextWrapper(this)
        val directory = cw.getDir(Config.INTERNAL_IMG_FOLDER, Context.MODE_PRIVATE)

        viewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel::class.java)

        val recipe: Recipe = intent.getParcelableExtra(RecipeListActivity.KEY_RECIPE_ID)

        createToolbar()
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        toolbar?.setNavigationOnClickListener { finish() }
        toolbar?.title = recipe.name

        //Add content
        setDishImage(recipe.imgFileName, directory)
        collapsingToolbar.title = recipe.name
        recName.text = recipe.name
        recIng.text = HtmlCompat.fromHtml(recipe.ingredientsDtl, HtmlCompat.FROM_HTML_MODE_LEGACY)
        recSmr.text = recipe.summary
        if (recipe.description.trim().isEmpty()) {
            recDsc.visibility = View.GONE
        } else {
            recDsc.text = recipe.description
        }

        loadCookingSteps.setOnClickListener { view ->
            progressBar.visibility = View.VISIBLE
            if (isStepsLoaded) {
                viewModel.deleteCookingSteps(recipe.id)
            } else {
                viewModel.loadSaveCookingSteps(recipe.id, this)
            }
        }

        progressBar.visibility = View.VISIBLE
        viewModel.fetchCookingStepsFromDB(recipe.id)
        viewModel.cookingStepListLiveData.observe(this, Observer {
            progressBar.visibility = View.GONE
            if (it.isNotEmpty()) {
                isStepsLoaded = true
                fillCookingStepTable(it, directory)
            }
        })

        viewModel.areStepsDeleted().observe(this, Observer { deleted ->
            if (deleted!!) {
                progressBar.visibility = View.GONE
                stepsTableLayout.removeAllViews()
                stepsTableLayout.visibility = View.GONE
                loadCookingSteps.text = HtmlCompat.fromHtml(getString(R.string.cooking_steps_load),
                        HtmlCompat.FROM_HTML_MODE_LEGACY)
                isStepsLoaded = false
            }
        })

        //Favourite
        if(recipe.favourite == FAVOURITE_ON) {
            setFavouriteFab(R.drawable.ic_favorite_on, R.color.white)
        } else {
            setFavouriteFab(R.drawable.ic_favorite_off, R.color.colorAccent)
        }

        fab.setOnClickListener { _ ->
            if(recipe.favourite == FAVOURITE_OFF) {
                viewModel.setFavouriteInDB(FAVOURITE_ON, recipe.id)
                toast(getString(R.string.add_to_favourites))
                setFavouriteFab(R.drawable.ic_favorite_on, R.color.white)
                recipe.favourite = FAVOURITE_ON
            } else {
                viewModel.setFavouriteInDB(FAVOURITE_OFF, recipe.id)
                toast(getString(R.string.remove_from_favourites))
                setFavouriteFab(R.drawable.ic_favorite_off, R.color.colorAccent)
                recipe.favourite = FAVOURITE_OFF
            }
            val messageToSent = MessageEvent(recipe.favourite)
            EventBus.getDefault().postSticky(messageToSent)
        }
    }

    private fun setFavouriteFab(imageRes: Int, colorRes: Int) {
        fab.hide()
        fab.setImageResource(imageRes)
        fab.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, colorRes))
        fab.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelAllRequests()
    }

    private fun fillCookingStepTable(cookingSteps: List<CookingStep>, directory: File) {
        stepsTableLayout.visibility = View.VISIBLE

        loadCookingSteps.paintFlags = loadCookingSteps.paintFlags or UNDERLINE_TEXT_FLAG
        loadCookingSteps.text = getString(R.string.cooking_steps_delete)

        val dipRight = resources.getDimension(R.dimen.cooking_step_img_padding_right).toInt()
        val paddingRight = Utils.convertDipToPixels(this, dipRight)

        val dipBottom = resources.getDimension(R.dimen.cooking_step_img_padding_bottom).toInt()
        val paddingBottom = Utils.convertDipToPixels(this, dipBottom)

        for (cookingStep in cookingSteps) {
            addRowToCookingStepTable(cookingStep.step, cookingStep.imgFileName, directory,
                    paddingRight, paddingBottom)
        }
    }

    private fun addRowToCookingStepTable(stepText: String, imgFileName: String,
                                         directory: File, paddingRight: Int, paddingBottom: Int) {
        val tableRow = TableRow(this)

        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
        tableRow.layoutParams = layoutParams

        val stepImage = ImageView(this)
        stepImage.setPadding(0, 0, paddingRight, paddingBottom)

        val file = File(directory, imgFileName)
        val bmOptions = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(file.absolutePath, bmOptions)
        stepImage.setImageBitmap(bitmap)
        tableRow.addView(stepImage, 0)

        val stepTextView = TextView(this)
        stepTextView.text = stepText
        stepTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        stepTextView.setTextColor(ContextCompat.getColor(this, R.color.black_brown))
        stepTextView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT, 5f)
        stepTextView.setPadding(0, 0, 0, paddingRight)
        tableRow.addView(stepTextView, 1)
        stepsTableLayout.addView(tableRow)
    }

    private fun createToolbar() {
        if (toolbar == null) {
            toolbar = findViewById<View>(R.id.toolbar) as Toolbar
            if (toolbar != null) {
                setSupportActionBar(toolbar)
            }
        }
    }

    private fun setDishImage(imgFileName: String, directory: File) {
        val file = File(directory, imgFileName)
        dishImage?.setImageDrawable(Drawable.createFromPath(file.toString()))
    }
}
