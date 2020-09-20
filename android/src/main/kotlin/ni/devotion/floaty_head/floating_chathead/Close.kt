package ni.devotion.floaty_head.floating_chathead

import android.graphics.*
import android.os.Build
import android.view.*
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.facebook.rebound.*
import ni.devotion.floaty_head.R
import ni.devotion.floaty_head.services.FloatyIconService
import ni.devotion.floaty_head.utils.Managment

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
    private var bitmapBg: Bitmap? = null
    private var bitmapClose: Bitmap? = null

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
        bitmapBg = Managment.backgroundCloseIcon ?: Bitmap.createScaledBitmap(BitmapFactory.decodeResource(Managment.globalContext!!.resources, R.drawable.close_bg), ChatHeads.CLOSE_SIZE, ChatHeads.CLOSE_SIZE, false)
        Managment.backgroundCloseIcon?.let {
            bitmapBg = Bitmap.createScaledBitmap(it, ChatHeads.CLOSE_SIZE, ChatHeads.CLOSE_SIZE, false)
        }
        bitmapClose = Managment.closeIcon ?: Bitmap.createScaledBitmap(BitmapFactory.decodeResource(Managment.globalContext!!.resources, R.drawable.close), WindowManagerHelper.dpToPx(28f), WindowManagerHelper.dpToPx(28f), false)
        Managment.closeIcon?.let {
            bitmapClose = Bitmap.createScaledBitmap(it, WindowManagerHelper.dpToPx(28f), WindowManagerHelper.dpToPx(28f), false)
        }
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
                bitmapBg = Managment.backgroundCloseIcon ?: Bitmap.createScaledBitmap(BitmapFactory.decodeResource(Managment.globalContext!!.resources, R.drawable.close_bg), (spring.currentValue + ChatHeads.CLOSE_SIZE).toInt(), (spring.currentValue + ChatHeads.CLOSE_SIZE).toInt(), false)
                Managment.backgroundCloseIcon?.let {
                    bitmapBg = Bitmap.createScaledBitmap(it, (spring.currentValue + ChatHeads.CLOSE_SIZE).toInt(), (spring.currentValue + ChatHeads.CLOSE_SIZE).toInt(), false)
                }
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
        bitmapBg?.let {
            canvas?.drawBitmap(it, width / 2 - it.width.toFloat() / 2, height / 2 - it.height.toFloat() / 2, paint)
        }
        bitmapClose?.let {
            canvas?.drawBitmap(it, width / 2 - it.width.toFloat() / 2, height / 2 - it.height.toFloat() / 2, paint)
        }
    }
}