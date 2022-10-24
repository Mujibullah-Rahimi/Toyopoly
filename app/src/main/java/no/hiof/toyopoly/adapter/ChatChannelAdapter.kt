package no.hiof.toyopoly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.toyopoly.R
import no.hiof.toyopoly.models.ChatChannelModel

class ChatChannelAdapter(
    private val chatChannelList:ArrayList<ChatChannelModel>,
    private val listener : (ChatChannelModel) -> Unit)
    : RecyclerView.Adapter<ChatChannelAdapter.MyViewHolder>(){

    private var currentUser  = FirebaseAuth.getInstance().currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.engaged_chat, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(chatChannelList[position])
    }

    override fun getItemCount(): Int {
        return chatChannelList.size
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val inChatWith : TextView = itemView.findViewById(R.id.engagedChatWith)

        fun bindItems(chatChannel : ChatChannelModel) = with(itemView){
            val otherUser : String? = chatChannel.userIds.find { it != currentUser }
            val otherUserName = db.collection("Users").document(otherUser.toString())
                .get()
                .addOnSuccessListener {
                    val userName = it.getString("firstName")
                    inChatWith.text = userName
                }
            setOnClickListener{ listener(chatChannel) }
        }
    }
}