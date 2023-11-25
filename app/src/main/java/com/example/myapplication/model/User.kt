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
    userType: UserType
){
}