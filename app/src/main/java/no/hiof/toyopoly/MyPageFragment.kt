package no.hiof.toyopoly

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import no.hiof.toyopoly.adapter.AdapterAds
import no.hiof.toyopoly.customDialog.TokenDialog
import no.hiof.toyopoly.models.AdModel


class MyPageFragment : Fragment(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var adsArrayList: ArrayList<AdModel>
    private lateinit var adapterAds: AdapterAds
    private val db = FirebaseFirestore.getInstance()
    private val user = Firebase.auth.currentUser
    private val TAG = "UserPhoto"
    private lateinit var userImage : ImageView
    private var userImageURI: Uri? = null
    private var storageRef = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userImage = view.findViewById(R.id.profilePicImageView)
        getUser()
        val changePic = view.findViewById<Button>(R.id.changePicBtn)
        changePic.setOnClickListener{
                invokeGallery()
        }
        recyclerView = view.findViewById(R.id.my_ads_recycler)
        recyclerView.layoutManager = LinearLayoutManager (this.activity)

        adsArrayList = arrayListOf()

        adapterAds = AdapterAds(adsArrayList){ad ->

            val builder = AlertDialog.Builder(this.activity)
            builder.setTitle(R.string.dialogTitle)
            builder.setMessage(R.string.dialogMessage)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("Gå til"){dialogInterface, which ->
                val action = MyPageFragmentDirections.actionMyPageFragmentToAdDetailFragment(ad.adId)

                val navController = view.findNavController()

                navController.navigate(action)

            }

            builder.setNegativeButton("Slett"){dialogInterface, which ->
                deleteAd(ad.adId)
                val action = MyPageFragmentDirections.actionMyPageFragmentSelf()

                val navController = view.findNavController()

                navController.navigate(action)
                Toast.makeText(this.activity,"Annonse slettet",Toast.LENGTH_LONG).show()
            }
            builder.show()

        }
        recyclerView.adapter = adapterAds

        val tokenBtn = view.findViewById<Button>(R.id.tokensBtn)
        tokenBtn.setOnClickListener{
            TokenDialog().show(childFragmentManager, "TokenDialog")
        }

        getAds()
    }

    private val getImageFromGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data!!
            userImageURI = fileUri
            Log.v(TAG, "$fileUri")
            userImage.setImageURI(fileUri)

            storageRef.child("images/users/${user!!.uid}").putFile(userImageURI!!).addOnSuccessListener {
                db.collection("Users").document(userUID).update("imageUri", it.storage.path)
                getUser()
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            Log.v(TAG, ImagePicker.getError(data))
        } else {
            Toast.makeText(activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
            Log.v(TAG, ImagePicker.getError(data))
        }
    }

    //permission handling

    fun openGallery() {
        ImagePicker.with(requireActivity())
            .galleryOnly()
            .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                getImageFromGallery.launch(intent)
            }
    }

    private fun invokeGallery(){
        if(hasExternalReadStoragePermission() == PackageManager.PERMISSION_GRANTED){
            openGallery()
        }else{
            requestMultiplePermissionsLauncher.launch(arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ))
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
                openGallery()
            } else {
                Toast.makeText(this.activity, "No read permission", Toast.LENGTH_LONG)
                    .show()
            }
        }

    fun hasExternalReadStoragePermission() = this.activity?.let {
        ContextCompat.checkSelfPermission(
            it,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    val userUID = user!!.uid

    fun getAds(){
        db.collection("Ads")
            .whereEqualTo("userId", userUID)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null){
                        Log.e("Firestore ERROR", error.message.toString())
                        return
                    }
                    for ( dc : DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED){
                            adsArrayList.add(dc.document.toObject(AdModel::class.java))
                        }
                    }
                    adapterAds.notifyDataSetChanged()
                }
            })
    }

    fun getUser() {
        val getName = view?.findViewById<TextView>(R.id.nameUserPage)
        val getEmail = view?.findViewById<TextView>(R.id.emailUserPage)
        val getTokens = view?.findViewById<TextView>(R.id.userTokens)
        val getAddress = view?.findViewById<TextView>(R.id.addressUserPage)

        userImage.setImageURI(null)
        getName?.text =""
        getEmail?.text =""
        val docRef = db.collection("Users").document(userUID)
        docRef
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("currentUser", "Snapshot: ${document.data}")
                    getEmail?.text = document.getString("email")
                    getName?.text = document.getString("firstName")+ " " + document.getString("lastName")
                    getTokens?.text = "Tokens: " + document.getLong("token").toString()
                    getAddress?.text = document.getString("address")
                    //time_ad?.text = document.getDate("timestamp").toString()
                } else {
                    Log.d("isNotHere", "The document snapshot doesn't exist")
                }
            }
            .addOnFailureListener { e -> Log.d("Error", "Fail at: ", e) }

        val pictureReference = storageRef.storage.getReference("images/users/${user!!.uid}")

        Glide.with(requireView())
            .load(pictureReference)
            .into(userImage)
    }

    fun updateImage(){

    }
    fun deleteAd(documentId: String) {

        db.collection("Ads").document(documentId)
            .delete()
            .addOnFailureListener { e -> Log.d("Error", "Fail at: ", e) }
    }
}