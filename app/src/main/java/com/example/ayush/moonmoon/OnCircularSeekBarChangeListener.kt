package com.example.ayush.moonmoon

/**
 * Created by ayush on 10/5/17.
 */
interface OnCircularSeekBarChangeListener {

    fun onProgressChanged(circularSeeker : RoundSeeker, progress : Int, fromUser : Boolean)

    fun onStopTrackingTouch(circularSeeker: RoundSeeker)

    fun onStartTrackingTouch(circularSeeker: RoundSeeker)
}