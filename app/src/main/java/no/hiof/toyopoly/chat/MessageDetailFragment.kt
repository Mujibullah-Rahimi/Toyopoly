package no.hiof.toyopoly.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import no.hiof.toyopoly.R
import no.hiof.toyopoly.adapter.MessageAdapter
import no.hiof.toyopoly.models.MessageModel
import no.hiof.toyopoly.util.RandomId


class MessageDetailFragment : Fragment(), View.OnClickListener  {
    private val args : MessageDetailFragmentArgs by navArgs()
    private lateinit var messagesRecyclerView : RecyclerView
    private lateinit var messageList: ArrayList<MessageModel>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    val TAG = "MESSAGE"
    private lateinit var otherUserId : String
    private var chatChannelId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        otherUserId = args.otherUser
        chatChannelId = args.chatChannelId
//        setChatChannelId()
        Log.v("OTHERUSER", otherUserId)

        val sendButton = view.findViewById<Button>(R.id.sendMessageButton)
        sendButton?.setOnClickListener(this)

        messagesRecyclerView = view.findViewById(R.id.messagesRecyclerView)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this.activity)
        messageList = arrayListOf()

        messageAdapter = MessageAdapter(messageList){}
        messagesRecyclerView.adapter = messageAdapter

        getMessages()
    }

//    private fun setChatChannelId(){
//        db.collection("Users").document(auth.currentUser!!.uid)
//            .collection("engagedChats").document(otherUserId)
//            .get()
//            .addOnSuccessListener {
//                this.chatChannelId = it.getString("chatChannelId").toString()
//                Log.v("chatChannelId", it.getString("chatChannelId").toString())
//            }
//            .addOnFailureListener{
//                Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
//                    .show()
//            }
//    }

    fun saveMsg(){
        val messageInput = view?.findViewById<EditText>(R.id.editText_message)
        val sendMsgButton = view?.findViewById<Button>(R.id.sendMessageButton)
        val message = messageInput?.text.toString()

        val messageToSave = MessageModel(RandomId.randomID(),message, Timestamp.now(), auth.currentUser!!.uid)

        if (messageToSave.message.isNotEmpty()){
            Log.v("chatChannelId", chatChannelId)
            db.collection("ChatChannels").document(chatChannelId)
                .collection("messages").document()
                .set(messageToSave)
                .addOnCompleteListener {
                    Log.v(TAG, "Messaged saved in chatChannels")
                    messageInput!!.text.clear()}
                .addOnFailureListener{
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                        .show()
                }
        }
    }
    fun getMessages(){
        Log.v("GETMESSAGES", "get messages has been called")
//        Users/FaTMSJiGPGQVKgHtFl34w3lk3pJ3/engagedChats/2FBGPPtWDrQVENG3A7t50XMz9Yk2/messages
        db.collection("ChatChannels/${chatChannelId}/messages")
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
        saveMsg()
    }
}