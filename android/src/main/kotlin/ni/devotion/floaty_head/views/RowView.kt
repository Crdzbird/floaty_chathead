package ni.devotion.floaty_head.views

import android.content.Context
import android.widget.LinearLayout
import ni.devotion.floaty_head.utils.Commons.getMapFromObject
import ni.devotion.floaty_head.utils.UiBuilder.getPadding
import ni.devotion.floaty_head.utils.UiBuilder.getTextView

class RowView(private val context: Context, private val rowMap: Map<String, Any>) {
    val view: LinearLayout
        get() {
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val columnsMap = rowMap["columns"] as List<Map<String, Any>>?
            val padding = getPadding(context, getMapFromObject(rowMap, "padding"))
            linearLayout.setPadding(padding.left, padding.top, padding.right, padding.bottom)
            if (columnsMap != null) {
                for (i in columnsMap.indices) {
                    val eachColumn = columnsMap[i]
                    val textView = getTextView(context, getMapFromObject(eachColumn, "text"))
                    linearLayout.addView(textView)
                }
            }
            return linearLayout
        }

}