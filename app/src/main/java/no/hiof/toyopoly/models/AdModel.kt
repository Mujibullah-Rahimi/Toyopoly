package no.hiof.toyopoly.models

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class AdModel(
    var adId: String = "",
    var title:String = "",
    var description:String = "",
    var address:String = "",
    var price: String = "",
    var category: String ="",
    var imageUri: String = "",
    var userId: String= "",
    var timestamp: Timestamp,
    var token : Int = 0,
    var sold : Boolean = false
    //val img_ref : ReferenceSet? = null
                   ){
    constructor() : this(adId = "", title ="", description="", price="", category="", imageUri = "", userId="", timestamp = Timestamp.now(), token = 0, sold = false)
}
