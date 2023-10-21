package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import android.widget.Toast
import com.example.myapplication.databinding.ActivityDashboardBinding
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sensorManager: SensorManager
    lateinit var temperatureSensor: Sensor
    lateinit var temperatureSensorListener: SensorEventListener
    private var temperatura: Float = 0.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        // navigationMenu = binding.bottomNavigationView
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!!

        binding.paginaPrincipioBtn.setOnClickListener {
            val intent = Intent(applicationContext, DashboardActivity::class.java)
            startActivity(intent)
        }

        binding.buscarBtn.setOnClickListener {
            val intent = Intent(applicationContext, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.reservasBtn.setOnClickListener {
            //val intent = Intent(applicationContext, ReservasActivity::class.java)
            val intent = Intent(applicationContext, FormularioReservaActivity::class.java)
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