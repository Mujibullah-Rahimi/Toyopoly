package no.hiof.toyopoly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.hiof.toyopoly.R
import no.hiof.toyopoly.model.AdModel
import no.hiof.toyopoly.model.CategoryModel

class AdapterCat(
    private val catList: ArrayList<CategoryModel>,
    private val listener: (CategoryModel) -> Unit
) : RecyclerView.Adapter<AdapterCat.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cat_list, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(catList[position])
    }

    override fun getItemCount(): Int {
        return catList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val category: TextView = itemView.findViewById(R.id.CategoryAd)

        fun bindItems(cat : CategoryModel) = with(itemView){
            category.text = cat.category
            setOnClickListener{ listener(cat) }
        }
    }
}