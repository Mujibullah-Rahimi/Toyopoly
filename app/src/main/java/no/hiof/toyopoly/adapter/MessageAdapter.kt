package no.hiof.toyopoly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.hiof.toyopoly.R
import no.hiof.toyopoly.model.MessageModel


class MessageAdapter(
    private val messageList: ArrayList<MessageModel>,
    private val listener : (MessageModel) -> Unit)
    : RecyclerView.Adapter<MessageAdapter.ChatViewHolder>() {

    // Called when there's a need for a new ViewHolder (a new item in the list/grid)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val messageView = LayoutInflater.from(parent.context).inflate(R.layout.message_item,parent,false)
        return ChatViewHolder(messageView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val message : TextView = view.findViewById(R.id.messagesRecyclerView)

        fun bind(item: MessageModel) = with(itemView){
            message.text = item.message
            setOnClickListener{listener(item)}
        }
    }
}