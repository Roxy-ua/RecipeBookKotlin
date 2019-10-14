package com.example.tapasoft.recipekotlin.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.tapasoft.recipekotlin.R
import com.example.tapasoft.recipekotlin.activity.RecipeListActivity
import com.example.tapasoft.recipekotlin.activity.RecipeListActivity.Companion.KEY_GROUP_ID
import com.example.tapasoft.recipekotlin.activity.RecipeListActivity.Companion.KEY_SUBGROUP_ID
import com.example.tapasoft.recipekotlin.activity.RecipeListActivity.Companion.KEY_TITLE_ID
import com.example.tapasoft.recipekotlin.adapter.GrpExpandableListAdapter
import com.example.tapasoft.recipekotlin.model.Group
import com.example.tapasoft.recipekotlin.viewmodel.DataViewModel

class GroupSubgroupFragment : Fragment() {
    private var progressBar: ProgressBar? = null
    private var emptyListMessage: TextView? = null
    private lateinit var viewModel: DataViewModel

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_group_subgroup, container, false)
        val context = activity as FragmentActivity

        progressBar = view.findViewById(R.id.progress_bar) as ProgressBar
        emptyListMessage = view.findViewById(R.id.empty_state_msg) as TextView
        val expandableListView = view.findViewById(R.id.expandableListView) as ExpandableListView

        viewModel = ViewModelProviders.of(context).get(DataViewModel::class.java)

        viewModel.fetchGroupsSubgroupsFromDB()

        viewModel.expandableListLiveData.observe(context, Observer {
            val expandableListGroup = ArrayList<Group>(it.keys)
            val expandableListAdapter = GrpExpandableListAdapter(context, expandableListGroup, it)
            expandableListView.setAdapter(expandableListAdapter)

            expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->

                val groupId = expandableListGroup[groupPosition].id
                val subgroupId = it[expandableListGroup[groupPosition]]!![childPosition].sgpId
                val title = it[expandableListGroup[groupPosition]]!![childPosition].name

                startRecipeListActivity(groupId, subgroupId, title)

                false
            }

            expandableListView.setOnGroupClickListener { parent, v, groupPosition, id ->
                if (expandableListAdapter.getChildrenCount(groupPosition) == 0) {
                    val groupId = expandableListGroup[groupPosition].id
                    val subgroupId = 0
                    val title = expandableListGroup[groupPosition].name

                    startRecipeListActivity(groupId, subgroupId, title)
                }

                false
            }

        })

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun startRecipeListActivity(groupId: Int, subgroupId: Int, title: String) {
        val intent = Intent(activity, RecipeListActivity::class.java)
        with(intent)
        {
            putExtra(KEY_GROUP_ID, groupId)
            putExtra(KEY_SUBGROUP_ID, subgroupId)
            putExtra(KEY_TITLE_ID, title)
        }
        startActivity(intent)
    }

    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        //fun onListFragmentInteraction(item: DummyItem?)
    }

    companion object {
        @JvmStatic
        fun newInstance(): GroupSubgroupFragment = GroupSubgroupFragment()
    }

}
