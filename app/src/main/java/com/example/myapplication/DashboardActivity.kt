package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityDashboardBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    private lateinit var mAuth: FirebaseAuth

    private lateinit var searchBtn: FloatingActionButton
    private lateinit var profileBtn: FloatingActionButton
    private lateinit var mapBtn: FloatingActionButton
    private lateinit var reservasBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        searchBtn = binding.buscarBtn
        profileBtn = binding.perfilBtn
        mapBtn = binding.mapaBtn
        reservasBtn = binding.reservasBtn

        searchBtn.setOnClickListener {
            val intent = Intent(applicationContext, SearchActivity::class.java)
            startActivity(intent)
        }

        profileBtn.setOnClickListener {
            val intent = Intent(applicationContext, PerfilActivity::class.java)
            startActivity(intent)
        }

        mapBtn.setOnClickListener {
            val intent = Intent(applicationContext, FormularioReservaActivity::class.java)
            startActivity(intent)
        }

        reservasBtn.setOnClickListener {
            val intent = Intent(applicationContext, ReservasActivity::class.java)
            startActivity(intent)
        }

        binding.SignOut.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
}