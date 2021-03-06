package com.iplant.ui

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.iplant.MediaAPI.JSONParser
import com.iplant.PlantsApplication
import com.iplant.R
import com.iplant.data.Plant
import com.iplant.databinding.ActivityMainBinding
import com.iplant.notification.NotificationsMaker
import java.io.File
import java.time.LocalDate
import java.time.Period.between
import java.util.*


class MainActivity : AppCompatActivity(), PlantListAdapter.PlantClickListener,
    PopupMenu.OnMenuItemClickListener {

    private val plantViewModel: PlantViewModel by viewModels {
        PlantViewModelFactory((application as PlantsApplication).repository)
    }

    val jsp = JSONParser(this)
    private val importPlant =
        registerForActivityResult(JSONParser.ReadDocument()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    val plantData =  jsp.readFromJSON(uri)
                    plantData.let {
                        if (it != null) {
                            plantViewModel.insertData(it)
                        }
                    }

                }
            }
        }

    private val addPlant = registerForActivityResult(AddingContract()) {
        it?.let {
            plantViewModel.insert(it)

            val alarmManager: AlarmManager =
                getSystemService(ALARM_SERVICE) as AlarmManager

            NotificationsMaker.makeWateringNotification(this, it, alarmManager)
            NotificationsMaker.makeFertilizingNotification(this, it, alarmManager)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PlantListAdapter(this, baseContext)

        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            fab.setOnClickListener {
                addPlant.launch(null)
            }

            val popupMenu = PopupMenu(this@MainActivity, editButton).apply {
                setOnMenuItemClickListener(this@MainActivity)
                inflate(R.menu.menu_main)
            }

            editButton.setOnClickListener {
                popupMenu.show()
            }
        }

        plantViewModel.allPlants.observe(this, Observer { plants ->
            plants?.let {
                adapter.submitList(it)
            }
        })

        val PERMISSION_ALL = 1
        val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA
        )

        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onPlantClick(plant: Plant) {
        val intent = Intent(this, InfoActivity::class.java)
        intent.putExtra("plant", plant)
        startActivity(intent)
    }

    override suspend fun getLastPhoto(plant: Plant): File? {
        return plantViewModel.getLastImage(plant)?.getFile(this)
    }

    override suspend fun checkIfNeedsWatering(plant: Plant): Boolean {
        val watering = plantViewModel.getLastWatering(plant)
        return if (watering != null) {
            between(watering.watering_date, LocalDate.now()).days >= plant.watering_period
        } else {
            between(plant.adding_date, LocalDate.now()).days >= plant.watering_period
        }
    }

    override suspend fun checkIfNeedsFertilizing(plant: Plant): Boolean {
        val fertilizing = plantViewModel.getLastFertilizing(plant)
        return if (fertilizing != null) {
            between(fertilizing.fertilizing_date, LocalDate.now()).days >= plant.fertilizing_period
        } else {
            between(plant.adding_date, LocalDate.now()).days >= plant.fertilizing_period
        }
    }

    internal class AddingContract : ActivityResultContract<Nothing?, Plant>() {
        override fun createIntent(context: Context, input: Nothing?): Intent =
            Intent(context, AddEditPlantActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?): Plant? =
            intent?.getParcelableExtra("plant")
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_hide -> {
                item.isChecked = !item.isChecked
                plantViewModel.hideDead.value = item.isChecked
                true
            }
            R.id.menu_import -> {
                importPlant.launch(null)
                true
            }
            else -> false
        }
    }


}