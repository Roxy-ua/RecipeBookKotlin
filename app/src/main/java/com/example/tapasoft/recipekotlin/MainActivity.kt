package com.example.tapasoft.recipekotlin

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.tapasoft.recipekotlin.fragment.FavouritesFragment
import com.example.tapasoft.recipekotlin.fragment.GroupSubgroupFragment
import com.example.tapasoft.recipekotlin.viewmodel.DataViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.widget.SearchView

class MainActivity : AppCompatActivity(), GroupSubgroupFragment.OnListFragmentInteractionListener {
    private val TAG_GROUP = "group"
    private val TAG_FAVOURITES = "favourites"

    private var mActionBarToolbar: Toolbar? = null
    private lateinit var viewModel: DataViewModel
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var searchView: SearchView

    private var mSelectedNavDrawerItemId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDrawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        viewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        createActionBarToolbar()
        setupDrawer()

        viewModel.imgFileNameLiveData.observe(this, Observer {
            viewModel.loadAllImages(this, it)
        })

        viewModel.isImgLoaded().observe(this, Observer { loaded ->
            if (loaded!!) {
                progressBar.visibility = View.GONE
            }
        })
    }

    public override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupActionBar()
        if (mSelectedNavDrawerItemId == 0) {
            changeFragment(GroupSubgroupFragment.newInstance(), TAG_GROUP)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelAllRequests()
    }

    //Action Bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.mn_search).actionView as SearchView
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(true)
        }

        return true
    }

    override fun setTitle(title: CharSequence) {
        mActionBarToolbar?.title = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                return true
            }
            R.id.mn_search -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (isNavDrawerOpen()) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }

        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

    private fun isNavDrawerOpen(): Boolean {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START) ||
                mDrawerLayout.isDrawerOpen(GravityCompat.END)
    }

    private fun createActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = findViewById<View>(R.id.toolbar) as Toolbar
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar)
            }
        }
    }

    private fun setupActionBar() {
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun setupDrawer() {
        navigationView.menu.getItem(0).isChecked = true

        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()

            val fragment: Fragment

            when (menuItem.itemId) {

                R.id.nav_groups -> {
                    fragment = GroupSubgroupFragment.newInstance()
                    changeFragment(fragment, TAG_GROUP)
                    title = getString(R.string.menu_groups)
                    mSelectedNavDrawerItemId = 0
                    searchView.setOnQueryTextListener(null)
                }
                R.id.nav_favourites -> {
                    fragment = FavouritesFragment.newInstance()
                    changeFragment(fragment, TAG_FAVOURITES)
                    title = getString(R.string.menu_favourites)
                    mSelectedNavDrawerItemId = 1

                    val queryTextListener = object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(query: String): Boolean {
                            fragment.adapterIn.filter.filter(query)
                            return true
                        }

                        override fun onQueryTextSubmit(query: String): Boolean {
                            fragment.adapterIn.filter.filter(query)
                            return true
                        }
                    }
                    searchView.setOnQueryTextListener(queryTextListener)
                }
                R.id.nav_load_recipe -> {
                    progressBar.visibility = View.VISIBLE
                    viewModel.fetchDataFromServer()
                    Toast.makeText(this, "All data are loaded", Toast.LENGTH_LONG).show()
                    title = getString(R.string.menu_load_all)
                    mSelectedNavDrawerItemId = 2
                    searchView.setOnQueryTextListener(null)
                }
                R.id.nav_load_new_recipe -> {
                    Toast.makeText(this, "There are no new recipes", Toast.LENGTH_LONG).show()
                    title = getString(R.string.menu_load_new)
                    mSelectedNavDrawerItemId = 3
                    searchView.setOnQueryTextListener(null)
                }
                R.id.nav_setting -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show()
                    title = getString(R.string.menu_settings)
                    mSelectedNavDrawerItemId = 4
                    searchView.setOnQueryTextListener(null)
                }
            }

            true
        }
    }

    private fun changeFragment(fragment: Fragment, tag: String) {

        val fragmentManager = supportFragmentManager
        if (fragmentManager.findFragmentByTag(tag) == null) {
            clearBackStack()
        }

        // Make sure the current transaction finishes first
        fragmentManager.executePendingTransactions()

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(tag)
                .commitAllowingStateLoss()

        setupActionBar()
    }

    private fun clearBackStack() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            val entry = fragmentManager.getBackStackEntryAt(0)
            fragmentManager.popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}
