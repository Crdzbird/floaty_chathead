package ni.devotion.floaty_head.views

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import ni.devotion.floaty_head.utils.Commons.getGravity
import ni.devotion.floaty_head.utils.Commons.getMapFromObject
import ni.devotion.floaty_head.utils.Commons.getMapListFromObject
import ni.devotion.floaty_head.utils.Constants.KEY_BUTTONS_LIST
import ni.devotion.floaty_head.utils.Constants.KEY_BUTTONS_LIST_POSITION
import ni.devotion.floaty_head.utils.Constants.KEY_DECORATION
import ni.devotion.floaty_head.utils.Constants.KEY_IS_SHOW_FOOTER
import ni.devotion.floaty_head.utils.Constants.KEY_PADDING
import ni.devotion.floaty_head.utils.Constants.KEY_TEXT
import ni.devotion.floaty_head.utils.UiBuilder.getButtonView
import ni.devotion.floaty_head.utils.UiBuilder.getDecoration
import ni.devotion.floaty_head.utils.UiBuilder.getGradientDrawable
import ni.devotion.floaty_head.utils.UiBuilder.getPadding
import ni.devotion.floaty_head.utils.UiBuilder.getTextView


class FooterView(private val context: Context, private val footerMap: Map<String, Any>) {
    val view: LinearLayout
        get() {
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            val footerPadding = getPadding(context, footerMap[KEY_PADDING])
            linearLayout.setPadding(footerPadding.left, footerPadding.top, footerPadding.right, footerPadding.bottom)
            linearLayout.layoutParams = params
            val decoration = getDecoration(context, footerMap[KEY_DECORATION])
            if (decoration != null) {
                val gd = getGradientDrawable(decoration)
                linearLayout.background = gd
            }
            if (footerMap[KEY_IS_SHOW_FOOTER] as Boolean) {
                val textMap = getMapFromObject(footerMap, KEY_TEXT)
                val buttonsMap: List<Map<String, Any>>? = getMapListFromObject(footerMap, KEY_BUTTONS_LIST)
                val textView = getTextView(context, textMap)
                val buttonsView: MutableList<Button?> = ArrayList()
                for (buttonMap in buttonsMap!!) {
                    buttonsView.add(getButtonView(context, buttonMap))
                }
                val buttonsPosition = footerMap[KEY_BUTTONS_LIST_POSITION] as String?
                if (textView != null) {
                    if (buttonsView.size > 0) {
                        if ("leading" == buttonsPosition) {
                            for (buttonView in buttonsView) {
                                linearLayout.addView(buttonView)
                            }
                            linearLayout.addView(textView)
                        } else {
                            val param = LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    1.0f
                            )
                            textView.layoutParams = param
                            linearLayout.addView(textView)
                            for (buttonView in buttonsView) {
                                linearLayout.addView(buttonView)
                            }
                        }
                    } else {
                        linearLayout.addView(textView)
                    }
                } else {
                    for (buttonView in buttonsView) {
                        linearLayout.addView(buttonView)
                    }
                    linearLayout.gravity = getGravity(buttonsPosition, Gravity.FILL)
                }
            }
            return linearLayout
        }

}
