package com.example.dankmemes

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
    }
    fun downloadFromUrl(url: String, title: String) {
        val request = DownloadManager.Request(url.toUri())
            .setTitle("Download Manager Dummy")
            .setDescription("Ho raha bsdk saas le")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                "$title+${url.subSequence(url.length-4, url.length)}")
            .setAllowedOverMetered(true)

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)
        Toast.makeText(this, "Image is being saved", Toast.LENGTH_SHORT).show()

        val broadcastReciever = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if(id == downloadId) {
                    Toast.makeText(this@MainActivity, "Meme Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
        registerReceiver(broadcastReciever, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }
}
