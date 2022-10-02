package no.hiof.toyopoly

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import no.hiof.toyopoly.model.AdModel

class CreateAdFragment : Fragment() {
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_createad, container, false)
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
        val title = view?.findViewById<EditText>(R.id.title_create_ad)
        val titleFire = title?.text.toString()
        val desc = view?.findViewById<EditText>(R.id.desc_createAd)
        val descFire = desc?.text.toString()
        val price = view?.findViewById<EditText>(R.id.price_createAd)
        val priceFire = price?.text.toString()
        val spinner = view?.findViewById<Spinner>(R.id.spinner_catergory)
        val spinnerFire = spinner?.selectedItem.toString()
        val userUID = user!!.uid

        val adToSave = AdModel(titleFire,descFire,priceFire,spinnerFire,userUID, Timestamp.now())

        db.collection("Ads").document()
            .set(adToSave)
             .addOnSuccessListener {
                 Toast.makeText(activity, "Ad was created successfully!", Toast.LENGTH_LONG)
                     .show()
        }
              .addOnFailureListener {
                  Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                      .show()
              }
    }
}