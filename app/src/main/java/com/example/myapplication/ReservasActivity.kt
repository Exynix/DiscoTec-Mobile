package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myapplication.databinding.ActivityReservasBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ReservasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservasBinding

    private lateinit var volverBtn: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReservasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        volverBtn = binding.paginaPrinciBtn

        volverBtn.setOnClickListener {
            finish()
        }
    }
}