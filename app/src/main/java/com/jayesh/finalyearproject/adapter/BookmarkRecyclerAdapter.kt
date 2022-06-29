package com.jayesh.finalyearproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.database.PhotographersEntity
import de.hdodenhof.circleimageview.CircleImageView

class BookmarkRecyclerAdapter(
    val context: Context,
    val photographersList: List<PhotographersEntity>
) :
    RecyclerView.Adapter<BookmarkRecyclerAdapter.BookmarkViewHolder>() {

    class BookmarkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val civProfileImage: CircleImageView = view.findViewById(R.id.civProfileImage)
        val txtPhotographersName: TextView = view.findViewById(R.id.txtPhotographersName)
//        val txtDesc: TextView = view.findViewById(R.id.txtDesc)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bookmark_item, parent, false)

        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BookmarkViewHolder,
        position: Int
    ) {
        val photographer = photographersList[position]
        holder.txtPhotographersName.text = photographer.photographersName
    }

    override fun getItemCount(): Int {
        return photographersList.size
    }
}