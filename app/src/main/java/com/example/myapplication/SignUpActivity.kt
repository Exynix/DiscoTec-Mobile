package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myapplication.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var loginBtn: Button
    private lateinit var signInBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginBtn = binding.backToLoginBtn
        signInBtn = binding.createAccountBtn

        loginBtn.setOnClickListener {
            finish()
        }

        signInBtn.setOnClickListener {

        }
    }
}