package com.iplant.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.iplant.data.Plant
import com.iplant.databinding.ActivityEditPlantBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddEditPlantActivity : AppCompatActivity() {
    private lateinit var editWordView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val plant = intent.getParcelableExtra<Plant>("plant")

        val calendarConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date of death")
                .setCalendarConstraints(calendarConstraints)
                .build()

        binding.apply {
            editWordView = editPlant
            hasDiedSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    deadDetails.visibility = VISIBLE
                    aliveDetails.visibility = GONE
                } else {
                    deadDetails.visibility = GONE
                    aliveDetails.visibility = VISIBLE
                }
            }
            datePickerDeathText.setText(
                LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/uu"))
            )

            datePickerDeathText.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    datePicker.addOnPositiveButtonClickListener {
                        datePickerDeathText.setText(
                            LocalDate.ofEpochDay(it / (1000 * 3600 * 24))
                                .format(DateTimeFormatter.ofPattern("dd/MM/uu"))
                        )
                        datePickerDeathText.clearFocus()
                    }
                    datePicker.addOnCancelListener {
                        datePickerDeathText.clearFocus()
                    }
                    datePicker.addOnDismissListener {
                        datePickerDeathText.clearFocus()
                    }
                    datePicker.show(supportFragmentManager, "deathdate")
                }

            }

            buttonSave.setOnClickListener {
                val replyIntent = Intent()
                if (editPlant.text.isNullOrBlank()) {
                    editPlantLayout.error = "Nickname cannot be empty"
                } else {
                    var deathDate: LocalDate? = null
                    if (hasDiedSwitch.isChecked) {
                        if (datePickerDeathText.text.isNullOrBlank()) {
                            datePickerDeath.error = "Death date cannot be empty"
                            return@setOnClickListener
                        }
                        deathDate = LocalDate.parse(
                            datePickerDeathText.text,
                            DateTimeFormatter.ofPattern("dd/MM/uu")
                        )
                    }

                    val newPlant = Plant(
                        editPlant.text.toString(),
                        editCommonName.text.toString(),
                        null,
                        plant?.adding_date ?: LocalDate.now(),
                        "idk",
                        waterPicker.value,
                        fertilizerPicker.value,
                        editNotes.text.toString(),
                        deathDate,
                        editDeathCause.text.toString(),
                        id = plant?.id ?: 0
                    )
                    replyIntent.putExtra("plant", newPlant)
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }

            }

            plant?.let {
                editPlant.setText(plant.caressing_name)
                editCommonName.setText(plant.common_name)
                waterPicker.value = plant.watering_period
                fertilizerPicker.value = plant.fertilizing_period
                hasDiedSwitch.isChecked = plant.death_date != null
                datePickerDeathText.setText(plant.death_date?.format(DateTimeFormatter.ofPattern("dd/MM/uu")))
                editDeathCause.setText(plant.death_cause)
                editNotes.setText(plant.notes)
            }
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}