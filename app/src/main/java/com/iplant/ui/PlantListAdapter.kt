package com.iplant.ui

import android.content.Context
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iplant.R
import com.iplant.data.Plant
import com.iplant.databinding.RecyclerviewItemBinding
import com.skydoves.transformationlayout.TransformationLayout
import java.time.format.DateTimeFormatter
import java.util.*

class PlantListAdapter(
    private val clickListener: PlantClickListener,
    private val context: Context
) :
    ListAdapter<Plant, PlantListAdapter.PlantViewHolder>(PlantsComparator()) {

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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class PlantViewHolder(private val binding: RecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            binding.apply {
                cardNick.text = plant.caressing_name
                cardCommonName.text = context.getString(R.string.common_name_placeholder, plant.common_name)
                cardStatus.text = getPlantStatus(plant)
            }
            itemView.setOnClickListener {
                clickListener.onPlantClick(it as TransformationLayout, plant)
            }
        }

        private fun getPlantStatus(plant: Plant): String {
            // TODO other status messages + do nullable fields need '?'
            if (plant.death_date != null) {
                return context.getString(
                    R.string.dead, plant.death_date.format(
                        DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH)
                    )
                )
            }
            return  ""
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