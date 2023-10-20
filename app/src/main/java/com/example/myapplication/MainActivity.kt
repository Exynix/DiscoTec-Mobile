package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mAuth: FirebaseAuth

    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var emailInput: EditText
    private lateinit var passInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        loginBtn = binding.loginBtn
        signUpBtn = binding.gotoSignUpBtn
        emailInput = binding.emailInput
        passInput = binding.passwordInput

        loginBtn.setOnClickListener {
            val email: String = emailInput.text.toString().trim()
            val password: String = passInput.text.toString().trim()
            signInUser(email, password)
            val intent = Intent(applicationContext, DashboardActivity::class.java)
            startActivity(intent)
        }

        signUpBtn.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
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
            emailInput.setText("")
            passInput.setText("")
        }
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email: String = emailInput.text.toString()
        if (TextUtils.isEmpty(email)) {
            emailInput.error = "Required"
            valid = false
        } else {
            emailInput.error = null
        }
        val password: String = passInput.text.toString()
        if (TextUtils.isEmpty(password)) {
            passInput.error = "Required"
            valid = false
        } else {
            passInput.error = null
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