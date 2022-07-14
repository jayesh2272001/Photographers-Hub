package com.jayesh.finalyearproject.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.activity.ConfirmedDateInfoActivity
import com.jayesh.finalyearproject.model.ConfirmedDate

class ConfirmedDates3Adapter(
    val context: Context,
    private val confirmedDatesList: ArrayList<ConfirmedDate>
) :
    RecyclerView.Adapter<ConfirmedDates3Adapter.ConfirmedDatesViewHolder>() {

    class ConfirmedDatesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cvLayout: CardView = view.findViewById(R.id.cvLayout)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvConfirmationDate: TextView = view.findViewById(R.id.tvConfirmationDate)
        val btnContinue: TextView = view.findViewById(R.id.btnContinue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmedDatesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.confirmed_dates_3, parent, false)
        return ConfirmedDates3Adapter.ConfirmedDatesViewHolder((view))
    }

    override fun onBindViewHolder(holder: ConfirmedDatesViewHolder, position: Int) {
        val data = confirmedDatesList[position]
        holder.tvDate.text = "Date: ${data.date}"
        holder.tvConfirmationDate.text = "Confirmed on: ${data.currTime}"
        var name: String? = null
        val userId = data.currId.toString()
        FirebaseDatabase.getInstance().getReference("users")
            .child(userId)
            .child("name")
            .get()
            .addOnSuccessListener {
                holder.tvName.text = "Confirmed by: ${it.value.toString()}"
                name = it.value.toString()
            }


        holder.btnContinue.setOnClickListener {
            val intent = Intent(context, ConfirmedDateInfoActivity::class.java)
            intent.putExtra("pid", data.currId)
            intent.putExtra("p-name", name)
            intent.putExtra("c-date", data.date)
            intent.putExtra("update-date", data.currTime)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return confirmedDatesList.size
    }
}