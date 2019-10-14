package com.example.tapasoft.recipekotlin.activity

import android.app.SearchManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tapasoft.recipekotlin.Config
import com.example.tapasoft.recipekotlin.R
import com.example.tapasoft.recipekotlin.adapter.RecipeListAdapter
import com.example.tapasoft.recipekotlin.model.Recipe
import com.example.tapasoft.recipekotlin.viewmodel.RecipeListViewModel
import kotlinx.android.synthetic.main.activity_search_results.*


class SearchResultsActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private lateinit var viewModel: RecipeListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        createToolbar()
        title = getString(R.string.search_title)

        viewModel = ViewModelProviders.of(this).get(RecipeListViewModel::class.java)

        handleIntent(intent)

        viewModel.recipeListLiveData.observe(this, Observer {
            title = "${getString(R.string.search_results)} ${it.size}"
            resultRecyclerView.layoutManager = LinearLayoutManager(this)

            val cw = ContextWrapper(this)
            val directory = cw.getDir(Config.INTERNAL_IMG_FOLDER, Context.MODE_PRIVATE)
            val adapterIn = RecipeListAdapter(it, directory) {
                startRecipeDetailsActivity(it)
            }
            resultRecyclerView.adapter = adapterIn
        })
    }

    public override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar?.setNavigationOnClickListener { finish() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {

        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            viewModel.getSearchResultListFromDB(this, "%$query%")
        }
    }

    private fun createToolbar() {
        if (toolbar == null) {
            toolbar = findViewById<View>(R.id.toolbar) as Toolbar
            if (toolbar != null) {
                setSupportActionBar(toolbar)
            }
        }
    }

    private fun startRecipeDetailsActivity(recipe: Recipe) {
        val intent = Intent(this, RecipeDetailsActivity::class.java)
        intent.putExtra(RecipeListActivity.KEY_RECIPE_ID, recipe)
        startActivity(intent)
    }
}
