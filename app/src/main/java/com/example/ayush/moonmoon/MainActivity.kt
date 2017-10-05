package com.example.ayush.moonmoon

import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ValueAnimator.AnimatorUpdateListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val widthAnimator = ValueAnimator.ofInt(100, 1000)
        widthAnimator.addUpdateListener(this)
        widthAnimator.startDelay = 1000
        widthAnimator.setDuration(3000)
        widthAnimator.start()
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {

        var animatedValue = animation?.animatedValue as Int
        animatedTextView.layoutParams.width = animatedValue
        animatedTextView.requestLayout()

    }

}
