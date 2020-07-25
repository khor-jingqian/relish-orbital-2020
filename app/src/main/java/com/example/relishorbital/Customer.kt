package com.example.relishorbital

import android.widget.ImageView
import java.io.Serializable
import kotlin.random.Random

class Customer : Serializable {
    private var userName: String
    private var emailAddress: String
    private var preferences: String
    private var password: String
    private var phoneNumber: Long
    private var identity: String?
    var pastOrders = ArrayList<String>()
    constructor(un: String, pw: String, ea: String, pn: Long, pref:String, id: String?) {
        userName = un
        emailAddress = ea
        preferences = pref
        password = pw
        phoneNumber = pn
        identity = id
    }

    fun getEmail(): String {
        return emailAddress
    }
    fun getPhone(): Long {
        return phoneNumber
    }

    fun getUserName(): String {
        return userName
    }
    fun getPreferences(): String {
        return preferences
    }
    fun getPassword(): String {
        return password
    }

    fun getID(): String {
        return identity ?: Random.nextInt().toString()
    }

    fun setPastOrders(orders:String) {
        pastOrders.add(orders)
    }

    fun getOrders():ArrayList<String> {
        return pastOrders
    }
}