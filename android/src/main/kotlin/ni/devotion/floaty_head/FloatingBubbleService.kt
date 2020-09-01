package ni.devotion.floaty_head

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import ni.devotion.floaty_head.FloatingBubbleConfig.Companion.getDefault
import kotlin.math.roundToInt

open class FloatingBubbleService : Service() {
    companion object {
        protected val TAG = FloatingBubbleService::class.java.simpleName
    }
    protected var logger: FloatingBubbleLogger? = null
    protected var windowManager: WindowManager? = null
    private var mWakeLock: PowerManager.WakeLock? = null
    protected var inflater: LayoutInflater? = null
    protected var windowSize = Point()
    protected var bubbleView: View? = null
    protected var removeBubbleView: View? = null
    protected var expandableView: View? = null
    protected var bubbleParams: WindowManager.LayoutParams? = null
    protected var removeBubbleParams: WindowManager.LayoutParams? = null
    protected var expandableParams: WindowManager.LayoutParams? = null
    private var config: FloatingBubbleConfig? = null
    private var physics: FloatingBubblePhysics? = null
    private var touch: FloatingBubbleTouch? = null
    private var binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService() = this@FloatingBubbleService
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "floaty_head:service")
        logger = FloatingBubbleLogger().setDebugEnabled(true).setTag(TAG)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mWakeLock?.acquire(30 * 60 * 1000L)
        intent ?: return START_NOT_STICKY
        removeAllViews()
        setupWindowManager()
        setupViews()
        setTouchListener()
        return super.onStartCommand(intent, flags, START_STICKY)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        removeAllViews()
    }

    private fun removeAllViews() {
        windowManager ?: return
        windowManager?.let{
            bubbleView?.let{ bv ->
                it.removeView(bv)
                bubbleView = null
            }
            removeBubbleView?.let{ rb ->
                it.removeView(rb)
                removeBubbleView = null
            }
            bubbleView?.let{ ev ->
                it.removeView(ev)
                expandableView = null
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun setupWindowManager() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        setLayoutInflater()
        windowManager!!.defaultDisplay.getSize(windowSize)
    }

    protected fun setLayoutInflater(): LayoutInflater? {
        inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater
    }

    protected fun setupViews() {
        config = getConfig()
        val padding = dpToPixels(config!!.paddingDp)
        val iconSize = dpToPixels(config!!.bubbleIconDp)
        val bottomMargin = expandableViewBottomMargin
        bubbleView = inflater!!.inflate(R.layout.floating_bubble_view, null)
        removeBubbleView = inflater!!.inflate(R.layout.floating_remove_bubble_view, null)
        expandableView = inflater!!.inflate(R.layout.floating_expandable_view, null)
        removeBubbleParams = defaultWindowParams
        removeBubbleParams!!.gravity = Gravity.TOP or Gravity.START
        removeBubbleParams!!.width = dpToPixels(config!!.removeBubbleIconDp)
        removeBubbleParams!!.height = dpToPixels(config!!.removeBubbleIconDp)
        removeBubbleParams!!.x = (windowSize.x - removeBubbleParams!!.width) / 2
        removeBubbleParams!!.y = windowSize.y - removeBubbleParams!!.height - bottomMargin
        removeBubbleView!!.visibility = View.GONE
        removeBubbleView!!.alpha = config!!.removeBubbleAlpha
        windowManager!!.addView(removeBubbleView, removeBubbleParams)
        expandableParams = getDefaultWindowParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        expandableParams!!.height = windowSize.y - iconSize - bottomMargin
        expandableParams!!.gravity = Gravity.TOP or Gravity.START
        expandableView!!.visibility = View.GONE
        (expandableView as LinearLayoutCompat?)!!.gravity = config!!.gravity
        expandableView!!.setPadding(padding, padding, padding, padding)
        windowManager!!.addView(expandableView, expandableParams)
        bubbleParams = defaultWindowParams
        bubbleParams!!.gravity = Gravity.TOP or Gravity.START
        bubbleParams!!.width = iconSize
        bubbleParams!!.height = iconSize
        windowManager!!.addView(bubbleView, bubbleParams)
        config?.removeBubbleIcon?.let {
            (removeBubbleView as AppCompatImageView).setImageDrawable(it)
        }
        config?.bubbleIcon?.let {
            (bubbleView as AppCompatImageView).setImageDrawable(it)
        }
        val card = expandableView!!.findViewById<View>(R.id.expandableViewCard) as CardView
        card.radius = dpToPixels(config!!.borderRadiusDp).toFloat()
        val triangle = expandableView!!.findViewById<View>(R.id.expandableViewTriangle) as AppCompatImageView
        val container = expandableView!!.findViewById<View>(R.id.expandableViewContainer) as LinearLayoutCompat
        if (config!!.expandableView != null) {
            triangle.setColorFilter(config!!.triangleColor)
            val params = triangle.layoutParams as MarginLayoutParams
            params.leftMargin = dpToPixels((config!!.bubbleIconDp - 16) / 2)
            params.rightMargin = dpToPixels((config!!.bubbleIconDp - 16) / 2)
            triangle.visibility = View.VISIBLE
            container.visibility = View.VISIBLE
            card.visibility = View.VISIBLE
            container.setBackgroundColor(config!!.expandableColor)
            container.removeAllViews()
            container.addView(config!!.expandableView)
        } else {
            triangle.visibility = View.GONE
            container.visibility = View.GONE
            card.visibility = View.GONE
        }
    }

    protected open fun getConfig(): FloatingBubbleConfig? {
        return getDefault(context)
    }

    protected fun setTouchListener() {
        physics = FloatingBubblePhysics.Builder()
            .sizeX(windowSize.x)
            .sizeY(windowSize.y)
            .bubbleView(bubbleView)
            .config(config)
            .windowManager(windowManager)
            .build()
        touch = FloatingBubbleTouch.Builder()
            .sizeX(windowSize.x)
            .sizeY(windowSize.y)
            .listener(touchListener)
            .physics(physics)
            .bubbleView(bubbleView)
            .removeBubbleSize(dpToPixels(config!!.removeBubbleIconDp))
            .windowManager(windowManager)
            .expandableView(expandableView)
            .removeBubbleView(removeBubbleView)
            .config(config)
            .marginBottom(expandableViewBottomMargin)
            .padding(dpToPixels(config!!.paddingDp))
            .build()
        bubbleView!!.setOnTouchListener(touch)
    }

    val touchListener: FloatingBubbleTouchListener
        get() = object : DefaultFloatingBubbleTouchListener() {
            override fun onRemove() {
                stopSelf()
            }
        }

    protected val defaultWindowParams: WindowManager.LayoutParams
        protected get() = getDefaultWindowParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)

    protected fun getDefaultWindowParams(width: Int, height: Int): WindowManager.LayoutParams {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams(
                width,
                height,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            return WindowManager.LayoutParams(
                width,
                height,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
    }

    protected fun getInflaterBubble(): LayoutInflater? {
        return if (inflater == null) setLayoutInflater() else inflater
    }

    protected val context: Context
        protected get() = applicationContext

    protected fun setState(expanded: Boolean) {
        touch!!.setState(expanded)
    }

    private val expandableViewBottomMargin: Int
        private get() {
            val resources = context.resources
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            var navBarHeight = 0
            if (resourceId > 0) {
                navBarHeight = resources.getDimensionPixelSize(resourceId)
            }
            return navBarHeight
        }

    private fun dpToPixels(dpSize: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dpSize * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT).toFloat()).roundToInt()
    }
}