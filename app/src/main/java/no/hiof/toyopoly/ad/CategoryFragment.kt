package no.hiof.toyopoly.ad

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import no.hiof.toyopoly.R
import no.hiof.toyopoly.adapter.AdapterAds
import no.hiof.toyopoly.models.AdModel


class CategoryFragment : Fragment() {
    private val args: CategoryFragmentArgs by navArgs()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adsArrayList: ArrayList<AdModel>
    private lateinit var adapterAds: AdapterAds
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewForAllToys)

        recyclerView.layoutManager = LinearLayoutManager (this.activity)

        adsArrayList = arrayListOf()

        adapterAds = AdapterAds(adsArrayList){ad ->
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToAdDetailFragment(
                    ad.adId
                )
            val navController = view.findNavController()

            navController.navigate(action)
        }

        recyclerView.adapter = adapterAds

        println(args.category)

        getAds()
    }

    private fun getAds(){
        db = FirebaseFirestore.getInstance()
        db.collection("Ads")
            .whereEqualTo("category", args.category)
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
}
