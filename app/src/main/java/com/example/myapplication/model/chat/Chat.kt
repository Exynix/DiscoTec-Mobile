package com.example.myapplication.model.chat

import java.sql.Timestamp

class Chat constructor(
    val id: String,
    //val created: Timestamp,
    var messages: ArrayList<String>
){
}