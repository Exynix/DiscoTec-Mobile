package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.widget.ImageView
import com.example.myapplication.databinding.ActivityPerfilBinding
import com.example.myapplication.databinding.ActivityQrBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.common.BitMatrix

class QrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val CantidadPersonas = intent.getStringExtra("CantidadPersonas")
        val Fecha = intent.getStringExtra("Fecha")
        val Hora = intent.getStringExtra("Hora")
        val Comentario = intent.getStringExtra("Comentario")

        // Llama a la función para generar el código QR con el contenido deseado
        val qrContent = "Cantidad de personas: $CantidadPersonas, Fecha: $Fecha, Hora: $Hora, Comentario: $Comentario" // Reemplaza con la información real del comprobante
        val width = 400 // Ancho del código QR (puedes ajustarlo)
        val height = 400 // Alto del código QR (puedes ajustarlo)
        val qrCode = generateQRCode(qrContent, width, height)

        binding.qrImageView.setImageBitmap(qrCode)
    }

    fun generateQRCode(content: String, width: Int, height: Int): Bitmap? {
        try {
            val writer = QRCodeWriter()
            val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height)
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            return bmp
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
}