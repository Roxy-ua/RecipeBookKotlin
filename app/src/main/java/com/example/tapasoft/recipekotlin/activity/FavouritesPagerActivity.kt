package com.example.tapasoft.recipekotlin.activity

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.tapasoft.recipekotlin.Config
import com.example.tapasoft.recipekotlin.R
import com.example.tapasoft.recipekotlin.messages.MessageEvent
import com.example.tapasoft.recipekotlin.model.CookingStep
import com.example.tapasoft.recipekotlin.model.Recipe
import com.example.tapasoft.recipekotlin.utils.Utils
import com.example.tapasoft.recipekotlin.viewmodel.RecipeDetailsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_favourites_pager.*
import org.greenrobot.eventbus.EventBus
import java.io.File


private const val KEY_ITEM_RECIPE = "key_item_recipe"
const val KEY_RECIPES_ID = "key_recipes_id"
const val KEY_POSITION_ID = "key_position_id"

class FavouritesPagerActivity : AppCompatActivity() {
    private lateinit var viewModel: RecipeDetailsViewModel
    private lateinit var viewPager: ViewPager2
    private var toolbar: Toolbar? = null
    var pagerAdapter: MyAdapter? = null
    var pagerPos: Int = 0
    var recipeId: Int = 0
    private var recipesIn: List<Recipe>? = null
    var mediator: TabLayoutMediator? = null
    private var isMediatorDetached: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites_pager)

        viewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel::class.java)

        createToolbar()
        viewPager = findViewById(R.id.viewPager)

        val position = intent.getIntExtra(KEY_POSITION_ID, 0)
        recipesIn = intent.getParcelableArrayListExtra<Recipe>(KEY_RECIPES_ID)
        if (recipesIn != null) {
            val recipes = recipesIn!!
            val items: MutableList<Recipe> = recipes.toMutableList()
            pagerAdapter = MyAdapter(this, items)
            viewPager.adapter = pagerAdapter

            //connect ViewPager & TabLayout
            mediator = TabLayoutMediator(tabLayout, viewPager) { _, _ ->
            }
            mediator!!.attach()

            viewPager.setCurrentItem(position, true)
            toolbar?.title = items[position].name
            pagerPos = position
            recipeId = items[position].id

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    toolbar?.title = items[position].name
                    pagerPos = position
                    recipeId = items[position].id
                    if (isMediatorDetached) {
                        mediator!!.attach()
                        isMediatorDetached = false
                    }
                }
            })
        }
    }

    private fun createToolbar() {
        if (toolbar == null) {
            toolbar = findViewById<View>(R.id.toolbar) as Toolbar
            if (toolbar != null) {
                setSupportActionBar(toolbar)
                toolbar?.setNavigationOnClickListener { finish() }
            }
        }

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(true)
            supportActionBar?.title = "Toolbar title"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_favourites, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mn_delete -> {
                val position: Int
                if (pagerPos == (pagerAdapter!!.itemCount) - 1) {
                    position = pagerPos - 1

                    mediator!!.detach()
                    isMediatorDetached = true
                } else {
                    position = pagerPos + 1
                }
                toolbar?.title = recipesIn?.get(position)?.name

                pagerAdapter?.deletePage(pagerPos)
                viewModel.setFavouriteInDB(RecipeDetailsActivity.FAVOURITE_OFF, recipeId)

                val messageToSent = MessageEvent()
                EventBus.getDefault().postSticky(messageToSent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

class MyAdapter(fa: FragmentActivity, val recipes: MutableList<Recipe>) : FragmentStateAdapter(fa) {
    private var baseId: Long = 0

    override fun createFragment(position: Int): Fragment {
        return PageFragment.create(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    override fun getItemId(position: Int): Long = baseId + position

    private fun notifyChangeInPosition(n: Int) {
        // shift the ID returned by getItemId outside the range of all previous fragments
        baseId += itemCount + n
    }

    fun deletePage(position: Int) {
        recipes.removeAt(position)
        notifyChangeInPosition(1)
        notifyItemRemoved(position)
    }
}

class PageFragment : Fragment() {
    private lateinit var txtViewRecName: TextView
    private lateinit var txtViewRecIng: TextView
    private lateinit var txtViewRecSmr: TextView
    private lateinit var txtViewRecDsc: TextView
    private lateinit var txtViewLoadSteps: TextView
    private lateinit var imgViewDish: ImageView
    private lateinit var stepsTableLayout: TableLayout

    private var isStepsLoaded: Boolean = false
    private var recId: Int = 0
    private var recipeCopy: Recipe? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pager_favourites, container, false)

        txtViewRecName = view.findViewById(R.id.recName)
        txtViewRecIng = view.findViewById(R.id.recIng)
        txtViewRecSmr = view.findViewById(R.id.recSmr)
        txtViewRecDsc = view.findViewById(R.id.recDsc)
        txtViewLoadSteps = view.findViewById(R.id.loadCookingSteps)
        imgViewDish = view.findViewById(R.id.dishImage)
        stepsTableLayout = view.findViewById(R.id.stepsTableLayout)

        recipeCopy = arguments?.getParcelable(KEY_ITEM_RECIPE) as Recipe
        val recipe = recipeCopy
        if (recipe != null) {
            recId = recipe.id
            txtViewRecName.text = recipe.name
            txtViewRecIng.text = HtmlCompat.fromHtml(recipe.ingredientsDtl, HtmlCompat.FROM_HTML_MODE_LEGACY)
            txtViewRecSmr.text = recipe.summary
            if (recipe.description.trim().isEmpty()) {
                txtViewRecDsc.visibility = View.GONE
            } else {
                txtViewRecDsc.text = recipe.description
            }
            //set dish image
            val cw = ContextWrapper(activity)
            val directory = cw.getDir(Config.INTERNAL_IMG_FOLDER, Context.MODE_PRIVATE)
            val file = File(directory, recipe.imgFileName)
            imgViewDish.setImageDrawable(Drawable.createFromPath(file.toString()))

            //cooking steps
            if (recipe.cookingSteps != null && recipe.cookingSteps!!.isNotEmpty()) {
                fillCookingStepTable(recipe.cookingSteps as List<CookingStep>, getDirectory(), context!!)
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel::class.java)

        txtViewLoadSteps.setOnClickListener {
            if (isStepsLoaded) {
                viewModel.deleteCookingSteps(recId)
            } else {
                viewModel.loadSaveCookingSteps(recId, context!!)
            }
        }

        viewModel.areStepsDeleted().observe(viewLifecycleOwner, Observer { deleted ->
            if (deleted!!) {
                stepsTableLayout.removeAllViews()
                stepsTableLayout.visibility = View.GONE
                txtViewLoadSteps.text = HtmlCompat.fromHtml(getString(R.string.cooking_steps_load),
                        HtmlCompat.FROM_HTML_MODE_LEGACY)
                isStepsLoaded = false
                recipeCopy?.cookingSteps = null
            }
        })
    }

    private fun getDirectory(): File {
        val cw = ContextWrapper(activity)
        return cw.getDir(Config.INTERNAL_IMG_FOLDER, Context.MODE_PRIVATE)
    }

    private fun fillCookingStepTable(cookingSteps: List<CookingStep>, directory: File,
                                     context: Context) {
        isStepsLoaded = true
        stepsTableLayout.visibility = View.VISIBLE

        txtViewLoadSteps.paintFlags = txtViewLoadSteps.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        txtViewLoadSteps.text = getString(R.string.cooking_steps_delete)

        val dipRight = resources.getDimension(R.dimen.cooking_step_img_padding_right).toInt()
        val paddingRight = Utils.convertDipToPixels(context, dipRight)

        val dipBottom = resources.getDimension(R.dimen.cooking_step_img_padding_bottom).toInt()
        val paddingBottom = Utils.convertDipToPixels(context, dipBottom)

        for (cookingStep in cookingSteps) {
            addRowToCookingStepTable(cookingStep.step, cookingStep.imgFileName, directory,
                    paddingRight, paddingBottom, context)
        }
    }

    private fun addRowToCookingStepTable(stepText: String, imgFileName: String,
                                         directory: File, paddingRight: Int, paddingBottom: Int,
                                         context: Context) {
        val tableRow = TableRow(context)

        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
        tableRow.layoutParams = layoutParams

        val stepImage = ImageView(context)
        stepImage.setPadding(0, 0, paddingRight, paddingBottom)
        val file = File(directory, imgFileName)
        stepImage.setImageDrawable(Drawable.createFromPath(file.toString()))
        tableRow.addView(stepImage, 0)

        val stepTextView = TextView(context)
        stepTextView.text = stepText
        stepTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        stepTextView.setTextColor(ContextCompat.getColor(context, R.color.black_brown))
        stepTextView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 5f)
        stepTextView.setPadding(0, 0, 0, paddingRight)
        tableRow.addView(stepTextView, 1)

        stepsTableLayout.addView(tableRow)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //outState.putInt(KEY_CLICK_COUNT, clickCount())
    }

    companion object {
        fun create(itemRecipe: Recipe) =
                PageFragment().apply {
                    arguments = Bundle(1).apply {
                        putParcelable(KEY_ITEM_RECIPE, itemRecipe)
                    }
                }
    }
}

