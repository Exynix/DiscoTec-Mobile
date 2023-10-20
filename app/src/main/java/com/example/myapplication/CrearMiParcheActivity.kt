package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myapplication.databinding.ActivityCrearMiParcheBinding

class CrearMiParcheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearMiParcheBinding

    private lateinit var volverBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCrearMiParcheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        volverBtn = binding.button4

        volverBtn.setOnClickListener {
            finish()
        }
    }


    }
}