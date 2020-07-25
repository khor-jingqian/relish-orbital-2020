package com.example.relishorbital

class Dishes(val dishName: String, val description:String, val p: Float, val imageURL: String, val id: String) {

    private fun getName():String {
        return dishName
    }

    private fun getDesc(): String {
        return description
    }

    private fun getPrice(): Float {
        return p
    }

    public fun getImageUrl(): String {
        val url = imageURL.removeRange(10,11)
        return url
    }

    fun getID(): String {
        return id
    }
}