package com.example.ayush.moonmoon

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.graphics.RectF




/**
 * Created by ayush on 10/5/17.
 */
class RoundSeeker : View {

    constructor(context : Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttributeSet: Int) : super(context,attrs,defStyleAttributeSet)

    val DPTOPX_SCALE = resources.displayMetrics.density

    val MIN_TOUCH_TARGET_DP = 48

    val DEFAULT_CIRCLE_X_RADIUS = 30f
    val DEFAULT_CIRCLE_Y_RADIUS = 30f
    val DEFAULT_POINTER_RADIUS = 7f
    val DEFAULT_POINTER_HALO_WIDTH = 6f
    val DEFAULT_POINTER_HALO_BORDER_WIDTH = 2f
    val DEFAULT_CIRCLE_STROKE_WIDTH = 5f
    val DEFAULT_START_ANGLE = 270f // Geometric (clockwise, relative to 3 o'clock)
    val DEFAULT_END_ANGLE = 270f // Geometric (clockwise, relative to 3 o'clock)
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

    lateinit var mCirclePaint : Paint
    lateinit var mCircleFillPaint : Paint
    lateinit var mCircleProgressPant : Paint
    lateinit var mCircleProgressGlowPaint : Paint
    lateinit var mPointerPaint : Paint
    lateinit var mPointerHaloPaint : Paint
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
    var lockEnabled = true
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

    fun init(attrArray : TypedArray){

        mCircleXRadius = attrArray.getDimension(R.styleable.RoundSeeker_circle_x_radius, DEFAULT_CIRCLE_X_RADIUS * DPTOPX_SCALE)
        mCircleYRadius = attrArray.getDimension(R.styleable.RoundSeeker_circle_y_radius, DEFAULT_CIRCLE_Y_RADIUS * DPTOPX_SCALE)
        mPointerRadius = attrArray.getDimension(R.styleable.RoundSeeker_pointer_radius , DEFAULT_POINTER_RADIUS * DPTOPX_SCALE)
        mPointerHaloWidth = attrArray.getDimension(R.styleable.RoundSeeker_pointer_halo_width, DEFAULT_POINTER_HALO_WIDTH * DPTOPX_SCALE)
        mPointerHaloBorderWidth = attrArray.getDimension(R.styleable.RoundSeeker_pointer_halo_border_width, DEFAULT_POINTER_HALO_BORDER_WIDTH * DPTOPX_SCALE)
        mCircleStrokeWidth = attrArray.getDimension(R.styleable.RoundSeeker_circle_stroke_width, DEFAULT_CIRCLE_STROKE_WIDTH * DPTOPX_SCALE)

        mPointerColor = attrArray.getColor(R.styleable.RoundSeeker_pointer_color, DEFAULT_CIRCLE_COLOR)
        mPointerHaloColor = attrArray.getColor(R.styleable.RoundSeeker_pointer_halo_color, DEFAULT_POINTER_HALO_COLOR)
        mPointerHaloColorOnTouch = attrArray.getColor(R.styleable.RoundSeeker_pointer_halo_color_ontouch, DEFAULT_POINTER_HALO_COLOR_ONTOUCH);
        mCircleColor = attrArray.getColor(R.styleable.RoundSeeker_circle_color, DEFAULT_CIRCLE_COLOR);
        mCircleProgressColor = attrArray.getColor(R.styleable.RoundSeeker_circle_progress_color, DEFAULT_CIRCLE_PROGRESS_COLOR);
        mCircleFillColor = attrArray.getColor(R.styleable.RoundSeeker_circle_fill, DEFAULT_CIRCLE_FILL_COLOR);

        mPointerAlpha = Color.alpha(mPointerHaloColor)
        mPointerAlphaOnTouch = attrArray.getInt(R.styleable.RoundSeeker_pointer_alpha_ontouch, DEFAULT_POINTER_ALPHA_ONTOUCH)


    }
}