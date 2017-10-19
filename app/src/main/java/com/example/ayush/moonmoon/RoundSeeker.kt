package com.example.ayush.moonmoon

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.layout_roundseeker.view.*

/**
 * Created by Ayush on 10/19/2017.
 */

class RoundSeeker : RelativeLayout, RoundSeekerView.OnRoundSeekerChangeListener{

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr,defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()

        roundSeeker.setOnSeekBarChangedListener(this)
    }

    fun initialize(context: Context, attrs : AttributeSet?){

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(R.layout.layout_roundseeker, this)

    }

    override fun onSeekBarChanged(newAngle: Int) {
        seekerAngle.text = newAngle.toString()
    }

}
