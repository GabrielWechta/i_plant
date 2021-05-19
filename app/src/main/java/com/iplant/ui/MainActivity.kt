package com.iplant.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.iplant.PlantsApplication
import com.iplant.R
import com.iplant.data.Plant
import com.iplant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), PlantListAdapter.PlantClickListener {

    private val newPlantActivityRequestCode = 1
    private val plantViewModel: PlantViewModel by viewModels {
        PlantViewModelFactory((application as PlantsApplication).repository)
    }
    private val addPlant = registerForActivityResult(AddingContract()) {
        it?.let {
            plantViewModel.insert(it)
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
        }

        plantViewModel.allPlants.observe(this, Observer { plants ->
            plants?.let { adapter.submitList(it) }
        })
    }

    override fun onPlantClick(plant: Plant) {
        val intent = Intent(this, InfoActivity::class.java)
        intent.putExtra("plant", plant)
        startActivity(intent)
    }

    internal class AddingContract : ActivityResultContract<Nothing?, Plant>() {
        override fun createIntent(context: Context, input: Nothing?): Intent =
            Intent(context, AddEditPlantActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?): Plant? =
            intent?.getParcelableExtra("plant")
    }
}