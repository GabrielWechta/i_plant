package com.iplant.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import com.iplant.PlantsApplication
import com.iplant.R
import com.iplant.data.Plant
import com.iplant.databinding.ActivityInfoBinding
import com.skydoves.transformationlayout.TransformationActivity
import com.skydoves.transformationlayout.TransformationCompat
import com.skydoves.transformationlayout.TransformationLayout

class InfoActivity : TransformationActivity(), PopupMenu.OnMenuItemClickListener {
    private val viewModel: PlantViewModel by viewModels {
        PlantViewModelFactory((application as PlantsApplication).repository)
    }

    var plant: Plant? = null
    lateinit var binding: ActivityInfoBinding
    private val editPlant = registerForActivityResult(EditContract()) {
        plant = it
        bindPlantData(it)
        it?.let {
            Log.i("TUTAJ", "$plant")
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
            binding.apply {
                plantNick.text = it.caressing_name
                commonName.text = it.common_name
            }
        }
    }

    internal class EditContract : ActivityResultContract<Plant, Plant>() {
        override fun createIntent(context: Context, input: Plant?): Intent =
            Intent(context, AddEditPlantActivity::class.java).putExtra("plant", input)

        override fun parseResult(resultCode: Int, intent: Intent?): Plant? =
            intent?.getParcelableExtra("plant")
    }

    companion object {
        fun startActivity(
            context: Context,
            transformationLayout: TransformationLayout,
            plant: Plant
        ) {
            val intent = Intent(context, InfoActivity::class.java)
            intent.putExtra("plant", plant)
            TransformationCompat.startActivity(transformationLayout, intent)
        }
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