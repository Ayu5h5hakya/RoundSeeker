package com.example.ayush.moonmoon

/**
 * Created by ayush on 10/5/17.
 */
interface OnCircularSeekBarChangeListener {

    fun onProgressChanged(circularSeeker : RoundSeekerView, progress : Int, fromUser : Boolean)

    fun onStopTrackingTouch(circularSeeker: RoundSeekerView)

    fun onStartTrackingTouch(circularSeeker: RoundSeekerView)
}