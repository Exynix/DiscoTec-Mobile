package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ajustes.setOnClickListener{
            // Intent hacia ajustes
            val intent = Intent(applicationContext, AjustesActivity::class.java)
            startActivity(intent)
        }

        binding.cerrarSesion.setOnClickListener{

        }

        binding.EliminarCuenta.setOnClickListener {

        }
    }
}