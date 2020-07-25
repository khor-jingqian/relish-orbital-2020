package com.example.relishorbital

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChefSignupFinal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chef_signup_final)
        val button = findViewById<Button>(R.id.chefsignupfinal_button)

        val customerDetails = intent.getSerializableExtra("customer") as Shop

        val storeName = findViewById<TextView>(R.id.chefsignupfinal_storename).setText(
            "Shop name: \n" +
            customerDetails.getShopName()
        )

        val emailAddress = findViewById<TextView>(R.id.chefsignupfinal_email).setText(
            "\nEmail Address: \n" +
            customerDetails.getEmail()
        )

        val postal = findViewById<TextView>(R.id.chefsignupfinal_postal).setText(
            "\nPostal code: \n" +
            customerDetails.getPostal().toString()
        )

        val shopDesc = findViewById<TextView>(R.id.chefsignupfinal_desc).setText(
            "\nShop description: \n" +
            customerDetails.getShopDesc()
        )
        val shopCuisine = findViewById<TextView>(R.id.chefsignupfinal_cuisine).setText(
            "\nShop Cuisine: \n" +
                    customerDetails.getShopCuisine()
        )



        val dishImage = findViewById<ImageView>(R.id.chefsignupfinal_pic)
        var bundle:Uri? = null
        try {
            Log.d("did we man", "yes we did")
            bundle = intent.extras?.get("dishImages") as? Uri
            if (bundle != null) {
                Log.d("final", "did we manage to hit this?")
                dishImage.setImageURI(null)
                dishImage.setImageURI(intent.extras?.get("dishImages") as Uri)
                customerDetails.dishImagePath = bundle.toString()
            } else {
                Toast.makeText(this,"Take note: No picture was taken",Toast.LENGTH_LONG).show()
            }
        } catch (e: TypeCastException) {
            Toast.makeText(this,"Take note: No picture was taken",Toast.LENGTH_LONG).show()
        }


        button.setOnClickListener(View.OnClickListener {
            openMainScreen(customerDetails)
        })
    }

    fun openMainScreen(shop:Shop) {
        val intent = Intent(this,MainScreen::class.java)
        val sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPrefs.getString("shops",null)
        val type = object : TypeToken<ArrayList<Shop>>() {}.type
        val shopArray = gson.fromJson(json,type) as ArrayList<Shop>
        shopArray.add(shop)
        val editor = sharedPrefs.edit()
        val json2 = gson.toJson(shopArray)
        editor.putString("shops",json2)
        editor.apply()
        startActivity(intent)
    }
}
