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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Model.Parche
import com.example.myapplication.Model.User
import com.example.myapplication.adapter.ParceroAdapter
import com.example.myapplication.databinding.ActivityCrearMiParcheBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.util.Date
import java.util.logging.Logger

class CrearMiParcheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearMiParcheBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    val usuarios = mutableListOf<User>()
    val usuariosSeleccionados = mutableListOf<User>()

    companion object {
        val TAG: String = CrearMiParcheActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)
    private val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        updateUICamara(it)
    }
    private var pictureImagePath: Uri? = null
    //var imageViewContainer: ImageView? = null

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Handle camera result
            binding.imageView.setImageURI(pictureImagePath)
           // binding.imageView!!.setScaleType(ImageView.ScaleType.FIT_CENTER)
           // binding.imageView!!.setAdjustViewBounds(true)
            binding.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            binding.imageView.adjustViewBounds = true
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
       // imageViewContainer = binding.imageView

        binding.camera.setOnClickListener {
            logger.info("Se va a solicitar el permiso")
            verifyPermissionsCam(this, Manifest.permission.CAMERA, "El permiso es requerido para acceder a la camara")
        }
        binding.gallery.setOnClickListener {
            val pickGalleryImage = Intent(Intent.ACTION_PICK)
            pickGalleryImage.type = "image/*"
            galleryActivityResultLauncher.launch(pickGalleryImage)
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
            val intent = Intent(applicationContext, ChatMenuActivity::class.java)
            startActivity(intent)
        }

        binding.perfilBtn.setOnClickListener {
            val intent = Intent(applicationContext, PerfilActivity::class.java)
            startActivity(intent)
        }

        binding.accept.setOnClickListener {
            createParche()
            finish()
        }

        setup()
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val idUsuario = user?.uid
        crearLista(idUsuario)
    }

    private fun createParche() {
        val parcheName = binding.editTextText.text.toString()
        val parcheImg = binding.imageView.toString()
        val parche = Parche(parcheName, "", parcheImg)

        dbRef = Firebase.database.reference.child("Parche")
        val parcheId = dbRef.push().key
        if (parcheId != null) {
            dbRef.child(parcheId).setValue(parche)
        }

        usuariosSeleccionados.forEach {
            dbRef = Firebase.database.reference.child("users").child(it.key!!).child("Parches")
            val id = dbRef.push().key
            if (parcheId != null && id != null) {
                dbRef.child(id).setValue(parcheId)
            }
        }

        dbRef = Firebase.database.reference.child("users").child(mAuth.currentUser!!.uid).child("Parches")
        val id = dbRef.push().key
        if (parcheId != null && id != null) {
            dbRef.child(id).setValue(parcheId)
        }

    }

    private fun crearLista(idUsuario: String?) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usuarios.clear()
                for (userSnapshot in dataSnapshot.children) {
                    val usuario = userSnapshot.getValue(User::class.java)
                    if (usuario != null && usuario.key != idUsuario) {
                        val nombre = usuario.nombre
                        val correo = usuario.correo
                        val nroId = usuario.nroId
                        val user = User(nombre, nroId, correo)
                        user.key = userSnapshot.key
                        usuarios.add(user)
                    }
                }
                if (usuarios.isNotEmpty()) {
                    initRecyclerView()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Maneja errores si es necesario
                Toast.makeText(this@CrearMiParcheActivity, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setup() {
        dbRef = Firebase.database.reference.child("users")
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.listaContactos.layoutManager = LinearLayoutManager(this)
        binding.listaContactos.adapter = ParceroAdapter(usuarios, usuariosSeleccionados)
    }

    private fun verifyPermissionsCam(context: Context, permission: String, rationale: String) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(binding.root, "Ya tengo los permisos 😜", Snackbar.LENGTH_LONG).show()

                updateUICamara(true)

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