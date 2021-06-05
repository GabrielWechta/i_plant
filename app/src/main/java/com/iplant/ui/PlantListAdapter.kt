package com.iplant.ui

import android.content.Context
import android.os.DeadObjectException
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iplant.PlantsApplication
import com.iplant.R
import com.iplant.data.Plant
import com.iplant.data.fertilizing.Fertilizing
import com.iplant.data.images.PlantImage
import com.iplant.data.watering.Watering
import com.iplant.databinding.RecyclerviewItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.*

class PlantListAdapter(
    private val clickListener: PlantClickListener,
    private val context: Context
) :
    ListAdapter<Plant, PlantListAdapter.PlantViewHolder>(PlantsComparator()) {

    interface PlantClickListener {
        fun onPlantClick(plant: Plant)
        suspend fun getLastPhoto(plant: Plant): PlantImage?
        suspend fun checkIfNeedsWatering(plant: Plant): Boolean
        suspend fun checkIfNeedsFertilizing(plant: Plant): Boolean
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
        lateinit var heldPlant: Plant
        var status: Status = Status.OK
        fun bind(plant: Plant) {
            heldPlant = plant
            binding.apply {
                cardNick.text = plant.caressing_name
                cardCommonName.text =
                    context.getString(R.string.common_name_placeholder, plant.common_name)
                var photo: PlantImage? = null
                val job = GlobalScope.launch {
                    val needsWatering = clickListener.checkIfNeedsWatering(plant)
                    val needsFertilizing = clickListener.checkIfNeedsFertilizing(plant)
                    status = status.getStatus(plant, needsWatering, needsFertilizing)
                    updateStatusMessage()
                    photo = clickListener.getLastPhoto(plant)
                    Handler(context.mainLooper).post {
                        photo?.let {
                            Glide.with(context).load(it.image_name).centerCrop().into(cardImage)
                        }
                    }
                }
            }
            itemView.setOnClickListener {
                clickListener.onPlantClick(plant)
            }
        }


        private fun updateStatusMessage() {
            binding.cardStatus.text = when (status) {
                Status.OK -> ""
                Status.DEAD -> context.getString(
                    R.string.dead, heldPlant.death_date!!.format(
                        DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH)
                    )
                )
                Status.NEEDS_WATERING -> "Needs watering!"
                Status.NEEDS_FERTILIZING -> "Needs fertilizing!"
                Status.NEEDS_BOTH -> "Needs watering and fertilizing!"
            }
        }
    }

    enum class Status {
        OK,
        DEAD,
        NEEDS_WATERING,
        NEEDS_FERTILIZING,
        NEEDS_BOTH;

        public fun getStatus(plant: Plant, needsWatering: Boolean, needsFertilizing: Boolean): Status {
            return if (plant.death_date != null) DEAD
            else if (needsWatering && needsFertilizing) NEEDS_BOTH
            else if (needsWatering) NEEDS_WATERING
            else if (needsFertilizing) NEEDS_FERTILIZING
            else OK
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