package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.User
import com.example.myapplication.R
import org.json.JSONObject

class ParceroAdapter(private val parceros: List<User>, private val parcerosSeleccionados: MutableList<User>) : RecyclerView.Adapter<ParceroViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParceroViewHolder {
        val layouteInflater = LayoutInflater.from(parent.context)
        return ParceroViewHolder(layouteInflater.inflate(R.layout.info_usuario, parent, false))
    }

    override fun onBindViewHolder(holder: ParceroViewHolder, position: Int) {
        holder.render(parceros[position], parcerosSeleccionados)
    }

    override fun getItemCount(): Int = parceros.size

}