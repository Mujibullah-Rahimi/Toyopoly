package no.hiof.toyopoly.model

import androidx.annotation.Keep
import com.google.firebase.firestore.local.ReferenceSet
import java.sql.Timestamp

@Keep
data class AdModel(
    var adId: String = "",
    var value:String = "",
    var description:String = "",
    var address:String = "",
    var price: String = "",
    var category: String ="",
    var remoteUri: String = "",
    var userId: String= "",
    var timestamp: com.google.firebase.Timestamp? = null,
    //val img_ref : ReferenceSet? = null
                   ){}
