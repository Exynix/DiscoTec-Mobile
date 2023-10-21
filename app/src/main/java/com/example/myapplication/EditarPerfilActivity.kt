package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityEditarPerfilBinding
import java.util.logging.Logger


class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding

    companion object {
        val TAG: String = EditarPerfilActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)

    var pictureImagePath: Uri? = null
    var imageViewContainer: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editarFotoBtn.setOnClickListener {
            // Intent hacia editar foto
            val intent = Intent(applicationContext, EditarFotoActivity::class.java)
            startActivity(intent)
        }

    }
}