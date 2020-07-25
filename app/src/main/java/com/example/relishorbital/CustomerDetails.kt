package com.example.relishorbital

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.w3c.dom.Text

class CustomerDetails : Fragment(){

    lateinit var sharedPrefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var username: String
    lateinit var customer: Customer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customerdetails, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val signoutButton = view?.findViewById<TextView>(R.id.navbar_signout)
        signoutButton?.setOnClickListener( View.OnClickListener {
            signOut()
        })

        //Retrieving username from SharedPreferences
        sharedPrefs = this.activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                as SharedPreferences
        editor = sharedPrefs.edit()
        val json = sharedPrefs.getString("customerdetails",null)
        val gson = Gson()
        val type = object : TypeToken<Customer>() {}.type
        customer = gson.fromJson(json,type)
        username = customer.getUserName()

        //If first time log in for user
        val firstTime = sharedPrefs.getBoolean("firstTime",true)

        if (firstTime) {
            AlertDialog.Builder(context,R.style.MyDialogTheme).setTitle("Welcome!")
                .setMessage("Welcome to Relish for the very first time!\n\nA navigation bar can be " +
                        "opened by swiping from left to right").show()
            editor.putBoolean("firstTime",false)
            editor.apply()
        }

        val text = view?.findViewById<TextView>(R.id.testingTextview)
        val postalcode = view?.findViewById<TextView>(R.id.customerdetailspostalcode)
        if (text != null && postalcode != null ) {
            text.append(username)
            postalcode.append("Yet to fill in")
        }

        val tableLayout = view?.findViewById<TableLayout>(R.id.customerdetails_pastorders)
        val pastOrder = customer.getOrders()
        println("this is past orders: " + pastOrder)

        pastOrder.forEach {
            val tabler = TableRow(context)
            val tv0 = TextView(context)
            tv0.setText("Order: ")
            tableLayout!!.addView(tabler)
            val dish = it.split("--").toTypedArray()

            dish.forEach {
                if(it.length>3) {
                    val dish = it.split('-').toTypedArray()
                    val tableR = TableRow(context)
                    val tableRparams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    )
                    tableRparams.weight = 1F
                    tableR.layoutParams = tableRparams
                    val tv1 = TextView(context)
                    val tv2 = TextView(context)
                    val tv3 = TextView(context)
                    println(dish.get(0))
                    tv1.setText(dish.get(0))
                    tv2.setText('$' + String.format("%.2f",dish.get(1).toDouble()))
                    tv3.setText('x' + dish.get(2))

                    tableR.addView(tv1)
                    tableR.addView(tv2)
                    tableR.addView(tv3)

                    tableLayout.addView(
                        tableR, TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT
                        )
                    )
                }
            }
        }

    }

    private fun signOut() {
        editor.remove("isCustomer")
        editor.remove("customerdetails")
        editor.remove("chefdetails")
        editor.apply()
        val int = Intent(context,FilterScreen::class.java)
        startActivity(int)
    }
}
