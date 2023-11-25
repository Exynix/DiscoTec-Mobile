package com.example.myapplication.model

import com.example.myapplication.lookupTableClasses.UserType
import com.google.firebase.auth.FirebaseUser
import java.sql.Timestamp

class User constructor(
    firebaseUser: FirebaseUser,
    name: String,
    lastName: String,
    idNumber: Number,
    birthdate: Timestamp,
    nightcoins: Float,
    latitud: Double,
    longitud: Double,
    userType: UserType
){

    var name: String = ""
    var lastName: String = ""
    var idNumber: Long = 0
    var latitud: Double = 0.0
    var longitud: Double = 0.0

}