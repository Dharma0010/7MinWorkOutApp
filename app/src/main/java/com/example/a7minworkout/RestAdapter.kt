package com.example.a7minworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minworkout.databinding.RestItemBinding



class RestAdapter(val items: ArrayList<ExerciseModel>): RecyclerView.Adapter<RestAdapter.MainViewHolder>() {

    class MainViewHolder(binding: RestItemBinding): RecyclerView.ViewHolder(binding.root) {
        val tvNoExercise = binding.tvNoExercise
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(RestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val level = items[position]
        holder.tvNoExercise.text = level.getId().toString()

        when {
            level.getSelected() -> {
                holder.tvNoExercise.background = ContextCompat.getDrawable(holder.itemView.context,
                    R.drawable.item_circular_background_color_white)
                holder.tvNoExercise.setTextColor(Color.parseColor("#212121"))
            }
            level.getCompleted() -> {
                holder.tvNoExercise.background = ContextCompat.getDrawable(holder.itemView.context,
                    R.drawable.item_circular_color_accent_background)
                holder.tvNoExercise.setTextColor(Color.parseColor("#FFFFFF"))
            }
            else -> {
                holder.tvNoExercise.background = ContextCompat.getDrawable(holder.itemView.context,
                    R.drawable.item_circular_background_color_gray)
                holder.tvNoExercise.setTextColor(Color.parseColor("#212121"))
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}