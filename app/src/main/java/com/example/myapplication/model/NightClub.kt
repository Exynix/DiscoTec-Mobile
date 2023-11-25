package com.example.myapplication.model

import com.example.myapplication.lookupTableClasses.City
import com.example.myapplication.lookupTableClasses.Currency

class NightClub constructor(
    var id: String,
    var name: String,
    var cityID: City,
    var addressLine1: String,
    var addressLine2: String,
    var description: String,
    var coverPrice: Long,
    var currencyID: Currency,
    var imageUrl: String = ""
){

}