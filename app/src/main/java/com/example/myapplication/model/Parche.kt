package com.example.myapplication.model

import com.example.myapplication.model.chat.Chat
import java.io.FileDescriptor

class Parche constructor(
    val id: String,
    var name:String,
    var imageUrl: String,
    var description: String,
    var chat: Chat

) {
}