package ni.devotion.floaty_head

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import ni.devotion.floaty_head.utils.Commons.getMapFromObject
import ni.devotion.floaty_head.utils.Constants.INTENT_EXTRA_PARAMS_MAP
import ni.devotion.floaty_head.utils.Constants.KEY_BODY
import ni.devotion.floaty_head.utils.Constants.KEY_FOOTER
import ni.devotion.floaty_head.utils.Constants.KEY_HEADER
import ni.devotion.floaty_head.utils.Managment.bodyMap
import ni.devotion.floaty_head.utils.Managment.footerMap
import ni.devotion.floaty_head.utils.Managment.headerView
import ni.devotion.floaty_head.utils.Managment.headersMap
import ni.devotion.floaty_head.utils.Managment.layoutParams
import ni.devotion.floaty_head.utils.Managment.paramsMap
import ni.devotion.floaty_head.views.HeaderView
import java.util.*

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
  }

    fun configureUI(mContext: Context) {
        headersMap = getMapFromObject(paramsMap!!, KEY_HEADER)
        bodyMap = getMapFromObject(paramsMap!!, KEY_BODY)
        footerMap = getMapFromObject(paramsMap!!, KEY_FOOTER)
        headerView = HeaderView(mContext, headersMap!!).view
        //val bodyView: LinearLayout = BodyView(mContext, bodyMap).getView()
        //val footerView: LinearLayout = FooterView(mContext, footerMap).getView()
        //bubbleLayout.setBackgroundColor(Color.WHITE)
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)
        /*bubbleLayout.setLayoutParams(params)
        bubbleLayout.addView(headerView)
        bubbleLayout.addView(bodyView)
        bubbleLayout.addView(footerView)*/
    }
}