package ni.devotion.floaty_head.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap

object Managment {
    var floatingIcon: Bitmap? = null
    var closeIcon: Bitmap? = null
    var backgroundCloseIcon: Bitmap? = null
    var notificationTitle: String = "Floaty Chathead"
    var notificationIcon: Bitmap? = null
    var globalContext: Context? = null
    var activity: Activity? = null
}
