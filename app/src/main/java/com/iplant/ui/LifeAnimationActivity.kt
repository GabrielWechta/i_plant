package com.iplant.ui

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageSwitcher
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.iplant.PlantsApplication
import com.iplant.R
import com.iplant.data.Plant


class LifeAnimationActivity : AppCompatActivity() {
    private val viewModel: PlantViewModel by viewModels {
        PlantViewModelFactory((application as PlantsApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_animation)

        val inAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val outAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        val imageSwitcher = findViewById<ImageSwitcher>(R.id.slide_trans_imageswitcher)
        imageSwitcher.inAnimation = inAnim
        imageSwitcher.outAnimation = outAnim

        imageSwitcher.setFactory {
            val myView = ImageView(applicationContext)
            myView.scaleType = ImageView.ScaleType.FIT_CENTER
            myView.layoutParams = FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            myView
        }

        val plant: Plant? = intent.getParcelableExtra("plant")
        if (plant == null) {
            finish()
        } else {
            viewModel.observeAllImages(plant).observe(this, Observer { images ->
//                var animationCounter = images.size
                val imageSwitcherHandler = Handler(Looper.getMainLooper())

                imageSwitcherHandler.post(object : Runnable {
                    override fun run() {
                        images.forEach { image ->
                            Log.e("ivan", image.image_name)
                            Glide.with(this@LifeAnimationActivity).load(image.image_name)
                                .into((imageSwitcher.nextView as ImageView))
                        }

//                        when (animationCounter++) {
//                            1 -> imageSwitcher.setImageResource(R.drawable.life_animation_top)
//                            2 -> imageSwitcher.setImageResource(R.drawable.life_animation_bottom)
//                        }
//                        animationCounter %= 3
//                        if (animationCounter == 0) animationCounter = 1
                        imageSwitcherHandler.postDelayed(this, 3000)

                    }
                })

            })
        }


    }
}