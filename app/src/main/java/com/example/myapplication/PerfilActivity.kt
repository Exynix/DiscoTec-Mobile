package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        binding.ajustes.setOnClickListener{
            // Intent hacia ajustes
            val intent = Intent(applicationContext, Ajustes::class.java)
            startActivity(intent)
        }

        binding.cerrarSesion.setOnClickListener{
            mAuth.signOut()
        }

        binding.EliminarCuenta.setOnClickListener {
            // TO DO
        }
    }
}