package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.ParcheChatApadter
import com.example.myapplication.Model.Parche
import com.example.myapplication.databinding.ActivityChatMenuBinding
import com.example.myapplication.databinding.ActivityDashboardBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMenuBinding
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("Parches")


        // Cargar todos los parches que tiene la persona
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)

        var parcheList = ArrayList<Parche>()


        parcheList.add(Parche("Parche 1", "Parche 1", "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.freepik.es%2Ffotos%2Famor&psig=AOvVaw0BEb5q9NSjFHMsa-eoBQ5G&ust=1700955215114000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCOiO5Izm3YIDFQAAAAAdAAAAABAE"))


        var parcheAdapter = ParcheChatApadter(this, parcheList)
        binding.recyclerView.adapter = parcheAdapter


        binding.paginaPrincipioBtn.setOnClickListener {
            val intent = Intent(applicationContext, DashboardActivity::class.java)
            startActivity(intent)
        }

        binding.buscarBtn.setOnClickListener {
            val intent = Intent(applicationContext, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.reservasBtn.setOnClickListener {
            val intent = Intent(applicationContext, ReservasActivity::class.java)
            startActivity(intent)
        }


        binding.parcheBtn.setOnClickListener{
            val intent = Intent(applicationContext, CrearMiParcheActivity::class.java)
            startActivity(intent)
        }

        binding.perfilBtn.setOnClickListener {
            val intent = Intent(applicationContext, PerfilActivity::class.java)
            startActivity(intent)
        }
    }
}