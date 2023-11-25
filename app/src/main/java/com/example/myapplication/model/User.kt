package com.example.myapplication.model

import com.example.myapplication.lookupTableClasses.UserType
import com.google.firebase.auth.FirebaseUser
import java.sql.Timestamp

class User(
    name: String,
    lastName: String,
    idNumber: String,
    birthdate: Timestamp,
    nightcoins: Float,
    userType: UserType
) {
    var name: String = name
    var lastName: String = lastName
    var idNumber: String = idNumber
    var birthdate: Timestamp = birthdate
    var nightcoins: Float = nightcoins
    var userType: UserType = userType
}