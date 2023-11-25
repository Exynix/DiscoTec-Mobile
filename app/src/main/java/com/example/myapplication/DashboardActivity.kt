package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.setMargins
import com.bumptech.glide.Glide
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityDashboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

import java.util.logging.Logger

class DashboardActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepCount = 0
    // Creating a variable which will give the running status
    // and initially given the boolean value as false
    private var running = false

    // Creating a variable which will counts total steps
    // and it has been given the value of 0 float
    private var totalSteps = 0f

    // Creating a variable  which counts previous total
    // steps and it has also been given the value of 0 float
    private var previousTotalSteps = 0f
    private val PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1


    companion object {
        val TAG: String = DashboardActivity::class.java.name
    }

    private val logger = Logger.getLogger(TAG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ------------------- Click listeners -------------------
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


        binding.parcheBtn.setOnClickListener {
            val intent = Intent(applicationContext, CrearMiParcheActivity::class.java)
            startActivity(intent)
        }

        binding.perfilBtn.setOnClickListener {
            val intent = Intent(applicationContext, PerfilActivity::class.java)
            startActivity(intent)
        }

        binding.mapBtn.setOnClickListener {
            val intent = Intent(applicationContext, MapsActivity::class.java)
            startActivity(intent)
        }

        binding.nighCoinsComprar.setOnClickListener {
            val intent = Intent(applicationContext, ComprarNightCoinsActivity::class.java)
            startActivity(intent)
        }

        // ------- Logica para la conexión a la realtime database, y lectura de su información -------
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
                    oldNightClub.idDiscoteca =
                        discotecaSnapshot.child("id").getValue(Long::class.java)!!

                    oldNightClub.nombre =
                        discotecaSnapshot.child("nombre").getValue(String::class.java).toString()

                    oldNightClub.precioCover =
                        discotecaSnapshot.child("precio_cover").getValue(Float::class.java)!!

                    oldNightClub.ubicacion =
                        discotecaSnapshot.child("ubicacion").getValue(String::class.java).toString()

                    oldNightClub.descripcion =
                        discotecaSnapshot.child("descripcion").getValue(String::class.java)
                            .toString()

                    oldNightClubs.add(oldNightClub)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }

        })

        // ------- Lectura de la información de cloud storage. Usado para las imágenes. -------
        val storage = Firebase.storage
        val storageReference = FirebaseStorage.getInstance().reference.child("fotos_discotecas")

        storageReference.listAll().addOnSuccessListener { listResult ->

            val linearLayout: LinearLayout = binding.horizontalLinearLayout
            for (item in listResult.items) {
                item.downloadUrl.addOnSuccessListener { imageUrl ->
                    // Add the image to the horizontal scroll view
                    val imageView: ImageView = ImageView(this)

                    Glide.with(this).load(imageUrl.toString()).into(imageView)

                    linearLayout.addView(imageView)
                    val layoutParams =
                        LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT)
                    layoutParams.setMargins(50)
                    imageView.layoutParams = layoutParams
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageView.requestLayout()
                }
            }
        }

        // ------------------- Step Counter -------------------

        // Check and request the ACTIVITY_RECOGNITION permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.ACTIVITY_RECOGNITION"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf("android.permission.ACTIVITY_RECOGNITION"),
                    PERMISSION_REQUEST_ACTIVITY_RECOGNITION
                )
            }
        }

        stepCount = 0

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Toast.makeText(this, "Step counter sensor not available", Toast.LENGTH_SHORT).show()
        }



    }

    override fun onPause() {
        super.onPause()
        // Unregister the SensorEventListener when the activity is in the background
        stepSensor?.let {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now initialize the step counter
            } else {
                // Permission denied, handle accordingly (e.g., show a message, disable functionality)
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the SensorEventListener when the activity is in the foreground
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Check if the sensor type is the step counter sensor
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            // The event values contain the total number of steps since the last device boot
            stepCount = event.values[0].toInt()

            // Update the TextView with the current step count
            updateStepCount()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Not needed for the step counter sensor, but required to implement the interface
    }

    // This method updates the TextView with the current step count
    private fun updateStepCount() {
        binding.pasos.text = "Steps: $stepCount"
    }

    fun resetSteps() {
        var tv_stepsTaken = binding.pasos
        tv_stepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        tv_stepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            tv_stepsTaken.text = 0.toString()

            // This will save the data
            saveData()

            true
        }
    }

    private fun saveData() {

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber
    }


}
