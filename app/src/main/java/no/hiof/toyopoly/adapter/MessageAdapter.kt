package no.hiof.toyopoly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.hiof.toyopoly.R
import no.hiof.toyopoly.models.MessageModel
import java.text.DateFormat


class MessageAdapter(
    private val messageList: ArrayList<MessageModel>,
    private val listener : (MessageModel) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        var MY_MESSAGE_TYPE = 1
        var OTHER_MESSAGE_TYPE = 2
    }
    // Called when there's a need for a new ViewHolder (a new item in the list/grid)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MY_MESSAGE_TYPE) {
            myMessagesViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.my_message_item, parent, false)
            )
        } else otherMessagesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.other_message_item, parent, false)
        )
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (messageList[position].messageType === MY_MESSAGE_TYPE) {
            (holder as myMessagesViewHolder).bind(messageList[position])
        } else {
            (holder as otherMessagesViewHolder).bind(messageList[position])
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return messageList[position].messageType
    }

//    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        private val message : TextView = view.findViewById(R.id.message_message)
//        private val date : TextView = view.findViewById(R.id.message_date)
//        private val time : TextView = view.findViewById(R.id.message_timestamp)
//
//        fun bind(item: MessageModel) = with(itemView){
//            message.text = item.message
//            date.text = DateFormat.getDateInstance().format(item.timestamp.toDate())
//            time.text =  DateFormat.getTimeInstance().format(item.timestamp.toDate())
//
//            if (item.senderId == FirebaseAuth.getInstance().currentUser?.uid){
//
//            }
//            setOnClickListener{listener(item)}
//        }
//    }

    inner class myMessagesViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val message : TextView = view.findViewById(R.id.myMessage_message)
        private val date : TextView = view.findViewById(R.id.myMessage_date)
        private val time : TextView = view.findViewById(R.id.myMessage_timestamp)

        fun bind(item : MessageModel) = with(itemView){
            message.text = item.message
            date.text = DateFormat.getDateInstance().format(item.timestamp.toDate())
            time.text =  DateFormat.getTimeInstance().format(item.timestamp.toDate())
        }
    }

    inner class otherMessagesViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val message : TextView = view.findViewById(R.id.otherMessage_message)
        private val date : TextView = view.findViewById(R.id.otherMessage_date)
        private val time : TextView = view.findViewById(R.id.otherMessage_timestamp)

        fun bind(item : MessageModel) = with(itemView){
            message.text = item.message
            date.text = DateFormat.getDateInstance().format(item.timestamp.toDate())
            time.text =  DateFormat.getTimeInstance().format(item.timestamp.toDate())
        }
    }

}