package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.ParcheChatApadter
import com.example.myapplication.Model.Parche
import com.example.myapplication.databinding.ActivityChatMenuBinding
import com.example.myapplication.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMenuBinding
    private lateinit var dbRef: DatabaseReference
    val parchesIdList = ArrayList<String>() // Lista para guardar los IDs de los parches
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("Parches")


        // Cargar todos los parches que tiene la persona
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)

        var parcheList = ArrayList<Parche>()

        var parcheAdapter = ParcheChatApadter(this@ChatMenuActivity, parcheList)
        binding.recyclerView.adapter = parcheAdapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid).child("Parches")
            dbRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (parcheSnapshot in snapshot.children) {
                        val parcheId = parcheSnapshot.getValue(String::class.java)
                        parcheId?.let { parchesIdList.add(it) }

                    }

                    // Limpia la lista antes de añadir nuevos datos
                    parcheList.clear()

                    // Base de datos referenciada para cargar los detalles del parche
                    val dbRef2 = FirebaseDatabase.getInstance().getReference("Parche")

                    // Carga los detalles de cada parche usando los IDs
                    parchesIdList.forEach { parcheId ->
                        dbRef2.child(parcheId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val parche = snapshot.getValue(Parche::class.java)
                                parche?.let {
                                    parcheList.add(parche)
                                    parcheAdapter.notifyDataSetChanged()
                                    // Resto del código de configuración de botones..
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Manejar el error adecuadamente
                            }
                        })
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejar el error adecuadamente
                }
            })
        }


        //parcheList.add(Parche("Parche 1", "Parche 1", "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.freepik.es%2Ffotos%2Famor&psig=AOvVaw0BEb5q9NSjFHMsa-eoBQ5G&ust=1700955215114000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCOiO5Izm3YIDFQAAAAAdAAAAABAE"))


        //parcheAdapter = ParcheChatApadter(this@ChatMenuActivity, parcheList)
        parcheAdapter.setOnItemClickListener(object : ParcheChatApadter.onItemClickListener {
            override fun onItemClick(position: Int) {
                // Handle the click event
                val parche = parcheList[position]
                Toast.makeText(this@ChatMenuActivity, "Clicked on: ${parche.nombre}", Toast.LENGTH_SHORT).show()
                // If you want to start a new Activity and pass data to it, you can do it here
                // Example:
                val intent = Intent(this@ChatMenuActivity, VistaChatActivity::class.java)
                intent.putExtra("extra_data", parchesIdList[position])
                startActivity(intent)
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
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