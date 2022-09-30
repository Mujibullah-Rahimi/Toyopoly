package no.hiof.toyopoly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterAds(private val adsList: ArrayList<Ads>) : RecyclerView.Adapter<AdapterAds.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val ads:Ads = adsList[position]
        holder.vare.text = ads.Vare
        holder.desc.text = ads.Desc
        holder.price.text = ads.Price
        holder.cat.text = ads.Category
    }

    override fun getItemCount(): Int {
        return adsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val vare: TextView = itemView.findViewById(R.id.VareAd)
        val desc: TextView = itemView.findViewById(R.id.DescAd)
        val price: TextView = itemView.findViewById(R.id.PriceAd)
        val time: TextView = itemView.findViewById(R.id.TimeStampAd)
        val cat: TextView = itemView.findViewById(R.id.CatAd)
    }

}