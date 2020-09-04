package ni.devotion.floaty_head.models

import ni.devotion.floaty_head.utils.NumberUtils
import android.content.Context
import ni.devotion.floaty_head.utils.Commons

class Padding(left: Any?, top: Any?, right: Any?, bottom: Any?, context: Context?) {
    val left: Int
    val top: Int
    val right: Int
    val bottom: Int

    init {
        this.left = Commons.getPixelsFromDp(context!!, NumberUtils.getInt(left))
        this.top = Commons.getPixelsFromDp(context, NumberUtils.getInt(top))
        this.right = Commons.getPixelsFromDp(context, NumberUtils.getInt(right))
        this.bottom = Commons.getPixelsFromDp(context, NumberUtils.getInt(bottom))
    }
}