package com.iplant.ui

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.ViewSwitcher.ViewFactory
import androidx.appcompat.app.AppCompatActivity
import com.iplant.R


class LifeAnimationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_animation)

        val inAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.left_to_right_in)
        val outAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.left_to_right_out)

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

        var animationCounter = 1
        val imageSwitcherHandler = Handler(Looper.getMainLooper())
        imageSwitcherHandler.post(object : Runnable {
            override fun run() {
                when (animationCounter++) {
                    1 -> imageSwitcher.setImageResource(R.drawable.life_animation_top)
                    2 -> imageSwitcher.setImageResource(R.drawable.life_animation_bottom)
                }
                animationCounter %= 3
                if (animationCounter == 0) animationCounter = 1
                imageSwitcherHandler.postDelayed(this, 3000)
            }
        })
    }
}