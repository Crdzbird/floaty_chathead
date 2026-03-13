package ni.devotion.floaty_head

import android.content.Context
import android.graphics.Color
import android.widget.FrameLayout
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import io.flutter.embedding.android.FlutterTextureView
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import ni.devotion.floaty_head.floating_chathead.SpringConfigs

class FlutterContentPanel(context: Context) : FrameLayout(context) {
    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()
    private var flutterView: FlutterView? = null

    init {
        scaleSpring.springConfig = SpringConfigs.CONTENT_SCALE
        scaleSpring.currentValue = 0.0
        scaleSpring.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                scaleX = spring.currentValue.toFloat()
                scaleY = spring.currentValue.toFloat()
            }
        })
    }

    fun attachEngine(engine: FlutterEngine) {
        val textureView = FlutterTextureView(context)
        flutterView = FlutterView(context, textureView).apply {
            attachToFlutterEngine(engine)
            setFitsSystemWindows(true)
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(Color.TRANSPARENT)
        }
        addView(
            flutterView,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
        )
        engine.lifecycleChannel.appIsResumed()
    }

    fun detachEngine() {
        flutterView?.detachFromFlutterEngine()
        removeAllViews()
        flutterView = null
    }

    fun showContent() {
        scaleSpring.endValue = 1.0
    }

    fun hideContent() {
        scaleSpring.endValue = 0.0
    }
}
