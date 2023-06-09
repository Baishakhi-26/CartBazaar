package com.example.cartbazaar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cartbazaar.databinding.AllOrdersItemLayoutBinding
import com.example.cartbazaar.model.AllOrderModel

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AllOrderAdapter(val list: ArrayList<AllOrderModel>, val context: Context)
    :RecyclerView.Adapter<AllOrderAdapter.AllOrderViewHolder>( ){

    inner class AllOrderViewHolder(val binding: AllOrdersItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderViewHolder {
        return AllOrderViewHolder(
            AllOrdersItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: AllOrderViewHolder, position: Int) {
        holder.binding.productTitle.text = list[position].name
        holder.binding.productPrice.text = list[position].price
        Glide.with(context).load(list[position].image).into(holder.binding.productImg)


        when (list[position].status) {
            "Ordered" -> {
                holder.binding.productStatus.text = "Ordered"
            }

            "Dispatched" -> {
                holder.binding.productStatus.text = "Dispatched"
            }

            "Delivered" -> {
                holder.binding.productStatus.text = "Delivered"
            }

            "Cancelled" -> {
                holder.binding.productStatus.text = "Cancelled"
            }

        }
    }




    override fun getItemCount(): Int {
        return list.size
    }

}