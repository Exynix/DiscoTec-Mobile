package com.example.myapplication.model

class Parche {
    var nombre: String = ""
    var parcheDescription: String = ""
    var parcheImg: String = ""
    constructor()
    constructor(nombre: String, parcheDescription: String, parcheImg: String) {
        this.nombre = nombre
        this.parcheDescription = parcheDescription
        this.parcheImg = parcheImg
    }
}