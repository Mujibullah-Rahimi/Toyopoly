package no.hiof.toyopoly

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class CategoryFragment : Fragment() {
    private val args: CategoryFragmentArgs by navArgs()
    private lateinit var adsArrayList: ArrayList<Ads>
    private lateinit var adpaterAds: AdpaterAds
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        view.recyclerView.layoutManager = LinearLayoutManager(activity)
        view.recyclerView.adapter = adapterAds()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAds()
    }

    fun getAds(){
        db = FirebaseFirestore.getInstance()
        db.collection("ads")
            .whereEqualTo("Category", "Toy-car")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null){
                        Log.e("Firestore ERROR", error.message.toString())
                        return
                    }
                    for ( dc : DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED){
                            adsArrayList.add(dc.document.toObject(Ads::class.java))
                        }
                    }
                    adpaterAds.notifyDataSetChanged()
                }

            })
    }

    }