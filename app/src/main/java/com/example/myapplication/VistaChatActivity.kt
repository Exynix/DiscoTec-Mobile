package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityChatMenuBinding
import com.example.myapplication.databinding.ActivityVistaChatBinding

class VistaChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVistaChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVistaChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            val intent = Intent(applicationContext, ChatMenuActivity::class.java)
            startActivity(intent)
        }
    }
}
