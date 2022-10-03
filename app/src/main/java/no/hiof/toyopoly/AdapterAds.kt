package no.hiof.toyopoly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterAds(private val adsList: ArrayList<Ads>) : RecyclerView.Adapter<AdapterAds.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val ads: Ads = adsList[position]
        holder.vare.text = ads.value
        holder.desc.text = ads.description
        holder.price.text = ads.price
        holder.cat.text = ads.category
        holder.btnAd.text = ads.Id
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
        val btnAd: Button = itemView.findViewById(R.id.btnAd)
    }

}
