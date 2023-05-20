package com.example.cartbazaar.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.cartbazaar.R
import com.example.cartbazaar.databinding.ActivityRegisterBinding
import com.example.cartbazaar.model.UserModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener {
            openLogin()
        }

        binding.signUpButton.setOnClickListener {
            val list = ArrayList<UserModel>()
            val numbers = ArrayList<String>()
            Firebase.firestore.collection("users")
                .get().addOnSuccessListener {
                    list.clear()
                    for (doc in it) {
                        val data = doc.toObject(UserModel::class.java)
                        list.add(data)
                    }
                    for (i in list) {
                        numbers.add(i.userPhoneNumber!!)
                    }
                    if (!numbers.contains(binding.userNumberInput.text.toString())) {
                        validateUser()
                    } else {
                        Toast.makeText(this, "This number is already registered.\n Go to sign in.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateUser() {
        if (binding.userNameInput.text!!.isEmpty() || binding.userNumberInput.text!!.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            storeData()
        }
    }

    private fun storeData() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Loading....")
            .setMessage("Please Wait")
            .setCancelable(false)
            .create()
        builder.show()

        val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("number", binding.userNumberInput.text.toString())
        editor.putString("name", binding.userNameInput.text.toString())
        editor.apply()

        val data = UserModel(userName = binding.userNameInput.text.toString(),
            userPhoneNumber = binding.userNumberInput.text.toString())

        Firebase.firestore.collection("users").document(binding.userNumberInput.text.toString())
            .set(data).addOnSuccessListener {
                Toast.makeText(this, "User registered", Toast.LENGTH_SHORT).show()
                builder.dismiss()
                openLogin()
            }
            .addOnFailureListener {
                builder.dismiss()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}