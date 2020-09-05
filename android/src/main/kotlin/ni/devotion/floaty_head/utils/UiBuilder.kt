package ni.devotion.floaty_head.utils

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import ni.devotion.floaty_head.FloatyHeadPlugin
import ni.devotion.floaty_head.models.Decoration
import ni.devotion.floaty_head.models.Margin
import ni.devotion.floaty_head.models.Padding
import ni.devotion.floaty_head.utils.Constants.CALLBACK_TYPE_ONCLICK
import ni.devotion.floaty_head.utils.Constants.KEY_BORDER_COLOR
import ni.devotion.floaty_head.utils.Constants.KEY_BORDER_RADIUS
import ni.devotion.floaty_head.utils.Constants.KEY_BORDER_WIDTH
import ni.devotion.floaty_head.utils.Constants.KEY_BOTTOM
import ni.devotion.floaty_head.utils.Constants.KEY_DECORATION
import ni.devotion.floaty_head.utils.Constants.KEY_END_COLOR
import ni.devotion.floaty_head.utils.Constants.KEY_FONT_SIZE
import ni.devotion.floaty_head.utils.Constants.KEY_FONT_WEIGHT
import ni.devotion.floaty_head.utils.Constants.KEY_HEIGHT
import ni.devotion.floaty_head.utils.Constants.KEY_LEFT
import ni.devotion.floaty_head.utils.Constants.KEY_MARGIN
import ni.devotion.floaty_head.utils.Constants.KEY_PADDING
import ni.devotion.floaty_head.utils.Constants.KEY_RIGHT
import ni.devotion.floaty_head.utils.Constants.KEY_START_COLOR
import ni.devotion.floaty_head.utils.Constants.KEY_TAG
import ni.devotion.floaty_head.utils.Constants.KEY_TEXT
import ni.devotion.floaty_head.utils.Constants.KEY_TEXT_COLOR
import ni.devotion.floaty_head.utils.Constants.KEY_TOP
import ni.devotion.floaty_head.utils.Constants.KEY_WIDTH


object UiBuilder {
    fun getTextView(context: Context?, textMap: Map<String, Any>?): TextView? {
        if (textMap == null) return null
        val textView = TextView(context)
        textView.text = textMap[KEY_TEXT] as String?
        textView.setTypeface(textView.typeface, Commons.getFontWeight(textMap[KEY_FONT_WEIGHT] as String?, Typeface.NORMAL))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, NumberUtils.getFloat(textMap[KEY_FONT_SIZE]))
        textView.setTextColor(NumberUtils.getInt(textMap[KEY_TEXT_COLOR]))
        val padding: Padding = getPadding(context, textMap[KEY_PADDING])
        textView.setPadding(padding.left, padding.top, padding.right, padding.bottom)
        return textView
    }

    fun getPadding(context: Context?, `object`: Any?): Padding {
        val paddingMap = `object` as Map<String, Any>?
                ?: return Padding(0, 0, 0, 0, context)
        return Padding(paddingMap[KEY_LEFT], paddingMap[KEY_TOP], paddingMap[KEY_RIGHT], paddingMap[KEY_BOTTOM], context)
    }

    fun getMargin(context: Context?, `object`: Any?): Margin {
        val marginMap = `object` as Map<String, Any>?
                ?: return Margin(0, 0, 0, 0, context)
        return Margin(marginMap[KEY_LEFT], marginMap[KEY_TOP], marginMap[KEY_RIGHT], marginMap[KEY_BOTTOM], context)
    }

    fun getDecoration(context: Context?, `object`: Any?): Decoration? {
        val decorationMap = `object` as Map<String, Any>? ?: return null
        return Decoration(decorationMap[KEY_START_COLOR], decorationMap[KEY_END_COLOR],
                decorationMap[KEY_BORDER_WIDTH], decorationMap[KEY_BORDER_RADIUS],
                decorationMap[KEY_BORDER_COLOR], context)
    }

    fun getButtonView(context: Context?, buttonMap: Map<String, Any>?): Button? {
        buttonMap ?: return null
        val button = Button(context)
        val buttonText = getTextView(context, Commons.getMapFromObject(buttonMap, KEY_TEXT))!!
        button.text = buttonText.text
        val tag = buttonMap[KEY_TAG]
        button.tag = tag
        button.textSize = Commons.getSpFromPixels(context!!, buttonText.textSize)
        button.setTextColor(buttonText.textColors)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) button.elevation = 10f
        val params = LinearLayout.LayoutParams(
                Commons.getPixelsFromDp(context, buttonMap[KEY_WIDTH] as Int),
                Commons.getPixelsFromDp(context, buttonMap[KEY_HEIGHT] as Int),
                1.0f)
        val buttonMargin: Margin = getMargin(context, buttonMap[KEY_MARGIN])
        params.setMargins(buttonMargin.left, buttonMargin.top, buttonMargin.right, buttonMargin.bottom.coerceAtMost(4))
        button.layoutParams = params
        val padding: Padding = getPadding(context, buttonMap[KEY_PADDING])
        button.setPadding(padding.left, padding.top, padding.right, padding.bottom)
        val decoration: Decoration? = getDecoration(context, buttonMap[KEY_DECORATION])
        decoration?.let{
            val gd = getGradientDrawable(it)
            button.background = gd
        }
        button.setOnClickListener {
            if(!FloatyHeadPlugin.instance.sIsIsolateRunning.get()){
                FloatyHeadPlugin.instance.startCallBackHandler(context)
            }
            FloatyHeadPlugin.instance.invokeCallBack(context, CALLBACK_TYPE_ONCLICK, tag!!)
        }
        return button
    }

    fun getGradientDrawable(decoration: Decoration?): GradientDrawable {
        val gd = GradientDrawable()
        if (decoration!!.isGradient) {
            val colors = intArrayOf(decoration.startColor, decoration.endColor)
            gd.colors = colors
            gd.orientation = GradientDrawable.Orientation.LEFT_RIGHT
        } else {
            gd.setColor(decoration.startColor)
        }
        gd.cornerRadius = decoration.borderRadius
        gd.setStroke(decoration.borderWidth, decoration.borderColor)
        return gd
    }
}