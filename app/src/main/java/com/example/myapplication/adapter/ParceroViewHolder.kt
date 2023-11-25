package com.example.myapplication.adapter

import android.graphics.Color
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.InfoUsuarioBinding
import org.json.JSONObject

class ParceroViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    private val binding = InfoUsuarioBinding.bind(view)

    fun render(parcero: JSONObject) {
        binding.contactName.text = parcero.getString("name")
        var selected = false
        binding.buttonPhone.setOnClickListener {
            selected = !selected
            if (selected) {
                binding.buttonPhone.text = "Eliminar"
                binding.buttonPhone.setBackgroundColor(Color.RED)
            } else {
                binding.buttonPhone.text = "Agregar"
                binding.buttonPhone.setBackgroundColor(android.R.color.holo_green_dark.hashCode())
            }
        }
        val photoUri = parcero.getString("photo")
        if (photoUri == "") {
            // Si no hay foto de avatar, podemos mostrar una imagen predeterminada
            binding.contactPhoto.setImageResource(R.drawable.default_avatar)
        } else {
            binding.contactPhoto.setImageURI(Uri.parse(photoUri))
        }
    }
}