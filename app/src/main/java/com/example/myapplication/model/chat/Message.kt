package com.example.myapplication.model.chat

import java.sql.Timestamp

class Message constructor(
    val id: String,
    val senderID: String,
    var content: String,
    val timeSent: Timestamp,
) {
}