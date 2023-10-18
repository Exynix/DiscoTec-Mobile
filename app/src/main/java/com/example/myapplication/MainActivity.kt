package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mAuth: FirebaseAuth

    private lateinit var loginBtn: Button
    private lateinit var signInBtn: Button
    private lateinit var userInput: TextInputEditText
    private lateinit var passInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        loginBtn = binding.loginBtn
        signInBtn = binding.switchAuth
        userInput = binding.emailInput
        passInput = binding.passwordInput

        loginBtn.setOnClickListener {
            val email: String = userInput.text.toString().trim()
            val password: String = passInput.text.toString().trim()
            Log.i("MainActivity", "Email: $email")
            Log.i("MainActivity", "Password: $password")
            signInUser(email, password)
        }

        signInBtn.setOnClickListener {
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(baseContext,
                DashboardActivity::class.java)
            intent.putExtra("user", currentUser.email)
            startActivity(intent)
        } else {
            userInput.setText("")
            passInput.setText("")
        }
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email: String = userInput.text.toString()
        if (TextUtils.isEmpty(email)) {
            userInput.error = "Required"
            valid = false
        } else {
            userInput.error = null
        }
        val password: String = userInput.text.toString()
        if (TextUtils.isEmpty(password)) {
            userInput.error = "Required"
            valid = false
        } else {
            userInput.error = null
        }
        return valid
    }

    private fun signInUser(email: String, password: String) {
        if (validateForm()) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail: Success")
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        Log.w(TAG, "signInWithEmail: Failure", task.exception)
                        Toast.makeText(this@MainActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
    }
}