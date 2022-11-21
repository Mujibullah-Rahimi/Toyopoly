package no.hiof.toyopoly.ad

//import com.google.firebase.storage.FirebaseStorage
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.wajahatkarim3.easyvalidation.core.view_ktx.maxLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.onlyNumbers
import no.hiof.toyopoly.R
import no.hiof.toyopoly.adapter.AdapterCat
import no.hiof.toyopoly.models.AdModel
import no.hiof.toyopoly.models.CategoryModel
import no.hiof.toyopoly.util.RandomId
import java.util.*


class CreateAdFragment : Fragment() {

    // objects and lists
    private var adImages: ArrayList<ImageView> = ArrayList<ImageView>()
    private val user = Firebase.auth.currentUser
    private lateinit var ad: AdModel
    private lateinit var adapterCat: AdapterCat
    private lateinit var catArrayList : ArrayList<CategoryModel>
    private var progressBarStatus = 0
    var progress:Int = 0

    // database instances and references
    private val db = Firebase.firestore
    private var storageRef = FirebaseStorage.getInstance().reference

    // strings
    private var TAG = "PHOTO"
    private lateinit var adImage: ImageView
    private var imageURI: Uri? = null

    //private lateInit var viewModel : AdsViewModel
    //private lateInit var _binding :

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
        adImage = view.findViewById(R.id.adImageView)
        val galleryBtn = view.findViewById<Button>(R.id.galleryBtn)
        galleryBtn.setOnClickListener {
            invokeGallery()
        }
        val photoBtn = view.findViewById<Button>(R.id.photoBtn)
        photoBtn.setOnClickListener{
            invokeCamera()
        }
        catArrayList = arrayListOf()

        val spinner: Spinner = view.findViewById(R.id.spinner_category)
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
        val title = view.findViewById<EditText>(R.id.title_create_ad)
        val title_txt = view.findViewById<TextView>(R.id.title_createad_txt)
        val desc = view.findViewById<EditText>(R.id.desc_createAd)
        val desc_txt = view.findViewById<TextView>(R.id.desc_createad_txt)
        val addr = view.findViewById<EditText>(R.id.address_createAd)
        val addr_txt = view.findViewById<TextView>(R.id.address_createad_txt)
        val prices = view.findViewById<EditText>(R.id.price_createAd)
        val price_txt = view.findViewById<TextView>(R.id.price_createad_txt)
        val cat_txt = view.findViewById<TextView>(R.id.cat_createad_txt)
        val card_view = view.findViewById<CardView>(R.id.cardView3)
        val price = prices.text.toString()


        val saveButton = view.findViewById<Button>(R.id.createAd)

//        when(!prices.nonEmpty()){
//            true -> {
//                saveButton.alpha = 0.5f
//                saveButton.isClickable = false
//            }
//            false ->
//        }
        saveButton.setOnClickListener { v ->
            val progressBar = view.findViewById<ProgressBar>(R.id.progressBar_creatad)
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
            } else if (!title.maxLength(20)) {
                Toast.makeText(
                    activity,
                    "Title is too long",
                    Toast.LENGTH_LONG
                ).show()
            } else if (!desc.minLength(16)) {
                Toast.makeText(
                    activity,
                    "Description is too short",
                    Toast.LENGTH_LONG
                ).show()
            } else if (!prices.onlyNumbers() || !prices.maxLength(5)) {
                Toast.makeText(
                    activity,
                    "Price can only consist of numbers and can only be 999999",
                    Toast.LENGTH_LONG
                ).show()
            }else if (!addr.minLength(0)) {
                Toast.makeText(
                    activity,
                    "address must be at least 8 chars long",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                progressBar.isVisible = true
                title.isVisible = false
                title_txt.isVisible = false
                desc.isVisible = false
                desc_txt.isVisible = false
                addr_txt.isVisible = false
                addr.isVisible = false
                price_txt.isVisible = false
                prices.isVisible = false
                cat_txt.isVisible = false
                card_view.isVisible = false
                galleryBtn.isVisible = false
                photoBtn.isVisible = false
                spinner.isVisible = false
                saveButton.isVisible = false
            }
            Thread(Runnable {
                try {
                    Looper.prepare()
                    saveAd(RandomId.randomID())
                }catch (e: InterruptedException){
                    e.printStackTrace()
                }

            }).start()
        }

/*
        if (!title!!.nonEmpty() ||
            !desc!!.nonEmpty() ||
            !addr!!.nonEmpty() ||
            !prices!!.nonEmpty()){
            saveButton.alpha = 0.5f
            saveButton.isClickable = false
        }else{
            saveButton.alpha = 1f
            saveButton.isClickable = true
        }
        */
    }

    private fun invokeGallery(){
        if(hasExternalReadStoragePermission() == PERMISSION_GRANTED){
            openGallery()
        }else{
            requestGalleryPermissionsLauncher.launch(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ))
        }
    }

    private fun invokeCamera(){
        if(hasCameraPermission() == PERMISSION_GRANTED){
            openCamera()
        }else{
            requestCameraPermissionsLauncher.launch(arrayOf(
                Manifest.permission.CAMERA
            ))
        }
    }

    private fun openGallery() {
        ImagePicker.with(requireActivity())
            .galleryOnly()
            .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                getImageResult.launch(intent)
            }
    }

    private fun openCamera(){
        ImagePicker.with(requireActivity())
            .cameraOnly()
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                getImageResult.launch(intent)
            }
    }

    private val requestGalleryPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap ->
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
            Toast.makeText(this.activity, "Allow permission to gallery", Toast.LENGTH_LONG)
                .show()
        }
    }
    private val requestCameraPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap ->
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
            Toast.makeText(this.activity, "Allow permission to gallery", Toast.LENGTH_LONG)
                .show()
        }
    }


    private val getImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data!!
            imageURI = fileUri
            Log.v(TAG, "$fileUri")
            adImage.setImageURI(fileUri)

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            Log.v(TAG, ImagePicker.getError(data))
        } else {
            Toast.makeText(activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
            Log.v(TAG, ImagePicker.getError(data))
        }
    }
    fun setTokens(){
        val price = view?.findViewById<EditText>(R.id.price_createAd)
        val price1 = price?.text.toString()
        val price2 = Integer.parseInt(price1)
        var token = 0

    if(price1.nonEmpty()) {
        if (price2 <= 100) {
            tokenValue = 1
        } else if (price2 <= 200) {
            tokenValue = 2
        } else if (price2 <= 500) {
            tokenValue = 5
        } else if (price2 <= 800) {
            tokenValue = 8
        } else if (price2 <= 1000) {
            tokenValue = 10
        } else if (price2 <= 1500) {
            tokenValue = 15
        } else if (price2 <= 2000) {
            tokenValue = 20
        } else if (price2 <= 2500) {
            tokenValue = 25
        } else if (price2 <= 10000) {
            tokenValue = 100
        } else if (price2 <= 99999) {
            tokenValue = 999
        }
    }else{
        Toast.makeText(activity, "Fill out every field", Toast.LENGTH_LONG).show()
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
        val priceFire = prices!!.text.toString()
        val price1 = Integer.parseInt(priceFire)
        var price2 = price1.toString()
        val spinner = view?.findViewById<Spinner>(R.id.spinner_category)
        val spinnerFire = spinner?.selectedItem.toString()
        val userUID = user!!.uid



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
        } else if (!title.maxLength(20)) {
            Toast.makeText(
                activity,
                "Title is too long",
                Toast.LENGTH_LONG
            ).show()
        } else if (!desc.minLength(16)) {
            Toast.makeText(
                activity,
                "Description is too short",
                Toast.LENGTH_LONG
            ).show()
        } else if (!prices.onlyNumbers() || !prices.maxLength(5)) {
            Toast.makeText(
                activity,
                "Price can only consist of numbers and can only be 999999",
                Toast.LENGTH_LONG
            ).show()
        }else if (!addr.minLength(0)) {
            Toast.makeText(
                activity,
                "address must be at least 8 chars long",
                Toast.LENGTH_LONG
            ).show()
        } else {
            setTokens()
            //changes the price input to a static value in line with the token system
            if (price1 <= 100) {
                price2 = "100"
            } else if (price1 <= 200) {
                price2 = "200"
            } else if (price1 <= 500) {
                price2 = "500"
            } else if (price1 <= 800) {
                price2 = "800"
            } else if (price1 <= 1000) {
                price2 = "1000"
            } else if (price1 <= 1500) {
                price2 = "1500"
            } else if (price1 <= 2000) {
                price2 = "2000"
            } else if (price1 <= 2500) {
                price2 = "2500"
            } else if (price1 <= 10000) {
                price2 = "10000"
            } else if (price1 <= 99999) {
                tokenValue = 99999
            }

            val adToSave = AdModel(
                adId = documentId,
                title = title?.text.toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT) else it.toString() },
                description = desc?.text.toString(),
                address = addr?.text.toString(),
                price = price2,
                category = spinner?.selectedItem.toString(),
                userId = user!!.uid,
                token = tokenValue,
                timestamp = Timestamp.now()
            )
            db.collection("Ads").document(documentId)
                .set(adToSave)
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
                            adToSave.adId
                        )
                    if (imageURI != null){
                        storageRef.child("images/ads/${adToSave.adId}").putFile(imageURI!!)
                            .addOnSuccessListener {
                                db.collection("Ads").document(adToSave.adId).update("imageUri", it.storage.path)

                                val navController = view?.findNavController()
                                navController?.navigate(action)
                            }
                        }else{
                            val navController = view?.findNavController()
                            navController?.navigate(action)
                        }
                    }
                .addOnFailureListener {
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                        .show()
                }
        }
    }
    fun getCategories() {
        db.collection("Category")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore ERROR", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            catArrayList.add(dc.document.toObject(CategoryModel::class.java))
                        }
                    }
                    adapterCat.notifyDataSetChanged()
                }
            })
    }
    fun hasExternalReadStoragePermission() = this.activity?.let {
        ContextCompat.checkSelfPermission(
            it,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
    fun hasCameraPermission() = this.activity?.let {
        ContextCompat.checkSelfPermission(
            it,
            Manifest.permission.CAMERA
        )
    }
}