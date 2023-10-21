package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executor
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

   private lateinit var mAuth: FirebaseAuth

    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var emailInput: EditText
    private lateinit var passInput: EditText
    private lateinit var biometricBtn: FloatingActionButton
    private lateinit var sh: SharedPreferences
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: PromptInfo

    companion object {
        val TAG: String = EditarFotoActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       mAuth = Firebase.auth

        sh = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

        loginBtn = binding.loginBtn
        signUpBtn = binding.gotoSignUpBtn
        emailInput = binding.emailInput
        passInput = binding.passwordInput
        biometricBtn = binding.usarHuellaBtn

        loginBtn.setOnClickListener {
            val email: String = emailInput.text.toString().trim()
            val password: String = passInput.text.toString().trim()
           signInUser(email, password)
        }

        signUpBtn.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)
        }

        biometricBtn.setOnClickListener {
            checkDiviceBiometric(it)
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

    private fun signInUser(email: String, password: String, huella: Boolean = false) {
        if (validateForm() || huella) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail: Success")
                        val user = mAuth.currentUser
                        setPreferences(email, password)
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

    private fun checkDiviceBiometric(v: View) {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                if (checkBiometric()) {
                    Snackbar.make(binding.root, "La aplicación puede usar biometría", Snackbar.LENGTH_SHORT).show()
                    createBiometricListener()
                    createBiometricPrompt()
                    biometricPrompt.authenticate(promptInfo)
                } else {
                    Snackbar.make(binding.root, "Debe activar esta opción dentro de la app", Snackbar.LENGTH_SHORT).show()
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Snackbar.make(binding.root, "La aplicacion no cuenta con lector de huella ", Snackbar.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Snackbar.make(binding.root, "No esta disponible el lector de huella", Snackbar.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Snackbar.make(binding.root, "No se pudo suscribir al sensor", Snackbar.LENGTH_SHORT).show()
            }
            else -> {
                Snackbar.make(binding.root, "Error grave", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun createBiometricListener() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Snackbar.make(binding.root, "Authentication error: $errString", Snackbar.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    Snackbar.make(binding.root, "Authentication succeeded!", Snackbar.LENGTH_SHORT).show()
                    val email = sh.getString("email", "")!!
                    val password = sh.getString("password", "")!!
                    signInUser(email, password, true)
                }

                override fun onAuthenticationFailed() {
                    Snackbar.make(binding.root, "Authentication failed", Snackbar.LENGTH_SHORT).show()
                }
            })
    }

    private fun createBiometricPrompt() {
        promptInfo = PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
    }

    private fun checkBiometric(): Boolean {
        return sh.getBoolean("biometric", false)
    }

    private fun setPreferences(email: String, password: String) {
        val editor = sh.edit()
        if (email != sh.getString("email", "") || password != sh.getString("password", "")) {
            editor.putBoolean("biometric", false)
        }
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }
}