package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityAjustesBinding
import com.example.myapplication.databinding.ActivityPerfilBinding

class AjustesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAjustesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        binding.editarPerfil.setOnClickListener {
            // Intent hacia editar perfil
            val intent = Intent(applicationContext, EditarPerfil::class.java)
            startActivity(intent)
        }
    }
}