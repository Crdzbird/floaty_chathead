package ni.devotion.floaty_head

import android.app.Activity
import android.content.Context;
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.NonNull
import java.io.IOException;
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import ni.devotion.floaty_head.services.FloatingService
import ni.devotion.floaty_head.utils.Managment

/* FloatyHeadPlugin */
class FloatyHeadPlugin : ActivityAware, FlutterPlugin, MethodChannel.MethodCallHandler {
    private var activity: Activity? = null
    private var channel: MethodChannel? = null
    private val channelName: String = "ni.devotion/floaty_head"
    private val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    private var mOverlayService: FloatingService? = null
    private var connection: ServiceConnection? = null
    private var connectionVideo: ServiceConnection? = null
    private var registrar: Registrar? = null
    private var mBound: Boolean = false
    private var mBoundVideo: Boolean = false
    private var paramsMap = emptyMap<String, Any>()
    private var DRAWABLE: String = "drawable";
    private var DEFAULT_ICON: String = "defaultIcon";
    private var context:Context? = null
    fun registerWith(pluginRegistrar:Registrar) {
        context = pluginRegistrar.context()
        channel = MethodChannel(pluginRegistrar.messenger(), channelName)
        channel!!.setMethodCallHandler(FloatyHeadPlugin())
    }

    private fun connect(call: MethodCall?) {
        connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as FloatingService.LocalBinder
            mOverlayService = binder.getService()
            mBound = true
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            mOverlayService?.stopSelf()
            mBound = false
        }
    }
    Intent(activity, FloatingService::class.java).also { intent ->
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
        "start" -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
                val packageName = activity?.packageName
                activity?.startActivityForResult(
                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")),
                        CODE_DRAW_OVER_OTHER_APP_PERMISSION)
            } else {
                activity?.startService(
                        Intent(activity, FloatingService::class.java))
            }
        }
        "isOpen" -> result.success(mBound)
        "close" -> if (mBound) release()
        "setIcon" -> result.success(setIconFromAsset(call.arguments as String))
        "setBackgroundCloseIcon" -> result.success(setBackgroundCloseIconFromAsset(call.arguments as String))
        "setCloseIcon" -> result.success(setCloseIconFromAsset(call.arguments as String))
        else -> result.notImplemented()
    }
  }

  private fun setBackgroundCloseIconFromAsset(assetPath:String):Int {
    var result = -1
    try
    {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
      {
        val inputStream = activity!!.applicationContext.getAssets().open("flutter_assets/" + assetPath)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        Managment.backgroundCloseIcon = bitmap
        result = 1
      }
      else
      {
        val assetLookupKey = FlutterLoader.getInstance().getLookupKeyForAsset(assetPath)
        val assetManager = activity!!.applicationContext.getAssets()
        val assetFileDescriptor = assetManager.openFd(assetLookupKey)
        val inputStream = assetFileDescriptor.createInputStream()
          Managment.backgroundCloseIcon = BitmapFactory.decodeStream(inputStream)
        result = 1
      }
    }
    catch (e:IOException) {
      e.printStackTrace()
    }
    return result
}


private fun setCloseIconFromAsset(assetPath:String):Int {
  var result = -1
  try
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
    {
      val inputStream = activity!!.applicationContext.getAssets().open("flutter_assets/" + assetPath)
      val bitmap = BitmapFactory.decodeStream(inputStream)
      Managment.closeIcon = bitmap
      result = 1
    }
    else
    {
      val assetLookupKey = FlutterLoader.getInstance().getLookupKeyForAsset(assetPath)
      val assetManager = activity!!.applicationContext.getAssets()
      val assetFileDescriptor = assetManager.openFd(assetLookupKey)
      val inputStream = assetFileDescriptor.createInputStream()
        Managment.closeIcon = BitmapFactory.decodeStream(inputStream)
      result = 1
    }
  }
  catch (e:IOException) {
    e.printStackTrace()
  }
  return result
}

  private fun setIconFromAsset(assetPath:String):Int {
    var result = -1
    try
    {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
      {
        val inputStream = activity!!.applicationContext.getAssets().open("flutter_assets/" + assetPath)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        Managment.floatingIcon = bitmap
        result = 1
      }
      else
      {
        val assetLookupKey = FlutterLoader.getInstance().getLookupKeyForAsset(assetPath)
        val assetManager = activity!!.applicationContext.getAssets()
        val assetFileDescriptor = assetManager.openFd(assetLookupKey)
        val inputStream = assetFileDescriptor.createInputStream()
          Managment.floatingIcon = BitmapFactory.decodeStream(inputStream)
        result = 1
      }
    }
    catch (e:IOException) {
      e.printStackTrace()
    }
    return result
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
