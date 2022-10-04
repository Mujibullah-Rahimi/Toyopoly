package no.hiof.toyopoly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import no.hiof.toyopoly.model.AdModel
import no.hiof.toyopoly.util.RandomId
import kotlin.random.Random

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
            saveAd(RandomId.randomID())
        }
    }

    fun saveAd(documentId:String){
        val title = view?.findViewById<EditText>(R.id.title_create_ad)
        val titleFire = title?.text.toString()
        val desc = view?.findViewById<EditText>(R.id.desc_createAd)
        val descFire = desc?.text.toString()
        val price = view?.findViewById<EditText>(R.id.price_createAd)
        val priceFire = price?.text.toString()
        val spinner = view?.findViewById<Spinner>(R.id.spinner_catergory)
        val spinnerFire = spinner?.selectedItem.toString()
        val userUID = user!!.uid


        val adToSave = AdModel(
            documentId,
            titleFire,
            descFire,
            priceFire,
            spinnerFire,
            userUID,
            Timestamp.now()
        )
        db.collection("Ads").document(documentId)
            .set(adToSave)
             .addOnSuccessListener {
                 title!!.text.clear()
                 desc!!.text.clear()
                 price!!.text.clear()
                 Toast.makeText(activity, "Ad was created successfully!", Toast.LENGTH_LONG)
                     .show()

        }.addOnCompleteListener {
                val action  = CreateAdFragmentDirections.actionCreateAdsFragmentToAdDetailFragment(adToSave.adId)
                val navController = view?.findNavController()
                navController?.navigate(action)
            }
              .addOnFailureListener {
                  Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                      .show()
              }
    }
}