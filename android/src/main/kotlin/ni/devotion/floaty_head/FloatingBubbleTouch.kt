package ni.devotion.floaty_head

import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager

class FloatingBubbleTouch private constructor(builder: Builder) :
    OnTouchListener {
    private val sizeX: Int
    private val sizeY: Int
    private val bubbleView: View?
    private val removeBubbleView: View?
    private val expandableView: View?
    private val windowManager: WindowManager?
    private val listener: FloatingBubbleTouchListener?
    private val physics: FloatingBubbleTouchListener?
    private val removeBubbleSize: Int
    private val config: FloatingBubbleConfig?
    private val padding: Int
    private val marginBottom: Int
    private val bubbleParams: WindowManager.LayoutParams
    private val removeBubbleParams: WindowManager.LayoutParams
    private val expandableParams: WindowManager.LayoutParams
    private val removeBubbleStartSize: Int
    private val removeBubbleExpandedSize: Int
    private val animator: FloatingBubbleAnimator
    private var touchStartTime: Long = 0
    private var lastTouchTime: Long = 0
    private var expanded = false
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchStartTime = System.currentTimeMillis()
                listener?.onDown(motionEvent.rawX, motionEvent.rawY)
                if (sendEventToPhysics()) {
                    physics!!.onDown(motionEvent.rawX, motionEvent.rawY)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                lastTouchTime = System.currentTimeMillis()
                moveBubbleView(motionEvent)
                if (lastTouchTime - touchStartTime > TOUCH_CLICK_TIME) {
                    compressView()
                    showRemoveBubble(View.VISIBLE)
                }
                listener?.onMove(motionEvent.rawX, motionEvent.rawY)
                if (sendEventToPhysics()) {
                    physics!!.onMove(motionEvent.rawX, motionEvent.rawY)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                showRemoveBubble(View.GONE)
                lastTouchTime = System.currentTimeMillis()
                if (lastTouchTime - touchStartTime < TOUCH_CLICK_TIME) {
                    toggleView()
                    listener?.onTap(expanded)
                    if (sendEventToPhysics()) {
                        physics!!.onTap(expanded)
                    }
                } else {
                    val isRemoved = checkRemoveBubble()
                    listener?.onUp(motionEvent.rawX, motionEvent.rawY)
                    if (!isRemoved && sendEventToPhysics()) {
                        physics!!.onUp(motionEvent.rawX, motionEvent.rawY)
                    }
                }
            }
        }
        return true
    }

    private fun moveBubbleView(motionEvent: MotionEvent) {
        val halfClipSize = bubbleView!!.width / 2.toFloat()
        val clipSize = bubbleView.width.toFloat()
        var leftX = motionEvent.rawX - halfClipSize
        leftX = if (leftX > sizeX - clipSize) sizeX - clipSize else leftX
        leftX = if (leftX < 0) 0f else leftX
        var topY = motionEvent.rawY - halfClipSize
        topY = if (topY > sizeY - clipSize) sizeY - clipSize else topY
        topY = if (topY < 0) 0f else topY
        bubbleParams.x = leftX.toInt()
        bubbleParams.y = topY.toInt()
        handleRemove()
        windowManager!!.updateViewLayout(bubbleView, bubbleParams)
        windowManager.updateViewLayout(removeBubbleView, removeBubbleParams)
    }

    private fun handleRemove() {
        if (isInsideRemoveBubble) {
            removeBubbleParams.height = removeBubbleExpandedSize
            removeBubbleParams.width = removeBubbleExpandedSize
            removeBubbleParams.x = (sizeX - removeBubbleParams.width) / 2
            removeBubbleParams.y = sizeY - removeBubbleParams.height - marginBottom
            bubbleParams.x =
                removeBubbleParams.x + (removeBubbleExpandedSize - bubbleView!!.width) / 2
            bubbleParams.y =
                removeBubbleParams.y + (removeBubbleExpandedSize - bubbleView.width) / 2
        } else {
            removeBubbleParams.height = removeBubbleStartSize
            removeBubbleParams.width = removeBubbleStartSize
            removeBubbleParams.x = (sizeX - removeBubbleParams.width) / 2
            removeBubbleParams.y = sizeY - removeBubbleParams.height - marginBottom
        }
    }

    private val isInsideRemoveBubble: Boolean
        get() {
            val bubbleSize =
                if (removeBubbleView!!.width == 0) removeBubbleStartSize else removeBubbleView.width
            val top = removeBubbleParams.y
            val right = removeBubbleParams.x + bubbleSize
            val bottom = removeBubbleParams.y + bubbleSize
            val left = removeBubbleParams.x
            val centerX = bubbleParams.x + bubbleView!!.width / 2
            val centerY = bubbleParams.y + bubbleView.width / 2
            return centerX in (left + 1) until right && centerY > top && centerY < bottom
        }

    private fun checkRemoveBubble(): Boolean {
        if (isInsideRemoveBubble) {
            listener?.onRemove()
            if (sendEventToPhysics()) {
                physics!!.onRemove()
            }
            return true
        }
        return false
    }

    private fun sendEventToPhysics(): Boolean {
        return config!!.isPhysicsEnabled && physics != null
    }

    private fun showRemoveBubble(visibility: Int) {
        removeBubbleView!!.visibility = visibility
    }

    fun setState(state: Boolean) {
        expanded = state
        if (expanded) {
            expandView()
        } else {
            compressView()
        }
    }

    private fun toggleView() {
        expanded = !expanded
        setState(expanded)
    }

    private fun compressView() {
        expandableView!!.visibility = View.GONE
    }

    private fun expandView() {
        var x = 0
        val y = padding
        when (config!!.gravity) {
            Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL -> x =
                (sizeX - bubbleView!!.width) / 2
            Gravity.LEFT, Gravity.START -> x = padding
            Gravity.RIGHT, Gravity.END -> x = sizeX - bubbleView!!.width - padding
        }
        animator.animate(x.toFloat(), y.toFloat())
        expandableView!!.visibility = View.VISIBLE
        expandableParams.y = y + bubbleView!!.width
        windowManager!!.updateViewLayout(expandableView, expandableParams)
    }

    class Builder {
        var sizeX = 0
        var sizeY = 0
        var bubbleView: View? = null
        var removeBubbleView: View? = null
        var expandableView: View? = null
        private var logger: FloatingBubbleLogger? = null
        var windowManager: WindowManager? = null
        var listener: FloatingBubbleTouchListener? = null
        var removeBubbleSize = 0
        var physics: FloatingBubbleTouchListener? = null
        var config: FloatingBubbleConfig? = null
        var padding = 0
        var marginBottom = 0
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

        fun removeBubbleView(`val`: View?): Builder {
            removeBubbleView = `val`
            return this
        }

        fun expandableView(`val`: View?): Builder {
            expandableView = `val`
            return this
        }

        fun logger(`val`: FloatingBubbleLogger?): Builder {
            logger = `val`
            return this
        }

        fun windowManager(`val`: WindowManager?): Builder {
            windowManager = `val`
            return this
        }

        fun build(): FloatingBubbleTouch {
            return FloatingBubbleTouch(this)
        }

        fun removeBubbleSize(`val`: Int): Builder {
            removeBubbleSize = `val`
            return this
        }

        fun physics(`val`: FloatingBubbleTouchListener?): Builder {
            physics = `val`
            return this
        }

        fun listener(`val`: FloatingBubbleTouchListener?): Builder {
            listener = `val`
            return this
        }

        fun config(`val`: FloatingBubbleConfig?): Builder {
            config = `val`
            return this
        }

        fun padding(`val`: Int): Builder {
            padding = `val`
            return this
        }

        fun marginBottom(`val`: Int): Builder {
            marginBottom = `val`
            return this
        }
    }

    companion object {
        private const val TOUCH_CLICK_TIME = 250
        private const val EXPANSION_FACTOR = 1.25f
    }

    init {
        padding = builder.padding
        config = builder.config
        removeBubbleSize = builder.removeBubbleSize
        physics = builder.physics
        listener = builder.listener
        windowManager = builder.windowManager
        expandableView = builder.expandableView
        removeBubbleView = builder.removeBubbleView
        bubbleView = builder.bubbleView
        sizeY = builder.sizeY
        sizeX = builder.sizeX
        marginBottom = builder.marginBottom
        bubbleParams = bubbleView!!.layoutParams as WindowManager.LayoutParams
        removeBubbleParams = removeBubbleView!!.layoutParams as WindowManager.LayoutParams
        expandableParams = expandableView!!.layoutParams as WindowManager.LayoutParams
        removeBubbleStartSize = removeBubbleSize
        removeBubbleExpandedSize =
            (EXPANSION_FACTOR * removeBubbleSize).toInt()
        animator = FloatingBubbleAnimator.Builder()
            .sizeX(sizeX)
            .sizeY(sizeY)
            .windowManager(windowManager)
            .bubbleView(bubbleView)
            .bubbleParams(bubbleParams)
            .build()
    }
}