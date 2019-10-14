package com.example.tapasoft.recipekotlin.activity

import android.app.SearchManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tapasoft.recipekotlin.Config
import com.example.tapasoft.recipekotlin.R
import com.example.tapasoft.recipekotlin.adapter.RecipeListAdapter
import com.example.tapasoft.recipekotlin.messages.MessageEvent
import com.example.tapasoft.recipekotlin.model.Recipe
import com.example.tapasoft.recipekotlin.viewmodel.RecipeListViewModel
import kotlinx.android.synthetic.main.activity_recipe_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RecipeListActivity : AppCompatActivity() {
    companion object {
        const val KEY_GROUP_ID = "key_group_id"
        const val KEY_SUBGROUP_ID = "key_subgroup_id"
        const val KEY_TITLE_ID = "key_title_id"
        const val KEY_RECIPE_ID = "key_recipe_id"
    }

    private lateinit var adapterIn: RecipeListAdapter
    private var toolbar: Toolbar? = null
    private lateinit var viewModel: RecipeListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        createToolbar()

        val groupId: Int = intent.getIntExtra(KEY_GROUP_ID, 0)
        val subgroupId: Int = intent.getIntExtra(KEY_SUBGROUP_ID, 0)
        val title: String = intent.getStringExtra(KEY_TITLE_ID)
        setTitle(title)

        viewModel = ViewModelProviders.of(this).get(RecipeListViewModel::class.java)
        viewModel.fetchRecipeListFromDB(groupId, subgroupId, this)

        viewModel.recipeListLiveData.observe(this, Observer {
            recipeRecyclerView.layoutManager = LinearLayoutManager(this)

            val cw = ContextWrapper(this)
            val directory = cw.getDir(Config.INTERNAL_IMG_FOLDER, Context.MODE_PRIVATE)
            adapterIn = RecipeListAdapter(it, directory) {
                startRecipeDetailsActivity(it)
            }
            recipeRecyclerView.adapter = adapterIn
        })
    }

    private fun startRecipeDetailsActivity(recipe: Recipe) {
        val intent = Intent(this, RecipeDetailsActivity::class.java)
        intent.putExtra(KEY_RECIPE_ID, recipe)
        startActivity(intent)
    }

    public override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar?.setNavigationOnClickListener { finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                adapterIn.filter.filter(query)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                adapterIn.filter.filter(query)
                return true
            }
        }

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.mn_search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(true)
            setOnQueryTextListener(queryTextListener)
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelAllRequests()
    }

    private fun createToolbar() {
        if (toolbar == null) {
            toolbar = findViewById<View>(R.id.toolbar) as Toolbar
            if (toolbar != null) {
                setSupportActionBar(toolbar)
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageEvent) {
        val adapter = recipeRecyclerView.adapter as RecipeListAdapter
        adapter.updateFavourite(event.message)
        EventBus.getDefault().removeStickyEvent(event)
    }

}
