package com.example.dankmemes

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.dankmemes.Fragments.MainFragment
import java.io.File

class MemeListAdapter(val activity: Activity, val context: Context) : RecyclerView.Adapter<MemeListAdapter.MemeViewHolder>() {

    private val items: ArrayList<Meme> = ArrayList()
    val mainFragment = MainFragment()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meme, parent, false)
        val viewHolder = MemeViewHolder(view)
        return viewHolder
    }


    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        val currentImage = items[position]

        holder.upsTV.text = currentImage.ups
        holder.titleTV.text = currentImage.title
        holder.authorTV.text = currentImage.author
        holder.subredditTV.text = currentImage.subreddit
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        Glide
            .with(holder.itemView.context)
            .load(currentImage.url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    holder.imageView.setImageResource(R.drawable.load_failed)
                    return false
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .placeholder(R.drawable.loading)
            .into(holder.imageView)
        holder.saveBT.setOnClickListener { downloadFromUrl(currentImage.url, currentImage.title, false) }
        holder.shareBT.setOnClickListener { mainFragment.shareIntent(currentImage.url, currentImage.title) }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun clear() { items.clear() }

    fun updateMeme(updatedMemes: ArrayList<Meme>) {
        items.addAll(updatedMemes)
        notifyDataSetChanged()
    }

    fun downloadFromUrl(url: String, title: String, silent: Boolean): String  {
        val request = DownloadManager.Request(url.toUri())
            .setTitle("Dank Memes")
            .setDescription("Ho raha bsdk saas le")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                File.separator + "DankMemes" + File.separator + "$title${url.subSequence(url.length-4, url.length)}")
//            .setDestinationUri((Environment.getExternalStorageDirectory().toString()+ "/DankingMemes").toUri())
            .setAllowedOverMetered(true)

        val fileName = "$title${url.subSequence(url.length-4, url.length)}"


        val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)
        if (!silent) {
            Toast.makeText(context, "Image is being saved", Toast.LENGTH_SHORT).show()
        }

        val broadcastReciever = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if(id == downloadId && !silent) {
                    Toast.makeText(context, "Meme Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return fileName
    }



    inner class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val upsTV: TextView = itemView.findViewById(R.id.upsTV)
        val titleTV: TextView = itemView.findViewById(R.id.titleTV)
        val authorTV: TextView = itemView.findViewById(R.id.authorTV)
        val subredditTV: TextView = itemView.findViewById(R.id.subredditTV)
        val saveBT: Button = itemView.findViewById(R.id.saveBT)
        val shareBT: Button = itemView.findViewById(R.id.shareBT)

    }

}
