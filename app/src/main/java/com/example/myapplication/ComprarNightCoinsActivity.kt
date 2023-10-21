package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityComprarNightCoinsBinding
import com.example.myapplication.databinding.ActivityDashboardBinding

class ComprarNightCoinsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComprarNightCoinsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComprarNightCoinsBinding.inflate(layoutInflater)
        // navigationMenu = binding.bottomNavigationView
        setContentView(binding.root)

        binding.btnCalcular.setOnClickListener {
            if(binding.cantidadAComprar.text != null)
            {
                var cantidad:Double = binding.cantidadAComprar.text.toString().toDouble()
                cantidad *= 0.5
                binding.numeroNightCoins.text = cantidad.toString()
            }
            else
            {
                Toast.makeText(this@ComprarNightCoinsActivity, "Ingrese un número",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}