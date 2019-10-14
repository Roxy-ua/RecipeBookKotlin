package com.example.tapasoft.recipekotlin.fragment

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tapasoft.recipekotlin.Config
import com.example.tapasoft.recipekotlin.R
import com.example.tapasoft.recipekotlin.activity.FavouritesPagerActivity
import com.example.tapasoft.recipekotlin.activity.KEY_POSITION_ID
import com.example.tapasoft.recipekotlin.activity.KEY_RECIPES_ID
import com.example.tapasoft.recipekotlin.adapter.FavouritesListAdapter
import com.example.tapasoft.recipekotlin.messages.MessageEvent
import com.example.tapasoft.recipekotlin.model.Recipe
import com.example.tapasoft.recipekotlin.viewmodel.RecipeListViewModel
import kotlinx.android.synthetic.main.fragment_favourites.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FavouritesFragment : Fragment() {
    private lateinit var viewModel: RecipeListViewModel
    private var progressBar: ProgressBar? = null
    private var emptyListMessage: TextView? = null
    lateinit var adapterIn: FavouritesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        val context = activity as FragmentActivity

        progressBar = view.findViewById(R.id.progress_bar) as ProgressBar
        emptyListMessage = view.findViewById(R.id.empty_state_msg) as TextView


        viewModel = ViewModelProviders.of(this).get(RecipeListViewModel::class.java)
        viewModel.fetchFavouriteListFromDB(context)

        viewModel.recipeListLiveData.observe(this, Observer {
            favouriteRecyclerView.layoutManager = LinearLayoutManager(context)

            val cw = ContextWrapper(context)
            val directory = cw.getDir(Config.INTERNAL_IMG_FOLDER, Context.MODE_PRIVATE)
            adapterIn = FavouritesListAdapter(it, directory) {
                startRecipePagerActivity(adapterIn.getRecipeList(), it)
            }
            favouriteRecyclerView.adapter = adapterIn
        })

        return view
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun startRecipePagerActivity(recipes: List<Recipe>, position: Int) {
        val intent = Intent(activity, FavouritesPagerActivity::class.java)
        intent.putParcelableArrayListExtra(KEY_RECIPES_ID, recipes as java.util.ArrayList<out Parcelable>)
        intent.putExtra(KEY_POSITION_ID, position)
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(): FavouritesFragment = FavouritesFragment()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageEvent) {
        viewModel.fetchFavouriteListFromDB(context!!)
    }
}
