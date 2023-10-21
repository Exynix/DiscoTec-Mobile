package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.example.myapplication.databinding.ActivityContactInfoAdapterBinding

class ContactInfoAdapterActivity (context: Context, cursor: Cursor?) : CursorAdapter(context, cursor, 0) {


    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup?): View {
        // Inicializamos el binding
        val binding = ActivityContactInfoAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return binding.root
    }

    @SuppressLint("Range", "Recycle")
    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val binding = ActivityContactInfoAdapterBinding.bind(view!!)
        // Podemos usar el binding para acceder a los elementos de la vista, y el cursor para acceder a los datos del cursor

        // Nomnbre del contacto
        val contactNameText: TextView = binding.contactName
        contactNameText.text = cursor!!.getString(1)

        // Teléfono principal del contacto
        val contactId = cursor.getInt(0)
        val phoneCursor = context!!.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(contactId.toString()), null)
        if (phoneCursor != null && phoneCursor.moveToFirst()) {
            val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val buttonPhoneText: TextView = binding.buttonPhone
            buttonPhoneText.text = phoneNumber
            phoneCursor.close()
        }

        // Imagen si el contacto es favorito
        val contactFavImageView: ImageView = binding.contactFav
        val isStarred = cursor.getInt(2)
        if (cursor.getInt(isStarred) == 1) {
            contactFavImageView.setImageResource(R.drawable.baseline_star_30)
        }

        // Avatar del contacto si lo tiene, si no, avatar por defecto
        val contactPhotoImageView: ImageView = binding.contactPhoto
        val photoUri = cursor.getString(3)
        if (photoUri != null) {
            contactPhotoImageView.setImageURI(Uri.parse(photoUri))
        } else {
            // Si no hay foto de avatar, podemos mostrar una imagen predeterminada
            contactPhotoImageView.setImageResource(R.drawable.default_avatar)
        }
    }
}