package ni.devotion.floaty_head.views

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import ni.devotion.floaty_head.utils.Commons.getGravity
import ni.devotion.floaty_head.utils.Commons.getMapFromObject
import ni.devotion.floaty_head.utils.Commons.setMargin
import ni.devotion.floaty_head.utils.Constants.KEY_COLUMNS
import ni.devotion.floaty_head.utils.Constants.KEY_DECORATION
import ni.devotion.floaty_head.utils.Constants.KEY_GRAVITY
import ni.devotion.floaty_head.utils.Constants.KEY_PADDING
import ni.devotion.floaty_head.utils.Constants.KEY_ROWS
import ni.devotion.floaty_head.utils.Constants.KEY_TEXT
import ni.devotion.floaty_head.utils.UiBuilder.getDecoration
import ni.devotion.floaty_head.utils.UiBuilder.getGradientDrawable
import ni.devotion.floaty_head.utils.UiBuilder.getPadding
import ni.devotion.floaty_head.utils.UiBuilder.getTextView


class BodyView(private val context: Context, private val bodyMap: Map<String, Any>) {
    val view: LinearLayout
        get() {
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.VERTICAL
            val decoration = getDecoration(context, bodyMap[KEY_DECORATION])
            if (decoration != null) {
                val gd = getGradientDrawable(decoration)
                linearLayout.background = gd
            } else {
                linearLayout.setBackgroundColor(Color.WHITE)
            }
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            setMargin(context, params, bodyMap)
            linearLayout.layoutParams = params
            val padding = getPadding(context, bodyMap[KEY_PADDING])
            linearLayout.setPadding(padding.left, padding.top, padding.right, padding.bottom)
            val rowsMap = bodyMap[KEY_ROWS] as List<Map<String, Any>>?
            if (rowsMap != null) {
                for (i in rowsMap.indices) {
                    val row = rowsMap[i]
                    linearLayout.addView(createRow(row))
                }
            }
            return linearLayout
        }

    private fun createRow(rowMap: Map<String, Any>): View {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        setMargin(context, params, rowMap)
        linearLayout.layoutParams = params
        linearLayout.gravity = getGravity(rowMap[KEY_GRAVITY] as String?, Gravity.START)
        val padding = getPadding(context, rowMap[KEY_PADDING])
        linearLayout.setPadding(padding.left, padding.top, padding.right, padding.bottom)
        val decoration = getDecoration(context, rowMap[KEY_DECORATION])
        if (decoration != null) {
            val gd = getGradientDrawable(decoration)
            linearLayout.background = gd
        }
        val columnsMap = rowMap[KEY_COLUMNS] as List<Map<String, Any>>?
        if (columnsMap != null) {
            for (j in columnsMap.indices) {
                val column = columnsMap[j]
                linearLayout.addView(createColumn(column))
            }
        }
        return linearLayout
    }

    private fun createColumn(columnMap: Map<String, Any>): View {
        val columnLayout = LinearLayout(context)
        columnLayout.orientation = LinearLayout.HORIZONTAL
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        setMargin(context, params, columnMap)
        columnLayout.layoutParams = params
        val padding = getPadding(context, columnMap[KEY_PADDING])
        columnLayout.setPadding(padding.left, padding.top, padding.right, padding.bottom)
        val decoration = getDecoration(context, columnMap[KEY_DECORATION])
        if (decoration != null) {
            val gd = getGradientDrawable(decoration)
            columnLayout.background = gd
        }
        val textView = getTextView(context, getMapFromObject(columnMap, KEY_TEXT))
        columnLayout.addView(textView)
        return columnLayout
    }
}
