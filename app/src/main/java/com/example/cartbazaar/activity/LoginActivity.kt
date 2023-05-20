package com.example.cartbazaar.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.cartbazaar.MainActivity
import com.example.cartbazaar.R
import com.example.cartbazaar.databinding.ActivityLoginBinding
import com.example.cartbazaar.model.UserModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.signInBtn.setOnClickListener {
            if (binding.userNumberInput.text!!.isNotEmpty()) {
                if (binding.userNumberInput.text!!.length == 10) {
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
                            if (numbers.contains(binding.userNumberInput.text.toString())) {
                                sendOtp(binding.userNumberInput.text.toString())
                            } else {
                                Toast.makeText(this, "Enter a registered number.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Please Enter Correct Number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Enter Number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var builder : AlertDialog
    private fun sendOtp(number: String) {
        builder = AlertDialog.Builder(this)
            .setTitle("Loading....")
            .setMessage("Please Wait")
            .setCancelable(false)
            .create()
        builder.show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$number")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(this@LoginActivity, "Invalid Request", Toast.LENGTH_SHORT).show()
                builder.dismiss()
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(this@LoginActivity, "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show()
                builder.dismiss()
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                Toast.makeText(this@LoginActivity, "reCAPTCHA verification attempted with null Activity", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            builder.dismiss()
            val intent = Intent(this@LoginActivity, OTPActivity::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("number", binding.userNumberInput.text.toString())
            intent.putExtra("resendToken", token)
            startActivity(intent)
        }


        private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this@LoginActivity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(this@LoginActivity, "Authentication Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        // Sign in failed, display a message and update the UI
                        Toast.makeText(this@LoginActivity, "Couldn't Authenticate", Toast.LENGTH_SHORT).show()
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(this@LoginActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                        }
                        // Update UI
                    }
                }
        }

    }
}