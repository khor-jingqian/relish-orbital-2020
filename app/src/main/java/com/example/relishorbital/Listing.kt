package com.example.relishorbital

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.core.content.res.ComplexColorCompat.inflate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_listing.*
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Listing : AppCompatActivity(),DishAdapter.onItemListener {

    lateinit var mQueue: RequestQueue
    lateinit var itemCount: TextView
    lateinit var price: TextView
    lateinit var checkOut: TextView
    val dishes = ArrayList<Dishes>()
    val checkedOutDishes = Hashtable<Dishes,Int>()
    lateinit var id: String
    private var checkOutString = ""
    private var totalSum = "0.00"
    lateinit var customer:Customer

    var jsonArray = JSONArray()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)

        id = intent.getStringExtra("id") as String
        mQueue = Volley.newRequestQueue(this)
        itemCount = findViewById(R.id.listing_checkout_itemcount)
        price = findViewById(R.id.listing_checkout_price)
        checkOut = findViewById(R.id.listing_checkout_now)
        retrieveShop(id)
        Handler().postDelayed(Runnable {
            findViewById<ProgressBar>(R.id.listing_progressbar).visibility = View.GONE
            if (jsonArray == null) {
                Toast.makeText(this,"Store has no dishes!",Toast.LENGTH_LONG).show()
                println("JSONARRAY IS NULL")
            } else {
                println(jsonArray.toString())
                for( counter in 0 until jsonArray.length()) {
                    val dish= jsonArray[counter] as JSONObject
                    dishes.add(Dishes(dish.getString("Name"),dish.getString("Description"),
                        dish.getString("price").toFloat(),dish.getString("Image"),dish.getString("DishID")))
                }
                val mRecyclerView = findViewById<RecyclerView>(R.id.listing_recyclerview)
                val mLayoutManager = LinearLayoutManager(this)
                val mAdapter = DishAdapter(this,dishes,this)
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
            }
        },1000)

        checkOut.setOnClickListener(View.OnClickListener {
            val cod = checkedOutDishes.keys
            val alert = android.app.AlertDialog.Builder(this, R.style.MyDialogTheme).setTitle("Summary: \n\n")
            val alertd = alert.create()

            val tableLayout = layoutInflater.inflate(R.layout.checkout_summary,null)
            val table = tableLayout.findViewById<TableLayout>(R.id.checkout_summary)
            cod.forEach {
                val count = checkedOutDishes.get(it)

                checkOutString += (it.dishName + '-' + it.p + '-' + count + "--")

                val tablerow = TableRow(this)
                tablerow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                val tv1 = TextView(this)
                val tv2 = TextView(this)
                val tv3 = TextView(this)
                tv1.setText(it.dishName)
                tv2.setText("x"+count)
                tv3.setText(String.format("%.2f",count!!.times(it.p)))
                tablerow.addView(tv1)
                tablerow.addView(tv2)
                tablerow.addView(tv3)
                    table.addView(tablerow)
            }
            println(table==null)
            val tlp = TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
            TableLayout.LayoutParams.WRAP_CONTENT)
            tlp.setMargins(10,30,10,30)
            alert.setView(table)
            alert.setNegativeButton("Back") { dialog, _ -> dialog.dismiss()}
            alert.setPositiveButton("Order Now") {
                    dialog, _ ->
                updateStore()
                Toast.makeText(this,"Order successful!",Toast.LENGTH_LONG).show()
            }
            alert.show()
        })
    }

    private fun retrieveShop(id: String) {
        val url = "http://relish.dyndns-remote.com/RelishBackend/ChefMenu.php"
        val params = JSONObject()
        params.put("VendorID",id)
        val request = CustomJsonReq(Request.Method.POST,url,params,
        Response.Listener {
            println(it.toString())
            jsonArray = it!!
        },Response.ErrorListener {
                println(it)
            })
        mQueue.add(request)
    }

    override fun onItemClick(position: Int) {
        val item = dishes[position]
        val keys = checkedOutDishes.keys
        var tracker = false
        keys.forEach {
            if (it.dishName.equals(item.dishName)) {
                println("addition successful")
                var newCount = checkedOutDishes[item] as Int
                newCount += 1
                checkedOutDishes[item] = newCount
                tracker = true
            }
        }
        if(!tracker) {
            println("addition unsuccessful")
            checkedOutDishes.put(item,1)
        }

        var currentItemCount = itemCount.text.toString().substring(7,itemCount.text.length-1).toInt()
        var currentPrice = price.text.toString().substring(2,price.text.length-1).toFloat()

        var newItemCount = currentItemCount++
        var newPrice = String.format("%.2f", currentPrice + item.p)
        itemCount.setText(" Item: " + currentItemCount + " ")
        price.setText(" $"+newPrice + " ")
        totalSum = newPrice.toString()
    }

    private fun updateStore() {

        val url = "http://relish.dyndns-remote.com/RelishBackend/checkOut.php"

        val params = JSONObject()
        val gson = Gson()
        val sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val edit = sharedPrefs.edit()
        val json = sharedPrefs.getString("customerdetails",null)
        val type = object : TypeToken<Customer>() {}.type
        val customer = gson.fromJson<Customer>(json,type)
        customer.setPastOrders(checkOutString)
        val json2 = gson.toJson(customer)
        edit.putString("customerdetails",json2)
        edit.apply()

        params.put("custID",customer.getID())
        params.put("ChefID",id)
        params.put("totalSum",totalSum)
        params.put("orders",checkOutString)
        params.put("dateTime",
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",Locale.US).format(Date()).toString())
        params.put("location","")

        println(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",Locale.US).format(Date()).toString())

        val request = CustomJsonReq(Request.Method.POST,url,params,
            Response.Listener {
                println(it.toString())
            },Response.ErrorListener {
                println(it)
            })
        mQueue.add(request)
    }
}
