package com.iplant.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.iplant.PlantsApplication
import com.iplant.R
import com.iplant.data.Plant
import com.iplant.databinding.ActivityInfoBinding
import java.time.LocalDate
import java.time.Period.between
import java.time.format.DateTimeFormatter
import java.util.*

class InfoActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    private val viewModel: PlantViewModel by viewModels {
        PlantViewModelFactory((application as PlantsApplication).repository)
    }

    var plant: Plant? = null
    lateinit var binding: ActivityInfoBinding
    private val editPlant = registerForActivityResult(EditContract()) {
        it?.let {
            plant = it
            bindPlantData(it)
            viewModel.update(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        plant = intent.getParcelableExtra("plant")
        binding.editButton.setOnClickListener {
            PopupMenu(this, it).apply {
                setOnMenuItemClickListener(this@InfoActivity)
                inflate(R.menu.menu_plant)
                show()
            }
        }
        bindPlantData(plant)
    }

    private fun bindPlantData(plant: Plant?) {
        plant?.let {
            val calendarConstraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build()
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setCalendarConstraints(calendarConstraints)
                    .build()

            viewModel.getLastWatering(plant).observe(this, Observer {
                if (it.isNotEmpty()) {
                    val watering = it[0]
                    val diffText =
                        when (val diff = between(watering.watering_date, LocalDate.now()).days) {
                            0 -> "today"
                            1 -> "yesterday"
                            else -> "$diff days ago"
                        }
                    binding.lastWateredText.text =
                        getString(R.string.last_watered, diffText)
                } else {
                    binding.lastWateredText.text = getString(R.string.last_watered, "never")
                }
            })

            viewModel.getLastFertilizing(plant).observe(this, Observer {
                if (it.isNotEmpty()) {
                    val fertilizing = it[0]
                    val diffText = when (val diff =
                        between(fertilizing.fertilizing_date, LocalDate.now()).days) {
                        0 -> "today"
                        1 -> "yesterday"
                        else -> "$diff days ago"
                    }
                    binding.lastFertilizedText.text =
                        getString(R.string.last_fertilized, diffText)
                } else {
                    binding.lastFertilizedText.text = getString(R.string.last_fertilized, "never")
                }
            })

            binding.apply {
                plantNick.text = it.caressing_name
                commonName.text = it.common_name
                buttonWater.setOnClickListener {
                    datePicker.addOnPositiveButtonClickListener {
                        val date = LocalDate.ofEpochDay(it / (1000 * 3600 * 24))
                        viewModel.addWateringNote(plant, date)
                        Toast.makeText(
                            this@InfoActivity,
                            "Added a watering note on " + date.format(DateTimeFormatter.ofPattern("dd/MM/uu")),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    datePicker.show(supportFragmentManager, "watering")
                }
                buttonFertilize.setOnClickListener {
                    datePicker.addOnPositiveButtonClickListener {
                        val date = LocalDate.ofEpochDay(it / (1000 * 3600 * 24))
                        viewModel.addFertilizingNote(plant, date)
                        Toast.makeText(
                            this@InfoActivity,
                            "Added a fertilizing note on " + date.format(
                                DateTimeFormatter.ofPattern(
                                    "dd/MM/uu"
                                )
                            ),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    datePicker.show(supportFragmentManager, "fertilizing")
                }
                if (plant.death_date != null) {
                    infoAlive.visibility = View.GONE
                    infoDead.visibility = View.VISIBLE
                    deadTitle.text = getString(
                        R.string.died_on,
                        plant.death_date.format(
                            DateTimeFormatter.ofPattern(
                                "MMMM dd, uuuu",
                                Locale.ENGLISH
                            )
                        )
                    )
                    deathCauseBody.text = plant.death_cause
                } else {
                    infoDead.visibility = View.GONE
                    infoAlive.visibility = View.VISIBLE
                }
            }
        }
    }

    internal class EditContract : ActivityResultContract<Plant, Plant>() {
        override fun createIntent(context: Context, input: Plant?): Intent =
            Intent(context, AddEditPlantActivity::class.java).putExtra("plant", input)

        override fun parseResult(resultCode: Int, intent: Intent?): Plant? =
            intent?.getParcelableExtra("plant")
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit -> {
                editPlant.launch(plant)
                true
            }
            R.id.menu_gallery -> {
                true
            }
            else -> false
        }
    }
}