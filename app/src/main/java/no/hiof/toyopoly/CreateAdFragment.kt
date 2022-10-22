package no.hiof.toyopoly

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.wajahatkarim3.easyvalidation.core.view_ktx.*
import no.hiof.toyopoly.model.AdModel
import no.hiof.toyopoly.model.PhotoModel
import no.hiof.toyopoly.util.RandomId
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateAdFragment : Fragment() {
    private var uri: Uri? = null

    //private lateinit var currentImagePath: String
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    private var photos: ArrayList<PhotoModel> = ArrayList<PhotoModel>()
    private var storageReference = FirebaseStorage.getInstance().getReference()
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
            saveAd(RandomId.randomID())
        }

        val photoBtn = view.findViewById<Button>(R.id.photoBtn)
        photoBtn.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto() {
        if (hasCameraPermission() == PERMISSION_GRANTED && hasExternalWriteStoragePermission() == PERMISSION_GRANTED && hasExternalReadStoragePermission() == PERMISSION_GRANTED) {
            //invokeCamera()
            openGallery()
        } else {
            requestMultiplePermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap ->
            var permissionGranted = false
            resultsMap.forEach {
                if (it.value == true) {
                    permissionGranted = it.value
                } else {
                    permissionGranted = false
                    return@forEach
                }
            }
            if (permissionGranted) {
                //invokeCamera()
                openGallery()
            } else {
                Toast.makeText(this.activity, "No camera without permission", Toast.LENGTH_LONG)
                    .show()
            }
        }


    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        getResult.launch(intent)
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                photo.localUri = it.data?.data!!.toString()
                getCameraImage.launch(uri)
            }else{
                Log.e(TAG, "Wrong with the URI")
            }
        }
    private val getCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()){
        success ->
      if(success){
          Log.i(TAG, "Img loc: ${uri}")
      }else{
         Log.e(TAG,"img no bueno. ${uri}")
     }
   }

    private fun uploadImg() {
        var uri = Uri.parse(photo?.localUri)
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
    }

    private fun updatePhotoDatabase(photo: PhotoModel) {
        var photoCollection = db.collection("Ads").document(ads.adId).collection("Images")
        var handle = photoCollection.add(photo)
        handle.addOnSuccessListener {
            Log.i(TAG, "Successfully updated photo metadata")
            photo.id = it.id
            db.collection("Ads").document(ads.adId).collection("Images").document(photo.id)
                .set(photo)
        }
        handle.addOnFailureListener {
            Log.e(TAG, "Error updating photo data: ${it.message}")
        }
    }

    fun hasCameraPermission() =
        this.activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }

    fun hasExternalWriteStoragePermission() = this.activity?.let {
        ContextCompat.checkSelfPermission(
            it,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    fun hasExternalReadStoragePermission() = this.activity?.let {
        ContextCompat.checkSelfPermission(
            it,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


    fun saveAd(documentId: String) {
        val title = view?.findViewById<EditText>(R.id.title_create_ad)
        val titleFire = title?.text.toString()
        val desc = view?.findViewById<EditText>(R.id.desc_createAd)
        val descFire = desc?.text.toString()
        val prices = view?.findViewById<EditText>(R.id.price_createAd)
        val priceFire = prices?.text.toString()
        val spinner = view?.findViewById<Spinner>(R.id.spinner_catergory)
        val spinnerFire = spinner?.selectedItem.toString()
        val userUID = user!!.uid

        var adModel = AdModel().apply {
            adId = documentId
            value = title?.text.toString()
            description = desc?.text.toString()
            price = prices?.text.toString()
            category = spinner?.selectedItem.toString()
            remoteUri = photo!!.remoteUri
            userId = user!!.uid
        }

        if (
            !title!!.nonEmpty() ||
            !desc!!.nonEmpty() ||
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
        } else if (!prices.onlyNumbers()) {
            Toast.makeText(
                activity,
                "The price can only consist of numbers",
                Toast.LENGTH_LONG
            ).show()
        } else {
            db.collection("Ads").document(documentId)
                .set(adModel)
                .addOnSuccessListener {
                    title.text.clear()
                    desc.text.clear()
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
                    uploadImg()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                        .show()
                }
        }
    }
}


