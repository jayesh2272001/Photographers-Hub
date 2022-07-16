package com.jayesh.finalyearproject.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.substring
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.activity.MainActivity
import com.jayesh.finalyearproject.model.Hires

class MyHiresAdapter(val context: Context, val list: ArrayList<Hires>) :
    RecyclerView.Adapter<MyHiresAdapter.HireViewHolder>() {

    class HireViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cvLayout: CardView = view.findViewById(R.id.cvLayout)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvAbout: TextView = view.findViewById(R.id.tvAbout)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTransId: TextView = view.findViewById(R.id.tvTransId)
        val tvTransDate: TextView = view.findViewById(R.id.tvTransDate)
        val tvTransStatus: TextView = view.findViewById(R.id.tvTransStatus)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HireViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_hire_item, parent, false)
        return HireViewHolder(view)
    }

    override fun onBindViewHolder(holder: HireViewHolder, position: Int) {
        val data = list[position]
        holder.tvName.text = "Name: ${data.uname?.capitalize()}"
        holder.tvDate.text = "Date: ${data.uDate}"
        holder.tvTransId.text = "Transaction id: ${data.transactionId}"
        holder.tvTransDate.text = "Transaction date: ${data.payDate?.substring(0..9)}"
        holder.tvTransStatus.text = "Transaction Status: ${data.transactionStatus}"
        FirebaseDatabase.getInstance().getReference("users")
            .child(data.uid.toString())
            .child("about")
            .get()
            .addOnSuccessListener {
                holder.tvAbout.text = it.value.toString().capitalize()
            }

        holder.cvLayout.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Do you want to delete ${data.uDate.toString()}")
            builder.setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->
                    FirebaseDatabase.getInstance().getReference("users")
                        .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .child("my-hires")
                        .orderByChild("transactionId")
                        .equalTo(data.transactionId.toString())
                        .addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (dataSnapshot in snapshot.children) {
                                        dataSnapshot.ref.removeValue()
                                        context.startActivity(
                                            Intent(
                                                context,
                                                MainActivity::class.java
                                            )
                                        )
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            }
                        )

                })

            builder.setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
            builder.create()
            builder.show()


        }
    }


    override fun getItemCount(): Int {
        return list.size
    }
}