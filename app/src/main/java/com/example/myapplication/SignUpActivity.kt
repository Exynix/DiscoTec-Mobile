package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var mAuth: FirebaseAuth

    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var emailEdit: EditText
    private lateinit var passEdit: EditText
    private lateinit var cedulaEdit: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        loginBtn = binding.gotoLoginBtn
        signUpBtn = binding.signUpBtn
        emailEdit = binding.emailInput
        passEdit = binding.passwordInput
        cedulaEdit = binding.cedulaInput

        loginBtn.setOnClickListener {
            finish()
        }

        signUpBtn.setOnClickListener {
            signUp()
        }
    }

    fun isEmailValid(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email: String = emailEdit.text.toString()
        if (TextUtils.isEmpty(email)) {
            emailEdit.error = "Required"
            valid = false
        } else {
            emailEdit.error = null
        }
        val password: String = passEdit.text.toString()
        if (TextUtils.isEmpty(password)) {
            passEdit.error = "Required"
            valid = false
        } else {
            passEdit.error = null
        }
        val cedula: String = cedulaEdit.text.toString()
        if (TextUtils.isEmpty(cedula)) {
            cedulaEdit.error = "Required"
            valid = false
        } else {
            cedulaEdit.error = null
        }

        return valid
    }

    private fun signUp() {
        val email = emailEdit.text.toString()
        val pass = passEdit.text.toString()
        if (!validateForm()) {
            return
        } else if (!isEmailValid(email)) {
            Toast.makeText(this@SignUpActivity, "Email is not a valid format", Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                Toast.makeText(
                    this@SignUpActivity,
                    String.format("The user %s is successfully registered", user!!.email),
                    Toast.LENGTH_LONG
                ).show()
            }
        }.addOnFailureListener(this) { e ->
            Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }
}