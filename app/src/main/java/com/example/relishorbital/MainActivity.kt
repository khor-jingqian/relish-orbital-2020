package com.example.relishorbital

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    lateinit var userEmail: EditText
    lateinit var userPassword: EditText
    val dummyPasswords = arrayOf("password123","correctpassword","anypassword")
    lateinit var sharedPref :SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var mQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Started","nothing is wrong so far")

        mQueue = Volley.newRequestQueue(this)

        sharedPref = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        editor = sharedPref.edit()
//        val firstTime = sharedPref.getBoolean("firstTime",true)

        userPassword = findViewById(R.id.user_password)
        userEmail = findViewById(R.id.user_email)

        val loginLoginButton = findViewById<Button>(R.id.login_button)
        loginLoginButton.setOnClickListener(View.OnClickListener {
            Log.d("login_page", "login_page -> main_page")
            openMainPage()
        })

        val loginRegisterTextView = findViewById<TextView>(R.id.login_signup)
        loginRegisterTextView.setOnClickListener(View.OnClickListener {
            openSignup()
        })

//        val loginRegisterButton = findViewById<Button>(R.id.login_register_button)
//        loginRegisterButton.setOnClickListener(View.OnClickListener {
//            openSignup()
//        })

//        if(firstTime == true) {
//        } else {
//            val intent = Intent(this,MainScreen::class.java)
//            startActivity(intent)
//        }


    }

    fun openSignup() {
        val intent = Intent(this,CustomerSignUp::class.java)
        startActivity(intent)
    }


    fun openMainPage() {
        val enteredPassword = userPassword.text.toString()
        val enteredUsername = userEmail.text.toString()

        val url = "http://relish.dyndns-remote.com/RelishBackend/CustLogin.php"

        val requests = JSONObject()
        requests.put("username",enteredUsername)
        requests.put("password",enteredPassword)

        val jsonObjectRequest = CustomJsonReq(
            Request.Method.POST, url, requests,
            Response.Listener {
                Log.d("json requesting", "it works hehe")
                println(it.toString())

                if( it != null && !it.isNull(0)) {
                    val intent = Intent(this, MainScreen::class.java)
                    val customer = it[0] as JSONObject
                    val c = Customer(customer.getString("CustomerName"),
                    customer.getString("Password_Cust"),
                    "dummyemail@gmail.com",
                    customer.getString("Handphone").toLong(),
                    customer.getString("Preferences"),
                        customer.getString("CustomerNumber"))
                    val gson = Gson()
                    val json = gson.toJson(c)
                    editor.putString("customerdetails", json)
                    editor.putBoolean("isCustomer", true)
                    editor.apply()
                    startActivity(intent)
                } else {
                    userEmail.setTextColor(Color.rgb(255,0,0))
                    userPassword.setTextColor(Color.rgb(255,0,0))
                    userEmail.setError("Wrong!")
                    userPassword.setError("Wrong!")
                    Toast.makeText(this, "Invalid username/Password", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Log.i("tagconverterstr", "[" + it + "]")

            }
        )
        mQueue.add(jsonObjectRequest)
        Log.d("json requesting","after")
    }

}
