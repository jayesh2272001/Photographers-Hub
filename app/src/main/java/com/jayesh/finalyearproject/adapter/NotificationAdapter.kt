package com.jayesh.finalyearproject.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.model.Dates

class NotificationAdapter(val context: Context, private val dateList: ArrayList<Dates>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNotificationItem: TextView = view.findViewById(R.id.tvNotificationItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val date = dateList[position]
        holder.tvNotificationItem.text = "Is ${date.date} available for booking?"

        holder.itemView.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm Dates")
            builder.setMessage("Do you want to take order on ${date.date}")

            builder.setPositiveButton("Yes") { _, _ ->
                //positive action here
                FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("dates")
                    .removeValue().addOnSuccessListener {
                        val dateMap = mapOf<String, String>("date" to date.date + "yes")
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            .child("dates").push()
                            .updateChildren(dateMap).addOnSuccessListener {
                                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
            builder.setNegativeButton("No") { _, _ ->
                //negative action here
                FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("dates")
                    .removeValue().addOnSuccessListener {
                        val dateMap = mapOf<String, String>("date" to date.date + "no")
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            .child("dates").push()
                            .updateChildren(dateMap).addOnSuccessListener {
                                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()


        }
    }

    override fun getItemCount(): Int {
        return dateList.size
    }
}