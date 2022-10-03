package no.hiof.toyopoly

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import no.hiof.toyopoly.adapter.ChatListAdapter
import no.hiof.toyopoly.model.MessageModel

class MessageFragment : Fragment(), View.OnClickListener {
    private lateinit var messageList : ArrayList<MessageModel>
    private lateinit var chatListAdapter : ChatListAdapter
    private lateinit var auth:FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    val TAG = "MESSAGE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//      EventChangeListener()
    }

//    private fun EventChangeListener(){
//        db.collection("Messages")
//            .addSnapshotListener(object : EventListener<QuerySnapshot> {
//                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//                    if (error != null){
//                        Log.v("Firestore Error", error.message.toString())
//                        return
//                    }else{
//                        for (doc : DocumentChange in value?.documentChanges!!){
//                            if (doc.type == DocumentChange.Type.ADDED){
//                                messageList.add(doc.document.toObject(MessageModel::class.java))
//                            }
//                        }
//                        chatListAdapter.notifyDataSetChanged()
//                    }
//                }
//            })
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val sendButton = view.findViewById<Button>(R.id.sendMessageButton)
        sendButton?.setOnClickListener(this)


//        val messageRecyclerView = view.findViewById<RecyclerView>(R.id.chatRecyclerView)
//
//        messageRecyclerView.adapter = ChatListAdapter(messageList)
    }

    fun saveMsg(){
        val messageInput = view?.findViewById<EditText>(R.id.editText_message)
        val sendMsgButton = view?.findViewById<Button>(R.id.sendMessageButton)
        val message = messageInput?.text.toString()

        val messageToSave = MessageModel(message, Timestamp.now(), auth.currentUser!!.uid)

        if (messageToSave.message.isNotEmpty()){
            db.collection("Messages").document()
                .set(messageToSave)
                .addOnCompleteListener { Log.v(TAG, "Messaged saved")
                messageInput!!.text.clear()}
                .addOnFailureListener{
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                        .show()
                }
        }
    }
    override fun onClick(view: View?) {
        saveMsg()
    }
}