package no.hiof.toyopoly.ad

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.wajahatkarim3.easyvalidation.core.view_ktx.maxLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.onlyNumbers
import no.hiof.toyopoly.R
import no.hiof.toyopoly.models.AdModel
import no.hiof.toyopoly.util.RandomId
import java.util.*

class CreateAdFragment : Fragment() {

    // objects and lists
    private var adImages: ArrayList<ImageView> = ArrayList<ImageView>()
    private val user = Firebase.auth.currentUser
    private lateinit var ad: AdModel

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
            //takePhoto()
            ImagePicker.with(requireActivity())
                .galleryOnly()
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    getImageResult.launch(intent)
                }
        }

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

    fun saveAd(documentId: String) {
        val title = view?.findViewById<EditText>(R.id.title_create_ad)
        val desc = view?.findViewById<EditText>(R.id.desc_createAd)
        val prices = view?.findViewById<EditText>(R.id.price_createAd)
        val spinner = view?.findViewById<Spinner>(R.id.spinner_catergory)

        val adToSave = AdModel(
            adId = documentId,
            title = title?.text.toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
            description = desc?.text.toString(),
            price = prices?.text.toString(),
            category = spinner?.selectedItem.toString(),
            imageUri = "",
            userId = user!!.uid,
            timestamp = Timestamp.now()
        )

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
        } else if (!prices.onlyNumbers()) {
            Toast.makeText(
                activity,
                "Price can only consist of digits",
                Toast.LENGTH_LONG
            ).show()
        } else {
            db.collection("Ads").document(documentId)
                .set(adToSave)
                .addOnSuccessListener {
                    title.text.clear()
                    desc.text.clear()
                    prices.text.clear()
                    Toast.makeText(activity, "Ad was created successfully!", Toast.LENGTH_LONG)
                        .show()

                }.addOnCompleteListener {
                    val action =
                        CreateAdFragmentDirections.actionCreateAdsFragmentToAdDetailFragment(
                            adToSave.adId
                        )

                    storageRef.child("images/ads/${adToSave.adId}").putFile(imageURI!!)
                        .addOnSuccessListener {
                            db.collection("Ads").document(adToSave.adId).update("imageUri", it.storage.path)

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
}