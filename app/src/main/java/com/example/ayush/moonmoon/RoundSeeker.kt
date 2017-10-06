package com.example.ayush.moonmoon

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View


/**
 * Created by ayush on 10/5/17.
 */
class RoundSeeker : View {

    constructor(context: Context) : this(context, null){
        init(null,0)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0){
        init(attrs,0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttributeSet: Int) : super(context, attrs, defStyleAttributeSet){
        init(attrs,defStyleAttributeSet)
    }

    val DPTOPX_SCALE = resources.displayMetrics.density

    val MIN_TOUCH_TARGET_DP = 48

    val DEFAULT_CIRCLE_X_RADIUS = 30f
    val DEFAULT_CIRCLE_Y_RADIUS = 30f
    val DEFAULT_POINTER_RADIUS = 7f
    val DEFAULT_POINTER_HALO_WIDTH = 6f
    val DEFAULT_POINTER_HALO_BORDER_WIDTH = 2f
    val DEFAULT_CIRCLE_STROKE_WIDTH = 5f
    val DEFAULT_START_ANGLE = 180f // Geometric (clockwise, relative to 3 o'clock)
    val DEFAULT_END_ANGLE = 180f // Geometric (clockwise, relative to 3 o'clock)
    val DEFAULT_MAX = 100
    val DEFAULT_PROGRESS = 0
    val DEFAULT_CIRCLE_COLOR = Color.DKGRAY
    val DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 74, 138, 255)
    val DEFAULT_POINTER_COLOR = Color.argb(235, 74, 138, 255)
    val DEFAULT_POINTER_HALO_COLOR = Color.argb(135, 74, 138, 255)
    val DEFAULT_POINTER_HALO_COLOR_ONTOUCH = Color.argb(135, 74, 138, 255)
    val DEFAULT_CIRCLE_FILL_COLOR = Color.TRANSPARENT
    val DEFAULT_POINTER_ALPHA = 135
    val DEFAULT_POINTER_ALPHA_ONTOUCH = 100
    val DEFAULT_USE_CUSTOM_RADII = false
    val DEFAULT_MAINTAIN_EQUAL_CIRCLE = true
    val DEFAULT_MOVE_OUTSIDE_CIRCLE = false
    val DEFAULT_LOCK_ENABLED = true

    lateinit var mCirclePaint: Paint
    lateinit var mCircleFillPaint: Paint
    lateinit var mCircleProgressPaint: Paint
    lateinit var mCircleProgressGlowPaint: Paint
    lateinit var mPointerPaint: Paint
    lateinit var mPointerHaloPaint: Paint
    lateinit var mPointerHaloBorderPaint: Paint
    var mCircleStrokeWidth: Float = 0.toFloat()
    var mCircleXRadius: Float = 0.toFloat()
    var mCircleYRadius: Float = 0.toFloat()
    var mPointerRadius: Float = 0.toFloat()
    var mPointerHaloWidth: Float = 0.toFloat()
    var mPointerHaloBorderWidth: Float = 0.toFloat()
    var mStartAngle: Float = 0.toFloat()
    var mEndAngle: Float = 0.toFloat()
    var mCircleRectF = RectF()
    var mPointerColor = DEFAULT_POINTER_COLOR
    var mPointerHaloColor = DEFAULT_POINTER_HALO_COLOR
    var mPointerHaloColorOnTouch = DEFAULT_POINTER_HALO_COLOR_ONTOUCH
    var mCircleColor = DEFAULT_CIRCLE_COLOR
    var mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR
    var mCircleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR
    var mPointerAlpha = DEFAULT_POINTER_ALPHA
    var mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH
    var mTotalCircleDegrees: Float = 0.toFloat()
    var mProgressDegrees: Float = 0.toFloat()
    lateinit var mCirclePath: Path
    lateinit var mCircleProgressPath: Path
    var mMax: Int = 0
    var mProgress: Int = 0
    var mCustomRadii: Boolean = false
    var mMaintainEqualCircle: Boolean = false
    var mMoveOutsideCircle: Boolean = false
    var mlockEnabled = true
    var lockAtStart = true
    var lockAtEnd = false
    var mUserIsMovingPointer = false
    var cwDistanceFromStart: Float = 0.toFloat()
    var ccwDistanceFromStart: Float = 0.toFloat()
    var cwDistanceFromEnd: Float = 0.toFloat()
    var ccwDistanceFromEnd: Float = 0.toFloat()
    var lastCWDistanceFromStart: Float = 0.toFloat()
    var cwDistanceFromPointer: Float = 0.toFloat()
    var ccwDistanceFromPointer: Float = 0.toFloat()
    var mIsMovingCW: Boolean = false
    var mCircleWidth: Float = 0.toFloat()
    var mCircleHeight: Float = 0.toFloat()
    var mPointerPosition: Float = 0.toFloat()
    var mPointerPositionXY = FloatArray(2)
    var mOnCircularSeekBarChangeListener: OnCircularSeekBarChangeListener? = null
    var isTouchEnabled = true

    fun init(attrs: AttributeSet?, defStyle: Int) {
        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.RoundSeeker, defStyle, 0)

        initAttributes(attrArray)

        attrArray.recycle()

        initPaints()
    }

    fun initAttributes(attrArray: TypedArray) {

        mCircleXRadius = attrArray.getDimension(R.styleable.RoundSeeker_circle_x_radius, DEFAULT_CIRCLE_X_RADIUS * DPTOPX_SCALE)
        mCircleYRadius = attrArray.getDimension(R.styleable.RoundSeeker_circle_y_radius, DEFAULT_CIRCLE_Y_RADIUS * DPTOPX_SCALE)
        mPointerRadius = attrArray.getDimension(R.styleable.RoundSeeker_pointer_radius, DEFAULT_POINTER_RADIUS * DPTOPX_SCALE)
        mPointerHaloWidth = attrArray.getDimension(R.styleable.RoundSeeker_pointer_halo_width, DEFAULT_POINTER_HALO_WIDTH * DPTOPX_SCALE)
        mPointerHaloBorderWidth = attrArray.getDimension(R.styleable.RoundSeeker_pointer_halo_border_width, DEFAULT_POINTER_HALO_BORDER_WIDTH * DPTOPX_SCALE)
        mCircleStrokeWidth = attrArray.getDimension(R.styleable.RoundSeeker_circle_stroke_width, DEFAULT_CIRCLE_STROKE_WIDTH * DPTOPX_SCALE)

        mPointerColor = attrArray.getColor(R.styleable.RoundSeeker_pointer_color, DEFAULT_CIRCLE_COLOR)
        mPointerHaloColor = attrArray.getColor(R.styleable.RoundSeeker_pointer_halo_color, DEFAULT_POINTER_HALO_COLOR)
        mPointerHaloColorOnTouch = attrArray.getColor(R.styleable.RoundSeeker_pointer_halo_color_ontouch, DEFAULT_POINTER_HALO_COLOR_ONTOUCH);
        mCircleColor = attrArray.getColor(R.styleable.RoundSeeker_circle_color, DEFAULT_CIRCLE_COLOR);
        mCircleProgressColor = attrArray.getColor(R.styleable.RoundSeeker_circle_progress_color, DEFAULT_CIRCLE_PROGRESS_COLOR);
        mCircleFillColor = attrArray.getColor(R.styleable.RoundSeeker_circle_fill, DEFAULT_CIRCLE_FILL_COLOR)

        mPointerAlpha = Color.alpha(mPointerHaloColor)
        mPointerAlphaOnTouch = attrArray.getInt(R.styleable.RoundSeeker_pointer_alpha_ontouch, DEFAULT_POINTER_ALPHA_ONTOUCH)
        if (mPointerAlphaOnTouch !in 0..255) mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH

        mMax = attrArray.getInt(R.styleable.RoundSeeker_max, DEFAULT_MAX)
        mProgress = attrArray.getInt(R.styleable.RoundSeeker_progress, DEFAULT_PROGRESS)
        mCustomRadii = attrArray.getBoolean(R.styleable.RoundSeeker_use_custom_radii, DEFAULT_USE_CUSTOM_RADII)
        mMaintainEqualCircle = attrArray.getBoolean(R.styleable.RoundSeeker_maintain_equal_circle, DEFAULT_MAINTAIN_EQUAL_CIRCLE)
        mMoveOutsideCircle = attrArray.getBoolean(R.styleable.RoundSeeker_move_outside_circle, DEFAULT_MOVE_OUTSIDE_CIRCLE)
        mlockEnabled = attrArray.getBoolean(R.styleable.RoundSeeker_lock_enabled, DEFAULT_LOCK_ENABLED)

        mStartAngle = ((360f + (attrArray.getFloat(R.styleable.RoundSeeker_start_angle, DEFAULT_START_ANGLE) % 360f)) % 360f)
        mEndAngle = ((360f + (attrArray.getFloat(R.styleable.RoundSeeker_end_angle, DEFAULT_END_ANGLE) % 360f)) % 360f)
        //if (mStartAngle == mEndAngle) mEndAngle -= .1f

    }

    fun initPaints() {

        mCirclePaint = Paint()
        mCirclePaint.isAntiAlias = true
        mCirclePaint.isDither = true
        mCirclePaint.color = mCircleColor
        mCirclePaint.strokeWidth = mCircleStrokeWidth
        mCirclePaint.style = Paint.Style.STROKE
        mCirclePaint.strokeJoin = Paint.Join.ROUND
        mCirclePaint.strokeCap = Paint.Cap.ROUND

        mCircleFillPaint = Paint()
        mCircleFillPaint.isAntiAlias = true
        mCircleFillPaint.isDither = true
        mCircleFillPaint.color = mCircleFillColor
        mCircleFillPaint.style = Paint.Style.FILL

        mCircleProgressPaint = Paint()
        mCircleProgressPaint.isAntiAlias = true
        mCircleProgressPaint.isDither = true
        mCircleProgressPaint.color = mCircleProgressColor
        mCircleProgressPaint.strokeWidth = mCircleStrokeWidth
        mCircleProgressPaint.style = Paint.Style.STROKE
        mCircleProgressPaint.strokeJoin = Paint.Join.ROUND
        mCircleProgressPaint.strokeCap = Paint.Cap.ROUND

        mCircleProgressGlowPaint = Paint()
        mCircleProgressGlowPaint.set(mCircleProgressPaint)
        mCircleProgressGlowPaint.maskFilter = BlurMaskFilter(5f * DPTOPX_SCALE, BlurMaskFilter.Blur.NORMAL)

        mPointerPaint = Paint()
        mPointerPaint.isAntiAlias = true
        mPointerPaint.isDither = true
        mPointerPaint.style = Paint.Style.FILL
        mPointerPaint.color = mPointerColor
        mPointerPaint.strokeWidth = mPointerRadius

        mPointerHaloPaint = Paint()
        mPointerHaloPaint.set(mPointerPaint)
        mPointerHaloPaint.color = mPointerHaloColor
        mPointerHaloPaint.alpha = mPointerAlpha
        mPointerHaloPaint.strokeWidth = mPointerRadius + mPointerHaloWidth

        mPointerHaloBorderPaint = Paint()
        mPointerHaloBorderPaint.set(mPointerPaint)
        mPointerHaloBorderPaint.strokeWidth = mPointerHaloBorderWidth
        mPointerHaloBorderPaint.style = Paint.Style.STROKE

    }

    protected fun calculateTotalDegrees() {
        mTotalCircleDegrees = (360f - (mStartAngle - mEndAngle)) % 360f // Length of the entire circle/arc
        if (mTotalCircleDegrees <= 0f) {
            mTotalCircleDegrees = 180f
        }
    }

    fun calculateProgressDegrees() {
        mProgressDegrees = mPointerPosition - mStartAngle // Verified
        mProgressDegrees = if (mProgressDegrees < 0) 360f + mProgressDegrees else mProgressDegrees // Verified
    }

    fun calculatePointerAngle() {
        val progressPercent = mProgress / mMax
        mPointerPosition = progressPercent * mTotalCircleDegrees + mStartAngle
        mPointerPosition %= 360f
    }

    fun calculatePointerXYPosition() {
        var pm = PathMeasure(mCircleProgressPath, false)
        var returnValue = pm.getPosTan(pm.length, mPointerPositionXY, null)
        if (!returnValue) {
            pm = PathMeasure(mCirclePath, false)
            returnValue = pm.getPosTan(0f, mPointerPositionXY, null)
        }
    }

    fun initPaths() {
        mCirclePath = Path()
        mCirclePath.addArc(mCircleRectF, mStartAngle, mTotalCircleDegrees)

        mCircleProgressPath = Path()
        mCircleProgressPath.addArc(mCircleRectF, mStartAngle, mProgressDegrees)
    }

    fun initRects() {
        mCircleRectF.set(-mCircleWidth, -mCircleHeight, mCircleWidth, mCircleHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.translate(this.width / 2f, this.height-40f)

        canvas.drawPath(mCirclePath, mCirclePaint)
        canvas.drawPath(mCircleProgressPath, mCircleProgressGlowPaint)
        canvas.drawPath(mCircleProgressPath, mCircleProgressPaint)

        canvas.drawPath(mCirclePath, mCircleFillPaint)

        canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius + mPointerHaloWidth, mPointerHaloPaint)
        canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius, mPointerPaint)
        if (mUserIsMovingPointer) {
            canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius + mPointerHaloWidth + mPointerHaloBorderWidth / 2f, mPointerHaloBorderPaint)
        }
    }

    fun getProgress(): Int {
        val progress = Math.round(mMax * mProgressDegrees / mTotalCircleDegrees)
        return progress
    }

    fun setProgress(progress: Int) {
        if (mProgress !== progress) {
            mProgress = progress
            if (mOnCircularSeekBarChangeListener != null) {
                mOnCircularSeekBarChangeListener?.onProgressChanged(this, progress, false)
            }

            recalculateAll()
            invalidate()
        }
    }

    protected fun setProgressBasedOnAngle(angle: Float) {
        mPointerPosition = angle
        calculateProgressDegrees()
        mProgress = Math.round(mMax * mProgressDegrees / mTotalCircleDegrees)
    }

    protected fun recalculateAll() {
        calculateTotalDegrees()
        calculatePointerAngle()
        calculateProgressDegrees()

        initRects()

        initPaths()

        calculatePointerXYPosition()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = View.getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        if (mMaintainEqualCircle) {
            val min = Math.min(width, height)
            setMeasuredDimension(min, min)
        } else {
            setMeasuredDimension(width, height)
        }

        // Set the circle width and height based on the view for the moment
        mCircleHeight = height.toFloat() / 2f - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f
        mCircleWidth = width.toFloat() / 2f - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f

        // If it is not set to use custom
        if (mCustomRadii) {
            // Check to make sure the custom radii are not out of the view. If they are, just use the view values
            if (mCircleYRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth < mCircleHeight) {
                mCircleHeight = mCircleYRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f
            }

            if (mCircleXRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth < mCircleWidth) {
                mCircleWidth = mCircleXRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f
            }
        }

        if (mMaintainEqualCircle) { // Applies regardless of how the values were determined
            val min = Math.min(mCircleHeight, mCircleWidth)
            mCircleHeight = min
            mCircleWidth = min
        }

        recalculateAll()
    }

    fun isLockEnabled(): Boolean {
        return mlockEnabled
    }

    fun setLockEnabled(lockEnabled: Boolean) {
        this.mlockEnabled = lockEnabled
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isTouchEnabled) {
            return false
        }

        // Convert coordinates to our internal coordinate system
        val x = event.x - width / 2
        val y = event.y - height / 2

        // Get the distance from the center of the circle in terms of x and y
        val distanceX = mCircleRectF.centerX() - x
        val distanceY = mCircleRectF.centerY() - y

        // Get the distance from the center of the circle in terms of a radius
        val touchEventRadius = Math.sqrt(Math.pow(distanceX.toDouble(), 2.0) + Math.pow(distanceY.toDouble(), 2.0)).toFloat()

        val minimumTouchTarget = MIN_TOUCH_TARGET_DP * DPTOPX_SCALE // Convert minimum touch target into px
        var additionalRadius: Float // Either uses the minimumTouchTarget size or larger if the ring/pointer is larger

        if (mCircleStrokeWidth < minimumTouchTarget) { // If the width is less than the minimumTouchTarget, use the minimumTouchTarget
            additionalRadius = minimumTouchTarget / 2
        } else {
            additionalRadius = mCircleStrokeWidth / 2 // Otherwise use the width
        }
        val outerRadius = Math.max(mCircleHeight, mCircleWidth) + additionalRadius // Max outer radius of the circle, including the minimumTouchTarget or wheel width
        val innerRadius = Math.min(mCircleHeight, mCircleWidth) - additionalRadius // Min inner radius of the circle, including the minimumTouchTarget or wheel width

        if (mPointerRadius < minimumTouchTarget / 2) { // If the pointer radius is less than the minimumTouchTarget, use the minimumTouchTarget
            additionalRadius = minimumTouchTarget / 2
        } else {
            additionalRadius = mPointerRadius // Otherwise use the radius
        }

        var touchAngle: Float
        touchAngle = (java.lang.Math.atan2(y.toDouble(), x.toDouble()) / Math.PI * 180 % 360).toFloat() // Verified
        touchAngle = if (touchAngle < 0) 360 + touchAngle else touchAngle // Verified

        cwDistanceFromStart = touchAngle - mStartAngle // Verified
        cwDistanceFromStart = if (cwDistanceFromStart < 0) 360f + cwDistanceFromStart else cwDistanceFromStart // Verified
        ccwDistanceFromStart = 360f - cwDistanceFromStart // Verified

        cwDistanceFromEnd = touchAngle - mEndAngle // Verified
        cwDistanceFromEnd = if (cwDistanceFromEnd < 0) 360f + cwDistanceFromEnd else cwDistanceFromEnd // Verified
        ccwDistanceFromEnd = 360f - cwDistanceFromEnd // Verified

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
//                // These are only used for ACTION_DOWN for handling if the pointer was the part that was touched
//                val pointerRadiusDegrees = (mPointerRadius * 180 / (Math.PI * Math.max(mCircleHeight, mCircleWidth)))
                cwDistanceFromPointer = touchAngle - mPointerPosition
                cwDistanceFromPointer = if (cwDistanceFromPointer < 0) 360f + cwDistanceFromPointer else cwDistanceFromPointer
                ccwDistanceFromPointer = 360f - cwDistanceFromPointer
                Log.d("RoundSeeker", cwDistanceFromPointer.toString())
//                // This is for if the first touch is on the actual pointer.
//                //if (touchEventRadius >= innerRadius && touchEventRadius <= outerRadius && (cwDistanceFromPointer <= pointerRadiusDegrees || ccwDistanceFromPointer <= pointerRadiusDegrees)) {
//                if (true) {
//                    setProgressBasedOnAngle(mPointerPosition)
//                    lastCWDistanceFromStart = cwDistanceFromStart
//                    mIsMovingCW = true
//                    mPointerHaloPaint.alpha = mPointerAlphaOnTouch
//                    mPointerHaloPaint.color = mPointerHaloColorOnTouch
//                    recalculateAll()
//                    invalidate()
//                    if (mOnCircularSeekBarChangeListener != null) {
//                        mOnCircularSeekBarChangeListener?.onStartTrackingTouch(this)
//                    }
//                    mUserIsMovingPointer = true
//                    lockAtEnd = false
//                    lockAtStart = false
//                } else if (cwDistanceFromStart > mTotalCircleDegrees) { // If the user is touching outside of the start AND end
//                    mUserIsMovingPointer = false
//                    return false
//                } else if (touchEventRadius >= innerRadius && touchEventRadius <= outerRadius) { // If the user is touching near the circle
//                    setProgressBasedOnAngle(touchAngle)
//                    lastCWDistanceFromStart = cwDistanceFromStart
//                    mIsMovingCW = true
//                    mPointerHaloPaint.alpha = mPointerAlphaOnTouch
//                    mPointerHaloPaint.color = mPointerHaloColorOnTouch
//                    recalculateAll()
//                    invalidate()
//                    if (mOnCircularSeekBarChangeListener != null) {
//                        mOnCircularSeekBarChangeListener?.onStartTrackingTouch(this)
//                        mOnCircularSeekBarChangeListener?.onProgressChanged(this, mProgress, true)
//                    }
//                    mUserIsMovingPointer = true
//                    lockAtEnd = false
//                    lockAtStart = false
//                } else { // If the user is not touching near the circle
//                    mUserIsMovingPointer = false
//                    return false
//                }
            }
            MotionEvent.ACTION_MOVE -> if (mUserIsMovingPointer) {
//                if (lastCWDistanceFromStart < cwDistanceFromStart) {
//                    if (cwDistanceFromStart - lastCWDistanceFromStart > 180f && !mIsMovingCW) {
//                        lockAtStart = true
//                        lockAtEnd = false
//                    } else {
//                        mIsMovingCW = true
//                    }
//                } else {
//                    if (lastCWDistanceFromStart - cwDistanceFromStart > 180f && mIsMovingCW) {
//                        lockAtEnd = true
//                        lockAtStart = false
//                    } else {
//                        mIsMovingCW = false
//                    }
//                }
//
//                if (lockAtStart && mIsMovingCW) {
//                    lockAtStart = false
//                }
//                if (lockAtEnd && !mIsMovingCW) {
//                    lockAtEnd = false
//                }
//                if (lockAtStart && !mIsMovingCW && ccwDistanceFromStart > 90) {
//                    lockAtStart = false
//                }
//                if (lockAtEnd && mIsMovingCW && cwDistanceFromEnd > 90) {
//                    lockAtEnd = false
//                }
//                // Fix for passing the end of a semi-circle quickly
//                if (!lockAtEnd && cwDistanceFromStart > mTotalCircleDegrees && mIsMovingCW && lastCWDistanceFromStart < mTotalCircleDegrees) {
//                    lockAtEnd = true
//                }
//
//                if (lockAtStart && mlockEnabled) {
//                    // TODO: Add a check if mProgress is already 0, in which case don't call the listener
//                    mProgress = 0
//                    recalculateAll()
//                    invalidate()
//                    if (mOnCircularSeekBarChangeListener != null) {
//                        mOnCircularSeekBarChangeListener?.onProgressChanged(this, mProgress, true)
//                    }
//
//                } else if (lockAtEnd && mlockEnabled) {
//                    mProgress = mMax
//                    recalculateAll()
//                    invalidate()
//                    if (mOnCircularSeekBarChangeListener != null) {
//                        mOnCircularSeekBarChangeListener?.onProgressChanged(this, mProgress, true)
//                    }
//                } else if (mMoveOutsideCircle || touchEventRadius <= outerRadius) {
//                    if (cwDistanceFromStart <= mTotalCircleDegrees) {
//                        setProgressBasedOnAngle(touchAngle)
//                    }
//                    recalculateAll()
//                    invalidate()
//                    if (mOnCircularSeekBarChangeListener != null) {
//                        mOnCircularSeekBarChangeListener?.onProgressChanged(this, mProgress, true)
//                    }
//                } else {
//                    lastCWDistanceFromStart = cwDistanceFromStart
//                }
//
//                lastCWDistanceFromStart = cwDistanceFromStart
            } else {
                return false
            }
            MotionEvent.ACTION_UP -> {
//                mPointerHaloPaint.alpha = mPointerAlpha
//                mPointerHaloPaint.color = mPointerHaloColor
//                if (mUserIsMovingPointer) {
//                    mUserIsMovingPointer = false
//                    invalidate()
//                    if (mOnCircularSeekBarChangeListener != null) {
//                        mOnCircularSeekBarChangeListener?.onStopTrackingTouch(this)
//                    }
//                } else {
//                    return false
//                }
            }
            MotionEvent.ACTION_CANCEL // Used when the parent view intercepts touches for things like scrolling
            -> {
//                mPointerHaloPaint.alpha = mPointerAlpha
//                mPointerHaloPaint.color = mPointerHaloColor
//                mUserIsMovingPointer = false
//                invalidate()
            }
        }

        if (event.action == MotionEvent.ACTION_MOVE && parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }

        return true
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        val state = Bundle()
        state.putParcelable("PARENT", superState)
        state.putInt("MAX", mMax)
        state.putInt("PROGRESS", mProgress)
        state.putInt("mCircleColor", mCircleColor)
        state.putInt("mCircleProgressColor", mCircleProgressColor)
        state.putInt("mPointerColor", mPointerColor)
        state.putInt("mPointerHaloColor", mPointerHaloColor)
        state.putInt("mPointerHaloColorOnTouch", mPointerHaloColorOnTouch)
        state.putInt("mPointerAlpha", mPointerAlpha)
        state.putInt("mPointerAlphaOnTouch", mPointerAlphaOnTouch)
        state.putBoolean("lockEnabled", mlockEnabled)
        state.putBoolean("isTouchEnabled", isTouchEnabled)

        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as Bundle

        val superState = savedState.getParcelable<Parcelable>("PARENT")
        super.onRestoreInstanceState(superState)

        mMax = savedState.getInt("MAX")
        mProgress = savedState.getInt("PROGRESS")
        mCircleColor = savedState.getInt("mCircleColor")
        mCircleProgressColor = savedState.getInt("mCircleProgressColor")
        mPointerColor = savedState.getInt("mPointerColor")
        mPointerHaloColor = savedState.getInt("mPointerHaloColor")
        mPointerHaloColorOnTouch = savedState.getInt("mPointerHaloColorOnTouch")
        mPointerAlpha = savedState.getInt("mPointerAlpha")
        mPointerAlphaOnTouch = savedState.getInt("mPointerAlphaOnTouch")
        mlockEnabled = savedState.getBoolean("lockEnabled")
        isTouchEnabled = savedState.getBoolean("isTouchEnabled")

        initPaints()

        recalculateAll()
    }

    fun setOnSeekBarChangeListener(l: OnCircularSeekBarChangeListener) {
        mOnCircularSeekBarChangeListener = l
    }

    fun setCircleColor(color: Int) {
        mCircleColor = color
        mCirclePaint.color = mCircleColor
        invalidate()
    }

    fun getCircleColor() = mCircleColor

    fun setCircleProgressColor(color: Int) {
        mCircleProgressColor = color
        mCircleProgressPaint.color = mCircleProgressColor
        invalidate()
    }

    fun getCircleProgressColor() = mCircleProgressColor

    fun setPointerColor(color: Int) {
        mPointerColor = color
        mPointerPaint.color = mPointerColor
        invalidate()
    }

    fun getPointerColor() = mPointerColor

    fun setPointerHaloColor(color: Int) {
        mPointerHaloColor = color
        mPointerHaloPaint.color = mPointerHaloColor
        invalidate()
    }

    fun getPointerHaloColor() = mPointerHaloColor

    fun setPointerAlpha(alpha: Int) {
        if (alpha >= 0 && alpha <= 255) {
            mPointerAlpha = alpha
            mPointerHaloPaint.alpha = mPointerAlpha
            invalidate()
        }
    }

    fun getPointerAlpha() = mPointerAlpha

    fun setPointerAlphaOnTouch(alpha: Int) {
        if (alpha >= 0 && alpha <= 255) {
            mPointerAlphaOnTouch = alpha
        }
    }

    fun getPointerAlphaOnTouch() = mPointerAlphaOnTouch

    fun setCircleFillColor(color: Int) {
        mCircleFillColor = color
        mCircleFillPaint.color = mCircleFillColor
        invalidate()
    }

    fun getCircleFillColor() = mCircleFillColor

    fun setMax(max: Int) {
        if (max > 0) { // Check to make sure it's greater than zero
            if (max <= mProgress) {
                mProgress = 0 // If the new max is less than current progress, set progress to zero
                if (mOnCircularSeekBarChangeListener != null) {
                    mOnCircularSeekBarChangeListener?.onProgressChanged(this, mProgress, false)
                }
            }
            mMax = max

            recalculateAll()
            invalidate()
        }
    }

    @Synchronized fun getMax() = mMax

    fun setIsTouchEnabled(isTouchEnabled: Boolean) {
        this.isTouchEnabled = isTouchEnabled

        fun getIsTouchEnabled() = isTouchEnabled


    }
}