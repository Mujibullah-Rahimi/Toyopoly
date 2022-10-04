package no.hiof.toyopoly.model

import androidx.annotation.Keep
import java.sql.Timestamp

@Keep
data class AdModel(
    val adId: String = "",
    val value:String = "",
    val description:String = "",
    val price: String = "",
    val category: String ="",
    val userId: String= "",
    val timestamp: com.google.firebase.Timestamp? = null
                   ){}
