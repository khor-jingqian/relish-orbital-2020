package com.example.relishorbital

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

import com.google.android.gms.tasks.OnCompleteListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.ui.IconGenerator
import org.json.JSONObject


class LocationServices : Fragment(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    lateinit var mMapView: MapView
    lateinit var mFusedClient: FusedLocationProviderClient
    lateinit var shopList: ArrayList<Shop>
    var myMap: GoogleMap? = null
    var loc: Location? = null
    val markerMap:HashMap<Marker,Shop> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        return inflater.inflate(R.layout.fragment_location_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPrefs = this.activity!!.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val gson = Gson()
        val json = sharedPrefs.getString("shoplist","")
        val type = object : TypeToken<ArrayList<Shop>>() {}.type
        shopList = gson.fromJson(json,type)

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey")
        }
        mMapView = view!!.findViewById(R.id.user_list_map) as MapView
        mMapView.onCreate(mapViewBundle)

        mMapView.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle("MapViewBundleKey")
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle("MapViewBundleKey", mapViewBundle)
        }
        mMapView.onSaveInstanceState(mapViewBundle)
    }

    public override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    public override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    public override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
        Log.d("size of shopList: ",shopList.size.toString())
//        mFusedClient = LocationServices.getFusedLocationProviderClient(context!!)
//        mFusedClient.lastLocation.addOnCompleteListener( OnCompleteListener {
//            loc = it.getResult()
//        })
        myMap = map
        val cu = CameraUpdateFactory.newLatLngZoom(LatLng(1.3068169,103.7682197),14.0F)
        map.moveCamera(cu)
        myMap!!.isMyLocationEnabled = true
        val sendreq = sendRequests()
        sendreq.execute(shopList)
        myMap!!.setOnMarkerClickListener(this)
    }

    public override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    public override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    private inner class sendRequests: AsyncTask<ArrayList<Shop>,MarkerOptions,Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
            myMap!!.addMarker(MarkerOptions().position(LatLng(1.286206,103.8396046)).title("Home"))
        }

        override fun doInBackground(vararg params: ArrayList<Shop>?): Void? {
            Log.d("doinbackground","reached")
            Log.d("params size: ",params.size.toString())
            Log.d("arraylist: ", params[0].toString())
            val list = params[0] as ArrayList<Shop>
            for( shops in list) run {
                val address = shops.getAddress().replace(' ','+')
                val url = "https://maps.googleapis.com/maps/api/geocode/json?address="+
                        address + "&key=AIzaSyA-yspdBNdhlFSvglNSYqYPrxH8w2D8EuI"
                Log.d("url input: " , url)
                val requests = JsonObjectRequest(Request.Method.POST,url,null,
                Response.Listener {
                    val jsonarr = it.getJSONArray("results")
                    if (jsonarr.length()>0) {
                        val coord = jsonarr.getJSONObject(0)
                        val coord2 = coord.getJSONObject("geometry")
                        val latlng = coord2.getJSONObject("location")
                        val m = myMap!!.addMarker(MarkerOptions().position(LatLng(
                            latlng.getDouble("lat"), latlng.getDouble("lng"))).title(shops.getShopName()))
                        val icg = IconGenerator(this@LocationServices.context)
                        m.setIcon(
                            BitmapDescriptorFactory.fromBitmap(icg.makeIcon(shops.getShopName())))


                        markerMap.put(m,shops)
//                        publishProgress(m)
                    }
                }, Response.ErrorListener {
                        println(it.message)
                    })
                val mQueue = Volley.newRequestQueue(this@LocationServices.context)
                mQueue.add(requests)
            }
            return null
        }

//        override fun onProgressUpdate(vararg values: MarkerOptions?) {
//            super.onProgressUpdate(*values)
//            myMap!!.addMarker(values[0])
//        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val s = markerMap[p0]
        val intent = Intent(context,Listing::class.java)
        intent.putExtra("id",s!!.getID())
        intent.putExtra("storename", s.getShopName())
        startActivity(intent)
        return true
    }

}
