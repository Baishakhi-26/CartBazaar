package com.example.cartbazaar.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cartbazaar.activity.AdressActivity
import com.example.cartbazaar.adapter.CartAdapter
import com.example.cartbazaar.databinding.FragmentCartBinding
import com.example.cartbazaar.roomdb.AppDatabase
import com.example.cartbazaar.roomdb.ProductModel

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var list : ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentCartBinding.inflate(layoutInflater)

        val preference =
            requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()

        val dao = AppDatabase.getInstance(requireContext()).productDao()

        list = ArrayList()

        dao.getAllProducts().observe(requireActivity()) {
            binding.cartRecyclerView.adapter = CartAdapter(requireContext(), it)

            list.clear()
            for (data in it) {
                list.add(data.productId)
            }

            totalCost(it)
        }

        return binding.root

    }

    private fun totalCost(data: List<ProductModel>?) {
        var total = 0
        for(item in data!!) {
            total += item.productSp!!.toInt()
        }

        binding.textViewCartFragment1.text = "Total item in cart is ${data.size}"
        binding.textViewCartFragment2.text = "Total cost : $total"

        binding.checkout.setOnClickListener{
            if (list.size == 0) {
                Toast.makeText(requireContext(), "Add Some Items", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(context, AdressActivity::class.java)
                val bundle = Bundle()
                bundle.putStringArrayList("productIds", list)
                bundle.putString("totalCost", total.toString())
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

}
