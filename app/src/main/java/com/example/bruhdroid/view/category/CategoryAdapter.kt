package com.example.bruhdroid.view.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bruhdroid.R
import com.example.bruhdroid.view.category.CategoryAdapter.CategoryViewHolder

class CategoryAdapter(
    private var context: Context,
    private var categories: List<Category>,
    private var onCategoryListener: OnCategoryListener
) :
    RecyclerView.Adapter<CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val categoryItems =
            LayoutInflater.from(context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(categoryItems, onCategoryListener)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.categoryTitle.text = categories[position].title
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    class CategoryViewHolder(itemView: View, private var onCategoryListener: OnCategoryListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var categoryTitle: TextView = itemView.findViewById(R.id.category_title)
        override fun onClick(view: View) {
            onCategoryListener.onCategoryClick(adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface OnCategoryListener {
        fun onCategoryClick(position: Int)
    }
}