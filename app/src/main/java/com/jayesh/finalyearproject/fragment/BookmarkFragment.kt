package com.jayesh.finalyearproject.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.BookmarkRecyclerAdapter
import com.jayesh.finalyearproject.database.PhotographersDatabase
import com.jayesh.finalyearproject.database.PhotographersEntity


class BookmarkFragment : Fragment() {
    var PhotographersList = listOf<PhotographersEntity>()
    lateinit var rvBookmark: RecyclerView
    lateinit var recyclerAdapter: BookmarkRecyclerAdapter
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)
        rvBookmark = view.findViewById(R.id.rvBookmark)

        layoutManager = GridLayoutManager(activity as Context, 2)
        PhotographersList = RetrieveBookmarks(activity as Context).execute().get()

        if (activity != null) {
            recyclerAdapter = BookmarkRecyclerAdapter(activity as Context, PhotographersList)
            rvBookmark.adapter = recyclerAdapter
            rvBookmark.layoutManager = layoutManager
        }
        return view
    }

    class RetrieveBookmarks(val context: Context) :
        AsyncTask<Void, Void, List<PhotographersEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<PhotographersEntity> {
            val db = Room.databaseBuilder(context, PhotographersDatabase::class.java, "bookmark-db")
                .build()

            return db.photographersDao().getAllPhotographers()
        }

    }


}