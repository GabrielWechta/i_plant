package com.iplant.ui

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.EventDay
import com.broooapps.graphview.CurveGraphConfig
import com.broooapps.graphview.models.GraphData
import com.broooapps.graphview.models.PointMap
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.*
import com.iplant.MediaAPI.CameraActivity
import com.iplant.MediaAPI.DataModel
import com.iplant.MediaAPI.JSONParser
import com.iplant.MediaAPI.TwitterToken
import com.iplant.PlantsApplication
import com.iplant.R
import com.iplant.data.Plant
import com.iplant.data.PlantEvent
import com.iplant.data.fertilizing.Fertilizing
import com.iplant.data.images.PlantImage
import com.iplant.data.watering.Watering
import com.iplant.databinding.ActivityInfoBinding
import com.iplant.notification.NotificationsMaker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period.between
import java.time.format.DateTimeFormatter
import java.util.*


class InfoActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    private val viewModel: PlantViewModel by viewModels {
        PlantViewModelFactory((application as PlantsApplication).repository)
    }

    var plant: Plant? = null
    var lastFert: Fertilizing? = null
    var lastWart: Watering? = null
    var lastImg: PlantImage? = null

    lateinit var binding: ActivityInfoBinding
    val jsp = JSONParser(this)
    private val exportPlant =
        registerForActivityResult(JSONParser.CreateDocument()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    jsp.writeToJSON(
                        uri, DataModel(
                            plant,
                            lastWart,
                            lastFert
                        )
                    )
                }
            }
        }
    private val editPlant = registerForActivityResult(EditContract()) {
        it?.let {
            plant = it
            bindPlantData(it)
            viewModel.update(it)
        }
    }
    private val addPicture = registerForActivityResult(CameraActivity.CreatePhoto())
    {
        if (it.resultCode == Activity.RESULT_OK) {
            val img = it.data?.getStringExtra("imageName")
            plant?.let { it1 ->
                if (img != null) {
                    viewModel.addImage(it1, LocalDateTime.now(), img)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        plant = intent.getParcelableExtra("plant")

        binding.photoButton.setOnClickListener {
            addPicture.launch(lastImg?.image_name)
        }
        binding.editButton.setOnClickListener {
            PopupMenu(this, it).apply {
                setOnMenuItemClickListener(this@InfoActivity)
                inflate(R.menu.menu_plant)
                show()
            }
        }
        if (plant == null) {
            val plantId = intent.getLongExtra("plant_id", -1)
            if (plantId < 0) finish()
            GlobalScope.launch {
                plant = viewModel.getPlantById(plantId)
                Handler(mainLooper).post {
                    bindPlantData(plant)
                }
            }
        }
        else {
            bindPlantData(plant)
        }
    }

    private fun bindPlantData(plant: Plant?) {
        plant?.let {
            viewModel.observeLastImage(it).observe(this, { images ->
                if (images.isNotEmpty()) {
                    val photo = images[0].getFile(this)
                    lastImg = images[0]

                    Glide.with(this)
                        .load(photo)
                        .centerCrop()
                        .into(binding.imageView)
                }
            })
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
                        datePicker.clearOnPositiveButtonClickListeners()
                        val date = LocalDate.ofEpochDay(it / (1000 * 3600 * 24))
                        viewModel.addWateringNote(plant, date)
                        NotificationsMaker.makeWateringNotification(
                            this@InfoActivity,
                            plant,
                            getSystemService(ALARM_SERVICE) as AlarmManager
                        )
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
                        datePicker.clearOnPositiveButtonClickListeners()
                        val date = LocalDate.ofEpochDay(it / (1000 * 3600 * 24))
                        viewModel.addFertilizingNote(plant, date)
                        NotificationsMaker.makeFertilizingNotification(
                            this@InfoActivity,
                            plant,
                            getSystemService(ALARM_SERVICE) as AlarmManager
                        )
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

                viewModel.observeLastWatering(plant).observe(this@InfoActivity, {
                    if (it.isNotEmpty()) {
                        lastWart = it[0]
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

                viewModel.observeLastFertilizing(plant).observe(this@InfoActivity, {
                    if (it.isNotEmpty()) {
                        lastFert = it[0]
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

                viewModel.getAllEvents(plant).observe(this@InfoActivity, { plantEvents ->
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

                viewModel.getAllFertilizing(plant)
                    .observe(this@InfoActivity, { plantFertilizing ->
                        val fertilizingDates = arrayListOf<LocalDate>()
                        var maxFertilizingDistance = 0
                        plantFertilizing.forEach {
                            fertilizingDates.add(it.fertilizing_date)
                        }
                        fertilizingDates.sort()
                        val fertilizingPointMap = PointMap()
                        for (i in 1 until fertilizingDates.size) {
                            val diff = between(fertilizingDates[i - 1], fertilizingDates[i]).days
                            if (diff > maxFertilizingDistance) {
                                maxFertilizingDistance = diff
                            }
                            fertilizingPointMap.addPoint(i - 1, diff)
                        }
                        // adding today
                        if (fertilizingDates.size > 0) {
                            fertilizingPointMap.addPoint(
                                fertilizingDates.size,
                                between(
                                    fertilizingDates[fertilizingDates.size - 1],
                                    LocalDate.now()
                                ).days
                            )
                        }
                        val fertilizingGD = GraphData.builder(this@InfoActivity)
                            .setPointMap(fertilizingPointMap)
                            .setGraphStroke(R.color.creme)
                            .setGraphGradient(
                                R.color.drieish,
                                R.color.fertilizish
                            )

                            .setStraightLine(false) // true for straight line; false for curved line graph
                            .setPointRadius(0) // set point radius
                            .setPointColor(R.color.waterish) // set point color
                            .animateLine(true) // Trigger animation for the particular graph line!
                            .build()

                        val fertilizingGraphView = binding.fertilizingCurveView

                        fertilizingGraphView.configure(
                            CurveGraphConfig.Builder(this@InfoActivity)
                                .setAxisColor(R.color.creme) // Set number of values to be displayed in X ax
                                .setHorizontalGuideline(2)
                                .setGuidelineColor(R.color.creme) // Set color of the visible guidelines.
                                .setNoDataMsg(" No Data ") // Message when no data is provided to the view.
                                .setxAxisScaleTextColor(R.color.creme) // Set X axis scale text color.
                                .setyAxisScaleTextColor(R.color.white) // Set Y axis scale text color
                                .setAnimationDuration(4000) // Set Animation Duration
                                .build()
                        )
                        Handler().postDelayed({
                            fertilizingGraphView.setData(
                                fertilizingDates.size,
                                maxFertilizingDistance,
                                fertilizingGD
                            )
                        }, 350)
                    })

                viewModel.getAllWatering(plant)
                    .observe(this@InfoActivity, { plantWatering ->
                        val wateringDates = arrayListOf<LocalDate>()
                        var maxWateringDistance = 0
                        plantWatering.forEach {
                            wateringDates.add(it.watering_date)
                        }
                        wateringDates.sort()

                        val wateringPointMap = PointMap()
                        for (i in 1 until wateringDates.size) {
                            val diff = between(wateringDates[i - 1], wateringDates[i]).days
                            if (diff > maxWateringDistance) {
                                maxWateringDistance = diff
                            }
                            wateringPointMap.addPoint(
                                i - 1,
                                diff
                            )
                        }
                        // adding today
                        if (wateringDates.size > 0) {
                            wateringPointMap.addPoint(
                                wateringDates.size,
                                between(wateringDates[wateringDates.size - 1], LocalDate.now()).days
                            )
                        }
                        val wateringGD = GraphData.builder(this@InfoActivity)
                            .setPointMap(wateringPointMap)
                            .setGraphStroke(R.color.creme)
                            .setGraphGradient(
                                R.color.drieish,
                                R.color.waterish
                            )
                            .setStraightLine(false)
                            .setPointRadius(0)
                            .setPointColor(R.color.waterish)
                            .animateLine(true)
                            .build()

                        val wateringGraphView = binding.wateringCurveView

                        wateringGraphView.configure(
                            CurveGraphConfig.Builder(this@InfoActivity)
                                .setAxisColor(R.color.creme)
                                .setHorizontalGuideline(2)
                                .setGuidelineColor(R.color.creme)
                                .setNoDataMsg(" No Data ")
                                .setxAxisScaleTextColor(R.color.creme)
                                .setyAxisScaleTextColor(R.color.white)
                                .setAnimationDuration(2500)
                                .build()
                        )

                        Handler().postDelayed(Runnable {
                            wateringGraphView.setData(
                                wateringDates.size,
                                maxWateringDistance,
                                wateringGD
                            )
                        }, 250)
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
                val intent = Intent(this, GalleryActivity::class.java)
                intent.putExtra("plant", plant)
                startActivity(intent)
                true
            }
            R.id.menu_lifeAnimation -> {
                val lifeIntent = Intent(this, LifeAnimationActivity::class.java)
                lifeIntent.putExtra("plant", plant)
                startActivity(lifeIntent)
                true
            }
            R.id.menu_export -> {
                if (plant != null) {
                    exportPlant.launch(jsp.generateName(plant!!))
                }
                true
            }
            R.id.menu_share -> {
                if (plant != null) {
                    when (TwitterToken.tryTweet(plant!!, lastImg?.getFile(this), this)) {
                        TwitterToken.tweet.TWEETED ->
                            Toast.makeText(this, "Tweeted!", Toast.LENGTH_SHORT)
                                .show()
                        TwitterToken.tweet.LOGGED ->
                            //  Toast.makeText(this, "Logged!", Toast.LENGTH_SHORT)
                            // .show()
                        {

                        }
                        TwitterToken.tweet.ERROR ->
                            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT)
                                .show()

                    }


                }
                true
            }
            else -> false
        }
    }
}