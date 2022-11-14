package no.hiof.toyopoly.ad

import android.app.AlertDialog
import android.content.Intent
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
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import no.hiof.toyopoly.R
import no.hiof.toyopoly.models.ChatChannelModel
import no.hiof.toyopoly.util.RandomId


class AdDetailFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var docSnap: DocumentSnapshot
    private val args: AdDetailFragmentArgs by navArgs()
    private lateinit var navController : NavController
    val user = Firebase.auth.currentUser
    private lateinit var binding: AdDetailFragment
    private lateinit var supportMapFragment: SupportMapFragment


    private val currentUser = auth.currentUser!!.uid
    private lateinit var otherUser : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ad_detail, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        getUser()
        getAd()
        getImage()
        getOtherUserId()
        val buyItemBtn = view?.findViewById<Button>(R.id.buyBtn)
        val contactSellerButton = view.findViewById<Button>(R.id.contactSellerButton)
        db.collection("Ads").document(args.adId).get().addOnSuccessListener {
            if (it.getString("userId") == currentUser){
                contactSellerButton.text = getString(R.string.goToMyPage)
                buyItemBtn.visibility = View.INVISIBLE
                contactSellerButton.setOnClickListener{
                    navController.navigate(AdDetailFragmentDirections.actionAdDetailFragmentToMyPageFragment())
                }
            }else{
                Log.v("OtherUserInClick", otherUser)
                contactSellerButton.setOnClickListener{
                    createChatChannel(otherUser, RandomId.randomID())
                }
            }
        }

        buyItemBtn?.setOnClickListener{

            getTokenAmount()

            val builder = AlertDialog.Builder(this.activity)
            builder.setTitle(R.string.dialogTitle)
            builder.setMessage(R.string.dialogMessageBuy)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("Buy"){dialogInterface, which ->
                buyItem()
            }

            builder.setNegativeButton("Cancel"){dialogInterface, which ->
                val action = AdDetailFragmentDirections.actionAdDetailFragmentSelf(args.adId)

                val navController = view?.findNavController()

                navController?.navigate(action)
            }
            builder.show()
        }
    }



    private fun getAd(){
        val buyItemBtn = view?.findViewById<Button>(R.id.buyBtn)
        val title_ad = view?.findViewById<TextView>(R.id.adDetailTitle)
        val desc_ad = view?.findViewById<TextView>(R.id.adDetailDescription)
        val addr_ad = view?.findViewById<TextView>(R.id.adDetailAddress)
        val addr_ad_link = view?.findViewById<TextView>(R.id.adDetailAddressLink)
        val token_ad = view?.findViewById<TextView>(R.id.tokenPrice)
        //val time_ad = view?.findViewById<TextView>(R.id.adDetailTimeStamp)

        val docRef = db.collection("Ads").document(args.adId)
        docRef
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("isHere", "Snapshot: ${document.data}")
                    title_ad?.text = document.getString("title")
                    desc_ad?.text = document.getString("description")
                    addr_ad?.text = document.getString("address")
                    token_ad?.text = document.getLong("token").toString() + " Tokens"
                    val sold = document.getBoolean("sold")!!
                    if(sold == true){
                        buyItemBtn?.text = context?.getText(R.string.Sold)
                        buyItemBtn?.isClickable = false
                        buyItemBtn?.alpha = .5f
                    }
                    //time_ad?.text = document.getDate("timestamp").toString()
                }
                else{
                    Log.d("isNotHere", "The document snapshot doesn't exist")
                }
            }
            .addOnFailureListener{e -> Log.d("Error", "Fail at: ", e) }

        addr_ad_link?.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("geo:0,0?q=" + addr_ad?.text)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)

        }
    }

    fun getImage(){
        val adImage = view?.findViewById<ImageView>(R.id.imageViewAdDetail)

        val adRef = db.collection("Ads").document(args.adId)
        adRef.get().addOnSuccessListener {
            var imageUri = it.getString("imageUri")
            if (imageUri!!.isNotEmpty()){
                val storageReference = Firebase.storage.getReference(imageUri)

                Glide.with(this)
                    .load(storageReference)
                    .into(adImage!!)
            }
        }
    }

    private fun createChatChannel(otherUser : String, chatChannelId : String) {
        val userIds : MutableList<String> = mutableListOf(currentUser,otherUser)
        val chatChannelToSave = ChatChannelModel(chatChannelId,userIds)

        // get users documentReference
        var userDocRef = db.collection("Users").document(currentUser).collection("engagedChats").document(otherUser)

        userDocRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                // if there already is a chat between these two users -> navigate to it
                if (document.exists()) {
                    Log.v("ChatExists", document.id + task.result)
                    val existingChatId = document.getString("chatChannelId")!!
                    Log.v("ChatExists", existingChatId)
                    navController.navigate(
                        AdDetailFragmentDirections.actionAdDetailFragmentToMessageDetailFragment(
                            this.otherUser,existingChatId)
                        )
                }

                // if no previous chat exists -> create new chatChannel between them
                else {
                    db.collection("Users").document(currentUser)
                    .collection("engagedChats")
                        .document(otherUser)
                        .set(chatChannelToSave)

                    db.collection("Users").document(otherUser)
                        .collection("engagedChats")
                        .document(currentUser)
                        .set(chatChannelToSave)
                    Log.v("NoExistingChat", document.id)

                    // save their chatChannel to ChatChannels
                    db.collection("ChatChannels").document(chatChannelToSave.chatChannelId)
                        .set(chatChannelToSave)
                        .addOnSuccessListener {
                            Log.v("userIds", userIds.toString() )
                        }.addOnCompleteListener{
                            navController.navigate(
                                AdDetailFragmentDirections.actionAdDetailFragmentToMessageDetailFragment(
                                    otherUser, chatChannelId
                                )
                            )
                        }.addOnFailureListener{
                            Log.v("userIds", userIds.toString())
                        }
                }
            } else {
                Log.d("", "Failed with: ", task.exception)
            }
        }
    }

    val userUID = user!!.uid

    private fun getUser() {
        val docRef = db.collection("Users").document(userUID)
        docRef
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("currentUser", "Snapshot: ${document.data}")
                } else {
                    Log.d("isNotHere", "The document snapshot doesn't exist")
                }
            }
            .addOnFailureListener { e -> Log.d("Error", "Fail at: ", e) }
    }



    private fun getOtherUserId() {
        db.collection("Ads")
            .document(args.adId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null){
                    this.otherUser = document.getString("userId").toString()
                    Log.v("otherUser",otherUser)
                }
            }
    }
    var tokenValue : Long = 0


    private fun getTokenAmount() {

        db.collection("Ads").document(args.adId)
            .get()
            .addOnSuccessListener { document ->
                Log.d("currentToken", "Snapshot: ${document.data}")
                tokenValue = document.getLong("token")!!
            }
    }

    fun buyItem(){
        val docRefUsers = db.collection("Users")
        val docRefAds = db.collection("Ads")

        val myUser = docRefUsers.document(userUID).get().addOnSuccessListener {
            if (it != null){
                val myTokens = it.getLong("token")
                if (myTokens?.toInt()!! < tokenValue.toInt()){
                    Toast.makeText(activity, "Kjøp tokens først", Toast.LENGTH_LONG).show()
                }else{
                    docRefUsers.document(userUID).update("token", FieldValue.increment(-tokenValue))

                    docRefUsers.document(otherUser).update("token", FieldValue.increment(tokenValue))

                    docRefAds.document(args.adId).update("sold", true)
                }
            }
        }

    }

}
