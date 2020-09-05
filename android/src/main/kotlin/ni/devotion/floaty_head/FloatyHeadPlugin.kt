package ni.devotion.floaty_head

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.NonNull
import io.flutter.embedding.engine.loader.FlutterLoader
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.view.FlutterCallbackInformation
import io.flutter.view.FlutterMain
import io.flutter.view.FlutterNativeView
import io.flutter.view.FlutterRunArguments
import ni.devotion.floaty_head.services.FloatingService
import ni.devotion.floaty_head.utils.Commons.getMapFromObject
import ni.devotion.floaty_head.utils.Constants.KEY_BODY
import ni.devotion.floaty_head.utils.Constants.KEY_FOOTER
import ni.devotion.floaty_head.utils.Constants.KEY_HEADER
import ni.devotion.floaty_head.utils.Managment
import ni.devotion.floaty_head.utils.Managment.bodyMap
import ni.devotion.floaty_head.utils.Managment.bodyView
import ni.devotion.floaty_head.utils.Managment.footerMap
import ni.devotion.floaty_head.utils.Managment.footerView
import ni.devotion.floaty_head.utils.Managment.headerView
import ni.devotion.floaty_head.utils.Managment.headersMap
import ni.devotion.floaty_head.utils.Managment.layoutParams
import ni.devotion.floaty_head.views.HeaderView
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


/* FloatyHeadPlugin */
class FloatyHeadPlugin : ActivityAware, FlutterPlugin, MethodChannel.MethodCallHandler {
    companion object{
      var sPluginRegistrantCallback: PluginRegistry.PluginRegistrantCallback? = null
        lateinit var instance: FloatyHeadPlugin
        fun setPluginRegistrant(callback: PluginRegistry.PluginRegistrantCallback) {
          sPluginRegistrantCallback = callback
      }
    }
    var callbackHandle: Long = -1L
    var codeCallbackHandle: Long = -1L
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
    private var sBackgroundFlutterView: FlutterNativeView? = null
    var sIntent: Intent? = null
    var sIsIsolateRunning = AtomicBoolean(false)


    fun registerWith(pluginRegistrar: Registrar) {
        context = pluginRegistrar.context()
        channel = MethodChannel(pluginRegistrar.messenger(), channelName)
        channel!!.setMethodCallHandler(FloatyHeadPlugin())
    }

    private fun connect(call: MethodCall?) {
        sIntent = Intent(activity, FloatingService::class.java)
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
    sIntent.also { intent ->
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
                activity?.startService(sIntent)
            }
        }
        "isOpen" -> result.success(mBound)
        "close" -> {
          if(mBound){
            mOverlayService!!.chatHeads!!.content.removeAllViews()
            mOverlayService!!.removeAllViews()
            release()
          }
        }
        "setIcon" -> result.success(setIconFromAsset(call.arguments as String))
        "setBackgroundCloseIcon" -> result.success(setBackgroundCloseIconFromAsset(call.arguments as String))
        "setCloseIcon" -> result.success(setCloseIconFromAsset(call.arguments as String))
        "setNotificationTitle" -> result.success(setNotificationTitle(call.arguments as String))
        "setNotificationIcon" -> result.success(setNotificationIcon(call.arguments as String))
        "setFloatyHeadContent" -> {
            assert((call.arguments != null))
            val updateParams = call.arguments as HashMap<String, Any>
            Log.d("TAG", "Going to set floaty head content")
            headersMap = getMapFromObject(updateParams, KEY_HEADER)
            bodyMap = getMapFromObject(updateParams, KEY_BODY)
            footerMap = getMapFromObject(updateParams, KEY_FOOTER)
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT)
            try {
                headersMap?.let {
                    headerView = HeaderView(activity!!.applicationContext, it).view
                }
                bodyMap?.let {
                    bodyView = HeaderView(activity!!.applicationContext, it).view
                }
                footerMap?.let {
                    footerView = HeaderView(activity!!.applicationContext, it).view
                }
            } catch (except: Exception) {
                println("KABOOOOMMM")
                println(except)
                println("-----------------------------------------------------------")
            }
            result.success(true)
        }
        "registerCallBackHandler" -> {
            try {
                val arguments = call.arguments as List<*>
                arguments ?: result.success(false)
                arguments?.let {
                    callbackHandle = (it[0]).toString().toLong()
                    codeCallbackHandle = (it[1]).toString().toLong()
                    startCallBackHandler(activity!!.applicationContext)
                    result.success(true)
                }
            } catch (ex: Exception) {
                Log.e("TAG", "Exception in registerOnClickHandler " + ex.toString())
                result.success(false)
            }
        }
        else -> result.notImplemented()
    }
  }

    fun startCallBackHandler(context: Context) {
        Log.d("TAG", "onClickCallBackHandle $callbackHandle")
        if (callbackHandle != -1L) {
            FlutterMain.ensureInitializationComplete(context, null)
            val mAppBundlePath: String = FlutterMain.findAppBundlePath()
            val flutterCallback: FlutterCallbackInformation = FlutterCallbackInformation.lookupCallbackInformation(callbackHandle)
            if (sBackgroundFlutterView == null) {
                sBackgroundFlutterView = FlutterNativeView(context, true)
                if (mAppBundlePath != null && !sIsIsolateRunning.get()) {
                    if (sPluginRegistrantCallback == null) {
                        Log.i("TAG", "Unable to start callBackHandle... as plugin is not registered")
                        return
                    }
                    Log.i("TAG", "Starting callBackHandle...")
                    val args = FlutterRunArguments()
                    args.bundlePath = mAppBundlePath
                    args.entrypoint = flutterCallback.callbackName
                    args.libraryPath = flutterCallback.callbackLibraryPath
                    sBackgroundFlutterView!!.runFromBundle(args)
                    sPluginRegistrantCallback!!.registerWith(sBackgroundFlutterView!!.getPluginRegistry())
                    sIsIsolateRunning.set(true)
                }
            } else {
                sIsIsolateRunning.set(true)
            }
        }
    }

    fun invokeCallBack(context: Context, type: String, params: Any) {
        val argumentsList: MutableList<Any> = ArrayList()
        Log.v("TAG", "invoking callback for tag $params")
        //Log.i(TAG, "codeCallBackHandle " + codeCallBackHandle);
        if (codeCallbackHandle == -1L) {
            Log.e("TAG", "invokeCallBack failed, as codeCallBackHandle is null")
        } else {
            argumentsList.clear()
            argumentsList.add(codeCallbackHandle)
            argumentsList.add(type)
            argumentsList.add(params)
            if (sIsIsolateRunning.get()) {
                if (channel == null) {
                    Log.v("TAG", "Recreating the background channel as it is null")
                    //channel = MethodChannel(sBackgroundFlutterView, channelName)
                }
                try {
                    Log.v("TAG", "Invoking on method channel")
                    val retries = intArrayOf(2)
                    invokeCallBackToFlutter(channel!!, "callBack", argumentsList, retries)
                    //backgroundChannel.invokeMethod("callBack", argumentsList);
                } catch (ex: Exception) {
                    Log.e("TAG", "Exception in invoking callback $ex")
                }
            } else {
                Log.e("TAG", "invokeCallBack failed, as isolate is not running")
            }
        }
    }

    private fun invokeCallBackToFlutter(channel: MethodChannel, method: String, arguments: List<Any>, retries: IntArray) {
        channel.invokeMethod(method, arguments, object : MethodChannel.Result {
            override fun success(o: Any?) {
                Log.i("TAG", "Invoke call back success")
            }

            override fun error(s: String?, s1: String?, o: Any?) {
                Log.e("TAG", "Error $s$s1")
            }

            override fun notImplemented() {
                //To fix the dart initialization delay.
                if (retries[0] > 0) {
                    Log.d("TAG", "Not Implemented method $method. Trying again to check if it works")
                    invokeCallBackToFlutter(channel, method, arguments, retries)
                } else {
                    Log.e("TAG", "Not Implemented method $method")
                }
                retries[0]--
            }
        })
    }

    private fun setNotificationTitle(title: String):Int {
        var result = -1
        try {
            Managment.notificationTitle = title
            result = 1
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun setNotificationIcon(assetPath: String):Int {
        var result = -1
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                val inputStream = activity!!.applicationContext.assets.open("flutter_assets/" + assetPath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                Managment.notificationIcon = bitmap
                result = 1
            }
            else
            {
                val assetLookupKey = FlutterLoader.getInstance().getLookupKeyForAsset(assetPath)
                val assetManager = activity!!.applicationContext.assets
                val assetFileDescriptor = assetManager.openFd(assetLookupKey)
                val inputStream = assetFileDescriptor.createInputStream()
                Managment.notificationIcon = BitmapFactory.decodeStream(inputStream)
                result = 1
            }
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }
    private fun setBackgroundCloseIconFromAsset(assetPath: String):Int {
    var result = -1
    try
    {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
      {
        val inputStream = activity!!.applicationContext.assets.open("flutter_assets/" + assetPath)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        Managment.backgroundCloseIcon = bitmap
        result = 1
      }
      else
      {
        val assetLookupKey = FlutterLoader.getInstance().getLookupKeyForAsset(assetPath)
        val assetManager = activity!!.applicationContext.assets
        val assetFileDescriptor = assetManager.openFd(assetLookupKey)
        val inputStream = assetFileDescriptor.createInputStream()
          Managment.backgroundCloseIcon = BitmapFactory.decodeStream(inputStream)
        result = 1
      }
    }
    catch (e: IOException) {
      e.printStackTrace()
    }
    return result
}
    private fun setCloseIconFromAsset(assetPath: String):Int {
  var result = -1
  try
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
    {
      val inputStream = activity!!.applicationContext.assets.open("flutter_assets/" + assetPath)
      val bitmap = BitmapFactory.decodeStream(inputStream)
      Managment.closeIcon = bitmap
      result = 1
    }
    else
    {
      val assetLookupKey = FlutterLoader.getInstance().getLookupKeyForAsset(assetPath)
      val assetManager = activity!!.applicationContext.assets
      val assetFileDescriptor = assetManager.openFd(assetLookupKey)
      val inputStream = assetFileDescriptor.createInputStream()
        Managment.closeIcon = BitmapFactory.decodeStream(inputStream)
      result = 1
    }
  }
  catch (e: IOException) {
    e.printStackTrace()
  }
  return result
}
    private fun setIconFromAsset(assetPath: String):Int {
    var result = -1
    try
    {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
      {
        val inputStream = activity!!.applicationContext.assets.open("flutter_assets/" + assetPath)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        Managment.floatingIcon = bitmap
        result = 1
      }
      else
      {
        val assetLookupKey = FlutterLoader.getInstance().getLookupKeyForAsset(assetPath)
        val assetManager = activity!!.applicationContext.assets
        val assetFileDescriptor = assetManager.openFd(assetLookupKey)
        val inputStream = assetFileDescriptor.createInputStream()
          Managment.floatingIcon = BitmapFactory.decodeStream(inputStream)
        result = 1
      }
    }
    catch (e: IOException) {
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
      instance = this@FloatyHeadPlugin
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
