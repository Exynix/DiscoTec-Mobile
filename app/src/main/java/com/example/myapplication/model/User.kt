package com.example.myapplication.model

class User {
    var key: String? = null
    var nombre: String = ""
    var nroId: Long = 0
    var correo: String = ""
    var longitud: Double =0.0
    var latitud: Double =0.0

    constructor(nombre: String, nroId: Long, correo: String, longitud: Double, latitud: Double) {
        this.nombre = nombre
        this.nroId = nroId
        this.correo = correo
        this.longitud = longitud
        this.latitud = latitud
    }

/*<<<<<<< HEAD
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

=======
    constructor()


>>>>>>> 95db9e3659860dad0fdbe8dc110678012d81295c*/
}