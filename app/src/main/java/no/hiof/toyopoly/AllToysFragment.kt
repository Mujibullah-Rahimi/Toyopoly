package no.hiof.toyopoly

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import no.hiof.toyopoly.adapter.AdapterAds
import no.hiof.toyopoly.adapter.AdapterCat
import no.hiof.toyopoly.model.AdModel
import no.hiof.toyopoly.model.CategoryModel

class AllToysFragment : Fragment() {
    private val args: CategoryFragmentArgs by navArgs()
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var catArrayList : ArrayList<CategoryModel>
    private lateinit var adsArrayList: ArrayList<AdModel>
    private lateinit var adapterAds: AdapterAds
    private lateinit var adapterCat: AdapterCat
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_toys, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewForAllToys)
        recyclerView2 = view.findViewById(R.id.recyclerViewForAllCategories)
        recyclerView.layoutManager = LinearLayoutManager (this.activity)
        recyclerView2.layoutManager = LinearLayoutManager (this.activity, LinearLayoutManager.HORIZONTAL, false)

        adsArrayList = arrayListOf()
        catArrayList = arrayListOf()

        adapterAds = AdapterAds(adsArrayList){ad ->
            val action = AllToysFragmentDirections.actionAllToysFragmentToAdDetailFragment(ad.adId)

            val navController = view.findNavController()

            navController.navigate(action)
        }

        recyclerView.adapter = adapterAds

        adapterCat = AdapterCat(catArrayList){cat ->
            val action = AllToysFragmentDirections.actionAllToysFragmentToCategoryFragment(cat.category)

            val navController = view.findNavController()

            navController.navigate(action)
        }
        recyclerView2.adapter = adapterCat

        db = FirebaseFirestore.getInstance()

        getCategories()

        getAds()
    }

    fun getAds(){
        db.collection("Ads")
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
    fun getCategories(){
        db.collection("Category")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null){
                        Log.e("Firestore ERROR", error.message.toString())
                        return
                    }
                    for ( dc : DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED){
                            catArrayList.add(dc.document.toObject(CategoryModel::class.java))
                        }
                    }
                    adapterCat.notifyDataSetChanged()
                }
            })
    }
}