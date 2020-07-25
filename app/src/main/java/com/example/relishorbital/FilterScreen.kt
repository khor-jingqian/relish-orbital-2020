package com.example.relishorbital

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class FilterScreen : AppCompatActivity() {

    private lateinit var firstScreen: View
    private lateinit var secondScreen: View
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var customerLogin: TextView
    private lateinit var chefLogin: TextView
    private lateinit var fadeIn: Animation
    private lateinit var fadeOut: Animation
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var mQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_screen)

        // Check if user has logged in before, skip login process
        sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val firstTimeCust = sharedPrefs.getString("customerdetails",null)
        val firstTimeChef = sharedPrefs.getString("chefdetails",null)

        if(firstTimeCust == null && firstTimeChef == null) {

            //Instantiate Volley for Requests to database

            mQueue = Volley.newRequestQueue(this)

            //Initialize animations and Views

            fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
            fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
            firstScreen = findViewById(R.id.filterscreen_first)
            secondScreen = findViewById(R.id.filterscreen_second)
            secondScreen.visibility = View.GONE
            customerLogin = findViewById<TextView>(R.id.filterscreen_secondCustomer)
            chefLogin = findViewById<TextView>(R.id.filterscreen_secondChef)

            // Use View's own Handler to arrange a delay
            firstScreen.postDelayed(Runnable() {
                firstScreen.startAnimation(fadeOut)
                firstScreen.visibility = View.GONE
                secondScreen.visibility = View.VISIBLE
                secondScreen.startAnimation(fadeIn)
            }, 2000)

            //Set OnClickListener on both TextView
            customerLogin.setOnClickListener(View.OnClickListener {
                openCustomerLogin()
            })

            chefLogin.setOnClickListener(View.OnClickListener {
                openChefLogin()
            })
        } else {
            val isCustomer = sharedPrefs.getBoolean("isCustomer",false)
            if(isCustomer) {
                Log.d("Debug","did it pass here")
                openFeed()
            } else {
                openChefFeed()
            }
        }
    }

    private fun openCustomerLogin() {


        val intent = Intent(this,MainActivity::class.java)
        secondScreen.postDelayed( Runnable {
            startActivity(intent)
        },1000)
        chefLogin.startAnimation(fadeOut)
        chefLogin.visibility = View.GONE
        findViewById<TextView>(R.id.filterscreen_secondor).startAnimation(fadeOut)
        findViewById<TextView>(R.id.filterscreen_secondor).visibility = View.GONE
    }

    private fun openChefLogin() {
        val intent = Intent(this,ChefLogin::class.java)
        secondScreen.postDelayed( Runnable {
            startActivity(intent)
        },1000)
        customerLogin.startAnimation(fadeOut)
        customerLogin.visibility = View.GONE
        findViewById<TextView>(R.id.filterscreen_secondor).startAnimation(fadeOut)
        findViewById<TextView>(R.id.filterscreen_secondor).visibility = View.GONE
    }

    private fun openFeed() {
        val intent = Intent(this,MainScreen::class.java)
        startActivity(intent)
    }

    private fun openChefFeed() {
        val intent = Intent(this,ChefMainPage::class.java)
        startActivity(intent)
    }
}
