package com.example.relishorbital

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.signup_page.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class SignUp : AppCompatActivity() {

    lateinit var registerTextView: TextView
    lateinit var mQueue: RequestQueue
    lateinit var registerButton: Button
    lateinit var file: Uri
    lateinit var phonePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_page)
        Log.d("is this class even","no its not")

        val loginButton = findViewById<Button>(R.id.signup_login_button)
        phonePicture = findViewById<ImageView>(R.id.signup_logo)


        registerTextView = findViewById<TextView>(R.id.signup_greeting)
        mQueue = Volley.newRequestQueue(this)
        registerButton = findViewById<Button>(R.id.signup_register_button)

        registerButton.setOnClickListener(View.OnClickListener {
            Log.d("IS it working", "yes it is")
            jsonParse();
        })
    }

    fun jsonParse() {
        val url = "http://relish.dyndns-remote.com/relish/Dishes.php"

        //JsonObjectRequest because this is a Json Object, else use JsonArrayRequest
        val request = JsonArrayRequest(Request.Method.POST, url, null, Response.Listener {
            for( vendors in 0 until it.length()) {
                val jsonObj = it[vendors] as JSONObject
                println("1")
                val firstName = jsonObj.getString("Vendor ID")
                println("2")
                val age = jsonObj.getString("VendorName")
                println("3")
                val email = jsonObj.getString("Email")
                println("4")
                registerTextView.append(firstName + age + email + "\n")
            }

        }, Response.ErrorListener() {
            Log.d("something is wrong", "yes something is wrong")
            println(it.message)
        })
        mQueue.add(request)

    }

}
