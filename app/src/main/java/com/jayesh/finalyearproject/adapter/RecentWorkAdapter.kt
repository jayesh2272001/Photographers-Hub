package com.jayesh.finalyearproject.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.jayesh.finalyearproject.R

class RecentWorkAdapter(val context: Context, val imagesList: List<Uri>) :
    RecyclerView.Adapter<RecentWorkAdapter.RecentWorkViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentWorkAdapter.RecentWorkViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recent_work_item, parent, false)
        return RecentWorkViewHolder(view)
    }

    class RecentWorkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivWorkImage :ImageView=view.findViewById(R.id.ivWorkImage)
    }

    override fun onBindViewHolder(holder: RecentWorkAdapter.RecentWorkViewHolder, position: Int) {
        val image = imagesList[position]
        holder.ivWorkImage.setImageURI(image)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }


}