package no.hiof.toyopoly

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import no.hiof.toyopoly.adapter.AdapterAds
import no.hiof.toyopoly.models.AdModel
import androidx.fragment.app.FragmentManager


class MyPageFragment : Fragment(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var adsArrayList: ArrayList<AdModel>
    private lateinit var adapterAds: AdapterAds
    private val db = FirebaseFirestore.getInstance()
    val user = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUser()

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

        val docRef = db.collection("Users").document(userUID)
        docRef
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("currentUser", "Snapshot: ${document.data}")
                    getEmail?.text = document.getString("email")
                    getName?.text = document.getString("firstName")+ " " + document.getString("lastName")
                    getTokens?.text = "Amount of tokens: " + document.getLong("token").toString()
                    getAddress?.text = document.getString("address")
                    //time_ad?.text = document.getDate("timestamp").toString()
                } else {
                    Log.d("isNotHere", "The document snapshot doesn't exist")
                }
            }
            .addOnFailureListener { e -> Log.d("Error", "Fail at: ", e) }
    }

    fun deleteAd(documentId: String) {

        db.collection("Ads").document(documentId)
            .delete()
            .addOnFailureListener { e -> Log.d("Error", "Fail at: ", e) }
    }
}