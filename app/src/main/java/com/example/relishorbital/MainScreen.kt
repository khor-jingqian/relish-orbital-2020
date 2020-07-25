package com.example.relishorbital

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import java.net.HttpURLConnection


class MainScreen : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {


    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var sharedPrefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var mAdapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        requestPermissions()

        //Set up fragments for NavigationView
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        //Setting up ActionBarToggle
        drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val header = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        toggle = ActionBarDrawerToggle(this,drawer,0,0)
        drawer.addDrawerListener(toggle)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()
        supportFragmentManager.beginTransaction().add(R.id.frame_container,RollingFeed())
//            .addToBackStack(null)
            .commit()
        navigationView.setCheckedItem(R.id.menu_customer_browse)

        //Handle Sign Out click
        sharedPrefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        val signOutButton = findViewById<TextView>(R.id.navbar_signout)
        signOutButton?.setOnClickListener(View.OnClickListener {
            signOut()
        })

    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
            Log.d("MainScreen","back button pressed while drawer open")
        } else {
            moveTaskToBack(true)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_customer_account -> {
                drawer.closeDrawer(GravityCompat.START)
                supportFragmentManager.beginTransaction().add(R.id.frame_container,CustomerDetails())
                    .commit()
            }
            R.id.menu_customer_browse -> {
                drawer.closeDrawer(GravityCompat.START)
                supportFragmentManager.beginTransaction().add(R.id.frame_container,RollingFeed())
                    .commit()
            }
            R.id.menu_customer_location -> {
                drawer.closeDrawer(GravityCompat.START)
                supportFragmentManager.beginTransaction().add(R.id.frame_container,LocationServices())
                    .commit()
//                Toast.makeText(this,"Feature currently unavailable!",Toast.LENGTH_LONG).show()
            }
        }
        return true
    }

    fun signOut() {
        editor.remove("customerdetails")
        editor.remove("isCustomer")
        editor.apply()
        val intent = Intent(this,FilterScreen::class.java)
        startActivity(intent)
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions,0)
        }
    }

}
