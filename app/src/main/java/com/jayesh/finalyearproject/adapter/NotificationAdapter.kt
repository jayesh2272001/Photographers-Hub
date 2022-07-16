package com.jayesh.finalyearproject.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.activity.NotificationActivity
import com.jayesh.finalyearproject.model.ConfirmedDate
import com.jayesh.finalyearproject.model.Dates
import java.time.LocalDate

class NotificationAdapter(val context: Context, private val dateList: ArrayList<Dates>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvAvailable: TextView = view.findViewById(R.id.tvAvailable)
        val cvLayout: CardView = view.findViewById(R.id.cvLayout)
        val btnDeleteDate: TextView = view.findViewById(R.id.btnDeleteDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val date = dateList[position]
        val dateWithouty = date.date?.dropLast(1)
        holder.tvDate.text = "Is $dateWithouty available for booking?"
        holder.tvName.text = "Ask by: ${date.senderName?.capitalize()}"
        holder.tvAvailable.text = "click here to change availability of $dateWithouty"


        holder.cvLayout.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm Dates")
            builder.setMessage("Do you want to take order on ${date.date}")

            builder.setPositiveButton("Yes") { _, _ ->
                //positive action here

                FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("dates")
                    .addValueEventListener(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (postSnapshot in snapshot.children) {
//                                Log.i("Notification Adapter", postSnapshot.key.toString())
                                //getting actual dates
                                FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                    .child("dates")
                                    .child(postSnapshot.key.toString())
                                    .addValueEventListener(object : ValueEventListener {
                                        @RequiresApi(Build.VERSION_CODES.O)
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (postSnapshot2 in snapshot.children) {
                                                val allVials =
                                                    postSnapshot2.getValue(Dates::class.java)

                                                val date2 = allVials?.date
                                                val test = Dates(
                                                    date2!!,
                                                    allVials.availability!!,
                                                    allVials.name!!,
                                                    allVials.senderName!!
                                                )
                                                Log.i("Notification Adapter", test.date.toString())

                                                if (date.date == test.date) {
                                                    Log.i(
                                                        "Notification Adapter",
                                                        "we are going to append date as ${test.date}y"
                                                    )
                                                    //removing date in which yes not present
                                                    val dbref = FirebaseDatabase.getInstance()
                                                        .getReference("users")
                                                    val query =
                                                        dbref.child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                                            .child("dates")
                                                            .child(postSnapshot.key.toString())
                                                            .orderByChild("date")
                                                            .equalTo(test.date)
                                                            .addListenerForSingleValueEvent(
                                                                object : ValueEventListener {
                                                                    val userToken = postSnapshot.key
                                                                    val currId =
                                                                        FirebaseAuth.getInstance().currentUser?.uid.toString()
                                                                    val currTime =
                                                                        LocalDate.now().toString()

                                                                    val newId = userToken?.replace(
                                                                        FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                                                        ""
                                                                    )

                                                                    override fun onDataChange(
                                                                        snapshot: DataSnapshot
                                                                    ) {
                                                                        for (dataSnapshot in snapshot.children) {
                                                                            dataSnapshot.ref.removeValue()
                                                                            updateConfirmDates(
                                                                                date.date!!,
                                                                                currId,
                                                                                currTime,
                                                                                newId
                                                                            )
                                                                            context.startActivity(
                                                                                Intent(
                                                                                    context,
                                                                                    NotificationActivity::class.java
                                                                                )
                                                                            )
                                                                        }
                                                                    }

                                                                    override fun onCancelled(error: DatabaseError) {
                                                                        Log.e(
                                                                            "Error",
                                                                            error.toString()
                                                                        )
                                                                    }

                                                                }
                                                            )
                                                }
                                            }

                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("Error", error.toString())
                                        }
                                    })


                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Error", error.toString())
                        }
                    })

            }
            builder.setNegativeButton("No") { _, _ ->
                //negative action here
                Toast.makeText(
                    context,
                    "You can change the availability status later",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }


        holder.btnDeleteDate.setOnClickListener {
            Toast.makeText(context, "Date Deleted ${date.date}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateConfirmDates(date: String, currId: String, currTime: String, newId: String?) {
        val confirmedDateObj = ConfirmedDate(
            date,
            currId,
            currTime
        )
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(newId.toString())
            .child("confirmed-dates")
            .push()
            .setValue(confirmedDateObj)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Successfully appended date with yes",
                    Toast.LENGTH_LONG
                ).show()


                TODO("append other users values as well")
            }

    }

    override fun getItemCount(): Int {
        return dateList.size
    }
}