package com.example.myapplication.model

class User {
    var key: String? = null
    var nombre: String = ""
    var nroId: Long = 0
    var correo: String = ""

    constructor()

    constructor(nombre: String, nroId: Long, correo: String) {
        this.nombre = nombre
        this.nroId = nroId
        this.correo = correo
    }
}