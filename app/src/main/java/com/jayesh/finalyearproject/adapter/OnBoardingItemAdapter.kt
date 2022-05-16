package com.jayesh.finalyearproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.model.OnBoardingItem

class OnBoardingItemAdapter(private val onBoardingItem: List<OnBoardingItem>) :
    RecyclerView.Adapter<OnBoardingItemAdapter.OnBoardingItemViewHolder>() {

    inner class OnBoardingItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageOnBoarding = view.findViewById<ImageView>(R.id.imageView)
        private val txtTitle = view.findViewById<TextView>(R.id.title)
        private val txtDescription = view.findViewById<TextView>(R.id.txtDescription)

        fun bind(onBoardingItem: OnBoardingItem) {
            imageOnBoarding.setImageResource(onBoardingItem.onBoardingImage)
            txtTitle.text = onBoardingItem.title
            txtDescription.text = onBoardingItem.description
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingItemViewHolder {
        return OnBoardingItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.onboarding_item_container,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OnBoardingItemViewHolder, position: Int) {
        holder.bind(onBoardingItem[position])
    }

    override fun getItemCount(): Int {
        return onBoardingItem.size
    }
}