package ni.devotion.floaty_head.views

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import ni.devotion.floaty_head.utils.Commons.getMapFromObject
import ni.devotion.floaty_head.utils.Constants.KEY_BUTTON
import ni.devotion.floaty_head.utils.Constants.KEY_BUTTON_POSITION
import ni.devotion.floaty_head.utils.Constants.KEY_DECORATION
import ni.devotion.floaty_head.utils.Constants.KEY_PADDING
import ni.devotion.floaty_head.utils.Constants.KEY_SUBTITLE
import ni.devotion.floaty_head.utils.Constants.KEY_TITLE
import ni.devotion.floaty_head.utils.UiBuilder.getButtonView
import ni.devotion.floaty_head.utils.UiBuilder.getDecoration
import ni.devotion.floaty_head.utils.UiBuilder.getGradientDrawable
import ni.devotion.floaty_head.utils.UiBuilder.getPadding
import ni.devotion.floaty_head.utils.UiBuilder.getTextView


class HeaderView(private val context: Context, private val headerMap: Map<String, Any>) {
    val relativeView: RelativeLayout
        get() {
            val relativeLayout = RelativeLayout(context)
            relativeLayout.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            val decoration = getDecoration(context, headerMap[KEY_DECORATION])
            if (decoration != null) {
                val gd = getGradientDrawable(decoration)
                relativeLayout.background = gd
            } else {
                relativeLayout.setBackgroundColor(Color.WHITE)
            }
            val titleMap = getMapFromObject(headerMap, KEY_TITLE)
            val subTitleMap = getMapFromObject(headerMap, KEY_SUBTITLE)
            val buttonMap = getMapFromObject(headerMap, KEY_BUTTON)
            val padding = getPadding(context, headerMap[KEY_PADDING])
            relativeLayout.setPadding(padding.left, padding.top, padding.right, padding.bottom)
            val isShowButton = buttonMap != null
            assert(titleMap != null)
            val textColumn = createTextColumn(titleMap, subTitleMap)
            if (isShowButton) {
                val buttonPosition = headerMap[KEY_BUTTON_POSITION] as String?
                val button = getButtonView(context, buttonMap)
                if ("leading" == buttonPosition) {
                    relativeLayout.addView(button)
                    relativeLayout.addView(textColumn)
                } else {
                    relativeLayout.addView(textColumn)
                    relativeLayout.addView(button)
                }
            } else {
                relativeLayout.addView(textColumn)
            }
            return relativeLayout
        }

    //assert titleMap != null;
    val view: LinearLayout
        get() {
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            val decoration = getDecoration(context, headerMap[KEY_DECORATION])
            if (decoration != null) {
                val gd = getGradientDrawable(decoration)
                linearLayout.background = gd
            } else {
                linearLayout.setBackgroundColor(Color.WHITE)
            }
            linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val titleMap = getMapFromObject(headerMap, KEY_TITLE)
            val subTitleMap = getMapFromObject(headerMap, KEY_SUBTITLE)
            val buttonMap = getMapFromObject(headerMap, KEY_BUTTON)
            val padding = getPadding(context, headerMap[KEY_PADDING])
            linearLayout.setPadding(padding.left, padding.top, padding.right, padding.bottom)
            val isShowButton = buttonMap != null
            assert(titleMap != null)
            val textColumn = createTextColumn(titleMap, subTitleMap)
            if (isShowButton) {
                val buttonPosition = headerMap[KEY_BUTTON_POSITION] as String?
                val button = getButtonView(context, buttonMap)
                if ("leading" == buttonPosition) {
                    linearLayout.addView(button)
                    textColumn?.let{
                        linearLayout.addView(it)
                    }
                } else {
                    textColumn?.let{
                        val param = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1.0f
                        )
                        it.layoutParams = param
                        linearLayout.addView(it)
                    }
                    linearLayout.addView(button)
                }
            } else {
                linearLayout.addView(textColumn)
            }
            return linearLayout
        }


    fun createTextColumn(titleMap: Map<String, Any>?, subTitleMap: Map<String, Any>?): View? {
        val titleView = getTextView(context, titleMap)
        if (subTitleMap != null) {
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.addView(titleView)
            linearLayout.addView(getTextView(context, subTitleMap))
            return linearLayout
        }
        return titleView
    }
}
