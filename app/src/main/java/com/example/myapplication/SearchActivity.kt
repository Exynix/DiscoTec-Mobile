package com.example.myapplication

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.setMargins
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivitySearchBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    // private lateinit var navigationMenu: BottomNavigationView
    private lateinit var searchBtn: FloatingActionButton
    private lateinit var profileBtn: FloatingActionButton
    private lateinit var mapBtn: FloatingActionButton
    private lateinit var reservasBtn: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

// Logica para la conexión a la realtime database, y lectura de su información
        val database = Firebase.database
        val myRef = database.getReference("discotecas")

        myRef.get()
        val oldNightClubs = ArrayList<OldNightClub>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (discotecaSnapshot in dataSnapshot.children) {
                    // Create a new Discoteca object
                    val oldNightClub = OldNightClub()

                    // Set the values of the Discoteca object
                    oldNightClub.idDiscoteca = discotecaSnapshot.child("id").getValue(Long::class.java)!!

                    oldNightClub.nombre =
                        discotecaSnapshot.child("nombre").getValue(String::class.java).toString()

                    oldNightClub.precioCover = discotecaSnapshot.child("precio_cover").getValue(Float::class.java)!!

                    oldNightClub.ubicacion =
                        discotecaSnapshot.child("ubicacion").getValue(String::class.java).toString()

                    oldNightClub.descripcion =
                        discotecaSnapshot.child("descripcion").getValue(String::class.java).toString()

                    oldNightClubs.add(oldNightClub)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", databaseError.toException())
            }

        })

        // Lectura de la información de cloud storage. Usado para las imágenes.
        val storage = Firebase.storage
        val storageReference = FirebaseStorage.getInstance().reference.child("fotos_discotecas")

        // Layout que contiene a las cartas
        val cardLinearLayoutContainer : LinearLayout = binding.cardContainer

        var i : Int = 0

        storageReference.listAll().addOnSuccessListener{listResult ->
            for (item in listResult.items) {

                // Layout de una carta
                val cardView: CardView = CardView(this)
                val cardLayoutParams  = LinearLayout.LayoutParams(800, LinearLayout.LayoutParams.WRAP_CONTENT)
                cardLayoutParams.setMargins(50)
                cardView.layoutParams = cardLayoutParams
                cardView.setCardBackgroundColor(Color.parseColor("#1F1A24"))

                val linearLayout: LinearLayout = LinearLayout(this)
                linearLayout.orientation = LinearLayout.VERTICAL
                val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                linearLayout.setPadding(20, 20, 20, 20)
                cardView.addView(linearLayout)


                item.downloadUrl.addOnSuccessListener{
                        imageUrl ->
                    // Imagen
                    val imageView: ImageView = ImageView(this)
                    Glide.with(this).load(imageUrl.toString()).into(imageView)
                    linearLayout.addView(imageView)
                    Log.i(ContentValues.TAG, "url imagen" + imageUrl.toString())

                    val imageLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150)
                    imageView.layoutParams = imageLayoutParams
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageView.requestLayout()

                    linearLayoutParams.setMargins(50)

                    // Adicion de los otros textos
                    val poppinsBoldTypeface: Typeface = resources.getFont(R.font.poppins_bold)
                    val poppinsTypeface: Typeface = resources.getFont(R.font.poppins)

                    val nameTextView: TextView = TextView(this)
                    linearLayout.addView(nameTextView)
                    nameTextView.text = oldNightClubs[i].nombre
                    nameTextView.typeface = poppinsBoldTypeface
                    nameTextView.requestLayout()

                    val locationTextView: TextView = TextView(this)
                    linearLayout.addView(locationTextView)
                    locationTextView.text = oldNightClubs[i].ubicacion
                    locationTextView.typeface = poppinsTypeface
                    locationTextView.requestLayout()

                    val priceTextView: TextView = TextView(this)
                    linearLayout.addView(priceTextView)
                    priceTextView.text = oldNightClubs[i].precioCover.toString()
                    priceTextView.typeface = poppinsTypeface
                    priceTextView.requestLayout()


                    i++
                    cardLinearLayoutContainer.addView(cardView)
                }
            }
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