package com.hus

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthButtonSize = 0
    private var heightButtonSize = 0
    private var valuesAnimator = ValueAnimator()
    private val textOfButton: String
        get() = context.getString(this.stateOfButton.selectARepository)
    private var colorsAccentyellow = context.getColor(R.color.colorAccent)
    private var colorFailedRed = context.getColor(R.color.button_failed_color)
    private var colorTextedWhite = context.getColor(R.color.white)
    private var colorPrimaryCyen = context.getColor(R.color.colorPrimary)
    private var colorInprogressDarkBlue = context.getColor(R.color.colorPrimaryDark)
    private val pathAngle = Path()
    private val radiusAngle = 50f
    private val rectangleLoad = Rect()
    private var InProgress: Float = 0f
    private var endOfProgress: Float = 0f
    private val rectangleText = Rect()
    private val rectangleCurve = RectF()
    var stateOfButton: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            is ButtonState.Loading -> {
                if (old != ButtonState.Loading) {
                    settingAnimation()
                    SettingColorBacgroundOfButton(colorPrimaryCyen)
                }
            }

            is ButtonState.Completed -> {
                settingProgressOfButton(1f)
            }

            is ButtonState.Failure -> {
                SettingColorBacgroundOfButton(colorFailedRed)
                resetingProgress()
            }

            else -> {} //empty
        }

        invalidate()
        requestLayout()
    }
    init {
        settingLoadStateForButton(ButtonState.Inititial)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            defStyleAttr,
            0
        ).apply {
            try {
                // styleable colors from XML attrs
                colorPrimaryCyen = getColor(R.styleable.LoadingButton_colorOfBackground, colorPrimaryCyen)
                colorInprogressDarkBlue = getColor(R.styleable.LoadingButton_colorOfInProgressBackground, colorInprogressDarkBlue)
                colorsAccentyellow = getColor(R.styleable.LoadingButton_colorOfCircleProgress, colorsAccentyellow)
                colorFailedRed = getColor(R.styleable.LoadingButton_colorOfFailure, colorFailedRed)
                colorTextedWhite = getColor(R.styleable.LoadingButton_colorOfText, colorTextedWhite)
            } finally {
                recycle()
            }
        }
    }
    private val colorOfBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorPrimaryCyen
    }
    private val colorOfText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlignment = TEXT_ALIGNMENT_CENTER
        textSize = 57f
        color = Color.MAGENTA
        typeface = Typeface.DEFAULT
    }
    private val colorOfBackgroundInProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorInprogressDarkBlue
    }
    private val colorCurveInprogress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorsAccentyellow
        style = Paint.Style.FILL
    }
    private fun SettingColorBacgroundOfButton(colorOfBackground: Int) {
        this.colorOfBackground.color = colorOfBackground
    }
    fun settingProgressOfButton(floatProgress: Float) {
        if (InProgress < floatProgress){
            endOfProgress = floatProgress
            settingAnimation()
        }
    }
    fun addButtonProgress(floatProgress: Float) {
        if (InProgress < 0.64f){
            settingProgressOfButton(InProgress + floatProgress)
        }
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //canvas?.drawText(buttonText, 3 - 0F, 50F, textPaint)

        canvas?.apply {
            save()
            clipPath(pathAngle)
            drawColor(colorOfBackground.color)
            colorOfText.getTextBounds(textOfButton, 0, textOfButton.length, rectangleText)
            val xText = width / 2f - rectangleText.width() / 2f
            val yText = height / 2f + rectangleText.height() / 2f - rectangleText.bottom
            var offsetText = 0

            rectangleLoad.set(0, 0, (width * InProgress).roundToInt(), height)
            drawRect(rectangleLoad, colorOfBackgroundInProgress)

            if (stateOfButton == ButtonState.Loading) {
                val startOfCurveX = width / 2f + rectangleText.width() / 2f
                val startOfCurveY = height / 2f - 19
                rectangleCurve.set(startOfCurveX, startOfCurveY, startOfCurveX + 38, startOfCurveY + 38)
                drawArc(
                    rectangleCurve, InProgress, InProgress * 360f, true, colorCurveInprogress
                )
                offsetText = 36
            }

            drawText(textOfButton, xText - offsetText, yText, colorOfText)
            restore()
        }

    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthButtonSize = w
        heightButtonSize = h
        setMeasuredDimension(w, h)

    }
    fun settingLoadStateForButton(buttonState1: ButtonState) {
        stateOfButton = buttonState1
    }
    override fun onSizeChanged(width: Int, height: Int, widthOld: Int, heightOld: Int) {
        super.onSizeChanged(width, height, widthOld, heightOld)
        pathAngle.reset()
        pathAngle.addRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            radiusAngle,
            radiusAngle,
            Path.Direction.CW
        )
        pathAngle.close()
    }

    private fun settingAnimation() {
        valuesAnimator.cancel()
        valuesAnimator = ValueAnimator.ofFloat(InProgress, endOfProgress).apply {
            interpolator = AccelerateDecelerateInterpolator()
            // 1.5 second for 100% progress, 750ms for 50% progress, etc.
            val animDuration = abs(1500 * ((endOfProgress - InProgress) / 100)).toLong()
            // set minimum increment of progress duration to 400ms
            duration = if (animDuration >= 400){
                animDuration
            }else{
                400
            }
            addUpdateListener { animation ->
                InProgress = animation.animatedValue as Float
                postInvalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (InProgress == 1f){
                        resetingProgress()
                    }else{
                        valuesAnimator.cancel()
                    }
                }
            })
            start()
        }
    }

    private fun resetingProgress() {
        endOfProgress = 0f
        settingAnimation()
    }
}