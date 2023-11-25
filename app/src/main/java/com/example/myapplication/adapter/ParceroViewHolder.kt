package com.example.myapplication.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.User
import com.example.myapplication.R
import com.example.myapplication.databinding.InfoUsuarioBinding

class ParceroViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    private val binding = InfoUsuarioBinding.bind(view)

    fun render(parcero: User, parcerosSeleccionados: MutableList<User>) {
        binding.contactName.text = parcero.nombre
        var selected = false
        binding.buttonPhone.setOnClickListener {
            selected = !selected
            if (selected) {
                binding.buttonPhone.text = "Eliminar"
                binding.buttonPhone.setBackgroundColor(Color.RED)
                parcerosSeleccionados.add(parcero)
            } else {
                binding.buttonPhone.text = "Agregar"
                binding.buttonPhone.setBackgroundColor(android.R.color.holo_green_dark.hashCode())
                parcerosSeleccionados.remove(parcero)
            }
        }
        binding.contactPhoto.setImageResource(R.drawable.default_avatar)
        /*val photoUri = parcero.getString("photo")
        if (photoUri == "") {
            // Si no hay foto de avatar, podemos mostrar una imagen predeterminada
        } else {
            binding.contactPhoto.setImageURI(Uri.parse(photoUri))
        }*/
    }
}