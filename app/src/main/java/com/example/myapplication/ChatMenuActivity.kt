package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.Parche
import com.example.myapplication.databinding.ActivityChatMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMenuBinding
    private lateinit var dbRef: DatabaseReference
    private val parcheList2 = ArrayList<Parche>() // Lista para almacenar los objetos Parche

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var parcheAdapter: ParcheChatApadter

        // Configura el RecyclerView con un adaptador
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)


        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid).child("Parches")
            dbRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val parchesIdList = ArrayList<String>() // Lista para guardar los IDs de los parches
                    for (parcheSnapshot in snapshot.children) {
                        val parcheId = parcheSnapshot.getValue(String::class.java)
                        parcheId?.let { parchesIdList.add(it) }
                    }

                    // Limpia la lista antes de añadir nuevos datos
                    parcheList2.clear()

                    // Base de datos referenciada para cargar los detalles del parche
                    val dbRef2 = FirebaseDatabase.getInstance().getReference("Parche")

                    // Carga los detalles de cada parche usando los IDs
                    parchesIdList.forEach { parcheId ->
                        dbRef2.child(parcheId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val parche = snapshot.getValue(Parche::class.java)
                                parche?.let {
                                    var parche = Parche(it.nombre, it.parcheDescription, it.parcheImg)
                                    parcheList2.add(parche)
                                    parcheAdapter = ParcheChatApadter(this, parcheList2)
                                    parcheAdapter.notifyDataSetChanged()
                                    // Resto del código de configuración de botones...
                                    binding.recyclerView.adapter = parcheAdapter
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