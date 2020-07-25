package com.example.relishorbital

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChefMainPage : AppCompatActivity(), DishAdapter.onItemListener {

    lateinit var sharedPrefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var shop: Shop
    var dishes = ArrayList<Dishes>()
    lateinit var mQueue: RequestQueue
    var id = 1
    lateinit var orders: TextView
    lateinit var tableLayout: TableLayout
    lateinit var signout: ImageButton
    lateinit var preview:ImageView
    private var imageData: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chef_main_page)
        mQueue = Volley.newRequestQueue(this)
        orders = findViewById(R.id.chefmainpage_ordersgreeting)
        tableLayout = findViewById(R.id.chefmainpage_tablelayout)
        signout = findViewById(R.id.chefmainpage_logout)

        //Retrieve Shop and its dishes
        sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        val gson = Gson()
        val json = sharedPrefs.getString("chefdetails", "")
        val type = object : TypeToken<Shop>() {}.type
        shop = gson.fromJson(json, type) ?: Shop(
            "Orbital2020", "dummy@gmail.com",
            12345, "short d", "Western", "abcd", "abcd", "abcd123", "1"
        ,null)

        populateDishes()
        checkOrders()

        //Initialise Views
        val cuisine = findViewById<TextView>(R.id.chefmainpage_cuisine)
        val shopName = findViewById<TextView>(R.id.chefmainpage_username)
        val greeting = findViewById<TextView>(R.id.chefmainpage_greeting)
        val emptyMenu = findViewById<TextView>(R.id.chefmainpage_emptymenumsg)
        val cameraButton = findViewById<TextView>(R.id.chefmainpage_newdish)

        emptyMenu.visibility = View.GONE
        shopName.append(shop.getShopName())
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            greeting, 20, 35, 1,
            TypedValue.COMPLEX_UNIT_SP
        )
        cuisine.append(shop.getShopCuisine())

        cameraButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,ChefUploadDish::class.java)
            startActivity(intent)
        })

        val upload = findViewById<Button>(R.id.chefmainpage_profilepic)
        upload.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 999)
        })


        //Instantiate RecyclerView


        Handler().postDelayed(Runnable {
            findViewById<ProgressBar>(R.id.chefmainpage_pb).visibility = View.GONE

            val mRecyclerView = findViewById<RecyclerView>(R.id.chefmainpage_recyclerview)
            val mLayoutManager = LinearLayoutManager(this)
            val mAdapter = DishAdapter(this,dishes, this)
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

            if( mAdapter.itemCount == 0 ) {
                emptyMenu.visibility = View.VISIBLE
            }
        },1000)


        //Instantiate sign-out button
        signout.setOnClickListener( View.OnClickListener {
            logOut()
        })

    }

    private fun populateDishes() {
        val url = "http://relish.dyndns-remote.com/RelishBackend/ChefMenu.php"
        val params = JSONObject()
        params.put("VendorID", shop.id)
        val request = CustomJsonReq(Request.Method.POST, url, params,
            Response.Listener {
                for (counter in 0 until it!!.length()) {
                    println("IS THIS VEN WORKING ANYMORE")
                    val d = it[counter] as JSONObject
                    dishes.add(
                        Dishes(
                            d.getString("Name"), d.getString("Description"),
                            d.getString("price").toFloat(),d.getString("Image"),
                            d.getString("DishID")
                        )
                    )
                }
            }, Response.ErrorListener {
                println(it)
            })
        mQueue.add(request)
    }

    override fun onItemClick(position: Int) {
        val dish = dishes.get(position).getID()

        val url = "http://relish.dyndns-remote.com/RelishBackend/DeleteDish.php"
        val params = JSONObject()
        params.put("DishID",dish)

        val request = CustomJsonReq(Request.Method.POST,url,params,
        Response.Listener {
            Toast.makeText(this,"Deletion successful! Refresh page to see effect",Toast.LENGTH_LONG).show()
        },Response.ErrorListener {
                Toast.makeText(this,"Deletion successful! Refresh page to see effect",Toast.LENGTH_LONG).show()
                println(it.message)
            })
        mQueue.add(request)
    }

    private fun checkOrders() {

        val url = "http://relish.dyndns-remote.com/RelishBackend/getOrders.php"
        val params = JSONObject()
        val gson = Gson()
        val sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val json = sharedPrefs.getString("customerdetails",null)
        val type = object : TypeToken<Customer>() {}.type
        val customer = gson.fromJson<Customer>(json,type)

        params.put("VendorID",shop.id)


        val request = CustomJsonReq(Request.Method.POST,url,params,
            Response.Listener {
                val table = findViewById<TableLayout>(R.id.chefmainpage_tablelayout)

                if( it != null ) {

                    for (count in 0 until it.length()) {
                        val tv0 = TextView(this)
                        val tableRr = TableRow(this)
                        tv0.setText("\nOrder: ")
                        tableRr.addView(tv0)
                        table.addView(tableRr)
                        val order = it[count] as JSONObject
                        val dishString = order.getString("OrderDetails")
                        val dishes = dishString.split("--").toTypedArray()
                        dishes.forEach {
                            println(it)
                            if(it.length > 3) {
                                val dish = it.split('-').toTypedArray()
                                println(dish.toString())
                                val tableR = TableRow(this)
                                val tableRparams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT)
                                tableRparams.weight = 1F
                                tableR.layoutParams = tableRparams
                                val tv1 = TextView(this)
                                val tv2 = TextView(this)
                                val tv3 = TextView(this)
                                println(dish.get(0))
                                tv1.setText(dish.get(0))
                                tv2.setText('$'+ String.format("%.2f",dish.get(1).toDouble()))
                                tv3.setText('x' + dish.get(2))

                                tableR.addView(tv1)
                                tableR.addView(tv2)
                                tableR.addView(tv3)

                                table.addView(tableR, TableLayout.LayoutParams(
                                    TableLayout.LayoutParams.MATCH_PARENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT
                                ))
                                println("here")
//                                println(tableLayout.childCount)
                            }
                        }
                    }
                } else {
                    println("empty")
                    orders.append("Store currently has no orders")
                    tableLayout.visibility = View.GONE
                }
            },Response.ErrorListener {
                println(it)
            })
        mQueue.add(request)
    }

    private fun logOut() {
        editor.remove("isCustomer")
        editor.remove("chefdetails")
        editor.remove("customerdetails")
        editor.apply()
        val intent = Intent(this,FilterScreen::class.java)
        startActivity(intent)
    }

    private fun uploadImage() {
        imageData?: return
        val request = object : VolleyFileUploadRequest(
            Request.Method.POST,
            "http://relish.dyndns-remote.com/RelishBackend/ChefProfileUpload.php",
            Response.Listener {
                println("response is: $it")
                Toast.makeText(this,"Successful change!",Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener {
                println("error is: " + it.message)
                it.printStackTrace()
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["ProfileImage"] = FileDataPart(shop.id, imageData!!, "jpeg")
                return params
            }

//            override fun getParams(): MutableMap<String, String> {
//                val p = HashMap<String,String>()
////                p.put
//            }
        }
        println(imageData == null)
        println(request.body)
        Volley.newRequestQueue(this).add(request)
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 999) {
            val uri = data?.data
            if (uri != null) {
                createImageData(uri)
                uploadImage()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
