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

    constructor(context: Context) : this(context, null) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttributeSet: Int) : super(context, attrs, defStyleAttributeSet) {
        init(attrs, defStyleAttributeSet)
    }

    val DPTOPX_SCALE = resources.displayMetrics.density

    val MIN_TOUCH_TARGET_DP = 48

    lateinit var mCircleRectF: RectF

    lateinit var mArcPaint: Paint
    lateinit var mProgressPaint : Paint
    lateinit var mPointerPaint: Paint
    lateinit var mArcPath: Path
    lateinit var mProgressPath : Path
    private var onRoundSeekerChangeListener : OnRoundSeekerChangeListener?=null
    var maxProgress = 200

    var mPointerPositionXY = FloatArray(2)
    var delta: Float = 0f
    var lastStopAngle = 0f
    var startAngle = 120f
    var stopAngle = 60f

    fun init(attrs: AttributeSet?, defStyle: Int) {
        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.RoundSeekerView, defStyle, 0)

        initRects()
        mArcPath = Path()
        mProgressPath =Path()
        initPaints()
        updatePath()
        attrArray.recycle()
    }

    private fun updatePath() {
        calculatePointerPositionXY()
        initPaths()
    }

    private fun initPaths() {

        mProgressPath.rewind()
        mProgressPath.addArc(mCircleRectF, startAngle, delta)
        Log.d("delta", delta.toString())

    }

    private fun calculatePointerPositionXY() {

        mPointerPositionXY[0] = 300 * Math.cos((startAngle + delta) *Math.PI/180).toFloat()
        mPointerPositionXY[1] = 300 * Math.sin((startAngle + delta) *Math.PI/180).toFloat()
    }

    fun initPaints() {

        mArcPaint = Paint()
        mArcPaint.isAntiAlias = true
        mArcPaint.isDither = true
        mArcPaint.color = Color.BLACK
        mArcPaint.strokeWidth = 10f
        mArcPaint.style = Paint.Style.STROKE
        mArcPaint.strokeJoin = Paint.Join.ROUND
        mArcPaint.strokeCap = Paint.Cap.ROUND

        mProgressPaint = Paint()
        mProgressPaint.isAntiAlias = true
        mProgressPaint.isDither = true
        mProgressPaint.color = Color.BLUE
        mProgressPaint.strokeWidth = 10f
        mProgressPaint.style = Paint.Style.STROKE
        mProgressPaint.strokeJoin = Paint.Join.ROUND
        mProgressPaint.strokeCap = Paint.Cap.ROUND


        mPointerPaint = Paint()
        mPointerPaint.color = Color.RED

    }

    fun gettotalGap() = if (stopAngle > startAngle) stopAngle - startAngle else 360 + (stopAngle - startAngle)


    fun getProgressFromAngle(angle : Float) : Int {

        var progress = (angle * maxProgress / gettotalGap()).toInt()
        if (progress < 0 ) progress = 0
        else if (progress > maxProgress) progress = maxProgress
        return progress
    }

    fun initRects() {
        mCircleRectF = RectF()
        mCircleRectF.set(-300f, -300f, 300f, 300f)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(width / 2f, height / 2f)

        canvas.drawPath(mArcPath, mArcPaint)
        canvas.drawPath(mProgressPath, mProgressPaint)
        canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], 15f, mPointerPaint)
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
                Log.d("roundSeeker", "pressed")
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d("roundSeeker", "moving")
                if (touchAngle > lastStopAngle) increaseArc()
                else decreaseArc()
                lastStopAngle = touchAngle.toFloat()
            }

            MotionEvent.ACTION_UP -> {
                Log.d("roundSeeker", "unpressed")
            }
        }
        return true
    }

    fun increaseArc() {

        if (delta > gettotalGap()) return
        delta += 1f
        updatePath()
        onRoundSeekerChangeListener?.onSeekBarChanged(getProgressFromAngle(delta))
        invalidate()

    }

    fun decreaseArc() {

        if (delta < 0) return
        delta -= 1f
        updatePath()
        onRoundSeekerChangeListener?.onSeekBarChanged(getProgressFromAngle(delta))
        invalidate()
    }

    fun setOnSeekBarChangedListener(onRoundSeekerChangeListener: OnRoundSeekerChangeListener){
        this.onRoundSeekerChangeListener = onRoundSeekerChangeListener
    }

    interface OnRoundSeekerChangeListener{

        fun onSeekBarChanged(newAngle : Int)

    }
}