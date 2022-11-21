package no.hiof.toyopoly.chat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import no.hiof.toyopoly.MainActivity
import no.hiof.toyopoly.R
import no.hiof.toyopoly.adapter.MessageAdapter
import no.hiof.toyopoly.models.MessageModel
import no.hiof.toyopoly.util.RandomId



class MessageDetailFragment : Fragment(), View.OnClickListener  {
    private val args : MessageDetailFragmentArgs by navArgs()
    private lateinit var messagesRecyclerView : RecyclerView
    private lateinit var messageList: ArrayList<MessageModel>
    private lateinit var messageAdapter: MessageAdapter
    private var messageType = 0
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    val TAG = "MESSAGE"
    private lateinit var otherUserId : String
    private var chatChannelId = ""
    private var CHANNELID = "notificationChannel"
    private val notificationId = 101
    private var myUserName : String = ""




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
        Log.v("otheruserinmessage",otherUserId)
        chatChannelId = args.chatChannelId

//        setChatChannelId()
        Log.v("OTHERUSER", otherUserId)

        val sendButton = view.findViewById<ImageButton>(R.id.sendMessageButton)
        sendButton?.setOnClickListener(this)

        messagesRecyclerView = view.findViewById(R.id.messagesRecyclerView)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this.activity)
        messageList = arrayListOf()
        if (messageList.size > 1){
            messagesRecyclerView.smoothScrollToPosition(messageList.size - 1)
        }
        messageAdapter = MessageAdapter(messageList, auth.currentUser?.uid.toString()){}
        messagesRecyclerView.adapter = messageAdapter

        getMessages()
        getOtherUserName()
        createNotificationChannel()
        myUserName()
    }


    private fun myUserName() {
        auth.currentUser?.let {
            db.collection("Users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                if (it.exists()){
                   this.myUserName = it.getString("firstName").toString()

                }
            }
        }
    }

     private fun getOtherUserName() {
        db.collection("Users").document(otherUserId).get().addOnSuccessListener {
            if (it.exists()){
                val otherUserName = it.getString("firstName")
                (activity as MainActivity).supportActionBar?.title = otherUserName
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNELID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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
        val sendMsgButton = view?.findViewById<ImageButton>(R.id.sendMessageButton)
        val message = messageInput?.text.toString()

        db.collection("ChatChannels").document(args.chatChannelId).get().addOnSuccessListener {
            if (it.exists()){
                var userIds : MutableList<String> = it.get("userIds") as MutableList<String>
                var index = userIds.indexOf(auth.currentUser?.uid)
                this.messageType = index + 1
            }
            val messageToSave = MessageModel(RandomId.randomID(),message, Timestamp.now(), auth.currentUser!!.uid, this.messageType)

            if (messageToSave.message.isNotEmpty()){
                Log.v("chatChannelId", chatChannelId)
                db.collection("ChatChannels").document(chatChannelId)
                    .collection("messages").document()
                    .set(messageToSave)
                    .addOnCompleteListener {
                        Log.v(TAG, "Messaged saved in chatChannels")
                        messageInput!!.text.clear()
                        messagesRecyclerView.smoothScrollToPosition(messageList.size - 1)



                        val builder = activity?.let { it ->
                            //navigering til message fragment
                            val pendingNotification = context?.let { it1 ->
                                NavDeepLinkBuilder(it1)
                                    .setComponentName(MainActivity::class.java)
                                    .setGraph(R.navigation.nav_graph)
                                    .setDestination(R.id.messageFragment)
                                    .createPendingIntent()
                            }

                            //sender melding
                            //problem brukerene som sender får notifikasjon og ikke bruker som mottar mld
                            NotificationCompat.Builder(it.application, CHANNELID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(this.myUserName)
                                .setContentText(messageToSave.message)
                                .setAutoCancel(true)
                                .setContentIntent(pendingNotification)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                        }

                        with(activity?.let { NotificationManagerCompat.from(it.application) }) {
                            if (builder != null) {
                                this?.notify(notificationId, builder.build())
                            }
                        }

                    }
                    .addOnFailureListener{
                        Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                            .show()
                    }
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
                            messageList.sortBy {
                                it.timestamp
                            }
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




