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

        binding.usuario1.setOnClickListener {
            val intent = Intent(applicationContext, VistaChatActivity::class.java)
            startActivity(intent)
        }
    }
}