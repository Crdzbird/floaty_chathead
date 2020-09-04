package ni.devotion.floaty_head.models

import android.content.Context
import ni.devotion.floaty_head.utils.Commons
import ni.devotion.floaty_head.utils.NumberUtils

class Decoration(startColor: Any?, endColor: Any?, borderWidth: Any?, borderRadius: Any?, borderColor: Any?, context: Context?) {
    val startColor: Int
    var endColor = 0
    val borderWidth: Int
    val borderRadius: Float
    val borderColor: Int
    var isGradient = false

    init {
        this.startColor = NumberUtils.getInt(startColor)
        if (endColor != null) {
            this.endColor = NumberUtils.getInt(endColor)
            isGradient = true
        } else {
            isGradient = false
        }
        this.borderWidth = Commons.getPixelsFromDp(context!!, NumberUtils.getInt(borderWidth))
        this.borderRadius = Commons.getPixelsFromDp(context, NumberUtils.getFloat(borderRadius))
        this.borderColor = NumberUtils.getInt(borderColor)
    }
}