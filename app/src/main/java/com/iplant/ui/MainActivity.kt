package com.iplant.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.iplant.PlantsApplication
import com.iplant.R
import com.iplant.data.Plant
import com.iplant.databinding.ActivityMainBinding
import com.skydoves.transformationlayout.TransformationCompat
import com.skydoves.transformationlayout.TransformationLayout
import com.skydoves.transformationlayout.onTransformationStartContainer
import java.time.LocalDateTime

class MainActivity : AppCompatActivity(), PlantListAdapter.PlantClickListener {

    private val newPlantActivityRequestCode = 1
    private val plantViewModel: PlantViewModel by viewModels {
        PlantViewModelFactory((application as PlantsApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onTransformationStartContainer()
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PlantListAdapter(this, baseContext)

        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            fab.setOnClickListener {
                val intent = Intent(this@MainActivity, AddEditPlantActivity::class.java)
                TransformationCompat.startActivityForResult(
                    transformationLayout,
                    intent,
                    newPlantActivityRequestCode
                )
            }
        }

        plantViewModel.allPlants.observe(this, Observer { plants ->
            plants?.let { adapter.submitList(it) }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newPlantActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(AddEditPlantActivity.EXTRA_REPLY)?.let {
                val plantName = it
                val plant = Plant(
                    plantName,
                    "dfg",
                    "hjk",
                    LocalDateTime.now(),
                    "qwe",
                    "rty",
                    7,
                    14,
                    LocalDateTime.now(),
                    "tyu"
                )
                plantViewModel.insert(plant)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onPlantClick(plantItem: TransformationLayout, plant: Plant) {
        InfoActivity.startActivity(this, plantItem, plant)
    }
}