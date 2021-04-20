package com.iplant

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private val newPlantActivityRequestCode = 1
    private val plantViewModel: PlantViewModel by viewModels {
        PlantViewModelFactory((application as PlantsApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = PlantListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        plantViewModel.allPlants.observe(this, Observer { plants ->
            plants?.let { adapter.submitList(it) }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewPlantActivity::class.java)
            startActivityForResult(intent, newPlantActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newPlantActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewPlantActivity.EXTRA_REPLY)?.let {
                val plantName = it
                val plant = Plant(
                    0,
                    plantName,
                    "dfg",
                    "hjk",
                    LocalDateTime.now(),
                    "qwe",
                    "rty",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
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
}