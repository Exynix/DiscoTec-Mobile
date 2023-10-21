package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myapplication.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.logging.Logger

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        val REQUEST_CODE_LOCATION = 0
        val TAG: String = MapsActivity::class.java.name
    }

    private val logger = Logger.getLogger(TAG)

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mGeocoder: Geocoder
    lateinit var mAddress: EditText
    lateinit var sensorManager: SensorManager
    lateinit var lightSensor: Sensor
    lateinit var lightSensorListener: SensorEventListener
    // Añade una variable para controlar si el mapa está listo
    private var isMapReady = false
    // Para crear la ruta entre dos puntos
    private var start: String =""
    private var end: String =""
    private var poly: Polyline? = null

    // Permission handler
    private val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        updateUI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mAddress = binding.address
        mAddress.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                findAddress()
            }
            false
        }

        // Initialize the sensors
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!

        // Initialize the listener
        lightSensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (mMap != null) {
                    if (event.values[0] < 5000) {
                        Log.i("MAPS", "DARK MAP " + event.values[0])
                        mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                this@MapsActivity,
                                R.raw.style_night
                            )
                        )
                    } else {
                        Log.i("MAPS", "LIGHT MAP " + event.values[0])
                        mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                this@MapsActivity,
                                R.raw.style_day_retro
                            )
                        )
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }
        // Initialize the geocoder
        mGeocoder = Geocoder(baseContext)
    }

    override fun onResume() {
        super.onResume()
        if(isMapReady)
        {
            sensorManager.registerListener(
                lightSensorListener,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if(isMapReady)
        {
            sensorManager.unregisterListener(lightSensorListener)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        isMapReady = true
        mMap.moveCamera(CameraUpdateFactory.zoomTo(20f))
        mMap.uiSettings.setAllGesturesEnabled(true)
        /// Add UI controls
        mMap.uiSettings.isZoomControlsEnabled = true
        // Cambia la posición del control de zoom
        mMap.uiSettings.isMapToolbarEnabled = true

        verifyPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION, "El permiso es requerido para poder mostrar tu ubicación en el mapa")

        if(isMapReady)
        {
            sensorManager.registerListener(
                lightSensorListener,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    // ----------------- PERMISOS ----------------------------------------------------------------------------

    private fun verifyPermissions(context: Context, permission: String, rationale: String) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(binding.root, "Ya tengo los permisos 😜", Snackbar.LENGTH_LONG).show()
                updateUI(true)
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // We display a snackbar with the justification for the permission, and once it disappears, we request it again.
                val snackbar = Snackbar.make(binding.root, rationale, Snackbar.LENGTH_LONG)
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            getSimplePermission.launch(permission)
                        }
                    }
                })
                snackbar.show()
            }
            else -> {
                getSimplePermission.launch(permission)
            }
        }
    }

    fun search(view: View?) {
        findAddress()
    }


    private fun findAddress() {
        val addressString = mAddress.text.toString()
        if (addressString.isNotEmpty()) {
            try {
                val addresses = mGeocoder.getFromLocationName(
                    addressString, 3
                )
                if (addresses != null && !addresses.isEmpty()) {
                    val addressResult = addresses[0]
                    val position = LatLng(addressResult.latitude, addressResult.longitude)

                    end = ""
                    poly?.remove()

                    if(poly!=null){
                        poly = null
                        poly?.remove()
                    }

                    if(start.isNotEmpty())
                    {
                        end = "${position.longitude},${position.latitude}"
                        crateRouteBetween()
                    }

                    // Se crea la ruta hacia el lugar que se busca
                    // Cargar la imagen del marcador personalizado
                    val b = BitmapFactory.decodeResource(resources, R.drawable.boladisco)

                    // Definir el tamaño fijo que deseas para el marcador (en píxeles)
                    val width = 100
                    val height = 100

                    // Escalar la imagen al tamaño deseado
                    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)

                    // Agregar el marcador al mapa con el icono personalizado y tamaño fijo
                    mMap.addMarker(
                        MarkerOptions().position(position)
                            .title("Marker in my actual position ${position.latitude} ${position.longitude}")
                            .snippet("Empire State Building, New York, EE.UU.")
                            .alpha(0.9f)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))


                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        "Dirección no encontrada",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this@MapsActivity, "La dirección está vacía", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @SuppressLint("MissingPermission")
    fun updateUI(permission: Boolean) {
        if (permission) {
            // granted
            logger.info("Permission granted")
            mMap.isMyLocationEnabled = true

            var fusedLocationClient: FusedLocationProviderClient
            var locationCallback: LocationCallback
            var polylineOptions = PolylineOptions()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val ubicacion = LatLng(location.latitude, location.longitude)

                    // Cargar la imagen del marcador personalizado
                    val b = BitmapFactory.decodeResource(resources, R.drawable.boladisco)

                    // Definir el tamaño fijo que deseas para el marcador (en píxeles)
                    val width = 100
                    val height = 100

                    // Escalar la imagen al tamaño deseado
                    val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)

                    // Agregar el marcador al mapa con el icono personalizado y tamaño fijo
                    mMap.addMarker(
                        MarkerOptions().position(ubicacion)
                            .title("Marker in my actual position ${location.latitude} ${location.longitude}")
                            .snippet("Empire State Building, New York, EE.UU.")
                            .alpha(0.9f)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    )

                    start = "${location.longitude},${location.latitude}"
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion))
                }
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.forEach { location ->
                        // Obtén la nueva ubicación
                        val latLng = LatLng(location.latitude, location.longitude)
                        start = "${location.longitude},${location.latitude}"

                        // Agrega el punto a PolylineOptions
                        polylineOptions.add(latLng)

                        // Dibuja el Polyline en el mapa
                        mMap.addPolyline(polylineOptions)


                        // Mueve la cámara al nuevo punto
                        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }
            }

            val locationRequest = LocationRequest.create().apply {
                interval = 10000 // Intervalo de actualización de ubicación en milisegundos
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            // Verifica la configuración de ubicación
            val client = LocationServices.getSettingsClient(this)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                // Configuración de ubicación aceptada, comienza la actualización
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }

        } else {
            logger.warning("Permission denied")
        }
    }

    private fun crateRouteBetween() {
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRetrofit().create(ApiService::class.java)
                .getRoute("5b3ce3597851110001cf6248cbef574c40e34749865dc70189b066a8", start, end)
            if(call.isSuccessful)
            {
                drawRoute(call.body())
                call.body()
            } else {
                Log.i("lau",":( MAL")
            }
        }
    }

    private fun drawRoute(routeResponse: RouteResponse?) {
        val polylineOptions = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach{
            polylineOptions.add(LatLng(it[1],it[0]))
                .color(Color.RED)
        }
        runOnUiThread{
            poly = mMap.addPolyline(polylineOptions)
        }
    }

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder().baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}