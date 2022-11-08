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
    var category: String ="Other",
    var imageUri: String = "",
    var userId: String= "",
    var timestamp: com.google.firebase.Timestamp,
    var token : Int = 0,
    //val img_ref : ReferenceSet? = null
                   ){
    constructor() : this(adId = "", title ="", description="", price="", category="Other", imageUri = "", userId="", timestamp = Timestamp.now(), token = 0)
}
