package no.hiof.toyopoly.model

import java.sql.Timestamp

data class MessageModel(
    val message : String,
    val timestamp: com.google.firebase.Timestamp,
    val senderId : String,
    //val receiverId : String
    ){}
