package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var loginBtn: Button
    private lateinit var signInBtn: Button
    private lateinit var userInput: TextInputEditText
    private lateinit var passInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginBtn = binding.loginBtn
        signInBtn = binding.signInBtn
        userInput = binding.input0
        passInput = binding.input1

        loginBtn.setOnClickListener {
            if (userInput.text.toString().trim() != "" && passInput.text.toString().trim() != "") {
                val intent = Intent(applicationContext, DashboardActivity::class.java)
                startActivity(intent)
            }
        }

        signInBtn.setOnClickListener {
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}