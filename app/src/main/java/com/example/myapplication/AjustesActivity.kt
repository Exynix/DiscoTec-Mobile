package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityAjustesBinding

class AjustesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAjustesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAjustesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editarPerfil.setOnClickListener {
            // Intent hacia editar perfil
            val intent = Intent(applicationContext, EditarPerfilActivity::class.java)
            startActivity(intent)
        }
    }
}