package no.hiof.toyopoly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.hiof.toyopoly.R
import no.hiof.toyopoly.models.TokenModel

class AdapterToken (
    private val tokenList: ArrayList<TokenModel>,
    private val listener: (TokenModel) -> Unit,
) : RecyclerView.Adapter<AdapterToken.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.token_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(tokenList[position])
    }

    override fun getItemCount(): Int {
        return tokenList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val value: TextView = itemView.findViewById(R.id.NameToken)
        val price: TextView = itemView.findViewById(R.id.PriceToken)

        fun bindItems(token : TokenModel) = with(itemView){
            value.text = token.Name
            price.text = token.Price + " kr"
            setOnClickListener{ listener(token) }
        }
    }
}