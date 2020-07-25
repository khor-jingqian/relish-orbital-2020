package com.example.relishorbital

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.android.volley.RequestQueue
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ChefSignupCont : AppCompatActivity() {

    lateinit var file: Uri
    lateinit var phonePicture: ImageView
    var tracker: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chef_signup_cont)

        val uploadButton = findViewById<Button>(R.id.chefsignupcont_button)
        Log.d("is this alive","it is")
        phonePicture = findViewById<ImageView>(R.id.chefsignupcont_image)

        //Requesting permission for camera usage and to write pictures to phone
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }
        Log.d("request permission","successful")
    }

    public fun takePicture2(view: View) {
        tracker = true
        Log.d("creation","here")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        Log.d("before", "here")
        file = FileProvider.getUriForFile(applicationContext,BuildConfig.APPLICATION_ID+".fileprovider",createImageFile())
        Log.d("after", "here")

        //
        if( file != null) {
            Log.d("file is not empty","successful")
            intent.putExtra(MediaStore.EXTRA_OUTPUT,file )
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        //100 is the requestCode you defined in takePicture so you can identify that this is
        //the return of takePicture
        if (requestCode.equals(100)) {
            if (resultCode == RESULT_OK) {
                Log.d("setting image", "is failing")
                phonePicture.setImageURI(file)
            }
        }
    }
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )/*.apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }*/
    }

    public fun rotateRight(view: View) {
        phonePicture.rotation = phonePicture.rotation + 90
    }

    fun openFinalisation(view: View) {
        val nextIntent = Intent(this,ChefSignupFinal::class.java)
        assert(intent.getSerializableExtra("customer")!= null) {
            print("something is  wrong")
        }
        val cust = intent.getSerializableExtra("customer") as? Shop
        if (cust != null) {
            if (tracker) {
                nextIntent.putExtra("dishImages",file)
            }
            nextIntent.putExtra("customer",cust)
            startActivity(nextIntent)
        } else {
            println("intent is empty over here")
        }

    }

}
