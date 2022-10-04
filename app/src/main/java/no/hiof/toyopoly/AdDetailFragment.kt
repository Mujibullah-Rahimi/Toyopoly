package no.hiof.toyopoly

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import no.hiof.toyopoly.model.AdModel
import android.widget.TextView
import androidx.navigation.fragment.navArgs

class AdDetailFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var docSnap : DocumentSnapshot
    private val args: AdDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ad_detail, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val docRef = db.collection("Ads").document(args.adId!!)
        docRef.get().addOnCompleteListener { task->
            if (task.isSuccessful){
                Log.v("AD_DETAIL", task.result.toString())
                docSnap = task.result
            }
            else{
                Log.v("AD_DETAIL", task.exception.toString())
            }
        }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val ad = docSnap.toObject(AdModel::class.java)
//        val value = view.findViewById<EditText>(R.id.adDetailTitle)
//        value.setText(ad?.value)
//
//    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryName = view.findViewById<TextView>(R.id.adDetailTitle)
        //categoryName.text = args.ad
    }
}