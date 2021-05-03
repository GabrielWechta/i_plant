package com.iplant.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.iplant.databinding.ActivityEditPlantBinding
import com.skydoves.transformationlayout.TransformationActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class AddEditPlantActivity : AppCompatActivity() {

    private lateinit var editWordView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date of death")
                .build()

        binding.apply {
            editWordView = editPlant
            hasDiedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
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
                if (TextUtils.isEmpty(editWordView.text)) {
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                } else {
                    val word = editWordView.text.toString()
                    replyIntent.putExtra(EXTRA_REPLY, word)
                    setResult(Activity.RESULT_OK, replyIntent)
                }
                finish()
            }
        }

    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}