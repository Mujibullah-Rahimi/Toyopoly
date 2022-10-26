package no.hiof.toyopoly.models

import androidx.annotation.Keep

@Keep
data class AdModel(
    var adId: String = "",
    var value:String = "",
    var description:String = "",
    var price: String = "",
    var category: String ="",
    var remoteUri: String = "",
    var userId: String= "",
    var timestamp: com.google.firebase.Timestamp? = null,
    var token : Int = 0,
    //val img_ref : ReferenceSet? = null
                   )
