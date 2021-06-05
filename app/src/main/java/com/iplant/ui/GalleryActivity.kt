package com.iplant.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.akshaykale.swipetimeline.TimelineFragment
import com.akshaykale.swipetimeline.TimelineGroupType
import com.akshaykale.swipetimeline.TimelineObject
import com.akshaykale.swipetimeline.TimelineObjectClickListener
import com.bumptech.glide.Glide
import com.iplant.PlantsApplication
import com.iplant.R
import com.iplant.data.Plant
import com.iplant.databinding.ActivityGalleryBinding
import kotlin.collections.ArrayList

class GalleryActivity : AppCompatActivity() {
    private val viewModel: PlantViewModel by viewModels {
        PlantViewModelFactory((application as PlantsApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val plant: Plant? = intent.getParcelableExtra("plant")
        if (plant == null) {
            finish()
        } else {
            viewModel.observeAllImages(plant).observe(this) { images ->
                val timeline = TimelineFragment()
                val data: ArrayList<TimelineObject> = arrayListOf()
                images.forEach {
                    data.add(object : TimelineObject {
                        override fun getTimestamp() = it.timestamp
                        override fun getTitle() = it.title
                        override fun getImageUrl() = it.imageUrl
                    })
                }
                timeline.setData(data, TimelineGroupType.DAY)
                timeline.addOnClickListener(object : TimelineObjectClickListener {
                    override fun onTimelineObjectClicked(timelineObject: TimelineObject?) {
                        timelineObject?.let {
                            Glide.with(this@GalleryActivity)
                                .load(it.imageUrl)
                                .centerCrop()
                                .into(binding.galleryPreview)
                        }
                    }

                    override fun onTimelineObjectLongClicked(timelineObject: TimelineObject?) {
                        return
                    }

                })
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.gallery_timeline, timeline)
                transaction.commit()
            }
        }
    }
}