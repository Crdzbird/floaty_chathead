package ni.devotion.floaty_head.floating_chathead

import android.graphics.*
import android.os.Build
import android.view.*
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.facebook.rebound.*
import ni.devotion.floaty_head.R
import ni.devotion.floaty_head.services.FloatingService

class Close(var chatHeads: ChatHeads): View(chatHeads.context) {
    private var params = WindowManager.LayoutParams(
        ChatHeads.CLOSE_SIZE + ChatHeads.CLOSE_ADDITIONAL_SIZE,
        ChatHeads.CLOSE_SIZE + ChatHeads.CLOSE_ADDITIONAL_SIZE,
            WindowManagerHelper.getLayoutFlag(),
        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        PixelFormat.TRANSLUCENT
    )

    private var gradientParams = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, WindowManagerHelper.dpToPx(150f))
    var springSystem = SpringSystem.create()
    var springY = springSystem.createSpring()
    var springX = springSystem.createSpring()
    var springAlpha = springSystem.createSpring()
    var springScale = springSystem.createSpring()
    val paint = Paint()
    val gradient = FrameLayout(context)
    private var bitmapBg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(FloatingService.instance.resources, R.drawable.close_bg), ChatHeads.CLOSE_SIZE, ChatHeads.CLOSE_SIZE, false)!!
    private val bitmapClose = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(FloatingService.instance.resources, R.drawable.close), WindowManagerHelper.dpToPx(28f), WindowManagerHelper.dpToPx(28f), false)!!

    fun hide() {
        val metrics = WindowManagerHelper.getScreenSize()
        springY.endValue = metrics.heightPixels.toDouble() + height
        springX.endValue = metrics.widthPixels.toDouble() / 2 - width / 2
        springAlpha.endValue = 0.0
    }

    fun show() {
        visibility = View.VISIBLE
        springAlpha.endValue = 1.0
    }

    private fun onPositionUpdate() {
        if (chatHeads.captured) {
            chatHeads.topChatHead!!.springX.endValue = springX.currentValue + width / 2 - chatHeads.topChatHead!!.width / 2 + 2
            chatHeads.topChatHead!!.springY.endValue = springY.currentValue + height / 2 - chatHeads.topChatHead!!.height / 2 + 2
        }
    }

    init {
        this.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
        visibility = View.INVISIBLE
        hide()
        springY.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                y = spring.currentValue.toFloat()
                if (chatHeads.captured && chatHeads.wasMoving) {
                    chatHeads.topChatHead!!.springY.currentValue = spring.currentValue
                }
                onPositionUpdate()
            }
        })
        springX.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                x = spring.currentValue.toFloat()
                onPositionUpdate()
            }
        })
        springScale.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                bitmapBg =  Bitmap.createScaledBitmap(BitmapFactory.decodeResource(FloatingService.instance.resources, R.drawable.close_bg), (spring.currentValue + ChatHeads.CLOSE_SIZE).toInt(), (spring.currentValue + ChatHeads.CLOSE_SIZE).toInt(), false)
                invalidate()
            }
        })
        springAlpha.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                gradient.alpha = spring.currentValue.toFloat()
            }
        })
        springScale.springConfig = SpringConfigs.CLOSE_SCALE
        springY.springConfig = SpringConfigs.CLOSE_Y
        params.gravity = Gravity.START or Gravity.TOP
        gradientParams.gravity = Gravity.BOTTOM
        gradient.background = ContextCompat.getDrawable(context, R.drawable.gradient_bg)
        springAlpha.currentValue = 0.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) z = 100f
        chatHeads.addView(this, params)
        chatHeads.addView(gradient, gradientParams)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(bitmapBg, width / 2 - bitmapBg.width.toFloat() / 2, height / 2 - bitmapBg.height.toFloat() / 2, paint)
        canvas?.drawBitmap(bitmapClose, width / 2 - bitmapClose.width.toFloat() / 2, height / 2 - bitmapClose.height.toFloat() / 2, paint)
    }
}