package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myapplication.databinding.ActivityMiParcheBinding

class MiParcheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMiParcheBinding

    private lateinit var volverBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMiParcheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        volverBtn = binding.button4

        volverBtn.setOnClickListener {
            finish()
        }
    }
}