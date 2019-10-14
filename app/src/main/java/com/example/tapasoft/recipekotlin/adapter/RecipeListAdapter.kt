package com.example.tapasoft.recipekotlin.adapter

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.recyclerview.widget.RecyclerView
import com.example.tapasoft.recipekotlin.R
import com.example.tapasoft.recipekotlin.activity.RecipeDetailsActivity
import com.example.tapasoft.recipekotlin.model.Recipe
import java.io.File


class RecipeListAdapter(var items: List<Recipe>, val directory: File, val listener: (Recipe) -> Unit)
    : RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>(), Filterable {

    private val recipeList: List<Recipe> = items
    var curPos: Int = 0

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe: Recipe = items[position]
        holder.bind(recipe, directory, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecipeViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    items = recipeList
                } else {
                    val filteredList = ArrayList<Recipe>()
                    for (row in recipeList) {

                        if (row.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }

                    items = filteredList
                }


                val filterResults = FilterResults()
                filterResults.values = items
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                items = filterResults.values as ArrayList<Recipe>
                notifyDataSetChanged()
            }
        }
    }

    fun updateFavourite(favourValue: Int) {
        val item = items[curPos]
        item.favourite = favourValue
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.recipe_list_item, parent, false)) {

        private var recNameView: TextView? = null
        private var recIngrView: TextView? = null
        private var recSmrView: TextView? = null
        private var recImgView: ImageView? = null

        init {
            recNameView = itemView.findViewById(R.id.recName)
            recIngrView = itemView.findViewById(R.id.recIng)
            recSmrView = itemView.findViewById(R.id.recSmr)
            recImgView = itemView.findViewById(R.id.recImg)
        }

        fun bind(recipe: Recipe, directory: File, listener: (Recipe) -> Unit) = with(itemView) {
            recIngrView?.text = recipe.ingredients
            recSmrView?.text = recipe.summary
            setOnClickListener {
                curPos = adapterPosition
                listener(recipe)
            }

            val file = File(directory, recipe.imgFileName)
            recImgView?.setImageDrawable(Drawable.createFromPath(file.toString()))

            if (recipe.favourite == RecipeDetailsActivity.FAVOURITE_ON) {
                val spanString = SpannableString(recipe.name + "   ")
                spanString.setSpan(ImageSpan(context, R.drawable.ic_favorite_on),
                        spanString.length - 2, spanString.length,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                recNameView?.setText(spanString, BufferType.SPANNABLE)
            } else {
                recNameView?.text = recipe.name
            }
        }
    }
}