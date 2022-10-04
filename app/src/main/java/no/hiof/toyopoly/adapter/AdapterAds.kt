package no.hiof.toyopoly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.hiof.toyopoly.R
import no.hiof.toyopoly.model.AdModel

class AdapterAds(
    private val adsList: ArrayList<AdModel>,
    private val listener: (AdModel) -> Unit
) : RecyclerView.Adapter<AdapterAds.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(adsList[position])
        //val ad: AdModel = adsList[position]
    }

    override fun getItemCount(): Int {
        return adsList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val value: TextView = itemView.findViewById(R.id.ValueAd)
        val price: TextView = itemView.findViewById(R.id.PriceAd)

        fun bindItems(ad : AdModel) = with(itemView){
            value.text = ad.value
            price.text = ad.price + " kr"
            setOnClickListener{ listener(ad) }
        }
    }
}
