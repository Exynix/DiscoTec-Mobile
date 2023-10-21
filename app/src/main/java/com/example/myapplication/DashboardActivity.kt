package com.example.myapplication

import android.R
import android.app.ActionBar.LayoutParams
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginRight
import androidx.core.view.setMargins
import com.bumptech.glide.Glide
import android.widget.Toast
import com.example.myapplication.databinding.ActivityDashboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

import java.util.logging.Logger

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sensorManager: SensorManager
    private var temperatureSensor: Sensor? = null
    private lateinit var temperatureSensorListener: SensorEventListener
    private var temperatura: Float = 0.0f

    companion object {
        val TAG: String = DashboardActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        // navigationMenu = binding.bottomNavigationView
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        if (temperatureSensor == null) {
            Toast.makeText(this@DashboardActivity, "Ambient temperature sensor not available on this device", Toast.LENGTH_SHORT).show()
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


        binding.mapBtn.setOnClickListener {
            val intent = Intent(applicationContext, MapsActivity::class.java)
            startActivity(intent)
        }

        temperatureSensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                temperatura = event.values[0]
            }
            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        sensorManager.registerListener(
            temperatureSensorListener,
            temperatureSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        binding.temperatura.setOnClickListener {
            Toast.makeText(
                this@DashboardActivity,
                "La temperatura actual es de $temperatura °C",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
            sensorManager.registerListener(
                temperatureSensorListener,
                temperatureSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
    }

    override fun onPause() {
        super.onPause()
            sensorManager.unregisterListener(temperatureSensorListener)
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