package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import java.util.logging.Logger

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        val REQUEST_CODE_LOCATION = 0
        val TAG: String = MapsActivity::class.java.name
    }

    private val logger = Logger.getLogger(TAG)

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
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
        mMap.uiSettings.isMapToolbarEnabled = true

        verifyPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION, "El permiso es requerido para poder mostrar tu ubicación en el mapa")
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

                    // Añadir un marcador personalizado
                    val b = BitmapFactory.decodeResource(resources, R.drawable.boladisco)
                    val smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false)
                    mMap.addMarker(
                        MarkerOptions().position(ubicacion)
                            .title("Marker in my actual position ${location.latitude} ${location.longitude}")
                            .snippet("Empire State Building, New York, EE.UU.")
                            .alpha(0.9f)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
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
}