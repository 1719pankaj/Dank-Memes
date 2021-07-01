package com.example.dankmemes

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        val dir = File(Environment.getExternalStorageDirectory().toString() + "/SharedDankMemes")
        if (dir.isDirectory()) {
            dir.deleteRecursively()
        }
        Log.i("TAGG", "onDestroy Called")
    }
}
