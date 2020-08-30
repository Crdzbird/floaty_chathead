package ni.devotion.floaty_head

import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.WindowManager

class FloatingBubbleAnimator private constructor(builder: Builder) {
    companion object {
        private const val ANIMATION_TIME = 100
        private const val ANIMATION_STEPS = 5
    }
    private val bubbleView: View?
    private val bubbleParams: WindowManager.LayoutParams?
    private val windowManager: WindowManager?
    private val sizeX: Int
    private val sizeY: Int

    init {
        bubbleView = builder.bubbleView
        bubbleParams = builder.bubbleParams
        windowManager = builder.windowManager
        sizeX = builder.sizeX
        sizeY = builder.sizeY
    }

    fun animate(x: Float, y: Float) {
        val startX = bubbleParams!!.x.toFloat()
        val startY = bubbleParams.y.toFloat()
        val animator = ValueAnimator.ofInt(0, 5).setDuration(ANIMATION_TIME.toLong())
        animator.addUpdateListener { valueAnimator ->
            try {
                val currentX = startX + (x - startX) * valueAnimator.animatedValue as Int / ANIMATION_STEPS
                val currentY = startY + (y - startY) * valueAnimator.animatedValue as Int / ANIMATION_STEPS
                bubbleParams.x = currentX.toInt()
                bubbleParams.x = if (bubbleParams.x < 0) 0 else bubbleParams.x
                bubbleParams.x = if (bubbleParams.x > sizeX - bubbleView!!.width) sizeX - bubbleView.width else bubbleParams.x
                bubbleParams.y = currentY.toInt()
                bubbleParams.y = if (bubbleParams.y < 0) 0 else bubbleParams.y
                bubbleParams.y = if (bubbleParams.y > sizeY - bubbleView.width) sizeY - bubbleView.width else bubbleParams.y
                windowManager!!.updateViewLayout(bubbleView, bubbleParams)
            } catch (exception: Exception) {
                Log.e(FloatingBubbleAnimator::class.java.simpleName, exception.message)
            }
        }
        animator.start()
    }

    class Builder {
        internal var bubbleView: View? = null
        internal var bubbleParams: WindowManager.LayoutParams? = null
        internal var windowManager: WindowManager? = null
        internal var sizeX = 0
        internal var sizeY = 0
        fun bubbleView(`val`: View?): Builder {
            bubbleView = `val`
            return this
        }

        fun bubbleParams(`val`: WindowManager.LayoutParams?): Builder {
            bubbleParams = `val`
            return this
        }

        fun windowManager(`val`: WindowManager?): Builder {
            windowManager = `val`
            return this
        }

        fun sizeX(`val`: Int): Builder {
            sizeX = `val`
            return this
        }

        fun sizeY(`val`: Int): Builder {
            sizeY = `val`
            return this
        }

        fun build(): FloatingBubbleAnimator {
            return FloatingBubbleAnimator(this)
        }
    }
}