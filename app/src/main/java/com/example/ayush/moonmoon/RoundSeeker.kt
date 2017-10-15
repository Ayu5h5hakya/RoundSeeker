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
class RoundSeeker : View {

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
    lateinit var pathMeasure : PathMeasure

    var mPointerPositionXY = FloatArray(2)
    var delta: Float = 5f

    fun init(attrs: AttributeSet?, defStyle: Int) {
        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.RoundSeeker, defStyle, 0)

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

//        mArcPath.addArc(mCircleRectF, 0f, 360f)
        mProgressPath.addArc(mCircleRectF, 0f, delta)
        pathMeasure.setPath(mProgressPath,false)

    }

    private fun calculatePointerPositionXY() {

        pathMeasure = PathMeasure(mProgressPath, false)
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


    fun initRects() {
        mCircleRectF = RectF()
        mCircleRectF.set(-300f, -300f, 300f, 300f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(width / 2f, height / 2f)

        canvas.drawPath(mArcPath, mArcPaint)
        canvas.drawPath(mProgressPath, mProgressPaint)
        pathMeasure.getPosTan(pathMeasure.length /2f, mPointerPositionXY, null)
        Log.d("eeker",pathMeasure.length.toString())
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

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                Log.d("roundSeeker", "pressed")
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d("roundSeeker", "moving")
                updatePath()
                increaseArc()
            }

            MotionEvent.ACTION_UP -> {
                Log.d("roundSeeker", "unpressed")
            }
        }
        return true
    }

    fun increaseArc() {

        delta += 1f
        invalidate()

    }
}