package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myapplication.databinding.ActivityReservasBinding


class ReservasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservasBinding

    private lateinit var volverBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReservasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        volverBtn = binding.button3

        volverBtn.setOnClickListener {
            finish()
        }
    }
}