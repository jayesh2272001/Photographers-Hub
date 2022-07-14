package com.jayesh.finalyearproject.adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.activity.DisplayImageActivity
import com.jayesh.finalyearproject.activity.WelcomeAuthActivity

class ShowRecentWorkAdapter(val context: Context, val imageList: ArrayList<String>) :
    RecyclerView.Adapter<ShowRecentWorkAdapter.ShowRecentWorkViewHolder>() {

    class ShowRecentWorkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivWorkImage: ImageView = view.findViewById(R.id.ivWorkImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowRecentWorkViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recent_work_item, parent, false)
        return ShowRecentWorkViewHolder(view)
    }


    override fun onBindViewHolder(holder: ShowRecentWorkViewHolder, position: Int) {
        val image = imageList[position]
        Glide.with(context).load(image.substring(10)).into(holder.ivWorkImage).view

        holder.ivWorkImage.setOnClickListener {
            val intent = Intent(context, DisplayImageActivity::class.java)
            intent.putExtra("image-url", image.substring(10))
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}