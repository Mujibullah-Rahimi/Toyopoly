package no.hiof.toyopoly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import no.hiof.toyopoly.R
import no.hiof.toyopoly.model.MessageModel


class ChatListAdapter(private val messageList: ArrayList<MessageModel>) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {
    private lateinit var db : FirebaseFirestore
    private lateinit var chatListAdapter: ChatListAdapter
    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Gets a reference to all the specific views we are going to use or fill with data
        private val message : TextView = view.findViewById(R.id.chatRecyclerView)
        fun bind(item: MessageModel){
            // Fills the views with the given data
            message.text = item.message
            // Sets the onClickListener
            //itemView.setOnClickListener(clickListener)
        }
    }

    // Called when there's a need for a new ViewHolder (a new item in the list/grid)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val messageView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_message_item,parent,false)
        // Create the viewHolder with the corresponding view (list item)
        return ChatViewHolder(messageView)
    }
    // Called when data is bound to a specific ViewHolder (and item in the list/grid)
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        // Gets the movie data we are going to use at the given position
        val currentMessage : MessageModel =  messageList[position]
        holder.bind(currentMessage)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}