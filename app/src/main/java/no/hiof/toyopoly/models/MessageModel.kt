package no.hiof.toyopoly.models

import com.google.firebase.Timestamp

data class MessageModel(
    val messageId : String,
    val message : String,
    val timestamp: com.google.firebase.Timestamp,
    val senderId : String,
    //val receiverId : String
    ){
    constructor() :this(messageId = "", message = "", timestamp = Timestamp.now(), senderId = "")
}
