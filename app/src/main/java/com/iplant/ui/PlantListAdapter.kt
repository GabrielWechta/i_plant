package com.iplant.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iplant.data.Plant
import com.iplant.databinding.RecyclerviewItemBinding
import com.skydoves.transformationlayout.TransformationLayout

class PlantListAdapter(private val clickListener: PlantClickListener) :
    ListAdapter<Plant, PlantListAdapter.PlantViewHolder>(
        PlantsComparator()
    ) {
    interface PlantClickListener {
        fun onPlantClick(plantItem: TransformationLayout, plant: Plant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val itemBinding =
            RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class PlantViewHolder(private val binding: RecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            binding.apply {
                textView.text = plant.caressing_name
            }
            itemView.setOnClickListener {
                clickListener.onPlantClick(it as TransformationLayout, plant)
            }
        }
    }

    class PlantsComparator : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem.id == newItem.id
        }
    }
}