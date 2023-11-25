package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityDashboardBinding
import com.example.myapplication.model.OldNightClub
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.zxing.integration.android.IntentIntegrator
import java.util.logging.Logger

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sensorManager: SensorManager
    val stepCounterSensor: Sensor? = null
    private lateinit var stepCounterSensorListener: SensorEventListener
   // private var temperatura: Float = 0.0f


    companion object {
        val TAG: String = DashboardActivity::class.java.name
        const val REQUEST_CODE_SCAN = 123
    }
    private val logger = Logger.getLogger(TAG)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        // navigationMenu = binding.bottomNavigationView
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        //temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        /*if (temperatureSensor == null) {
            Toast.makeText(this@DashboardActivity, "Ambient temperature sensor not available on this device", Toast.LENGTH_SHORT).show()
        }*/
        if (stepCounterSensor == null) {
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
        binding.scanButton.setOnClickListener {
            // Lanza la actividad de escaneo
            /*val intent = Intent(applicationContext, ScannerActivity::class.java)
            intent.putExtra("Codigo", REQUEST_CODE_SCAN)
            startActivity(intent)*/
            initScanner()
        }

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

        binding.nighCoinsComprar.setOnClickListener {
            val intent = Intent(applicationContext, ComprarNightCoinsActivity::class.java)
            startActivity(intent)
        }

        stepCounterSensorListener = object : SensorEventListener {
           override fun onSensorChanged(event: SensorEvent?) {
                var stepCount: Int = 0
                // Manejar los cambios en el sensor de podómetro
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                    // Actualizar el contador de pasos
                    stepCount = event.values[0].toInt()

                    // Mostrar el número de pasos en un TextView
                    binding.pasos.text = "$stepCount"
                    println("$stepCount")
                    // Imprimir el número de pasos en el Logcat
                    Log.d(TAG, "Número de pasos: $stepCount")
                }
            }
            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        sensorManager.registerListener(
                stepCounterSensorListener,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
       /* binding.temperatura.setOnClickListener {
            Toast.makeText(
                this@DashboardActivity,
                "La temperatura actual es de $temperatura °C",
                Toast.LENGTH_SHORT
            ).show()
        }*/
    }

    private fun initScanner() {
       val integrator =  IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea aquí tu reserva")
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("TuApp", "Entrando en onActivityResult")

        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode, data )
        if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK) {
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
                } else {
                    //Toast.makeText(this, "${result.contents}", Toast.LENGTH_LONG).show()
                    /*val intent = Intent(this, DetallesReservaActivity::class.java)
               // intent.putExtra("RESERVATION_INFO", result.contents.toString() )
                startActivity(intent)*/

                  /*  val intent = Intent(this@DashboardActivity, DetallesReservaActivity::class.java)
                    intent.putExtra("RESERVATION_INFO", result.contents.toString())
                    startActivity(intent)*/
                    Log.d("TuApp", "Resultado del escaneo: $resultCode")
                    handleQRCodeResult(result.contents)
                }
            } else {

            }
        }
    }
    private fun handleQRCodeResult(contents: String) {
        Toast.makeText(this, contents, Toast.LENGTH_LONG).show()
        val intent = Intent(this, DetallesReservaActivity::class.java)
        intent.putExtra("RESERVATION_INFO", contents)
        Log.d("TuApp", "Datos del Intent: ${contents}")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        /*stepCounterSensor?.let {
            sensorManager.registerListener(stepCounterSensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }*/
           sensorManager.registerListener(
                stepCounterSensorListener,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(stepCounterSensorListener)
            //sensorManager.unregisterListener(temperatureSensorListener)
    }

   /* override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario implementar este método para el podómetro
    }*/



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