package ni.devotion.floaty_head.floating_chathead

import com.facebook.rebound.SpringConfig

object SpringConfigs {
    val NOT_DRAGGING = SpringConfig.fromOrigamiTensionAndFriction(60.0, 7.5)
    val CAPTURING = SpringConfig.fromBouncinessAndSpeed(8.0, 40.0)
    val CLOSE_SCALE = SpringConfig.fromBouncinessAndSpeed(7.0, 25.0)
    val CLOSE_Y = SpringConfig.fromBouncinessAndSpeed(3.0, 3.0)
    val DRAGGING = SpringConfig.fromOrigamiTensionAndFriction(0.0, 5.0)
    val CONTENT_SCALE = SpringConfig.fromBouncinessAndSpeed(5.0, 40.0)
}