package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityEditarPerfilBinding


class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editarFotoBtn.setOnClickListener {
            // Intent hacia editar foto
            val intent = Intent(applicationContext, EditarFotoActivity::class.java)
            startActivity(intent)
        }

    }
}