package com.jayesh.finalyearproject.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.compose.animation.core.snap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.activity.MainActivity
import com.jayesh.finalyearproject.adapter.FetchUserAdapter
import com.jayesh.finalyearproject.data.User
import com.jayesh.finalyearproject.model.Users
import java.util.ArrayList


class HomeFragment : Fragment() {
    lateinit var rvMain: RecyclerView
    lateinit var dbref: DatabaseReference
    lateinit var dbref1: DatabaseReference
    lateinit var usersArrayList: ArrayList<User>
    lateinit var rlProgressBar: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var homeAdapter: FetchUserAdapter

    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        rvMain = view.findViewById(R.id.rvMain)
        rlProgressBar = view.findViewById(R.id.rlProgressBar)
        rvMain.layoutManager = LinearLayoutManager(activity)
        rvMain.setHasFixedSize(true)
        auth = FirebaseAuth.getInstance()


        rlProgressBar.visibility = View.VISIBLE
        usersArrayList = arrayListOf<User>()
        getUserData()

        return view
    }

    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("users")

        /*getting the snapshot of current user
        start*/
        var exist: DataSnapshot? = null
        dbref1 = FirebaseDatabase.getInstance().getReference("users")
            .child(auth.currentUser?.uid.toString())
        dbref1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    exist = snapshot
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        /*ends*/

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        /*reason: to skip current user from users list*/
                        if (userSnapshot.key == exist?.key) {
                            continue
                        }

                        val user = userSnapshot.getValue(User::class.java)
                        usersArrayList.add(user!!)
                    }
                    //homeAdapter.notifyDataSetChanged()

                    homeAdapter = FetchUserAdapter(activity!!, usersArrayList)
                    rvMain.adapter = homeAdapter
                    rlProgressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    //setting up notification icon on toolbar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itmNotification -> Toast.makeText(
                activity,
                "notification clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
        return super.onOptionsItemSelected(item)
    }


}