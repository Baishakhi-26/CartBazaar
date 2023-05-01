package com.example.cartbazaar.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.cartbazaar.R
import com.example.cartbazaar.activity.LoginActivity
import com.example.cartbazaar.adapter.AllOrderAdapter
import com.example.cartbazaar.databinding.FragmentHomeBinding
import com.example.cartbazaar.databinding.FragmentMoreBinding
import com.example.cartbazaar.model.AllOrderModel
import com.example.cartbazaar.model.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding
    private lateinit var list : ArrayList<AllOrderModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(layoutInflater)

        list = ArrayList()

        val preference = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)

        Firebase.firestore.collection("users")
            .whereEqualTo("userPhoneNumber", preference.getString("number", ""))
            .get().addOnSuccessListener {
                for (doc in it) {
                    val data = doc.toObject(UserModel::class.java)
                    binding.userName.setText(data.userName)
                }
            }

        Firebase.firestore.collection("allOrders")
            .whereEqualTo("userId", preference.getString("number", "")!!)
            .get().addOnSuccessListener {
            list.clear()
            for (doc in it) {
                val data = doc.toObject(AllOrderModel::class.java)
                list.add(data)
            }
            binding.moreFragmentRecyclerView.adapter = AllOrderAdapter(list, requireContext())
        }

        binding.signOutBtn.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
        return binding.root
    }
}
