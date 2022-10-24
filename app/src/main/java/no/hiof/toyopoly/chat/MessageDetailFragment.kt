package no.hiof.toyopoly.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import no.hiof.toyopoly.R
import no.hiof.toyopoly.adapter.MessageAdapter
import no.hiof.toyopoly.models.MessageModel


class MessageDetailFragment : Fragment(), View.OnClickListener  {
    private val otherUserId : MessageDetailFragmentArgs by navArgs()
    private lateinit var messagesRecyclerView : RecyclerView
    private lateinit var messageList: ArrayList<MessageModel>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    val TAG = "MESSAGE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_channel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

//        val sendButton = view.findViewById<Button>(R.id.sendMessageButton)
//        sendButton?.setOnClickListener(this)

        messagesRecyclerView = view.findViewById(R.id.chatChannelsRecylcerView)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this.activity)
        messageList = arrayListOf()

        messageAdapter = MessageAdapter(messageList){}
        messagesRecyclerView.adapter = messageAdapter

        getMessages()
    }

//    fun saveMsg(){
//        val messageInput = view?.findViewById<EditText>(R.id.editText_message)
//        val sendMsgButton = view?.findViewById<Button>(R.id.sendMessageButton)
//        val message = messageInput?.text.toString()
//
//        val messageToSave = MessageModel(RandomId.randomID(),message, Timestamp.now(), auth.currentUser!!.uid)
//
//        if (messageToSave.message.isNotEmpty()){
//            db.collection("Messages").document()
//                .set(messageToSave)
//                .addOnCompleteListener { Log.v(TAG, "Messaged saved")
//                    messageInput!!.text.clear()}
//                .addOnFailureListener{
//                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
//                        .show()
//                }
//        }
//    }
    fun getMessages(){
    Log.v("GETMESSAGES", "get messages has been called")
//        Users/FaTMSJiGPGQVKgHtFl34w3lk3pJ3/engagedChats/2FBGPPtWDrQVENG3A7t50XMz9Yk2/messages
        db.collection("Users/${auth.currentUser!!.uid}/engagedChats/${otherUserId}/messages")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null){
                        Log.e("Firestore ERROR", error.message.toString())
                        return
                    }
                    for ( dc : DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED){
                            messageList.add(dc.document.toObject(MessageModel::class.java))
                            Log.v("GETMESSAGES", messageList.lastIndex.toString())
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                }
            })
    }

    override fun onClick(view: View?) {
//        saveMsg()
    }
}