package ni.devotion.floaty_head

import android.graphics.Point
import android.util.Log
import android.view.View
import android.view.WindowManager

class FloatingBubblePhysics private constructor(builder: Builder) :
    DefaultFloatingBubbleTouchListener() {
    private val sizeX: Int
    private val sizeY: Int
    private val bubbleView: View?
    private val windowManager: WindowManager?
    private val config: FloatingBubbleConfig?
    private val bubbleParams: WindowManager.LayoutParams
    private val animator: FloatingBubbleAnimator
    private val previous = arrayOf<Point?>(null, null)

    override fun onDown(x: Float, y: Float) {
        super.onDown(x, y)
        previous[0] = null
        previous[1] = Point(x.toInt(), y.toInt())
    }

    override fun onMove(x: Float, y: Float) {
        super.onMove(x, y)
        addSelectively(x, y)
    }

    override fun onUp(x: Float, y: Float) {
        addSelectively(x, y)
        Log.d(FloatingBubblePhysics::class.java.simpleName, previous.toString())
        if (previous[0] == null) {
            moveToCorner()
        } else {
            moveLinearlyToCorner()
        }
    }

    private fun moveLinearlyToCorner() {
        val x1 = previous[0]!!.x
        val y1 = previous[0]!!.y
        val x2 = previous[1]!!.x
        val y2 = previous[1]!!.y
        if (x2 == x1) {
            moveToCorner()
            return
        }
        val xf = if (x1 < x2) sizeX - bubbleView!!.width else 0
        val yf = y1 + (y2 - y1) * (xf - x1) / (x2 - x1)
        animator.animate(xf.toFloat(), yf.toFloat())
    }

    private fun moveToCorner() {
        if (previous[1]!!.x < sizeX / 2) {
            animator.animate(0f, previous[1]!!.y.toFloat())
        } else {
            animator.animate(sizeX - bubbleView!!.width.toFloat(), previous[1]!!.y.toFloat())
        }
    }

    private fun addSelectively(x: Float, y: Float) {
        if (previous[1] != null && previous[1]!!.x == x.toInt() && previous[1]!!.y == y.toInt()) {
            return
        }
        previous[0] = previous[1]
        previous[1] = Point(x.toInt(), y.toInt())
    }

    class Builder {
        internal var sizeX = 0
        internal var sizeY = 0
        internal var bubbleView: View? = null
        internal var windowManager: WindowManager? = null
        internal var config: FloatingBubbleConfig? = null
        fun sizeX(`val`: Int): Builder {
            sizeX = `val`
            return this
        }

        fun sizeY(`val`: Int): Builder {
            sizeY = `val`
            return this
        }

        fun bubbleView(`val`: View?): Builder {
            bubbleView = `val`
            return this
        }

        fun windowManager(`val`: WindowManager?): Builder {
            windowManager = `val`
            return this
        }

        fun config(`val`: FloatingBubbleConfig?): Builder {
            config = `val`
            return this
        }

        fun build(): FloatingBubblePhysics {
            return FloatingBubblePhysics(this)
        }
    }

    init {
        sizeX = builder.sizeX
        sizeY = builder.sizeY
        bubbleView = builder.bubbleView
        windowManager = builder.windowManager
        config = builder.config
        bubbleParams = bubbleView!!.layoutParams as WindowManager.LayoutParams
        animator = FloatingBubbleAnimator.Builder()
            .bubbleParams(bubbleParams)
            .bubbleView(bubbleView)
            .sizeX(sizeX)
            .sizeY(sizeY)
            .windowManager(windowManager)
            .build()
    }
}