package no.hiof.toyopoly

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
        val spinner : Spinner = view.findViewById(R.id.spinner_catergory)
        this.activity?.let {
            ArrayAdapter.createFromResource(it, R.array.toyCategory,android.R.layout.simple_spinner_item).also{
                adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        }
        val saveButton = view.findViewById<Button>(R.id.createAd)
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
        var spinner = view?.findViewById<Spinner>(R.id.spinner_catergory)
        var spinnerFire = spinner?.selectedItem.toString()

        val ad = hashMapOf(
            //persistenID til bruker
            "TimeStamp" to Timestamp.now(),
            "Vare" to titleFire,
            "Desc" to descFire,
            "Price" to priceFire,
            "Category" to spinnerFire

        )

        db.collection("ads").document()
            .set(ad)
             .addOnSuccessListener {
                 Log.d(TAG, "DocumentSnapshot successfully updated!" )
        }
              .addOnFailureListener {e -> Log.w(TAG, "Error adding document", e)}
    }
}