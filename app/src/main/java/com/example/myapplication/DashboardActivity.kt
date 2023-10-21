package com.example.myapplication

import android.R
import android.app.ActionBar.LayoutParams
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginEnd
import androidx.core.view.marginRight
import androidx.core.view.setMargins
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityDashboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        // navigationMenu = binding.bottomNavigationView
        setContentView(binding.root)


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
            val intent = Intent(applicationContext, MiParcheActivity::class.java)
            startActivity(intent)
        }

        binding.perfilBtn.setOnClickListener {
            val intent = Intent(applicationContext, PerfilActivity::class.java)
            startActivity(intent)
        }

        // Logica para la conexión a la realtime database, y lectura de su información
        val database = Firebase.database
        val myRef = database.getReference("discotecas")

        myRef.get()
        val discotecas = ArrayList<Discoteca>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (discotecaSnapshot in dataSnapshot.children) {
                    // Create a new Discoteca object
                    val discoteca = Discoteca()

                    // Set the values of the Discoteca object
                    discoteca.idDiscoteca = discotecaSnapshot.child("id").getValue(Long::class.java)!!

                    discoteca.nombre =
                        discotecaSnapshot.child("nombre").getValue(String::class.java).toString()

                    discoteca.precioCover = discotecaSnapshot.child("precio_cover").getValue(Float::class.java)!!

                    discoteca.ubicacion =
                        discotecaSnapshot.child("ubicacion").getValue(String::class.java).toString()

                    discoteca.descripcion =
                        discotecaSnapshot.child("descripcion").getValue(String::class.java).toString()

                    discotecas.add(discoteca)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }

        })

        // Lectura de la información de cloud storage. Usado para las imágenes.
        val storage = Firebase.storage
        val storageReference = FirebaseStorage.getInstance().reference.child("fotos_discotecas")

        storageReference.listAll().addOnSuccessListener{listResult ->

            val linearLayout: LinearLayout = binding.horizontalLinearLayout
            for (item in listResult.items) {
                item.downloadUrl.addOnSuccessListener{
                        imageUrl ->
                    // Add the image to the horizontal scroll view
                    val imageView: ImageView = ImageView(this)

                    Glide.with(this).load(imageUrl.toString()).into(imageView)

                    linearLayout.addView(imageView)
                    val layoutParams = LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT)
                    layoutParams.setMargins(50)
                    imageView.layoutParams = layoutParams
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageView.requestLayout()
                }
            }
        }


    }

}


/*
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            print("here2")
            when (item.itemId){
                R.id.navExplorar -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    print("here1")
                    startActivity(intent)
                    true
                }

                R.id.navBuscar -> {
                    val intent = Intent(this, SearchActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.navReservas -> {
                    val intent = Intent(this, ReservasActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navMiParche -> {
                    val intent = Intent(this, MiParcheActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navPerfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> {false}
            }

        }
*/