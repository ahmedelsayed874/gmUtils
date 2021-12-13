package gmutils.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import gmutils.R
import kotlin.math.min


class ShapeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mPaintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        maskFilter = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val mRectStroke = RectF()

    private val mPaintMain = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRectMain = RectF()


    var sides = 0
        set(value) {
            field = value
            postInvalidate()
        }

    var strokeThickness = 2f
        get() = mPaintStroke.strokeWidth
        set(value) {
            field = value
            mPaintStroke.strokeWidth = value
            postInvalidate()
        }

    var strokeColor = Color.TRANSPARENT
        set(value) {
            field = value
            mPaintStroke.color = value
            postInvalidate()
        }

    var shapeColor = Color.BLACK
        set(value) {
            field = value
            mPaintMain.color = value
            postInvalidate()
        }

    var roundRadius = 0
        set(value) {
            field = value
            postInvalidate()
        }

    var lineWidth = 1f
        set(value) {
            field = value
            postInvalidate()
        }


    init {
        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(
                attrs,
                R.styleable.ShapeView,
                defStyleAttr,
                0
            )

            strokeThickness = attributes.getDimension(R.styleable.ShapeView_strokeThickness, 0f)
            //strokeThickness = attributes.getInteger(R.styleable.ShapeView_strokeThickness, 0).toFloat()
            strokeColor = attributes.getColor(R.styleable.ShapeView_stroke_Color, Color.TRANSPARENT)

            shapeColor = attributes.getColor(R.styleable.ShapeView_shapeColor, Color.BLACK)
            sides = attributes.getInteger(R.styleable.ShapeView_sides, 0)
            roundRadius = attributes.getDimension(R.styleable.ShapeView_roundRadius, 0f).toInt()
            lineWidth = attributes.getDimension(R.styleable.ShapeView_lineWidth, 1f)

            attributes.recycle()
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        when (sides) {
            0 -> {
                drawCircle(canvas)
            }
            1 -> {
                drawLine(canvas)
            }
            2 -> {
                drawParallelLines(canvas)
            }
            3 -> {
                drawTriangle(canvas)
            }
            4 -> {
                drawRectangle(canvas)
            }
        }
    }

    private fun drawCircle(canvas: Canvas) {
        val cx = width / 2.0f
        val cy = height / 2.0f
        val constant = strokeThickness / 2f
        val radius = min(width, height) / 2f - constant - min(
            min(paddingLeft, paddingTop),
            min(paddingRight, paddingBottom)
        )

        canvas.drawCircle(cx, cy, radius - constant, mPaintMain)

        canvas.drawCircle(cx, cy, radius, mPaintStroke)
    }

    private fun drawLine(canvas: Canvas) {
        val halfLineWidth = lineWidth / 2f
        val cy = (height - paddingTop - paddingBottom) / 2f

        //top line
        mRectMain.left = strokeThickness + paddingLeft
        mRectMain.right = width - strokeThickness - paddingRight
        mRectMain.top = cy - halfLineWidth
        mRectMain.bottom = cy + halfLineWidth

        canvas.drawRect(mRectMain, mPaintMain)


        val constant = strokeThickness / 2f

        //top stroke line
        mRectStroke.left = constant + paddingLeft
        mRectStroke.right = width - constant - paddingRight
        mRectStroke.top = cy - halfLineWidth - constant
        mRectStroke.bottom = cy + halfLineWidth + constant;

        canvas.drawRect(mRectStroke, mPaintStroke)

    }

    private fun drawParallelLines(canvas: Canvas) {

        //top line
        mRectMain.left = strokeThickness + paddingLeft
        mRectMain.right = width - strokeThickness - paddingRight
        mRectMain.top = strokeThickness + paddingTop
        mRectMain.bottom = mRectMain.top + lineWidth

        canvas.drawRect(mRectMain, mPaintMain)


        val constant = strokeThickness / 2f

        //top stroke line
        mRectStroke.left = constant + paddingLeft
        mRectStroke.right = width - constant - paddingRight
        mRectStroke.top = constant + paddingTop
        mRectStroke.bottom = mRectStroke.top + (strokeThickness + lineWidth)

        canvas.drawRect(mRectStroke, mPaintStroke)

        //------------------------------------------------------------------------------------------

        //bottom line
        mRectMain.left = strokeThickness + paddingLeft
        mRectMain.right = width - strokeThickness - paddingRight
        mRectMain.top = height - (strokeThickness + lineWidth) - paddingBottom
        mRectMain.bottom = height.toFloat() - strokeThickness - paddingBottom

        canvas.drawRect(mRectMain, mPaintMain)

        //bottom stroke line
        mRectStroke.left = constant + paddingLeft
        mRectStroke.right = width.toFloat() - constant - paddingRight
        mRectStroke.top = height - (constant + strokeThickness + lineWidth) - paddingBottom
        mRectStroke.bottom = height.toFloat() - constant - paddingBottom

        canvas.drawRect(mRectStroke, mPaintStroke)

    }

    private fun drawTriangle(canvas: Canvas) {
        var constant = strokeThickness + 20

        var xTop = width / 2f
        var yTop = constant + paddingTop - 5
        var xRight = width.toFloat() - constant - paddingRight + 20
        var yBottom = height.toFloat() - constant - paddingBottom + 20
        var xLeft = constant + paddingLeft - 20

        val path2 = Path()
        path2.moveTo(xTop, yTop)
        path2.lineTo(xRight, yBottom)
        path2.lineTo(xLeft, yBottom)
        path2.lineTo(xTop, yTop)

        canvas.drawPath(path2, mPaintMain)

        //------------------------------------------------------------------------------------------

        val constant0 = strokeThickness / 2f
        constant = strokeThickness

        xTop = width / 2f
        yTop = constant0 + paddingTop
        xRight = width.toFloat() - constant - paddingRight + 20
        yBottom = height.toFloat() - constant + constant0 - paddingBottom
        xLeft = constant + paddingLeft - 20

        val path = Path()
        path.moveTo(xTop, yTop)
        path.lineTo(xRight, yBottom)
        path.lineTo(xLeft, yBottom)
        path.lineTo(xTop, yTop)

        canvas.drawPath(path, mPaintStroke)

    }

    private fun drawRectangle(canvas: Canvas) {
        var constant = strokeThickness - 3

        mRectMain.left = constant + paddingLeft
        mRectMain.right = width - constant - paddingRight
        mRectMain.top = constant + paddingTop
        mRectMain.bottom = height - constant - paddingBottom

        //canvas.drawRect(mRectMain, mPaintMain)
        canvas.drawRoundRect(mRectMain, roundRadius.toFloat(), roundRadius.toFloat(), mPaintMain)

        //------------------------------------------------------------------------------------------

        constant = strokeThickness / 2f

        mRectStroke.left = constant + paddingLeft
        mRectStroke.right = width.toFloat() - constant - paddingRight
        mRectStroke.top = constant + paddingTop
        mRectStroke.bottom = height.toFloat() - constant - paddingBottom

        //canvas.drawRect(mRectStroke, mPaintStroke)
        canvas.drawRoundRect(
            mRectStroke,
            roundRadius.toFloat(),
            roundRadius.toFloat(),
            mPaintStroke
        )

    }

}