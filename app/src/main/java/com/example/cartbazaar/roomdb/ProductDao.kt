package com.example.cartbazaar.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.selects.select

@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(productModel: ProductModel)

    @Delete
    suspend fun deleteProduct(productModel: ProductModel)

    @Query("SELECT * FROM products")
    fun getAllProducts() : LiveData<List<ProductModel>>

    @Query("SELECT * FROM products WHERE productId = :id")
    fun isExit(id : String) : ProductModel
}