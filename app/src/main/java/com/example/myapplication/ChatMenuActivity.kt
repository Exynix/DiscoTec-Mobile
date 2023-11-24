package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import com.example.myapplication.databinding.ActivityChatMenuBinding
import com.example.myapplication.databinding.ActivityDashboardBinding

class ChatMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar todos los parches que tiene la persona




        binding.usuario1.setOnClickListener {
            val intent = Intent(applicationContext, VistaChatActivity::class.java)
            startActivity(intent)
        }
        binding.paginaPrincipioBtn.setOnClickListener {
            val intent = Intent(applicationContext, DashboardActivity::class.java)
            startActivity(intent)
        }

        binding.buscarBtn.setOnClickListener {
            val intent = Intent(applicationContext, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.reservasBtn.setOnClickListener {
            val intent = Intent(applicationContext, ReservasActivity::class.java)
            startActivity(intent)
        }


        binding.parcheBtn.setOnClickListener{
            val intent = Intent(applicationContext, CrearMiParcheActivity::class.java)
            startActivity(intent)
        }

        binding.perfilBtn.setOnClickListener {
            val intent = Intent(applicationContext, PerfilActivity::class.java)
            startActivity(intent)
        }
    }
}