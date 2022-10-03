package no.hiof.toyopoly

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


class CategoryFragment : Fragment(), View.OnClickListener {
    private val args: CategoryFragmentArgs by navArgs()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adsArrayList: ArrayList<Ads>
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

        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager (this.activity)

        adsArrayList = arrayListOf()

        adapterAds = AdapterAds(adsArrayList)

        recyclerView.adapter = adapterAds


        getAds()
    }

    override fun onClick(v: View?) {
        val navController = v?.findNavController()
        val adBtn = v?.findViewById<Button>(R.id.btnAd)

        //val AdAction = CategoryFragmentDirections.actionCategoryFragmentToAdDetailFragment()

        when (v?.id){
            R.id.btnAd -> {
                //AdAction.ad = adBtn?.text.toString()
                //navController?.navigate(AdAction)
                Log.v("AD_CLICKED", R.id.btnAd.toString())
            }
        }
    }

    fun getAds(){


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
                            adsArrayList.add(dc.document.toObject(Ads::class.java))
                        }
                    }
                    adapterAds.notifyDataSetChanged()
                }

            })
    }

    }
