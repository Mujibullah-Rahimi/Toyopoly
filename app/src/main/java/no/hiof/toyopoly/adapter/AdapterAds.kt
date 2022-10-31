package no.hiof.toyopoly.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import no.hiof.toyopoly.R
import no.hiof.toyopoly.models.AdModel

class AdapterAds(
    private val adsList: ArrayList<AdModel>,
    private val listener: (AdModel) -> Unit,
) : RecyclerView.Adapter<AdapterAds.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ad_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(adsList[position])
    }

    override fun getItemCount(): Int {
        return adsList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.ValueAd)
        val price: TextView = itemView.findViewById(R.id.PriceAd)
        val adImage: ImageView = itemView.findViewById(R.id.adPresentationImage)
//        val userImage : ImageView = itemView.findViewById(R.id.profilePicImageView)


        fun bindItems(ad : AdModel) = with(itemView){
            if (ad.imageUri.isNotEmpty()){
                val adImageReference = Firebase.storage.getReference(ad.imageUri)
//                val userImageReference = Firebase.storage.getReference(FirebaseAuth.getInstance().currentUser!!.uid)

                Glide.with(this)
                    .load(adImageReference)
                    .into(adImage)

//                Glide.with(this)
//                    .load(userImageReference)
//                    .into(userImage)
            }
            Log.v("PHOTO", ad.imageUri)
            title.text = ad.title
            price.text = ad.price + " kr"
            setOnClickListener{ listener(ad) }
        }
    }
}
