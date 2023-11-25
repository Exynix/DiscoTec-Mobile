package com.example.myapplication

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.myapplication.Model.User
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.util.Date
import java.util.logging.Logger

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    private lateinit var user: User

    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var emailEdit: EditText
    private lateinit var passEdit: EditText
    private lateinit var cedulaEdit: EditText
    private lateinit var scannerBtn: Button

    companion object {
        val TAG: String = SignUpActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)

    private val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        updateUI(it)
    }

    // Create ActivityResultLauncher instances
    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Handle camera result
            logger.info("Image capture successfully.")
        } else {
            logger.warning("Image capture failed.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth
        dbRef = Firebase.database.reference.child("users")

        loginBtn = binding.gotoLoginBtn
        signUpBtn = binding.signUpBtn
        emailEdit = binding.emailInput
        passEdit = binding.passwordInput
        cedulaEdit = binding.cedulaInput
        scannerBtn = binding.scanner

        loginBtn.setOnClickListener {
            finish()
        }

        signUpBtn.setOnClickListener {
            signUp()
        }

        scannerBtn.setOnClickListener {
            verifyPermissions(this, android.Manifest.permission.CAMERA, "El permiso es requerido para...")
        }
    }

    fun isEmailValid(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email: String = emailEdit.text.toString()
        if (TextUtils.isEmpty(email)) {
            emailEdit.error = "Required"
            valid = false
        } else {
            emailEdit.error = null
        }
        val password: String = passEdit.text.toString()
        if (TextUtils.isEmpty(password)) {
            passEdit.error = "Required"
            valid = false
        } else {
            passEdit.error = null
        }
        val cedula: String = cedulaEdit.text.toString()
        if (TextUtils.isEmpty(cedula)) {
            cedulaEdit.error = "Required"
            valid = false
        } else {
            cedulaEdit.error = null
        }

        return valid
    }

    private fun signUp() {
        val email = emailEdit.text.toString()
        val pass = passEdit.text.toString()
        if (!validateForm()) {
            return
        } else if (!isEmailValid(email)) {
            Toast.makeText(this@SignUpActivity, "Email is not a valid format", Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                saveUserData {
                    // Ir a la pantalla de inicio
                    val homeIntent = Intent(this, DashboardActivity::class.java)
                    Snackbar.make(this.findViewById(android.R.id.content), "Usuario creado con éxito", Snackbar.LENGTH_LONG).show()
                    startActivity(homeIntent)
                }
            }
        }.addOnFailureListener(this) { e ->
            Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun saveUserData(onSuccessListener: OnSuccessListener<Void>? = null) {
        user = User()
        if (mAuth.currentUser == null) {
            showAlert()
            return
        }
        val userId = mAuth.currentUser?.uid.toString()
        user.key = userId
        user.nombre = binding.nombreInput.text.toString()
        user.correo = binding.emailInput.text.toString()
        user.nroId = binding.cedulaInput.text.toString().toLong()

        dbRef.child(userId).setValue(user).addOnCompleteListener {
            onSuccessListener?.onSuccess(null)
        }.addOnFailureListener {err ->
            Toast.makeText(this,"Error: ${err.message}", Toast.LENGTH_LONG).show()
        }
    }

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

    fun updateUI(permission : Boolean) {
        if(permission){
            //granted
            logger.info("Permission granted")
            dipatchTakePictureIntent()
        } else {
            logger.warning("Permission denied")
        }
    }

    private fun dipatchTakePictureIntent() {
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
            val pictureImagePath = FileProvider.getUriForFile(this,"com.example.myapplication.fileprovider", imageFile)
            logger.info("Ruta: $pictureImagePath")
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

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}