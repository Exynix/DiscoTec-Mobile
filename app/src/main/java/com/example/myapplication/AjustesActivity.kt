package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityAjustesBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executor

class AjustesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAjustesBinding

    private lateinit var sh: SharedPreferences

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAjustesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        sh = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

        binding.editarPerfil.setOnClickListener {
            // Intent hacia editar perfil
            val intent = Intent(applicationContext, EditarPerfilActivity::class.java)
            startActivity(intent)
        }

        binding.huellaDigitalBtn.setOnClickListener {
            checkDiviceBiometric(it)
        }
    }

    private fun checkDiviceBiometric(v: View) {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Snackbar.make(binding.root, "La aplicación puede usar biometría", Snackbar.LENGTH_SHORT).show()
                val editor = sh.edit()
                editor.putBoolean("biometric", true)
                editor.apply()
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


}