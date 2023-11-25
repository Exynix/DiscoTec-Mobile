package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityDetallesReservaBinding

class DetallesReservaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetallesReservaBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDetallesReservaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textViewDetalles: TextView = binding.textViewDetalles
        val reservationInfo = intent.getStringExtra("RESERVATION_INFO")
        //textViewDetalles.text = "Detalles de la reserva:\n$reservationInfo"
        val parts = reservationInfo?.split(", ") ?: emptyList()

        // Obtener los valores individuales
        var cantidadPersonas = ""
        var fecha = ""
        var hora = ""
        var comentario = ""

        for (part in parts) {
            val keyValue = part.split(": ")
            if (keyValue.size == 2) {
                val key = keyValue[0].trim()
                val value = keyValue[1].trim()

                when (key) {
                    "Cantidad de personas" -> cantidadPersonas = value
                    "Fecha" -> fecha = value
                    "Hora" -> hora = value
                    "Comentario" -> comentario = value
                }
            }
        }

        // Mostrar los valores en TextViews o donde lo necesites
        val cantidadTextView: TextView = binding.cantidadPersonas
        val fechaTextView: TextView = binding.fecha
        val horaTextView: TextView = binding.hora
        val comentarioTextView: TextView = binding.comentario

        cantidadTextView.text = "Cantidad de personas: $cantidadPersonas"
        fechaTextView.text = "Fecha: $fecha"
        horaTextView.text = "Hora: $hora"
        comentarioTextView.text = "Comentario: $comentario"
    }
}
