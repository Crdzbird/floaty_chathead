package ni.devotion.floaty_head.floating_chathead

import android.content.res.Resources
import android.os.Build
import android.view.WindowManager
import android.util.TypedValue

class WindowManagerHelper {
    companion object {
        fun getLayoutFlag(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        fun getScreenSize() = Resources.getSystem().displayMetrics
        fun dpToPx(dp: Float) = (dp * Resources.getSystem().displayMetrics.density).toInt()
        fun spToPx(sp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().displayMetrics)
    }
}