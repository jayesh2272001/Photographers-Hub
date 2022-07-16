package com.jayesh.finalyearproject.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.MyHiresAdapter
import com.jayesh.finalyearproject.model.Hires


class MyHiresFragment : Fragment() {
    private lateinit var rvMyHires: RecyclerView
    private lateinit var hireList: ArrayList<Hires>
    private lateinit var myHireAdapter: MyHiresAdapter
    private lateinit var loteeProgressBar: RelativeLayout
    private lateinit var rlNoDataFound: RelativeLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_hires, container, false)
        rvMyHires = view.findViewById(R.id.rvMyHires)
        loteeProgressBar = view.findViewById(R.id.loteeProgressBar)
        rlNoDataFound = view.findViewById(R.id.rlNoDataFound)
        hireList = ArrayList()

        loteeProgressBar.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child("my-hires")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val test = postSnapshot.getValue(Hires::class.java)
                        val obj = Hires(
                            test?.uname!!,
                            test.uid!!,
                            test.uDate!!,
                            test.senderUpi!!,
                            test.transactionStatus!!,
                            test.transactionId!!,
                            test.transactionRefId!!,
                            test.amount!!,
                            test.userContact!!,
                            test.payDate!!

                        )

                        hireList.add(obj)

                    }

                    Log.i("list", hireList.toString())
                    if (hireList.isNotEmpty()) {
                        myHireAdapter = MyHiresAdapter(activity as Context, hireList)
                        rvMyHires.layoutManager = LinearLayoutManager(activity)
                        rvMyHires.adapter = myHireAdapter
                        loteeProgressBar.visibility = View.GONE
                    } else {
                        loteeProgressBar.visibility = View.GONE
                        rlNoDataFound.visibility = View.VISIBLE

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DB Error", error.toString())
                }

            })

        return view

    }

}