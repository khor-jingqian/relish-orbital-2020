package com.example.relishorbital

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject

class CustomerSignUp : AppCompatActivity() {

    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var email: EditText
    lateinit var phone: EditText
    lateinit var prefs: Spinner
    lateinit var sharedPrefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var mQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_sign_up)

        username = findViewById(R.id.customersignup_username)
        password = findViewById(R.id.customersignup_password)
        email = findViewById(R.id.customersignup_email)
        phone = findViewById(R.id.customersignup_phone)
        prefs = findViewById(R.id.customersignup_spinner)
        sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        mQueue = Volley.newRequestQueue(this)
        val button = findViewById<Button>(R.id.customersignup_button)
        button.setOnClickListener(View.OnClickListener {
            openMainPage()
        })
    }

    private fun openMainPage() {
        val customer = Customer(username.text.toString(),
        password.text.toString(),
        email.text.toString(),
        phone.text.toString().toLong(),
        prefs.selectedItem.toString(),null)

        sendDetails()

        val intent = Intent(this,MainScreen::class.java)
        val gson = Gson()
        val editor = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE).edit()
        editor.putString("customerdetails",gson.toJson(customer))
        editor.putBoolean("isCustomer",true)
        editor.apply()
        startActivity(intent)


    }

    private fun sendDetails() {
        val url = "http://relish.dyndns-remote.com/RelishBackend/ConsumerRegister.php"
        val req = JSONObject()
        req.put("username",username.text.toString())
        req.put("password",password.text.toString())
        req.put("email",email.text.toString())
        req.put("phoneNumber",phone.text.toString())
        req.put("preference",prefs.selectedItem.toString())

        println(req.toString())
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, req,
            Response.Listener {
                Log.d("json requesting", "it works hehe")
            },
            Response.ErrorListener {
                Log.i("tagconverterstr", "[" + it + "]")
//                Toast.makeText(this,"Server might not be on",Toast.LENGTH_LONG).show()
            }
        )
        mQueue.add(jsonObjectRequest)
    }
}
