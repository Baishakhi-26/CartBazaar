package com.example.cartbazaar.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.cartbazaar.MainActivity
import com.example.cartbazaar.R
import com.example.cartbazaar.databinding.ActivityProductDetailsBinding
import com.example.cartbazaar.roomdb.AppDatabase
import com.example.cartbazaar.roomdb.ProductDao
import com.example.cartbazaar.roomdb.ProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductDetailsBinding.inflate(layoutInflater)

        getProductDetails(intent.getStringExtra("id"))

        setContentView(binding.root)
    }

    private fun getProductDetails(proId: String?) {
        Firebase.firestore.collection("products")
            .document(proId!!).get().addOnSuccessListener {
                val list = it.get("productImages") as ArrayList<String>
                val name = it.getString("productName")
                val productSp = it.getString("productSp")
                val productDesc = it.getString("productDescription")
                binding.productName.text = name
                binding.productPrice.text = productSp
                binding.productDesc.text = productDesc

                val slideList = ArrayList<SlideModel>()
                for (data in list ) {
                    slideList.add(SlideModel(data, ScaleTypes.CENTER_CROP))
                }

                cartAction(proId,name,productSp,it.getString("productCoverImage"))

                binding.imageSlider.setImageList(slideList)

            }.addOnFailureListener {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cartAction(proId: String, name: String?, productSp: String?, coverImage: String?) {

        val productDao =AppDatabase.getInstance(this).productDao()

        if (productDao.isExit(proId) != null){
            binding.addToCartBtn.text = "Go to Cart"
        }else{
            binding.addToCartBtn.text = "Add to Cart"
        }

        binding.addToCartBtn.setOnClickListener{
            if (productDao.isExit(proId) != null){
                openCart()
            }else{
                addToCart(productDao, proId, name, productSp, coverImage)
            }
        }
    }

    private fun addToCart(productDao: ProductDao, proId: String, name: String?, productSp: String?, coverImage: String?) {

        val data = ProductModel(proId, name,coverImage,productSp)
        lifecycleScope.launch(Dispatchers.IO){
            productDao.insertProduct(data)
            binding.addToCartBtn.text = "Go to Cart"
        }

    }

    private fun openCart() {
        val preference = this.getSharedPreferences("info", MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", true)
        editor.apply()

        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}