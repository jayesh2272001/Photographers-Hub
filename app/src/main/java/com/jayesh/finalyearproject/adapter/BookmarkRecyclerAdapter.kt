package com.jayesh.finalyearproject.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.activity.PhotographersDescActivity
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
        val cvLayout: CardView = view.findViewById(R.id.cvLayout)
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

        val name = photographer.photographersName
        val id = photographer.photographersId

        holder.cvLayout.setOnClickListener {
            val intent = Intent(context, PhotographersDescActivity::class.java)
            intent.putExtra("userVal", id)
            intent.putExtra("userName", name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return photographersList.size
    }
}