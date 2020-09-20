package ni.devotion.floaty_head.utils

import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.FrameLayout
import java.util.HashMap
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import ni.devotion.floaty_head.services.FloatyIconService
import java.util.concurrent.atomic.AtomicBoolean

object Managment {
    var floatingIcon: Bitmap? = null
    var closeIcon: Bitmap? = null
    var backgroundCloseIcon: Bitmap? = null
    var notificationTitle: String = "Floaty_head"
    var notificationIcon: Bitmap? = null
    var paramsMap: HashMap<String, Any>? = null
    var headersMap: Map<String, Any>? = null
    var bodyMap: Map<String, Any>? = null
    var footerMap: Map<String, Any>? = null
    var headerView: View? = null
    var bodyView: View? = null
    var footerView: View? = null
    var layoutParams: FrameLayout.LayoutParams? = null
    var pluginRegistrantC: PluginRegistry.PluginRegistrantCallback? = null
    var floatyIconService: FloatyIconService? = null
    var globalContext: Context? = null
    var activity: Activity? = null
    var sIsIsolateRunning = AtomicBoolean(false)
}