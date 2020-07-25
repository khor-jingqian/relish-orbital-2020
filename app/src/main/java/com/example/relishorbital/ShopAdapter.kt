package com.example.relishorbital

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ShopAdapter(con: Context, shopArray: ArrayList<Shop>, onitemlistener: onItemListener) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {
    val mShopArray = shopArray
    val mOnItemListener = onitemlistener
    val cont = con


    class ShopViewHolder(itemView: View, onitemlistener: onItemListener ) : RecyclerView.ViewHolder(itemView) ,
    View.OnClickListener {
        val mShopLogo = itemView.findViewById<ImageView>(R.id.shop_logo)
        val mShopName = itemView.findViewById<TextView>(R.id.shop_name)
        val mShopCuisine = itemView.findViewById<TextView>(R.id.shop_cuisine)
        val mShopDish1 = itemView.findViewById<ImageView>(R.id.shop_dish1)
        val mShopRatings = itemView.findViewById<TextView>(R.id.shop_ratings)
        val mShopImage = itemView.findViewById<ImageView>(R.id.shop_logo)
        val oil = onitemlistener

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            oil.onItemClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.customers_details,parent,false)
        return ShopViewHolder(view, mOnItemListener)
    }

    override fun getItemCount(): Int {
        //How many items are we expecting
        return mShopArray.size
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        //val position refers to which item we are currently looking at

        // grab the current item from the arraylist
        val currentItem = mShopArray.get(position)

        //set upcoming card logo as the new logo from currentItem
        //.getShopLogo() is a getter methods from Shop class
        holder.mShopLogo.setImageResource(currentItem.getShopLogo())

        //do the same for the textview
        holder.mShopName.setText(currentItem.getShopName())
        holder.mShopCuisine.setText(currentItem.getShopCuisine())
        holder.mShopDish1.setImageResource(currentItem.getStarLogo())
        holder.mShopRatings.setText(currentItem.getRatings().toString())

        Picasso.with(cont).isLoggingEnabled = true
        Picasso.with(cont).load("http://relish.dyndns-remote.com/RelishBackend/" + currentItem.getImageURL())
            .resize(1000,1000).onlyScaleDown().centerCrop().into(holder.mShopImage)
    }

    public interface onItemListener {
        fun onItemClick(position: Int)
    }
}