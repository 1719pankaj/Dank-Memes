package com.example.dankmemes.Fragments

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.dankmemes.Meme
import com.example.dankmemes.MemeListAdapter
import com.example.dankmemes.MySingleton
import com.example.dankmemes.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.io.File
import java.io.FileOutputStream


class MainFragment : Fragment() {
    lateinit var mAdapter: MemeListAdapter
    var downloadId = 0
    var isScrolling = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val manager = LinearLayoutManager(context)
        view.recyclerView.layoutManager = manager

        val items = fetchData()
        mAdapter = MemeListAdapter(requireActivity(), requireContext())
        view.recyclerView.adapter = mAdapter
        view.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentItems = manager.childCount
                val totalItems = manager.itemCount
                val scrollOutItems = manager.findFirstVisibleItemPosition()

                if(isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    fetchData()
                    !isScrolling
                }
            }
        })

        view.swipeContainer.setOnRefreshListener {
            mAdapter.clear()
            fetchData()
            swipeContainer.setRefreshing(false)
        }

        return view
    }

    fun fetchData(): Boolean {
        val url = "https://meme-api.herokuapp.com/gimme/10"
        val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, {
                val memeJsonArray = it.getJSONArray("memes")
                val memeArray = ArrayList<Meme>()
                for (i in 0 until memeJsonArray.length()) {
                    val memeJsonObject = memeJsonArray.getJSONObject(i)
                    val meme = Meme(
                        memeJsonObject.getString("subreddit"),
                        memeJsonObject.getString("title"),
                        memeJsonObject.getString("url"),
                        memeJsonObject.getString("preview"),
                        memeJsonObject.getString("author"),
                        memeJsonObject.getString("ups")
                    )
                    val format = meme.url.subSequence(meme.url.length-4, meme.url.length)
                    if(format != ".gif") {
                        memeArray.add(meme)
                    }
                    Log.i("TAGG","fetch data core")
                }
                mAdapter.updateMeme(memeArray)
                Log.i("TAGG", "${memeArray.size}")


            }, Response.ErrorListener {
                Toast.makeText(context, "Net off hai shayad", Toast.LENGTH_SHORT).show()
            }) {  }
        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
        return true
    }

}