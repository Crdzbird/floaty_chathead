package ni.devotion.floaty_head.utils

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.Nullable
import ni.devotion.floaty_head.models.Margin
import ni.devotion.floaty_head.utils.Constants.KEY_MARGIN


object Commons {
    fun getMapFromObject(map: Map<String, Any>, key: String?): Map<String, Any>? {
        return map[key] as Map<String, Any>?
    }

    fun getMapListFromObject(map: Map<String, Any>, key: String?): List<Map<String, Any>>? {
        return map[key] as List<Map<String, Any>>?
    }

    fun getSpFromPixels(context: Context, px: Float): Float {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return px / scaledDensity
    }

    fun getPixelsFromDp(context: Context, dp: Int): Int {
        return if (dp == -1) -1 else TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun getPixelsFromDp(context: Context, dp: Float): Float {
        return if (dp == -1f) (-1).toFloat() else TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    fun getGravity(@Nullable gravityStr: String?, defVal: Int): Int {
        var gravity = defVal
        if (gravityStr != null) {
            when (gravityStr) {
                "top" -> gravity = Gravity.TOP
                "center" -> gravity = Gravity.CENTER
                "bottom" -> gravity = Gravity.BOTTOM
                "leading" -> gravity = Gravity.START
                "trailing" -> gravity = Gravity.END
            }
        }
        return gravity
    }

    fun getFontWeight(@Nullable fontWeightStr: String?, defVal: Int): Int {
        var fontWeight = defVal
        if (fontWeightStr != null) {
            fontWeight = when (fontWeightStr) {
                "normal" -> Typeface.NORMAL
                "bold" -> Typeface.BOLD
                "italic" -> Typeface.ITALIC
                "bold_italic" -> Typeface.BOLD_ITALIC
                else -> Typeface.NORMAL
            }
        }
        return fontWeight
    }

    fun setMargin(context: Context?, params: LinearLayout.LayoutParams, map: Map<String, Any?>) {
        val margin: Margin = UiBuilder.getMargin(context, map[KEY_MARGIN])
        params.setMargins(margin.left, margin.top, margin.right, margin.bottom)
    }
}
