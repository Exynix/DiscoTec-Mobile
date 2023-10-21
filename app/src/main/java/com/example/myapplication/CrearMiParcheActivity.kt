package com.example.myapplication

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.example.myapplication.databinding.ActivityCrearMiParcheBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.util.Date
import java.util.logging.Logger

class CrearMiParcheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearMiParcheBinding
    companion object {
        val TAG: String = CrearMiParcheActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)
    private val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        updateUICamara(it)
        updateUIContact(it)

    }
    val projection = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
        ContactsContract.Contacts.STARRED,
        ContactsContract.Contacts.PHOTO_URI)
    var pictureImagePath: Uri? = null
    //var imageViewContainer: ImageView? = null
    val filter = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} IS NOT NULL"
    val order = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} COLLATE NOCASE"
    lateinit var adapter: ContactInfoAdapterActivity


    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Handle camera result
            binding.imageView!!.setImageURI(pictureImagePath)
           // binding.imageView!!.setScaleType(ImageView.ScaleType.FIT_CENTER)
           // binding.imageView!!.setAdjustViewBounds(true)
            binding.imageView!!.scaleType = ImageView.ScaleType.FIT_CENTER
            binding.imageView!!.adjustViewBounds = true
            logger.info("Image capture successfully.")
        } else {
            logger.warning("Image capture failed.")
        }
    }
    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Handle gallery result
           // val imageUri: Uri? = result.data!!.data
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            binding.imageView!!.setImageURI(imageUri)
            logger.info("Image loaded successfully")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCrearMiParcheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listaContactos.isVisible = false
       // imageViewContainer = binding.imageView

        binding.camera.setOnClickListener {
            logger.info("Se va a solicitar el permiso")
            verifyPermissions(this, android.Manifest.permission.CAMERA, "El permiso es requerido para acceder a la camara", "Camara")
        }
        binding.gallery.setOnClickListener {
            val pickGalleryImage = Intent(Intent.ACTION_PICK)
            pickGalleryImage.type = "image/*"
            galleryActivityResultLauncher.launch(pickGalleryImage)
        }
        adapter = ContactInfoAdapterActivity(this, null)
        binding.listaContactos.adapter = adapter

        verifyPermissions(this, android.Manifest.permission.READ_CONTACTS, "El permiso es requerido para acceder a los contactos", "Contact")
    }
    private fun verifyPermissions(context: Context, permission: String, rationale: String, tipo: String) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(binding.root, "Ya tengo los permisos 😜", Snackbar.LENGTH_LONG).show()
                if(tipo == "Camara") {
                    updateUICamara(true)
                }else{
                    binding.listaContactos.isVisible = true
                    updateUIContact(true)
                }
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
    fun updateUICamara(permission : Boolean) {
        if(permission){
            //granted
            logger.info("Permission granted")
            dipatchTakePictureIntent()
        }else{
            logger.warning("Permission denied")
        }
    }
    fun updateUIContact(permission : Boolean) {
        if(permission){
            //granted
            Log.i("Permission: ", "Granted")
            // Assigning a cursor to init the adapter
            val cursor: Cursor? = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, filter, null, order)
            adapter.changeCursor(cursor)
        }else{
            Log.i("Permission: ", "Denied")
        }
    }
    fun dipatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Crear el archivo donde debería ir la foto
        var imageFile: File? = null
        try {
            imageFile = createImageFile()
        } catch (ex: IOException) {
            logger.warning(ex.message)
        }
        // Continua si el archivo ha sido creado exitosamente
        if (imageFile != null) {
            // Guardar un archivo: Ruta para usar con ACTION_VIEW intents
            pictureImagePath = FileProvider.getUriForFile(this,"com.example.myapplication.fileprovider", imageFile)
            logger.info("Ruta: ${pictureImagePath}")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureImagePath)
            try {
                cameraActivityResultLauncher.launch(takePictureIntent)
            } catch (e: ActivityNotFoundException) {
                logger.warning("Camera app not found.")
            }
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        //Crear un nombre de archivo de imagen
        val timeStamp: String = DateFormat.getDateInstance().format(Date())
        val imageFileName = "${timeStamp}.jpg"
        val imageFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),imageFileName)
        return imageFile
    }
    override fun onResume() {
        super.onResume()
        Log.w("Callback: ", "onResume")
    }

}