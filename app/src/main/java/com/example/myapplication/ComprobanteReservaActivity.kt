package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityComprobanteReservaBinding

class ComprobanteReservaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComprobanteReservaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComprobanteReservaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val CantidadPersonas = intent.getStringExtra("CantidadPersonas")
        val Fecha = intent.getStringExtra("Fecha")
        val Hora = intent.getStringExtra("Hora")
        val Comentario = intent.getStringExtra("Comentario")

        binding.intentCantPersonas.setText(CantidadPersonas)
        binding.intentFechayHora.setText("$Fecha - $Hora")
        binding.intentTitular.setText("User")
        binding.intentEstado.setText("Completo y Pago")
        binding.intentLugar.setText("PalenqueRoofTop")

        binding.Continuar.setOnClickListener {
            val intent = Intent(applicationContext, QrActivity::class.java)
            intent.putExtra("CantidadPersonas", CantidadPersonas)
            intent.putExtra("Fecha", Fecha)
            intent.putExtra("Hora", Hora)
            intent.putExtra("Comentario", Comentario)
            startActivity(intent)
        }
    }
}