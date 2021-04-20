package com.iplant.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.iplant.data.Plant
import com.iplant.databinding.ActivityInfoBinding
import com.skydoves.transformationlayout.TransformationActivity
import com.skydoves.transformationlayout.TransformationCompat
import com.skydoves.transformationlayout.TransformationLayout

class InfoActivity : TransformationActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getParcelableExtra<Plant>("plant")?.let {
            binding.plantNick.text = it.caressing_name
        }
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
}