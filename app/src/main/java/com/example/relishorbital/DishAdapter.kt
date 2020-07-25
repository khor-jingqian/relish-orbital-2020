package com.example.relishorbital

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class DishAdapter(context: Context, dishArray: ArrayList<Dishes>, onitemlistener: onItemListener) : RecyclerView.Adapter<DishAdapter.DishViewHolder>() {
    val mDishArray = dishArray
    val mOnItemListener = onitemlistener
    val con = context


    class DishViewHolder(itemView: View, onitemlistener: onItemListener ) : RecyclerView.ViewHolder(itemView) ,
        View.OnClickListener {
//        val mShopLogo = itemView.findViewById<ImageView>(R.id.shop_logo)
//        val mShopName = itemView.findViewById<TextView>(R.id.shop_name)
//        val mShopCuisine = itemView.findViewById<TextView>(R.id.shop_cuisine)
//        val mShopDish1 = itemView.findViewById<ImageView>(R.id.shop_dish1)
//        val mShopRatings = itemView.findViewById<TextView>(R.id.shop_ratings)
        val mDishName = itemView.findViewById<TextView>(R.id.dish_name)
        val mDishPrice = itemView.findViewById<TextView>(R.id.dish_price)
        val mDishImage = itemView.findViewById<ImageView>(R.id.dish_image)
        val oil = onitemlistener

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            oil.onItemClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dish_details,parent,false)
        return DishViewHolder(view, mOnItemListener)
    }

    override fun getItemCount(): Int {
        //How many items are we expecting
        return mDishArray.size
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        //val position refers to which item we are currently looking at

        // grab the current item from the arraylist
        val currentItem = mDishArray.get(position)

        //set upcoming card logo as the new logo from currentItem
        //.getShopLogo() is a getter methods from Shop class
//        holder.mShopLogo.setImageResource(currentItem.getShopLogo())
//
//        //do the same for the textview
//        holder.mShopName.setText(currentItem.getShopName())
//        holder.mShopCuisine.setText(currentItem.getShopCuisine())
//        holder.mShopDish1.setImageResource(currentItem.getStarLogo())
//        holder.mShopRatings.setText(currentItem.getRatings().toString())

        holder.mDishName.setText(currentItem.dishName)
        holder.mDishPrice.setText('$' + String.format("%.2f",currentItem.p))
        Picasso.with(con).load("http://relish.dyndns-remote.com/RelishBackend/" + currentItem.imageURL)
            .into(holder.mDishImage)

//        holder.mDishImage.setImageBitmap(
//            RetrieveImage()
//                .execute("http://relish.dyndns-remote.com/RelishBackend/" + currentItem.imageURL))
    }

    public interface onItemListener {
        fun onItemClick(position: Int)
    }
}