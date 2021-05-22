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
import com.applandeo.materialcalendarview.EventDay
import com.google.android.material.datepicker.*
import com.iplant.PlantsApplication
import com.iplant.R
import com.iplant.data.Plant
import com.iplant.data.PlantEvent
import com.iplant.databinding.ActivityInfoBinding
import java.time.LocalDate
import java.time.Period.between
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

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
                .setValidator(
                    CompositeDateValidator.allOf(
                        listOf(
                            DateValidatorPointBackward.now(),
                            DateValidatorPointForward.from(plant.adding_date.toEpochDay() * 24 * 3600 * 1000)
                        )
                    )
                ).build()
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setCalendarConstraints(calendarConstraints)
                    .build()

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

                viewModel.observeLastWatering(plant).observe(this@InfoActivity, Observer {
                    if (it.isNotEmpty()) {
                        val watering = it[0]
                        val diffText =
                            when (val diff =
                                between(watering.watering_date, LocalDate.now()).days) {
                                0 -> "today"
                                1 -> "yesterday"
                                else -> "$diff days ago"
                            }
                        lastWateredText.text =
                            getString(R.string.last_watered, diffText)
                    } else {
                        lastWateredText.text = getString(R.string.last_watered, "never")
                    }
                })

                viewModel.observeLastFertilizing(plant).observe(this@InfoActivity, Observer {
                    if (it.isNotEmpty()) {
                        val fertilizing = it[0]
                        val diffText = when (val diff =
                            between(fertilizing.fertilizing_date, LocalDate.now()).days) {
                            0 -> "today"
                            1 -> "yesterday"
                            else -> "$diff days ago"
                        }
                        lastFertilizedText.text =
                            getString(R.string.last_fertilized, diffText)
                    } else {
                        lastFertilizedText.text = getString(R.string.last_fertilized, "never")
                    }
                })

                LocalDate.now().apply {
                    val maxDate = Calendar.getInstance()
                    maxDate.set(year, monthValue - 1, dayOfMonth)
                    historyCalendar.setMaximumDate(maxDate)
                }

                plant.adding_date.minusDays(1).apply {
                    val minDate = Calendar.getInstance()
                    minDate.set(year, monthValue - 1, dayOfMonth)
                    historyCalendar.setMinimumDate(minDate)
                }

                viewModel.getAllEvents(plant).observe(this@InfoActivity, Observer { plantEvents ->
                    val eventMap: HashMap<LocalDate, PlantEvent> = hashMapOf()
                    plantEvents.forEach {
                        val date = it.getEventDate()
                        val icon = it.getIcon()
                        if (eventMap.containsKey(date) && eventMap[date]!!.getIcon() != icon) {
                            eventMap[date] = object : PlantEvent {
                                override fun getEventDate(): LocalDate = date
                                override fun getIcon(): Int = R.drawable.ic_both_events
                            }
                        } else {
                            eventMap[date] = it
                        }
                    }

                    val events: ArrayList<EventDay> = ArrayList(eventMap.size)
                    var i = 0
                    eventMap.values.forEach {
                        val day: Calendar = Calendar.getInstance()
                        it.getEventDate().apply {
                            day.set(year, monthValue - 1, dayOfMonth)
                        }
                        events.add(i++, EventDay(day, it.getIcon()))
                    }
                    historyCalendar.setEvents(events)
                })

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