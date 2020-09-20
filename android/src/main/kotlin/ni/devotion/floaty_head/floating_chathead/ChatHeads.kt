package ni.devotion.floaty_head.floating_chathead

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.view.VelocityTracker
import com.facebook.rebound.Spring
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.SpringChain
import java.util.*
import kotlin.math.*
import android.app.ActivityManager
import ni.devotion.floaty_head.FloatFragment
import ni.devotion.floaty_head.R
import ni.devotion.floaty_head.services.FloatyContentJobService
import ni.devotion.floaty_head.services.FloatyIconService
import ni.devotion.floaty_head.utils.Managment


class ChatHeads(context: Context) : View.OnTouchListener, FrameLayout(context) {
    companion object {
        val CHAT_HEAD_OUT_OF_SCREEN_X: Int = WindowManagerHelper.dpToPx(10f)
        val CHAT_HEAD_SIZE: Int = WindowManagerHelper.dpToPx(64f)
        val CHAT_HEAD_PADDING: Int = WindowManagerHelper.dpToPx(6f)
        val CHAT_HEAD_EXPANDED_PADDING: Int = WindowManagerHelper.dpToPx(4f)
        val CHAT_HEAD_EXPANDED_MARGIN_TOP: Float = WindowManagerHelper.dpToPx(4f).toFloat()
        val CLOSE_SIZE = WindowManagerHelper.dpToPx(64f)
        val CLOSE_CAPTURE_DISTANCE = WindowManagerHelper.dpToPx(100f)
        val CLOSE_ADDITIONAL_SIZE = WindowManagerHelper.dpToPx(24f)
        const val CHAT_HEAD_DRAG_TOLERANCE: Float = 20f
        fun distance(x1: Float, x2: Float, y1: Float, y2: Float): Float {
            return ((x1 - x2).pow(2) + (y1-y2).pow(2))
        }
    }
    var wasMoving = false
    var captured = false
    var movingOutOfClose = false
    private var initialX = 0.0f
    private var initialY = 0.0f
    private var initialTouchX = 0.0f
    private var initialTouchY = 0.0f
    private var initialVelocityX = 0.0
    private var initialVelocityY = 0.0
    private var lastY = 0.0
    private var moving = false
    private var toggled = false
    private var motionTrackerUpdated = false
    private var collapsing = false
    private var blockAnim = false
    private var horizontalSpringChain: SpringChain? = null
    private var verticalSpringChain: SpringChain? = null
    private var isOnRight = false
    private var velocityTracker: VelocityTracker? = null
    private var motionTracker = LinearLayout(context)
    var topChatHead: ChatHead? = null
    var content = FloatFragment(context)
    private var close = Close(this)
    var chatHeads = ArrayList<ChatHead>()

    private var motionTrackerParams = WindowManager.LayoutParams(
        CHAT_HEAD_SIZE,
        CHAT_HEAD_SIZE + 16,
            WindowManagerHelper.getLayoutFlag(),
        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        PixelFormat.TRANSLUCENT
    )

    private var params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
            WindowManagerHelper.getLayoutFlag(),
        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        PixelFormat.TRANSLUCENT
    )

    init {
        context.setTheme(R.style.Theme_MaterialComponents_Light)
        params.gravity = Gravity.START or Gravity.TOP
        params.dimAmount = 0.7f
        motionTrackerParams.gravity = Gravity.START or Gravity.TOP
        FloatyContentJobService.instance?.windowManager?.addView(motionTracker, motionTrackerParams)
        FloatyContentJobService.instance?.windowManager?.addView(this, params)
        this.addView(content)
        motionTracker.setOnTouchListener(this)
        this.setOnTouchListener{ v, event ->
            v.performClick()
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (v == this) {
                        collapse()
                    }
                }

            }
            return@setOnTouchListener false
        }
    }

    fun setTop(chatHead: ChatHead) {
        topChatHead?.isTop = false
        chatHead.isTop = true
        topChatHead = chatHead
    }

    fun fixPositions(animation: Boolean = true) {
        if (topChatHead == null) return
        val metrics = WindowManagerHelper.getScreenSize()
        val newX =  if (isOnRight) metrics.widthPixels - topChatHead!!.width + CHAT_HEAD_OUT_OF_SCREEN_X.toDouble() else -CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
        val newY = initialY.toDouble()
        if (animation) {
            topChatHead!!.springX.endValue = newX
            topChatHead!!.springY.endValue = newY
        } else {
            topChatHead!!.springX.currentValue = newX
            topChatHead!!.springY.currentValue = newY
        }
    }

    private fun destroySpringChains() {
        horizontalSpringChain?.let {
            for (spring in it.allSprings) {
                spring.destroy()
            }
        }
        verticalSpringChain?.let {
            for (spring in it.allSprings) {
                spring.destroy()
            }
        }
        verticalSpringChain = null
        horizontalSpringChain = null
    }

    @SuppressLint("NewApi")
    private fun resetSpringChains() {
       destroySpringChains()
        horizontalSpringChain = SpringChain.create(0, 0, 200, 15)
        verticalSpringChain = SpringChain.create(0, 0, 200, 15)
        chatHeads.forEachIndexed { index, element ->
            element.z = index.toFloat()
            if (element.isTop) {
                horizontalSpringChain!!.addSpring(object : SimpleSpringListener() { })
                verticalSpringChain!!.addSpring(object : SimpleSpringListener() { })

                element.z = chatHeads.size.toFloat()
                horizontalSpringChain!!.setControlSpringIndex(index)
                verticalSpringChain!!.setControlSpringIndex(index)
            } else {
                horizontalSpringChain!!.addSpring(object : SimpleSpringListener() {
                    override fun onSpringUpdate(spring: Spring?) {
                        if (!toggled && !blockAnim) {
                            if (collapsing) {
                                element.springX.endValue = spring!!.endValue + (chatHeads.size - 1 - index) * CHAT_HEAD_PADDING * if (isOnRight) 1 else -1
                            } else {
                                element.springX.currentValue = spring!!.currentValue + (chatHeads.size - 1 - index) * CHAT_HEAD_PADDING * if (isOnRight) 1 else -1
                            }
                        }
                    }
                })
                verticalSpringChain!!.addSpring(object : SimpleSpringListener() {
                    override fun onSpringUpdate(spring: Spring?) {
                        if (!toggled && !blockAnim) {
                            element.springY.currentValue = spring!!.currentValue
                        }
                    }
                })
            }
        }
    }

    fun add(): ChatHead {
        chatHeads.forEach {
            it.visibility = View.VISIBLE
        }
        val chatHead = ChatHead(this)
        chatHeads.add(chatHead)
        var lx = -CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
        var ly = 0.0
        if (topChatHead != null) {
            lx = topChatHead!!.springX.currentValue
            ly = topChatHead!!.springY.currentValue
        }

        setTop(chatHead)
        destroySpringChains()
        resetSpringChains()

        blockAnim = true

        chatHeads.forEachIndexed { index, element ->
            element.springX.currentValue = lx + (chatHeads.size - 1 - index) * CHAT_HEAD_PADDING * if (isOnRight) 1 else -1
            element.springY.currentValue = ly
        }

        motionTrackerParams.x = chatHead.springX.currentValue.toInt()
        motionTrackerParams.y = chatHead.springY.currentValue.toInt()
        motionTrackerParams.flags = motionTrackerParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()

        FloatyContentJobService.instance?.windowManager?.updateViewLayout(motionTracker, motionTrackerParams)

        return chatHead
    }

    fun collapse() {
        toggled = false
        collapsing = true

        fixPositions()

        chatHeads.forEach {
            it.isActive = false
        }
        content.hideContent()
        motionTrackerParams.flags = motionTrackerParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
        FloatyContentJobService.instance?.windowManager?.updateViewLayout(motionTracker, motionTrackerParams)

        params.flags = ((params.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()) and WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL.inv() or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        FloatyContentJobService.instance?.windowManager?.updateViewLayout(this, params)
    }

    fun changeContent() {
        val chatHead = chatHeads.find { it.isActive }!!

        //content.messagesView.removeAllViews()

//        for (message in chatHead.messages) {
//            content.addMessage(message)
//        }
    }

    fun getRunningServiceInfo(serviceClass: Class<*>, context: Context): ActivityManager.RunningServiceInfo? {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return service
            }
        }
        return null
    }

    fun hideChatHeads(isClosed:Boolean = false) {
        if(isClosed){
            close.hide()
            postDelayed({
                topChatHead?.let {
                    it.springY.currentValue = 0.0
                    it.springX.currentValue = 0.0
                }
                FloatyContentJobService.instance!!.closeWindow(true)
            }, 300)
        }else{
            close.hide()
            postDelayed({
                topChatHead?.let {
                    it.springY.currentValue = 0.0
                    it.springX.currentValue = 0.0
                }
            }, 300)
        }
    }

    fun onSpringUpdate(chatHead: ChatHead, spring: Spring, totalVelocity: Int) {
        val metrics = WindowManagerHelper.getScreenSize()
        if (topChatHead != null && chatHead == topChatHead!!) {
            if (horizontalSpringChain != null && spring == chatHead.springX) {
                horizontalSpringChain!!.controlSpring.currentValue = spring.currentValue
            }
            if (verticalSpringChain != null && spring == chatHead.springY) {
                verticalSpringChain!!.controlSpring.currentValue = spring.currentValue
            }
        }
        var tmpChatHead: ChatHead? = null
        if (collapsing) tmpChatHead = topChatHead!!
        else if (chatHead.isActive) tmpChatHead = chatHead
        if (tmpChatHead != null) {
            content.x = tmpChatHead.springX.currentValue.toFloat() - metrics.widthPixels.toFloat() + ((chatHeads.size - 1 - chatHeads.indexOf(tmpChatHead)) * (tmpChatHead.width + CHAT_HEAD_EXPANDED_PADDING)) + tmpChatHead.width
            content.y = tmpChatHead.springY.currentValue.toFloat() - CHAT_HEAD_EXPANDED_MARGIN_TOP
            content.pivotX = metrics.widthPixels.toFloat() - chatHead.width / 2 - ((chatHeads.size - 1 - chatHeads.indexOf(tmpChatHead)) * (tmpChatHead.width + CHAT_HEAD_EXPANDED_PADDING))
        }
        content.pivotY = chatHead.height.toFloat()
        if (!moving && distance(close.x, topChatHead!!.springX.currentValue.toFloat(), close.y, topChatHead!!.springY.currentValue.toFloat()) < CLOSE_CAPTURE_DISTANCE * CLOSE_CAPTURE_DISTANCE && !captured && close.visibility == View.VISIBLE) {
            topChatHead!!.springX.springConfig = SpringConfigs.CAPTURING
            topChatHead!!.springY.springConfig = SpringConfigs.CAPTURING
            topChatHead!!.springX.endValue = close.springX.endValue
            topChatHead!!.springY.endValue = close.springY.endValue
            postDelayed({
                hideChatHeads(false)
            }, 300)
            captured = true
        }
        if (wasMoving) {
            motionTrackerParams.x = if (isOnRight) metrics.widthPixels - chatHead.width else 0
            lastY = chatHead.springY.currentValue
            if (abs(chatHead.springY.velocity) > 3000 && (chatHead.springX.currentValue > metrics.widthPixels - chatHead.width + CHAT_HEAD_OUT_OF_SCREEN_X / 2 || chatHead.springX.currentValue < -CHAT_HEAD_OUT_OF_SCREEN_X / 2) && abs(initialVelocityX) > 3000) {
                chatHead.springY.velocity = 3000.0 * if (initialVelocityY < 0) -1 else 1
            }
            if ((chatHead.springX.currentValue < -CHAT_HEAD_OUT_OF_SCREEN_X / 2 && initialVelocityX < -3000 || chatHead.springX.currentValue > metrics.widthPixels - chatHead.width  + CHAT_HEAD_OUT_OF_SCREEN_X / 2) && abs(initialVelocityY) < abs(initialVelocityX)) {
                chatHead.springY.velocity = 0.0
            }
            if (abs(chatHead.springY.velocity) > 500) {
                if (chatHead.springY.currentValue < 0) {
                    chatHead.springY.velocity = -500.0
                } else if (chatHead.springY.currentValue > metrics.heightPixels) {
                    chatHead.springY.velocity = 500.0
                }
            }

            if (!moving) {
                if (spring === chatHead.springX) {
                    val xPosition = chatHead.springX.currentValue
                    if (xPosition + chatHead.width > metrics.widthPixels && chatHead.springX.velocity > 0) {
                        val newPos = metrics.widthPixels - chatHead.width + CHAT_HEAD_OUT_OF_SCREEN_X
                        chatHead.springX.springConfig = SpringConfigs.NOT_DRAGGING
                        chatHead.springX.endValue = newPos.toDouble()
                        isOnRight = true
                    } else if (xPosition < 0 && chatHead.springX.velocity < 0) {
                        chatHead.springX.springConfig = SpringConfigs.NOT_DRAGGING
                        chatHead.springX.endValue = -CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                        isOnRight = false
                    }
                } else if (spring === chatHead.springY) {
                    val yPosition = chatHead.springY.currentValue
                    if (yPosition + chatHead.height > metrics.heightPixels && chatHead.springY.velocity > 0) {
                        chatHead.springY.springConfig = SpringConfigs.NOT_DRAGGING
                        chatHead.springY.endValue = metrics.heightPixels - chatHead.height.toDouble() -
                                WindowManagerHelper.dpToPx(25f)
                    } else if (yPosition < 0 && chatHead.springY.velocity < 0) {
                        chatHead.springY.springConfig = SpringConfigs.NOT_DRAGGING
                        chatHead.springY.endValue = 0.0
                    }
                }
            }

            if (abs(totalVelocity) % 10 == 0 && !moving) {
                motionTrackerParams.y = topChatHead!!.springY.currentValue.toInt()
                FloatyContentJobService.instance?.windowManager?.updateViewLayout(motionTracker, motionTrackerParams)
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val metrics = WindowManagerHelper.getScreenSize()
        if (topChatHead == null) return true
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                topChatHead?.let {
                    initialX = it.springX.currentValue.toFloat()
                    initialY = it.springY.currentValue.toFloat()
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    wasMoving = false
                    collapsing = false
                    blockAnim = false
                    close.show()
                    it.scaleX = 0.9f
                    it.scaleY = 0.9f
                    it.springX.springConfig = SpringConfigs.DRAGGING
                    it.springY.springConfig = SpringConfigs.DRAGGING
                    it.springX.setAtRest()
                    it.springY.setAtRest()
                }
                motionTrackerUpdated = false
                when (velocityTracker) {
                    null -> velocityTracker = VelocityTracker.obtain()
                    else -> velocityTracker?.clear()
                }
                velocityTracker?.addMovement(event)
            }
            MotionEvent.ACTION_UP -> {
                if (moving) wasMoving = true
                postDelayed({
                    close.hide()
                    if (captured) {
                        content.removeAllViews()
                        hideChatHeads(true)
                    }
                }, 200)
                if (captured) return true
                if (!moving) {
                    if (!toggled) {
                        toggled = true
                        chatHeads.forEachIndexed { index, it ->
                            it.springX.springConfig = SpringConfigs.NOT_DRAGGING
                            it.springY.springConfig = SpringConfigs.NOT_DRAGGING
                            it.springY.endValue = CHAT_HEAD_EXPANDED_MARGIN_TOP.toDouble()
                            it.springX.endValue = metrics.widthPixels - topChatHead!!.width.toDouble() - (chatHeads.size - 1 - index) * (it.width + CHAT_HEAD_EXPANDED_PADDING).toDouble()
                        }
                        motionTrackerParams.flags = motionTrackerParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        FloatyContentJobService.instance?.windowManager?.updateViewLayout(motionTracker, motionTrackerParams)
                        params.flags = (params.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()) or WindowManager.LayoutParams.FLAG_DIM_BEHIND or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
                        FloatyContentJobService.instance?.windowManager?.updateViewLayout(this, params)
                        topChatHead!!.isActive = true
                        changeContent()
                        android.os.Handler().postDelayed(
                            {
                                content.showContent()
                            }, 200
                        )
                    }
                } else if (!toggled) {
                    moving = false
                    var xVelocity = velocityTracker!!.xVelocity.toDouble()
                    val yVelocity = velocityTracker!!.yVelocity.toDouble()
                    var maxVelocityX = 0.0
                    velocityTracker?.recycle()
                    velocityTracker = null
                    if (xVelocity < -3500) {
                        val newVelocity = ((-topChatHead!!.springX.currentValue -  CHAT_HEAD_OUT_OF_SCREEN_X) * SpringConfigs.DRAGGING.friction)
                        maxVelocityX = newVelocity - 5000
                        if (xVelocity > maxVelocityX)
                            xVelocity = newVelocity - 500
                    } else if (xVelocity > 3500) {
                        val newVelocity = ((metrics.widthPixels - topChatHead!!.springX.currentValue - topChatHead!!.width + CHAT_HEAD_OUT_OF_SCREEN_X) * SpringConfigs.DRAGGING.friction)
                        maxVelocityX = newVelocity + 5000
                        if (maxVelocityX > xVelocity)
                            xVelocity = newVelocity + 500
                    } else if (yVelocity > 20 || yVelocity < -20) {
                        topChatHead!!.springX.springConfig = SpringConfigs.NOT_DRAGGING
                        if (topChatHead!!.x >= metrics.widthPixels / 2) {
                            topChatHead!!.springX.endValue = metrics.widthPixels - topChatHead!!.width + CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                            isOnRight = true
                        } else {
                            topChatHead!!.springX.endValue = -CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                            isOnRight = false
                        }
                    } else {
                        topChatHead!!.springX.springConfig = SpringConfigs.NOT_DRAGGING
                        topChatHead!!.springY.springConfig = SpringConfigs.NOT_DRAGGING
                        if (topChatHead!!.x >= metrics.widthPixels / 2) {
                            topChatHead!!.springX.endValue = metrics.widthPixels - topChatHead!!.width +
                                    CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                            topChatHead!!.springY.endValue = topChatHead!!.y.toDouble()
                            isOnRight = true
                        } else {
                            topChatHead!!.springX.endValue = -CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                            topChatHead!!.springY.endValue = topChatHead!!.y.toDouble()
                            isOnRight = false
                        }
                    }
                    if (xVelocity < 0) {
                        topChatHead!!.springX.velocity = max(xVelocity, maxVelocityX)
                    } else {
                        topChatHead!!.springX.velocity = min(xVelocity, maxVelocityX)
                    }
                    initialVelocityX = topChatHead!!.springX.velocity
                    initialVelocityY = topChatHead!!.springY.velocity
                    topChatHead!!.springY.velocity = yVelocity
                }
                topChatHead!!.scaleX = 1f
                topChatHead!!.scaleY = 1f
            }
            MotionEvent.ACTION_MOVE -> {
                if (distance(initialTouchX, event.rawX, initialTouchY, event.rawY) > CHAT_HEAD_DRAG_TOLERANCE.pow(2)) {
                    moving = true
                }
                velocityTracker?.addMovement(event)
                if (moving) {
                    close.springX.endValue = (metrics.widthPixels / 2) + (((event.rawX + topChatHead!!.width / 2) / 7) - metrics.widthPixels / 2 / 7) - close.width.toDouble() / 2
                    close.springY.endValue = (metrics.heightPixels - CLOSE_SIZE) + max(((event.rawY + close.height / 2) / 10) - metrics.heightPixels / 10, -WindowManagerHelper.dpToPx(30f).toFloat()) - WindowManagerHelper.dpToPx(60f).toDouble()
                    if (distance(close.x + close.width / 2, event.rawX, close.y + close.height / 2, event.rawY) < CLOSE_CAPTURE_DISTANCE * CLOSE_CAPTURE_DISTANCE) {
                        topChatHead!!.springX.springConfig = SpringConfigs.CAPTURING
                        topChatHead!!.springY.springConfig = SpringConfigs.CAPTURING
                        close.springScale.endValue = CLOSE_ADDITIONAL_SIZE.toDouble()
                        captured = true
                    } else if (captured) {
                        topChatHead!!.springX.springConfig = SpringConfigs.CAPTURING
                        topChatHead!!.springY.springConfig = SpringConfigs.CAPTURING
                        close.springScale.endValue = 0.0
                        topChatHead!!.springX.endValue = initialX + (event.rawX - initialTouchX).toDouble()
                        topChatHead!!.springY.endValue = initialY + (event.rawY - initialTouchY).toDouble()
                        captured = false
                        movingOutOfClose = true
                        postDelayed({ movingOutOfClose = false }, 100)
                    } else if (!movingOutOfClose) {
                        topChatHead!!.springX.springConfig = SpringConfigs.DRAGGING
                        topChatHead!!.springY.springConfig = SpringConfigs.DRAGGING
                        topChatHead!!.springX.currentValue = initialX + (event.rawX - initialTouchX).toDouble()
                        topChatHead!!.springY.currentValue = initialY + (event.rawY - initialTouchY).toDouble()
                        velocityTracker?.computeCurrentVelocity(2000)
                    }
                }
            }
        }
        return true
    }
}