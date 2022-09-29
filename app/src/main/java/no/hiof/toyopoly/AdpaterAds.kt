package no.hiof.toyopoly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.FieldPosition

class AdpaterAds(private val adsList: ArrayList<Ads>) : RecyclerView.Adapter<AdpaterAds.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdpaterAds.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdpaterAds.MyViewHolder, position: Int) {
        val ads:Ads = adsList[position]
        holder.vare.text = ads.Vare
        holder.desc.text = ads.Desc
        holder.price.text = ads.Price.toString()
        holder.time.text = ads.TimeStamp.toString()
        holder.cat.text = ads.Category
    }

    override fun getItemCount(): Int {
        return adsList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val vare: TextView = itemView.findViewById(R.id.Vare)
        val desc: TextView = itemView.findViewById(R.id.Desc)
        val price: TextView = itemView.findViewById(R.id.Price)
        val time: TextView = itemView.findViewById(R.id.TimeStamp)
        val cat: TextView = itemView.findViewById(R.id.Cat)
    }

}