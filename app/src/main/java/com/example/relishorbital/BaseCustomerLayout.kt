package com.example.relishorbital

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout


abstract class BaseCustomerLayout : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var username: String
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout


   open fun onCreate(savedInstanceState: Bundle?, layoutID: Int){

        super.onCreate(savedInstanceState)
        setContentView(layoutID)
        drawerLayout = findViewById(R.id.base_drawer_layout)
        drawerToggle = ActionBarDrawerToggle(this,drawerLayout,0,0)
        drawerLayout.addDrawerListener(drawerToggle)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeButtonEnabled(true)




        //Retrieving username
        sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        username = sharedPrefs.getString("username","placeholder text") as String
//        val navbarUsername = findViewById<TextView>(R.id.nav_header_username)
//        navbarUsername.text = username
        //Handle Sign Out click
//        val signOutButton = findViewById<TextView>(R.id.navbar_signout)
//        signOutButton.setOnClickListener(View.OnClickListener {
//            signOut()
//        })
    }

    public fun getUsername(): String {
        return username
    }

    fun signOut() {
        editor.remove("username")
        editor.apply()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
//        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    abstract fun getLayoutID():Int
}