package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityPagoReservaBinding

class PagoReserva : AppCompatActivity() {
    private lateinit var binding: ActivityPagoReservaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPagoReservaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val CantidadPersonas = intent.getStringExtra("CantidadPersonas")
        val Fecha = intent.getStringExtra("Fecha")
        val Hora = intent.getStringExtra("Hora")
        val Comentario = intent.getStringExtra("Comentario")


        binding.mercadoPago.setOnClickListener{
            val intent = Intent(applicationContext, ComprobanteReservaActivity::class.java)
            intent.putExtra("CantidadPersonas", CantidadPersonas)
            intent.putExtra("Fecha", Fecha)
            intent.putExtra("Hora", Hora)
            intent.putExtra("Comentario", Comentario)
            startActivity(intent)
        }

        binding.Nequi.setOnClickListener{
            val intent = Intent(applicationContext, ComprobanteReservaActivity::class.java)
            intent.putExtra("CantidadPersonas", CantidadPersonas)
            intent.putExtra("Fecha", Fecha)
            intent.putExtra("Hora", Hora)
            intent.putExtra("Comentario", Comentario)
            startActivity(intent)
        }

        binding.tarjeta.setOnClickListener{
            val intent = Intent(applicationContext, ComprobanteReservaActivity::class.java)
            intent.putExtra("CantidadPersonas", CantidadPersonas)
            intent.putExtra("Fecha", Fecha)
            intent.putExtra("Hora", Hora)
            intent.putExtra("Comentario", Comentario)
            startActivity(intent)
        }
    }
}