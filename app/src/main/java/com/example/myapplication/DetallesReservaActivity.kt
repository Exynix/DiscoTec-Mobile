package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.myapplication.databinding.ActivityDetallesReservaBinding
import com.example.myapplication.databinding.ActivityQrBinding

class DetallesReservaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetallesReservaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val textViewDetalles: TextView = binding.textViewDetalles
        val reservationInfo = intent.getStringExtra("RESERVATION_INFO")
        textViewDetalles.text = "Detalles de la reserva:\n$reservationInfo"
    }
}