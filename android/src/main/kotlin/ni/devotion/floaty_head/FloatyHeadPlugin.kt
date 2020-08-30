package ni.devotion.floaty_head

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

/* FloatyHeadPlugin */
class FloatyHeadPlugin : ActivityAware, FlutterPlugin, MethodChannel.MethodCallHandler {
  private var activity: Activity? = null
  private var channel: MethodChannel? = null
  private val channelName: String = "ni.devotion/floaty_head"
  private val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
  private var mOverlayService: FloatingService? = null
  private var connection: ServiceConnection? = null
  private var connectionVideo: ServiceConnection? = null
  private var mBound: Boolean = false
  private var mBoundVideo: Boolean = false

  private fun connect(call: MethodCall?) {
    connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as FloatingService.LocalBinder
            mOverlayService = binder.getService()
            mBound = true
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            mOverlayService?.stopSelf()
            println("desconected")
            mBound = false
        }
    }

    Intent(activity, FloatingService::class.java).also { intent ->
      println("connection: $connection")
        if (connection != null) activity?.bindService(intent, connection!!, 0)
    }
  }

  private fun release() {
    if (connection != null) activity?.unbindService(connection!!)
    mOverlayService?.stopSelf()
    mBound = false
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    connect(call)
    when (call.method) {
        "openBubble" -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
                val packageName = activity?.packageName
                println( "PACKAGE: ${Uri.parse("package:$packageName")}")
                activity?.startActivityForResult(
                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")),
                        CODE_DRAW_OVER_OTHER_APP_PERMISSION)
            } else {
                activity?.startService(
                        Intent(activity, FloatingService::class.java))
                //activity?.moveTaskToBack(true)
            }
        }
        "isBubbleOpen" -> result.success(mBound)
        "closeBubble" -> if (mBound) release()
        else -> {
          println("fail")
          result.notImplemented()
        }
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel?.setMethodCallHandler(null)
    release()
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
      channel = MethodChannel(flutterPluginBinding.binaryMessenger, channelName)
      channel?.setMethodCallHandler(this)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
      activity = binding.activity
  }

  override fun onDetachedFromActivity() {
      release()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
      activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
      release()
  }
}
