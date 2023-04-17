package com.example.cartbazaar.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.cartbazaar.R
import com.example.cartbazaar.activity.CategoryActivity
import com.example.cartbazaar.databinding.LayoutCategoryItemBinding
import com.example.cartbazaar.model.CategoryModel

class CategoryAdapter(var context : Context, var list : java.util.ArrayList<CategoryModel>)
    : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view : View) : ViewHolder(view) {
        var binding = LayoutCategoryItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_category_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.binding.textViewCategory.setText(list[position].cat.toString())
        Glide.with(context).load(list[position].img).into(holder.binding.imageViewCategory)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CategoryActivity::class.java)
            intent.putExtra("cat",  list[position].cat)
            context.startActivity(intent)
        }
    }
}