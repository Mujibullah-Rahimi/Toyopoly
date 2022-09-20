package no.hiof.toyopoly

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateAdsFragment : Fragment() {
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_createads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val saveButton = view?.findViewById<Button>(R.id.createAd)
        saveButton.setOnClickListener{
            saveAd()
        }
    }



    fun saveAd(){
        var title = view?.findViewById<EditText>(R.id.title_create_ad)
        var titleFire = title?.text.toString()
        var desc = view?.findViewById<EditText>(R.id.desc_createAd)
        var descFire = desc?.text.toString()
        var price = view?.findViewById<EditText>(R.id.price_createAd)
        var priceFire = price?.text.toString()

        val ad = hashMapOf(
            "TimeStamp" to Timestamp.now(),
            "Title" to titleFire,
            "Desc" to descFire,
            "price" to priceFire

        )

        db.collection("ads").document()
            .set(ad)
             .addOnSuccessListener {
                 Log.d(TAG, "DocumentSnapshot successfully updated!" )
        }
              .addOnFailureListener {e -> Log.w(TAG, "Error adding document", e)}
    }
}