package com.example.relishorbital

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import org.w3c.dom.Text
import java.time.Duration

class ChefLogin : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var editor: SharedPreferences.Editor
    lateinit var sharedPrefs: SharedPreferences
    lateinit var shopArray: ArrayList<Shop>
    lateinit var mQueue: RequestQueue
    lateinit var userEmail: EditText
    lateinit var userPassword: EditText

    private val dummyPasswords = arrayListOf<String>("password123")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chef_login)

        //Member variables initialization

        mQueue = Volley.newRequestQueue(this)
        userEmail = findViewById(R.id.cheflogin_user_email)
        userPassword = findViewById(R.id.cheflogin_user_password)
        email = findViewById<EditText>(R.id.cheflogin_user_email)
        password = findViewById<EditText>(R.id.cheflogin_user_password)
        sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
//        val json = sharedPrefs.getString("shops",null)
//        val gson = Gson()
//        val type = object : TypeToken<ArrayList<Shop>>() {}.type
//        shopArray = gson.fromJson(json,type)
        editor = sharedPrefs.edit()

        val button = findViewById<Button>(R.id.cheflogin_button)
        val registerText = findViewById<TextView>(R.id.cheflogin_register_text)

        //Setting OnClickListener

        button.setOnClickListener( View.OnClickListener {
            openFeed()

//            startActivity(Intent(this,ChefMainPage::class.java))
        })

        registerText.setOnClickListener( View.OnClickListener {
            openSignup()
        })

    }

    private fun openFeed() {
        val enteredPassword = password.text.toString()
        val enteredUsername = email.text.toString()
        var tracker = true

//        val newShop = Shop(enteredUsername,"gmail",1321321,"","Western",enteredPassword
//            ,"42342342","Hello","2",null)
//
//        val gson = Gson()
//        val json = gson.toJson(newShop)
//        editor.putString("chefdetails",json)
//        editor.putBoolean("isCustomer", false)
//        editor.apply()
//        val intent = Intent(this,ChefMainPage::class.java)
//        startActivity(intent)

        val url = "http://relish.dyndns-remote.com/RelishBackend/ChefLogin.php"

        val requests = JSONObject()
        requests.put("username",enteredUsername)
        requests.put("password",enteredPassword)

        val jsonObjectRequest = CustomJsonReq(
            Request.Method.POST, url, requests,
            Response.Listener {
                Log.d("json requesting", "it works hehe")
                println(it.toString())

                if (it != null) {
                    if( it.length() != 0) {
                        val intent = Intent(this, ChefMainPage::class.java)
                        val shop = it[0] as JSONObject
                        val postalCode = shop.get("PostalCode") as String
                        val email = shop.get("Email") as String
                        val cuisine = shop.get("Cuisines") as String
                        val id = shop.get("Vendor ID") as String
                        val phoneNumber = shop.get("HandphoneNum") as String
                        val address = shop.get("Address") as String

                        val newShop = Shop(enteredUsername,email,postalCode.toLong(),"",cuisine,enteredPassword
                            ,phoneNumber,address,id,null)

                        val gson = Gson()
                        val json = gson.toJson(newShop)
                        editor.putString("chefdetails",json)
                        editor.putBoolean("isCustomer", false)
                        editor.apply()
                        startActivity(intent)
                    } else {
                        userEmail.setTextColor(Color.rgb(255,0,0))
                        userPassword.setTextColor(Color.rgb(255,0,0))
                        userEmail.setError("Wrong!")
                        userPassword.setError("Wrong!")
                        Toast.makeText(this, "Invalid Username/Password", Toast.LENGTH_LONG).show()
                    }
                }
            },
            Response.ErrorListener {
                Log.i("tagconverterstr", "[" + it + "]")
                Toast.makeText(this,"Server might not be on",Toast.LENGTH_LONG).show()

            }
        )
        mQueue.add(jsonObjectRequest)
        Log.d("json requesting","after")

    }

    private fun openSignup() {
        startActivity(Intent(
            this,ChefSignup::class.java
        ))
    }
}
