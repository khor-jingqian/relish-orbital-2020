package com.example.relishorbital

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.graphics.Color
import android.os.Parcelable
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject

class ChefSignup : AppCompatActivity(),
    AdapterView.OnItemSelectedListener {

    lateinit var storeName: EditText
    lateinit var emailAddress: EditText
    lateinit var postalCode: EditText
    lateinit var storeDesc: EditText
    lateinit var button: Button
    lateinit var cuisine: String
    lateinit var password: EditText
    lateinit var phonenumber: EditText
    lateinit var address: EditText
    lateinit var mQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chef_signup)

        mQueue = Volley.newRequestQueue(this)
        button = findViewById(R.id.chefsignup_button)
        storeName = findViewById(R.id.chefsignup_storename)
        emailAddress = findViewById(R.id.chefsignup_email)
        postalCode = findViewById(R.id.chefsignup_postal)
        storeDesc = findViewById(R.id.chefsignup_description)
        password = findViewById(R.id.chefsignup_password)
        phonenumber = findViewById(R.id.chefsignup_phonenumber)
        address = findViewById(R.id.chefsignup_address)

        button.setOnClickListener(View.OnClickListener {
            openSignup()
        })

        val spinner = findViewById<Spinner>(R.id.chefsignup_spinner)
        spinner.onItemSelectedListener = this
    }

    private fun openSignup() {
        if(storeName.text.toString() == "" ||
            emailAddress.text.toString() == "" ||
            postalCode.text.toString() == "" ||
            storeDesc.text.toString() == "" ||
                password.text.toString() == "") {
                Toast.makeText(this,"One of the fields is empty!",Toast.LENGTH_LONG).show()
                if(storeName.text.toString() == "") {
                    storeName.setError("Please fill this")
                }
                if(emailAddress.text.toString() == "") {
                    emailAddress.setError("Please fill this")
                }
                if(postalCode.text.toString() == "") {
                    postalCode.setError("Please fill this")
                }
                if(storeDesc.text.toString() == "") {
                    storeDesc.setError("Please fill this")
                }
                if(password.text.toString() == "") {
                    password.setError("Please fill this")
                }
                if(address.text.toString() == "") {
                    address.setError("Please fill this")
                }
                if(phonenumber.text.toString() == "") {
                    phonenumber.setError("Please fill this")
                }
        } else {
            val intent = Intent(this, ChefMainPage::class.java)
            try {
                val cust = Shop(
                    storeName.text.toString(), emailAddress.text.toString(),
                    postalCode.text.toString().toLong(),
                    storeDesc.text.toString(),
                    cuisine,password.text.toString(),phonenumber.text.toString(),address.text.toString(),null,null)

                uploadDetails()
                val gson = Gson()
                val json = gson.toJson(cust)

                val sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                val editor = sharedPrefs.edit()
                editor.putString("chefdetails",json)
                editor.putBoolean("isCustomer",false)
                editor.apply()
                startActivity(intent)
            } catch (e: NumberFormatException) {
                AlertDialog.Builder(this).setMessage("Please input only numbers in your postal code!")
                    .create().show()
            }
        }
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this,"Please select a cuisine",Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            cuisine = parent.getItemAtPosition(position) as String
        }
    }

    private fun uploadDetails() {

        val url = "http://relish.dyndns-remote.com/RelishBackend/ChefRegister.php"

        val requests = JSONObject()
        requests.put("username",storeName.text.toString())
        requests.put("password",password.text.toString())
        requests.put("phoneNumber",phonenumber.text.toString())
        requests.put("email",emailAddress.text.toString())
        requests.put("address",address.text.toString())
        requests.put("postalCode",postalCode.text.toString())
        requests.put("cuisine",cuisine)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, requests,
            Response.Listener {
                Log.d("json requesting", "it works hehe")
                println(it.toString())
            },
            Response.ErrorListener {
                Log.i("tagconverterstr", "[" + it + "]")
//                Toast.makeText(this,"Server might not be on",Toast.LENGTH_LONG).show()


            }
        )
        mQueue.add(jsonObjectRequest)
    }
}
