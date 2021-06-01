package com.example.dankmemes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    var currentImageUrl: String = "https://umdcareers.files.wordpress.com/2016/02/himym-5.png"
    var arr = arrayOf("https://umdcareers.files.wordpress.com/2016/02/himym-5.png")
    var currentIndex = 0
    var max = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moveIt(true)

        button2.setOnClickListener {
            if(isReadStoragePermissionGranted() && isWriteStoragePermissionGranted()){
                saveToGallery()
            }
        }
        back.setOnClickListener { moveIt(false) }

    }

    fun isReadStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "READ permission granted")
                true
            } else {
                Log.v("TAG", "READ permission revoked")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 3)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "READ permission granted")
            true
        }
    }


    fun isWriteStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "WRITE permission granted")
                true
            } else {
                Log.v("TAG", "WRITE permission revoked")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "WRITE permission granted")
            true
        }
    }


    fun loadMemeUrl() {
        progressBar.visibility = View.VISIBLE

        val url = "https://meme-api.herokuapp.com/gimme"

        val JsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->

                    currentImageUrl = response.getString("url")
                    showMeme(currentImageUrl, true)

                },
                {
                    Toast.makeText(this, "Net off hai kya?", Toast.LENGTH_SHORT).show()
                })

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(JsonObjectRequest)
    }


    fun append(arr: Array<String>, element: String): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }


    fun showMeme(ImageUrl: String, bool: Boolean) {
        if (bool) {
            arr = append(arr, ImageUrl)
        }

        Glide.with(this).load(ImageUrl).listener(object : RequestListener<Drawable> {

            override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
            ): Boolean {
                progressBar.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
            ): Boolean {
                progressBar.visibility = View.GONE
                return false
            }

        }).into(imageView)
    }


    fun moveIt(bool: Boolean) {
        if (bool) {
            if (currentIndex == max) {
                loadMemeUrl()
                max++
                currentIndex++

            } else {
                showMeme(arr[currentIndex+1], false)
                currentIndex++
            }
        }
        else if( currentIndex <= 0) {
            Toast.makeText(this, "You have reached the end", Toast.LENGTH_SHORT).show()
        }
        else {
            showMeme(arr[currentIndex-1], false)
            currentIndex--
        }
    }


    fun shareMeme(view: View) {

        val bitmap = (imageView.getDrawable() as BitmapDrawable).bitmap

        //var outStream: FileOutputStream? = null
        val sdCard = Environment.getExternalStorageDirectory().toString()
        val dir = File(sdCard + "/DankMemes")
        dir.mkdirs()
        val fileName = String.format("%d.jpg", System.currentTimeMillis())
        val outFile = File(dir, fileName)
        val outStream = FileOutputStream(outFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.flush()
        outStream.close()

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(outFile.toString()))
        val chooser = Intent.createChooser(intent, "🤣")
        startActivity(chooser)
    }


    fun nextMeme(view: View) {
        moveIt(true)
    }


    fun saveToGallery() {
        val bitmap = (imageView.getDrawable() as BitmapDrawable).bitmap

        //var outStream: FileOutputStream? = null
        val sdCard = Environment.getExternalStorageDirectory().toString()
        val dir = File(sdCard + "/DankMemes")
        dir.mkdirs()
        val fileName = String.format("%d.jpg", System.currentTimeMillis())
        val outFile = File(dir, fileName)
        val outStream = FileOutputStream(outFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.flush()
        outStream.close()
        Toast.makeText(this, "Memes has been saved to $dir + $fileName", Toast.LENGTH_SHORT).show()
    }




}
