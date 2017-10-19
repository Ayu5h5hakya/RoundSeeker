package com.example.ayush.moonmoon

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View


/**
 * Created by ayush on 10/5/17.
 */
class RoundSeekerView : View {

    val DPTOPX_SCALE = resources.displayMetrics.density
    val MIN_TOUCH_TARGET_DP = 48
    val DEFAULT_SHOW_TRACK = true
    val DEFAULT_MAX_PROGRESS = 100
    val DEFAULT_POINTER_RADIUS = 15f
    val DEFAULT_START_ANGLE = 0f
    val DEFAULT_END_ANGLE = 360f
    val DEFAULT_POINTER_COLOR = Color.argb(235, 74, 138, 255)
    val DEFAULT_CIRCLE_COLOR = Color.DKGRAY
    val DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 74, 138, 255)

    lateinit var mCircleRectF: RectF
    lateinit var mArcPaint: Paint
    lateinit var mProgressPaint : Paint
    lateinit var mPointerPaint: Paint
    lateinit var mArcPath: Path
    lateinit var mProgressPath : Path
    private var onRoundSeekerChangeListener : OnRoundSeekerChangeListener?=null

    var maxProgress = DEFAULT_MAX_PROGRESS
    var mPointerPositionXY = FloatArray(2)
    var delta: Float = 0f
    var lastStopAngle = 0f
    var startAngle = DEFAULT_START_ANGLE
    var stopAngle = DEFAULT_END_ANGLE
    var pointerRadius = DEFAULT_POINTER_RADIUS
    var showTrack = DEFAULT_SHOW_TRACK
    var pointerColor = DEFAULT_POINTER_COLOR
    var trackColor = DEFAULT_CIRCLE_COLOR
    var progressColor = DEFAULT_CIRCLE_PROGRESS_COLOR

    constructor(context: Context) : this(context, null) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttributeSet: Int) : super(context, attrs, defStyleAttributeSet) {
        init(attrs, defStyleAttributeSet)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        initAttributes(attrs, defStyle)
        initRects()
        initPaints()
        updatePath()

    }

    private fun initAttributes(attrs: AttributeSet?, defStyle: Int){

        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.RoundSeekerView, defStyle, 0)
        maxProgress = attrArray.getInt(R.styleable.RoundSeekerView_max, DEFAULT_MAX_PROGRESS)
        startAngle = attrArray.getFloat(R.styleable.RoundSeekerView_start_angle, DEFAULT_START_ANGLE)
        stopAngle = attrArray.getFloat(R.styleable.RoundSeekerView_end_angle, DEFAULT_END_ANGLE)
        showTrack = attrArray.getBoolean(R.styleable.RoundSeekerView_show_track, DEFAULT_SHOW_TRACK)
        pointerRadius = attrArray.getFloat(R.styleable.RoundSeekerView_pointer_radius, DEFAULT_POINTER_RADIUS)
        pointerColor = attrArray.getColor(R.styleable.RoundSeekerView_pointer_color, DEFAULT_POINTER_COLOR)
        trackColor = attrArray.getColor(R.styleable.RoundSeekerView_circle_color, DEFAULT_CIRCLE_COLOR)
        progressColor = attrArray.getColor(R.styleable.RoundSeekerView_circle_progress_color, DEFAULT_CIRCLE_PROGRESS_COLOR)
        attrArray.recycle()

    }

    private fun updatePath() {
        calculatePointerPositionXY()
        initPaths()
    }

    private fun initPaths() {

        mArcPath = Path()
        mProgressPath =Path()
        mProgressPath.rewind()
        mArcPath.addArc(mCircleRectF, startAngle, stopAngle)
        mProgressPath.addArc(mCircleRectF, startAngle, delta)

    }

    private fun initPaints() {

        mArcPaint = Paint()
        mArcPaint.isAntiAlias = true
        mArcPaint.isDither = true
        mArcPaint.color = trackColor
        mArcPaint.strokeWidth = 10f
        mArcPaint.style = Paint.Style.STROKE
        mArcPaint.strokeJoin = Paint.Join.ROUND
        mArcPaint.strokeCap = Paint.Cap.ROUND

        mProgressPaint = Paint()
        mProgressPaint.isAntiAlias = true
        mProgressPaint.isDither = true
        mProgressPaint.color = progressColor
        mProgressPaint.strokeWidth = 10f
        mProgressPaint.style = Paint.Style.STROKE
        mProgressPaint.strokeJoin = Paint.Join.ROUND
        mProgressPaint.strokeCap = Paint.Cap.ROUND


        mPointerPaint = Paint()
        mPointerPaint.color = pointerColor

    }

    fun setOnSeekBarChangedListener(onRoundSeekerChangeListener: OnRoundSeekerChangeListener){
        this.onRoundSeekerChangeListener = onRoundSeekerChangeListener
    }

    private fun initRects() {
        mCircleRectF = RectF()
        mCircleRectF.set(-300f, -300f, 300f, 300f)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(width / 2f, height / 2f)

        if (showTrack) canvas.drawPath(mArcPath, mArcPaint)
        canvas.drawPath(mProgressPath, mProgressPaint)
        canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], pointerRadius, mPointerPaint)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = View.getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val min = Math.min(width, height)
        setMeasuredDimension(min, min)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.x - width / 2f
        val y = event.y - height / 2f

        val distanceX = x - mCircleRectF.centerX()
        val distanceY = y - mCircleRectF.centerY()

        val touchRadius = Math.sqrt(Math.pow(distanceX.toDouble(), 2.0) + Math.pow(distanceY.toDouble(), 2.0))
        var touchAngle = Math.toDegrees(Math.atan2(y.toDouble(),x.toDouble()))
        touchAngle = if (touchAngle < 0) 360 + touchAngle else touchAngle

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {

            }

            MotionEvent.ACTION_MOVE -> {

                if (touchAngle > lastStopAngle) increaseArc()
                else decreaseArc()
                lastStopAngle = touchAngle.toFloat()
            }

            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }

    //------------------------------------------------------------------------------------------------------------------->

    private fun calculatePointerPositionXY() {

        mPointerPositionXY[0] = 300 * Math.cos((startAngle + delta) *Math.PI/180).toFloat()
        mPointerPositionXY[1] = 300 * Math.sin((startAngle + delta) *Math.PI/180).toFloat()
    }

    private fun gettotalGap() = if (stopAngle > startAngle) stopAngle - startAngle else 360 + (stopAngle - startAngle)

    private fun getProgressFromAngle(angle : Float) : Int {

        var progress = (angle * maxProgress / gettotalGap()).toInt()
        if (progress < 0 ) progress = 0
        else if (progress > maxProgress) progress = maxProgress
        return progress
    }

    private fun increaseArc() {

        if (delta > gettotalGap()) return
        delta += 1f
        updatePath()
        onRoundSeekerChangeListener?.onSeekBarChanged(getProgressFromAngle(delta))
        invalidate()

    }

    private fun decreaseArc() {

        if (delta < 0) return
        delta -= 1f
        updatePath()
        onRoundSeekerChangeListener?.onSeekBarChanged(getProgressFromAngle(delta))
        invalidate()
    }

    //------------------------------------------------------------------------------------------------------------------->

    interface OnRoundSeekerChangeListener{

        fun onSeekBarChanged(newAngle : Int)

    }
}