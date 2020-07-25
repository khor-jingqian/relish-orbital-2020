package com.example.relishorbital

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.net.HttpURLConnection

class ChefUploadDish : AppCompatActivity() {

    lateinit var galleryButton: Button
    lateinit var sharedPrefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var name: EditText
    lateinit var price: EditText
    lateinit var desc: EditText
    lateinit var shop: Shop
    lateinit var preview: ImageView
    lateinit var upload: Button
    private var imageData: ByteArray? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chef_upload_dish)

        preview = findViewById(R.id.chefuploaddish_preview)
        name = findViewById(R.id.chefuploaddish_name)
        price = findViewById(R.id.chefuploaddish_price)
        desc = findViewById(R.id.chefuploaddish_desc)
        upload = findViewById(R.id.chefuploaddish_upload)

        galleryButton = findViewById(R.id.chefuploaddish_gallery)
        sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        val gson = Gson()
        val json = sharedPrefs.getString("chefdetails", "")
        val type = object : TypeToken<Shop>() {}.type
        shop = gson.fromJson(json, type) ?: Shop(
            "Orbital2020", "dummy@gmail.com",
            12345, "short d", "Western", "abcd", "abcd", "abcd123", "1"
            , null
        )

        galleryButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 9999)
        })

        upload.setOnClickListener(View.OnClickListener {
            var final = ""
            final += name.text.toString()
            final += '-'
            final += price.text.toString()
            final += '-'
            final += desc.text.toString()
            final += '-'
            final += shop.id
            uploadImage(final)
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 9999) {
            val uri = data?.data
            if (uri != null) {
                createImageData(uri)
                preview.setImageURI(uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    private fun uploadImage(final: String) {
        imageData ?: return
        val request = object : VolleyFileUploadRequest(
            Request.Method.POST,
            "http://relish.dyndns-remote.com/RelishBackend/ChefDishUpload.php",
            Response.Listener {
                println("response is: $it")
                Toast.makeText(this, "Successful change!", Toast.LENGTH_LONG).show()
                val intent = Intent(this,ChefMainPage::class.java)
                startActivity(intent)
            },
            Response.ErrorListener {
                println("error is: " + it.message)
                it.printStackTrace()
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["DishImage"] = FileDataPart(final, imageData!!, "jpeg")
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}
