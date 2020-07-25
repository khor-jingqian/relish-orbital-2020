package com.example.relishorbital

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RollingFeed : Fragment(), AdapterView.OnItemSelectedListener,
    ShopAdapter.onItemListener{

    var shopArray = arrayListOf<Shop>()
    lateinit var sharedPrefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var mQueue: RequestQueue
    var mCurCheckPosition = 0
    lateinit var customer: Customer
    lateinit var mRecyclerView:RecyclerView
    lateinit var mAdapter: ShopAdapter
    lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        retainInstance = true
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rollingfeed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if( savedInstanceState == null ) {
            Log.d("savedInstanceState","is null")
            //Retrieving username from SharedPreferences
            sharedPrefs = this.activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                    as SharedPreferences
            editor = sharedPrefs.edit()
            val gson = Gson()
            val json = sharedPrefs.getString("customerdetails",null)
            val type = object : TypeToken<Customer>() {}.type
            customer = gson.fromJson(json,type)
            mQueue = Volley.newRequestQueue(this.context)

            sendJsonRequest()
            //Setting up welcome message in the main screen
            val username = customer.getUserName()
            val mainscreenTaskbar = view?.findViewById<TextView>(R.id.main_screen_taskbar)
            if (mainscreenTaskbar != null) {
                mainscreenTaskbar.bringToFront()
                val msg = SpannableString(username)
                msg.setSpan(ForegroundColorSpan(Color.parseColor("#FF7400")), 0, msg.length, 0)
                mainscreenTaskbar.setText("Welcome! ")
                mainscreenTaskbar.append(msg)
                val msg2 = SpannableString("\nWhat would you like to order today?")
                msg2.setSpan(
                    AbsoluteSizeSpan(
                        20, true
                    ), 0, msg2.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                mainscreenTaskbar.append(msg2)
                !mainscreenTaskbar.isEnabled
            }
            mRecyclerView = view?.findViewById<RecyclerView>(R.id.main_screen_recyclerview) as RecyclerView
            mLayoutManager = LinearLayoutManager(context)
            Handler().postDelayed(Runnable {
                view.findViewById<ProgressBar>(R.id.frame_progressbar).visibility = View.GONE
                //Setting up rolling feed of Stores
                mAdapter = ShopAdapter(context!!,shopArray, this)
                if (mRecyclerView != null) {
                    mRecyclerView.setHasFixedSize(true)
                    mRecyclerView.adapter = mAdapter
                    mRecyclerView.layoutManager = mLayoutManager
                    mRecyclerView.addItemDecoration(
                        DividerItemDecoration(
                            mRecyclerView?.context,
                            mLayoutManager.orientation
                        )
                    )
                }
                val json2 = gson.toJson(shopArray)
                editor.putString("shoplist",json2)
                editor.apply()
            },1000)

            //Setting filter spinner
            val filterSpinner = view?.findViewById<Spinner>(R.id.main_screen_filter)
            filterSpinner?.onItemSelectedListener = this

            //Handle Sign Out click
            val signOutButton = view?.findViewById<TextView>(R.id.navbar_signout)
            signOutButton?.setOnClickListener(View.OnClickListener {
                signOut()
            })
            //If first time log in for user
            val firstTime = sharedPrefs.getBoolean("firstTime", true)

            if (firstTime) {
                AlertDialog.Builder(context, R.style.MyDialogTheme).setTitle("Welcome!")
                    .setMessage(
                        "Welcome to Relish for the very first time!\n\nA navigation bar can be " +
                                "opened by swiping from left to right"
                    ).show()
                editor.putBoolean("firstTime", false)
                editor.apply()
            }

        } else {
            Log.d("savedInstanceState","is not null")
            mCurCheckPosition = savedInstanceState.getInt("curChoice",0)
        }
        retainInstance = true
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val filter = parent?.getItemAtPosition(position)

        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = mLayoutManager
        }

        if( filter == "Any") {
            var mAdapter = ShopAdapter(context!!,shopArray,this)
            if (mRecyclerView != null) {
                mRecyclerView.adapter = mAdapter
                mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context,
                    mLayoutManager.orientation))
            }
        } else {
            val newShopArray = arrayListOf<Shop>()
            val newFilter = filter as String
            for (shops in shopArray) {
                if (shops.getShopCuisine() == (newFilter + " Cuisine")) {
                    newShopArray.add(shops)
                }
            }
            var mAdapter = ShopAdapter(context!!,newShopArray,this)
            if (mRecyclerView != null) {
                mRecyclerView.adapter = mAdapter
                mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context,
                    mLayoutManager.orientation))
            }
        }
    }

    override fun onItemClick(position: Int) {
        //This will give you the item in question
        Log.d("Testing the onitemclick","did it work")
        val id = shopArray.get(position).getID()
        val intent = Intent(context,Listing::class.java)
        intent.putExtra("id",id)
        intent.putExtra("storename",shopArray.get(position).getShopName())
        startActivity(intent)
    }

    fun signOut() {
        editor.remove("username")
        editor.remove("customerdetails")
        editor.remove("isCustomer")
        editor.apply()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun sendJsonRequest() {
        val url = "http://relish.dyndns-remote.com/RelishBackend/Stores.php"

        val request = JsonArrayRequest(Request.Method.POST,url,null,
        Response.Listener {
            for( shops in 0 until it.length()) {
                val shop = it[shops] as JSONObject
                val postalCode = shop.get("PostalCode") as String
                val email = shop.get("Email") as String
                val cuisine = shop.get("Cuisines") as String
                val enteredUsername = shop.get("VendorName") as String
                val enteredPassword = shop.get("Password_Vendor") as String
                val id = shop.get("Vendor ID") as String
                val phoneNumber = shop.get("HandphoneNum") as String
                val address = shop.get("Address") as String
                var profileImage = ""
                try {
                    profileImage = shop.get("ProfileImage") as String
                    profileImage.removeRange(10,11)
                } catch (err:Exception) {
                    println(err.message)
                }



                try {
                    if(profileImage!="") {
                        val newShop = Shop(
                            enteredUsername,
                            email,
                            postalCode.toLong(),
                            "",
                            cuisine,
                            enteredPassword,
                            phoneNumber,
                            address, id, profileImage
                        )
                        shopArray.add(newShop)
                    }
                } catch (err: NumberFormatException) {
                    println(err.message)
                }

            }
        }, Response.ErrorListener {
                println(it)
            })
        mQueue.add(request)
    }

}
