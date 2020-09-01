package ni.devotion.floaty_head

import android.content.Context
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import ni.devotion.floaty_head.floating_chathead.SpringConfigs

class FloatFragment(context: Context) : LinearLayout(context) {
    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()

    init {
        setupView()
    }

    private fun setupView() {
        context.setTheme(R.style.Theme_MaterialComponents_Light)
        inflate(context, R.layout.fragment_float, this)
        scaleSpring.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                scaleX = spring.currentValue.toFloat()
                scaleY = spring.currentValue.toFloat()
            }
        })
        scaleSpring.springConfig = SpringConfigs.CONTENT_SCALE
        scaleSpring.currentValue = 0.0
    }

    fun hideContent() {
        scaleSpring.endValue = 0.0
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }

    fun showContent() {
        scaleSpring.endValue = 1.0
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }
}