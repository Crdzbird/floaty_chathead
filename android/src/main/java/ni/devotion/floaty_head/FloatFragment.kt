package ni.devotion.floaty_head

import android.content.Context
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import ni.devotion.floaty_head.floating_chathead.SpringConfigs
import ni.devotion.floaty_head.R
import ni.devotion.floaty_head.utils.Managment.bodyView
import ni.devotion.floaty_head.utils.Managment.footerView
import ni.devotion.floaty_head.utils.Managment.headerView


class FloatFragment(context: Context) : LinearLayout(context) {
    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()
    private lateinit var content: LinearLayout

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
        content = findViewById<LinearLayout>(R.id.contentLayout)
        headerView?.let {
            content.addView(it)
        }
        bodyView?.let {
            content.addView(it)
        }
        footerView?.let {
            content.addView(it)
        }
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        content.removeAllViews()
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