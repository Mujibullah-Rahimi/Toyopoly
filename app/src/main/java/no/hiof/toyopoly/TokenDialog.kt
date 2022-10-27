package no.hiof.toyopoly

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import no.hiof.toyopoly.adapter.AdapterToken
import no.hiof.toyopoly.models.AdModel
import no.hiof.toyopoly.models.TokenModel

class TokenDialog : DialogFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tokenArrayList: ArrayList<TokenModel>
    private lateinit var adapterToken: AdapterToken
    private val db = FirebaseFirestore.getInstance()
    val user = Firebase.auth.currentUser
    private val args: TokenDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.token_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewForTokens)
        recyclerView.layoutManager = LinearLayoutManager(this.activity)
        tokenArrayList = arrayListOf()

         adapterToken = AdapterToken(tokenArrayList){ token ->
             val builder = AlertDialog.Builder(this.activity)
             builder.setTitle(R.string.dialogTitle)
             builder.setMessage(R.string.dialogMessage)
             builder.setIcon(android.R.drawable.ic_dialog_alert)
             getTokenAmount()


             builder.setPositiveButton("Buy Token"){dialogInterface, which ->

                 UpdateToken()

                 Log.d(TAG, "clicked")

             }

             builder.setNegativeButton("Cancel"){dialogInterface, which ->

             }
             builder.show()
        }
        recyclerView.adapter = adapterToken
        getTokens()
    }
    var tokenValue : Long = 0



    private fun getTokenAmount() {
            val test = view?.findViewById<TextView>(R.id.token_ref)
            val test1 = test?.text.toString()
            Log.d(TAG, test1)
            db.collection("Tokens").document(test1)
                .get()
                .addOnSuccessListener { document ->
                    Log.d("currentToken", "Snapshot: ${document.data}")
                    tokenValue = document.getLong("Amount")!!
                }
    }

    private fun getTokens() {
            db.collection("Tokens")
                 .addSnapshotListener(object : EventListener<QuerySnapshot> {
                     override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                         if(error != null){
                             Log.e("Firestore ERROR", error.message.toString())
                             return
                         }
                         for ( dc : DocumentChange in value?.documentChanges!!) {
                             if (dc.type == DocumentChange.Type.ADDED){
                                 tokenArrayList.add(dc.document.toObject(TokenModel::class.java))
                             }
                         }
                         adapterToken.notifyDataSetChanged()
                     }
                 })
    }

    val userUID = user!!.uid

    private fun UpdateToken() {
       db.collection("Users").document(userUID).update("token",FieldValue.increment(tokenValue) )
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
