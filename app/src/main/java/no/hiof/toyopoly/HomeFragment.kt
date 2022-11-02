package no.hiof.toyopoly

import android.content.Intent
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import no.hiof.toyopoly.adapter.AdapterAds
import no.hiof.toyopoly.models.AdModel
import kotlin.text.replaceFirstChar as replaceFirstChar1


class HomeFragment : Fragment(), View.OnClickListener{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adsArrayList: ArrayList<AdModel>
    private lateinit var adapterAds: AdapterAds
    private var db = FirebaseFirestore.getInstance()
    val user = Firebase.auth.currentUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.home_ads_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager (this.activity)

        adsArrayList = arrayListOf()

        adapterAds = AdapterAds(adsArrayList){ad ->
            val action = HomeFragmentDirections.actionHomeFragmentToAdDetailFragment(ad.adId)
            val navController = view.findNavController()
            navController.navigate(action)

        }

        recyclerView.adapter = adapterAds


        val searchbtn = view.findViewById<ImageButton>(R.id.searchButton)
        searchbtn.setOnClickListener{
                adsArrayList.clear()
                getSearchResult()
                getSearchResultCat()
        }

        getRecycleAds()

    }

    private fun getRecycleAds(){
        db = FirebaseFirestore.getInstance()
        db.collection("Ads")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
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

    fun getAds(){
        db.collection("Ads")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore ERROR", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            adsArrayList.add(dc.document.toObject(AdModel::class.java))
                        }
                    }
                    adapterAds.notifyDataSetChanged()
                }
            })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getSearchResult() {
        val searchBox = view?.findViewById<EditText>(R.id.searchBar)
        val getSearch = searchBox?.text.toString().replaceFirstChar1(Char::titlecase)
        //Log.d(TAG, docID)
            db.collection("Ads").whereGreaterThanOrEqualTo("title", getSearch)
                .whereLessThanOrEqualTo("title", "$getSearch\uF7FF")
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null) {
                            Log.e("Firestore ERROR", error.message.toString())
                            return
                        }
                        for (dc: DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                adsArrayList.add(dc.document.toObject(AdModel::class.java))
                            }
                        }
                        adapterAds.notifyDataSetChanged()
                    }
                })

    }

    fun getSearchResultCat(){
        val searchBox = view?.findViewById<EditText>(R.id.searchBar)
        val getSearch = searchBox?.text.toString().replaceFirstChar1(Char::titlecase)
        db.collection("Ads").whereGreaterThanOrEqualTo("category", getSearch)
            .whereLessThanOrEqualTo("category", "$getSearch\uF7FF")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore ERROR", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            adsArrayList.add(dc.document.toObject(AdModel::class.java))
                        }
                    }
                    adapterAds.notifyDataSetChanged()
                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id){
//            R.id.messageActivityBtn -> {
//                val intent = Intent(activity, MessageActivity::class.java)
//                startActivity(intent)
//            }
        }
    }


}