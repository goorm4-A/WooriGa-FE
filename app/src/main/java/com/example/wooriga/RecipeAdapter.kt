package com.example.wooriga

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class RecipeAdapter : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(DIFF_CALLBACK) {

    inner class RecipeViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.text_recipe_title)
        private val name = view.findViewById<TextView>(R.id.text_name)
        private val time = view.findViewById<TextView>(R.id.text_time)
        private val image = view.findViewById<ImageView>(R.id.image_recipe)

        fun bind(recipe: Recipe) {
            title.text = recipe.title
            name.text = recipe.author
            time.text = "${recipe.cookTimeMinutes}분"

            Glide.with(view.context)
                .load(recipe.coverImageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .centerCrop()
                .transform(RoundedCorners(10))
                .into(image)

            view.setOnClickListener {
                // TODO: 클릭 이벤트 처리
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
                oldItem == newItem
        }
    }
}
