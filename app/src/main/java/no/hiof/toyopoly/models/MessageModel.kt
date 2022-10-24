package no.hiof.toyopoly.models

data class MessageModel(
    val messageId : String,
    val message : String,
    val timestamp: com.google.firebase.Timestamp,
    val senderId : String,
    //val receiverId : String
    ){}
