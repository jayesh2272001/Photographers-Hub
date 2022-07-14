package com.jayesh.finalyearproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.capitalize
import androidx.recyclerview.widget.RecyclerView
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.model.PreviousDates

class PreviousDatesAdapter(
    val context: Context,
    private val previousDateDataList: ArrayList<PreviousDates>
) :
    RecyclerView.Adapter<PreviousDatesAdapter.PreviousViewHolder>() {

    class PreviousViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainLayout: CardView = view.findViewById(R.id.cvPreviousDatesItem)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvAvailable: TextView = view.findViewById(R.id.tvAvailable)
        val btnDeleteDate: TextView = view.findViewById(R.id.btnDeleteDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviousViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.previous_dates_item, parent, false)
        return PreviousViewHolder((view))
    }

    override fun onBindViewHolder(holder: PreviousViewHolder, position: Int) {
        val previousDate = previousDateDataList[position]
        holder.tvDate.text = "Date: ${previousDate.date}"
        holder.tvName.text = "Requested by: ${previousDate.name?.capitalize()}"

        if (previousDate.availability == false) {
            holder.tvAvailable.text = "Availability: Not Confirmed"
        } else {
            holder.tvAvailable.text = "Availability: YES"
        }




        //delete date function
        holder.btnDeleteDate.setOnClickListener {
            Toast.makeText(context, "Delete ${previousDate.date}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return previousDateDataList.size
    }
}