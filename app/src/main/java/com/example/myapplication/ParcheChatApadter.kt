package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.Parche
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference


class ParcheChatApadter(private val context: ValueEventListener, private val parcheList:ArrayList<Parche>) : RecyclerView.Adapter<ParcheChatApadter.ViewHolder>(){
    private lateinit var storageRef: StorageReference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parche, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parcheList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parche: Parche = parcheList[position]
        holder.txtParcheName.text = parche.Nombre
        holder.ParcheDescripcion.text = parche.Descripcion
        holder.ParcheImg.setImageResource(R.drawable.chat)
        // Buscar la img en la BD
        //storageRef = FirebaseStorage.getInstance().reference.child("Images/${parche.parcheImg}")
        //storageRef.downloadUrl.addOnSuccessListener { uri ->
            //Glide.with(context).load(uri.toString()).into(holder.ParcheImg)
        //}
    }

    class ViewHolder (view:View): RecyclerView.ViewHolder(view){
        val txtParcheName:TextView = view.findViewById(R.id.Parche_name)
        val ParcheDescripcion:TextView = view.findViewById(R.id.Parche_description)
        val ParcheImg:ImageView = view.findViewById(R.id.parche_img)
    }
}