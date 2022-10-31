package no.hiof.toyopoly.ad

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.FirebaseStorage
import com.wajahatkarim3.easyvalidation.core.view_ktx.*
import no.hiof.toyopoly.R
import no.hiof.toyopoly.models.AdModel
import no.hiof.toyopoly.models.PhotoModel
import no.hiof.toyopoly.util.RandomId

class CreateAdFragment : Fragment() {
    private var uri: Uri? = null

    //private lateinit var currentImagePath: String
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    private var photos: ArrayList<PhotoModel> = ArrayList<PhotoModel>()
    private var storageReference = FirebaseStorage.getInstance().getReference()
    //private lateinit var photo: PhotoModel
    //private var storageReference = FirebaseStorage.getInstance().getReference()
    private lateinit var photo: PhotoModel

    //private lateinit var viewModel : AdsViewModel
    private lateinit var ads: AdModel
    //private lateinit var _binding :

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_createad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel = ViewModelProvider.get(AdsViewModel::class.java)

        val spinner: Spinner = view.findViewById(R.id.spinner_catergory)
        this.activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.toyCategory,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        }
        val saveButton = view.findViewById<Button>(R.id.createAd)
        saveButton.setOnClickListener {
            setTokens()
            saveAd(RandomId.randomID())
          saveAd(RandomId.randomID())
        }

        val photoBtn = view.findViewById<Button>(R.id.photoBtn)
        photoBtn.setOnClickListener {

        }


    }

    fun setTokens(){
        val price = view?.findViewById<EditText>(R.id.price_createAd)
        val price1 = price?.text.toString()
        val price2 = Integer.parseInt(price1)
        var token = 0
    /*private fun uploadImg() {
        var uri = Uri.parse(photo.localUri)
        var imageRef = storageReference.child("images/${user?.uid}/${uri.lastPathSegment}")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            Log.i(TAG, "Image uploaded $imageRef")
            val downloadUrl = imageRef.downloadUrl
            downloadUrl.addOnSuccessListener { remoteUri ->
                photo.remoteUri = remoteUri.toString()
                updatePhotoDatabase(photo)
            }
        }
        uploadTask.addOnFailureListener {
            Log.e(TAG, it.message ?: "no message")
        }
    }*/

        if(price2 <= 100){
            tokenValue = 1
        } else if(price2 <= 200){
            tokenValue = 2
        }else if(price2 <= 500){
            tokenValue = 5
        }else if(price2 <= 800){
            tokenValue = 8
        }else if(price2 <= 1000){
            tokenValue = 1000
        }else if(price2 <= 1500){
            tokenValue = 15
        }else if(price2 <= 2000){
            tokenValue = 20
        }else if(price2 <= 2500){
            tokenValue = 25
        }
        else if(price2 <= 10000){
            tokenValue = 100
        }else if(price2 <= 99999){
            tokenValue = 999
        }

        Log.d(TAG, "${token}")
    }
    var tokenValue = 0

    fun saveAd(documentId: String) {
        val title = view?.findViewById<EditText>(R.id.title_create_ad)
        val titleFire = title?.text.toString()
        val desc = view?.findViewById<EditText>(R.id.desc_createAd)
        val descFire = desc?.text.toString()
        val addr = view?.findViewById<EditText>(R.id.address_createAd)
        val addrFire = addr?.text.toString()
        val prices = view?.findViewById<EditText>(R.id.price_createAd)
        val priceFire = prices?.text.toString()
        val price1 = Integer.parseInt(priceFire)
        var price2 = price1.toString()
        val spinner = view?.findViewById<Spinner>(R.id.spinner_catergory)
        val spinnerFire = spinner?.selectedItem.toString()
        val userUID = user!!.uid

        //changes the price input to a static value in line with the token system
        if(price1 <= 100){
            price2 = "100"
        }else if(price1 <= 200){
            price2 = "200"
        }else if(price1 <= 500){
            price2 = "500"
        }else if(price1 <= 800){
            price2 = "800"
        }else if(price1 <= 1000){
            price2 = "1000"
        }else if(price1 <= 1500){
            price2 = "1500"
        }else if(price1 <= 2000){
            price2 = "2000"
        }else if(price1 <= 2500){
            price2 = "2500"
        }
        else if(price1 <= 10000){
            price2 = "10000"
        }else if(price1 <= 99999){
            tokenValue = 99999
        }


        var adModel = AdModel().apply {
            adId = documentId
            value = title?.text.toString()
            description = desc?.text.toString()
            address = addr?.text.toString()
            price = prices?.text.toString()
            price = price2
            category = spinner?.selectedItem.toString()
       //     remoteUri = photo!!.remoteUri
            //remoteUri = photo!!.remoteUri
            userId = user!!.uid
            token = tokenValue
        }

        if (
            !title!!.nonEmpty() ||
            !desc!!.nonEmpty() ||
            !addr!!.nonEmpty() ||
            !prices!!.nonEmpty()
        ) {
            Toast.makeText(
                activity,
                "All fields needs to be filled",
                Toast.LENGTH_LONG
            ).show()
        } else if (!title.maxLength(16) || !title.atleastOneUpperCase()) {
            Toast.makeText(
                activity,
                "The title can only be 10 chars long and must have at least one uppercase char",
                Toast.LENGTH_LONG
            ).show()
        } else if (!desc.minLength(16)) {
            Toast.makeText(
                activity,
                "The description must at least be 16 chars long",
                Toast.LENGTH_LONG
            ).show()
        } else if (!prices.onlyNumbers() || !prices.maxLength(5)) {
            Toast.makeText(
                activity,
                "The price can only consist of numbers and can only be 999999",
                "The price can only consist of numbers",
                Toast.LENGTH_LONG
            ).show()
        }else if (!addr.minLength(8)) {
            Toast.makeText(
                activity,
                "address must be at least 8 chars long",
                Toast.LENGTH_LONG
            ).show()
        } else {
            db.collection("Ads").document(documentId)
                .set(adModel)
                .addOnSuccessListener {
                    title.text.clear()
                    desc.text.clear()
                    addr.text.clear()
                    prices.text.clear()
                    Toast.makeText(activity, "Ad was created successfully!", Toast.LENGTH_LONG)
                        .show()

                }.addOnCompleteListener {
                    val action =
                        CreateAdFragmentDirections.actionCreateAdsFragmentToAdDetailFragment(
                            adModel.adId
                        )
                    val navController = view?.findNavController()
                    navController?.navigate(action)
                    //uploadImg()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                        .show()
                }
        }
    }
}


