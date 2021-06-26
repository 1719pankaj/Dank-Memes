package com.example.dankmemes.Fragments

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.dankmemes.MySingleton
import com.example.dankmemes.R
import android.Manifest
import android.content.Intent
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
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.fragment_onboarding.*
import java.io.File
import java.io.FileOutputStream


class MainFragment : Fragment() {
    var currentImageUrl: String = "https://umdcareers.files.wordpress.com/2016/02/himym-5.png"
    var arr = arrayOf("https://umdcareers.files.wordpress.com/2016/02/himym-5.png")
    var currentIndex = 0
    var max = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        moveIt(true)

        view.back.setOnClickListener { moveIt(false) }
        view.nextWpermission.setOnClickListener { moveIt(true) }
//        view.share.setOnClickListener { shareMeme(view) }
//        view.button2.setOnClickListener { if(isReadStoragePermissionGranted() && isWriteStoragePermissionGranted()) { saveToGallery() } }
        view.share.setOnClickListener { shareMeme(view) }
        view.button2.setOnClickListener { dexter() }
        return view
    }

    fun isReadStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                Log.v("TAG", "READ permission granted")
                true
            } else {
                Log.v("TAG", "READ permission revoked")
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 3)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "READ permission granted")
            true
        }
    }


    fun isWriteStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                Log.v("TAG", "WRITE permission granted")
                true
            } else {
                Log.v("TAG", "WRITE permission revoked")
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "WRITE permission granted")
            true
        }
    }

    fun dexter() {
        Dexter.withContext(context).withPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport){
                    if (p0.areAllPermissionsGranted()) {
                        Toast.makeText(context, "Mil gaya boss", Toast.LENGTH_SHORT).show()


                    } else {
                        Toast.makeText(context, "Nahi mila boss", Toast.LENGTH_SHORT).show()


                    }
                }

                override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    }


    fun loadMemeUrl() {

        val url = "https://meme-api.herokuapp.com/gimme"

        val JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                var raw = response.getString("preview")
                val preview: List<String> = (raw.subSequence(1,raw.length-1) as String).split(",")
                Log.i("TAGG", preview[2].replace("\\/","/"))


                currentImageUrl = preview[2].replace("\\/","/")
//                currentImageUrl = response.getString("url")
                showMeme(currentImageUrl, true)

            },
            {
                Toast.makeText(context, "Net off hai kya?", Toast.LENGTH_SHORT).show()
            })

        // Add the request to the RequestQueue.
        MySingleton.getInstance(requireContext()).addToRequestQueue(JsonObjectRequest)
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

        Glide
            .with(this)
            .load(Uri.parse(ImageUrl.trim()))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
            })
            .error(R.drawable.giphy)
            .into(imageView)

    }


    private fun moveIt(bool: Boolean) {
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
            Toast.makeText(context, "You have reached the end", Toast.LENGTH_SHORT).show()
        }
        else {
            showMeme(arr[currentIndex-1], false)
            currentIndex--
        }
    }


    fun shareMeme(view: View) {
        if (isReadStoragePermissionGranted() && isWriteStoragePermissionGranted()) {
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
    }


    fun nextMeme(view: View) {
        moveIt(true)
    }


    private fun saveToGallery() {
        if (isReadStoragePermissionGranted() && isWriteStoragePermissionGranted()) {
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
            Toast.makeText(context, "Memes has been saved to $dir + $fileName", Toast.LENGTH_SHORT).show()
        }
    }

}