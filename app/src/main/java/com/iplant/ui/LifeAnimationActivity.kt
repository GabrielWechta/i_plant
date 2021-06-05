package com.iplant.ui

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.ViewSwitcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.iplant.PlantsApplication
import com.iplant.R
import com.iplant.data.Plant


class LifeAnimationActivity : AppCompatActivity() {
    var index = 0
    private val viewModel: PlantViewModel by viewModels {
        PlantViewModelFactory((application as PlantsApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_animation)

        val inAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        inAnim.duration = 1500
        val outAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        outAnim.duration = 1500

        val imageSwitcher = findViewById<ImageSwitcher>(R.id.slide_trans_imageswitcher)
        imageSwitcher.inAnimation = inAnim
        imageSwitcher.outAnimation = outAnim

        imageSwitcher.setFactory {
            val myView = ImageView(applicationContext)
            myView.scaleType = ImageView.ScaleType.FIT_CENTER
            myView.layoutParams = FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
            myView
        }

        val plant: Plant? = intent.getParcelableExtra("plant")
        if (plant == null) {
            finish()
        } else {
            viewModel.observeAllImages(plant).observe(this, Observer { images ->
                val sortedImages = images.sortedBy { it.image_name }

                val handler = Handler()
                val runnable: Runnable = object : Runnable {
                    override fun run() {
                        index++
                        index %= sortedImages.size
                        imageSwitcher.setImageURI(
                            sortedImages[index].getFile(this@LifeAnimationActivity).toUri()
                        )
                        handler.postDelayed(this, 3000)
                    }
                }
                handler.postDelayed(runnable, 10)

            })
        }
    }
}