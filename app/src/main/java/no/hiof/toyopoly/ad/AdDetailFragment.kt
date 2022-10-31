package no.hiof.toyopoly.ad

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.common.io.Files.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import no.hiof.toyopoly.model.ChatChannelModel
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
        val contactSellerButton = view.findViewById<Button>(R.id.contactSellerButton)

        getUser()
        getAds()
        getOtherUserId()

        contactSellerButton.setOnClickListener{

            Log.v("OtherUserInClick", otherUser)
//            FirestoreUtil.getOrCreateChatChannel(otherUser)
            createChatChannel(otherUser, RandomId.randomID())
        }

    }

    fun getAds(){
        val title_ad = view?.findViewById<TextView>(R.id.adDetailTitle)
        val price_ad = view?.findViewById<TextView>(R.id.adDetailPrice)
        val desc_ad = view?.findViewById<TextView>(R.id.adDetailDescription)
        val addr_ad = view?.findViewById<TextView>(R.id.adDetailAddress)
        val token_ad = view?.findViewById<TextView>(R.id.tokenPrice)
        //val time_ad = view?.findViewById<TextView>(R.id.adDetailTimeStamp)

        val docRef = db.collection("Ads").document(args.adId!!)
        docRef
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("isHere", "Snapshot: ${document.data}")
                    title_ad?.text = document.getString("value")
                    price_ad?.text = "Adjusted Money price: " + document.getString("price") + " kr"
                    desc_ad?.text = document.getString("description")
                    addr_ad?.text = document.getString("address")
                    token_ad?.text = "Token price: " + document.getLong("token").toString() + " Tokens"
                    //time_ad?.text = document.getDate("timestamp").toString()
                }
                else{
                    Log.d("isNotHere", "The document snapshot doesn't exist")
                }
            }
            .addOnFailureListener{e -> Log.d("Error", "Fail at: ", e) }

        addr_ad?.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("geo:0,0?q=" + addr_ad.text)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)

        }
    }

    private fun createChatChannel(otherUser : String, randomId : String) {
        val userIds : MutableList<String> = mutableListOf(currentUser,otherUser)
        val chatChannelToSave = ChatChannelModel(randomId,userIds)

        // Creating a collection in currentUsers document called engagedChats
        // engagedChats contains a document for each chat user is engaged in
        // the name/id of each document is the uid of the other user in the chat
        db.collection("Users").document(currentUser)
            .collection("engagedChats")
            .document(otherUser)
            .set(chatChannelToSave)

        db.collection("Users").document(otherUser)
            .collection("engagedChats")
            .document(currentUser)
            .set(chatChannelToSave)

        db.collection("ChatChannels").whereArrayContains("UserIds",chatChannelToSave)
            .get()
            .addOnSuccessListener {
                Log.v("CHATCHANNEL", it.size().toString()) }

        db.collection("ChatChannels").document(chatChannelToSave.chatChannelId)
            .set(chatChannelToSave)
            .addOnSuccessListener {
                Log.v("userIds", userIds.toString() )
            }.addOnCompleteListener{
                navController.navigate(
                    AdDetailFragmentDirections.actionAdDetailFragmentToMessageDetailFragment(
                        otherUser, randomId
                    )
                )
            }.addOnFailureListener{
                Log.v("userIds", userIds.toString())
//                Toast.makeText(activity, it.message , Toast.LENGTH_LONG)
//                    .show()
            }
//        db.collection("ChatChannels").document(randomId)
//            .collection("Messages").document().set(HashMap<String, Any>())
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
}
