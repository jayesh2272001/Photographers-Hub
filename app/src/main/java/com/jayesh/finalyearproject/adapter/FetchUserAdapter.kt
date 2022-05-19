package com.jayesh.finalyearproject.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.capitalize
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.activity.PhotographersDescActivity
import com.jayesh.finalyearproject.data.User
import com.jayesh.finalyearproject.model.Users
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class FetchUserAdapter(val context: Context, val usersList: ArrayList<User>) :
    RecyclerView.Adapter<FetchUserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cvUser: CardView = view.findViewById(R.id.cvUser)
        val civProfileImage: CircleImageView = view.findViewById(R.id.civProfileImage)
        val txtPPost: TextView = view.findViewById(R.id.txtPPost)
        val txtPFees: TextView = view.findViewById(R.id.txtPFees)

        //val btnBookPhotographer: Button = view.findViewById(R.id.btnBookPhotographer)
        val txtPLocation: TextView = view.findViewById(R.id.txtPLocation)
        val txtPName: TextView = view.findViewById(R.id.txtPName)
        val txtPExperience: TextView = view.findViewById(R.id.txtPExperience)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = usersList[position]
        holder.txtPName.text =
            user.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        Glide.with(context).load(user.profileImage).into(holder.civProfileImage).view
        //holder.txtPPost.text = user.postTitle
        holder.txtPExperience.text = user.experience + " years experience overall"
        holder.txtPLocation.text = user.location
        holder.txtPFees.text = user.uid
        var currUser = user.uid
        holder.cvUser.setOnClickListener {
            val intent = Intent(context, PhotographersDescActivity::class.java)
            Log.i("User", "Got current user $currUser")
            intent.putExtra("userVal",currUser)
            context.startActivity(intent)
            Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

}

