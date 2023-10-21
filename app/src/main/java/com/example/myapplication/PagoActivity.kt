package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.myapplication.databinding.ActivityPagoBinding

class PagoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPagoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPagoBinding.inflate(layoutInflater)
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