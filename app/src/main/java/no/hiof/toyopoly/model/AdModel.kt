package no.hiof.toyopoly.model

import java.sql.Timestamp

data class AdModel(
    val value:String,
    val description:String,
    val price: String,
    val category: String,
    val userId: String,
    val timestamp: com.google.firebase.Timestamp
                   )
