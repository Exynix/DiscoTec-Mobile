package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.model.User
import com.google.firebase.firestore.FirebaseFirestore

class AuthenticationHandler {
    fun crearUsuario(user: User) {
        val document = FirebaseFirestore.getInstance().collection("Users").document()
        document.set(user).addOnSuccessListener {
            Log.d("Firebase", "Document saved")
        } .addOnFailureListener {
            Log.d("Firebase", "Document not saved")
        }
    }

    fun obtenerUsuario(id: String) {

    }
}