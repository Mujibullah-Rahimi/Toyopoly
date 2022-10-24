package no.hiof.toyopoly.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import no.hiof.toyopoly.R
import no.hiof.toyopoly.adapter.ChatChannelAdapter
import no.hiof.toyopoly.models.ChatChannelModel


class ChatChannelFragment : Fragment(), View.OnClickListener {
    private lateinit var chatChannelsRecyclerView: RecyclerView
    private lateinit var chatChannelsArrayList : ArrayList<ChatChannelModel>
    private lateinit var chatChannelAdapter : ChatChannelAdapter
    private lateinit var auth:FirebaseAuth
    private lateinit var db: FirebaseFirestore

    val TAG = "MESSAGE"

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

        chatChannelsRecyclerView = view.findViewById(R.id.chatChannelsRecylcerView)
        chatChannelsRecyclerView.layoutManager = LinearLayoutManager(this.activity)
        chatChannelsArrayList = arrayListOf()

        chatChannelAdapter = ChatChannelAdapter(chatChannelsArrayList){chatChannel ->
            Log.v("userIds", chatChannel.userIds.toString())
            val otherUser = chatChannel.userIds[0]
            val chatChannelId = chatChannel.chatChannelId

            val action =
                ChatChannelFragmentDirections.actionMessageFragmentToMessageDetailFragment(
                    otherUser,chatChannelId
                )
            val navController = view.findNavController()
            Log.v("OtherUser", otherUser)
            navController.navigate(action)
        }
        chatChannelsRecyclerView.adapter = chatChannelAdapter
        db = FirebaseFirestore.getInstance()
        getMyChatChannels()
    }

    fun getMyChatChannels(){
        db.collection("Users").document(auth.currentUser!!.uid).collection("engagedChats")
//        whereArrayContains(auth.currentUser!!.uid)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null){
                        Log.e("Firestore ERROR", error.message.toString())
                        return
                    }
                    for ( dc : DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED){
                            chatChannelsArrayList.add(dc.document.toObject(ChatChannelModel::class.java))
                        }
                    }
                    chatChannelAdapter.notifyDataSetChanged()
                }
            })
    }

    override fun onClick(view: View?) {

    }
}