package com.example.relishorbital

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.net.URI
import java.util.*
import kotlin.random.asKotlinRandom

class Shop(
    val shop_name: String, val shop_email: String, val shop_postal: Long, val shop_desc: String,
    val shop_cuisine: String, val pw: String, val pn: String, val add: String, val id: String?,
    val profile:String?) : Serializable {

    private var shopEmail = shop_email
    var dishImagePath: String? = null
    var shop_logo = R.mipmap.ic_launcher_round
    var shop_ratings = 0.0
    private var shopDesc = shop_desc
    private var ID = id ?: Random().asKotlinRandom().nextInt().toString()
    private var phoneNumber = pn
    private var address = add

//    private var password = pw

    fun getShopName(): String {
        return shop_name
    }

    fun getShopLogo(): Int {
        return shop_logo
    }

    fun getShopCuisine(): String {
        return shop_cuisine
    }

    fun getStarLogo(): Int {
        return R.mipmap.ratings_icon_round
    }

    fun getRatings(): Double {
        return shop_ratings
    }

    fun setUri(uri:String?) {
        dishImagePath = uri
    }

    fun getEmail(): String {
        return shopEmail
    }

    fun getPostal(): Long {
        return shop_postal
    }

    fun getShopDesc(): String {
        return shopDesc
    }

    fun getPassword(): String {
        return pw
    }

    fun getID(): String {
        return ID
    }

    fun getPhone():String {
        return phoneNumber
    }

    fun getAddress(): String {
        return address
    }

    fun getImageURL():String {
        return profile!!
    }

}